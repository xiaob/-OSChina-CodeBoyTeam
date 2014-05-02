package com.codeboy.app.oschina;

import net.oschina.app.bean.Post;
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
import com.codeboy.app.oschina.ui.QADetailBodyFragment;
import com.codeboy.app.oschina.ui.QADetailCommentFragment;
import com.codeboy.app.oschina.widget.CommentDialog;
import com.codeboy.app.oschina.widget.CommentDialog.OnCommentCallListener;

/**
 * 类名 QADetailActivity.java</br>
 * 创建日期 2014年5月2日</br>
 * @author LeonLee (http://my.oschina.net/lendylongli)</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年5月2日 下午3:08:23</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 问答详细界面
 */
public class QADetailActivity extends BaseDetailActivity 
	implements OnCommentCallListener {
	
	private int mPostId;
	private int mCommentCount;
	private Post mPost;
	
	private CommentDialog mCommentDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mPostId = getIntent().getIntExtra(Contanst.POST_ID_KEY, 0);
		super.onCreate(savedInstanceState);
		loadDatas(false);
	}

	@Override
	protected TabInfo getBodyTabInfo(String tag) {
		Bundle args = new Bundle();
		args.putSerializable(Contanst.POST_DATA_KEY, mPost);
		
		return new TabInfo(null, tag, QADetailBodyFragment.class, args);
	}

	@Override
	protected TabInfo getCommentTabInfo(String tag) {
		Bundle args = new Bundle();
		args.putInt(Contanst.POST_ID_KEY, mPostId);
		return new TabInfo(null, tag, QADetailCommentFragment.class, args);
	}

	@Override
	protected int getCommentCount() {
		return mCommentCount;
	}

	@Override
	protected CommentDialog getCommentDialog() {
		if(mCommentDialog == null) {
			mCommentDialog = new CommentDialog(this);
			mCommentDialog.setOnCommentCallListener(this);
		}
		return mCommentDialog;
	}

	@Override
	protected boolean isDataLoaded() {
		return mPost != null;
	}

	/** 更新资讯内容数据*/
	private void updateBodyFragment(Post post) {
		//更新adapter里的数据
		TabInfo info = mAdapter.getTab(0);
		FragmentManager fm = getSupportFragmentManager();
		String tag = info.tag;
		
		Fragment fragment = fm.findFragmentByTag(tag);
		if(fragment != null && fragment instanceof UpdateDatasEvent) {
			UpdateDatasEvent event = (UpdateDatasEvent)fragment;
			event.onNotifyUpdate(post);
		} else {
			L.d("body is null");
			info.args.putSerializable(Contanst.POST_DATA_KEY, post);
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
					res = getOsChinaApplication().pubComment(2, mPostId, 
							uid, text, 0);
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
				final Context context = getActivity();
				dialog.dismiss();
				if (msg.what == 1) {
					Result res = (Result) msg.obj;
					if (res.OK()) {
						UIHelper.ToastMessage(context, R.string.comment_publish_success);
						// 显示评论数
						mCommentCount ++;
						updateButton();
						//更新评论界面
						updateCommentFragment(mPostId);
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
					Post post = mApplication.getPost(mPostId, isRefresh);
					
					msg.what = (post != null && post.getId() > 0) ? 1 : 0;
					msg.obj = post;
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
				final Context context = getActivity();
				if (msg.what == 1) {
					final Post postDetail = (Post) msg.obj;
					mPost = postDetail;
					mCommentCount = postDetail.getAnswerCount();
					
					//更新评论数
					updateButton();
					//更新内容界面
					updateBodyFragment(postDetail);
				} else if (msg.what == 0) {
					UIHelper.ToastMessage(context, R.string.msg_load_is_null);
				} else if (msg.what == -1 && msg.obj != null) {
					((AppException) msg.obj).makeToast(context);
				}
			}
		}.execute();
	}
}