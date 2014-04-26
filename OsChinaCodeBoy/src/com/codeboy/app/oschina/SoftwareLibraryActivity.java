package com.codeboy.app.oschina;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.widget.TabHost;

import com.codeboy.app.oschina.adapter.TabsFragmentPagerAdapter;
import com.codeboy.app.oschina.ui.SoftwareChinaFragment;
import com.codeboy.app.oschina.ui.SoftwareHotFragment;
import com.codeboy.app.oschina.ui.SoftwareLastestFragment;
import com.codeboy.app.oschina.ui.SoftwareRecommonFragment;

/**
 * 类名 SoftwareLibraryActivity.java</br>
 * 创建日期 2014年4月25日</br>
 * @author LeonLee (http://my.oschina.net/lendylongli)</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月25日 上午12:10:39</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 开源软件
 */
public class SoftwareLibraryActivity extends BaseActionBarActivity {

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

        mTabsAdapter.addTab(mTabHost.newTabSpec("software_recommon").setIndicator(
        		getString(R.string.frame_title_software_recommon)), 
        		SoftwareRecommonFragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("software_lastest").setIndicator(
        		getString(R.string.frame_title_software_lastest)),
                SoftwareLastestFragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("software_hot").setIndicator(
        		getString(R.string.frame_title_software_hot)),
        		SoftwareHotFragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("software_china").setIndicator(
        		getString(R.string.frame_title_software_china)),
        		SoftwareChinaFragment.class, null);

        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
	}
	
	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }
}
