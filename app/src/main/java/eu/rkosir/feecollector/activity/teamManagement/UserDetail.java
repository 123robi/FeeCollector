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
import android.util.Log;
import android.view.MenuItem;
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

import org.apache.commons.codec.binary.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.Normalizer;
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
import eu.rkosir.feecollector.helper.JsonObjectConverter;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;


public class UserDetail extends AppCompatActivity implements View.OnLongClickListener{
	static final int REQUEST_IMAGE_CAPTURE = 1;
	private User myUser;
	private Toolbar mToolbar;
	private TextView mName, mTeam, mEmail, mNumber, mAddress;
	private RelativeLayout mRelativeLayoutEmail, mRelativeLayoutAddress, mRelativeLayoutPhoneNumber;
	private CircleImageView mCircleImageView;
	private Bitmap bitmap;
	private ProgressBar mProgressBar;
	private FloatingActionButton mSendNotificaiton, mPay;
	private List<MemberFee> mMemberFees;
	private RecyclerView mRecyclerView;
	private ShowMemberFeesAdapter mAdapter;
	public boolean is_in_action_mode;
	private ArrayList<MemberFee> selected;
	private ArrayList<MemberFee> notPaid;
	private int counter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_detail);
		Intent intent = getIntent();
		mToolbar = findViewById(R.id.back_action_bar);
		setSupportActionBar(mToolbar);
		mToolbar.setNavigationOnClickListener(view -> onBackPressed());

		if (intent != null) {
			Object user = intent.getParcelableExtra("user");
			if (user instanceof User) {
				myUser = (User) user;
			}
		}

		is_in_action_mode = false;
		selected = new ArrayList<>();
		notPaid = new ArrayList<>();
		counter = 0;

		mProgressBar = findViewById(R.id.pb_loading_indicator);
		mSendNotificaiton = findViewById(R.id.send_notification);
		mPay = findViewById(R.id.pay);
		if (!SharedPreferencesSaver.isAdmin(getApplicationContext())) {
			mSendNotificaiton.setVisibility(View.GONE);
		}

		mToolbar.setTitle(myUser.getName());
		mName = findViewById(R.id.player_name);
		mTeam = findViewById(R.id.player_team);
		mEmail = findViewById(R.id.email);
		mNumber = findViewById(R.id.phone_number);
		mAddress = findViewById(R.id.address);

		mMemberFees = new ArrayList<>();
		mRecyclerView = findViewById(R.id.feesList);
		loadFees();

		mSendNotificaiton.setOnClickListener(view -> {
			Intent sendNotification = new Intent(this, SendNotification.class);
			sendNotification.putExtra("user", myUser);
			this.startActivity(sendNotification);
		});

		mCircleImageView = findViewById(R.id.user_picture);
		String imageUrl = "https://rkosir.eu/images/" + myUser.getEmail() + ".jpg";
		Picasso.get().load(imageUrl).error(R.mipmap.ic_team_member_no_photo).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(mCircleImageView);

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
		if (new JsonObjectConverter(SharedPreferencesSaver.getUser(getApplicationContext())).getString("email").equals(myUser.getEmail()) || SharedPreferencesSaver.isAdmin(getApplicationContext())) {
			mCircleImageView.setOnClickListener(view -> {
				Picasso.get().invalidate("https://rkosir.eu/images/" + myUser.getEmail() + ".jpg");
				Intent selectPicture = new Intent();
				selectPicture.setType("image/*");
				selectPicture.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(selectPicture, REQUEST_IMAGE_CAPTURE);
			});
			if (SharedPreferencesSaver.getCurrencySymbol(getApplicationContext()).equals("CZK")) {
				mPay.setVisibility(View.VISIBLE);
				mPay.setOnClickListener(view -> {
					getUrlQrImage();
				});
			}

		}

		if (myUser.getName() != null || !myUser.getName().equals("")) {
			mName.setText(myUser.getName());
		}
		mTeam.setText(SharedPreferencesSaver.getLastTeamName(getApplicationContext()));
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
	}

	/**
	 * Volley request to load all fees of user(paid) unpaid
	 */
	private void loadFees() {
		mProgressBar.bringToFront();
		mProgressBar.setVisibility(View.VISIBLE);
		String uri = String.format(AppConfig.URL_GET_FEES_OF_USER,
				SharedPreferencesSaver.getLastTeamID(getApplicationContext()),
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
						MemberFee addMemberFee = new MemberFee(memberFee.getInt("id"),fee.getString("name"), fee.getString("cost"), calendar, false);
						mMemberFees.add(addMemberFee);
						notPaid.add(addMemberFee);
					} else {
						MemberFee addMemberFee = new MemberFee(memberFee.getInt("id"),fee.getString("name"), fee.getString("cost"), calendar, true);
						mMemberFees.add(addMemberFee);
					}
				}

				mAdapter = new ShowMemberFeesAdapter(mMemberFees,this);
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
	private void getUrlQrImage() {
		mProgressBar.bringToFront();
		mProgressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GET_QR, response -> {
			JSONObject object = null;

			Intent intent = new Intent(this, FullScreenImage.class);
			Log.d("TEST",response);
			intent.putExtra("url",response);
			startActivity(intent);


		}, error -> {
			Toast.makeText(getApplicationContext(),R.string.toast_size_error,Toast.LENGTH_LONG).show();
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String,String> params = new HashMap<>();
				params.put("connection_number", SharedPreferencesSaver.getLastTeamID(getApplicationContext()));
				params.put("symbol", SharedPreferencesSaver.getCurrencyCode(getApplicationContext()));

				double cost = 0.00;
				String message = myUser.getName() + " " + SharedPreferencesSaver.getLastTeamName(getApplicationContext());
				for(MemberFee memberFee : notPaid) {
					cost += Double.parseDouble(memberFee.getAmount());
				}
				String normalized = Normalizer.normalize(message, Normalizer.Form.NFD)
						.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
				params.put("cost", Double.toString(120.30));
				params.put("message", normalized);
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

	@Override
	public boolean onLongClick(View v) {
		if (SharedPreferencesSaver.isAdmin(getApplicationContext()) && !is_in_action_mode) {
			mToolbar.setTitle("0 items selected");
			mToolbar.inflateMenu(R.menu.menu_action_mode);
			is_in_action_mode = true;
			mAdapter.notifyDataSetChanged();
		}
		return false;
	}

	public void prepareSelection(boolean v, int adapterPosition) {
		try{
			if (v) {
				selected.add(mMemberFees.get(adapterPosition));
				counter += 1;
				updateCounter();
			} else {
				selected.remove(mMemberFees.get(adapterPosition));
				counter -= 1;
				updateCounter();
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

	}

	private void updateCounter() {
		mToolbar.setTitle(counter + " Items selected");
	}

	@Override
	public void onBackPressed() {
		if (is_in_action_mode) {
			mToolbar.getMenu().clear();
			mToolbar.setTitle(myUser.getName());
			is_in_action_mode = false;
			mAdapter.notifyDataSetChanged();
			counter = 0;
			selected.clear();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.fee_is_paid) {
			for(MemberFee fee : selected) {
				updateMembers(fee);
				if (mMemberFees.contains(fee)) {
					mMemberFees.get(mMemberFees.indexOf(fee)).setPaid(true);
				}
			}
			mToolbar.getMenu().clear();
			mToolbar.setTitle(myUser.getName());
			is_in_action_mode = false;
			mAdapter.notifyDataSetChanged();
			counter = 0;
			selected.clear();
		}
		if (item.getItemId() == R.id.fee_delete) {
			for(MemberFee fee : selected) {
				deleteFee(fee);
				if (mMemberFees.contains(fee)) {
					mMemberFees.remove(fee);
				}
			}
			mToolbar.getMenu().clear();
			mToolbar.setTitle(myUser.getName());
			is_in_action_mode = false;
			mAdapter.notifyDataSetChanged();
			counter = 0;
			selected.clear();
		}
		return super.onOptionsItemSelected(item);
	}

	private void deleteFee(MemberFee fee) {
		mProgressBar.bringToFront();
		mProgressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_DELETE_FEES_OF_USER, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				if (!object.getBoolean("error")) {
					Toast.makeText(getApplicationContext(),R.string.toast_fee_sucessfully_deleted,Toast.LENGTH_LONG).show();
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
				params.put("id", String.valueOf(fee.getId()));
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

	private void updateMembers(MemberFee fee) {
		mProgressBar.bringToFront();
		mProgressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_UPDATE_FEES_OF_USER, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				if (!object.getBoolean("error")) {
					Toast.makeText(getApplicationContext(),R.string.toast_fee_sucessfully_updated,Toast.LENGTH_LONG).show();
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
				params.put("id", String.valueOf(fee.getId()));
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
}
