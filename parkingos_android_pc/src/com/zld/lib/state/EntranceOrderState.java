/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015��4��20�� 
 * 
 *******************************************************************************/ 
package com.zld.lib.state;

/**
 * <pre>
 * ����˵��: ���ɶ���״̬
 * ����:	2015��4��20��
 * ������:	HZC
 * 
 * ��ʷ��¼
 *    �޸����ݣ�
 *    �޸���Ա��
 *    �޸����ڣ� 2015��4��20��
 * </pre>
 */
public class EntranceOrderState {
	private int state = AUTO_COME_IN_STATE;
	public static final int AUTO_COME_IN_STATE = 0;//�����Զ����ɶ���״̬
	public static final int ADD_CAR_ORDER_STATE = 1;//��¼�������ɶ���״̬

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
