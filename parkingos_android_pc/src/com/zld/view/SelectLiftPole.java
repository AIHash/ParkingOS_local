package com.zld.view;

import java.util.ArrayList;
import java.util.Map;

import com.zld.R;
import com.zld.adapter.AccountDropListAdapter;
import com.zld.bean.AppInfo;
import com.zld.bean.FreeResons;
import com.zld.bean.LiftReason;
import com.zld.fragment.BaseFragment;
import com.zld.fragment.CashFragment;
import com.zld.fragment.EntranceFragment;
import com.zld.lib.constant.Constant;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.util.StringUtils;
import com.zld.ui.ZldNewActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SelectLiftPole extends BaseFragment{
	private View parent;
	private Toast mToast;
	private ZldNewActivity activity;
	private boolean isInPole;
    
	private BaseFragment cashFragment;
	private ListView listview;
	private PopupWindow popupWindow;
	public static String comid = null; 
	private static final String TAG = "SelectCarType";

	public SelectLiftPole() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SelectLiftPole(ZldNewActivity activity, View parent,BaseFragment cashFragment) {
		this.parent = parent;
		this.activity = activity;
		this.cashFragment = cashFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState); 
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@SuppressLint("NewApi")
	public void showLiftPoleView(String poleIP,boolean isInPole) {
		this.isInPole = isInPole;
		showPopupWindow(parent);
	}
	public void closePop() {
		if (popupWindow != null) {
			popupWindow.dismiss();	
		}
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
	 * ��ʾ�������ѡ���б�
	 * @param parent
	 */
	@SuppressWarnings({ "deprecation", "unused" })
	private void showPopupWindow(View parent) {
		int screenHeight = 0; 
		// TODO Auto-generated method stub
		if(listview == null){
			listview = (ListView) LayoutInflater.from(activity).inflate(R.layout.account_droplist, null); 
		}
		int count = 0;
		if (AppInfo.getInstance().getLiftreason() != null) {
			count = AppInfo.getInstance().getLiftreason().size();
		}
		
		if (count == 0) {
			liftOrderRecord(-1); // û��ԭ��Ͳ������ˣ�ֱ�ӷ��ͷ�����
			return;
		}
		final ArrayList<String> selectAllAccount = new ArrayList<String>();
		selectAllAccount.add("ѡ̧��ԭ��");
		for (int i = 0; i < count; i++) {
			 LiftReason value = AppInfo.getInstance().getLiftreason().get(i);
			selectAllAccount.add(value.getValue_name());
		}

		if(selectAllAccount.size() != 0){
			AccountDropListAdapter adapter = new AccountDropListAdapter(activity, selectAllAccount,true);
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
//			popupWindow.setWidth((int)parent.getWidth());
		}  
		popupWindow.setFocusable(true);  
		popupWindow.setOutsideTouchable(false);  
		// �����Ϊ�˵��������Back��Ҳ��ʹ����ʧ�����Ҳ�����Ӱ����ı���  
//		ColorDrawable cd = new ColorDrawable(0x000000); 
//		popupWindow.setBackgroundDrawable(cd);  
		final int[] location = new int[2];  
		parent.getLocationOnScreen(location);  

		popupWindow.showAsDropDown(parent,0,-(int)parent.getHeight());
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String account = selectAllAccount.get(position);
				Log.e(TAG,"ѡ��̧�������ǣ�"+account);
				if(account == null){
					return;
				}
				int count = AppInfo.getInstance().getLiftreason().size();
				final ArrayList<String> selectAllAccount = new ArrayList<String>();
				for (int i = 0; i < count; i++) {
					String name = AppInfo.getInstance().getLiftreason().get(i).getValue_name();
					if (name.equals(account)) {
						int reasonID = AppInfo.getInstance().getLiftreason().get(i).getValue_no();
						
						popupWindow.dismiss();
						liftOrderRecord(reasonID);
						break;
					}
				}
			
			}
		});
		
	}
	
	private void liftOrderRecord(int reason){
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.LIFT_ORDER);
		params.setUrlParams("token", AppInfo.getInstance().getToken()); //0:ͨ��ɨ���Զ����ɶ�����1����¼�������ɶ���
		if (reason != -1) {
			params.setUrlParams("reason", reason);
		}
		
		if (isInPole) {
			params.setUrlParams("passid", activity.selectCameraIn.get(0).getPassid());
		}else {
			params.setUrlParams("passid", activity.selectCameraOut.get(0).getPassid());
		}
		
		String url = params.getRequstUrl();
		Log.e(TAG, "����̧�˼�¼url---------------->>" + url);
		HttpManager.requestGET(activity, url,this);	
	}
	
	@Override
	public boolean doSucess(String url, String object) {
		// TODO Auto-generated method stub
		if(url.contains(Constant.LIFT_ORDER)){
			Map<String, String> resultMap = StringUtils.getMapForJson(object);
			if (resultMap.get("result").endsWith("1")) {
				String lrid = resultMap.get("lrid");
				if (isInPole) {
					// ����̧�˼�¼���ɳɹ���ȥ����
					activity.getPoleIDInList().add(lrid);
//					((EntranceFragment)cashFragment).setPoleRecordID(lrid);
					((EntranceFragment)cashFragment).takePhotos(activity.selectCameraIn.get(0).getIp());
				}else {
					// ����̧�˼�¼���ɳɹ���ȥ����
					activity.getPoleIDOutList().add(lrid);
					activity.controlExitCamera();
				}
				
				//// ���������
			}
		}
		return true;
	}

}