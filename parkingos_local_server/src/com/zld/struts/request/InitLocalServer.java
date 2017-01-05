package com.zld.struts.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.annotation.Autowired;

import pay.Constants;

import com.zld.AjaxUtil;
import com.zld.CustomDefind;
import com.zld.service.DataBaseService;
import com.zld.utils.HttpProxy;

public class InitLocalServer extends Action {
	@Autowired
	private DataBaseService daService;
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
//		clernDB();
		int r = 0;//��ʼ����������
		StringBuffer sb = new StringBuffer();
		r = initUtil("initComInfo","com_info_tb");
		sb.append("�ɹ����"+r+"������");
		r = initUtil("initProPack","product_package_tb");
		sb.append("\r\n"+r+"���¿��ײ�");
		r = initUtil("initCarPro","carower_product");
		sb.append("\r\n"+r+"���¿���Ա");
		r = initUtil("initCarInfo","car_info_tb");
		sb.append("\r\n"+r+"������");
		r = initUtil("initLed","com_led_tb");
		sb.append("\r\n"+r+"��led��Ϣ");
		r = initUtil("initPass","com_pass_tb");
		sb.append("\r\n"+r+"��ͨ����Ϣ");
		r = initUtil("initWorkSite","com_worksite_tb");
		sb.append("\r\n"+r+"������վ��Ϣ");
		r = initUtil("initCarmera","com_camera_tb");
		sb.append("\r\n"+r+"����ͷ��Ϣ");
		r = initUtil("initPrice","price_tb");
		sb.append("\r\n"+r+"���۸�");
//		initUtil("initOrder","order_tb");
		r = initUtil("initFreeRe","free_reasons_tb");
		sb.append("\r\n"+r+"�����ԭ��");
		r = initUtil("initPriceAst","price_assist_tb");
		sb.append("\r\n"+r+"�������۸�");
		r = initUtil("initCarType","car_type_tb");
		sb.append("\r\n"+r+"��������Ϣ");
		r = initUtil("initUser","user_info_tb");
		sb.append("\r\n"+r+"���շ�Ա�ͳ���");
		AjaxUtil.ajaxOutput(response, sb.toString()+"");
		return null;
	}
//	private void clernDB() {
//		daService.update("delete from com_info_tb ",new Object[]{});
//		daService.update("delete from product_package_tb ",new Object[]{});
//		daService.update("delete from carower_product ",new Object[]{});
//		daService.update("delete from car_info_tb ",new Object[]{});
//		daService.update("delete from com_led_tb ",new Object[]{});
//		daService.update("delete from com_pass_tb ",new Object[]{});
//		daService.update("delete from com_worksite_tb ",new Object[]{});
//		daService.update("delete from com_camera_tb ",new Object[]{});
//		daService.update("delete from price_tb ",new Object[]{});
//		daService.update("delete from free_reasons_tb ",new Object[]{});
//		daService.update("delete from price_assist_tb ",new Object[]{});
//		daService.update("delete from car_type_tb ",new Object[]{});
//		daService.update("delete from order_tb ",new Object[]{});
//		daService.update("delete from user_info_tb where nickname like '%admin%' ",new Object[]{});
//
//	}
	/**
	 * ��ʼ������  ƴ��sqlִ��
	 * @param comJo  ����
	 * @param columnsList
	 * @param tablename
	 */
	private int initData(JSONObject comJo, Map<String,String> columnsList,String tablename) {
		StringBuffer insertsql = new StringBuffer("insert into "+tablename +" (");
		StringBuffer valuesql = new StringBuffer(" values(");
		ArrayList values = new ArrayList();
		for (Map.Entry<String, String> entry : columnsList.entrySet()) {
			try{
				if(comJo.getString(entry.getKey())!=null&&!"null".equals(comJo.getString(entry.getKey()))){
					insertsql.append(entry.getKey()+",");
					valuesql.append("?,");
					if(entry.getValue().startsWith("bigint")){
						values.add(comJo.getLong(entry.getKey()));
					}else if(entry.getValue().startsWith("numeric")){
						values.add(comJo.getDouble(entry.getKey()));
					}else if(entry.getValue().startsWith("integer")){
						values.add(comJo.getInt(entry.getKey()));
					}else if(entry.getValue().startsWith("charact")){
						values.add(comJo.getString(entry.getKey()));
					}
				}
			}catch (Exception e) {
				System.out.println("0-----------------"+e.getMessage());
			}
		}
		String sql = "";
		int r = 0;
		if(insertsql.toString().endsWith(",")&&valuesql.toString().endsWith(",")){
			sql = insertsql.substring(0,insertsql.length()-1)+") "+valuesql.substring(0,valuesql.length()-1)+")";
			try{
				r = daService.update(sql, values);
			}catch (Exception e) {
				if(e.getMessage().endsWith("����")){
					daService.update("delete from "+tablename +" where id = ?", new Object[]{comJo.getLong("id")});
					r = daService.update(sql, values);
				}
			}
		}
		return r;
	}

	/**
	 * ���ݱ��ȡ���е��ֶ������ֶ�����
	 * @param tablename
	 * @return
	 */
	public Map getColumns(String tablename){
		HashMap<String, String> hashMap = new HashMap<String,String>();
		List list = daService.getAll("select column_name,data_type from information_schema.columns where table_schema='public' and table_name= ? ", new Object[]{tablename});
		for (Object object : list) {
			Map map = (Map)object;
			hashMap.put(map.get("column_name")+"",map.get("data_type")+"");
		}
		return hashMap;
	}
	 /**
	  * ��ʼ�������� 
	  * @param actionName �����action��
	  * @param tableName ����
	  */
	private int initUtil(String actionName,String tableName) {
		int r =0;
		try{
			HttpProxy proxy = new HttpProxy();
			String result = proxy.doGetInit(CustomDefind.DOMAIN+"/localinit.do?comid="+CustomDefind.COMID+"&action="+actionName);
			JSONObject jo = new JSONObject();
			Map<String,String> columnsList = getColumns(tableName);
			if(result!=null&&result.length()>2){
				if(result.startsWith("{")){
					jo = jo.fromObject(result);
					r += initData(jo,columnsList,tableName);
				}else{
					JSONArray ja = new JSONArray();
					ja = ja.fromObject(result);
					for (int i = 0; i < ja.size(); i++) {
						jo = ja.getJSONObject(i);
						r += initData(jo,columnsList,tableName);
					}
				}
			}
		}catch (Exception e) {
			return -1;
		}
		return r;
	}
}
