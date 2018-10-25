package eu.rkosir.feecollector.fragment.registrationFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.activity.LoginActivity;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class Registration_1st_step extends Fragment {
	private EditText mInputEmail;
	private Button mNext;
	private ProgressBar mProgressBar;
	private TextView mSignIn;

	public Registration_1st_step() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_registration_1st_step, container, false);
		mInputEmail = view.findViewById(R.id.email);
		mSignIn = view.findViewById(R.id.signIn);
		mNext = view.findViewById(R.id.next);
		mProgressBar = getActivity().findViewById(R.id.pb_loading_indicator);
		buttonListeners();
		return view;
	}

	/**
	 * append button listeners to all buttons
	 */
	private void buttonListeners() {
		mNext.setOnClickListener(v -> {
			if(attemptToRegister()) {
				Bundle bundle = new Bundle();
				bundle.putString("email", mInputEmail.getText().toString());
				Registration_2nd_step registration = new Registration_2nd_step();
				registration.setArguments(bundle);
				getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, registration, "registation_2_step").addToBackStack(null).commit();
			}
		});

		mSignIn.setOnClickListener(v -> {
			Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
			this.startActivity(intent);
			getActivity().finish();
		});

	}

	/**
	 * validate fields and request focus if any trouble
	 * @return true|false
	 */
	private boolean attemptToRegister() {
		mInputEmail.setError(null);
		String email = mInputEmail.getText().toString();

		boolean cancel = false;
		View focusView = null;
		if (TextUtils.isEmpty(email)) {
			mInputEmail.setError(getString(R.string.error_field_required),null);
			focusView = mInputEmail;
			cancel = true;
		} else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			mInputEmail.setError(getString(R.string.error_email_validation),null);
			focusView = mInputEmail;
			cancel = true;
		}
		if (cancel) {
			focusView.requestFocus();
			return false;
		}else
			return true;
	}


}
