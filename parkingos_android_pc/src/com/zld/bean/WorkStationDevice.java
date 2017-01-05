/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015��5��15�� 
 * 
 *******************************************************************************/ 
package com.zld.bean;

import java.util.Arrays;

/**
 * <pre>
 * ����˵��: 
 * ����:	2015��5��15��
 * ������:	HZC
 * 
 * ��ʷ��¼
 *    �޸����ݣ�
 *    �޸���Ա��
 *    �޸����ڣ� 2015��5��15��
 * </pre>
 */
public class WorkStationDevice {
	
	private String id;
	private String worksite_id;  // ����վid
	private String passname;   // ͨ������
	private String passtype;   // ͨ������
	private String description;
	private String comid;
	
//	private String cameras;
//	private String leds;
//	public String getCameras() {
//		return cameras;
//	}
//	public void setCameras(String cameras) {
//		this.cameras = cameras;
//	}
//	public String getLeds() {
//		return leds;
//	}
//	public void setLeds(String leds) {
//		this.leds = leds;
//	}
//	@Override
//	public String toString() {
//		return "WorkStationDevice [id=" + id + ", worksite_id=" + worksite_id
//				+ ", passname=" + passname + ", passtype=" + passtype
//				+ ", description=" + description + ", comid=" + comid
//				+ ", cameras=" + cameras + ", leds=" + leds + "]";
//	}
	
	
	private MyCameraInfo cameras[];
	private MyLedInfo leds[];
	public MyCameraInfo[] getCameras() {
		return cameras;
	}
	public void setCameras(MyCameraInfo[] cameras) {
		this.cameras = cameras;
	}
	public MyLedInfo[] getLeds() {
		return leds;
	}
	public void setLeds(MyLedInfo[] leds) {
		this.leds = leds;
	}
	@Override
	public String toString() {
		return "WorkStationDevice [id=" + id + ", worksite_id=" + worksite_id
				+ ", passname=" + passname + ", passtype=" + passtype
				+ ", description=" + description + ", comid=" + comid
				+ ", cameras=" + Arrays.toString(cameras) + ", leds="
				+ Arrays.toString(leds) + "]";
	}
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getWorksite_id() {
		return worksite_id;
	}
	public void setWorksite_id(String worksite_id) {
		this.worksite_id = worksite_id;
	}
	public String getPassname() {
		return passname;
	}
	public void setPassname(String passname) {
		this.passname = passname;
	}
	public String getPasstype() {
		return passtype;
	}
	public void setPasstype(String passtype) {
		this.passtype = passtype;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getComid() {
		return comid;
	}
	public void setComid(String comid) {
		this.comid = comid;
	}

}
