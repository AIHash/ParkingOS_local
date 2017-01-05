package com.zld.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zld.application;
import com.zld.bean.AppInfo;
import com.zld.bean.CarNumberMadeOrder;
import com.zld.bean.EscapedOrder;
import com.zld.bean.MyCameraInfo;
import com.zld.db.SqliteManager;
import com.zld.fragment.EntranceFragment;
import com.zld.lib.constant.Constant;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.state.EntranceOrderState;
import com.zld.lib.util.CameraManager;
import com.zld.lib.util.FileUtil;
import com.zld.lib.util.ImageUitls;
import com.zld.lib.util.SharedPreferencesUtils;
import com.zld.lib.util.StringUtils;
import com.zld.lib.util.TimeTypeUtil;
import com.zld.lib.util.VoicePlayer;
import com.zld.photo.DecodeManager;
import com.zld.photo.UpLoadImage;
import com.zld.ui.ZldNewActivity;
import com.zld.view.LineLocalRestartDialog;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeExitPageService extends BaseService {

	private static final String TAG = "HomeExitPageService";
	private static final String INTENT_KEY = "intentkey";

	private Intent intent;
	public Bundle mBundle;
	int xCoordinate;
	int yCoordinate;
	int carPlateheight;
	int carPlatewidth;
//	private int resType;
	private String cameraIp;
	private Context context;
	private Bitmap resultBitmap;
	private String netType = Constant.sZero;
	private String carPlate = "";
	private String isPole;
	private String poleRecordID;
	int i = 0;
	private String uid;
	private Toast mToast;
	private String comid;
	private String passid;
	private long time = 0;
	private Timer timer;
	public String issuplocal;
	private boolean isCameraOk = false;
	private int confidenceLevel;
	ArrayList<MyCameraInfo> selectCamera;
	public ArrayList<MyCameraInfo> selectCameraIn;
//	/** ��ȡ���ͬһͨ���µ�cameraip��MyLedInfo */
//	public HashMap<String, MyLedInfo> selectIpIn;

//	boolean flag = true;// ����ֹͣ�߳�
	private SqliteManager sqliteManager;
	private EntranceFragment entranceFragment;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.COMECAR_MSG:
				FileUtil.writeSDFile("�볡̧������", "");
				if (msg.obj instanceof Bitmap) {
					Log.e(TAG, "ServiceͼƬ����+isPole+" + isPole);
					if (isPole != null && isPole.equals("TRUE")) {
						Log.e(TAG, "Service����̧��ͼƬ����");
						resultBitmap = (Bitmap) msg.obj;
						mBundle = msg.getData();
						mBundle.putString("POLEID", poleRecordID);
						byte[] bitmapByte = ImageUitls.bitmapByte(resultBitmap);
						mBundle.putByteArray("bitmap", bitmapByte);
						sendKey(ZldNewActivity.POLE_UP_IMAGE, null, null, null);
						isPole = "false";
						break;
					} else {
						Log.e(TAG, "Handler��65 ��ȡ��ͼƬ");
						callbackBitmap(msg);
					}
				}
				break;
			case Constant.SHOWVIDEO_MSG:
				callbackBitmap(msg);
				break;
			case Constant.OPENCAMERA_SUCCESS_MSG:
				Log.e(TAG, "Service����������ͷ�ɹ�");
				DecodeManager.getinstance().setConfidenceLevel(confidenceLevel);
				Toast.makeText(getApplicationContext(), "��ڴ���������ͷ�ɹ�", Toast.LENGTH_LONG).show();
				sendKey(ZldNewActivity.CANCEL_HOME_CAMERA_ERROR_DIALOG, null, null, null);
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						if (selectCameraIn.size() > 0)
//							uploadCameraState(selectCameraIn.get(0), Constant.CAMERA_STATE_SUCCESS);
//					}
//				}).start();
				break;
			case Constant.OPENCAMERA_FAIL_MSG:
				Log.e(TAG, "Service����������ͷʧ��");
				if (msg.arg1 == -1) {
					showToast("�������ͷ���ӳ���");
					CameraManager.reOpenCamera();
				} else if (msg.arg2 == 0) {
					// Toast.makeText(getApplicationContext(), "��ڴ���������ͷʧ��",
					// 1).show();
					Log.e("--","��ڴ���������ͷʧ��");
				}
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						if (selectCameraIn.size() > 0)
//							uploadCameraState(selectCameraIn.get(0), Constant.CAMERA_STATE_FAILE);
//					}
//				}).start();
				break;
			case Constant.RESTART_YES:
				sendKey(ZldNewActivity.RESTART, null, null, null);
				break;
			case Constant.KEEPALIVE:
				Log.e("-----", "�����KEEPALIVEKEEPALIVE");
				time = System.currentTimeMillis();
				sendKey(ZldNewActivity.CANCEL_HOME_CAMERA_ERROR_DIALOG, null, null, null);
				break;
			case Constant.KEEPALIVE_TIME:
				Log.e(TAG, "�������ͷ���ӶϿ�����" + isCameraOk);
				showToast("�������ͷ���ӶϿ�����");
				// if(isCameraOk){//��������ͷ����û����
				sendKey(ZldNewActivity.HOME_CAMERA_ERROR_DIALOG, null, null, null);
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						if (selectCameraIn.size() > 0)
//							uploadCameraState(selectCameraIn.get(0), Constant.CAMERA_STATE_FAILE);
//					}
//				}).start();
				break;
			}
		}

		private void callbackBitmap(Message msg) {
			FileUtil.writeSDFile("�볡̧������", "callbackBitmap");
			String numbers = Constant.sZero;
			String tsNumber = SharedPreferencesUtils.getParam(context, "carNumber", "carNumber", numbers);
			int carNumber = Integer.valueOf(tsNumber);
			if (carNumber <= 0) {
				String fullset = SharedPreferencesUtils.getParam(getApplicationContext(), "zld_config", "fullset", Constant.sZero);
				if (fullset.equals(Constant.sOne)) {
					showToast("��λ����");
					sendKey(ZldNewActivity.FULL, null, "��λ����", "��λ����");
					return;
				}
			}
			resultBitmap = (Bitmap) msg.obj;
			mBundle = msg.getData();

			int resType = mBundle.getInt("resType");// ��Դ����
			carPlateheight = mBundle.getInt("carPlateheight");
			carPlatewidth = mBundle.getInt("carPlatewidth");
			xCoordinate = mBundle.getInt("xCoordinate");
			yCoordinate = mBundle.getInt("yCoordinate");
			carPlate = mBundle.getString("carPlate");
			cameraIp = mBundle.getString("cameraIp");

			byte[] bitmapByte = ImageUitls.bitmapByte(resultBitmap);
			mBundle.putByteArray("bitmap", bitmapByte);
			if (intent == null) {
				intent = new Intent("android.intent.action.exit");
			}
			try {
				passid = getPassid(cameraIp);
				if (resType == 4) {
					String carnumber = StringUtils.buildCarNumber(context);
					carPlate = "��" + (carnumber);
				}

				// �Ƿ��Ǳ��ط�����
				boolean isLocalServer = SharedPreferencesUtils.getParam(getApplicationContext(), "nettype",
						"isLocalServer", false);
				if (!isLocalServer) {// ���Ǳ��ط�����
					// ���ػ����
					boolean param = SharedPreferencesUtils.getParam(getApplicationContext(), "nettype", "isLocal",
							false);
					Log.e("isLocal", "HomeExitPageService callbackBitmap get isLocal " + param);
				}
				FileUtil.writeSDFile("�볡̧������", "resType="+resType);
				if (resType == 8) {
					madeOrder(carPlate, 0);
				} else if (resType == 4) {
					Log.e(TAG, "�ֶ���������Service���ɶ�������¼Ϊ1");
					madeOrder(carPlate, 1);
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
//	ZldNewActivity zldNewActivity;
	@Override
	public void onCreate() {
		super.onCreate();
//		zldNewActivity = ZldNewActivity.getInstance();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		context = this;
		initTimer();
		init(intent);
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * ��ʼ����ʱ��
	 */
	private void initTimer() {
		// TODO Auto-generated method stub
		if (timer == null) {
			timer = new Timer();
		}
	}

	/**
	 * 
	 * ����˵������ʼ�� ����: 2015��3��13��
	 *
	 */
	private void init(Intent intent) {
		if (intent != null) {
			Bundle extras = intent.getExtras();
			String intent_key = extras.getString(INTENT_KEY);
			if (intent_key.equals("catchimage")) {
				isPole = extras.getString("POLE");
			}

			poleRecordID = extras.getString("POLEID");
			if (intent_key.equals("init")) {
				// ��ʼ��ת���ݵ���Ϣ
				initGetInfo(extras);
			} else if (intent_key.equals("catchimage")) {
				// ��������ͷץȡͼƬ
				Log.e(TAG, "��¼������������ͷ��ַ��" + extras.getString("cameraip"));
				String res = DecodeManager.getinstance().getOneImg(extras.getString("cameraip"));
				Log.e(TAG, "��¼����һ������صĽ����:   " + res);
			} else if (intent_key.equals("addcar")) {
				// ���ƺ����ɶ��� ��¼����ֱ��������
				Log.e("--","���ƺ����ɶ��� ��¼����ֱ��������");
			} else if (intent_key.equals("updatecar")) {
				// ���ƺ��޸Ķ���
				String token = extras.getString("token");
				String comid = extras.getString("comid");
				String orderid = extras.getString("orderid");
				String carNumber = extras.getString("carnumber");
				try {
					alterCarNumber(token, comid, orderid, carNumber);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (intent_key.equals("setConfidenceLevel")) {
				confidenceLevel = extras.getInt("confidenceLevel");
				Log.e(TAG, "confidenceLevel = " + confidenceLevel);
				DecodeManager.getinstance().setConfidenceLevel(confidenceLevel);
			} else if (intent_key.equals("openPole")) {
				controlHomePole(extras.getString("poleip"));
			} else if (intent_key.equals("closeService")) {
				this.onDestroy();
			} else if (intent_key.equals("updateuid")) {
				uid = extras.getString("uid");
			} else if (intent_key.equals("exitcamerastate")) {
				isCameraOk = extras.getBoolean("isCameraOk");
			} else if (intent_key.equals("updatetime")) {
				Log.e("-----", "updatetimeupdatetime");
				time = System.currentTimeMillis() + 30000;
			}
		}
	}

	private void initGetInfo(Bundle extras) {
		uid = extras.getString("uid");
		comid = extras.getString("comid");
		AppInfo.getInstance().setUid(uid);
		AppInfo.getInstance().setComid(comid);
//		isEnableMutiBill = extras.getBoolean("isEnableMutiBill");
		if (sqliteManager == null) {
//			sqliteManager = new SqliteManager(HomeExitPageService.this);
//			sqliteManager = zldNewActivity.sqliteManager;
//			sqliteManager = ((application)getApplication()).getSqliteManager();
			sqliteManager = ((application) getApplication()).getSqliteManager(HomeExitPageService.this);
		}
		// initSqliteManager();
		selectCameraIn = sqliteManager.selectCamera(SqliteManager.PASSTYPE_IN);
		// ��ȡ���õĵ�ǰ��������
		netType = SharedPreferencesUtils.getParam(HomeExitPageService.this, "nettype", "netType", Constant.sZero);
		// ��������Ŷ�
		confidenceLevel = SharedPreferencesUtils.getParam(context, "cameraParam", "confidenceLevel", 80);
		boolean isLocalServer = SharedPreferencesUtils.getParam(getApplicationContext(), "nettype", "isLocalServer",
				false);
		if (isLocalServer) {
			String linelocal = SharedPreferencesUtils.getParam(getApplicationContext(), "nettype", "linelocal",
					"local");
			Log.e("linelocal", "HomeExitPageService��linelocal:" + linelocal);
			if (linelocal.equals("local")) {
				String ip = SharedPreferencesUtils.getParam(getApplicationContext(), "nettype", "localip", null);
				if (ip != null) {
					/* ����mserver��Ϊ���Ϻͱ��ط����� */
					Constant.requestUrl(ip);
					Constant.serverUrl(ip);
				} else {
					// Constant.requestUrl��ֵĬ��Ϊ����
					Log.e("linelocal", "Constant.requestUrl��ֵĬ��Ϊ����");
				}
			}
		}
		// �����������ͷ
		setCameraConn();
		satrtTiming();
		initIssUpLocal();
	}

	/**
	 * �����������ͷ
	 */
	private void setCameraConn() {
		/** ��ȡͨ������Ϊ0��ڵ���������ͷ��Ϣ */
		selectCamera = sqliteManager.selectCamera(SqliteManager.PASSTYPE_IN);
		Log.e(TAG, "��ȡ������ͷ��" + selectCamera);
		if (i < selectCamera.size()) {
			handler.postDelayed(runnable, 50);
		}
	}

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (i < selectCamera.size()) {
				Log.e(TAG, "i��ֵ��" + i + selectCamera.get(i).getIp());
				CameraManager.openCamera(handler, selectCamera.get(i).getIp());
			} else {
				handler.removeCallbacks(runnable);
			}
			handler.postDelayed(this, 10000);
			i++;
			if (i == selectCamera.size()) {
				handler.removeCallbacks(runnable);
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopSelf();
		DecodeManager.getinstance().stopYitiji();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * 
	 * ����˵�������������ɶ��� ����: 2015��3��13�� ������:
	 *
	 * @param carNumber
	 *            ���ƺ���
	 * @param iType
	 *            0Ϊɨ��;1Ϊ��¼
	 * @throws UnsupportedEncodingException
	 */
	public void madeOrder(final String carNumber, int iType) throws UnsupportedEncodingException {
		String token = AppInfo.getAppInfo().getToken();
		if (token != null && token.equals("false")) {
			return; // �����ύû��token������������������ֹ������û�����������
		}
		final String carnumber = URLEncoder.encode(carNumber, "utf-8");
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.MADE_ORDER);
		params.setUrlParams("comid", comid);
		params.setUrlParams("uid", uid);
		params.setUrlParams("carnumber", URLEncoder.encode(carnumber, "utf-8"));
		params.setUrlParams("through", 3);
		params.setUrlParams("from", iType); // 0:ͨ��ɨ���Զ����ɶ�����1����¼�������ɶ���
		// ֮ǰΪ������ɫ�����ڴ���������
		params.setUrlParams("car_type", -1);
		params.setUrlParams("passid", passid);
		
		String url = params.getRequstUrl();
		Log.e(TAG, "���ɶ���url---------------->>" + url);
		FileUtil.writeSDFile("�볡̧������", "�������ɶ���������"+url);
		HttpManager.requestGET(this, url, carNumber, this);
	}

//	/**
//	 * �ϴ�����ͷ״̬
//	 *
//	 * @param cameraid
//	 *            ����ͷid
//	 * @param camerastate
//	 *            ����ͷ״̬
//	 */
//	public void uploadCameraState(MyCameraInfo cameraid, String camerastate) {
////		RequestParams params = new RequestParams();
////		params.setUrlHeader(Constant.requestUrl + Constant.UPLOAD_CAMERA_STATE);
////		Log.e(TAG, "�ϴ�����ͷid" + cameraid.getId());
////		params.setUrlParams("cameraid", cameraid.getCameraid());
////		params.setUrlParams("state", camerastate);
////		String url = params.getRequstUrl();
////		Log.e(TAG, "�ϴ�����ͷ״̬url---------------->>" + url);
////		HttpManager.requestGET(this, url, this);
//	}
//
//	/**
//	 * �ϴ���բ״̬
//	 *
//	 * @param cameraid
//	 *            ����ͷid
//	 * @param brakestate
//	 *            ����ͷ״̬
//	 */
//	public void uploadBrakeState(MyCameraInfo cameraid, String brakestate) {
////		RequestParams params = new RequestParams();
////		params.setUrlHeader(Constant.requestUrl + Constant.UPLOAD_BRAKE_STATE);
////		params.setUrlParams("passid", cameraid.getPassid());
////		params.setUrlParams("cameraid", cameraid.getCameraid());
////		params.setUrlParams("state", brakestate);
////		String url = params.getRequstUrl();
////		Log.e(TAG, "�ϴ���բ״̬url---------------->>" + url);
////		HttpManager.requestGET(this, url, this);
//	}

	private StringBuffer buildStr(final String carNumber, int parseInt, int num) {
		StringBuffer sb = new StringBuffer();
		sb.append(carNumber);
		sb.append("��");
		sb.append(num);
		sb.append("���ӵ�,�����ĳ����ӵ�");
		sb.append(parseInt);
		sb.append("��!");
		return sb;
	}

	/**
	 * 
	 * ����˵�������ɶ�����Ĳ��� ע����¼�������Զ�̧�� ����: 2015��3��13��
	 *
	 */
	private void buildOrderAfter(final String carnumber, CarNumberMadeOrder info) {
		FileUtil.writeSDFile("�볡̧������", "buildOrderAfter="+carnumber);
		String orderid = info.getOrderid();
		getEntranceFragment();
		// �����ǰ״̬Ϊ�Զ�����״̬,���Զ�̧��,Ϊ��¼״̬,��̧��
		// stateOpenPole(info);
		// �����״̬һֱ�ǽ���ȥ�ģ�����Ϊ��ʵ�ַ��غ�̧�˹��ܣ���ʱ���������̧�ˣ��������޸�
		FileUtil.writeSDFile("�볡̧������", "isPoleAutoWorking()="+isPoleAutoWorking()+"  cameraIp="+cameraIp);
		if (isPoleAutoWorking()) {
			controlHomePole(cameraIp);
		}
		// ����ͼƬ
		saveImage(carnumber, orderid);
		// �������ݿ�
		// save(carnumber,orderid);
		// �޸��볡����״̬
		setEntranceOrderState();
		// ˢ�½���
		sendKey(ZldNewActivity.REFRESH, null, null, null);
	}
	/**
	 * ��ȡEntranceFragment
	 */

	private void getEntranceFragment() {
		if (((application) getApplicationContext()).getZldNewActivity() != null) {
			entranceFragment = ((application) getApplicationContext()).getZldNewActivity().entranceFragment;
			Log.e(TAG, "��ȡ��entranceFragment:" + entranceFragment);
		}
	}

	/**
	 * �޸��볡����״̬
	 */
	private void setEntranceOrderState() {
		if (entranceFragment != null && entranceFragment.entranceOrderState != null) {
			entranceFragment.entranceOrderState.setState(EntranceOrderState.AUTO_COME_IN_STATE);
		}
	}

	/**
	 * 
	 * ����˵��������ͼƬ��ˢ�½��� ����: 2015��3��13��
	 *
	 */
	public void saveImage(final String carnumber, String orderid) {
		// ���ر���ͼƬ��ͼƬ��Ϣ���ϴ�ͼƬ
		if (resultBitmap != null) {
			saveAndUpload(carnumber, orderid);
		}
	}

	/**
	 * 
	 * ����˵����������ϴ�ͼƬ ����: 2015��3��13��
	 *
	 */
	private void saveAndUpload(final String carnumber, String orderid) {
		// ���ر���ͼƬ��ͼƬ��Ϣ---ԭͼ
		ImageUitls.SaveImageInfo(sqliteManager, resultBitmap, uid, carnumber, orderid, xCoordinate + "",
				yCoordinate + "", Constant.HOME_PHOTOTYPE + "", carPlatewidth + "", carPlateheight + "");
		Log.e(TAG, "��ȡ�������netType:" + netType);
		// netType �ϴ�ͼƬ�������ͣ�0����ʾ����---��Ҫѹ��ͼƬ; 1����ʾ���---����Ҫѹ��
		InputStream bitmapToInputStream = ImageUitls.getBitmapInputStream(netType, resultBitmap);
		upload(bitmapToInputStream, orderid, xCoordinate + "", yCoordinate + "", carPlatewidth + "",
				carPlateheight + "", carnumber);
	}

	/**
	 * 
	 * ����˵�����ϴ�ͼƬ ����: 2015��3��13��
	 * 
	 * @param bitmapToInputStream ͼƬ��
	 * @param orderid ������
	 * @param lefttop 1
	 * @param rightbottom 2
	 * @param width 3
	 * @param height 4
	 * @param carNumber 6
	 */
	private void upload(InputStream bitmapToInputStream, String orderid, String lefttop, String rightbottom,
			String width, String height, String carNumber) {
		// TODO Auto-generated method stub
		UpLoadImage upLoadImage = new UpLoadImage();
		upLoadImage.setComid(comid);
		upLoadImage.setmHandler(handler);
		upLoadImage.setPhotoType(Constant.HOME_PHOTOTYPE);
		upLoadImage.upload(bitmapToInputStream, orderid, lefttop, rightbottom, width, height, carNumber);

	}

	/**
	 * 
	 * ����˵������ʾ��Ϣ ����: 2015��3��13��
	 *
	 */
	public void showToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	/**
	 * ���������ɶ���; ǿ��ֱ�����ɶ���
	 * http://192.168.199.239/zld/cobp.do?action=addorder&comid=10&uid=1000028&
	 * carnumber=aaabebdd
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void addOrder(final String carNumber) throws UnsupportedEncodingException {
		String carnumber = URLEncoder.encode(carNumber, "utf-8");
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.ADD_CAR);
		params.setUrlParams("comid", comid);
		params.setUrlParams("uid", uid);
		params.setUrlParams("carnumber", URLEncoder.encode(carnumber, "utf-8"));
		params.setUrlParams("out", "json");
		params.setUrlParams("through", 3);
		String url = params.getRequstUrl();
		Log.e(TAG, "ǿ�����ɶ���url---------------->>" + url);
		FileUtil.writeSDFile("�볡̧������", "ǿ������"+url);
//		HttpManager.requestGET(this, url, this);111
		HttpManager.requestGET(this, url, carNumber, this);
	}

	/**
	 * �޸ĳ��ƺţ�
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void alterCarNumber(final String token, String comid, final String orderid, String carNumber)
			throws UnsupportedEncodingException {
		Log.e("--","token="+token);
		if (comid != null && orderid != null) {
			String carnumber = URLEncoder.encode(carNumber, "utf-8");
			RequestParams params = new RequestParams();
			params.setUrlHeader(Constant.requestUrl + Constant.MODIFY_ORDER);
			params.setUrlParams("comid", comid);
			params.setUrlParams("orderid", orderid);
			params.setUrlParams("carnumber", URLEncoder.encode(carnumber, "utf-8"));
			params.setUrlParams("through", 3);
			String url = params.getRequstUrl();
			Log.e(TAG, "�޸ĳ��ƺ�url---------------->>" + url);
			HttpManager.requestGET(this, url, this);
		}
	}

	/**
	 * ��ѽӿ�
	 */
	public void freeOrder(String token, String orderid) {
		Log.e("--","token="+token);
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.FREE_ORDER);
		params.setUrlParams("token", AppInfo.getInstance().getToken());
		params.setUrlParams("orderid", orderid);
		params.setUrlParams("passid", passid);
		String url = params.getRequstUrl();
		Log.e(TAG, "���url---------------->>" + url);
		HttpManager.requestGET(this, url, this);
	}

	/**
	 * 
	 * ����˵����ˢ�½��� ����: 2015��3��14��
	 */
	public void sendKey(int receiver_key, String orderid, String ledContent, String collect) {
		if (intent == null) {
			intent = new Intent("android.intent.action.exit");
		}
		if (mBundle == null) {
			mBundle = new Bundle();
		}
		mBundle.putInt("receiver_key", receiver_key);
		if (ledContent != null) {
			mBundle.putString("led_content", ledContent);
		}

		if (collect != null) {
			mBundle.putString("led_collect", collect);
		}

		if (orderid != null) {
			mBundle.putString("orderid", orderid);
		}
		intent.putExtras(mBundle);
		sendBroadcast(intent);// Activity����ʾͼƬ
	}

	/**
	 * 
	 * ����˵�����Ƿ��Զ�̧�� ����: 2015��3��13��
	 *
	 */
	private boolean isPoleAutoWorking() {
		boolean param = SharedPreferencesUtils.getParam(getApplicationContext(), "cameraParam", "auto", true);
		System.out.println("���õ��Ƿ��Զ�̧�ˣ�" + param);
		return param;
	}

	/**
	 * ���̧��
	 */
	public void controlHomePole(final String ip) {
		Log.e(TAG, "̧����");
		DecodeManager.getinstance().controlPole(DecodeManager.openPole, ip);
		new Thread(new Runnable() {
			// ������̧��żȻʧЧ�����⣬�ȼӸ��ط����ƣ�������ȷ�Ϸ��ͳɹ������������
			public void run() {
				try {
					Thread.sleep(2000);
					DecodeManager.getinstance().controlPole(DecodeManager.openPole, ip);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		/**
		 * �����´��� Log.e("taigan","̧�˽��:"+result); new Thread(new Runnable() {
		 * 
		 * @Override public void run() {
		 *           uploadBrakeState(selectCameraIn.get(0),result+""); }
		 *           }).start();
		 */
	}

	@Override
	public boolean doSucess(String url, String object, String token, String orderid) {
		// TODO Auto-generated method stub
		if (url.contains(Constant.FREE_ORDER)) {
			doFreeOrderResult(object);
		} else if (url.contains(Constant.MODIFY_ORDER)) {
			doModifyOrderResult(object, token, orderid);
		}
		return true;
	}

	/**
	 * ��Ѷ����Ľ��
	 *
	 */
	private void doFreeOrderResult(String object) {
		if (object.equals(Constant.sOne)) {
			showToast("��ѳɹ�");
		}
	}

	/**
	 * �޸ĳ��ƺ�
	 *
	 */
	private void doModifyOrderResult(String object, String token, String orderid) {
		// TODO Auto-generated method stub
		// �ɹ�-ˢ�½�����ʾ�޸Ĺ��ĵ�ǰ����
		if (object.equals(Constant.sOne)) {
			showToast("�޸ĳ��Ƴɹ�");
			// ˢ�½���
			// sendKey(HomeExitPageActivity.REFRESH,null,null);
		} else if (object.equals(Constant.sZero)) {
			// ��ǰ�������Ѿ����ڴ����޸ĵĳ��ƺţ��򽫵�ǰ�Ķ�����ѵ�
			freeOrder(token, orderid);
		} else {
			showToast("�޸ĳ���ʧ��");
		}
	}

	@Override
	public void timeout(String url) {
		// TODO Auto-generated method stub
		if (url.contains(Constant.ADD_CAR) || url.contains(Constant.MADE_ORDER)) {
			FileUtil.writeSDFile("�볡̧������", madeOrderFailureCount + "   " + TimeTypeUtil.getNowTime() + "�������ɳ�ʱ  url:" + url);
			if (madeOrderFailureCount < 3) {
				madeOrderFailureCount++;
				showToast("��������ʧ��,����������ӣ�");
				HttpManager.requestGET(this, url, this);
			} else {
				showToast("��������ʧ��,���˹�ȷ�ϣ�");
			}
		}
		super.timeout(url);
	}

	@Override
	public boolean doFailure(String url, String status) {
		// TODO Auto-generated method stub
		if (url.contains(Constant.ADD_CAR) || url.contains(Constant.MADE_ORDER)) {
			FileUtil.writeSDFile("�볡̧������", madeOrderFailureCount + "  " + TimeTypeUtil.getNowTime() + "��������ʧ��+status:"
					+ status + "  url:" + url);
			if (madeOrderFailureCount < 3) {
				madeOrderFailureCount++;
				showToast("��������ʧ��,����������ӣ�");
				HttpManager.requestGET(this, url, this);
			} else {
				showToast("��������ʧ��,���˹�ȷ�ϣ�");
			}
		}
		return super.doFailure(url, status);
	}

	int madeOrderFailureCount = -1;

	@Override
	public boolean doSucess(String url, String object, String carnumber) {
		// TODO Auto-generated method stub
		Log.e(TAG, "doSucess---------------->>" + url);
		FileUtil.writeSDFile("�볡̧������", "���سɹ�"+carnumber);
		if (url.contains(Constant.MADE_ORDER)) {
//			j = 0;
			try {
				doMadeOrderResult(object, carnumber);
				madeOrderFailureCount = 0;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (url.contains(Constant.ADD_CAR)) {
			doAddCarResult(object, carnumber);
		}
		return true;
	}
	/**
	 * ǿ�����ɶ����Ľ��
	 *
	 */
	private void doAddCarResult(String object, String carNumber) {
		// TODO Auto-generated method stub
		EscapedOrder info = new Gson().fromJson(object, EscapedOrder.class);
		FileUtil.writeSDFile("�볡̧������", "ǿ�����ɶ���"+info.toString());
		if (info.getInfo().equals(Constant.sOne)) {
			showToast("��������,���ڵ�ǰ�����鿴��");
//			// �򿪵�բ
//			 DecodeManager.getinstance().controlPole(DecodeManager.openPole);
//			// ����ͼƬ
//			saveImage(carNumber, info.getOrderid());
//			// ˢ�½���
//			sendKey(ZldNewActivity.REFRESH, null, null, null);
			
			showToast("��������,���ڵ�ǰ������鿴��");
			sendKey(ZldNewActivity.SHOW, null, carPlate + "��ӭ����", null);
			Log.e(TAG, "�������ɵ�Orderid��" + info.getOrderid());
			CarNumberMadeOrder infos = new CarNumberMadeOrder();
			infos.setOrderid(info.getOrderid());
			buildOrderAfter(carNumber, infos);
		} else {
			showToast("��������ʧ��" + object);
		}
	}

	/**
	 * ���ɶ����Ľ����Ϣ
	 *
	 * @throws UnsupportedEncodingException
	 */
	private void doMadeOrderResult(String object, String carNumber) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		Log.e(TAG, "����ʶ�����ɶ�����carNumber--->" + carNumber);
		Log.e(TAG, "����ʶ�����ɶ����Ľ��--->" + object);
		CarNumberMadeOrder info = new Gson().fromJson(object, CarNumberMadeOrder.class);
		Log.e(TAG, "����ʶ�����ɶ���--->" + info.toString());
		FileUtil.writeSDFile("�볡̧������", "����ʶ�����ɶ���--->" + info.toString());
		if (info.getInfo().equals(Constant.sOne)) {
			showToast("��������,���ڵ�ǰ������鿴��");
			sendKey(ZldNewActivity.SHOW, null, carPlate + "��ӭ����", null);
			Log.e(TAG, "�������ɵ�Orderid��" + info.getOrderid());
			buildOrderAfter(carNumber, info);
		} else if (info.getInfo().equals(Constant.sZero)) {// �ӵ�
			if (info.getOther() != null && info.getOwn() != null) {
				int parseInt = Integer.parseInt(info.getOwn());
				int parseInt2 = Integer.parseInt(info.getOther());
				int num = parseInt + parseInt2;
				StringBuffer sb = buildStr(carNumber, parseInt, num);
				// warning �ӵ����ٴ�
				mBundle.putString("warning", sb.toString());
				mBundle.putString("carNumber", carNumber);
				mBundle.putString("comid", comid);
				mBundle.putString("uid", uid);
				// sendKey(HomeExitPageActivity.SHOW_DIALOG,null,null);
				//[info=0,orderid=null,own=0,other=1,ismonthuser=0,proorderid=null]
				//�Ա���������������Ҫǿ�����ɶ���
				//[info=1,orderid=24707902,own=0,other=0,ismonthuser=0,proorderid=null]
				FileUtil.writeSDFile("�볡̧������", "���ӵ�Ҫǿ������");
				addOrder(carNumber);
			} else {
				showToast("��ѯ�ӵ���Ϣ����������");
			}
		} else if (info.getInfo().equals("-1")) {
			showToast("������Ŵ���");
		} else if (info.getInfo().equals("-2")) {
			showToast(carNumber + "����δ���㶩��,���Ƚ��㣡");
		} else if (info.getInfo().equals("-4")) {
			VoicePlayer.getInstance(this).playVoice("�¿��ڶ�������ֹ����");
			showToast(carNumber + "�¿��ڶ�������ֹ����");
			sendKey(ZldNewActivity.SPEAK, null, "�¿��ڶ�������ֹ����", "�¿�ռ��");
			sendKey(Constant.REFRESH_NOMONTHCAR2_IMAGE, null, null, null);
			Message m = new Message();
//			m.what = 1221;
//			m.obj = carNumber + "�¿��ڶ�������ֹ����";
//			handler.sendMessage(m);
			sendKey(1221,carNumber + "�¿��ڶ�������ֹ����",null,null);
//			LineLocalRestartDialog dialog = new LineLocalRestartDialog(context,com.zld.R.style.nfcnewdialog,null,carNumber + "�¿��ڶ�������ֹ����","ȡ��","̧��");
//			dialog.show();
		} else if (info.getInfo().equals("-3")) {
			VoicePlayer.getInstance(this).playVoice("���¿���ֹ����");
//			showToast("���¿���ֹ����");1
			sendKey(ZldNewActivity.SPEAK, null, "���¿���ֹ����", "���¿���");
			sendKey(Constant.REFRESH_NOMONTHCAR_IMAGE, null, null, null);
//			LineLocalRestartDialog dialog = new LineLocalRestartDialog(context,com.zld.R.style.nfcnewdialog,null,"���¿���ֹ����","ȡ��","̧��");
//			dialog.show();
//			Message m = new Message();
//			m.what = 1221;
//			m.obj = "���¿���ֹ����";
//			handler.sendMessage(m);
			sendKey(1221, "���¿���ֹ����",null,null);
		} else if (info.getInfo().equals("-5")) {
			VoicePlayer.getInstance(this).playVoice("�¿����ڽ�ֹ����");
//			showToast("�¿����ڽ�ֹ����");
			sendKey(ZldNewActivity.SPEAK, null, "�¿����ڽ�ֹ����", "�¿�����");
			sendKey(Constant.HOME_CAR_OUTDATE_ICON, null, null, null);
//			LineLocalRestartDialog dialog = new LineLocalRestartDialog(context,com.zld.R.style.nfcnewdialog,null, "�¿����ڽ�ֹ����","ȡ��","̧��");
//			dialog.show();
//			Message m = new Message();
//			m.what = 1221;
//			m.obj = "�¿����ڽ�ֹ����";
//			handler.sendMessage(m);
			sendKey(1221, "�¿����ڽ�ֹ����",null,null);
		}
	}
	
	private String getPassid(String cameraIp) {
		if (selectCameraIn.size() > 0) {
			for (int i = 0; i < selectCameraIn.size(); i++) {
				MyCameraInfo myCameraInfo = selectCameraIn.get(i);
				Log.e(TAG, "����ͷ�ص�cameraIp:" + cameraIp + "		���ݿⱣ��ip��" + myCameraInfo.getIp());
				if (cameraIp.equals(myCameraInfo.getIp())) {
					passid = myCameraInfo.getPassid();
					Log.e(TAG, "����ͷ��Ӧ��passid:" + passid);
					return passid;
				}
			}
		}
		return null;
	}
	/**
	 * ִ�ж�ʱ����
	 */
	private void satrtTiming() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.e("-----", "runnnnnnnnnnnnnn");
				long currentTimeMillis = System.currentTimeMillis();
				if (time == 0 || (currentTimeMillis - time) > 30000) {// ��ǰʱ��������ظ���ʱ��time
																		// ����30��
					Message message = new Message();
					message.what = Constant.KEEPALIVE_TIME;
					handler.sendMessage(message);
				}
			}
		};
		timer.schedule(task, 20000, 10000); // 20���ִ�У�20��һ��
	}

	private void initIssUpLocal() {
		// TODO Auto-generated method stub
		if (issuplocal == null) {
			issuplocal = SharedPreferencesUtils.getParam(this.getApplicationContext(), "zld_config", "issuplocal", "");
			Log.e("isLocal", "BaseActivity initIssUpLocal get issuplocal " + issuplocal);
		}
	}
}
