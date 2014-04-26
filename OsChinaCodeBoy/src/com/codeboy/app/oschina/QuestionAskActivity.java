package com.codeboy.app.oschina;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

import com.codeboy.app.oschina.adapter.TabsFragmentPagerAdapter;
import com.codeboy.app.oschina.ui.QAJobFragment;
import com.codeboy.app.oschina.ui.QAOtherFragment;
import com.codeboy.app.oschina.ui.QAQuestionAskFragment;
import com.codeboy.app.oschina.ui.QAShareFragment;
import com.codeboy.app.oschina.ui.QASiteFragment;

/**
 * 类名 QuestionAskActivity.java</br>
 * 创建日期 2014年4月24日</br>
 * @author LeonLee (http://my.oschina.net/lendylongli)</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月24日 下午8:16:34</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 问答UI
 */
public final class QuestionAskActivity extends BaseActionBarActivity{

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

        mTabsAdapter.addTab(mTabHost.newTabSpec("question_ask").setIndicator(
        		getString(R.string.frame_title_question_ask)), 
        		QAQuestionAskFragment.class, null);
        
        mTabsAdapter.addTab(mTabHost.newTabSpec("share").setIndicator(
        		getString(R.string.frame_title_question_share)),
                QAShareFragment.class, null);
        
        mTabsAdapter.addTab(mTabHost.newTabSpec("other").setIndicator(
        		getString(R.string.frame_title_question_other)),
                QAOtherFragment.class, null);
        
        mTabsAdapter.addTab(mTabHost.newTabSpec("job").setIndicator(
        		getString(R.string.frame_title_question_job)),
                QAJobFragment.class, null);
        
        mTabsAdapter.addTab(mTabHost.newTabSpec("site").setIndicator(
        		getString(R.string.frame_title_question_site)),
                QASiteFragment.class, null);

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
