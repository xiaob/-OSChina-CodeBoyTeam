package com.codeboy.app.oschina.ui;

import net.oschina.app.bean.News;
import net.oschina.app.bean.News.Relative;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.core.AppContext;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.codeboy.app.library.util.L;
import com.codeboy.app.oschina.BaseFragment;
import com.codeboy.app.oschina.OSChinaApplication;
import com.codeboy.app.oschina.R;
import com.codeboy.app.oschina.core.Contanst;
import com.codeboy.app.oschina.modul.UpdateDatasEvent;

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
public class NewsDetailBodyFragment extends BaseFragment implements UpdateDatasEvent{
	
	public static NewsDetailBodyFragment newInstance(News news) {
		NewsDetailBodyFragment fragment = new NewsDetailBodyFragment();
		Bundle args = new Bundle();
		args.putSerializable(Contanst.NEWS_DATA_KEY, news);
		fragment.setArguments(args);
		return fragment;
	}
	
	private OSChinaApplication mApplication;
	
	private WebView mWebView;
	private TextView mTitleTextView;
	private TextView mAuthorTextView;
	private TextView mDateTextView;
	private TextView mCountTextView;
	private View mMainView;
	private View mProgressBar;
	
	private News newsDetail;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
	}
	
	@Override
	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle argus = getArguments();
		if(argus != null) {
			newsDetail = (News) argus.getSerializable(Contanst.NEWS_DATA_KEY);
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
		mMainView = view.findViewById(R.id.news_detail_scrollview);
		mProgressBar = view.findViewById(R.id.news_detail_progressbar);
		
		mMainView.setVisibility(View.INVISIBLE);
		
		mWebView = (WebView) view.findViewById(R.id.news_detail_webview);
		WebSettings settings = mWebView.getSettings();
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(false);
		//mWebView.getSettings().setDefaultFontSize(15);
		
		//TODO
        //UIHelper.addWebImageShow(getActivity(), mWebView);
		setupView();
		loadDatas();
	}
	
	private void setupView() {
		if(newsDetail == null || !isAdded()) {
			if(L.Debug) {
				L.d("newsDetail == null || !isAdded()-->" + isAdded());
			}
			return;
		}
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

		mTitleTextView.setText(newsDetail.getTitle());
		mAuthorTextView.setText(newsDetail.getAuthor());
		mDateTextView.setText(StringUtils.friendly_time(newsDetail.getPubDate()));
		mCountTextView.setText(String.valueOf(newsDetail.getCommentCount()));
	}
	
	private void dismissProgressBar() {
		if(mProgressBar != null) {
			mProgressBar.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 加载资讯数据
	 * */
	private void loadDatas() {
		if(newsDetail == null || !isAdded()) {
			if(L.Debug) {
				L.d("newsDetail == null || !isAdded()-->" + isAdded());
			}
			return;
		}
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				//异步更新数据
				final News news = newsDetail;
				
				String body = UIHelper.WEB_STYLE + news.getBody();
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
				String softwareName = news.getSoftwareName();
				String softwareLink = news.getSoftwareLink();
				if (!StringUtils.isEmpty(softwareName)
						&& !StringUtils.isEmpty(softwareLink))
					body += String
							.format("<div id='oschina_software' style='margin-top:8px;color:#FF0000;font-weight:bold'>更多关于:&nbsp;<a href='%s'>%s</a>&nbsp;的详细信息</div>",
									softwareLink, softwareName);

				// 相关新闻
				if (news.getRelatives().size() > 0) {
					String strRelative = "";
					for (Relative relative : news.getRelatives()) {
						strRelative += String
								.format("<a href='%s' style='text-decoration:none'>%s</a><p/>",
										relative.url, relative.title);
					}
					body += String.format(
							"<p/><hr/><b>相关资讯</b><div><p/>%s</div>",
							strRelative);
				}

				body += "<div style='margin-bottom: 80px'/>";

				if(L.Debug) {
					L.d(body);
				}
				return body;
			}
			
			@Override
			protected void onPreExecute() {
				
			}
			
			@Override
			protected void onPostExecute(String body) {
				if(isDetached()) {
					return;
				}
				mMainView.setVisibility(View.VISIBLE);
				dismissProgressBar();
					
				mWebView.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
				mWebView.setWebViewClient(UIHelper.getWebViewClient());
			}
		}.execute();
	}

	@Override
	public void onNotifyUpdate(Object obj) {
		if(obj == null || !(obj instanceof News)) {
			dismissProgressBar();
			return;
		}
		newsDetail = (News)obj;
		setupView();
		loadDatas();
	}
}