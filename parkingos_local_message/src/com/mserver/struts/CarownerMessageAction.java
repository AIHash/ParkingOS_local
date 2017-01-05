package com.mserver.struts;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.mserver.AjaxUtil;
import com.mserver.service.PgOnlyReadService;
import com.mserver.service.PgService;
import com.mserver.utils.RequestUtil;
import com.mserver.utils.StringUtils;
import com.mserver.utils.TimeTools;

/**
 * ������������Ϣ����
 * 
 * @author Administrator
 * 
 */
public class CarownerMessageAction extends Action {

	@Autowired
	private PgService pgService;
	@Autowired
	private PgOnlyReadService pOnlyReadService;
	
	private Logger logger = Logger.getLogger(CarownerMessageAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String mobile = RequestUtil.processParams(request, "mobile");
		String action = RequestUtil.processParams(request, "action");
		Long msgId = RequestUtil.getLong(request, "msgid", -1L);
		logger.error("��������Ϣ...�ֻ���"+mobile+" ,msgid:"+msgId+",action��"+action);
		if (mobile == null || "null".equals(mobile) || "".equals(mobile)) {
			AjaxUtil.ajaxOutput(response, "{}");
			return null;
		}
		if(action.equals("checkcode")){//ȡ��½������ֻ���֤���ղ���ʱ���ֻ�������Ϣ��������������������ͣ�������ж��Ƿ����ֻ��û����˵�¼
			// http://127.0.0.1/mserver/carmessage.do?action=checkcode&mobile=13860132164
			Map userMap = pOnlyReadService.getMap("select * from user_info_tb where mobile=?" +
					" and auth_flag=?", new Object[]{mobile,4});
			String result ="{}";
			if(userMap!=null){
				Long online= (Long)userMap.get("online_flag");
				Long ltime = (Long)userMap.get("logon_time");
				Long ntime = System.currentTimeMillis()/1000;
				if(online!=null&&ntime-ltime<200){//�������Ѿ��ص�������
					if(online==-1){//��¼ʧ��
						logger.error(">>>>>>>>>>>>>������"+mobile+",checkcode:-1,��¼ʧ��");
						result= "{\"mtype\":\"3\",\"msgid\":\"\",\"info\":{\"result\":\"-1\"}}";
					}else if(online==1){
						Map carMap = pOnlyReadService.getMap("select * from car_info_tb where uin=? ",new Object[]{ userMap.get("id")});
						String car_number = "";
						if(carMap!=null)
							car_number = (String)carMap.get("car_number");
						logger.error(">>>>>>>>>>>>>������"+mobile+",checkcode:1,��¼�ɹ�,car_number:"+car_number);
						if(car_number!=null&&car_number.length()>1)//��¼�ɹ�
							result= "{\"mtype\":\"3\",\"msgid\":\"\",\"info\":{\"result\":\"1\"}}";
						else//���복�ƺ�
							result= "{\"mtype\":\"3\",\"msgid\":\"\",\"info\":{\"result\":\"2\"}}";
						int ret = pgService.update("update user_info_tb set online_flag=? where id=? ", new Object[]{22,userMap.get("id")});
						logger.error(mobile+"��ȡ����¼��Ϣ������online_flag=0:ret="+ret);
					}
				}
			}
			logger.error(mobile+",��¼��Ϣ:"+result);
			AjaxUtil.ajaxOutput(response, result);
			return null;
		}
		Long uin = validMobile(mobile);
		// http://127.0.0.1/mserver/carmessage.do?mobile=13860132164&msgid=112
		if (uin != null) {
			String sql = "select * from order_message_tb where uin=? ";// ";//order by id desc limit ?";
			Map messageMap = null;
			Long ltime = System.currentTimeMillis()/1000-60*60;
			if(msgId>0){
				messageMap = pOnlyReadService.getMap(sql+" and id> ? and create_time >? order by id desc limit ?",
						new Object[] { uin,msgId, ltime,1 });
			}else {
				messageMap = pOnlyReadService.getMap(sql +" and already_read =? and create_time >? order by id desc limit ?", 
						new Object[] { uin,0, ltime,1 });
			}
			
			if (messageMap != null && !messageMap.isEmpty()) {
				Integer mtype =(Integer) messageMap.get("message_type");
				if(mtype==null){
					AjaxUtil.ajaxOutput(response, "{}");
					return null;
				}
				Map<String, Object> infomMap = new HashMap<String, Object>();
				if(mtype==2){//��ֵ�������Ʒ��Ϣ
					infomMap.put("result",  messageMap.get("state"));
					infomMap.put("errmsg",  messageMap.get("duartion"));
					infomMap.put("bonusid", messageMap.get("orderid"));
				}else if(mtype==0||mtype==9){//������Ϣ
					Long comId = (Long)messageMap.get("comid");
					Long orderId = (Long)messageMap.get("orderid");
					Map comMap = pOnlyReadService.getMap("select company_name from com_info_tb where id=?",new Object[]{comId});
					String cname = "";//(String)pOnlyReadService.getObject("select company_name from com_info_tb where id=?",new Object[]{comId}, String.class);
					if(comMap!=null&&comMap.get("company_name")!=null)
						cname = (String)comMap.get("company_name");
					infomMap.put("parkname",cname);
					Integer state = (Integer)messageMap.get("state");
					infomMap.put("btime", messageMap.get("btime"));
					infomMap.put("etime", messageMap.get("etime"));
					infomMap.put("total", messageMap.get("order_total"));
					infomMap.put("state", state);//0:δ֧�� 1����֧�� 
					infomMap.put("orderid",orderId);
					if(state==2){//֧����ɣ���ѯ��û�к������
						Long count = null;
//						Map bMap  =pOnlyReadService.getMap("select id from bouns_tb where uin=? and order_id=? and ctime > ? ",
//								new Object[]{uin,orderId,TimeTools.getToDayBeginTime()});
						Map bMap  = null;
						if(orderId==997||orderId==998||orderId==-1){
							bMap= pOnlyReadService.getMap("select id,btime from order_ticket_tb where uin=? and order_id=? and ctime > ? order by id desc limit ?",
									new Object[]{uin,orderId,TimeTools.getToDayBeginTime(),1});
							if(bMap!=null){
								Long btime = (Long)bMap.get("btime");
								if(btime!=null&&btime>10000){//�Ѿ�����������ٷ���
									bMap=null;
								}
							}
						}else {
							bMap =pOnlyReadService.getMap("select id from order_ticket_tb where uin=? and order_id=? and ctime > ? ",
									new Object[]{uin,orderId,TimeTools.getToDayBeginTime()});
						}
						if(bMap!=null&&bMap.get("id")!=null)
							count = (Long)bMap.get("id");
						if(count!=null&&count>0){
							infomMap.put("bonusid", count);
						}
					}
				}
				String json = StringUtils.createJson(infomMap);
				AjaxUtil.ajaxOutput(response, "{\"mtype\":\""+messageMap.get("message_type")+"\",\"msgid\":\""+messageMap.get("id")+"\",\"info\":"+json+"}");
				logger.error("�����յ���Ϣ��"+"{\"mtype\":"+messageMap.get("message_type")+",\"msgid\":\""+messageMap.get("id")+"\",\"info\":"+json+"}");
				//������ϢΪ�Ѷ�״̬
				pgService.update("update order_message_tb set already_read =? where uin=? ", new Object[]{1,uin});
			}else {
				AjaxUtil.ajaxOutput(response, "{}");
			}
		} else {
			AjaxUtil.ajaxOutput(response, "{}");
		}
		return null;
	}

	/**
	 * @param mobile
	 * @return �������
	 */
	private Long validMobile(String mobile) {
		Map userMap = pOnlyReadService.getMap(
				"select * from user_info_tb where mobile=? and auth_flag=?",
				new Object[] { mobile, 4 });
		Long uin = null;
		if (userMap != null && userMap.get("id") != null) {
			uin = (Long) userMap.get("id");
		}
		return uin;
	}

}