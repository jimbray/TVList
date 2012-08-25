package com.jim.tvs;

import net.youmi.android.AdManager;
import net.youmi.android.AdView;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jim.data.DataProviderManager;

public class MainActivity extends SherlockFragmentActivity implements TabListener {
	public static final int Theme = R.style.Theme_Sherlock_Light_DarkActionBar;
	
	private static final int TAB_COUNT = 2;
	
	private ViewPager mPager = null;
	private DSFragmentPagerAdapter mPagerAdapter = null;
	
	private Fragment mPersonalFg = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setTheme(Theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initAdView();
        
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new MainAdapter(getSupportFragmentManager(), TAB_COUNT);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener((OnPageChangeListener) mPagerAdapter);
        
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        setUpTabs();
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	getSupportMenuInflater().inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.menu_about:
				Intent intent = new Intent();
				intent.setClass(this, AboutActivity.class);
				startActivity(intent);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initAdView() {
    	AdManager.init(this, "653f7251b5216ee5", "dec6c04529c98f17", 10, false);
    	
    	LinearLayout adViewLayout = (LinearLayout) findViewById(R.id.adViewLayout);
    	adViewLayout.addView(new AdView(this),
    			new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }
    
    private void setUpTabs() {
    	setUpTabStream();
    	setUpTabPersonal();
    }
    
    private void setUpTabStream() {
    	Tab tab = getSupportActionBar().newTab();
    	tab.setText(getString(R.string.tab_stream));
    	tab.setTabListener(this);
    	getSupportActionBar().addTab(tab);
    }
    
    private void setUpTabPersonal() {
    	Tab tab = getSupportActionBar().newTab();
    	tab.setText(getString(R.string.tab_personal));
    	tab.setTabListener(this);
    	getSupportActionBar().addTab(tab);
    }
    
    private class MainAdapter extends DSFragmentPagerAdapter implements OnPageChangeListener {
    	private int mPageNum;

		public MainAdapter(FragmentManager fm) {
			super(fm);
		}
		
		public MainAdapter(FragmentManager fm, int pageNum) {
			super(fm);
			this.mPageNum = pageNum;
		}

		@Override
		public Fragment getItem(int position) {
			switch(position) {
				case 0:
					return FragmentStream.newInstance();
					
				case 1:
					return (mPersonalFg = FragmentPersonal.newInstance());
			}
			return FragmentStream.newInstance();
		}

		@Override
		public int getCount() {
			return mPageNum;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int position) {
			getSupportActionBar().setSelectedNavigationItem(position);
			if(position == 1) {
				if(mPersonalFg != null) {
					((FragmentPersonal) mPersonalFg).updateData();
				}
			}
		}
    	
    }

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if(mPager.getCurrentItem() != tab.getPosition()) {
			mPager.setCurrentItem(tab.getPosition());
		}
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
}