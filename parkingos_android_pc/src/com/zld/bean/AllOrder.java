package com.zld.bean;

import java.io.Serializable;

//6.//��������
//<?xml version="1.0" encoding="gb2312" ?> 
//- <content>
//  <total>0.0</total> 
//  <carnumber>��GPS223</carnumber> 
//  <state>�ѽ���</state> 
//  <orderid>1</orderid> 
//  <end>1400839200</end> 
//  <mobile>15801482643</mobile> 
//  <begin>1400781600</begin> 
//  </content>

@SuppressWarnings("serial")
public class AllOrder implements Serializable {
	private String total;//�ܽ��
	private String duration;//ͣ��ʱ��
	private String carnumber;//���ƺ�
	private String btime;//ͣ����ʼʱ��
	private String ordercount;//��������
	private String ismonthuser;//�����û���ʶ	
	private String state;//����״̬;
	private String id;//������
	private String end;//����ʱ��
	private String begin;//��ʼʱ��
	private String mobile;//�ֻ���
	private String lefttop;
	private String width;
	private String height;
	private String rightbottom;
	private String prepay;//Ԥ֧��
	//------
	private String befcollect;//����֮ǰ��ͣ�����ܽ��
	private String distotal;//����ȯ�ֿ۵Ľ��
	private String shopticketid;//����ȯID�������ж���û��ʹ�ü���ȯ
	private String tickettype;//����ȯ���� 3����ʱȯ 4��ȫ��ȯ
	private String tickettime;//��ʱȯʱ��
	//------
	private String limitday;
	private String ptype;
	private String car_type;
	private String uin;
	private String localid;
	private String exptime;
	public String ctype; // ������ʽ��ʶ0:NFC,1:IBeacon,2:����,3:ͨ��ɨ�� 4ֱ�� 5�¿��û�6:��λ��ά�� 7���¿��û���2��3�����볡��5ȫ���¿�   8  �ֶ��¿�
	
	public String getCtype() {
		return ctype;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

	public String getExptime() {
		return exptime;
	}

	public void setExptime(String exptime) {
		this.exptime = exptime;
	}

	public String getLocalid() {
		return localid;
	}

	public void setLocalid(String localid) {
		this.localid = localid;
	}

	public String getLimitday() {
		return limitday;
	}

	public void setLimitday(String limitday) {
		this.limitday = limitday;
	}

	public String getLefttop() {
		return lefttop;
	}

	public void setLefttop(String lefttop) {
		this.lefttop = lefttop;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
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

	public AllOrder() {
		super();
	}

	public String getOrdercount() {
		return ordercount;
	}

	public void setOrdercount(String ordercount) {
		this.ordercount = ordercount;
	}

	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getCarnumber() {
		return carnumber;
	}
	public void setCarnumber(String carnumber) {
		this.carnumber = carnumber;
	}
	public String getBtime() {
		return btime;
	}
	public void setBtime(String btime) {
		this.btime = btime;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public String getBegin() {
		return begin;
	}
	public void setBegin(String begin) {
		this.begin = begin;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getIsmonthuser() {
		return ismonthuser;
	}

	public void setIsmonthuser(String ismonthuser) {
		this.ismonthuser = ismonthuser;
	}

	public String getPrepay() {
		return prepay;
	}

	public void setPrepay(String prepay) {
		this.prepay = prepay;
	}
	public String getBefcollect() {
		return befcollect;
	}

	public void setBefcollect(String befcollect) {
		this.befcollect = befcollect;
	}

	public String getDistotal() {
		return distotal;
	}

	public void setDistotal(String distotal) {
		this.distotal = distotal;
	}

	public String getShopticketid() {
		return shopticketid;
	}

	public void setShopticketid(String shopticketid) {
		this.shopticketid = shopticketid;
	}

	public String getTickettype() {
		return tickettype;
	}

	public void setTickettype(String tickettype) {
		this.tickettype = tickettype;
	}

	public String getTickettime() {
		return tickettime;
	}

	public void setTickettime(String tickettime) {
		this.tickettime = tickettime;
	}

	public String getPtype() {
		return ptype;
	}

	public void setPtype(String ptype) {
		this.ptype = ptype;
	}

	public String getCar_type() {
		return car_type;
	}

	public void setCar_type(String car_type) {
		this.car_type = car_type;
	}

	public String getUin() {
		return uin;
	}

	public void setUin(String uin) {
		this.uin = uin;
	}

	@Override
	public String toString() {
		return "AllOrder [total=" + total + ", duration=" + duration + ", carnumber=" + carnumber + ", btime=" + btime
				+ ", ordercount=" + ordercount + ", ismonthuser=" + ismonthuser + ", state=" + state + ", id=" + id
				+ ", end=" + end + ", begin=" + begin + ", mobile=" + mobile + ", lefttop=" + lefttop + ", width="
				+ width + ", height=" + height + ", rightbottom=" + rightbottom + ", prepay=" + prepay + ", befcollect="
				+ befcollect + ", distotal=" + distotal + ", shopticketid=" + shopticketid + ", tickettype="
				+ tickettype + ", tickettime=" + tickettime + ", limitday=" + limitday + ", ptype=" + ptype
				+ ", car_type=" + car_type + ", uin=" + uin + ", localid=" + localid + ", exptime=" + exptime
				+ ", ctype=" + ctype + "]";
	}

}
