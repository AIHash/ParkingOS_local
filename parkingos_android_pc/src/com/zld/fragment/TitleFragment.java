/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015��4��13�� 
 * 
 *******************************************************************************/ 
package com.zld.fragment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import com.zld.R;
import com.zld.application;
import com.zld.bean.AppInfo;
import com.zld.bean.UpdataInfo;
import com.zld.engine.UpdataInfoParser;
import com.zld.lib.constant.Constant;
import com.zld.lib.util.AppInfoUtil;
import com.zld.lib.util.FileUtil;
import com.zld.lib.util.ImageUitls;
import com.zld.lib.util.IsNetWork;
import com.zld.lib.util.OkHttpUtil;
import com.zld.lib.util.SharedPreferencesUtils;
import com.zld.lib.util.TimeUtil;
import com.zld.lib.util.UpdateManager;
import com.zld.service.DetectionServerService;
import com.zld.service.HomeExitPageService;
import com.zld.ui.ChooseWorkstationActivity;



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
public class TitleFragment extends BaseFragment implements OnClickListener{
	private TextView tv_tcb;//����
	private TextView tv_tcb_version;//�汾��
	private TextView tv_update_time;//ʱ��
	private TextView tv_tcb_workstation;//����վ
	private Button btn_more;//����
	private Button btn_Restart;//����
	private Button btn_update;//�ֶ�����
	private String versiontext;//�汾��
	private UpdataInfo info;
	private UpdateManager manager;
	private ImageView iv_home_page_icon;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.title, container,
				false);
		initView(rootView);
		onClickEvent();
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState); 
		String linelocal = SharedPreferencesUtils.getParam(this.activity.getApplicationContext(),"nettype", "linelocal", "local");
		Log.e("linelocal", "linelocal:"+linelocal);
		if (AppInfo.getInstance().getIsLocalServer(this.activity)) {
			if(linelocal.equals("local")){
				tv_tcb_version.setText("V."+AppInfoUtil.getVersionName(activity)+"_��������");
			}else {
				tv_tcb_version.setText("V."+AppInfoUtil.getVersionName(activity)+"_�ƶ�����");
			}
		}else {
			tv_tcb_version.setText("V."+AppInfoUtil.getVersionName(activity)+"_�ƶ�����");
		}
		
		
		new TimeUtil().updateData(tv_update_time);
		String workstation = SharedPreferencesUtils.getParam(activity.getApplicationContext(),
				"set_workStation", "staname", "����վ");
		tv_tcb_workstation.setText(workstation);
	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void initView(View rootView) {
		tv_tcb_version =(TextView) rootView.findViewById(R.id.tv_tcb_version);
		tv_update_time = (TextView) rootView.findViewById(R.id.tv_update_time);
		tv_tcb_workstation = (TextView) rootView.findViewById(R.id.tv_tcb_workstation);
		btn_more = (Button) rootView.findViewById(R.id.btn_more);
		btn_Restart = (Button) rootView.findViewById(R.id.btn_Restart);
		btn_update = (Button) rootView.findViewById(R.id.btn_update);
		
		iv_home_page_icon =(ImageView) rootView.findViewById(R.id.iv_home_page_icon);
		List list = ImageUitls.getLOGO();
		if(list!=null&&list.get(0)!=null){
			Bitmap bitmap = (Bitmap) list.get(0);
			iv_home_page_icon.setImageBitmap(bitmap);
			if(list.get(1)!=null){
				tv_tcb =(TextView) rootView.findViewById(R.id.tv_tcb);
				tv_tcb.setText((""+list.get(1)).split("\\.")[0]);
			}
			
		}
		manager = new UpdateManager(activity);
	}

	/**
	 * �ؼ�����¼�
	 */
	private void onClickEvent() {
		btn_more.setOnClickListener(this);
		btn_Restart.setOnClickListener(this);
		btn_update.setOnClickListener(this);
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_more:
			//����
			Intent intent = new Intent(getActivity(),ChooseWorkstationActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_Restart:
			//�����رյ�HomeExitPageService�����ص��ײ���㷨Ҳ�ز�����������ײ����Ŷ�������˾͡�
			closeRemotService();
			//����
			restartApp(activity);
			break;
		case R.id.btn_update:
			//�����رյ�HomeExitPageService�����ص��ײ���㷨Ҳ�ز�����������ײ����Ŷ�������˾͡�
//			closeRemotService();
			versiontext = AppInfoUtil.getVersionCode(this.getActivity());
			long lasttime = 0;

            if (System.currentTimeMillis() - lasttime >= 2000) {
                isNeedUpdate(versiontext);
            }
            lasttime = System.currentTimeMillis();
			break;
		default:
			break;
		}
	}

	public void closeRemotService() {
		if(activity != null){
			Intent intent = new Intent(activity, HomeExitPageService.class);
			Bundle bundle = new Bundle();
			bundle.putString(Constant.INTENT_KEY, "closeService");
			intent.putExtras(bundle);
			activity.startService(intent);
		}
	}

	public void restartApp(Activity activity) {
		/*Intent intent = new Intent(activity, HelloActivity.class);  
		PendingIntent restartIntent = PendingIntent.getActivity(
				activity, 0, intent,Intent.FLAG_ACTIVITY_NEW_TASK);
		//�˳�����                                          
		AlarmManager mgr = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);    
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,    
				restartIntent); // 1���Ӻ�����Ӧ��   
		 */		
		if(activity != null){
			Intent i = activity.getBaseContext().getPackageManager()  
					.getLaunchIntentForPackage(activity.getBaseContext().getPackageName());  
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			startActivity(i);
			((application) activity.getApplication()).closeActivity();
		}
	}
	
	public void closeAndRestart() {
		//�ر�DetectionServerService--���Ʒɣ��������������ں�̨��⣩
		Intent intent = new Intent(activity,DetectionServerService.class);
		activity.stopService(intent);
		Log.e("shuyu", "ɱ����");
		//�����رյ�HomeExitPageService�����ص��ײ���㷨Ҳ�ز�����������ײ����Ŷ�������˾͡�
		closeRemotService();
		//����
		restartApp(activity);
	}
//     AlertDialog dialog;
	ProgressDialog dialog;
	 private void isNeedUpdate(final String versiontext) {
		    String url = Constant.getUpdateUrlHand();		
	        System.out.println("���ʸ�����Ϣ��url--------->>>>>>" + url);
	        if (IsNetWork.IsHaveInternet(activity)) {
	        	dialog = ProgressDialog.show(activity, "������...", "��ȡ����������...", true, true);
//	        	dialog = new AlertDialog.Builder(activity)
//	        			.setTitle("������...")
//	        			.setMessage("��ȡ����������...")
//	        			.setCancelable(false)
//	        			.create();
//	        	dialog.show();
	        	//	            ShowDialog.checkUpdateDialog(this.activity.getApplicationContext());
	            Request request = new Request.Builder().url(url).build();
			    OkHttpUtil.enqueue(request, new Callback() {
					
					@Override
					public void onResponse(Call arg0, Response arg1) throws IOException {
						// TODO Auto-generated method stub
						byte[] object = arg1.body().bytes();
						Message m = new Message();
						m.obj = object;
						handle.sendMessage(m);
					}
					
					@Override
					public void onFailure(Call arg0, IOException arg1) {
						// TODO Auto-generated method stub
					}
				});
	        } else {
	        	Log.e(TAG, "û������, ����������");
	            activity.showToast("��������!");
	        }

	    }
	 Handler handle = new Handler(){
		 public void handleMessage(android.os.Message msg) {
			 byte[] object = (byte[]) msg.obj;
			 if (object != null) {
                 dialog.dismiss();
                 InputStream is = new ByteArrayInputStream(object);
                 try {
                     info = UpdataInfoParser.getUpdataInfo(is);
                     Log.e("SetActivity", "��ȡ��������Ϣ�ǣ�" + info.toString());
                     is.close();
                     String version = info.getVersion();
                     String versionBeta = info.getVersionBeta();
                     Log.e("SetActivity", "�������˵İ汾Ϊ" + version);
                     Log.e("SetActivity", "�ͻ��˵İ汾Ϊ" + versiontext);
                     boolean hasFormal = false;
                     if (version == null || version.equals("")) {
                     	Log.e(TAG, "��ȡ����˰汾���쳣��");
                         activity.showToast("��ȡ����˰汾���쳣!");
                     } else {
                     	if(Integer.parseInt(versiontext) < Integer.parseInt(version)){
                         	Log.e(TAG, "�汾��ͬ,��Ҫ����");
                             showUpdataDialog(info.getDescription());
                             hasFormal = true;
                     	}
                     }
                     if(!hasFormal){
                     	if (versionBeta == null || versionBeta.equals("")) {
                          	Log.e(TAG, "��ȡ����˰汾���쳣��");
                              activity.showToast("��ȡ����˰汾���쳣!");
                          } else {
                          	if(Integer.parseInt(versiontext) < Integer.parseInt(versionBeta)){
                              	Log.e(TAG, "�汾��ͬ,��Ҫ����");
                                 showUpdataDialog(info.getDescriptionBeta());
                          	}else{
                          		activity.showToast("�������°汾,����Ҫ������");
                          	}
                          }
                     }
                 } catch (Exception e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                     activity.showToast("��ȡ�����쳣!");
                 }
             } else {
                 dialog.dismiss();
                 Log.e(TAG, "��ȡ���³�ʱ������������");
                 activity.showToast("��ȡ���³�ʱ!");
             }
		 };
	 };
	   // ��Ҫ����ʱ���������Ի���
	    private void showUpdataDialog(String message) {
	        AlertDialog.Builder builder = new Builder(activity);
	        builder.setIcon(R.drawable.app_icon_32);
	        builder.setTitle("��������");
//	        builder.setMessage(info.getDescription());
	        builder.setMessage(message);
	        builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	Log.e(TAG, "����������apk�ļ�" + info.getApkurl());
	            	if(FileUtil.getSDCardPath() == null){
						activity.showToast("sd�������û�洢����!");
						return;
					}
//	                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
	                    manager.new DownLoadApkAsyncTask().execute(info.getApkurl());
//	                } else {
//	                    Toast.makeText(activity.getApplicationContext(), "sd��������", 1).show();
//	                    return;
//	                }
	            }
	        });
	        builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	Log.e(TAG, "�û�ȡ�������½����");
	            }
	        });
	        builder.setCancelable(false).create().show();
	    }
	 
}
