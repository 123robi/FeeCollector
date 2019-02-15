package eu.rkosir.feecollector.activity.teamManagement;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;

public class Payment extends AppCompatActivity {

    private TextInputEditText mAccountPrefix, mAccountNumber, mBankCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        mAccountNumber = findViewById(R.id.account_number);
        mAccountPrefix = findViewById(R.id.account_prefix);
        mBankCode = findViewById(R.id.bank_code);
        getSupportActionBar().setTitle("Create Payment");
    }

    /**
     * Sending a Volley Post Request to create a payment using 3 parameter: team_id, addNum, prefix
     */
    private void createPayment() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.ULR_CREATE_PAYMENT, response -> {
            JSONObject object = null;

            try {
                object = new JSONObject(response);
                if (!object.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), R.string.toast_successful_payment_creation,Toast.LENGTH_LONG).show();
                    this.finish();
                } else {
                    Toast.makeText(getApplicationContext(), object.getString("error_msg"),Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(getApplicationContext(),R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("account_number", mAccountNumber.getText().toString());
                params.put("account_prefix", mAccountPrefix.getText().toString());
                params.put("bank_code", mBankCode.getText().toString());
                params.put("connection_number", SharedPreferencesSaver.getLastTeamID(Payment.this));
                return params;
            }
        };

        RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        requestQueue.add(stringRequest);
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
            createPayment();

        }
        return super.onOptionsItemSelected(item);
    }
}
