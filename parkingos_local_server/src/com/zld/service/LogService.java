package com.zld.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zld.AjaxUtil;
import com.zld.CustomDefind;
import com.zld.impl.PublicMethods;
import com.zld.impl.PushtoSingle;
import com.zld.utils.HttpProxy;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;

@Service
public class LogService {

	
	@Autowired
	private DataBaseService databasedao;
	@Autowired
	private PushtoSingle pushtoSingle;
//	@Autowired
//	private MemcacheUtils memcacheUtils;
	@Autowired
	private PublicMethods publicMethods;
	
	private Logger logger = Logger.getLogger(LogService.class);
	/**
	 * 
	 * @param dService
	 * @param comid
	 * @param uin
	 * @param log
	 * @type:
	 * 0:����������1�����㶩��,2:�Żݣ�3:�ֽ��շ�
	 */
	public void updateOrderLog(Long comid,Long uin,String log,Integer type){
		databasedao.update("insert into order_log_tb (comid,uin,create_time,log,type) values (?,?,?,?,?)", 
				new Object[]{comid,uin,System.currentTimeMillis()/1000,log,type});
	}
	/**
	 * 
	 * @param dService
	 * @param comid
	 * @param uin
	 * @param log
	 * @param type 
	 * 100:����ͣ������101���޸�ͣ������102��ɾ��ͣ������
	 * 201���޸Ĺ���Ա��202������ͣ��Ա��203���޸�ͣ��Ա��204������ͣ��Ա��
	 * 205��ɾ��ͣ��Ա��206���޸����룬207����Ӽ۸�208���޸ļ۸�209��ɾ���۸�210����Ӱ��²�Ʒ��211��ɾ�����£�
	 * 300������г�רԱ��301�޸��г�רԱ��302ɾ���г�רԱ��
	 * 400�������Ȧ��401���༭��Ȧ��402��ɾ����Ȧ
	 */
	public void updateSysLog(Long comid,String uid,String log,Integer type){
		databasedao.update("insert into user_log_tb (comid,uid,create_time,logs,type) values (?,?,?,?,?)", 
				new Object[]{comid,uid,System.currentTimeMillis()/1000,log,type});
	}
	/**
	 * ������־��
	 * @param comid ͣ�������
	 * @param uin �շ�Ա�����Ա��
	 * @param numer ������
	 */
	public void updateShareLog(Long comid,Long uin,Integer numer){
		databasedao.update("insert into share_log_tb (comid,uin,create_time,s_number) values (?,?,?,?)", 
				new Object[]{comid,uin,System.currentTimeMillis()/1000,numer});
	}
	
	/**
	 * //д������Ϣ
	 * @param comId
	 * @param state  ---- 0:δ���㣬1����֧����2��֧�����, -1:֧��ʧ��
	 * @param uin �ʺţ��û����շ�Ա��
	 * @param body ���ƺ�
	 * @param orderId �������
	 * @param total ���
	 * @param duration ʱ��
	 * @param issale �Ƿ���ۣ�0�� 1:��
	 * @param btime ��ʼʱ��UTC
	 * @param etime ����ʱ��UTC
	 * @mtype  0:������Ϣ��1����λԤ����Ϣ  2:��ֵ�����Ʒ  3ֱ��������Ϣ���շ�Ա�ã� 4Ibeacon�����Ϣ(�շ�Ա)  9Ibeacon֧����Ϣ
	 */
	public void insertMessage(Long comId,Integer state,Long uin,String body,Long orderId,
			Double total,String duration,Integer isSale,Long  btime,Long etime,Integer mtype){
		Long id = databasedao.getLong("SELECT nextval('seq_order_message_tb'::REGCLASS) AS newid", null);
		int result = databasedao.update("insert into order_message_tb (id,comid,state,uin,create_time,car_number," +
				"orderid,order_total,duartion,is_sale,btime,etime,message_type) values (?,?,?,?,?,?,?,?,?,?,?,?,?)", 
				new Object[]{id,comId,state,uin,System.currentTimeMillis()/1000,body,
				orderId,total,duration,isSale,btime,etime,mtype});
		if(result==1){//��Ϣд��ɹ�
			//���û���ɫ����¼ID,����ͨ�����Ʒ�����Ϣ
			String cid  = "";
			Map userMap = databasedao.getPojo("select cid from user_info_tb where id =? and auth_flag=?",new Object[]{uin,4});
			if(userMap!=null&&userMap.get("cid")!=null){
				cid =(String) userMap.get("cid");
			}
			//����,����Ϣ
			Map messageMap = databasedao.getPojo("select * from order_message_tb where id=?", new Object[]{id});
			String ret = getMessage(uin,messageMap);
			if(cid!=null&&cid.length()>10){
				if(ret!=null&&!"".equals(ret)){
					if(cid.length()>32){//IOS��Ϣ
						pushtoSingle.sendMessageByApns(uin,ret, cid);
					}else {//android��Ϣ
						pushtoSingle.sendSingle(cid, ret);
					}
				}
			}
			/*if(state!=1){//���Ǵ�֧����Ϣ��д�뻺�棬2.0�汾����Ϣ�ӻ����ȡ
				Map<Long, String> messCacheMap = memcacheUtils.doMapLongStringCache("caruser_messages", null, null);
				if(messCacheMap==null)
					messCacheMap = new HashMap<Long, String>();
				messCacheMap.put(uin, ret);
				memcacheUtils.doMapLongStringCache("caruser_messages", messCacheMap, "update");
			}*/
//			uploadOrder2Line(id);
		}
	}
	/**
	 * //д�շ�Ա��Ϣ�����շ�Ա��ʱȡ��Ϣ��
	 * @param comId
	 * @param state  ---- 0:δ���㣬1����֧����2��֧�����, -1:֧��ʧ��
	 * @param uin �ʺţ��û����շ�Ա��
	 * @param body ���ƺ�
	 * @param orderId �������
	 * @param total ���
	 * @param duration ʱ��
	 * @param issale �Ƿ���ۣ�0�� 1:��
	 * @param btime ��ʼʱ��UTC
	 * @param etime ����ʱ��UTC
	 * @mtype  0:������Ϣ��1����λԤ����Ϣ  2:��ֵ�����Ʒ  3ֱ��������Ϣ���շ�Ա�ã� 4Ibeacon�����Ϣ(�շ�Ա) 5:������Ϣ 6��ҳ֪ͨ��Ϣ  7������ȯ��Ϣ 8�Ƽ�������֪ͨ9Ibeacon֧����Ϣ
	 */
//	public void insertParkUserMessage(Long comId,Integer state,Long uin,String body,Long orderId,
//			Double total,String duration,Integer isSale,Long  btime,Long etime,Integer mtype){
//		Long id = databasedao.getLong("SELECT nextval('seq_order_message_tb'::REGCLASS) AS newid", null);
//		int result = databasedao.update("insert into order_message_tb (id,comid,state,uin,create_time,car_number," +
//				"orderid,order_total,duartion,is_sale,btime,etime,message_type) values (?,?,?,?,?,?,?,?,?,?,?,?,?)", 
//				new Object[]{id,comId,state,uin,System.currentTimeMillis()/1000,body,
//				orderId,total,duration,isSale,btime,etime,mtype});
//		if(result==1){//��Ϣд��ɹ�
//			//д���շ�Ա��Ϣ����
//			String ret ="{}";
//			if(mtype==3){//������Ϣ
//				ret = "{\"mtype\":"+mtype+",\"info\":{\"orderid\":\""+orderId+"\""+
//						",\"carnumber\":\""+body+"\",\"duration\":\""+duration+"\"," +
//						"\"state\":\""+state+"\"}}";
//			}else if(mtype==4){//�շ�Ա�뿪����վ֪ͨ
//				ret = "{\"mtype\":"+mtype+",\"info\":{}}";
//			}else if(mtype==5){//�շ�Ա����֪ͨ
//				int limit = 0;
//				if(comId == -2){
//					limit = 1;
//				}
//				int fivelimit = 0;
//				Long count = databasedao.getLong("select count(*) from reward_account_tb r,ticket_tb t where r.ticket_id=t.id and r.type=? and r.target=? and r.create_time>? and t.money=? and r.uin=? ",
//						new Object[] { 1, 2, TimeTools.getToDayBeginTime(), 5, uin });
//				if(count >= 10){
//					fivelimit = 1;
//				}
//				Long fivescore = 20 * (count + 1);
//				ret = "{\"mtype\":"+mtype+",\"info\":{\"carnumber\":\""+body+"\",\"uin\":\""+orderId+"\",\"rcount\":\""+duration+"\",\"total\":\""+total+"\",\"limit\":\""+limit+"\",\"fivelimit\":\""+fivelimit+"\",\"fivescore\":\""+fivescore+"\"}}";
//				System.out.println("rewardmessage��ret:"+ret);
//			}else if(mtype==8){//�Ƽ�������֪ͨ
//				ret = "{\"mtype\":"+mtype+",\"info\":{\"mobile\":\""+body+"\",\"uin\":\""+orderId+"\",\"total\":\""+total+"\"}}";
//			}else {
//				Map<String, Object> infomMap = new HashMap<String, Object>();
//				infomMap.put("btime",TimeTools.getTime_yyMMdd_HHmm(btime*1000).substring(9));
//				infomMap.put("etime", TimeTools.getTime_yyMMdd_HHmm(etime*1000).substring(9));
//				infomMap.put("carnumber", body);
//				infomMap.put("duration",duration);
//				infomMap.put("total",total);
//				infomMap.put("state",state);//0:δ֧�� 1����֧�� 
//				infomMap.put("orderid",orderId);
//				String json = StringUtils.createJson(infomMap);
//				ret= "{\"mtype\":"+mtype+",\"info\":"+json+"}";
//			}
//			databasedao.update("update order_message_tb set already_read =? where id=?", new Object[]{1,id});
//			Map<Long, String> messCacheMap = memcacheUtils.doMapLongStringCache("parkuser_messages", null, null);
//			if(messCacheMap==null)
//				messCacheMap = new HashMap<Long, String>();
//			messCacheMap.put(uin, ret);
//			memcacheUtils.doMapLongStringCache("parkuser_messages", messCacheMap, "update");
//		}
//	}
	
	
	/**
	 * д����Ϣ���棬�ڿͻ��˶�ȡ(��ʱȡ��Ϣ)
	 * @param type 6��ҳ������Ϣ 7:������ȯ֪ͨ
	 * @param uin  �û�/�շ�Ա�ʺ�
	 * @param infoMap  ������Ϣ
	 */
//	public void insertParkUserMesg(Integer type, Map<String, Object> infoMap){
//		Map<Long, String> messCacheMap = memcacheUtils.doMapLongStringCache("parkuser_messages", null, null);
//		if(messCacheMap==null){
//			messCacheMap = new HashMap<Long, String>();
//		}
//		String ret ="{}";
//		if(type == 6){
//			ret = "{\"mtype\":"+type+",\"info\":{}}";
//			List<Object> uins = new ArrayList<Object>();
//			if(infoMap.get("uins") != null){
//				uins = (List)infoMap.get("uins");
//			}
//			for(Object object : uins){
//				Long uin = (Long)object;
//				messCacheMap.put(uin, ret);
//			}
//			System.out.print("notice msg>>>type:"+type+",֪ͨ����:"+uins.size());
//		}else if(type == 7){
//			ret = "{\"mtype\":"+type+",\"info\":{\"carnumber\":\""+infoMap.get("carnumber")+"\",\"score\":\""+infoMap.get("score")+"\",\"tmoney\":\""+infoMap.get("tmoney")+"\"}}";
//			Long uin = (Long)infoMap.get("uin");
//			messCacheMap.put(uin, ret);
//			System.out.print("take ticket msg>>>type:"+type+",uid:"+uin+",carnumber:"+infoMap.get("carnumber"));
//		}
//		memcacheUtils.doMapLongStringCache("parkuser_messages", messCacheMap, "update");
//	}
	
	/**
	 * д��ϵͳ��Ϣ���ڿͻ��˶�ȡ
	 * @param type 0 ֧��ʧ������1 �������2 �Զ�֧������3 ע������ 4ͣ���볡����5�����6 �Ƽ���Ϣ7�տ����� 8��ֵ��Ϣ
	 * @param uin  �û�/�շ�Ա�ʺ�
	 * @param content ����
	 * @param title ����
	 * @return Ӱ�����ݿ��¼��
	 */
	public int insertUserMesg(Integer type,Long uin,String content,String title){
		int ret =databasedao.update("insert into user_message_tb(type,ctime,uin,title,content) values(?,?,?,?,?)",
				new Object[]{type,System.currentTimeMillis()/1000,uin,title,content} );
		return ret;
	}
	
	
	/**
	 * д��ϵͳ��Ϣ���ڿͻ��˶�ȡ
	 * @param type 0 ֧��ʧ������1 �������2 �Զ�֧������3 ע������ 4ͣ���볡����5�����6 �Ƽ���Ϣ7�տ����� 8��ֵ��Ϣ
	 * @param uin  �û�/�շ�Ա�ʺ�
	 * @param content ����
	 * @param title ����
	 * @return Ӱ�����ݿ��¼��
	 */
	public int insertParkUserMesg(Integer type,Long uin,String content,String title){
		int ret =databasedao.update("insert into parkuser_message_tb(type,ctime,uin,title,content) values(?,?,?,?,?)",
				new Object[]{type,System.currentTimeMillis()/1000,uin,title,content} );
		return ret;
	}
	
	/**
	 * д�û�֧���˻���Ϣ
	 * @param type  0:֧������1:΢��
	 * @param uin  �����˺�
	 * @param account ����֧���˺�
	 * @return
	 */
//	public int insertUserAccountMesg(Integer type,Long uin,String account){
//		int ret =databasedao.update("insert into user_payaccount_tb(type,ctime,uin,account) values(?,?,?,?)",
//				new Object[]{type,System.currentTimeMillis()/1000,uin,account} );
//		if(ret==1){//��д��
//			if(!publicMethods.isAuthUser(uin)){
//				List list = databasedao.getAll("select distinct uin from user_payaccount_tb where account=? ", new Object[]{account});
//				if(list!=null&&list.size()>2){//���˻���Ϊ�������ϳ�ֵ����ǰ�ĳ�����Ϊ������
//					//д���������
//					Long ntime = System.currentTimeMillis()/1000;
//					try {
//						String atype = "֧����";
//						if(type==1){
//							atype="΢��";
//						}else if(type == 2){
//							atype = "΢�Ź��ں�";
//						}
//						List<Long> whiteUsers = memcacheUtils.doListLongCache("zld_white_users", null, null);
//						if(whiteUsers==null||!whiteUsers.contains(uin)){
//							List<Long> blackUsers = memcacheUtils.doListLongCache("zld_black_users", null, null);
//							if(!blackUsers.contains(uin)){
//								ret = databasedao.update("insert into zld_black_tb(ctime,utime,uin,state,remark) values(?,?,?,?,?)",
//										new Object[]{ntime,ntime,uin,0,"��ֵ�˻�("+atype+")Ϊ����˻���ֵ :"+account});
//								System.out.println(">>>��ֵ���������,uin:"+uin+",account:"+account+"����� ��"+ret);
//							}
//							if(ret==1){
//								//�������������
//								//System.err.println(">>>zld black users :"+blackUsers);
//								if(blackUsers==null){
//									blackUsers = new ArrayList<Long>();
//									blackUsers.add(uin);
//									memcacheUtils.doListLongCache("zld_black_users", blackUsers, "update");
//								}else {
//									if(!blackUsers.contains(uin)){
//										blackUsers.add(uin);
//										memcacheUtils.doListLongCache("zld_black_users", blackUsers, "update");
//									}
//								}
//							}
//						}else{
//							System.out.println(">>zld_white_tb>>>>>uin:"+uin+",account:"+account+"���ڰ������У�������:"+whiteUsers);
//						}
//					} catch (Exception e) {
//						System.out.println(">>>��ֵ�������������,uin:"+uin+",account:"+account+"���Ѿ����ڣ�");
//						e.printStackTrace();
//					}
//				}
//			}else{
//				logger.info("LogService>>>>insertUserAccountMesg>>>��ǰ��ֵ��������֤�û�����ȥ�жϸ��˻��Ƿ�Ϊ�������ϳ�ֵ");
//			}
//		}
//		return ret;
//	}
	
	/**
	 * @param type ���ͣ�1:����(��һ��)��2:NFC����1�� 3:��������10�֣�,4:���ƽ���(��1��),5�Ƽ����� 
	 * @param uin �շ�Ա�ʺ�
	 * @param btime ��ʼʱ�䣬ÿ�������һ����¼
	 */
	public void updateScroe(int type,Long uin,Long comId){
		
		/*Long endtime=1424016000L;//2��16��ֹͣ
		Long begintime = 1425225600L;//3��2�տ�ʼ
		Long time = System.currentTimeMillis()/1000;
		if(time>endtime&&time<begintime){//ֹͣ���� 
			System.err.println(">>>ֹͣ���֣�");
			return ;
		}*/
		if(uin==null)
			return;
		if(type<1||type>5)
			return ;
		//System.out.println(">>>>>>>>>>���Ļ��� ��"+type);
	//	String monday = StringUtils.getMondayOfThisWeek();
		Long btime = TimeTools.getToDayBeginTime();//getLongMilliSecondFrom_HHMMDD(monday)/1000;
//		if(btime==null){
//			btime = TimeTools.getLongMilliSecondFrom_HHMMDD(monday)/1000;
//		}
		Long t1 = 1419177600L;
		Long nt = System.currentTimeMillis()/1000;
		//System.out.println(">>>>>>>����:comid="+comId+",ʱ�� :"+nt+",��ʼʱ�䣺"+t1);
		if(comId!=null&&comId>0){//�Ƽ�ʱ���ж��Ƿ�֧������֧�� 
			//if(nt>t1){
			//if(type<5){
				Integer epay = 0;
				Map comMap = databasedao.getPojo("select epay from com_info_tb where id=? ", new Object[]{comId});
				if(comMap!=null&&comMap.get("epay")!=null)
					epay= (Integer)comMap.get("epay");
				//System.out.println(">>>>>�����Ƿ���Ч="+epay);
				if(epay==0){
					System.out.println(">>>>>��֧�ֵ���֧����������........");
					return ;
				}
			//}
			//}
		}else {
			return ;
		}
		
		Long count = databasedao.getLong("select count(*) from collector_scroe_tb where create_time=?" +
				" and uin=? ", new Object[]{btime,uin});
		String sql ="";// "insert into collector_scroe_tb (uin,lala_scroe,nfc_score,praise_scroe,create_time) values (?,?,?,?,?)";
		Object values[]= null;
		
		if(count>0){//���»���
			sql = "update collector_scroe_tb "; 
			if(type==1){
				sql +=" set lala_scroe=lala_scroe+?";
				values=new Object[]{0.1,uin,btime};
			}else if(type==2){
				sql +=" set nfc_score=nfc_score+?";
				values=new Object[]{2,uin,btime};
			}else if(type==3){
				sql +=" set praise_scroe=praise_scroe+?";
				values=new Object[]{-10,uin,btime};
			}else if(type==4){
				sql +=" set pai_score=pai_score+?";
				values=new Object[]{2,uin,btime};
			}else if(type==5){
				sql +=" set recom_scroe=recom_scroe+?";
				values=new Object[]{1,uin,btime};
			}else if(type==6){
				sql +=" set nfc_score=nfc_score+?";
				values=new Object[]{0.01,uin,btime};
			}else if(type==7){
				sql +=" set pai_score=pai_score+?";
				values=new Object[]{0.01,uin,btime};
			}
			sql +=" where uin=? and create_time=? ";
		}else {//�½�����
			sql =  "insert into collector_scroe_tb (uin,lala_scroe,nfc_score,praise_scroe,create_time,pai_score,recom_scroe) values (?,?,?,?,?,?,?)";
			if(type==1){
				values=new Object[]{uin,0.1,0d,0,btime,0,0};
			}else if(type==2){
				values=new Object[]{uin,0,2d,0,btime,0,0};
			}else if(type==3){
				values=new Object[]{uin,0,0d,-10,btime,0,0};
			}else if(type==4){
				values=new Object[]{uin,0,0d,0,btime,2,0};
			}else if(type==5){
				values=new Object[]{uin,0,0d,0,btime,0,1};
			}else if(type==6){
				values=new Object[]{uin,0,0.01d,0,btime,0,0};
			}else if(type==7){
				values=new Object[]{uin,0,0d,0,btime,0.01,0};
			}
		}
		databasedao.update(sql, values);
	}
	
	//ȡ��Ϣ
	private String getMessage(Long uin,Map messageMap){
		String result = "";
		if (messageMap != null && !messageMap.isEmpty()) {
			Integer mtype =(Integer) messageMap.get("message_type");
			if(mtype==null){
				return result;
			}
			Map<String, Object> infomMap = new HashMap<String, Object>();
			if(mtype==2){//��ֵ�������Ʒ��Ϣ
				infomMap.put("result",  messageMap.get("state"));
				infomMap.put("errmsg",  messageMap.get("duartion"));
				infomMap.put("bonusid", messageMap.get("orderid"));
			}else if(mtype==0||mtype==9){
				Long comId = (Long)messageMap.get("comid");
				Long orderId = (Long)messageMap.get("orderid");
				Map<String, Object> comMap = databasedao.getPojo("select company_name from com_info_tb where id=?", new Object[]{comId});
				String cname ="";
				if(comMap!=null&&comMap.get("company_name")!=null)
					cname = (String)comMap.get("company_name");
				//String cname = (String)databasedao.getObject("select company_name from com_info_tb where id=?",new Object[]{comId}, String.class);
				infomMap.put("parkname",cname);
				infomMap.put("btime", messageMap.get("btime"));
				infomMap.put("etime", messageMap.get("etime"));
				infomMap.put("total", messageMap.get("order_total"));
				infomMap.put("state", messageMap.get("state"));//0:δ֧�� 1����֧�� 
				infomMap.put("orderid",orderId);
				//���
				Long count = getBonusId(uin, orderId);
				if(count!=null&&count>0){
					infomMap.put("bonusid", count);
				}
			}
			String json =StringUtils.createJson(infomMap);
			result =  "{\"mtype\":\""+messageMap.get("message_type")+"\",\"msgid\":\""+messageMap.get("id")+"\",\"info\":"+json+"}";
		}
		return result;
	}
	
	
	private Long getBonusId(Long uin,Long orderId){
		Long count = null;
		//���
//		Map bMap  =pOnlyReadService.getMap("select id from bouns_tb where uin=? and order_id=? and ctime > ? ",
//				new Object[]{uin,orderId,TimeTools.getToDayBeginTime()});
		Map bMap  = null;
		if(orderId==997||orderId==998||orderId==-1){
			bMap= databasedao.getMap("select id,btime from order_ticket_tb where uin=? and order_id=? and ctime > ? order by id desc limit ?",
					new Object[]{uin,orderId,TimeTools.getToDayBeginTime(),1});
			if(bMap!=null){
				Long btime = (Long)bMap.get("btime");
				if(btime!=null&&btime>10000){//�Ѿ�����������ٷ���
					bMap=null;
				}
			}
		}else {
			bMap =databasedao.getMap("select id from order_ticket_tb where uin=? and order_id=? and ctime > ? ",
					new Object[]{uin,orderId,TimeTools.getToDayBeginTime()});
		}
		if(bMap!=null&&bMap.get("id")!=null)
			count = (Long)bMap.get("id");
		
		return count;
	}
	public void uploadOrder2Line(final Long orderid){
	    ExecutorService executor = Executors.newSingleThreadExecutor();  
	    FutureTask<String> future =  
	           new FutureTask<String>(new Callable<String>() {//ʹ��Callable�ӿ���Ϊ�������  
	             public String call() {
	            	 System.out.println(orderid);
	            	 Map map = databasedao.getMap("select * from order_message_tb where id = ?",new Object[]{orderid});
	            	 String order = AjaxUtil.encodeUTF8(StringUtils.createJson(map));
	            	 HttpProxy httpProxy = new HttpProxy();
	            	 Map parammap = new HashMap();
	            	 parammap.put("order_message", order);
	            	 String ret = null;
	            	 try {
	            		 ret = httpProxy.doPost(CustomDefind.DOMAIN+"/syncInter.do?action=uploadOrderMsg2Line&type=1", parammap);
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println("tongbufanhui:"+ret);
					return ret;  
	           }});  
	    executor.execute(future);  
	    try {  
	        String result = future.get(3000, TimeUnit.MILLISECONDS); //ȡ�ý����ͬʱ���ó�ʱִ��ʱ��Ϊ5�롣ͬ��������future.get()��������ִ�г�ʱʱ��ȡ�ý��  
	        if(result!=null&&"1".equals(result)){
	        	
	        }else{
	        }
	    } catch (InterruptedException e) {  
	    	future.cancel(true);  
	    } catch (ExecutionException e) {  
	    	future.cancel(true);  
	    } catch (TimeoutException e) { 
	    	future.cancel(true);  
	    }catch (Exception e) { 
	    	future.cancel(true);  
	    } finally {  
	        executor.shutdown();  
	    }  
	}
}
