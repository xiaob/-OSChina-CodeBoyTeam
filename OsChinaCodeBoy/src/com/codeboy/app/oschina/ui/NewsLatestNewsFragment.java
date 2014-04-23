package com.codeboy.app.oschina.ui;

import java.util.List;

import net.oschina.app.adapter.ListViewNewsAdapter;
import net.oschina.app.bean.News;
import net.oschina.app.bean.NewsList;
import net.oschina.app.core.AppException;
import android.os.Bundle;
import android.widget.BaseAdapter;

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
public class NewsLatestNewsFragment extends BaseSwipeRefreshFragment<News, NewsList> {
	
	public static NewsLatestNewsFragment newInstance() {
		return new NewsLatestNewsFragment();
	}
	
	final static int CATELOG = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public BaseAdapter getAdapter(List<News> list) {
		return new ListViewNewsAdapter(getActivity(), list, R.layout.news_listitem);
	}

	@Override
	public Integer getDataTag() {
		return CATELOG;
	}

	@Override
	protected NewsList asyncLoadList(Object tag, int page, boolean reflash) {
		try {
			return mApplication.getNewsList((Integer)tag, page, reflash);
		} catch (AppException e) {
			e.printStackTrace();
		}
		return null;
	}
}
