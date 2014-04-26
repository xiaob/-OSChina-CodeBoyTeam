package com.codeboy.app.oschina;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

import com.codeboy.app.oschina.adapter.TabsFragmentPagerAdapter;
import com.codeboy.app.oschina.ui.TweetHotFragment;
import com.codeboy.app.oschina.ui.TweetLatestFragment;
import com.codeboy.app.oschina.ui.TweetMyFragment;

/**
 * 类名 TweetActivity.java</br>
 * 创建日期 2014年4月24日</br>
 * @author LeonLee (http://my.oschina.net/lendylongli)</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月24日 下午10:09:25</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 动弹界面
 */
public class TweetActivity extends BaseActionBarActivity{

	TabHost mTabHost;
    ViewPager  mViewPager;
    TabsFragmentPagerAdapter mTabsAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewpager_tabs);
		
		ActionBar bar = getSupportActionBar();
		int flags = ActionBar.DISPLAY_HOME_AS_UP;
		int change = bar.getDisplayOptions() ^ flags;
        bar.setDisplayOptions(change, flags);
		
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mViewPager = (ViewPager)findViewById(R.id.viewpager);

        mTabsAdapter = new TabsFragmentPagerAdapter(this, mTabHost, mViewPager);

        mTabsAdapter.addTab(mTabHost.newTabSpec("tweet_latest").setIndicator(
        		getString(R.string.frame_title_tweet_lastest)), 
        		TweetLatestFragment.class, null);
        
        mTabsAdapter.addTab(mTabHost.newTabSpec("tweet_hot").setIndicator(
        		getString(R.string.frame_title_tweet_hot)),
                TweetHotFragment.class, null);
        
        mTabsAdapter.addTab(mTabHost.newTabSpec("tweet_my").setIndicator(
        		getString(R.string.frame_title_tweet_my)),
                TweetMyFragment.class, null);

        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }
	
	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == R.id.action_question_ask) {
			
		} else if(id == R.id.action_tweet) {
			
		} else if(id == R.id.action_active) {
			
		} else if(id == R.id.action_myinfo) {
			
		} else if (id == R.id.action_settings) {
			return true;
		} 
		return super.onOptionsItemSelected(item);
	}
}
