/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015��6��1�� 
 * 
 *******************************************************************************/ 
package com.zld.bean;

/**
 * <pre>
 * ����˵��: 
 * ����:	2015��6��1��
 * ������:	HZC
 * 
 * ��ʷ��¼
 *    �޸����ݣ�
 *    �޸���Ա��
 *    �޸����ڣ� 2015��6��1��
 * </pre>
 */
public class HistoryOrder {
	/*[{"total":"1.41","carnumber":"��FF6203","ismonthuser":"0","width":"265","state":"1",
		"btime":"2015-05-26 14:30","car_type":"0","id":"787323","duration":"ͣ�� 5�� 20Сʱ57����",
		"height":"105","rightbottom":"527","lefttop":"600","ptype":"1"},*/
	private String total;//���
	private String carnumber;//���ƺ�
	private String ismonthuser;//�¿�
	private String width;//Сͼ��
	private String state;//����״̬
	private String btime;//�볡ʱ��
	private String car_type;//��������
	private String id;//������
	private String duration;//ͣ��ʱ��
	private String height;//��
	private String rightbottom;//�ҵ�
	private String lefttop;//���
	private String ptype;//ͨ������,1Ϊ����
	private String ctype;
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getCarnumber() {
		return carnumber;
	}
	public void setCarnumber(String carnumber) {
		this.carnumber = carnumber;
	}
	public String getIsmonthuser() {
		return ismonthuser;
	}
	public void setIsmonthuser(String ismonthuser) {
		this.ismonthuser = ismonthuser;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getBtime() {
		return btime;
	}
	public void setBtime(String btime) {
		this.btime = btime;
	}
	public String getCar_type() {
		return car_type;
	}
	public void setCar_type(String car_type) {
		this.car_type = car_type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getRightbottom() {
		return rightbottom;
	}
	public void setRightbottom(String rightbottom) {
		this.rightbottom = rightbottom;
	}
	public String getLefttop() {
		return lefttop;
	}
	public void setLefttop(String lefttop) {
		this.lefttop = lefttop;
	}
	public String getPtype() {
		return ptype;
	}
	public void setPtype(String ptype) {
		this.ptype = ptype;
	}

	@Override
	public String toString() {
		return "HistoryOrder [total=" + total + ", carnumber=" + carnumber
				+ ", ismonthuser=" + ismonthuser + ", width=" + width
				+ ", state=" + state + ", btime=" + btime + ", car_type="
				+ car_type + ", id=" + id + ", duration=" + duration
				+ ", height=" + height + ", rightbottom=" + rightbottom
				+ ", lefttop=" + lefttop + ", ptype=" + ptype + ", ctype="
				+ ctype + "]";
	}
	/**
	 * @return the ctype
	 */
	public String getCtype() {
		return ctype;
	}
	/**
	 * @param ctype the ctype to set
	 */
	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

}
