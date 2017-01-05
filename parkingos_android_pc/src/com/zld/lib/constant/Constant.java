package com.zld.lib.constant;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class Constant {
	/**
	 * ������س�����
	 */
	/*1��PollingService�޸Ļ�ȡ�볡��Ϣ�ֶ�
	  2��LoginActivity���޸�longinSuccess�������
	  Constant.requestUrl  Constant.serverUrl  Constant.pingUrl(ip);*/
	/**
	 * ����URL ��������һ��
	 */
	public static final String UPDATE_URL = "http://d.tingchebao.com/update/puserhd/update.xml";
	/**
	 * �ֶ����µ�URL
	 */
//	public static final String UPDATE_URL_HAND = "http://d.tingchebao.com/update/puserhd/update_hand.xml";
	public static String getUpdateUrlHand(){
		String url="";
		if(requestUrl.contains("/zld/")){
			url = requestUrl.replace("/zld/", "");
		}
		url+="/update/puserhd/update_hand.xml";
		Log.e("----", url);
		return url;
	}
//	
//	/*���Ƿ��ñ��ط�����ʱ,Ĭ�������Ϸ�����*/
	public static String requestUrl = "http://s.tingchebao.com/zld/";
//	public static String requestUrl = "http://192.168.199.239/zld/";
//	public static String requestUrl = "http://180.150.188.224:8080/zld/";
//	public static String requestUrl = "http://yxiudongyeahnet.vicp.cc:50803/zld/";
	public static String serverUrl = "http://s.tingchebao.com/mserver/";
//	���ڱ��ط�����ʱ,��ȡ���ϵ�֧����Ϣ
//	public static final String mserverline = "http://s.tingchebao.com/mserver/";

	//	public static String mserverline = serverUrl;
	
	// Ԥ���߷�����
//	public static String requestUrl = "http://180.150.188.224:8080/zld/";
//	public static String serverUrl = "http://180.150.188.224:8080/mserver/";
//	// ���ڱ��ط�����ʱ,��ȡ���ϵ�֧����Ϣ
//	public static final String mserverline = "http://180.150.188.224:8080/mserver/";

//	 haixiang���ز���
//	public static String requestUrl = "http://192.168.199.239/zld/";
//	public static String serverUrl = "http://192.168.199.239/mserver/";	
//	/*���ڱ��ط�����ʱ,��ȡ���ϵ�֧����Ϣ*/
//	public static String mserverline = "http://192.168.199.239/mserver/";

	
//	// ��Ҧ���ز���
//	public static String requestUrl = "http://192.168.199.240/zld/";
//	public static String serverUrl = "http://192.168.199.240/mserver/";	
////	/*���ڱ��ط�����ʱ,��ȡ���ϵ�֧����Ϣ*/
//	public static String mserverline = "http://192.168.199.240/mserver/";

//	�ٻԱ��ز���
//	public static String requestUrl = "http://192.168.199.156:8088/zld/";
//	public static String serverUrl = "http://192.168.199.156:8088/mserver/";	
//	/*���ڱ��ط�����ʱ,��ȡ���ϵ�֧����Ϣ*/
//	public static String mserverline = "http://192.168.199.156:8088/mserver/";
	
	/*���ط�����ʱ,�������,Pingͨ����������,Ping��ͨ������,����ʱPing����,ͨ���л�������*/
//	public static String PING_TEST_LOCAL = "http://192.251:8080/zld/";
	
	/*��¼ʱ����ip,��ʹ�ñ��ط�����,*/
	public static void requestUrl(String ip){
		requestUrl = "http://"+ip+":8080/zld/";
	}
	public static void serverUrl(String ip){
		serverUrl = "http://"+ip+":8080/mserver/";
	}
	
	public static void pingUrl(String ip){
		Log.e("shuyu", "pingUrl"+ip);
		PING_TEST_LOCAL = "http://"+ip+":8080/zld/worksiteinfo.do?comid=&action=queryworksite";
	}
	
	/*����ƽ�屾�ػ�,�������*/
	public static String PING_TEST_LOCAL = "http://s.tingchebao.com/zld/worksiteinfo.do?comid=&action=queryworksite";
//	public static String PING_TEST_LOCAL = "http://192.168.199.251:8080/zld/";
	// ��Ҧ
//	public static String PING_TEST_LOCAL = "http://192.168.199.240/zld/";
//	public static String PING_TEST_LOCAL = "http://192.168.199.239/zld/";
	
	// Ԥ���߷�����
//	public static String PING_TEST_LOCAL = "http://180.150.188.224:8080/zld/";
	
	//	/**���°汾*/
	//	public static final String UPDATE_URL = requestUrl;
	//	/**PING��������ж�����*/
//		public static final String PING_TEST = "http://192.168.199.251:8080/zld/";

	/**�������������ϻ��Ǳ���*/
//	public static final String LINE_LOCAL = "192.168.199.251";
//	public static final String LINE_LOCAL = "192.168.199.240";
//	public static final String LINE_LOCAL = "192.168.199.239";
	
//	/**�������������ϻ��Ǳ���*/
	public static final String LINE_LOCAL = "s.tingchebao.com";

//	/**�������������ϻ��Ǳ���*/  Ԥ����
//	public static final String LINE_LOCAL = "180.150.188.224:8080";

	public static final int DELETE_IMAGE = 500;
	public static long ONEDAYTAMP = 1*24*60*60*1000;// 10�� ���ڵĺ���

	/**�����̨*/
	public static final String INTO_BACK = "http://s.tingchebao.com/zld/";
	/**�����̨*/
//	public static final String INTO_BACK = "http://180.150.188.224:8080/zld/";
//	public static final String INTO_BACK = "http://192.168.199.251:8080/zld/";
//	public static final String INTO_BACK = "http://192.168.199.240/zld/";

	/**��ѯ����*/
	public static final String QUERY_ORDER = "cobp.do?action=queryorder";
	/**��ǰ����*/
	public static final String GET_CURRORDER = "cobp.do?action=getcurrorder";
	/**�볡����*/
	public static final String ORDER_HISTORY = "collectorrequest.do?action=orderhistory";
	/**��ǰ��������*/
	public static final String CAT_ORDER = "cobp.do?action=catorder";
	/**�޸Ĵ�С���ƷѲ���*/
	public static final String CHANGE_CAR_TYPE = "cobp.do?action=changecartype";
	/**�޸Ĵ�С���ƷѲ���*/
//	public static final String GET_CAR_TYPE = "cobp.do?action=getcartype";
	/**�շ�Ա��Ϣ*/
	public static final String COLLECTOR_INFO = "collectorrequest.do?action=getnewincome";
	/**���㶩��*/
	public static final String COMPLETE_ORDER = "nfchandle.do?action=completeorder";
	/**�޸Ķ���*/
	public static final String MODIFY_ORDER = "cobp.do?action=addcarnumber";
	/**��Ѷ���*/
	public static final String FREE_ORDER = "collectorrequest.do?action=freeorder";
	/**���ɶ���*/
	public static final String MADE_ORDER = "cobp.do?action=preaddorder";
	/**̧�˶�����¼*/
	public static final String LIFT_ORDER = "collectorrequest.do?action=liftrodrecord";
//	/**̧�˶���ԭ��*/
//	public static final String LIFT_ORDER_REASON = "collectorrequest.do?action=liftrodreason";
//	/**̧�˶���ͼƬ*/
//	public static final String LIFT_ORDER_PICTURE = "collectorrequest.do?action=liftroduppic";
	/**����ʱ����Сʱ**/
	public static final String HD_DERATE = "nfchandle.do?action=hdderate";
	/**��������������*/
	public static final String CHANG_INVALIDORDER = "collectorrequest.do?action=invalidorders";
	/**��ȡ������Ϣ*/
	public static final String COMINFO = "collectorrequest.do?action=cominfo";
//	/**��ȡ��բ��Ϣ*/
//	public static final String CONTROLINFO = "worksiteinfo.do?action=getbrake";
//	/**��ȡ����ͷ��Ϣ*/
//	public static final String CAMERAINFO = "worksiteinfo.do?action=querycamera";
//	/**��ȡLED��Ϣ*/
//	public static final String LEDINFO = "worksiteinfo.do?action=queryled";
	/**��ȡ����վ����������ͷ��LED��Ϣ*/
	public static final String WORKINFO = "worksiteinfo.do?action=getpassinfo";
	/**ǿ�����ɶ���*/
	public static final String ADD_CAR = "cobp.do?action=addorder";
	/**Ԥ֧��*/
	public static final String PRE_PAY = "nfchandle.do?action=doprepayorder";
	/**�°�*/
	public static final String AFTER_WORK = "collectorrequest.do?action=gooffwork";
	/**��ȡ����վ��Ϣ*/
	public static final String QUERY_WORKSITE = "worksiteinfo.do?action=queryworksite";
	/**��ȡ��Ӧ����վͨ����Ϣ*/
	public static final String QUERY_PASS_INFO = "worksiteinfo.do?action=querypass";
	/**��ȡ��¼��Ϣ*/
	public static final String LOGIN = "collectorlogin.do?";
	/**��ȡ�볡������Ϣ*/
	public static final String GET_LEAVE_MESG = "getmesg.do?";
	/**��ȡ������Ϣ*/
	public static final String GET_SHARE = "getshare.do?";
	/**����ͼƬ*/
	public static final String DOWNLOAD_IMAGE = "carpicsup.do?action=downloadpic";
	/**����log*/
	public static final String DOWNLOAD_LOGO_IMAGE = "carpicsup.do?action=downloadlogpic";
//	/**�����¿����ƺ�*/
//	public static final String MONTH_CARD_CARNUMBER = "local.do?action=synchroVip";
//	/**ͬ������*/
//	public static final String SYNCHRO_ORDER = "local.do?action=synchroOrder";

	/**��ȡ������֧����ص�*/
	public static final String PAY_BACK = "cobp.do?action=line2local";
//	/**�ϴ�����ͷ״̬*/
//	public static final String UPLOAD_CAMERA_STATE = "parkinter.do?action=uploadcamerastate";
//	/**�ϴ���բ״̬*/
//	public static final String UPLOAD_BRAKE_STATE = "parkinter.do?action=uploadbrakestate";
//	/**�ϴ�LED״̬*/
//	public static final String UPLOAD_LED_STATE = "parkinter.do?action=uploadledstate";

	/**ͼƬ�������*/
	public static final int HOME_PHOTOTYPE = 0;
	/**ͼƬ���ͳ���*/
	public static final int EXIT_PHOTOTYPE = 1;

	/**
	 * ��Ϣ��س�����
	 */
	/**�볡������Ϣ*/
	public static final int LEAVEORDER_MSG = 1;
	/**�볡������Ϣ*/
	public static final int PARKING_NUMS_MSG = 2;
	public static final int SHOWVIDEO_MSG = 60;
	public static final int OPENCAMERA_SUCCESS_MSG = 61;
	public static final int PICUPLOAD_FILE = 62;
	public static final int SHOWPIC_ONRIGHT_MSG = 63;
	public static final int OPENCAMERA_FAIL_MSG = 64;
	public static final int COMECAR_MSG = 65;

	public static final int KEEPALIVE = 66;
	public static final int KEEPALIVE_TIME = 67;
	public static final int HOME_DELAYED_TIME = 68;
	public static final int EXIT_DELAYED_TIME = 69;
	public static final int STOP = 70;
	public static final int NONETWORK_MSG = 80;	

	public static final int DELAY_UPLOAD = 81;	
	public static final int LIST_REFRESH = 82;	
	public static final int CLEAR_ORDER = 83;
	public static final int UPPOLE_IMAGR_SUCCESS = 84;
	public static final int UPPOLE_IMAGR_ERROR = 85;
	public static final int REFRESH_NOMONTHCAR_IMAGE = 86;
	public static final int REFRESH_NOMONTHCAR2_IMAGE = 87;
	public static final int HOME_CAR_OUTDATE_ICON = 89;
	public static final int PLAY_PULL = 88;

//	public final static int ADDORDER_SUCCESS = 10;
//	public final static int ADDORDER_ERROR = 11;
//	public final static int LED_CONN_ERROR = 12;

//	public static final int MORE_CLICK = 3;
//	public static final int CALCULATE_TIME = 4;
	public static final int KEEP_TIME = 5;
	public static final int RESTART_YES = 6;

	/*ͬ���������ʱ��*/
	public static final long time = 1000*60*1;

	//������س���ֵ
	/*#define LT_UNKNOWN  0   //δ֪����
	#define LT_BLUE     1   //����С����
	#define LT_BLACK    2   //����С����
	#define LT_YELLOW   3   //���Ż���
	#define LT_YELLOW2  4   //˫�Ż��ƣ���β�ƣ�ũ�ó���
	#define LT_POLICE   5   //��������
	#define LT_ARMPOL   6   //�侯����
	#define LT_INDIVI   7   //���Ի�����
	#define LT_ARMY     8   //���ž�����
	#define LT_ARMY2    9   //˫�ž�����
	#define LT_EMBASSY  10  //ʹ�ݳ���
	#define LT_HONGKONG 11  //��۽����й���½����
	#define LT_TRACTOR  12  //ũ�ó���
	#define LT_COACH	13  //��������
	#define LT_MACAO	14  //���Ž����й���½����
	#define LT_ARMPOL2   15 //˫���侯����
	#define LT_ARMPOL_ZONGDUI 16  // �侯�ܶӳ���
	#define LT_ARMPOL2_ZONGDUI 17 // ˫���侯�ܶӳ���*/
	public static final int LT_POLICE = 5;
	public static final int LT_ARMPOL = 6;
	public static final int LT_ARMY = 8;
	public static final int LT_ARMY2 = 9;
	public static final int LT_ARMPOL2 = 15;
	public static final int LT_ARMPOL_ZONGDUI = 16;
	public static final int LT_ARMPOL2_ZONGDUI = 17;

	public static final String INTENT_KEY = "intentkey";
	//���ƺŵ���������
//	public static final int CAR_PLATE_LENTH = 7;

	//����ͼƬ�ļ���·��
	public static final String FRAME_DUMP_FOLDER_PATH = Environment
			.getExternalStorageDirectory() + File.separator + "tingchebao/";
	
//	/**����ͷ����״̬:�ɹ�*/
//	public static final String CAMERA_STATE_SUCCESS = Constant.sOne;
//	/**����ͷ����״̬:�Ͽ�*/
//	public static final String CAMERA_STATE_FAILE = Constant.sZero;

	public static final int StopVedio = 0x20001;
	public static final int StartVedio = 0x20002;
 
	public static final int SelectVedio = 0x20009;
	public static final int ConfigDeivce = 0x20010;
	public static final int DClickVedio = 0x200011;
	public static final int PlateImage = 0x200012;

	public static final String sZero = "0";
	public static final String sOne = "1";
	public static final String sTwo = "2";
	public static final String sThree = "3";
	public static final String sNine = "9";

	public static final int BerthHandlerWhat = 1219;
}
