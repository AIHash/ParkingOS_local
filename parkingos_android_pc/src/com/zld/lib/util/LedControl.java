package com.zld.lib.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.graphics.drawable.Animatable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView.BufferType;

import com.zld.decode.LedServerRunnable;

/**
 * ƽ����Ϊ�����������ӿ��ƿ�--��¼
 * �·��������ֺ���������
 * @author HZC
 */
@SuppressLint("HandlerLeak")
public class LedControl{

	private static final String TAG = "LedControl";
	private static LedControl ledControl = new LedControl();

	private ServerSocket serverSocket;
	private HashMap<String, Socket> socketMap = new HashMap<String, Socket>();

	//��¼���ƿ�ָ��
	public static byte[] login = {
		(byte) 0xfe,0x5c,0x4b,(byte) 0x89, //��ͷ
		0x2a,0x00,0x00,0x00, //�ܳ�
		0x62, //��Ϣ����
		0x00,0x00,0x00,0x00, //ID
		0x17,0x00,0x00,0x00, //���ݳ���
		0x31, //��������1=ͨ����0=�ܾ���18
		0x23,0x32,0x30,0x30,0x38,0x30,0x32,0x32,0x39,0x30,0x35,0x31,0x31,0x30,0x34,0x31,0x35, //ʱ��17
		0x23,0x30,0x35,0x30, //������ʱ�� 50��
		0x23,(byte) 0xff,(byte) 0xff, //��β7
	};

	//����������UIDΪ��095223906�������������ݸ�Ϊ1234567890
	public static byte[] showtext = {
		(byte) 0xfe,0x5c,0x4b,(byte) 0x89,
		0x5e,0x00,0x00,0x00,//��94=5E�ֽ�//��4λ
		0x31,0x00,0x00,(byte) 0x9e,(byte) 0xe4,
		0x4b,0x00,0x00,0x00,//���ݳ���75=4B�ֽ�//��13λ
		0x30,0x39,0x35,0x32,0x32,0x33,0x39,0x30,0x36,//UID//��17λ��ʼ//��25λ����
		0x2c,// �ָ�����,��
		0x01,//�ƶ���ʽ,��27λ
		0x01,//�ƶ��ٶ�,��28λ
		0x01,//ͣ��ʱ��,��29λ
		0x30,0x31,0x30,0x31,0x30,0x31,0x39,0x39,0x31,0x32,0x33,0x31,
		0x13,0x00,0x00,0x00,//�ز����Գ���
		0x55,(byte) 0xaa,0x00,0x00,0x37,0x31,0x31,0x31,
		0x32,//��Ļ˫��ɫ������Ϊ˫��ɫӦΪ31,��54λ
		0x31,0x00,0x00,
		0x08,0x00,//�޸���Ļ���//��58λ��ʼ//ʵ����Ļ/8= д������
		0x10,0x00,//�޸���Ļ�߶�//��60λ��ʼ//ʵ����Ļȡ16���� = д������
		0x01,//��ɫ	 //��62λ��ʼ//��ɫ������λ=������ɫ������λ=��׺��ɫ�� 1=��ɫ  2=��ɫ  3=��ɫ
		0x11,//�����ֺ�//��63λ��ʼ//����λ=����       ����λ=�ֺ�
		//���壺����1��ʼ������Ϊ�����塢���塢���塢���顢���飩;
		//�ֺţ�����0��ʼ������Ϊ12*12��16*16��24*24��32*32��48*48��64*64��80*80��;
		0x00,
		0x14,0x00,0x00,0x00,//�޸������������ݳ��ȣ����������ֽڣ�//��65λ��ʼ
		//0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x30,//�޸ĵ�������������//��69λ��ʼ//��78λ����
		(byte) 0xff,0x00,0x01,0x00,0x01,0x00,0x01,0x00,0x10,
		0x48,0x2d,0x31,0x2c,(byte) 0xff,(byte) 0xff//��93λ
	};

	@SuppressWarnings("unused")
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.e(TAG,"LED��������");
			startLedConn();
		}
	};

	@SuppressLint("HandlerLeak")
	public static LedControl getLedinstance(){
		return ledControl;
	}

	private LedControl() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * ����LED,��ȡLed�����¼����Ϣ,����Ӧ
	 * @return
	 */
	public void startLedConn(){ 
		new Thread(new Runnable(){
			@Override  
			public void run() {
				// TODO Auto-generated method stub  
				service();
			}
		}).start();
	}
	public String bcd2Str(byte[] b) {
		if (b == null) {
			return null;
		}
		char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}

		return sb.toString();
	}	
public void wirteLog(byte[] buffer){
	
	String fileName = FileUtil.getSDCardPath()
			+ "/tcb"+ "/" +"LEDlog.txt";
	if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
		String path = FileUtil.getSDCardPath()+ "/tcb/";  
		File dir = new File(path);  
		if (!dir.exists()) {  
			dir.mkdirs();  
		}  
		
		try {
			FileOutputStream fos = new FileOutputStream(fileName,true);  
			fos.write(bcd2Str(buffer).getBytes());
			String huan = "\n";
			fos.write(huan.toString().getBytes());
			fos.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		 
	}  
}
	/**
	 * ���ݴ��ݵ�ledip��ȡ��ͬ��socket,����LED����
	 * @param ledip 
	 * @param cmd
	 * @return
	 * @throws IOException
	 */		
	public boolean sendLedData(String ledip, byte cmd[]){
//		wirteLog(cmd);
		// TODO Auto-generated method stub
		Socket socket = null;
		if(socketMap != null&&socketMap.size()!=0){
			socket = socketMap.get(ledip);
			Log.e(TAG, "��Ҫ���͵���:"+ledip+"---��Ҫ���͵���Socket��"+socket);
			if(socket != null){
				try{
					OutputStream out = socket.getOutputStream();
					if (out == null){
						return false;
					}
					out.write(cmd);
					out.flush();
					return true;
				}catch(Exception e){
					Log.e(TAG, "�ͻ��˷�����Ϣ�쳣��"+e.getMessage());
					close();
					return false;
				}
			}
		}
		return false;
	}
	public static ArrayList<Byte> setLoginTime(){
		ArrayList<Byte> list = null;
		list = LedStringUtils.asArrayList(login);
		SimpleDateFormat dateYear = new SimpleDateFormat("yyyyMMdd");//�������ڸ�ʽ
		String timeyear = dateYear.format(new Date());// new Date()Ϊ��ȡ��ǰϵͳʱ��
		SimpleDateFormat dateHour = new SimpleDateFormat("HHmmss");//�������ڸ�ʽ
		String timehour = dateHour.format(new Date());// new Date()Ϊ��ȡ��ǰϵͳʱ��
		Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w<1) {
			w=7;
		}
        String allTime = timeyear+"0"+String.valueOf(w)+timehour;
		int index = 19;
		for (int i = 0; i < allTime.length(); i++) {
			int value = Integer.valueOf(allTime.substring(i, i+1));
			list.set(index, (byte)value);
			index ++;
		}
		return list;
	}
	/**
	 * �������������ز�
	 * Byte����ת���ϣ��ı�Byte
	 * UID = "111813617";
	 * content = "��E88888"
	 * width=����
	 * height=����
	 * color = 1=��ɫ  2=��ɫ  3=��ɫ
	 * fontSize = 1����
	 * wordSize = 1�ֺ�
	 * @param UID
	 * @param content
	 */
	public static ArrayList<Byte> change(String UID,String content,int width,int height,
			String color,String fontSize,String wordSize,String ledmovemode){
		int allSize = 0;
		int dataSize = 0;
		int contentSize = 0;
		ArrayList<Byte> list = null;
		List<String> transContentList = null;
		try {
			/*Byte����ת����*/
			list = LedStringUtils.asArrayList(showtext); 
			String encode = URLEncoder.encode(content,"gb2312");
			/*��ʾ�����ֽڼ���*/
			transContentList = LedStringUtils.transContentList(encode);
			contentSize = transContentList.size();
			allSize = contentSize+84;
			dataSize = contentSize+65;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*UID��ֳɼ���*/
		List<String> transList = LedStringUtils.transList(UID);
		for(int i = 0;i < list.size();i++){
			if(i == 4){//�޸��ܳ���
				list.set(4, (byte)(allSize));
			}
			if(i == 13){//�޸����ݳ���
				list.set(13, (byte)dataSize);
			}
			if(i == 17){//�޸�UID j<8
				for(int j=0;j<transList.size();j++){
					list.set(17+j, (byte)(48+Integer.parseInt(transList.get(j))));
					i++;
				}
			}
			if(i == 27){//�ƶ���ʽ
				/*Log.e(TAG,"�ƶ���ʽ��"+ledmovemode);*/
				list.set(27, (byte)Integer.parseInt(ledmovemode));
			}
			if(i == 28){//�ƶ��ٶ�
				/*Log.e(TAG,"�ƶ��ٶȣ�"+ledmovemode);*/
				list.set(28, (byte)Integer.parseInt(ledmovemode));
			}
			if(i == 29){//ͣ��ʱ��
				/*Log.e(TAG,"ͣ��ʱ�䣺"+ledmovemode);*/
				list.set(29, (byte)Integer.parseInt(ledmovemode));
			}
			if(i == 58){//�޸���Ļ���
				/*Log.e(TAG,"width:"+width+"  height:"+height);*/
				list.set(58, (byte)(width/8));
			}
			if(i == 60){//�޸���Ļ�߶�
				list.set(60, (byte)height);
			}
			if(i == 62){//������ɫ
				/*Log.e(TAG,"������ɫ��"+color);*/
				list.set(62, (byte)(Integer.parseInt(color+1)));
			}
			/*if(i == 63){//�����ֺ� 31  25 
				System.out.println("�����ֺţ�"+fontSize+wordSize+"---"+Integer.parseInt(fontSize+wordSize));
				list.set(63, (byte)(Integer.parseInt(fontSize+wordSize)+6));
						}*/
			if(i == 65){//�޸������������ݳ���*/
				list.set(65, (byte)(contentSize+10));
			}
			if(i == 68){//�޸ĵ�������������*/
				for(int k=0;k<contentSize;k++){
					list.add(69+k, (byte)(LedStringUtils.stringToByte(transContentList.get(k))));
				}
			}
		}
		return list;
	}
	
	/**
	 * ͨ��LED���ƿ�������Ƶ������
	 * @param content
	 * @return
	 */
	public ArrayList<Byte> change(String content,int cmd){
		int allSize = 0;
		int dataSize = 0;
		int contentSize = 0;
		ArrayList<Byte> list = null;
		List<String> transContentList = null;
		/*Byte����ת����*/
		if (cmd == 1) {
			list = LedStringUtils.asArrayList(SpeakerControl.cmd); 
		}else {
			list = LedStringUtils.asArrayList(SpeakerControl.cmd2); 
		}
		
		try {
			String encode = URLEncoder.encode(content,"gb2312");
			//���� ����
			transContentList = LedStringUtils.transContentList(encode);
			contentSize = transContentList.size();
			allSize = contentSize+26;
			dataSize = contentSize+7;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0;i < list.size();i++){
			if(i == 4){//�޸��ܳ���
				list.set(4, (byte)(allSize));
			}
			if(i == 13){//�޸����ݳ���
				list.set(13, (byte)dataSize);
			}
			if(i == 19){//�޸������������ݳ���
				list.set(19, (byte)(contentSize+2));
			}
			if(i == 22){//�޸ĵ�������������
				for(int k=0;k<contentSize;k++){
					list.add(22+k, (byte)(LedStringUtils.stringToByte(transContentList.get(k))));
				}
			}
		}
		LedStringUtils.dumpMemory("LedControl", LedStringUtils.asByteList(list));
		return list;
	}

	public void service(){
		try{
			Log.e(TAG, "������������");
			while(true){
				if(serverSocket == null){
					serverSocket = new ServerSocket(8888);
				}
				Socket socket = serverSocket.accept();
				Log.e(TAG, "�����豸����");
				String inetAddress = socket.getInetAddress().toString();
				String address = inetAddress.subSequence(1, inetAddress.length()).toString();
				if(null != socketMap&&!socketMap.containsKey(address)){
					socketMap.put(address, socket);
				}
				Log.e(TAG,"���տͻ���Socket��"+socket.toString());
				Thread workThread = new Thread(new LedServerRunnable(address,socket));
				workThread.start();
			}
		}catch(IOException e){
			e.printStackTrace();
			Log.e(TAG,"ServerSocket��acceptʱ����"+e.getMessage());
		}
	}

	public void close(){
		try {
			if(socketMap != null){
				for (Entry<String, Socket> entry : socketMap.entrySet()) {
					if(null != entry.getValue()&&entry.getValue().isConnected()){
						/*entry.getValue().setSoLinger(true, 3000);*/
						Log.e(TAG,"�Ƿ��ѹرգ�"+entry.getValue().isClosed());
						if(!entry.getValue().isClosed()){
							entry.getValue().close();
							Log.e(TAG, "Map��Socket�ȹر�");
						}
					}
				}
			}
			socketMap.clear();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			Log.e(TAG, "�ر�Socket���socketMap��"+socketMap);
//			Message message = new Message();
//			message.what = 1;
//			Log.e(TAG, "ServerScoket30�������");
//			handler.sendMessageDelayed(message, 30000);
		}
	}
	
	public void destory(){
		Log.e(TAG,"LedSocket:destory");
		try {
			if(socketMap != null){
				for (Entry<String, Socket> entry : socketMap.entrySet()) {
					if(null != entry.getValue()&&entry.getValue().isConnected()){
						Log.e(TAG,"Activity��Destoryʱ�ر�Socket�Ƿ��ѹرգ�"+entry.getValue().isClosed());
						if(!entry.getValue().isClosed()){
							entry.getValue().close();
							Log.e(TAG, "Map��Socket�ȹر�");
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**����ʵʱ�ɼ��ز�*/
	public static ArrayList<Byte> changeshow(String UID,String content,int width,int height,
			String color,String fontSize,String wordSize,String ledmovemode){
		int allSize = 0;
		int dataSize = 0;
		int contentSize = 0;
		ArrayList<Byte> list = null;
		List<String> transContentList = null;
		try {
			/*ģ������Byte����ת����*/
			list = LedStringUtils.asArrayList(intimeshow); 
			/*��ʾ����*/
			String encode = URLEncoder.encode(content,"gb2312");
			/*��ʾ�����ֽڼ���*/
			transContentList = LedStringUtils.transContentList(encode);
			/*��ʾ�����ֽڼ��ϵĳ���*/
			contentSize = transContentList.size();
			/*�ܳ�*/
			allSize = contentSize+24;
			/*���ݳ���*/
			dataSize = contentSize+5;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0;i < list.size();i++){
			if(i == 4){//�޸��ܳ���
				list.set(4, (byte)(allSize));
			}
			if(i == 13){//�޸����ݳ���
				list.set(13, (byte)(dataSize+6));
			}
			if(i == 17){//�޸�������
				list.set(17, (byte)Integer.parseInt(UID));
			}
			if(i == 19){//������ɫ--δ����
				if (color.equals("0")) {
					list.set(19, (byte)(0x11));
				}else if (color.equals("1")) {
					list.set(19, (byte)(0x22));
				}else if (color.equals("2")) {
					list.set(19, (byte)(0x33));
				}

			}
//			if(i == 19){//�����ֺ� 31  25 
//				System.out.println("�����ֺţ�"+
//				fontSize+wordSize+"---"+Integer.parseInt(fontSize+wordSize));
//				list.set(20, (byte)(Integer.parseInt(fontSize+wordSize)+6));
//			}
			if(i == 21){//�޸ı������ݳ���
				list.set(21, (byte)contentSize);
			}
			if(i == 22){//�޸ĵ�ʵʱ�ɼ�����*/
				for(int k=0;k<contentSize;k++){
					list.add(22+k, (byte)(LedStringUtils.stringToByte(transContentList.get(k))));
				}
			}

		}
		LedStringUtils.dumpMemory("yuyin", LedStringUtils.asByteList(list));
		return list;
	}

	//��ʵʱ�ɼ������ͺ�Ϊ��41��ʵʱ�ɼ�����Ϊ2013;
	public static byte[] intimeshow = {
		(byte) 0xfe,0x5c,0x4b,(byte) 0x89,//��ͷ
		0x1c,0x00,0x00,0x00,//�����ܳ�
		0x65,//��Ϣ����
		(byte) 0x92,0x79,(byte) 0x95,0x72,//����ID
		0x09,0x00,0x00,0x00,//���ݳ���
		0x29,//������
		0x00,//��˸
		0x11,//��ɫ
		0x11,//����
		0x04,//�������ݳ���
		/*0x32,0x30,0x31,0x33,//��ʾ����*/	
		(byte) 0xff,(byte) 0xff
	};
	
	/**���õ㲥�ز�*/
	public static ArrayList<Byte> trafficLightBlue(){
		ArrayList<Byte> list = null;
		list = LedStringUtils.asArrayList(demandBlue); 
		
		LedStringUtils.dumpMemory("yuyin", LedStringUtils.asByteList(list));
		return list;
	}
	/**���õ㲥�ز�*/
	public static ArrayList<Byte> trafficLightRed(){
		ArrayList<Byte> list = null;
		list = LedStringUtils.asArrayList(demandRed); 
		
		LedStringUtils.dumpMemory("yuyin", LedStringUtils.asByteList(list));
		return list;
	}
	
	// �㲥����
	public static byte[] demandRed = {
			(byte)0xfe,0x5c,0x4b,(byte) 0x89, // ǰ����ʶ
			0x20,0x00,0x00,0x00,//�����ܳ�
			0x67,  // ��Ϣ����             
			(byte) 0x99,0x43,(byte) 0x02,0x34,//��Ϣid
			0x0d,0x00,0x00,0x00,//����ָ��� 
			0x01, (byte) 0xFE, 			//����������������
			0x00, 			//����ʱ��
			0x00, 0x00, 			//����
			0x00, 			//�����
			0x00, 0x00, 			//ͼƬ��ʼ���
			0x01, 0x00, 			//ͼƬ����
			0x09, 0x00, (byte) 0xff, 		//�ƶ���ʽ
			(byte) 0xFF,(byte) 0xFF			//��β
	};
	// �㲥����
		public static byte[] demandBlue = {
				(byte)0xfe,0x5c,0x4b,(byte) 0x89, // ǰ����ʶ
				0x20,0x00,0x00,0x00,//�����ܳ�
				0x67,  // ��Ϣ����             
				(byte) 0x99,0x43,(byte) 0x02,0x34,//��Ϣid
				0x0d,0x00,0x00,0x00,//����ָ��� 
				0x01, (byte) 0xFE, 			//����������������
				0x00, 			//����ʱ��
				0x00, 0x00, 			//����
				0x00, 			//�����
				0x01, 0x00, 			//ͼƬ��ʼ���
				0x01, 0x00, 			//ͼƬ����
				0x09, 0x00, (byte) 0xff, 		//�ƶ���ʽ
				(byte) 0xFF,(byte) 0xFF			//��β
		};
}

