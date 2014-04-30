package com.codeboy.app.oschina;

import net.oschina.app.bean.Result;
import net.oschina.app.common.UIHelper;
import net.oschina.app.core.AppException;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.codeboy.app.oschina.core.BroadcastController;
import com.codeboy.app.oschina.core.Contanst;
import com.codeboy.app.oschina.modul.CommentCountCallBack;
import com.codeboy.app.oschina.ui.NewsDetailBodyFragment;
import com.codeboy.app.oschina.ui.NewsDetailCommentFragment;
import com.codeboy.app.oschina.widget.CommentDialog;

/**
 * 类名 NewsDetailActivity.java</br>
 * 创建日期 2014年4月28日</br>
 * @author LeonLee (http://my.oschina.net/lendylongli)</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月28日 下午11:49:49</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 资讯详情界面
 */
public class NewsDetailActivity extends BaseActionBarActivity 
	implements CommentCountCallBack, OnClickListener, OnPageChangeListener,
	CommentDialog.OnCommentCallListener {

	private ViewPager mViewPager;
	//评论数显示的按钮
	private Button mCommentCountButton;
	
	private NewsDetailPageAdapter mAdapter;
	
	private int mNewsId;
	private int mCommentCount;
	
	private CommentDialog mCommentDialog;
	
	private OSChinaApplication mApplication;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar bar = getSupportActionBar();
		int flags = ActionBar.DISPLAY_HOME_AS_UP;
		int change = bar.getDisplayOptions() ^ flags;
        bar.setDisplayOptions(change, flags);
		
        mApplication = getOsChinaApplication();
		mNewsId = getIntent().getIntExtra(Contanst.NEWS_ID_KEY, 0);
		mAdapter = new NewsDetailPageAdapter(getSupportFragmentManager(), mNewsId);
		
		setContentView(R.layout.activity_news_detail);
		
		initView();
	}
	
	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}
	
	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.newsdetail_viewpager);
		mCommentCountButton = (Button) findViewById(R.id.newdetail_button);
		findViewById(R.id.newsdetail_editbox).setOnClickListener(this);
		
		mCommentCountButton.setOnClickListener(this);
		
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setAdapter(mAdapter);
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if(id == R.id.newdetail_button) {
			//点击评论与原文
			int pos = mViewPager.getCurrentItem();
			if(pos == 0){
				mViewPager.setCurrentItem(1);
			} else {
				mViewPager.setCurrentItem(0);
			}
		} else if(id == R.id.newsdetail_editbox) {
			//判断是否已经登录
			boolean login = mApplication.isLogin();
			if(login) {
				getCommentDialog().show();
			} else {
				Toast.makeText(this, R.string.msg_login_request, Toast.LENGTH_LONG).show();
				startActivity(new Intent(this, LoginActivity.class));
			}
		}
	}
	
	@Override
	public void onPageScrollStateChanged(int state) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int position) {
		updateButton();
	}
	
	/** 更新按钮状态*/
	private void updateButton() {
		int pos = mViewPager.getCurrentItem();
		if(pos == 0) {
			updateBodyButton();
		} else {
			updateCommentButton();
		}
	}
	
	/** 在显示内容时，按钮为显示评论数*/
	private void updateBodyButton () {
		final int count = mCommentCount;
		String text = String.valueOf(count);
		
		Drawable drable = null;
		Resources res = getResources();
		if(count > 100) {
			drable = res.getDrawable(R.drawable.comment_lajiao2_icon);
		} else if(count > 50) {
			drable = res.getDrawable(R.drawable.comment_lajiao1_icon);
		} else if(count == 0){
			text = "";
			drable = res.getDrawable(R.drawable.comment_sofa_icon);
		} else {
			drable = res.getDrawable(R.drawable.comment_simple_icon);
		}
		mCommentCountButton.setText(text);
		mCommentCountButton.setCompoundDrawablesWithIntrinsicBounds(null, null, drable, null);
	}
	
	/** 在显示评论时，按钮为显示原文*/
	private void updateCommentButton() {
		mCommentCountButton.setText(R.string.comment_original_text);
		mCommentCountButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
	}
	
	@Override
	public void onCommentCount(int count) {
		mCommentCount = count;
		updateButton();
	}
	
	private CommentDialog getCommentDialog() {
		if(mCommentDialog == null) {
			mCommentDialog = new CommentDialog(this);
			mCommentDialog.setOnCommentCallListener(this);
		}
		return mCommentDialog;
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
					res = getOsChinaApplication().pubComment(1, mNewsId, uid, text, 0);
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
				dialog.dismiss();
				if (msg.what == 1) {
					Result res = (Result) msg.obj;
					UIHelper.ToastMessage(NewsDetailActivity.this,
							res.getErrorMessage());
					if (res.OK()) {
						// 发送通知广播
						if (res.getNotice() != null) {
							BroadcastController.sendNoticeBroadCast(
									NewsDetailActivity.this,
									res.getNotice());
						}
						
						// 显示评论数
						mCommentCount ++;
						updateButton();
					}
				} else {
					((AppException) msg.obj).makeToast(NewsDetailActivity.this);
				}
			}
			
		}.execute();
	}
	
	static class NewsDetailPageAdapter extends FragmentPagerAdapter {

		private int mNewsId;
		
		public NewsDetailPageAdapter(FragmentManager fm, int newid) {
			super(fm);
			mNewsId = newid;
		}

		@Override
		public Fragment getItem(int position) {
			if(position == 0) {
				return NewsDetailBodyFragment.newInstance(mNewsId);
			} else {
				return NewsDetailCommentFragment.newInstance(mNewsId);
			}
		}

		@Override
		public int getCount() {
			return 2;
		}
	}
}