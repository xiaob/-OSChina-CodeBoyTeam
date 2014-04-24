package com.codeboy.app.oschina.ui;

import java.util.List;

import com.codeboy.app.oschina.R;
import com.codeboy.app.oschina.modul.MessageData;

import android.widget.BaseAdapter;
import net.oschina.app.adapter.ListViewQuestionAdapter;
import net.oschina.app.bean.Post;
import net.oschina.app.bean.PostList;
import net.oschina.app.core.AppException;

/**
 * 类名 QAShareFragment.java</br>
 * 创建日期 2014年4月24日</br>
 * @author LeonLee</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月24日 下午8:34:20</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 问答-分享
 */
public class QAShareFragment extends BaseSwipeRefreshFragment<Post, PostList> {

	@Override
	public BaseAdapter getAdapter(List<Post> list) {
		return new ListViewQuestionAdapter(getActivity(), list,
				R.layout.question_listitem);
	}

	@Override
	protected MessageData<PostList> asyncLoadList(int page, boolean reflash) {
		MessageData<PostList> msg = null;
		try {
			PostList list = mApplication.getPostList(PostList.CATALOG_SHARE, page, reflash);
			msg = new MessageData<PostList>(list);
		} catch (AppException e) {
			e.printStackTrace();
			msg = new MessageData<PostList>(e);
		}
		return msg;
	}

}
