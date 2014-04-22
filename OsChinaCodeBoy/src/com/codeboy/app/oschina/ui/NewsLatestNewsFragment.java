package com.codeboy.app.oschina.ui;

import java.util.ArrayList;
import java.util.List;

import net.oschina.app.adapter.ListViewNewsAdapter;
import net.oschina.app.bean.News;
import net.oschina.app.bean.NewsList;
import net.oschina.app.core.AppException;
import android.os.Bundle;
import android.widget.BaseAdapter;

import com.codeboy.app.library.util.L;
import com.codeboy.app.oschina.BaseSwipeRefreshFragment;
import com.codeboy.app.oschina.R;
import com.codeboy.app.oschina.core.DataRequestThreadHandler;

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
	
	final static int CATELOG = 0;
	
	private DataRequestThreadHandler mRequestThreadHandler = new DataRequestThreadHandler();
	
	private List<News> mNews = new ArrayList<News>();
	private ListViewNewsAdapter mAdapter;
	
	private int mCurrentPage = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mAdapter = new ListViewNewsAdapter(getActivity(), mNews, R.layout.news_listitem);
	}
	
	@Override
	public void onInitViewFinish() {
		mRequestThreadHandler.request(new NewsAsyncDataHandler(0));
	}
	
	@Override
	public void onRefresh() {
		mRequestThreadHandler.request(new NewsAsyncDataHandler(++mCurrentPage));
	}

	@Override
	public BaseAdapter getListViewAdapter() {
		return mAdapter;
	}
	
	private class NewsAsyncDataHandler implements DataRequestThreadHandler.AsyncDataHandler<NewsList> {

		private int mPage;
		
		NewsAsyncDataHandler(int page) {
			mPage = page;
		}
		
		@Override
		public void onPreExecute() {
			setLoadingState();
		}
		
		@Override
		public NewsList execute() {
			if(L.Debug) {
				L.d("正在加载:" + mPage);
			}
			try {
				return getOsChinaApplication().getNewsList(CATELOG, mPage, true);
			} catch (AppException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public void onPostExecute(NewsList result) {
			setLoadedState();
			if(result == null) {
				return;
			}
			if(L.Debug) {
				L.d("Load Page:" + mPage);
				L.d("NewsCount--->" + result.getNewsCount());
			}
			mNews.addAll(result.getNewslist());
			mAdapter.notifyDataSetChanged();
		}
	}
}
