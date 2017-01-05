package com.zld.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.zld.application;
import com.zld.bean.AppInfo;
import com.zld.bean.LoginInfo;
import com.zld.bean.SmAccount;
import com.zld.db.SqliteManager;
import com.zld.engine.LoginInfoParser;
import com.zld.lib.constant.Constant;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.util.AppInfoUtil;
import com.zld.lib.util.IsNetWork;
import com.zld.lib.util.MD5Utils;
import com.zld.lib.util.SharedPreferencesUtils;

/**
 * PING ��ַ
 * 
 * @author HZC
 *
 */
public class DetectionServerService extends BaseService {

	Thread thread;
	private String token;
	int i = 0;
	private Context context;
	private SqliteManager sqliteManager;
	private static final String TAG = "DetectionServerService";
	boolean isLocalServer;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		context = this;
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					Log.e(TAG, "ÿ10sִ��һ��ping " + Constant.PING_TEST_LOCAL);
					token = AppInfo.getInstance().getToken();
					boolean ping = IsNetWork.ping();
					Log.e(TAG, "ping:" + ping + " i��ֵ��" + i + "==" + Constant.PING_TEST_LOCAL);
					if (ping) {
                        // �Ƿ��Ǳ��ط�����
//                        boolean isLocalServer = SharedPreferencesUtils.getParam(getApplicationContext(), "nettype",
//                                "isLocalServer", false);
                        Log.e(TAG, "DetectionService isLocalServer��true�Ǳ��ط�����:" + isLocalServer);
                        if (isLocalServer) {// �Ǳ��ط�����,����Pingͨ

                            if (Constant.requestUrl.contains("s.tingchebao.com")) {// ��ʱ����״̬,Pingͨ����,��ʾ������¼����
                                i = 0;
                                SharedPreferencesUtils.setParam(getApplicationContext(), "nettype", "linelocal", true);
                                SharedPreferencesUtils.setParam(getApplicationContext(), "nettype", "isLocalServer", true);
                                reStart(false);// ���ط�������ͨ,ȷ���л���������
                            } else {// ��ʱ����״̬,��Pingͨ�Ͳ��ù�
                            }
                        } else {
                            // �����pingͨ�����ϵ�ַ���Ͱ�ping��ַ���ɱ��صģ�����ping���ط�����
                            String localip = SharedPreferencesUtils.getParam(getApplicationContext(), "nettype",
                                    "localip", null);
                            if (!TextUtils.isEmpty(localip)) {
                                Constant.pingUrl(localip);
                                isLocalServer = true;
//                                SharedPreferencesUtils.setParam(getApplicationContext(), "nettype", "isLocalServer",
//                                        true);
                            }
                            Log.e(TAG, "  token:" + token);
                            if (token == null) {
                                login();
                            }
                        }
                        i = 0;
                    } else {// Ping��ͨ
                        if (Constant.requestUrl.contains("s.tingchebao.com")) {
                            // ��ʱ����״̬,Ping��ͨ����,��������
                        } else {
                            // ��ʱ����״̬,Ping��ͨ��׼��������
                            i++;
                            if (i > 2) {
                                // �Ƿ��Ǳ��ط�����
                                boolean isLocalServer = SharedPreferencesUtils.getParam(getApplicationContext(),
                                        "nettype", "isLocalServer", false);
                                Log.e(TAG, "DetectionService isLocalServer��true:" + isLocalServer);
                                if (isLocalServer) {// �Ǳ��ط�����
                                    String linelocal = SharedPreferencesUtils.getParam(getApplicationContext(),
                                            "nettype", "linelocal", "local");
                                    Log.e(TAG, "�Ǳ��ط�����,Ping��ͨ,��ǰ״̬��" + linelocal);
                                    if (linelocal.equals("line")) {// ���ط�����������ʱPing����Ping��ͨ,���ô���
                                        i = 0;
                                    } else {// ���ط������ڱ���ʱPing��ͨ,���µ�¼

                                        SharedPreferencesUtils.setParam(getApplicationContext(), "nettype",
                                                "isLocalServer", false);
                                        Log.e(TAG, "������false");
//                                        FileUtil.writeSDFile("�л�����", "  ������false");

                                        reStart(true);// ���ط������쳣,ȷ���л������Ϸ�������
                                    }
                                } else {// ���Ǳ��ط�����,Ping��ͨ,����ƽ�屾�ػ�
                                    // ����3��ûPingͨ �������ػ�ģʽ
                                    Log.e(TAG, "DetectionService onStart set isLocal true");
                                }
                            }
                        }
                    }
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		IBinder result = null;
		if (null == result)
			result = new ServiceBinder();
		Toast.makeText(this, "onBind", Toast.LENGTH_LONG);
		return result;
	}

	@Override
	public void onStart(Intent intent, int startId) {

	}

	/**
	 * ��������¼ʱ�л�
	 * 
	 * @param isLine
	 *            true���������� false ����������
	 */
	private void reStart(boolean isLine) {
		// ���͹㲥�����Ϳ���,��¼ʱ,Ĭ�ϵ�¼����,��¼����,�Զ��л�Ϊ����
		Intent inte = new Intent();
		Bundle bundle = new Bundle();
		bundle.putBoolean("isLine", isLine);
		inte.putExtras(bundle);
		inte.setAction("com.zld.action.restartservicereceiver");
		sendBroadcast(inte);
	}

	private void login() {
		String uid = AppInfo.getInstance().getUid();
		Log.e(TAG, "�˺ţ�" + uid);
		if (uid != null) {
			SmAccount selectAccount = sqliteManager.selectAccountByUid(uid);
			if (selectAccount != null) {
				String username = selectAccount.getUsername();
				String password = selectAccount.getPassword();
				Log.e(TAG, "���ݿ��б�����û���Ϣ��" + username + "---123456:" + password);
				if (username != null) {
					String md5password = null;
					try {
						md5password = MD5Utils.MD5(MD5Utils.MD5(password) + "zldtingchebao201410092009");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					longinSuccess(username, md5password, password);
				}
			}
		}
	}

	/**
	 * �л��˺�����ĵ�¼����worksite_id�ֶΣ�Ϊ�˽��Ӱ� ���û�������˺ź������ύ������������֤�˻��������Ƿ���ȷ��
	 */
	public void longinSuccess(final String username, final String MD5password, final String password) {
		String worksiteId = SharedPreferencesUtils.getParam(getApplicationContext(), "set_workStation",
				"workstation_id", "");
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.LOGIN);
		params.setUrlParams("username", username);
		params.setUrlParams("password", MD5password);
		params.setUrlParams("version", AppInfoUtil.getVersionName(this));
		params.setUrlParams("worksite_id", worksiteId);
		String url = params.getRequstUrl();
		Log.e(TAG, "��¼��URL---------------->>" + url);
		HttpManager.requestLoginTest(this, url, this, username, password);
	}

	@Override
	public boolean doSucess(String url, byte[] buffer, String username, String password) {
		if (url.contains(Constant.LOGIN)) {
			Log.e(TAG, "��ȡ��¼��ϢΪ��" + Constant.LOGIN + "---------------->>" + buffer);
			doLoginResult(buffer, username, password);
		}
		return true;
	}

	private void doLoginResult(byte[] buffer, String username, String password) {
		// TODO Auto-generated method stub
		if (buffer != null) {
			try {
				Log.e(TAG, "��½�ķ�����Ϣ��---" + new String(buffer, "utf-8"));
				InputStream is = new ByteArrayInputStream(buffer);
				LoginInfo info = LoginInfoParser.getLoginInfo(is);
				if (null != info) {
					if (null != info.getState() && info.getState().equals("1")) {
						SharedPreferencesUtils.delete(context, "autologin", info.getName());
						sqliteManager.deleteAccountData(info.getName());
						return;
					}
					Log.e(TAG, "������¼��Ϣ��" + info.toString() + "==username==" + username);
					if (info.getInfo().equals("success")) {
						token = info.getToken();
						Log.e(TAG, "success��token��" + token);
						if (token != null) {
							AppInfo.getInstance().setToken(token);
							AppInfo.getInstance().setUid(username);
						}
						/* �����¼ʱ�� */
						saveLongOnTime(username, info);
						SmAccount selectAccount = sqliteManager.selectAccountByUsrName(username);
						if (selectAccount == null) {
							sqliteManager.insertAccountData(info.getName(), username, password);
						} else {
							if (!selectAccount.getPassword().equals(password)
									|| !selectAccount.getAccount().equals(info.getName())) {
								sqliteManager.updateAccountData(info.getName(), username, password);
							}
						}
						// ���͹㲥
						Intent intent = new Intent();
						intent.setAction("com.zld.action.startservicereceiver");
						sendBroadcast(intent);
					} else {
						Log.e(TAG, "��¼ʧ�ܣ�" + info.getInfo());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** ֮ǰ�Ĵ�û�й���¼�˺�,���¼���˺Ÿ�֮ǰ�Ĳ�һ��,�򱣴��¼ʱ��,���ڲ�ѯ���Ӱ�����Ϣ */
	private void saveLongOnTime(final String username, LoginInfo info) {
		String beforeUser = SharedPreferencesUtils.getParam(context.getApplicationContext(), "autologin", "account",
				"");
		Log.e(TAG, "beforeUser:" + beforeUser + "	username:" + username);
		if (TextUtils.isEmpty(beforeUser) || !beforeUser.equals(username)) {
			Log.e(TAG, "Ϊnull��ͬ�򱣴�" + info.getLogontime());
			SharedPreferencesUtils.setParam(context.getApplicationContext(), "autologin", "logontime",
					info.getLogontime());
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "����DetectionServerService ");
	}

	// �˷�����Ϊ�˿�����Acitity�л�÷����ʵ��
	public class ServiceBinder extends Binder {
		public DetectionServerService getService() {
			return DetectionServerService.this;
		}
	}
}
