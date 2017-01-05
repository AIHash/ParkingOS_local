package com.zld.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zld.application;
import com.zld.bean.AllOrder;
import com.zld.bean.AppInfo;
import com.zld.bean.AutoDepartureOrder;
import com.zld.bean.CurrentOrder;
import com.zld.bean.DepartureInfo;
import com.zld.bean.UploadImg;
import com.zld.db.SqliteManager;
import com.zld.lib.constant.Constant;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.util.BitmapUtil;
import com.zld.lib.util.ImageUitls;
import com.zld.lib.util.PollingUtils;
import com.zld.lib.util.TimeTypeUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

public class DownLoadService extends BaseService {

	private String uid;
	private String token;
	private SqliteManager sm;
	private ImageLoader imageLoader;
	private static final String TAG = "DownLoadService";

	// ����ʱ��С��1Сʱ ��������
	private static long FIVETIMESTAMP = 1*60*60*1000;// 60�� ���ڵĺ���
	
//	// �ж�����޸������Ƿ�С������ǰ������ɾ��
//	private static long FIVEDAYTAMP = 6*60*1000;
//	// ����ʱ��С��10���� ��������
//	private static long FIVETIMESTAMP = 1*10*60*1000;
	private String comid;
	private int PHOTOTYPE = 0;
	private ArrayList<String> orderidList;
	private ArrayList<String> sameList;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		token = AppInfo.getInstance().getToken();
		if (token == null) {
			PollingUtils.stopPollingService(this,DownLoadService.class,"com.zld.service.DownLoadImage_Service");
			stopSelf();
			return;
		}
		if(imageLoader == null){
			imageLoader = ((application)getApplication()).getImageLoader();
		}
		if(sm == null){
			sm = ((application)getApplication()).getSqliteManager(DownLoadService.this);
		}
		uid = AppInfo.getInstance().getUid();
		comid = AppInfo.getInstance().getComid();
//		getOrder();
//		fileRegularDelete();
	}

	/**	
	 * ��ȡ1500��������Ϣ �Ƚϱ������ݿ⣬û�е������ء��������ݿ��ľ�ɾ��
	 * page��sizeΪnull
	 */
//	private void getOrder(){
//		RequestParams params = new RequestParams();
//		params.setUrlHeader(Constant.requestUrl +Constant.GET_CURRORDER);
//		params.setUrlParams("comid", AppInfo.getInstance().getComid());
//		params.setUrlParams("page", "");
//		params.setUrlParams("size", "1000");
//		params.setUrlParams("through", 3);
//		String url = params.getRequstUrl();
//		Log.e(TAG, "��ȡ�������е�ǰ����url---------------->>" + url);
//		HttpManager.requestGET(this,url,this);
//	}

	private void deleteImage(UploadImg selectImage) {
		String imgpath = selectImage.getImghomepath();
		if(imgpath != null){
			ImageUitls.deleteImageFile(imgpath);
		}
	}

	/**
	 * ��ѯ��Ӧ������Ϣ��
	 * http://192.168.199.239/zld/cobp.do?action=queryorder&comid=1197&carnumber=%be%a9JA6036 
	 * @param carNumber
	 * @throws UnsupportedEncodingException 
	 */
	public void queryCarNumberOrder(String carNumber) throws UnsupportedEncodingException{
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.QUERY_ORDER);
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("carnumber", URLEncoder.encode(carNumber, "utf-8"));
		params.setUrlParams("through", 3);
		String url = params.getRequstUrl();
		Log.e(TAG, "���Ʋ�ѯ������URL---------------->>" + url);
		HttpManager.requestGET(this, url,this);	
	}

	/**
	 * �������ϻ�ȡ��Ӧ������ͼƬ��
	 */
	public void getCarPhoto(final DepartureInfo info) {
		if(info != null){
			String orderid = info.getId();
			if (info.getWidth() != null&&info.getHeight() != null&&
					info.getLefttop() != null &&info.getRightbottom() != null){
				RequestParams params = new RequestParams();
				params.setUrlHeader(Constant.requestUrl + Constant.DOWNLOAD_IMAGE);
				params.setUrlParams("comid", AppInfo.getInstance().getComid());
				params.setUrlParams("orderid", orderid);
				params.setUrlParams("type", 0);
				String uri = params.getRequstUrl();
				Log.e(TAG, "��Ƭ��uri��ַ��-->>"+uri);
				imageLoader.loadImage(uri, new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub
						Log.e(TAG, "--->>"+"Start");
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
						// TODO Auto-generated method stub
						Log.e(TAG, "--->>"+"Fail");
					}

					@SuppressLint("NewApi")
					@Override
					public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
						// TODO Auto-generated method stub
						Log.e(TAG, "--->>"+"Complete");
						if(info.getWidth() != null&&info.getHeight() != null
								&&info.getLefttop() != null&& info.getRightbottom() != null){
							if (!info.getWidth().equals("null") && !info.getHeight().equals("null") &&
									!info.getLefttop().equals("null") && !info.getRightbottom().equals("null")){
								Bitmap bitmap = BitmapUtil.zoomImg(arg2, 1280, 720);
								//����ͼƬ������ͼƬ��Ϣ
								ImageUitls.SaveImageInfo(sm,bitmap, uid, info.getCarnumber(),info.getId(), info.getLefttop(),
										info.getRightbottom(), Constant.HOME_PHOTOTYPE+"", info.getWidth(), info.getHeight());
							}
						}else{
							Log.e(TAG, "--->>"+"Complete--ͼƬ��Сδ֪");
						}
					}
					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub
						Log.e(TAG, "--->>"+"Cancelle");
					}
				});
			}else{
				Log.e(TAG, "--->>"+"Complete--ͼƬ��Ϣδ֪");
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("DownLoadImage_Service----:onDestroy");
	}
	
	@Override
	public boolean doSucess(String url, String object) {
		// TODO Auto-generated method stub
		Log.e(TAG, "doSucess---------------->>" + url);
		if (url.contains(Constant.GET_CURRORDER)){
			Log.e(TAG, "��ȡ������ϢΪ��"+Constant.GET_CURRORDER+"---------------->>" + object);
			doGetOrderResult(object);
		}else if(url.contains(Constant.QUERY_ORDER)){
			Log.e(TAG, "��ȡ���Ʋ�ѯ����Ϊ��"+Constant.QUERY_ORDER+"---------------->>" + object);
			doQueryOrderResult(object);
		}
		return false;
	}


	/**
	 * ��ȡ�������ж������
	 * @param object
	 */
	private void doGetOrderResult(String object) {
		// TODO Auto-generated method stub
		if(object.equals("-1")){
			return;
		}
		Gson gson = new Gson();
		CurrentOrder orders = gson.fromJson(object, CurrentOrder.class);
		ArrayList<AllOrder> info = orders.getInfo();
		int infosize = info.size();
		if (orders == null || infosize == 0) {
			return;
		}
		 Log.e(TAG, "�����������е�ǰ����Ϊ-->>"+orders.toString());
		ArrayList<String> smOrderidList = sm.selectAllOrderid();
		if(smOrderidList == null){
			return;
		}
		int smOrderidSize = smOrderidList.size();
		 Log.e(TAG, "���ݿ⵱ǰ��������Ϊ-->>"+smOrderidSize);
		// ���ж���Orderid�ļ���
		orderidList = new ArrayList<String>();
		// ���ݿ��������ж�����ͬOrderid�ļ���
		sameList = new ArrayList<String>();
		if(smOrderidSize >= 0){
			for(int i = 0;i < infosize;i++){
				String orderid  = info.get(i).getId();
				orderidList.add(orderid);
				for(int j = 0;j < smOrderidSize;j++){
					if(smOrderidList.get(j).equals(orderid)){
						sameList.add(smOrderidList.get(j));
					}
				}
			}
			 Log.e(TAG, "���ݿ���Ϣ��"+smOrderidList.toString());
		}
		 Log.e(TAG, "�����ϻ�ȡ���Ķ�����Ϣ:"+orderidList.toString());
		 Log.e(TAG, "��ͬ�Ķ�����Ϣ:"+sameList.toString());
		ArrayList<String> netDifList = orderidList;
		ArrayList<String> smDifList = smOrderidList;
		//�����ȡ�Ķ�����ȥ�������ݿ���ͬ�Ķ�����Ȼ���ȡ5�������ڵ�--����
		netDifList.removeAll(sameList);
		//���ݿ��ȡ�Ķ�����ȥ����������ͬ�Ķ�����Ȼ��ɾ��
		smDifList.removeAll(sameList);
		for(int j = 0;j < netDifList.size();j++){
			String orderid = netDifList.get(j);
			for(int i = 0;i < infosize;i++){
				AllOrder allOrder = info.get(i);
				if(allOrder.getId() == orderid){
					String btime = allOrder.getBtime();
					Long longTime = TimeTypeUtil.getLongTime(btime);
					long restTime = System.currentTimeMillis() - longTime;
					UploadImg selectImage = sm.selectImage(allOrder.getId());
					if(selectImage == null){
						//ʱ��С�������
						if (restTime < FIVETIMESTAMP) {
							try {
								queryCarNumberOrder(URLEncoder.encode(
										allOrder.getCarnumber(), "utf-8"));
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		for(int l = 0;l<smDifList.size();l++){
			for(int k = 0;k <smOrderidList.size();k++){
				if(smDifList.get(l) == smOrderidList.get(k)){
					UploadImg selectImage = sm.selectImage(smOrderidList.get(k));
					if(selectImage != null&&selectImage.getOrderid()!=null){
						//�˴���ֱ��ɾ��������Ϊ���ػ����ɵĶ�����orderid��������û�е�
						Log.e(TAG, "���ݿ����е�ͼƬ,���϶���û�е�ͼƬ��Ϣ��"+selectImage.toString());
						if(selectImage.getOrderid().length()<30){
							deleteImage(selectImage);
							sm.deleteData(smOrderidList.get(k));
						}
					}
				}
			}
		}
	}
	
	

	/**
	 *  �������ƺ�
	 */
	private void doQueryOrderResult(String object) {
		Gson gson = new Gson();
		AutoDepartureOrder orders = gson.fromJson(object, AutoDepartureOrder.class);
		if(orders != null){
			//��ȡ��Ӧ��ڶ���ͼƬ����ʾ��Ӧ����ͼƬ
			getCarPhoto(orders.getInfo().get(0));
		}
	}

	@Override
	public boolean doFailure(String url, String status) {
		// TODO Auto-generated method stub
		return false;
	}
}
