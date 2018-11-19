package eu.rkosir.feecollector.activity.teamManagement;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.entity.User;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;

public class SendNotification extends AppCompatActivity {
    private static final int SMS_PERMISSION_CODE = 61231;
    private Toolbar mToolbar;
    private User myUser;
    private TextInputEditText mInputTo, mInputMessage;
    private ProgressBar mProgressBar;
    private CheckBox mCheckNotificaiton, mCheckSMS, mCheckEmail;
    private TextView merror;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        Intent intent = getIntent();
        if (intent != null) {
            Object user = intent.getParcelableExtra("user");
            if (user instanceof User) {
                myUser = (User) user;
            }
        }
        mProgressBar = findViewById(R.id.pb_loading_indicator);

        mToolbar = findViewById(R.id.back_action_bar);
        mToolbar.setTitle(getResources().getString(R.string.send_notification_title));
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationOnClickListener(view -> onBackPressed());

        mInputTo = findViewById(R.id.recipient);
        mInputMessage = findViewById(R.id.notification_message);

        mInputTo.setText(myUser.getName());

        merror = findViewById(R.id.errorCheck);
        mCheckNotificaiton = findViewById(R.id.notification);
        mCheckNotificaiton.setChecked(true);
        mCheckSMS = findViewById(R.id.sms);
        mCheckSMS.setChecked(true);
        mCheckEmail = findViewById(R.id.email);
        mCheckEmail.setChecked(true);

        mCheckNotificaiton.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                merror.setVisibility(View.INVISIBLE);
            }
        });
        mCheckSMS.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                merror.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * Inflate a menu with team_menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            if (attemptToSendNotification()) {
                if (checkPremission(Manifest.permission.SEND_SMS)) {
                    if (mCheckSMS.isChecked()) {
                        try {
                            sendSMS(myUser.getPhoneNumber(), mInputMessage.getText().toString());
                        } catch (Exception e) {
                            Toast.makeText(this, R.string.toast_send_sms_error, Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
                }
                if (mCheckNotificaiton.isChecked()) {
                    sendNotification(myUser.getEmail(), mInputMessage.getText().toString(), SharedPreferencesSaver.getLastTeamName(this));
                }
                if (mCheckEmail.isChecked()) {
                    sendEmail(myUser.getEmail(),getResources().getString(R.string.send_notification_email_subject) ,mInputMessage.getText().toString());
                }

            }

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean attemptToSendNotification() {
        if (!mCheckSMS.isChecked() && !mCheckNotificaiton.isChecked() && !mCheckEmail.isChecked()) {
            merror.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(this, R.string.send_notification_success, Toast.LENGTH_LONG).show();

    }

    private void sendEmail(String to, String subject, String body) {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setType("message/rfc822");
        i.setData(Uri.parse("mailto:" + to));
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT   , body);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotification(String email, String message,String title) {
        mProgressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SEND_MESSAGE_NOTIFICATION, response -> {
            JSONObject object = null;

            try {
                object = new JSONObject(response);
                if (!object.getBoolean("error")) {
                    Toast.makeText(this, R.string.send_notification_success,Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, object.getString("error_msg"),Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this,R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email", email);
                params.put("title",title);
                params.put("message", message);
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

    private boolean checkPremission(String permission) {
        int check = ContextCompat.checkSelfPermission(this,permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }
}
