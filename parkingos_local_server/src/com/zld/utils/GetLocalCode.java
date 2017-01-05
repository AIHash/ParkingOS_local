package com.zld.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;


/**
 * 
*    
* ��Ŀ���ƣ�vip   
* �����ƣ�GetPhonePlace   
* �����ˣ�laoyao 
* ����ʱ�䣺Apr 29, 2010 3:45:33 PM   
* �޸�ʱ�䣺Apr 29, 2010 3:45:33 PM   
* �޸ı�ע��   ͨ�������༭�õ������أ���ʱ��ѯ�����ļ���
* @version    
*
 */
public class GetLocalCode {
	
	public static Map<Integer , String> localDataMap=null;
	public static void Init(){
		String path = GetLocalCode.class.getClassLoader().getResource("").toString().substring(5);
		String fileNameString=path+"china_local_code.txt";
		BufferedReader reader = null;
		String lineString=null;
		try {
			localDataMap=new TreeMap<Integer, String>();
			reader = new BufferedReader(new FileReader2(fileNameString,"GBK"));
			while ((lineString = reader.readLine()) != null) {
				String temp[] = lineString.split("\\|");
				String code=temp[0];
				String local = temp[1];
				try {
					localDataMap.put(Integer.valueOf(code), local);
				} catch (Exception e) {
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}
	public static String getLocalData(){
		if(localDataMap==null){
			Init();
		}
		StringBuffer localdata = new StringBuffer();
		localdata.append("{\"root_0\":{\"id\":\"0\",\"name\":\"�����б�\"},");
		for(Integer code : localDataMap.keySet()){
			String local =localDataMap.get(code);
			try {
				if (code%10000==0) {//ʡ��������
					localdata.append("\"0_"+code+"\":{\"id\":\""+code+"\",\"name\":\""+local+"\"},");
				} else if (code%100==0) {//��
					String pid = code/10000+"0000";
					localdata.append("\""+pid+"_"+code+"\":{\"id\":\""+code+"\",\"name\":\""+local+"\"},");
				} else {//����
					String pid = code/100+"00";
					localdata.append("\""+pid+"_"+code+"\":{\"id\":\""+code+"\",\"name\":\""+local+"\"},");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String value = localdata.toString();
		if(value.endsWith(","))
			value = value.substring(0,value.length()-1)+"}";
		return value;
	}
	static{
		Init();
	}
}
class FileReader2 extends InputStreamReader{
	public FileReader2(String FileName,String charSetName)throws  FileNotFoundException,UnsupportedEncodingException{
		super(new FileInputStream(FileName), charSetName);
	}
}
