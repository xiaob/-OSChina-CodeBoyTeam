package com.codeboy.app.oschina.ui;

import com.codeboy.app.oschina.R;
import com.codeboy.app.oschina.adapter.TabsFragmentPagerAdapter;

/**
 * 类名 NewsMainFragment.java</br>
 * 创建日期 2014年4月27日</br>
 * @author LeonLee (http://my.oschina.net/lendylongli)</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月27日 下午4:28:25</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 资讯主界面
 */
public class NewsMainFragment extends BaseMainFragment{

	public static NewsMainFragment newInstance() {
		return new NewsMainFragment();
	}

	@Override
	protected void onSetupTabAdapter(TabsFragmentPagerAdapter adapter) {
		adapter.addTab(mTabHost.newTabSpec("news").setIndicator(
        		getString(R.string.frame_title_news_lastest)), NewsLatestNewsFragment.class, null);
		adapter.addTab(mTabHost.newTabSpec("blog").setIndicator(
        		getString(R.string.frame_title_news_blog)),
                NewsRecentBlogPostsFragment.class, null);
		adapter.addTab(mTabHost.newTabSpec("recommon").setIndicator(
        		getString(R.string.frame_title_news_recommend)),
                NewsRecommonFragment.class, null);
	}
}
