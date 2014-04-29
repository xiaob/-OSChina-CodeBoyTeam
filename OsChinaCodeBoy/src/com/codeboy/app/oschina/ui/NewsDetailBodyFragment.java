package com.codeboy.app.oschina.ui;

import net.oschina.app.bean.News;
import net.oschina.app.bean.News.Relative;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.core.AppContext;
import net.oschina.app.core.AppException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.codeboy.app.oschina.BaseFragment;
import com.codeboy.app.oschina.OSChinaApplication;
import com.codeboy.app.oschina.R;
import com.codeboy.app.oschina.core.BroadcastController;
import com.codeboy.app.oschina.core.Contanst;

/**
 * 类名 NewsDetailBodyFragment.java</br>
 * 创建日期 2014年4月29日</br>
 * @author LeonLee (http://my.oschina.net/lendylongli)</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月29日 下午10:40:44</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 资讯详情界面
 */
public class NewsDetailBodyFragment extends BaseFragment {
	
	public static NewsDetailBodyFragment newInstance(int newsid) {
		NewsDetailBodyFragment fragment = new NewsDetailBodyFragment();
		Bundle args = new Bundle();
		args.putInt(Contanst.NEWS_ID_KEY, newsid);
		fragment.setArguments(args);
		return fragment;
	}
	
	private OSChinaApplication mApplication;
	
	private int mNewsId;
	private WebView mWebView;
	private TextView mTitleTextView;
	private TextView mAuthorTextView;
	private TextView mDateTextView;
	private TextView mCountTextView;
	
	private News newsDetail;
	
	@Override
	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle argus = getArguments();
		if(argus != null) {
			mNewsId = argus.getInt(Contanst.NEWS_ID_KEY);
		}
		mApplication = getOsChinaApplication();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_newsdetail_body, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mTitleTextView = (TextView) view.findViewById(R.id.news_detail_title);
		mAuthorTextView = (TextView) view.findViewById(R.id.news_detail_author);
		mDateTextView = (TextView) view.findViewById(R.id.news_detail_date);
		mCountTextView = (TextView) view.findViewById(R.id.news_detail_commentcount);
		
		mWebView = (WebView) view.findViewById(R.id.news_detail_webview);
		WebSettings settings = mWebView.getSettings();
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		//mWebView.getSettings().setDefaultFontSize(15);
		
		//TODO
        //UIHelper.addWebImageShow(getActivity(), mWebView);
		
		loadDatas(false);
	}
	
	/**
	 * 加载资讯数据
	 * */
	private void loadDatas(final boolean isRefresh) {
		new AsyncTask<Void, Void, Message>() {

			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					News newsDetail = getOsChinaApplication().getNews(mNewsId, isRefresh);
					msg.what = (newsDetail != null && newsDetail.getId() > 0) ? 1 : 0;
					msg.obj = newsDetail;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				return msg;
			}
			
			@Override
			protected void onPostExecute(Message msg) {
				if(isDetached()) {
					return;
				}
				if (msg.what == 1) {
					newsDetail = (News) msg.obj;

					mTitleTextView.setText(newsDetail.getTitle());
					mAuthorTextView.setText(newsDetail.getAuthor());
					mDateTextView.setText(StringUtils.friendly_time(newsDetail.getPubDate()));
					mCountTextView.setText(String.valueOf(newsDetail
							.getCommentCount()));
					//TODO
					// 是否收藏
					if (newsDetail.getFavorite() == 1) {
						//mFavorite.setImageResource(R.drawable.widget_bar_favorite2);
					} else {
						//mFavorite.setImageResource(R.drawable.widget_bar_favorite);
					}

					// 显示评论数
					if (newsDetail.getCommentCount() > 0) {
						//bv_comment.setText(newsDetail.getCommentCount() + "");
						//bv_comment.show();
					} else {
						//bv_comment.setText("");
						//bv_comment.hide();
					}

					String body = UIHelper.WEB_STYLE + newsDetail.getBody();
					// 读取用户设置：是否加载文章图片--默认有wifi下始终加载图片
					boolean isLoadImage;
					if (AppContext.NETTYPE_WIFI == mApplication.getNetworkType()) {
						isLoadImage = true;
					} else {
						isLoadImage = mApplication.isLoadImage();
					}
					if (isLoadImage) {
						// 过滤掉 img标签的width,height属性
						body = body.replaceAll(
								"(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
						body = body.replaceAll(
								"(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");

						// 添加点击图片放大支持
						body = body.replaceAll("(<img[^>]+src=\")(\\S+)\"",
								"$1$2\" onClick=\"javascript:mWebViewImageListener.onImageClick('$2')\"");
					} else {
						// 过滤掉 img标签
						body = body.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
					}

					// 更多关于***软件的信息
					String softwareName = newsDetail.getSoftwareName();
					String softwareLink = newsDetail.getSoftwareLink();
					if (!StringUtils.isEmpty(softwareName)
							&& !StringUtils.isEmpty(softwareLink))
						body += String
								.format("<div id='oschina_software' style='margin-top:8px;color:#FF0000;font-weight:bold'>更多关于:&nbsp;<a href='%s'>%s</a>&nbsp;的详细信息</div>",
										softwareLink, softwareName);

					// 相关新闻
					if (newsDetail.getRelatives().size() > 0) {
						String strRelative = "";
						for (Relative relative : newsDetail.getRelatives()) {
							strRelative += String
									.format("<a href='%s' style='text-decoration:none'>%s</a><p/>",
											relative.url, relative.title);
						}
						body += String.format(
								"<p/><hr/><b>相关资讯</b><div><p/>%s</div>",
								strRelative);
					}

					body += "<div style='margin-bottom: 80px'/>";

					System.out.println(body);

					mWebView.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
					mWebView.setWebViewClient(UIHelper.getWebViewClient());

					// 发送通知广播
					if (newsDetail.getNotice() != null) {
						BroadcastController.sendNoticeBroadCast(getActivity(), newsDetail.getNotice());
					}
				} else if (msg.what == 0) {
					UIHelper.ToastMessage(getActivity(), R.string.msg_load_is_null);
				} else if (msg.what == -1 && msg.obj != null) {
					((AppException) msg.obj).makeToast(getActivity());
				}
			}
		}.execute();
	}
}