package com.codeboy.app.oschina;

import net.oschina.app.bean.Result;
import net.oschina.app.bean.User;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.core.ApiClient;
import net.oschina.app.core.AppContext;
import net.oschina.app.core.AppException;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * 类名 LoginActivity.java</br>
 * 创建日期 2014年4月26日</br>
 * @author LeonLee (http://my.oschina.net/lendylongli)</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月26日 上午12:08:38</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 登录
 */
public class LoginActivity extends BaseActionBarActivity 
	implements OnClickListener{

	private EditText mAccountEditText;
	private EditText mPasswordEditText;
	
	private CheckBox mRememberCheckBox;
	private ProgressDialog mLoginProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);
		
		ActionBar bar = getSupportActionBar();
		int flags = ActionBar.DISPLAY_HOME_AS_UP;
		int change = bar.getDisplayOptions() ^ flags;
        bar.setDisplayOptions(change, flags);
        
        //初始化界面
        mAccountEditText = (EditText) findViewById(R.id.login_account_edittext);
        mPasswordEditText = (EditText) findViewById(R.id.login_passwd_edittext);
        mRememberCheckBox = (CheckBox) findViewById(R.id.login_remember_checkbox);
        
        findViewById(R.id.login_button).setOnClickListener(this);
        
        //是否显示登录信息
	    AppContext ac = (AppContext)getApplication();
	    User user = ac.getLoginInfo();
	    if(user==null || !user.isRememberMe()) { 
	    	return;
	    }
	    if(!StringUtils.isEmpty(user.getAccount())){
	    	mAccountEditText.setText(user.getAccount());
	    	mAccountEditText.selectAll();
	    	mRememberCheckBox.setChecked(user.isRememberMe());
	    }
	    if(!StringUtils.isEmpty(user.getPwd())){
	    	mPasswordEditText.setText(user.getPwd());
	    }
	}
	
	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}

	@Override
	public void onClick(View v) {
		String account = mAccountEditText.getText().toString();
		String passwd = mPasswordEditText.getText().toString();
		boolean remember = mRememberCheckBox.isChecked();
		
		////检查用户输入的参数
		if(StringUtils.isEmpty(account)){
			UIHelper.ToastMessage(v.getContext(), getString(R.string.msg_login_email_null));
			return;
		}
		if(StringUtils.isEmpty(passwd)){
			UIHelper.ToastMessage(v.getContext(), getString(R.string.msg_login_pwd_null));
			return;
		}
		
		login(account, passwd, remember);
	}
	
	//登录验证
    private void login(final String account, final String pwd, final boolean isRememberMe) {
    	if(mLoginProgressDialog == null) {
    		mLoginProgressDialog = new ProgressDialog(this);
    		mLoginProgressDialog.setCancelable(false);
    		mLoginProgressDialog.setMessage(getString(R.string.login_dialog_tips));
    	}
    	
    	//异步登录
    	new AsyncTask<Void, Void, Message>() {

			@Override
			protected Message doInBackground(Void... params) {
				Message msg =new Message();
				try {
					AppContext ac = getOsChinaApplication(); 
	                User user = ac.loginVerify(account, pwd);
	                user.setAccount(account);
	                user.setPwd(pwd);
	                user.setRememberMe(isRememberMe);
	                Result res = user.getValidate();
	                if(res.OK()){
	                	ac.saveLoginInfo(user);//保存登录信息
	                	msg.what = 1;//成功
	                	msg.obj = user;
	                }else{
	                	ac.cleanLoginInfo();//清除登录信息
	                	msg.what = 0;//失败
	                	msg.obj = res.getErrorMessage();
	                }
	            } catch (AppException e) {
	            	e.printStackTrace();
			    	msg.what = -1;
			    	msg.obj = e;
	            }
				return msg;
			}
			
			@Override
			protected void onPreExecute() {
				if(mLoginProgressDialog != null) {
					mLoginProgressDialog.show();
				}
			}
			
			@Override
			protected void onPostExecute(Message msg) {
				//如果程序已经关闭，则不再执行以下处理
				if(isFinishing()) {
					return;
				}
				if(mLoginProgressDialog != null) {
					mLoginProgressDialog.dismiss();
				}
				Context context = LoginActivity.this;
				if(msg.what == 1){
					User user = (User)msg.obj;
					if(user != null){
						//清空原先cookie
						ApiClient.cleanCookie();
						//发送通知广播
						UIHelper.sendBroadCast(context, user.getNotice());
						//提示登陆成功
						UIHelper.ToastMessage(context, R.string.msg_login_success);
						//返回标识，成功登录
						setResult(RESULT_OK);
						finish();
					}
				}else if(msg.what == 0){
					UIHelper.ToastMessage(context, getString(R.string.msg_login_fail)+msg.obj);
				}else if(msg.what == -1){
					((AppException)msg.obj).makeToast(context);
				}
			}
		}.execute();
    }
}