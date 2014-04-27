package com.codeboy.app.oschina;


import com.codeboy.app.library.util.L;
import com.codeboy.app.oschina.modul.DrawerMenuCallBack;
import com.codeboy.app.oschina.ui.DrawerMenuFragment;
import com.codeboy.app.oschina.ui.NewsMainFragment;
import com.codeboy.app.oschina.ui.QAMainFragment;
import com.codeboy.app.oschina.ui.SoftwareMainFragment;
import com.codeboy.app.oschina.ui.TweetMainFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;


/**
 * 类名 MainActivity.java</br>
 * 创建日期 2014年4月26日</br>
 * @author LeonLee (http://my.oschina.net/lendylongli)</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月26日 下午12:15:10</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 主界面 
 */
public class MainActivity extends BaseActionBarActivity implements DrawerMenuCallBack{
    
	static final String DRAWER_MENU_TAG = "drawer_menu";
	static final String DRAWER_CONTENT_TAG = "drawer_content";
	
	static final String CONTENT_TAG_NEWS = "content_news";
	static final String CONTENT_TAG_QA = "content_questionask";
	static final String CONTENT_TAG_TWEET = "content_tweet";
	static final String CONTENT_TAG_SOFTWARE = "content_software";
	
	static final String CONTENTS[] = {
		CONTENT_TAG_NEWS,
		CONTENT_TAG_QA,
		CONTENT_TAG_TWEET,
		CONTENT_TAG_SOFTWARE
	};
	
	static final String FRAGMENTS[] = {
		NewsMainFragment.class.getName(),
		QAMainFragment.class.getName(),
		TweetMainFragment.class.getName(),
		SoftwareMainFragment.class.getName()
	};
	
	private FragmentManager mFragmentManager;
    private DrawerLayout mDrawerLayout;
    
    //当前显示的界面标识
    private String mCurrentContentTag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mFragmentManager = getSupportFragmentManager();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		
		if(savedInstanceState == null) {
			FragmentTransaction ft = mFragmentManager.beginTransaction();
			ft.replace(R.id.drawer_menu, DrawerMenuFragment.newInstance(), DRAWER_MENU_TAG)
			.replace(R.id.drawer_content, NewsMainFragment.newInstance(), CONTENT_TAG_NEWS)
			.commit();
			
			mCurrentContentTag = CONTENT_TAG_NEWS;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			//判断菜单是否打开
			if(mDrawerLayout.isDrawerOpen(Gravity.START)) {
				mDrawerLayout.closeDrawers();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/** 显示内容*/
	private void showContent(int pos) {
		mDrawerLayout.closeDrawers();
		String tag = CONTENTS[pos];
		if(tag.equals(mCurrentContentTag)) {
			if(L.Debug) {
				L.d("show content:" + tag);
			}
			return;
		}
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		if(mCurrentContentTag != null) {
			Fragment fragment = mFragmentManager.findFragmentByTag(mCurrentContentTag);
			if(fragment != null) {
				ft.remove(fragment);
			}
		}
		ft.replace(R.id.drawer_content, Fragment.instantiate(this, FRAGMENTS[pos]), tag);
		ft.commit();
		mCurrentContentTag = tag;
	}

	@Override
	public void onClickNews() {
		showContent(0);
	}

	@Override
	public void onClickQuestionAsk() {
		showContent(1);
	}

	@Override
	public void onClickTweet() {
		showContent(2);
	}

	@Override
	public void onClickSoftware() {
		showContent(3);
	}
}