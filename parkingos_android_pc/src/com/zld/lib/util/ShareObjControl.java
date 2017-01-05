/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015��3��16�� 
 * 
 *******************************************************************************/ 
package com.zld.lib.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import com.zld.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

/**
 * <pre>
 * ����˵��: 
 * ����:	2015��3��16��
 * ������:	HZC
 * 
 * ��ʷ��¼
 *    �޸����ݣ�
 *    �޸���Ա��
 *    �޸����ڣ� 2015��3��16��
 * </pre>
 */
public class ShareObjControl {

	/**
	 * desc:�������

	 * @param context
	 * @param key 
	 * @param obj Ҫ����Ķ���ֻ�ܱ���ʵ����serializable�Ķ���
	 * modified:	
	 */
	public static void saveObject(Context context,String fileName,String key ,Object obj){
		try {
			// �������
			SharedPreferences.Editor sharedata = ((application)context.getApplicationContext())
					.getSharedPreferences(fileName, Context.MODE_MULTI_PROCESS).edit();
			//�Ƚ����л����д��byte�����У���ʵ�ͷ���һ���ڴ�ռ�
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			ObjectOutputStream os=new ObjectOutputStream(bos);
			//���������л�д��byte����
			os.writeObject(obj);
			//�����л�������תΪ16���Ʊ���
			String bytesToHexString = bytesToHexString(bos.toByteArray());
			//�����16��������
			sharedata.putString(key, bytesToHexString);
			sharedata.commit();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("", "����objʧ��");
		}
	}

	/**
	 * desc:������תΪ16����
	 * @param bArray
	 * @return
	 * modified:	
	 */
	public static String bytesToHexString(byte[] bArray) {
		if(bArray == null){
			return null;
		}
		if(bArray.length == 0){
			return "";
		}
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * desc:��ȡ�����Object����
	 * @param context
	 * @param key
	 * @return
	 * modified:	
	 */
	public static Object readObject(Context context,String fileName,String key ){
		try {
			SharedPreferences sharedata = ((application)context.getApplicationContext()).getSharedPreferences(fileName, 0);
			if (sharedata.contains(key)) {
				String string = sharedata.getString(key, "");
				if(TextUtils.isEmpty(string)){
					return null;
				}else{
					//��16���Ƶ�����תΪ���飬׼�������л�
					byte[] stringToBytes = StringToBytes(string);
					ByteArrayInputStream bis=new ByteArrayInputStream(stringToBytes);
					ObjectInputStream is=new ObjectInputStream(bis);
					//���ط����л��õ��Ķ���
					Object readObject = is.readObject();
					return readObject;
				}
			}
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//�����쳣����null
		return null;
	}

	/**
	 * desc:��16���Ƶ�����תΪ����
	 * @param data
	 * @return
	 * modified:	
	 */
	public static byte[] StringToBytes(String data){
		String hexString=data.toUpperCase().trim();
		if (hexString.length()%2!=0) {
			return null;
		}
		byte[] retData=new byte[hexString.length()/2];
		for(int i=0;i<hexString.length();i++)
		{
			int int_ch;  // ��λ16������ת�����10������
			char hex_char1 = hexString.charAt(i); ////��λ16�������еĵ�һλ(��λ*16)
			int int_ch1;
			if(hex_char1 >= '0' && hex_char1 <='9')
				int_ch1 = (hex_char1-48)*16;   //// 0 ��Ascll - 48
			else if(hex_char1 >= 'A' && hex_char1 <='F')
				int_ch1 = (hex_char1-55)*16; //// A ��Ascll - 65
			else
				return null;
			i++;
			char hex_char2 = hexString.charAt(i); ///��λ16�������еĵڶ�λ(��λ)
			int int_ch2;
			if(hex_char2 >= '0' && hex_char2 <='9')
				int_ch2 = (hex_char2-48); //// 0 ��Ascll - 48
			else if(hex_char2 >= 'A' && hex_char2 <='F')
				int_ch2 = hex_char2-55; //// A ��Ascll - 65
			else
				return null;
			int_ch = int_ch1+int_ch2;
			retData[i/2]=(byte) int_ch;//��ת�����������Byte��
		}
		return retData;
	}
}
