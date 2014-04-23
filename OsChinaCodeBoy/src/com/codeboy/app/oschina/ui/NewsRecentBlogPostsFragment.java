package com.codeboy.app.oschina.ui;

import java.util.List;

import net.oschina.app.adapter.ListViewBlogAdapter;
import net.oschina.app.bean.Blog;
import net.oschina.app.bean.BlogList;
import net.oschina.app.core.AppException;
import android.os.Bundle;
import android.widget.BaseAdapter;

import com.codeboy.app.oschina.R;

/**
 * 类名 NewsRecentBlogPostsFragment.java</br>
 * 创建日期 2014年4月20日</br>
 * @author LeonLee</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月20日 下午11:33:52</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 最新博客
 */
public class NewsRecentBlogPostsFragment extends BaseSwipeRefreshFragment<Blog, BlogList> {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public BaseAdapter getAdapter(List<Blog> list) {
		return new ListViewBlogAdapter(
				getActivity(), 0, list, R.layout.blog_listitem);
	}

	@Override
	public Object getDataTag() {
		return "";
	}

	@Override
	protected BlogList asyncLoadList(Object tag, int page, boolean reflash) {
		try {
			return mApplication.getBlogList((String)tag, page, reflash);
		} catch (AppException e) {
			e.printStackTrace();
		}
		return null;
	}
}
