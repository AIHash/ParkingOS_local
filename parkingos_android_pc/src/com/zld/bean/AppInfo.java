/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015��4��15�� 
 * 
 *******************************************************************************/ 
package com.zld.bean;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.zld.lib.util.SharedPreferencesUtils;


/**
 * <pre>
 * ����˵��: ��¼������ȡ����Ϣ
 * ����:	2015��4��15��
 * ������:	HZC
 * 
 * ��ʷ��¼
 *    �޸����ݣ�
 *    �޸���Ա��
 *    �޸����ڣ� 2015��4��15��
 * </pre>
 */
public class AppInfo {

	private String name;			//�շ�Ա
	private String token; 			//token
	private String comid; 			//����id
	private String parkName;        //��������
	private String uid;				//�û��˺�
	private boolean parkBilling; 	//���ִ�С��
	private boolean passfree;		//�Ƿ����
	private boolean ishdmoney;      //�Ƿ���ʾ�շ��ۼƽ�� 0����ʾ 1������
	private String ishidehdbutton; 	//�Ƿ���ʾ���㶩����ť
	private String issuplocal;		//�Ƿ�֧�ֱ��ػ�
	private String equipmentModel;	//�豸
	private String imei;
	private String stname;			//����վ����
	private List<CarType> allCarTypes; // ���г�������
	private List<FreeResons> freeResons; // �����������
	private List<LiftReason> liftreason;       //̧��ԭ�� 
	private String fullset;		//��λ�����ܷ����
	private String leaveset;	//����ʶ��ʶ��̧������  ���е��¿�����û���շѣ����շѣ���

	//AppInfoʵ��  
	private static AppInfo appInfo = new AppInfo(); 

	/** ��ֻ֤��һ��AppInfoʵ�� */  
	private AppInfo() {  
	}  

	/** ��ȡAppInfoʵ�� ,����ģʽ */  
	public static AppInfo getInstance() {
		
		return appInfo;  
	}

	
	public boolean isIshdmoney() {
		return ishdmoney;
	}

	public void setIshdmoney(boolean ishdmoney) {
		this.ishdmoney = ishdmoney;
	}

	public String getFullset() {
		return fullset;
	}

	public void setFullset(String fullset) {
		this.fullset = fullset;
	}

	public String getLeaveset() {
		return leaveset;
	}

	public void setLeaveset(String leaveset) {
		this.leaveset = leaveset;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getComid() {
		return comid;
	}

	public void setComid(String comid) {
		this.comid = comid;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public boolean isParkBilling() {
		return parkBilling;
	}

	public void setParkBilling(boolean parkBilling) {
		this.parkBilling = parkBilling;
	}

	public boolean isPassfree() {
		return passfree;
	}

	public void setPassfree(boolean passfree) {
		this.passfree = passfree;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEquipmentModel() {
		return equipmentModel;
	}

	public void setEquipmentModel(String equipmentModel) {
		this.equipmentModel = equipmentModel;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getStname() {
		return stname;
	}

	public void setStname(String stname) {
		this.stname = stname;
	}

	public String getIshidehdbutton() {
		return ishidehdbutton;
	}

	public void setIshidehdbutton(String ishidehdbutton) {
		this.ishidehdbutton = ishidehdbutton;
	}

	public String getIssuplocal() {
		return issuplocal;
	}

	public void setIssuplocal(String issuplocal) {
		this.issuplocal = issuplocal;
	}

	public static AppInfo getAppInfo() {
		return appInfo;
	}

	public static void setAppInfo(AppInfo appInfo) {
		AppInfo.appInfo = appInfo;
	}

	/**
	 * Service����ֱ�ӻ�ȡ
	 * @param activity
	 * @return
	 */
	public boolean getIsLocalServer(Activity activity){
		//�Ƿ��Ǳ��ط�����
		boolean isLocalServer = SharedPreferencesUtils.getParam(
				activity.getApplicationContext(), "nettype","isLocalServer", false);
		return isLocalServer;
	}
	public List<CarType> getAllCarTypes() {
		return allCarTypes;
	}

	public void setAllCarTypes(List<CarType> allCarTypes) {
		Log.e("", "���ɶ���url---------------->>>>>" + AppInfo.getInstance().getAllCarTypes());
		this.allCarTypes = allCarTypes;
	}

	public List<FreeResons> getFreeResons() {
		return freeResons;
	}

	public void setFreeResons(List<FreeResons> freeResons) {
		this.freeResons = freeResons;
	}
	
	public List<LiftReason> getLiftreason() {
		return liftreason;
	}

	public void setLiftreason(List<LiftReason> liftreason) {
		this.liftreason = liftreason;
	}

	public boolean getIsShowhdmoney() {
		return ishdmoney;
	}

	public void setIsShowhdmoney(boolean ishdmoney) {
		this.ishdmoney = ishdmoney;
	}
	
	public String getParkName() {
		return parkName;
	}

	public void setParkName(String parkName) {
		this.parkName = parkName;
	}


	@Override
	public String toString() {
		return "AppInfo [name=" + name + ", token=" + token + ", comid=" + comid + ", parkName=" + parkName + ", uid="
				+ uid + ", parkBilling=" + parkBilling + ", passfree=" + passfree + ", ishdmoney=" + ishdmoney
				+ ", ishidehdbutton=" + ishidehdbutton + ", issuplocal=" + issuplocal + ", equipmentModel="
				+ equipmentModel + ", imei=" + imei + ", stname=" + stname + ", allCarTypes=" + allCarTypes
				+ ", freeResons=" + freeResons + ", liftreason=" + liftreason + ", fullset=" + fullset + ", leaveset="
				+ leaveset + "]";
	}

	public boolean getIsLocalServer(Context context) {
		// TODO Auto-generated method stub
		//�Ƿ��Ǳ��ط�����
		boolean isLocalServer = SharedPreferencesUtils.getParam(
				context.getApplicationContext(), "nettype","isLocalServer", false);
		return isLocalServer;
	}

}
