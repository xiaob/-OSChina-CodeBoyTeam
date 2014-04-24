package com.codeboy.app.oschina.ui;

import java.util.List;

import com.codeboy.app.oschina.R;

import android.os.Bundle;
import android.widget.BaseAdapter;
import net.oschina.app.adapter.ListViewBlogAdapter;
import net.oschina.app.bean.Blog;
import net.oschina.app.bean.BlogList;
import net.oschina.app.core.AppException;

/**
 * 类名 NewsRecommonFragment.java</br>
 * 创建日期 2014年4月24日</br>
 * @author LeonLee</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月24日 下午1:34:30</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 推荐阅读
 */
public class NewsRecommonFragment extends BaseSwipeRefreshFragment<Blog, BlogList> {

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
		return BlogList.TYPE_RECOMMEND;
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