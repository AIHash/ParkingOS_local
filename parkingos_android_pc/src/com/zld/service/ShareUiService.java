package com.zld.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import com.zld.application;
import com.zld.bean.AppInfo;
import com.zld.bean.ShaerUiInfo;
import com.zld.bean.UploadImg;
import com.zld.db.SqliteManager;
import com.zld.engine.ShareUiInfoParser;
import com.zld.lib.constant.Constant;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.util.AppInfoUtil;
import com.zld.lib.util.ImageUitls;
import com.zld.lib.util.SharedPreferencesUtils;
import com.zld.photo.UpLoadImage;
import com.zld.ui.LoginActivity;
import com.zld.ui.ZldNewActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class ShareUiService extends BaseService{

	private static final String TAG = "ShareUiService";
	SqliteManager sm;
	long startTime = 0;
	private String netType;
	private String token = null;
	private ZldNewActivity zldNewActivity;
//	private LocalOrderDBManager loDBManager;
	private boolean isFirst = true;/*��¼��,������һ�α�����Ҫ����Ϣ*/
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.SHOWPIC_ONRIGHT_MSG:
				//�ϴ�ͼƬ�ɹ��ص�
				String orderid = (String) msg.obj;
				Log.e(TAG,"ͼƬ�ϴ��ɹ�orderid��"+orderid);
				if(orderid!=null){
					//�޸�ͼƬ�ϴ�״̬
					int ishomeexitup = msg.arg1;
					Log.e(TAG,"�޸�ͼƬ�ϴ�״̬-ishomeexitupΪ0����ڣ�"+ishomeexitup+" orderid:"+orderid);
					if(ishomeexitup==0){
						//����ϴ��ɹ�
						sm.updateOrderImg(orderid, "1", true);
					}else{
						//�����ϴ��ɹ�
						sm.updateOrderImg(orderid, "1", false);
					}
					UploadImg selectImage = sm.selectImage(orderid);
					if(selectImage!=null){
						//�����ͼƬ�ϴ�״̬����1�Ļ�,�򱾵�ɾ��
						String imghomepath = selectImage.getImghomepath();
						String homeimgup = selectImage.getHomeimgup();
						String exitimgup = selectImage.getExitimgup();

						if((imghomepath==null&&exitimgup.equals("1"))||
								(homeimgup.equals("1")&&exitimgup.equals("1"))){
							//����ɾ������ͼƬ�����ݿ�ͼƬ��Ϣ
							deleteOrderIamgeInfo(orderid);
						}
					}
				}
				break;
			case Constant.PICUPLOAD_FILE:
				//�ϴ�ͼƬʧ�ܻص�
				Log.e(TAG, "��ѯ���ݿ�,ɾ��ͼƬ�ļ������ݿ�ͼƬ��Ϣ");
				//deleteOrderIamgeInfo((String) msg.obj);
				break;
			}
			recursionUpload();
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
//		initSqliteManager();
		startTime = System.currentTimeMillis();
		netType = SharedPreferencesUtils.getParam(ShareUiService.this, "nettype", "netType", "0");
	}

//	private LocalOrderDBManager initSqliteManager() {
//		// TODO Auto-generated method stub
//		if(loDBManager == null){
//			application application = ((application) getApplication());
//			loDBManager = application.getLocalOrderDBManager(ShareUiService.this);
//		}
//		return loDBManager;
//	}

	@Override
	public void onStart(Intent intent, int startId) {
		zldNewActivity = ((application) getApplication()).getZldNewActivity();
		token = AppInfo.getInstance().getToken();
		if (token == null || zldNewActivity == null) {
			//stopService();
			return;
		}
		getShareInfo();
		boolean isLocalServer = SharedPreferencesUtils.getParam(getApplicationContext(),"nettype", "isLocalServer", false);
		if(!isLocalServer){//Ϊfalse,���Ǳ��ط�����,����ͬ������
			if(isFirst){
				/*ͬ������ʱ���¿�*/
//				doSynchronize();
				/*�������Ƿ�Ϊ�գ�Ϊ�����أ���Ϊ�ո���*/
//				startUpdataLocalData();
				isFirst = false;
				startTime = System.currentTimeMillis();
			}
			if(intent != null){
				String refresh = intent.getStringExtra("refresh");
				if(refresh != null&&refresh.equals("refresh")){
					startTime = startTime - Constant.time;
				}
			}
//			startFiveMinuteLocal();
		}else{//Ϊtrue��ʾ�б��ط�����,��ͬ��������
		}
//		fileRegularDelete();
	}

//	private void initIssUpLocal() {
//		// TODO Auto-generated method stub
//		if(issuplocal == null){
//			issuplocal = SharedPreferencesUtils.getParam(
//					getApplicationContext(), "zld_config", "issuplocal", "");
//			Log.e("isLocal","BaseFragment initIssUpLocal get issuplocal "+issuplocal);
//		}
//	}

	public void getShareInfo() {
		//��ȡ��׿������״̬
		if(zldNewActivity!=null){
			String passid = zldNewActivity.passid;
			Log.e("taigan","��ȡ��λ��Ϣ�Ǵ���passid:"+passid);
			RequestParams params = new RequestParams();
			params.setUrlHeader(Constant.serverUrl + Constant.GET_SHARE);
			AppInfoUtil.displayBriefMemory(zldNewActivity);
			params.setUrlParams("comid", AppInfo.getInstance().getComid());
			params.setUrlParams("passid", passid);
			params.setUrlParams("type", 1);//�½ӿ�
			params.setUrlParams("token",AppInfo.getInstance().getToken());
			params.setUrlParams("equipmentmodel", AppInfoUtil.getEquipmentModel());
			params.setUrlParams("memoryspace",(AppInfoUtil.getAvailableMemory(zldNewActivity)/1024/1024)+"_"+(AppInfoUtil.getTotalMemorySize(zldNewActivity)/1024/1024));
			params.setUrlParams("internalspace", +AppInfoUtil.getAvailableInternalMemorySize()+"_"+AppInfoUtil.getTotalInternalMemorySize());
			String url = params.getRequstUrl();
			Log.e(TAG, "��ȡ����λ����Ϣ��"+url);
			HttpManager.requestShareGET(this, url,this);	
		}
	}

	/**---------------------���ػ��߼�-------------------------*/
//	private void startFiveMinuteLocal() {
//		Log.e(TAG,"��ǰʱ��"+(System.currentTimeMillis()-startTime));
//		if(System.currentTimeMillis()-startTime>Constant.time){
//			//��״̬����"�������״̬"5
//			//��״̬����"���ڽ��㶩��״̬"6
//			//��״̬���� "�������״̬����û�е���շ���ɻ���ѣ���ʱ��״̬���������ˢ���б�"7
//
//			Log.e(TAG, "��״̬"+OrderListState.getInstance().getState());
//			Log.e(TAG, "isClearFinishState:"+OrderListState.getInstance().isClearFinishState());
//			Log.e(TAG, "isClearOrderState:"+OrderListState.getInstance().isClearOrderState());
//			Log.e(TAG, "isOrderFinishState:"+OrderListState.getInstance().isOrderFinishState());
//			Log.e(TAG, "isHandSearchState:"+OrderListState.getInstance().isHandSearchState());
//			Log.e(TAG, "isAutoSearchState:"+OrderListState.getInstance().isAutoSearchState());
//			Log.e(TAG, "isModifyOrderState:"+OrderListState.getInstance().isModifyOrderState());
//			if(!OrderListState.getInstance().isClearFinishState()&&
//					OrderListState.getInstance().isClearOrderState()&&
//					OrderListState.getInstance().isOrderFinishState()&&
//					!OrderListState.getInstance().isModifyOrderState()&&
//					!OrderListState.getInstance().isAutoSearchState()&&
//					!OrderListState.getInstance().isHandSearchState()){
////				startUpdataLocalData();
//				startTime =System.currentTimeMillis();
//			}else{
//				startTime =System.currentTimeMillis();
//			}
//		}
//	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

//	private void startUpdataLocalData() {
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				// ȥ���һ��ͬ������������
//				updateLocalData();
//			}
//		}).start();
//	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG,"ShareUiService------onDestroy");
	}

	@Override
	public boolean doSucess(String url,byte[] buffer) {
		// TODO Auto-generated method stub
		if(url.contains(Constant.GET_SHARE)){
			doGetShareInfo(buffer);
		}
		return true;
	}

	/**
	 * ʵʱ�ĳ�λ������Ϣ
	 * @param object
	 */
	private void doGetShareInfo(byte[] object) {
		// TODO Auto-generated method stub
		InputStream is = new ByteArrayInputStream(object);
		try {
			ShaerUiInfo info = ShareUiInfoParser.getUpdataInfo(is);
			is.close();
			Log.e(TAG, "��ȡ���÷�����ϢΪ"+info.toString());
			if(info.getResult()!=null&&"fail".equals(info.getResult())){
				Log.e(TAG, "���token��״̬--token��Ч");
				Message msg = new Message();
				msg.what = 4;//tokenʧЧ
				if(zldNewActivity != null){
					zldNewActivity.finish();
				}
				Intent intent = new Intent(ShareUiService.this, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("token", "false");
				startActivity(intent);
				stopSelf();
			}else{
				Message msg = new Message();
				msg.what = Constant.PARKING_NUMS_MSG;
				msg.obj = info;
				if(zldNewActivity != null){
					zldNewActivity.handler.dispatchMessage(msg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean doFailure(String url, String status) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * ��¼���ͬ��
	 */
//	public void doSynchronize(){
//		long lineTime = SharedPreferencesUtils.getParam(
//				getApplicationContext(), "zld_config", "linetime", 0L);
//		if (0 == lineTime) {
//			new Thread(new Runnable() {// ȥͬ������ʱ�䣻
//				@Override
//				public void run() {
//					Log.e(TAG,"ͬ��������ʱ��");
//					MySynchronizeUitls.SynchronizeTime(ShareUiService.this);
//				}
//			}).start();
//		}
//		new Thread(new Runnable() {// ȥͬ�������ļ۸�
//			@Override
//			public void run() {
//				Log.e(TAG,"ͬ�������۸�");
//				MySynchronizeUitls.SynchronizePrice(loDBManager,ShareUiService.this);
//			}
//		}).start();
//		new Thread(new Runnable() {// �����¿����ƺ�
//			@Override
//			public void run() {
//				Log.e(TAG,"ͬ�������¿�");
//				MySynchronizeUitls.SynchronizeMonthCard(loDBManager,ShareUiService.this);
//			}
//		}).start();
//	}

	/**
	 * ȥͬ���������ݿ�ĵ�ǰ�������ݣ�
	 */
//	public void updateLocalData(){
//		if (!IsNetWork.IsHaveInternet(ShareUiService.this)) {
//			return;
//		}
//		if (loDBManager.isOrdertbEmpty()) {
//			//ȥͬ������˽������е�ǰ���������뵽�������ݿ⣩����¼�����������;
//			Log.e(TAG, "������Ϊ�գ�������");
//			downloadOrder(this);
//		}else {
//			updateOrder(this);
//			//���ݿ��ж���ʱ����ڵ�ǰʱ������� ��ɾ��
//			deleteSqliteOrder();
//		}
//	}

//	private void deleteSqliteOrder() {
//		// TODO Auto-generated method stub
//		if(loDBManager != null){
//			loDBManager.deleteSqliteOrder();
//		}
//	}

	/**
	 * ���ʷ�������÷�����ʱ�䡣
	 * ��������ݿ���Ҫ�����߳������У���
	 * @param context
	 */
//	public void downloadOrder(final Context context) {
//		String url = 
//				Constant.requestUrl+"local.do?action=firstDownloadOrder&token="+
//						AppInfo.getInstance().getToken();
//		Log.e(TAG, "�������ط�����������url��"+url);
//		AQuery aq = new AQuery(context);
//		aq.ajax(url, String.class, new AjaxCallback<String>() {
//			@Override
//			public void callback(String url, String object, AjaxStatus status) {
//				super.callback(url, object, status);
//				if (!TextUtils.isEmpty(object)) {
//					Log.e(TAG,	 "���������ص�Ҫͬ�������ǣ�"+object);
//					try {
//						JSONObject json = new JSONObject(object);
//						String maxid = json.getString("maxid");
//						SharedPreferencesUtils.setParam(
//								getApplicationContext(), "zld_config", "maxid", maxid);
//						Gson gson = new Gson();
//						List<Order_tb> list  = gson.fromJson(json.getString("orders"), new TypeToken<List<Order_tb>>() {}.getType());
//						loDBManager.addMoreOrder(list);
//						Log.e(TAG, "��ǰ����������ϣ�---------------!!!");
//						zldNewActivity.getMoney();
//						zldNewActivity.refreshListOrder();
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		});
//	}

	/**
	 *���������ɵĶ���,���ض������滻,��󶩵����,Ҫɾ���Ķ�����ţ�
	 * @param context
	 */
//	public void updateOrder(final Context context){
//		if(sm == null){
//			sm = ((application) getApplicationContext()).getSqliteManager(this);
//		}
//		//StringBuffer Ids = loDBManager.getcurrOrderIds();//��ǰδ����Ķ����ż��ϣ�
//		JSONArray updateOrders = loDBManager.getUpdateOrders();//���ز������Ķ�����
//		String maxid = SharedPreferencesUtils.getParam(getApplicationContext(), "zld_config", "maxid", "0");
//		String url = Constant.requestUrl+Constant.SYNCHRO_ORDER;
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("token", AppInfo.getInstance().getToken());
//		params.put("maxid", maxid);
//		//params.put("ids", Ids.toString());
//		params.put("orders", "{\"data\":"+updateOrders+"}");
//		Log.e(TAG,"======"+updateOrders.toString());
//		Log.e(TAG, "����ͬ��������������url��"+url+params.toString());
//		FileUtil.writeSDFile("���浽�ļ�", ""+url+params.toString());
//		HttpManager.requestSynchronizeOrder(context, url, params, ShareUiService.this);
//	}

//	@Override
//	public boolean doSucess(String url, String object) {
//		// TODO Auto-generated method stub
//		if(url.contains(Constant.SYNCHRO_ORDER)){
//			if (!TextUtils.isEmpty(object)) {
//				Gson gson = new Gson();
//				synchroUpdateInfo info = gson.fromJson(object,synchroUpdateInfo.class);
//				Log.e(TAG, "����ͬ�������������Ľ����"+info.toString());
//				if (info.getOrders() != null && info.getOrders().size() > 0) {
////					loDBManager.addMoreOrder(info.getOrders());
//				}
//				ArrayList<String> lineList = null;
//				if (info.getRelation() != null && info.getRelation().size() > 0) {
//					lineList = new ArrayList<String>();
//					for(int i=0;i<info.getRelation().size();i++){
//						Relation relation = info.getRelation().get(i);
//						if(relation != null){
//							lineList.add(relation.getLine());
//						}
//					}
////					loDBManager.updateOrder(info.getRelation());
//					sm.updateImgOrderid(info.getRelation());
//				}
//				if (!TextUtils.isEmpty(info.getMaxid())) {
//					SharedPreferencesUtils.setParam(
//							getApplicationContext(), "zld_config", "maxid",info.getMaxid());
//				}
//				//orderid����С��30�ļ���
//				recursionUpload();
//				if (!TextUtils.isEmpty(info.getDelOrderIds())) {
//					String[] split = info.getDelOrderIds().split(",");
//					if(split!=null&&split.length>0){
////						loDBManager.deleteMoreOrder(split);
//					}
//				}
//				Log.e(TAG, "���һ��ͬ�����ݹ���--ɾ���ѽ���״̬�Ķ�����");
//				if(zldNewActivity!=null){
//					// ͬ����,��ȡ�շѽ��
//					zldNewActivity.getMoney();
//					//�볡�б�״̬,��֧�ֱ��ػ��ĳ��� ��ˢ��
//					if(!OrderListState.getInstance().isParkOutState()&&
//							AppInfo.getInstance().getIssuplocal().equals("1")){
//						// ��Ϊ��ǰ�б�״̬,��ˢ��
//						zldNewActivity.refreshListOrder();
//					}
//				}
//			}
//		}
//		return true;
//	}

	@Override
	public void timeout(String url) {
		// TODO Auto-generated method stub
		super.timeout(url);
	}
	/**
	 * ��ѯ���ݿ�����Ϣ �ϴ�
	 */
	private void recursionUpload() {
		int i = 0;
		boolean isHave = true;
		while(isHave){
			ArrayList<UploadImg> selectOrderid = sm.selectOrderid();
			if(selectOrderid!=null&&selectOrderid.size()>0){
				if(i>=selectOrderid.size()){
					isHave = false;
					break;
				}
				UploadImg uploadImg = selectOrderid.get(i);
				if(uploadImg!=null){
					String orderid = uploadImg.getOrderid();
					String lefttop = uploadImg.getLefttop();
					String rightbottom = uploadImg.getRightbottom();
					String width = uploadImg.getWidth();
					String height = uploadImg.getHeight();
					String carnumber = uploadImg.getCarnumber();
					String imghomepath = uploadImg.getImghomepath();
					String homeimgup = uploadImg.getHomeimgup();
					String exitimgup = uploadImg.getExitimgup();
					//�����ͼƬ
					if(imghomepath!=null&&homeimgup!=null&&homeimgup.equals("0")){
						Log.e(TAG,"orderid����С��30�����ͼƬ��Ϣ��"+uploadImg.toEasyString());
						Bitmap bitmap = BitmapFactory.decodeFile(imghomepath);
						if(bitmap != null){
							InputStream bitmapToInputStream = ImageUitls.getBitmapInputStream(netType,bitmap);
							upload(bitmapToInputStream, orderid, Constant.HOME_PHOTOTYPE, lefttop, rightbottom, width, height, carnumber);	
						}
						break;
					}
					String imgexitpath = uploadImg.getImgexitpath();
					//�г���ͼƬ
					if(imgexitpath!=null&&exitimgup!=null&&exitimgup.equals("0")){
						Log.e(TAG,"orderid����С��30�ĳ���ͼƬ��Ϣ��"+uploadImg.toEasyString());
						Bitmap bitmap = BitmapFactory.decodeFile(imgexitpath);
						if(bitmap != null){
							InputStream bitmapToInputStream = ImageUitls.getBitmapInputStream(netType,bitmap);
							upload(bitmapToInputStream, orderid, Constant.EXIT_PHOTOTYPE, lefttop, rightbottom, width, height, carnumber);	
						}
						break;
					}
					i++;
				}else{
					i++;
					break;
				}
			}else{
				isHave = false;
				break;
			}
		}
	}

	/**
	 * �ϴ�ͼƬ��������
	 */
	public void upload(
			InputStream bitmapToInputStream, String orderId,
			int type,String x, String y,String width, String height,String carPlate) {
		UpLoadImage upLoadImage = new UpLoadImage();
		upLoadImage.setPhotoType(type);
		upLoadImage.setmHandler(handler);
		upLoadImage.setComid(AppInfo.getInstance().getComid());
		upLoadImage.upload(bitmapToInputStream, orderId,
				x + "",	y + "", width + "",	height + "", carPlate);

	}

	/**
	 * ��ѯ���ݿ�,ɾ��ͼƬ�ļ������ݿ�ͼƬ��Ϣ
	 * @param orderId
	 */
	private void deleteOrderIamgeInfo(String orderId) {
		if(orderId == null){
			return;
		}
		UploadImg selectImage = sm.selectImage(orderId);
		if (selectImage != null) {
			String imgpath = selectImage.getImgexitpath();
			if (imgpath != null) {
				Log.e(TAG, "ɾ��sd��ͼƬ" + imgpath);
				ImageUitls.deleteImageFile(imgpath);
				Log.e(TAG, "���ݿ��Ӧ������Ϣ" + orderId);
				sm.deleteData(orderId);
			}
		}
	}

//	/**
//	 * �ļ�����ɾ��
//	 */
//	private void fileRegularDelete() {
//		File file = new File(Constant.FRAME_DUMP_FOLDER_PATH);
//		if(file != null){
//			File[] listFiles = file.listFiles();
//			if(listFiles == null){
//				return;
//			}
//			int listFilesLength = listFiles.length;
//			//����ļ���������3500��,5�죬���һ��700����,ͼƬ300kһ�����൱��1G
//			if(listFilesLength > Constant.DELETE_IMAGE){
//				long currentTime = System.currentTimeMillis();
//				ArrayList<File> deleList = new ArrayList<File>();
//				for(int i=0;i<listFilesLength;i++){
//					if(listFiles[i].isFile()){
//						long lastModified = listFiles[i].lastModified();
//						if((currentTime - Constant.ONEDAYTAMP) >lastModified){
//							Log.e(TAG,"ɾ���ļ�����"+listFiles[i].getName());
//							deleList.add(listFiles[i]);
//						}
//					}
//				}
//				int size = deleList.size();
//				if(size>0){
//					for(int i=0;i<size;i++){
//						deleList.get(i).delete();
//					}
//				}
//			}
//		}
//	}

	/**
	 * ���ݿ���Ϣ����ɾ��
	 */
//	private void dbDataRegularDelete(){
//		if(loDBManager == null){
//			loDBManager = initSqliteManager();
//		}
//		long time = System.currentTimeMillis();
//		time = time - 1296000;
//		Log.e(TAG, "ɾ�����ݿ���Ϣ��ʱ�䣺"+time);
//		boolean selectOrder = loDBManager.selectOrder(""+time);
//		Log.e(TAG,"��ѯ���ݿ��ʱ�䣺"+time);
//		//		deleteOrderIamgeInfo
//	}




}
