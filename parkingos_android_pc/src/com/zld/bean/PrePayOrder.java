/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015��5��23�� 
 * 
 *******************************************************************************/ 
package com.zld.bean;

/**
 * <pre>
 * ����˵��: Ԥ֧�����
 * ����:	2015��5��23��
 * ������:	HZC
 * 
 * ��ʷ��¼
 *    �޸����ݣ�
 *    �޸���Ա��
 *    �޸����ڣ� 2015��5��23��
 * </pre>
 */
public class PrePayOrder {
	
	private String result;//1�ɹ�-1ʧ��2�����
	private String prefee;//Ԥ֧�����
	private String total;//�ܽ��
	private String collect;//��۽��
	private String discount;// ������
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getPrefee() {
		return prefee;
	}
	public void setPrefee(String prefee) {
		this.prefee = prefee;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
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
	@Override
	public String toString() {
		return "PrePayOrder [result=" + result + ", prefee=" + prefee + ", total=" + total + ", collect=" + collect
				+ ", discount=" + discount + "]";
	}

}
