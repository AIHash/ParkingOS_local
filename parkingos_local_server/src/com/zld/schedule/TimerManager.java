package com.zld.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.zld.CustomDefind;
import com.zld.service.DataBaseService;
import com.zld.schedule.DeleteSchedule;

/**
 * ÿ���µĵ�һ���2��00ִ�зֺ����
 * @author Administrator
 *
 */
public class TimerManager {
	private static final long PERIOD_DAY = 24*60*60 * 1000;

	public TimerManager(DataBaseService dataBaseService) {
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.HOUR_OF_DAY, 2);
//		calendar.set(Calendar.MINUTE, 0);
//		calendar.set(Calendar.SECOND, 0);
//		Date date = calendar.getTime();
//		// ��һ��ִ�ж�ʱ�����ʱ��
//		// �����һ��ִ�ж�ʱ�����ʱ�� С�� ��ǰ��ʱ��
//		// ��ʱҪ�� ��һ��ִ�ж�ʱ�����ʱ�� ��һ�죬�Ա���������¸�ʱ���ִ�С��������һ�죬���������ִ�С�
//		if (date.before(new Date())) {
//			date = this.addDay(date, 1);
//			//System.out.println("��һ�죬һ����2��00ִ��");
//		}
//		Timer timer = new Timer();
//		ParkSchedule task = new ParkSchedule(dataBaseService);
		// ����ָ����������ָ����ʱ�俪ʼ�����ظ��Ĺ̶��ӳ�ִ�С�
//		timer.schedule(task, new Date(), PERIOD_DAY);
		System.out.println(" ////////////////start//////////////////////");
		 ScheduledExecutorService executor = Executors.newScheduledThreadPool(6);
		 DeleteSchedule task1 = new DeleteSchedule();//��ʱɾ��ͼƬ

		    executor.scheduleAtFixedRate(
		      task1,
		      60,
		      60*60*12,
		      TimeUnit.SECONDS);

		 SyncFromLineSchedule task2 = new SyncFromLineSchedule(dataBaseService);//��ʱͬ���ƶ˵��޸�

		    executor.scheduleAtFixedRate(
		      task2,
		      45000L,
		      Long.valueOf(CustomDefind.SYNCFROM),
		      TimeUnit.MILLISECONDS);
		    SyncToLineSchedule task3 = new SyncToLineSchedule(dataBaseService);//��ʱ�ϴ����ݸ��ƶ�

		    executor.scheduleAtFixedRate(
		      task3,
		      40000L,
		      Long.valueOf(CustomDefind.SYNCTO),
		      TimeUnit.MILLISECONDS);
		    AutoUpdateSchedule task4 = new AutoUpdateSchedule(dataBaseService);//��ʱ��ȡ���������°�

		    executor.scheduleAtFixedRate(
		      task4,
		      36000L,
		      Long.valueOf(CustomDefind.AUTO),
		      TimeUnit.MILLISECONDS);

//		timer.schedule(task, date, PERIOD_DAY);
	}

	// ���ӻ��������

	public Date addDay(Date date, int num) {
		Calendar startDT = Calendar.getInstance();
		startDT.setTime(date);
		startDT.add(Calendar.DAY_OF_MONTH, num);
		return startDT.getTime();
	}
}
