package com.codeboy.app.oschina;

import java.util.ArrayList;

import net.oschina.app.bean.News;
import net.oschina.app.bean.Result;
import net.oschina.app.common.UIHelper;
import net.oschina.app.core.AppException;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.codeboy.app.library.util.L;
import com.codeboy.app.oschina.NewsDetailActivity.NewsDetailPageAdapter.TabInfo;
import com.codeboy.app.oschina.core.Contanst;
import com.codeboy.app.oschina.modul.UpdateDatasEvent;
import com.codeboy.app.oschina.ui.NewsDetailBodyFragment;
import com.codeboy.app.oschina.ui.NewsDetailCommentFragment;
import com.codeboy.app.oschina.widget.CommentDialog;
import com.codeboy.app.oschina.widget.CommentDialog.OnCommentCallListener;

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
	implements OnClickListener, OnPageChangeListener, OnCommentCallListener {
	
	private ViewPager mViewPager;
	//评论数显示的按钮
	private Button mCommentCountButton;
	private View mEditBoxView;
	
	private NewsDetailPageAdapter mAdapter;
	
	private int mNewsId;
	private int mCommentCount;
	private News mNews;
	
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
		mAdapter = new NewsDetailPageAdapter(this, getSupportFragmentManager());
		
		setContentView(R.layout.activity_news_detail);
		
		initView();
		loadDatas(false);
	}
	
	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}
	
	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.newsdetail_viewpager);
		mCommentCountButton = (Button) findViewById(R.id.newdetail_button);
		mEditBoxView = findViewById(R.id.newsdetail_editbox);
		
		mCommentCountButton.setOnClickListener(this);
		mEditBoxView.setOnClickListener(this);
		
		mEditBoxView.setEnabled(false);
		
		mViewPager.setOnPageChangeListener(this);
		
		Bundle args = new Bundle();
		args.putInt(Contanst.NEWS_ID_KEY, mNewsId);
		
		mAdapter.addTab(makeFragmentName(mViewPager.getId(), 0), NewsDetailBodyFragment.class, new Bundle());
		mAdapter.addTab(makeFragmentName(mViewPager.getId(), 1), NewsDetailCommentFragment.class, args);
		mViewPager.setAdapter(mAdapter);
	}
	
	/** 
	 * 在AndroidSupportV4里，每一个位置的fragment的tag生成规则
	 * 详细看源码
	 * @param viewId ViewPager的id
	 * @param id adapter里的long id,规则以位置作为id
	 * */
	private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
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
			//先判断是否已经加载数据
			if(mNews == null) {
				return;
			}
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
	
	/** 获取并初始化评论的对话框*/
	private CommentDialog getCommentDialog() {
		if(mCommentDialog == null) {
			mCommentDialog = new CommentDialog(this);
			mCommentDialog.setOnCommentCallListener(this);
		}
		return mCommentDialog;
	}
	
	/** 更新资讯内容数据*/
	private void updateBodyFragment(News news) {
		//更新adapter里的数据
		TabInfo info = mAdapter.getTab(0);
		FragmentManager fm = getSupportFragmentManager();
		String tag = info.tag;
		
		Fragment fragment = fm.findFragmentByTag(tag);
		if(fragment != null && fragment instanceof UpdateDatasEvent) {
			UpdateDatasEvent event = (UpdateDatasEvent)fragment;
			event.onNotifyUpdate(news);
		} else {
			L.d("body is null");
			info.args.putSerializable(Contanst.NEWS_DATA_KEY, news);
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
					res = getOsChinaApplication().pubComment(1, mNewsId, 
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
				final Context context = NewsDetailActivity.this;
				dialog.dismiss();
				if (msg.what == 1) {
					Result res = (Result) msg.obj;
					UIHelper.ToastMessage(context,
							res.getErrorMessage());
					if (res.OK()) {
						// 显示评论数
						mCommentCount ++;
						updateButton();
						//更新评论界面
						updateCommentFragment(mNewsId);
						//更新缓存数据
						loadDatas(true);
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
					News news = getOsChinaApplication().getNews(
							mNewsId, isRefresh);
					
					msg.what = (news != null && news.getId() > 0) ? 1 : 0;
					msg.obj = news;
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
				final Context context = NewsDetailActivity.this;
				if (msg.what == 1) {
					final News newsDetail = (News) msg.obj;
					mNews = newsDetail;
					mCommentCount = newsDetail.getCommentCount();
					
					mEditBoxView.setEnabled(true);
					//更新评论数
					updateButton();
					//更新内容界面
					updateBodyFragment(newsDetail);
				} else if (msg.what == 0) {
					UIHelper.ToastMessage(context, R.string.msg_load_is_null);
				} else if (msg.what == -1 && msg.obj != null) {
					((AppException) msg.obj).makeToast(context);
				}
			}
		}.execute();
	}
	
	//内容、评论两个界面的适配器
	static class NewsDetailPageAdapter extends FragmentPagerAdapter {

		static final class TabInfo {
            private final String tag;
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                tag = _tag;
                clss = _class;
                args = _args;
            }
        }
		
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
		private Context mContext;
		
		public NewsDetailPageAdapter(Context context, FragmentManager fm) {
			super(fm);
			mContext = context;
		}
		
		public void addTab(String tag, Class<?> clss, Bundle args) {
            TabInfo info = new TabInfo(tag, clss, args);
            mTabs.add(info);
            notifyDataSetChanged();
        }
		
		public TabInfo getTab(int position) {
			return mTabs.get(position);
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
	        return Fragment.instantiate(mContext, info.clss.getName(), info.args);
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}
	}
}