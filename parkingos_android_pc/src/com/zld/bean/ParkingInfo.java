package com.zld.bean;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class ParkingInfo implements Serializable {

	private String service;//�˹�����
	private String parkingtotal;// �ܳ�λ��
	private String phone; // �����绰
	private String parktype; // ��������
	private String price; // �����۸�
	private String address; // ��ַ
	private String name; // ��������
	private String mobile; // �ֻ���
	private String stoptype; // ͣ������
	private String timebet; // ����ʱ���
	private String id; // ��˾���
	private String picurls;
	private String resume; // ��������
	private String isfixed;// -- 0:δ��λ 1�Ѷ�λ
	private String longitude;// ����
	private String latitude;// γ��
	private String car_type = "";//�Ƿ����ִ�С��
	private int passfree;//�Ƿ�֧�����
	private String ishidehdbutton;//�Ƿ���ʾ���㶩����ť��1��ʾ 0����
	private String issuplocal;//�Ƿ�֧�ֱ��ػ�,1֧�� 0��֧��
	private List<CarType> allCarTypes; // ���г�������
	private List<FreeResons> freereasons; // �����������
	private List<LiftReason> liftreason; // �ֶ�̧��ԭ��
	private String ishdmoney;  // �Ƿ���ʾ�շ��ۼӽ�� 0����ʾ 1������
	private String fullset;		//��λ�����ܷ����
	private String leaveset;	//����ʶ��ʶ��̧������  ���е��¿�����û���շѣ����շѣ���

	public List<LiftReason> getLiftReason() {
		return liftreason;
	}

	public void setLiftReason(List<LiftReason> liftReason) {
		this.liftreason = liftReason;
	}

	public List<FreeResons> getFreeResons() {
		return freereasons;
	}

	public void setFreeResons(List<FreeResons> freereasons) {
		this.freereasons = freereasons;
	}

	public List<CarType> getAllCarTypes() {
		return allCarTypes;
	}

	public void setAllCarTypes(List<CarType> allCarTypes) {
		this.allCarTypes = allCarTypes;
	}

	public String getIssuplocal() {
		return issuplocal;
	}

	public void setIssuplocal(String issuplocal) {
		this.issuplocal = issuplocal;
	}

	public String getIshidehdbutton() {
		return ishidehdbutton;
	}

	public void setIshidehdbutton(String ishidehdbutton) {
		this.ishidehdbutton = ishidehdbutton;
	}

	public int getPassfree() {
		return passfree;
	}

	public void setPassfree(int passfree) {
		this.passfree = passfree;
	}

	public ParkingInfo() {
		super();
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getPicurls() {
		return picurls;
	}

	public void setPicurls(String picurls) {
		this.picurls = picurls;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParkingtotal() {
		return parkingtotal;
	}

	public void setParkingtotal(String parkingtotal) {
		this.parkingtotal = parkingtotal;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getParktype() {
		return parktype;
	}

	public void setParktype(String parktype) {
		this.parktype = parktype;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getStoptype() {
		return stoptype;
	}

	public void setStoptype(String stoptype) {
		this.stoptype = stoptype;
	}

	public String getTimebet() {
		return timebet;
	}

	public void setTimebet(String timebet) {
		this.timebet = timebet;
	}

	public String getResume() {
		return resume;
	}

	public void setResume(String resume) {
		this.resume = resume;
	}

	public String getIsfixed() {
		return isfixed;
	}

	public void setIsfixed(String isfixed) {
		this.isfixed = isfixed;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public String getCar_type() {
		return car_type;
	}

	public void setCar_type(String car_type) {
		this.car_type = car_type;
	}

	public String getIsShowMoney() {
		return ishdmoney;
	}

	public void setIsHDShowMoney(String isShowMoney) {
		this.ishdmoney = isShowMoney;
	}

	

	@Override
	public String toString() {
		return "ParkingInfo [service=" + service + ", parkingtotal="
				+ parkingtotal + ", phone=" + phone + ", parktype=" + parktype
				+ ", price=" + price + ", address=" + address + ", name="
				+ name + ", mobile=" + mobile + ", stoptype=" + stoptype
				+ ", timebet=" + timebet + ", id=" + id + ", picurls="
				+ picurls + ", resume=" + resume + ", isfixed=" + isfixed
				+ ", longitude=" + longitude + ", latitude=" + latitude
				+ ", car_type=" + car_type + ", passfree=" + passfree
				+ ", ishidehdbutton=" + ishidehdbutton + ", issuplocal="
				+ issuplocal + ", allCarTypes=" + allCarTypes
				+ ", freereasons=" + freereasons + ", liftreason=" + liftreason
				+ ", ishdmoney=" + ishdmoney + ", fullset=" + fullset
				+ ", leaveset=" + leaveset + "]";
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

}
