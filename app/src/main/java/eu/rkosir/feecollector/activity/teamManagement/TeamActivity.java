package eu.rkosir.feecollector.activity.teamManagement;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

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
	private String team_id, title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_team);
		toolbar = findViewById(R.id.back_action_bar);
		toolbar.setTitle(SharedPreferencesSaver.getLastTeamName(this));
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
}
