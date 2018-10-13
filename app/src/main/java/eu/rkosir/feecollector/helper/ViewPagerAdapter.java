package eu.rkosir.feecollector.helper;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import eu.rkosir.feecollector.fragment.teamManagementFragment.AddMember;

public class ViewPagerAdapter extends FragmentPagerAdapter {

	private final List<Fragment> fragmentList = new ArrayList<>();
	private final List<String> fragmetnListTitles = new ArrayList<>();
	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return fragmentList.get(position);
	}

	@Override
	public int getCount() {
		return fragmetnListTitles.size();
	}

	@Nullable
	@Override
	public CharSequence getPageTitle(int position) {
		return fragmetnListTitles.get(position);
	}

	public void addFragment(Fragment fragment, String title) {
		fragmentList.add(fragment);
		fragmetnListTitles.add(title);
	}
}
