package com.codeboy.app.oschina.ui;

import java.util.List;

import net.oschina.app.adapter.ListViewTweetAdapter;
import net.oschina.app.bean.Tweet;
import net.oschina.app.bean.TweetList;
import net.oschina.app.core.AppException;
import android.widget.BaseAdapter;

import com.codeboy.app.oschina.R;

/**
 * 类名 NewsTweetFragment.java</br>
 * 创建日期 2014年4月20日</br>
 * @author LeonLee</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月20日 下午11:33:40</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 推荐阅读
 */
public class NewsTweetFragment extends BaseSwipeRefreshFragment<Tweet, TweetList> {

	@Override
	public BaseAdapter getAdapter(List<Tweet> list) {
		return new ListViewTweetAdapter(getActivity(), list, R.layout.tweet_listitem);
	}

	@Override
	public Object getDataTag() {
		return 0;
	}

	@Override
	protected TweetList asyncLoadList(Object tag, int page, boolean reflash) {
		try {
			return mApplication.getTweetList((Integer)tag, page, reflash);
		} catch (AppException e) {
			e.printStackTrace();
		}
		return null;
	}
}