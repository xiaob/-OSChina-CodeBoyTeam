package com.codeboy.app.oschina;


import com.codeboy.app.oschina.adapter.TabsFragmentPagerAdapter;
import com.codeboy.app.oschina.ui.NewsLatestNewsFragment;
import com.codeboy.app.oschina.ui.NewsRecentBlogPostsFragment;
import com.codeboy.app.oschina.ui.NewsRecommonFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

public class MainActivity extends BaseActionBarActivity {
	
	TabHost mTabHost;
    ViewPager  mViewPager;
    TabsFragmentPagerAdapter mTabsAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewpager_tabs);
		
		/*if(savedInstanceState == null) {
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.container, NewsLatestNewsFragment.newInstance())
			.commit();
		}*/
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mViewPager = (ViewPager)findViewById(R.id.viewpager);

        mTabsAdapter = new TabsFragmentPagerAdapter(this, mTabHost, mViewPager);

        mTabsAdapter.addTab(mTabHost.newTabSpec("news").setIndicator(
        		getString(R.string.frame_title_news_lastest)), NewsLatestNewsFragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("blog").setIndicator(
        		getString(R.string.frame_title_news_blog)),
                NewsRecentBlogPostsFragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("recommon").setIndicator(
        		getString(R.string.frame_title_news_recommend)),
                NewsRecommonFragment.class, null);

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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == R.id.action_question_ask) {
			Intent intent = new Intent(this, QuestionAskActivity.class);
			startActivity(intent);
		} else if(id == R.id.action_tweet) {
			
		} else if(id == R.id.action_active) {
			
		} else if(id == R.id.action_myinfo) {
			
		} else if (id == R.id.action_settings) {
			return true;
		} 
		return super.onOptionsItemSelected(item);
	}
}