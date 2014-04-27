package com.codeboy.app.oschina.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.codeboy.app.oschina.BaseFragment;
import com.codeboy.app.oschina.R;
import com.codeboy.app.oschina.adapter.TabsFragmentPagerAdapter;

/**
 * 类名 BaseMainFragment.java</br>
 * 创建日期 2014年4月27日</br>
 * @author LeonLee (http://my.oschina.net/lendylongli)</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月27日 下午4:29:16</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 各类主界面的基类
 */
public abstract class BaseMainFragment extends BaseFragment{

	protected TabHost mTabHost;
	protected ViewPager  mViewPager;
	protected TabsFragmentPagerAdapter mTabsAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_viewpager_tabs, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mTabHost = (TabHost)view.findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mViewPager = (ViewPager)view.findViewById(R.id.viewpager);

        mTabsAdapter = new TabsFragmentPagerAdapter(getChildFragmentManager(), mTabHost, mViewPager);

        onSetupTabAdapter(mTabsAdapter);

        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }
	
	protected abstract void onSetupTabAdapter(TabsFragmentPagerAdapter adapter);
}
