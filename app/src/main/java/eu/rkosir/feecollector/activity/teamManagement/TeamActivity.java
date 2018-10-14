package eu.rkosir.feecollector.activity.teamManagement;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.fragment.teamManagementFragment.AddFeeToMember;
import eu.rkosir.feecollector.fragment.teamManagementFragment.AddMember;
import eu.rkosir.feecollector.fragment.teamManagementFragment.Summary;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.ViewPagerAdapter;

public class TeamActivity extends AppCompatActivity {

	private TabLayout tabLayout;
	private ViewPager viewPager;
	private Toolbar toolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_team);
		toolbar = findViewById(R.id.back_action_bar);
		toolbar.setTitle(SharedPreferencesSaver.getLastTeamName(this));
		setSupportActionBar(toolbar);
		toolbar.setNavigationOnClickListener(view -> {
			onBackPressed();
		});

		tabLayout = findViewById(R.id.navigation_top);
		viewPager = findViewById(R.id.content);
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(new Summary(), "Summary");
		adapter.addFragment(new AddFeeToMember(), "Add Fee");
		adapter.addFragment(new AddMember(), "Add Member");

		viewPager.setAdapter(adapter);
		tabLayout.setupWithViewPager(viewPager);
	}

	@Override
	public void onBackPressed() {
		SharedPreferencesSaver.setLastTeamId(this,null);
		SharedPreferencesSaver.setLastTeamName(this,null);
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.team_menu,menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		ClipboardManager clipboard = (ClipboardManager)
				this.getSystemService(Context.CLIPBOARD_SERVICE);
		if (item.getItemId() == R.id.copy) {
			Toast.makeText(this, "Your team_id " + SharedPreferencesSaver.getLastTeamID(this) + " has been copied to clipboard",Toast.LENGTH_LONG).show();
			ClipData clip = ClipData.newPlainText("id",SharedPreferencesSaver.getLastTeamID(this));
			clipboard.setPrimaryClip(clip);
		}
		return super.onOptionsItemSelected(item);
	}
}
