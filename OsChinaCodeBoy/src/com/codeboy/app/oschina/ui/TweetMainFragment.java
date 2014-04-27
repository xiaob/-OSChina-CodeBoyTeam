package com.codeboy.app.oschina.ui;

import com.codeboy.app.oschina.R;
import com.codeboy.app.oschina.adapter.TabsFragmentPagerAdapter;

/**
 * 类名 TweetMainFragment.java</br>
 * 创建日期 2014年4月27日</br>
 * @author LeonLee (http://my.oschina.net/lendylongli)</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月27日 下午7:51:29</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 动弹主界面
 */
public class TweetMainFragment extends BaseMainFragment{
	
	public static TweetMainFragment newInstance() {
		return new TweetMainFragment();
	}

	@Override
	protected void onSetupTabAdapter(TabsFragmentPagerAdapter adapter) {
		adapter.addTab(mTabHost.newTabSpec("tweet_latest").setIndicator(
        		getString(R.string.frame_title_tweet_lastest)), 
        		TweetLatestFragment.class, null);
        
        adapter.addTab(mTabHost.newTabSpec("tweet_hot").setIndicator(
        		getString(R.string.frame_title_tweet_hot)),
                TweetHotFragment.class, null);
        
        adapter.addTab(mTabHost.newTabSpec("tweet_my").setIndicator(
        		getString(R.string.frame_title_tweet_my)),
                TweetMyFragment.class, null);
	}
}
