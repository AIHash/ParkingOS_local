package com.zld.schedule;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;

import org.apache.log4j.Logger;

import pay.Constants;

import com.zld.CustomDefind;
import com.zld.service.DataBaseService;
import com.zld.utils.AESEncryptor;
import com.zld.utils.HttpProxy;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.rarfile.FileHeader;

public class AutoUpdateSchedule implements Runnable {
	
	DataBaseService dataBaseService;
	
	public AutoUpdateSchedule(DataBaseService dataBaseService ){
		this.dataBaseService = dataBaseService;
	}
	private static Logger log = Logger.getLogger(AutoUpdateSchedule.class);
	public void run() {
		log.info("�Զ����¿�ʼͬ��");
		OutputStream os = null;
		InputStream ins = null;
		String lineTxt = "10";
		InputStreamReader read = null;
   	 	try {//����try�Ǳ�֤���񲻻���Ϊ�쳣��ȡ����ʱ��
	   	 	String token = "";
	   	 	//��ȡtoken
			HttpProxy httpProxy = new HttpProxy();
			String mes = AESEncryptor.encrypt("0123456789ABCDEFtingcaidfjalsjffdaslfkdafjdaljdf", CustomDefind.SECRET+":"+CustomDefind.COMID);
	   	 	token = httpProxy.doGet(CustomDefind.DOMAIN+"/syncInter.do?action=getToken&mes="+mes);
	   	 	log.info("�Զ����¿�ʼͬ��,���� ��"+token);
	   	 	if(token!=null&&!token.equals("")){
				int r = dataBaseService.update("update sync_time_tb set token=? ", new Object[]{token});
				log.info("�Զ����¿�ʼͬ��,����token ��"+r);
	   	 	}
			//��ȡ����ʱ��
			String limit_time = httpProxy.doGet(CustomDefind.DOMAIN+"/syncInter.do?action=getlimitday&comid="+CustomDefind.COMID+"&token="+token);
			if(limit_time!=null&Long.parseLong(limit_time)>0){
				int r = dataBaseService.update("update sync_time_tb set maxid=? where id = ?", new Object[]{Long.parseLong(limit_time),4});//��ʱmaxidΪ����ʱ��
				if(r==0){
					dataBaseService.update("insert into sync_time_tb values(?,?)", new Object[]{4,System.currentTimeMillis()/1000+30*24*60*60});//��ʱmaxidΪ����ʱ��
				}
			}
   	 		File version = new File(CustomDefind.TOMCATHOMT+"version\\version.txt");
   	 		log.error(CustomDefind.TOMCATHOMT+"version\\version.txt");
   	 		if(version.isFile() && version.exists()){ //�ж��ļ��Ƿ����
   	 			read = new InputStreamReader(
	            new FileInputStream(version));//���ǵ������ʽ
	            BufferedReader bufferedReader = new BufferedReader(read);
	            lineTxt = bufferedReader.readLine();
	            read.close();
   	 		}
   	 		deleteDir(new File(CustomDefind.TOMCATHOMT+"update\\"));
		 	URL url = new URL(CustomDefind.DOMAIN+"/syncInter.do?action=updateLocal&version="+lineTxt+"&comid="+CustomDefind.COMID);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(5000);
			conn.setConnectTimeout(5000);
			conn.setDoInput(true);  //����������
			conn.setDoOutput(true); //���������
			ins = conn.getInputStream();
			int leng = ins.available();
			System.out.println(leng+":-------------------------------");
			if(leng>0){
				File f = new File(CustomDefind.TOMCATHOMT+"update");
				if(!f.exists()){
					f.mkdirs();
				}
	   	 		File file = new File(CustomDefind.TOMCATHOMT+"update\\update.rar");
	   	 		os = new FileOutputStream(file);
				int bytesRead = 0;
				byte[] buffer = new byte[8192];
				while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
					os.write(buffer, 0, bytesRead);
				}
				unRarFile(CustomDefind.TOMCATHOMT+"update\\update.rar", CustomDefind.TOMCATHOMT+"update");
				File updatebat = new File(CustomDefind.TOMCATHOMT+"update\\update\\update.bat");
				if(new File("c:\\tomcat\\update\\update\\").listFiles().length>0&&updatebat.exists()){
					try {
						//ִ�������ű�
						Runtime.getRuntime().exec("cmd /c start "+CustomDefind.TOMCATHOMT+"update\\update\\update.bat");
//						Thread.sleep(3000);
//						deleteDir(new File(CustomDefind.TOMCATHOMT+"update\\update\\"));
//						updateversion();
//						new HttpProxy().doGet(CustomDefind.DOMAIN+"/syncInter.do?action=updateversion&comid="+CustomDefind.COMID+"&version="+(Integer.parseInt(lineTxt)+1));
					} catch ( IOException e) {
						
					}
//					Runtime.getRuntime().exec("cmd /c start "+CustomDefind.TOMCATHOMT+"updatestartup.bat");
				}else{
					deleteDir(new File(CustomDefind.TOMCATHOMT+"update\\update\\"));
				}
				
			}
   	 	}catch (Exception e) {
   	 		e.printStackTrace();
   	 	}finally{
	   	 	if(read!=null)
				try {
					read.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
   	 		if(os!=null)
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
   	 		if(ins!=null)
				try {
					ins.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
   	 	}
	}

	/**
	 * ɾ���ļ���
	 * @param dir
	 * @return
	 */
	private static boolean deleteDir(File dir) {
	       if (dir.exists()&&dir.isDirectory()) {
	    	   String[] children = dir.list();
               for (int i=0; i<children.length; i++) {
                   boolean success = deleteDir(new File(dir, children[i]));
                   if (!success) {
                       return false;
                   }
               }
	        }
	        // Ŀ¼��ʱΪ�գ�����ɾ��
//	       return true;
	       if(dir.exists()){
	    	   return dir.delete();
	       }
	       return true;
	    }
    /**  
     * �� ѹrar��ʽ��ѹ���ļ���ָ��Ŀ¼��  
     * @param srcRarPath ѹ ���ļ�  
     * @param srcRarPath �� ѹĿ¼  
     * @throws Exception  
     */   
    public static void unRarFile(String srcRarPath, String dstDirectoryPath) {
        if (!srcRarPath.toLowerCase().endsWith(".rar")) {
            System.out.println("��rar�ļ���");
            return;
        }
        File dstDiretory = new File(dstDirectoryPath);
        if (!dstDiretory.exists()) {// Ŀ��Ŀ¼������ʱ���������ļ���
            dstDiretory.mkdirs();
        }
        Archive a = null;
        try {
            a = new Archive(new File(srcRarPath));
            if (a != null) {
                a.getMainHeader().print(); // ��ӡ�ļ���Ϣ.
                FileHeader fh = a.nextFileHeader();
                while (fh != null) {
                    if (fh.isDirectory()) { // �ļ��� 
                        File fol = new File(dstDirectoryPath + File.separator
                                + fh.getFileNameString());
                        fol.mkdirs();
                    } else { // �ļ�
                        File out = new File(dstDirectoryPath + File.separator
                                + fh.getFileNameString().trim());
                        //System.out.println(out.getAbsolutePath());
                        try {// ֮������ôдtry������Ϊ��һ�����������쳣����Ӱ�������ѹ. 
                            if (!out.exists()) {
                                if (!out.getParentFile().exists()) {// ���·�����ܶ༶��������Ҫ������Ŀ¼. 
                                    out.getParentFile().mkdirs();
                                }
                                out.createNewFile();
                            }
                            FileOutputStream os = new FileOutputStream(out);
                            a.extractFile(fh, os);
                            os.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    fh = a.nextFileHeader();
                }
                a.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void updateversion() throws IOException {
    	FileOutputStream fos = null;
    	BufferedOutputStream pw = null;
    	BufferedReader readerseg = null;
    	try{
    		File f = new File(CustomDefind.TOMCATHOMT+"version");
    		if(!f.exists()){
    			f.mkdirs();
    		}
    		File ver = new File(CustomDefind.TOMCATHOMT+"version\\version.txt");
        	
        	if(!ver.exists()){
        		ver.createNewFile();
        		fos = new FileOutputStream(ver);
        		pw=new BufferedOutputStream(fos); 
                pw.write("11".getBytes());
                pw.flush();
                pw.close();
        	}else{
        		readerseg=new BufferedReader(new FileReader(ver));
        		String s = null;
        		while((s=readerseg.readLine())!=null){
        			int newver = (Integer.parseInt(s)+1);
        			String sb = newver+"";
        			fos = new FileOutputStream(ver);
        			pw=new BufferedOutputStream(fos); 
        	        pw.write(sb.getBytes());
        	            pw.flush();
        	            pw.close();
        		}
        	}
    	}catch (Exception e) {
			// TODO: handle exception
		}finally{
//			FileOutputStream fos = null;
//	    	PrintWriter pw = null;
//	    	BufferedReader readerseg = null;
			if(pw!=null){
	    		pw.flush();
	    		pw.close();
	    	}
	    	if(fos!=null)
	    		fos.close();
	    	
	    	if(readerseg!=null)
	    		readerseg.close();
		}
    	
		
	}
    public static void main(String[] args) {
    	OutputStream os = null;
		InputStream ins = null;
		String lineTxt = "10";
		InputStreamReader read = null;
    	try{
    	File version = new File(CustomDefind.TOMCATHOMT+"version\\version.txt");
	 		if(version.isFile() && version.exists()){ //�ж��ļ��Ƿ����
	 			read = new InputStreamReader(
            new FileInputStream(version));//���ǵ������ʽ
            BufferedReader bufferedReader = new BufferedReader(read);
            lineTxt = bufferedReader.readLine();
            read.close();
	 		}
	 	URL url = new URL(CustomDefind.DOMAIN+"/syncInter.do?action=updateLocal&version="+lineTxt+"&comid="+CustomDefind.COMID);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(5000);
		conn.setConnectTimeout(5000);
		conn.setDoInput(true);  //����������
		conn.setDoOutput(true); //���������
		ins = conn.getInputStream();
		int leng = ins.available();
		System.out.println(leng+":-------------------------------");
		if(leng>0){
			File f = new File(CustomDefind.TOMCATHOMT+"update");
			if(!f.exists()){
				f.mkdirs();
			}
   	 		File file = new File(CustomDefind.TOMCATHOMT+"update\\update.rar");
   	 		os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			unRarFile(CustomDefind.TOMCATHOMT+"update\\update.rar", CustomDefind.TOMCATHOMT+"update");
			File updatebat = new File(CustomDefind.TOMCATHOMT+"update\\update\\update.bat");
			if(new File("c:\\tomcat\\update\\update\\").listFiles().length>0&&updatebat.exists()){
//				Runtime.getRuntime().exec("cmd /k start /b C:\\Users\\drh\\Desktop\\shutdown.bat");
				try {
					Runtime.getRuntime().exec("cmd /c start "+CustomDefind.TOMCATHOMT+"update\\update\\update.bat");
					Thread.sleep(3000);
					deleteDir(new File(CustomDefind.TOMCATHOMT+"update\\update\\"));
					updateversion();
					new HttpProxy().doGet(CustomDefind.DOMAIN+"/syncInter.do?action=updateversion&comid="+CustomDefind.COMID+"&version="+(Integer.parseInt(lineTxt)+1));
				} catch ( IOException e) {
					
				}
				Runtime.getRuntime().exec("cmd /c start "+CustomDefind.TOMCATHOMT+"updatestartup.bat");
			}else{
				deleteDir(new File(CustomDefind.TOMCATHOMT+"update\\update\\"));
			}
			
			
		}
	 	}catch (Exception e) {
	 		e.printStackTrace();
	 	}finally{
   	 	if(read!=null)
			try {
				read.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 		if(os!=null)
			try {
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 		if(ins!=null)
			try {
				ins.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 	}
	}
}
