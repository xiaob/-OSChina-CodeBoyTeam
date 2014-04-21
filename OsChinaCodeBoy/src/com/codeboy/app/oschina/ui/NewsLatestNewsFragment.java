package com.codeboy.app.oschina.ui;

import android.os.Bundle;
import android.os.Handler;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.codeboy.app.oschina.BaseSwipeRefreshFragment;

/**
 * 类名 NewsLatestNewsFragment.java</br>
 * 创建日期 2014年4月20日</br>
 * @author LeonLee</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月20日 下午11:34:05</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 最新资讯
 */
public class NewsLatestNewsFragment extends BaseSwipeRefreshFragment {
	
	public static NewsLatestNewsFragment newInstance() {
		return new NewsLatestNewsFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onRefresh() {
		Toast.makeText(getActivity(), "刷新中...", Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mSwipeRefreshLayout.setRefreshing(false);
				Toast.makeText(getActivity(), "刷新结束", Toast.LENGTH_SHORT).show();
			}
		}, 2000);
	}

	@Override
	public BaseAdapter getListViewAdapter() {
		return null;
	}
	
}
