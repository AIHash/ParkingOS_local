/**
 * @author drh
 * Excel���ݵ������ݿ�
 * @version 1.0
 */
package com.zld.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ImportExcelUtil4zj {
	
	/**
	 * ����Excel���Ĺ�����
	 */
    private static Workbook wb;
    private static Sheet sheet;
    private static Row row;
    
    /**
	 * ���뱨��Excel���ݣ������û�������ݿ⵼�����
	 * @param File formFile���ϴ����ļ�
	 *        String formFileName���ϴ����ļ�������ȡ��׺���ж���2007��.xlsx������2003��.xls��
	 *        int isTitle:�Ƿ��б��⣬������1������0
	 * @return ArrayList<Object[]>
	 * @throws Exception,Set<String> set
	 */
	public static ArrayList<Object[]> generateUserSql(File formFile,String formFileName,int isTitle)
			throws Exception {
		FileInputStream in = null;
		ArrayList<Object[]> list = new ArrayList<Object[]>();
		Map<String, String> localMap = readLocal();
		try {
			if (formFile == null) {
				throw new Exception("�ļ�Ϊ�գ�");
			}
			
			in = new FileInputStream(formFile);//���ļ����뵽��������
			
			//���������л�ȡWorkBook���󣬼���ѡ�е�excel�ļ�
			String suffix = formFileName.substring(formFileName.lastIndexOf("."));  // �ļ����.
			String area = "330700";
			
			//֧��office2007
			if (".xlsx".equals(suffix.toLowerCase())) {
				wb = new XSSFWorkbook(in);
			}
			else{
				//֧��office2003
	        	//wb = new HSSFWorkbook(in);
			}
			
			for (int i=0; i<wb.getNumberOfSheets(); i++) {//��ȡÿ��Sheet��
	             sheet = wb.getSheetAt(i);
	             if(sheet!=null){
	            	 int count = i+1;
	            	 System.err.println(">>>>>�ļ����� ��"+sheet.getPhysicalNumberOfRows());
	            	 for (int j=isTitle; j<sheet.getPhysicalNumberOfRows(); j++) {//��ȡÿ�У�j=isTitle��ʾ�ӵ�j�п�ʼ��ȡ����
//	            		 Object[] valStr = new String[row.getPhysicalNumberOfCells()];//�����������ÿһ�е����ݣ�9��ʾÿһ�е����ݲ��ܳ���9������<=9
	            		 ArrayList<Object> arrayList = new ArrayList<Object>();
		                 row = sheet.getRow(j);
		                 for (int k=0; k<row.getPhysicalNumberOfCells(); k++) {//��ȡÿ����Ԫ��
		                     String content = getCellFormatValue(row.getCell(k)).trim();
		                     if(content.equals("ZLD"))
		                    	 content = null;
		                     if(StringUtils.isNotNull(content)){
//		                    	 valStr[k] = content;//��excel��ȡ����ֵ��ֵ��object���͵�����
		                    	 if(k==2){
		                    		 String []ll = content.split(",");
		                    		 String pattern = "#0.000000";
		               			  	 DecimalFormat formatter = new DecimalFormat();
		               			  	 formatter.applyPattern(pattern);
		                    		// String resString = formatter.format(Double.parseDouble(content));
		                    		 arrayList.add(formatter.format(Double.parseDouble(ll[0])));
		                    		 arrayList.add(formatter.format(Double.parseDouble(ll[1])));
		                    	 }else if(k==4){
		                    		 if(content==null||content.equals("")||content.equals("��"))
		                    			 arrayList.add(1);
		                    		 else
		                    			 arrayList.add(0);
		                    	 }else if(k==3){
		                    		 arrayList.add(StringUtils.formatDouble(content).intValue());
		                    	 }else {
		                    		 arrayList.add(content+"");
								 }
		                     } else {
		                    	 arrayList.add(content);
							}
		                 }
		                 //�����ֶ�˳�򣬱������org.postgresql.util.PSQLException: δ�趨����ֵ *�����ݡ�
//		                 if(arrayList.size()<9){
//		                	 continue;
//		                 }
		                 //file:address,name,lng,lat,parking_total,type,mcompany,phone/mobile,remarks
		                 //company_name,address,remarks,create_time,update_time,longitude,latitude,parking_total,type,mcompany,mobile,state,city
		                 ArrayList<Object> arrayListRet = new ArrayList<Object>();
		                 arrayListRet.add(arrayList.get(1));
		                 arrayListRet.add(arrayList.get(0));
		                 arrayListRet.add(arrayList.get(8));
		                 arrayListRet.add(TimeTools.getLongMilliSeconds());
		                 arrayListRet.add(TimeTools.getLongMilliSeconds());
		                 arrayListRet.add(Double.parseDouble(arrayList.get(2)+""));
		                 arrayListRet.add(Double.parseDouble(arrayList.get(3)+""));
		                 arrayListRet.add(arrayList.get(4));
		                 arrayListRet.add(arrayList.get(5));
		                 arrayListRet.add(arrayList.get(6));
		                 arrayListRet.add(arrayList.get(7));
		                 arrayListRet.add(0);
		                 arrayListRet.add(Integer.parseInt(area));
		                 list.add(arrayListRet.toArray());
		             }
	             }
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

    /**
     * ����HSSFCell������������
     * @param cell
     * @return
     */
    private static String getCellFormatValue(Cell cell) {
        String cellvalue = "";
        if (cell != null) {
            // �жϵ�ǰCell��Type
            switch (cell.getCellType()) {
            // �����ǰCell��TypeΪNUMERIC
            case HSSFCell.CELL_TYPE_NUMERIC:
            case HSSFCell.CELL_TYPE_FORMULA: {
                // �жϵ�ǰ��cell�Ƿ�ΪDate
//                if (HSSFDateUtil.isCellDateFormatted(cell)) {
//                    // �����Date������ת��ΪData��ʽ
//                    
//                    //����1�������ӵ�data��ʽ�Ǵ�ʱ����ģ�2011-10-12 0:00:00
//                    //cellvalue = cell.getDateCellValue().toLocaleString();
//                    
//                    //����2�������ӵ�data��ʽ�ǲ�����ʱ����ģ�2011-10-12
//                    Date date = cell.getDateCellValue();
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                    cellvalue = sdf.format(date);
//                    
//                }
//                // ����Ǵ�����
//                else {
//                    // ȡ�õ�ǰCell����ֵ
//                    cellvalue = String.valueOf(cell.toString());
//                }
                break;
            }
            // �����ǰCell��TypeΪSTRIN
            case HSSFCell.CELL_TYPE_STRING:
                // ȡ�õ�ǰ��Cell�ַ���
                cellvalue = cell.getRichStringCellValue().getString();
                break;
            // Ĭ�ϵ�Cellֵ
            default:
                cellvalue = " ";
            }
        } else {
            cellvalue = "";
        }
        return cellvalue;

    }

    
    public  static List<Object[]> importExcelFile(){
    	String name = "d://zj_jinhua_park.xls";
    	File file = new File(name);
    	List<Object[]> resultList=new ArrayList<Object[]>();
    	List<Object[]>  list = null;
		try {
			list = generateUserSql(file, name, 0);
			if(list!=null)
				resultList.addAll(list);
//			name = "d://sz.xls";
//			file= new File(name);
//			name= "sz.xls";
//			list = generateUserSql(file, name, 0,set);
//			if(list!=null)
//				resultList.addAll(list);
//			name = "d://sh.xls";
//			file= new File(name);
//			name= "sh.xls";
//			list = generateUserSql(file, name, 0,set);
//			if(list!=null)
//				resultList.addAll(list);
//			name = "d://gz.xls";
//			file= new File(name);
//			name= "gz.xls";
//			list = generateUserSql(file, name, 0,set);
//			if(list!=null)
//				resultList.addAll(list);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return resultList;
	}
    
    private static Map<String, String> readLocal(){
    	BufferedReader br =null;
    	Map<String, String>  resultMap = new HashMap<String, String>();
    	String data ="";
    	try {
    		br =new BufferedReader(new FileReader("d:\\datafile.txt"));  
			data = br.readLine();
			while( data!=null){  
				
				resultMap.put(data.split("\\|")[0], data.split("\\|")[1]);
				data = br.readLine(); //���Ŷ���һ��  
			} 
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//һ�ζ���һ�У�ֱ������nullΪ�ļ�����
    	return resultMap;
    }
    
    public static void main(String[] args) {
    	try {
    		 List<Object[]> values = ImportExcelUtil4zj.importExcelFile();
    		 System.err.println(values.size());
    		 for(Object[] o: values)
    			 System.err.println(StringUtils.objArry2String(o));
			//String sql = "insert into com_info_tb(company_name,resume,create_time,longitude,latitude,type,state,city) values(?,?,?,?,?,?,?,?)";
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
