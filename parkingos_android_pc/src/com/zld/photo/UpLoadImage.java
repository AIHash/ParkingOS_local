package com.zld.photo;

import java.io.InputStream;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.zld.bean.AppInfo;
import com.zld.lib.constant.Constant;
import com.zld.lib.util.StringUtils;
import com.zld.lib.util.UploadUtil;
import com.zld.ui.ZldNewActivity;


public class UpLoadImage extends Service {

	private String comid;
	private Toast mToast;
	private int photoType;
	private Handler mHandler;
	private static final String TAG = "UpLoadImage";
	private Intent intent;
	private Bundle mBundle;

	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}
	public void setComid(String comid) {
		this.comid = comid;
	}
	public void setPhotoType(int photoType) {
		this.photoType = photoType;
	}
	/**
	 * �ϴ�ͼƬ
	 */
	public void upload(final InputStream bitmapToInputStream,final String orderid,final String lefttop,
			final String rightbottom,final String width,final String height,final String carNumber) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				String url = Constant.requestUrl + "carpicsup.do?action=uploadpic&comid="
						+ comid + "&orderid=" + orderid + "&lefttop=" + lefttop
						+ "&rightbottom=" + rightbottom + "&type=" + photoType
						+ "&width=" + width + "&height=" + height;
				String result = UploadUtil.uploadFile(bitmapToInputStream, url);
				Log.e(TAG, "�ϴ���Ƭ��url��-->>" + url+" ���ƺţ�"+carNumber);
				Log.e(TAG, "�ϴ���Ƭ�ķ��ؽ����-->>" + result);
				Message msg = new Message();
				if (result != null) {
					if (result.equals("1")) {
						msg.what = Constant.SHOWPIC_ONRIGHT_MSG;
						msg.arg1 = photoType;
						msg.obj = orderid;
						Log.e(TAG, "��Ƭ�ϴ��ɹ���");
					} else {
						msg.what = Constant.PICUPLOAD_FILE;
						msg.obj = orderid;
						Log.e(TAG, "��Ƭ�ϴ�ʧ�ܣ�");
					}
				} else {
					msg.what = Constant.PICUPLOAD_FILE;
					msg.obj = orderid;
					Log.e(TAG, "��Ƭ�ϴ�ʧ�ܣ�");
				}
				mHandler.sendMessage(msg);
			}
		}.start();
	}
	/**
	 * �ϴ�ͼƬ
	 */
	public void uploadPoleImage(final InputStream bitmapToInputStream,final ZldNewActivity activityfinal, final boolean isIn) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				String poleID = "";
				if (isIn) {
					poleID = activityfinal.getPoleIDInList().get(0);
					activityfinal.getPoleIDInList().remove(0);
				}else {
					poleID = activityfinal.getPoleIDOutList().get(0);
					activityfinal.getPoleIDOutList().remove(0);
				}
				String url = Constant.requestUrl + "collectorrequest.do?action=liftroduppic&"
						+ "token=" + AppInfo.getInstance().getToken() + "&lrid=" + poleID;
				String result = UploadUtil.uploadFile(bitmapToInputStream, url);
				Log.e(TAG, "�ϴ���Ƭ�ķ��ؽ����-->>" + result);
				Map<String, String> resultMap = StringUtils.getMapForJson(result);
				Message msg = new Message();
				if (resultMap != null) {
					if (resultMap.get("result").equals("1")) {
						msg.what = Constant.UPPOLE_IMAGR_SUCCESS;
//						sendKey(ZldNewActivity.TOAST, "̧�˼�¼�ϴ��ɹ�");
//						activity.showToast("̧�˼�¼�ϴ��ɹ�");
					} else {
						msg.what = Constant.UPPOLE_IMAGR_ERROR;
//						sendKey(ZldNewActivity.TOAST, "̧�˼�¼�ϴ�ʧ��");
//						activity.showToast("̧�˼�¼�ϴ�ʧ��");
					}
				} 
				mHandler.sendMessage(msg);
			}
		}.start();
	}
	
	/**
	 * 
	 * ����˵����ˢ�½���
	 * ����:	2015��3��14��
	 */
	public void sendKey(int receiver_key,String toast) {
		if(intent == null){
			intent = new Intent("android.intent.action.uploadimage");
		}
		if(mBundle == null){
			mBundle = new Bundle();
		}
		mBundle.putInt("receiver_key", receiver_key);
		mBundle.putString("toast", toast);
		intent.putExtras(mBundle);
		sendBroadcast(intent);//Activity����ʾͼƬ
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}