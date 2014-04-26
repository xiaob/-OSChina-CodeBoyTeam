package com.codeboy.app.oschina;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.oschina.app.bean.MyInformation;
import net.oschina.app.bean.Result;
import net.oschina.app.common.FileUtils;
import net.oschina.app.common.ImageUtils;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.core.AppConfig;
import net.oschina.app.core.AppException;
import net.oschina.app.widget.LoadingDialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 用户资料
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */

/**
 * 类名 UserInfoActivity.java</br>
 * 创建日期 2014年4月26日</br>
 * @author LeonLee (http://my.oschina.net/lendylongli)</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月26日 上午10:02:03</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 用户资料界面
 */
public class UserInfoActivity extends BaseActionBarActivity {
	
	//头像截图的大小
	private final static int CROP_SIZE = 200;
	//头像保存目录
	private final static String FILE_SAVEPATH = 
			AppConfig.DEFAULT_IMAGE_PORTRAIT_PATH;
	
	private final static int REFLASH_ITEM_ID = 100;

	private ImageView face;
	private ImageView gender;
	private Button editer;
	private TextView name;
	private TextView jointime;
	private TextView from;
	private TextView devplatform;
	private TextView expertise;
	private TextView followers;
	private TextView fans;
	private TextView favorites;
	private LinearLayout favorites_ll;
	private LinearLayout followers_ll;
	private LinearLayout fans_ll;
	
	private MyInformation user;
	
	private LoadingDialog loadingDialog;
	//控制加载用户数据
	private boolean isLoaddingUserInfo = false;

	private Uri origUri;
	private Uri cropUri;
	private File protraitFile;
	private Bitmap protraitBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//须要在setContentView 前调用
		supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_user_info);
		
		ActionBar bar = getSupportActionBar();
		int flags = ActionBar.DISPLAY_HOME_AS_UP;
		int change = bar.getDisplayOptions() ^ flags;
        bar.setDisplayOptions(change, flags);

		// 初始化视图控件
		initView();
		// 加载用户数据
		loadUserInfo(false);
	}
	
	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//刷新按钮
		MenuItem reflashItem = menu.add(0, REFLASH_ITEM_ID, 
				100, R.string.footbar_refresh);
        reflashItem.setIcon(R.drawable.ic_menu_refresh);
        MenuItemCompat.setShowAsAction(reflashItem, 
        		MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == REFLASH_ITEM_ID) {
			//刷新
			loadUserInfo(true);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/** 初始化界面*/
	private void initView() {
		loadingDialog = new LoadingDialog(this);
		
		editer = (Button) findViewById(R.id.user_info_editer);
		editer.setOnClickListener(editerClickListener);

		face = (ImageView) findViewById(R.id.user_info_userface);
		gender = (ImageView) findViewById(R.id.user_info_gender);
		name = (TextView) findViewById(R.id.user_info_username);
		jointime = (TextView) findViewById(R.id.user_info_jointime);
		from = (TextView) findViewById(R.id.user_info_from);
		devplatform = (TextView) findViewById(R.id.user_info_devplatform);
		expertise = (TextView) findViewById(R.id.user_info_expertise);
		followers = (TextView) findViewById(R.id.user_info_followers);
		fans = (TextView) findViewById(R.id.user_info_fans);
		favorites = (TextView) findViewById(R.id.user_info_favorites);
		favorites_ll = (LinearLayout) findViewById(R.id.user_info_favorites_ll);
		followers_ll = (LinearLayout) findViewById(R.id.user_info_followers_ll);
		fans_ll = (LinearLayout) findViewById(R.id.user_info_fans_ll);
	}
	
	/** 
	 * 异步加载用户数据
	 * @param isRefresh 是否刷新，false则表示加载本地的缓存
	 * */
	private void loadUserInfo(final boolean isRefresh) {
		//如果已经正在刷新,则不处理,避免重复加载
		if(isLoaddingUserInfo) {
			return;
		}
		new AsyncTask<Void, Void, Message>() {

			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					MyInformation user = getOsChinaApplication()
							.getMyInformation(isRefresh);
					msg.what = 1;
					msg.obj = user;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				return msg;
			}

			@Override
			protected void onPreExecute() {
				if(isRefresh) {
					isLoaddingUserInfo = true;
					loadingDialog.setLoadText(R.string.loading_userinfo);
					loadingDialog.show();
				}
			}

			@Override
			protected void onPostExecute(Message msg) {
				if(isRefresh) {
					isLoaddingUserInfo = false;
				}
				//如果当前界面已经退出，则不执行以下操作
				if(isFinishing()) {
					return;
				}
				loadingDialog.hide();
				if (msg.what == 1 && msg.obj != null) {
					user = (MyInformation) msg.obj;

					// 加载用户头像
					UIHelper.showUserFace(face, user.getFace());

					// 用户性别
					if (user.getGender() == 1)
						gender.setImageResource(R.drawable.widget_gender_man);
					else
						gender.setImageResource(R.drawable.widget_gender_woman);

					// 其他资料
					name.setText(user.getName());
					jointime.setText(StringUtils.friendly_time(user
							.getJointime()));
					from.setText(user.getFrom());
					devplatform.setText(user.getDevplatform());
					expertise.setText(user.getExpertise());
					followers.setText(user.getFollowerscount() + "");
					fans.setText(user.getFanscount() + "");
					favorites.setText(user.getFavoritecount() + "");

					favorites_ll.setOnClickListener(favoritesClickListener);
					fans_ll.setOnClickListener(fansClickListener);
					followers_ll.setOnClickListener(followersClickListener);
					
					if(isRefresh) {
						UIHelper.ToastMessage(UserInfoActivity.this, R.string.loaded_userinfo);
					}
				} else if (msg.obj != null) {
					((AppException) msg.obj).makeToast(UserInfoActivity.this);
				}
			}
			
		}.execute();
	}

	private View.OnClickListener editerClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			CharSequence[] items = { getString(R.string.img_from_album),
					getString(R.string.img_from_camera) };
			imageChooseItem(items);
		}
	};

	private View.OnClickListener favoritesClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			//TODO
			//UIHelper.showUserFavorite(v.getContext());
		}
	};

	private View.OnClickListener fansClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			int followers = user != null ? user.getFollowerscount() : 0;
			int fans = user != null ? user.getFanscount() : 0;
			//TODO
			/*UIHelper.showUserFriend(v.getContext(), FriendList.TYPE_FANS,
					followers, fans);*/
		}
	};

	private View.OnClickListener followersClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			int followers = user != null ? user.getFollowerscount() : 0;
			int fans = user != null ? user.getFanscount() : 0;
			//TODO
			/*UIHelper.showUserFriend(v.getContext(), FriendList.TYPE_FOLLOWER,
					followers, fans);*/
		}
	};

	/** 裁剪头像的绝对路径*/
	private Uri getUploadTempFile(Uri uri) {
		String storageState = Environment.getExternalStorageState();
		if (storageState.equals(Environment.MEDIA_MOUNTED)) {
			File savedir = new File(FILE_SAVEPATH);
			if (!savedir.exists()) {
				savedir.mkdirs();
			}
		} else {
			UIHelper.ToastMessage(UserInfoActivity.this, 
					R.string.upload_user_avatar_error);
			return null;
		}
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date());
		String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);

		// 如果是标准Uri
		if (StringUtils.isEmpty(thePath)) {
			thePath = ImageUtils.getAbsoluteImagePath(UserInfoActivity.this, uri);
		}
		String ext = FileUtils.getFileFormat(thePath);
		ext = StringUtils.isEmpty(ext) ? "jpg" : ext;
		// 照片命名
		String cropFileName = "osc_crop_" + timeStamp + "." + ext;
		// 裁剪头像的绝对路径
		String protraitPath = FILE_SAVEPATH + cropFileName;
		protraitFile = new File(protraitPath);

		cropUri = Uri.fromFile(protraitFile);
		return this.cropUri;
	}

	/** 拍照保存的绝对路径*/
	private Uri getCameraTempFile() {
		String storageState = Environment.getExternalStorageState();
		if (storageState.equals(Environment.MEDIA_MOUNTED)) {
			File savedir = new File(FILE_SAVEPATH);
			if (!savedir.exists()) {
				savedir.mkdirs();
			}
		} else {
			UIHelper.ToastMessage(UserInfoActivity.this, 
					R.string.upload_user_avatar_error);
			return null;
		}
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date());
		// 照片命名
		String cropFileName = "osc_camera_" + timeStamp + ".jpg";
		// 裁剪头像的绝对路径
		String protraitPath = FILE_SAVEPATH + cropFileName;
		protraitFile = new File(protraitPath);
		cropUri = Uri.fromFile(protraitFile);
		this.origUri = this.cropUri;
		return this.cropUri;
	}

	/**
	 * 操作选择
	 * 
	 * @param items
	 */
	public void imageChooseItem(CharSequence[] items) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.userinfo_upload_avatar_title)
		.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				// 相册选图
				if (item == 0) {
					startImagePick();
				}
				// 手机拍照
				else if (item == 1) {
					startActionCamera();
				}
			}
		});
		 builder.create().show();
	}

	/**
	 * 选择图片裁剪
	 * 
	 * @param output
	 */
	private void startImagePick() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, 
				getString(R.string.userinfo_upload_avatar_choose)),
				ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
	}

	/**
	 * 相机拍照
	 * 
	 * @param output
	 */
	private void startActionCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, this.getCameraTempFile());
		startActivityForResult(intent,
				ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
	}

	/**
	 * 拍照后裁剪
	 * 
	 * @param data 原始图片
	 */
	private void startActionCrop(Uri data) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(data, "image/*");
		intent.putExtra("output", this.getUploadTempFile(data));
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", CROP_SIZE);// 输出图片大小
		intent.putExtra("outputY", CROP_SIZE);
		intent.putExtra("scale", true);// 去黑边
		intent.putExtra("scaleUpIfNeeded", true);// 去黑边
		startActivityForResult(intent,
				ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
	}

	/** 上传新照片 */
	private void uploadNewPhoto() {
		// 获取头像缩略图
		if (protraitFile == null || !protraitFile.exists()) {
			UIHelper.ToastMessage(this, R.string.upload_user_avatar_error2);
			return;
		}
		new AsyncTask<Void, Void, Message>() {

			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				// 获取头像缩略图
				protraitBitmap = ImageUtils.loadImgThumbnail(
						protraitFile.getAbsolutePath(), 200, 200);

				if (protraitBitmap != null) {
					try {
						Result res = getOsChinaApplication()
								.updatePortrait(protraitFile);
						if (res != null && res.OK()) {
							// 保存新头像到缓存
							String filename = FileUtils.getFileName(user
									.getFace());
							ImageUtils.saveImage(UserInfoActivity.this, filename,
									protraitBitmap);
						}
						msg.what = 1;
						msg.obj = res;
					} catch (AppException e) {
						msg.what = -1;
						msg.obj = e;
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					msg.what = -2;
				}
				return msg;
			}
			
			@Override
			protected void onPreExecute() {
				loadingDialog.setLoadText(R.string.upload_user_avatar);
				//设置为不可以关闭的对话框
				loadingDialog.setCancelable(false);
				loadingDialog.show();
			}
			
			@Override
			protected void onPostExecute(Message msg) {
				//如果当前界面已经退出，则不执行以下操作
				if(isFinishing()) {
					return;
				}
				loadingDialog.setCancelable(true);
				loadingDialog.hide();
				if (msg.what == 1 && msg.obj != null) {
					Result res = (Result) msg.obj;
					// 提示信息
					UIHelper.ToastMessage(UserInfoActivity.this, res.getErrorMessage());
					if (res.OK()) {
						// 显示新头像
						face.setImageBitmap(protraitBitmap);
					}
				} else if (msg.what == -1 && msg.obj != null) {
					((AppException) msg.obj).makeToast(UserInfoActivity.this);
				} else if(msg.what == -2) {
					UIHelper.ToastMessage(UserInfoActivity.this, 
							R.string.upload_user_avatar_error2);
				}
			}
			
		}.execute();
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
			// 拍照后裁剪
			startActionCrop(origUri);
			break;
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
			// 选图后裁剪
			startActionCrop(data.getData());
			break;
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:
			// 上传新照片
			uploadNewPhoto();
			break;
		}
	}
}