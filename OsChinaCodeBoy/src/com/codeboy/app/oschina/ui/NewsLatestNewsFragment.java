package com.codeboy.app.oschina.ui;

import java.util.ArrayList;
import java.util.List;

import net.oschina.app.adapter.ListViewNewsAdapter;
import net.oschina.app.bean.News;
import net.oschina.app.bean.NewsList;
import net.oschina.app.core.AppException;
import android.os.Bundle;
import android.os.Handler;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.codeboy.app.library.util.L;
import com.codeboy.app.oschina.BaseSwipeRefreshFragment;
import com.codeboy.app.oschina.R;

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
	
	private List<News> mNews = new ArrayList<News>();
	private ListViewNewsAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mAdapter = new ListViewNewsAdapter(getActivity(), mNews, R.layout.news_listitem);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					NewsList newsList = getOsChinaApplication().getNewsList(0, 0, true);
					mNews.addAll(newsList.getNewslist());
					getActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							mAdapter.notifyDataSetChanged();
						}
					});
				} catch (AppException e) {
					e.printStackTrace();
				}
			}
		}).start();;
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
		return mAdapter;
	}
}
