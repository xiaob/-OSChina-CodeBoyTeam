package com.codeboy.app.oschina;


import com.codeboy.app.oschina.adapter.TabsFragmentPagerAdapter;
import com.codeboy.app.oschina.ui.NewsLatestNewsFragment;
import com.codeboy.app.oschina.ui.NewsRecentBlogPostsFragment;
import com.codeboy.app.oschina.ui.NewsRecommonFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;


/**
 * 类名 MainActivity.java</br>
 * 创建日期 2014年4月26日</br>
 * @author LeonLee (http://my.oschina.net/lendylongli)</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月26日 下午12:15:10</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 类的描述
 */
public class MainActivity extends BaseActionBarActivity {
	
	private TabHost mTabHost;
	private ViewPager  mViewPager;
	private TabsFragmentPagerAdapter mTabsAdapter;
    
    private DrawerLayout mDrawerLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		
		findViewById(R.id.drawer_login_button).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, LoginActivity.class);
				startActivityForResult(intent, 12345);
			}
		});
		
		findViewById(R.id.drawer_userinfo_button).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
				startActivity(intent);
			}
		});
		
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
			Intent intent = new Intent(this, TweetActivity.class);
			startActivity(intent);
		} else if(id == R.id.action_software) {
			Intent intent = new Intent(this, SoftwareLibraryActivity.class);
			startActivity(intent);
		} else if(id == R.id.action_active) {
			
		} else if(id == R.id.action_myinfo) {
			
		} else if (id == R.id.action_settings) {
			return true;
		} 
		return super.onOptionsItemSelected(item);
	}
}