package com.zld.lib.http;

import java.io.IOException;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zld.bean.AppInfo;
import com.zld.bean.LoginInfo;
import com.zld.lib.util.FileUtil;
import com.zld.lib.util.IsNetWork;
import com.zld.lib.util.OkHttpUtil;
import com.zld.lib.util.SharedPreferencesUtils;
import com.zld.lib.util.TimeTypeUtil;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings.Global;
import android.util.Log;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class HttpManager {

	private static final String TAG = "HttpManager";
//	private static final int timeout = 5 * 1000;
//	private static final int sharetimeout = 3 * 1000;
//	// private static final int logintimeout = 10 * 1000;
//	private static final int logintimeout = 5 * 1000;


	public static void requestGET(final Context context, final String requestUrls, final HttpCallBack callBack) {
		isInternet(context);
		final String requestUrl = requestUrls + "&r=" + Math.random();
		Log.e(TAG, "3��������" + requestUrl);
//		Request request = new Request.Builder().url(requestUrl).build();
//		OkHttpUtil.enqueue(request,new Callback() {
//			
//			@Override
//			public void onResponse(Call arg0, Response arg1) throws IOException {
//				// TODO Auto-generated method stub
//				Log.e(TAG, "3������  callback��" + requestUrl);
//				callBack.doSucess(requestUrl, arg1.body().string());
//			}
//			
//			@Override
//			public void onFailure(Call arg0, IOException e) {
//				// TODO Auto-generated method stub
//				if (e.getStackTrace() != null && e.getStackTrace().equals("timeout")) {
//					callBack.timeout(requestUrl);
//				} else {
//					// callBack.doFailure(requestUrl, arg1);
//				}
//			}
//		});
		OkHttpUtils.get().url(requestUrl).build().execute(new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int arg2) {
				// TODO Auto-generated method stub
				if (e.getStackTrace() != null && e.getStackTrace().equals("timeout")) {
					callBack.timeout(requestUrl);
				} else {
					// callBack.doFailure(requestUrl, arg1);
				}
			}

			@Override
			public void onResponse(String arg0, int arg1) {
				// TODO Auto-generated method stub
				Log.e(TAG, "3������  callback��" + requestUrl);
				callBack.doSucess(requestUrl, arg0);
			}

		});
	}


	public static void requestGET(final Context context, final String requestUrls, final String str,
			final HttpCallBack callBack) {
		// if(aQuery2 == null){
		// FileUtil.writeSDFile("+", TimeTypeUtil.getNowTime() + "
		// 4444����requestGET����ǰ:" + " url:" + requestUrl);
		final String requestUrl = requestUrls + "&r=" + Math.random();
		OkHttpUtils.get().url(requestUrl).build().execute(new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int arg2) {
				if (e.getStackTrace() != null && e.getStackTrace().equals("timeout")) {
					callBack.timeout(requestUrl);
				} else {
					// callBack.doFailure(requestUrl, arg1);
				}

			}

			@Override
			public void onResponse(String object, int arg1) {
				// TODO Auto-generated method stub
				Log.e(TAG, "4������  callback��" + requestUrl);

				callBack.doSucess(requestUrl, object, str);
			}

		});
	}


	public static void requestGET(final Context context, final String requestUrls, final String str1, final String str2,
			final HttpCallBack callBack) {
		final String requestUrl = requestUrls + "&r=" + Math.random();
		OkHttpUtils.get().url(requestUrl).build().execute(new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int arg2) {
				if (e.getStackTrace() != null && e.getStackTrace().equals("timeout")) {
					callBack.timeout(requestUrl);
				} else {
					// callBack.doFailure(requestUrl, arg1);
				}
			}

			@Override
			public void onResponse(String arg0, int arg1) {
				// TODO Auto-generated method stub
				Log.e(TAG, "5������  callback��" + requestUrl);
				callBack.doSucess(requestUrl, arg0, str1, str2);
			}

		});

	}


	public static void requestGET(final Context context, final String requestUrls, final byte[] buffer,
			final String username, final String password, final HttpCallBack callBack) {
		final String requestUrl = requestUrls + "&r=" + Math.random();
		Log.e(TAG, "6��������" + requestUrl);
		OkHttpUtils.get().url(requestUrl).build().execute(new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int arg2) {
				if (e.getStackTrace() != null && e.getStackTrace().equals("timeout")) {
					callBack.timeout(requestUrl);
				} else {
					// callBack.doFailure(requestUrl, arg1);
				}
			}

			@Override
			public void onResponse(String arg0, int arg1) {
				// TODO Auto-generated method stub
				Log.e(TAG, "6������  callback��" + requestUrl);
				// FileUtil.writeSDFile(" ","6������requestGET
				// callback��"+requestUrl+" status.getCode()="+arg1+arg0);
				callBack.doSucess(requestUrl, arg0, buffer, username, password);
			}

		});

	}

	// loginactivity 552 switchaccount 251
	public static void requestCominfoGET(final Context context, final String requestUrl, final HttpCallBack callBack,
			final String username2, final String password2, final LoginInfo info2) {

		FileUtil.writeSDFile("  ", "requestCominfoGET��" + requestUrl);
		OkHttpUtils.get().url(requestUrl).build().execute(new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int arg2) {
				if (e.getStackTrace() != null && e.getStackTrace().equals("timeout")) {
					callBack.timeout(requestUrl);
				} else {
					// callBack.doFailure(requestUrl, arg1);
				}
				FileUtil.writeSDFile("  ", "  requestCominfoGET exception:" + e.getCause() + "\n msg:" + e.getMessage()
						+ "   url:" + requestUrl);
			}

			@Override
			public void onResponse(String object, int arg1) {
				// TODO Auto-generated method stub
				Log.e(TAG, "requestCominfoGET  callback��" + requestUrl);
				FileUtil.writeSDFile("  ",
						"requestCominfoGET callback��" + requestUrl + " status.getCode()=" + arg1 + object);
				callBack.doSucess(requestUrl, object, username2, password2, info2);
				;
			}

		});
	}

	static long latesttime = 0;

	public static void requestLoginGET(final Context context, final String requestUrl, final HttpCallBack callBack,
			final String username2, final String password2) {
		FileUtil.writeSDFile("  ", "requestLoginGET��" + requestUrl);
		isInternet(context);
		Request request = new Request.Builder().url(requestUrl).build();
		OkHttpUtil.enqueue(request,new Callback() {
			@Override
			public void onFailure(Call arg0, IOException e) {
				if (e.getStackTrace() != null && e.getStackTrace().equals("timeout")) {
					callBack.timeout(requestUrl, username2, password2);
				} else {
					// callBack.doFailure(requestUrl, arg1);
				}
				FileUtil.writeSDFile("  ", "  requestLoginGET exception:" + e.getCause() + "\n msg:" + e.getMessage()
						+ "   url:" + requestUrl);
			}

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				callBack.doSucess(requestUrl, arg1.body().bytes(), username2, password2);
			}
		});

	}


	public static void requestLoginTest(final Context context, final String requestUrl, final HttpCallBack callBack,
			final String username2, final String password2) {
		isInternet(context);
		Request request = new Request.Builder().url(requestUrl).build();
		OkHttpUtil.enqueue(request, new Callback() {
			@Override
			public void onFailure(Call arg0, IOException e) {
				if (e.getStackTrace() != null && e.getStackTrace().equals("timeout")) {
					callBack.timeout(requestUrl, username2, password2);
				} else {
					// callBack.doFailure(requestUrl, arg1);
				}
				FileUtil.writeSDFile("  ", "  requestLoginGET exception:" + e.getCause() + "\n msg:" + e.getMessage()
						+ "   url:" + requestUrl);
			}

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				callBack.doSucess(requestUrl, arg1.body().bytes(), username2, password2);
			}
		});
	}


	public static void requestShareGET(final Context context, final String requestUrl, final HttpCallBack callBack) {
		isInternet(context);
		Request request = new Request.Builder().url(requestUrl).build();
		OkHttpUtil.enqueue(request, new Callback() {
			@Override
			public void onFailure(Call arg0, IOException e) {
				if (e.getStackTrace() != null && e.getStackTrace().equals("timeout")) {
					callBack.timeout(requestUrl);
				} else {
					// callBack.doFailure(requestUrl, arg1);
				}
				FileUtil.writeSDFile("  ", "  requestShareGET exception:" + e.getCause() + "\n msg:" + e.getMessage()
						+ "   url:" + requestUrl);
			}

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				callBack.doSucess(requestUrl, arg1.body().bytes());
			}
		});
		
	}

	/**
	 * ������,�������ػ�
	 * 
	 * @param context
	 */
	@SuppressLint("InlinedApi")
	public static void isInternet(Context context) {
		if (!IsNetWork.IsHaveInternet(context)) {
			// �Ƿ��Ǳ��ط�����
			if (!AppInfo.getInstance().getIsLocalServer(context)) {// �Ǳ��ط�������û��ƽ�屾�ػ��ĸ���
				// ��ǰ���粻����
				boolean isLocal = SharedPreferencesUtils.getParam(context.getApplicationContext(), "nettype", "isLocal",
						false);
				Log.e("isLocal", "HttpManager isInternet get isLocal " + isLocal);
				if (isLocal) {
					// Ϊ���ػ�ʱ��׼һ�㣬����������ʱ�䣻
					ContentResolver cv = context.getContentResolver();
					String isAutoTime = android.provider.Settings.System.getString(cv, Global.AUTO_TIME);
					if ("0".equals(isAutoTime)) {// 1=yes, 0=no
						/*
						 * SharedPreferencesUtils.setParam(
						 * context.getApplicationContext(), "nettype",
						 * "isLocal", false);
						 */
						return;
					}
					// �ж϶����Ƿ񳬹�5����
					if (TimeTypeUtil.isOffFiveMinutes(context)) {
					} else {
					}
				}
				/*
				 * SharedPreferencesUtils.setParam(
				 * context.getApplicationContext(), "nettype", "isLocal", true);
				 */
				return;
			} else {
				// Log.e(TAG,"������������״̬,���������,������wifi�������,û����,Ҳ��������");
				/*
				 * SharedPreferencesUtils.setParam(
				 * context.getApplicationContext(), "nettype", "isLocal",
				 * false);
				 */
			}
		}
	}

}
