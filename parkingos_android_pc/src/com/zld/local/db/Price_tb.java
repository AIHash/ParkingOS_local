//package com.zld.local.db;
//
//public class Price_tb {
//	// price_tb �۸��
//	// id bigint NOT NULL,
//	// comid bigint,
//	// price numeric(10,2) DEFAULT 0,
//	// state bigint DEFAULT 0, -- 0������1ע��
//	// unit integer,
//	// pay_type integer, -- 0:��ʱ�Σ�1������
//	// create_time bigint,
//	// b_time integer,
//	// e_time integer,
//	// is_sale integer DEFAULT 0, -- �Ƿ���� 0��1��
//	// first_times integer DEFAULT 0, -- ���Ż�ʱ�Σ���λ����
//	// fprice numeric(10,2) DEFAULT 0, -- ���Żݼ۸�
//	// countless integer DEFAULT 0, -- ��ͷ�Ʒ�ʱ������λ����
//	// free_time integer DEFAULT 0, -- ���ʱ������λ:����
//	// fpay_type integer DEFAULT 0, -- �����ʱ���Ʒѷ�ʽ��1:��� ��0:�շ�
//	// isnight integer DEFAULT 0, -- ҹ��ͣ����0:֧�֣�1��֧��
//	// isedit integer DEFAULT 0, -- �Ƿ�ɱ༭�۸�Ŀǰֻ���ռ䰴ʱ�۸���Ч,0��1�ǣ�Ĭ��0
//	// car_type integer DEFAULT 0, -- 0��ͨ�ã�1��С����2����
//	// is_fulldaytime integer DEFAULT 0, -- �Ƿ����ռ�ʱ��
//	// update_time bigint,
//
//	public String id;
//	public String comid;
//	public String price;
//	public String state;
//	public String unit;
//	public String pay_type;
//	public String create_time;
//	public String b_time;
//	public String e_time;
//	public String is_sale;
//	public String first_times;
//	public String fprice;
//	public String countless;
//	public String free_time;
//	public String fpay_type;
//	public String isnight;
//	public String isedit;
//	public String car_type;
//	public String is_fulldaytime;
//	public String update_time;
//
//	public Price_tb() {
//		super();
//	}
//
//	public Price_tb(String id, String comid, String price, String state, String unit, String pay_type, String create_time,
//			String b_time, String e_time, String is_sale, String first_times, String fprice, String countless, String free_time,
//			String fpay_type, String isnight, String isedit, String car_type, String is_fulldaytime, String update_time) {
//		super();
//		this.id = id;
//		this.comid = comid;
//		this.price = price;
//		this.state = state;
//		this.unit = unit;
//		this.pay_type = pay_type;
//		this.create_time = create_time;
//		this.b_time = b_time;
//		this.e_time = e_time;
//		this.is_sale = is_sale;
//		this.first_times = first_times;
//		this.fprice = fprice;
//		this.countless = countless;
//		this.free_time = free_time;
//		this.fpay_type = fpay_type;
//		this.isnight = isnight;
//		this.isedit = isedit;
//		this.car_type = car_type;
//		this.is_fulldaytime = is_fulldaytime;
//		this.update_time = update_time;
//	}
//
//	@Override
//	public String toString() {
//		return "Price_tb [id=" + id + ", comid=" + comid + ", price=" + price + ", state=" + state + ", unit=" + unit
//				+ ", pay_type=" + pay_type + ", create_time=" + create_time + ", b_time=" + b_time + ", e_time=" + e_time
//				+ ", is_sale=" + is_sale + ", first_times=" + first_times + ", fprice=" + fprice + ", countless=" + countless
//				+ ", free_time=" + free_time + ", fpay_type=" + fpay_type + ", isnight=" + isnight + ", isedit=" + isedit
//				+ ", car_type=" + car_type + ", is_fulldaytime=" + is_fulldaytime + ", update_time=" + update_time + "]";
//	}
//
//
//
//}
