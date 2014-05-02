package com.codeboy.app.oschina;

import net.oschina.app.bean.Blog;
import net.oschina.app.bean.Result;
import net.oschina.app.common.UIHelper;
import net.oschina.app.core.AppException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.codeboy.app.library.util.L;
import com.codeboy.app.oschina.adapter.TabInfo;
import com.codeboy.app.oschina.core.Contanst;
import com.codeboy.app.oschina.modul.UpdateDatasEvent;
import com.codeboy.app.oschina.ui.BlogDetailBodyFragment;
import com.codeboy.app.oschina.ui.BlogDetailCommentFragment;
import com.codeboy.app.oschina.widget.CommentDialog;
import com.codeboy.app.oschina.widget.CommentDialog.OnCommentCallListener;

/**
 * 类名 BlogDetailActivity.java</br>
 * 创建日期 2014年5月2日</br>
 * @author LeonLee (http://my.oschina.net/lendylongli)</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年5月2日 上午11:25:42</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 博客详情界面
 */
public class BlogDetailActivity extends BaseDetailActivity 
	implements OnCommentCallListener {
	
	private int mBlogId;
	private int mCommentCount;
	private Blog mBlog;
	
	private CommentDialog mCommentDialog;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mBlogId = getIntent().getIntExtra(Contanst.BLOG_ID_KEY, 0);
		if(L.Debug) {
			L.d("---> blog id:" + mBlogId);
		}
		super.onCreate(savedInstanceState);
		loadDatas(false);
	}
	
	@Override
	protected TabInfo getBodyTabInfo(String tag) {
		Bundle args = new Bundle();
		args.putSerializable(Contanst.BLOG_DATA_KEY, mBlog);
		return new TabInfo(null, tag, BlogDetailBodyFragment.class, args);
	}

	@Override
	protected TabInfo getCommentTabInfo(String tag) {
		Bundle args = new Bundle();
		args.putInt(Contanst.BLOG_ID_KEY, mBlogId);
		return new TabInfo(null, tag, BlogDetailCommentFragment.class, args);
	}

	@Override
	protected int getCommentCount() {
		return mCommentCount;
	}

	@Override
	protected boolean isDataLoaded() {
		return mBlog != null;
	}
	
	/** 获取并初始化评论的对话框*/
	@Override
	protected CommentDialog getCommentDialog() {
		if(mCommentDialog == null) {
			mCommentDialog = new CommentDialog(this);
			mCommentDialog.setOnCommentCallListener(this);
		}
		return mCommentDialog;
	}
	
	/** 更新资讯内容数据*/
	private void updateBodyFragment(Blog blog) {
		//更新adapter里的数据
		TabInfo info = mAdapter.getTab(0);
		FragmentManager fm = getSupportFragmentManager();
		String tag = info.tag;
		
		Fragment fragment = fm.findFragmentByTag(tag);
		if(fragment != null && fragment instanceof UpdateDatasEvent) {
			UpdateDatasEvent event = (UpdateDatasEvent)fragment;
			event.onNotifyUpdate(blog);
		} else {
			L.d("body is null");
			info.args.putSerializable(Contanst.BLOG_DATA_KEY, blog);
		}
	}
	
	/** 更新评论界面*/
	private void updateCommentFragment(int newsId) {
		FragmentManager fm = getSupportFragmentManager();
		String tag = mAdapter.getTab(1).tag;
		Fragment fragment = fm.findFragmentByTag(tag);
		if(fragment != null && fragment instanceof UpdateDatasEvent) {
			UpdateDatasEvent event = (UpdateDatasEvent)fragment;
			event.onNotifyUpdate(newsId);
		} else {
			L.d("comment is null");
		}
	}
	
	/**
	 * 发送评论
	 * */
	@Override
	public void onCommentCall(final String text) {
		final int uid = mApplication.getLoginUid();
		if(uid <= 0) {
			Toast.makeText(this, R.string.msg_login_request, Toast.LENGTH_LONG).show();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		}
		//隐藏评论对话框
		if(mCommentDialog != null) {
			mCommentDialog.dismiss();
		}
		
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.comment_publish_loading));
		
		//异步发表
		new AsyncTask<Void, Void, Message>() {
			
			@Override
			protected void onPreExecute() {
				dialog.show();
			}

			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				Result res = new Result();
				try {
					res = getOsChinaApplication().pubBlogComment(mBlogId, uid, text);
					msg.what = 1;
					msg.obj = res;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				return msg;
			}
			
			@Override
			protected void onPostExecute(Message msg) {
				if(isFinishing()) {
					return;
				}
				final Context context = BlogDetailActivity.this;
				dialog.dismiss();
				if (msg.what == 1) {
					Result res = (Result) msg.obj;
					if (res.OK()) {
						UIHelper.ToastMessage(context, R.string.comment_publish_success);
						// 显示评论数
						mCommentCount ++;
						updateButton();
						//更新评论界面
						updateCommentFragment(mBlogId);
						//更新缓存数据
						loadDatas(true);
					} else {
						UIHelper.ToastMessage(context, res.getErrorMessage());
					}
				} else {
					((AppException) msg.obj).makeToast(context);
				}
			}
			
		}.execute();
	}
	
	/**
	 * 加载资讯数据
	 * @param isRefresh 是否刷新，否则加载本地缓存
	 * */
	private void loadDatas(final boolean isRefresh) {
		new AsyncTask<Void, Void, Message>() {

			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					Blog blog = mApplication.getBlog(mBlogId, isRefresh);
					
					msg.what = (blog != null && blog.getId() > 0) ? 1 : 0;
					msg.obj = blog;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				return msg;
			}
			
			@Override
			protected void onPreExecute() {
				
			}
			
			@Override
			protected void onPostExecute(Message msg) {
				if(isFinishing()) {
					return;
				}
				final Context context = BlogDetailActivity.this;
				if (msg.what == 1) {
					final Blog blog = (Blog) msg.obj;
					mBlog = blog;
					mCommentCount = blog.getCommentCount();
					
					//更新评论数
					updateButton();
					//更新内容界面
					updateBodyFragment(blog);
				} else if (msg.what == 0) {
					UIHelper.ToastMessage(context, R.string.msg_load_is_null);
				} else if (msg.what == -1 && msg.obj != null) {
					((AppException) msg.obj).makeToast(context);
				}
			}
		}.execute();
	}
}