package com.codeboy.app.oschina.ui;

import java.util.ArrayList;
import java.util.List;

import net.oschina.app.adapter.ListViewNewsAdapter;
import net.oschina.app.bean.News;
import net.oschina.app.bean.NewsList;
import net.oschina.app.bean.Notice;
import net.oschina.app.core.AppContext;
import net.oschina.app.core.AppException;
import net.oschina.app.widget.NewDataToast;
import android.os.Bundle;
import android.widget.BaseAdapter;

import com.codeboy.app.library.util.L;
import com.codeboy.app.oschina.BaseSwipeRefreshFragment;
import com.codeboy.app.oschina.OSChinaApplication;
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
	
	private int lvNewsSumData;
	
	private OSChinaApplication application;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = getOsChinaApplication();
		mAdapter = new ListViewNewsAdapter(getActivity(), mNews, R.layout.news_listitem);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mRequestThreadHandler.quit();
	}
	
	@Override
	public void onInitViewFinish() {
		mRequestThreadHandler.request(0, new NewsAsyncDataHandler(LISTVIEW_ACTION_INIT, 0));
	}
	
	@Override
	public void onRefresh() {
		mRequestThreadHandler.request(0, new NewsAsyncDataHandler(LISTVIEW_ACTION_REFRESH, 0));
	}

	@Override
	public void onLoadNextPage() {
		// 当前pageIndex
		int pageIndex = lvNewsSumData / AppContext.PAGE_SIZE;
		if(L.Debug) {
			L.d("加载下一页:" + pageIndex);
		}
		mRequestThreadHandler.request(pageIndex, new NewsAsyncDataHandler(LISTVIEW_ACTION_SCROLL, pageIndex));
	}

	@Override
	public BaseAdapter getListViewAdapter() {
		return mAdapter;
	}
	
	private class NewsAsyncDataHandler implements DataRequestThreadHandler.AsyncDataHandler<NewsList> {

		private int mPage;
		private int mAction;
		
		NewsAsyncDataHandler(int action, int page) {
			mAction = action;
			mPage = page;
		}
		
		@Override
		public void onPreExecute() {
			if(mAction == LISTVIEW_ACTION_REFRESH) {
				setLoadingState();
			}
		}
		
		@Override
		public NewsList execute() {
			if(L.Debug) {
				L.d("正在加载:" + mPage);
			}
			try {
				return application.getNewsList(CATELOG, mPage, true);
			} catch (AppException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public void onPostExecute(NewsList result) {
			if(isDetached()) {
				return;
			}
			if(mAction == LISTVIEW_ACTION_REFRESH) {
				setLoadedState();
			}
			if(result == null) {
				return;
			}
			if(L.Debug) {
				L.d("Load Page:" + mPage);
				L.d("NewsCount--->" + result.getNewsCount());
			}
			Notice notice = null;
			if(mPage == 0) {
				int newdata = 0;
				notice = result.getNotice();
				lvNewsSumData = result.getPageSize();
				if (mAction == LISTVIEW_ACTION_REFRESH) {
					if (mNews.size() > 0) {
						for (News news1 : result.getNewslist()) {
							boolean b = false;
							for (News news2 : mNews) {
								if (news1.getId() == news2.getId()) {
									b = true;
									break;
								}
							}
							if (!b)
								newdata++;
						}
					} else {
						newdata = result.getPageSize();
					}
					// 提示新加载数据
					if (newdata > 0) {
						NewDataToast.makeText( getActivity(),
										getString(R.string.new_data_toast_message,
										newdata), application.isAppSound()).show();
					} else {
						NewDataToast.makeText(getActivity(),
								getString(R.string.new_data_toast_none), false).show();
					}
				}
				mNews.clear();// 先清除原有数据
				mNews.addAll(result.getNewslist());
			} else {
				lvNewsSumData += result.getPageSize();
				if (mNews.size() > 0) {
					for (News news1 : result.getNewslist()) {
						boolean b = false;
						for (News news2 : mNews) {
							if (news1.getId() == news2.getId()) {
								b = true;
								break;
							}
						}
						if (!b)
							mNews.add(news1);
					}
				} else {
					mNews.addAll(result.getNewslist());
				}
			}
			mAdapter.notifyDataSetChanged();
		}
	}
}
