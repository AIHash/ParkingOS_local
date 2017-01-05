package com.zld.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class ContinueFTP {
	
	public enum UploadStatus {  
		 Create_Directory_Fail,      //Զ�̷�������ӦĿ¼����ʧ��  
		 Create_Directory_Success,   //Զ�̷���������Ŀ¼�ɹ�  
		 Upload_New_File_Success,    //�ϴ����ļ��ɹ�  
		 Upload_New_File_Failed,     //�ϴ����ļ�ʧ��  
		 File_Exits,                 //�ļ��Ѿ�����  
		 Remote_Bigger_Local,        //Զ���ļ����ڱ����ļ�  
		 Upload_From_Break_Success,  //�ϵ������ɹ�  
		 Upload_From_Break_Failed,   //�ϵ�����ʧ��  
		 Delete_Remote_Faild;        //ɾ��Զ���ļ�ʧ��  
	}  

	
	private FTPClient ftpClient = new FTPClient();
	
	public ContinueFTP(){
		//���ý�������ʹ�õ����������������̨
		this.ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
	}
	
	/**
	 * ���ӵ�FTP������
	 * @param hostname ������
	 * @param port �˿�
	 * @param username �û���
	 * @param password ����
	 * @return �Ƿ����ӳɹ�
	 * @throws IOException
	 */
	public boolean connect(String hostname,int port,String username,String password) throws IOException{
		ftpClient.connect(hostname, port);
		if(FTPReply.isPositiveCompletion(ftpClient.getReplyCode())){
			if(ftpClient.login(username, password)){
				return true;
			}
		}
		disconnect();
		return false;
	}
	
	/**
	 * ��FTP�������������ļ�
	 * @param remote Զ���ļ�·��
	 * @param local �����ļ�·��
	 * @return �Ƿ�ɹ�
	 * @throws IOException
	 */
	public boolean download(String remote,String local) throws IOException{
		ftpClient.enterLocalPassiveMode();
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		boolean result;
		File f = new File(local);
		FTPFile[] files = ftpClient.listFiles(remote);
		if(files.length != 1){
			System.out.println("Զ���ļ���Ψһ");
			return false;
		}
		long lRemoteSize = files[0].getSize();
		if(f.exists()){
			OutputStream out = new FileOutputStream(f,true);
			System.out.println("�����ļ���СΪ:"+f.length());
			if(f.length() >= lRemoteSize){
				System.out.println("�����ļ���С����Զ���ļ���С��������ֹ");
				return false;
			}
			ftpClient.setRestartOffset(f.length());
			result = ftpClient.retrieveFile(remote, out);
			out.close();
		}else {
			OutputStream out = new FileOutputStream(f);
			result = ftpClient.retrieveFile(remote, out);
			out.close();
		}
		return result;
	}
	
	/**
	 * �ϴ��ļ���FTP��������֧�ֶϵ�����
	 * @param local �����ļ����ƣ�����·��
	 * @param remote Զ���ļ�·����ʹ��/home/directory1/subdirectory/file.ext ����Linux�ϵ�·��ָ����ʽ��֧�ֶ༶Ŀ¼Ƕ�ף�֧�ֵݹ鴴�������ڵ�Ŀ¼�ṹ
	 * @return �ϴ����
	 * @throws IOException
	 */
	public UploadStatus upload(String local,String remote) throws IOException{
		//����PassiveMode����
		ftpClient.enterLocalPassiveMode();
		//�����Զ��������ķ�ʽ����
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		UploadStatus result;
		//��Զ��Ŀ¼�Ĵ���
		String remoteFileName = remote;
		if(remote.contains("/")){
			remoteFileName = remote.substring(remote.lastIndexOf("/")+1);
			String directory = remote.substring(0,remote.lastIndexOf("/")+1);
			if(!directory.equalsIgnoreCase("/")&&!ftpClient.changeWorkingDirectory(directory)){
				//���Զ��Ŀ¼�����ڣ���ݹ鴴��Զ�̷�����Ŀ¼
				int start=0;
				int end = 0;
				if(directory.startsWith("/")){
					start = 1;
				}else{
					start = 0;
				}
				end = directory.indexOf("/",start);
				while(true){
					String subDirectory = remote.substring(start,end);
					if(!ftpClient.changeWorkingDirectory(subDirectory)){
						if(ftpClient.makeDirectory(subDirectory)){
							ftpClient.changeWorkingDirectory(subDirectory);
						}else {
							System.out.println("����Ŀ¼ʧ��");
							return UploadStatus.Create_Directory_Fail;
						}
					}
					
					start = end + 1;
					end = directory.indexOf("/",start);
					
					//�������Ŀ¼�Ƿ񴴽����
					if(end <= start){
						break;
					}
				}
			}
		}
		
		//���Զ���Ƿ�����ļ�
		FTPFile[] files = ftpClient.listFiles(remoteFileName);
		if(files.length == 1){
			long remoteSize = files[0].getSize();
			File f = new File(local);
			long localSize = f.length();
			if(remoteSize==localSize){
				return UploadStatus.File_Exits;
			}else if(remoteSize > localSize){
				return UploadStatus.Remote_Bigger_Local;
			}
			
			//�����ƶ��ļ��ڶ�ȡָ��,ʵ�ֶϵ�����
			InputStream is = new FileInputStream(f);
			if(is.skip(remoteSize)==remoteSize){
				ftpClient.setRestartOffset(remoteSize);
				if(ftpClient.storeFile(remote, is)){
					return UploadStatus.Upload_From_Break_Success;
				}
			}
			
			//����ϵ�����û�гɹ�����ɾ�����������ļ��������ϴ�
			if(!ftpClient.deleteFile(remoteFileName)){
				return UploadStatus.Delete_Remote_Faild;
			}
			is = new FileInputStream(f);
			if(ftpClient.storeFile(remote, is)){	
				result = UploadStatus.Upload_New_File_Success;
			}else{
				result = UploadStatus.Upload_New_File_Failed;
			}
			is.close();
		}else {
			InputStream is = new FileInputStream(local);
			if(ftpClient.storeFile(remoteFileName, is)){
				result = UploadStatus.Upload_New_File_Success;
			}else{
				result = UploadStatus.Upload_New_File_Failed;
			}
			is.close();
		}
		return result;
	}
	/**
	 * �Ͽ���Զ�̷�����������
	 * @throws IOException
	 */
	public void disconnect() throws IOException{
		if(ftpClient.isConnected()){
			ftpClient.disconnect();
		}
	}
	
	public static void main(String[] args) {
		ContinueFTP myFtp = new ContinueFTP();
		try {
			myFtp.connect("118.192.91.210", 21, "tcbftp", "tqserver");
			System.out.println(myFtp.upload("c:\\err.txt", "/fff/aaas.txt"));
			myFtp.disconnect();
		} catch (IOException e) {
			System.out.println("����FTP����"+e.getMessage()); 
			e.printStackTrace();
		}
	}
}
