package com.mserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletResponse;

public class AjaxUtil {
  
 
  
    /** 
     * ����AJAX���ý�� String����
     * @param response
     * @param outputString 
     * @throws java.io.IOException 
     */  
    public static void ajaxOutput(HttpServletResponse response, String outputString) throws IOException {  
        response.setContentType("text/html; charset=gbk");  
        PrintWriter printWriter = response.getWriter();
        printWriter.write(outputString);  
        printWriter.flush();  
        printWriter.close();
    }  
    
    /** 
     * ����AJAX���ý�� ���������INT
     * @param response
     * @param outputInt
     * @throws java.io.IOException
     */  
    public static void ajaxOutputRint(HttpServletResponse response, int outputInt) throws IOException {  
        response.setContentType("text/html; charset=gbk");  
        PrintWriter printWriter = response.getWriter();
        printWriter.write(outputInt);
        printWriter.flush();
        printWriter.close();
    }  
    
    /** 
     *����Ajax urf-8������url��ʽ���Ĳ��� ����UTF-8���
     *@param String
     */ 
	public static String decodeUTF8(String someStr) {
		String newStr = null;
		if(someStr!=null&&someStr.equals(""))
			return "";
		if(someStr!=null&&!someStr.equals("")) {
			try {
				newStr = URLDecoder.decode(someStr,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return newStr;
	}
}  
