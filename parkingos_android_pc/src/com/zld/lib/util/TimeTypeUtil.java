package com.zld.lib.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class TimeTypeUtil {

	// ���������ʱ���
	public static String processTwo(long startMil, long endMil) {
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(startMil);
		Calendar c2 = Calendar.getInstance();
		c2.setTimeInMillis(endMil);
		StringBuilder time = new StringBuilder();
		int year = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
		if (year != 0) {
			time.append(year).append("��");
		}
		int month = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
		if (month != 0) {
			time.append(month).append("��");
		}
		int day = c2.get(Calendar.DAY_OF_MONTH) - c1.get(Calendar.DAY_OF_MONTH);
		if (day != 0) {
			time.append(day).append("��");
		}
		int hour = c2.get(Calendar.HOUR_OF_DAY) - c1.get(Calendar.HOUR_OF_DAY);
		time.append(hour).append("Сʱ");
		int min = c2.get(Calendar.MINUTE) - c1.get(Calendar.MINUTE);
		time.append(min).append("��");
		int sec = c2.get(Calendar.SECOND) - c1.get(Calendar.SECOND);
		time.append(sec).append("��");
		return time.toString();
	}

	// ����һ��ʱ�����ֵ--���������ʱ��͵�ǰʱ������ʱ�䣻
	public static String process(long startMil) {
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(startMil);
		Calendar c2 = Calendar.getInstance();
		StringBuilder time = new StringBuilder();
		int year = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
		if (year != 0) {
			time.append(year).append("��");
		}
		int month = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
		if (month != 0) {
			time.append(month).append("��");
		}
		int day = c2.get(Calendar.DAY_OF_MONTH) - c1.get(Calendar.DAY_OF_MONTH);
		if (day != 0) {
			time.append(day).append("��");
		}
		int hour = c2.get(Calendar.HOUR_OF_DAY) - c1.get(Calendar.HOUR_OF_DAY);
		time.append(hour).append("Сʱ");
		int min = c2.get(Calendar.MINUTE) - c1.get(Calendar.MINUTE);
		time.append(min).append("��");
		int sec = c2.get(Calendar.SECOND) - c1.get(Calendar.SECOND);
		time.append(sec).append("��");
		return time.toString();
	}

	public static String getTimeString(Long start,Long end){
		Long date = (end - start)/(3600*24);
		//		Log.e("TimeTypeUtil", "ͣ��������Ϊ��"+ date);
		Long hour = ((end-start)%86400)/3600;
		Long minute = ((end-start)%3600)/60;
		String result = "";
		if (date == 0) {
			if(hour==0)
				if(minute == 0){
					result = 1+"����";
				}else{
					result = minute+"����";
				}
			else 
				result =hour+"Сʱ"+minute+"����";
		}else{
			result = date+"��"+hour+"Сʱ"+minute+"����";
		}
		return result;
	}
	public static String getTime(Long start){
		//Date date = new Date(System.currentTimeMillis());
		Long now =System.currentTimeMillis()/1000; 
		Long date = (now - start)/(3600*24);
		//		Log.e("TimeTypeUtil", "ͣ��������Ϊ��"+ date);
		Long hour = ((now-start)%86400)/3600;
		Long minute = ((now-start)%3600)/60;
		String result = "";
		if (date == 0) {
			if(hour==0)
				result =minute+"����";
			else 
				result =hour+"Сʱ"+minute+"����";
		}else{
			result =date+"��"+hour+"Сʱ"+minute+"����";
		}
		return result;
	}

	@SuppressLint("SimpleDateFormat")
	public static String getStringTime(Long time){

		//		SimpleDateFormat dateaf = new SimpleDateFormat("yyyy.MM.dd"); 
		//		SimpleDateFormat timef = new SimpleDateFormat("HH:mm"); 
		SimpleDateFormat dateaf = new SimpleDateFormat("MM-dd"); 
		SimpleDateFormat timef = new SimpleDateFormat("HH:mm"); 
		String date = dateaf.format(time);
		String times = timef.format(time);
		String result = date+" "+times;
		return result;
	}

	public static String getEasyStringTime(Long time){
		SimpleDateFormat timef = new SimpleDateFormat("HH:mm"); 
		String times = timef.format(time);
		String result = ""+times;
		return result;
	}

	public static Long getLongTime(String user_time) { 
		String re_time = null; 
		long parseLong = 0L;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
		Date d; 
		try { 
			d = sdf.parse(user_time); 
			long l = d.getTime(); 
			re_time = String.valueOf(l); 
		} catch (ParseException e) { 
			// TODO Auto-generated catch block 
			e.printStackTrace(); 
		} 
		if(re_time != null){
			parseLong = Long.parseLong(re_time);
		}
		return parseLong; 
	} 

	public static String getTodayDate(Date date){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String str=sdf.format(date);
		return str;
	}

	public static String getTodayTime(Date date){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String str=sdf.format(date);
		return str;
	}

	public static String getComplexStringTime(Long time){
		SimpleDateFormat timef = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
		String times = timef.format(time);
		String result = ""+times;
		return result;
	}

	public static String getFutureDate(int days, String format){
		SimpleDateFormat formatDate = new SimpleDateFormat(format);  //�ַ���ת��
		Calendar c = Calendar.getInstance();  
		//new Date().getTime();����ǻ�õ�ǰ���Ե�ʱ�䣬��Ҳ���Ի���һ�������ʱ��
		c.setTimeInMillis(new Date().getTime());
		c.add(Calendar.DATE, days);//��������
		Date date= new Date(c.getTimeInMillis()); //��cת����Date
		String date1 =  formatDate.format(date);
		if ("0".equals(date1.substring(0,1))){
			return date1.substring(1);
		}
		return date1;
	}

	public static boolean compareDate(String begins){
		boolean databoolean = false;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			long begin = df.parse(begins).getTime();
			long nowtime = new Date().getTime();
			int dateday = (int)(nowtime - begin) / (1000 * 60 * 60 * 24);
			if(dateday>14||dateday==14){
				databoolean = true;
			}
		} catch (Exception e) {
			Log.i("Exception", e.getMessage()+"");
			e.printStackTrace();
		}
		return databoolean;
	}

	public static boolean isMthUserExpire(String exptime) {
		// TODO Auto-generated method stub
		if (exptime != null){
			int iExptime = Integer.parseInt(exptime);
			if (iExptime <= 5){
				return true;
			}
		}
		return false;
	}

	/**
	 * ���㴫��ʱ���뱾��ʱ���ֵ��
	 * 
	 * @param time
	 * @return
	 */
	public static Long getDifferenceTime(Long time) {
		Long now = System.currentTimeMillis();
		return time - now;
	}

	/**
	 * �ж϶���ʱ���Ƿ񳬹������
	 * @param context
	 * @return
	 */
	public static Boolean isOffFiveMinutes(Context context){
		Long now = System.currentTimeMillis();
		com.zld.lib.util.SharedPreferencesUtils.setParam(
				context.getApplicationContext(), "zld_config", "netoff", System.currentTimeMillis());
		Long netOffTime = SharedPreferencesUtils.getParam(
				context.getApplicationContext(), "zld_config", "netoff", 0L);
		if ((now - netOffTime) >1000*60*5) {
			return true;
		}
		return false;
	}
	public static String getNowTime(){
		SimpleDateFormat timef = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
//		String times = timef.format(System.currentTimeMillis());
		Date date = new Date();
		String result = timef.format(date);
		return result;
	}
	public static String getNowTimeMIN(){
		SimpleDateFormat timef = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
//		String times = timef.format(System.currentTimeMillis());
		Date date = new Date();
		String result = timef.format(date);
		return result;
	}
}
