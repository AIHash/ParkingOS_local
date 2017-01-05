/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015��4��15�� 
 * 
 *******************************************************************************/ 
package com.zld.lib.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * <pre>
 * ����˵��: 
 * ����:	2015��4��15��
 * ������:	HZC
 * 
 * ��ʷ��¼
 *    �޸����ݣ�
 *    �޸���Ա��
 *    �޸����ڣ� 2015��4��15��
 * </pre>
 */
public class AppInfoUtil {
	public static final String TAG = "AppInfoUtil";
	private static Build build;

	/**
	 * ����˵������ȡ�豸�ͺ�
	 */
	@SuppressWarnings("static-access")
	public static String getEquipmentModel() {
		if(build == null){
			build = new Build();
		}
		return build.MODEL;
	}

	/** 
	 * ��ȡ��ǰӦ�ó���İ汾��versionCode
	 * @return
	 */
	public static String getVersionCode(Activity activity) {
		try {
			PackageManager manager = activity.getPackageManager();
			PackageInfo info = manager.getPackageInfo(activity.getPackageName(), 0);
			return String.valueOf(info.versionCode);
		} catch (Exception e) {
			e.printStackTrace();
			return "�汾��δ֪";
		}
	}

	/**
	 * ��ȡ��ǰӦ�ó���İ汾��versionName
	 * @return
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return String.valueOf(info.versionName);
		} catch (Exception e) {
			e.printStackTrace();
			return "�汾��δ֪";
		}
	}

	public static void displayBriefMemory(Context context) {        
		final ActivityManager activityManager = (ActivityManager)
				context.getSystemService(Context.ACTIVITY_SERVICE);    
		ActivityManager.MemoryInfo   info = new ActivityManager.MemoryInfo();   
		activityManager.getMemoryInfo(info);    
		Log.i(TAG,"ϵͳʣ���ڴ�:"+(info.availMem >> 10)+"k");   
		Log.i(TAG,"ϵͳ�Ƿ��ڵ��ڴ����У�"+info.lowMemory);
		Log.i(TAG,"��ϵͳʣ���ڴ����"+info.threshold+"ʱ�Ϳ��ɵ��ڴ�����");
	}
	 
	public static long getAvailMemory() {// ��ȡandroid��ǰ�����ڴ��С 
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		long memSize = memoryInfo.availMem;
//		String leftMemSize = Formatter.formatFileSize(context, memSize);
		return memSize;
	}

	 public static long getTotalRam(){
		 FileInputStream fis = null;
		 BufferedReader br = null;
		 try {
			 File file = new File("/proc/meminfo");
			 fis = new FileInputStream(file);
		     br = new BufferedReader(new InputStreamReader(fis));
		     String totalRam = br.readLine();
		     StringBuffer sb = new StringBuffer();
		     char[] cs = totalRam.toCharArray();
		     for (char c : cs) {
		         if(c>='0' && c<='9'){
		              sb.append(c);
		          }
		      }
		      long result = Long.parseLong(sb.toString())*1024;
	          return result;
		   } catch (Exception e) {
		       e.printStackTrace();
		       return 0;
		   }finally{
			   if(fis!=null){
				   try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			   }
			   if(br!=null){
				   try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			   }
		   }
	}	
	    /**
	     * ��ȡ�ֻ��ڲ�ʣ��洢�ռ�
	     * 
	     * @return
	     */
	    public static long getAvailableInternalMemorySize() {
	        File path = Environment.getDataDirectory();
	        StatFs stat = new StatFs(path.getPath());
	        long blockSize = stat.getBlockSize();
	        long availableBlocks = stat.getAvailableBlocks();
	        return availableBlocks * blockSize/1024/1024;
	    }

	    /**
	     * ��ȡ�ֻ��ڲ��ܵĴ洢�ռ�
	     * 
	     * @return
	     */
	    public static long getTotalInternalMemorySize() {
	        File path = Environment.getDataDirectory();
	        StatFs stat = new StatFs(path.getPath());
	        long blockSize = stat.getBlockSize();
	        long totalBlocks = stat.getBlockCount();
	        return totalBlocks * blockSize/1024/1024;
	    }
	    
	    /** ��ȡϵͳ���ڴ�
	     * 
	     * @param context �ɴ���Ӧ�ó��������ġ�
	     * @return ���ڴ��λΪB��
	     */
	    public static long getTotalMemorySize(Context context) {
	        String dir = "/proc/meminfo";
	        try {
	            FileReader fr = new FileReader(dir);
	            BufferedReader br = new BufferedReader(fr, 2048);
	            String memoryLine = br.readLine();
	            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
	            br.close();
	            return Integer.parseInt(subMemoryLine.replaceAll("\\D+", "")) * 1024l;
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return 0;
	    }

	    /**
	     * ��ȡ��ǰ�����ڴ棬�����������ֽ�Ϊ��λ��
	     * 
	     * @param context �ɴ���Ӧ�ó��������ġ�
	     * @return ��ǰ�����ڴ浥λΪB��
	     */
	    public static long getAvailableMemory(Context context) {
	        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
	        am.getMemoryInfo(memoryInfo);
	        return memoryInfo.availMem;
	    }

}
