package com.zld.lib.util;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.widget.BaseAdapter;

import com.zld.R;
import com.zld.lib.constant.Constant;

public class ShowDialog {

	static Dialog buildDialog;
	static AlertDialog dialog;
	static Timer timer = new Timer();
	static TimerTask task = new TimerTask() {
		public void run() {
			dialog.dismiss();
			timer.cancel();
			//			ʹ���е�ʱ������˵Ļ���3�����ʾ�������ػ��𣿻����Զ��ͱ��ػ��ˣ�
			//			�Զ����ػ��Ļ������ǵ�һЩService�Ĺرգ�����ĳ�λ��������

		}
	};

	public static ProgressDialog getdialog(Context context,String message){
		ProgressDialog dialog = new ProgressDialog(context,R.style.dialog);
		dialog.setMessage(message);
		return dialog;
	}

	/**
	 * ���ɶԻ���
	 * @param activity
	 * @param msg
	 * @param title
	 * @return
	 */
	public static AlertDialog.Builder buildDialog(final Context context,
			String msg, String title) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(msg);
		builder.setTitle(title);
		return builder;
	}

	static Builder buildDialog2 = null;
	static AlertDialog create = null;
	@SuppressWarnings("deprecation")
	public static void showBuildDialog(final Context context,
			String msg, String title,final Handler handler){
		if(buildDialog2 == null){
			buildDialog2 = buildDialog(context, msg, title);
		}
		if(create == null){
			create = buildDialog2.create();
		}
		create.setButton("����", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(handler != null){
					Message msg1 = handler.obtainMessage();
					msg1.what = Constant.RESTART_YES;
					handler.sendMessage(msg1);	
					}
				create.dismiss();
			}
		});
		if(!create.isShowing()){
			create.show();
		}
	}

	/**
	 * �ⵥȷ�Ͽ�
	 */
	public static void buildeSelectDialog(Activity activity,String msg,String title,
			final BaseAdapter adapter, final int selectedPosition){
		AlertDialog.Builder builder = buildDialog(activity, msg, title);
		buildDialogSelect(activity, adapter, selectedPosition, builder);
		builder.create().show();
	}

	/**
	 * �ⵥȷ�Ͽ�
	 */
	public static void buildeChooseDialog(Activity activity,String msg,String title){
		AlertDialog.Builder builder = buildDialog(activity, msg, title);
		buildDialogSelect(builder);
		builder.create().show();
	}

	/**
	 * �ⵥȷ�Ͽ�
	 */
	public static void buildeChooseDialog(Service service,String msg,String title){
		AlertDialog.Builder builder = buildDialog(service, msg, title);
		buildDialogSelect(builder);
		builder.create().show();
	}

	private static void buildDialogSelect(final Activity activity,
			final BaseAdapter adapter, final int selectedPosition,
			AlertDialog.Builder builder) {
		builder.setPositiveButton("ȷ��", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("ȡ��", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
	}

	public static void buildDialogSelect(AlertDialog.Builder builder) {
		builder.setPositiveButton("ȷ��", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("ȡ��", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * ���ӵ���ʾ�Ի���
	 * @param hr 
	 * 
	 * @param warn
	 */
	public void LostOrderDialog(final Activity activity,
			String warn,final String carNumber,final String comid,final String uid) {
		Builder builder = new Builder(activity);
		builder.setIcon(R.drawable.app_icon_32);
		builder.setTitle("������δ����");
		builder.setMessage(warn);
		builder.setCancelable(false);
		builder.setPositiveButton("ȡ������", null);
		builder.setNegativeButton("�������ɶ���",   
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//				try {
				//					hr.addOrder(carNumber, comid, uid);
				//				} catch (UnsupportedEncodingException e) {
				//					((BaseActivity) activity).showToast("�ύ�����ַ�ת���쳣��");
				//					e.printStackTrace();
				//				}
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

//	public static void showSetTimeDialog(final Context context){
//		AlertDialog.Builder builder = new Builder(context);
//		builder.setTitle("��ʾ");
//		builder.setMessage("������ʱ��������ͬ����");
//		builder.setPositiveButton("ȥ����", new  DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				Intent intent =  new Intent(Settings.ACTION_DATE_SETTINGS);  
//				context.startActivity(intent);
//			}
//		});
//		builder.setNegativeButton("����Ҫ", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				dialog.dismiss();
//			}
//		});
//		builder.create().show();
//	}

	public static void startSetLocalDialog(final Context context){
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("��ʾ");
		builder.setMessage("��ǰ�Ѷ���,׼���������ػ�ģʽ��");
		dialog = builder.create();
		dialog.show();
		timer.schedule(task, 5000);
	}
	
	public static void checkUpdateDialog(final Context context){
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("��ʾ");
		builder.setMessage("��ȡ����������...");
		dialog = builder.create();
		dialog.show();
//		timer.schedule(task, 5000);
	}
}

