/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015��4��13�� 
 * 
 *******************************************************************************/ 
package com.zld.fragment;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


import com.google.gson.Gson;
import com.zld.R;
import com.zld.bean.AllOrder;
import com.zld.bean.AppInfo;
import com.zld.bean.CarNumberOrder;
import com.zld.bean.PrePayOrder;
import com.zld.lib.constant.Constant;
import com.zld.lib.dialog.DialogManager;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.state.ComeInCarState;
import com.zld.lib.state.OrderListState;
import com.zld.lib.util.FileUtil;
import com.zld.lib.util.ImageUitls;
import com.zld.lib.util.InputUtil;
import com.zld.lib.util.SharedPreferencesUtils;
import com.zld.lib.util.StringUtils;
import com.zld.lib.util.TimeTypeUtil;
import com.zld.lib.util.VoicePlayer;
import com.zld.service.PollingService;
import com.zld.view.KeyboardViewPager;
import com.zld.view.SelectCarType;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * <pre>
 * ����˵��: 
 * ����:	2015��4��13��
 * ������:	HZC
 * 
 * ��ʷ��¼
 *    �޸����ݣ�
 *    �޸���Ա��
 *    �޸����ڣ� 2015��4��13��
 * </pre>
 */
public class OrderDetailsFragment extends BaseFragment implements OnClickListener, OnTouchListener{
	private static final String TAG = "OrderDetailsFragment";
	public static final String action = "REFRESH_STATE";
	public static final int cashorder_action = 1;
	public static final int modifyorder_action = 2;
	private EditText et_car_num;//���ƺ���
	private ImageView iv_car_image;//����С��
	private Button btn_bigsmall_switch;//��С��
	private Button btn_car_type; //��������
	private TextView tv_account_type;//�˻�����
	private TextView tv_entrance_time;//�볡ʱ��
	private TextView tv_park_duration;//ͣ��ʱ��
	private Button btn_clear_order;//���㶩��
	private KeyboardViewPager kvp;
	public CarNumberOrder currenOrder;
	public ComeInCarState comeInCarState;
	private int billingType = 0;//Ĭ�ϲ����ִ�С��
	public boolean isHideClearOrderBtn = false;//�Ƿ����ؽ��㶩����ť
	//���浱ǰ����Ķ�����id�ͳ��ƺţ�����ͼƬ�ص�ʱ�Ŀ���
//	private String ordid = "";
//	private String carPlate = "";
	
	private SelectCarType selectCarType;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.DELAY_UPLOAD:
				String orderid = (String) msg.obj;
				exitCarBmpUpload(orderid);
				break;

			default:
				break;
			}
			super.handleMessage(msg);  
		}
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.order_details, container,
				false);
		initView(rootView);
		onClickEvent();
		isShowClearOrder();
//		setShowBillButton();
		registerBr();
		initComeInCarState();
		return rootView;
	}

	/**
	 * 1��ʾ���㶩����ť,0����
	 */
	private void isShowClearOrder() {
		// TODO Auto-generated method stub
		String ishidehdbutton = AppInfo.getInstance().getIshidehdbutton();
		if(ishidehdbutton == null){
			String param = SharedPreferencesUtils.getParam(activity.getApplicationContext(),"zld_config", "ishidehdbutton", Constant.sZero);
			AppInfo.getInstance().setIshidehdbutton(param);
		}
		if(ishidehdbutton != null){
			if(ishidehdbutton.equals(Constant.sZero)){
				isHideClearOrderBtn = false;
				btn_clear_order.setVisibility(View.VISIBLE);
			}else if(ishidehdbutton.equals(Constant.sOne)){
				isHideClearOrderBtn = true;
				btn_clear_order.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void initView(View rootView) {
		iv_car_image =(ImageView) rootView.findViewById(R.id.iv_car_image);
		et_car_num = (EditText)rootView.findViewById(R.id.et_car_num);
//		btn_bigsmall_switch = (Button) rootView.findViewById(R.id.btn_bigsmall_switch);
		btn_car_type = (Button) rootView.findViewById(R.id.btn_car_type);
		tv_account_type = (TextView) rootView.findViewById(R.id.tv_account_type);
		tv_entrance_time = (TextView) rootView.findViewById(R.id.tv_entrance_time);
		tv_park_duration = (TextView) rootView.findViewById(R.id.tv_park_duration);
		btn_clear_order = (Button) rootView.findViewById(R.id.btn_clear_order);
		selectCarType = new SelectCarType(activity,btn_car_type,this);
		InputUtil.hideTypewriting(activity, et_car_num);
	}

	/**
	 * �ؼ�����¼�
	 */
	private void onClickEvent() {
//		btn_bigsmall_switch.setOnClickListener(this);
		btn_car_type.setOnClickListener(this);
		btn_clear_order.setOnClickListener(this);
		et_car_num.setOnTouchListener(this);
	}

	/**
	 * ��������㲥
	 */
	private void registerBr() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(action);
		if(activity != null){
			activity.registerReceiver(new OrderDetailsReceiver(), intentFilter);
		}
	}

	private void initComeInCarState() {
		if(comeInCarState == null){
			comeInCarState = new ComeInCarState();
		}
	}

	/**
	 * ��ʾ������ĸ����
	 */
	private void editOnTouch(View v) {
		// TODO Auto-generated method stub
		if(kvp == null){
			kvp = new KeyboardViewPager(activity, true);
		}
		kvp.setEt_carnumber(et_car_num);
		// ������ʾ��������ť�ұ�
		kvp.setDirection("left");
		kvp.showPopupWindow(et_car_num);
	}

	public void hidePopupWindow(){
		if(kvp != null){
			kvp.hidePopupWindow();
		}
	}

	/**
	 * ���ؽ��㶩����ť
	 */
	public void hideBtn(){
		if(btn_clear_order.getVisibility() == View.VISIBLE){
			btn_clear_order.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * ��ʾ���㶩����ť
	 */
	public void showBtn(){
		if(btn_clear_order.getVisibility() == View.INVISIBLE){
			btn_clear_order.setVisibility(View.VISIBLE);
		}
	}	

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
//		case R.id.btn_bigsmall_switch:
//			setSwitchBtnStyle(billingType);
//			/* �޸Ķ�����С���ƷѲ���  */
//			alertCarType();
//			break;
		case R.id.btn_car_type:
			//չ����������
			if (activity.getItemOrder() != null) {
				selectCarType.showSwitchAccountView(activity.getItemOrder().getId());
			}	
			break;
		case R.id.btn_clear_order:
			int action = getOrderAction();
			//���㶩��or�޸ĳ��ƺ�
			if (action == cashorder_action){
				if(OrderListState.getInstance().isOrderFinishState()){
					activity.controlExitCamera();
				}
				if(isHideClearOrderBtn){
					hideBtn();
				}
				selectClearOder(true);
			}else if(action == modifyorder_action){
				modifyOrder();
				Log.e(TAG, "�޸ĳ��ƺ����,״̬��Ϊ��ǰ����״̬");
				activity.setCurrentOrderState();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		editOnTouch(v);//������ʾ�����	
		return false;
	}

	/**
	 * ��ʾ����
	 * @param order
	 */
	public void refreshView(CarNumberOrder order){
		if (order == null){
			clearView();
			return;
		}
		activity.hideSealBtn();
		/* ����Ԥ֧��,��ʾ����,��ʾ���㰴ť*/
		activity.hidePrepay();
		activity.showCost();

		/*��������,�ֶ�������; ���Զ�����״̬;���̨����Ϊ��ʾ;����ʾ���㰴ť*/
		System.out.println("isExit:"+activity.isExitComeinCar+
				"==isHand:"+OrderListState.getInstance().isHandSearchState()+
				"==isAuto"+OrderListState.getInstance().isAutoSearchState()+
				"==isHide"+!isHideClearOrderBtn);
		System.out.println("====="+(activity.isExitComeinCar&&OrderListState.getInstance().isHandSearchState()));

		if(((activity.isExitComeinCar&&OrderListState.getInstance().isHandSearchState())||
				OrderListState.getInstance().isAutoSearchState()||!isHideClearOrderBtn)
				&&!OrderListState.getInstance().isParkOutState()){
			showBtn();
		}else{
			hideBtn();
			activity.isExitComeinCar = false;
		}

		if(!OrderListState.getInstance().isHandSearchState()){
			activity.hideFreeAndChargeBtn();
		}
		
		if (order.getState() != null && order.getState().equals(Constant.sOne)) {
			activity.showFreeAndChargeBtn();
		}
		currenOrder = order;
		Log.e(TAG, "������ʾǰ������"+order.toString());
		if (!"-1".equals(order.getUin())){
			tv_account_type.setText(getResources().getString(R.string.account_type_tingchebao));
		}else{
			tv_account_type.setText(getResources().getString(R.string.account_type_content));
		}
		if ("5".equals(order.getCtype())){
			tv_account_type.setText("�¿��û�");
			Log.e(TAG, "��������״̬��"+comeInCarState.getState());
			if(comeInCarState.getState() == ComeInCarState.EXIT_COME_IN_CAR_STATE ){
				VoicePlayer.getInstance(activity).playVoice("�˳�Ϊ�¿��û�");
			}
		}
		et_car_num.setText(order.getCarnumber());
		String btime = order.getBtime();
		tv_entrance_time.setText(btime);
		if(order.getDuration().substring(0, 2).equals("��ͣ")){
			tv_park_duration.setText(order.getDuration().substring(2));
		}else{
			tv_park_duration.setText(order.getDuration());
		}
		iv_car_image.setImageResource(R.drawable.plate_sample);
//		setSwitchBtnStyle(order);
		setSwitchCarType(order);
	}

//	private String getBtime(CarNumberOrder order) {
//		String btime = order.getBtime();
//		// ���糬ʱ״̬
//		try{
//			long longtime = Long.parseLong(btime);
//			if(longtime > 1400562840){
//				if(btime!=null&&btime.length()>9){
//					btime = TimeTypeUtil.getEasyStringTime(Long.parseLong(btime)*1000);
//				}
//			}
//		}catch(NumberFormatException e){
//			Log.e(TAG,"���糬ʱ״̬,�볡ʱ�䱾������");
//		}
//		Log.e(TAG, "������ʾʱ�䣺"+btime);
//		// 2015-12-13 12:13
//		if(btime.matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}")){
//			Log.e(TAG,"btime:"+btime);
//			Long stringTime = TimeTypeUtil.getLongTime(btime);
//			Log.e(TAG,"stringTime:"+stringTime);
//			btime = TimeTypeUtil.getEasyStringTime(stringTime);
//		}else if(btime.matches("\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}")){
//			btime = btime.substring(6, btime.length());
//		}
//		return btime;
//	}

	/**
	 * ѡ����㷽ʽ
	 * Ԥ֧����ʽ��������ʽ
	 */
	public void selectClearOder(boolean isclick){
		/*��ʾ���㷽ʽǰ,����ʾ��Ѻ��շ���ɰ�ť
		 * ���������⣬Ӧ��Ҫ�ȵ��������õ��Ժ�����ʾ��Ѻ��շ����
		 * */
//		activity.showFreeAndChargeBtn();
		FileUtil.writeSDFile("������������", "��ѡ����㷽ʽ   OrderListState.getInstance().isOrderFinishState()= "+OrderListState.getInstance().isOrderFinishState()+"   (activity.getItemOrder() == null)="+(activity.getItemOrder() == null));
		if(activity.getItemOrder() == null){
			activity.showToast("δѡ�ж�����");
			return;
		}

		if (!OrderListState.getInstance().isOrderFinishState()) {
			activity.showToast("��ִ����������");
			return;
		}
		
		String prepay = activity.getItemOrder().getPrepay();
		Log.e(TAG, "ѡ����㷽ʽ"+activity.getItemOrder());
		FileUtil.writeSDFile("������������", "��activity.getItemOrder() = "+activity.getItemOrder());
		if(prepay != null){
			Double prepayNum = Double.valueOf(prepay);
		    
			if(prepayNum > 0){
				prePayOrder();
			}else{
				/*����Ǿ���,�������,ֱ�����ˢ��,����һ�¾���һ·˳��*/
				if(activity.getItemOrder() != null){
					String carnumber = activity.getItemOrder().getCarnumber();
					if(carnumber != null){
						
//						SharedPreferences sp = getActivity().getSharedPreferences("policeman", Context.MODE_PRIVATE);
						boolean sir = SharedPreferencesUtils.getParam(getActivity(), "zld_config", "yessir", true);
						if(sir&&StringUtils.isPolice(carnumber)){
							activity.clickFreeOrder(true);
						}else{
							FileUtil.writeSDFile("������������", "��selectClearOder  OrderListState.getInstance().isOrderFinishState():"+ OrderListState.getInstance().isOrderFinishState()+"  isclick:"+isclick);
							if(OrderListState.getInstance().isOrderFinishState()){
								completeOrder(isclick);
							}else{
								activity.showToast("��ִ����������");
							}
						}
					}
				}
			}
		}else{
			completeOrder(isclick);
		}
	}

//	/**
//	 * �޸Ķ�����С���ƷѲ��� car_type=0 ��ͨ�� 1��С�� 2����
//	 */
//	private void alertCarType(){
//		RequestParams params = new RequestParams();
//		params.setUrlHeader(Constant.requestUrl + Constant.CHANGE_CAR_TYPE);
//		params.setUrlParams("comid", AppInfo.getInstance().getComid());
//		params.setUrlParams("orderid", activity.getItemOrder().getId());
//		params.setUrlParams("car_type", ""+billingType);
//		String url = params.getRequstUrl();
//		Log.e(TAG, "�޸Ķ�����С���ƷѲ��Ե�url��--->"+url);
//		HttpManager.requestGET(getActivity(), url, this);
//	}
	
	/**
	 * ����ʶ��-������㶩��URL---->>
	 */
	private void completeOrder(boolean isclick){
		//�Ƿ��Ǳ��ط�����
		if(!AppInfo.getInstance().getIsLocalServer(activity)){//�Ǳ��ط�������û��ƽ�屾�ػ��ĸ���		
			/*���ػ�*/
			boolean param = SharedPreferencesUtils.getParam(
					activity.getApplicationContext(),"nettype","isLocal", false);
			Log.e("isLocal","OrderDetailFragment completeOrder get isLocal "+param);
//			if(param||AppInfo.getInstance().getIssuplocal().equals(Constant.sOne)){
//				localCompleteOrder();
//				return;
//			}
		}
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.COMPLETE_ORDER);
		/**listѡ�е�item���ò�������֤������������������ȷ�Ķ���   */
		params.setUrlParams("orderid", activity.getItemOrder().getId());
		if (isMonthCardUser()){
			params.setUrlParams("collect", 0);
		}else{
			if(activity.getItemOrder() != null){
				params.setUrlParams("collect", activity.getItemOrder().getTotal());
			}
		}
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("uid", AppInfo.getInstance().getUid());
		params.setUrlParams("imei", AppInfo.getInstance().getImei());
		params.setUrlParams("passid", activity.passid);
		params.setUrlParams("isclick", isclick+"");
		String url = params.getRequstUrl();
		Log.e(TAG, "������㶩����url��--->"+url);
		FileUtil.writeSDFile("LOG", "���̣�completeOrder��ʼ����  url:"+ url);
//		HttpManager.UpLogs(getActivity(),"���̣�completeOrder��ʼ����  url:"+ url);
		HttpManager.requestGET(getActivity(), url, this);
	}

	/**
	 * ����ʶ��-Ԥ֧��������㶩��URL---->>
	 * http://s.tingchebao.com/zld/
	 * nfchandle.do?action=doprepayorder&orderid=**&collect=*&uid=&comid=&passid=
	 */
	public void prePayOrder(){
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.PRE_PAY);
		/**listѡ�е�item���ò�������֤������������������ȷ�Ķ���   */
		params.setUrlParams("orderid", activity.getItemOrder().getId());
		if (isMonthCardUser()){
			params.setUrlParams("collect", 0);
		}else{
			params.setUrlParams("collect", activity.getItemOrder().getTotal());
		}
		params.setUrlParams("uid", AppInfo.getInstance().getUid());
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("passid", activity.passid);
		String url = params.getRequstUrl();
		Log.e(TAG, "������㶩����url��--->"+url);
		HttpManager.requestGET(getActivity(), url, this);
	}
	int failureCount =  -1;
	@Override
	public boolean doSucess(String url, String object) {
		// TODO Auto-generated method stub
		DialogManager.getInstance().dissMissProgressDialog();
		if (url.contains(Constant.CHANGE_CAR_TYPE)){
			doChargeCarTypeResult(object);
		}else if(url.contains(Constant.COMPLETE_ORDER)){
			boolean isclick= url.contains("true");
//			PollingService.sendMessage(url+"\n"+object, getActivity());
//			HttpManager.UpLogs(getActivity(),"���̽���<<<<<<<<<<��completeOrder�ɹ�    url:"+ url+"  object:"+object);
			FileUtil.writeSDFile("LOG", "���̣�completeOrder�ɹ�    url:"+ url+"  object:"+object);
			activity.showFreeAndChargeBtn();
			if(object.startsWith("{")){
				doPrePayOrderResult(object);
			}else{
				doCompleteOrderResult(object,isclick);
			}
			failureCount = 0;
		}else if(url.contains(Constant.MODIFY_ORDER)){
			doModifyOrderResult(object);
		}else if(url.contains(Constant.PRE_PAY)){
			doPrePayOrderResult(object);
		}
		return super.doSucess(url, object);
	}

	@Override
	public boolean doFailure(String url, String status) {
		// TODO Auto-generated method stub
		DialogManager.getInstance().dissMissProgressDialog();
		if (url.contains(Constant.CHANGE_CAR_TYPE)){
			Log.e(TAG,"CHANGE_CAR_TYPE_URL����5Sʧ�ܣ�"+status);
		}else if(url.contains(Constant.COMPLETE_ORDER)){
			if (failureCount <3) {
				failureCount ++;
				activity.showToast("��������ʧ�ܣ����������ύ��");
				HttpManager.requestGET(getActivity(), url, this);
			}else{
				activity.showToast("��������ʧ�ܣ����˹�ȷ�ϣ�");
			}
			Log.e(TAG,"COMPLETE_ORDER_URL����5Sʧ�ܣ�"+status);
		}else if(url.contains(Constant.MODIFY_ORDER)){
			Log.e(TAG,"MODIFY_ORDER_URL����5Sʧ�ܣ�"+status);
		}else if(url.contains(Constant.PRE_PAY)){
			Log.e(TAG,"PRE_PAY_URL����5Sʧ�ܣ�"+status);
		}
		return super.doFailure(url, status);
	}

	@Override
	public void timeout(String url) {
		// TODO Auto-generated method stub
		Log.e(TAG,"��������ʱ��"+url);
		DialogManager.getInstance().dissMissProgressDialog();
		if (url.contains(Constant.CHANGE_CAR_TYPE)){
			//doChargeCarTypeTimeOut();
		}else if(url.contains(Constant.COMPLETE_ORDER)){
			if (failureCount <3) {
				failureCount ++;
				activity.showToast("�������㳬ʱ�����������ύ��");
				HttpManager.requestGET(getActivity(), url, this);
			}else {
				activity.showToast("�������㳬ʱ�����˹�ȷ��");
			}
			
//			localCompleteOrder();
		}else if(url.contains(Constant.MODIFY_ORDER)){
//			doModifyOrderTimeOut();
		}else if(url.contains(Constant.PRE_PAY)){
			//doPrePayOrderTimeOut();
		}
		super.timeout(url);
	}

	/**
	 * �ı��С������������ 
	 * @param object 
	 */
	private void doChargeCarTypeResult(String object) {
		if (Constant.sOne.equals(object)){
			activity.showToast("�޸ĳɹ���");

			if (billingType == 1){
				btn_bigsmall_switch.setSelected(false);
				billingType = 2;
			}else if (billingType == 2){
				btn_bigsmall_switch.setSelected(true);
				billingType = 1;
			}
			activity.cashOrder();
		}
	}

	/**
	 * ���㶩���������
	 * @param object
	 */
	private void doCompleteOrderResult(String object,Boolean isclick) {
		// TODO Auto-generated method stub
		if (Constant.sOne.equals(object)||"4".equals(object)||
				"2".equals(object)||"3".equals(object)) {
			if(currenOrder == null){
				activity.showToast("�˶����ѽ��㣡");
				return;
			}
			//���ػ�
//			getLocalOrderDBManager().deleteOrderLocalByid(currenOrder.getOrderid());
			/*�������б�״̬��Ϊ�������״̬*/
			OrderListState.getInstance().
			setState(OrderListState.ORDER_FINISH_STATE);
			activity.cashFragment.setChargeFinishBtn(currenOrder);
			AllOrder itemOrder = activity.getItemOrder();
			if(itemOrder == null){
				return;
			}
			
			if (itemOrder.getCtype().equals("7")) {
				VoicePlayer.getInstance(this.getActivity()).playVoice("�˳�Ϊ�¿��ڶ�����");
				activity.showToast("�˳�Ϊ�¿��ڶ�����");
			}
			
			String collect = itemOrder.getTotal();
			String carnumber = itemOrder.getCarnumber();
			String limitday = itemOrder.getLimitday();
			String duration = itemOrder.getDuration();
			String showCollect1 = null;
			String showCollect2 = null;
			String content = null;
			if(TextUtils.isEmpty(collect)){
				activity.showToast("����������");
				return;
			}
			if(collect.equals("")||collect.equals("\"null\"")||collect == null){
				activity.showToast("����������");
				return;
			}
			if(isMonthCardUser()){
				Log.e(TAG,"�¿��Զ�����");
				collect = Constant.sZero;
					if (activity.getExitledinfo() != null && Integer.parseInt(activity.getExitledinfo().getWidth()) > 64) {
						showCollect2 = "�¿�	һ·˳��";
					}else {
						showCollect2 = "һ·˳��";
					}
				showCollect1 = carnumber;
				content = "�¿�	һ·˳��";
				Log.e(TAG, "�¿���Ч�ڣ�"+limitday);
				if (TimeTypeUtil.isMthUserExpire(limitday)){
					content = "�¿���Ч����"+ TimeTypeUtil.getFutureDate(Integer.parseInt(limitday), "MM��dd��") + "һ·˳��";
					activity.showToast(content);
				}
				activity.controlExitPole();
				/*�޸�Ϊ�շ����״̬Ϊ���ö���Ϊ�¿�ʱ,����������Զ�ˢ��*/
				OrderListState.getInstance().
				setState(OrderListState.CLEAR_FINISH_STATE);
				/*��������״̬Ϊ�������Զ�ˢ��״̬,�����Զ�ˢ��֮��,��ȡ���б��һ��Ϊ�¿��û��Ļ�,�Ͳ�������*/
				comeInCarState.setState(ComeInCarState.AUTO_REFRESH_ORDER_LIST);
				/*�¿��û���һ�������������*/
				activity.cashFragment.setFree(false);
				/*ֱ��ˢ��,���õ����������*/
				activity.refreshListOrder();
			}else{
				if(Double.parseDouble(collect) == 0.00f){
					Log.e(TAG,"0Ԫ�Զ�����");
					content = /**"ͣ��ʱ��"+duration+*/"ͣ����"+0+"Ԫ	һ·˳��";
					collect = Constant.sZero;
					if (activity.getExitledinfo() != null && Integer.parseInt(activity.getExitledinfo().getWidth()) > 64) {
						showCollect2 = carnumber+"	0Ԫ";
					}else {
					    showCollect1 = carnumber;
						showCollect2 = "	0Ԫ";
					}

					activity.controlExitPole();
					// ���ýɷѾ�̧�˷��У����ʹ���˼���ȯ���Ͳ�Ҫˢ�£���Ȼ�շ�Ա������զ�˳���������Ϊ������
					String ticketID = activity.getItemOrder().getShopticketid();
					if (ticketID != null) {
						activity.cashFragment.hideCost();
						activity.cashFragment.showPrepay();
						activity.cashFragment.setPrepayed(null);
						activity.cashFragment.hideFreeBtn();
						OrderListState.getInstance().setState(OrderListState.ORDER_FINISH_UPPOLE_STATE);
					}else {
						/*�޸�Ϊ�շ����״̬Ϊ���ö���Ϊ���ʱ,���Զ�ˢ��*/
						OrderListState.getInstance().
						setState(OrderListState.CLEAR_FINISH_STATE);
						/*��������״̬Ϊ�������Զ�ˢ��״̬,�����Զ�ˢ��֮��,��ȡ���б��һ��Ϊ�¿��û��Ļ�,�Ͳ�������*/
						comeInCarState.setState(ComeInCarState.AUTO_REFRESH_ORDER_LIST);
						/*0Ԫ�û���һ�������������*/
						activity.cashFragment.setFree(false);
						/*ֱ��ˢ��,���õ����������*/
						activity.refreshListOrder();
					}
				}else if(Double.parseDouble(collect) > 0.00f){
//					AllOrder order = activity.getItemOrder();
					StringBuilder sb = new StringBuilder();
					sb.append("************������************\n");
					sb.append("�������ƣ�"+AppInfo.getInstance().getParkName()+"\n");
					sb.append("�շ�Ա��"+AppInfo.getInstance().getName()+"\n");
					sb.append("���ƺţ�"+activity.getItemOrder().getCarnumber()+"\n");
					sb.append("����ʱ�䣺"+activity.getItemOrder().getBtime()+"\n");
					sb.append("����ʱ�䣺"+(activity.getItemOrder().getEnd()==null?TimeTypeUtil.getNowTime():(activity.getItemOrder().getEnd()))+"\n");
					sb.append("ͣ��ʱ����"+activity.getItemOrder().getDuration()+"\n");
					sb.append("�շѽ�"+collect+"Ԫ\n");
					sb.append("******************************\n\n\n");
					PollingService.sendMessage(sb.toString(), getActivity());
					
					String ticketID = activity.getItemOrder().getShopticketid();
					if (ticketID != null) {
						activity.cashFragment.hideCost();
						activity.cashFragment.showPrepay();
						activity.cashFragment.setPrepayed(null);
					}
					
					if (collect.endsWith(Constant.sZero)) {
						Log.e(TAG,"�Ե����β");
						collect = collect.substring(0,collect.length()-2);
					}
					content = /**"ͣ��ʱ��"+duration+*/"�뽻��"+collect+"Ԫ";
					if (activity.getExitledinfo() != null && Integer.parseInt(activity.getExitledinfo().getWidth()) > 64) {
						showCollect2 = carnumber+"	"+collect+"Ԫ";
						
					}else {
					    showCollect1 = carnumber;
						showCollect2 = collect+"Ԫ";
					}
				}
			}
			Log.e(TAG,"�ۼ��շ�Ա���");
			activity.addTollmanMoney(collect,itemOrder.getPrepay(),true);
			Log.e(TAG,"LED��ʾ");
			activity.sendLedShow(showCollect1,showCollect2,content);
			Log.e(TAG,"�ϴ�����ͼƬ");
			//�ֶ����㣬������ɹ��Ȼص�ʱ��ͼƬδ�ص��������ӳ�6s���ϴ�ͼƬ
			uploadExitPhoto(currenOrder.getOrderid(),isclick);
			activity.showToast("���㶩���ɹ�");
			if(isHideClearOrderBtn){
				hideBtn();
			}
		}else if("-5".equals(object)){
			activity.showToast("�����µ���������㣡");
		}else if("-6".equals(object)){
			activity.showToast("�����µ���������㣡");
		}else{
			activity.showToast("���㶩��ʧ�ܣ�");
		}
	}

	/**
	 * �ϴ�������Ƭ
	 */
	private void uploadExitPhoto(String orderid,Boolean isclick){
		Log.e(TAG,"exitFragment�Ƿ�Ϊnull��"+activity.exitFragment);
		if(activity.exitFragment != null){
			Log.e(TAG,"exitCarBmpInfo�Ƿ�Ϊnull��"+activity.exitFragment.exitCarBmpInfo);
			if(activity.exitFragment.exitCarBmpInfo != null&&!isclick){
				exitCarBmpUpload(orderid);
			}else{
				final String orderId = orderid;
			    new Handler().postDelayed(new Runnable(){      
			        public void run() {    
			        	exitCarBmpUpload(orderId);
			        }      
			     }, 3500);  
			  
			}
		}
	}

	private void exitCarBmpUpload(String orderid) {
		if(activity.exitFragment != null){
			if(activity.exitFragment.exitCarBmpInfo != null){
				String netType = 
						SharedPreferencesUtils.getParam(
								activity.getApplicationContext(),"nettype", "netType", null);
				InputStream bitmapToInputStream = 
						ImageUitls.getBitmapInputStream(netType,
								activity.exitFragment.exitCarBmpInfo.getBitmap());
				activity.upload(bitmapToInputStream,
						orderid, Constant.EXIT_PHOTOTYPE);
			}
		}
	}

	/**
	 * ����С����ͼƬ
	 * @param bitmap
	 */
	public void refreshCarPlate(Bitmap bitmap){
		if(bitmap == null){
			iv_car_image.setImageResource(R.drawable.plate_sample);
		}else{
			iv_car_image.setImageBitmap(bitmap);
		}
	}

	/**
	 * ���㶩����ť����
	 */
	private void setOrderAction(String action){
		btn_clear_order.setText(action);
	}

	private int getOrderAction(){
		String action =  btn_clear_order.getText().toString().trim();
		if (activity.getResources().getString(R.string.clear_order).equals(action)){
			return cashorder_action;
		}else{
			return modifyorder_action;
		}
	}

//	/**
//	 * ��ʾ�Ƿ���ʾ��С���ƷѰ�ť
//	 */
//	private void setShowBillButton(){
//		if (AppInfo.getInstance().isParkBilling()){
//			btn_bigsmall_switch.setVisibility(View.VISIBLE);			
//		}else{
//			btn_bigsmall_switch.setVisibility(View.GONE);
//		}
//	}

//	/**
//	 * ���ô�С���л���ť����ʽ 
//	 * @param order
//	 */
//	private void setSwitchBtnStyle(CarNumberOrder order){
//		if (order != null){
//			String carType = order.getCar_type();
//			if (carType != null){
//				if (carType.equals(Constant.sZero) || carType.equals(Constant.sOne)){
//					btn_bigsmall_switch.setSelected(false);
//					billingType = 2;
//				}else{
//					btn_bigsmall_switch.setSelected(true);
//					billingType = 1;
//				}
//			}
//		}
//	}

//	/**
//	 * ���ô�С���л���ť����ʽ 
//	 * @param billingType
//	 */
//	private void setSwitchBtnStyle(int billingType){
//		if (billingType == 1){
//			btn_bigsmall_switch.setSelected(false);
//			billingType = 2;
//		}else if (billingType == 2){
//			btn_bigsmall_switch.setSelected(true);
//			billingType = 1;
//		}
//	}
	
	private void setSwitchCarType(CarNumberOrder order){
		selectCarType.closePop();
		if (order != null){
			String carType = order.getCar_type();
			String carTypeName = null;
			if(AppInfo.getInstance()==null)
				return;
			int count = AppInfo.getInstance().getAllCarTypes().size();
			for (int i = 0; i < count; i++) {
				String name = AppInfo.getInstance().getAllCarTypes().get(i).getCarTypeID();
				if (name.equals(carType)) {
					carTypeName = AppInfo.getInstance().getAllCarTypes().get(i).getCarTypeName();
					btn_car_type.setText(carTypeName);
					break;
				}
			}

			if (carTypeName == null){
				carTypeName = AppInfo.getInstance().getAllCarTypes().get(0).getCarTypeName();
				btn_car_type.setText(carTypeName);
			}
			btn_car_type.setEnabled(true);
		}
	}

	private void clearView(){
		currenOrder = null;
		iv_car_image.setImageBitmap(null);
		tv_account_type.setText("");
		et_car_num.setText("");
		tv_entrance_time.setText("");
		tv_park_duration.setText("");
		btn_clear_order.setVisibility(View.INVISIBLE);
		setOrderAction(activity.getString(R.string.clear_order));
		btn_car_type.setEnabled(false);
	}

	private String getInput(){
		return et_car_num.getText().toString().trim();
	}

	private boolean isInputLegal(){
		return TextUtils.isEmpty(getInput())? false : true;
	}

	private boolean isMonthCardUser(){
		return Constant.sOne.equals(activity.listFragment.getItemOrder().getIsmonthuser());
	}

	public CarNumberOrder getCurrenOrder() {
		return currenOrder;
	}

	/**
	 * �޸ĳ��ƺ�
	 */
	private void modifyOrder() {
		if (!isInputLegal()){
			return;
		}
		//�Ƿ��Ǳ��ط�����
		if(!AppInfo.getInstance().getIsLocalServer(activity)){//�Ǳ��ط�������û��ƽ�屾�ػ��ĸ���
			//���ػ�
			boolean param = SharedPreferencesUtils.getParam(
					activity.getApplicationContext(), "nettype", "isLocal", false);
			Log.e("isLocal","OrderDetailsFragment modifyOrder get isLocal "+param);
//			if(param||AppInfo.getInstance().getIssuplocal().equals(Constant.sOne)){
////				doModifyOrderTimeOut();
//				return;
//			}
		}
		String carnumber = "";
		try {
			carnumber = URLEncoder.encode(URLEncoder.encode(getInput(), "utf-8"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.MODIFY_ORDER);
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("orderid", currenOrder.getOrderid());
		params.setUrlParams("carnumber", carnumber);
		params.setUrlParams("through", 3);
		String url = params.getRequstUrl();
		Log.e(TAG, "url---------------->>" + url); 
		DialogManager.getInstance().showProgressDialog(getActivity(),
				"�޸ĵ�ǰ����...");
		HttpManager.requestGET(getActivity(), url, this);
	}

//	private void doModifyOrderTimeOut() {
//		String carnumber = getInput();
//		String orderid = currenOrder.getOrderid();
//		String localid = currenOrder.getLocalid();
//		//�����޸�
////		getLocalOrderDBManager().updateOrderCarplateLocalCash(orderid,carnumber,localid);
//		//�޸ĳ��ƺ�ˢ����ʾ
//		modifyCarPlateAfter();
//	}

	/**
	 * �޸ĳ��ƺ� ˢ����ʾ
	 */
	private void modifyCarPlateAfter() {
		/*��������ؽ��㶩����ť*/
		if(isHideClearOrderBtn){
			btn_clear_order.setVisibility(View.INVISIBLE);
		}else{
			setOrderAction(activity.getResources().getString(R.string.clear_order));
		}
		Intent intent = new Intent();
		intent.setAction(OrderListFragment.REFRESH_ITEM);
		intent.putExtra("carNumber", getInput());
		activity.sendBroadcast(intent);
	}

	/**
	 * �޸ĳ��ƺŽ��
	 * @param object
	 */
	private void doModifyOrderResult(String object) {
		Log.e(TAG, object);
		if (Constant.sOne.equals(object)){
			modifyCarPlateAfter();
		}else if(Constant.sZero.equals(object)){
			activity.showToast("�޸�ʧ��,���ƺ��Ѵ��ڡ�");
		}
	}
	public void payBack(String orderid){
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.PAY_BACK);
		params.setUrlParams("orderid", orderid);
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		String url = params.getRequstUrl();
		Log.e(TAG, "�볡֧���ص�url---------------->>" + url);
		HttpManager.requestGET(getActivity(), url, this);
	}	
	/**
	 * Ԥ֧���������
	 * @param object
	 */
	private void doPrePayOrderResult(String object) {
		// TODO Auto-generated method stub
		Log.e(TAG, "��ȡ����ǰ����Ϊ" + object);
		Gson gson = new Gson();
		PrePayOrder prePayOrder = gson.fromJson(object, PrePayOrder.class);
		AllOrder itemOrder = activity.getItemOrder();
		if(itemOrder == null){
			return;
		}
		
		double waitCollect = -1;
		if (itemOrder.getShopticketid() != null) {
			waitCollect = Double.parseDouble(itemOrder.getBefcollect()) - Double.parseDouble(itemOrder.getDistotal());
		}
		
		if (prePayOrder.getResult().equals(Constant.sOne) || waitCollect == 0) {//�ɹ�
			String carnumber = itemOrder.getCarnumber();
			String total = itemOrder.getTotal();
			activity.carUserPayed(carnumber,total);
			payBack(itemOrder.getId());//�볡֧���ص�
			uploadExitPhoto(currenOrder.getOrderid(),false);
			//�ۼ��շ�Ա���
			activity.addTollmanMoney(null,total,true);
		}else if(prePayOrder.getResult().equals("2") || waitCollect >0){//��Ҫ�����
			activity.cashFragment.hideCost();
			activity.cashFragment.showPrepay();
			String prefee = prePayOrder.getPrefee();
			if(prefee != null){
				activity.cashFragment.setPrepayed(prePayOrder);
				VoicePlayer.getInstance(activity).playVoice("������֧��"+prefee+"Ԫ");
			}
			uploadExitPhoto(itemOrder.getId(),false);
			//�ۼ��շ�Ա���
			String total = itemOrder.getTotal();
			Float money = 0F;
			if(total != null&&prefee!=null){
				money = Float.parseFloat(total)-Float.parseFloat(prefee);
			}
			activity.addTollmanMoney(""+money,prefee,true);
		}else if(prePayOrder.getResult().equals("-1")){//ʧ��
			activity.showToast("����Ԥ֧������ʧ�ܣ�����ϵ�Ƴ���ϵͳ�ͷ���");
		}
	}

	private class OrderDetailsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String intentAction = intent.getAction();
			if (action.equals(intentAction)){
				int state = intent.getIntExtra("state", cashorder_action);
				if (state == cashorder_action){
					setOrderAction(activity.getResources().getString(R.string.clear_order));
				}else if (state == modifyorder_action){
					if(OrderListState.getInstance().isParkOutState()){
						return;
					}
					Log.e(TAG, "��ǰ״̬����Ϊ�޸�״̬");
					activity.setModifyState();
					btn_clear_order.setVisibility(View.VISIBLE);
					setOrderAction(activity.getResources().getString(R.string.save_change));
				}
			}
		}

	}

	/**
	 * ���ػ����
	 */
//	private void localCompleteOrder() {
//		// TODO Auto-generated method stub
//		Log.e(TAG,"���ػ�����");
//		if(currenOrder == null){
//			return ;
//		}
//		if(activity.getItemOrder() == null){
//			return;	
//		}
//		boolean police = false;
//		boolean isOpen = false;
//		String pay_type = Constant.sOne;
//		String c_type = "3";
//		Long currentTime = System.currentTimeMillis()/1000;
//		Log.e(TAG, "���㶩��,ItemOrder���ݲ������ݿ�"+activity.getItemOrder().toString());
//		Log.e(TAG, "���㶩��,currenOrder���ݲ������ݿ�"+currenOrder.toString());
//		// ���ݳ��ƺŲ�ѯ�Ƿ��б����볡����
//		final String carnumber = activity.getItemOrder().getCarnumber();
//		String total = activity.getItemOrder().getTotal();
//		String prepay = activity.getItemOrder().getPrepay();
//		final String orderid = activity.getItemOrder().getId();
//		String localid = activity.getItemOrder().getLocalid();
//
//		if(currenOrder.getIsmonthuser()!=null){
//			if(currenOrder.getIsmonthuser().equals(Constant.sOne)){
//				total = Constant.sZero;
//				pay_type = "3";
//				c_type = "5";
//				isOpen = true;
//			}
//			//�Ƿ��Ǿ�����
//			if(StringUtils.isPolice(carnumber)){
//				//��
//				total = Constant.sZero;
//				police = true;
//			}
//		}
//		if(getLocalOrderDBManager().selectOrderIsCash(orderid,localid)){
//			//�����Զ�����
//			getLocalOrderDBManager().updateOrderLocalCash(orderid,localid,pay_type,c_type,
//					AppInfo.getInstance().getUid(),total,""+currentTime,activity.passid);
//
//			carPlate = carnumber;
//			ordid = orderid;
//			Log.e(TAG,""+carnumber + "==" + orderid);
//			//�������,����ͼƬ
//			activity.setTakePhotoLinster(new TakePhotoLinster() {
//
//				@Override
//				public void setTakePhotoLinster(Bitmap bitmap) {
//					// TODO Auto-generated method stub
//					if(carPlate.equals(carnumber)){
//						Log.e(TAG,"true carPlate:"+carPlate+" ordid:"+ordid+" carnumber:"+carnumber+" orderid:"+orderid);
//						activity.saveImage(activity.resultBitmap, carnumber, orderid);
//					}else{
//						Log.e(TAG,"false carPlate:"+carPlate+" ordid:"+ordid+" carnumber:"+carnumber+" orderid:"+orderid);
//
//						activity.saveImage(bitmap,carPlate, ordid);
//					}
//				}
//			});
//
//			//�ۼ��շ�Ա���
//			activity.addTollmanMoney(total,prepay,true);
//			activity.showToast("���㶩���ɹ�");
//			Log.e(TAG,"=������"+police+"==�¿���"+isOpen);
//			if(total==null){
//				return;
//			}
//			if(police||isOpen||(total!=null&&total.equals(Constant.sZero))||total.equals("0.0")){
//				if(isOpen){
//					VoicePlayer.getInstance(activity).playVoice("�¿�һ·˳��");
//				}
//				if(police){
//					VoicePlayer.getInstance(activity).playVoice("���������");
//				}
//				activity.controlExitPole();
//				//activity.sendLedShow(exitledinfo,Constant.sZero,"һ·˳��");
//				activity.hideFreeAndChargeBtn();
//				OrderListState.getInstance().setState(OrderListState.CLEAR_FINISH_STATE);
//				activity.refreshListOrder();
//			}else{
//				OrderListState.getInstance().setState(OrderListState.ORDER_FINISH_STATE);
//			}
//		}else
//		{
//			activity.showToast("�����ѽ���");
//		}
//		return;
//	}

}
