package com.mserver.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import org.springframework.context.ApplicationContext;

/**
 * ÿ���µĵ�һ���2��00ִ�зֺ����
 * @author Administrator
 *
 */
public class TimerManager {
	private static final long PERIOD_DAY = 24*60*60 * 1000;

	public TimerManager(ApplicationContext ctx) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 2);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date date = calendar.getTime();
		// ��һ��ִ�ж�ʱ�����ʱ��
		// �����һ��ִ�ж�ʱ�����ʱ�� С�� ��ǰ��ʱ��
		// ��ʱҪ�� ��һ��ִ�ж�ʱ�����ʱ�� ��һ�죬�Ա���������¸�ʱ���ִ�С��������һ�죬���������ִ�С�
		if (date.before(new Date())) {
			date = this.addDay(date, 1);
			//System.out.println("��һ�죬һ����2��00ִ��");
		}
		Timer timer = new Timer();
		BonusComputeTask task = new BonusComputeTask(ctx);
		// ����ָ����������ָ����ʱ�俪ʼ�����ظ��Ĺ̶��ӳ�ִ�С�
		timer.schedule(task, new Date(), PERIOD_DAY);
		//timer.schedule(task, date, PERIOD_DAY);
	}

	// ���ӻ��������

	public Date addDay(Date date, int num) {
		Calendar startDT = Calendar.getInstance();
		startDT.setTime(date);
		startDT.add(Calendar.DAY_OF_MONTH, num);
		return startDT.getTime();
	}
}
