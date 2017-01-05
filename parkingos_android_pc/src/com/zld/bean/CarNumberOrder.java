package com.zld.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CarNumberOrder implements Serializable{
//	{"total":"4.0","carnumber":"��ZL1Z11","duration":"21����","etime":"19:58","btime":"19:37","uin":"-1","orderid":"48260","collect":"2.0","discount":"2.0"}
	
	public String total;//�ܽ��
	public String carnumber;//���ƺ�
	public String etime;//����ʱ��
	public String btime;//��ʼʱ��
	public String orderid;//�������
	public String localid;//���ض������
	public String collect;//������
	public String discount;//������
	public String ctype; // ������ʽ��ʶ  5���¿��û�  7���¿��ڶ�����

	// -------------
	public String befcollect;//����֮ǰ��ͣ�����ܽ��
	public String distotal;//����ȯ�ֿ۵Ľ��
	public String shopticketid;//����ȯID�������ж���û��ʹ�ü���ȯ
	public String tickettype;//����ȯ���� 3����ʱȯ 4��ȫ��ȯ
	public String tickettime;//��ʱȯʱ��
	// -----------------
	public String duration;//ʱ��
	public String uin;//-1δ�� �������ǰ�
	public String hascard;//�Ƿ��г���
	private String ismonthuser;//�����û���ʶ	
	private String prepay;//Ԥ֧��
	private String limitday;// �¿�����ʱ��
	
	private String lefttop;
	private String width;
	private String height;
	private String rightbottom;
	private String car_type;
	private String state;
	
	public String getLefttop() {
		return lefttop;
	}

	public String getLocalid() {
		return localid;
	}

	public void setLocalid(String localid) {
		this.localid = localid;
	}

	public void setLefttop(String lefttop) {
		this.lefttop = lefttop;
	}
	
	public String getCtype() {
		return ctype;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
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

	public String getIsmonthuser() {
		return ismonthuser;
	}

	public void setIsmonthuser(String ismonthuser) {
		this.ismonthuser = ismonthuser;
	}

	public CarNumberOrder() {
		super();
	}
	
	public String getHascard() {
		return hascard;
	}

	public void setHascard(String hascard) {
		this.hascard = hascard;
	}

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
	public String getEtime() {
		return etime;
	}
	public void setEtime(String etime) {
		this.etime = etime;
	}
	public String getBtime() {
		return btime;
	}
	public void setBtime(String btime) {
		this.btime = btime;
	}
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public String getCollect() {
		return collect;
	}
	public void setCollect(String collect) {
		this.collect = collect;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getDuration() {
		return duration;
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
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getUin() {
		return uin;
	}
	public void setUin(String uin) {
		this.uin = uin;
	}
	
	public String getCar_type() {
		return car_type;
	}

	public void setCar_type(String car_type) {
		this.car_type = car_type;
	}
	
	public String getPrepay() {
		return prepay;
	}

	public void setPrepay(String prepay) {
		this.prepay = prepay;
	}

	public String getLimitday() {
		return limitday;
	}

	public void setLimitday(String limitday) {
		this.limitday = limitday;
	}
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "CarNumberOrder [total=" + total + ", carnumber=" + carnumber + ", etime=" + etime + ", btime=" + btime
				+ ", orderid=" + orderid + ", localid=" + localid + ", collect=" + collect + ", discount=" + discount
				+ ", ctype=" + ctype + ", befcollect=" + befcollect + ", distotal=" + distotal + ", shopticketid="
				+ shopticketid + ", tickettype=" + tickettype + ", tickettime=" + tickettime + ", duration=" + duration
				+ ", uin=" + uin + ", hascard=" + hascard + ", ismonthuser=" + ismonthuser + ", prepay=" + prepay
				+ ", limitday=" + limitday + ", lefttop=" + lefttop + ", width=" + width + ", height=" + height
				+ ", rightbottom=" + rightbottom + ", car_type=" + car_type + ", state=" + state + "]";
	}

	public CarNumberOrder(String total, String carnumber, String etime,
			String btime, String orderid, String collect, String discount, 
			String befcollect, String distotal,String shopticketid,
			String tickettype, String tickettime,
			String duration, String uin, String hascard, String ismonthuser,
			String prepay, String lefttop, String width, String height,
			String rightbottom, String car_type ,String state) {
		super();
		this.total = total;
		this.carnumber = carnumber;
		this.etime = etime;
		this.btime = btime;
		this.orderid = orderid;
		this.collect = collect;
		this.discount = discount;
		this.befcollect = befcollect;
		this.distotal = distotal;
		this.shopticketid = shopticketid;
		this.tickettype = tickettype;
		this.tickettime = tickettime;
		this.duration = duration;
		this.uin = uin;
		this.hascard = hascard;
		this.ismonthuser = ismonthuser;
		this.prepay = prepay;
		this.lefttop = lefttop;
		this.width = width;
		this.height = height;
		this.rightbottom = rightbottom;
		this.car_type = car_type;
		this.state = state;
	}

}
