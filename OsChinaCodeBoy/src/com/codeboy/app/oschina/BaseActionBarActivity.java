package com.codeboy.app.oschina;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * 类名 BaseActionBarActivity.java</br>
 * 创建日期 2014年4月20日</br>
 * @author LeonLee</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月20日 下午11:39:14</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 ActionBar的基类
 */
public class BaseActionBarActivity extends ActionBarActivity 
	implements ActivityHelperInterface{

	ActivityHelper mHelper = new ActivityHelper(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHelper.onCreate(savedInstanceState);
	}
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		mHelper.onAttachedToWindow();
	}
	
	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mHelper.onDetachedFromWindow();
	}
	
	@Override
	public OSChinaApplication getOsChinaApplication() {
		return mHelper.getOsChinaApplication();
	}

}
