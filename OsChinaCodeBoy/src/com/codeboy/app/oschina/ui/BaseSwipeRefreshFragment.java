package com.codeboy.app.oschina.ui;

import java.util.ArrayList;
import java.util.List;

import net.oschina.app.bean.Entity;
import net.oschina.app.bean.Notice;
import net.oschina.app.bean.PageList;
import net.oschina.app.core.AppContext;
import net.oschina.app.widget.NewDataToast;

import com.codeboy.app.library.util.L;
import com.codeboy.app.oschina.BaseFragment;
import com.codeboy.app.oschina.OSChinaApplication;
import com.codeboy.app.oschina.R;
import com.codeboy.app.oschina.core.DataRequestThreadHandler;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * 类名 BaseSwipeRefreshFragment.java</br>
 * 创建日期 2014年4月21日</br>
 * @author LeonLee</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月21日 下午11:53:10</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 下拉刷新界面的基类
 */
public abstract class BaseSwipeRefreshFragment <Data extends Entity, Result extends PageList<Data>> 
	extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, OnItemClickListener,
	AbsListView.OnScrollListener {
	
	public final static int LISTVIEW_ACTION_INIT = 0x01;
	public final static int LISTVIEW_ACTION_REFRESH = 0x02;
	public final static int LISTVIEW_ACTION_SCROLL = 0x03;

	protected OSChinaApplication mApplication;
	
	protected SwipeRefreshLayout mSwipeRefreshLayout;
	protected ListView mListView;
	private View mFooterView;
	private BaseAdapter mAdapter;
	
	private List<Data> mDataList = new ArrayList<Data>();
	private int mSumData;
	
	private DataRequestThreadHandler mRequestThreadHandler = new DataRequestThreadHandler();
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mApplication = (OSChinaApplication) activity.getApplication();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = getAdapter(mDataList);
		/** 初始化数据*/
		loadList(getDataTag(), 0, LISTVIEW_ACTION_INIT);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mRequestThreadHandler.quit();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mFooterView = inflater.inflate(R.layout.listview_footer, null);
		return inflater.inflate(R.layout.fragment_base_swiperefresh, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_swiperefreshlayout);
		mListView = (ListView)view.findViewById(R.id.fragment_listview);
		
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorScheme(
				android.R.color.holo_blue_bright, 
	            android.R.color.holo_green_light, 
	            android.R.color.holo_orange_light, 
	            android.R.color.holo_red_light);
		
		setupListView();
	}
	
	/** 初始化ListView*/
	protected void setupListView() {
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);
		mListView.addFooterView(mFooterView);
		mListView.setAdapter(mAdapter);
	}
	
	/** 获取适配器*/
	public abstract BaseAdapter getAdapter(List<Data> list);
	public abstract Object getDataTag();
	/** 异步加载数据*/
	protected abstract Result asyncLoadList(Object tag, int page, boolean reflash);

	@Override
	public void onRefresh() {
		loadList(getDataTag(), 0, LISTVIEW_ACTION_REFRESH);
	}
	
	/** 加载下一页*/
	protected void onLoadNextPage() {
		// 当前pageIndex
		int pageIndex = mSumData / AppContext.PAGE_SIZE;
		if(L.Debug) {
			L.d("加载下一页:" + pageIndex);
		}
		loadList(getDataTag(), pageIndex, LISTVIEW_ACTION_SCROLL);
	}
	
	/** 
	 * 加载数据
	 * @param tag
	 * @param page
	 * @param action
	 * */
	void loadList(Object tag, int page, int action) {
		mRequestThreadHandler.request(page, new AsyncDataHandler(tag, page, action));
	}
	
	/** 设置正在加载的状态*/
	void setLoadingState() {
		if(mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setRefreshing(true);
			//防止多次重复刷新
			mSwipeRefreshLayout.setEnabled(false);
		}
	}
	
	/** 设置加载完毕的状态*/
	void setLoadedState() {
		if(mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setRefreshing(false);
			mSwipeRefreshLayout.setEnabled(true);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		Adapter adapter = view.getAdapter();
		if(adapter == null || adapter.getCount() == 0) {
			return;
		}
		// 判断是否滚动到底部
		boolean scrollEnd = false;
		try {
			if (view.getPositionForView(mFooterView) == view
					.getLastVisiblePosition())
				scrollEnd = true;
		} catch (Exception e) {
			scrollEnd = false;
		}
		
		if (scrollEnd) {
			onLoadNextPage();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}
	
	private class AsyncDataHandler implements DataRequestThreadHandler.AsyncDataHandler<Result> {

		private Object mTag;
		private int mPage;
		private int mAction;
		
		AsyncDataHandler(Object tag, int page, int action) {
			mTag = tag;
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
		public Result execute() {
			if(L.Debug) {
				L.d("正在加载:" + mPage);
			}
			boolean reflash = true;
			if(mAction == LISTVIEW_ACTION_INIT) {
				reflash = false;
			}
			return asyncLoadList(mTag, mPage, reflash);
		}

		@Override
		public void onPostExecute(Result result) {
			if(mAction == LISTVIEW_ACTION_REFRESH) {
				setLoadedState();
			}
			if(result == null) {
				return;
			}
			if(L.Debug) {
				L.d("Load Page:" + mPage);
				L.d("NewsCount--->" + result.getCount());
			}
			Notice notice = null;
			if(mPage == 0) {
				int newdata = 0;
				notice = result.getNotice();
				mSumData = result.getPageSize();
				if (mAction == LISTVIEW_ACTION_REFRESH) {
					if (mDataList.size() > 0) {
						for (Data data1 : result.getList()) {
							boolean b = false;
							for (Data data2 : mDataList) {
								if (data1.getId() == data2.getId()) {
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
										newdata), mApplication.isAppSound()).show();
					} else {
						NewDataToast.makeText(getActivity(),
								getString(R.string.new_data_toast_none), false).show();
					}
				}
				mDataList.clear();// 先清除原有数据
				mDataList.addAll(result.getList());
			} else {
				mSumData += result.getPageSize();
				if (mDataList.size() > 0) {
					for (Data data1 : result.getList()) {
						boolean b = false;
						for (Data data2 : mDataList) {
							if (data1.getId() == data2.getId()) {
								b = true;
								break;
							}
						}
						if (!b)
							mDataList.add(data1);
					}
				} else {
					mDataList.addAll(result.getList());
				}
			}
			mAdapter.notifyDataSetChanged();
		}
	}
}