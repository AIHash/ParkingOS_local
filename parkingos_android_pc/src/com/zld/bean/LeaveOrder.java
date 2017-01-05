package com.zld.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LeaveOrder implements Serializable {
//
//	<content id='1'>
//	<info>
//	<id>142</id>
//	<issale>1</issale>
//	<total>12.00</total>
//	<carnumber>��A54321</carnumber>
//	<etime>14:17</etime>
//	<btime>10:46</btime>
//	<state>0</state>
//	<mtype>1</mtype>
//	<orderid>47</orderid>
//	</info>
//	<info>
//	{"mtype":2,"info":{"total":"1.00","duration":"null","carnumber":"��F8KR99","etime":"20:08","state":"1",
//	"btime":"20:03","orderid":"1506"}}
	private String state;// 0δ֧��.1.��֧��.2.�ֽ�֧��3.֧���У�
	private String carnumber;// ����
	private String total;// ���
	private String btime;// ��ʼʱ��
	private String etime;// ��ʼʱ��
	private  int   id;// ��Ϣ���
	private String orderid;// ������
	private String mtype;// ��Ϣ���� -1 token��Ч 0 �볡������
	private String issale;// �Ƿ��Ż� 0��.1��
	private int maxid;// ��ǰ����curri��

	public String getState() {
		return state;
	}

	public int getMaxid() {
		return maxid;
	}

	public void setMaxid(int maxid) {
		this.maxid = maxid;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCarnumber() {
		return carnumber;
	}

	public void setCarnumber(String carnumber) {
		this.carnumber = carnumber;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getBtime() {
		return btime;
	}

	public void setBtime(String btime) {
		this.btime = btime;
	}

	public String getEtime() {
		return etime;
	}

	public void setEtime(String etime) {
		this.etime = etime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getMtype() {
		return mtype;
	}

	public void setMtype(String mtype) {
		this.mtype = mtype;
	}

	public String getIssale() {
		return issale;
	}

	public void setIssale(String issale) {
		this.issale = issale;
	}

	public LeaveOrder() {
		super();
	}

	@Override
	public String toString() {
		return "LeaveOrder [state=" + state + ", carnumber=" + carnumber
				+ ", total=" + total + ", btime=" + btime + ", etime=" + etime
				+ ", id=" + id + ", orderid=" + orderid + ", mtype=" + mtype
				+ ", issale=" + issale + ", maxid=" + maxid + "]";
	}

}
