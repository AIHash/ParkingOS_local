package com.zld.schedule;

import java.io.File;

import org.apache.log4j.Logger;

import com.zld.utils.TimeTools;

public class DeleteSchedule implements Runnable {
	private static Logger log = Logger.getLogger(DeleteSchedule.class);
	public void run() {
		try {
			log.info("��ʼɾ������ͼƬ��ֻ����30���ͼƬ");
			  String newDir2 = "C:\\carpics";
		    	File file = new File("C:\\carpics");
				if(file.isDirectory()){
					File[] listFiles = file.listFiles();
					for (File f : listFiles) {
						System.out.println(f.getName());
						int fileint = Integer.parseInt(f.getName());
						String curTime = TimeTools.getTimeStr_yyyy_MM_dd(System.currentTimeMillis()-30*24*60*60*1000L);
						String[] time = curTime.split("-");
						int cu = Integer.parseInt(time[0]+time[1]+time[2]);
						System.out.println(fileint +":"+cu);
						if(fileint<cu){
							boolean success = deleteDir(f);
							System.out.println(success);
						}
					}
				}
		} catch (Exception e) {
			log.info(e.getMessage());
			e.printStackTrace();
		}
	}
	   
	    private static boolean deleteDir(File dir) {
	        if (dir.isDirectory()) {
	            String[] children = dir.list();
	            //�ݹ�ɾ��Ŀ¼�е���Ŀ¼��
	            for (int i=0; i<children.length; i++) {
	                boolean success = deleteDir(new File(dir, children[i]));
	                if (!success) {
	                    return false;
	                }
	            }
	        }
	        // Ŀ¼��ʱΪ�գ�����ɾ��
	        return dir.delete();
	    }
	    public static void main(String[] args) {
//	        doDeleteEmptyDir("new_dir1");
	        String newDir2 = "C:\\carpics";
	    	File file = new File("C:\\carpics");
			if(file.isDirectory()){
				File[] listFiles = file.listFiles();
				for (File f : listFiles) {
					System.out.println(f.getName());
					int fileint = Integer.parseInt(f.getName());
					String curTime = TimeTools.getTimeStr_yyyy_MM_dd(System.currentTimeMillis()-30*24*60*60*1000L);
					String[] time = curTime.split("-");
					int cu = Integer.parseInt(time[0]+time[1]+time[2]);
					System.out.println(fileint +":"+cu);
					if(fileint<cu){
						boolean success = deleteDir(f);
						System.out.println(success);
					}
				}
			}
	    }
}
