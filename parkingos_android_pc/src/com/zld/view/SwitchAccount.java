package com.zld.view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.zld.R;
import com.zld.application;
import com.zld.adapter.AccountDropListAdapter;
import com.zld.bean.AppInfo;
import com.zld.bean.LoginInfo;
import com.zld.bean.ParkingInfo;
import com.zld.bean.SmAccount;
import com.zld.db.SqliteManager;
import com.zld.engine.LoginInfoParser;
import com.zld.lib.constant.Constant;
import com.zld.lib.dialog.DialogManager;
import com.zld.lib.http.HttpCallBack;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.util.AppInfoUtil;
import com.zld.lib.util.InputUtil;
import com.zld.lib.util.MD5Utils;
import com.zld.lib.util.SharedPreferencesUtils;
import com.zld.lib.util.ShowDialog;
import com.zld.lib.util.StringDesUtils;
import com.zld.ui.ZldNewActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

@SuppressLint("NewApi")
public class SwitchAccount implements HttpCallBack{
	public static final int ENTRANCE = 0;
	public static final int EXIT = 1;
	public static final int HOMEEXIT = 2;
	private View parent;
	private Toast mToast;
	private View loginView;
	private Context activity;
	private Activity mainactivity;
	private Button bt_login;
	private ProgressDialog dialog;
	@SuppressWarnings("unused")
	private ArrayList<String> accounts;
	final static String regularEx = "|";
	private LinearLayout rv_login_activity;
	public EditText et_login_password;
	public AutoCompleteTextView at_login_username;
	private Set<String> users = new HashSet<String>();

	@SuppressWarnings("unused")
	private int stationType;
	private ListView listview;
	private PopupWindow popupWindow;
	private PopupDialog popupDialog;
	public static String token = null;
	public static String comid = null; 
	private SqliteManager sqliteManager;
	private static final String TAG = "SwitchAccount";

	public SwitchAccount() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SwitchAccount(ZldNewActivity activity, View parent, int stationType) {
		this.parent = parent;
		this.activity = activity;
		mainactivity = activity;
		this.stationType = stationType;
		if (sqliteManager == null) {
			sqliteManager = ((application) activity.getApplicationContext()).getSqliteManager(activity);
		}
		if (dialog == null) {
			dialog = new ProgressDialog(activity);
			dialog.setMessage("��¼��...");
		}
	}

	@SuppressLint("NewApi")
	public void showSwitchAccountView() {
		showPopupWindow(parent);
	}

	public void showSwitchAccount() {
		initView();
		setView();
		popupDialog = new PopupDialog(activity);
		popupDialog.setContentView(loginView);
		popupDialog.showAsDropDown(parent);
		popupDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				// ���뷨�Ƿ񵯳�
				Log.e("SwitchAccount", "Dialog���أ�����");
				InputUtil.closeInputMethod(activity);
			}
		});
	}

	public void initView() {
		loginView = LayoutInflater.from(activity).inflate(
				R.layout.relogin_activity, null);
		rv_login_activity = (LinearLayout) loginView.
				findViewById(R.id.rv_login_activity);
		at_login_username = (AutoCompleteTextView) loginView
				.findViewById(R.id.at_login_account);
		et_login_password = (EditText) loginView
				.findViewById(R.id.et_login_password);
		bt_login = (Button) loginView.findViewById(R.id.bt_longin_login);
	}

	public void setView() {
		rv_login_activity.setBackground(activity.getResources().getDrawable(R.drawable.small_login_bg));
		bt_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String username = at_login_username.getText().toString().trim();
				String password = et_login_password.getText().toString().trim();
				if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
					showToast("�˺Ż����벻��Ϊ��");
				} else {
					try {
						String md5password = MD5Utils.MD5(MD5Utils
								.MD5(password) + "zldtingchebao201410092009");
						longinSuccess(username, md5password, password);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.e(TAG, "MD5�����쳣��");
						e.printStackTrace();
					}

				}
			}

		});	
		//�ָ��༭������
		at_login_username.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				at_login_username.setFocusableInTouchMode(true);
				et_login_password.setFocusableInTouchMode(true);
				return false;
			}
		});
	}

	/**
	 * �л��˺�����ĵ�¼����worksite_id�ֶΣ�Ϊ�˽��Ӱ�
	 * ���û�������˺ź������ύ������������֤�˻��������Ƿ���ȷ��
	 */
	public void longinSuccess(final String username,
			final String MD5password,final String password) {
		if(popupWindow != null){
			popupWindow.dismiss();
		}
		if(popupDialog != null){
			popupDialog.dismiss();
		}

		// Ϊ���ػ�ʱ��׼һ�㣬����������ʱ�䣻
//		setInternetTime();
		/*// ���� ���ػ�ģʽ
		if (SharedPreferencesUtils.getParam(
		activity.getApplicationContext(),"nettype", "isLocal", false)) {
			localLoginTimeOut(username, password);
			return;
		}*/

		String worksiteId = SharedPreferencesUtils.getParam(
				activity.getApplicationContext(),"set_workStation", "workstation_id", "");
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.LOGIN);
		params.setUrlParams("username", username);
		params.setUrlParams("password", MD5password);
		params.setUrlParams("version", AppInfoUtil.getVersionName(activity));
		params.setUrlParams("worksite_id", worksiteId);
		String url = params.getRequstUrl();
		Log.e(TAG, "��¼��URL---------------->>" + url);
		dialog.show();
		HttpManager.requestLoginGET(activity, url,this,username,password);	
	}

	/**
	 * ���Ϊ�Զ���ʱ��,������Ϊ����ʱ��
	 */
//	private void setInternetTime() {
//		ContentResolver cv = activity.getContentResolver();
//		String isAutoTime = android.provider.Settings.System.getString(cv, Global.AUTO_TIME);
//		if ("0".equals(isAutoTime)) {//1=yes, 0=no
//			ShowDialog.showSetTimeDialog(activity);
//			return;
//		}
//	}

	/**
	 * ��ȡ������Ϣ ��ȡcomid��Ϣ
	 * 
	 * @param username
	 * @param password
	 * @param users
	 * @param info
	 */
	public void getParkingInfo(final String username, final String password,
			final Set<String> users, final LoginInfo info) {
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.COMINFO);
		params.setUrlParams("token", AppInfo.getInstance().getToken());
		params.setUrlParams("out", "json");
		String url = params.getRequstUrl();
		Log.e(TAG, "��ȡ������Ϣ��URL---------------->>" + url);
		HttpManager.requestCominfoGET(activity, url,this,username,password,info);	
	}

	private void loginChoose(final String username, final String password,
			final Set<String> users, LoginInfo info) {
		// �Զ���¼��
		SharedPreferences userinfo = activity.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		SharedPreferences autologin = activity.getSharedPreferences(
				"autologin", Context.MODE_PRIVATE);
		Editor autoEdit = autologin.edit();
		Editor userInfoedit = userinfo.edit();
		autoEdit.putString("account", username);
		autoEdit.putString("passwd", password);
		try {
			String encode = new StringDesUtils().encrypt(password);
			userInfoedit.putString(username, encode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		userInfoedit.commit();
		autoEdit.commit();
		// �Զ��˺���ʾ��¼
		if (!users.contains(username)) {
			Log.e("Ҫ�����set��������Ϊ---", users.toString());
			SharedPreferences sp1 = activity.getSharedPreferences("usernames",Context.MODE_PRIVATE);
			Editor edit = sp1.edit();
			users.add(username);
			SharedPreferencesUtils.putStringSet(edit, "usernames", users).commit();
		}
		if (popupDialog != null){
			popupDialog.dismiss();
		}
		((ZldNewActivity)activity).setUserName();
	}

	public void showToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(activity.getApplicationContext(), text,
					Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	/**
	 * չʾ�л��˺ŵ�¼��
	 * @param parent
	 */
	@SuppressWarnings({ "deprecation", "unused" })
	private void showPopupWindow(View parent) {
		int screenHeight = 0; 
		// TODO Auto-generated method stub
		if(listview == null){
			listview = (ListView) LayoutInflater.from(activity).inflate(R.layout.account_droplist, null); 
		}
		final ArrayList<String> selectAllAccount = sqliteManager.selectAllAccount();
		//Ŀ������ʾ�û����б�ײ�������˺�
		selectAllAccount.add("����˺�");
		selectAllAccount.add("�°�");
		if(selectAllAccount.size() != 0){
			AccountDropListAdapter adapter = new AccountDropListAdapter(activity, selectAllAccount,false);
			listview.setAdapter(adapter);
		}
		if (popupWindow == null) {  
			popupWindow = new PopupWindow(listview,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			DisplayMetrics dm = new DisplayMetrics();
			//��ȡ��Ļ��Ϣ
			((Activity) activity).getWindowManager().getDefaultDisplay().getMetrics(dm);
			int screenWidth = dm.widthPixels;
			screenHeight = dm.heightPixels;
			popupWindow.setWidth((int)(screenWidth/5));
		}  
		popupWindow.setFocusable(true);  
		popupWindow.setOutsideTouchable(true);  
		// �����Ϊ�˵��������Back��Ҳ��ʹ����ʧ�����Ҳ�����Ӱ����ı���  
		popupWindow.setBackgroundDrawable(new BitmapDrawable());  
		final int[] location = new int[2];  
		parent.getLocationOnScreen(location);  
		popupWindow.showAsDropDown(parent);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String account = selectAllAccount.get(position);
				Log.e(TAG,"�л��˺ŵ�����ǣ�"+account);
				if(account == null||account.equals(AppInfo.getInstance().getName())){
					return;
				}

				SmAccount selectAccount = sqliteManager.selectAccount(account);
				if(selectAccount != null){
					String username = selectAccount.getUsername();
					String password = selectAccount.getPassword();
					Log.e(TAG, "���ݿ��б�����û���Ϣ��"+username+"---123456:"+password);
					if (username != null) {
						String md5password = null;
						try {
							md5password = MD5Utils.MD5(MD5Utils.MD5(password)
									+ "zldtingchebao201410092009");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						longinSuccess(username, md5password, password);
					}
				}else{
					if("����˺�".equals(account)){
						popupWindow.dismiss();
						showSwitchAccount();
					}else if("�°�".equals(account)){
						afterWorkDialog("ȷ���°���","�°�");
					}
				}
			}
		});
	}

	/**ѡ���°�Ի���*/
	private void afterWorkDialog(String msg,String title) {
		Builder buildDialog = ShowDialog.buildDialog(activity,msg,title);
		buildDialog.setPositiveButton("ȷ��", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				afterWork();
			}
		});
		buildDialog.setNegativeButton("ȡ��", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		buildDialog.show();
	}

	/**֮ǰ�Ĵ�û�й���¼�˺�,���¼���˺Ÿ�֮ǰ�Ĳ�һ��,�򱣴��¼ʱ��,���ڲ�ѯ���Ӱ�����Ϣ*/
	private void saveLongOnTime(final String username, LoginInfo info) {
		String beforeUser = SharedPreferencesUtils.getParam(activity.getApplicationContext(), "autologin", "account", "");
		Log.e(TAG,"beforeUser:"+beforeUser+"	username:"+username);
		if(TextUtils.isEmpty(beforeUser)||!beforeUser.equals(username)){
			Log.e(TAG,"Ϊnull��ͬ�򱣴�"+info.getLogontime());
			if(activity!=null){
				SharedPreferencesUtils.setParam(activity.getApplicationContext(), "autologin", "logontime", info.getLogontime());
				SharedPreferencesUtils.setParam(activity.getApplicationContext(),"userinfo", "mobilepay", "0");
				SharedPreferencesUtils.setParam(activity.getApplicationContext(),"userinfo", "cashpay", "0");
				Message msg = new Message();
				msg.obj = info.getLogontime();
				handle.sendMessage(msg);
			}
		}
	}
	Handler handle = new Handler(){
		public void handleMessage(android.os.Message msg) {
			String obj = (String) msg.obj;
			((ZldNewActivity) activity).setMoneyAndTime(obj);
		};
	};
	/**ֻ��һ���շ�Ա�����ϰ�������Ϣ���������Ҫ����°ఴť�����¼Ʒ�
	 * collectorlogin.do?action=gooffwork&worksiteid=3&uid=1197*/
	private void afterWork() {
		// TODO Auto-generated method stub
		String worksiteId = SharedPreferencesUtils.getParam(activity.getApplicationContext(),
				"set_workStation", "workstation_id", "");
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.AFTER_WORK);
		params.setUrlParams("token", AppInfo.getInstance().getToken());
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("uid", AppInfo.getInstance().getUid());
		params.setUrlParams("worksiteid", worksiteId);
		String url = params.getRequstUrl();
		Log.e(TAG, "�°��url��--->"+url);
		HttpManager.requestGET(activity, url, this);
	}

	@Override
	public boolean doSucess(String url, String object,String username,String password,LoginInfo info) {
		// TODO Auto-generated method stub
		DialogManager.getInstance().dissMissProgressDialog();
		if(url.contains(Constant.COMINFO)){
			doGetComInfo(object,username,password,info);
		}
		return true;
	}

	@Override
	public boolean doSucess(String url, byte[] buffer, String username, String password){
		dialog.dismiss();
		if(url.contains(Constant.LOGIN)){
			Log.e(TAG, "��ȡ��¼��ϢΪ��"+Constant.LOGIN+"---------------->>" + buffer);
			doLoginResult(buffer,username,password);
		}
		return true;
	}

	@Override
	public boolean doSucess(String url, String object) {
		// TODO Auto-generated method stub
		DialogManager.getInstance().dissMissProgressDialog();
		if (url.contains(Constant.AFTER_WORK)){
			doAfterWorkResult(object);
		}
		return true;
	}

	/**
	 * @param object
	 */
	private void doAfterWorkResult(String object) {
		// TODO Auto-generated method stub
		Log.e(TAG,"�°�����"+object);
		if("1".equals(object)){
			//������ؽ����ϰ�ʱ��
			SharedPreferencesUtils.setParam(activity.getApplicationContext(), "userinfo", "cashpay", "0");
			SharedPreferencesUtils.setParam(activity.getApplicationContext(), "userinfo", "mobilepay", "0");
			SharedPreferencesUtils.setParam(activity.getApplicationContext(), "autologin", "logontime", "0");
			SharedPreferences autologin = 
					activity.getSharedPreferences("autologin",Context.MODE_PRIVATE);
			autologin.edit().putString("passwd", "").commit();
			SharedPreferences firstSetSPF = activity.getSharedPreferences("set_workStation",
					Context.MODE_PRIVATE);
			firstSetSPF.edit().putBoolean("is_first", true).commit();
//			android.os.Process.killProcess(android.os.Process.myPid());
//			System.exit(0);
			mainactivity.finish();
//			ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
//			am.
//			mainactivity.getTaskId();
//			ActivityManager.
		}else if("-1".equals(object)){
			afterWorkDialog("ȷ���°���", "����ȷ���°�");
		}
	}

	private void doLoginResult(byte[] buffer,String username,String password) {
		// TODO Auto-generated method stub
		if (buffer != null) {
			try {
				Log.e(TAG, "��½�ķ�����Ϣ��---" + new String(buffer, "utf-8"));
				InputStream is = new ByteArrayInputStream(buffer);
				LoginInfo info = LoginInfoParser.getLoginInfo(is);
				if(null != info){
					if(null != info.getState()&&info.getState().equals("1")){
						showToast("���շ�Ա�Ѿ���ɾ��");
						SharedPreferencesUtils.delete(activity, "autologin", info.getName());
						sqliteManager.deleteAccountData(info.getName());
						return;
					}
					Log.e(TAG, "������¼��Ϣ��" + info.toString()+"==username=="+username);
					if (info.getInfo().equals("success")) {
						token = info.getToken();
						Log.e(TAG, "success��token��" + token);
						if (token != null) {
							AppInfo.getInstance().setToken(token);
							AppInfo.getInstance().setUid(username);
						}
						/*�����¼ʱ��*/
						saveLongOnTime(username, info);
						SmAccount selectAccount = sqliteManager.selectAccountByUsrName(username);
						if(selectAccount == null){
							sqliteManager.insertAccountData(info.getName(),username, password);
						}else {
							if (!selectAccount.getPassword().equals(password) || !selectAccount.getAccount().equals(info.getName())){
								sqliteManager.updateAccountData(info.getName(), username, password);
							}
						}
						SharedPreferencesUtils.setParam(activity.getApplicationContext(), "autologin", "account", username);
						SharedPreferencesUtils.setParam(activity.getApplicationContext(), "autologin", "passwd", password);
						SharedPreferencesUtils.setParam(activity.getApplicationContext(), "autologin", "name", info.getName());
						SharedPreferencesUtils.setParam(activity.getApplicationContext(), "autologin", "role", info.getRole());
						getParkingInfo(username, password, users, info);
					} else {
						popupWindow.dismiss();
						showSwitchAccount();
						showToast("��¼ʧ�ܣ�" + info.getInfo());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
	}

	private void doGetComInfo(String object,String username,String password,LoginInfo info) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		ParkingInfo parkingInfo = gson.fromJson(object,ParkingInfo.class);
		if (parkingInfo != null) {
			Log.e(TAG, "�����ĳ�����ϢΪ" + parkingInfo.toString());
			if (parkingInfo.getParkingtotal() != null) {
				if (parkingInfo.getParkingtotal() != null) {
					comid = parkingInfo.getId();
					AppInfo.getInstance().setComid(comid);
					if(info != null){
						AppInfo.getInstance().setName(info.getName());
					}
					if(parkingInfo != null){
						AppInfo.getInstance().setIshidehdbutton(
								parkingInfo.getIshidehdbutton());
					}
					loginChoose(username, password, users, info);
				}
			}
		}	
	}

	/* (non-Javadoc)
	 * @see com.zld.lib.http.HttpCallBack#doSucess(java.lang.String, boolean, java.lang.String, java.lang.String, int, java.lang.String)
	 */
	@Override
	public boolean doSucess(String url, boolean isSingle, String passid,
			String object, int i, String object2) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.zld.lib.http.HttpCallBack#doFailure(java.lang.String, com.androidquery.callback.AjaxStatus)
	 */
	@Override
	public boolean doFailure(String url, String status) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String url, String object, String worksiteId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String url, String object, byte[] buffer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String url, String object, String str1, String str2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String requestUrl, byte[] buffer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String requestUrl, String username2,
			String password2, LoginInfo info2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String url, String object, byte[] buffer,
			String username, String password) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void timeout(String url) {
		// TODO Auto-generated method stub

	}

	@Override
	public void timeout(String url, String str) {
		// TODO Auto-generated method stub

	}

	@Override
	public void timeout(String url, String str, String str2) {

		// TODO Auto-generated method stub
		dialog.dismiss();
		if(url.contains(Constant.LOGIN)){
			Builder buildDialog = ShowDialog.buildDialog(activity,"���Ժ��¼","�������");
			buildDialog.setPositiveButton("ȷ��", new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					afterWork();
				}
			});
			buildDialog.show();
			return;
			//���س�ʱ�л��˺�
			//localLoginTimeOut(str, str2);
		}
	}

	/**
	 * �������ػ���¼
	 * @param username
	 * @param password
	 */
	@SuppressWarnings("unused")
	private void localLoginTimeOut(final String username, final String password) {
		// ��ѯ���ݿ����Ƿ����˺� ������, �еĻ�����ת���޵Ļ� ��ʾ
		SmAccount selectAccount = sqliteManager.selectUsername(username);
		if(selectAccount != null){
			Log.e(TAG,"������˻���"+selectAccount.getAccount()+"=="+username+"=��ȡ��������룺"+selectAccount.getPassword()+"=��������룺"+password);
			if(selectAccount.getPassword()!=null&&password.equals(selectAccount.getPassword())){
				String comid = SharedPreferencesUtils.getParam(activity.getApplicationContext(), "zld_config", "comid", null);
				SharedPreferencesUtils.setParam(activity.getApplicationContext(), "userinfo", "name", selectAccount.getAccount());
				AppInfo.getInstance().setComid(comid);
				AppInfo.getInstance().setUid(username);
				AppInfo.getInstance().setName(selectAccount.getAccount());
				((ZldNewActivity)activity).setUserName();
				SharedPreferencesUtils.setParam(activity.getApplicationContext(), "userinfo", "cashpay", "0");
				SharedPreferencesUtils.setParam(activity.getApplicationContext(), "userinfo", "mobilepay", "0");
			}
		}else{
			showToast("��������ȷ���˺ź�����");
		}
	}

}
