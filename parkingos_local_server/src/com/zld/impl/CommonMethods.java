package com.zld.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zld.AjaxUtil;
import com.zld.CustomDefind;
import com.zld.service.DataBaseService;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;
@Repository
public class CommonMethods {
	
	
	private Logger logger = Logger.getLogger(CommonMethods.class);
//	@Autowired
//	private MemcacheUtils memcacheUtils;
	@Autowired
	private DataBaseService daService;
	@Autowired
	private PublicMethods publicMethods;
	
	/**
	 * ������Ľӿ�
	 */
	public void requestInitialized(HttpServletRequest request){
		try {
			request.setAttribute("reqInitTime", System.currentTimeMillis()/1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ������Ľӿ�
	 */
	public void requestDestroyed(HttpServletRequest request){
		try {
			String url = request.getServletPath();
			String action = request.getParameter("action");
			url = (action != null) ? (url + "?action=" +action ) : url;
			if(request.getAttribute("reqInitTime") == null){
				return;
			}
			long reqInitTime = (Long)request.getAttribute("reqInitTime");
			long reqDestTime = System.currentTimeMillis()/1000;
			long lifeTime = reqDestTime - reqInitTime;
			logger.error("url:"+url);
			logger.error("interface lifecycle>>>action:"+action+",lifeTime:"+lifeTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//���³�λ��Ϣ�������ѽ��㶩����ռ�ó�λ
	public void updateParkInfo(Long comId) {
		int r =daService.update("update com_park_tb set state =?,order_id=? where order_id in " +
				"(select id from order_tb where state in(?,?) and id in(select order_id from com_park_tb where comid=?)) ",
				new Object[]{0,null,1,2,comId});
		logger.info(comId+"��������"+r+"����λ��Ϣ");
	}
	
	
	//��ѯ���
	public boolean checkBonus(String mobile,Long uin){
		List bList = daService.getAll("select * from bonus_record_tb where mobile=? and state=? ",new Object[]{mobile,0});
		String tsql = "insert into ticket_tb (create_time,limit_day,money,state,uin,type) values(?,?,?,?,?,?) ";
		List<Object[]> values = new ArrayList<Object[]>();
		if(bList!=null&&bList.size()>0){
			Long bid = null;
			for(int i=0;i<bList.size();i++){
				Map map = (Map)bList.get(i);
				Long _bid = (Long)map.get("bid");
				if(_bid!=null&&_bid>0)
					bid = _bid;
				Integer money = (Integer)map.get("amount");
				
				Integer type = (Integer)map.get("type");
				Long ctime = TimeTools.getToDayBeginTime();//(Long)map.get("ctime");
				Long etime = ctime+6*24*60*60-1;
				
				if(type==1){//΢�Ŵ���ȯ
					values.add(new Object[]{ctime,etime,money,0,uin,2});
				}else {//��ͨͣ��ȯ
					if(money==30||money==100){//3��10Ԫȯ
						if(money==30){
							values.add(new Object[]{ctime,etime,4,0,uin,0});
							values.add(new Object[]{ctime,etime,4,0,uin,0});
							values.add(new Object[]{ctime,etime,1,0,uin,0});
							values.add(new Object[]{ctime,etime,1,0,uin,0});
							values.add(new Object[]{ctime,etime,3,0,uin,0});
							values.add(new Object[]{ctime,etime,3,0,uin,0});
							values.add(new Object[]{ctime,etime,2,0,uin,0});
							values.add(new Object[]{ctime,etime,2,0,uin,0});
							values.add(new Object[]{ctime,etime,4,0,uin,0});
							values.add(new Object[]{ctime,etime,1,0,uin,0});
							values.add(new Object[]{ctime,etime,3,0,uin,0});
							values.add(new Object[]{ctime,etime,2,0,uin,0});
						}else {
							int end = 10;
							for(int j=0;j<end;j++){
								values.add(new Object[]{ctime,etime,10,0,uin,0});
							}
						}
					}else if(money==10){//1��10Ԫȯ
						values.add(new Object[]{ctime,etime,4,0,uin,0});
						values.add(new Object[]{ctime,etime,1,0,uin,0});
						values.add(new Object[]{ctime,etime,3,0,uin,0});
						values.add(new Object[]{ctime,etime,2,0,uin,0});
					}else {
						Object[] v1 = new Object[]{ctime,etime,money,0,uin,0};
						values.add(v1);
					}
				}
			}
			if(values.size()>0){
				int ret= daService.bathInsert(tsql, values, new int[]{4,4,4,4,4,4});
				logger.info("�˻�:"+uin+",�ֻ���"+mobile+",�û���¼ ��д����ͣ��ȯ"+ret+"��");
				logger.info(">>>>�û�������ȯ�����º����¼��"+daService.update("update bonus_record_tb set state=? where mobile=?", new Object[]{1,mobile}));
				if(ret>0){
					//���³���ע��ý����Դ 0������ע�ᣬ1-997�Ƕ��ƺ����1����ͷ���������������2�������,3���պ��.4.����ͷ������أ�����998ֱ�����,999���շ�Ա�Ƽ���1000�����ǳ������������
					if(bid!=null&&bid>0){
						Integer media = 0;
						if(bid>999){//1000���ϵı���ǳ������������������Ϊ���ƺ������д���û���
							media=1000;
						}else {
							media = bid.intValue();
						}
						if(media>0){//����ý����Դ
							daService.update("update user_info_tb set media=? where id=? ", new Object[]{media,uin});
						}
					}
					return true;
				}
			}
		}else {
			logger.info("�˻�:"+uin+",�ֻ���"+mobile+",û�к��....");
		}
		return false;
	}
	/**
	 * ȡ����ͣ��ȯ��δ��֤�������ʹ��3Ԫȯ��
	 * 	 * 9Ԫ��ͣ���ѣ� Ҳ����ʹ��18Ԫ��ͣ��ȯ����ֻ�ֿܵ�8Ԫ��  
		���8����Ƕ�̬�ķ�������ȡ����Ϊ�п���ѹ�������������Ż�ȯֻ�ֿۣܵ�ͣ����-2����8�ͱ�Ϊ7�ˡ�
	 * @param uin
	 * @param fee
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getTickets(Long uin,Double fee,Long comId,Long uid){
		//������п��õ�ȯ
		//Long ntime = System.currentTimeMillis()/1000;
		Integer limit = CustomDefind.getUseMoney(fee,0);
		Double splimit = StringUtils.formatDouble(CustomDefind.getValue("TICKET_LIMIT"));
		boolean blackuser = isBlackUser(uin);
		boolean blackparkuser =false;
		if(comId!=null)
			blackparkuser=publicMethods.isBlackParkUser(comId, false);
		boolean isauth = publicMethods.isAuthUser(uin);
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
			}
		}
		logger.info("���շ�Ա:"+uid+"����ȯ����ǣ�"+ticketquota+"��(-1����û����)");
		if(!isauth){//δ��֤�������ʹ��2Ԫȯ��
			double noAuth = 1;//δ��֤�����������noAuth(2)Ԫȯ,�Ժ�Ķ����ֵ��ok
			if(ticketquota>=0&&ticketquota<=noAuth){
//				ticketquota = ticketquota+1;
			}else{
				ticketquota=noAuth;
			}
			list=	daService.getAll("select * from ticket_tb where uin = ? " +
					"and state=? and limit_day>=? and type<? and money<?  order by limit_day",
					new Object[]{uin,0,TimeTools.getToDayBeginTime(),2,ticketquota+1});

		}else {
			list  = daService.getAll("select * from ticket_tb where uin = ? " +
					"and state=? and limit_day>=? and type<=?  order by limit_day",
					new Object[]{uin,0,TimeTools.getToDayBeginTime(),2});
		}
		logger.info("uin:"+uin+",fee:"+fee+",comid:"+comId+",today:"+TimeTools.getToDayBeginTime());
		if(list!=null&&!list.isEmpty()){
			List<String> _over3day_moneys = new ArrayList<String>();
			int i=0;
			for(Map<String, Object> map : list){
				Integer money = (Integer)map.get("money");
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
				if(money==limit){//ȯֵ+1Ԫ ���� ֧�����ʱֱ�ӷ���
					return map;
				}
				//�ж� �Ƿ� �� ���Ǹó�����ר��ȯ
				
				map.remove("comid");
//				map.remove("limit_day");
				_over3day_moneys.add(i+"_"+Math.abs(limit-money));
				i++;
			}
			if(_over3day_moneys.size()>0){//ͣ��ȯ��ͣ���ѵľ���ֵ���� ��ȡ����ֵ��С��
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
	}
	
	/**�Ƿ��ں�����*/
	public boolean isBlackUser(Long uin){
		List<Long> blackUserList = null;//memcacheUtils.doListLongCache("zld_black_users", null, null);
		boolean isBlack = true;
		if(blackUserList==null||!blackUserList.contains(uin))//���ں������п��Դ����Ƽ�����
			isBlack=false;
		return isBlack;
	}
	
	/**
	 * ����openid��ȡ�û���Ϣ
	 * @param openid
	 * @return
	 */
	public Map<String, Object> getUserByOpenid(String openid){
		Map<String, Object> userMap = daService.getMap("select * from user_info_tb where wxp_openid=? limit ? ",
				new Object[] { openid, 1 });
		return userMap;
	}
	
	/**
	 * ����openid��ȡ�û�����Ϣ
	 * @param openid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getUserinfoByOpenid(String openid){
		Map<String, Object> map = new HashMap<String, Object>();
		Integer bindflag = 0;
		Long uin = -1L;
		String mobile = "";
		Double balance = 0d;
		Map<String, Object> userMap = daService.getMap("select * from user_info_tb where wxp_openid=? limit ? ",
				new Object[] { openid, 1 });
		if(userMap != null){
			bindflag = 1;
			uin = (Long)userMap.get("id");
			mobile = (String)userMap.get("mobile");
			balance = Double.valueOf(userMap.get("balance") + "");
		}else{
			userMap = daService.getMap("select * from wxp_user_tb where openid=? limit ? ", new Object[]{openid, 1});
			if(userMap == null){
				uin = daService.getLong("SELECT nextval('seq_user_info_tb'::REGCLASS) AS newid",null);
				int r = daService.update("insert into wxp_user_tb(openid,create_time,uin) values(?,?,?) ",
								new Object[] { openid, System.currentTimeMillis() / 1000, uin});
				logger.info("û����ʱ�˻�������һ��uin:"+uin+",openid:"+openid+",r:"+r);
			}else{
				uin = (Long)userMap.get("uin");
				balance = Double.valueOf(userMap.get("balance") + "");
			}
		}
		map.put("bindflag", bindflag);
		map.put("uin", uin);
		map.put("mobile", mobile);
		map.put("balance", balance);
		return map;
	}
	
	/**
	 * ɨ����ȯ����ȡ����ǰ���ͣ���ѽ��
	 * @param orderMap
	 * @param shopTicketMap
	 * @param delaytime Ԥ֧����ʱʱ��
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getPrice(Long orderId, Long end_time){
		Map<String, Object> orderMap = daService.getMap("select * from order_tb where id=? ", 
				new Object[]{orderId});
		
		Map<String, Object> map = new HashMap<String, Object>();
		Long comid = (Long)orderMap.get("comid");
		Double beforetotal = 0d;
		Double aftertotal = 0d;
		Integer car_type = (Integer)orderMap.get("car_type");//0��ͨ�ã�1��С����2����
		Integer pid = (Integer)orderMap.get("pid");
		Long create_time = (Long)orderMap.get("create_time");
		Integer distime = 0;//�ֿ۵�ʱ��
		
//		beforetotal = getPrice(car_type, pid, comid, create_time, end_time);
		beforetotal = getPrice(car_type, pid, comid, create_time, end_time,orderId);
		
		Map<String, Object> shopTicketMap = daService.getMap("select * from ticket_tb where orderid=? and (type=? or type=?) ", 
				new Object[]{orderId, 3, 4});
		if(shopTicketMap != null){
			Integer type = (Integer)shopTicketMap.get("type");
			if(type == 3){
				Integer time = (Integer)shopTicketMap.get("money");
				if(end_time > create_time + time *60 *60){
					aftertotal = getPrice(car_type, pid, comid, create_time, end_time - time * 60 *60,orderId);
					distime =time *60 *60;
				}else if(end_time > create_time){
					distime = (end_time.intValue() - create_time.intValue());
				}
			}else if(type == 4){
				if(end_time > create_time){
					distime = (end_time.intValue() - create_time.intValue());
				}
			}
		}else{
			aftertotal = beforetotal;
		}
		
		Double distotal = beforetotal - aftertotal >0 ? (beforetotal - aftertotal) : 0d;
		
		if(shopTicketMap != null && beforetotal > aftertotal){
			int r = daService.update("update ticket_tb set umoney=?,bmoney=? where id=? ", 
					new Object[]{StringUtils.formatDouble(distotal), Double.valueOf(distime)/(60*60), shopTicketMap.get("id")});
		}
		map.put("beforetotal", beforetotal);
		map.put("aftertotal", aftertotal);
		return map;
	}
	
	/**
	 * ���ݶ�����Ϣ��ȡ���ѽ��
	 * @param car_type
	 * @param pid
	 * @param comid
	 * @param create_time
	 * @param end_time
	 * @return
	 */
	public Double getPrice(Integer car_type, Integer pid, Long comid, Long create_time, Long end_time,Long orderid){
		Double total = 0d;
		if(pid>-1){
			total = Double.valueOf(publicMethods.getCustomPrice(create_time, end_time, pid));
		}else {
			Map orderMap = daService.getMap("select * from order_tb where id = ?", new Object[]{orderid});
			//ģ���ѽ���
			orderMap.put("end_time", end_time);
			orderMap.put("state", 1);
			String result = "";
			try {
				result = publicMethods.getOrderPrice(comid, orderMap);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//getPrice(create_time, end_time, comid, car_type));
			net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(result);
			if(jsonObject.get("collect")!=null){
				total = jsonObject.getDouble("collect");
			}
			
		}
		return total;
	}
	
	/**
	 * ��ȡ��������Ϣ
	 * @param orderId
	 * @param shopTicketId ����ȯID
	 * @param uin �û�ID
	 * @param delaytime Ԥ֧�����ӳ�ʱ��
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getOrderInfo(Long orderId, Long shopTicketId, Long end_time){
		Double pretotal = 0d;//�Ѿ�Ԥ֧���Ľ��
		Map<String, Object> orderMap = daService.getMap("select * from order_tb where id=? ", 
				new Object[]{orderId});
		if(orderMap == null){
			return null;
		}
		if(orderMap.get("total") != null){
			pretotal = Double.valueOf(orderMap.get("total") + "");//Ԥ֧���Ľ��
		}
		Long create_time = (Long)orderMap.get("create_time");
		Map<String, Object> map = getComOrderInfo(orderId, shopTicketId, create_time, end_time);
		
		map.put("createtime", create_time);
		map.put("starttime", TimeTools.getTime_yyyyMMdd_HHmm(create_time * 1000));
		map.put("parktime", StringUtils.getTimeString(create_time, System.currentTimeMillis()/1000));
		map.put("pretotal", pretotal);
		map.put("uid", orderMap.get("uid"));
		map.put("comid", orderMap.get("comid"));
		map.put("carnumber", orderMap.get("car_number"));
		return map;
	}
	
	/**
	 * ��ȡ�ѽ��㶩������Ϣ
	 * @param orderid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getOrderInfoPayed(Long orderid, Long shopTicketId){
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> orderMap = daService.getMap("select * from order_tb where id=? and state=? ", 
				new Object[]{orderid, 1});
		if(orderMap == null){
			return null;
		}
		Long create_time = (Long)orderMap.get("create_time");
		Long end_time = (Long)orderMap.get("end_time");
		Double total = Double.valueOf(orderMap.get("total") + "");
		map = getComOrderInfo(orderid, shopTicketId, create_time, end_time);
		
		map.put("createtime", create_time);
		map.put("starttime", TimeTools.getTime_yyyyMMdd_HHmm(create_time * 1000));
		map.put("parktime", StringUtils.getTimeString(create_time, end_time));
		map.put("total", total);
		map.put("uid", orderMap.get("uid"));
		map.put("comid", orderMap.get("comid"));
		map.put("carnumber", orderMap.get("car_number"));
		map.put("shopticketid", shopTicketId);
		map.put("paytype", orderMap.get("pay_type"));
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getComOrderInfo(Long orderid, Long shopTicketId, Long create_time, Long end_time){
		Map<String, Object> map = new HashMap<String, Object>();
		Double beforetotal = 0d;//����֮ǰ��ͣ���ѽ��
		Double aftertotal = 0d;//����֮���ͣ���ѽ��
		
		Integer tickettype = 3;//����ȯ���ͣ�Ĭ�ϼ�ʱȯ
		Integer tickettime = 0;//��ʱȯ��ʱ��
		Integer ticketstate = 0;//����ȯ��״̬��0�������� 1:����
		Map<String, Object> shopTicketMap = daService.getMap("select * from ticket_tb where orderid=? and (type=? or type=?) ", 
				new Object[]{orderid, 3, 4});
		if(shopTicketMap == null){
			if(shopTicketId != null && shopTicketId > 0){
				shopTicketMap = daService.getMap("select * from ticket_tb where id=? and (orderid=? or orderid=?) and state=? and (type=? or type=?) and limit_day>? ", 
						new Object[]{shopTicketId, -1, orderid, 0, 3, 4, end_time});
			}
		}else{
			shopTicketId = (Long)shopTicketMap.get("id");
		}
		if(shopTicketMap != null){
			int r = daService.update("update ticket_tb set orderid=? where id=? ", new Object[]{orderid, shopTicketId});
			tickettype = (Integer)shopTicketMap.get("type");
			tickettime = (Integer)shopTicketMap.get("money");
			ticketstate = 1;//�ü���ȯ����
		}
		Map<String, Object> map2 = getPrice(orderid, end_time);
		beforetotal = Double.valueOf(map2.get("beforetotal") + "");
		aftertotal = Double.valueOf(map2.get("aftertotal") + "");
		map.put("beforetotal", beforetotal);
		map.put("aftertotal", aftertotal);
		map.put("ticketstate", ticketstate);
		map.put("tickettype", tickettype);
		map.put("tickettime", tickettime);
		map.put("shopticketid", shopTicketId);
		return map;
	}
	
	/**
	 * �����û�ID��ȡ����ʱ�˻�������ʽ�˻�
	 * @param uin
	 * @return
	 */
	public Integer getBindflag(Long uin){
		Long count = daService.getLong("select count(1) from user_info_tb where id=? ", new Object[]{uin});
		return count.intValue();
	}
	
	public Integer addCarnumber(Long uin, String carnumber){
		Integer bindflag = getBindflag(uin);
		if(bindflag == 1){
			Long count = daService.getLong("select count(*) from car_info_tb where uin!=? and car_number=? and state=? ",
					new Object[] { uin, carnumber, 1 });
			if(count > 0){//�ó��ƺ��ѱ�����ע��
				return -1;
			}
			count = daService.getLong("select count(*) from car_info_tb where uin=? and car_number=? ",
					new Object[] { uin, carnumber});
			if(count > 0){//�ó����Ѿ�ע����ó��ƺ�
				return -2;
			}else{
				count = daService.getLong("select count(*) from car_info_tb where uin=? ",
						new Object[] { uin });
				if(count >= 3){//�ó���ע��ĳ��ƺŵĸ���
					return -3;
				}
				int r=daService.update("insert into car_info_Tb (uin,car_number) values(?,?)", 
						new Object[]{uin, carnumber});
				if(r > 0){
					return 1;
				}
			}
		}else if(bindflag == 0){
			int r = daService.update("update wxp_user_tb set car_number=? where uin=? ", 
					new Object[]{carnumber, uin});
			if(r > 0){
				return 1;
			}
		}
		return -4;
	}
	
	/**
	 * @param uin          �����˻�
	 * @param total        �������
	 * @return             ����ͣ��ȯ�б�
	 */
	public List<Map<String,Object>> getUseTickets(Long uin,Double total){
		Long time = System.currentTimeMillis()/1000;
		List<Map<String,Object>> ticketList=daService.getAll("select id,limit_day as limitday,money,resources," +
				"comid,type from ticket_tb where uin = ?" +
				" and limit_day >= ? and state=? and type<?  order by type desc,money,limit_day ",
				new Object[]{uin,time,0,2});
		
		Integer limit = CustomDefind.getUseMoney(total, 0);//��ͨȯ�ֿ۽��
		Integer sysLimit = Integer.valueOf(CustomDefind.getValue("TICKET_LIMIT"));//ר��ȯ������ȯ����붩���Ĳ��
		if(ticketList!=null&&!ticketList.isEmpty()){
			for(Map<String,Object> map:ticketList){
				Integer money = (Integer)map.get("money");
				Integer res = (Integer)map.get("resources");
				Integer topMoney = CustomDefind.getUseMoney(money.doubleValue(), 1);
				Integer type=(Integer)map.get("type");
				if(res==1||type==1){//����ר��ȯ����ȯ
					topMoney = money+sysLimit;
					limit = total.intValue()-sysLimit;
				}else {
					limit = CustomDefind.getUseMoney(total, 0);
					
				}
				if(topMoney<total){//����޶�С��֧�����
					limit=money;
				}
				map.put("limit", limit);
			}
		}
		//logger.info(ticketList);
		return ticketList;
	}
	
	//----------------����ȯѡȯ�߼�begin--------------------//
	/**
	 * ѡ�����ȯ
	 * @param uin
	 * @param uid
	 * @param total
	 * @return
	 */
	public Map<String, Object> chooseDistotalTicket(Long uin, Long uid, Double total){
		double firstorderquota = 8.0;//Ĭ�϶��
		double ditotal = 0d;//���۶��
		double disquota = StringUtils.formatDouble(firstorderquota * ditotal);//�����ۺ�ĵֿ۽��
		
		logger.info("ѡ�ۿ�ȯuin:"+uin+",uid:"+uid+",disquota:"+disquota+",firstorderquota:"+firstorderquota+",total:"+total);
		Map<String, Object> userMap2 = daService.getMap("select comid,firstorderquota from user_info_tb where id = ? ", new Object[]{uid});
		if(userMap2!=null){
			firstorderquota = Double.valueOf(userMap2.get("firstorderquota") + "");
			disquota = StringUtils.formatDouble(firstorderquota * ditotal);
		}
		logger.info("ѡ�ۿ�ȯuin:"+uin+",uid:"+uid+",firstorderquota:"+firstorderquota+",disquota:"+disquota);
		Map<String, Object> ticketMap = new HashMap<String, Object>();
		ticketMap.put("id", -100);
		Double ticket_money = Double.valueOf(StringUtils.formatDouble(total*ditotal));
		if(ticket_money > disquota){
			ticket_money =disquota;
		}
		ticketMap.put("money", ticket_money);
		logger.info("uin:"+uin+",total:"+total+",ticketMap:"+ticketMap);
		return ticketMap;
	}
	//----------------����ȯѡȯ�߼�end--------------------//
	
	//----------------����ȯѡȯ�߼�begin--------------------//
	/**
	 * 
	 * @param uin
	 * @param total
	 * @param utype 0��ͨѡȯ��Ĭ�ϣ�1���ô������ֿ۽���ͣ��ȯ
	 * @param uid
	 * @param isAuth
	 * @param ptype 0�˻���ֵ��1���²�Ʒ��2ͣ���ѽ��㣻3ֱ��;4���� 5����ͣ��ȯ
	 * @param parkId
	 * @param source 0:���Կͻ���ѡȯ 1�����Թ��ں�ѡȯ
	 * @return
	 */
//	public List<Map<String, Object>> chooseTicket(Long uin, Double total, Integer utype, Long uid, boolean isAuth, Integer ptype, Long parkId, Long orderId, Integer source){
//		List<Map<String, Object>> list = null;
//		if(ptype == 4){//����ѡȯ
//			list = chooseRewardTicket(uin, total, isAuth, uid, utype, ptype, parkId, orderId, source);
//		}else if(ptype == -1 || ptype == 2 || ptype == 3){
//			list = chooseParkingTicket(uin, total, utype, uid, isAuth, ptype, parkId, orderId, source);
//		}
//		return list;
//	}
	
	/**
	 * ͣ������ѡȯ
	 * @param uin
	 * @param total ͣ���ѽ��
	 * @param utype 0��ͨѡȯ��Ĭ�ϣ�1���ô������ֿ۽���ͣ��ȯ
	 * @param uid
	 * @param isAuth
	 * @param source 0:���Կͻ���ѡȯ 1�����Թ��ں�ѡȯ
	 * @return
	 */
//	public List<Map<String, Object>> chooseParkingTicket(Long uin, Double total, Integer utype, Long uid, boolean isAuth, Integer ptype, Long parkId, Long orderId, Integer source){
//		List<Map<String, Object>> list = null;
//		boolean isCanUserTicket = memcacheUtils.readUseTicketCache(uin);
//		logger.info("choose parking pay ticket>>>uin:"+uin+",total:"+total+",utype:"+utype+",uid:"+uid+",isAuth:"+isAuth+",isCanUserTicket:"+isCanUserTicket);
//		if(isCanUserTicket){
//			Double moneylimit = 9999d;//ѡȯ������
//			Map<String, Object> uidMap =daService.getMap("select ticketquota from user_info_Tb where id =? and ticketquota<>?", new Object[]{uid,-1});
//			if(uidMap != null){
//				moneylimit = Double.parseDouble(uidMap.get("ticketquota")+"");
//			}
//			logger.info("uin:"+uin+",uid:"+uid+",moneylimit:"+moneylimit+",isAuth:"+isAuth);
//			Integer tickettype = 2;//ѡȯ����
//			if(!isAuth){
//				if(source == 0){
//					moneylimit = 0d;
//				}else if(source == 1){
//					moneylimit = 0d;
//				}
//			}
//			logger.info("uin:"+uin+",uid:"+uid+",moneylimit:"+moneylimit+",isAuth:"+isAuth+",tickettype:"+tickettype);
//			list = getLimitTickets(moneylimit, tickettype, uin, utype, ptype, uid, total, parkId, orderId);
//		}
//		return list;
//	}
	
	/**
	 * ѡ����ȯ
	 * @param uin
	 * @param total
	 * @param isAuth
	 * @param uid
	 * @param utype 0��ͨѡȯ��Ĭ�ϣ�1���ô������ֿ۽���ͣ��ȯ
	 * @param ptype 0�˻���ֵ��1���²�Ʒ��2ͣ���ѽ��㣻3ֱ��;4���� 5����ͣ��ȯ
	 * @param source 0:���Կͻ���ѡȯ 1�����Թ��ں�ѡȯ
	 * @return
	 */
//	public List<Map<String, Object>> chooseRewardTicket(Long uin, Double total, boolean isAuth, Long uid, Integer utype, Integer ptype, Long parkId, Long orderId, Integer source){
//		List<Map<String, Object>> list = null;
//		Map<Long, Long> tcacheMap =memcacheUtils.doMapLongLongCache("reward_userticket_cache", null, null);
//		boolean isCanUserTicket=true;
//		if(tcacheMap!=null){
//			Long time = tcacheMap.get(uin);
//			if(time!=null&&time.equals(TimeTools.getToDayBeginTime())){
//				isCanUserTicket=false;
//			}
//			logger.info("today reward cache:"+tcacheMap.size()+",uin:"+uin+",uid:"+uid+",time:"+time+",todaybegintime:"+TimeTools.getToDayBeginTime());
//		}
//		logger.info("choose reward ticket:uin:"+uin+",uid:"+uid+",isCanUserTicket:"+isCanUserTicket+",isAuth:"+isAuth+",total:"+total);
//		
//		if(isCanUserTicket){
//			Double moneylimit = 9999d;//ѡȯ������
//			Integer tickettype = 1;//ѡȯ����
//			if(!isAuth){
//				if(source == 0){
//					moneylimit = 0d;
//				}else if(source == 1){
//					moneylimit = 0d;
//				}
//			}
//			list = getLimitTickets(moneylimit, tickettype, uin, utype, ptype, uid, total, parkId, orderId);
//		}
//		return list;
//	}
//	
	/**
	 * ����ͣ��ȯ�������ƺ�ͣ��ȯ�������ȡͣ��ȯ�б�
	 * @param moneylimit ͣ��ȯ�������
	 * @param tickettype ͣ��ȯ��������
	 * @param uin
	 * @param utype 0��ͨѡȯ��Ĭ�ϣ�1���ô������ֿ۽���ͣ��ȯ
	 * @param ptype 0�˻���ֵ��1���²�Ʒ��2ͣ���ѽ��㣻3ֱ��;4���� 5����ͣ��ȯ
	 * @param uid
	 * @param total ���
	 * @return
	 */
//	private List<Map<String, Object>> getLimitTickets(Double moneylimit, Integer tickettype, Long uin, Integer utype, Integer ptype, Long uid, Double total, Long parkId, Long orderId){
//		Integer resource = 1;//ֻ���ù���ȯ
//		if(readAllowCache(parkId)){
//			logger.info("already uplimit of allowance everyday>>>uin:"+uin+",orderid:"+orderId);
//			resource = 1;
//		}
//		List<Map<String, Object>> list = daService.getAll("select * from ticket_tb where uin = ? and state=? and limit_day>=? and type<? and money<=? and resources>=?  order by money ",
//				new Object[] { uin, 0, TimeTools.getToDayBeginTime(), tickettype, moneylimit, resource });
//		list = chooseTicketByLevel(list, ptype, uid, total, utype, parkId, orderId);
//		return list;
//	}
	
//	private boolean readAllowCache(Long comid){
//		Double limit = memcacheUtils.readAllowLimitCacheByPark(comid);
//		logger.info("comid:"+comid+",limit:"+limit);
//		if(limit != null){//�л���
//			Double allowmoney = memcacheUtils.readAllowCacheByPark(comid);
//			logger.info("comid:"+comid+",allowmoney:"+allowmoney);
//			Map<String, Object> comMap = daService.getMap(
//					"select allowance from com_info_tb where id=? ",
//					new Object[] { comid });
//			if(comMap != null && comMap.get("allowance") != null){
//				Double allowance = Double.valueOf(comMap.get("allowance") + "");
//				logger.info("comid:"+comid+",allowance:"+allowance);
//				if(allowance > 0){
//					if(allowmoney >= allowance){
//						return true;
//					}
//				}
//			}
//			if(allowmoney >= limit){//�鿴�Ƿ񳬹�ÿ�ղ�������
//				return true;
//			}
//		}else{//û�а�������������Ĳ���,��ʱ�����ܵ���������
//			Double allallowmoney = memcacheUtils.readAllowanceCache();
//			if(CustomDefind.getValue("ALLOWANCE") != null){
//				Double uplimit = Double.valueOf(CustomDefind.getValue("ALLOWANCE") + "");
//				Double toDaylimit = getAllowance(TimeTools.getToDayBeginTime(), uplimit);
////				if(toDaylimit<1000||toDaylimit>uplimit)
////					toDaylimit=1000d;
//				logger.info("���ղ����ܶ� ��allallowmoney:"+allallowmoney+",uplimit:"+uplimit+",toDaylimit:"+toDaylimit);
//				if(allallowmoney >= toDaylimit){//���ղ����ܶ��Ѿ�����������
//					return true;
//				}
//			}
//		}
//		return false;
//	}
	
	//2015-11-05 ��ʼ��ÿ���100,��0ֹͣ
	private Double getAllowance(Long time,Double limit) {
		Long baseTime = 1446652800L;//2015-11-05
		Long abs = time-baseTime;
		Long t  = abs/(24*60*60);
		logger.info(">>>>>��2015-11-03��ʼ�������ݼ�100�ı�����"+t);
		if(t>0){
			Double retDouble= limit-t*100;
			if(retDouble<0d)
				retDouble=0d;
			return retDouble;
		}
		return limit;
	}
	
	/**
	 * @param ptype 0�˻���ֵ��1���²�Ʒ��2ͣ���ѽ��㣻3ֱ��;4���� 5����ͣ��ȯ
	 * @param uid   �շ�Ա���
	 * @param total ���ѽ��
	 * @param type  0�����ݽ�����ȯ�ֿ۽�� 1������ȯ���������������ѽ���ȫ��ֿ�
	 * @param utype 0��ͨѡȯ��Ĭ�ϣ�1���ô������ֿ۽���ͣ��ȯ
	 * @return
	 */
	private Map<String, Object> getDistotalLimit(Integer ptype,Long uid, Double total, Integer type, Integer utype, Long orderId){
//		logger.info("getDistotalLimit>>>ptype:"+ptype+",uid:"+uid+",total:"+total+",utype:"+utype);
		Map<String, Object> map = new HashMap<String, Object>();
		Double climit = 0d;
		Double blimit = 0d;
		Double slimit = 0d;
		if(ptype == 4){//����ѡȯ
			Double rewardquota = 3.0;//�ֿ�����
			Map<String, Object> userMap = daService.getMap("select rewardquota from user_info_tb where id = ?", new Object[]{uid});
			if(userMap != null && userMap.get("rewardquota") != null){
				rewardquota =StringUtils.formatDouble(userMap.get("rewardquota"));
			}
			if(type == 0){
				if(orderId != null && orderId > 0){
					Map<String, Object> orderMap = daService.getMap("select total from order_tb where id=? ", new Object[]{orderId});
					if(orderMap != null && orderMap.get("total") != null){
						Double fee = Double.valueOf(orderMap.get("total") + "");//ͣ���ѽ��
						
						//��ͨȯ  X��֧�����ѽ���� (fee) Y������ȯ�ֿ۽�� (climit) �㷨��X=6Y-2 ������rewardquota
						climit = Math.floor((fee+2)*(1.0/6));//����ȡ��
						if(climit < 0){
							climit = 0d;
						}
						if(climit > total){
							climit = total;
						}
						if(climit > rewardquota){
							climit = rewardquota;
						}
						//����ȯ   X��֧�����ѽ���� (fee) Y������ȯ�ֿ۽�� (blimit) �㷨��X=Y������rewardquota
						blimit = Math.floor(fee);//����ȡ��
						if(blimit < 0){
							blimit = 0d;
						}
						if(blimit > total){
							blimit = total;
						}
						if(blimit > rewardquota){
							blimit = rewardquota;
						}
						//ר��ȯ   X��֧�����ѽ���� (fee) Y������ȯ�ֿ۽�� (slimit) �㷨��X=6Y-2 ������rewardquota
						slimit = Math.floor((fee+2)*(1.0/6));//����ȡ��
						if(slimit < 0){
							slimit = 0d;
						}
						if(slimit > total){
							slimit = total;
						}
						if(slimit > rewardquota){
							slimit = rewardquota;
						}
					}
				}
				logger.info("getDistotalLimit>>>uid:"+uid+",climit:"+climit+",blimit:"+blimit+",slimit:"+slimit+",total:"+total+",ptype:"+ptype+",utype:"+utype+",type:"+type);
				
			}else if(type == 1){
				if(total > rewardquota){
					total = rewardquota;
				}
				//��ͨȯ  X��֧�����ѽ���� (climit) Y������ȯ�ֿ۽�� (total) �㷨��X=6Y-2 ������rewardquota
				climit = Math.ceil(total*6 - 2);
				//����ȯ  X��֧�����ѽ���� (blimit) Y������ȯ�ֿ۽�� (total) �㷨��X=Y ������rewardquota
				blimit = Math.ceil(total);
				//ר��ȯ  X��֧�����ѽ���� (slimit) Y������ȯ�ֿ۽�� (total) �㷨��X=6Y-2 ������rewardquota
				slimit = Math.ceil(total*6 - 2);
				
				map.put("distotal", total);//ʵ����ߵֿ۽��
//					logger.info("getDistotalLimit>>>uid:"+uid+",climit:"+climit+",blimit:"+blimit+",slimit:"+slimit+",rewardquota:"+rewardquota+",total:"+total+",ptype:"+ptype+",utype:"+utype+",type:"+type+",distotal:"+total);
			}
			
		}else if(ptype == -1 || ptype == 2 || ptype == 3){
			Double uplimit = 9999d;//�ֿ�����
			if(type == 0){
				//��ͨȯ  X�����ѽ���� (total) Y������ȯ�ֿ۽�� (climit) �㷨��X=6Y - 2 ������uplimit
				climit = Math.floor((total + 2)*(1.0/6));//����ȡ��
				if(climit < 0){
					climit = 0d;
				}
				if(climit > uplimit){
					climit = uplimit;
				}
				//����ȯ  X�����ѽ���� (total) Y������ȯ�ֿ۽�� (climit) �㷨��X=Y ������uplimit
				blimit = Math.floor(total);//����ȡ��
				if(blimit < 0){
					blimit = 0d;
				}
				if(blimit > uplimit){
					blimit = uplimit;
				}
				//ר��ȯ  X�����ѽ���� (total) Y������ȯ�ֿ۽�� (climit) �㷨��X=3Y+1 ������uplimit
				slimit = Math.floor((total - 1)*(1.0/3));//����ȡ��
				if(slimit < 0){
					slimit = 0d;
				}
				if(slimit > uplimit){
					slimit = uplimit;
				}
				logger.info("getDistotalLimit>>>uid:"+uid+",climit:"+climit+",blimit:"+blimit+",slimit:"+slimit+",uplimit:"+uplimit+",total:"+total+",ptype:"+ptype+",utype:"+utype+",type:"+type);
			}else if(type == 1){
				if(total > uplimit){
					total = uplimit;
				}
				//��ͨȯ  X��֧������� (climit) Y������ȯ�ֿ۽�� (total) �㷨��X=Y+1+Y/1 ������uplimit
				climit = Math.ceil(total*6 - 2);
				//����ȯ  X��֧������� (blimit) Y������ȯ�ֿ۽�� (total) �㷨��X=Y ������uplimit
				blimit = Math.ceil(total);
				//ר��ȯ  X��֧������� (slimit) Y������ȯ�ֿ۽�� (total) �㷨��X=Y+1 ������uplimit
				slimit = Math.ceil(total*3 + 1);
				map.put("distotal", total);//ʵ����ߵֿ۽��
//				logger.info("getDistotalLimit>>>uid:"+uid+",climit:"+climit+",blimit:"+blimit+",slimit:"+slimit+",uplimit:"+uplimit+",total:"+total+",ptype:"+ptype+",utype:"+utype+",type:"+type+",distotal:"+total);
			}
			
		}
		map.put("climit", climit);
		map.put("blimit", blimit);
		map.put("slimit", slimit);
//		logger.info("uid:"+uid+",map:"+map);
		setDistotalByUtype(map, utype, type);
		return map;
	}
	
	/**
	 * ��Ҫ�����Ͽͻ���utype=1������£�ȡ���ֵֿ��㷨�еֿ���С��һ����Ϊ�ֿۣ��Ͽͻ�����ѡ��ͬ��ȯ��ͬһ��limit���������Է�ֹ�û��ֶ�ѡȯʱ�ֿ۴���
	 * @param map
	 * @param utype 0��ͨѡȯ��Ĭ�ϣ�1���ô������ֿ۽���ͣ��ȯ
	 * @param type 0�����ݽ�����ȯ�ֿ۽�� 1������ȯ���������������ѽ���ȫ��ֿ�
	 * @return
	 */
	private Map<String, Object> setDistotalByUtype(Map<String, Object> map, Integer utype, Integer type){
//			logger.info("setDistotalByUtype>>>map:"+map+",utype:"+utype+",type:"+type);
		if(map != null && utype == 1 && type == 0){
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			for(String key : map.keySet()){
				Map<String, Object> dMap = new HashMap<String, Object>();
				dMap.put("dlimit", map.get(key));
				list.add(dMap);
			}
			//���մ�С��������
			Collections.sort(list, new ListSort6());
			Double dlimit = Double.valueOf(list.get(0).get("dlimit") + "");
//			logger.info("setDistotalByUtype>>>list:"+list+",utype:"+utype+",type:"+type);
			for(String key : map.keySet()){
				map.put(key, dlimit);
			}
//			logger.info("setDistotalByUtype>>>map:"+map+",utype:"+utype+",type:"+type);
		}
		return map;
	}
	
	public List<Map<String, Object>> getTicketInfo(List<Map<String, Object>> list, Integer ptype,Long uid, Integer utype){
		if(list != null && !list.isEmpty()){
			for(Map<String, Object> map : list){
				Integer type=(Integer)map.get("type");
				Integer money = (Integer)map.get("money");
				Integer resources = (Integer)map.get("resources");
				Long limitDay = (Long)map.get("limit_day");
				Double backmoney = StringUtils.formatDouble(map.get("pmoney"));
				Long btime =TimeTools.getToDayBeginTime();
				//==========��ȡ������Ԫ��ȫ��ֿ�begin=============//
				Map<String, Object> fullMap = getDistotalLimit(2, uid, Double.valueOf(money + ""), 1, utype, -1L);
				Double climit = Double.valueOf(fullMap.get("climit") + "");
				Double blimit = Double.valueOf(fullMap.get("blimit") + "");
				Double slimit = Double.valueOf(fullMap.get("slimit") + "");
				Double distotal = Double.valueOf(fullMap.get("distotal") + "");
				map.put("distotal", distotal);
				if(type == 1){
					map.put("full", slimit);
				}
				if(type == 0 && resources == 0){
					map.put("full", climit);
				}
				if(type == 0 && resources == 1){
					map.put("full", blimit);
				}
				//==========��ȡ������Ԫ��ȫ��ֿ�end=============//
				if(btime >limitDay)
					map.put("exp", 0);
				else {
					map.put("exp", 1);
				}
				map.put("isbuy",resources);
				if(resources == 1){//�����ȯ
					map.put("desc", "��"+map.get("full")+"Ԫ���Եֿ�ȫ��,���ں��˻�"+backmoney+"Ԫ�������˻�");
				}else{
					map.put("desc", "��"+map.get("full")+"Ԫ���Եֿ�ȫ��");
				}
				map.put("cname", "");
				if(type == 1 && map.get("comid") != null){
					map.put("cname", getParkNameByComid((Long)map.get("comid")));
				}
				map.put("limitday", limitDay);
			}
		}
		return list;
	}
	
	/**
	 * 
	 * @param comid
	 * @return
	 */
	public String getParkNameByComid(Long comid){
		Map<String, Object> comMap = daService.getMap("select company_name from com_info_tb where id =? ",new Object[]{comid});
		if(comMap!=null){
			return (String)comMap.get("company_name");
		}
		return "";		
	}
	
	/**
	 * @param list ȯ�б�
	 * @param ptype 0�˻���ֵ��1���²�Ʒ��2ͣ���ѽ��㣻3ֱ��;4���� 5����ͣ��ȯ
	 * @param uid 
	 * @param total ���ѽ��
	 * @param utype  0��ͨѡȯ��Ĭ�ϣ�1���ô������ֿ۽���ͣ��ȯ
	 * @return
	 */
	public List<Map<String, Object>> chooseTicketByLevel(List<Map<String, Object>> list, Integer ptype,Long uid, Double total, Integer utype, Long parkId, Long orderId){
		//�ֿ��㷨
		Map<String, Object> distotalMap = getDistotalLimit(ptype, uid, total, 0, utype, orderId);
		Double climit = Double.valueOf(distotalMap.get("climit") + "");
		Double blimit = Double.valueOf(distotalMap.get("blimit") + "");
		Double slimit = Double.valueOf(distotalMap.get("slimit") + "");
		logger.info("the up limit of distotal>>>uid:"+uid+",map:"+distotalMap+",ptype:"+ptype+",total:"+total);
		if(list != null && !list.isEmpty()){
			for(int i=0; i<list.size();i++){
				Map<String, Object> map = list.get(i);
				Integer iscanuse = 1;//0:������ 1������
				Double limit = 0d;//��ͣ��ȯ�ɵֿ۽��
				Integer type=(Integer)map.get("type");
				Integer money = (Integer)map.get("money");
				Integer resources = (Integer)map.get("resources");
				if(type == 1){//ר��ͣ��ȯ
					if(map.get("comid") != null){
						Long comid = (Long)map.get("comid");
						if(comid.intValue() != parkId.intValue()){//���Ǹó���ר��ȯ������
							iscanuse = 0;
						}
					}else{
						iscanuse = 0;
					}
					
					if(slimit >= money){
						limit = Double.valueOf(money + "");
					}else{
						limit = slimit;
						if(utype == 0){//��ѡ��������ֿ۽���ȯ
							iscanuse = 0;
						}
					}
					map.put("limit", limit);//�ֿ۽��
					map.put("level", 3);//ר��ȯ���ȼ����
				}
				if(type == 0 && resources == 0){//�ǹ���ͣ��ȯ
					if(climit >= money){
						limit = Double.valueOf(money + "");
					}else{
						limit = climit;
						if(utype == 0){//��ѡ��������ֿ۽���ȯ
							iscanuse = 0;
						}
					}
					map.put("limit", limit);//�ֿ۽��
					map.put("level", 2);//��ͨ�ǹ���ȯ����Ȩ���
				}
				if(type == 0 && resources == 1){//����ͣ��ȯ
					if(blimit >= money){
						limit = Double.valueOf(money + "");
					}else{
						iscanuse = 0;//С��˵����ȯ����ѡ 
					}
					map.put("limit", limit);//�ֿ۽��
					map.put("level", 1);//����ȯ���ȼ����
				}
				if(limit == 0){//�ֿ�0������
					iscanuse = 0;
				}
				map.put("offset",  Math.abs(limit-money));//��ֵ����ֵ
				map.put("iscanuse", iscanuse);//�Ƿ���ô������ֿ�
			}
			Collections.sort(list, new ListSort());//����iscanuse�ɴ�С����
			Collections.sort(list, new ListSort1());//��ͬ��iscanuse���յֿ۽��limit�ɴ�С����
			Collections.sort(list, new ListSort2());//��ͬ��iscanuse��limit����offset��С��������
			Collections.sort(list, new ListSort3());//��ͬ��iscanuse��limit��offset����money��С��������
			Collections.sort(list, new ListSort4());//��ͬiscanuse��limit��offset��money����level�ɴ�С����
			Collections.sort(list, new ListSort5());//��ͬiscanuse��limit��offset��money��level��ͬ����limit_day��С��������
			
			getTicketInfo(list, ptype, uid, utype);//����ͣ��ȯ������Ԫ�ɴ����ֿ۶�
			
		}
		return list;
	}
	
	class ListSort implements Comparator<Map<String, Object>>{
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			// TODO Auto-generated method stub
			Map map = getParams(o1, o2);
			Integer c1 = (Integer)map.get("c1");
			Integer c2 = (Integer)map.get("c2");
			
			return c2.compareTo(c1);
		}
		
	}
	
	class ListSort1 implements Comparator<Map<String, Object>>{
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			// TODO Auto-generated method stub
			Map map = getParams(o1, o2);
			Integer c1 = (Integer)map.get("c1");
			Integer c2 = (Integer)map.get("c2");
			
			BigDecimal b1 = (BigDecimal)map.get("b1");
			BigDecimal b2 = (BigDecimal)map.get("b2");
			if(c2.compareTo(c1) == 0){
				return b2.compareTo(b1);
			}else{
				return 0;
			}
		}
		
	}
	
	class ListSort2 implements Comparator<Map<String, Object>>{
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			// TODO Auto-generated method stub
			Map map = getParams(o1, o2);
			Integer c1 = (Integer)map.get("c1");
			Integer c2 = (Integer)map.get("c2");
			
			BigDecimal b1 = (BigDecimal)map.get("b1");
			BigDecimal b2 = (BigDecimal)map.get("b2");
			
			BigDecimal l1 = (BigDecimal)map.get("l1");
			BigDecimal l2 = (BigDecimal)map.get("l2");
			if(c2.compareTo(c1) == 0 && b2.compareTo(b1) == 0){
				return l1.compareTo(l2);
			}else{
				return 0;
			}
		}
		
	}
	
	class ListSort3 implements Comparator<Map<String, Object>>{

		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			// TODO Auto-generated method stub
			Map map = getParams(o1, o2);
			Integer c1 = (Integer)map.get("c1");
			Integer c2 = (Integer)map.get("c2");
			
			BigDecimal b1 = (BigDecimal)map.get("b1");
			BigDecimal b2 = (BigDecimal)map.get("b2");
			
			BigDecimal l1 = (BigDecimal)map.get("l1");
			BigDecimal l2 = (BigDecimal)map.get("l2");
			
			Integer m1 = (Integer)map.get("m1");
			Integer m2 = (Integer)map.get("m2");
			
			if(c2.compareTo(c1) == 0 && b2.compareTo(b1) == 0 && l2.compareTo(l1) == 0){
				return m1.compareTo(m2);
			}else{
				return 0;
			}
		}
		
	}
	
	class ListSort4 implements Comparator<Map<String, Object>>{

		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			// TODO Auto-generated method stub
			Map map = getParams(o1, o2);
			Integer c1 = (Integer)map.get("c1");
			Integer c2 = (Integer)map.get("c2");
			
			BigDecimal b1 = (BigDecimal)map.get("b1");
			BigDecimal b2 = (BigDecimal)map.get("b2");
			
			BigDecimal l1 = (BigDecimal)map.get("l1");
			BigDecimal l2 = (BigDecimal)map.get("l2");
			
			Integer m1 = (Integer)map.get("m1");
			Integer m2 = (Integer)map.get("m2");
			
			Integer e1 = (Integer)map.get("e1");
			Integer e2 = (Integer)map.get("e2");
			
			if(c2.compareTo(c1) == 0 && b2.compareTo(b1) == 0 && l2.compareTo(l1) == 0 && m2.compareTo(m1) == 0){
				return e2.compareTo(e1);
			}else{
				return 0;
			}
		}
		
	}
	
	class ListSort5 implements Comparator<Map<String, Object>>{

		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			// TODO Auto-generated method stub
			Map map = getParams(o1, o2);
			Integer c1 = (Integer)map.get("c1");
			Integer c2 = (Integer)map.get("c2");
			
			BigDecimal b1 = (BigDecimal)map.get("b1");
			BigDecimal b2 = (BigDecimal)map.get("b2");
			
			BigDecimal l1 = (BigDecimal)map.get("l1");
			BigDecimal l2 = (BigDecimal)map.get("l2");
			
			Integer m1 = (Integer)map.get("m1");
			Integer m2 = (Integer)map.get("m2");
			
			Integer e1 = (Integer)map.get("e1");
			Integer e2 = (Integer)map.get("e2");
			
			Long d1 = (Long)map.get("d1");
			Long d2 = (Long)map.get("d2");
			
			if(c2.compareTo(c1) == 0 && b2.compareTo(b1) == 0 && l2.compareTo(l1) == 0 && m2.compareTo(m1) == 0 && e2.compareTo(e1) == 0){
				return d1.compareTo(d2);
			}else{
				return 0;
			}
		}
		
	}
	
	class ListSort6 implements Comparator<Map<String, Object>>{
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			// TODO Auto-generated method stub
			BigDecimal b1 = new BigDecimal(0);
			BigDecimal b2 = new BigDecimal(0);
			if(o1.get("dlimit") != null){
				if(o1.get("dlimit") instanceof Double){
					Double ctotal = (Double)o1.get("dlimit");
					b1 = b1.valueOf(ctotal);
				}else{
					b1 = (BigDecimal)o1.get("dlimit");
				}
			}
			if(o2.get("dlimit") != null){
				if(o2.get("dlimit") instanceof Double){
					Double ctotal = (Double)o2.get("dlimit");
					b2 = b2.valueOf(ctotal);
				}else{
					b2 = (BigDecimal)o2.get("dlimit");
				}
			}
			return b1.compareTo(b2);
		}
		
	}
	
	public List<Map<String, Object>> getCarType(Long comid){
		Map<String, Object> map = daService.getMap("select car_type from com_info_tb where id=? ", new Object[]{comid});
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		
		if(map != null){
			Integer car_type = (Integer)map.get("car_type");
			if(car_type != 0){
				List<Map<String, Object>> list = daService.getAll("select id as value_no,name as value_name from car_type_tb where comid=? order by sort , id desc ", new Object[]{comid});
				if(!list.isEmpty()){
					result.addAll(list);
				}else {
					Map<String, Object> bigMap = new HashMap<String, Object>();
					bigMap.put("value_name","С��");
					bigMap.put("value_no", 1);
					Map<String, Object> smallMap = new HashMap<String, Object>();
					smallMap.put("value_name","��");
					smallMap.put("value_no", 2);
					result.add(bigMap);
					result.add(smallMap);
				}
			}else {
				Map<String, Object> firtstMap = new HashMap<String, Object>();
				firtstMap.put("value_name","ͨ��");
				firtstMap.put("value_no", 0);
				result.add(firtstMap);
			}
		}
		return result;
	}
	private Map<String, Object> getParams(Map<String, Object> o1, Map<String, Object> o2){
		Map<String, Object> map = new HashMap<String, Object>();
		Integer c1 = (Integer)o1.get("iscanuse");
		if(c1 == null) c1 = 0;
		Integer c2 = (Integer)o2.get("iscanuse");
		if(c2 == null) c2 = 0;
		map.put("c1", c1);
		map.put("c2", c2);
		
		BigDecimal b1 = new BigDecimal(0);
		BigDecimal b2 = new BigDecimal(0);
		if(o1.get("limit") != null){
			if(o1.get("limit") instanceof Double){
				Double ctotal = (Double)o1.get("limit");
				b1 = b1.valueOf(ctotal);
			}else{
				b1 = (BigDecimal)o1.get("limit");
			}
		}
		if(o2.get("limit") != null){
			if(o2.get("limit") instanceof Double){
				Double ctotal = (Double)o2.get("limit");
				b2 = b2.valueOf(ctotal);
			}else{
				b2 = (BigDecimal)o2.get("limit");
			}
		}
		map.put("b1", b1);
		map.put("b2", b2);

		BigDecimal l1 = new BigDecimal(0);
		BigDecimal l2 = new BigDecimal(0);
		if(o1.get("offset") != null){
			if(o1.get("offset") instanceof Double){
				Double ctotal = (Double)o1.get("offset");
				l1 = l1.valueOf(ctotal);
			}else{
				l1 = (BigDecimal)o1.get("offset");
			}
		}
		if(o2.get("offset") != null){
			if(o2.get("offset") instanceof Double){
				Double ctotal = (Double)o2.get("offset");
				l2 = l2.valueOf(ctotal);
			}else{
				l2 = (BigDecimal)o2.get("offset");
			}
		}
		map.put("l1", l1);
		map.put("l2", l2);
		
		Integer m1 = (Integer)o1.get("money");
		if(m1 == null) m1 = 0;
		Integer m2 = (Integer)o2.get("money");
		if(m2 == null) m2 = 0;
		map.put("m1", m1);
		map.put("m2", m2);
		
		Integer e1 = (Integer)o1.get("level");
		if(e1 == null) e1 = 0;
		Integer e2 = (Integer)o2.get("level");
		if(e2 == null) e2 = 0;
		map.put("e1", e1);
		map.put("e2", e2);
		
		Long d1 = (Long)o1.get("limit_day");
		if(d1 == null) d1 = 0L;
		Long d2 = (Long)o2.get("limit_day");
		if(d2 == null) d2 = 0L;
		map.put("d1", d1);
		map.put("d2", d2);
		
		return map;
	}
	//----------------ѡȯ�߼�end--------------------//
}
