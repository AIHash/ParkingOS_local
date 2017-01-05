//package com.zld.lib.util;
//
//import java.util.List;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.content.Context;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.androidquery.AQuery;
//import com.androidquery.callback.AjaxCallback;

//import com.androidquery.callback.AjaxStatus;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.zld.bean.AppInfo;
//import com.zld.lib.constant.Constant;
//import com.zld.local.db.ComInfo_tb;
//import com.zld.local.db.LocalOrderDBManager;
//import com.zld.local.db.MonthCard_tb;
//import com.zld.local.db.Price_tb;
//
//public class MySynchronizeUitls {
//	private static final String TAG = "MySynchronizeUitls";
//	/**
//	 * ���ʷ�������÷�����ʱ�䡣
//	 * ��������ݿ���Ҫ�����߳������У���
//	 * @param context
//	 */
//	//parkoffline.do?action=synchroTime
//	public static void SynchronizeTime(final Context context) {
//		String url = Constant.requestUrl+"local.do?action=synchroTime";
//		Log.e(TAG, "����ͬ��������ʱ���url��"+url);
//		AQuery aq = new AQuery(context);
//		aq.ajax(url, String.class, new AjaxCallback<String>() {
//			@Override
//			public void callback(String url, String object, AjaxStatus status) {
//				super.callback(url, object, status);
//				if (!TextUtils.isEmpty(object)) {
//					long duration = status.getDuration();
//					Log.e(TAG, "���������ص�ʱ�䣺"+object+"��������ʱ���ǣ�"+duration);
//					Long differenceTime = TimeTypeUtil.getDifferenceTime(Long.parseLong(object)+duration);
//					SharedPreferencesUtils.setParam(context, "zld_config", "linetime", differenceTime);
//					Log.i(TAG, "ͬ��������ʱ��ɹ�---�����ֵΪ��"+differenceTime);
//				}
//			}
//		});
//	}
//
//	/**
//	 * ���������ͬ�������۸���ԣ�
//	 * ��������ݿ���Ҫ�����߳������У���
//	 * @param context
//	 */
//	public static void SynchronizePrice(final LocalOrderDBManager dao,final Context context){
//		String url = Constant.requestUrl+"local.do?action=synchroPrice&token="+AppInfo.getInstance().getToken();
//		Log.e(TAG, "����ͬ���۸��url��"+url);
//		AQuery aq = new AQuery(context);
//		aq.ajax(url, String.class, new AjaxCallback<String>() {
//			@Override
//			public void callback(String url, String object, AjaxStatus status) {
//				super.callback(url, object, status);
//				if (!TextUtils.isEmpty(object)) {
//					Log.e(TAG, "���յļ۸��"+object);
//					JSONObject jsoninfo = null;
//					String priceinfo = null;
//					String cominfo = null;
//					try {
//						jsoninfo = new JSONObject(object);
//						priceinfo = jsoninfo.getString("price_tb");
//						cominfo = jsoninfo.getString("com_info_tb");
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//					if (! TextUtils.isEmpty(priceinfo) && ! TextUtils.isEmpty(cominfo) ) {
//						Gson gson = new Gson();
//						List<Price_tb> list  = gson.fromJson(priceinfo, new TypeToken<List<Price_tb>>() {}.getType());
//						Log.e(TAG, "�����ļ۸��"+list.toString());
//						List<ComInfo_tb> cominfos = gson.fromJson(cominfo, new TypeToken<List<ComInfo_tb>>() {}.getType());
//						Log.e(TAG, "������cominfo��"+cominfos.toString());
//						//��ռ۸��
//						dao.clearPrice_tb();
//						dao.clearCominfo_tb();
//						dao.addMorePrice(list);
//						if (cominfos != null && cominfos.size() != 0) {
//							dao.addComInfoTb(cominfos.get(0));
//						}
//						Log.e(TAG, "ͬ���۸����ϣ�---------------!!!");
//					}
//				}
//			}
//		});
//	}
//	
//	/**
//	 * �����¿����ƺż�����ʱ��
//	 * @param context
//	 */
//	public static void SynchronizeMonthCard(final LocalOrderDBManager dao,final Context context){
//		String url = Constant.requestUrl+Constant.MONTH_CARD_CARNUMBER+"&token="+AppInfo.getInstance().getToken();
//		Log.e(TAG, "����ͬ���¿���url��"+url);
//		AQuery aq = new AQuery(context);
//		aq.ajax(url, String.class, new AjaxCallback<String>() {
//			@Override
//			public void callback(String url, String object, AjaxStatus status) {
//				super.callback(url, object, status);
//				if (!TextUtils.isEmpty(object)) {
//						Gson gson = new Gson();
//						List<MonthCard_tb> list  = gson.fromJson(object, new TypeToken<List<MonthCard_tb>>() {}.getType());
//						/*������¿���*/
//						dao.clearMonthCard_tb();
//						if (list != null && list.size() != 0) {
//							for(int i=0;i<list.size();i++){
//							dao.addMonthCardInfoTb(list.get(i));
//						}
//						Log.e(TAG, "ͬ���¿�����ϣ�---------------!!!");
//					}
//				}
//			}
//		});
//	}
//}
