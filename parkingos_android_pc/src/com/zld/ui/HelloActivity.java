package com.zld.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//import com.blueware.agent.android.BlueWare;
import com.networkbench.agent.impl.NBSAppAgent;
//import com.oneapm.agent.android.OneApmAgent;
import com.umeng.analytics.MobclickAgent;
import com.vzvison.MainActivity;
import com.zld.R;
import com.zld.application;
import com.zld.bean.AppInfo;
import com.zld.bean.UpdataInfo;
import com.zld.engine.UpdataInfoParser;
import com.zld.lib.constant.Constant;
import com.zld.lib.util.AppInfoUtil;
import com.zld.lib.util.FileUtil;
import com.zld.lib.util.SharedPreferencesUtils;
import com.zld.service.UpdateService;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("SdCardPath")
public class HelloActivity extends BaseActivity {

	private static final String TAG = "HelloActivity";
	private TextView tv_hello_version;
	private LinearLayout ll_hello_main;
	private UpdataInfo info;
	private String versiontext;
	private Handler handler;
	private final int UPDATE = 888;

	@SuppressLint("HandlerLeak")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		setContentView(R.layout.hello_activity);
		MobclickAgent.updateOnlineConfig(this);// ���˷��Ͳ��ԣ�
//		BlueWare.withApplicationToken("5106067A94F46EB853A5042CF1F00C4E64").start(this.getApplication());
//		OneApmAgent.init(this.getApplicationContext()).setToken("5106067A94F46EB853A5042CF1F00C4E64").start();
		NBSAppAgent.setLicenseKey("a04ad42a66984f4391c6a23596a9dc9c")
		.withLocationServiceEnabled(true).start(this);
		((application) getApplication()).setHelloActivity(this);
		initview();
		setVersion();
		saveVersion();
		initAnimation();
		//ģ�����رձ��ػ�
		SharedPreferencesUtils.setParam(
				getApplicationContext(), "nettype", "isLocal", false);
		Log.e("isLocal","HelloActivity onCreate set isLocal false");
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case UPDATE:
					if(FileUtil.getSDCardPath() == null){
						showToast("sd��������");
						loadMainUI();
						return;
					}

					String version_num = SharedPreferencesUtils.getParam(
							getApplicationContext(), "version", "new_version", "111");

					System.out.println("�ͻ��˰汾��:"+Integer.parseInt(versiontext)+
							"���ر�����°汾��:"+Integer.parseInt(version_num));

					Intent intentact = new Intent(HelloActivity.this, LoginActivity.class);
					if(Integer.parseInt(versiontext) < Integer.parseInt(version_num)){
						//�ͻ��˰汾��С���°汾��,ѡ����´ΰ�װ
						File tempFile = new File(FileUtil.getSDCardPath(),"/tingchebaohd_hd.apk");
						if(tempFile.exists()){
							Uri fromFile = Uri.fromFile(tempFile);
						    Bundle bundle = new Bundle();
							bundle.putParcelable("installUri", fromFile);
							intentact.putExtras(bundle);
						}else{
							Log.i(TAG, "����������apk�ļ�" + info.getApkurl());
							Intent intent = new Intent(HelloActivity.this,UpdateService.class);
							intent.putExtra("urlpath", info.getApkurl());
							intent.putExtra("version", info.getVersion());
							startService(intent);
						}
					}else if(Integer.parseInt(versiontext) == Integer.parseInt(version_num)){

					}else{
						//û�������ļ�ʱ������
						Log.i(TAG, "����������apk�ļ�" + info.getApkurl());
						Intent intent = new Intent(HelloActivity.this,UpdateService.class);
						intent.putExtra("urlpath", info.getApkurl());
						intent.putExtra("version", info.getVersion());
						startService(intent);
					}
					startActivity(intentact);
					break;
				}
			}
		};
		isNeedUpdate();			// �����£�
//		initImageLoader();  	// ��ʼ��imageLoader��
		FileUtil.buildFolder();	// �����ļ���
	}

	private void initAnimation() {
		AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
		aa.setDuration(2000);
		ll_hello_main.startAnimation(aa);
	}

	private void saveVersion() {
		SharedPreferencesUtils.setParam(getApplicationContext(),
				"version", "old_version", versiontext);
	}

	private void setVersion() {
		versiontext = AppInfoUtil.getVersionCode(this);
		tv_hello_version.setText(versiontext);
	}

	private void initview() {
		ll_hello_main = (LinearLayout) findViewById(R.id.ll_hello);
		tv_hello_version = (TextView) findViewById(R.id.tv_hello_version);
	}

	public void isNeedUpdate() {
		new Thread(runnable).start();
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			HttpURLConnection conn = null;
			InputStream inputStream = null;
			try {
				URL url = new URL(Constant.UPDATE_URL);
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(10000);
				conn.setReadTimeout(8000);
				conn.setDoInput(true);
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Content-Type", "text/html");
				conn.setRequestProperty("Accept-Charset", "utf-8");
				conn.setRequestProperty("contentType", "utf-8");
				inputStream = conn.getInputStream();
				byte[] buffer = null;
				if (conn.getResponseCode() == 200) {
					SharedPreferencesUtils.setParam(
							getApplicationContext(), "nettype", "isLocal", false);
					Log.e("isLocal","HelloActivity Runnable set isLocal false");
					buffer = new byte[1024];
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					int len;
					while ((len = inputStream.read(buffer)) != -1) {
						out.write(buffer, 0, len);
					}
					buffer = out.toByteArray();
				}
				InputStream is = new ByteArrayInputStream(buffer);
				info = UpdataInfoParser.getUpdataInfo(is);
				String version = info.getVersion();
				if (version == null || version.equals("")) {
					Log.i(TAG, "��ȡ����˰汾���󣬽���������");
					loadMainUI();
				} else {
					if (Integer.parseInt(versiontext) >= Integer.parseInt(version)) {
						Log.i(TAG, "�������°�,��������, ����������");
						loadMainUI();
					} else {
						Log.i(TAG, "�汾��ͬ,��Ҫ����");
						Message message = new Message();
						message.what = UPDATE;
						handler.sendMessage(message);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				//�Ƿ��Ǳ��ط�����
				if(!AppInfo.getInstance().getIsLocalServer(HelloActivity.this)){//�Ǳ��ط�������û��ƽ�屾�ػ��ĸ���
					//����,���糬ʱ  �������ػ�
//					SharedPreferencesUtils.setParam(
//							getApplicationContext(), "nettype", "isLocal", true);
//					Log.e("isLocal","HelloActivity Runnable set isLocal false");
				}
				loadMainUI();
			} finally {
				try {
					if (inputStream != null) {
						inputStream.close();
					}
					if (conn != null) {
						conn.disconnect();
					}
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("HelloActivity", "�ͷ���Դ����");
				}
			}
		}
	};

	public void loadMainUI() {
		Intent intent = new Intent(this, LoginActivity.class);
//		Intent intent = new Intent(this, MainActivity.class);

		startActivity(intent);
		finish(); 
	}

//	public void onResume() {
//		super.onResume();
//		MobclickAgent.updateOnlineConfig(this);// ���˷��Ͳ��ԣ�
//		MobclickAgent.onResume(this);
//	}
//
//	public void onPause() {
//		super.onPause();
//		MobclickAgent.onPause(this);
//	}

	
}
