package com.zld.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import pay.Constants;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.zld.AjaxUtil;
import com.zld.CustomDefind;
import com.zld.service.DataBaseService;
import com.zld.service.LogService;
import com.zld.utils.Check;
import com.zld.utils.CountPrice;
import com.zld.utils.HttpProxy;
import com.zld.utils.SendMessage;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;
import com.zld.utils.ZLDType;


/**
 * memcached���ߣ�������²�Ʒ��֧����������ѯ���ƺ��� 
 * @author Administrator
 *
 */

@Repository
public class PublicMethods {

	
	private Logger logger = Logger.getLogger(PublicMethods.class);
	
	@Autowired
	private DataBaseService daService;
	@Autowired
	private LogService logService;
//	@Autowired
//	private MemcacheUtils memcacheUtils;
	@Autowired
	private CommonMethods methods;
	

	/**
	 * ����ͣ��ȯ
	 * @param uin   �����˻�
	 * @param value ������
	 * @param number��������
	 * @param ptype ֧������ 0��� 1֧���� 2΢�ţ�4���+֧����,5���+΢��
	 * @return 
	 */
//	public int buyTickets(Long uin, Integer value, Integer number,Integer ptype) {
//		logger.info("buyticket>>>uin:"+uin+",value"+value+",number:"+number+",ptype:"+ptype);
//		boolean isAuth = isAuthUser(uin);
//		//�ۿ�
//		Double discount = Double.valueOf(CustomDefind.getValue("NOAUTHDISCOUNT"));
//		if(isAuth){
//			discount=Double.valueOf(CustomDefind.getValue("AUTHDISCOUNT"));
//		}
//		logger.info("buyticket>>>uin:"+uin+",discount"+discount);
//		 //�˻����֧��
//		Double balance =null;
//		Map userMap = null;
//		//������ʵ�˻����
//		userMap = daService.getPojo("select balance,wxp_openid from user_info_tb where id =?",	new Object[]{uin});
//		if(userMap!=null&&userMap.get("balance")!=null){
//			balance = Double.valueOf(userMap.get("balance")+"");
//		}
//		//ÿ��Ӧ�����
//		Double etotal =  StringUtils.formatDouble(value*discount);
//		//Ӧ�����
//		Double total = StringUtils.formatDouble(etotal*number);
//		logger.info(uin+",balance:"+balance+",total:"+total);
//		logger.info("buyticket>>>uin:"+uin+",discount"+discount+",total:"+total+",balance:"+balance);
//		if(total>balance){//����
//			logger.info(uin+",balance:"+balance+",total:"+total+",����");
//			return -1;
//		}
//		
//		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
//		//�����û����
//		Map<String, Object> userSqlMap = new HashMap<String, Object>();
//		//�����û����
//		Map<String, Object> userAccSqlMap = new HashMap<String, Object>();
//		
//		Long ntime = System.currentTimeMillis()/1000;
//		Long ttime = TimeTools.getToDayBeginTime();
//	    userSqlMap.put("sql", "update user_info_tb  set balance =balance-? where id=?");
//		userSqlMap.put("values", new Object[]{total,uin});
//		bathSql.add(userSqlMap);
//		
//		userAccSqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,target) values(?,?,?,?,?,?,?)");
//		userAccSqlMap.put("values", new Object[]{uin,total,1,ntime,"����ͣ��ȯ("+number+"��"+value+"Ԫ)",ptype,2});
//		bathSql.add(userAccSqlMap);
//		if(number > 0){
//			for(int i=0;i<number;i++){
//				Map<String, Object> ticketSqlMap = new HashMap<String, Object>();
//				ticketSqlMap.put("sql", "insert into ticket_tb (create_time,limit_day,money,pmoney,state,uin,type,resources) values(?,?,?,?,?,?,?,?)" );
//				ticketSqlMap.put("values",new Object[]{ttime,ttime+31*24*60*60-1,value,etotal,0,uin,0,1});
//				bathSql.add(ticketSqlMap);
//			}
//		}
//		boolean result = daService.bathUpdate(bathSql);
//		logger.info("uin:"+uin+",value:"+value+",number:"+number+",result:"+result);
//		if(result){
//			return 1;
//		} else {
//			return 0;
//		}
//	}
	
	
	/**
	 * ���������շ�Ա
	 * @param uin
	 * @param uid
	 * @param orderId
	 * @param ticketId
	 * @param money
	 * @param comId
	 * @param ptype 0��1֧������2΢�ţ�4���+֧����,5���+΢��,7ͣ��ȯ 
	 * @return
	 */
//	public int doparkUserReward(Long uin,Long uid,Long orderId,Long ticketId,Double money,Integer ptype,Integer bind_flag) {
//		logger.info("doparkUserReward>>>uin:"+uin+",uid:"+uid+",orderid:"+orderId+",money:"+money+",ptype"+ptype+",bind_flag:"+bind_flag);
//		Long comId = daService.getLong("select comid from user_info_tb where id=? ", new Object[]{uid});
//		//��ͣ��ȯ���
//		Long count = daService.getLong("select count(id) from parkuser_reward_tb where uin=? and order_id=? ", new Object[]{uin,orderId});
//		if(count>0){
//			logger.info("�Ѵ��͹�>>>uin:"+uin+",orderid:"+orderId+",uid:"+uid);
//			//�Ѵ��͹�
//			return -2;
//		}
//		Long ntime = System.currentTimeMillis()/1000;
//		Double ticketMoney=0.0;
//		if(ticketId != null && ticketId>0){
//			ticketMoney = getTicketMoney(ticketId, 4, uid, money, 2, comId, orderId);
//		}
//		logger.info("uin:"+uin+",uid:"+uid+",orderid:"+orderId+",ticketMoney:"+ticketMoney+",money:"+money+",ticketid:"+ticketId);
//		//���û����
//		Map<String, Object> userMap = null;
//		Double ubalance =null;
//		//������ʵ�˻����
//		if(bind_flag == 1){
//			userMap = daService.getPojo("select balance from user_info_tb where id =?",	new Object[]{uin});
//		}else{
//			userMap = daService.getPojo("select balance from wxp_user_tb where uin=? ", new Object[]{uin});
//		}
//		if(userMap!=null&&userMap.get("balance")!=null){
//			ubalance = Double.valueOf(userMap.get("balance")+"");
//			logger.info(":uin:"+uin+",uid:"+uid+",orderid:"+orderId+",ubalance:"+ubalance+",ticketMoney:"+ticketMoney);
//			ubalance +=ticketMoney;//�û��������Ż�ȯ���
//		}
//		if(ubalance==null||ubalance<money){//�ʻ�����
//			logger.info("�����˻����㣬�˻���"+ubalance+",���ͷѽ�"+money+",uin:"+uin+",orderid:"+orderId+",ticketMoney:"+ticketMoney);
//			return -1;
//		}
//		logger.info("uin:"+uin+",orderid:"+orderId+",uid:"+uid+",ticketMoney:"+ticketMoney);
//		
//		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
//		//ͣ��ȯ
//		Map<String, Object> ticketSqlMap = new HashMap<String, Object>();
//		//�����û����
//		Map<String, Object> userSqlMap = new HashMap<String, Object>();
//		//�û��˻�
//		Map<String, Object> userAccSqlMap = new HashMap<String, Object>();
//		//�շ�Ա���
//		Map<String, Object> parkuserSqlMap = new HashMap<String, Object>();
//		//�շ�Ա�˻�
//		Map<String, Object> parkuserAccSqlMap = new HashMap<String, Object>();
//		//ͣ�����˻�
//		Map<String, Object> tingchebaoAccountsqlMap = new HashMap<String, Object>();
//		//���ͼ�¼
//		Map<String, Object> prakuserRewardsqlMap = new HashMap<String, Object>();
//		
//		Map<String, Object> userTicketAccountsqlMap = new HashMap<String, Object>();
//		//���ͻ���
//		Map<String, Object> rewardsqlMap = new HashMap<String, Object>();
//		//���ͻ�����ϸ
//		Map<String, Object> rewardAccountsqlMap = new HashMap<String, Object>();
//		
//		String carNumber = getCarNumber(uin);
//		if(ticketMoney>0){//����ͣ��ȯ
//			ticketSqlMap.put("sql", "update ticket_tb  set state=?,comid=?,utime=?,umoney=?,orderid=? where id=?");
//			ticketSqlMap.put("values", new Object[]{1,comId,ntime,ticketMoney,orderId,ticketId});
//			bathSql.add(ticketSqlMap);
//			
//			tingchebaoAccountsqlMap.put("sql", "insert into tingchebao_account_tb(amount,type,create_time,remark,utype,orderid) values(?,?,?,?,?,?)");
//			tingchebaoAccountsqlMap.put("values", new Object[]{ticketMoney,1,ntime,"����"+carNumber+"��ʹ��ͣ������ȯ�����շ�Ա"+uid,7,orderId});
//			bathSql.add(tingchebaoAccountsqlMap);
//		}
//		if(money>ticketMoney){//Ҫ�������֧��
//			if(bind_flag == 1){
//				userSqlMap.put("sql", "update user_info_tb  set balance =balance-? where id=?");
//			}else{
//				userSqlMap.put("sql", "update wxp_user_tb  set balance =balance-? where uin=?");
//			}
//			userSqlMap.put("values", new Object[]{money-ticketMoney,uin});
//			bathSql.add(userSqlMap);
//		}
//		
//		userAccSqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,orderid) values(?,?,?,?,?,?,?)");
//		userAccSqlMap.put("values", new Object[]{uin,money,1,ntime,"�����շ�Ա-"+uid,ptype,orderId});
//		bathSql.add(userAccSqlMap);
//		
//		if(ticketMoney>0&&ticketId!=null){//ʹ��ͣ��ȯ���������˻��ȳ�ֵ
//			userTicketAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,orderid) values(?,?,?,?,?,?,?)");
//			userTicketAccountsqlMap.put("values", new Object[]{uin,ticketMoney,0,ntime-1,"����-ͣ��ȯ��ֵ",7,orderId});
//			bathSql.add(userTicketAccountsqlMap);
//		}
//		
//		//�����շ�Ա�˻�
//		parkuserSqlMap.put("sql", "update user_info_tb  set balance =balance+? where id=?");
//		parkuserSqlMap.put("values", new Object[]{money,uid});
//		bathSql.add(parkuserSqlMap);
//		
//		parkuserAccSqlMap.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target,orderid) values(?,?,?,?,?,?,?)");
//		parkuserAccSqlMap.put("values", new Object[]{uid,money,0,ntime,"���ͷ�_"+carNumber,4,orderId});
//		bathSql.add(parkuserAccSqlMap);
//		
//		Long rewardId = daService.getkey("seq_parkuser_reward_tb");
//		prakuserRewardsqlMap.put("sql", "insert into parkuser_reward_tb(id,uin,uid,money,ctime,comid,order_id,ticket_id) values(?,?,?,?,?,?,?,?)");
//		prakuserRewardsqlMap.put("values", new Object[]{rewardId,uin,uid,money,ntime,comId,orderId,ticketId});
//		bathSql.add(prakuserRewardsqlMap);
//		
//		//���ͻ���
//		Long btime = TimeTools.getToDayBeginTime();
//		Long rewardCount = daService.getLong("select count(id) from parkuser_reward_tb where uid=? and ctime>=? ",
//				new Object[] { uid, btime });
//		Map<String, Object> tscoreMap = daService.getMap("select sum(score) tscore from reward_account_tb where type=? and create_time>? and uin=? ", new Object[]{0, btime, uid});
//		Double tscore = 0d;
//		Double rscore = (rewardCount+1)*money;
//		if(tscoreMap != null && tscoreMap.get("tscore") != null){
//			tscore = Double.valueOf(tscoreMap.get("tscore") + "");
//		}
//		logger.info("�շ�Ա���ջ������룺uid:"+uid+",tscore:"+tscore+",���λ���:"+rscore+",rewardCount:"+rewardCount);
//		if(tscore < 5000){//ÿ���������5000����
//			if(tscore + rscore > 5000){
//				rscore = 5000 - tscore;
//				logger.info("���ջ����Ѿ������ޣ�tscore:"+tscore+",rscore:"+rscore+",uid:"+uid);
//			}
//			rewardsqlMap.put("sql", "update user_info_tb set reward_score=reward_score+? where id=? ");
//			rewardsqlMap.put("values", new Object[]{rscore, uid});
//			bathSql.add(rewardsqlMap);
//			
//			rewardAccountsqlMap.put("sql", "insert into reward_account_tb(uin,score,type,create_time,target,reward_id,remark) values(?,?,?,?,?,?,?) ");
//			rewardAccountsqlMap.put("values", new Object[]{uid, rscore, 0, ntime, 0, rewardId,"���� "+carNumber});
//			bathSql.add(rewardAccountsqlMap);
//		}
//		boolean result = daService.bathUpdate(bathSql);
//		logger.info("uin:"+uin+",uid:"+uid+",orderid:"+orderId+",result:"+result);
//		if(result){
//			if(ticketId > 0){//���洦��
//				Map<Long, Long> tcacheMap = memcacheUtils.doMapLongLongCache("reward_userticket_cache", null, null);
//				Long ttime = TimeTools.getToDayBeginTime();
//				if(tcacheMap!=null){
//					tcacheMap.put(uin, ttime);
//				}else {
//					tcacheMap = new HashMap<Long, Long>();
//					tcacheMap.put(uin, ttime);
//				}
//				memcacheUtils.doMapLongLongCache("reward_userticket_cache", tcacheMap, "update");
//			}
//			
//			if(ticketMoney > 0){//����ÿ�ղ�������
////				updateAllowCache(comId, ticketId, ticketMoney);
//				logger.info("update allowance today>>>uin:"+uin+",orderid:"+orderId+",ticketMoney:"+ticketMoney);
//			}
//			return 1;
//		} else {
//			return 0;
//		}
//	}
	
	/**
	 * 
	 * @param uin �û��ʺ�
	 * @param pid ��Ʒ��š�
	 * @param number ��������
	 * @param start ��ʼʱ�䣬��ʽ��20140815
	 * @param ptype :0��1֧������2΢�ţ�3������4���+֧����,5���+΢��,6���+����
	 * @return  0ʧ��,1�ɹ���-1����
	 */
	public int buyProducts(Long uin,Map productMap,Integer number,String start,int ptype){
//		Map productMap = daService.getPojo("select * from product_package_tb where id=? and state=? and remain_number>?",
//				new Object[]{pid,0,0});
//		if(productMap==null||productMap.isEmpty())
//			return 0;
		Long pid = (Long)productMap.get("id");
		//1��ѯ���
		BigDecimal _balance  = (BigDecimal)daService.getObject("select balance from user_info_Tb where id=?",
				new Object[]{uin}, BigDecimal.class);
		Double balance = 0d;
		if(_balance!=null)
			balance = _balance.doubleValue();
		logger.info("��ǰ�ͻ���"+balance);
		Double price = Double.valueOf(productMap.get("price")+"");
		//logger.info("��Ʒ�۸�:"+price);
		//2���û����
		//3����ͣ�����ʺŽ��
		//�Ǽ��û����²�Ʒ
		logger.info("��Ʒ�۸�:"+price);
		
		Long comid = (Long)productMap.get("comid");
		
		boolean b = false;
		Double total = number*price;
		String time = start.substring(0,4)+"-"+start.substring(4,6)+"-"+start.substring(6);
		Long btime = TimeTools.getLongMilliSecondFrom_HHMMDD(time);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(btime);
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+Integer.valueOf(number));
		Long etime = calendar.getTimeInMillis();
		if(balance>=total){//�����Թ����Ʒ
			List<Map<String , Object>> sqlMaps = new ArrayList<Map<String,Object>>();
			logger.info ("�����Թ����Ʒ...");
			
			Map<String, Object> usersqlMap = new HashMap<String, Object>();
			usersqlMap.put("sql", "update user_info_tb set balance = balance-? where id=? ");
			usersqlMap.put("values", new Object[]{total,uin});
			sqlMaps.add(usersqlMap);
			
			Map<String, Object> userAccountsqlMap = new HashMap<String, Object>();
			userAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type) values(?,?,?,?,?,?)");
			userAccountsqlMap.put("values", new Object[]{uin,total,1,System.currentTimeMillis()/1000,"����-"+productMap.get("p_name"),ptype});
			sqlMaps.add(userAccountsqlMap);
			
			Map<String, Object> comsqlMap = new HashMap<String, Object>();
			comsqlMap.put("sql", "update com_info_tb set money=money+?,total_money=total_money+? where id=? ");
			comsqlMap.put("values", new Object[]{total,total,comid});
			sqlMaps.add(comsqlMap);
			
			Map<String, Object> buysqlMap = new HashMap<String, Object>();
			buysqlMap.put("sql", "insert into carower_product(pid,uin,create_time,b_time,e_time,total) values(?,?,?,?,?,?)");
			buysqlMap.put("values", new Object[]{pid,uin,System.currentTimeMillis()/1000,btime/1000,etime/1000-1,total});
			sqlMaps.add(buysqlMap);
			
			Map<String, Object> ppSqlMap =new HashMap<String, Object>();
			ppSqlMap.put("sql", "update product_package_tb set remain_number=remain_number-? where id=?");
			ppSqlMap.put("values", new Object[]{1,pid});
			sqlMaps.add(ppSqlMap);
			
			b= daService.bathUpdate(sqlMaps);
			logger.info("�����Ʒ�ɹ�...");
		}else 
			return -1;
		//4д�û��۷���־
		//5д������ֵ��־
		if(b){//����ɹ�
			Map comMap = daService.getMap("select company_name from com_info_tb where id = ?", new Object[]{productMap.get("comid")});
			daService.update( "insert into money_record_tb(comid,create_time,amount,uin,type,remark,pay_type) values (?,?,?,?,?,?,?)", 
					new Object[]{productMap.get("comid"),System.currentTimeMillis()/1000,total,uin,
					ZLDType.MONEY_CONSUM,comMap.get("company_name")+" ����-"+productMap.get("p_name"),ptype});
			logger.info("��ֵ��־д��ɹ�...");
			//���Ͷ��� ,��������Ա������;
			
			Map userMap1 = daService.getMap("select mobile from user_info_tb where id=? ",new Object[]{uin});
			Map userMap2 = daService.getMap("select mobile,nickname from user_info_tb where comid=? and auth_flag=? limit ?", new Object[]{productMap.get("comid"),1,1});
			
			String umobile = userMap1.get("mobile")==null?"":userMap1.get("mobile")+"";//(String)daService.getObject("select mobile from user_info_tb where id=? ",new Object[]{uin},String.class);
			String pmobile = userMap2.get("mobile")==null?"":userMap2.get("mobile")+"";//(String)daService.getObject("select mobile from user_info_tb where comid=? and auth_flag=? ", new Object[]{productMap.get("comid"),1},String.class);
			String puserName = userMap2.get("nickname")==null?"":userMap2.get("nickname")+"";
			
			String exprise = "";
			//List userList = daService.getAll("select mobile,nickname,id from user_info_tb where (comid=? or id=?) ", new Object[]{uin});
			
			if(!umobile.equals(""))
				exprise = TimeTools.getTimeStr_yyyy_MM_dd(btime)+"��"+TimeTools.getTimeStr_yyyy_MM_dd(etime);
			String carNumber ="";
			if(!umobile.equals("")||!pmobile.equals(""))
				carNumber = getCarNumber(uin);//(String)daService.getObject("select id,car_number from car_info_tb where uin=? ", new Object[]{uin},String.class);
			//��ʼ������
			if(!umobile.equals("")&&Check.checkMobile(umobile));
//				SendMessage.sendMessage(umobile, "�𾴵�"+carNumber+"�������ã�����ͨ��ͣ��������"+comMap.get("company_name")+"���·��񣬷���"+total+"Ԫ����Ч��"+exprise+
//						"��������ƾ�˶��ŵ�"+comMap.get("company_name")+"��ȡ��Ӧ�¿����ͷ���01053618108 ��ͣ������");
				SendMessage.sendMultiMessage(umobile, "�𾴵�"+carNumber+"�������ã�����ͨ��ͣ��������"+comMap.get("company_name")+"���·��񣬷���"+total+"Ԫ����Ч��"+exprise+
						"��������ƾ�˶��ŵ�"+comMap.get("company_name")+"��ȡ��Ӧ�¿���ȷ���¿�����������ʱ�������ǰ�͸ó���������"+puserName+"(�ֻ���"+pmobile+")��ϵ���������ʿ���ѯͣ�����ͷ���01053618108 ��ͣ������");
				
				
			if(!pmobile.equals("")&&Check.checkMobile(pmobile))
				SendMessage.sendMultiMessage(pmobile,"�𾴵ĺ���������ã�����"+carNumber+"(�ֻ���"+umobile+")��ͨ��ͣ��������󳵳����·���1���£�����"+total+"Ԫ���������ں�̨�鿴��Ӧ��Ϣ��"+
						"������ƾ����ǰ����ȡ�¿�����������ǰ��֮��ϵȷ����Ӧ��Ϣ����������Ӧ�¿���лл���ͷ���01053618108 ��ͣ������");
				
//				SendMessage.sendMessage(pmobile, "�𾴵ĺ���������ã�����"+carNumber+"��ͨ��ͣ��������󳵳����·���1���£�����"+total+"Ԫ���������ں�̨�鿴��Ӧ��Ϣ��"+
//						"������ƾ����ǰ����ȡ�¿����뱸����Ӧ�¿���лл���ͷ���01053618108 ��ͣ������");
			return 1;
		}
		return 0;
	}
	
	
	/**
	 * 
	 * @param orderId �������
	 * @param total �ܼ�
	 * @param uin �����ʺ�
	 * @param type ֧����ʽ��0���,1�ֽ�,2�ֻ�
	 * @param ptype :0��1֧������2΢�ţ�3������4���+֧����,5���+΢��,6���+����
	 * @param ticketId:ͣ��ȯ��� 
	 * @return 0:ʧ�ܡ� 5:�ɹ� 
	 *  -7:�����շѴ���
	 *  -8:�����ѽ��㣬������������
	 *  -9:����������
	 *  -10:ͣ����������
	 *  -12������ 
	 *  -13:ͣ��ȯʹ�ó���3��
	 */
//	public int payOrder(Map orderMap,Double total,Long uin,Integer type,int ptype,Long ticketId,String wxp_orderid){
//		Long count = daService.getLong(
//				"select count(*) from user_info_tb where id=? ",
//				new Object[] { uin });
//		Integer bind_flag = 1;//Ĭ�ϰ����˻� 
//		if(count == 0){
//			bind_flag = 0;//û�а��˻�
//		}
//		Long comid = null;
//		Long uid = null;
//		Integer state = null;
//		String comName = "";
//		if(orderMap!=null){
//			state = (Integer)orderMap.get("state");
//			comid = (Long)orderMap.get("comid");
//			uid = (Long)orderMap.get("uid");
//			//��ѯ��˾��Ϣ
//			Map<String, Object> comMap = daService.getPojo("select * from com_info_tb where id=?", new Object[]{comid});
//			
//			if(comMap == null){
//				logService.insertUserMesg(0, uin, "ͣ�����Ѳ����ڣ�����ϵͣ�����ͷ�", "֧��ʧ������");
//				return -10;
//			}
//			if(comMap != null && comMap.get("company_name") != null){
//				comName = (String)comMap.get("company_name");
//			}
//			if(state==1){//����ɹ����������ش�����Ϣ
//				logger.info("payOrder>>>>>:�����������orderid:"+orderMap.get("id")+",uin:"+uin+",c_type:"+orderMap.get("c_type")+",pay_type:"+orderMap.get("pay_type"));
//				if(type==2||type==0){//�ֻ������֧����ֻ���Ķ�����֧����ʽ
//					total = Double.valueOf(orderMap.get("total")+"");
//					//�����ֽ���ϸ
//					Integer pay_type = (Integer)orderMap.get("pay_type");
//					if(pay_type == 1){
//						int r = daService.update("update parkuser_cash_tb set amount=? where orderid=? and type=? ",
//										new Object[] { 0, orderMap.get("id"), 0 });
//						logger.info("payOrder>>>>>�����ֽ����ģ����ڵ��ӽ��㣬��֮ǰ��ͤ���ֽ���ϸ�����Ϊ0��orderid:"+orderMap.get("id")+",r:"+r);
//					}
//				}else {
//					logService.insertUserMesg(0, uin, comName+"��ͣ����"+total+"Ԫ��������֧���������ظ�֧��", "֧��ʧ������");
//					return -8;
//				}
//			}
//			//duration =StringUtils.getTimeString(start, end);
//		}else {//���������� �����ش�����Ϣ
//			logService.insertUserMesg(0, uin, comName+"��ͣ����"+total+"Ԫ������������", "֧��ʧ������");
//			return -9;
//		}
//		Long orderId = (Long)orderMap.get("id");
//		Integer payType = (Integer)orderMap.get("pay_type");
//		Long ntime = System.currentTimeMillis()/1000;
//		logger.info(">>>>>>>>>>ticket:"+ticketId+">>>>>>>>ԭ����֧����ʽ��"+payType);
//		if(payType!=null&&payType==2){//��֧���������ظ�֧��
//			logger.info("payOrder>>>>�����Ѿ�����֧�����ˣ������ظ�֧����orderid:"+orderId+",uin:"+uin);
//			logService.insertUserMesg(0, uin, "������֧���������ظ�֧��", "֧��ʧ������");
//			return -8;
//		}
//		//�Ż�ȯ���
//		Double ticketMoney = 0d;
//		Integer ticket_type = 7;//7��ͣ��ȯ��11��΢�Ŵ���ȯ
//		String ticket_dp = "ͣ��ȯ��ֵ";
//		if(ticketId != null &&  ticketId == -100){//΢������ȯ
//			ticketMoney = getDisTicketMoney(uin, uid, total);
//			ticket_type = 11;
//			ticket_dp = "΢�Ŵ���ȯ��ֵ";
//			logger.info("orderid:"+orderId+",uin:"+uin+",ticketMoney:"+ticketMoney);
//		}else if(ticketId != null && ticketId > 0){
//			ticketMoney = getTicketMoney(ticketId, 2, uid, total, 2, comid, orderId);
//		}
//		logger.info("orderid:"+orderId+",uin:"+uin+",ticketid:"+ticketId+",ticketMoney:"+ticketMoney);
//		Double ubalance =null;
//	    //�˻����֧��
//		Map<String, Object> userMap = null;
//		if(bind_flag == 1){//�Ѱ��˻�
//			//������ʵ�˻����
//			userMap = daService.getPojo("select balance,wxp_openid from user_info_tb where id =?",	new Object[]{uin});
//		}else{//δ���˻�
//			//�����˻����
//			userMap = daService.getPojo("select balance,openid from wxp_user_tb where uin =?",	new Object[]{uin});
//		}
//		if(userMap!=null&&userMap.get("balance")!=null){
//			ubalance = Double.valueOf(userMap.get("balance")+"");
//			ubalance +=ticketMoney;//�û��������Ż�ȯ���
//		}
//		if(ubalance==null||ubalance<total){//�ʻ�����
//			logger.info("����payOrder>>>orderid:"+orderId+",uin:"+uin+",ticketMoney:"+ticketMoney+",balance:"+userMap.get("balance")+",total:"+total);
//			logService.insertUserMesg(0, uin, comName+"��ͣ����"+total+"Ԫ������("+ubalance+")", "֧��ʧ������");
//			return -12;
//		}
//		String carNumber=null;//�������ƺ�
//		Map carNuberMap = daService.getPojo("select * from car_info_tb where uin=? and state=?", 
//				new Object[]{uin,1});
//		if(carNuberMap!=null&&carNuberMap.get("car_number")!=null)
//			carNumber = (String)carNuberMap.get("car_number");
//		
//		//��ѯ�շ��趨 mtype:0:ͣ����,1:Ԥ����,2:ͣ��������
//		Map msetMap = daService.getPojo("select giveto from money_set_tb where comid=? and mtype=? ",
//				new Object[]{comid,0});
//		Integer giveTo =null;
//		if(msetMap!=null)
//			giveTo =(Integer)msetMap.get("giveto");
//		logger.info(">>>>>>"+msetMap+">>>>>giveto:"+giveTo+"comid:"+comid+",uin:"+uid);
//		
//		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
//		//���¶���״̬���շѳɹ�
//		Map<String, Object> orderSqlMap = new HashMap<String, Object>();
//		//�����û����
//		Map<String, Object> userSqlMap = new HashMap<String, Object>();
//		//����ͣ�������
//	    Map<String, Object> comSqlMap = new HashMap<String, Object>();
//		//������ˮ
//		Map<String, Object> consumptionSqlMap = new HashMap<String, Object>();
//		//�շ�Ա�˻�
//		Map<String, Object> parkuserAccountsqlMap =new HashMap<String, Object>();
//		//�����˻�
//		Map<String, Object> userAccountsqlMap =new HashMap<String, Object>();
//		//�����˻�
//		Map<String, Object> parkAccountsqlMap =new HashMap<String, Object>();
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
//		orderSqlMap.put("values", new Object[]{1,type,etime,total,orderId});
//		bathSql.add(orderSqlMap);
//		
//		//�۳������˻����
//		if(bind_flag == 1){
//			userSqlMap.put("sql", "update user_info_tb  set balance =balance-? where id=?");
//		}else{
//			userSqlMap.put("sql", "update wxp_user_tb  set balance =balance-? where uin=?");
//		}
//		userSqlMap.put("values", new Object[]{total-ticketMoney,uin});
//		if(total-ticketMoney>0)
//			bathSql.add(userSqlMap);
//		
//		
//		consumptionSqlMap.put("sql", "insert into  money_record_tb  (comid,create_time,amount,uin,type,remark,pay_type) values (?,?,?,?,?,?,?)");
//		consumptionSqlMap.put("values", new Object[]{comid,ntime,total,uin,ZLDType.MONEY_CONSUM,"ͣ����-"+comName,ptype});
//		bathSql.add(consumptionSqlMap);
//		
//		if(ticketMoney>0&&ticketId!=null){//ʹ��ͣ��ȯ���������˻��ȳ�ֵ
//			userTicketAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,orderid) values(?,?,?,?,?,?,?)");
//			userTicketAccountsqlMap.put("values", new Object[]{uin,ticketMoney,0,ntime-1,ticket_dp,ticket_type,orderId});
//			bathSql.add(userTicketAccountsqlMap);
//		}
//		
//		userAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,orderid) values(?,?,?,?,?,?,?)");
//		userAccountsqlMap.put("values", new Object[]{uin,total,1,ntime,"ͣ����-"+comName,ptype,orderId});
//		bathSql.add(userAccountsqlMap);
//		
//		//������Ĭ�ϸ������˻�20141120����Ҫ���޸�
//		if(giveTo!=null&&giveTo==0){//д�빫˾�˻�
//			comSqlMap.put("sql", "update com_info_tb  set total_money =total_money+?,money=money+? where id=?");
//			comSqlMap.put("values", new Object[]{total,total,comid});
//			bathSql.add(comSqlMap);
//			
//			parkAccountsqlMap.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source,orderid) values(?,?,?,?,?,?,?,?)");
//			parkAccountsqlMap.put("values",  new Object[]{comid,total,0,ntime,"ͣ����_"+orderMap.get("car_number"),orderMap.get("uid"),0,orderId});
//			bathSql.add(parkAccountsqlMap);
//		}else {//д������˻�
//			parkusersqlMap.put("sql", "update user_info_tb  set balance =balance+? where id=?");
//			parkusersqlMap.put("values", new Object[]{total,orderMap.get("uid")});
//			bathSql.add(parkusersqlMap);
//			
//			parkuserAccountsqlMap.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target,orderid) values(?,?,?,?,?,?,?)");
//			parkuserAccountsqlMap.put("values", new Object[]{orderMap.get("uid"),total,0,ntime,"ͣ����_"+orderMap.get("car_number"),4,orderId});
//			bathSql.add(parkuserAccountsqlMap);
//		}
//		//�Ż�ȯʹ�ú󣬸���ȯ״̬�����ͣ�����˻�֧����¼
//		if(ticketMoney>0&&ticketId!=null&&ticketId>0){
//			ticketsqlMap.put("sql", "update ticket_tb  set state=?,comid=?,utime=?,umoney=?,orderid=?,wxp_orderid=? where id=?");
//			ticketsqlMap.put("values", new Object[]{1,comid,System.currentTimeMillis()/1000,ticketMoney,orderId,wxp_orderid,ticketId});
//			bathSql.add(ticketsqlMap);
//			
//			tingchebaoAccountsqlMap.put("sql", "insert into tingchebao_account_tb(amount,type,create_time,remark,utype,orderid) values(?,?,?,?,?,?)");
//			tingchebaoAccountsqlMap.put("values", new Object[]{ticketMoney,1,ntime,comName+"������"+orderMap.get("car_number")+"��ʹ��ͣ������ȯ",0,orderId});
//			bathSql.add(tingchebaoAccountsqlMap);
////			memcacheUtils.updateUseTicketCache(uin);//��ȯ����ʹ��ȯ����
//		}
//		
//		boolean result= daService.bathUpdate(bathSql);
//		logger.info(">>>>>>>>>>>>>>>֧�� ��"+result);
//		if(result){//����ɹ�������ȯ������ 
//			//�����֣�����������ȯ��������
//			/* ÿ��������΢�Ż�֧����֧��1Ԫ���ϵ���ɵģ���������2Ԫ����������3Ԫ��ͣ��ȯ��
//			 * �������ֲ���(ͬһ����ÿ��ֻ�ܷ�3��)��
//			 * ����ÿ�շ�ȯ��3��ȯ
//			 * ÿ������ÿ��ʹ��ͣ��ȯ������3�� */
//			try {
//				boolean isBlack = isBlackUser(uin);
//				if(!isBlackUser(uin)){
//					if(total>=1){//&&memcacheUtils.readBackMoneyCache(orderMap.get("comid")+"_"+uin)){//���Ը��������� 
//						boolean isCanBackMoney = isCanBackMoney(comid);//�Ƿ��Ǽ��ϳ���
//						Double backmoney = getBackMoney();
//						logger.info("payorder>>>>>:orderid:"+orderId+",backmoney:"+backmoney+",isCanBackMoney:"+isCanBackMoney);
//						if(isCanBackMoney && backmoney > 0){
//							boolean isset = false;
//							Integer giveMoneyTo = null;
//							msetMap = daService.getPojo("select giveto from money_set_tb where comid=? and mtype=? ",
//									new Object[]{comid,2});
//							if(msetMap!=null)
//								giveMoneyTo =(Integer)msetMap.get("giveto");
//							if(giveMoneyTo!=null&&giveMoneyTo==0){//���ָ������˻�
//								List<Map<String, Object>> insertSqlList = new ArrayList<Map<String,Object>>();
//								Map<String, Object> comInfoSql = new HashMap<String, Object>();
//								Map<String, Object> parkAccountSql = new HashMap<String, Object>();
//								comInfoSql.put("sql", "update com_info_tb set money=money+?, total_money=total_money+? where id=?");
//								comInfoSql.put("values",new Object[]{backmoney,backmoney,comid});
//								parkAccountSql.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source,orderid) values(?,?,?,?,?,?,?,?)");
//								parkAccountSql.put("values",new Object[]{orderMap.get("comid"),backmoney,2,ntime,"ͣ��������",uid,1,orderId});
//								insertSqlList.add(comInfoSql);
//								insertSqlList.add(parkAccountSql);
//								isset = daService.bathUpdate(insertSqlList);
//							}else {//���ָ��շ�Ա�˻�
//								List<Map<String, Object>> insertSqlList = new ArrayList<Map<String,Object>>();
//								Map<String, Object> userInfoSql = new HashMap<String, Object>();
//								Map<String, Object> parkAccountSql = new HashMap<String, Object>();
//								userInfoSql.put("sql", "update user_info_tb set balance=balance+? where id=?");
//								userInfoSql.put("values",new Object[]{backmoney,uid});
//								parkAccountSql.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target,orderid) values(?,?,?,?,?,?,?)");
//								parkAccountSql.put("values",new Object[]{uid,backmoney,0,ntime,"ͣ��������",3,orderId});
//								insertSqlList.add(userInfoSql);
//								insertSqlList.add(parkAccountSql);
//								isset = daService.bathUpdate(insertSqlList);
//							}
//							logger.info(">>>>>>>>>>>>ͣ�������ָ�"+giveMoneyTo+",�����"+isset+",���»��� ");
//							if(isset){
////								memcacheUtils.updateBackMoneyCache(orderMap.get("comid")+"_"+uin);
//							}
//						}
//					}else {
//						logger.info(">>>>>total:"+total+">>>>���ֳ���1��..."+orderMap.get("comid")+"_"+uin);
//					}
//				}else {
//					logger.info(">>>>>black>>>>������"+uin+",�ں������ڣ������������� ......");
//				}
//			
//				try {
//					//����5��ȯ����ͣ��ȯ������ ������һ����¼���������
//					if(ticket_type==11&&ticketId<0&&ticketMoney>0){
//						int ret = daService.update( "insert into ticket_tb (create_time,limit_day,money,state,uin,comid,type,orderid,utime,umoney,wxp_orderid)" +
//								" values(?,?,?,?,?,?,?,?,?,?,?) ",
//								new Object[]{TimeTools.getToDayBeginTime(),TimeTools.getToDayBeginTime()+24*60*60-1,5,1,uin,comid,2,orderId,ntime,ticketMoney,wxp_orderid});
////						memcacheUtils.updateUseTicketCache(uin);//��ȯ����ʹ��ȯ����
//						logger.info(">>>pay order ,û������ȯʱ��дһ����¼�����:"+ret);
//					}
//				} catch (Exception e) {
//					logger.info(">>>pay order error,д������ȯ����"+e.getMessage());
//				}
//				
//				if(ticketMoney > 0){
////					updateAllowCache(comid, ticketId, ticketMoney);
//					logger.info("update allowance cache>>>uin:"+uin+",ticketMoney:"+ticketMoney);
//				}
//				
//				if(!isBlack){
//					backTicket(total-ticketMoney, orderId, uin,comid,wxp_orderid);
//				}else {
//					logger.info(">>>>>black>>>>������"+uin+",�ں������ڣ�������� ......");
//				}
//				if(total>=1){
//					handleRecommendCode(uin,isBlack);
//				}
//				//дϵͳ��־ 
//				String time = TimeTools.gettime();
//				if(state!=null&&state==0){
//					logService.updateOrderLog(comid,uin,time+",�ʺţ�"+uin+",���ƣ�"+carNumber+",ͣ���շѣ�"+total+",ͣ������"+comName,1);
//				}
//			} catch (Exception e) {
//				logger.info(">>>>>>>>>>ͣ�������֡�ͣ������ȯʧ�ܣ�...............");
//				e.printStackTrace();
//			}
//			
//			try {//ֱ����ɺ�����Ϣ
//				String openid = "";
//				String url = "";
//				if(bind_flag == 1){
//					openid = (String)userMap.get("wxp_openid");
//					url = "http://"+Constants.WXPUBLIC_REDIRECTURL+"/zld/wxpaccount.do?action=toaccountdetail&openid="+openid;
//				} else {
//					openid = (String)userMap.get("openid");
//					url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+Constants.WXPUBLIC_APPID+"&redirect_uri=http%3a%2f%2f"+Constants.WXPUBLIC_REDIRECTURL+"%2fzld%2fwxpaccount.do&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
//				}
//				if(openid!=null&&openid.length()>10 && (ptype == 10 || ptype == 9)){
//					logger.info(">>>����֧���ɹ���ͨ��΢�ŷ���Ϣ������...orderpaymsg:openid:"+openid+",uin��"+uin);
//					Map<String, String> baseinfo = new HashMap<String, String>();
//					List<Map<String, String>> orderinfo = new ArrayList<Map<String,String>>();
//					String remark = "�������鿴�˻���ϸ��";
//					String remark_color = "#000000";
//					Map bMap  =daService.getMap("select * from order_ticket_tb where uin=? and  order_id=? and ctime>? order by ctime desc limit ?",
//							new Object[]{uin,orderId,System.currentTimeMillis()/1000-5*60, 1});//�����ǰ�ĺ��
//					
//					if(bMap!=null&&bMap.get("id")!=null){
//						Integer bonus_type = 0;//0:��ͨ���������1��΢���ۿۺ��
//						if(bMap.get("type")!= null && (Integer)bMap.get("type") == 1){
//							bonus_type = 1;//΢�Ŵ��ۺ��
//						}
//						if(bonus_type == 1){
//							remark = "��ϲ�����"+bMap.get("bnum")+"��΢��"+bMap.get("money")+"��ȯ������������ɣ�";
//						}else{
//							remark = "��ϲ�����"+bMap.get("bnum")+"����"+bMap.get("money")+"Ԫͣ��ȯ������������ɣ�";
//						}
//						remark_color = "#FF0000";
//						Integer first_flag = 0;
//						Long first = daService.getLong("select count(*) from user_account_tb where uin=? and type=? ", new Object[]{uin, 1});
//						logger.info("�Ƿ����׵�֧��>>>>orderid:"+orderId+",uin:"+uin+",openid:"+openid+",first:"+first+",time:"+System.currentTimeMillis()/1000);
//						if(first == 1){
//							first_flag = 1;
//						}
//						url = "http://"+Constants.WXPUBLIC_REDIRECTURL+"/zld/wxpublic.do?action=balancepayinfo&openid="+openid+"&money="+total+"&bonusid="+bMap.get("id")+"&bonus_type="+bonus_type+"&orderid="+orderId+"&paytype=1"+"&first_flag="+first_flag;
//					}
//					Map uidMap = daService.getMap("select nickname from user_info_tb where id=? ", new Object[]{uid});
//					String first = "����"+comName+"���շ�Ա"+uidMap.get("nickname")+"���ѳɹ���";
//					baseinfo.put("url", url);
//					baseinfo.put("openid", openid);
//					baseinfo.put("top_color", "#000000");
//					baseinfo.put("templeteid", Constants.WXPUBLIC_SUCCESS_NOTIFYMSG_ID);
//					Map<String, String> keyword1 = new HashMap<String, String>();
//					keyword1.put("keyword", "orderMoneySum");
//					keyword1.put("value", total+"Ԫ");
//					keyword1.put("color", "#000000");
//					orderinfo.add(keyword1);
//					Map<String, String> keyword2 = new HashMap<String, String>();
//					keyword2.put("keyword", "orderProductName");
//					keyword2.put("value", "ͣ����");
//					keyword2.put("color", "#000000");
//					orderinfo.add(keyword2);
//					Map<String, String> keyword3 = new HashMap<String, String>();
//					keyword3.put("keyword", "Remark");
//					keyword3.put("value", remark);
//					keyword3.put("color", remark_color);
//					orderinfo.add(keyword3);
//					Map<String, String> keyword4 = new HashMap<String, String>();
//					keyword4.put("keyword", "first");
//					keyword4.put("value", first);
//					keyword4.put("color", "#000000");
//					orderinfo.add(keyword4);
//					sendWXTempleteMsg(baseinfo, orderinfo);
//					
//					sendBounsMessage(openid,uid,2d,orderId ,uin);//��������Ϣ
//				}
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//			//���»���
//			
//			//д��Ϣ��
////			logService.doMessage(comid,type,uin,carNumber,orderId,total,duration,0,
////					(Long)orderMap.get("create_time"),ntime);
//			//���¶�����Ϣ�е�״̬ 
////			daService.update("update order_message_tb set state=? where orderid=?", 
////					new Object[]{1,orderId});
//			return 5;
//		}else {
//			return -7;
//		}
//	}
	
	/**
	 * ����Ԥ֧��
	 * @param orderid �������
	 * @param total Ԥ֧�����
	 * @param uin �����˺�
	 * @param ticketId ͣ��ȯ��
	 * @param bind_flag 0��δ���˻� 1�������˻�
	 * @param ptype ֧������  0��1֧������2΢�ţ�3������4���+֧����,5���+΢��,6���+���� ,7ͣ������ֵ,8�����,9΢�Ź��ں�,10���+΢�Ź��ں�
	 */
	
//	public int prepay(Map orderMap, Double total, Long uin, Long ticketId, Integer ptype, Integer bind_flag, String wxp_orderid){
//		logger.info(">>>>>>>>>>>>>>>>>����Ԥ֧����orderid"+orderMap.get("id")+",uin:"+uin+",�Ѿ�Ԥ֧���Ľ��:"+orderMap.get("total"));
//		DecimalFormat dFormat = new DecimalFormat("#.00");
//		Long comid = null;
//		Integer state = null;//(Integer)orderMap.get("state");
//		String comName = "";
//		Long uid =null;
//		if(orderMap!=null){
//			state = (Integer)orderMap.get("state");
//			comid = (Long)orderMap.get("comid");
//			uid  = (Long)orderMap.get("uid");
//			//��ѯ��˾��Ϣ
//			Map comMap = daService.getPojo("select * from com_info_tb where id=?", new Object[]{comid});
//			if(comMap!=null){
//				comName=(String)comMap.get("company_name");
//			}else {//���������� �����ش�����Ϣ
//				logger.info(">>>>>>>>>>>>>>ͣ�����Ѳ����ڣ�֧��ʧ��>>>>>>>>>>>>");
//				logService.insertUserMesg(0, uin, "ͣ�����Ѳ����ڣ�����ϵͣ�����ͷ�", "֧��ʧ������");
//				return -10;
//			}
//			
//			if(state==1){//����ɹ����������ش�����Ϣ
//				logger.info(">>>>>>>>>>>>>�ö�����֧���������ظ�֧����֧��ʧ�ܣ�orderid:"+orderMap.get("id"));
//				
//				try {//ֱ����ɺ�����Ϣ
//					String openid = "";
//					String url = "";
//					Map userMap = daService.getMap("select wxp_openid from user_info_Tb where id = ? ", new Object[]{uin});
//					if(userMap!=null){
//						logger.info(">>>>>>>>>�Ѿ����˻���uin��"+uin);
//						openid = (String)userMap.get("wxp_openid");
//						url = "http://"+Constants.WXPUBLIC_REDIRECTURL+"/zld/wxpaccount.do?action=toaccountdetail&openid="+openid;
//					} else {
//						logger.info(">>>>>>>>>>>δ���˻���uin��"+uin);
//						userMap = daService.getMap("select openid from wxp_user_Tb where uin= ? ", new Object[]{uin});
//						if(userMap!=null){
//							openid = (String)userMap.get("openid");
//							url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+Constants.WXPUBLIC_APPID+"&redirect_uri=http%3a%2f%2f"+Constants.WXPUBLIC_REDIRECTURL+"%2fzld%2fwxpaccount.do&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
//						}	
//					}
//					
//					if(openid!=null&&openid.length()>10 && (ptype == 10 || ptype == 9)){
//						logger.info(">>>Ԥ֧��ʧ�ܣ���Ϊ�����ѽ��㣬ͨ��΢�ŷ���Ϣ������...directpaymsg:openid:"+openid+",uin��"+uin+",orderid:"+orderMap.get("id")+",Ԥ֧�����total:"+total);
//						
//						Map uidMap = daService.getMap("select nickname from user_info_tb where id=? ", new Object[]{orderMap.get("uid")});
//						String first = "����"+comMap.get("company_name")+"���շ�Ա"+uidMap.get("nickname")+"����ʧ�ܣ�";
//						Map<String, String> baseinfo = new HashMap<String, String>();
//						List<Map<String, String>> orderinfo = new ArrayList<Map<String,String>>();
//						baseinfo.put("url", url);
//						baseinfo.put("openid", openid);
//						baseinfo.put("top_color", "#000000");
//						baseinfo.put("templeteid", Constants.WXPUBLIC_FAIL_NOTIFYMSG_ID);
//						Map<String, String> keyword1 = new HashMap<String, String>();
//						keyword1.put("keyword", "keyword1");
//						keyword1.put("value", total+"Ԫ");
//						keyword1.put("color", "#000000");
//						orderinfo.add(keyword1);
//						Map<String, String> keyword2 = new HashMap<String, String>();
//						keyword2.put("keyword", "keyword2");
//						keyword2.put("value", "ͣ����");
//						keyword2.put("color", "#000000");
//						orderinfo.add(keyword2);
//						Map<String, String> keyword3 = new HashMap<String, String>();
//						keyword3.put("keyword", "keyword3");
//						keyword3.put("value", "�ö���Ԥ֧��ǰ�ѽ��㣬֧����ͣ����"+total+"Ԫ�ѷ���������ͣ�����˻���");
//						keyword3.put("color", "#FF0000");
//						orderinfo.add(keyword3);
//						Map<String, String> keyword4 = new HashMap<String, String>();
//						keyword4.put("keyword", "remark");
//						keyword4.put("value", "���������˻���ϸ��");
//						keyword4.put("color", "#000000");
//						orderinfo.add(keyword4);
//						Map<String, String> keyword5 = new HashMap<String, String>();
//						keyword5.put("keyword", "first");
//						keyword5.put("value", first);
//						keyword5.put("color", "#000000");
//						orderinfo.add(keyword5);
//						sendWXTempleteMsg(baseinfo, orderinfo);
//					}
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//				
//				logService.insertUserMesg(0, uin, comName+"��ͣ����"+total+"Ԫ��������֧���������ظ�֧��", "֧��ʧ������");
//				return -8;
//			}
//			//duration =StringUtils.getTimeString(start, end);
//		}else {//���������� �����ش�����Ϣ
//			logger.info(">>>>>>>>>>>>>>����������>>>>>>>>>>>>>>");
//			logService.insertUserMesg(0, uin, comName+"��ͣ����"+total+"Ԫ������������", "֧��ʧ������");
//			return -9;
//		}
//		
//		Long orderId = (Long)orderMap.get("id");
//		Integer payType = (Integer)orderMap.get("pay_type");
//		Long ntime = System.currentTimeMillis()/1000;
//		logger.info(">>>>>>>>>>ticket:"+ticketId+">>>>>>>>ԭ����֧����ʽ��"+payType);
//		if(payType!=null&&payType==2){//��֧���������ظ�֧��
//			logger.info(">>>>>>>>>>>��֧�������ظ�֧����payType:"+payType);
//			logService.insertUserMesg(0, uin, "������֧���������ظ�֧��", "֧��ʧ������");
//			return -8;
//		}//�Ż�ȯ���
//		Double ticketMoney = 0d;
//		Integer ticket_type = 7;//7��ͣ��ȯ��11��΢�Ŵ���ȯ
//		String ticket_dp = "ͣ��ȯ��ֵ";
//		if(ticketId!= null && ticketId == -100){//΢������ȯ
//			ticketMoney = getDisTicketMoney(uin, uid, total);
//			ticket_type = 11;
//			ticket_dp = "΢�Ŵ���ȯ��ֵ";
//			logger.info("orderid:"+orderId+",uin:"+uin+",ticketMoney:"+ticketMoney);
//		}else if(ticketId!=null&&ticketId>0){
//			ticketMoney = getTicketMoney(ticketId, 2, uid, total, 2, comid, orderId);
//		}
//		ticketMoney = Double.valueOf(dFormat.format(ticketMoney));
//		logger.info("orderid:"+orderId+",uin:"+uin+",ticketid:"+ticketId+",ticketMoney:"+ticketMoney);
//		Double ubalance =null;
//		Map userMap = null;
//		if(bind_flag == 0){
//			//�˻����֧��
//			userMap = daService.getPojo("select balance from wxp_user_tb where uin =?", new Object[]{uin});
//		}else{
//			//�˻����֧��
//			userMap = daService.getPojo("select balance from user_info_tb where id =?", new Object[]{uin});
//		}
//	    
//		if(userMap!=null&&userMap.get("balance")!=null){
//			ubalance = Double.valueOf(userMap.get("balance")+"");
//			ubalance +=ticketMoney;//�û��������Ż�ȯ���
//		}
//		if(ubalance==null||ubalance<total){//�ʻ�����
//			logger.info("Ԥ֧��ͣ��������,uin:"+uin+",�û����:"+userMap.get("balance")+",ͣ���ѽ��:"+total+",orderid:"+orderId+",ticketMoney:"+ticketMoney);
//			logService.insertUserMesg(0, uin, comName+"��ͣ����"+total+"Ԫ������("+ubalance+")", "֧��ʧ������");
//			return -12;
//		}
//		
//		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
//		//���¶���״̬���շѳɹ�
//		Map<String, Object> orderSqlMap = new HashMap<String, Object>();
//		//�����û����
//		Map<String, Object> userSqlMap = new HashMap<String, Object>();
//		//������ˮ
//		Map<String, Object> consumptionSqlMap = new HashMap<String, Object>();
//		//�����˻�
//		Map<String, Object> userAccountsqlMap =new HashMap<String, Object>();
//		//�����˻���ͣ��ȯ
//		Map<String, Object> userTicketAccountsqlMap =new HashMap<String, Object>();
//		//ʹ��ͣ��ȯ����
//		Map<String, Object> ticketsqlMap =new HashMap<String, Object>();
//		//ͣ�����˻���ͣ��ȯ���
//		Map<String, Object> tingchebaoAccountsqlMap =new HashMap<String, Object>();
//		//Ԥ֧��״̬
//		Map<String, Object> prestatesqlMap = new HashMap<String, Object>();
//		
//		Double ntotal = 0d;
//		if(orderMap.get("total") != null){
//			ntotal = Double.valueOf(orderMap.get("total") + "");
//		}
//		ntotal += total;//Ԥ֧���ܽ��
//		
//		orderSqlMap.put("sql", "update order_tb set total=?,uin=? where id=?");
//		orderSqlMap.put("values", new Object[]{ntotal,uin,orderId});
//		bathSql.add(orderSqlMap);
//		if(bind_flag == 0){//δ���˻������
//			userSqlMap.put("sql", "update wxp_user_tb  set balance =balance-? where uin=?");
//		}else{//��ʵ�ʻ�
//			userSqlMap.put("sql", "update user_info_tb  set balance =balance-? where id=?");
//		}
//		userSqlMap.put("values", new Object[]{total-ticketMoney,uin});
//		if(total-ticketMoney>0)
//			bathSql.add(userSqlMap);
//		
//		consumptionSqlMap.put("sql", "insert into  money_record_tb  (comid,create_time,amount,uin,type,remark,pay_type) values (?,?,?,?,?,?,?)");
//		consumptionSqlMap.put("values", new Object[]{comid,ntime,total,uin,ZLDType.MONEY_CONSUM,"Ԥ֧��ͣ����-"+comName,ptype});
//		bathSql.add(consumptionSqlMap);
//		
//		if(ticketMoney>0&&ticketId!=null){//ʹ��ͣ��ȯ���������˻��ȳ�ֵ
//			userTicketAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,orderid) values(?,?,?,?,?,?,?)");
//			userTicketAccountsqlMap.put("values", new Object[]{uin,ticketMoney,0,ntime-1,ticket_dp,ticket_type,orderId});
//			bathSql.add(userTicketAccountsqlMap);
//		}
//		
//		userAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,uid,orderid) values(?,?,?,?,?,?,?,?)");
//		userAccountsqlMap.put("values", new Object[]{uin,total,1,ntime,"Ԥ֧��ͣ����-"+comName,ptype,orderMap.get("uid"),orderId});
//		bathSql.add(userAccountsqlMap);
//		
//		//�Ż�ȯʹ�ú󣬸���ȯ״̬�����ͣ�����˻�֧����¼
//		if(ticketMoney>0&&ticketId!=null&&ticketId>0){
//			ticketsqlMap.put("sql", "update ticket_tb  set state=?,comid=?,utime=?,umoney=?,orderid=?,wxp_orderid=? where id=?");
//			ticketsqlMap.put("values", new Object[]{1,comid,System.currentTimeMillis()/1000,ticketMoney,orderId,wxp_orderid,ticketId});
//			bathSql.add(ticketsqlMap);
//			
//			tingchebaoAccountsqlMap.put("sql", "insert into tingchebao_account_tb(amount,type,create_time,remark,utype,orderid) values(?,?,?,?,?,?)");
//			tingchebaoAccountsqlMap.put("values", new Object[]{ticketMoney,1,ntime,comName+"������"+orderMap.get("car_number")+"��ʹ��ͣ������ȯ",0,orderId});
//			bathSql.add(tingchebaoAccountsqlMap);
////			memcacheUtils.updateUseTicketCache(uin);//��ȯ����ʹ��ȯ����
//		}
//		
//		prestatesqlMap.put("sql", "update order_tb set pre_state=? where id=? ");
//		prestatesqlMap.put("values", new Object[]{0,orderId});
//		bathSql.add(prestatesqlMap);
//		
//		boolean result= daService.bathUpdate(bathSql);
//		logger.info("Ԥ֧�� �����"+result+",orderid:"+orderId+",uin:"+uin);
//		if(!result){
//			return -7;
//		}else {
//			//����5��ȯ����ͣ��ȯ������ ������һ����¼���������
//			try {
//				int ret =0;
//				if(ticketId==-101&&ticketMoney>0){
//					ret = daService.update("update ticket_tb set umoney = umoney+?,wxp_orderid=? where type=? and orderid=? ", new Object[]{ticketMoney,wxp_orderid,2,orderId});
//					logger.info(">>>prepay order ,��������ȯʹ�ý�ticketmoney="+ticketMoney+",orderid="+orderId+",���:"+ret);
//				}else if(ticketId==-100&&ticketMoney>0){
//					ret = daService.update( "insert into ticket_tb (create_time,limit_day,money,state,uin,comid,type,orderid,utime,umoney,wxp_orderid)" +
//							" values(?,?,?,?,?,?,?,?,?,?,?) ",
//							new Object[]{TimeTools.getToDayBeginTime(),TimeTools.getToDayBeginTime()+24*60*60-1,5,1,uin,comid,2,orderId,ntime,ticketMoney,wxp_orderid});
////					memcacheUtils.updateUseTicketCache(uin);//��ȯ����ʹ��ȯ����
//					logger.info(">>>prepay order ,û������ȯʱ��дһ����¼�����:"+ret);
//				}
//			} catch (Exception e) {
//				logger.info(">>>prepay дͣ��ȯ���� ��error:"+e.getMessage());
//			}
//			
//			if(ticketMoney > 0){
////				updateAllowCache(comid, ticketId, ticketMoney);
//				logger.info("update allowance cache>>>uin:"+uin+",ticketMoney:"+ticketMoney);
//			}
//		}
//		return 1;
//	}
	
	
	/**
	 * ����Ԥ���Ѷ���
	 * @param orderMap ����
	 * @param total ʵ�ս��
	 * @return 0ʧ�� 1�ɹ�
	 */
//	public Integer doPrePayOrder(Map orderMap,Double total){
//		logger.info(">>>>>>>>>>>����Ԥ���Ѷ�����orderid:"+orderMap.get("id")+",Ԥ֧����"+orderMap.get("total")+",uin:"+orderMap.get("uin")+",ͣ���ѽ�"+total);
//		Long comid = null;
//		Integer state = null;
//		String comName = "";
//		
//		comid = (Long)orderMap.get("comid");
//		Long uin = (Long)orderMap.get("uin");
//		Long uid = (Long)orderMap.get("uid");
//		Long orderId = (Long)orderMap.get("id");
//		String carNumber = (String)orderMap.get("car_number");
//		if(carNumber==null||"".equals(carNumber))
//			carNumber = uin+"";
//		Double prefee = StringUtils.formatDouble(orderMap.get("total"));//Ԥ���ѽ��
//		Long ntime = System.currentTimeMillis()/1000;
//		
//		//��ѯ�շ��趨 mtype:0:ͣ����,1:Ԥ����,2:ͣ��������
//		Map msetMap = daService.getPojo("select giveto from money_set_tb where comid=? and mtype=? ",
//				new Object[]{comid,0});
//		Integer giveTo =null;
//		if(msetMap!=null)
//			giveTo =(Integer)msetMap.get("giveto");
//		logger.info(">>>>>>"+msetMap+">>>>>giveto:"+giveTo+"comid:"+comid+",uin:"+uid);
//		
//		//��ѯ��˾��Ϣ
//		Map<String, Object> comMap = daService.getPojo("select * from com_info_tb where id=?", new Object[]{comid});
//		if(comMap != null && comMap.get("company_name") != null){
//			comName = (String)comMap.get("company_name");
//		}
//		
//		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
//		//���¶���״̬���շѳɹ�
//		Map<String, Object> orderSqlMap = new HashMap<String, Object>();
//		//����ͣ�������
//	    Map<String, Object> comSqlMap = new HashMap<String, Object>();
//		//������ˮ
//		Map<String, Object> consumptionSqlMap = new HashMap<String, Object>();
//		//�շ�Ա�˻�
//		Map<String, Object> parkuserAccountsqlMap =new HashMap<String, Object>();
//		//�����˻�
//		Map<String, Object> parkAccountsqlMap =new HashMap<String, Object>();
//		//�����˻�1
//		Map<String, Object> cashsqlMap =new HashMap<String, Object>();
//		//�շ�Ա���
//		Map<String, Object> parkusersqlMap =new HashMap<String, Object>();
//		
//		Long etime = System.currentTimeMillis()/1000;
////		if(start!=null&&start==etime)
////			etime = etime+60;
//		orderSqlMap.put("sql", "update order_tb set state =?,pay_type=?, end_time=?,total=? where id=?");
//		orderSqlMap.put("values", new Object[]{1,2,etime,total,orderId});
//		bathSql.add(orderSqlMap);
//		
//		consumptionSqlMap.put("sql", "insert into  money_record_tb  (comid,create_time,amount,uin,type,remark,pay_type) values (?,?,?,?,?,?,?)");
//		consumptionSqlMap.put("values", new Object[]{comid,ntime,total,uin,ZLDType.MONEY_CONSUM,"ͣ����-"+comName,2});
//		bathSql.add(consumptionSqlMap);
//		
//		//������Ĭ�ϸ������˻�20141120����Ҫ���޸�
////		if(giveTo!=null&&giveTo==0){//д�빫˾�˻�
////			if(prefee<total){//֧������ʱ����һ���ֽ���ȡ
////				parkAccountsqlMap.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source,orderid) values(?,?,?,?,?,?,?,?)");
////				parkAccountsqlMap.put("values",  new Object[]{comid,prefee,0,ntime,"ͣ����_"+carNumber,orderMap.get("uid"),0,orderId});
////				bathSql.add(parkAccountsqlMap);
////				
////				comSqlMap.put("sql", "update com_info_tb  set total_money =total_money+?,money=money+? where id=?");
////				comSqlMap.put("values", new Object[]{prefee,prefee,comid});
////				bathSql.add(comSqlMap);
////				
////				cashsqlMap.put("sql", "insert into parkuser_cash_tb(uin,amount,type,orderid,create_time) values(?,?,?,?,?)");
////				cashsqlMap.put("values",  new Object[]{uid,(total-prefee),0,orderId,ntime});
////				bathSql.add(cashsqlMap);
////				
////			}else {
////				parkAccountsqlMap.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source,orderid) values(?,?,?,?,?,?,?,?)");
////				parkAccountsqlMap.put("values",  new Object[]{comid,total,0,ntime,"ͣ����_"+carNumber,orderMap.get("uid"),0,orderId});
////				bathSql.add(parkAccountsqlMap);
////				
////				comSqlMap.put("sql", "update com_info_tb  set total_money =total_money+?,money=money+? where id=?");
////				comSqlMap.put("values", new Object[]{total,total,comid});
////				bathSql.add(comSqlMap);
////			}
////			
////		}else {//д������˻�
//			
//			if(prefee<total){//֧������ʱ����һ���ֽ���ȡ
//				parkuserAccountsqlMap.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target,orderid) values(?,?,?,?,?,?,?)");
//				parkuserAccountsqlMap.put("values", new Object[]{orderMap.get("uid"),prefee,0,ntime,"ͣ����_"+carNumber,4,orderId});
//				bathSql.add(parkuserAccountsqlMap);
//				
//				parkusersqlMap.put("sql", "update user_info_tb  set balance =balance+? where id=?");
//				parkusersqlMap.put("values", new Object[]{prefee,orderMap.get("uid")});
//				bathSql.add(parkusersqlMap);
//				
//				cashsqlMap.put("sql", "insert into parkuser_cash_tb(uin,amount,type,orderid,create_time) values(?,?,?,?,?)");
//				cashsqlMap.put("values", new Object[]{uid,(total-prefee),0,orderId,ntime});
//				bathSql.add(cashsqlMap);
//				
//			}else {
//				parkuserAccountsqlMap.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target,orderid) values(?,?,?,?,?,?,?)");
//				parkuserAccountsqlMap.put("values", new Object[]{orderMap.get("uid"),total,0,ntime,"ͣ����_"+carNumber,4,orderId});
//				bathSql.add(parkuserAccountsqlMap);
//				
//				parkusersqlMap.put("sql", "update user_info_tb  set balance =balance+? where id=?");
//				parkusersqlMap.put("values", new Object[]{total,orderMap.get("uid")});
//				bathSql.add(parkusersqlMap);
//			}
//			
////		}
//		boolean result= daService.bathUpdate(bathSql);
//		logger.info("Ԥ֧�������� ��"+result+",orderid:"+orderId+",uin:"+uin);
//		if(result){//����ɹ�������ȯ������ 
//			Double back = 0d;
//			Double tcbback = 0d;
//			if(prefee>total){//�������˻س���΢��Ǯ��
//				logger.info("Ԥ֧��������ͣ���ѽ��,Ԥ֧����"+prefee+",ͣ���ѽ�"+total+",orderid:"+orderId+",uin:"+uin);
//				List<Map<String, Object>> backSqlList = new ArrayList<Map<String,Object>>();
//				DecimalFormat dFormat = new DecimalFormat("#.00");
//				//����ù�����ȯ����һֱ������ȯ
//				Map<String, Object> ticketMap = daService.getMap(
//						"select * from ticket_tb where orderid=? order by utime limit ?",
//						new Object[] { orderId,1});
//				if(ticketMap != null){
//					Long ticketId = (Long)ticketMap.get("id");
//					logger.info("ʹ�ù�ȯ��ticketid:"+ticketId+",orderid="+orderId+",uin:"+uin);
//					Double umoney = Double.valueOf(ticketMap.get("umoney")+"");
//					umoney = Double.valueOf(dFormat.format(umoney));
//					Double preupay = Double.valueOf(dFormat.format(prefee - umoney));
//					logger.info("Ԥ֧�����prefee��"+prefee+",ʹ��ȯ�Ľ��umoney��"+umoney+",����ʵ��֧���Ľ�"+preupay+",orderid:"+orderId);
//					Double tmoney = 0d;
//					Integer type = (Integer)ticketMap.get("type");
//					if(type == 0 || type == 1){//����ȯ
//						tmoney = getTicketMoney(ticketId, 2, uid, total, 2, comid, orderId);
//						logger.info("orderid:"+orderId+",uin:"+uin+",tmoney:"+tmoney);
//					}else if(type == 2){
//						tmoney = getDisTicketMoney(uin, uid, total);
//						logger.info("orderid:"+orderId+",uin:"+uin);
//					}
//					Double upay = Double.valueOf(dFormat.format(total - tmoney));
//					logger.info("ʵ��ͣ����total:"+total+",ʵ��ͣ����Ӧ�ô��۵Ľ��tmoney:"+tmoney+",ʵ��ͣ���ѳ���ʵ��Ӧ��֧���Ľ��upay��"+upay+",orderid:"+orderId);
//					if(preupay > upay){
//						back = Double.valueOf(dFormat.format(preupay - upay));
//						logger.info("preupay:"+preupay+",upay:"+upay+",orderid:"+orderId+",uin:"+uin);
//					}
//					if(umoney > tmoney){
//						tcbback = Double.valueOf(dFormat.format(umoney - tmoney));
//					}
//					int r = daService.update("update ticket_tb set bmoney = ? where id=? ", new Object[]{tmoney, ticketMap.get("id")});
//				}else{
//					logger.info("û��ʹ�ù�ȯorderid:"+orderId+",uin:"+uin);
//					back = Double.valueOf(dFormat.format(prefee - total));
//				}
//				logger.info("Ԥ֧���˻����:"+back+",ͣ��ȯ�����tcbback:"+tcbback);
//				if(back > 0){
//					Long count = daService.getLong("select count(*) from user_info_tb where id=? ", new Object[]{uin});
//					Map<String, Object> usersqlMap = new HashMap<String, Object>();
//					if(count > 0){//��ʵ�ʻ�
//						usersqlMap.put("sql", "update user_info_tb set balance=balance+? where id=? ");
//						usersqlMap.put("values", new Object[]{back,uin});
//						backSqlList.add(usersqlMap);
//					}else{//�����˻�
//						usersqlMap.put("sql", "update wxp_user_tb set balance=balance+? where uin=? ");
//						usersqlMap.put("values", new Object[]{back,uin});
//						backSqlList.add(usersqlMap);
//					}
//					Map<String, Object> userAccountsqlMap = new HashMap<String, Object>();
//					userAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,orderid) values(?,?,?,?,?,?,?)");
//					userAccountsqlMap.put("values", new Object[]{uin,back,0,System.currentTimeMillis() / 1000 - 2,"Ԥ֧������", 12, orderId });
//					backSqlList.add(userAccountsqlMap);
//					if(tcbback > 0){
//						Map<String, Object> tcbbacksqlMap = new HashMap<String, Object>();
//						tcbbacksqlMap.put("sql", "insert into tingchebao_account_tb(amount,type,create_time,remark,utype,orderid) values(?,?,?,?,?,?)");
//						tcbbacksqlMap.put("values", new Object[]{tcbback,0,ntime,"ͣ��ȯ������",6,orderId});
//						backSqlList.add(tcbbacksqlMap);
//					}
//					
//					boolean b = daService.bathUpdate(backSqlList);
//					logger.info("Ԥ֧����������"+b+",orderid:"+orderId+",uin:"+uin);
//				}else{
//					logger.info("�˻����backС��0��orderid��"+orderId+",uin:"+uin);
//				}
//			}
//			try {
//				boolean isBlack = isBlackUser(uin);
//				if(!isBlackUser(uin)){
//					if(total>=1){//&&memcacheUtils.readBackMoneyCache(orderMap.get("comid")+"_"+uin)){//���Ը��������� 
//						boolean isCanBackMoney = isCanBackMoney(comid);//�Ƿ��Ǽ��ϳ���
//						Double backmoney = getBackMoney();
//						logger.info("payorder>>>>>:orderid:"+orderId+",backmoney:"+backmoney+",isCanBackMoney:"+isCanBackMoney);
//						if(isCanBackMoney && backmoney > 0){
//							boolean isset = false;
//							Integer giveMoneyTo = null;
//							msetMap = daService.getPojo("select giveto from money_set_tb where comid=? and mtype=? ",
//									new Object[]{comid,2});
//							if(msetMap!=null)
//								giveMoneyTo =(Integer)msetMap.get("giveto");
//							if(giveMoneyTo!=null&&giveMoneyTo==0){//���ָ������˻�
//								List<Map<String, Object>> insertSqlList = new ArrayList<Map<String,Object>>();
//								Map<String, Object> comInfoSql = new HashMap<String, Object>();
//								Map<String, Object> parkAccountSql = new HashMap<String, Object>();
//								comInfoSql.put("sql", "update com_info_tb set money=money+?, total_money=total_money+? where id=?");
//								comInfoSql.put("values",new Object[]{backmoney,backmoney,comid});
//								parkAccountSql.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source,orderid) values(?,?,?,?,?,?,?,?)");
//								parkAccountSql.put("values",new Object[]{orderMap.get("comid"),backmoney,2,ntime,"ͣ��������",uid,1,orderId});
//								insertSqlList.add(comInfoSql);
//								insertSqlList.add(parkAccountSql);
//								isset = daService.bathUpdate(insertSqlList);
//							}else {//���ָ��շ�Ա�˻�
//								List<Map<String, Object>> insertSqlList = new ArrayList<Map<String,Object>>();
//								Map<String, Object> userInfoSql = new HashMap<String, Object>();
//								Map<String, Object> parkAccountSql = new HashMap<String, Object>();
//								userInfoSql.put("sql", "update user_info_tb set balance=balance+? where id=?");
//								userInfoSql.put("values",new Object[]{backmoney,uid});
//								parkAccountSql.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target,orderid) values(?,?,?,?,?,?,?)");
//								parkAccountSql.put("values",new Object[]{uid,backmoney,0,ntime,"ͣ��������",3,orderId});
//								insertSqlList.add(userInfoSql);
//								insertSqlList.add(parkAccountSql);
//								isset = daService.bathUpdate(insertSqlList);
//							}
//							logger.info(">>>>>>>>>>>>ͣ�������ָ�"+giveMoneyTo+",�����"+isset+",���»��� ");
//							if(isset){
////								memcacheUtils.updateBackMoneyCache(orderMap.get("comid")+"_"+uin);
//							}
//						}
//					}else {
//						logger.info(">>>>>total:"+total+">>>>���ֳ���1��..."+orderMap.get("comid")+"_"+uin);
//					}
//				}else {
//					logger.info(">>>>>black>>>>������"+uin+",�ں������ڣ������������� ......");
//				}
//				if(!isBlack){
//					Double ticket_money = 0d;
//					Map<String, Object> ticketMap = daService.getMap(
//							"select * from ticket_tb where orderid=? and type<=? order by utime limit ?",
//							new Object[] { orderId,2,1});
//					if(ticketMap != null){
//						if(ticketMap.get("bmoney") != null){
//							ticket_money = Double.valueOf(ticketMap.get("bmoney") + "");
//						}else if(ticketMap.get("umoney") != null){
//							ticket_money = Double.valueOf(ticketMap.get("umoney") + "");
//						}
//					}
//					logger.info("doprepayorder>>>>>:orderid:"+orderId+",ticket_money:"+ticket_money);
//					backTicket(total - ticket_money, orderId, uin,comid,"");
////					if(total>=1)
////						updateSorce(start, etime, cType, uid, comid);
//				}else {
//					logger.info(">>>>>black>>>>������"+uin+",�ں������ڣ�������� ......");
//				}
//				//дϵͳ��־ 
//				String time = TimeTools.gettime();
//				if(state!=null&&state==0){
//					logService.updateOrderLog(comid,uin,time+",�ʺţ�"+uin+",���ƣ�"+carNumber+",ͣ���շѣ�"+total+",ͣ������"+comName,1);
//				}
//				
//				//[����XX������֧���ɹ�,10.0Ԫ,������,����鿴��������]
//				
//				String openid = "";
//				Map userMap = daService.getMap("select wxp_openid from user_info_Tb where id = ? ", new Object[]{uin});
//				if(userMap!=null)
//					openid = (String)userMap.get("wxp_openid");
//				else {
//					userMap = daService.getMap("select openid from wxp_user_Tb where uin= ? ", new Object[]{uin});
//					if(userMap!=null)
//						openid = (String)userMap.get("openid");
//				}
//				if(openid!=null&&openid.length()>10){
//					logger.info(">>>֧���ɹ���ͨ��΢�ŷ���Ϣ������...openid:"+openid);
//					Map bMap  =daService.getMap("select * from order_ticket_tb where uin=? and  order_id=? and ctime>? order by ctime desc limit ?",
//							new Object[]{uin,orderId,System.currentTimeMillis()/1000-5*60, 1});//�����ǰ�ĺ��
//					
//					Map<String, String> baseinfo = new HashMap<String, String>();
//					List<Map<String, String>> orderinfo = new ArrayList<Map<String,String>>();
//					String first = "Ԥ֧��"+prefee+"Ԫ";
//					String orderMoneySum = total +"Ԫ";
//					String orderMoneySum_color = "#000000";
//					String orderProductName = "ͣ����";
//					String orderProductName_color = "#000000";
//					String remark = "����鿴��������";
//					String remark_color = "#000000";
//					if(prefee<total){
//						orderMoneySum = "ͣ����"+total+"Ԫ";
//						orderProductName = "����֧��"+StringUtils.formatDouble(total-prefee)+"Ԫ";
//						orderProductName_color = "#FF0000";
//					}else if(prefee > total && back > 0){
//						orderProductName_color = "#FF0000";
//						orderProductName = "�����Ԥ�����"+back + "Ԫ(���Ż�ȯ�ۿۺ�)�ѷ��������˻���";
//					}
//					Integer first_flag = 0;//�Ƿ����׵�֧��
//					if(bMap != null){
//						remark_color = "#FF0000";
//						remark = "��ϲ�����"+bMap.get("bnum")+"����"+bMap.get("money")+"Ԫͣ��ȯ������������ɣ�";
//						
//						Long count = daService.getLong("select count(*) from user_account_tb where uin=? and type=? ", new Object[]{uin, 1});
//						logger.info("�Ƿ����׵�֧��>>>>orderid:"+orderId+",uin:"+uin+",openid:"+openid+",count:"+count+",time:"+System.currentTimeMillis()/1000);
//						if(count == 1){
//							first_flag = 1;
//						}
//					}
//					String url =  "http://"+Constants.WXPUBLIC_REDIRECTURL+"/zld/carinter.do?action=orderdetail&prepay="+prefee+"&orderid="+orderId+"&back="+back+"&first_flag="+first_flag;
//					baseinfo.put("url", url);
//					baseinfo.put("openid", openid);
//					baseinfo.put("top_color", "#000000");
//					baseinfo.put("templeteid", Constants.WXPUBLIC_SUCCESS_NOTIFYMSG_ID);
//					Map<String, String> keyword1 = new HashMap<String, String>();
//					keyword1.put("keyword", "orderMoneySum");
//					keyword1.put("value", orderMoneySum);
//					keyword1.put("color", orderMoneySum_color);
//					orderinfo.add(keyword1);
//					Map<String, String> keyword2 = new HashMap<String, String>();
//					keyword2.put("keyword", "orderProductName");
//					keyword2.put("value", orderProductName);
//					keyword2.put("color", orderProductName_color);
//					orderinfo.add(keyword2);
//					Map<String, String> keyword3 = new HashMap<String, String>();
//					keyword3.put("keyword", "Remark");
//					keyword3.put("value", remark);
//					keyword3.put("color", remark_color);
//					orderinfo.add(keyword3);
//					Map<String, String> keyword4 = new HashMap<String, String>();
//					keyword4.put("keyword", "first");
//					keyword4.put("value", first);
//					keyword4.put("color", "#000000");
//					orderinfo.add(keyword4);
//					sendWXTempleteMsg(baseinfo, orderinfo);
//					
//					sendBounsMessage(openid,uid,2d,orderId ,uin);//��������Ϣ
//				}
//				
//			} catch (Exception e) {
//				logger.info(">>>>>>>>>>ͣ�������֡�ͣ������ȯʧ�ܣ�...............");
//				e.printStackTrace();
//			}
//			
//			return 1;
//		}else {
//			return -1;
//		}
//	}
	
	/**
	 * ����Ԥ���Ѷ���
	 * @param orderMap ����
	 * @param total ʵ�ս��
	 * @return 0ʧ�� 1�ɹ�
	 */
	public Map<String, Object> doMidPayOrder(Map<String, Object> orderMap, Double total, Long uid){
		logger.info("�����ֽ�Ԥ֧������doMidPayOrder��orderid:"+orderMap.get("id")+",Ԥ֧����"+orderMap.get("total")+",uin:"+orderMap.get("uin")+",ͣ���ѽ�total:"+total+",car_number:"+orderMap.get("car_number"));
		Double prefee = Double.valueOf(orderMap.get("total") + "");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long orderid = (Long)orderMap.get("id");
		Long comid = (Long)orderMap.get("comid");
		Integer state = (Integer)orderMap.get("state");
		Long create_time = (Long)orderMap.get("create_time");
		Integer car_type = (Integer)orderMap.get("car_type");//0��ͨ�ã�1��С����2����
		Integer pid = (Integer)orderMap.get("pid");
		if(state == 1){
			logger.info("doMidPayOrder>>>>orderid:"+orderid+",������֧�������أ�");
			resultMap.put("result", -1);
			return resultMap;
		}
		Long ntime = System.currentTimeMillis()/1000;
		
		//������ȯʹ�����
		Double distotal = 0d;
		Double umoney = 0d;
		Map<String, Object> shopticketMap = daService
				.getMap("select * from ticket_tb where (type=? or type=?) and orderid=? ",
						new Object[] { 3, 4, orderMap.get("id") });
		if(shopticketMap != null){
			Integer type = (Integer)shopticketMap.get("type");
			Integer money = (Integer)shopticketMap.get("money");
			umoney = Double.valueOf(shopticketMap.get("umoney") + "");
			Long end_time = ntime;
			logger.info("doMidPayOrder>>>>>:orderid:"+orderid+",shopticketid:"+shopticketMap.get("id")+",type:"+type+",umoney:"+umoney);
			if(type == 4){//ȫ��
				distotal = total;
				logger.info("doMidPayOrder>>>>ȫ��ȯ:orderid:"+orderid+",distotal:"+distotal);
			}else if(type == 3){
				if(create_time + money * 60 * 60 > end_time){
					distotal = total;
					logger.info("doMidPayOrder>>>>��ʱȯ:orderid:"+orderid+",distotal:"+distotal);
				}else{
					end_time = end_time - money * 60 *60;
					Double dtotal = 0d;
					if(pid>-1){
						dtotal = Double.valueOf(getCustomPrice(create_time, end_time, pid));
					}else {
						dtotal = Double.valueOf(getPrice(create_time, end_time, comid, car_type));
					}
					if(total > dtotal){
						distotal = StringUtils.formatDouble(total - dtotal);
					}
					logger.info("doMidPayOrder>>>>��ʱȯ:orderid:"+orderid+",distotal="+distotal);
				}
			}
			resultMap.put("ticket_type", type);
		}
		resultMap.put("distotal", distotal);
		
		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
		//���¶���״̬���շѳɹ�
		Map<String, Object> orderSqlMap = new HashMap<String, Object>();
		//������ȯ���
		Map<String, Object> ticketSqlMap = new HashMap<String, Object>();
		
		orderSqlMap.put("sql", "update order_tb set state=?,total=?,end_time=? where id=?");
		orderSqlMap.put("values", new Object[]{1,total,System.currentTimeMillis()/1000,orderid});
		bathSql.add(orderSqlMap);
		if(shopticketMap != null){
			ticketSqlMap.put("sql", "update ticket_tb set bmoney=? where id=?");
			ticketSqlMap.put("values", new Object[]{distotal, shopticketMap.get("id")});
			bathSql.add(ticketSqlMap);
		}
		prefee = StringUtils.formatDouble(prefee - umoney + distotal);//����ʵ��Ԥ֧�����
		logger.info("doMidPayOrder>>>>>:���¼�����Ԥ֧�����prefee:"+prefee+",orderid:"+orderid);
		resultMap.put("prefee", prefee);
		if(prefee < total){
			//���ֽ��¼
			Map<String, Object> cashsqlMap = new HashMap<String, Object>();
			cashsqlMap.put("sql", "insert into parkuser_cash_tb(uin,amount,type,orderid,create_time) values(?,?,?,?,?)");
			cashsqlMap.put("values", new Object[]{uid, total - prefee, 0, orderid, System.currentTimeMillis()/1000});
			bathSql.add(cashsqlMap);
		}
		boolean result= daService.bathUpdate(bathSql);
		logger.info("doMidPayOrder>>>>,orderid:"+orderid+",result:"+result);
		if(result){
			resultMap.put("result", 1);
			return resultMap;
		}
		resultMap.put("result", -1);
		return resultMap;
	}
	
//	private boolean backWeixinTicket(Double money, Long orderId, Long uin){
//		Integer bonus = 5;//5��
//		if(money>=1&&memcacheUtils.readBackTicketCache(uin)){//һ��ֻ��һ�κ��
//			String sql = "insert into order_ticket_tb (uin,order_id,money,bnum,ctime,exptime,bwords,type) values(?,?,?,?,?,?,?,?)";
//			Object []values = null;
//			Long ctime = System.currentTimeMillis()/1000;
//			Long exptime = ctime + 24*60*60;
//			values = new Object[]{uin,orderId,bonus,5,ctime,exptime,"΢��֧������ȯ",1};
//			logger.info(">>>>>΢��Ԥ֧�����,5��"+bonus+"��ȯ...");
//			int ret = daService.update(sql, values);
//			logger.info(">>>>>΢��Ԥ֧����� ret :"+ret);
//			if(ret==1){
//				memcacheUtils.updateBackTicketCache(uin);
//				return true;
//			}
//		}else {
//			if(money< 1){
//				logger.info(">>>>>>>>֧�����С��1Ԫ���������>>>>>>uin:"+uin+",orderid:"+orderId+",money:"+money);
//			}else if(!memcacheUtils.readBackTicketCache(uin)){
//				logger.info(">>>>>>>>һ��ֻ��һ�κ�����Ѿ��������������>>>>>>uin:"+uin+",orderid:"+orderId+",money:"+money);
//			}
//			logger.info(">>>>>΢��֧�����,�Ѿ�������������.....");
//		}
//		return false;
//		
//	}


	/**
	 * ֱ�����շ�Ա����
	 * @param comId �������
	 * @param total ֧�����
	 * @param uin �����˺�
	 * @param uid �շ�Ա�˺�
	 * @param ticketId ͣ��ȯ��
	 * @param bind_flag 0��δ���˻� 1�������˻�
	 * @param ptype ֧������  0��1֧������2΢�ţ�3������4���+֧����,5���+΢��,6���+���� ,7ͣ������ֵ,8�����,9΢�Ź��ں�,10���+΢�Ź��ں�
	 * @return 0:ʧ�ܡ� 5:�ɹ� 
	 *  -12������ 
	 *  -13:ͣ��ȯʹ�ó���3��
	 */
//	public int epay(Long comId,Double total,Long uin,Long uid,Long ticketId,String carNumber,Integer ptype, Integer bind_flag,Long orderId, String wxp_orderid){
//		
//		Long ntime = System.currentTimeMillis()/1000;
//		logger.info(">>>>>>>>>>epay,ticket:"+ticketId+",uin:"+uin+",uid:"+uid);
//		//�Ż�ȯ���
//		Double ticketMoney = 0d;
//		Integer ticket_type = 7;//7��ͣ��ȯ��11��΢�Ŵ���ȯ
//		String ticket_dp = "ͣ��ȯ��ֵ";
//		if(ticketId !=null && ticketId == -100){//΢������ȯ
//			ticketMoney = getDisTicketMoney(uin, uid, total);
//			ticket_type = 11;
//			ticket_dp = "΢�Ŵ���ȯ��ֵ";
//			logger.info("orderid:"+orderId+",uin:"+uin+",ticketMoney:"+ticketMoney);
//		}else if(ticketId != null && ticketId > 0){
//			ticketMoney = getTicketMoney(ticketId, 3, uid, total, 2, comId, orderId);
//		}
//		logger.info("orderid:"+orderId+",uin:"+uin+",ticketid:"+ticketId+",ticketMoney:"+ticketMoney);
//		Map userMap = null;
//		Double ubalance =null;
//		if(bind_flag == 1){//�Ѱ��˻�
//			//������ʵ�˻����
//			userMap = daService.getPojo("select balance from user_info_tb where id =?",	new Object[]{uin});
//		}else{//δ���˻�
//			//�����˻����
//			userMap = daService.getPojo("select balance from wxp_user_tb where uin =?",	new Object[]{uin});
//		}
//		
//		if(userMap!=null&&userMap.get("balance")!=null){
//			ubalance = Double.valueOf(userMap.get("balance")+"");
//			ubalance +=ticketMoney;//�û��������Ż�ȯ���
//		}
//		logger.info("ticket money��"+ticketMoney+",uin:"+uin+",orderid:"+orderId+",balance:"+userMap.get("balance")+",total:"+total);
//		if(ubalance==null||ubalance<total){//�ʻ�����
//			logger.info("ֱ���˻����㣬�˻���"+ubalance+",ͣ���ѽ�"+total+",uin:"+uin);
//			return -12;
//		}
//		
//		Map<String, Object> comMap = daService.getMap("select company_name,city from com_info_tb where id=?",  new Object[]{comId});
//		String comName = "ͣ����";
//		if(comMap!=null&&comMap.get("company_name")!=null)
//			comName = (String)comMap.get("company_name");
//		else {
//			return -10;
//		}
//		//��ѯ�շ��趨 mtype:0:ͣ����,1:Ԥ����,2:ͣ��������
//		Map msetMap = daService.getPojo("select giveto from money_set_tb where comid=? and mtype=? ",
//				new Object[]{comId,0});
//		Integer giveTo =null;
//		if(msetMap!=null)
//			giveTo =(Integer)msetMap.get("giveto");
//		logger.info(">>>>>>"+msetMap+">>>>>giveto:"+giveTo+"comid:"+comId+",uin:"+uid);
//		
//		if("���ƺ�δ֪".equals(carNumber))
//			carNumber = uin+"";
//		
//		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
//		//�����û����
//		Map<String, Object> userSqlMap = new HashMap<String, Object>();
//		//����ͣ�������
//	    Map<String, Object> comSqlMap = new HashMap<String, Object>();
//		//�շ�Ա�˻�
//		Map<String, Object> parkuserAccountsqlMap =new HashMap<String, Object>();
//		//�����˻�
//		Map<String, Object> userAccountsqlMap =new HashMap<String, Object>();
//		//�����˻�
//		Map<String, Object> parkAccountsqlMap =new HashMap<String, Object>();
//		//�����˻���ͣ��ȯ
//		Map<String, Object> userTicketAccountsqlMap =new HashMap<String, Object>();
//		//�շ�Ա���
//		Map<String, Object> parkusersqlMap =new HashMap<String, Object>();
//		//ʹ��ͣ��ȯ����
//		Map<String, Object> ticketsqlMap =new HashMap<String, Object>();
//		//ͣ�����˻���ͣ��ȯ���
//		Map<String, Object> tingchebaoAccountsqlMap =new HashMap<String, Object>();
//		
//		//�۳������˻����
//		if(bind_flag == 1){
//			userSqlMap.put("sql", "update user_info_tb  set balance =balance-? where id=?");
//		}else{
//			userSqlMap.put("sql", "update wxp_user_tb  set balance =balance-? where uin=?");
//		}
//		userSqlMap.put("values", new Object[]{total-ticketMoney,uin});
//		if(total-ticketMoney>0)
//			bathSql.add(userSqlMap);
//		//�����˻��Ż�ȯ��ֵ
//		if(ticketMoney>0&&ticketId!=null){//ʹ��ͣ��ȯ���������˻��ȳ�ֵ
//			userTicketAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,orderid) values(?,?,?,?,?,?,?)");
//			userTicketAccountsqlMap.put("values", new Object[]{uin,ticketMoney,0,ntime-1,ticket_dp,ticket_type,orderId});
//			bathSql.add(userTicketAccountsqlMap);
//		}
//		//�����˻�֧��ͣ������ϸ
//		userAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,uid,target,orderid) values(?,?,?,?,?,?,?,?,?)");
//		userAccountsqlMap.put("values", new Object[]{uin,total,1,ntime,"ͣ����-"+comName,ptype,uid,1,orderId});
//		bathSql.add(userAccountsqlMap);
//
//		//������Ĭ�ϸ������˻�20141120����Ҫ���޸�
//		if(giveTo!=null&&giveTo==0){//д�빫˾�˻�
//			comSqlMap.put("sql", "update com_info_tb  set total_money =total_money+?,money=money+? where id=?");
//			comSqlMap.put("values", new Object[]{total,total,comId});
//			bathSql.add(comSqlMap);
//			
//			parkAccountsqlMap.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source,orderid) values(?,?,?,?,?,?,?,?)");
//			parkAccountsqlMap.put("values",  new Object[]{comId,total,0,ntime,"ͣ����_"+carNumber,uid,0,orderId});
//			bathSql.add(parkAccountsqlMap);
//		}else {//д������˻�
////			//ͣ����д���շ�Ա�˻�
//			parkusersqlMap.put("sql", "update user_info_tb  set balance =balance+? where id=?");
//			parkusersqlMap.put("values", new Object[]{total,uid});
//			bathSql.add(parkusersqlMap);
//			//�շ�Ա�˻��շ���ϸ
//			parkuserAccountsqlMap.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target,orderid) values(?,?,?,?,?,?,?)");
//			parkuserAccountsqlMap.put("values", new Object[]{uid,total,0,ntime,"ͣ����_"+carNumber,4,orderId});
//			bathSql.add(parkuserAccountsqlMap);
//		}
//		
//		//�Ż�ȯʹ�ú󣬸���ȯ״̬�����ͣ�����˻�֧����¼
//		if(ticketMoney>0&&ticketId!=null&&ticketId>0){
//			ticketsqlMap.put("sql", "update ticket_tb  set state=?,comid=?,utime=?,umoney=?,wxp_orderid=? where id=?");
//			ticketsqlMap.put("values", new Object[]{1,comId,System.currentTimeMillis()/1000,ticketMoney,wxp_orderid,ticketId});
//			bathSql.add(ticketsqlMap);
//			
//			tingchebaoAccountsqlMap.put("sql", "insert into tingchebao_account_tb(amount,type,create_time,remark,utype,orderid) values(?,?,?,?,?,?)");
//			tingchebaoAccountsqlMap.put("values", new Object[]{ticketMoney,1,ntime,comName+"������"+carNumber+"��ʹ��ͣ������ȯ",0,orderId});
//			bathSql.add(tingchebaoAccountsqlMap);
//			memcacheUtils.updateUseTicketCache(uin);//��ȯ����ʹ��ȯ����
//		}
//		
//		boolean result= daService.bathUpdate(bathSql);
//		logger.info("ֱ����� ��"+result+",uin:"+uin+",orderid:"+orderId);
//		if(result){//����ɹ�������ȯ������ 
//			//�����֣�����������ȯ��������
//			/* ÿ��������΢�Ż�֧����֧��1Ԫ���ϵ���ɵģ���������2Ԫ����������3Ԫ��ͣ��ȯ��
//			 * �������ֲ���(ͬһ����ÿ��ֻ�ܷ�3��)��
//			 * ����ÿ�շ�ȯ��3��ȯ
//			 * ÿ������ÿ��ʹ��ͣ��ȯ������3�� */
//			try {
//				boolean isBlack = isBlackUser(uin);
//				double ownpay = total-ticketMoney;
//				if(!isBlack){
//					if(ownpay>=1&&memcacheUtils.readBackMoneyCache(comId+"_"+uin)){//���Ը��������� 
//						boolean isCanBackMoney = isCanBackMoney(comId);//�Ƿ��Ǽ��ϳ���
//						Double backmoney = getBackMoney();
//						logger.info("epay>>>>>uin:"+uin+",backmoney:"+backmoney+",isCanBackMoney:"+isCanBackMoney);
//						if(isCanBackMoney && backmoney > 0){
//							//�鷵������
//							Integer giveMoneyTo = null;//��ѯ�շ��趨 mtype:0:ͣ����,1:Ԥ����,2:ͣ��������
//							msetMap = daService.getPojo("select giveto from money_set_tb where comid=? and mtype=? ",
//									new Object[]{comId,2});
//							if(msetMap!=null)
//								giveMoneyTo =(Integer)msetMap.get("giveto");
//							boolean isset =false;
//							if(giveMoneyTo!=null&&giveMoneyTo==0){//���ָ������˻�
//								List<Map<String, Object>> insertSqlList = new ArrayList<Map<String,Object>>();
//								Map<String, Object> comInfoSql = new HashMap<String, Object>();
//								Map<String, Object> parkAccountSql = new HashMap<String, Object>();
//								comInfoSql.put("sql", "update com_info_tb set money=money+?, total_money=total_money+? where id=?");
//								comInfoSql.put("values",new Object[]{backmoney,backmoney,comId});
//								parkAccountSql.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source,orderid) values(?,?,?,?,?,?,?,?)");
//								parkAccountSql.put("values",new Object[]{comId,backmoney,2,ntime,"ͣ��������",uid,1,orderId});
//								insertSqlList.add(comInfoSql);
//								insertSqlList.add(parkAccountSql);
//								isset = daService.bathUpdate(insertSqlList);
//							}else {//���ָ��շ�Ա�˻�
//								List<Map<String, Object>> insertSqlList = new ArrayList<Map<String,Object>>();
//								Map<String, Object> userInfoSql = new HashMap<String, Object>();
//								Map<String, Object> parkAccountSql = new HashMap<String, Object>();
//								//���ָ��շ�Ա
//								userInfoSql.put("sql", "update user_info_tb set balance=balance+? where id=?");
//								userInfoSql.put("values",new Object[]{backmoney,uid});
//								//���ָ��շ�Ա��ϸ
//								parkAccountSql.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target,orderid) values(?,?,?,?,?,?,?)");
//								parkAccountSql.put("values",new Object[]{uid,backmoney,0,ntime,"ͣ��������",3,orderId});
//								
//								insertSqlList.add(userInfoSql);
//								insertSqlList.add(parkAccountSql);
//								isset = daService.bathUpdate(insertSqlList);
//							}
//							logger.info(">>>>>>>>>>>>ͣ�������ָ��շ�Ա,�����"+isset+",���»��� ");
//							if(isset){
//								memcacheUtils.updateBackMoneyCache(comId+"_"+uin);
//							}
//						}
//					}else {
//						logger.info(">>>>>total:"+total+">>>>���ֳ���1��..."+comId+"_"+uin);
//					}
//				}else {
//					logger.info(">>>>>black>>>>������"+uin+",�ں������ڣ������������� ......");
//				}
//				if(!isBlack){
//					backTicket(total-ticketMoney, orderId, uin,comId,wxp_orderid);
////					if(ownpay>=1)
////						updateSorce(ntime, ntime+16*60, 0, uid, comId);
//				}else {
//					logger.info(">>>>>black>>>>������"+uin+",�ں������ڣ����ܷ����......");
//				}
//				if(ownpay>=1)
//					handleRecommendCode(uin,isBlack);
//			} catch (Exception e) {
//				logger.info(">>>>>>>>>>ͣ�������֡�ͣ������ȯʧ�ܣ�...............");
//				e.printStackTrace();
//			}
//			
//			//дһ��������¼
//			try {
//				//�½�һ��������Ԥȡ������� ,ֱ�������ɹ���д�붩����
//				int orderRet = daService.update("insert into order_tb(id,create_time,comid,uin,total,state,end_time,pay_type," +
//						"c_type,uid,car_number) values(?,?,?,?,?,?,?,?,?,?,?)", 
//						new Object[]{orderId,ntime,comId,uin,total,1,ntime+60,2,4,uid,carNumber});
//				
//				logger.info(">>>epay,д�붩��..."+orderRet);
//				//����5��ȯ����ͣ��ȯ������ ������һ����¼���������
//				if(ticket_type==11&&ticketId<0&&ticketMoney>0){
//					int ret = ret = daService.update( "insert into ticket_tb (create_time,limit_day,money,state,uin,comid,type,orderid,utime,umoney,wxp_orderid)" +
//							" values(?,?,?,?,?,?,?,?,?,?,?) ",
//							new Object[]{TimeTools.getToDayBeginTime(),TimeTools.getToDayBeginTime()+24*60*60-1,5,1,uin,comId,2,orderId,ntime,ticketMoney,wxp_orderid});
//					memcacheUtils.updateUseTicketCache(uin);//��ȯ����ʹ��ȯ����
//					logger.info(">>>epay  ,û������ȯʱ��дһ����¼�����:"+ret);
//				}else if(ticketId!=null&&ticketId>0&&ticketMoney>0){
//					int ret  = daService.update("update ticket_tb  set orderid=? where id=?", new Object[]{orderId,ticketId});
//				}
//			} catch (Exception e) {
//				logger.info(">>>>>epay error:д�붩����д��ͣ��ȯ����..."+e.getMessage());
//			}
//			
//			if(ticketMoney > 0){
////				updateAllowCache(comId, ticketId, ticketMoney);
//				logger.info("update allowance cache>>>uin:"+uin+",ticketMoney:"+ticketMoney);
//			}
//			try {//ֱ����ɺ�����Ϣ
//				String openid = "";
//				String url = "";
//				userMap = daService.getMap("select wxp_openid from user_info_Tb where id = ? ", new Object[]{uin});
//				if(userMap!=null){
//					logger.info(">>>>>>>>>�Ѿ����˻���uin��"+uin);
//					openid = (String)userMap.get("wxp_openid");
//					url = "http://"+Constants.WXPUBLIC_REDIRECTURL+"/zld/wxpaccount.do?action=toaccountdetail&openid="+openid;
//				} else {
//					logger.info(">>>>>>>>>>>δ���˻���uin��"+uin);
//					userMap = daService.getMap("select openid from wxp_user_Tb where uin= ? ", new Object[]{uin});
//					if(userMap!=null){
//						openid = (String)userMap.get("openid");
//						url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+Constants.WXPUBLIC_APPID+"&redirect_uri=http%3a%2f%2f"+Constants.WXPUBLIC_REDIRECTURL+"%2fzld%2fwxpaccount.do&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
//					}	
//				}
//				if(openid!=null&&openid.length()>10 && (ptype == 10 || ptype == 9 || ptype == 0) ){
//					logger.info(">>>ֱ���ɹ���ͨ��΢�ŷ���Ϣ������...directpaymsg:openid:"+openid+",uin��"+uin);
//					Map<String, String> baseinfo = new HashMap<String, String>();
//					List<Map<String, String>> orderinfo = new ArrayList<Map<String,String>>();
//					Map uidMap = daService.getMap("select nickname from user_info_tb where id=? ", new Object[]{uid});
//					String first = "����"+comMap.get("company_name")+"���շ�Ա"+uidMap.get("nickname")+"���ѳɹ���";
//					String remark = "���������˻���ϸ��";
//					String remark_color = "#000000";
//					//��ѯ�������
//					Map bMap  =daService.getMap("select * from order_ticket_tb where uin=? and  order_id=? ",
//							new Object[]{uin,orderId});//�����ǰ�ĺ��
//					
//					if(bMap!=null&&bMap.get("id")!=null){
//						Integer bonus_type = 0;//0:��ͨ���������1��΢���ۿۺ��
//						if(bMap.get("type")!= null && (Integer)bMap.get("type") == 1){
//							bonus_type = 1;//΢�Ŵ��ۺ��
//						}
//						if(bonus_type == 1){
//							remark = "��ϲ�����"+bMap.get("bnum")+"��΢��"+bMap.get("money")+"��ȯ������������ɣ�";
//						}else{
//							remark = "��ϲ�����"+bMap.get("bnum")+"����"+bMap.get("money")+"Ԫ ͣ��ȯ������������ɣ�";
//						}
//						remark_color = "#FF0000";
//						
//						Integer first_flag = 0;
//						Long count = daService.getLong("select count(*) from user_account_tb where uin=? and type=? ", new Object[]{uin, 1});
//						logger.info("�Ƿ����׵�֧��>>>>orderid:"+orderId+",uin:"+uin+",openid:"+openid+",count:"+count+",time:"+System.currentTimeMillis()/1000);
//						if(count == 1){
//							first_flag = 1;
//						}
//						url = "http://"+Constants.WXPUBLIC_REDIRECTURL+"/zld/wxpublic.do?action=balancepayinfo&openid="+openid+"&money="+total+"&bonusid="+bMap.get("id")+"&bonus_type="+bonus_type+"&first_flag="+first_flag;
//					}
//					baseinfo.put("url", url);
//					baseinfo.put("openid", openid);
//					baseinfo.put("top_color", "#000000");
//					baseinfo.put("templeteid", Constants.WXPUBLIC_SUCCESS_NOTIFYMSG_ID);
//					Map<String, String> keyword1 = new HashMap<String, String>();
//					keyword1.put("keyword", "orderMoneySum");
//					keyword1.put("value", total+"Ԫ");
//					keyword1.put("color", "#000000");
//					orderinfo.add(keyword1);
//					Map<String, String> keyword2 = new HashMap<String, String>();
//					keyword2.put("keyword", "orderProductName");
//					keyword2.put("value", "ͣ����");
//					keyword2.put("color", "#000000");
//					orderinfo.add(keyword2);
//					Map<String, String> keyword3 = new HashMap<String, String>();
//					keyword3.put("keyword", "Remark");
//					keyword3.put("value", remark);
//					keyword3.put("color", remark_color);
//					orderinfo.add(keyword3);
//					Map<String, String> keyword4 = new HashMap<String, String>();
//					keyword4.put("keyword", "first");
//					keyword4.put("value", first);
//					keyword4.put("color", "#000000");
//					orderinfo.add(keyword4);
//					sendWXTempleteMsg(baseinfo, orderinfo);
//					
//					sendBounsMessage(openid,uid,2d,orderId,uin);//��������Ϣ
//				}
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//			return 5;
//		}else {
//			return -7;
//		}
//	}
	/**
	 * �鳵�ƺ�
	 * @param uin
	 * @return
	 */
	public String getCarNumber(Long uin){
		String carNumber="���ƺ�δ֪";//�������ƺ�
		Map carNuberMap = daService.getPojo("select car_number from car_info_tb where uin=? and state=?  ", 
				new Object[]{uin,1});
		if(carNuberMap!=null&&carNuberMap.get("car_number")!=null&&!carNuberMap.get("car_number").toString().equals(""))
			carNumber = (String)carNuberMap.get("car_number");
		return carNumber;
	}
	
	
	public Map getPriceMap(Long comid){
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		//��ʼСʱ
		int bhour = calendar.get(Calendar.HOUR_OF_DAY);
		List<Map> priceList=daService.getAll("select * from price_tb where comid=? " +
				"and state=? and pay_type=? order by id desc", new Object[]{comid,0,0});
		if(priceList==null||priceList.size()==0){//û�а�ʱ�β���
			//�鰴�β���
			priceList=daService.getAll("select * from price_tb where comid=? " +
					"and state=? and pay_type=? order by id desc", new Object[]{comid,0,1});
			if(priceList==null||priceList.size()==0){//û�а��β��ԣ�������ʾ
				return null;
			}else {//�а��β��ԣ�ֱ�ӷ���һ�ε��շ�
				 return priceList.get(0);
			}
			//�����Ÿ�����Ա��ͨ�����úü۸�
		}else {//�Ӱ�ʱ�μ۸�����зּ���ռ��ҹ���շѲ���
			if(priceList.size()>0){
				for(Map map : priceList){
					Integer btime = (Integer)map.get("b_time");
					Integer etime = (Integer)map.get("e_time");
					if(btime<etime){//�ռ�
						if(bhour>=btime&&bhour<etime)
							return map;
					}else {
						if((bhour>=btime&&bhour<24)||(bhour>=0&&bhour<etime))
							return map;
					}
				}
			}
		}
		return null;
	}
	/**
	 * ���㶩�����
	 * @param start
	 * @param end
	 * @param comId
	 * @param car_type 0��ͨ�ã�1��С����2����
	 * @return �������_�Ƿ��Ż�
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public  String getPrice(Long start,Long end,Long comId,Integer car_type){
//		String pid = CustomDefind.CUSTOMPARKIDS;
//		if(pid.equals(comId.toString())){//���Ƽ۸����
//			return "������";
//		}
//		
		if(car_type == 0){//0:ͨ��
			Long count = daService.getLong("select count(*) from com_info_tb where id=? and car_type=?", new Object[]{comId,1});
			if(count > 0){//���ִ�С��
				car_type = 1;//Ĭ�ϳ�С���ƷѲ���
			}
		}
		Map priceMap1=null;
		Map priceMap2=null;
		List<Map> priceList=daService.getAll("select * from price_tb where comid=? " +
				"and state=? and pay_type=? and car_type=? order by id desc", new Object[]{comId,0,0,car_type});
		if(priceList==null||priceList.size()==0){
			//�鰴�β���
			priceList=daService.getAll("select * from price_tb where comid=? " +
					"and state=? and pay_type=? and car_type=? order by id desc", new Object[]{comId,0,1,car_type});
			if(priceList==null||priceList.size()==0){//û���κβ���
				return "0.0";
			}else {//�а��β��ԣ�����N�ε��շ�
				Map timeMap =priceList.get(0);
				Object ounit  = timeMap.get("unit");
				Double total = Double.valueOf(timeMap.get("price")+"");
				try {
					if(ounit!=null){
						Integer unit = Integer.valueOf(ounit.toString());
						if(unit>0){
							Long du = (end-start)/60;//ʱ����
							int times = du.intValue()/unit;
							if(du%unit!=0)
								times +=1;
							total = times*total;
							
						}
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				return StringUtils.formatDouble(total)+"";
			}
		}else {
			priceMap1=priceList.get(0);
			boolean pm1 = false;//�ҵ�map1,�����ǽ���ʱ����ڿ�ʼʱ��
			boolean pm2 = false;//�ҵ�map2
			Integer payType = (Integer)priceMap1.get("pay_type");
			if(payType==0&&priceList.size()>1){
				for(Map map : priceList){
					if(pm1&&pm2)
						break;
					payType = (Integer)map.get("pay_type");
					Integer btime = (Integer)map.get("b_time");
					Integer etime = (Integer)map.get("e_time");
					if(payType==0&&etime>btime){
						if(!pm1){
							priceMap1 = map;
							pm1=true;
						}else {
							priceMap2=map;
							pm2=true;
						}
					}else {
						if(!pm2){
							priceMap2=map;
							pm2=true;
						}
					}
				}
			}
		}
		double minPriceUnit = getminPriceUnit(comId);
		Map assistMap = daService.getMap("select * from price_assist_tb where comid = ? and type = ?", new Object[]{comId,0});
		Map orderInfp = CountPrice.getAccount(start, end, priceMap1, priceMap2,minPriceUnit,assistMap);
		
		//Double count= StringUtils.getAccount(start, end, priceMap1, priceMap2);
		return StringUtils.formatDouble(orderInfp.get("collect"))+"";	
	}
	/**
	 * 
	 * @param start
	 * @param end
	 * @param pid �Ʒѷ�ʽ��0��ʱ(0.5/15����)��1���Σ�12Сʱ��10Ԫ,ǰ1/30min����ÿСʱ1Ԫ��
	 * @return
	 */
	public String getCustomPrice(Long start,Long end,Integer pid) {
		/**һԪ/��Сʱ     12Сʱ�ڷⶥ10Ԫ��12Сʱ��ÿ��һСʱ����һԪ��*/
		logger.info(">>>>>>���Ƽ۸񳵳�,pid(0��ʱ(0.5/15����)��1���Σ�12Сʱ��10Ԫ,ǰ1/30min����ÿСʱ1Ԫ��)="+pid);
		Long duration = (end-start)/60;//����
		Long hour = duration/(60);//Сʱ��;
		if(pid==0){
			Long t = duration/15;
			if(duration%15!=0)
				t= t+1;
			return StringUtils.formatDouble(t*0.5)+"";
		}else if(pid==1){
			if(duration%60!=0)
				hour = hour+1;
			if(hour<12){
				if(hour<6){
					Long tLong = duration/30;
					if(duration%30!=0)
						tLong += 1L;
					return StringUtils.formatDouble(tLong)+"";
				}
				else 
					return 10.0+"";
			}else {
				return 10.0+(hour-12)+"";
			}
		}else {
			return "0";
		}
	}


	//@SuppressWarnings({ "rawtypes", "unchecked" })
	public String handleOrder(Long comId,Map orderMap) throws Exception{
		Map dayMap=null;//�ռ����
		Map nigthMap=null;//ҹ�����
		//��ʱ�μ۸����
		List<Map<String ,Object>> priceList=null;//SystemMemcachee.getPriceByComid(comId);
		priceList=daService.getAll("select * from price_tb where comid=? " +
					"and state=? and pay_type=? order by id desc", new Object[]{comId,0,0});
		Long ntime = System.currentTimeMillis()/1000;
		if(priceList==null||priceList.size()==0){//û�а�ʱ�β���
			//�鰴�β���
			priceList=daService.getAll("select * from price_tb where comid=? " +
					"and state=? and pay_type=? order by id desc", new Object[]{comId,0,1});
			Long btLong = (Long)orderMap.get("create_time");
			String btime = TimeTools.getTime_MMdd_HHmm(btLong*1000).substring(6);
			String etime = TimeTools.getTime_MMdd_HHmm(ntime*1000).substring(6);
			Map<String, Object> orMap=new HashMap<String, Object>();
			Long start = (Long)orderMap.get("create_time");
			Long end = ntime;
			orMap.put("btime", btime);
			orMap.put("etime", etime);
			orMap.put("duration", StringUtils.getTimeString(start, end));
			orMap.put("orderid", orderMap.get("id"));
			orMap.put("carnumber",orderMap.get("car_number")==null?"���ƺ�δ֪": orderMap.get("car_number"));
			orMap.put("handcash", "0");
			orMap.put("uin", orderMap.get("uin"));
			if(priceList==null||priceList.size()==0){//û�а��β��ԣ�������ʾ
				//���ظ��շ�Ա���ֹ�����۸�
				orMap.put("total", "0.00");
				orMap.put("collect", "0.00");
				orMap.put("handcash", "1");
			}else {//�а��β��ԣ�ֱ�ӷ���һ�ε��շ�
				Map timeMap =priceList.get(0);
				Object ounit  = timeMap.get("unit");
//				orMap.put("btime", btime);
//				orMap.put("etime", etime);
//				orMap.put("duration", StringUtils.getTimeString(start, end));
//				orMap.put("orderid", orderMap.get("id"));
//				orMap.put("carnumber",orderMap.get("car_number")==null?"���ƺ�δ֪": orderMap.get("car_number"));
//				
				orMap.put("collect", timeMap.get("price"));
				orMap.put("total", timeMap.get("price"));
				if(ounit!=null){
					Integer unit = Integer.valueOf(ounit.toString());
					if(unit>0){
						Long du = (end-start)/60;//ʱ����
						int times = du.intValue()/unit;
						if(du%unit!=0)
							times +=1;
						double total = times*Double.valueOf(timeMap.get("price")+"");
						orMap.put("collect", total);
						orMap.put("total", total);
					}
				}
			}
			return StringUtils.createJson(orMap);
		}else {//�Ӱ�ʱ�μ۸�����зּ���ռ��ҹ���շѲ���
			dayMap= priceList.get(0);
			boolean pm1 = false;//�ҵ�map1,�����ǽ���ʱ����ڿ�ʼʱ��
			boolean pm2 = false;//�ҵ�map2
			if(priceList.size()>1){
				for(Map map : priceList){
					if(pm1&&pm2)
						break;
					Integer btime = (Integer)map.get("b_time");
					Integer etime = (Integer)map.get("e_time");
					if(btime==null||etime==null)
						continue;
					if(etime>btime){
						if(!pm1){
							dayMap = map;
							pm1=true;
						}
					}else {
						if(!pm2){
							nigthMap=map;
							pm2=true;
						}
					}
				}
			}
		}
		double minPriceUnit = getminPriceUnit(comId);
		
		Map assistMap = daService.getMap("select * from price_assist_tb where comid = ? and type = ?", new Object[]{comId,0});
		
		Map<String, Object> orMap=CountPrice.getAccount((Long)orderMap.get("create_time"),ntime, dayMap, nigthMap,minPriceUnit,assistMap);
		orMap.put("orderid", orderMap.get("id"));
		orMap.put("uin", orderMap.get("uin"));
		String hascard = "1";//�Ƿ��г���
		String carNumber = (String)orderMap.get("car_number");
		if(carNumber==null||carNumber.toString().trim().equals("")){
			carNumber="���ƺ�δ֪";
			hascard = "0";
		}
		orMap.put("carnumber",carNumber);
		orMap.put("hascard", hascard);
		orMap.put("handcash", "0");
		orMap.put("car_type", orderMap.get("car_type"));
		logger.info("���㶩�������أ�"+orMap);
		return StringUtils.createJson(orMap);	
	}
	
	/**
	 * ֧�ֶ�۸����//20141118
	 * //V1115���ϰ汾ʵ�ְ��²�Ʒ����۸���Ե�֧��
	 * @param comId
	 * @param orderMap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String getOrderPrice(Long comId,Map orderMap) throws Exception{
		Long uin = (Long) orderMap.get("uin");
		Double pretotal = StringUtils.formatDouble(orderMap.get("total"));// Ԥ֧�����
		// Integer preState =(Integer)orderMap.get("pre_state");//Ԥ֧��״̬
		// ,1����Ԥ֧��,2�ȴ��������Ԥ֧��
		// System.err.println("Ԥ֧�� ��"+pretotal);
		Long ntime = System.currentTimeMillis() / 1000;
		Map<String, Object> orMap = new HashMap<String, Object>();
		Long btLong = (Long) orderMap.get("create_time");
		// if(ntime>btLong){
		//			
		// }else {
		// ntime = ntime +60;
		// }
		Integer cType = (Integer) orderMap.get("c_type");// ������ʽ
		// ��0:NFC,1:IBeacon,2:����
		// 3ͨ������ 4ֱ�� 5�¿��û�
		// 6��λ��ά��7�¿��ڶ�����
		orMap.put("ctype", cType);
		String btimestr = TimeTools.getTime_MMdd_HHmm(btLong * 1000);
		String etimestr = TimeTools.getTime_MMdd_HHmm(ntime * 1000);
		String btime = btimestr.substring(6);
		String etime = etimestr.substring(6);
		Long start = (Long) orderMap.get("create_time");
		Integer pid = (Integer) orderMap.get("pid");// �Ʒѷ�ʽ��0����(0.5/h)��1��ʱ��12Сʱ��10Ԫ����ÿСʱ1Ԫ��
		Integer type = (Integer) orderMap.get("type");
		Integer state = (Integer) orderMap.get("state");
		Long end = ntime;
		String hascard = "1";// �Ƿ��г���
		// ��ѯ���ƺ�
		String carNumber = (String) orderMap.get("car_number");
		if (carNumber == null || carNumber.toString().trim().equals("")) {
			carNumber = null;
			if (uin != null)
				carNumber = getCarNumber(uin);
			if (carNumber == null) {
				carNumber = "���ƺ�δ֪";
				hascard = "0";
			}
		}
		orMap.put("carnumber", carNumber);

		List<Map<String, Object>> cardList = daService.getAll(
				"select car_number from car_info_Tb where uin=? ",
				new Object[] { uin });
		if (cardList != null && cardList.size() > 0) {
			String cards = "";
			for (Map<String, Object> cMap : cardList) {
				cards += ",\"" + cMap.get("car_number") + "\"";
			}
			cards = cards.substring(1);
			orMap.put("cards", "[" + cards + "]");
		} else {
			orMap.put("cards", "[]");
		}
		Integer isfast = (Integer) orderMap.get("type");
		if (isfast != null && isfast == 2) {// �������������ɵĶ���,���ƺ�Ӧ��д����������
			String cardno = (String) orderMap.get("nfc_uuid");
			if (cardno != null && cardno.indexOf("_") != -1)
				orMap.put("carnumber", cardno
						.substring(cardno.indexOf("_") + 1));
		}
		orMap.put("hascard", hascard);
		orMap.put("handcash", "0");
		orMap.put("btime", btime);
		orMap.put("etime", etime);
		orMap.put("btimestr", btimestr);
		orMap.put("etimestr", etimestr);
		orMap.put("duration", StringUtils.getTimeString(start, end));
		orMap.put("orderid", orderMap.get("id"));
		// orMap.put("carnumber",orderMap.get("car_number")==null?"���ƺ�δ֪":
		// orderMap.get("car_number"));
		orMap.put("uin", orderMap.get("uin"));
		orMap.put("total", "0.00");
		orMap.put("collect", "0.00");
		orMap.put("handcash", "1");
		orMap.put("isedit", 0);
		orMap.put("car_type", orderMap.get("car_type"));
		orMap.put("prepay", pretotal);
		orMap.put("isfast", type);
		// String pid = CustomDefind.CUSTOMPARKIDS;

		if (pid != null && pid > -1) {// ���Ƽ۸����
			// orMap.put("collect0", getCustomPrice(start, end, pid));
			orMap.put("handcash", "0");
			// Long duration = (end-start)/60;//����
			// Long t = duration/15;
			// if(duration%15!=0)
			// t= t+1;
			orMap.put("collect", getCustomPrice(start, end, pid));
			// logger.error("���㶩�������أ�"+orMap);
			return StringUtils.createJson(orMap);
		}
		// ���ж��¿�
		if (uin != null && uin != -1 && cType == 5) {
			Map<String, Object> pMap = daService
					.getMap(
							"select c.e_time limitday from product_package_tb p,"
									+ "carower_product c where c.pid=p.id and p.comid=? and c.uin=? and c.e_time>? order by c.id desc limit ?",
							new Object[] { comId, uin, ntime, 1 });
			if (pMap != null && !pMap.isEmpty()) {
				// System.out.println(pMap);
				Long limitDay = (Long) pMap.get("limitday");
				Long day = (limitDay - ntime) / (24 * 60 * 60) + 1;
				orMap.put("limitday", day + "");
				orMap.put("handcash", "2");
				logger.error("���㶩�������أ�" + orMap);
				return StringUtils.createJson(orMap);
			}
		}
		Integer car_type = (Integer) orderMap.get("car_type");
		if (car_type == 0) {// 0:ͨ��
			Long count = daService
					.getLong(
							"select count(*) from com_info_tb where id=? and car_type=?",
							new Object[] { comId, 1 });
			if (count > 0) {// ���ִ�С��
				car_type = 1;// Ĭ�ϳ�С���ƷѲ���
			}
		}
		Map dayMap = null;// �ռ����
		Map nigthMap = null;// ҹ�����
		// ��ʱ�μ۸����
		List<Map<String, Object>> priceList1 = daService
				.getAll(
						"select * from price_tb where comid=? "
								+ "and state=? and pay_type=? and car_type=? order by id desc",
						new Object[] { comId, 0, 0, car_type });
		// �鰴�β���
		List<Map<String, Object>> priceList2 = daService
				.getAll(
						"select * from price_tb where comid=? "
								+ "and state=? and pay_type=? and car_type=? order by id desc",
						new Object[] { comId, 0, 1, car_type });
		// boolean isHasTimePrice=false;//�Ƿ��а��μ۸�
		if (priceList2 != null && !priceList2.isEmpty()) {// ���β���
			int i = 0;
			String total0 = "";
			String total1 = "[";
			for (Map<String, Object> timeMap : priceList2) {
				Object ounit = timeMap.get("unit");
				String total = timeMap.get("price") + "";
				if (ounit != null) {
					Integer unit = Integer.valueOf(ounit.toString());
					if (unit > 0) {
						Long du = (end - start) / 60;// ʱ����
						int times = du.intValue() / unit;
						if (du % unit != 0)
							times += 1;
						total = StringUtils.formatDouble(times
								* Double.valueOf(timeMap.get("price") + ""))
								+ "";
					}
				}
				if (i == 0) {
					total0 = total;
					total1 += total;
				} else {
					total1 += "," + total;
				}
				i++;
			}
			total1 += "]";
			orMap.put("collect0", total0);
			orMap.put("collect1", total1);
			orMap.put("handcash", "0");
			// isHasTimePrice = true;
		}
		boolean isHasDatePrice = false;// �Ƿ��а�ʱ�μ۸�
		if (priceList1 != null && !priceList1.isEmpty()) {// �Ӱ�ʱ�μ۸�����зּ���ռ��ҹ���շѲ���
			dayMap = priceList1.get(0);
			boolean pm1 = false;// �ҵ�map1,�����ǽ���ʱ����ڿ�ʼʱ��
			boolean pm2 = false;// �ҵ�map2
			Integer isEdit = 0;// �Ƿ�ɱ༭�۸�Ŀǰֻ���ռ䰴ʱ�۸���Ч,0��1�ǣ�Ĭ��0
			if (priceList1.size() > 1) {
				for (Map map : priceList1) {
					if (pm1 && pm2)
						break;
					Integer pbtime = (Integer) map.get("b_time");
					Integer petime = (Integer) map.get("e_time");
					if (btime == null || etime == null)
						continue;
					if (petime > pbtime) {
						if (!pm1) {
							dayMap = map;
							isEdit = (Integer) map.get("isedit");
							pm1 = true;
						}
					} else {
						if (!pm2) {
							nigthMap = map;
							pm2 = true;
						}
					}
				}
			}
			double minPriceUnit = getminPriceUnit(comId);
			Long end_time = ntime;
			if (state == 1) {
				end_time = (Long) orderMap.get("end_time");
			}
			Map assistMap = daService
					.getMap(
							"select * from price_assist_tb where comid = ? and type = ?",
							new Object[] { comId, 0 });
			Map<String, Object> oMap = null;
			if ((uin != null && uin != -1 && (cType == 8||cType==5))) {
				String sqlparm = "";
				List comsList = daService.getAll("select * from com_info_tb where pid = ?",new Object[]{comId});
				Long parcomid = daService.getLong("select pid from com_info_tb where id = ?", new Object[]{comId});
				String sql = "";
				Object[] parm = null;
				int j=0;
				if(parcomid!=null&&parcomid>0){
					parm = new Object[comsList.size()+6];
					parm[0] = parcomid;
					sql += " or p.comid = ? ";
					j=1;
				}else{
					parm = new Object[comsList.size()+5];
				}
				parm[0+j] = comId;
				for (int i = 1; i < comsList.size()+1; i++) {
					long comidoth = Long.parseLong(((Map)comsList.get(i-1)).get("id")+"");
					parm[i+j] = comidoth;
					sql += " or p.comid = ? ";
				}
				parm[comsList.size()+1+j] = uin;
				parm[comsList.size()+2+j] = 1;
				parm[comsList.size()+3+j] = ntime;
				parm[comsList.size()+4+j] = ntime;
				List<Map<String, Object>> list = daService
						.getAll("select p.b_time,p.e_time,p.bmin,p.emin,p.type,c.b_time bt,c.e_time et from product_package_tb p,"
										+ "carower_product c where c.pid=p.id and (p.comid=? "+sqlparm+")and c.uin=? and p.state<? and c.e_time>? and c.b_time<? order by c.id desc ",
								new Object[] { comId, uin, 1, ntime, ntime });
				if (list != null && list.size() > 0) {
					oMap = monthPrice(orderMap, list, end_time, dayMap,
							nigthMap, assistMap);
					double collect = Double.parseDouble(oMap.get("collect")+"");
					if(minPriceUnit!=0.00){
						collect = CountPrice.dealPrice(collect,minPriceUnit);
						oMap.put("collect",collect);
					}
				} else {
					oMap = CountPrice.getAccount((Long) orderMap.get("create_time"), end_time, dayMap, nigthMap,minPriceUnit, assistMap);
				}
			} else {
				oMap = CountPrice.getAccount(
						(Long) orderMap.get("create_time"), end_time, dayMap,
						nigthMap, minPriceUnit, assistMap);
			}
			// orMap.put("total", oMap.get("total"));
			// if(isHasTimePrice){
			// orMap.put("collect0", orMap.get("collect"));
			// }else {
			// }
			orMap.put("collect", oMap.get("collect"));
			orMap.put("isedit", isEdit);
			orMap.put("handcash", "0");
			isHasDatePrice = true;
		}

		if (!isHasDatePrice) {// û�а�ʱ�μ۸�
			orMap.put("collect", orMap.get("collect0"));
			orMap.remove("collect0");
		}

		// orMap.put("prestate", preState);

		// logger.error("���㶩�������أ�"+orMap);
		return StringUtils.createJson(orMap);
	}
	//����lala������ô˷��������Ա���һ���������ζ�����ɼ�
	/*public  boolean isCanLaLa(Integer number,Long uin,Long time) throws Exception{
		//logger.info("lala scroe ---uin:"+uin+",sharenumber:"+number+",time:"+TimeTools.getTime_yyyyMMdd_HHmmss(time*1000));
		Map<Long, Long> lalaMap = memcacheUtils.doMapLongLongCache("zld_lala_time_cache",null, null);
		String lastDate = "";
		boolean isLalaScore=true;
		if(lalaMap!=null){
			Long lastTime = lalaMap.get(uin);
			//logger.info("lala scroe ---uin:"+uin+",sharenumber:"+number+",cache time:"+lastTime);
			if(lastTime!=null){
				lastDate=TimeTools.getTime_yyyyMMdd_HHmmss(lastTime*1000);
				if(time<lastTime+15*60){
					isLalaScore=false;
					ParkingMap.setLastLalaTime(uin, lastTime);//ͬ��ʱ�䵽���ػ���
				}
			}
		}else {
//				logger.info("error, no memcached ������please check memcached ip config........");
			lalaMap=new HashMap<Long, Long>();
		}
		if(isLalaScore){
			lalaMap.put(uin, time);
			ParkingMap.setLastLalaTime(uin, time);//ͬ��ʱ�䵽���ػ���
			memcacheUtils.doMapLongLongCache("zld_lala_time_cache", lalaMap, "update");
		}
		logger.info("lala scroe ---return :"+isLalaScore+"---uin:"+uin+",sharenumber:"+number+",time:"+TimeTools.getTime_yyyyMMdd_HHmmss(time*1000)+",lastTime:"+lastDate);
		return isLalaScore;
	}*/
	/**
	 * �ӻ�����ȡ��ͨ���û�
	 * @param uuid
	 * @return
	 */
//	public Long getUinByUUID(String uuid){
//		Long uin = memcacheUtils.getUinUuid(uuid);
//		if(uin!=null&&uin==-1){//δ��ʼ������ 
//			logger.info("��ʼ����ͨ���û�.....");
//			List<Map<String, Object>> list = daService.getAll("select nfc_uuid,uin from com_nfc_tb where uin>?",new Object[]{0});
//			logger.info(">>>>>>>>>>>>>>>��ʼ����NFC�û�����"+list.size());
//			Map<String, Long> uinUuidMap = new HashMap<String, Long>();
//			if(list!=null&&list.size()>0){
//				for(Map<String, Object> map : list){
//					uinUuidMap.put(map.get("nfc_uuid")+"",(Long)map.get("uin"));
//				}
//				uin = uinUuidMap.get(uuid);
//				logger.info("������ͨ���û�.....size:"+uinUuidMap.size());
//				memcacheUtils.setUinUuid(uinUuidMap);
//			}
//		}
//		return uin;
//	}
	/**
	 * ������ͨ������ 
	 * @param uuid
	 * @param uin
	 */
	public void updateUinUuidMap(String uuid,Long uin){
//		Map<String,Long> uuidUinMap = memcacheUtils.doUinUuidCache("uuid_uin_map", null, null);
//		if(uuidUinMap!=null){
//			logger.info("������ͨ������ ...");
//			uuidUinMap.put(uuid, uin);
//			memcacheUtils.setUinUuid(uuidUinMap);
//		}else {
			logger.info("��ʼ����ͨ���û�.....");
			List<Map<String, Object>> list = daService.getAll("select nfc_uuid,uin from com_nfc_tb where uin>?",new Object[]{0});
			logger.info(">>>>>>>>>>>>>>>��ʼ����NFC�û�����"+list.size());
			Map<String, Long> uinUuidMap = new HashMap<String, Long>();
			if(list!=null&&list.size()>0){
				for(Map<String, Object> map : list){
					uinUuidMap.put(map.get("nfc_uuid")+"",(Long)map.get("uin"));
				}
				//uinUuidMap.put(uuid, uin);
				logger.info("������ͨ���û�.....size:"+uinUuidMap.size());
//				memcacheUtils.setUinUuid(uinUuidMap);
			}
//		}
	}
	
	public int backNewUserTickets(Long ntime,Long key){
		return 0;//2015-03-10������������ʱ������д��ͣ��ȯ����¼ʱ�жϺ����������ͣ��ȯ
	}
	
	//��ȡ��ɫ�Ĺ���Ȩ��
	public List<Object> getAuthByRole(Long roleid) throws JSONException{
		String auth = "[]";
		List<Object> authids = new ArrayList<Object>();
		Map<String, Object> map = daService.getMap("select * from role_auth_tb where role_id=? ", new Object[]{roleid});
		if(map != null){
			auth = (String) map.get("auth");
		}
		JSONArray jsonArray = new JSONArray(auth);
		for(int i=0;i<jsonArray.length();i++){
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Long nid = jsonObject.getLong("nid");
			Long pId = jsonObject.getLong("pid");
			Map<String, Object> map2 = daService.getMap("select id from auth_tb where nid=? and pid=? ", new Object[]{nid,pId});
			if(map2 != null){
				authids.add(map2.get("id"));
			}
		}
		return authids;
	}
	
	//��ȡ����Ȩ��
	public List<Object> getDataAuth(Long id){
		List<Object> params = new ArrayList<Object>();
		params.add(id);//�Լ�
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		list = daService.getAll("select authorizer from dataauth_tb where authorizee=? order by authorizer desc ", new Object[]{id});
		for(Map<String, Object> map : list){
			Long authorizer = (Long)map.get("authorizer");
			if(!params.contains(authorizer)){
				params.add(authorizer);
			}
		}
		return params;
	}
	/**
	 * ���ݳ���,������Ų�ѯ�Ƿ����¿�
	 * @param carNumber
	 * @return
	 */
//	public boolean isMonthUser(Long uin,Long comId){
//		//���ж��¿�
//	/*	if(carNumber==null||"".equals(carNumber))
//			return false;
//		Long uin  = null;
//		
//		Map carMap = daService.getMap("select uin from car_info_tb where car_number=?", new Object[]{carNumber});
//		
//		if(carMap==null||carMap.get("uin")==null){
//			return false;
//		}
//		uin=(Long) carMap.get("uin");*/
//		if(uin!=null&&uin!=-1){
//			Long ntime = System.currentTimeMillis()/1000;
//			Map<String, Object> pMap = daService.getMap("select p.b_time,p.e_time,p.type from product_package_tb p," +
//					"carower_product c where c.pid=p.id and p.comid=? and c.uin=? and c.e_time>? and c.b_time<? order by c.id desc limit ?", 
//					new Object[]{comId,uin,ntime,ntime,1});
//			if(pMap!=null&&!pMap.isEmpty()){
//				//System.out.println(pMap);
//				Integer b_time = (Integer)pMap.get("b_time");
//				Integer e_time = (Integer)pMap.get("e_time");
//				Calendar c = Calendar.getInstance();
//				Integer hour = c.get(Calendar.HOUR_OF_DAY);
//				Integer type = (Integer)pMap.get("type");//0:ȫ�� 1ҹ�� 2�ռ�
//				boolean isVip = false;
//				if(type==0){//0:ȫ�� 1ҹ�� 2�ռ�
//					logger.info("ȫ������û���uin��"+uin);
//					isVip = true;
//				}else if(type==2){//0:ȫ�� 1ҹ�� 2�ռ�
//					if(hour>=b_time&&hour<=e_time){
//						logger.info("�ռ�����û���uin��"+uin);
//						isVip = true;
//					}
//				}else if(type==1){//0:ȫ�� 1ҹ�� 2�ռ�
//					if(hour<=e_time||hour>=b_time){
//						logger.info("ҹ������û���uin��"+uin);
//						isVip = true;
//					}
//				}
//				return isVip;
//			}
//		}
//		return false;
//	}
	public List isMonthUser(Long uin,Long comId){
		Map<String, Object> rMap = new HashMap<String, Object>();
		Integer count = 0;
		ArrayList rList = new ArrayList();
		if (uin != null && uin != -1) {
			Long ntime = System.currentTimeMillis() / 1000;
			// 20160303�����ײ�״̬ ���õĻ��Ͳ����¿�
//			List<Map<String, Object>> list = daService
//					.getAll(
//							"select p.b_time,p.e_time,p.type from product_package_tb p,"
//									+ "carower_product c where p.state<? and c.pid=p.id and p.comid=? and c.uin=? and c.e_time>? and c.b_time<? order by c.id desc ",
//							new Object[] { 1, comId, uin, ntime, ntime });
			// ArrayList<Integer> arrayList = new ArrayList<Integer>();
			String sql = "select p.b_time,p.e_time,c.e_time etime, c.b_time btime,p.type,p.scope,p.comid from product_package_tb p,"
					+ "carower_product c where c.pid=p.id and (p.comid=? ";
			List comsList = daService.getAll("select * from com_info_tb where pid = ?",new Object[]{comId});
			Long parcomid = daService.getLong("select pid from com_info_tb where id = ?",new Object[]{comId});

			Object[] parm = null;
			int j=0;
			if(parcomid!=null&&parcomid>0){
				parm = new Object[comsList.size()+4];
				parm[0] = parcomid;
				sql += " or p.comid = ? ";
				j=1;
			}else{
				parm = new Object[comsList.size()+3];
			}
			parm[0+j] = comId;
			for (int i = 1; i < comsList.size()+1; i++) {
				long comidoth = Long.parseLong(((Map)comsList.get(i-1)).get("id")+"");
				parm[i+j] = comidoth;
				sql += " or p.comid = ? ";
			}
			parm[comsList.size()+1+j] = 1;
			parm[comsList.size()+2+j] = uin;
			List<Map<String, Object>> list = daService.getAll(sql+")and p.state<?  and c.uin=? order by c.id desc ",
					parm);
			logger.error(sql+")and p.state<?  and c.uin=? order by c.id desc "+","+parm);
			// ArrayList<Integer> arrayList = new ArrayList<Integer>();
			Map<Integer, Integer> map = new HashMap<Integer, Integer>();
			if (list != null && !list.isEmpty()) {
				for (Map<String, Object> pMap : list) {
					// System.out.println(pMap);
					Long scope = (Long) pMap.get("scope");
					long comid = Long.parseLong(pMap.get("comid")+"");
					if(scope==0&&comid!=comId.longValue()){
						continue;
					}
					if(scope==0&&comid==parcomid.longValue()){
						continue;
					}
					Integer b_time = (Integer) pMap.get("b_time");
					Integer e_time = (Integer) pMap.get("e_time");
					Calendar c = Calendar.getInstance();
					Integer hour = c.get(Calendar.HOUR_OF_DAY);
					Integer type = (Integer) pMap.get("type");// 0:ȫ�� 1ҹ�� 2�ռ�
					Long btime = (Long) pMap.get("btime");
					Long etime = (Long) pMap.get("etime");
					if(ntime<btime||ntime>etime){
						if (map.containsKey(3)) {
							Integer value = map.get(3) + 1;
							map.put(3, value);
						} else {
							map.put(3, 1);
						}
						continue;
					}
					boolean isVip = false;
					if (type == 0) {// 0:ȫ�� 1ҹ�� 2�ռ�
						logger.error("ȫ������û���uin��" + uin);
						isVip = true;
						if (map.containsKey(0)) {
							Integer value = map.get(0) + 1;
							map.put(0, value);
						} else {
							map.put(0, 1);
						}
					} else if (type == 2) {// 0:ȫ�� 1ҹ�� 2�ռ�
						// if(hour>=b_time&&hour<=e_time){
						logger.error("�ռ�����û���uin��" + uin);
						isVip = true;
						if (map.containsKey(2)) {
							Integer value = map.get(2) + 1;
							map.put(2, value);
						} else {
							map.put(2, 1);
						}
						// }
					} else if (type == 1) {// 0:ȫ�� 1ҹ�� 2�ռ�
						// if(hour<=e_time||hour>=b_time){
						logger.error("ҹ������û���uin��" + uin);
						isVip = true;
						if (map.containsKey(1)) {
							Integer value = map.get(1) + 1;
							map.put(1, value);
						} else {
							map.put(1, 1);
						}
						// }
					}

					if (isVip) {
						count++;
					}
				}
			}
			rList.add(map);
			rList.add(count);
		}
		logger.error("check monthusers>>>uin:" + uin + ",count:" + count);
		return rList;
	}
	
	/**
	 * ȡ����ͣ��ȯ��δ��֤�������ʹ��3Ԫȯ�� ͣ����ר��ͣ��ȯ����
	 * @param uin
	 * @param fee
	 * @param type: //0����汾�������ȶ��������ȯ��1�ϰ汾���Զ�ѡȯʱ��������ͨȯ����ߵֿ۽�2�°汾������ȯ���ͷ�����ߵֿ۽��
	 * @param comId
	 * @return
	 */
	
	//http://127.0.0.1/zld/carowner.do?action=getaccount&mobile=13641309140&total=10&uid=21694&utype=1
	public  Map<String, Object> getTickets(Long uin,Double fee,Long comId,Integer type,Long uid){
		Map<String, Object> map=null;
		Integer limit = CustomDefind.getUseMoney(fee,0);
		Double splimit = StringUtils.formatDouble(CustomDefind.getValue("TICKET_LIMIT"));
		if(type==0){//0����汾�������ȶ��������ȯ��1�ϰ汾���Զ�ѡȯʱ��������ͨȯ����ߵֿ۽�2�°汾������ȯ���ͷ�����ߵֿ۽��
			 logger.info("uin:"+uin+",comid:"+comId+",type:"+type+",fee:"+fee);
//			 map= useTickets(uin, fee, comId,uid,type);
		}else {
			logger.info("uin:"+uin+",comid:"+comId+",type:"+type+",fee:"+fee+",uselimit:"+limit);
			map = methods.getTickets(uin,fee,comId,uid);
			if(map!=null){
				Integer ttype = (Integer)map.get("type");
				Integer res = (Integer)map.get("resources");
				if(ttype==1||res==1)//��ͨȯ
					map.put("limit",StringUtils.formatDouble(fee-splimit));
				else {//����ר��ȯ
					map.put("limit",limit);
				}
			}
		}
		logger.info("uin:"+uin+",comid:"+comId+",fee:"+fee+",map:"+map);
		if(map!=null){//��һ����û����ͬ���ĳ���ר��ȯ
			Integer money = (Integer)map.get("money");
			Long limitday=(Long)map.get("limit_day");
			Integer ttype = (Integer)map.get("type");
			Integer res = (Integer)map.get("resources");
			if(ttype!=1&&res!=1){
				Map<String, Object> map1 = daService.getMap("select * from ticket_tb where comid=? and state=? and uin=? and  money=? and type=? and limit_day >=?  ",
						new Object[]{comId,0,uin,money,1,limitday});
				logger.info("uin:"+uin+",comid:"+comId+",fee:"+fee+",map1:"+map1);
				if(map1!=null&&!map1.isEmpty()){
					if(type==1)
						map1.put("limit",StringUtils.formatDouble(fee-splimit));
					return map1;
				}
			}
		}
		return map;
	}
	
	/**
	 * ȡ����ͣ��ȯ
	 * @param uin    �����˻�
	 * @param total  �������
	 * @param comId  ������� 
	 * @param uid    �շ�Ա���
	 * @param utype  0����汾�������ȶ��������ȯ��1�ϰ汾���Զ�ѡȯʱ��������ͨȯ����ߵֿ۽�2�°汾������ȯ���ͷ�����ߵֿ۽��
	 * @return   ����ͣ��ȯ
	 */
//	public Map<String, Object> useTickets(Long uin,Double total,Long comId,Long uid,Integer utype){
//		//������п��õ�ȯ
//		//Long ntime = System.currentTimeMillis()/1000;
//		Integer limit = CustomDefind.getUseMoney(total,0);
////		boolean blackuser = isBlackUser(uin);
//		boolean blackparkuser = isBlackParkUser(comId, false);
//		boolean isauth = isAuthUser(uin);
//		if(!isauth){
//			if(blackuser||blackparkuser){
//				if(blackuser){
//					logger.info("�����ں�������uin:"+uin+",fee:"+total+",comid:"+comId);
//				}
//				if(blackparkuser){
//					logger.info("�����ں�������uin:"+uin+",fee:"+total+",comid:"+comId);
//				}
//				return null;
//			}
//		}else{
//			logger.info("����uin:"+uin+"����֤��������ȯ���ж��Ƿ��Ǻ������������Ƿ��������");
//		}
//		double ticketquota=-1;
//		if(uid!=-1){
//			Map usrMap =daService.getMap("select ticketquota from user_info_Tb where id =? and ticketquota<>?", new Object[]{uid,-1});
//			if(usrMap!=null){
//				ticketquota = Double.parseDouble(usrMap.get("ticketquota")+"");
//				logger.info("���շ�Ա:"+uid+"����ȯ����ǣ�"+ticketquota+"��(-1����û����)");
//			}
//		}
//		//���п���ͣ��ȯ
//		List<Map<String,Object>> allTickets =methods.getUseTickets(uin, total);
//		Map<String, Object> ticketMap=null;
//		logger.info(allTickets);
//		if(allTickets!=null&&!allTickets.isEmpty()){
//			double spr_abs = 100;             //ר��ȯ�ֿ۽����֧�����Ĳ�ֵ
//			Integer spr_money_limit_abs=100;  //ר��ȯ�ֿ۽����ȯ���Ĳ�ֵ
//			double comm_abs = 100;            //��ͨȯ�ֿ۽����ȯ�����Ĳ�ֵ
//			Integer comm_money_limit_abs=100; //��ͨȯ�ֿ۽����ȯ���Ĳ�ֵ
//			double buy_abs = 100;             //����ȯ�ֿ۽����֧�����Ĳ�ֵ
//			Integer buy_money_limit_abs=100;  //����ȯ�ֿ۽����ȯ���Ĳ�ֵ
//			Integer comm_index=-1;  //��ͨȯ����      
//			Integer spr_index=-1;  //ר��ȯ����
//			Integer buy_index=-1;  //��ȯ����
//			Integer comm_money=0;  //��ͨȯ�ֿ۽��
//			Integer spr_money=0;   //ר��ȯ�ֿ۽��
//			Integer buy_money=0;   //��ȯȯ�ֿ۽��
//			Integer index=-1;      //��������
//			Integer spr_ticket_money=0;  //ר��ȯ���
//			Integer buy_ticket_money=0;  //��ȯȯ���
//			Integer comm_ticket_money=0; //��ͨȯ���
//			for(Map<String,Object> map: allTickets){
//				Long cid = (Long)map.get("comid");//��˾���
//				Integer money = (Integer)map.get("money");
//				Integer type=(Integer)map.get("type");
//				Integer useLimit = (Integer)map.get("limit");
//				Integer res = (Integer)map.get("resources");
//				index ++;
//				if(utype==0&&money>=limit){//0����汾�������ֿ۽��ȶ��������ȯ
//					continue;
//				}
//				if(type==1&&cid!=null&&!cid.equals(comId)){//�Ǵ˳�����ר��ȯ����
//					continue;
//				}
//				if(ticketquota!=-1&&ticketquota>money){//ȯ������շ�Ա��ȯ��߽�������
//					continue;
//				}
//				if(!isauth&&money>1){//����֤��������ʹ�ô���1Ԫ���ϵ�ȯ
//					continue;
//				}
//				if(useLimit==0){//�ֿ۽��Ϊ0
//					continue;
//				}
//				if(money>Math.ceil(total)&&res==1){//�����ȯ����ڶ������
//					continue;
//				}
//				if(type==1){//ר��ȯ��ȡ��С֧�������ֿ۽����
//					double abs = total-useLimit;   
//					Integer mlabs = money-useLimit;
//					if(spr_abs>abs){
//						spr_abs =abs;
//						spr_money_limit_abs=mlabs;
//						spr_index=index;  //��������
//						spr_money=useLimit; //����ֿ۽��
//						spr_ticket_money=money;//����ȯ���
//					}else if(spr_abs==abs&&spr_money_limit_abs>mlabs){//��ǰ֧�������ֿ۽���ֵ����һ��ȯһ��ʱ��ȡȯ�����ֿ۽���ֵ��С��
//						spr_index=index;
//						spr_money=useLimit;
//						spr_ticket_money=money;
//					}
//				}else {
//					if(res==1){//����ȯ
//						double abs = total-useLimit;
//						Integer mlabs = money-useLimit;
//						if(buy_abs>abs){
//							buy_abs =abs;
//							buy_money_limit_abs=mlabs;
//							buy_index=index;
//							buy_money=useLimit;
//							buy_ticket_money=money;
//						}else if(buy_abs==abs&&buy_money_limit_abs>mlabs){
//							buy_index=index;
//							buy_money=useLimit;
//							buy_ticket_money=money;
//						}
//						map.put("isbuy", "1");
//					}else {//��ͨȯ
//						double abs = total-useLimit;
//						Integer mlabs = money-useLimit;
//						if(comm_abs>abs){
//							comm_abs =abs;
//							comm_money_limit_abs=mlabs;
//							comm_index=index;
//							comm_money=useLimit;
//							comm_ticket_money=money;
//						}else if(comm_abs==abs&&comm_money_limit_abs>mlabs){
//							comm_index=index;
//							comm_money=useLimit;
//							comm_ticket_money=money;
//						}
//					}
//				}
//			}
//			logger.info(spr_index+":"+spr_money+":"+spr_ticket_money+","+buy_index+":"+buy_money+":"+
//						buy_ticket_money+","+comm_index+":"+comm_money+":"+comm_ticket_money);
//			if(spr_money>=comm_money&&spr_money>=buy_money){//���ݵֿ۽�ѡ���ģ�����ѡר��ȯ
//				if(spr_money==buy_money){//ר��ȯ�͹���ȯ�ֿ۽����ͬʱ��ѡȯ����С��
//					if(spr_ticket_money>buy_ticket_money){
//						ticketMap=allTickets.get(buy_index);
//					}
//				}
//				if(spr_money==comm_money){//ר��ȯ����ͨȯ�ֿ۽����ͬʱ��ѡȯ����С��
//					if(spr_ticket_money>comm_ticket_money){
//						ticketMap=allTickets.get(comm_index);
//					}
//				}
//				if(ticketMap==null&&spr_index>-1){
//					ticketMap=allTickets.get(spr_index);
//				}
//				if(utype<2&&ticketMap!=null)//�ϰ汾������ͨȯ�ĵֿ����ޣ���ֹ֧��ʧ��
//					ticketMap.put("limit", limit<1?1:limit);
//			}else if(comm_money>=buy_money&&comm_index>-1){//���ݵֿ۽�ѡ���ģ�û��ר��ȯʱ����ѡ��ͨȯ
//				if(buy_money==comm_money){
//					if(comm_ticket_money>buy_ticket_money){
//						ticketMap=allTickets.get(buy_index);
//					}
//				}else {
//					ticketMap=allTickets.get(comm_index);
//				}
//				if(utype<2&&ticketMap!=null)
//					ticketMap.put("limit", limit<1?1:limit);
//			}else if(buy_index!=-1){
//				ticketMap=allTickets.get(buy_index);
//				if(utype<2&&ticketMap!=null)
//					ticketMap.put("limit", limit<1?1:limit);
//			}
//			logger.info("uin:"+uin+",total:"+total+",comid:"+comId+",uid:"+uid+",utype:"+utype+"ѡȯ�����"+ticketMap);
//		}
//		return ticketMap;
//	}
	/**
	 * ȡ����ͣ��ȯ��δ��֤�������ʹ��3Ԫȯ��
	 * @param uin
	 * @param fee
	 * @return
	 */
	/*@SuppressWarnings("unchecked")
	public Map<String, Object> useTickets(Long uin,Double fee,Long comId,Long uid){
		//������п��õ�ȯ
		//Long ntime = System.currentTimeMillis()/1000;
		Integer limit = CustomDefind.getUseMoney(fee,0);
		Double splimit = StringUtils.formatDouble(CustomDefind.getValue("TICKET_LIMIT"));
		boolean blackuser = isBlackUser(uin);
		boolean blackparkuser = isBlackParkUser(comId, false);
		boolean isauth = isAuthUser(uin);
		if(!isauth){
			if(blackuser||blackparkuser){
				if(blackuser){
					logger.info("�����ں�������uin:"+uin+",fee:"+fee+",comid:"+comId);
				}
				if(blackparkuser){
					logger.info("�����ں�������uin:"+uin+",fee:"+fee+",comid:"+comId);
				}
				return null;
			}
		}else{
			logger.info("����uin:"+uin+"����֤��������ȯ���ж��Ƿ��Ǻ������������Ƿ��������");
		}
		List<Map<String, Object>> list = null;
		double ticketquota=-1;
		if(uid!=-1){
			Map usrMap =daService.getMap("select ticketquota from user_info_Tb where id =? and ticketquota<>?", new Object[]{uid,-1});
			if(usrMap!=null){
				ticketquota = Double.parseDouble(usrMap.get("ticketquota")+"");
				logger.info("���շ�Ա:"+uid+"����ȯ����ǣ�"+ticketquota+"��(-1����û����)");
			}
		}
		if(!isauth){//δ��֤�������ʹ��2Ԫȯ��
			double noAuth = 1.0;//δ��֤�����������noAuth(2)Ԫȯ,�Ժ�Ķ����ֵ��ok
			if(ticketquota>=0&&ticketquota<=noAuth){
//				ticketquota = ticketquota+1;
			}else{
				ticketquota=noAuth;//δ��֤�������ʹ��2Ԫȯ
			}
			list=	daService.getAll("select * from ticket_tb where uin = ? " +
					"and state=? and limit_day>=? and type<? and money<?  order by limit_day",
					new Object[]{uin,0,TimeTools.getToDayBeginTime(),2,ticketquota+1});
		}else {
			list  = daService.getAll("select * from ticket_tb where uin = ? " +
					"and state=? and limit_day>=? and type<? order by limit_day",
					new Object[]{uin,0,TimeTools.getToDayBeginTime(),2});
		}
		logger.info("uin:"+uin+",fee:"+fee+",comid:"+comId+",today:"+TimeTools.getToDayBeginTime());
		if(list!=null&&!list.isEmpty()){
			List<String> _over3day_moneys = new ArrayList<String>();
			int i=0;
			for(Map<String, Object> map : list){
				Double money = Double.valueOf(map.get("money")+"");
				//Long limit_day = (Long)map.get("limit_day");
				Long tcomid = (Long)map.get("comid");
				Integer type = (Integer)map.get("type");
//				logger.info("ticket>>>uin:"+uin+",comId:"+comId+",tcomid:"+tcomid+",type:"+type+",ticketid:"+map.get("id"));
				if(comId!=null&&comId!=-1&&tcomid!=null&&type == 1){
					if(comId.intValue()!=tcomid.intValue()){
						logger.info(">>>>get ticket:�������������ͣ��ȯ��������....comId:"+comId+",tcomid:"+tcomid+",uin:"+uin);
						i++;
						continue;
					}
				}
				Integer res = (Integer)map.get("resources");
				if(limit==0&&res==0&&type==0){//֧�����С��3Ԫ��������ͨȯ
					i++;					
					continue;
				}
				if(type==1||res==1){
					limit=Double.valueOf((fee-splimit)).intValue();
				}else {
					limit= CustomDefind.getUseMoney(fee,0);
				}
				map.put("isbuy", res);
				if(money>limit.intValue()){
					i++;
					continue;
				}else if(limit.intValue()==money){//ȯֵ+1Ԫ ���� ֧�����ʱֱ�ӷ���
					return map;
				}
				//�ж� �Ƿ� �� ���Ǹó�����ר��ȯ
				
				map.remove("comid");
				//map.remove("limit_day");
				_over3day_moneys.add(i+"_"+Math.abs(limit-money));
				i++;
			}
			if(_over3day_moneys.size()>0){//3������ͣ��ȯ��ͣ���ѵľ���ֵ���� ��ȡ����ֵ��С��
				int sk = 0;//����index
				double sv=0;//������Сֵ
				int index = 0;
				for(String s : _over3day_moneys){
					int k = Integer.valueOf(s.split("_")[0]);
					double v = Double.valueOf(s.split("_")[1]);
					if(index==0){
						sk=k;
						sv = v;
					}else {
						if(sv>v){
							sk=k;
							sv = v;
						}
					}
					index++;
				}
				logger.info("uin:"+uin+",comid:"+comId+",sk:"+sk);
				return list.get(sk);
			}
		}else{
			logger.info("δѡ��ȯuin:"+uin+",comid:"+comId+",fee:"+fee);
		}
		return null;
	}*/
	//������ƴ�������
//	public void backTicket(double money,Long orderId,Long uin,Long comid,String openid){
//		Long ctime = System.currentTimeMillis()/1000;
//		//����ר��ȯ
//		Map btcomMap = daService.getMap("select * from park_ticket_tb where comid=? ", new Object[]{comid});
//		if(money>=1&&btcomMap!=null){
//			logger.info(">>>>back ticket comid="+comid+",��ר��ͣ��ȯ");
//			Integer num = (Integer)btcomMap.get("tnumber");
//			Integer exptime = (Integer)btcomMap.get("exptime");
//			Integer haveget = (Integer)btcomMap.get("haveget");
//			Long btid = (Long)btcomMap.get("id");
//			Double amount =StringUtils.formatDouble(btcomMap.get("money"));
//			if(haveget<num){//��������
//				int ret = daService.update("update park_ticket_tb set haveget=? where id = ? and tnumber>=? ",  new Object[]{haveget+1,btid,haveget+1});
//				if(ret==1){
//					ret = daService.update( "insert into ticket_tb (create_time,limit_day,money,state,uin,comid,type) values(?,?,?,?,?,?,?) ",
//							new Object[]{TimeTools.getToDayBeginTime(),TimeTools.getToDayBeginTime()+(exptime+1)*24*60*60-1,amount,0,uin,comid,1});
//					logger.info(">>>>back ticket comid="+comid+",ר��ͣ��ȯ����� ��"+amount+",������:"+num+",��ʹ��:"+(haveget+1)+"���û���"+uin+",��ȯ���:"+ret);
//				}
//			}
//		}
//		
//		if(money>=1&&memcacheUtils.readBackTicketCache(uin)){//һ��ֻ��һ�κ��
//			String sql = "insert into order_ticket_tb (uin,order_id,money,bnum,ctime,exptime,bwords) values(?,?,?,?,?,?,?)";
//			Object []values = null;
//			String content= CustomDefind.getValue("DESCRIPTION");
//			Long exptime = ctime + 24*60*60;
//			Long count = daService.getLong("select count(*) from user_account_tb where type=? and uin=? ", new Object[]{1, uin});
//			if(count == 1){//�����ױ�֧������������
//				values = new Object[]{uin,orderId,36,18,ctime,exptime,content};
//				logger.info(">>>>>�����ױ����ѣ���18�����36Ԫ...");
//			}else if(money>=1&&money<10){//�ֻ�֧������1ԪС��10Ԫ����3�������8Ԫ
//				values = new Object[]{uin,orderId,18,12,ctime,exptime,content};
//				logger.info(">>>>>������ƴ�������,����1ԪС��10Ԫ����3�������8Ԫ...");
//			}else if(money>=10){// �ֻ�֧������10Ԫ����8�������18Ԫ
//				values = new Object[]{uin,orderId,28,20,ctime,exptime,content};
//				logger.info(">>>>>������ƴ�������,����10Ԫ����8�������18Ԫ...");
//			}
//			else {
//				logger.info(">>>>>������ƴ�������,����һԪ��������.....");
//				return;
//			}
//			int ret = daService.update(sql, values);
//			logger.info(">>>>>������ƴ�������,money :"+money+" ret :"+ret);
//			if(ret==1)
//				memcacheUtils.updateBackTicketCache(uin);
//			/*if(openid==null){//�ͻ���֧������������Ԫͣ��ȯ��openidΪ��ʱ���ǿͻ���֧��
//				ret = daService.update( "insert into ticket_tb (create_time,limit_day,money,state,uin,type) values(?,?,?,?,?,?) ",
//						new Object[]{TimeTools.getToDayBeginTime(),TimeTools.getToDayBeginTime()+16*24*60*60-1,3,0,uin,0});
//				logger.info(">>>>back ticket �ͻ���֧������� ��3,�û���"+uin+",��ȯ���:"+ret);
//				logService.insertUserMesg(5, uin, "��ϲ�����һ����Ԫͣ��ȯ", "ͣ��ȯ����");
//			}*/
//		}else {
//			logger.info(">>>>>������ƴ�������,�Ѿ�������������.....");
//		}
//		
//	}
	
	/*
	 * ��ȡ΢�Ź��ںŵĻ���access_token
	 */
//	public String getWXPAccessToken(){
//		//String access_token ="notoken";// memcacheUtils.getWXPublicToken();
//		String access_token = memcacheUtils.getWXPublicToken();
//		if(access_token.equals("notoken")){
//			String url = Constants.WXPUBLIC_GETTOKEN_URL;
//			//��weixin�ӿ�ȡaccess_token
//			String result = new HttpProxy().doGet(url);
//			logger.info("wxpublic_access_token json:"+result);
////			access_token = JsonUtil.getJsonValue(result, "access_token");//result.substring(17,result.indexOf(",")-1);
//			logger.info("wxpublic_access_token:"+access_token);
//			//���浽���� 
////			memcacheUtils.setWXPublicToken(access_token);
//		}
//		logger.info("΢�Ź��ں�access_token��"+access_token);
//		return access_token;
//	}
	
	/*
	 * ��ȡ΢�Ź��ں�jsapi_ticket
	 */
//	public String getJsapi_ticket() throws JDOMException, IOException{
//		//String jsapi_ticket ="no_jsapi_ticket";// memcacheUtils.getJsapi_ticket();
//		String jsapi_ticket =memcacheUtils.getJsapi_ticket();
//		if(jsapi_ticket.equals("no_jsapi_ticket")){
//			String access_token = getWXPAccessToken();
//			String jsapi_ticket_url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+access_token+"&type=jsapi";
////			String result = CommonUtil.httpsRequest(jsapi_ticket_url, "GET", null);
////			net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(result);
////			jsapi_ticket = jsonObject.getString("ticket");
////			logger.info("wxpublic jsapi_ticket:"+jsapi_ticket);
////			//���浽����
////			memcacheUtils.setJsapi_ticket(jsapi_ticket);
//		}
//		return jsapi_ticket;
//	}
	
	/*
	 * ΢�Ź��ںŻ�ȡ����
	 */
//	public String getShortUrl(String longurl) throws JDOMException, IOException{
//		String short_url = null;
//		String access_token = getWXPAccessToken();
//		String params = "{\"action\":\"long2short\",\"long_url\":\""+longurl+"\"}";
//		String url = "https://api.weixin.qq.com/cgi-bin/shorturl?access_token="+access_token;
////		String result = CommonUtil.httpsRequest(url, "POST", params);
////		net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(result);
////		Integer errcode = (Integer)jsonObject.get("errcode");
////		if(errcode == 0){
////			short_url = (String)jsonObject.get("short_url");
////		}
//		return short_url;
//	}

//	public boolean isBlackUser(Long uin){
//		List<Long> blackUserList = memcacheUtils.doListLongCache("zld_black_users", null, null);
////		logger.info(">>>zld black users :"+blackUserList);
//		//�Ƿ��ں����� 
//		boolean isBlack = true;
//		if(blackUserList==null||!blackUserList.contains(uin))//���ں������п��Դ����Ƽ�����
//			isBlack=false;
////		if(blackUserList!=null&&blackUserList.size()>5)
////			clearBlackUser();
//		return isBlack;
//	}
	//�жϳ����Ƿ��ں������ڣ�uid���շ�Աʱ��isparkuser��true,uidΪ����ʱ��isparkuser��false
	public boolean isBlackParkUser(long uid,Boolean isparkuser){
		boolean isBlack = false;
		String parkback = CustomDefind.getValue("PARKBACK");
		if(StringUtils.isNotNull(parkback)){
			String []str = parkback.split(",");
			if(isparkuser){
				long count = 0;
				for (String string : str) {
					count += daService.getLong("select count(*) from user_info_tb where id=? and comid =?", new Object[]{uid,Long.parseLong(string)});
					if(count>0){
						isBlack=true;
						logger.info("�շ�Աuid:"+uid+"���ڵĳ����ں������ڣ����з���,�Ƽ���,����ȯȡ��");
						break;
					}
				}
			}else{
				for (String string : str) {
					if(Long.parseLong(string)==uid){
						isBlack=true;
						logger.info("����:"+uid+"�ں������ڣ����з���,�Ƽ���,����ȯȡ��");
						break;
					}
				}
			}
		}
		logger.info("�жϳ��������շ�Ա�����ڵĳ������Ƿ��ں������У�"+isBlack);
		return isBlack;
	}
	
	public boolean isAuthUser(long uin){
		Map userMap = daService.getMap("select is_auth from user_info_tb where id =? ", new Object[]{uin});
		Integer isAuth = 0;
		if(userMap!=null&&userMap.get("is_auth")!=null)
			isAuth=(Integer)userMap.get("is_auth");
		boolean ret = isAuth==1?true:false;
		logger.info("uin:"+uin+"�Ƿ�����֤�û�ret:"+ret+"(true:��֤�û���false:������֤�û�)");
		return ret;
	}
	
//	public void clearBlackUser(){
//		List<Long> blackUserList = memcacheUtils.doListLongCache("zld_black_users", null, null);
//		//logger.info(">>>zld black users :"+blackUserList);
//		if(blackUserList!=null){//���ں������п��Դ����Ƽ�����
//			blackUserList=new ArrayList<Long>();
//			memcacheUtils.doListLongCache("zld_black_users", blackUserList, "update");
//		}
//	}
	/**
	 * �ϴ���Ƭ
	 * @param request
	 * @param uin
	 * @return
	 * @throws Exception
	 */
	public String uploadPicToMongodb (HttpServletRequest request,Long uin,String table) throws Exception{
		logger.info(">>>>>begin upload order picture....");
		Map<String, String> extMap = new HashMap<String, String>();
	    extMap.put(".jpg", "image/jpeg");
	    extMap.put(".jpeg", "image/jpeg");
	    extMap.put(".png", "image/png");
	    extMap.put(".gif", "image/gif");
		request.setCharacterEncoding("UTF-8"); // ���ô�����������ı����ʽ
		DiskFileItemFactory  factory = new DiskFileItemFactory(); // ����FileItemFactory����
		factory.setSizeThreshold(16*4096*1024);
		ServletFileUpload upload = new ServletFileUpload(factory);
		// �������󣬲��õ��ϴ��ļ���FileItem����
		upload.setSizeMax(16*4096*1024);
		List<FileItem> items = null;
		try {
			items =upload.parseRequest(request);
		} catch (FileUploadException e) {
			e.printStackTrace();
			return "-1";
		}
		String filename = ""; // �ϴ��ļ����浽���������ļ���
		InputStream is = null; // ��ǰ�ϴ��ļ���InputStream����
		// ѭ�������ϴ��ļ�
		for (FileItem item : items){
			// ������ͨ�ı���
			if (item.isFormField()){
				/*if(item.getFieldName().equals("comid")){
					if(!item.getString().equals(""))
						comId = item.getString("UTF-8");
				}*/
				
			}else if (item.getName() != null && !item.getName().equals("")){// �����ϴ��ļ�
				// �ӿͻ��˷��͹������ϴ��ļ�·���н�ȡ�ļ���
				logger.info(item.getName());
				filename = item.getName().substring(item.getName().lastIndexOf("\\")+1);
				is = item.getInputStream(); // �õ��ϴ��ļ���InputStream����
				
			}
		}
		String file_ext =filename.substring(filename.lastIndexOf(".")).toLowerCase();// ��չ��
		String picurl = uin+"_"+System.currentTimeMillis()+file_ext;
		BufferedInputStream in = null;  
		ByteArrayOutputStream byteout =null;
	    try {
	    	in = new BufferedInputStream(is);   
	    	byteout = new ByteArrayOutputStream(1024);        	       
		      
	 	    byte[] temp = new byte[1024];        
	 	    int bytesize = 0;        
	 	    while ((bytesize = in.read(temp)) != -1) {        
	 	          byteout.write(temp, 0, bytesize);        
	 	    }        
	 	      
	 	    byte[] content = byteout.toByteArray(); 
	 	    DB mydb = MongoClientFactory.getInstance().getMongoDBBuilder("zld");
		    mydb.requestStart();
			  
		    DBCollection collection = mydb.getCollection(table);
		  //  DBCollection collection = mydb.getCollection("records_test");
			  
			BasicDBObject document = new BasicDBObject();
			document.put("uin",  uin);
			document.put("ctime",  System.currentTimeMillis()/1000);
			document.put("type", extMap.get(file_ext));
			document.put("content", content);
			document.put("filename", picurl);
			  //��ʼ����
			mydb.requestStart();
			collection.insert(document);
			  //��������
			mydb.requestDone();
			in.close();        
		    is.close();
		    byteout.close();
		    logger.info(">>>>�ϴ�ͼƬ��� .....");
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}finally{
			if(in!=null)
				in.close();
			if(byteout!=null)
				byteout.close();
			if(is!=null)
				is.close();
		}
	    
		return picurl;
	}

/**
 * ���֧��������
 * @param id
 * @param total
 * @param ticketId �Ż�ȯ��� 
 * @return
 */
//	public int payCarStopOrder(Long id,Double total,Long ticketId) {
//		Long ntime = System.currentTimeMillis()/1000;
//		
//		//�鳵��������Ա���������ţ����ƺ�
//		Map cotMap = daService.getMap("select cid,uin,euid,car_number from carstop_order_tb where id=?  ", new Object[]{id});
//		Long uin =(Long) cotMap.get("uin");
//		Long uid =(Long) cotMap.get("euid");
//		Long cid =(Long) cotMap.get("cid");
//		String carNumber = (String)cotMap.get("car_number");
//		
//		//����Ա����,comidΪ0��ͣ��������Ա��Ϊ����ʱ��Ϊ��Ӧ��ͣ����
//		Long comId  = -1L;
//		Map userMap=daService.getMap("select comid from user_info_Tb where id =?", new Object[]{uid});
//		if(userMap!=null){
//			comId = (Long)userMap.get("comid");
//		}
//		//�鲴�������� 
//		Map csMap = daService.getMap("select name from car_stops_tb where id=? ", new Object[]{cid});
//		String comName ="";
//		if(csMap!=null)
//			comName = (String)csMap.get("name");
//		//��ͣ��ȯ
//		Double ticketMoney = 0d;
//		if(ticketId!=null&&ticketId>0){
//			if(!memcacheUtils.readUseTicketCache(uin))//����ʹ��3�Σ����ز��ɹ�!
//				return -13;
//			Map ticketMap = daService.getMap("select money,type from ticket_tb where limit_day>=? and id=? and state=?",
//					new Object[]{TimeTools.getToDayBeginTime(),ticketId,0});
//			if(ticketMap!=null&&ticketMap.get("money")!=null&&Check.isDouble(ticketMap.get("money")+"")){
//				Integer type = (Integer)ticketMap.get("type");
//				if(type!=null&&type==2){//�����г�΢��ר��ȯ
//					ticketMoney = Double.valueOf(ticketMap.get("money")+"");
//					ticketMoney = (10-ticketMoney)*total*0.1;
//				}else {
//					ticketMoney = Double.valueOf(ticketMap.get("money")+"");
//				}
//			}
//		}
//		
//		if(ticketMoney>total){//�Ż�ȯ������֧�����
//			ticketMoney=total;
//		}else {//�����˻��������Ż�ȯ���
//			Double ubalance =null;
//			//�����˻����
//			userMap = daService.getPojo("select balance from user_info_tb where id =?",	new Object[]{uin});
//			if(userMap!=null&&userMap.get("balance")!=null){
//				ubalance = Double.valueOf(userMap.get("balance")+"");
//				ubalance +=ticketMoney;//�û��������Ż�ȯ���
//			}
//			logger.info(">>>>>>>>>>>>>>>>>>ticket money��"+ticketMoney);
//			if(ubalance==null||ubalance<total){//�ʻ�����
//				return -12;
//			}
//		}
//		logger.info(">>>>>>>>>>carstoporder,comid:"+comId+",ticket:"+ticketId+",uin:"+uin+",uid:"+uid);
//		
//		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
//		//�����û����
//		Map<String, Object> userSqlMap = new HashMap<String, Object>();
//		//����ͣ�������
//	    Map<String, Object> comSqlMap = new HashMap<String, Object>();
//		//�����˻�
//		Map<String, Object> userAccountsqlMap =new HashMap<String, Object>();
//		//�����˻�
//		Map<String, Object> parkAccountsqlMap =new HashMap<String, Object>();
//		//�����˻���ͣ��ȯ
//		Map<String, Object> userTicketAccountsqlMap =new HashMap<String, Object>();
//		//ʹ��ͣ��ȯ����
//		Map<String, Object> ticketsqlMap =new HashMap<String, Object>();
//		//ͣ�����˻���ͣ��ȯ���
//		Map<String, Object> tingchebaoAccountsqlMap =new HashMap<String, Object>();
//		
//		//�۳������˻����
//		userSqlMap.put("sql", "update user_info_tb  set balance =balance-? where id=?");
//		userSqlMap.put("values", new Object[]{total-ticketMoney,uin});
//		if(total-ticketMoney>0)
//			bathSql.add(userSqlMap);
//		//�����˻��Ż�ȯ��ֵ
//		if(ticketMoney>0&&ticketId!=null&&ticketId>0){//ʹ��ͣ��ȯ���������˻��ȳ�ֵ
//			userTicketAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type) values(?,?,?,?,?,?)");
//			userTicketAccountsqlMap.put("values", new Object[]{uin,ticketMoney,0,ntime-1,"ͣ��ȯ��ֵ",7});
//			bathSql.add(userTicketAccountsqlMap);
//		}
//		//�����˻�֧��ͣ������ϸ
//		userAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,uid,target) values(?,?,?,?,?,?,?,?)");
//		userAccountsqlMap.put("values", new Object[]{uin,total,1,ntime,"������-"+comName,0,uid,1});
//		bathSql.add(userAccountsqlMap);
//
//		//������Ĭ�ϸ������˻�20141120����Ҫ���޸�
//		if(comId!=0){//д�빫˾�˻�
//			comSqlMap.put("sql", "update com_info_tb  set total_money =total_money+?,money=money+? where id=?");
//			comSqlMap.put("values", new Object[]{total,total,comId});
//			bathSql.add(comSqlMap);
//			
//			parkAccountsqlMap.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source) values(?,?,?,?,?,?,?)");
//			parkAccountsqlMap.put("values",  new Object[]{comId,total,0,ntime,"������_"+carNumber,uid,2});
//			bathSql.add(parkAccountsqlMap);
//		}else {//д��ͣ�����˻�
//			parkAccountsqlMap.put("sql", "insert into tingchebao_account_tb(amount,type,create_time,remark,utype) values(?,?,?,?,?)");
//			parkAccountsqlMap.put("values", new Object[]{total,1,ntime,"������-����"+carNumber,5});
//			bathSql.add(parkAccountsqlMap);
//		}
//		
//		//�Ż�ȯʹ�ú󣬸���ȯ״̬�����ͣ�����˻�֧����¼
//		if(ticketMoney>0&&ticketId!=null&&ticketId>0){
//			ticketsqlMap.put("sql", "update ticket_tb  set state=?,comid=?,utime=?,umoney=? where id=?");
//			ticketsqlMap.put("values", new Object[]{1,comId,System.currentTimeMillis()/1000,ticketMoney,ticketId});
//			bathSql.add(ticketsqlMap);
//			
//			tingchebaoAccountsqlMap.put("sql", "insert into tingchebao_account_tb(amount,type,create_time,remark,utype) values(?,?,?,?,?)");
//			tingchebaoAccountsqlMap.put("values", new Object[]{ticketMoney,1,ntime,"����"+carNumber+"��ʹ��ͣ������ȯ",0});
//			bathSql.add(tingchebaoAccountsqlMap);
//			memcacheUtils.updateUseTicketCache(uin);//��ȯ����ʹ��ȯ����
//		}
//		
//		boolean result= daService.bathUpdate(bathSql);
//		logger.info(">>>>>>>>>>>>>>>֧�� ��"+result);
//		if(result){//����ɹ�������ȯ������ 
//			//�����֣�����������ȯ��������
//			/* ÿ��������΢�Ż�֧����֧��1Ԫ���ϵ���ɵģ���������2Ԫ����������3Ԫ��ͣ��ȯ��
//			 * �������ֲ���(ͬһ����ÿ��ֻ�ܷ�3��)��
//			 * ����ÿ�շ�ȯ��3��ȯ
//			 * ÿ������ÿ��ʹ��ͣ��ȯ������3�� */
//			boolean isBlack = isBlackUser(uin);
//			if(!isBlack){
//				backTicket(total-ticketMoney, 997L, uin,comId,"");
//			}else {
//				logger.info(">>>>>black>>>>������"+uin+",�ں������ڣ����ܷ����......");
//			}
//			if(total>=1)
//				handleRecommendCode(uin,isBlack);
//			return 5;
//		}else {
//			return -7;
//		}
//	}
	
	/*
	 * ΢�Ź��ں�ҡһҡ���˺�
	 */
//	public int sharkbinduser(String openid, Long uin, Long bind_count){//ҡһҡ���˺�
//		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
//		Map<String, Object> wxuserMap = daService.getMap(
//				"select * from wxp_user_tb where openid=? ",
//				new Object[] { openid });//δ���˻�
//		logger.info(">>>>>>>>>>>>>�����΢�Ź��ں�>>>>>>>>>>>>,openid:"+openid);
//		if(wxuserMap != null){//ʹ�ù�ҡһҡֱ��
//			logger.info(">>>>>>>>>>>>>>>>>�������˻�����Ϣת�Ƶ��󶨵���ʵ�ʻ���>>>>>>>>>>>>>�������˺�uin��"+wxuserMap.get("uin")+",��ʵ�˺�uin:"+uin);
//			logger.info(">>>>>>>>>>>���������˻����Ƽ��߼�>>>>>>>>>>>>>>>>>>");
////			handleWxRecommendCode((Long)wxuserMap.get("uin"), bind_count);
//			Double wx_balance = 0d;//�����˻�������
//			if(wxuserMap.get("balance") != null){
//				 wx_balance = Double.valueOf(wxuserMap.get("balance") + "");
//			}
//			/*Map<String, Object> recomsqlMap =new HashMap<String, Object>();
//			recomsqlMap.put("sql", "update recommend_tb set nid=? where nid=?");
//			recomsqlMap.put("values", new Object[]{ uin, wxuserMap.get("uin") });
//			bathSql.add(recomsqlMap);*/
//			
//			Map<String, Object> trueUsersqlMap =new HashMap<String, Object>();
//			trueUsersqlMap.put("sql", "update user_info_tb set balance=balance+? where id=?");
//			trueUsersqlMap.put("values", new Object[]{ wx_balance, uin });
//			bathSql.add(trueUsersqlMap);
//			
//			Map<String, Object> userAccountsqlMap =new HashMap<String, Object>();
//			userAccountsqlMap.put("sql", "update user_account_tb set uin=? where uin=?");
//			userAccountsqlMap.put("values", new Object[]{uin, wxuserMap.get("uin")});
//			bathSql.add(userAccountsqlMap);
//			
//			//order_ticket_tb
//			Map<String, Object> orderTicketsqlMap =new HashMap<String, Object>();
//			orderTicketsqlMap.put("sql", "update order_ticket_tb set uin=? where uin=?");
//			orderTicketsqlMap.put("values", new Object[]{uin, wxuserMap.get("uin")});
//			bathSql.add(orderTicketsqlMap);
//			
//			//ticket_tb
//			Map<String, Object> ticketsqlMap =new HashMap<String, Object>();
//			ticketsqlMap.put("sql", "update ticket_tb set uin=? where uin=?");
//			ticketsqlMap.put("values", new Object[]{uin, wxuserMap.get("uin")});
//			bathSql.add(ticketsqlMap);
//			
//			//΢�Ź��ں��û���
//			Map<String, Object> wxusersqlMap =new HashMap<String, Object>();
//			wxusersqlMap.put("sql", "delete from wxp_user_tb where openid=?");
//			wxusersqlMap.put("values", new Object[]{ openid });
//			bathSql.add(wxusersqlMap);
//			
//			//�û��ʻ���
//			Map<String, Object> userPayAccountsqlMap =new HashMap<String, Object>();
//			userPayAccountsqlMap.put("sql", "update user_payaccount_tb set uin=? where uin=?");
//			userPayAccountsqlMap.put("values", new Object[]{uin, wxuserMap.get("uin")});
//			bathSql.add(userPayAccountsqlMap);
//			
//			//��־��
//			Map<String, Object> logsqlMap =new HashMap<String, Object>();
//			logsqlMap.put("sql", "update alipay_log set uin=? where uin=?");
//			logsqlMap.put("values", new Object[]{uin, wxuserMap.get("uin")});
//			bathSql.add(logsqlMap);
//			
//			Map<String, Object> ordersqlMap = new HashMap<String, Object>();
//			ordersqlMap.put("sql", "update order_tb set uin=? where uin=? ");
//			ordersqlMap.put("values", new Object[]{uin, wxuserMap.get("uin")});
//			bathSql.add(ordersqlMap);
//			
//			Map<String, Object> rewardsqlMap = new HashMap<String, Object>();
//			rewardsqlMap.put("sql", "update parkuser_reward_tb set uin=? where uin=? ");
//			rewardsqlMap.put("values", new Object[]{uin, wxuserMap.get("uin")});
//			bathSql.add(rewardsqlMap);
//			
//			Integer addcar_flag = 0;//����ӳ��ƺ�
//			if(wxuserMap.get("car_number") != null){
//				Long count = daService.getLong("select count(*) from car_info_tb where car_number=? ",
//						new Object[] { wxuserMap.get("car_number") });
//				if(count == 0){
//					addcar_flag = 1;//��ӳ��ƺ�
//					Map<String, Object> carsqlMap = new HashMap<String, Object>();
//					carsqlMap.put("sql", "insert into car_info_Tb (uin,car_number) values(?,?)");
//					carsqlMap.put("values", new Object[]{uin, wxuserMap.get("car_number")});
//					bathSql.add(carsqlMap);
//				}
//			}
//			
//			
//			boolean b = daService.bathUpdate2(bathSql);
//			if(b){
//				b = memcacheUtils.readUseTicketCache((Long)wxuserMap.get("uin"));
//				
//				if(!b){
//					memcacheUtils.updateUseTicketCache(uin);
//					logger.info(">>>>>>>>>>>>>>>΢�Ź��ںŰ��˻����������˺Ž������ù�ȯ���󶨵���ʵ�˺�д������������˺ţ�"+wxuserMap.get("uin")+"��ʵ�˺ţ�"+uin);
//				}else{
//					logger.info(">>>>>>>>>>>>>>>΢�Ź��ںŰ��˻����������˺Ž���û���ù�ȯ�������˺ţ�"+wxuserMap.get("uin")+"��ʵ�˺ţ�"+uin);
//				}
//				
//				if(addcar_flag == 1){
//					logger.info(">>>>>>>>>>>�����˺����г��ó��ƺţ�����֮ǰ����û��ע�ᳵ�ƣ��Ѹó���ע��Ϊ�û����ƣ������˺ţ�"+wxuserMap.get("uin")+",��ʵ�˺ţ�"+uin+",���ƺţ�"+wxuserMap.get("car_number"));
//					Map<String, Object> userMap = daService.getMap(
//							"select mobile from user_info_tb where id=? ",
//							new Object[] { uin });
//					if(userMap != null){
//						logger.info(">>>>>>>>>>������ӳ��ƺź�Ĳ�ѯ����߼�,�����˺ţ�"+wxuserMap.get("uin")+",��ʵ�˺ţ�"+uin+",���ƺţ�"+wxuserMap.get("car_number"));
//						methods.checkBonus((String)userMap.get("mobile"), uin);
//					}
//				}
//				return 1;
//			}
//		}
//		return 0;
//	}
	
//	public int handleWxRecommendCode(Long nid, Long bind_count){
//		logger.info("handleWxRecommendCode>>>>>���Ƽ���nid:"+nid);
//		Map<String, Object> userMap = daService.getMap("select wxp_openid from user_info_tb where id=? ", new Object[]{nid});
//		
//		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
//		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
//		list = daService.getAll("select * from recommend_tb where (nid=? or openid=?) and type=? and state=? ",
//						new Object[] { nid,userMap.get("wxp_openid"), 0, 0 });
//		Long count = daService.getLong("select count(*) from car_info_tb where uin=? and state=? ", new Object[]{nid, 1});
//		boolean isBlack = isBlackUser(nid);
//		if(list.isEmpty()){
//			logger.info("handleWxRecommendCode>>>>>���û�û�б��Ƽ���¼,���Ƽ���nid:"+nid);
//			return 1;
//		}else{
//			logger.info("handleWxRecommendCode>>>>>���û��б��Ƽ���¼,���Ƽ���uin:"+nid);
//			if(bind_count == 0){
//				logger.info("handleWxRecommendCode>>>>>��ʼ����ɹ��Ƽ��߼������շ�Ա��Ǯ,���Ƽ���uin:"+nid);
//			}else{
//				logger.info("handleWxRecommendCode>>>>>�����Ƽ���¼��Ч,���Ƽ���uin:"+nid);
//			}
//			if(isBlack){
//				logger.info("handleWxRecommendCode>>>>>���û��ں�������Ƽ�ʧЧ������Ǯuin:"+nid);
//			}
//		}
//		if(count > 0){
//			logger.info("handleWxRecommendCode>>>>>:�г���count:"+count+",uin:"+nid);
//			for(Map<String, Object> map : list){
//				Double money = 5d;//Ĭ�Ϸ�5��
//				Long uid = -1L;
//				Integer parker_flag = 0;//0:���շ�Ա�Ƽ���1�շ�Ա�Ƽ�
//				if(map.get("pid") != null){
//					uid = (Long)map.get("pid");
//					Map usrMap =daService.getMap("select recommendquota from user_info_Tb where id =? ", new Object[]{uid});
//					if(usrMap!=null){
//						money = StringUtils.formatDouble(Double.parseDouble(usrMap.get("recommendquota")+""));
//						logger.info("���շ�Ա���Ƽ�������ǣ�"+money);
//					}
//					boolean isParkBlack = isBlackParkUser(uid,true);
//					if(isParkBlack)
////						continue;
//						return 0;
//					Long count1 = daService.getLong(
//									"select count(*) from user_info_tb where id=? and (auth_flag=? or auth_flag=?) ",
//									new Object[] { uid, 1, 2 });
//					if(count1 > 0){
//						parker_flag = 1;//���շ�Ա�Ƽ���
//					}
//				}
//				if(map.get("money") != null && Double.valueOf(map.get("money") + "")>0){
//					money = Double.valueOf(map.get("money") + "");
//				}
//				if(parker_flag == 1 && count> 0){
//					if(bind_count == 0 && !isBlack ){
//						Long comId = -1L;
//						Map comMap = daService.getPojo("select comid from user_info_tb where id=?  ",new Object[] {uid});
//						Map msetMap =null;
//						Integer giveMoneyTo = null;//��ѯ�շ��趨 mtype:0:��˾�˻���1�������˻�'
//						if(comMap!=null){
//							comId =(Long)comMap.get("comid");
//							if(comId!=null&&comId>0);
//								msetMap = daService.getPojo("select giveto from money_set_tb where comid=? and mtype=? ",
//									new Object[]{comId,4});
//						}
//						if(msetMap!=null)
//							giveMoneyTo =(Integer)msetMap.get("giveto");
//						if(giveMoneyTo!=null&&giveMoneyTo==0&&comId!=null&&comId>0){//���ָ�ͣ�����˻�
//							Map<String, Object> comqlMap = new HashMap<String, Object>();
//							//ͣ�����˻�����
//							comqlMap.put("sql", "update com_info_tb set total_money=total_money+?,money=money+?  where id=? ");
//							comqlMap.put("values", new Object[]{money,money,comId});
//							bathSql.add(comqlMap);
//							
//							//д��ͣ�����˻���ϸ
//							Map<String, Object> parkAccountMap = new HashMap<String, Object>();
//							parkAccountMap.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source) " +
//									"values(?,?,?,?,?,?,?)");
//							parkAccountMap.put("values", new Object[]{comId,money,0,System.currentTimeMillis()/1000,"�Ƽ�����",uid,3});
//							bathSql.add(parkAccountMap);
//							logger.info(uid+">>>�Ƽ�������ͣ����");
//							
//						}else {//���ָ��շ�Ա�˻�
//							Map<String, Object> usersqlMap = new HashMap<String, Object>();
//							//�շ�Ա�˻�����
//							usersqlMap.put("sql", "update user_info_tb set balance=balance+? where id=? ");
//							usersqlMap.put("values", new Object[]{money,uid});
//							bathSql.add(usersqlMap);
//							
//							//д���շ�Ա�˻���ϸ
//							Map<String, Object> parkuserAccountMap = new HashMap<String, Object>();
//							parkuserAccountMap.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target) " +
//									"values(?,?,?,?,?,?)");
//							parkuserAccountMap.put("values", new Object[]{uid,money,0,System.currentTimeMillis()/1000,"�Ƽ�����",3});
//							bathSql.add(parkuserAccountMap);
//						}
//						//�����Ƽ���¼
//						Map<String, Object> recomsqlMap = new HashMap<String, Object>();
//						recomsqlMap.put("sql", "update recommend_tb set state=? where (nid=? or openid=?) and pid=?");
//						recomsqlMap.put("values", new Object[]{1,nid,userMap.get("wxp_openid"),uid});
//						bathSql.add(recomsqlMap);
//					}else{
//						//�����Ƽ���¼
//						Map<String, Object> recomsqlMap = new HashMap<String, Object>();
//						recomsqlMap.put("sql", "update recommend_tb set state=? where (nid=? or openid=?) and pid=?");
//						recomsqlMap.put("values", new Object[]{3,nid,userMap.get("wxp_openid"),uid});
//						bathSql.add(recomsqlMap);
//					}
//				}
//			}
//			boolean b = false;
//			if(!bathSql.isEmpty()){
//				b = daService.bathUpdate2(bathSql);
//			}
//			
//			if(b){
//				logger.info("handleWxRecommendCode>>>>>�Ƽ��߼�����ɹ�,���Ƽ���uin:"+nid);
//			}else{
//				logger.info("handleWxRecommendCode>>>>>�Ƽ��߼�����ʧ��uin:"+nid);
//			}
//			
//			if(b){
//				return 1;
//			}else{
//				return 0;
//			}
//		}else{
//			logger.info("handleWxRecommendCode>>>>>:�޳���count:"+count+",uin:"+nid);
//			return 0;
//		}
//	}
	
	/**
	 * ȡͣ����
	 * @param lat
	 * @param lon
	 * @param payable�Ƿ��֧��
	 * @return 2000���ڵ�ͣ����
	 */
	public List<Map<String, Object>> getPark2kmList(Double lat,Double lon,Integer payable){
//		payable=1;//ǿ�ƹ��˲���֧������
		double lon1 = 0.023482756;
		double lat1 = 0.017978752;
		String sql = "select id,company_name as name,longitude lng,latitude lat,parking_total total,share_number," +
				"address addr,phone,monthlypay,epay,type,isfixed from com_info_tb where longitude between ? and ? " +
				"and latitude between ? and ? and state=? and isview=? ";//and isfixed=? ";
		List<Object> params = new ArrayList<Object>();
		params.add(lon-lon1);
		params.add(lon+lon1);
		params.add(lat-lat1);
		params.add(lat+lat1);
		params.add(0);
		params.add(1);
	//	params.add(1);
//		if(payable==1){
//			sql +=" and isfixed=? and epay=? ";
//			params.add(1);
//			params.add(1);
//		}
		List list = null;//daService.getPage(sql, null, 1, 20);
		list = daService.getAll(sql, params, 0, 0);
		return list;
	}
	
	/**
	 * ��ȡ���ע���û�
	 * @param mobile �ֻ���
	 * @param media ý����Դ 
	 * @param getcode �Ƿ��ȡ��֤��
	 * @return
	 */
	public Long regUser(String mobile,Long media,Long uid,boolean getcode){
		Long uin = daService.getkey("seq_user_info_tb");
		Long ntime = System.currentTimeMillis()/1000;
		String strid = "zlduser"+uin;
		//�û���
		String sql= "insert into user_info_tb (id,nickname,password,strid," +
				"reg_time,mobile,auth_flag,comid,media,recom_code) " +
				"values (?,?,?,?,?,?,?,?,?,?)";
		Object[] values= new Object[]{uin,"����",strid,strid,ntime,mobile,4,0,media.intValue(),uid};
		//2015-03-10������������ʱ������д��ͣ��ȯ����¼ʱ�жϺ����������ͣ��ȯ
		/*if(media==8||media==7){//7"��360",8"������"
			String tsql = "insert into ticket_tb (create_time,limit_day,money,state,uin) values(?,?,?,?,?) ";
			List<Object[]> insertvalues = new ArrayList<Object[]>();
			//Long ntime = System.currentTimeMillis()/1000;
			Object[] v1 = new Object[]{ntime,ntime+15*24*60*60,10,0,uin};
			Object[] v2 = new Object[]{ntime,ntime+15*24*60*60,10,0,uin};
			Object[] v3 = new Object[]{ntime,ntime+15*24*60*60,10,0,uin};
			Object[] v4 = new Object[]{ntime,ntime+15*24*60*60,10,0,uin};
			Object[] v5 = new Object[]{ntime,ntime+15*24*60*60,10,0,uin};
			Object[] v6 = new Object[]{ntime,ntime+15*24*60*60,10,0,uin};
			Object[] v7 = new Object[]{ntime,ntime+15*24*60*60,10,0,uin};
			Object[] v8 = new Object[]{ntime,ntime+15*24*60*60,10,0,uin};
			Object[] v9 = new Object[]{ntime,ntime+15*24*60*60,10,0,uin};
			Object[] v10 = new Object[]{ntime,ntime+15*24*60*60,10,0,uin};
			insertvalues.add(v1);insertvalues.add(v2);insertvalues.add(v3);insertvalues.add(v4);insertvalues.add(v5);
			insertvalues.add(v6);insertvalues.add(v7);insertvalues.add(v8);insertvalues.add(v9);insertvalues.add(v10);
			int result= daService.bathInsert(tsql, insertvalues, new int[]{4,4,4,4,4});
			if(result>0){
				logService.insertUserMesg(1, uin, "��ϲ�����ʮ��10Ԫͣ��ȯ!", "�������");
			}
		}else {*/
			int ts = backNewUserTickets(ntime, uin);
			logger.info("��ȡ���ע���û�����ȯ��"+ts+",��ֱ��д��ͣ��ȯ���У���¼ʱ��֤�Ƿ��Ǻ������󷵻�");
//		}
		int r = daService.update(sql,values);
		logger.info("��ȡ���ע���û���ע������"+r);
		if(r==1){
			//ע��ɹ�����һ���Ƿ����շ�Ա�Ƽ�
			if(media==999&&uid>0){
				Map userMap = daService.getMap("select comid from user_info_Tb where id =? and auth_flag in(?,?) and state=?", 
						new Object[]{uid,1,2,0}) ;
				Long comId =null;
				if(userMap!=null){
					comId =(Long)userMap.get("comid");
				}
				if(comId!=null&&comId>0){
					int rem = daService.update("insert into recommend_tb (pid,nid,type,state,create_time) values(?,?,?,?,?)",
							new Object[]{uid,uin,0,0,System.currentTimeMillis()/1000});
//					if(uid!=null&&comId!=null)
//						logService.updateScroe(5, uid, comId);//�Ƽ���������1���� 
					//int backmoney = daService.update("update user_info_tb set balance=balance+5 where id=?", new Object[]{uid});
					logger.info("�շ�Ա�Ƽ�������ͨ����ȡ���ע��ɹ�,�Ƽ���¼��"+rem);
				}else {
					logger.info("�շ�Ա�Ƽ�������ͨ����ȡ���ע��ɹ������Ƽ����շ�Ա������:"+uid);
				}
			}
			
			
			int	eb = daService.update("insert into user_profile_tb (uin,low_recharge,limit_money,auto_cash," +
					"create_time,update_time) values(?,?,?,?,?,?)", 
					new Object[]{uin,10,25,1,ntime,ntime});
			logger.info("�շ�Ա�Ƽ��������Զ�֧������:"+eb);
			if(!getcode){
				//ע��ɹ������Ͷ���
				String mesg ="��ʵ���ͣ���ѣ���������ͣ������ͣ���������Żݣ�8ԪǮͣ5�γ������ص�ַ�� http://t.cn/RZJ4UAv ��ͣ������";
				SendMessage.sendMultiMessage(mobile, mesg);
			}
			return uin;
		}
		return -1L;
	}
	
	/**
	 * �����շ�Ա�Ƽ����֣�������һ�ʣ���֧�����1Ԫ���ϣ����ں������ڵĳ������ŷ��ָ��շ�Ա
	 * @param uin  ����
	 */
	private void handleRecommendCode(Long uin,boolean isBlack){
		Long recom_code = null;
		Map recomMap = daService.getMap("select pid from recommend_tb where nid=? and state=? and type=? ", new Object[]{uin,0,0});
		if(recomMap==null||recomMap.isEmpty()){//û����س���δ������Ƽ���ֱ�ӷ���
			logger.info(">>>>>>>>>>handle recommend,error: no pid ,uin:"+uin);
			return ;
		}else {
			recom_code = (Long )recomMap.get("pid");
		}
		//logger.info();
		Map usrMap = daService.getMap("select recom_code from user_info_tb where id=?", new Object[]{uin});
		if(usrMap == null){//�����������˻�����δ���˻�
			return;
		}
		logger.info(">>>>>>>>>>handle recommend"+usrMap);
		Long uid = (Long)usrMap.get("recom_code");
		if(recom_code==null||uid==null||recom_code.intValue()!=uid.intValue()||isBlackParkUser(recom_code,true)){
			logger.info(">>>>>>>>>>handle recommend,error:  recomCode:"+recom_code+",uid:"+uid);
			return ;
		}
		usrMap =daService.getMap("select auth_flag,mobile,recommendquota from user_info_Tb where id =? ", new Object[]{uid});
		logger.info(">>>>>>>>>>handle recommend"+usrMap);
		String mobile = "";
		
		//�Ƽ��˽�ɫ
		Long auth_flag = null;
		Double recommendquota = 5d;
		if(usrMap!=null){
			auth_flag = (Long) usrMap.get("auth_flag");
			mobile = (String)usrMap.get("mobile");
			recommendquota = StringUtils.formatDouble(Double.parseDouble(usrMap.get("recommendquota")+""));
			logger.info("���շ�Ա���Ƽ�������ǣ�"+recommendquota);
		}
		
		if(isBlack){
			String mobile_end = mobile.substring(7);
			int result =daService.update("insert into parkuser_message_tb(type,ctime,uin,title,content) values(?,?,?,?,?)",
					new Object[]{0,System.currentTimeMillis()/1000, uid, "�Ƽ�����", "���Ƽ��ĳ������ֻ�β��"+mobile_end+"�����˻���ˢ�����ɣ�����ȡ����"} );
			int result1 = daService.update("update recommend_tb set state=? where nid=? and pid=?", new Object[]{2,uin,uid});
			logger.info(">>>>>>>>>���������շ�Ա �����Ƽ��ĳ����ں������У�����ȡ��:����Ϣ��"+result+"���Ƽ�����Ϊ��������"+result1);
			return ;
		}
		
		//���շ�Ա�Ƽ��ĳ�����Ŀǰû�г����Ƽ������ļ�¼
		if(auth_flag!=null&&(auth_flag==1||auth_flag==2)){
			Long count  = daService.getLong("select count(ID) from recommend_tb where nid=? and pid=? and state=? and type=?", new Object[]{uin,uid,0,0});
			//�Ƽ�����.0��������1:����
			logger.info("is recom:"+count);
			if(count!=null&&count>0){//���Ƽ������Ƽ��˵Ľ���û��֧��//���������շ�Ա�˺�5Ԫ
				List<Map<String , Object>> sqlMaps = new ArrayList<Map<String,Object>>();
				Long comId = -1L;
				Map comMap = daService.getPojo("select comid from user_info_tb where id=?  ",new Object[] {uid});
				Map msetMap =null;
				Integer giveMoneyTo = null;//��ѯ�շ��趨 mtype:0:��˾�˻���1�������˻�'
				if(comMap!=null){
					comId =(Long)comMap.get("comid");
					if(comId!=null&&comId>0);
						msetMap = daService.getPojo("select giveto from money_set_tb where comid=? and mtype=? ",
							new Object[]{comId,4});
				}
				if(msetMap!=null)
					giveMoneyTo =(Integer)msetMap.get("giveto");
				if(comId!=null&&comId>0&&giveMoneyTo!=null&&giveMoneyTo==0){//���ָ�ͣ�����˻�
					Map<String, Object> comqlMap = new HashMap<String, Object>();
					//ͣ�����˻�����
					comqlMap.put("sql", "update com_info_tb set total_money=total_money+?,money=money+?  where id=? ");
					comqlMap.put("values", new Object[]{recommendquota,recommendquota,comId});
					sqlMaps.add(comqlMap);
					
					//д��ͣ�����˻���ϸ
					Map<String, Object> parkAccountMap = new HashMap<String, Object>();
					parkAccountMap.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source) " +
							"values(?,?,?,?,?,?,?)");
					parkAccountMap.put("values", new Object[]{comId,recommendquota,0,System.currentTimeMillis()/1000,"�Ƽ�����",uid,3});
					sqlMaps.add(parkAccountMap);
					logger.info(uid+">>>�Ƽ�������ͣ����");
				}else {
					Map<String, Object> usersqlMap = new HashMap<String, Object>();
					//�շ�Ա�˻���5Ԫ
					usersqlMap.put("sql", "update user_info_tb set balance=balance+? where id=? ");
					usersqlMap.put("values", new Object[]{recommendquota,uid});
					sqlMaps.add(usersqlMap);
				
					//д���շ�Ա�˻���ϸ
					Map<String, Object> parkuserAccountMap = new HashMap<String, Object>();
					parkuserAccountMap.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target) " +
							"values(?,?,?,?,?,?)");
					parkuserAccountMap.put("values", new Object[]{uid,recommendquota,0,System.currentTimeMillis()/1000,"�Ƽ�����",3});
					sqlMaps.add(parkuserAccountMap);
					
				}
				//�����Ƽ���¼
				Map<String, Object> recomsqlMap = new HashMap<String, Object>();
				recomsqlMap.put("sql", "update recommend_tb set state=?,money=? where nid=? and pid=?");
				recomsqlMap.put("values", new Object[]{1,recommendquota,uin,uid});
				sqlMaps.add(recomsqlMap);
				
				logger.info(count);
				boolean ret = daService.bathUpdate(sqlMaps);
				if(ret){//д���շ�Ա��Ϣ��
					
					String mobile_end = mobile.substring(7);
					int result =daService.update("insert into parkuser_message_tb(type,ctime,uin,title,content) values(?,?,?,?,?)",
							new Object[]{0,System.currentTimeMillis()/1000, uid, "�Ƽ�����", "���Ƽ��ĳ������ֻ�β��"+mobile_end+"��ע��ɹ�������"+recommendquota+"Ԫ������"} );
					logger.info(">>>>>>>>>���������շ�Ա�Ƽ�����"+recommendquota+"Ԫ��Ϣ:"+result);
				}
				logger.info(">>>>>>>>>���������շ�Ա�Ƽ�����"+recommendquota+"Ԫ��"+ret);
			}
		}else {
			logger.info(uid);
		}
	}
	
//	public String getCollectMesgSwith(){
//		String swith = memcacheUtils.doStringCache("collectormesg_swith", null, null);
//		if(swith==null)
//			return "0";
//		return swith;
//	}
	
	/*
	 * ��ȡjssdk�ӿ�ע��Ȩ����֤��Ϣ
	 */
//	public Map<String, String> getJssdkApiSign(HttpServletRequest request) throws JDOMException, IOException{
//		Map<String, String> ret = new HashMap<String, String>();
//		String jsapi_ticket = getJsapi_ticket();//jsapi_ticket�ǹ��ں����ڵ���΢��JS�ӿڵ���ʱƱ��
//		String request_url = request.getRequestURL().toString();//��ǰ�����·��
//		String request_params = request.getQueryString();//����Ĳ���
//		if(request_params != null){
//			request_url += "?" + request_params;
//		}
////		ret = PayCommonUtil.sign(jsapi_ticket, request_url);
//		return ret;
//	}
	
	/*public void updateSorceq(Long btime,Long etime,Integer cType,Long uid,Long comId){
		if(cType!=null&&btime!=null&&(etime-btime>=15*60)){//����ʱ������15���ӣ����Լ�һ��0.2�Ļ���
			if(cType==0)//NFC����  ( ˢNFC������ɨ��������Ч������������֧����0.01�֣�����֧������һԪ����2�֡�)
				logService.updateScroe(2, uid,comId);
			else if(cType==2||cType==3)//ɨ�ƻ����ƻ��� 
				logService.updateScroe(4, uid,comId);
		}
	}*/
	
//	public void setCityCache(Long comid,Integer city){
//		Map<Long, Integer> map = memcacheUtils.doMapLongIntegerCache("comid_backmoney_cache", null, null);
//		if(map==null||map.size()<20){
//			List<Map<String, Object>> idlist= daService.getAll("select id from com_info_tb where city between ? and ? ", new Object[]{370100,370199});
//			if(idlist!=null){
//				if(map==null)
//					map = new HashMap<Long, Integer>();
//				for(Map<String, Object> maps: idlist){
//					Long id = (Long)maps.get("id");
//					if(id!=null)
//						map.put(id, 1);
//				}
//				logger.info(">>>>>jinan city cache size:"+map.size());
//				memcacheUtils.doMapLongIntegerCache("comid_backmoney_cache", map, "update");
//			}
//		}else {
//			if(city>=110000&&city<120000){
//				if(map!=null&&map.containsKey(comid)){
//					map.remove(comid);
//					memcacheUtils.doMapLongIntegerCache("comid_backmoney_cache", map, "update");
//				}
//			}else {
//				if(!map.containsKey(comid)){
//					map.put(comid, 0);
//					memcacheUtils.doMapLongIntegerCache("comid_backmoney_cache", map, "update");
//				}
//			}
//		}
//	}
	
	public boolean isCanBackMoney(Long comid){
		logger.info(">>>���г���������");
		return false;
//		Map<Long, Integer> map = memcacheUtils.doMapLongIntegerCache("comid_backmoney_cache", null, null);
//		if(map==null){
//			logger.info(">>>û�зǱ���������ֱ�ӷ���...");
//			return true;
//		}else {
//			logger.info(">>>�Ǳ�����������Ϊ:"+map.size()+",comid:"+comid);
//			if(map.containsKey(comid)){
//				/*Integer times = map.get(comid);
//				Integer rand = RandomUtils.nextInt(10);//��0-9��ȡ�������Ϊ8ʱ����
//				if(rand==8){//����
//					times = times+1;
//					map.put(comid, times);
//					memcacheUtils.doMapLongIntegerCache("comid_backmoney_cache", map, "update");
//					logger.info(">>>���ϳ����������Ϸ��ּ���,�¸�������"+times+"������Ϊ:"+map);
//					return true;
//				}else {
//					return false;
//				}
//				logger.info(">>>���ϳ��������ּ���:"+times);
//				if(times%10==0){
//					logger.info(">>>���ϳ��������Ϸ��ּ���:"+times);
//					return true;
//				}else {
//					times = times+1;
//					map.put(comid, times);
//					memcacheUtils.doMapLongIntegerCache("comid_backmoney_cache", map, "update");
//					logger.info(">>>���ϳ����������Ϸ��ּ���,�¸�������"+times+"������Ϊ:"+map);
//					return false;
//				}*/
//				return false;
//			}else {
//				logger.info(">>>���ڷǱ������������ڣ�ֱ�ӷ���...");
//				return true;	
//			}
//		}
	}
	
	/*
	 * ���ʹ�����Ϣ������
	 */
//	public void sendBounsMessage(String openid,Long uid, Double total,Long orderid ,Long uin){
//		try {
//			Map uidMap = daService.getMap("select nickname from user_info_tb where id=? ", new Object[]{uid});
//			Map<String, String> baseinfo = new HashMap<String, String>();
//			List<Map<String, String>> orderinfo = new ArrayList<Map<String,String>>();
//			String url = "http://"+Constants.WXPUBLIC_REDIRECTURL+"/zld/wxpfast.do?action=toreward&uid="+uid+"&orderid="+orderid+"&openid="+openid;
//			String remark = "������飬��ȯ���ͣ�";
//			String remark_color = "#FF0000";
//			baseinfo.put("url", url);
//			baseinfo.put("openid", openid);
//			baseinfo.put("top_color", "#000000");
//			baseinfo.put("templeteid", Constants.WXPUBLIC_BONUS_NOTIFYMSG_ID);
//			Map<String, String> keyword1 = new HashMap<String, String>();
//			keyword1.put("keyword", "keyword1");
//			keyword1.put("value", uidMap.get("nickname") + "("+uid+")");
//			keyword1.put("color", "#000000");
//			orderinfo.add(keyword1);
//			Map<String, String> keyword2 = new HashMap<String, String>();
//			keyword2.put("keyword", "keyword2");
//			keyword2.put("value", "ʹ��ͣ�����Ʒ��շ�");
//			keyword2.put("color", "#000000");
//			orderinfo.add(keyword2);
//			Map<String, String> keyword3 = new HashMap<String, String>();
//			keyword3.put("keyword", "remark");
//			keyword3.put("value", remark);
//			keyword3.put("color", remark_color);
//			orderinfo.add(keyword3);
//			Map<String, String> keyword4 = new HashMap<String, String>();
//			keyword4.put("keyword", "first");
//			keyword4.put("value", "����Ա����շ�Ա�������⣬�����ʹ��ͣ��ȯ�����շ�Ա��");
//			keyword4.put("color", "#000000");
//			orderinfo.add(keyword4);
//			Long count = daService.getLong("select count(*) from parkuser_reward_tb where uin=? and ctime>=? and uid=? ",
//					new Object[] { uin, TimeTools.getToDayBeginTime(), uid });
//			logger.info("ͬһ������ͬһ�շ�Ա�Ľ��մ��ʹ�����uid:"+uid+",uin:"+uin+",openid:"+openid+",orderid:"+orderid+",count:"+count);
//			if(count == 0){
//				sendWXTempleteMsg(baseinfo, orderinfo);
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//	}
	
	/*
	 * ��Ϣģ�幫�÷��� 
	 */
//	public void sendWXTempleteMsg(Map<String, String> baseinfo,List<Map<String, String>> orderinfo) throws JSONException{
//		//����ģ����Ϣ
//		JSONObject msgObject = new JSONObject();
//		JSONObject dataObject = new JSONObject();
//		for(Map<String, String> map : orderinfo){
//			JSONObject keynote = new JSONObject();
//			keynote.put("value", map.get("value"));
//			keynote.put("color", map.get("color"));
//			dataObject.put(map.get("keyword"), keynote);
//		}
//		msgObject.put("touser", baseinfo.get("openid"));
//		msgObject.put("template_id", baseinfo.get("templeteid"));//
//		msgObject.put("url", baseinfo.get("url"));
//	    msgObject.put("topcolor", baseinfo.get("top_color"));
//		msgObject.put("data", dataObject);
//		String accesstoken = getWXPAccessToken();
//		String msg = msgObject.toString();
////		PayCommonUtil.sendMessage(msg, accesstoken);
//	}
	
	private Double getBackMoney(){
		Double moneys[] = new Double[]{0d,1d,2d,3d,4d};
		Integer rand = RandomUtils.nextInt(5);
		return moneys[rand];
	}
	
	private double getminPriceUnit(Long comId){
		Map com =daService.getPojo("select * from com_info_tb where id=? "
				, new Object[]{comId});
		double minPriceUnit = Double.valueOf(com.get("minprice_unit")+"");
		return minPriceUnit;
	}
	
//	public boolean isCanSendShortMesg(String mobile){
//		Map<String, String> sendCache = memcacheUtils.doMapStringStringCache("verification_code_cache", null, null);
//		Long ttime = TimeTools.getToDayBeginTime();
//		//System.err.println(sendCache);
//		if(sendCache==null){
//			sendCache = new HashMap<String, String>();
//			sendCache.put(mobile, ttime+"_"+1);
//		}else {
//			String value = sendCache.get(mobile);
//			if(value!=null&&value.indexOf("_")!=-1){
//				String dayt[] =value.split("_");
//				Long time= Long.valueOf(dayt[0]);
//				if(time.equals(ttime)){
//					Integer times = Integer.valueOf(dayt[1]);
//					if(times>9){
//						logger.info(mobile+"������֤�볬��10��");
//						return false;
//					}else {
//						value = time+"_"+(times+1);
//						sendCache.put(mobile,value);
//					}
//				}else {
//					sendCache.put(mobile, ttime+"_"+1);
//				}
//			}else {
//				sendCache.put(mobile, ttime+"_"+1);
//			}
//		}
//		 memcacheUtils.doMapStringStringCache("verification_code_cache", sendCache, "update");
//		return true;
//	}

	/**
	 * ����ͣ��ȯ��ߵֿ۽��
	 * @param type 0���ͣ�1֧��
	 * @param uin  �����˻�
	 * @param uid  �շ�Ա�˻�
	 * @param total  ͣ���ѽ��
	 * @return �ֿ۽��
	 */
	public Double useTicket(Long uin,Long uid,Integer type,Double total){
		boolean isAuth = isAuthUser(uin);
		Double maxMoney = 1.0;//δ��֤����ߵ�һԪ
		if(type==0&&isAuth){//0����
			Double rewardquota = 2.0;
			Map user = daService.getMap("select rewardquota from user_info_tb where id = ?", new Object[]{uid});
			if(user!=null&&user.get("rewardquota")!=null)
				rewardquota =StringUtils.formatDouble(user.get("rewardquota"));
			if(rewardquota>total)//�շ�Ա��ߴ������ý����ڴ��ͽ��ʱ����ȯ�������Ϊ���ͽ��
				rewardquota= total;
			maxMoney= rewardquota;
		}else if(type==1&&isAuth){//1֧��
			//֧�������ͣ��ȯ����ֵ����ֿ۽���붩�����Ĳ�ֵΪ1���򶩵�Ϊ10Ԫʱ��ȯ��ߵֿ�9Ԫ��
			//20Ԫ��ȯҲֻ�ֿܵ�9Ԫ�����Ϊ2������ߵֿ�8Ԫ
			Double uselimit = StringUtils.formatDouble(CustomDefind.getValue("TICKET_LIMIT"));
			maxMoney= total-uselimit;
		}
		return maxMoney;
	}
	
	/**
	 * ��ȡ����ȯ�ֿ۽��
	 * @param ticketId
	 * @param ptype 0�˻���ֵ��1���²�Ʒ��2ͣ���ѽ��㣻3ֱ��;4���� 5����ͣ��ȯ
	 * @param uid
	 * @param total ���
	 * @param utype 0��ͨѡȯ��Ĭ�ϣ�1���ô������ֿ۽���ͣ��ȯ
	 * @param comid
	 * @param orderId
	 * @return
	 */
	private Double getTicketMoney(Long ticketId, Integer ptype, Long uid, Double total, Integer utype, Long comid, Long orderId){
		Double ticketMoney = 0d;
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> ticketMap = daService.getMap("select * from ticket_tb where id=? ", new Object[]{ ticketId });
		logger.info("orderid:"+orderId+",ticketid:"+ticketId+",ticketMap:"+ticketMap);
		if(ticketMap != null){
			list.add(ticketMap);
			list = methods.chooseTicketByLevel(list, ptype, uid, total, utype, comid, orderId);
			ticketMap = list.get(0);
			ticketMoney = Double.valueOf(ticketMap.get("limit") + "");
			logger.info("orderid:"+orderId+",ticketMap:"+ticketMap);
		}
		return ticketMoney;
	}
	
	/**
	 * ��ȡ����ȯ�ֿ۽��
	 * @param uin
	 * @param uid
	 * @param total
	 * @return
	 */
	private Double getDisTicketMoney(Long uin, Long uid, Double total){
		Double ticketMoney = 0d;
		Map<String, Object> ticketMap = methods.chooseDistotalTicket(uin, uid, total);
		if(ticketMap != null){
			ticketMoney = StringUtils.formatDouble(ticketMap.get("money"));
		}
		return ticketMoney;
	}
	public void uploadpic2line(String fn,Long comid,Long orderid,String lefttop,String rightbottom,String width,String height,Long type) throws Exception{
		System.out.println("-----------------------------------");
		InputStream is = null;
		try {
			File f = new File(fn);
			is = new FileInputStream(f);
//    		map.put("content",list.get(1));
    		Map map = daService.getMap("select line_id from order_tb where id = ?", new Object[]{orderid});
    		System.out.println(orderid);
    		if(map!=null&&map.get("line_id")!=null){
    			Long line_id = Long.parseLong(map.get("line_id")+"");
//    			Long Line_preorderid = daService.getLong("select line_id from order_tb where id = ?", new Object[]{preorderid});
        		String params = "action=uploadpic&comid="
    				+ comid + "&orderid=" + line_id + "&lefttop=" + lefttop
    				+ "&rightbottom=" + rightbottom + "&type=" + type
    				+ "&width=" + width + "&height=" + height;
//    			String rets = requestLine("http://192.168.199.251/zldline/syncInter.do"+ "?" + request.getQueryString(),null);
    			URL url = new URL(CustomDefind.DOMAIN+"/carpicsup.do"+ "?" + params);
    			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    			String  BOUNDARY =  UUID.randomUUID().toString();
    			conn.setReadTimeout(4*1000);
    			conn.setConnectTimeout(4*1000);
    			conn.setDoInput(true);  //����������
    			conn.setDoOutput(true); //���������
    			conn.setUseCaches(false);  //������ʹ�û���
    			conn.setRequestMethod("POST");  //����ʽ
    			conn.setRequestProperty("Charset", "utf-8");  //���ñ���
    			conn.setRequestProperty("connection", "keep-alive");   
    			conn.setRequestProperty("Content-Type", "multipart/form-data" + ";boundary=" +BOUNDARY ); 
//    			InputStream is = null;
    			if(is != null)
    			{
    				/**
    				 * ���ļ���Ϊ�գ����ļ���װ�����ϴ�
    				 */
    				DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
    				StringBuffer sb = new StringBuffer();
    				sb.append("--");
    				sb.append(BOUNDARY);
    				sb.append("\r\n");
    				/**
    				 * �����ص�ע�⣺
    				 * name�����ֵΪ����������Ҫkey   ֻ�����key �ſ��Եõ���Ӧ���ļ�
    				 * filename���ļ������֣�������׺����   ����:abc.png  
    				 */
    				sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""+"zhenlaidian.jpg"+"\""+"\r\n"); 
    				sb.append("Content-Type: application/octet-stream; charset="+"utf-8"+"\r\n");
    				sb.append("\r\n");
    				dos.write(sb.toString().getBytes());
    				byte[] bytes = new byte[1024];
    				int len = 0;
    				while((len=is.read(bytes))!=-1)
    				{
    					dos.write(bytes, 0, len);
    				}
    				is.close();
    				dos.write("\r\n".getBytes());
    				byte[] end_data = ("--"+BOUNDARY+"--"+"\r\n").getBytes();
    				dos.write(end_data);
    				dos.flush();
    				/**
    				 * ��ȡ��Ӧ��  200=�ɹ�
    				 * ����Ӧ�ɹ�����ȡ��Ӧ����  
    				 */
    				int res = conn.getResponseCode();  
    				String msg = conn.getResponseMessage();
    				if(res==200){
    					if(type==0){
    						int r = daService.update("update order_tb set uploadinpic_state=? where id = ?", new Object[]{1,orderid});
    					}else if(type==1){
    						int r = daService.update("update order_tb set uploadoutpic_state=? where id = ?", new Object[]{1,orderid});
    					}
    					System.out.println("uploadpic success----------------");
    				}else{
    					if(type==0){
    						int r = daService.update("update order_tb set uploadinpic_state=? where id = ?", new Object[]{0,orderid});
    					}else if(type==1){
    						int r = daService.update("update order_tb set uploadoutpic_state=? where id = ?", new Object[]{0,orderid});
    					}
    				}
    			}
    		}
		} catch (Exception e) {
			if(type==0){
				int r = daService.update("update order_tb set uploadinpic_state=? where id = ?", new Object[]{0,orderid});
			}else if(type==1){
				int r = daService.update("update order_tb set uploadoutpic_state=? where id = ?", new Object[]{0,orderid});
			}
			e.printStackTrace();
		}finally{
			if(is!=null)
				is.close();
		}
//    		
	}
	public void uploadOrder2Line(final Long orderid,final Integer type,Integer sync_state){
//	    ExecutorService executor = Executors.newSingleThreadExecutor();  
//	    FutureTask<String> future =  
//	           new FutureTask<String>(new Callable<String>() {//ʹ��Callable�ӿ���Ϊ�������  
//	             public String call() {
//	            	 System.out.println(orderid);
	            	 Map map = daService.getMap("select * from order_tb where id = ?",new Object[]{orderid});
	            	 if(type==3&&(map==null||map.get("line_id")==null)){
	            		 int r = daService.update("update order_tb set sync_state=? where id = ?", new Object[]{0,orderid});
	            	 }else{
	            		 String order = AjaxUtil.encodeUTF8(StringUtils.createJson(map));
		            	 HttpProxy httpProxy = new HttpProxy();
		            	 Map parammap = new HashMap();
		            	 parammap.put("order", order);
		            	 String ret = null;
		            	 try {
		            		String token = null;
		         			Map session = daService.getMap("select * from  sync_time_tb where id = ? ", new Object[]{1});
		         			if(session!=null&&session.get("token")!=null){
		         				token = session.get("token")+"";
		         			}
		         			 parammap.put("token", token);
		            		 ret = httpProxy.doPost(CustomDefind.DOMAIN+"/syncInter.do?action=uploadOrder2Line&type="+type, parammap);
		            		 System.out.println("�ϴ�������"+ret);
			         	        if(ret!=null&&ret.startsWith("1")){
			         	        	String rets[] = ret.split("_");
			         	        	if(sync_state==1){
			         	        		int r = daService.update("update order_tb set uin = ?,sync_state=?,line_id=? where id = ?", new Object[]{Long.valueOf(ret.split("_")[1]),1,Long.valueOf(ret.split("_")[2]),orderid});
			         	        		daService.update("update ticket_tb set lineorderid = ? where orderid = ?", new Object[]{Long.valueOf(rets[2]),Long.valueOf(rets[3])});
			         	        	}else if(sync_state==2){
			         	        		int r = daService.update("update order_tb set sync_state=? where id = ?", new Object[]{sync_state,orderid});
			         	        		Map map2 = daService.getMap("select line_id from order_tb where id = ?", new Object[]{Long.valueOf(rets[1])});
										if(map2!=null&&map2.get("line_id")!=null){
											daService.update("update ticket_tb set lineorderid = ? where orderid = ?", new Object[]{Long.valueOf(map.get("line_id")+""),Long.valueOf(rets[1])});
											String tk = "";
											Map m = daService.getMap("select * from  sync_time_tb where id = ? ", new Object[]{1});
											if(m!=null&&m.get("token")!=null){
												token = m.get("token")+"";
											}
											upload2ticket(tk);
										}
			         	        	}
			         	        }else{
			         	        	int r = daService.update("update order_tb set sync_state=? where id = ? and sync_state<3", new Object[]{0,orderid});
			         	        	}
							} catch (Exception e) {
								int r = daService.update("update order_tb set sync_state=? where id = ? and sync_state<3", new Object[]{0,orderid});
								e.printStackTrace();
							}
	            	 }
//					return ret;  
//	           }});  
//	    executor.execute(future);  
//	    try {  
//	        String result = future.get(3000, TimeUnit.MILLISECONDS); //ȡ�ý����ͬʱ���ó�ʱִ��ʱ��Ϊ5�롣ͬ��������future.get()��������ִ�г�ʱʱ��ȡ�ý��  
//	        if(result!=null&&result.startsWith("1")){
//	        	int r = daService.update("update order_tb set sync_state=? where id = ?", new Object[]{2,orderid});
//	        	System.out.println(r);
//	        }
//	    } catch (InterruptedException e) {  
//	    	future.cancel(true);  
//	    } catch (ExecutionException e) {  
//	    	future.cancel(true);  
//	    } catch (TimeoutException e) {  
//	    	future.cancel(true);  
//	    } finally {  
//	        executor.shutdown();  
//	    }  
	}
	
//	private void updateAllowCache(Long comid,Long ticketId, Double ticketMoney){
//		logger.info("updateAllowCache>>>ticketId:"+ticketId+",ticketMoney:"+ticketMoney+",comid:"+comid);
//		if(ticketMoney > 0){
//			Double tcballow = ticketMoney;//ͣ���������Ĳ���
//			if(ticketId != null && ticketId > 0){
//				Map<String, Object> ticketMap = daService.getMap(
//						"select * from ticket_tb where id=? ",
//						new Object[] { ticketId });
//				Integer type = (Integer)ticketMap.get("type");
//				Integer resources = (Integer)ticketMap.get("resources");
//				if(type == 0 && resources == 1){//����ȯ
//					if(ticketMap.get("pmoney") != null){
//						Double pmoney = Double.valueOf(ticketMap.get("pmoney") + "");
//						logger.info("updateAllowCache>>>ticketId:"+ticketId+",ticketMoney:"+ticketMoney);
//						if(ticketMoney > pmoney){
//							tcballow = ticketMoney - pmoney;
//						}else{
//							tcballow = 0d;
//						}
//					}
//				}
//			}
//			logger.info("updateAllowCache>>>ticketId:"+ticketId+",tcballow:"+tcballow);
//			memcacheUtils.updateAllowanceCache(tcballow);
//			memcacheUtils.updateAllowCacheByPark(comid, tcballow);
//		}
//	}
	private void upload2ticket(String tk){
		ArrayList list = new ArrayList();
		 list.add(0);
		 list.add(-1);
		 List lists = daService.getAll("select * from ticket_tb where sync_state=? and lineorderid >?",list,1,10);
		 String ret = null;
		 if(lists!=null&&lists.size()>0){
			 String ticket = AjaxUtil.encodeUTF8(StringUtils.createJson(lists));
	       	 HttpProxy httpProxy = new HttpProxy();
	       	 Map parammap = new HashMap();
	       	 parammap.put("ticket", ticket);
	       	 try {
	       		 ret = httpProxy.doPost(CustomDefind.DOMAIN+"/syncInter.do?action=uploadTicket2Line&token="+tk, parammap);
	       		 if(ret!=null){
	       			 String[] strs = ret.split(",");
	           		 for (int i = 0; i < strs.length; i++) {
	    					if(strs[i]!=null){
	    						if(strs[i].startsWith("1")){
	    							String rets[] = strs[i].split("_");
	    							int r = daService.update("update ticket_tb set sync_state=? where id = ?", new Object[]{1,Long.valueOf(rets[1])});
	    						}
	    					}
	    				}
    				} 
       		 }catch (Exception e) {
					e.printStackTrace();
				}
		 }
	}
	public void updateShopTicket(Long orderid, Long uin){
		int r = daService.update("update ticket_tb set state=?,uin=?,utime=?,sync_state=? where orderid=? ", 
				new Object[]{1, uin, System.currentTimeMillis()/1000,0, orderid});
	}
	/**
	 * ����ֶ��¿��۸�
	 */
	public Map monthPrice(Map orderMap, List<Map<String, Object>> list,
			Long end_time, Map dayMap, Map nigthMap, Map assistMap) {
		logger.error("����ֶ��¿��۸�");
		// oMap=CountPrice.getAccount((Long)orderMap.get("create_time"),end_time,
		// dayMap, nigthMap,minPriceUnit,assistMap);
		Long create_time = (Long) orderMap.get("create_time");
		Map oMap = new HashMap();
		// {total=2.0, duration=24����, etime=15:50, btime=15:26, collect=2.0,
		// discount=0}
		// oMap.put("collect", 2.0);
		// oMap.put("collect", 2.0);
		// oMap.put("collect", 2.0);
		// oMap.put("collect", 2.0);
		double total = 0;
		if (list != null && list.size() > 0) {
			Map<String, Object> pMap = list.get(0);
			Integer b_time = (Integer) pMap.get("b_time");// �ײ�ÿ�쿪ʼ��Сʱ
			Integer e_time = (Integer) pMap.get("e_time");// �ײ�ÿ�������Сʱ
			Integer bmin = (Integer) pMap.get("bmin");// �ײ�ÿ�쿪ʼ�ķ���
			Integer emin = (Integer) pMap.get("emin");// �ײ�ÿ������ķ���
			Integer type = (Integer) pMap.get("type");// �ײ�ÿ������ķ���
			Long bt = (Long) pMap.get("bt");// �ײͿ�ʼ��ʱ��
			Long et = (Long) pMap.get("et");// �ײͽ�����ʱ��
			long durday = 0;
			long times = 0;
			boolean frist = true;
			// if(create_time<bt){//���¿�ʼǰ�����ĳ�
			// //�����ײͿ�ʼǰ�ļ۸�
			// if(end_time>bt){
			// oMap=CountPrice.getAccount(create_time,bt, dayMap,
			// nigthMap,0,assistMap);
			// if(oMap!=null&&oMap.get("collect")!=null){
			// total+=Double.parseDouble(oMap.get("collect")+"");
			// }
			// //�����ʱ����Ϊ0 �Ż�ʱ��ȥ��
			// if(dayMap!=null){
			// dayMap.put("first_times", 0);
			// dayMap.put("fprice", 0);
			// dayMap.put("free_time", 0);
			// }
			// if(nigthMap!=null){
			// nigthMap.put("first_times", 0);
			// nigthMap.put("fprice", 0);
			// nigthMap.put("free_time", 0);
			// }
			// frist = false;
			// //��ʱ�俪ʼ��Ų���ײͿ�ʼʱ
			// create_time = bt;
			// }else{
			// oMap=CountPrice.getAccount(create_time,bt, dayMap,
			// nigthMap,0,assistMap);
			// return oMap;
			// }
			// }else{
			// create_time = et;
			// }
			// if(end_time>et){
			// //�����ײͽ�����ļ۸�
			// oMap=CountPrice.getAccount(bt,end_time, dayMap, nigthMap,0,null);
			// if(oMap!=null&&oMap.get("collect")!=null){
			// total+=Double.parseDouble(oMap.get("collect")+"");
			// }
			// //�����ʱ����Ϊ0 �Ż�ʱ��ȥ��
			// if(dayMap!=null){
			// dayMap.put("first_times", 0);
			// dayMap.put("fprice", 0);
			// dayMap.put("free_time", 0);
			// }
			// if(nigthMap!=null){
			// nigthMap.put("first_times", 0);
			// nigthMap.put("fprice", 0);
			// nigthMap.put("free_time", 0);
			// }
			// //��ʱ�����ʱ���Ų���ײͽ���ʱ
			// end_time = et;
			// }
			//�ж���û�зⶥ��
//			double totalday = 0;
			Object dtotal24 = -1;
			if (dayMap != null)
				dtotal24 = dayMap.get("total24");
			double total24 = -1;
			Boolean b = StringUtils.isDouble(dtotal24 + "");
			if (b) {
				total24 = Double.parseDouble(dtotal24 + "");
			}
			if(b&&total24>0){//�зⶥ��
				if ((end_time - create_time) % (24 * 3600) == 0) {
					times = (end_time - create_time) / (24 * 3600);
				} else {
					times = (end_time - create_time) / (24 * 3600) + 1;
				}
				for (int i = 1; i <= times; i++) {// ÿһ�ηⶥһ��
					double totalday = 0;
//					Object dtotal24 = -1;
//					if (dayMap != null)
//						dtotal24 = dayMap.get("total24");
//					double total24 = -1;
//					Boolean b = StringUtils.isDouble(dtotal24 + "");
//					if (b) {
//						total24 = Double.parseDouble(dtotal24 + "");
//					}
					if (i == times) {
						ArrayList<String> monthPrice = monthTimes(create_time,
								end_time, b_time, e_time, bmin, emin, type);
						for(int j = 0;j<monthPrice.size();j++) {
							String tmp = monthPrice.get(j);
							String[] tmpArr = tmp.split("_");
							if (tmpArr.length == 2) {
								Long startTime = Long.parseLong(tmpArr[0]);
								Long endTime = Long.parseLong(tmpArr[1]);
								if(TimeTools.getTime_yyyyMMdd_HHmmss(endTime*1000).endsWith("00:00:00")){//����֮ǰ�ķֶ��賿24���Զ���ֶΣ������������Ҫ��   
									if(j+1<monthPrice.size()){
										String tmp2 = monthPrice.get(j+1);
										String[] tmpArr2 = tmp2.split("_");
										if (tmpArr2.length == 2) {
											endTime = Long.parseLong(tmpArr2[1]);
										}
										j++;
									}
								}
								if (frist) {
									// CountPrice.getAccount(start, end, dayMap,
									// nightMap, minPriceUnit, assistPrice)
									oMap = CountPrice
											.getAccount(startTime, endTime, dayMap,
													nigthMap, 0, assistMap);
									if (oMap != null && oMap.get("collect") != null) {
										totalday += Double.parseDouble(oMap
												.get("collect")
												+ "");
									}
									frist = false;
									// �����ʱ����Ϊ0 �Ż�ʱ��ȥ��
									if (dayMap != null) {
										dayMap.put("first_times", 0);
										dayMap.put("fprice", 0);
										dayMap.put("free_time", 0);
									}
									if (nigthMap != null) {
										nigthMap.put("first_times", 0);
										nigthMap.put("fprice", 0);
										nigthMap.put("free_time", 0);
									}
								} else {
									oMap = CountPrice.getAccount(startTime,
											endTime, dayMap, nigthMap, 0, null);
									if (oMap != null && oMap.get("collect") != null) {
										totalday += Double.parseDouble(oMap
												.get("collect")
												+ "");
									}
								}
							}
						}
					} else {
						ArrayList<String> monthPrice = monthTimes(create_time,
								create_time + 24 * 3600, b_time, e_time, bmin,
								emin, type);
						create_time = create_time + 24 * 3600;
//						for (String str : monthPrice) {
						for(int j = 0;j<monthPrice.size();j++) {
							String tmp = monthPrice.get(j);
							String[] tmpArr = tmp.split("_");
							if (tmpArr.length == 2) {
								Long startTime = Long.parseLong(tmpArr[0]);
								Long endTime = Long.parseLong(tmpArr[1]);
								if(TimeTools.getTime_yyyyMMdd_HHmmss(endTime*1000).endsWith("00:00:00")){//����֮ǰ�ķֶ��賿24���Զ���ֶΣ������������Ҫ��   
									if(j+1<monthPrice.size()){
										String tmp2 = monthPrice.get(j+1);
										String[] tmpArr2 = tmp2.split("_");
										if (tmpArr2.length == 2) {
											endTime = Long.parseLong(tmpArr2[1]);
										}
										j++;
									}
								}
								if (frist) {
									// CountPrice.getAccount(start, end, dayMap,
									// nightMap, minPriceUnit, assistPrice)
									oMap = CountPrice
											.getAccount(startTime, endTime, dayMap,
													nigthMap, 0, assistMap);
									if (oMap != null && oMap.get("collect") != null) {
										totalday += Double.parseDouble(oMap
												.get("collect")
												+ "");
									}
									frist = false;
									// �����ʱ����Ϊ0 �Ż�ʱ��ȥ��
									if (dayMap != null) {
										dayMap.put("first_times", 0);
										dayMap.put("fprice", 0);
										dayMap.put("free_time", 0);
									}
									if (nigthMap != null) {
										nigthMap.put("first_times", 0);
										nigthMap.put("fprice", 0);
										nigthMap.put("free_time", 0);
									}
								} else {
									oMap = CountPrice.getAccount(startTime,
											endTime, dayMap, nigthMap, 0, null);
									if (oMap != null && oMap.get("collect") != null) {
										totalday += Double.parseDouble(oMap
												.get("collect")
												+ "");
									}
								}
							}
						}
					}
					if (total24 >0&&totalday > total24) {// �ⶥ
						total += total24;
					}else{
						total += totalday;
					}
				}
			}else{
				ArrayList<String> monthPrice = monthTimes(create_time,
						end_time, b_time, e_time, bmin,
						emin, type);
				create_time = create_time + 24 * 3600;
//				for (String str : monthPrice) {
				for(int j = 0;j<monthPrice.size();j++) {
					String tmp = monthPrice.get(j);
					String[] tmpArr = tmp.split("_");
					if (tmpArr.length == 2) {
						Long startTime = Long.parseLong(tmpArr[0]);
						Long endTime = Long.parseLong(tmpArr[1]);
						if(TimeTools.getTime_yyyyMMdd_HHmmss(endTime*1000).endsWith("00:00:00")){//����֮ǰ�ķֶ��賿24���Զ���ֶΣ������������Ҫ��   
							if(j+1<monthPrice.size()){
								String tmp2 = monthPrice.get(j+1);
								String[] tmpArr2 = tmp2.split("_");
								if (tmpArr2.length == 2) {
									endTime = Long.parseLong(tmpArr2[1]);
								}
								j++;
							}
						}
						if (frist) {
							// CountPrice.getAccount(start, end, dayMap,
							// nightMap, minPriceUnit, assistPrice)
							oMap = CountPrice
									.getAccount(startTime, endTime, dayMap,
											nigthMap, 0, assistMap);
							if (oMap != null && oMap.get("collect") != null) {
								total += Double.parseDouble(oMap
										.get("collect")
										+ "");
							}
							frist = false;
							// �����ʱ����Ϊ0 �Ż�ʱ��ȥ��
							if (dayMap != null) {
								dayMap.put("first_times", 0);
								dayMap.put("fprice", 0);
								dayMap.put("free_time", 0);
							}
							if (nigthMap != null) {
								nigthMap.put("first_times", 0);
								nigthMap.put("fprice", 0);
								nigthMap.put("free_time", 0);
							}
						} else {
							oMap = CountPrice.getAccount(startTime,
									endTime, dayMap, nigthMap, 0, null);
							if (oMap != null && oMap.get("collect") != null) {
								total += Double.parseDouble(oMap
										.get("collect")
										+ "");
							}
						}
					}
				}
			}
		}
		oMap.put("collect", total);
		return oMap;
	}
	//����û�зⶥ�ҵķ�������ʱû��  �´�����ʱ������õ�
	private Double noTotal24(Long create_time, Long end_time, Integer b_time, Integer e_time, Integer bmin, Integer emin, Integer type, Map dayMap, Map nigthMap, Map assistMap){
		Map oMap = new HashMap();
		double total = 0;
		boolean frist = true;
		ArrayList<String> monthPrice = monthTimes(create_time,end_time, b_time, e_time, bmin,emin, type);
		create_time = create_time + 24 * 3600;
//		for (String str : monthPrice) {
		for(int j = 0;j<monthPrice.size();j++) {
			String tmp = monthPrice.get(j);
			String[] tmpArr = tmp.split("_");
			if (tmpArr.length == 2) {
				Long startTime = Long.parseLong(tmpArr[0]);
				Long endTime = Long.parseLong(tmpArr[1]);
				if(TimeTools.getTime_yyyyMMdd_HHmmss(endTime*1000).endsWith("00:00:00")){//����֮ǰ�ķֶ��賿24���Զ���ֶΣ������������Ҫ��   
					if(j+1<monthPrice.size()){
						String tmp2 = monthPrice.get(j+1);
						String[] tmpArr2 = tmp2.split("_");
						if (tmpArr2.length == 2) {
							endTime = Long.parseLong(tmpArr2[1]);
						}
						j++;
					}
				}
				if (frist) {
					// CountPrice.getAccount(start, end, dayMap,
					// nightMap, minPriceUnit, assistPrice)
					oMap = CountPrice
							.getAccount(startTime, endTime, dayMap,
									nigthMap, 0, assistMap);
					if (oMap != null && oMap.get("collect") != null) {
						total += Double.parseDouble(oMap
								.get("collect")
								+ "");
					}
					frist = false;
					// �����ʱ����Ϊ0 �Ż�ʱ��ȥ��
					if (dayMap != null) {
						dayMap.put("first_times", 0);
						dayMap.put("fprice", 0);
						dayMap.put("free_time", 0);
					}
					if (nigthMap != null) {
						nigthMap.put("first_times", 0);
						nigthMap.put("fprice", 0);
						nigthMap.put("free_time", 0);
					}
				} else {
					oMap = CountPrice.getAccount(startTime,
							endTime, dayMap, nigthMap, 0, null);
					if (oMap != null && oMap.get("collect") != null) {
						total += Double.parseDouble(oMap
								.get("collect")
								+ "");
					}
				}
			}
		}
		return total;
	}
	/**
	 * �ֶ��¿��ֶ�
	 * 
	 * @param create_time
	 * @param end_time
	 * @param btime
	 * @param etime
	 * @param bmin
	 * @param emin
	 * @param type
	 * @return
	 */
	public static ArrayList<String> monthTimes(Long create_time, Long end_time,
			Integer btime, Integer etime, Integer bmin, Integer emin,
			Integer type) {
		ArrayList<String> resultList = new ArrayList<String>();
		if (create_time < end_time) {
			Integer b_time = btime;// (Integer)pMap.get("b_time");
			Integer e_time = etime;// (Integer)pMap.get("e_time");
			long durday = 0;
			// ������˼���
			long b = create_time;
			while (b < end_time) {
				long a = getToDayBeginTime(b) + 3600 * 24;
				durday += 1;
				b = a;
			}
			Long todayb = getToDayBeginTime(create_time);// ���쿪ʼ�賿��һ��
			Long todaypb = todayb + 60 * 60 * b_time + bmin * 60;// �����ײͿ�ʼ
			long todaype = todayb + 60 * 60 * e_time + emin * 60;// �����ײͽ���
			Integer packagetype = type;// (Integer)pMap.get("type");//0:ȫ�� 1ҹ��
			// 2�ռ�
			if (type == 1) {
				todaype = todayb + 3600 * 24 + 60 * 60 * e_time + emin * 60;
			}
			for (int i = 1; i <= durday; i++) {// �������ѭ�����ٴΣ�ÿ�����2�μ۸�
				if (packagetype == 2) {// 1ҹ�� 2�ռ�
					if (create_time < todaypb) {// �ײͿ�ʼʱǰ������
						if (i == durday) {
							if (end_time > todaypb) {
								// todo���㿪ʼʱ�䵽�ײͿ�ʼʱ��
								System.out
										.println("����ʱ���1:"
												+ TimeTools
														.getTime_yyyyMMdd_HHmmss(create_time * 1000)
												+ "--->"
												+ TimeTools
														.getTime_yyyyMMdd_HHmmss(todaypb * 1000));
								resultList.add(create_time + "_" + todaypb);
								if (end_time > todaype) {
									// todo�����ײͽ�����end_time
									System.out
											.println("����ʱ���2:"
													+ TimeTools
															.getTime_yyyyMMdd_HHmmss(todaype * 1000)
													+ "--->"
													+ TimeTools
															.getTime_yyyyMMdd_HHmmss(end_time * 1000));
									resultList.add(todaype + "_" + end_time);
								}
							} else {
								// todo���㿪ʼʱ�䵽����ʱ��
								System.out
										.println("����ʱ���3:"
												+ TimeTools
														.getTime_yyyyMMdd_HHmmss(create_time * 1000)
												+ "--->"
												+ TimeTools
														.getTime_yyyyMMdd_HHmmss(end_time * 1000));
								resultList.add(create_time + "_" + end_time);
							}
						} else {
							// todo���㿪ʼʱ�䵽�ײͿ�ʼʱ
							System.out
									.println("����ʱ���4:"
											+ TimeTools
													.getTime_yyyyMMdd_HHmmss(create_time * 1000)
											+ "--->"
											+ TimeTools
													.getTime_yyyyMMdd_HHmmss(todaypb * 1000));
							resultList.add(create_time + "_" + todaypb);
							// todo�����ײͽ���ʱ��24��
							System.out
									.println("����ʱ���5:"
											+ TimeTools
													.getTime_yyyyMMdd_HHmmss(todaype * 1000)
											+ "--->"
											+ TimeTools
													.getTime_yyyyMMdd_HHmmss((todayb + 24 * 3600) * 1000));
							resultList
									.add(todaype + "_" + (todayb + 24 * 3600));
							create_time = todayb + 24 * 3600;
							todayb = todayb + 24 * 3600;
							todaypb = (todaypb + 24 * 3600);
							todaype = (todaype + 24 * 3600);
						}
					} else {// �ײͿ�ʼʱ�������
						if (i == durday) {
							// todo���㿪ʼʱ�䵽�ײͿ�ʼʱ��
							if (end_time > todaype) {
								if (create_time>todaype) {
									// todo�����ײͽ�����end_time
									System.out
											.println("����ʱ���6:"
													+ TimeTools
															.getTime_yyyyMMdd_HHmmss(create_time * 1000)
													+ "--->"
													+ TimeTools
															.getTime_yyyyMMdd_HHmmss(end_time * 1000));
									resultList.add(create_time + "_" + end_time);
								}else{
									// todo�����ײͽ�����end_time
									System.out
											.println("����ʱ���7:"
													+ TimeTools
															.getTime_yyyyMMdd_HHmmss(todaype * 1000)
													+ "--->"
													+ TimeTools
															.getTime_yyyyMMdd_HHmmss(end_time * 1000));
									resultList.add(todaype + "_" + end_time);
								}
							}
						} else {
							// todo���㿪ʼʱ�䵽
							// System.out.println("����ʱ���3:"+todaype+"--->23:59:59");
							if (create_time > todaype) {
								System.out
										.println("����ʱ���8:"
												+ TimeTools
														.getTime_yyyyMMdd_HHmmss(create_time * 1000)
												+ "--->"
												+ TimeTools
														.getTime_yyyyMMdd_HHmmss((todayb + 24 * 3600) * 1000));
								resultList.add(create_time + "_"
										+ (todayb + 24 * 3600));
							} else {
								System.out
										.println("����ʱ���9:"
												+ TimeTools
														.getTime_yyyyMMdd_HHmmss(todaype * 1000)
												+ "--->"
												+ TimeTools
														.getTime_yyyyMMdd_HHmmss((todayb + 24 * 3600) * 1000));
								resultList.add(todaype + "_"
										+ (todayb + 24 * 3600));
							}
							create_time = todayb + 24 * 3600;
							todayb = todayb + 24 * 3600;
							todaypb = (todaypb + 24 * 3600);
							todaype = (todaype + 24 * 3600);
						}
					}
				} else if (packagetype == 1) {// 0:ȫ�� 1ҹ�� 2�ռ�
					if (end_time > create_time) {
						if (create_time < todaypb) {// �ײͿ�ʼǰ������
							if(create_time<todaype-24*3600){
								create_time = todaype-24*3600;
							}
							// ���㿪ʼʱ�䵽�ײͿ�ʼ
							if (i == durday) {
								if(end_time>todaypb){
									System.out.println("����ʱ���1:"+TimeTools.getTime_yyyyMMdd_HHmmss(create_time * 1000)+ "--->"+ TimeTools.getTime_yyyyMMdd_HHmmss(todaypb * 1000));
									resultList.add(create_time + "_" + todaypb);
								}else{
									System.out.println("����ʱ���2:"+TimeTools.getTime_yyyyMMdd_HHmmss(create_time * 1000)+ "--->"+ TimeTools.getTime_yyyyMMdd_HHmmss(end_time * 1000));
									resultList.add(create_time + "_" + end_time);
								}
							} else {
								System.out.println("����ʱ���3:"+ TimeTools.getTime_yyyyMMdd_HHmmss(create_time * 1000)+ "--->"+ TimeTools.getTime_yyyyMMdd_HHmmss(todaypb * 1000));
								resultList.add(create_time + "_" + todaypb);
							}
							create_time = todaype;
							todayb = todaype;
							todaypb = (todaypb + 24 * 3600);
							todaype = (todaype + 24 * 3600);
						} else {// �ײͿ�ʼ������ģ�����ֱ�ӽ�ָ�붼ָ��ڶ���Ľ�����
							create_time = todaype;
							todayb = todaype;
							todaypb = (todaypb + 24 * 3600);
							todaype = (todaype + 24 * 3600);
						}
					}
				}
			}
		}
		return resultList;

	}
	public static Long getToDayBeginTime(long date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String today = sdf.format(new Date(date * 1000));
		today = today.substring(0, 10) + " 00:00:00";
		long millSeconds = (new GregorianCalendar()).getTimeInMillis();
		try {
			millSeconds = sdf.parse(today).getTime();
		} catch (Exception e) {

		}
		return new Long(millSeconds / 1000);
	}
}
