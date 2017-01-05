package com.zld.bean;

import java.io.Serializable;

public class WorkStation implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String comid;
	private String worksite_name;
	private String description;
	private String net_type;//0��������1�����
	private String worksite_type;//0����ڣ�1���ڣ�2�����  -1Ĭ��ֵ
	
	public String getWorksite_type() {
		return worksite_type;
	}
	public void setWorksite_type(String worksite_type) {
		this.worksite_type = worksite_type;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getComid() {
		return comid;
	}
	public void setComid(String comid) {
		this.comid = comid;
	}
	public String getWorksite_name() {
		return worksite_name;
	}
	public void setWorksite_name(String worksite_name) {
		this.worksite_name = worksite_name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getNet_type() {
		return net_type;
	}
	public void setNet_type(String net_type) {
		this.net_type = net_type;
	}
	public WorkStation() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "WorkStation [id=" + id + ", comid=" + comid
				+ ", worksite_name=" + worksite_name + ", description="
				+ description + ", net_type=" + net_type + ", worksite_type=" + worksite_type +"]";
	}
}
