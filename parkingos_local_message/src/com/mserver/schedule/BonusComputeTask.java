package com.mserver.schedule;

import java.util.Calendar;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.mserver.service.PgService;

public class BonusComputeTask extends TimerTask {
	
	ApplicationContext ctx;
	
	public BonusComputeTask(ApplicationContext ctx ){
		this.ctx = ctx;
	}

	private static Logger log = Logger.getLogger(BonusComputeTask.class);

	@Override
	public void run() {
		// TODO Auto-generated method stub

		Calendar calendar = Calendar.getInstance();
		log.error("��ʼִ�зֺ춨ʱ����");
		//ÿ���µĵ�һ��Ӌ��
		if(calendar.get(Calendar.DAY_OF_MONTH)==1){
			log.error("��ʼִ��");
			start();
		}else {
			log.error("����ִ��ʱ�䣡");
		}
//		start();
	}

	/**
	 * ��ʼͳ��
	 */
	@SuppressWarnings("rawtypes")
	private void start(){
		PgService userService = (PgService) ctx.getBean("userService");
		//log.error(userService);
	}
	
}
