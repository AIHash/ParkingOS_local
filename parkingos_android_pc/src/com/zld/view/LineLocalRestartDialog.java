package com.zld.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zld.R;
import com.zld.lib.constant.Constant;

import java.util.Timer;
import java.util.TimerTask;

/**
 * <pre>
 * ����˵��: ���ط����������Ϸ������л�,������ʱ�Ի���
 * ����:	2015��10��14��
 * ������:	HZC
 *
 * ��ʷ��¼
 *    �޸����ݣ�
 *    �޸���Ա��
 *    �޸����ڣ� 2015��10��14��
 * </pre>
 */
public class LineLocalRestartDialog extends Dialog {
    private int i = 5;
    private Button bt_ok;
    private Button bt_after;
    private TextView tv_linelocal_hint;
    private TextView tv_timing;
    private Handler handler;
    private Timer timer;
    private String hint = "�л�������";
    @SuppressLint("HandlerLeak")
    final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    tv_timing.setText("" + i--);
                    if (i < 0) {
                        restart();        //��������
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public LineLocalRestartDialog(Context context) {
        super(context);
    }

    public LineLocalRestartDialog(Context context, int theme, Handler handler, boolean isLine) {
        super(context, theme);
        this.handler = handler;
        this.context = context;
        if (isLine) {//true����������      false ����������
            hint = "���ط������쳣,ȷ���л������Ϸ�������";
        } else {
            hint = "���ط�������ͨ,ȷ���л���������";
        }
    }

    Context context;
    String content, cancel, ok;

    public LineLocalRestartDialog(Context context, int theme, Handler handler, String content, String cancel, String ok) {
        super(context, theme);
        this.context = context;
        this.handler = handler;
        this.cancel = cancel;
        this.content = content;
        this.ok = ok;
    }

    public void setI(int i) {
        this.i = i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.linelocal_dialog_restart);
        initView();
        setVeiw();
        initTimer();
        satrtTiming();
    }

    public void initView() {
        tv_linelocal_hint = (TextView) findViewById(R.id.tv_linelocal_hint);
        tv_timing = (TextView) findViewById(R.id.tv_timing);
        bt_after = (Button) findViewById(R.id.bt_after);
        bt_ok = (Button) findViewById(R.id.bt_ok);
        if (TextUtils.isEmpty(content))
            tv_linelocal_hint.setText(hint);
        else
            tv_linelocal_hint.setText(content);
    }

    public void setVeiw() {
        if (!TextUtils.isEmpty(cancel)) {
            bt_after.setText(cancel);
        }
        bt_after.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (timer != null) {
                    timer.cancel();    //�رյ���ʱ��
                }
                LineLocalRestartDialog.this.dismiss();
            }
        });

        if (TextUtils.isEmpty(ok)) {
            bt_ok.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (timer != null) {
                        timer.cancel();    //�رյ���ʱ��
                    }
                    LineLocalRestartDialog.this.dismiss();
                    restart();
                }
            });
        } else {
            bt_ok.setText(ok);
            bt_ok.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction("HAND_OPEN_POLE");
                    context.sendBroadcast(intent);
                    LineLocalRestartDialog.this.dismiss();
                }
            });
        }

        LineLocalRestartDialog.this.dismiss();
    }

    public void initTimer() {
        // TODO Auto-generated method stub
        if (timer == null) {
            timer = new Timer();
        }
    }

    /**
     * ִ�ж�ʱ����
     */
    public void satrtTiming() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        };
        timer.schedule(task, 0, 1000);
    }

    public void setText(String text) {
        if (tv_linelocal_hint != null) {
            tv_linelocal_hint.setText(text);
        }
    }

    private void restart() {
        if (handler != null) {
            Message message = new Message();
            message.what = Constant.RESTART_YES;
            handler.sendMessage(message);
            if (timer != null) {
                timer.cancel();
            }
        }else{
            if (timer != null) {
                timer.cancel();
            }
            dismiss();
        }
    }

    public void cancle() {
        timer.cancel();
        this.cancel();
    }
}

