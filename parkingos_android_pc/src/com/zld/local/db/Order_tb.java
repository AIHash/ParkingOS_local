//package com.zld.local.db;
//
//public class Order_tb {
//
//	// order_tb ������
//	// id bigint NOT NULL,
//	// create_time bigint,
//	// comid bigint NOT NULL,
//	// uin bigint NOT NULL,--�����˺ţ�
//	// total numeric(30,2),
//	// state integer, -- 0δ֧�� 1��֧�� 2:�ӵ�.6��������
//	// end_time bigint,
//	// auto_pay integer DEFAULT 0, -- �Զ����㣬0����1����
//	// pay_type integer DEFAULT 0, -- 0:�ʻ�֧��,1:�ֽ�֧��,2:�ֻ�֧�� 3�¿�8���
//	// nfc_uuid character varying(36),
//	// c_type integer DEFAULT 1, -- 0:NFC,1:IBeacon,2:���� 3ͨ������ 4ֱ�� 5�¿��û�
//	// uid bigint DEFAULT (-1), -- �շ�Ա�ʺ�
//	// car_number character varying(50), -- ����
//	// imei character varying(50), -- �ֻ�����
//	// pid integer DEFAULT (-1), --�۸�
//	// �Ʒѷ�ʽ��0��ʱ(0.5/15����)��1���Σ�12Сʱ��10Ԫ,ǰ1/30min����ÿСʱ1Ԫ��
//	// car_type integer DEFAULT 0, -- 0��ͨ�ã�1��С����2����
//	// pre_state integer DEFAULT 0, -- Ԥ֧��״̬ 0 �ޣ�1Ԥ֧���У�2�ȴ�����֧�����
//	// in_passid bigint DEFAULT (-1), -- ����ͨ��id
//	// out_passid bigint DEFAULT (-1), -- ����ͨ��id
//
//	public String id;
//	public String create_time;
//	public String comid;
//	public String uin;
//	public String total;
//	public String prepay;
//	public String state;
//	public String end_time;
//	public String auto_pay;
//	public String pay_type;
//	public String nfc_uuid;
//	public String c_type;
//	public String uid;
//	public String car_number;
//	public String imei;
//	public String pid;
//	public String car_type;
//	public String pre_state;
//	public String in_passid;
//	public String out_passid;
//	public String localid;
//
//	public Order_tb() {
//		super();
//	}
//
//	/**
//	 * @param id �������
//	 * @param create_time ����ʱ��
//	 * @param comid �������
//	 * @param state ����״̬ 0δ��
//	 * @param nfc_uuid 
//	 * @param c_type 0:NFC,1:IBeacon,2:���� 3ͨ������ 4ֱ�� 5�¿��û�
//	 * @param uid �շ�Ա���
//	 * @param car_number ����
//	 * @param imei �ֻ�����
//	 */
//	public Order_tb(String id, String create_time, String comid, String state, String nfc_uuid, String c_type, String uid,
//			String car_number, String imei) {
//		super();
//		this.id = id;
//		this.create_time = create_time;
//		this.comid = comid;
//		this.state = state;
//		this.nfc_uuid = nfc_uuid;
//		this.c_type = c_type;
//		this.uid = uid;
//		this.car_number = car_number;
//		this.imei = imei;
//	}
//	/**
//	 * 
//	 * @param id orderid
//	 * @param create_time
//	 * @param comid
//	 * @param uin �����˺�
//	 * @param total
//	 * @param state ����״̬
//	 * @param end_time
//	 * @param auto_pay  �Զ����㣬0����1����
//	 * @param pay_type DEFAULT 0, -- 0:�ʻ�֧��,1:�ֽ�֧��,2:�ֻ�֧�� 3�¿�8���
//	 * @param nfc_uuid 
//	 * @param c_type 0:NFC,1:IBeacon,2:���� 3ͨ������ 4ֱ�� 5�¿��û�
//	 * @param uid �շ�Ա���
//	 * @param car_number ���ƺ�
//	 * @param imei �ֻ�����
//	 * @param pid �۸� ��ʱ ����
//	 * @param car_type 0��ͨ�ã�1��С����2����
//	 * @param pre_state Ԥ֧��״̬ 0 �ޣ�1Ԥ֧���У�2�ȴ�����֧�����
//	 * @param in_passid ����ͨ��id
//	 * @param out_passid ����ͨ��id
//	 */
//	public Order_tb(
//			String id,String localid, String create_time, String comid, String uin,
//			String total, String prepay, String state, String end_time,String auto_pay,
//			String pay_type, String nfc_uuid, String c_type, String uid, String car_number,
//			String imei,String pid, String car_type, String pre_state, String in_passid,
//			String out_passid) {
//		super();
//		this.id = id;
//		this.create_time = create_time;
//		this.comid = comid;
//		this.uin = uin;
//		this.total = total;
//		this.prepay = prepay;
//		this.state = state;
//		this.end_time = end_time;
//		this.auto_pay = auto_pay;
//		this.pay_type = pay_type;
//		this.nfc_uuid = nfc_uuid;
//		this.c_type = c_type;
//		this.uid = uid;
//		this.car_number = car_number;
//		this.imei = imei;
//		this.pid = pid;
//		this.car_type = car_type;
//		this.pre_state = pre_state;
//		this.in_passid = in_passid;
//		this.out_passid = out_passid;
//		this.localid = localid;
//	}
//
//	public String getId() {
//		return id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}
//
//	public String getCreate_time() {
//		return create_time;
//	}
//
//	public void setCreate_time(String create_time) {
//		this.create_time = create_time;
//	}
//
//	public String getComid() {
//		return comid;
//	}
//
//	public void setComid(String comid) {
//		this.comid = comid;
//	}
//
//	public String getUin() {
//		return uin;
//	}
//
//	public void setUin(String uin) {
//		this.uin = uin;
//	}
//
//	public String getTotal() {
//		return total;
//	}
//
//	public void setTotal(String total) {
//		this.total = total;
//	}
//
//	public String getPrepay() {
//		return prepay;
//	}
//
//	public void setPrepay(String prepay) {
//		this.prepay = prepay;
//	}
//
//	public String getState() {
//		return state;
//	}
//
//	public void setState(String state) {
//		this.state = state;
//	}
//
//	public String getEnd_time() {
//		return end_time;
//	}
//
//	public void setEnd_time(String end_time) {
//		this.end_time = end_time;
//	}
//
//	public String getAuto_pay() {
//		return auto_pay;
//	}
//
//	public void setAuto_pay(String auto_pay) {
//		this.auto_pay = auto_pay;
//	}
//
//	public String getPay_type() {
//		return pay_type;
//	}
//
//	public void setPay_type(String pay_type) {
//		this.pay_type = pay_type;
//	}
//
//	public String getNfc_uuid() {
//		return nfc_uuid;
//	}
//
//	public void setNfc_uuid(String nfc_uuid) {
//		this.nfc_uuid = nfc_uuid;
//	}
//
//	public String getC_type() {
//		return c_type;
//	}
//
//	public void setC_type(String c_type) {
//		this.c_type = c_type;
//	}
//
//	public String getUid() {
//		return uid;
//	}
//
//	public void setUid(String uid) {
//		this.uid = uid;
//	}
//
//	public String getCar_number() {
//		return car_number;
//	}
//
//	public void setCar_number(String car_number) {
//		this.car_number = car_number;
//	}
//
//	public String getImei() {
//		return imei;
//	}
//
//	public void setImei(String imei) {
//		this.imei = imei;
//	}
//
//	public String getPid() {
//		return pid;
//	}
//
//	public void setPid(String pid) {
//		this.pid = pid;
//	}
//
//	public String getCar_type() {
//		return car_type;
//	}
//
//	public void setCar_type(String car_type) {
//		this.car_type = car_type;
//	}
//
//	public String getPre_state() {
//		return pre_state;
//	}
//
//	public void setPre_state(String pre_state) {
//		this.pre_state = pre_state;
//	}
//
//	public String getIn_passid() {
//		return in_passid;
//	}
//
//	public void setIn_passid(String in_passid) {
//		this.in_passid = in_passid;
//	}
//
//	public String getOut_passid() {
//		return out_passid;
//	}
//
//	public void setOut_passid(String out_passid) {
//		this.out_passid = out_passid;
//	}
//
//	public String getLocalid() {
//		return localid;
//	}
//
//	public void setLocalid(String localid) {
//		this.localid = localid;
//	}
//
//	@Override
//	public String toString() {
//		return "Order_tb [id=" + id + ", create_time=" + create_time
//				+ ", comid=" + comid + ", uin=" + uin + ", total=" + total
//				+ ", prepay=" + prepay + ", state=" + state + ", end_time="
//				+ end_time + ", auto_pay=" + auto_pay + ", pay_type="
//				+ pay_type + ", nfc_uuid=" + nfc_uuid + ", c_type=" + c_type
//				+ ", uid=" + uid + ", car_number=" + car_number + ", imei="
//				+ imei + ", pid=" + pid + ", car_type=" + car_type
//				+ ", pre_state=" + pre_state + ", in_passid=" + in_passid
//				+ ", out_passid=" + out_passid + ", localid=" + localid + "]";
//	}
//
//}
