package com.zld.view;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zld.R;
import com.zld.lib.constant.Constant;

/**
 * 
 * <pre>
 * ����˵��: ����ͷ����,������ʱ�Ի���
 * ����:	2015��10��14��
 * ������:	HZC
 * 
 * ��ʷ��¼
 *    �޸����ݣ�
 *    �޸���Ա��
 *    �޸����ڣ� 2015��10��14��
 * </pre>
 */
public class RestartDialog extends Dialog {
	private int i = 5;
	private Button bt_ok;
	private Button bt_after;
	private TextView tv_timing;
	private Handler handler;
	private Timer timer;
	private boolean isOk = true;
	private int type;//0 ����ZldNewActivity 1���HomeExitPageService
	@SuppressLint("HandlerLeak")
	final Handler mHandler = new Handler(){  
		public void handleMessage(Message msg) {  
			switch (msg.what) {      
			case 1: 
//				Log.e("life", "һ��һ��"+i);
				tv_timing.setText(""+i--);
//				Log.e("life", "��ʱ���ģ�"+i);
				if(isOk){
					if(i==0){
						//if(timer!=null){
						//timer.cancel();	//�رյ���ʱ��
						//}
						restart();		//��������
					}
				}else{
					cancel();
				}
				break;  
			case 2:
				if(isOk){
					Message message = new Message();
					message.what = Constant.KEEPALIVE_TIME;
					Log.e("life","RestartDialog����");
					handler.sendMessage(message);
				}
			}      
			super.handleMessage(msg);  
		}
	};  
	public RestartDialog(Context context) {
		super(context);
	}

	public RestartDialog(Context context,int theme,Handler handler,int type) {
		super(context,theme);
		this.handler = handler;
		this.type = type;
	}

	public void setI(int i) {
		this.i = i;
	}

	public boolean isOk() {
		return isOk;
	}

	public void setOk(boolean isOk) {
		this.isOk = isOk;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_restart);
		initView();
		setVeiw();
	}

	public void initTimer() {
		// TODO Auto-generated method stub
		if(timer == null){
			timer= new Timer();
		}
	}

	public void initView() {
		tv_timing = (TextView) findViewById(R.id.tv_timing);
		bt_after = (Button) findViewById(R.id.bt_after);
		bt_ok = (Button) findViewById(R.id.bt_ok);
	}

	public void setVeiw() {
		bt_after.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(timer!=null){
					timer.cancel();	//�رյ���ʱ��
				}
				if(type == 0){//����
					setOk(false);
					exitDelayedSet();	//��ʱ����ǰ,��ֹ����ͷ����ʱ�ٵ����Ի���
					afterRestart();		//��ʱ�ٵ���
				}else{
					homeDelayedSet();	
				}
				RestartDialog.this.dismiss();
			}
		});
		bt_ok.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(timer!=null){
					timer.cancel();	//�رյ���ʱ��
				}
				RestartDialog.this.dismiss();
				restart();
			}
		});
		RestartDialog.this.dismiss();
	}

	private void restart() {
		if(handler != null){
			Log.e("life","restart");
			Message message = new Message();
			message.what = Constant.RESTART_YES;
			handler.sendMessage(message);
			if(timer != null){
				timer.cancel();
			}
		}
	}   

	/**
	 * ������ʱ����
	 */
	protected void exitDelayedSet() {
		// TODO Auto-generated method stub
		if(handler != null){
			Log.e("life","exitdelayedSet_restart");
			Message message = new Message();
			message.what = Constant.EXIT_DELAYED_TIME;
			handler.sendMessage(message);
		}
	}
	/**
	 * �����ʱ����
	 */
	protected void homeDelayedSet() {
		// TODO Auto-generated method stub
		if(handler != null){
			Log.e("life","homedelayedSet_restart");
			Message message = new Message();
			message.what = Constant.HOME_DELAYED_TIME;
			handler.sendMessage(message);
		}
	}
	/**
	 * �Ժ�����
	 */
	private void afterRestart() {
		if(handler != null){
			Log.e("life","afterRestart");
			Message message = new Message();
			message.what = 2;
			mHandler.sendMessageDelayed(message, 60000);
		}
	}   


	/**
	 * ִ�ж�ʱ����
	 */
	public void satrtTiming(){
		TimerTask task = new TimerTask(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message = new Message();
				message.what = 1;
				mHandler.sendMessage(message);
			}
		};
		timer.schedule(task,0,1000); //1��һ��
	}

	public void cancle(){
		timer.cancel();
		this.cancel();
	}
}
