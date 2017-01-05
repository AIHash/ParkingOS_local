package com.zld.ui;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zld.R;
import com.zld.application;
import com.zld.bean.AppInfo;
import com.zld.bean.EnterClose;
import com.zld.bean.MyCameraInfo;
import com.zld.bean.MyLedInfo;
import com.zld.bean.WifiAdmin;
import com.zld.bean.WorkStation;
import com.zld.bean.WorkStationDevice;
import com.zld.db.SqliteManager;
import com.zld.lib.constant.Constant;
import com.zld.lib.dialog.DialogManager;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.util.SharedPreferencesUtils;
//import com.zld.photo.DecodeManager;
import com.zld.service.HomeExitPageService;
import com.zld.view.WorkStationPicker;
import com.zld.view.WorkStationPicker.OnWorkStationChangedListener;
/**
 * ���ù���վ
 * @author HZC
 *
 */
public class ChooseWorkstationActivity extends BaseActivity{

	private static final String TAG = "ChooseWorkstationActivity";
	private TextView tv_close, tv_pole_worktype, tv_softkeyboard, tv_fuzy_search,tv_month_card;
	private CheckBox cb_pole_worktype, cb_softkeyboard, cb_fuzy_search,cb_month_card;
	private EditText et_wifi,et_confidence_level;
	private RelativeLayout rl_pole_worktype, rl_softkeyboard, rl_fuzy_search,rl_month_card;
	private WorkStationPicker np_work;
	private Button btn_into_background;
	private Button btn_yes;
	private int id;
	private String value;
	private String token;
	private String comid;
	/*�����������ͬ���͵�ͨ������passItemΪͨ�������еĵڼ���*/
	private int passItem = 0;
	private String intentStaname;
	private String preActivityName;
	private String workStation[] = null;
	private List<EnterClose> enterCloseList;
	private ArrayList<WorkStation> workStations;

	private WifiAdmin wifiAdmin;
	private boolean showMonthCard;
	private String configurateSsid;
	private SqliteManager sqliteManager;
	private List<ScanResult> scanResultList;
	private BroadcastReceiver wifiConnectReceiver;
	private List<WifiConfiguration> configuratedList;
	/*��ǰ���ӵ�wifi*/
	private WifiInfo currentWifiInfo;
	/*�ж��Ƿ��������ƶ���wifi*/
	private boolean isConnected = false;
	private int networkId;
	/*��ʾ�Ƿ��ڿ����ӷ�Χ��*/
	private boolean canConnectable = false;
	/*��ʾwifi�Ƿ��Ѿ����ù���*/
	private boolean isConfigurated = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // �����ޱ���
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // ����ȫ��
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_set_workstation);
		initView();
		onClickEvent();
		initSqliteManager();
		token = AppInfo.getInstance().getToken();
		comid = AppInfo.getInstance().getComid();	
		super.changeEquipment(et_confidence_level,false);
		initIntentStaname();
		initWifi();
	}

	private void initView() {
		// TODO Auto-generated method stub
		tv_close = (TextView) findViewById(R.id.tv_close);
		tv_pole_worktype = (TextView) findViewById(R.id.tv_pole_worktype);
		tv_softkeyboard = (TextView) findViewById(R.id.tv_softkeyboard);
		tv_month_card = (TextView) findViewById(R.id.tv_month_card);
		tv_fuzy_search = (TextView) findViewById(R.id.tv_fuzy_search);
		np_work = (WorkStationPicker) findViewById(R.id.np_work);
		btn_into_background = (Button) findViewById(R.id.btn_into_background);
		btn_yes = (Button) findViewById(R.id.btn_yes);
		rl_pole_worktype = (RelativeLayout) findViewById(R.id.rl_pole_worktype);
		rl_softkeyboard = (RelativeLayout) findViewById(R.id.rl_softkeyboard);
		rl_fuzy_search = (RelativeLayout) findViewById(R.id.rl_fuzy_search);
		rl_month_card = (RelativeLayout) findViewById(R.id.rl_month_card);
		cb_pole_worktype = (CheckBox) findViewById(R.id.cb_pole_worktype);
		cb_softkeyboard = (CheckBox) findViewById(R.id.cb_softkeyboard);
		cb_fuzy_search = (CheckBox) findViewById(R.id.cb_fuzy_search);
		cb_month_card = (CheckBox) findViewById(R.id.cb_month_card);
		et_confidence_level = (EditText) findViewById(R.id.et_confidence_level);
		et_wifi = (EditText) findViewById(R.id.et_wifi);
		/*��ȡ�ϴα�������Ŷ�,�������Ŷ�*/
		int confidence_level = SharedPreferencesUtils.getParam(getApplicationContext(), "cameraParam", "confidenceLevel",65);
		//���ó��ڵ����Ŷ�
//		DecodeManager.getinstance().setConfidenceLevel(confidence_level);
		//������ڵ����Ŷ�
		setInCameraLevel(confidence_level);
		
//		et_confidence_level.setText(String.valueOf(DecodeManager.getinstance().getConfidenceLevel()));
		if(getWifiName() != null){
			et_wifi.setText(getWifiName().replace("\"", ""));
		}
		if (preActivityName != null && preActivityName.equals("ExitPageActivity")){
			rl_pole_worktype.setVisibility(View.GONE);
		}
		if (isPoleAutoWorking()){
			cb_pole_worktype.setChecked(true);
			tv_pole_worktype.setTextColor(getResources().getColor(R.color.occupy_red));
		}else {
			cb_pole_worktype.setChecked(false);
		}
		if (isShowSoftKeyBoard()){
			cb_softkeyboard.setChecked(true);
			tv_softkeyboard.setTextColor(getResources().getColor(R.color.occupy_red));
		}else {
			cb_softkeyboard.setChecked(false);
		}
		if (isShowFuzySearch()){
			cb_fuzy_search.setChecked(true);
			tv_fuzy_search.setTextColor(getResources().getColor(R.color.occupy_red));
		}else {
			cb_fuzy_search.setChecked(false);
		}
		showMonthCard = isShowMonthCard();
		if (showMonthCard){
			cb_month_card.setChecked(true);
			tv_month_card.setTextColor(getResources().getColor(R.color.occupy_red));
		}else {
			cb_month_card.setChecked(false);
		}
	}

	private void onClickEvent() {
		// TODO Auto-generated method stub
		//�ر�
		tv_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ChooseWorkstationActivity.this.finish();
			}
		});
		//����վNumpicker
		np_work.setOnWorkStationChangedListener(new OnWorkStationChangedListener() {

			@Override
			public void onWorkStationChanged(String value,int id) {
				// TODO Auto-generated method stub
				ChooseWorkstationActivity.this.value = value;
				ChooseWorkstationActivity.this.id = id;
			}
		});
		//�����̨
		btn_into_background.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//�ж��Ƿ�Ϊ����Ա�˺ţ��ǣ���ת������Ա��WebView������ʾ��
				ChooseWorkstationActivity.this.startActivity(
						new Intent(ChooseWorkstationActivity.this,BackgroundActivity.class));
			}
		});
		//ȷ��
		btn_yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//���ó���ʶ������Ŷ�
				if (!TextUtils.isEmpty(et_confidence_level.getText().toString())){
					SharedPreferencesUtils.setParam(getApplicationContext(), "cameraParam", "confidenceLevel", Integer.parseInt(et_confidence_level.getText().toString()));
					//���ó��ڵ����Ŷ�
//					DecodeManager.getinstance().setConfidenceLevel(Integer.parseInt(et_confidence_level.getText().toString()));
					//������ڵ����Ŷ�
					setInCameraLevel(Integer.parseInt(et_confidence_level.getText().toString()));					
				}
				if (cb_pole_worktype.isChecked()){
					SharedPreferencesUtils.setParam(getApplicationContext(), "cameraParam", "auto", true);
				}else {
					SharedPreferencesUtils.setParam(getApplicationContext(), "cameraParam", "auto", false);
				}
				if (cb_softkeyboard.isChecked()){
					SharedPreferencesUtils.setParam(getApplicationContext(), "zld_config", "showsoftkeyboard", true);
				}else {
					SharedPreferencesUtils.setParam(getApplicationContext(), "zld_config", "showsoftkeyboard", false);
				}
				if (cb_fuzy_search.isChecked()){
//					SharedPreferencesUtils.setParam(getApplicationContext(), "zld_config", "showfuzysearch", true);
					SharedPreferencesUtils.setParam(getApplicationContext(), "zld_config", "yessir", true);
				}else {
//					SharedPreferencesUtils.setParam(getApplicationContext(), "zld_config", "showfuzysearch", false);
					SharedPreferencesUtils.setParam(getApplicationContext(), "zld_config", "yessir", false);
				}
				if (cb_month_card.isChecked()){
					SharedPreferencesUtils.setParam(getApplicationContext(), "zld_config", "isshowmonthcard", true);
				}else {
					SharedPreferencesUtils.setParam(getApplicationContext(), "zld_config", "isshowmonthcard", false);
				}
				//��������ָ��wifi
				if (!TextUtils.isEmpty(et_wifi.getText().toString())){
					String wifiName = et_wifi.getText().toString().trim();
					connectWifi(wifiName);
				}
				String param = SharedPreferencesUtils.getParam(getApplicationContext(),
						"set_workStation", "staname", "����վ");
				if(param.equals(value)){
					if(showMonthCard != cb_month_card.isChecked()){
						SharedPreferencesUtils.setParam(getApplicationContext(), "zld_config", "isupdate", true);
					}else{
						SharedPreferencesUtils.setParam(getApplicationContext(), "zld_config", "isupdate", false);	
					}
					finish();
					return;
				}
				System.out.println("�������õ��Զ�̧�ˣ�"+cb_pole_worktype.isChecked());
				Log.e(TAG, "workStations:"+workStations+"---->>ID:"+id);
				AppInfo.getInstance().setStname(value);
				if(workStations != null){
					if(workStations.size() != 0){
						WorkStation workStation2 = workStations.get(id);
						if(workStation2.getId() != null&&workStation2.getId() != ""){
							getEnterclose(workStation2.getId());
							if(workStation2.getNet_type() !=null){
								SharedPreferencesUtils.setParam(getApplicationContext(),
										"nettype", "netType", workStation2.getNet_type());
								SharedPreferencesUtils.setParam(getApplicationContext(),
										"set_workStation", "workstation_id", workStation2.getId());
							}
						}
					}else{
						showToast("���ù���Ա�˺���ӹ���վ��");
					}
				}else{
					showToast("�����������ã�");
					finish();
				}
			}
		});

		rl_pole_worktype.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cb_pole_worktype.isChecked()){
					cb_pole_worktype.setChecked(false);
					tv_pole_worktype.setTextColor(getResources().getColor(R.color.gray));
				}else{
					tv_pole_worktype.setTextColor(getResources().getColor(R.color.occupy_red));
					cb_pole_worktype.setChecked(true);					
				}
			}
		});

		rl_softkeyboard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cb_softkeyboard.isChecked()){
					cb_softkeyboard.setChecked(false);
					tv_softkeyboard.setTextColor(getResources().getColor(R.color.gray));
				}else{
					tv_softkeyboard.setTextColor(getResources().getColor(R.color.occupy_red));
					cb_softkeyboard.setChecked(true);					
				}
			}
		});

		rl_fuzy_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cb_fuzy_search.isChecked()){
					cb_fuzy_search.setChecked(false);
					tv_fuzy_search.setTextColor(getResources().getColor(R.color.gray));
				}else{
					tv_fuzy_search.setTextColor(getResources().getColor(R.color.occupy_red));
					cb_fuzy_search.setChecked(true);					
				}
			}
		});

		rl_month_card.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cb_month_card.isChecked()){
					cb_month_card.setChecked(false);
					tv_month_card.setTextColor(getResources().getColor(R.color.gray));
				}else{
					tv_month_card.setTextColor(getResources().getColor(R.color.occupy_red));
					cb_month_card.setChecked(true);					
				}
			}
		});
	}
	/**
	 * ������ڵ�����������Ŷ�
	 */
	private void setInCameraLevel(int confidence_level){
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), HomeExitPageService.class);
		Bundle bundle = new Bundle();
		bundle.putInt("confidenceLevel", confidence_level);
		bundle.putString("intentkey", "setConfidenceLevel");
		intent.putExtras(bundle);
		startService(intent);
	}

	private void initSqliteManager() {
		if (sqliteManager == null) {
			sqliteManager = ((application) getApplication())
					.getSqliteManager(ChooseWorkstationActivity.this);
		}
	}

	/**
	 * ��ȡ����վ����
	 */
	private void initIntentStaname() {
		if(intentStaname == null){
			intentStaname = SharedPreferencesUtils.getParam(
					getApplicationContext(), "set_workStation", "staname", "δ֪����վ");
		}
	}


	/**
	 * ��ȡ����վ�Ľӿڣ�
	 * http://192.168.199.239/zld/
	 * worksiteinfo.do?comid=0&action=queryworksite
	 */
	public void getWorkStation() {
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.QUERY_WORKSITE);
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("out", "json");
		String url = params.getRequstUrl();
		Log.e(TAG, "��ȡ����վ��Ϣurl---------------->>" + url);
		DialogManager.getInstance()
		.showProgressDialog(this,"��ȡ����վ��Ϣ...");
		HttpManager.requestGET(this, url,this);	
	}

	/**
	 * ��ȡ����վͨ���Ľӿڣ�
	 * http://192.168.199.239/zld/worksiteinfo.do?
	 * worksite_id=4&action=querypass&comid=1749
	 */
	public void getEnterclose(final String worksiteId) {
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.QUERY_PASS_INFO);
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("worksite_id", worksiteId);
		params.setUrlParams("uid", AppInfo.getInstance().getUid());
		String url = params.getRequstUrl();
		Log.e(TAG, "��ȡ����վͨ��URL---------------->>" + url);
		DialogManager.getInstance()
		.showProgressDialog(this,"��ȡ����վͨ��...");
		HttpManager.requestGET(this, url,worksiteId,this);	
	}

	/**
	 * ��ȡ����վ�µ���������ͷ��LED��Ϣ
	 * http://192.168.199.239/zld/worksiteinfo.do?
	 * action=getpassinfo&worksite_id=29&comid=1749
	 */
	public void getWorksiteAllInfo(String worksiteId){
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl +Constant.WORKINFO);
		params.setUrlParams("worksite_id", worksiteId);//����վid
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		String url = params.getRequstUrl();
		Log.e(TAG, "��ȡ����վ�µ���������ͷ��LED��Ϣurl---------------->>" + url);
		HttpManager.requestGET(this,url,this);
	}

	@Override
	public boolean doSucess(String url,String object) {
		// TODO Auto-generated method stub
		DialogManager.getInstance().dissMissProgressDialog();
		Log.e(TAG, "doSucess---------------->>" + url);
		if (url.contains(Constant.WORKINFO)){
			doGetWorkInfoResult(object);
		}else if(url.contains(Constant.QUERY_WORKSITE)){
			doQueryWorkSite(object);
		}
		return true;
	}
	
	@Override
	public boolean doSucess(String url, String object, String worksiteId) {
		// TODO Auto-generated method stub
		DialogManager.getInstance().dissMissProgressDialog();
		Log.e(TAG, "doSucess---------------->>" + url);
		if(url.contains(Constant.QUERY_PASS_INFO)){
			doQueryPassInfo(object,worksiteId);
		}
		return true;
	}

	/**
	 * ����վ�µ���Ϣ���
	 */
	private void doGetWorkInfoResult(String object) {
		Gson gson = new Gson();
		Type typeToken = new TypeToken<List<WorkStationDevice>>() {}.getType();
		ArrayList<WorkStationDevice> workStationDevices = gson.fromJson(object, typeToken);

		if(workStationDevices!=null&&workStationDevices.size()!=0){
			Log.e(TAG, "�����Ĺ���վ�µ�������ϢΪ"+ workStationDevices.toString());
			for(int i=0;i<workStationDevices.size();i++){
				WorkStationDevice workStationDevice = workStationDevices.get(i);
				String passtype = workStationDevice.getPasstype();
				String passname = workStationDevice.getPassname();
				MyCameraInfo[] cameras = workStationDevice.getCameras();
				MyLedInfo[] leds = workStationDevice.getLeds();
				if(cameras!=null&&cameras.length!=0){
					for(int j=0;j<cameras.length;j++){
						MyCameraInfo cameraInfo = cameras[j];
						cameraInfo.setPasstype(passtype);
						cameraInfo.setPassname(passname);
						sqliteManager.insertCameraData(cameras[j]);
					}
				}
				if(leds!=null&&leds.length!=0){
					for(int k=0;k<leds.length;k++){
						MyLedInfo ledInfo = leds[k];
						ledInfo.setPasstype(passtype);
						ledInfo.setPassname(passname);
						sqliteManager.insertLedData(leds[k]);
					}
				}
			}
			/**��������Ϣ��,ִ����ת����*/
			startIntent(enterCloseList);
		}
	}

	/**
	 * ��ȡ����վ��Ϣ�Ľ��
	 * @param object
	 */
	private void doQueryWorkSite(String object) {
		// TODO Auto-generated method stub
		int index = 0;
		if (object == null) {
			showToast("�����»�ȡ����վ��Ϣ");
			return;
		}
			Log.e(TAG,"��ȡ������վΪ"+object);
			Gson gson = new Gson();
			Type type = new TypeToken<List<WorkStation>>() {}.getType();
			workStations = gson.fromJson(object, type);
			workStation = new String[workStations.size()];
			System.out.println("��ȡ������վ�ļ��ϵĳ���" + workStations.size());
			if (workStations == null || workStations.size() == 0){
				showToast("û�й���վ��Ϣ��");
			}else{
				Log.e(TAG,"����������վΪ-->>"+workStations.toString());
				for(int i=0; i<workStations.size();i++){
					System.out.println("********"+ workStations.get(i).getWorksite_name());
					workStation[i] = workStations.get(i).getWorksite_name();
					if (intentStaname != null){
						Log.e(TAG, "workStation[i] + i---->>"+workStation[i] + i);
						if (workStation[i].equals(intentStaname)){
							index = i;
						}
					}
				}
				np_work.setValue(index);
				np_work.setData(0, workStation.length-1, workStation);
				value = workStation[index];
				id = index;												
			}
		}

	/**
	 * ��ȡ��Ӧ����վ��ͨ����Ϣ
	 * @param object
	 */
	private void doQueryPassInfo(String object, String worksiteId) {
		// TODO Auto-generated method stub
		Log.e(TAG,"��ȡ������վͨ��Ϊ:"+object);
		Gson gson = new Gson();
		Type type = new TypeToken<List<EnterClose>>() {}.getType();
		enterCloseList = gson.fromJson(object, type);
		if (enterCloseList == null || enterCloseList.size() == 0){
			showToast("û�й���վͨ����Ϣ��");
		}else{
			/*���ݿ����ͨ����Ϣ,����ɾ��,�ٻ�ȡ*/
			ArrayList<MyCameraInfo> selectCamera = 
					sqliteManager.selectCamera(SqliteManager.PASSTYPE_ALL);
			if(selectCamera.size()>0){
				sqliteManager.deleteCameraData();
				sqliteManager.deleteLedData();
			}
			getWorksiteAllInfo(worksiteId);
		}
	}
	
	private void startIntent(List<EnterClose>  enterClose) {
		//�ù���վ��ֻ��һ��ͨ��
		if(enterClose != null){
			if(enterClose.size()==1){
				if(enterClose.get(0) != null){
					savePass(enterClose);
				}
				//ͨ����������2��;
			}else if(enterClose.size()>1){
				EnterClose enterClo= enterClose.get(0);
				if(enterClo != null){
					String passtype0 = enterClo.getPasstype();
					boolean twoPassType = isTwoPassType(enterClose, passtype0);//�������Ƿ��и���һ��ͨ������ͬ���͵�ͨ��
					Log.e(TAG, "��һ��ͨ�����ͣ�"+passtype0+"=�Ƿ���˫ͨ�����ͣ�"+twoPassType);
					if(twoPassType){
						saveFirstRecord("�����", value,enterClo.getId(),enterClose.get(passItem).getId());
					}else{
						savePass(enterClose);
					}
				}
			}
		}
		intentInfo();
	}

	private void intentInfo(){
		/* ����л��˹���վ */
		//�����رյ�HomeExitPageService�����ص��ײ���㷨Ҳ�ز�����������ײ����Ŷ�������˾͡�
		ZldNewActivity zldNewActivity = ((application)getApplication()).getZldNewActivity();
		if(zldNewActivity!=null){
			if(zldNewActivity.titleFragment != null){
				//����
				zldNewActivity.titleFragment.closeRemotService();
				zldNewActivity.titleFragment.restartApp(this);
			}
		}else{
			Intent intent = new Intent(this,ZldNewActivity.class);
			startActivity(intent);
			finish();
		}
	}

	/**
	 * ͨ������
	 * @param intent
	 * @param enterClose
	 * @return
	 */
	private void savePass(List<EnterClose> enterClose) {
		Log.e(TAG,"=="+enterClose.get(0).getPasstype()+"=="+enterClose.get(0).toString());
		if(enterClose.get(0).getPasstype().equals("0")){
			saveFirstRecord("���", value,enterClose.get(0).getId(),null);

		}else if(enterClose.get(0).getPasstype().equals("1")){
			saveFirstRecord("����", value,enterClose.get(0).getId(),null);
		}
	}

	/**�Ƿ�������ͨ��*/
	private boolean isTwoPassType(List<EnterClose> enterClose, String passtype0) {
		for(int i = 0; i < enterClose.size(); i++ ){
			String passtype = enterClose.get(i).getPasstype();//��ȡͨ������
			if(!passtype0.equals(passtype)){
				passItem = i;
				return true;
			}
		}
		return false;
	}

	/**
	 * �����һ�ν������ù���վ�ļ�¼
	 */
	private void saveFirstRecord(String workStationType,
			String staname,String passOneId,String passTwoId) {
		Log.e(TAG, staname + " this is saveFirstRexord stanme ");
		SharedPreferences spf = getApplicationContext().
				getSharedPreferences("set_workStation",Context.MODE_PRIVATE);
		Editor spfEdit = spf.edit();
		spfEdit.putBoolean("is_first", false);
		spfEdit.putString("workStationType", workStationType);
		spfEdit.putString("staname", staname);
		spfEdit.putString("passOneId", passOneId);
		spfEdit.putString("passTwoId", passTwoId);
		spfEdit.commit();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(token != null && comid != null){
			getWorkStation();
		}
	}

	private boolean isPoleAutoWorking(){
		return SharedPreferencesUtils.getParam(getApplicationContext(), "cameraParam", "auto", false);
	}

	private boolean isShowSoftKeyBoard(){
		return SharedPreferencesUtils.getParam(getApplicationContext(), "zld_config", "showsoftkeyboard", false);
	}

	private boolean isShowFuzySearch(){
//		return SharedPreferencesUtils.getParam(getApplicationContext(), "zld_config", "showfuzysearch", false);
		return SharedPreferencesUtils.getParam(getApplicationContext(), "zld_config", "yessir", true);
	}
	private boolean isShowMonthCard(){
		return SharedPreferencesUtils.getParam(getApplicationContext(), "zld_config", "isshowmonthcard", false);
	}
	/**
	 * ��ʼ��wifi��������
	 */
	private void initWifi() {
		if(wifiAdmin == null){
			wifiAdmin = new WifiAdmin(this);
		}
		if(wifiConnectReceiver == null){
			wifiConnectReceiver = new WifiConnectReceiver();
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		this.registerReceiver(wifiConnectReceiver, filter);
	}

	/**
	 * ��ȡ��ǰ���ӵ�wifi��
	 * @return
	 */
	private String getWifiName() {
		WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiMgr.getConnectionInfo();
		String wifiId = info != null ? info.getSSID() : null;
		return wifiId;
	}

	/**
	 * ����ѡ����wifi
	 */
	private void connectWifi(String wifiName){
		configuratedList = wifiAdmin.getConfiguratedList();
		scanResultList = wifiAdmin.getScanResultList();
		if (scanResultList != null) {
			for (int i = 0; i < scanResultList.size(); i++) {
				System.out.println("scanResultList" + i + "----->"
						+ scanResultList.get(i).SSID);
				if (scanResultList.get(i).SSID.equals(wifiName)) {
					canConnectable = true;
					break;
				}
			}
			if (canConnectable) {
				if (configuratedList != null) {
					for (int j = 0; j < configuratedList.size(); j++) {
						System.out.println("configuratedList" + j
								+ "------->"+ configuratedList.get(j).SSID);
						configurateSsid = "\"" + wifiName + "\"";
						if (configuratedList.get(j).SSID
								.equals(configurateSsid)) {
							isConfigurated = true;
							networkId = configuratedList.get(j).networkId;
							break;
						}
						// isConfigurated = false;
					}
					System.out.println("%%%%%%%%%%%%%%%%%%%%%%"+ isConfigurated);
					if (isConfigurated) {
						System.out.println("^^^^^^^����ǰ");
						wifiAdmin.connectWifi(networkId);
						System.out.println("#########���Ӻ�");
						System.out.println("isConnected--------->" + isConnected);
						while (!isConnected) {
							System.out
							.println("*************������ָ��wifi*************");
							break;
						}
						System.out.println("*****************************");
					}
				}
			} else {
				System.out.println("$$$$$$$$$$���ڿ����ӷ�Χ��$$$$$$$$$$$$$");
			}
		}
	}

	class WifiConnectReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
				NetworkInfo notewokInfo = manager.getActiveNetworkInfo();
				if (notewokInfo != null) {
					System.out.println("notewokInfo.getExtraInfo()------->"
							+ notewokInfo.getExtraInfo());
					currentWifiInfo = wifiAdmin.getCurrentWifiInfo();
					System.out.println("currentWifiInfo.getSSID()----->"
							+ currentWifiInfo.getSSID());
					if (currentWifiInfo.getSSID().equals(configurateSsid)) {
						isConnected = true;
					}
				} else {
					System.out.println("notewokInfo is null");
				}
			}
		}

	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.unregisterReceiver(wifiConnectReceiver);
	}
}
