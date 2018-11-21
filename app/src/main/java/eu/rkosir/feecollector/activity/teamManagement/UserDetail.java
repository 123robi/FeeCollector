package eu.rkosir.feecollector.activity.teamManagement;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.github.clans.fab.FloatingActionButton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.adapters.ShowMemberFeesAdapter;
import eu.rkosir.feecollector.entity.MemberFee;
import eu.rkosir.feecollector.entity.User;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;

import static com.facebook.FacebookSdk.getApplicationContext;


public class UserDetail extends AppCompatActivity {
	static final int REQUEST_IMAGE_CAPTURE = 1;
	private User myUser;
	private Toolbar mToolbar;
	private TextView mName, mTeam, mAge, mEmail, mNumber, mAddress, mBirthday;
	private RelativeLayout mRelativeLayoutEmail, mRelativeLayoutAddress, mRelativeLayoutPhoneNumber;
	private CircleImageView mCircleImageView;
	private Bitmap bitmap;
	private ProgressBar mProgressBar;
	private FloatingActionButton mSendNotificaiton;
	private List<MemberFee> mMemberFees;
	private RecyclerView mRecyclerView;
	private ShowMemberFeesAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_detail);
		Intent intent = getIntent();
		mToolbar = findViewById(R.id.back_action_bar);
		mToolbar.setNavigationOnClickListener(view -> this.finish());

		if (intent != null) {
			Object user = intent.getParcelableExtra("user");
			if (user instanceof User) {
				myUser = (User) user;
			}
		}
		mProgressBar = findViewById(R.id.pb_loading_indicator);
		mSendNotificaiton = findViewById(R.id.send_notification);

		mToolbar.setTitle(myUser.getName());
		mName = findViewById(R.id.player_name);
		mTeam = findViewById(R.id.player_team);
		mAge = findViewById(R.id.player_age);
		mEmail = findViewById(R.id.email);
		mNumber = findViewById(R.id.phone_number);
		mAddress = findViewById(R.id.address);
		mBirthday = findViewById(R.id.birthday);

		mMemberFees = new ArrayList<>();
		mRecyclerView = findViewById(R.id.feesList);
		loadFees();

		mSendNotificaiton.setOnClickListener(view -> {
			Intent sendNotification = new Intent(this, SendNotification.class);
			sendNotification.putExtra("user", myUser);
			this.startActivity(sendNotification);
		});

		mCircleImageView = findViewById(R.id.user_picture);
		String imageUrl = "http://rkosir.eu/images/" + myUser.getEmail() + ".jpg";
		Picasso.get().load(imageUrl).error(R.mipmap.ic_team_member_no_photo).into(mCircleImageView);

		mRelativeLayoutEmail = findViewById(R.id.relative_email);
		mRelativeLayoutEmail.setOnClickListener(view -> {
			Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
			emailIntent.setData(Uri.parse("mailto:" + myUser.getEmail()));
			if (emailIntent.resolveActivity(getPackageManager()) != null) {
				startActivity(emailIntent);
			}
		});

		mRelativeLayoutAddress = findViewById(R.id.relative_address);
		mRelativeLayoutAddress.setOnClickListener(view -> {
			Intent intent1 = new Intent(Intent.ACTION_VIEW);
			intent1.setData(Uri.parse("geo:0,0?q=" + myUser.getAddress()));
			if (intent1.resolveActivity(getPackageManager()) != null) {
				startActivity(intent1);
			}
		});

		mRelativeLayoutPhoneNumber = findViewById(R.id.relative_phone_number);
		mRelativeLayoutPhoneNumber.setOnClickListener(view -> {
			Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
			phoneIntent.setData(Uri.parse("tel:" + myUser.getPhoneNumber()));
			if (phoneIntent.resolveActivity(getPackageManager()) != null) {
				startActivity(phoneIntent);
			}
		});
		mCircleImageView.setOnClickListener(view -> {
			/*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
				startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
			}*/
			Picasso.get().invalidate("http://rkosir.eu/images/" + myUser.getEmail() + ".jpg");
			Intent selectPicture = new Intent();
			selectPicture.setType("image/*");
			selectPicture.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(selectPicture, REQUEST_IMAGE_CAPTURE);
		});
		if (myUser.getName() != null || !myUser.getName().equals("")) {
			mName.setText(myUser.getName());
		}
		mTeam.setText(SharedPreferencesSaver.getLastTeamName(getApplicationContext()));
		// #todo age
		mEmail.setText(myUser.getEmail());
		if (myUser.getPhoneNumber() != null && !(myUser.getPhoneNumber().equals("")) && !(myUser.getPhoneNumber().equals("null"))) {
			mNumber.setText(myUser.getPhoneNumber());
		} else {
			findViewById(R.id.line_phone_number).setVisibility(View.GONE);
			mRelativeLayoutPhoneNumber.setVisibility(View.GONE);
		}

		if (myUser.getAddress() != null && !(myUser.getAddress().equals("")) && !(myUser.getAddress().equals("null"))) {
			mAddress.setText(myUser.getAddress());
		} else {
			findViewById(R.id.line_address).setVisibility(View.GONE);
			mRelativeLayoutAddress.setVisibility(View.GONE);
		}
		// #todo birhtday date
	}

	/**
	 * Volley request to load all fees of user(paid) unpaid
	 */
	private void loadFees() {
		mProgressBar.bringToFront();
		mProgressBar.setVisibility(View.VISIBLE);
		String uri = String.format(AppConfig.URL_GET_FEES_OF_USER,
				myUser.getEmail());
		StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, response -> {
			JSONObject object = null;

			try {
				object = new JSONObject(response);
				JSONArray memberFeesArray = object.getJSONArray("fees");
				for(int i = 0; i < memberFeesArray .length(); i++) {
					JSONObject memberFee = memberFeesArray .getJSONObject(i);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(AppConfig.parse.parse(memberFee.getString("date")));
					JSONObject matchingData = memberFee.getJSONObject("_matchingData");
					JSONObject fee = matchingData.getJSONObject("Fees");

					if (memberFee.getInt("paid") == 0) {
						MemberFee addMemberFee = new MemberFee(fee.getString("name"), fee.getString("cost"), calendar, false);
						mMemberFees.add(addMemberFee);
					} else {
						MemberFee addMemberFee = new MemberFee(fee.getString("name"), fee.getString("cost"), calendar, false);
						mMemberFees.add(addMemberFee);
					}
				}

				mAdapter = new ShowMemberFeesAdapter(mMemberFees,getApplicationContext());
				mRecyclerView.setAdapter(mAdapter);
				mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
			} catch (JSONException | ParseException e) {
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(getApplicationContext(),R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
		});

		RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
		requestQueue.add(stringRequest);
		requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
			if (mProgressBar != null) {
				mProgressBar.setVisibility(View.INVISIBLE);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
			Uri path = data.getData();
			try {
				bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
				uploadImage();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private void uploadImage(){
		mProgressBar.bringToFront();
		mProgressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.ULR_SAVE_IAMGE, response -> {
			JSONObject object = null;

			try {
				object = new JSONObject(response);
				if (!object.getBoolean("error")) {
					mCircleImageView.setImageBitmap(bitmap);
				} else {
					Toast.makeText(getApplicationContext(),R.string.toast_uploading_error,Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(getApplicationContext(),R.string.toast_size_error,Toast.LENGTH_LONG).show();
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String,String> params = new HashMap<>();
				params.put("image", imageToString(bitmap));
				params.put("email", myUser.getEmail());
				return params;
			}
		};

		RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
		requestQueue.add(stringRequest);
		requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
			if (mProgressBar != null) {
				mProgressBar.setVisibility(View.INVISIBLE);
			}
		});
	}

	private String imageToString(Bitmap bitmap) {
		ByteArrayOutputStream byteArrayOutputStream =  new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG,10,byteArrayOutputStream);
		byte[] imgBytes = byteArrayOutputStream.toByteArray();
		return Base64.encodeToString(imgBytes,Base64.DEFAULT);
	}
}
