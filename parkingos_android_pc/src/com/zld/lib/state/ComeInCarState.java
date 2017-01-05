/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015��4��20�� 
 * 
 *******************************************************************************/ 
package com.zld.lib.state;


/**
 * <pre>
 * ����˵��: ���ɶ���״̬
 * 		       �������״̬,�����ﲻ����"�˳�Ϊ�¿��û�"
 * 		       ����������״̬ʱ,�շ�Ա�ֶ�ˢ��,����Ϊˢ���б�״̬,��ȻҲ�Ქ��.
 * 		       ����һ����Ϊ��ѳ�ʱ,������Զ�ˢ��,����Ϊˢ���б�״̬,��Ȼ�Ļ������ǻᲥ��.
 * ����:	2015��4��20��
 * ������:	HZC
 * 
 * ��ʷ��¼
 *    �޸����ݣ�
 *    �޸���Ա��
 *    �޸����ڣ� 2015��4��20��
 * </pre>
 */
public class ComeInCarState {
	private int state = ENTRANCE_COME_IN_CAR_STATE;
	/**�������״̬*/
	public static final int ENTRANCE_COME_IN_CAR_STATE = 0;
	/**�����������ɶ���״̬*/
	public static final int EXIT_COME_IN_CAR_STATE = 1;
	/**�ֶ�ˢ���б�״̬*/
	public static final int MANUAL_REFRESH_ORDER_LIST = 3;
	/**�Զ�ˢ���б�״̬*/
	public static final int AUTO_REFRESH_ORDER_LIST = 4;
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	/**
	 * �Ƿ���"��������״̬"״̬
	 * @return
	 */
	public boolean isAutoSearchState() {
		return this.getState() == ComeInCarState.EXIT_COME_IN_CAR_STATE;
	}
}
