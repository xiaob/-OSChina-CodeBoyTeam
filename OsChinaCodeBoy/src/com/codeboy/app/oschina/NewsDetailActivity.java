package com.codeboy.app.oschina;

import com.codeboy.app.oschina.core.Contanst;
import com.codeboy.app.oschina.ui.NewsDetailBodyFragment;
import com.codeboy.app.oschina.ui.NewsDetailCommentFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Button;
import android.widget.EditText;

/**
 * 类名 NewsDetailActivity.java</br>
 * 创建日期 2014年4月28日</br>
 * @author LeonLee (http://my.oschina.net/lendylongli)</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月28日 下午11:49:49</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 资讯详情界面
 */
public class NewsDetailActivity extends BaseActionBarActivity{

	private ViewPager mViewPager;
	private EditText mEditText;
	private Button mButton;
	
	private NewsDetailPageAdapter mAdapter;
	
	private int mNewsId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mNewsId = getIntent().getIntExtra(Contanst.NEWS_ID_KEY, 0);
		mAdapter = new NewsDetailPageAdapter(getSupportFragmentManager(), mNewsId);
		
		setContentView(R.layout.activity_news_detail);
		
		initView();
	}
	
	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.newsdetail_viewpager);
		mEditText = (EditText) findViewById(R.id.newsdetail_edittext);
		mButton = (Button) findViewById(R.id.newdetail_button);
		
		mViewPager.setAdapter(mAdapter);
	}
	
	static class NewsDetailPageAdapter extends FragmentPagerAdapter {

		private int mNewsId;
		
		public NewsDetailPageAdapter(FragmentManager fm, int newid) {
			super(fm);
			mNewsId = newid;
		}

		@Override
		public Fragment getItem(int position) {
			if(position == 0) {
				return NewsDetailBodyFragment.newInstance(mNewsId);
			} else {
				return NewsDetailCommentFragment.newInstance(mNewsId);
			}
		}

		@Override
		public int getCount() {
			return 2;
		}
	}
}
