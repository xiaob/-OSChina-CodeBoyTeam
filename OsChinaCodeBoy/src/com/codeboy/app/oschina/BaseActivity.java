package com.codeboy.app.oschina;

import android.app.Activity;
import android.os.Bundle;

/**
 * 类名 BaseActivity.java</br>
 * 创建日期 2014年4月20日</br>
 * @author LeonLee</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月20日 下午11:38:59</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 类的描述
 */
public class BaseActivity extends Activity implements ActivityHelperInterface{

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
