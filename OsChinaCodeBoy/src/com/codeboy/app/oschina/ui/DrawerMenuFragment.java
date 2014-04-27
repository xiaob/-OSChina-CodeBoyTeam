package com.codeboy.app.oschina.ui;

import net.oschina.app.bean.MyInformation;
import net.oschina.app.common.UIHelper;
import net.oschina.app.core.AppException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeboy.app.oschina.BaseFragment;
import com.codeboy.app.oschina.OSChinaApplication;
import com.codeboy.app.oschina.R;
import com.codeboy.app.oschina.UserInfoActivity;
import com.codeboy.app.oschina.modul.DrawerMenuCallBack;

/**
 * 类名 DrawerMenuFragment.java</br>
 * 创建日期 2014年4月27日</br>
 * @author LeonLee (http://my.oschina.net/lendylongli)</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月27日 下午1:09:08</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 菜单界面
 */
public class DrawerMenuFragment extends BaseFragment implements OnClickListener{
	
	public static DrawerMenuFragment newInstance() {
		return new DrawerMenuFragment();
	}
	
	private ImageView mAvatarImageView;
	private ImageView mGenderImageView;
	private TextView mNameTextView;

	//通过回调与Activity通讯
	private DrawerMenuCallBack mCallBack;
	private OSChinaApplication mApplication;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof DrawerMenuCallBack) {
			mCallBack = (DrawerMenuCallBack) activity;
		}
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mCallBack = null;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication = getOsChinaApplication();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_drawer_menu, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mAvatarImageView = (ImageView) view.findViewById(R.id.user_info_userface);
		mGenderImageView = (ImageView) view.findViewById(R.id.user_info_gender);
		mNameTextView = (TextView) view.findViewById(R.id.user_info_username);
		
		view.findViewById(R.id.menu_user_layout).setOnClickListener(this);
		view.findViewById(R.id.menu_item_news).setOnClickListener(this);
		view.findViewById(R.id.menu_item_qa).setOnClickListener(this);
		view.findViewById(R.id.menu_item_tweet).setOnClickListener(this);
		view.findViewById(R.id.menu_item_software).setOnClickListener(this);
		view.findViewById(R.id.menu_item_active).setOnClickListener(this);
		
		//如果已经登录，则显示用户的头像与信息
		if(!mApplication.isLogin()) {
			return;
		}
		MyInformation user = null;
		try {
			user = mApplication.getMyInformation(false);
		} catch (AppException e) {
			e.printStackTrace();
		}
		if(user == null) {
			return;
		}
		// 加载用户头像
		UIHelper.showUserFace(mAvatarImageView, user.getFace());

		// 用户性别
		if (user.getGender() == 1) {
			mGenderImageView.setImageResource(R.drawable.widget_gender_man);
		} else {
			mGenderImageView.setImageResource(R.drawable.widget_gender_woman);
		}

		// 其他资料
		mNameTextView.setText(user.getName());
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.menu_user_layout) {
			onClickUserLayout();
		} else if (id == R.id.menu_item_news) {
			onClickNews();
		} else if (id == R.id.menu_item_qa) {
			onClickQuestionAsk();
		} else if (id == R.id.menu_item_tweet) {
			onClickTweet();
		} else if (id == R.id.menu_item_software) {
			onClickSoftware();
		} else if (id == R.id.menu_item_active) {
			onClickActive();
		}
	}
	
	/** 点击了用户*/
	private void onClickUserLayout() {
		Intent intent = new Intent(getActivity(), UserInfoActivity.class);
		startActivity(intent);
	}
	
	/** 点击了资讯*/
	private void onClickNews() {
		if(mCallBack != null) {
			mCallBack.onClickNews();
		}
	}
	
	/** 点击了问答*/
	private void onClickQuestionAsk() {
		if(mCallBack != null) {
			mCallBack.onClickQuestionAsk();
		}
	}
	
	/** 点击了开源软件*/
	private void onClickSoftware() {
		if(mCallBack != null) {
			mCallBack.onClickSoftware();
		}
	}
	
	/** 点击了动弹*/
	private void onClickTweet() {
		if(mCallBack != null) {
			mCallBack.onClickTweet();
		}
	}
	
	/** 点击了我的空间*/
	private void onClickActive() {
		
	}
}
