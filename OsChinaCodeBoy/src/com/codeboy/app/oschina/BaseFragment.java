package com.codeboy.app.oschina;

import android.support.v4.app.Fragment;

/**
 * 类名 BaseFragment.java</br>
 * 创建日期 2014年4月20日</br>
 * @author LeonLee</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月20日 下午11:47:28</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 碎片的基类
 */
public class BaseFragment extends Fragment{

	
	public OSChinaApplication getOsChinaApplication() {
		return (OSChinaApplication) getActivity().getApplication();
	}
}
