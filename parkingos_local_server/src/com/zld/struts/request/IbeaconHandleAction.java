package com.zld.struts.request;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.zld.AjaxUtil;
import com.zld.impl.PublicMethods;
import com.zld.service.DataBaseService;
import com.zld.service.LogService;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;

/**
 * ��������uuid����̨�����������ɼ���ɶ���
 * �����ֻ��ϴ�Ibeacon���������� �ʹ���
 * 
 * @author Laoyao
 * @date 2014-05-12
 */
public class IbeaconHandleAction extends Action {


	@Autowired
	private DataBaseService daService;
	@Autowired
	private LogService logService;

	@Autowired
	private PublicMethods publicMethods;
	
//	@Autowired
//	private MemcacheUtils memcacheUtils;
	
	private Logger logger = Logger.getLogger(IbeaconHandleAction.class);
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		
		Integer major = RequestUtil.getInteger(request, "major", -1);
		Integer minor = RequestUtil.getInteger(request, "minor", -1);
		
		String result = "";
		// �����ֻ�����
		String mobile = RequestUtil.processParams(request, "mobile");
		Long uin = null;
//		if(!mobile.equals("")){
//			Map userMap = getUserMap(mobile,null);
//			if(userMap==null){
//				result = "{\"inout\":\"0\",\"uid\":\"-1\"}";
//				AjaxUtil.ajaxOutput(response, result);
//				return null;
//			}else {
//				uin = (Long)userMap.get("id");
//			}
//		}
		
		logger.info(">>>action:"+action+",major:"+major+",minor:"+minor+",mobile:"+mobile+",uin:"+uin);
		/**
		 * ����ҡһ��,����Ibeacon�Ĳ�������ѯ���豸�ǳ��ڻ�����ڻ��ǳ����ͨ��
		 * ��ڣ����Ƿ���δ����Ķ���:
		 *    �У����ض�����Ϣ
		 *    û�У����ɶ���
		 * ���ڣ���ѯ�Ƿ���δ����Ķ�����
		 * 	      �У����㶩��
		 *    û�У�����ֱ��
		 * ��ںͳ�����һ����
		 * 	�ж������㣬�޶������ɶ������ٴ�ҡ1����������ֱ��
		 * **/
		if (action.equals("ibcincom")) {// �ύibeacon uuid,��ѯͣ������Ϣ
			// ibeacon uuid
			Map cominfo = daService.getPojo("select * from area_ibeacon_tb where major=? and minor=? ",new Object[] { major,minor });
			result = "{\"inout\":\"-1\",\"uid\":\"-1\",\"orderid\":\"0\"}";
			if(cominfo!=null){
				Long pass = (Long) cominfo.get("pass");// ���볡��־ 0��ڣ�1���� 2������ 
				Long comid = (Long) cominfo.get("comid");// ��˾��ͣ������ID
				String inOut = "";
				Map passMap = daService.getMap("select passtype,worksite_id from com_pass_tb where id=?", new Object[]{pass});
				Long worksiteId = null;
				Long uid = -1L;
				if(passMap!=null){
					inOut = (String) passMap.get("passtype");
					worksiteId = (Long)passMap.get("worksite_id");
				}
				//��ѯ�Ƿ���δ����Ķ���
				Long orderId = 0L;
				if(worksiteId!=null){
					Map useWorkSiteMap = daService.getMap("select uin from user_worksite_tb where worksite_id=? ", new Object[]{worksiteId});
					if(useWorkSiteMap!=null){
						uid = (Long)useWorkSiteMap.get("uin");
					}
					orderId = daService.getLong("select max(id) from order_tb where comid=? and uin=?  and state=? ",
							new Object[]{comid,uin,0});
				}
				result = "{\"inout\":\""+inOut+"\",\"uid\":\""+uid+"\",\"orderid\":\""+orderId+"\"}";
				if(inOut.equals("1")&&orderId==0){
					Map uMap = daService.getMap("select u.nickname,c.company_name from user_info_Tb u left join com_info_Tb c " +
							"on u.comid = c.id where u.id =? ", new Object[]{uid});
					if(uMap!=null&&!uMap.isEmpty()){
						result = "{\"inout\":\""+inOut+"\",\"uid\":\""+uid+"\",\"orderid\":\""+orderId+"\",\"name\":\""+uMap.get("nickname")+"\",\"parkname\":\""+uMap.get("company_name")+"\"}";
					}else {
						result = "{\"inout\":\"1\",\"uid\":\"-1\",\"orderid\":\"0\"}";
					}
				}
			}
			//http://s.tingchebao.com/zld/ibeaconhandle.do?major=0&minor=1&action=ibcincom&mobile=15801482643
		} else if (action.equals("addorder")) {// ���ɶ���
			Long uid = RequestUtil.getLong(request, "uid", -1L);
			Map cominfo = daService.getPojo("select comid from area_ibeacon_tb where major=? and minor=? ",new Object[] { major,minor });
			if(cominfo!=null){
				Long comid = (Long) cominfo.get("comid");// ��˾��ͣ������ID
				result =addOrder(uin,comid,uid,major+"_"+minor);
			}
			//infoMap = addOrder(request);
			// ����ѯ������10Ԫ����
		} else if (action.equals("doorder")) {// �����ύ���㶩������
			//infoMap = balance(request);
			Long uid = RequestUtil.getLong(request, "uid", -1L);
			Long orderid = RequestUtil.getLong(request, "orderid", -1L);
			if(uid==-1||uid==0){
				result= "{\"result\":\"0\",\"info\":\"����ʧ�ܣ�û���շ�Ա�ڸڣ�\"}";
			}else {
				result = doPreOrder(uid,orderid);
			}
			//http://s.tingchebao.com/zld/ibeaconhandle.do?action=doorder&uid=orderid=
		} else if(action.equals("payorder")){//�շ�Ա���㶩��
			Long id = RequestUtil.getLong(request, "id", -1L);
			Double price = RequestUtil.getDouble(request, "total", 0d);
//			result = doOrder(id,price);
			//http://s.tingchebao.com/zld/ibeaconhandle.do?action=payorder&id=&total=
			
		}else if(action.equals("clearmem")){
//			Integer type = RequestUtil.getInteger(request, "type", 0);
//			result = "���ʧ��";
//			if(type==0){//���ͣ��ȯʹ�ô��� 
//				Map<Long ,String> map = memcacheUtils.doMapLongStringCache("usetickets_times", null, null);
//				//System.err.println(map);
//				//logger.info(">>>>update>>> uin:"+uin+",map:"+ map);
//				if(map!=null){
//					if(map.get(uin)!=null){
//						map.remove(uin);
//						memcacheUtils.doMapLongStringCache("usetickets_times", map, "update");
//						logger.info(">>>>>>>>>>�����ͣ��ȯʹ�ô���,�ֻ���"+mobile);
//						result = "����ɹ�";
//					}
//				}
//			}else if(type==1){//����������
//				Map<Long ,String> map = memcacheUtils.doMapLongStringCache("backtickets_times", null, null);
//				//System.err.println(map);
//				//logger.info(">>>>update>>> uin:"+uin+",map:"+ map);
//				if(map!=null){
//					if(map.get(uin)!=null){
//						map.remove(uin);
//						memcacheUtils.doMapLongStringCache("backtickets_times", map, "update");
//						logger.info(">>>>>>>>>>������������,�ֻ���"+mobile);
//						result = "����ɹ�";
//					}
//				}
//			}else if(type==2){//������ͻ���
//				Map<Long ,Long> map = memcacheUtils.doMapLongLongCache("reward_userticket_cache", null, null);
//				//System.err.println(map);
//				//logger.info(">>>>update>>> uin:"+uin+",map:"+ map);
//				if(map!=null){
//					if(map.get(uin)!=null){
//						map.remove(uin);
//						memcacheUtils.doMapLongLongCache("reward_userticket_cache", map, "update");
//						logger.info(">>>>>>>>>>��������ͻ���,�ֻ���"+mobile);
//						result = "����ɹ�";
//					}
//				}
//			}
		}
		logger.info(result);
		AjaxUtil.ajaxOutput(response, result);
		return null;
	}
	
	private String doPreOrder(Long uid,Long orderid) {
		logger.info("uid:"+uid+",orderid:"+orderid);
		Map orderMap = daService.getPojo("select * from order_tb where id=? ",new Object[] {orderid});
		if (orderMap != null && orderMap.get("state") != null) {// ����δ����Ķ����������ύ��ɶ���
			//�ж�״̬���Ƿ���δ����
			//Long uin =(Long)orderMap.get("uin");
			Integer state = (Integer)orderMap.get("state");
			Long start = (Long)orderMap.get("create_time");
			Long comid = (Long)orderMap.get("comid");
			Integer car_type = (Integer)orderMap.get("car_type");//0��ͨ�ã�1��С����2����
			String carNumber = (String)orderMap.get("car_number");
			Long orderId = (Long)orderMap.get("id");
			Long etime = System.currentTimeMillis()/1000;
			String duration = StringUtils.getTimeString(start, etime);
			Long uin = (Long)orderMap.get("uin");
			//����ɹ����������ش�����Ϣ
			if(state==1){
				return "{\"result\":\"2\",\"info\":\"��֧�����������ظ�֧��\"}";
			}else {
				daService.update("update order_tb set uid = ? where id   = ?", new Object[]{uid,orderid});
			}
			//�鶩�����
			String price = publicMethods.getPrice(start,etime, comid, car_type);
			Double totalmoney = Double.valueOf(price);
			Double ticketMoney = 0d;
			Double balance = 0d;
			//����õ�ȯ
			Map ticketMap = null;
//			Long time = System.currentTimeMillis()/1000;
//			if(memcacheUtils.readUseTicketCache(uin))//ÿ��ʹ�ò���������
//				ticketMap= publicMethods.useTickets(uin, totalmoney,comid,uid,0);
			if(ticketMap!=null)
				ticketMoney = Double.valueOf(ticketMap.get("money")+"");
			//���û����
			Map userMap = daService.getMap("select balance from user_info_Tb where id =?", new Object[]{uin});
			if(userMap!=null)
				balance = Double.valueOf(userMap.get("balance")+"");
			else {
				return "{\"result\":\"0\",\"info\":\"�˻��쳣���Ժ�����!\"}";
			}
			if(totalmoney>(ticketMoney+balance)){
				return "{\"result\":\"3\",\"info\":\"���㣬���ȳ�ֵ!\"}";
			}
			//���շ�Ա����Ϣ
//			logService.insertParkUserMessage(comid, 1, uid,carNumber, orderId, StringUtils.formatDouble(price),duration, 0, start, etime,0);
			return "{\"result\":\"1\",\"info\":\"�շ�Ա���ڽ���\"}";
		} else {// ����û�пɽ���Ķ���
			return "{\"result\":\"0\",\"info\":\"û�пɽ���Ķ���\"}";
		}
	}

	
//	private String doOrder(Long id,Double totalmoney) {
//		Map orderMap = daService.getPojo("select * from order_tb where id=? ",new Object[] {id});
//		if (orderMap != null && orderMap.get("state") != null) {// ����δ����Ķ����������ύ��ɶ���
//			//�ж�״̬���Ƿ���δ����
//			double balance=0d;
//			Long uin =(Long)orderMap.get("uin");
//			Map userMap = getUserMap(null,uin);
//			balance = Double.valueOf(userMap.get("balance")+"");
//			Integer state = (Integer)orderMap.get("state");
//			Long start = (Long)orderMap.get("create_time");
//			Long comid = (Long)orderMap.get("comid");
//			Long uid = (Long)orderMap.get("uid");
//			String carNumber = publicMethods.getCarNumber(uin);
//			//����ɹ����������ش�����Ϣ
//			if(state==1){
//				return "{\"result\":\"2\",\"info\":\"��֧�����������ظ�֧��\"}";
//			}
//			/*//�ж��Ƿ����Զ�֧��
//			Integer autoCash=0;
//			Map upMap = daService.getPojo("select auto_cash,limit_money from user_profile_tb where uin =?", new Object[]{uin});
//			Integer limitMoney = 0;
//			if(upMap!=null&&upMap.get("auto_cash")!=null){
//				autoCash= (Integer)upMap.get("auto_cash");
//				limitMoney = (Integer)upMap.get("limit_money");
//			}
//			//δ�����Զ�֧��
//			if(autoCash==0){
//				return "{\"result\":\"0\",\"info\":\"��δ�����Զ�֧��\"}";
//			}*/
//			
//			//�����ͣ��ȯ
//			Map ticketMap = null;
////			Long time = System.currentTimeMillis()/1000;
////			if(memcacheUtils.readUseTicketCache(uin))//ÿ��ʹ�ò���������
////				ticketMap= publicMethods.useTickets(uin, totalmoney,comid,uid,0);
////					daService.getMap("select * from ticket_tb where uin = ? and limit_day > ? " +
////						"and state=? order by money desc limit ? ",
////						new Object[]{uin,time,0,1});
//			Double tickMoney = 0d;//����ͣ��ȯ���
//			Long ticketId = null;//ͣ��ȯID
//			if(ticketMap!=null){
//				tickMoney = StringUtils.formatDouble(ticketMap.get("money"));
//				ticketId = (Long)ticketMap.get("id");
//			}
//			
//			boolean isupmoney=false;//�Ƿ�ɳ����Զ�֧���޶�
////			if(limitMoney!=null){
////				if(limitMoney==-1||limitMoney>=totalmoney-tickMoney)//����ǲ��޻����֧�������Զ�֧�� 
////					isupmoney=false;
////			}
//			//���������Զ�֧���޶�
//			if(isupmoney){
//				//����������Ϣ
//				logService.insertMessage(comid, -1, uin,carNumber, id,totalmoney,"", 0,start,System.currentTimeMillis()/1000,9);
//				return "{\"result\":\"0\",\"info\":\"���������Զ�֧���޶�\"}";
//			}
//			//����
//			if(balance+tickMoney<totalmoney){
//				//����������Ϣ
//				logService.insertMessage(comid, -1, uin,carNumber, id,totalmoney,"", 0,start,System.currentTimeMillis()/1000,9);
//				return "{\"result\":\"0\",\"info\":\"����\"}";
//			}
//			//֧������
//			String comName = "";
//			Map comMap = daService.getMap("select company_name from com_info_tb where id=? ",new Object[]{comid});
//			if(comMap!=null)
//				comName = (String)comMap.get("company_name");
//			String ret =  payIbeaconOrder(start, totalmoney, (Long)orderMap.get("id"), tickMoney, comid, uin, uid, ticketId, comName, carNumber);
//			//����Ϣ������
//			
//			if(ret.equals("1")){
//				return "{\"result\":\"1\",\"info\":\"����ɹ�\"}";
//			}else {
//				return "{\"result\":\"0\",\"info\":\"����ʧ��\"}";
//			}
//		} else {// ����û�пɽ���Ķ���
//			return "{\"result\":\"0\",\"info\":\"û�пɽ���Ķ���\"}";
//		}
//	}

	
	private String addOrder(Long uin, Long comid, Long uid, String uuid) {
		//��ѯ���ƺ�
		String carNumber=publicMethods.getCarNumber(uin);
		if(carNumber.equals("���ƺ�δ֪"))
			carNumber = "";
		Map orderMap = daService.getPojo("select * from order_tb where comid=? and uin=? and state=? ",new Object[] { comid, uin, 0});
		int result =0;
		if (orderMap != null && orderMap.get("state") != null) {// �Ѿ������˶���
			return "{\"result\":\"0\",\"info\":\"�Ѵ���δ����Ķ������������ɽ�������\"}";
		} else {// �����¶���
			result = daService.update("insert into order_tb(create_time,comid,uin,state,c_type,car_number,uid,nfc_uuid) values(?,?,?,?,?,?,?,?)",
					new Object[] {System.currentTimeMillis()/1000, comid, uin, 0 ,1,carNumber,uid,uuid});
			String ret = "��ӭ����ͣ����";
			if(result!=1)
				ret = "���ɶ������󣬽���ʧ��";
			//doInOrder(infoMap, comid, uin, balance, carNumber);
			return "{\"result\":\""+result+"\",\"info\":\""+ret+"\"}";
		}
	}

	/**
	 * �����ֻ������uuid�������½�����ɶ���
	 * @param mobile
	 * @param uuid
	 * @return
	 */
	
	/**
	 * ֧��Ibeacon����
	 * @param start ������ʼʱ��
	 * @param total �ܽ��
	 * @param orderId �������
	 * @param ticketMoney ȯ���
	 * @param comid ������� 
	 * @param uin �����˺�
	 * @param uid �շ�Ա�˺�
	 * @param ticketId ȯ��� 
	 * @param comName �������� 
	 * @param carNumber ���ƺ�
	 * @return
	 */
//	private String payIbeaconOrder(Long start,Double total,Long orderId,Double ticketMoney,
//			Long comid,Long uin,Long uid,Long ticketId,String comName,String carNumber){
//
//		Long ntime = System.currentTimeMillis()/1000;
//		
//		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
//		//���¶���״̬���շѳɹ�
//		Map<String, Object> orderSqlMap = new HashMap<String, Object>();
//		//�����û����
//		Map<String, Object> userSqlMap = new HashMap<String, Object>();
//		//�շ�Ա�˻�
//		Map<String, Object> parkuserAccountsqlMap =new HashMap<String, Object>();
//		//�����˻�
//		Map<String, Object> userAccountsqlMap =new HashMap<String, Object>();
//		//�����˻���ͣ��ȯ
//		Map<String, Object> userTicketAccountsqlMap =new HashMap<String, Object>();
//		//�շ�Ա���
//		Map<String, Object> parkusersqlMap =new HashMap<String, Object>();
//		//ʹ��ͣ��ȯ����
//		Map<String, Object> ticketsqlMap =new HashMap<String, Object>();
//		//ͣ�����˻���ͣ��ȯ���
//		Map<String, Object> tingchebaoAccountsqlMap =new HashMap<String, Object>();
//		
//		Long etime = System.currentTimeMillis()/1000;
////		if(start!=null&&start==etime)
////			etime = etime+60;
//		orderSqlMap.put("sql", "update order_tb set state =?,pay_type=?, end_time=?,total=? where id=?");
//		orderSqlMap.put("values", new Object[]{1,2,etime,total,orderId});
//		bathSql.add(orderSqlMap);
//		
//		userSqlMap.put("sql", "update user_info_tb  set balance =balance-? where id=?");
//		userSqlMap.put("values", new Object[]{total-ticketMoney,uin});
//		if(total-ticketMoney>0)
//			bathSql.add(userSqlMap);
//		
//		if(ticketMoney>0&&ticketId!=null&&ticketId>0){//ʹ��ͣ��ȯ���������˻��ȳ�ֵ
//			userTicketAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type) values(?,?,?,?,?,?)");
//			userTicketAccountsqlMap.put("values", new Object[]{uin,ticketMoney,0,ntime-1,"ͣ��ȯ��ֵ",7});
//			bathSql.add(userTicketAccountsqlMap);
//		}
//		
//		userAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type) values(?,?,?,?,?,?)");
//		userAccountsqlMap.put("values", new Object[]{uin,total,1,ntime,"ͣ����-"+comName,0});
//		bathSql.add(userAccountsqlMap);
//		
//
//		parkusersqlMap.put("sql", "update user_info_tb  set balance =balance+? where id=?");
//		parkusersqlMap.put("values", new Object[]{total,uid});
//		bathSql.add(parkusersqlMap);
//		
//		parkuserAccountsqlMap.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target) values(?,?,?,?,?,?)");
//		parkuserAccountsqlMap.put("values", new Object[]{uid,total,0,ntime,"ͣ����_"+carNumber,4});
//		bathSql.add(parkuserAccountsqlMap);
//		
//		//�Ż�ȯʹ�ú󣬸���ȯ״̬�����ͣ�����˻�֧����¼
//		if(ticketMoney>0&&ticketId!=null&&ticketId>0){
//			ticketsqlMap.put("sql", "update ticket_tb  set state=?,comid=?,utime=?,umoney=? where id=?");
//			ticketsqlMap.put("values", new Object[]{1,comid,System.currentTimeMillis()/1000,ticketMoney,ticketId});
//			bathSql.add(ticketsqlMap);
//			
//			tingchebaoAccountsqlMap.put("sql", "insert into tingchebao_account_tb(amount,type,create_time,remark,utype) values(?,?,?,?,?)");
//			tingchebaoAccountsqlMap.put("values", new Object[]{ticketMoney,1,ntime,comName+"������"+carNumber+"��ʹ��ͣ������ȯ",0});
//			bathSql.add(tingchebaoAccountsqlMap);
////			memcacheUtils.updateUseTicketCache(uin);//��ȯ����ʹ��ȯ����
//		}
//		
//		boolean result= daService.bathUpdate(bathSql);
//		logger.info(">>>>>>>>>>>>>>>֧�� ��"+result);
//		if(result){
//			//�����֣�����������ȯ��������
//			/* ÿ��������΢�Ż�֧����֧��1Ԫ���ϵ���ɵģ���������2Ԫ����������3Ԫ��ͣ��ȯ��
//			 * �������ֲ���(ͬһ����ÿ��ֻ�ܷ�3��)��
//			 * ����ÿ�շ�ȯ��3��ȯ
//			 * ÿ������ÿ��ʹ��ͣ��ȯ������3�� */
//			try {
//				//���ǲ��Ǻ�������
//				boolean isBlack=true;
////				List<Long> blackUserList = memcacheUtils.doListLongCache("zld_black_users", null, null);
////				if(blackUserList==null||!blackUserList.contains(uin)){//���ں������п��Դ����Ƽ�����
////					isBlack=false;
////				}
////				if(!isBlack){
////					if(total>=1//&&memcacheUtils.readBackMoneyCache(comid+"_"+uin)){//���Ը��������� 
////						boolean isset = false;
////						boolean isCanBackMoney = publicMethods.isCanBackMoney(comid);//�Ƿ��Ǽ��ϳ���
////						if(isCanBackMoney){
////							List<Map<String, Object>> insertSqlList = new ArrayList<Map<String,Object>>();
////							Map<String, Object> userInfoSql = new HashMap<String, Object>();
////							Map<String, Object> parkAccountSql = new HashMap<String, Object>();
////							
////							userInfoSql.put("sql", "update user_info_tb set balance=balance+? where id=?");
////							userInfoSql.put("values",new Object[]{2.0,uid});
////							insertSqlList.add(userInfoSql);
////							
////							parkAccountSql.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target) values(?,?,?,?,?,?)");
////							parkAccountSql.put("values",new Object[]{uid,2.0,0,ntime,"ͣ��������",3});
////							insertSqlList.add(parkAccountSql);
////							
////							isset = daService.bathUpdate(insertSqlList);
////							if(isset){
////								memcacheUtils.updateBackMoneyCache(comid+"_"+uin);
////							}
////						}
////					}else {
////						logger.info(">>>>>>>>>���ֳ���3��..."+comid+"_"+uin);
////					}
////				}else {
////					logger.info(">>>>>black>>>>������"+uin+",�ں������ڣ������������� ......");
////				}
//////				if(total-ticketMoney>=1){
////				if(!isBlack){
////					publicMethods.backTicket(total-ticketMoney, orderId, uin,comid,"");
////				}else { 
////					logger.info(">>>>>black>>>>������"+uin+",�ں������ڣ����ܷ����......");
////				}
////				//����������Ϣ
////				logService.insertMessage(comid, 2, uin,carNumber, orderId,total,"", 0,start,etime,9);
////				//logService.insertMessage(comid, 2, uid,carNumber, orderId,total,"", 0,start,etime,0);
////				return "1";
////			}catch (Exception e) {
////				//����������Ϣ
////				logService.insertMessage(comid, -1, uin,carNumber, orderId,total,"", 0,start,etime,9);
////				//logService.insertMessage(comid, -1, uid,carNumber, orderId,total,"", 0,start,etime,0);
////				e.printStackTrace();
////				return "0";
////			}
////		}else {
////			//����������Ϣ
////			logService.insertMessage(comid, -1, uin,carNumber, orderId,total,"", 0,start,etime,9);
////			//logService.insertMessage(comid, -1, uid,carNumber, orderId,total,"", 0,start,etime,0);
////			return  "0";
//		}
//	}
//	
//	/**
//	 * ��֤�����ֻ�
//	 * @param response
//	 * @param mobile
//	 * @return
//	 */
//	private Map getUserMap(String mobile,Long id) {
//		Map userMap =null;
//		if(mobile!=null){
//			userMap = daService.getPojo(
//					"select id,balance from user_info_Tb where mobile=? and auth_flag= ?",
//					new Object[] { mobile ,4});
//			
//		}else if(id!=null){
//			userMap = daService.getPojo(
//					"select id,balance from user_info_Tb where id=?",
//					new Object[] {id});
//		}
//		if (userMap != null && userMap.get("id") != null)
//			return userMap;
//		return null;
//	}
//	
}
