package com.zld.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.zld.R;
import com.zld.application;
import com.zld.bean.AppInfo;
import com.zld.bean.LeaveOrder;
import com.zld.bean.PullMessage;
import com.zld.lib.constant.Constant;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.util.BluetoothService;
import com.zld.lib.util.FileUtil;
import com.zld.lib.util.PollingUtils;
import com.zld.lib.util.UploadUtil;
import com.zld.ui.LoginActivity;
import com.zld.ui.ZldNewActivity;
import com.zld.view.RewardDialog;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class PollingService extends BaseService {

	private static final String TAG = "PollingService";
	public static final String ACTION = "com.zld.service.Polling_Service";
	private Notification mNotification;
	private NotificationManager mManager;
	private ZldNewActivity zldNewActivity;
	public static int count = 0;
	private String token;

	@Override
	public IBinder onBind(Intent intent) {
		IBinder result = null;
		if (null == result)
			result = new ServiceBinder();
		Toast.makeText(this, "onBind", Toast.LENGTH_LONG);
		return result;
	}

	@Override
	public void onCreate() {
		Log.e(TAG, "onstart");
		initNotifiManager();
		Log.e(TAG, "onstart");
		zldNewActivity = ((application) getApplication()).getZldNewActivity();
		token = AppInfo.getInstance().getToken();
		if (token == null) {
			PollingUtils.stopPollingService(this, ShareUiService.class, "com.zld.service.ShareUi_Service");
			stopSelf();
			return;
		}
		conn2bluetooth();
		if (token != null) {
			getLeaveInfo();
			Log.e(TAG, "token��Ϣ:" + token);
			new Thread(new Runnable() {
				public void run() {
					while (true) {
//						conn2bluetooth();
//						getLeaveInfo();
						countNum++;
						if(countNum>4){
							getLeaveInfo();
							countNum = 0;
						}
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}

			}).start();
		}

	}
	int countNum = 0;
	@Override
	public void onStart(Intent intent, int startId) {

	}

	/**
	 * ��ʼ��֪ͨ������
	 */
	private void initNotifiManager() {
		mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		int icon = R.drawable.app_icon_32;
		mNotification = new Notification();
		mNotification.icon = icon;
		mNotification.tickerText = "�Ƴ�������Ϣ";
		mNotification.defaults |= Notification.DEFAULT_SOUND;// ϵͳĬ������
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;// ����Զ���ʧ
	}

	/**
	 * ����Notification
	 */
	@SuppressWarnings("deprecation")
	private void showNotification() {
		mNotification.when = System.currentTimeMillis();
		// Navigator to the new activity when click the notification title
		Intent i = new Intent(this, ZldNewActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, Intent.FLAG_ACTIVITY_NEW_TASK);
		mNotification.setLatestEventInfo(this, getResources().getString(R.string.app_name), "��ʶ��ϵͳ����֧����Ϣ!",
				pendingIntent);
		mManager.notify(0, mNotification);
	}

	/**
	 * Polling thread ģ����Server��ѯ���첽�߳�
	 * http://s.zhenlaidian.com/mserver/getmesg.do?token=&currid= currid
	 * �����Ϣ���,δʹ��
	 */
	public void getLeaveInfo() {
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.serverUrl + Constant.GET_LEAVE_MESG);
		boolean isLocalServer = AppInfo.getInstance().getIsLocalServer(this);
		Log.e(TAG, "isLocalServer:" + isLocalServer);
		if (isLocalServer) {// �Ǳ��ط�����
			params.setUrlParams("comid", AppInfo.getInstance().getComid());
			params.setUrlParams("uin", AppInfo.getInstance().getUid());
			params.setUrlParams("notoken", "true");
		} else {// ���Ϸ�������token
			params.setUrlParams("token", AppInfo.getInstance().getToken());
		}
		params.setUrlParams("out", "json");
		String url = params.getRequstUrl();
		try {
			HttpManager.requestGET(this, url, this);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * ���µ�¼
	 */
	private void reLogin() {
		Intent intent = new Intent(PollingService.this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("token", "false");
		startActivity(intent);
		stopSelf();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("PollingService:onDestroy");
	}

	/**
	 * �жϵ�ǰӦ���Ƿ��ں�̨����
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isBackground(Context context) {

		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
					Log.i("��̨", appProcess.processName);
					return true;
				} else {
					Log.i("ǰ̨", appProcess.processName);
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public boolean doSucess(String url, String object) {
		// TODO Auto-generated method stub
		if (url.contains(Constant.GET_LEAVE_MESG)) {
			doGetLeaveMsg(object);
		}
		return true;
	}

	private void doGetLeaveMsg(String object) {
		// TODO Auto-generated method stub
		PullMessage pullmsg = null;
		Gson gson = null;
		try {
			gson = new Gson();
			pullmsg = gson.fromJson(object, PullMessage.class);
		} catch (Exception e) {
			Log.e("pollingService", "" + e.getMessage());
		}
		if (pullmsg == null || pullmsg.getMtype() == null) {
			return;
		}
		Log.e(TAG, "��ȡ������Ϣ����Ϊ" + pullmsg.getMtype());
		Log.e(TAG, "��ȡ���볡������Ϣ....." + pullmsg.toString());
		switch (Integer.parseInt(pullmsg.getMtype())) {
		case 0:
			if (pullmsg.getInfo() != null) {
				LeaveOrder leaveorder = gson.fromJson(pullmsg.getInfo(), LeaveOrder.class);
				Log.e(TAG, "���������볡����Ϊ" + leaveorder.toString());
				SharedPreferences sp1 = getSharedPreferences("config", Context.MODE_PRIVATE);
				boolean flag = sp1.getBoolean("active", false);
				if (zldNewActivity != null && !zldNewActivity.isFinishing()) {
					if (flag) {
						Log.e(TAG, "���ص��볡����...");
						Message msg1 = new Message();
						msg1.what = Constant.LEAVEORDER_MSG;// ��ȡ���볡����
						msg1.obj = leaveorder;

						zldNewActivity.handler.sendMessage(msg1);
						Log.e("pollingService", "flagΪtrue.........");
						break;
					} else {
						if (isBackground(PollingService.this) == false) {// ��ǰ̨
							Log.e(TAG, "������ǰ̨.........");
							Message msg2 = new Message();
							msg2.what = Constant.LEAVEORDER_MSG;// ��ȡ���볡����
							msg2.obj = leaveorder;
							zldNewActivity.handler.sendMessage(msg2);
							break;
						} else {// �ں�̨
							Log.e(TAG, "�����ں�̨.........");
							showNotification();
							Message msg3 = new Message();
							msg3.what = Constant.LEAVEORDER_MSG;// ��ȡ���볡����
							msg3.obj = leaveorder;
							zldNewActivity.handler.sendMessage(msg3);
						}
					}
				}
				System.out.println("New message!");
			}
			break;
		case -1:
			Log.e(TAG, "���token��״̬--token��Ч");
			// PollingUtils.stopPollingService(PollingService.this,PollingService.class,
			// "com.zld.service.Polling_Service");
			PollingUtils.stopPollingService(PollingService.this, ShareUiService.class,
					"com.zld.service.ShareUi_Service");
			PollingUtils.stopPollingService(PollingService.this, DownLoadService.class,
					"com.zld.service.DownLoadImage_Service");

			Message msg = new Message();
			msg.what = 4;// tokenʧЧ
			if (zldNewActivity != null) {
				zldNewActivity.finish();
			}
			reLogin();
			break;
		case 5:
			Log.e(TAG, "��ȡ��������Ϣ");
			if (pullmsg.getInfo() != null) {
				LeaveOrder leaveorder = gson.fromJson(pullmsg.getInfo(), LeaveOrder.class);
				Log.e(TAG, "�������볡��Ϣ��" + leaveorder.toString());
				new RewardDialog(zldNewActivity, com.zld.R.style.nfcnewdialog, leaveorder).show();
			}
			break;
		case 6:
			String isuplog = pullmsg.getInfo().get("isuplog").getAsString();
			Log.e("------------0000000000000000-----------", isuplog);
//			http://192.168.199.122/zld/
//			http://127.0.0.1/zld/collectorrequest.do?action=uplogfile
			
//			if(isuplog.equals("0")&&once){
//				Log.e("------------0000000000000000-----------", "�ϴ���");
//				File file = null;
//				try {
//					file = FileUtil.createSDFile();
//					UploadUtil.uploadFile(new FileInputStream(file));
////					UploadUtil.testUploadFile();
////					OkHttpUploadFile.OkGoUpload(this);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
////				UploadUtil.testUploadFile();
//				once = false;
//			}else{
//				Log.e("------------0000000000000000-----------", "û���ϴ�"+isuplog);
//			}
			
			if(isuplog.equals("1")){
				File file = null;
				try {
					file = FileUtil.createSDFile();
					UploadUtil.uploadFile(new FileInputStream(file));
				} catch (IOException e) {
					e.printStackTrace();
				}
//				UploadUtil.testUploadFile();
			}
			break;
		}
	}
	boolean once = true;

	@Override
	public boolean doFailure(String url, String status) {
		// TODO Auto-generated method stub
		return false;
	}

	// �˷�����Ϊ�˿�����Acitity�л�÷����ʵ��
	public class ServiceBinder extends Binder {
		public PollingService getService() {
			return PollingService.this;
		}
	}
	/**
	 * ��ӡСƱ
	 * @param message
	 * @param context
	 */
	public static void sendMessage(final String message, final Context context) {
        if (message.length() > 0) {
           
                byte[] send;
                try {
                    send = message.getBytes("GB2312");
                } catch (UnsupportedEncodingException e) {
                    send = message.getBytes();
                }
                mService.printLeft();
                mService.write(send);
            }
         else {

        }
    }
	public static boolean CanPrint = false;
    public static BluetoothService mService = null;
    Set<BluetoothDevice> pairedDevices;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


    private void conn2bluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            //������
//            System.out.println("������");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableIntent);
        }
//        System.out.println("�Դ�");
        if (mService == null) {
            mService = new BluetoothService();
        }
//        System.out.println("��ȡ�豸");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
//                System.out.println(device.getName() + "\n" + device.getAddress());
                Message m = new Message();
                m.what = 1001;
                m.obj = device;
                mHandler.sendMessage(m);
//                System.out.println("��ȡ��������Ϣ");
            }

        } else {
//            System.out.println("pairedDevices��sizeС��0");
            CanPrint = false;
        }
    }

	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1001:
                    BluetoothDevice device = (BluetoothDevice) msg.obj;
                    if (mService.getState() != BluetoothService.STATE_CONNECTED) {
                        mService.connect(device);
                        Log.d(TAG, "δ����--������");
                    } else {
                        Log.d(TAG, "������HHHHHHHHHH");
                        CanPrint = true;
                    }

                    break;
            }

        }
    };

}
