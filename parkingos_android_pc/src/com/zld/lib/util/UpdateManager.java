package com.zld.lib.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Date;

import com.zld.ui.HelloActivity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class UpdateManager {

	private Context mContext;
	private long lastModified = 0;

	public UpdateManager(Context context) {
		this.mContext = context;
	}

	// ��װapk��
	private void install(File file) {
		// �ɷ�������װ���Ժ�ֱ���˳�
		// Intent intent = new Intent();
		// intent.setAction(Intent.ACTION_VIEW);
		// intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
		// mContext.startActivity(intent);
		// ��װ���Ժ���ʾ���ڴ� by xulu
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		mContext.startActivity(intent);
		android.os.Process.killProcess(android.os.Process.myPid());

	}

	/**
	 * �°汾APK����
	 * 
	 * @author Clare
	 * 
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)

	public class DownLoadApkAsyncTask extends AsyncTask<String, Integer, File> {

		private ProgressDialog pd;
		private NotificationManager mNotificationManager;
		private NotificationCompat.Builder mBuilder;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(mContext);
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setMessage("��������");
			pd.setCancelable(false);
			pd.setProgressNumberFormat("%1dKB / %2dKB");
			pd.setButton(DialogInterface.BUTTON_POSITIVE, "��̨����", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					mNotificationManager = (NotificationManager) mContext
							.getSystemService(Context.NOTIFICATION_SERVICE);
					mBuilder = new NotificationCompat.Builder(mContext);
					mBuilder.setContentTitle("��������")
							// ����֪ͨ������
							.setContentText("�����أ�0%")
							// ����֪ͨ����ʾ����
							// .setNumber(number) //����֪ͨ���ϵ�����
							.setTicker("���ں�̨���أ������ɲ鿴����...") // ֪ͨ�״γ�����֪ͨ��������������Ч����
							.setWhen(System.currentTimeMillis())// ֪ͨ������ʱ�䣬����֪ͨ��Ϣ����ʾ��һ����ϵͳ��ȡ����ʱ��
							.setPriority(Notification.PRIORITY_DEFAULT) // ���ø�֪ͨ���ȼ�
							// .setAutoCancel(true)//���������־���û��������Ϳ�����֪ͨ���Զ�ȡ��
							.setOngoing(true);// true��������Ϊһ�����ڽ��е�֪ͨ������ͨ����������ʾһ����̨����,�û���������(�粥������)����ĳ�ַ�ʽ���ڵȴ�,���ռ���豸(��һ���ļ�����,ͬ������,������������)
					// .setDefaults(Notification.DEFAULT_LIGHTS)//
					// ��֪ͨ������������ƺ���Ч������򵥡���һ�µķ�ʽ��ʹ�õ�ǰ���û�Ĭ�����ã�ʹ��defaults���ԣ��������
					// Notification.DEFAULT_ALL
					// Notification.DEFAULT_SOUND ������� //
					// requires VIBRATE permission
					// .setSmallIcon(R.);// ����֪ͨСICON
					mNotificationManager.notify(0, mBuilder.build());
					pd.dismiss();
					if (mContext.getClass().equals(HelloActivity.class)) {
						HelloActivity activity = (HelloActivity) mContext;
						activity.loadMainUI();
					}
				}
			});
			pd.setButton(DialogInterface.BUTTON_NEGATIVE, "ȡ��", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					UpdateManager.DownLoadApkAsyncTask.this.cancel(true);
					Toast.makeText(mContext, "������ȡ����", Toast.LENGTH_SHORT).show();
					pd.dismiss();
					if (mContext.getClass().equals(HelloActivity.class)) {
						HelloActivity activity = (HelloActivity) mContext;
						activity.loadMainUI();
					}
				}
			});
			pd.show();
			super.onPreExecute();
		}

		@SuppressWarnings("resource")
		@Override
		protected File doInBackground(String... params) {
			String downloadUrl = params[0];
			if (TextUtils.isEmpty(downloadUrl) || !downloadUrl.endsWith(".apk")) {
				return null;
			}
			InputStream is = null;
			FileOutputStream fos = null;
			HttpURLConnection conn = null;
			try {
				URL url = new URL(downloadUrl);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(5000);
				if (conn.getResponseCode() == 200) {
					File file = createTargetFile();
					lastModified = conn.getLastModified();
					int length = conn.getContentLength();
					if (checkIfDownloaded(file, lastModified, length)) {
						if (mContext.getClass().equals(HelloActivity.class)) {
							HelloActivity activity = (HelloActivity) mContext;
							activity.loadMainUI();
						}
						return file;
					}
					if (pd.isShowing()) {
						pd.setMax(formatFileSize(length));
					}
					is = conn.getInputStream();
					fos = new FileOutputStream(file);
					byte[] buffer = new byte[1024];
					int len = 0;
					int total = 0;
					while ((len = is.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
						total += len;
						if (mBuilder != null) {
							String percent = new DecimalFormat("##%").format((total * 1.0 / (length * 1.0)));
							mBuilder.setProgress(length, total, false).setContentText("����ɣ�" + percent);// ֪ͨ����ʾ���ؽ�����
							mNotificationManager.notify(0, mBuilder.build());
						}
						if (pd.isShowing()) {
							pd.setProgress(formatFileSize(total));
						}
						if (DownLoadApkAsyncTask.this.isCancelled()) {
							return null;
						}
					}
					fos.flush();
					return file;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (is != null) {
						is.close();
					}
					if (fos != null) {
						fos.close();
					}
					if (conn != null) {
						conn.disconnect();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(File result) {
			if (result != null && lastModified != 0) {
				Log.e("UpdateManager", "���ñ����ļ�������ʱ�䣺--->> " + result.setLastModified(lastModified));
			}
			if (mBuilder != null) {
				if (result != null) {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(result), "application/vnd.android.package-archive");
					PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
					mBuilder.setContentTitle("���Ұ�װ�°汾").setProgress(100, 100, false).setOngoing(false)
							.setContentText("�°汾����������\n.../sdcard/Download/tingchebao.apk").setContentIntent(pIntent)
							.setTicker("������ɣ������װ��");
					mNotificationManager.notify(0, mBuilder.build());
				} else {
					mBuilder.setContentTitle("���س���").setProgress(0, 0, false).setContentText("���ص��ļ��𻵣����Ժ��������ء�")
							.setTicker("���س���").setAutoCancel(false).setOngoing(false);
					mNotificationManager.notify(0, mBuilder.build());
				}
				return;
			}
			if (pd.isShowing()) {
				pd.dismiss();
			}
			if (result != null) {
				install(result);
			} else {
				Toast.makeText(mContext, "���س���δ֪����", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}

	// ����°汾�Ƿ��Ѿ��������
	private File createTargetFile() {
		File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		File file = null;
		if (!downloadDir.exists()) {
			downloadDir.mkdirs();
		}
		File[] downloadedFiles = downloadDir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {
				return filename.contains("tingchebao");
			}
		});
		if (downloadedFiles != null && downloadedFiles.length > 0) {
			return downloadedFiles[0];
		} else {
			file = new File(downloadDir, "tingchebao.apk");
			try {
				file.createNewFile();
			} catch (IOException e) {
				Log.e("UpdateManager", "--->> ������ʱ�����ļ�ʧ�ܣ�����");
				e.printStackTrace();
			}
		}
		return file;
	}

	private boolean checkIfDownloaded(File target, long lastModified, long length) {
		if (target != null && target.exists()) {
			long targetFileLength = target.length();
			long targetFileLastModified = target.lastModified();
			Log.e("UpdateManager",
					"�����ļ�����ʱ�䣺--->> " + new Date(targetFileLastModified).toString() + ",��С��--->> " + targetFileLength
							+ "\n�������ļ�����ʱ�䣺--->> " + new Date(lastModified).toString() + ",��С��--->> " + length);
			return targetFileLength == length && targetFileLastModified >= lastModified;
		}
		return false;
	}

	// ���ֽ�ת��ΪMB��λ
	private int formatFileSize(int length) {
		return length / 1024;
	}
}
