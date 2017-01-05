package com.zld.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.log4j.Logger;
public class ExportExcelUtil {

static Logger logger = Logger.getLogger(ExportExcelUtil.class);
	
	public  String excelName="����";
	public  String[] headBody = null;
	public  List<List<String>> bodyList = null;
	/**
	 * 
	 * @param excelName �ļ���
	 * @param headBody ��ͷ
	 * @param bodyList ����
	 * @param isEncrypt �绰�Ƿ����
	 */
	public ExportExcelUtil(String excelName,String[] headBody,List<List<String>> bodyList){
		this.excelName=excelName;
		this.headBody = headBody;
		this.bodyList = bodyList;
	}
	public void createExcelFile(OutputStream os) throws IOException {
	    try {
            //����һ���ļ�
			WritableWorkbook workbook = Workbook.createWorkbook(os);
	        //ʹ�õ�һ�Ź�����
	        WritableSheet sheet = workbook.createSheet(excelName, 0); 
	        //������ͷ
	        for (int i=0;i<headBody.length;i++) {
	        	Label cell= new Label(i, 0, headBody[i]);
		        sheet.addCell(cell);
			}
	        //��������
	        if(bodyList != null) {     
	        	logger.info("��ʼ�����ļ�");
	        	for(int i = 0,j=1; i < bodyList.size(); i++,j++) {
	        		//��ȡд������
	        		List<String > dateList=bodyList.get(i);	              				        	
	        		//д������
	        		for(int k=0 ;k<dateList.size();k++){
	        			String value = dateList.get(k);//�������ͻ��绰���������ַ�������
	        			value = (value==null||value.equals("null"))?"":value;
	        			Label label = new Label(k,j,value);
	 	    	        sheet.addCell(label);
	        		}
	        	}
	        }
	        logger.info("�����ļ�����");
	        //�رն����ͷ���Դ
	        workbook.write();
	        workbook.close();
	        os.close();
		} catch (Exception e) {
			os.close();
			logger.info(e);
		}
	}
}
