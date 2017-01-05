package com.zld.lib.state;

import android.util.Log;

public class OrderListState {
	private int state = PARK_IN_STATE;
	public static final int PARK_IN_STATE = 0;//�ڳ�����״̬
	public static final int PARK_OUT_STATE = 1;//�볡����״̬
	public static final int HAND_SEARCH_STATE = 2;//�ֶ�������״̬
	public static final int AUTO_SEARCH_STATE = 3;//���������Զ�������״̬
	public static final int MODIFY_ORDER_STATE = 4;//�޸Ķ���״̬
	public static final int CLEAR_ORDER_STATE = 5;//���ڽ��㶩��״̬
	public static final int CLEAR_FINISH_STATE = 6;//�������״̬
	public static final int ORDER_FINISH_STATE = 7;//�������״̬����û�е���շ���ɻ���ѣ���ʱ��״̬���������ˢ���б�
	public static final int NO_ORDER_STATE = 8;//���������Զ�������û������������ʱ��Ϊ��״̬�������Ѱ�ť���߼����õ�
	public static final int ORDER_FINISH_UPPOLE_STATE = 9;//������ɲ��ң��Ѿ�̧��
	
	/* AppInfoʵ��  */
	private static OrderListState orderListState = new OrderListState(); 
	
	/* ��ֻ֤��һ��OrderListStateʵ�� */  
	private OrderListState() {  
	}  

	/* ��ȡOrderListStateʵ�� ,����ģʽ */  
	public static OrderListState getInstance() {  
		return orderListState;  
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	/**
	 * �Ƿ��ڳ�����״̬
	 * @return
	 */
	public boolean isParkInState() {
		Log.e("OrderListState", "�ڳ�����������ǰ״̬��" + orderListState.getState());
		return orderListState.getState() == OrderListState.PARK_IN_STATE;
	}

	
	/**
	 * �Ƿ��޶���״̬
	 * @return
	 */
	public boolean isNoOrderState() {
		Log.e("OrderListState", "�޶���������ǰ״̬��" + orderListState.getState());
		return orderListState.getState() == OrderListState.NO_ORDER_STATE;
	}

	/**
	 * �Ƿ���㶩��״̬
	 * @return
	 */
	public boolean isClearFinishState() {
		Log.e("OrderListState", "���㶩��������ǰ״̬��" + orderListState.getState());
		return orderListState.getState() == OrderListState.CLEAR_FINISH_STATE;
	}
	
	/**
	 * �Ƿ��볡����״̬
	 * @return
	 */
	public boolean isParkOutState() {
		Log.e("OrderListState", "�볡����������ǰ״̬��" + orderListState.getState());
		return orderListState.getState() == OrderListState.PARK_OUT_STATE;
	}
	
	/**
	 * �Ƿ���"���ڽ��㶩��"״̬
	 * @return
	 */
	public boolean isClearOrderState() {
		Log.e("OrderListState", "���ڽ��㶩��״̬������ǰ״̬��" + orderListState.getState());
		return orderListState.getState() != OrderListState.CLEAR_ORDER_STATE;
	}	
	
	
	
	/**
	 * �Ƿ���"�������״̬����û�е���շ���ɻ���ѣ���ʱ��״̬���������ˢ���б�"״̬
	 * @return
	 */
	public boolean isOrderFinishState() {
		Log.e("OrderListState", "�������״̬����û�е���շ���ɻ���ѣ���ʱ��״̬���������ˢ���б�����ǰ״̬��" + orderListState.getState());
		return orderListState.getState() != OrderListState.ORDER_FINISH_STATE;
	}
	
	/**
	 * �Ƿ���"������ɲ��ң��Ѿ�̧��"״̬
	 * @return
	 */
	public boolean isOrderFinishUppoleState() {
		Log.e("OrderListState", "������ɲ��ң��Ѿ�̧�ˡ�����ǰ״̬��" + orderListState.getState());
		return orderListState.getState() == OrderListState.ORDER_FINISH_UPPOLE_STATE;
	}
	
	/**
	 * �Ƿ���"�ֶ�����"״̬
	 * @return
	 */
	public boolean isHandSearchState() {
		Log.e("OrderListState", "�ֶ�����������ǰ״̬��" + orderListState.getState());
		return orderListState.getState() == OrderListState.HAND_SEARCH_STATE;
	}
	
	/**
	 * �Ƿ���"���������Զ�������"״̬
	 * @return
	 */
	public boolean isAutoSearchState() {
		Log.e("OrderListState", "���������Զ������󡪡���ǰ״̬��" + orderListState.getState());
		return orderListState.getState() == OrderListState.AUTO_SEARCH_STATE;
	}
	
	/**
	 * �Ƿ���"�޸Ķ���״̬"״̬
	 * @return
	 */
	public boolean isModifyOrderState() {
		Log.e("OrderListState", "�޸Ķ���״̬������ǰ״̬��" + orderListState.getState());
		return orderListState.getState() == OrderListState.MODIFY_ORDER_STATE;
	}
}
