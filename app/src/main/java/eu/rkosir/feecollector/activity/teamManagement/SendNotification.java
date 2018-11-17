package eu.rkosir.feecollector.activity.teamManagement;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.entity.User;

public class SendNotification extends AppCompatActivity {
    private static final int SMS_PERMISSION_CODE = 61231;
    private Toolbar mToolbar;
    private User myUser;
    private TextInputEditText mInputTo, mInputMessage;

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

        mToolbar = findViewById(R.id.back_action_bar);
        mToolbar.setTitle(getResources().getString(R.string.send_notification_title));
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationOnClickListener(view -> onBackPressed());

        mInputTo = findViewById(R.id.recipient);
        mInputMessage = findViewById(R.id.notification_message);

        mInputTo.setText(myUser.getName());
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
            if (checkPremission(Manifest.permission.SEND_SMS)) {
                sendSMS(myUser.getPhoneNumber());
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendSMS(String phoneNumber) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, mInputMessage.getText().toString(), null, null);
        Toast.makeText(this, R.string.send_notification_success, Toast.LENGTH_LONG).show();
    }

    private boolean checkPremission(String permission) {
        int check = ContextCompat.checkSelfPermission(this,permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }
}
