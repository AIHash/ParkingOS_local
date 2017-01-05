package com.zld.lib.http;

//import com.androidquery.callback.AjaxStatus;
import com.zld.bean.LoginInfo;

/**
 * ������ͨ�Żص���
 * @author  lulogfei
 *
 */
public interface HttpCallBack {
	/**
	 * ������ͨ�ųɹ��Ļص�
	 * @param object �ص�����
	 * @param url �����ʾ
	 * @return
	 */
	public boolean doSucess(String url, String object);
	
	/**
	 * ������ͨ�ųɹ��Ļص�
	 * @param object �ص�����
	 * @param url �����ʾ
	 * @param worksiteId ͨ��id/carnumber
	 * @return
	 */
	public boolean doSucess(String url, String object, String str);
	
	/**
	 * ������ͨ�ųɹ��Ļص�
	 * @param object �ص�����
	 * @param url �����ʾ
	 * @param worksiteId ͨ��id/carnumber
	 * @return
	 */
	public boolean doSucess(String url, String object, String str1, String str2);
	
	/**
	 * ������ͨ�ųɹ��Ļص�
	 * @param object �ص�����
	 * @param url �����ʾ
	 * @param buffer ��¼��Ϣ
	 * @return
	 */
	public boolean doSucess(String url, String object, byte[] buffer);
	
	/**
	 * ������ͨ�ųɹ��Ļص�
	 * @param object �ص�����
	 * @param url �����ʾ
	 * @param buffer ��¼��Ϣ
	 * @param username �˺�
	 * @param password ����
	 * @return
	 */
	public boolean doSucess(String url, String object, byte[] buffer,String username,String password);

	/**
	 * ������ͨ�ųɹ��Ļص�
	 * @param object �ص�����
	 * @param url �����ʾ
	 * @param username �˺�
	 * @param password ����
	 * @param info ��¼����Ϣ
	 * @return
	 */
	public boolean doSucess(String url,String object,String username,String password,LoginInfo info);
	
	
	/**
	 * ��ͨ��id��ͨ�Żص�
	 * @param url
	 * @param isSingle 
	 * @param passid
	 * @param object
	 * @param object2 
	 * @param i 
	 * @return
	 */
	public boolean doSucess(String url,boolean isSingle, String passid, String object, int i, String object2);
	
	/**
	 * ������ͨ���쳣�Ļص�
	 */
//	public boolean doFailure(String url, AjaxStatus status);
	public boolean doFailure(String url, String status);

	public boolean doSucess(String requestUrl, byte[] buffer);

	public boolean doSucess(String requestUrl, String username2, String password2,
			LoginInfo info2);
	
	public boolean doSucess(String requestUrl,byte[] buffer, String username2, String password2);

	public void timeout(String url);

	public void timeout(String url, String str);
	
	public void timeout(String url, String str,String str2);
}