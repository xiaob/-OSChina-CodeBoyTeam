package com.codeboy.app.oschina;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
public abstract class BaseSwipeRefreshFragment extends BaseFragment 
	implements SwipeRefreshLayout.OnRefreshListener, OnItemClickListener{

	protected SwipeRefreshLayout mSwipeRefreshLayout;
	protected ListView mListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_base_swiperefresh, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_swiperefreshlayout);
		mListView = (ListView)view.findViewById(R.id.fragment_listview);
		
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, 
	            android.R.color.holo_green_light, 
	            android.R.color.holo_orange_light, 
	            android.R.color.holo_red_light);
		
		setupListView();
		
		onInitViewFinish();
	}
	
	/** 初始化ListView*/
	protected void setupListView() {
		mListView.setOnItemClickListener(this);
		mListView.setAdapter(getListViewAdapter());
	}
	
	public abstract BaseAdapter getListViewAdapter();

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
	}
	
	/** 初始化界面结束*/
	public void onInitViewFinish() {
		
	}

	@Override
	public void onRefresh() {
		
	}
	
	/** 设置正在加载的状态*/
	public void setLoadingState() {
		if(mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setRefreshing(true);
		}
	}
	
	/** 设置加载完毕的状态*/
	public void setLoadedState() {
		if(mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}
}
