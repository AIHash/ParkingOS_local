package com.zld.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public class GetTableFields {

	/**
	 * ȡ�ñ���ֶ�
	 * @param tqbleName
	 * @param type
	 * @return
	 */
	public static String getFieddsByTableName(String tableName,String type){
		if(tableName.equals("com_info")){
			if(type.equals("query")){
				return getComInfoQueryFields();
			}
		}
		return null;
	}
	
	private static String getComInfoQueryFields(){
		List<TableFields> list = new ArrayList<TableFields>();
		List<NoList> typeList = new ArrayList<NoList>();
		typeList.add(new NoList("-1", "ȫ��"));
		typeList.add(new NoList("0", "����"));
		typeList.add(new NoList("1", "���"));
		List<NoList> parkTypeList = new ArrayList<NoList>();
		parkTypeList.add(new NoList("-1", "ȫ��"));
		parkTypeList.add(new NoList("0", "����"));
		parkTypeList.add(new NoList("1", "����"));
		parkTypeList.add(new NoList("2", "ռ��"));
		list.add(new TableFields("���", "id", "", "text", 50, null, false, false, false, false, true,null));
		list.add(new TableFields("����", "company_name", "", "text", 200, null, false, false, false, false, true,null));
		list.add(new TableFields("��������", "type", "", "select", 50, null, false, false, false, false, true,typeList));
		list.add(new TableFields("��¼�ʺ�", "strid", "", "text", 50, null, false, false, false, false, false,null));
		list.add(new TableFields("��ϸ��ַ", "address", "", "text", 50, null, false, false, false, false, true,null));
		list.add(new TableFields("�绰", "phone", "", "text", 50, null, false, false, false, false, true,null));
		list.add(new TableFields("�ֻ�", "moblie", "", "text", 50, null, false, false, false, false, true,null));
		list.add(new TableFields("��ϵ��", "nickname", "", "text", 50, null, false, false, false, false, false,null));
		Map<String, List<TableFields>> map = new HashMap<String, List<TableFields>>();
		map.put("root", list);
		JSONObject json= JSONObject.fromObject(map);
		return json.toString();
	}
	public static void main(String[] args) {
		System.out.println(getComInfoQueryFields());
	}
}
