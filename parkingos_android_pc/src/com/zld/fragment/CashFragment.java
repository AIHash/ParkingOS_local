/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015��4��13�� 
 * 
 *******************************************************************************/
package com.zld.fragment;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.google.gson.Gson;
import com.zld.R;
import com.zld.bean.AllOrder;
import com.zld.bean.AppInfo;
import com.zld.bean.CarNumberMadeOrder;
import com.zld.bean.CarNumberOrder;
import com.zld.bean.PrePayOrder;
import com.zld.lib.constant.Constant;
import com.zld.lib.dialog.DialogManager;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.state.OrderListState;
import com.zld.lib.util.SharedPreferencesUtils;
import com.zld.lib.util.StringUtils;
import com.zld.lib.util.VoicePlayer;
import com.zld.view.DiscountViewPager;
import com.zld.view.SelectFreeCar;

import android.R.string;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * <pre>
 * ����˵��: �շѲ���Fragment
 * ����:	2015��4��13��
 * ������:	HZC
 * 
 * ��ʷ��¼
 *    �޸����ݣ�
 *    �޸���Ա��
 *    �޸����ڣ� 2015��4��13��
 * </pre>
 */
public class CashFragment extends BaseFragment implements OnClickListener {
	private static final String TAG = "CashFragment";
	private Button btn_free;// ���
	private Button btn_discount;// ����
	private View rootView;
	private RelativeLayout rl_cost;
	private TextView tv_total;// �ܽ��
	private TextView tv_collect;// ���
	private TextView tv_prefee;// Ԥ֧��
	private TextView tv_discount;// �������
	private TextView tv_park_cost;// ����
	private Button btn_charge_finish;// �շ����
	private TextView tv_mobile_payment;// �ֻ�֧��-���£�
	private RelativeLayout rl_pay_before;
	private SelectFreeCar selectFreeCar;
	@SuppressWarnings("unused")
	private CarNumberOrder currentOrder;
	private String orderId;// ���ɺ����������Ķ���id
	/** �����ѡ��շ����Ϊfalse,����һ��Ϊ��ͨ��δ������ʱΪtrue; */
	private boolean isFree = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.cash_operation, container, false);
		initView(rootView);
		onClickEvent();
		return rootView;
	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void initView(View rootView) {
		rl_cost = (RelativeLayout) rootView.findViewById(R.id.rl_cost);
		tv_park_cost = (TextView) rootView.findViewById(R.id.tv_park_cost);
		tv_mobile_payment = (TextView) rootView.findViewById(R.id.tv_mobile_payment);
		rl_pay_before = (RelativeLayout) rootView.findViewById(R.id.rl_pay_before);
		tv_collect = (TextView) rootView.findViewById(R.id.tv_collect);
		tv_total = (TextView) rootView.findViewById(R.id.tv_total);
		tv_prefee = (TextView) rootView.findViewById(R.id.tv_prefee);
		tv_discount = (TextView) rootView.findViewById(R.id.tv_prediscount);
		btn_free = (Button) rootView.findViewById(R.id.btn_free);
		btn_charge_finish = (Button) rootView.findViewById(R.id.btn_charge_finish);
		btn_discount = (Button) rootView.findViewById(R.id.btn_discount);
		selectFreeCar = new SelectFreeCar(activity, btn_free, this);
		this.rootView = rootView;
	}

	/**
	 * �ؼ�����¼�
	 */
	private void onClickEvent() {
		btn_free.setOnClickListener(this);
		btn_charge_finish.setOnClickListener(this);
		btn_discount.setOnClickListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_free:
			if (AppInfo.getInstance().getFreeResons() != null && AppInfo.getInstance().getFreeResons().size() > 0) {
				if (activity.getItemOrder() != null) {
					selectFreeCar.showFreeTypeView(activity.getItemOrder().getId());
				}
				return;
			}
			freeActionHandle(false, null);
			break;
		case R.id.btn_discount:
			showDiscountPage();
			return;
		// break;
		case R.id.btn_charge_finish:
			chargeFinish();
			break;
		default:
			break;
		}
		hideSeal();
		hidePrepay();
		isFree = false;
		activity.hideFreeAndChargeBtn();
	}

	public void showDiscountPage() {
		DiscountViewPager dvp = new DiscountViewPager(activity, false, this);
		dvp.setDirection("left");
		dvp.showPopupWindow(btn_discount, rootView.getHeight());
	}

	public void showFreePage() {
		DiscountViewPager dvp = new DiscountViewPager(activity, false, this);
		dvp.setDirection("left");
		dvp.showPopupWindow(btn_discount, rootView.getHeight());
	}

	/**
	 * �շ���ɰ�ť�Ĳ���
	 */
	public void chargeFinish() {
		ShowCost();
		if (btn_charge_finish.getText().equals("֪����")) {
			activity.refreshListOrder();
		} else if (btn_charge_finish.getText().equals("�շ����")) {
			if (!(OrderListState.getInstance().isOrderFinishUppoleState()
					|| OrderListState.getInstance().isOrderFinishState())) {
				activity.controlExitPole();
				if (activity.getExitledinfo() != null) {
					if (activity.getExitledinfo() != null
							&& Integer.parseInt(activity.getExitledinfo().getWidth()) > 64) {
						activity.sendLedShow(activity.getExitledinfo().getMatercont(), "		һ·˳��", "һ·˳��");
					} else {
						activity.sendLedShow(activity.getExitledinfo().getMatercont(), "һ·˳��", "һ·˳��");
					}
				}
			}
			OrderListState.getInstance().setState(OrderListState.CLEAR_FINISH_STATE);
			activity.refreshListOrder();
			double cost = 0;
			try {
				cost = Double.parseDouble(tv_park_cost.getText().toString());
			} catch (Exception e) {
				e.printStackTrace();
				cost = 1;
			}
			if (cost > 0)
				activity.getChargeInfo();
		}
		btn_charge_finish.setText("�շ����");
		btn_discount.setText("����");
	}

	@SuppressWarnings("static-access")
	public void refreshView(CarNumberOrder order) {
		if (order == null) {
			clearView();
			return;
		}
		// ��������û����������ǿ����ʾ��Ѱ�ť�������շ���ɰ�ť
		if (OrderListState.getInstance().isNoOrderState()) {
			if (AppInfo.getInstance().getInstance().isPassfree()) {
				btn_free.setVisibility(View.VISIBLE);
			}
			activity.detailsFragment.hideBtn();
			btn_charge_finish.setVisibility(View.INVISIBLE);
		} else if (OrderListState.getInstance().isParkOutState()) {

		}
		currentOrder = order;
		/* �Ƿ�Ϊ�¿��û�-�Ƿ�Ϊ0Ԫ */
		setChargeFinishBtn(order);
	}

	/**
	 * �����շ���ɰ�ť
	 * 
	 * @param order
	 */
	public void setChargeFinishBtn(CarNumberOrder order) {
		if (order == null) {
			return;
		}
		try {
			if ((order.getCtype() != null && order.getCtype().equals("5"))
					|| (order.getShopticketid() == null && order.getCollect() != null
							&& !order.getCollect().equals("null") && Double.parseDouble(order.getCollect()) == 0f)) {
				tv_park_cost.setText("0.0");
				Log.e("OrderListState", "��ǰ״̬Ϊ��" + OrderListState.getInstance().getState());
				if (OrderListState.getInstance().isClearFinishState()) {
					btn_charge_finish.setText("֪����");
				}
			} else {
				if (order.getCollect() != null) {
					if (order.getCollect().equals("null")) {
						activity.showToast("�����ú�̨��С���۸�.");
					}
					boolean sir = SharedPreferencesUtils.getParam(getActivity(), "zld_config", "yessir", true);
					if (sir && StringUtils.isPolice(order.getCarnumber())) {
						/* ������ ��ʾΪ0Ԫ */
						tv_park_cost.setText("0.0");
					} else {
						// ���˼���ȯ��collect�ǽ����beforecollect���ܽ��
						if (order.getBefcollect() != null) {
							tv_park_cost.setText(order.getBefcollect());
						} else {
							tv_park_cost.setText(order.getCollect());
						}
					}
					btn_charge_finish.setText("�շ����");
				}
			}
		} catch (NumberFormatException e) {
			tv_park_cost.setText("�۸�δ֪");
			Log.e("OrderListState", "��ǰ״̬Ϊ��" + OrderListState.getInstance().getState());
			if (OrderListState.getInstance().isClearFinishState()) {
				btn_charge_finish.setText("֪����");
			}
		}
	}

	private void clearView() {
		tv_park_cost.setText("");
	}

	/**
	 * ���س���
	 */
	public void hideCost() {
		if (rl_cost.getVisibility() == View.VISIBLE) {
			rl_cost.setVisibility(View.GONE);
		}
	}

	/**
	 * ��ʾ����
	 */
	public void ShowCost() {
		if (rl_cost.getVisibility() == View.GONE) {
			rl_cost.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * ������Ѻ��շ���ɰ�ť
	 */
	public void hideFreeAndChargeBtn() {
		btn_free.setVisibility(View.INVISIBLE);
		btn_charge_finish.setVisibility(View.INVISIBLE);
		btn_discount.setVisibility(View.INVISIBLE);
	}

	/**
	 * ������Ѱ�ť
	 */
	public void hideFreeBtn() {
		btn_free.setVisibility(View.INVISIBLE);
		btn_discount.setVisibility(View.INVISIBLE);
	}

	/**
	 * ��ʾ��Ѻ��շ���ɰ�ť
	 */
	public void showFreeAndChargeBtn() {
		if (AppInfo.getInstance().isPassfree()) {
			btn_free.setVisibility(View.VISIBLE);
		} else {
			btn_free.setVisibility(View.INVISIBLE);
		}
		if (activity.getItemOrder() != null && activity.getItemOrder().getPrepay() != null
				&& activity.getItemOrder().getPrepay().equals("0.0")
				&& activity.getItemOrder().getShopticketid() == null) {
			btn_discount.setVisibility(View.VISIBLE);
		}
		btn_charge_finish.setVisibility(View.VISIBLE);
	}

	/**
	 * ��ʾ��Ѱ�ť
	 */
	public void showFree() {
		if (AppInfo.getInstance().isPassfree()) {
			btn_free.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * �����շ���ɰ�ť
	 */
	public void hideChargeBtn() {
		if (btn_charge_finish.getVisibility() == View.VISIBLE) {
			btn_charge_finish.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * ��ʾ��֧��ӡ��
	 */
	public void showSeal() {
		if (tv_mobile_payment.getVisibility() == View.INVISIBLE) {
			tv_mobile_payment.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * ������֧��ӡ��
	 */
	public void hideSeal() {
		if (tv_mobile_payment.getVisibility() == View.VISIBLE) {
			tv_mobile_payment.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * ��ʾԤ֧�����
	 */
	public void showPrepay() {
		activity.cashFragment.hideCost();
		if (rl_pay_before.getVisibility() == View.GONE) {
			rl_pay_before.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * ����Ԥ֧�����
	 */
	public void hidePrepay() {
		if (rl_pay_before.getVisibility() == View.VISIBLE) {
			rl_pay_before.setVisibility(View.GONE);
		}
	}

	/**
	 * ���ó���Ԥ֧�����
	 */
	public void setPrepayed(PrePayOrder prePayOrder) {
		showPrepay();

		if (activity.getItemOrder().getShopticketid() != null) {
			double waitCollect = StringUtils.formatDouble(Double.parseDouble(activity.getItemOrder().getBefcollect())
					- Double.parseDouble(activity.getItemOrder().getDistotal())
					- Double.parseDouble(activity.getItemOrder().getPrepay()));
			if (waitCollect > 0) {
				tv_collect.setText("" + waitCollect);
			} else {
				tv_collect.setText("0.0");
			}
		} else if (prePayOrder != null && prePayOrder.getCollect() != null) {
			tv_collect.setText(prePayOrder.getCollect());
		}

		if (activity.getItemOrder().getShopticketid() != null) {
			double waitCollect = StringUtils.formatDouble(Double.parseDouble(activity.getItemOrder().getBefcollect())
					- Double.parseDouble(activity.getItemOrder().getDistotal())
					- Double.parseDouble(activity.getItemOrder().getPrepay()));
			if (waitCollect < 0) {
				double showValue = StringUtils
						.formatDouble(Double.parseDouble(activity.getItemOrder().getPrepay()) + waitCollect);
				tv_prefee.setText("" + showValue); // Ԥ֧������ֱ���˸������������շ�Ա����ʾ��Ĳ���
			} else {
				tv_prefee.setText(activity.getItemOrder().getPrepay());
			}

		} else if (prePayOrder != null && prePayOrder.getPrefee() != null) {
			tv_prefee.setText(prePayOrder.getPrefee());
		}

		if (activity.getItemOrder().getShopticketid() != null) {
			tv_total.setText(activity.getItemOrder().getBefcollect());
		} else if (prePayOrder != null && prePayOrder.getTotal() != null) {
			tv_total.setText(prePayOrder.getTotal());
		}

		if (activity.getItemOrder().getShopticketid() != null) {
			tv_discount.setText(activity.getItemOrder().getDistotal());
		} else {
			if (prePayOrder.getDiscount() != null) {
				tv_discount.setText(prePayOrder.getDiscount());
			} else {
				tv_discount.setText("0");
			}
		}
	}

	@SuppressWarnings("static-access")
	public void showFreeHideChargeFinish() {
		// ��������û����������ǿ����ʾ��Ѱ�ť�������շ���ɰ�ť
		if (OrderListState.getInstance().isNoOrderState()) {
			if (AppInfo.getInstance().getInstance().isPassfree()) {
				btn_free.setVisibility(View.VISIBLE);
			}
			activity.detailsFragment.hideBtn();
			btn_charge_finish.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * �����Ѱ�ť�Ĵ���
	 */
	public void freeActionHandle(boolean isPolice, String freeReason) {
		// ��������û��ƥ�������£������ѵĴ���
		Log.e("OrderListState", "freeActionHandle��ǰ״̬Ϊ��" + OrderListState.getInstance().getState());
		if (OrderListState.getInstance().isNoOrderState()) {
			activity.detailsFragment.showBtn();
			madeOrder();
		} else {
			hideSeal();
			hidePrepay();
			isFree = false;
			activity.hideFreeAndChargeBtn();
			freeOrder(isPolice, freeReason);
		}
	}

	public void disCountAfterComplete(String time, String type) {
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.HD_DERATE);
		params.setUrlParams("orderid", activity.getItemOrder().getId());
		params.setUrlParams("type", type); // 3 jianshi 4 quanmian
		params.setUrlParams("time", time);
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("uid", AppInfo.getInstance().getUid());
		String url = params.getRequstUrl();
		Log.e(TAG, "��������url��--->" + url);
		HttpManager.requestGET(activity, url, this);
	}

	/**
	 * ��Ѷ��� isPolice �Ƿ��Ǿ�����,Ϊ���þ�����ѳ�,�������̨���ͳ����
	 */
	public void freeOrder(boolean isPolice, String freeReason) {
		AllOrder itemOrder = null;
		if (activity != null) {
			itemOrder = activity.getItemOrder();
		}
		// �Ƿ��Ǳ��ط�����
		if (!AppInfo.getInstance().getIsLocalServer(activity)) {// �Ǳ��ط�������û��ƽ�屾�ػ��ĸ���
			// ���ػ���Ѳ���
			boolean param = SharedPreferencesUtils.getParam(activity.getApplicationContext(), "nettype", "isLocal",
					false);
			Log.e("isLocal", "CashFragment freeOrder get isLocal " + param);
			if (param || AppInfo.getInstance().getIssuplocal().equals("1")) {
				localFree(itemOrder);
				return;
			}
		}
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.FREE_ORDER);
		params.setUrlParams("token", AppInfo.getInstance().getToken());
		/* �Ƿ����޶���״̬,����һ�������ֽ���� */
		if (OrderListState.getInstance().isNoOrderState()) {
			params.setUrlParams("orderid", orderId);
		} else {
			/* ���������Ȼ����� */
			if (itemOrder != null) {
				params.setUrlParams("orderid", itemOrder.getId());
			}
		}
		params.setUrlParams("passid", activity.passid);
		if (isPolice) {
			params.setUrlParams("isPolice", 1);
		}
		if (freeReason != null) {
			params.setUrlParams("freereasons", freeReason);
		}
		String url = params.getRequstUrl();
		Log.e(TAG, "��Ѷ�����url��--->" + url);
		HttpManager.requestGET(activity, url, this);
	}

	/**
	 * ���ɶ���
	 */
	public void madeOrder() {
		String token = AppInfo.getAppInfo().getToken();
		if (token != null && token.equals("false")) {
			return; // �����ύû��token������������������ֹ������û�����������
		}
		String carNumber = "";
		try {
			carNumber = StringUtils.encodeString(activity.exitFragment.exitCarBmpInfo.getCarPlate(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.MADE_ORDER);
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("uid", AppInfo.getInstance().getUid());
		params.setUrlParams("carnumber", carNumber);
		params.setUrlParams("through", 3);
		params.setUrlParams("from", 0);
		params.setUrlParams("car_type", -1);
		params.setUrlParams("passid", activity.passid);
		String url = params.getRequstUrl();
		Log.e(TAG, "���ɶ�����url��--->" + url);
		HttpManager.requestGET(activity, url, this);
	}

	/**
	 * collectorrequest.do?action=invalidorders&invalid_order=-1
	 * &token=198f697eb27de5515e91a70d1f64cec7 ���г�λ���Ӽ�1
	 */
	private void changeFreePark(String value) {
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.CHANG_INVALIDORDER);
		params.setUrlParams("token", AppInfo.getInstance().getToken());
		params.setUrlParams("invalid_order", value);
		String url = params.getRequstUrl();
		HttpManager.requestGET(activity, url, this);
	}

	@Override
	public boolean doSucess(String url, String object) {
		// TODO Auto-generated method stub
		Log.e(TAG, "���緵�صĽ��Ϊ" + object);
		DialogManager.getInstance().dissMissProgressDialog();
		if (url.contains(Constant.FREE_ORDER)) {
			doFreeOrderResult(object);
		} else if (url.contains(Constant.MADE_ORDER)) {
			doMadeOrderResult(object);
		} else if (url.contains(Constant.CHANG_INVALIDORDER)) {
			doChangInvalidOrderResult(object);
		} else if (url.contains(Constant.HD_DERATE)) {
			PrePayOrder preOrder = new PrePayOrder();
			Map<String, String> discount = StringUtils.getMapForJson(object);
			preOrder.setCollect(discount.get("collect"));
			preOrder.setPrefee("0");
			preOrder.setResult(discount.get("result"));
			preOrder.setTotal(discount.get("befcollect"));
			preOrder.setDiscount(discount.get("distotal"));
			setPrepayed(preOrder);
			btn_discount.setVisibility(View.INVISIBLE);
			if (discount.get("collect").equals("0.0")) {
				btn_free.setVisibility(View.INVISIBLE);
			}
		}
		return true;
	}

	@Override
	public void timeout(String url) {
		// TODO Auto-generated method stub
		DialogManager.getInstance().dissMissProgressDialog();
		if (url.contains(Constant.FREE_ORDER)) {
			localFree(activity.getItemOrder());
		} else if (url.contains(Constant.MADE_ORDER)) {

		}
	}

	private void doChangInvalidOrderResult(String object) {
		Log.e(TAG, "doChangInvalidOrderResult--------------->" + object);
	}

	private void doMadeOrderResult(String object) {
		CarNumberMadeOrder info = new Gson().fromJson(object, CarNumberMadeOrder.class);
		if ("1".equals(info.getInfo())) {
			orderId = info.getOrderid();
		}
		freeOrder(false, null);
	}

	/**
	 * ��Ѷ����������
	 * 
	 * @param object
	 */
	private void doFreeOrderResult(String object) {
		Log.e(TAG, "��Ѷ��������緵�ؽ����---------------��" + object + " isFree��" + isFree);
		if ("1".equals(object)) {
			if (activity.exitFragment.exitCarBmpInfo == null) {
				return;
			}
			freeCar();
			showLedAndPole();
		}
	}

	/**
	 * ��ʾled �� ��բ
	 */
	private void showLedAndPole() {
		if (!isFree) {
			/* �������ڵ�բ */
			activity.controlExitPole();
			if (activity.getExitledinfo() != null) {
				if (Integer.parseInt(activity.getExitledinfo().getWidth()) > 64) {
					activity.sendLedShow(activity.getExitledinfo().getMatercont(), "		һ·˳��", "һ·˳��");
				} else {
					activity.sendLedShow(activity.getExitledinfo().getMatercont(), "һ·˳��", "һ·˳��");
				}
			}

			// ����ǳ���û��ƥ���ϵģ���Ҫ����һ������������������һ�����г�λ����
			Log.e("OrderListState", "��ǰ״̬Ϊ��" + OrderListState.getInstance().getState());
			if (OrderListState.getInstance().isNoOrderState()) {
				changeFreePark("1");
			}
			OrderListState.getInstance().setState(OrderListState.CLEAR_FINISH_STATE);
			activity.refreshListOrder();
		}
	}

	/**
	 * ��ѳ���������
	 */
	private void freeCar() {
		if (activity.exitFragment == null || activity.exitFragment.exitCarBmpInfo == null) {
			return;
		}
		String carnumber = activity.exitFragment.exitCarBmpInfo.getCarPlate();
		if (carnumber != null) {
			if (StringUtils.isPolice(carnumber)) {
				VoicePlayer.getInstance(activity).playVoice("�����������");
				isFree = false;
			} else {
				VoicePlayer.getInstance(activity).playVoice("�˳������");
			}
		}
	}

	public boolean isFree() {
		return isFree;
	}

	public void setFree(boolean isFree) {
		this.isFree = isFree;
	}

	public void setFocus() {
		btn_charge_finish.callOnClick();
	}

	/**
	 * ���ػ����
	 */
	private void localFree(AllOrder itemOrder) {
		Log.e(TAG, "���ػ����activity.getItemOrder():" + itemOrder);
		if (itemOrder != null) {
			String orderid = itemOrder.getId();
			String localid = itemOrder.getLocalid();
			String total = itemOrder.getTotal();
			String prepay = itemOrder.getPrepay();
			// ��ȥ�ۼӵ��շ�Ա���
			activity.addTollmanMoney(total, prepay, false);
			// loDBManager.updateOrderTotalLocalFree(orderid,localid);
			// ����������LED��ʾ����բ
			freeCar();
			showLedAndPole();
		}
		OrderListState.getInstance().setState(OrderListState.CLEAR_FINISH_STATE);
		activity.refreshListOrder();
		double cost = 0;
		try {
			cost = Double.parseDouble(tv_park_cost.getText().toString());
		} catch (Exception e) {
			e.printStackTrace();
			cost = 1;
		}
		if (cost > 0)
			activity.getChargeInfo();
		
	}
}
