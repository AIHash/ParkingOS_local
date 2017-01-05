//package com.zld.local.db;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.json.JSONArray;
//
//import android.R.string;
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteStatement;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.google.gson.JsonObject;
//import com.zld.bean.AllOrder;
//import com.zld.bean.AppInfo;
//import com.zld.bean.CarNumberOrder;
//import com.zld.lib.util.TimeTypeUtil;
//import com.zld.local.bean.LocalCurrentOrder;
//import com.zld.local.bean.Relation;
//
//public class LocalOrderDBManager {
//
//	private static final String TAG = "LocalOrderDBManager";
//	private SQLiteDatabase db ;
//	private static LocalDBHelper dbhelper;
//	private static String DATABASE_TABLE = "order_tb";
//
//	public LocalOrderDBManager(Context context) {
//		dbhelper = new LocalDBHelper(context);
//	}
//
//	public void close(){
//		if (db != null) {
//			db.close();
//		}
//	}
//
//	private void cursorclose(Cursor cursor){
//		if(cursor != null){
//			cursor.close();
//		}
//	}
//
//	/**
//	 * ����com_info_tb���ݣ�
//	 */
//	public void addComInfoTb(ComInfo_tb info){
//		db = dbhelper.getWritableDatabase();
//		if (db != null) {
//			ContentValues cv = new ContentValues();
//			cv.put("id", info.id);
//			cv.put("minprice_unit", info.minprice_unit);
//			db.insert("com_info_tb", null, cv);
//		} 
//	}
//
//	/**
//	 * ����com_info_tb���ݣ�
//	 */
//	public void addMonthCardInfoTb(MonthCard_tb info){
//		Log.e("SynchronizeMonthCard","�����info���ݣ�"+info.toString());
//		db = dbhelper.getWritableDatabase();
//		if (db != null) {
//			ContentValues cv = new ContentValues();
//			cv.put("uin",info.getUin());
//			cv.put("car_number", info.getCar_number());
//			cv.put("e_time", info.getE_time());
//			db.insert("monthcard_tb", null, cv);
//		} 
//	}
//
//	/**
//	 * �ж϶������Ƿ�Ϊ��
//	 * @return
//	 */
//	public Boolean isOrdertbEmpty(){
//		Cursor cursor = null;
//		try {
//			db = dbhelper.getWritableDatabase();
//			cursor = db.rawQuery("select * from " + DATABASE_TABLE,null);
//			if(cursor!=null&&cursor.getCount()==0){
//				cursorclose(cursor);
//				return true;
//			}
//		} catch (Exception e) {
//			// �����쳣
//		} finally {
//			cursorclose(cursor);
//		}
//		return false;
//	}
//
//	/**
//	 * ��Ӷ�����¼��
//	 * @param order
//	 */
//	public void addOrder(Order_tb order) {
//		db = dbhelper.getWritableDatabase();
//		if (db != null) {
//			ContentValues cv = new ContentValues();
//			cv.put("id", order.id);
//			cv.put("localid", order.id);
//			cv.put("create_time", order.create_time);
//			cv.put("comid", order.comid);
//			cv.put("uin", order.uin);
//			cv.put("total", order.total);
//			cv.put("prepay", order.prepay);
//			cv.put("state", order.state);
//			cv.put("end_time", order.end_time);
//			cv.put("auto_pay", order.auto_pay);
//			cv.put("pay_type", order.pay_type);
//			cv.put("nfc_uuid", order.nfc_uuid);
//			cv.put("c_type", order.c_type);
//			cv.put("uid", order.uid);
//			cv.put("car_number", order.car_number);
//			cv.put("imei", order.imei);
//			cv.put("pid", order.pid);
//			cv.put("car_type", order.car_type);
//			cv.put("pre_state", order.pre_state);
//			cv.put("in_passid", order.in_passid);
//			cv.put("out_passid", order.out_passid);
//			db.insert(DATABASE_TABLE, null, cv);
//		} 
//	}
//
//	/**
//	 * ������Ӷ�����
//	 * @param list
//	 * @return
//	 */
//	public boolean addMoreOrder(List<Order_tb> list) {
//		if (null == list || list.size() <= 0) {
//			return false;
//		}
//		db = dbhelper.getWritableDatabase();
//		try {
//			String sql = "insert into " + DATABASE_TABLE 
//					+ "(id" + ","// �������
//					+ "localid" + ","//���ض������
//					+ "create_time" + ","// ����ʱ��
//					+ "comid" + ","// �������
//					+ "uin" + ","// �����˺�
//					+ "total" + ","// �۸�
//					+ "prepay"+ ","//Ԥ�����
//					+ "state" + ","// ״̬
//					+ "end_time" + ","// ����ʱ��
//					+ "auto_pay" + ","// -- �Զ����㣬0����1����
//					+ "pay_type" + ","// -- 0:�ʻ�֧��,1:�ֽ�֧��,2:�ֻ�֧�� 3�¿�
//					+ "nfc_uuid " + ","//
//					+ "c_type" + "," // -- 0:NFC,1:IBeacon,2:���� 3ͨ������ 4ֱ�� 5�¿��û�
//					+ "uid" + ","// -- �շ�Ա�ʺ�
//					+ "car_number " + "," // -- ����
//					+ "imei" + ","// -- �ֻ�����
//					+ "pid" + ","// �Ʒѷ�ʽ��0��ʱ(0.5/15����)��1���Σ�12Сʱ��10Ԫ,ǰ1/30min����ÿСʱ1Ԫ��
//					+ "car_type" + ","// -- 0��ͨ�ã�1��С����2����
//					+ "pre_state" + ","// -- Ԥ֧��״̬ 0 �ޣ�1Ԥ֧���У�2�ȴ�����֧�����
//					+ "in_passid" + "," // -- ����ͨ��id
//					+ "out_passid"// -- ����ͨ��id
//					+ ") " + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//			SQLiteStatement stat = db.compileStatement(sql);
//			db.beginTransaction();
//			for (Order_tb Order_tbinfo : list) {
//				stat.bindString(1, Order_tbinfo.id);
//				stat.bindString(2, Order_tbinfo.id);
//				stat.bindString(3, Order_tbinfo.create_time);
//				stat.bindString(4, Order_tbinfo.comid);
//				stat.bindString(5, Order_tbinfo.uin);
//				stat.bindString(6, Order_tbinfo.total);
//				stat.bindString(7, "0.0");
//				stat.bindString(8, Order_tbinfo.state);
//				stat.bindString(9, Order_tbinfo.end_time);
//				stat.bindString(10, Order_tbinfo.auto_pay);
//				stat.bindString(11, Order_tbinfo.pay_type);
//				stat.bindString(12, Order_tbinfo.nfc_uuid);
//				stat.bindString(13, Order_tbinfo.c_type);
//				stat.bindString(14, Order_tbinfo.uid);
//				stat.bindString(15, Order_tbinfo.car_number);
//				stat.bindString(16, Order_tbinfo.imei);
//				stat.bindString(17, Order_tbinfo.pid);
//				stat.bindString(18, Order_tbinfo.car_type);
//				stat.bindString(19, Order_tbinfo.pre_state);
//				stat.bindString(20, Order_tbinfo.in_passid);
//				stat.bindString(21, Order_tbinfo.out_passid);
//				long result = stat.executeInsert();
//				if (result < 0) {
//					return false;
//				}
//			}
//			db.setTransactionSuccessful();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		} finally {
//			db.endTransaction();
//		}
//		return true;
//	}
//
//	/**
//	 * ɾ��һ��������¼��
//	 * @param type ��ѯ���� 1 nfc����,2���ƺ�
//	 * @param value
//	 */
//	public void deleteOrder(String type, String value) {
//		if (TextUtils.isEmpty(type) || TextUtils.isEmpty(value)) {
//			return;
//		}
//		db = dbhelper.getWritableDatabase();
//		String deletesql;
//		if ("1".equals(type)) {
//			deletesql = "delete from " + DATABASE_TABLE + " where nfc_uuid=" + "'"+value+"'";
//		} else {
//			deletesql = "delete from " + DATABASE_TABLE + " where car_number=" + "'"+value+"'";
//		}
//		db.execSQL(deletesql);
//	}
//
//	/**
//	 * ɾ��һ��������¼by������
//	 * @param list
//	 */
//	public void deleteMoreOrder(String[] list){
//		db = dbhelper.getWritableDatabase();
//		try {
//			db.beginTransaction();//��ʼ����
//			for (int i = 0; i < list.length; i++) {
//				String sql = "delete from " + DATABASE_TABLE + " where id ='" + list[i]+"'";
//				Log.e(TAG, "ɾ���������ĳ�����ݣ�"+sql);
//				db.execSQL(sql);
//			}
//			db.setTransactionSuccessful();//���ô˷�������ִ�е�endTransaction() ʱ�ύ��ǰ������������ô˷�����ع�����
//		} finally {
//			db.endTransaction();//������ı�־�������ύ���񣬻��ǻع�����
//		} 
//	}
//
//	/**
//	 * ɾ���ѽ���״̬�Ķ�����
//	 */
//	public void deleteCashOrder(){
//		db = dbhelper.getWritableDatabase();
//		try {
//			db.beginTransaction();
//			db.execSQL("delete from " + DATABASE_TABLE + " where state = 1");
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//		} 
//	}
//
//	/**
//	 * ���ݳ��ƺŲ�ѯ����CType
//	 * @param type ��ѯ���� 1���ƺ� ,2������ţ�
//	 * @param value
//	 */
//	public int queryOrderCtype(String carNumber){
//		db = dbhelper.getWritableDatabase();
//		String create_timesql = null;
//		Cursor rawQuery = null;
//		int Ctype = -1;
//		create_timesql = "select c_type from "+DATABASE_TABLE+" where car_number = ? and state = 0";
//		
//		try {
//			db.beginTransaction();
//			rawQuery = db.rawQuery(create_timesql, new String[]{carNumber} );
//			if (rawQuery.moveToFirst()) {
//				Ctype = rawQuery.getInt(rawQuery.getColumnIndex("c_type"));
//			}
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//			cursorclose(rawQuery);
//		} 
//		return Ctype;
//	}
//
//	/**
//	 * ���ݳ��ƺŲ�ѯ����ʱ��
//	 * @param type ��ѯ���� 1���ƺ� ,2������ţ�
//	 * @param value
//	 */
//	public Long queryOrderTime(String type, String value){
//		db = dbhelper.getWritableDatabase();
//		String create_timesql = null;
//		Long create_time = null;
//		Cursor rawQuery = null;
//		if ("1".equals(type)) {
//			create_timesql = "select create_time from "+DATABASE_TABLE+" where car_number = ? and state = 0";
//		} else if ("2".equals(type)) {
//			create_timesql = "select create_time from "+DATABASE_TABLE+" where id = ? and state = 0";
//		}
//		try {
//			db.beginTransaction();
//			rawQuery = db.rawQuery(create_timesql, new String[]{value} );
//			if (rawQuery.moveToFirst()) {
//				create_time = rawQuery.getLong(rawQuery.getColumnIndex("create_time"));
//			}
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//			cursorclose(rawQuery);
//		} 
//		return create_time;
//	}
//
//	/**
//	 * ���ݳ��ƺŲ�ѯ�Ƿ���δ����Ķ���id
//	 * @param carNumber ��ѯ���ƺ� 
//	 */
//	public String queryOrderIdByCarNumber(String carNumber){
//		db = dbhelper.getWritableDatabase();
//		String create_timesql = null;
//		String id = null;
//		Cursor rawQuery = null;
//		create_timesql = "select id from "+DATABASE_TABLE+" where car_number = ? and state = 0";
//		try {
//			db.beginTransaction();
//			rawQuery = db.rawQuery(create_timesql, new String[]{carNumber} );
//			if (rawQuery.moveToFirst()) {
//				id = rawQuery.getString(rawQuery.getColumnIndex("id"));
//			}
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//			cursorclose(rawQuery);
//		} 
//		return id;
//	}
//	public String queryMonthCardUINByCarNumber(String carNumber){
//		
//		db = dbhelper.getWritableDatabase();
//		String sql = null;
//		Cursor rawQuery = null;
//		String currentTimeMillis = ""+System.currentTimeMillis()/1000;
//		sql = "select * from monthcard_tb where car_number = ? and e_time > ?";
//		try {
//			db.beginTransaction();
//			rawQuery = db.rawQuery(sql, new String[]{carNumber,currentTimeMillis} );
//			if (rawQuery.moveToFirst()) {
//				
//				String uin = rawQuery.getString(rawQuery.getColumnIndex("uin"));
//				return uin;
//			}
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//			cursorclose(rawQuery);
//		} 
//		return null;
//	}
//	/**
//	 * ���ݳ��ƺŲ�ѯ�Ƿ�Ϊ�¿��û�
//	 * @param carNumber ��ѯ���ƺ� 
//	 */
//	public boolean queryMonthCardUserByCarNumber(String carNumber){
//		db = dbhelper.getWritableDatabase();
//		String sql = null;
//		String car_number = null;
//		Cursor rawQuery = null;
//		String currentTimeMillis = ""+System.currentTimeMillis()/1000;
//		sql = "select * from monthcard_tb where car_number = ? and e_time > ?";
//		try {
//			db.beginTransaction();
//			rawQuery = db.rawQuery(sql, new String[]{carNumber,currentTimeMillis} );
//			if (rawQuery.moveToFirst()) {
//				car_number = rawQuery.getString(rawQuery.getColumnIndex("car_number"));
//				
//				if(car_number !=null){
//					
//					String uin = rawQuery.getString(rawQuery.getColumnIndex("uin"));
//					
//					sql = "select * from " +DATABASE_TABLE+ " where uin = ? and state = 0";
//					{
//						db.setTransactionSuccessful();
//						db.endTransaction();
//						db.beginTransaction();
//						Cursor rawQuery1 = db.rawQuery(sql, new String[]{uin});
//						
//						if (rawQuery1.moveToFirst()) {
//							int count = rawQuery1.getCount();
//						if (count > 1)
//							return false;
//						
//						db.setTransactionSuccessful();
//	
//						}
//					}
//					
//					cursorclose(rawQuery);
//
//					return true;
//				}
//			}
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//			cursorclose(rawQuery);
//		} 
//		return false;
//	}
//
//	/**
//	 * ���ݳ��ƺŲ�ѯδ���㶩����Ԥ�����
//	 * @param carNumber ��ѯ���ƺ� 
//	 */
//	public String queryPrepayTotalByCarNumber(String carNumber){
//		db = dbhelper.getWritableDatabase();
//		String sql = null;
//		String prepay_total = null;
//		Cursor rawQuery = null;
//		sql = "select prepay from "+DATABASE_TABLE+" where car_number = ?  and state = 0";
//		try {
//			db.beginTransaction();
//			rawQuery = db.rawQuery(sql, new String[]{carNumber} );
//			if (rawQuery.moveToFirst()) {
//				prepay_total = rawQuery.getString(rawQuery.getColumnIndex("prepay"));
//			}
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//			cursorclose(rawQuery);
//		} 
//		return prepay_total;
//	}
//
//	/*	*//**
//	 * �޸Ķ����¿��û���ʾΪ1
//	 * @param carNumber ��ѯ���ƺ� 
//	 *//*
//	public void updateMonthCardUserByCarNumber(String carNumber){
//		db = dbhelper.getWritableDatabase();
//		String sql = null;
//		sql = "update "+DATABASE_TABLE+" set monthcard = 1 where car_number = ?";
//		try {
//			db.beginTransaction();
//			db.execSQL(sql, new String[]{carNumber} );
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//		} 
//	}*/
//
//	/**
//	 * 
//	 * ��ȡ���ص�ǰ�����б�CarNumberOrder
//	 * @param type	����״̬
//	 * @param str orderid
//	 * @param localid 
//	 * @return
//	 */
//	public CarNumberOrder queryLocalCarNumberOrderDetailsBycarNumber(int type,String str,String localid){
//		CarNumberOrder order = null;
//		db = dbhelper.getWritableDatabase();
//		Cursor cursor = null;
//		String sql = "";
//		if(localid == null){
//			sql = "select * from "+DATABASE_TABLE+" where state = ? and id = ?";
//			Log.e(TAG,"type:"+type+" str:"+str+" localid:"+localid);
//			cursor = db.rawQuery(sql, new String[]{""+type,str});
//		}else{
//			sql = "select * from "+DATABASE_TABLE+" where state = ? and (id = ? or localid = ?)";
//			cursor = db.rawQuery(sql, new String[]{""+type,str,localid});
//		}
//		if(cursor.moveToFirst()){
//			order = new CarNumberOrder();
//			order.setOrderid(cursor.getString(cursor.getColumnIndex("id")));
//			order.setLocalid(cursor.getString(cursor.getColumnIndex("localid")));
//			order.setTotal(cursor.getString(cursor.getColumnIndex("total")));
//			String btime = cursor.getString(cursor.getColumnIndex("create_time"));
//			if(type == 0){
//				order.setDuration("��ͣ"+TimeTypeUtil.getTimeString(Long.parseLong(btime),System.currentTimeMillis()/1000));
//			}
//			order.setBtime(btime);
//			String etime = cursor.getString(cursor.getColumnIndex("end_time"));
//			order.setEtime(etime);
//			if(btime!=null&&etime!=null&&!btime.equals("null")&&!etime.equals("null")&&type==1){
//				order.setDuration(TimeTypeUtil.getTimeString(Long.parseLong(btime), Long.parseLong(etime)));
//			}
//			order.setCarnumber(cursor.getString(cursor.getColumnIndex("car_number")));
//			order.setUin(cursor.getString(cursor.getColumnIndex("uin")));
//			order.setPrepay(cursor.getString(cursor.getColumnIndex("prepay")));
//			order.setCar_type(cursor.getString(cursor.getColumnIndex("car_type")));
//			cursor.moveToNext();
//		}
//		cursorclose(cursor);
//		return order;
//	}
//
//	/**
//	 * ��ȡ���ص�ǰ�����б�AllOrder
//	 * @param type	0����� 1��orderid
//	 * @param str 
//	 * @return
//	 */
//	public ArrayList<AllOrder> queryLocalAllOrderBycarNumber(int type,String str,String localid){
//		AllOrder order = null;
//		db = dbhelper.getWritableDatabase();
//		ArrayList<AllOrder> allOrderList = new ArrayList<AllOrder>();
//		Cursor cursor = null;
//		if(type == 0){
//			cursor = db.rawQuery("select * from "+DATABASE_TABLE+" where car_number = ? and state = 0", new String[]{str});
//		}else{
//			cursor = db.rawQuery("select * from "+DATABASE_TABLE+" where state = 0 and (id = ? or localid = ?)", new String[]{str,localid});
//		}
//
//		if(cursor.moveToFirst()){
//			order = new AllOrder();
//			order.setId(cursor.getString(cursor.getColumnIndex("id")));
//			order.setLocalid(cursor.getString(cursor.getColumnIndex("localid")));
//			order.setTotal(cursor.getString(cursor.getColumnIndex("total")));
//			String btime = cursor.getString(cursor.getColumnIndex("create_time"));
//			order.setDuration("��ͣ"+TimeTypeUtil.getTimeString(Long.parseLong(btime),System.currentTimeMillis()/1000));
//			order.setBtime(TimeTypeUtil.getStringTime(Long.parseLong(btime)*1000));
//			order.setCarnumber(cursor.getString(cursor.getColumnIndex("car_number")));
//			order.setUin(cursor.getString(cursor.getColumnIndex("uin")));
//			order.setPrepay(cursor.getString(cursor.getColumnIndex("prepay")));
//			order.setCar_type(cursor.getString(cursor.getColumnIndex("car_type")));
//			allOrderList.add(order);
//			cursor.moveToNext();
//		}
//		cursorclose(cursor);
//		return allOrderList;
//	}
//
//	/**
//	 * ģ����ȡ���ص�ǰ�����б�AllOrder
//	 * @param type	0����� 1��orderid
//	 * @param str 
//	 * @param localid 
//	 * @return
//	 */
//	public ArrayList<AllOrder> queryLocalLikeAllOrderBycarNumber(int type,String str,String localid){
//		AllOrder order = null;
//		db = dbhelper.getWritableDatabase();
//		ArrayList<AllOrder> allOrderList = new ArrayList<AllOrder>();
//		Cursor cursor = null;
//		String sql = "";
//		if(type == 0){
//			str = "'%"+str+"%'";
//			Log.e(TAG,"�������ַ���"+str);
//			sql = "select * from "+DATABASE_TABLE+" where car_number like "+str+" and state = 0";
//			cursor = db.rawQuery(sql,null);
//		}else{
//			cursor = db.rawQuery("select * from "+DATABASE_TABLE+" where state = 0 and (id = ? or localid = ?)", new String[]{str,localid});
//		}
//
//		if(cursor.moveToFirst()){
//			for (int i = 0; i < cursor.getCount(); i++) {
//				order = new AllOrder();
//				order.setId(cursor.getString(cursor.getColumnIndex("id")));
//				order.setLocalid(cursor.getString(cursor.getColumnIndex("localid")));
//				order.setTotal(cursor.getString(cursor.getColumnIndex("total")));
//				String btime = cursor.getString(cursor.getColumnIndex("create_time"));
//				order.setDuration("��ͣ"+TimeTypeUtil.getTimeString(Long.parseLong(btime),System.currentTimeMillis()/1000));
//				order.setBtime(TimeTypeUtil.getStringTime(Long.parseLong(btime)*1000));
//				order.setCarnumber(cursor.getString(cursor.getColumnIndex("car_number")));
//				order.setUin(cursor.getString(cursor.getColumnIndex("uin")));
//				order.setPrepay(cursor.getString(cursor.getColumnIndex("prepay")));
//				order.setCar_type(cursor.getString(cursor.getColumnIndex("car_type")));
//				allOrderList.add(order);
//				cursor.moveToNext();
//			}
//		}
//		cursorclose(cursor);
//		return allOrderList;
//	}
//
//	/**
//	 * �޸ĳ��ƺ�
//	 * @param orderid
//	 * @param carPlate
//	 */
//	public void updateOrderCarplateLocalCash(String orderid,String carPlate,String localid){
//		db = dbhelper.getWritableDatabase();
//		try {
//			db.beginTransaction();
//			db.execSQL("update "+DATABASE_TABLE+" set car_number = ? where state = 0 and (id = ? or localid = ?)",new String[]{carPlate,orderid,localid});
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//		} 
//	}
//
//	/**
//	 * ��Ѽ۸�
//	 * @param orderid
//	 * @param carPlate
//	 */
//	public void updateOrderTotalLocalFree(String orderid,String localid){
//		db = dbhelper.getWritableDatabase();
//		try {
//			db.beginTransaction();
//			if(localid == null){
//				db.execSQL("update "+DATABASE_TABLE+" set state = 1 , pay_type = 8 where id = ?",new String[]{orderid});				
//			}else{
//				db.execSQL("update "+DATABASE_TABLE+" set state = 1 , pay_type = 8 where id = ? or localid = ?",new String[]{orderid,localid});
//			}
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//		} 
//	}
//
//	/**
//	 * �Ƿ�δ����Ĳ�ѯ��Ӧid�Ķ���
//	 * @param orderid
//	 */
//	public boolean selectOrderIsCash(String id,String localid){
//		boolean result = false;
//		db = dbhelper.getWritableDatabase();
//		Cursor cursor = null;
//		try {
//			db.beginTransaction();
//			Log.e(TAG,"����δ����Ĳ�ѯ��Ӧid�Ķ���"+id+"==="+localid);
//			if(localid == null){
//				cursor = db.rawQuery("select * from "+DATABASE_TABLE+" where state = 0 and id = ?", new String[]{id});
//			}else{
//				cursor = db.rawQuery("select * from "+DATABASE_TABLE+" where state = 0 and (id = ? or localid = ?)", new String[]{id,localid});
//			}
//			if (cursor.moveToFirst()) {
//				Log.e(TAG,"����δ����Ĳ�ѯ��Ӧid�Ķ���");
//				result = true;
//			}
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//			cursorclose(cursor);
//		}
//		return result; 
//	}
//
//
//	/**
//	 * 15��ǰ�Ķ���
//	 */
//	public boolean selectOrder(String time){
//		db = dbhelper.getWritableDatabase();
//		Cursor cursor = null;
//		try {
//			db.beginTransaction();
//			if(time != null){
//				cursor = db.rawQuery("select * from "+DATABASE_TABLE+" where state = 0 and create_time < ?", new String[]{time});
//			}
//			if (cursor.moveToFirst()) {
//				Log.e(TAG,"����15��ǰ�Ķ���");
//				return true;
//			}
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//			cursorclose(cursor);
//		}
//		return false; 
//	}
//
//	/**
//	 * ���ؽ��㶩��
//	 * @param orderid
//	 * @param total
//	 * @param end_time
//	 * @param out_passid
//	 */
//	public void updateOrderLocalCash(String id,String localid,String pay_type,
//			String c_type,String uid,String total,String end_time,String out_passid){
//		db = dbhelper.getWritableDatabase();
//		try {
//			db.beginTransaction();
//			if(localid == null){
//				db.execSQL("update "+DATABASE_TABLE+" set total = ?,state = 1,pay_type = ?,c_type = ?,uid = ?,"
//						+ "end_time = ?,out_passid = ? where id = ?",new String[]{total,pay_type,c_type,uid,end_time,out_passid,id});
//			}else{
//				db.execSQL("update "+DATABASE_TABLE+" set total = ?,state = 1,pay_type = ?,c_type = ?,uid = ?,"
//						+ "end_time = ?,out_passid = ? where id = ? or localid = ?",new String[]{total,pay_type,c_type,uid,end_time,out_passid,id,localid});
//			}
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//		} 
//	}
//
//	/**
//	 * ��ѯ���ط�����δ����ĵ�ǰ�����Ķ����ţ�
//	 * @return
//	 */
//	public StringBuffer getcurrOrderIds(){
//		StringBuffer ids = new StringBuffer();
//		db = dbhelper.getWritableDatabase();
//		Cursor idcursor = null;
//		try {
//			db.beginTransaction();
//			idcursor = db.rawQuery("select id from "+DATABASE_TABLE+" where state = 0 and length(id) < 30",null);
//			if (idcursor.moveToFirst()) {
//				for (int i = 0; i < idcursor.getCount(); i++) {
//					if (i == idcursor.getCount()-1 ) {
//						ids.append(idcursor.getString(idcursor.getColumnIndex("id")));
//					}else {
//						ids.append(idcursor.getString(idcursor.getColumnIndex("id"))+",");
//					}
//					idcursor.moveToNext();
//				}
//			}
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//			cursorclose(idcursor);
//		} 
//		return ids;
//	}
//
//	/**
//	 * ��ѯ���ض����������Ķ����� 
//	 * �ѽ���������ɵ�
//	 * @return
//	 */
//	@SuppressWarnings("resource")
//	public JSONArray getUpdateOrders(){
//		JSONArray jorders = new JSONArray();
//		db = dbhelper.getWritableDatabase();
//		Cursor cursor = null;
//		Cursor newcursor = null;
//		try {
//			db.beginTransaction();//��ʼ����
//			cursor = db.rawQuery("select * from "+DATABASE_TABLE+" where state = ? order by create_time desc limit 0,10",new String[]{"1"});
//			if (cursor.moveToFirst()) {
//				for (int i = 0; i < cursor.getCount(); i++) {
//					JsonObject jorder = new JsonObject();
//					jorder.addProperty("id", cursor.getString(cursor.getColumnIndex("id")));
//					jorder.addProperty("localid", cursor.getString(cursor.getColumnIndex("localid")));
//					jorder.addProperty("create_time", cursor.getString(cursor.getColumnIndex("create_time")));
//					jorder.addProperty("comid", cursor.getString(cursor.getColumnIndex("comid")));
//					jorder.addProperty("uin", cursor.getString(cursor.getColumnIndex("uin")));
//					jorder.addProperty("total", cursor.getString(cursor.getColumnIndex("total")));
//					jorder.addProperty("prepay", cursor.getString(cursor.getColumnIndex("prepay")));
//					jorder.addProperty("state", cursor.getString(cursor.getColumnIndex("state")));
//					jorder.addProperty("end_time", cursor.getString(cursor.getColumnIndex("end_time")));
//					jorder.addProperty("auto_pay", cursor.getString(cursor.getColumnIndex("auto_pay")));
//					jorder.addProperty("pay_type", cursor.getString(cursor.getColumnIndex("pay_type")));
//					jorder.addProperty("nfc_uuid", cursor.getString(cursor.getColumnIndex("nfc_uuid")));
//					jorder.addProperty("c_type", cursor.getString(cursor.getColumnIndex("c_type")));
//					String uid = cursor.getString(cursor.getColumnIndex("uid"));
//					if(uid == null){
//						uid = AppInfo.getInstance().getUid();
//					}
//					jorder.addProperty("uid", uid);
//					//jorder.addProperty("car_number", cursor.getString(cursor.getColumnIndex("car_number")));
//					jorder.addProperty("car_number", 
//							URLEncoder.encode(cursor.getString(cursor.getColumnIndex("car_number")),"utf-8"));
//					jorder.addProperty("imei", cursor.getString(cursor.getColumnIndex("imei")));
//					jorder.addProperty("pid", cursor.getString(cursor.getColumnIndex("pid")));
//					jorder.addProperty("car_type", cursor.getString(cursor.getColumnIndex("car_type")));
//					jorder.addProperty("pre_state", cursor.getString(cursor.getColumnIndex("pre_state")));
//					jorder.addProperty("in_passid", cursor.getString(cursor.getColumnIndex("in_passid")));
//					jorder.addProperty("out_passid", cursor.getString(cursor.getColumnIndex("out_passid")));
//					jorders.put(jorder);
//					cursor.moveToNext();
//				}
//			}
//			db.setTransactionSuccessful();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			db.endTransaction();
//			cursorclose(cursor);
//		} 
//		try{
//			db.beginTransaction();//��ʼ����
//			newcursor = db.rawQuery("select * from "+DATABASE_TABLE+" where length(id) > 30 and end_time is null order by create_time desc limit 0,10",null);
//			if (newcursor.moveToFirst()) {
//				for (int i = 0; i < newcursor.getCount(); i++) {
//					JsonObject jorder = new JsonObject();
//					jorder.addProperty("id", newcursor.getString(newcursor.getColumnIndex("id")));
//					jorder.addProperty("localid", newcursor.getString(newcursor.getColumnIndex("localid")));
//					jorder.addProperty("create_time", newcursor.getString(newcursor.getColumnIndex("create_time")));
//					jorder.addProperty("comid", newcursor.getString(newcursor.getColumnIndex("comid")));
//					jorder.addProperty("uin", newcursor.getString(newcursor.getColumnIndex("uin")));
//					jorder.addProperty("total", newcursor.getString(newcursor.getColumnIndex("total")));
//					jorder.addProperty("prepay", newcursor.getString(newcursor.getColumnIndex("prepay")));
//					jorder.addProperty("state", newcursor.getString(newcursor.getColumnIndex("state")));
//					jorder.addProperty("end_time", newcursor.getString(newcursor.getColumnIndex("end_time")));
//					jorder.addProperty("auto_pay", newcursor.getString(newcursor.getColumnIndex("auto_pay")));
//					jorder.addProperty("pay_type", newcursor.getString(newcursor.getColumnIndex("pay_type")));
//					jorder.addProperty("nfc_uuid", newcursor.getString(newcursor.getColumnIndex("nfc_uuid")));
//					jorder.addProperty("c_type", newcursor.getString(newcursor.getColumnIndex("c_type")));
//					jorder.addProperty("uid", newcursor.getString(newcursor.getColumnIndex("uid")));
//					jorder.addProperty("car_number", 
//							URLEncoder.encode(newcursor.getString(newcursor.getColumnIndex("car_number")),"utf-8"));
//					jorder.addProperty("imei", newcursor.getString(newcursor.getColumnIndex("imei")));
//					jorder.addProperty("pid", newcursor.getString(newcursor.getColumnIndex("pid")));
//					jorder.addProperty("car_type", newcursor.getString(newcursor.getColumnIndex("car_type")));
//					jorder.addProperty("pre_state", newcursor.getString(newcursor.getColumnIndex("pre_state")));
//					jorder.addProperty("in_passid", newcursor.getString(newcursor.getColumnIndex("in_passid")));
//					jorder.addProperty("out_passid", newcursor.getString(newcursor.getColumnIndex("out_passid")));
//					jorders.put(jorder);
//					newcursor.moveToNext();
//				}
//			}
//			db.setTransactionSuccessful();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			db.endTransaction();
//			cursorclose(cursor);
//			cursorclose(newcursor);
//		} 
//		return jorders;
//	}
//
//	/**
//	 * ���¶���״̬���ػ�������
//	 * @param relations
//	 */
//	public void updateOrder(List<Relation> relations){
//		Log.e(TAG, "����ͬ�������������Ľ�����ڶ�����"+relations.toString());
//		db = dbhelper.getWritableDatabase();
//		try {
//			db.beginTransaction();
//			for (int i = 0; i < relations.size(); i++) {
//				String sql = "update "+DATABASE_TABLE+" set id = "+relations.get(i).getLine()+" where id = '"+relations.get(i).getLocal()+"'";
//				db.execSQL(sql);
//			}
//			db.setTransactionSuccessful();
//			Log.e(TAG,"ZldNewActivityִ�����");
//		} finally {
//			db.endTransaction();
//		} 
//	}
//
//	/**
//	 * ��ѯ�۸���ԣ����ڼ��㶩����Ǯ��
//	 * @param comId
//	 * @return
//	 */
//	@SuppressWarnings({ "rawtypes" })
//	public List<List<Map>> getPriceList(int car_type,String comId){
//		List<List<Map>> priceList = new ArrayList<List<Map>>();
//		SQLiteDatabase db = dbhelper.getWritableDatabase();
//		Cursor cursor1 = null;
//		Cursor cursor2 = null;
//		db.beginTransaction();//��ʼ����
//		try {
//			cursor1= db.rawQuery("select * from price_tb where comid=? and state=? and pay_type=? and car_type=? order by id desc",new String[]{comId,"0","0",""+car_type});
//			if(cursor1.moveToFirst()) {
//				priceList = getpriceMpa(cursor1,"1");
//			}else {
//				cursor2 = db.rawQuery("select * from price_tb where comid=? and state=? and pay_type=? and car_type=? order by id desc",new String[]{comId,"0","1",""+car_type});
//				if (cursor2.moveToFirst()) {
//					priceList = getpriceMpa(cursor2,"2");
//				}
//			}
//			db.setTransactionSuccessful();//���ô˷�������ִ�е�endTransaction() ʱ�ύ��ǰ������������ô˷�����ع�����
//		} finally {
//			db.endTransaction();//������ı�־�������ύ���񣬻��ǻع�����
//			cursorclose(cursor1);
//			cursorclose(cursor2);
//		} 
//
//		return priceList;
//	}
//
//	@SuppressWarnings("rawtypes")
//	public List<List<Map>> getpriceMpa(Cursor cursor, String mprice_type) {
//		List<List<Map>> mLists = new ArrayList<List<Map>>();
//		List<Map> mList = new ArrayList<Map>();
//		Log.e("getpriceMpa", "��ѯ�α�ļ�¼����" + cursor.getCount());
//		if (cursor.moveToFirst()) {// �ж��α��Ƿ�Ϊ��
//			for (int i = 0; i < cursor.getCount(); i++) {
//				cursor.moveToPosition(i);// �ƶ���ָ����¼
//				Map<Object, Object> pricemap = new HashMap<Object, Object>();
//				pricemap.put("id", cursor.getLong(cursor.getColumnIndex("id")));
//				pricemap.put("comid", cursor.getLong(cursor.getColumnIndex("comid")));
//				pricemap.put("price", cursor.getDouble(cursor.getColumnIndex("price")));
//				pricemap.put("state", cursor.getLong(cursor.getColumnIndex("state")));
//				pricemap.put("unit", cursor.getInt(cursor.getColumnIndex("unit")));
//				pricemap.put("pay_type", cursor.getInt(cursor.getColumnIndex("pay_type")));
//				pricemap.put("create_time", cursor.getLong(cursor.getColumnIndex("create_time")));
//				pricemap.put("b_time", cursor.getInt(cursor.getColumnIndex("b_time")));
//				pricemap.put("e_time", cursor.getInt(cursor.getColumnIndex("e_time")));
//				pricemap.put("is_sale", cursor.getInt(cursor.getColumnIndex("is_sale")));
//				pricemap.put("first_times", cursor.getInt(cursor.getColumnIndex("first_times")));
//				pricemap.put("fprice", cursor.getDouble(cursor.getColumnIndex("fprice")));
//				pricemap.put("countless", cursor.getInt(cursor.getColumnIndex("countless")));
//				pricemap.put("free_time", cursor.getInt(cursor.getColumnIndex("free_time")));
//				pricemap.put("fpay_type", cursor.getInt(cursor.getColumnIndex("fpay_type")));
//				pricemap.put("isnight", cursor.getInt(cursor.getColumnIndex("isnight")));
//				pricemap.put("isedit", cursor.getInt(cursor.getColumnIndex("isedit")));
//				pricemap.put("car_type", cursor.getInt(cursor.getColumnIndex("car_type")));
//				pricemap.put("is_fulldaytime", cursor.getInt(cursor.getColumnIndex("is_fulldaytime")));
//				pricemap.put("update_time", cursor.getLong(cursor.getColumnIndex("update_time")));
//				pricemap.put("mprice_type", mprice_type);
//				mList.add(pricemap);
//			}
//			if (mprice_type.equals("1")) {
//				mLists.add(0, mList);
//				mLists.add(1, new ArrayList<Map>());
//			}else {
//				mLists.add(0, new ArrayList<Map>());
//				mLists.add(1, mList);
//			}
//		}
//		return mLists;
//	}
//
//	/**
//	 * ������Ӽ۸���ԣ�
//	 * @param list
//	 * @return
//	 */
//	public void addMorePrice(List<Price_tb> list) {
//		if (null == list || list.size() <= 0) {
//			return;
//		}
//		db = dbhelper.getWritableDatabase();
//		try {
//			String sql = "insert into " + "price_tb" + "(comid" + ","
//					+ "price" + ","
//					+ "state" + ","
//					+ "unit" + ","
//					+ "pay_type" + ","
//					+ "create_time" + ","
//					+ "b_time" + ","
//					+ "e_time" + ","
//					+ "is_sale " + ","
//					+ "first_times" + "," 
//					+ "fprice" + ","
//					+ "countless " + "," 
//					+ "free_time" + ","
//					+ "fpay_type" + ","
//					+ "isnight" + ","
//					+ "isedit" + ","
//					+ "car_type" + "," 
//					+ "is_fulldaytime" + "," 
//					+ "update_time"
//					+ ") " + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//			SQLiteStatement stat = db.compileStatement(sql);
//			db.beginTransaction();
//			for (Price_tb Price_tbinfo : list) {
//				Log.e(TAG,"�۸���Ϣ��"+Price_tbinfo.toString());
//				stat.bindString(1, Price_tbinfo.comid);
//				stat.bindString(2, Price_tbinfo.price);
//				stat.bindString(3, Price_tbinfo.state);
//				stat.bindString(4, Price_tbinfo.unit);
//				stat.bindString(5, Price_tbinfo.pay_type);
//				stat.bindString(6, Price_tbinfo.create_time);
//				stat.bindString(7, Price_tbinfo.b_time);
//				stat.bindString(8, Price_tbinfo.e_time);
//				stat.bindString(9, Price_tbinfo.is_sale);
//				stat.bindString(10, Price_tbinfo.first_times);
//				stat.bindString(11, Price_tbinfo.fprice);
//				stat.bindString(12, Price_tbinfo.countless);
//				stat.bindString(13, Price_tbinfo.free_time);
//				stat.bindString(14, Price_tbinfo.fpay_type);
//				stat.bindString(15, Price_tbinfo.isnight);
//				stat.bindString(16, Price_tbinfo.isedit);
//				stat.bindString(17, Price_tbinfo.car_type);
//				stat.bindString(18, Price_tbinfo.is_fulldaytime);
//				if(Price_tbinfo.update_time == null){
//					Price_tbinfo.update_time = ""+System.currentTimeMillis();
//				}
//				stat.bindString(19, Price_tbinfo.update_time);
//				long result = stat.executeInsert();
//				if (result < 0) {
//					return;
//				}
//			}
//			db.setTransactionSuccessful();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		} finally {
//			try {
//				if (null != db) {
//					db.endTransaction();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return;
//	}
//
//	/**
//	 * �жϼ۸���Ƿ�Ϊ�գ�
//	 * @return
//	 */
//	public Boolean isPricetbEmpty(){
//		db = dbhelper.getWritableDatabase();
//		Cursor cursor = db.rawQuery("select * from price_tb",null);
//		if(cursor.getCount()==0){
//			cursorclose(cursor);
//			return true;
//		}
//		cursorclose(cursor);
//		return false;
//	}
//
//	/**
//	 * ��ռ۸��
//	 */
//	public void clearPrice_tb(){
//		db = dbhelper.getWritableDatabase();
//		String sql = "delete from price_tb";
//		db.execSQL(sql);
//	}
//
//	/**
//	 * ���com_info��
//	 */
//	public void clearCominfo_tb(){
//		db = dbhelper.getWritableDatabase();
//		String sql = "delete from com_info_tb";
//		db.execSQL(sql);
//	}
//
//	/**
//	 * ����¿���
//	 */
//	public void clearMonthCard_tb(){
//		db = dbhelper.getWritableDatabase();
//		String sql = "delete from monthcard_tb";
//		db.execSQL(sql);
//	}
//
//	/**
//	 * ��ȡ���ص�ǰ�����б�
//	 * @param page
//	 * @return
//	 */
//	public LocalCurrentOrder getLocalCurrOrder(int page,int type){
//		LocalCurrentOrder horders = new LocalCurrentOrder();
//		ArrayList<AllOrder> info = new ArrayList<AllOrder>();
//		int count = 0;
//		db = dbhelper.getWritableDatabase();
//		Cursor rawQuery = null;
//		if(type == 0){
//			rawQuery = db.rawQuery("select count(*) from "+DATABASE_TABLE+" where state=?", new String[]{"0"});
//		}else{
//			rawQuery = db.rawQuery("select count(*) from "+DATABASE_TABLE+" where state=?", new String[]{"1"});
//		}
//		count = rawQuery.getCount();
//		rawQuery.close();
//		horders.setCount(count+"");
//		Cursor cursor = null;
//		if(type == 0){
//			cursor = db.rawQuery("select * from "+DATABASE_TABLE+" where state=0 order by create_time desc limit "+10*(page-1)+",10",null);
//		}else{
//			cursor = db.rawQuery("select * from "+DATABASE_TABLE+" where state=1 order by create_time desc limit "+10*(page-1)+",10",null);
//		}
//		if(cursor.moveToFirst()){
//			for (int i = 0; i < cursor.getCount(); i++) {
//				AllOrder order = new AllOrder();
//				order.setId(cursor.getString(cursor.getColumnIndex("id")));
//				order.setLocalid(cursor.getString(cursor.getColumnIndex("localid")));
//				order.setTotal(cursor.getString(cursor.getColumnIndex("total")));
//				order.setPrepay(cursor.getString(cursor.getColumnIndex("prepay")));
//				String btime = cursor.getString(cursor.getColumnIndex("create_time"));
//				order.setDuration("��ͣ"+TimeTypeUtil.getTimeString(Long.parseLong(btime),System.currentTimeMillis()/1000));
//				order.setBtime(TimeTypeUtil.getStringTime(Long.parseLong(btime)*1000));
//				order.setCarnumber(cursor.getString(cursor.getColumnIndex("car_number")));
//				order.setState(cursor.getString(cursor.getColumnIndex("state")));
//				order.setCar_type(cursor.getString(cursor.getColumnIndex("car_type")));
//				info.add(order);
//				cursor.moveToNext();
//			}
//			cursorclose(rawQuery);
//			cursorclose(cursor);
//			horders.setInfo(info);
//		}
//		return horders;
//	}
//
//	/**
//	 * ���ݳ��ƺŲ�ѯδ���㶩����Ԥ�����
//	 * @param carNumber ��ѯ���ƺ� 
//	 */
//	public double queryMinPriceUnitByComid(String comid){
//		db = dbhelper.getWritableDatabase();
//		String sql = null;
//		String prepay_total = null;
//		Cursor rawQuery = null;
//		sql = "select minprice_unit from com_info_tb where id = ?";
//		try {
//			db.beginTransaction();
//			rawQuery = db.rawQuery(sql, new String[]{comid});
//			Log.e(TAG,"sql��䣺"+comid);
//			if (rawQuery.moveToFirst()) {
//				prepay_total = rawQuery.getString(rawQuery.getColumnIndex("minprice_unit"));
//			}
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//			cursorclose(rawQuery);
//		} 
//		if(prepay_total == null){
//			return 0L;
//		}
//		return Double.parseDouble(prepay_total);
//	}
//
//	/**
//	 * ����comid��car_type��ѯ������
//	 */
//	public int queryCountByComid(String car_type){
//		db = dbhelper.getWritableDatabase();
//		int count = 0;
//		Cursor cursor = null;
//		String create_timesql = null;
//		create_timesql = "select count(*) from "+DATABASE_TABLE+" where car_type = 1";//�󳵲���ֱ��Ϊ1��
//		try {
//			db.beginTransaction();
//			cursor = db.rawQuery(create_timesql, null);
//			count = cursor.getCount();
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//			cursorclose(cursor);
//		} 
//		return count;
//	}
//
//
//	/**
//	 * �������ɾ����Ӧid����
//	 * @param orderid
//	 */
//	public void deleteOrderLocalByid(String orderid){
//		if(orderid == null){
//			return;
//		}
//		db = dbhelper.getWritableDatabase();
//		try {
//			db.beginTransaction();
//			db.execSQL("delete from "+DATABASE_TABLE+" where id = ?",new String[]{orderid});
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//		} 
//	}
//
//	/**
//	 * ɾ���������ݿⶩ��
//	 * 5��ǰ�ģ������Ѿ��еģ�δ����ģ�create_time   total
//	 */
//	public void deleteSqliteOrder() {
//		// TODO Auto-generated method stub
//		long currentTimeMillis = System.currentTimeMillis();
//		long currentTime = currentTimeMillis/1000 - 432000;
//		Log.e(TAG,"ɾ���������ݿⶩ��5��ǰ�ģ������е�δ�����:"+currentTime);
//		db = dbhelper.getWritableDatabase();
//		try {
//			db.beginTransaction();
//			db.execSQL("delete from "+DATABASE_TABLE+" where length(id) < 30 and length(localid) < 30 and state = 0 and create_time < ?",new String[]{""+currentTime});
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//		} 
//	}
//}
