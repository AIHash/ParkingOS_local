package com.zld.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;

import com.ibatis.common.resources.Resources;

public class TestMain {
 
 //����key��ȡvalue
 public static String readValue(String filePath,String key) {
  Properties props = new Properties();
        try {
    	 File file = Resources.getResourceAsFile(filePath);
    	 props.load(new FileInputStream(file));
         String value = props.getProperty (key);
            System.out.println(key+value);
            return value;
        } catch (Exception e) {
         e.printStackTrace();
         return null;
        }
 }
 
// public static String updateValue(String filePath,String key) {
//	  Properties props = new Properties();
//	        try {
//	    	 File file = Resources.getResourceAsFile(filePath);
//	    	 props.load(new FileInputStream(file));
//	         String value = props.getProperty (key);
//	            System.out.println(key+value);
//	            props.setProperty(key, "3333");
//	            OutputStream fos = new FileOutputStream(filePath);
//	            props.setProperty(parameterName, parameterValue);
//	            //���ʺ�ʹ�� load �������ص� Properties ���еĸ�ʽ��
//	            //���� Properties ���е������б�����Ԫ�ضԣ�д�������
//	            props.store(fos, "Update '" + parameterName + "' value");
//	            fos.flush();
//	            fos.close();
//	            return value;
//	        } catch (Exception e) {
//	         e.printStackTrace();
//	         return null;
//	        }
//	 }

    //д��properties��Ϣ
    public static void writeProperties(String filePath,String parameterName,String parameterValue) {
     Properties prop = new Properties();
     try {
    	 File file = Resources.getResourceAsFile(filePath);
    	 prop.load(new FileInputStream(file));
            //���� Hashtable �ķ��� put��ʹ�� getProperty �����ṩ�����ԡ�
            //ǿ��Ҫ��Ϊ���Եļ���ֵʹ���ַ���������ֵ�� Hashtable ���� put �Ľ����
            OutputStream fos = new FileOutputStream(file);
            System.out.println(prop.getProperty(parameterName));
            prop.setProperty(parameterName, parameterValue);
            //���ʺ�ʹ�� load �������ص� Properties ���еĸ�ʽ��
            //���� Properties ���е������б�����Ԫ�ضԣ�д�������
            System.out.println(prop.getProperty(parameterName));
            prop.store(fos, "Update '" + parameterName + "' value");
            fos.flush();
            fos.close();
        } catch (IOException e) {
         System.err.println("Visit "+filePath+" for updating "+parameterName+" value error");
        }
    }

    public static void main(String[] args) {
     readValue("info.properties","url");
        writeProperties("info.properties","SYNCTO","33333");
//        readProperties("info.properties" );
        System.out.println("OK");
    } 
}