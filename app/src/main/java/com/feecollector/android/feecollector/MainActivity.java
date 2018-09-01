package com.feecollector.android.feecollector;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.feecollector.android.feecollector.BackgroundTasks.CreateNewUser;
import com.feecollector.android.feecollector.User.Entity.User;

public class MainActivity extends AppCompatActivity {

	private String TAG = MainActivity.class.getSimpleName();

	private EditText inputName;
	private EditText inputSurname;
	private EditText inputDescription;
	private Button createUserbtn;
	private Context context;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;

		inputName = findViewById(R.id.name);
		inputSurname = findViewById(R.id.surname);
		inputDescription = findViewById(R.id.description);
		createUserbtn = findViewById(R.id.createUser);

		createUserbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				User user = new User(inputName.getText().toString(),inputSurname.getText().toString(), inputDescription.getText().toString());
				new CreateNewUser(context,user).execute();
			}
		});
	}

}
