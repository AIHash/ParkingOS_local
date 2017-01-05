package com.zld.struts.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.zld.utils.Check;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;

public class ShopLoginAction extends Action {
	@Autowired
	private DataBaseService daService;
	@Autowired
	private PublicMethods publicMethods;
	
	private Logger logger = Logger.getLogger(ShopLoginAction.class);
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String,Object> infoMap = new HashMap<String, Object>();
		String action = RequestUtil.processParams(request, "action");
		logger.info("action:"+action);
		if(action.equals("forpass")){//�һ����룬���͵�ע���ֻ���
			String userId = RequestUtil.processParams(request, "username");
			Map userMap = daService.getPojo("select id,password,mobile from user_info_tb where id=?",
					new Object[]{Long.valueOf(userId)});
			if(userMap!=null){
				String mobile = (String)userMap.get("mobile");
				logger.equals(mobile);
				if(mobile==null||"".equals(mobile)){
					AjaxUtil.ajaxOutput(response, "{\"info\":\"��ע���ʺ�ʱû����д�ֻ�������ϵ�ͷ���Ա��\"}");
				}else if(Check.checkPhone(mobile,"m")){
					String _mString = mobile.substring(0,3)+"****"+mobile.substring(7);
					//SendMessage.sendMessage((String)userMap.get("mobile"),(String)userMap.get("password"));
					AjaxUtil.ajaxOutput(response, "{\"info\":\"������ͨ�����ŷ��͵���ע����ֻ���["+_mString+"]������գ���ͣ������\"}");
				}else {
					AjaxUtil.ajaxOutput(response, "{\"info\":\"��ע����ֻ��Ų��Ϸ���\"}");
				}
			}else {
				AjaxUtil.ajaxOutput(response, "{\"info\":\"�ʺŲ����ڣ�\"}");
			}
			return null;
		}else if(action.equals("editpass")){//�޸����룬���͵�ע���ֻ���
			Long userId = RequestUtil.getLong(request, "username",-1L);
			String oldPass = RequestUtil.processParams(request, "oldpass");
			String newPass = RequestUtil.processParams(request, "newpass");
			if(oldPass.length()<32){
				oldPass =StringUtils.MD5(oldPass);
				oldPass = StringUtils.MD5(oldPass+"zldtingchebao201410092009");
			}
			Long count  = daService.getLong("select count(*) from user_info_tb where id=? and md5pass=? ", 
					new Object[]{userId,oldPass});
			int result = 0;
			if(newPass.length()<32){
				newPass =StringUtils.MD5(newPass);
				newPass = StringUtils.MD5(newPass+"zldtingchebao201410092009");
			}
			if(count>0){
				result = daService.update("update user_info_tb set md5pass=? where id=? ",
						new Object[]{newPass,userId});
			}else
				result = -1;
			logger.info("oldpass:"+oldPass+",newpass:"+newPass);
			AjaxUtil.ajaxOutput(response, result+"");
			return null;
		}else if(action.equals("editname")){
			String name = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "name"));
			Long uin = RequestUtil.getLong(request, "uin", -1L);
			int result = 0;
			if(uin!=-1){
				result = daService.update("update user_info_tb set nickname=? where id=? ",
						new Object[]{name,uin});
			}
			AjaxUtil.ajaxOutput(response, result+"");
			return null;
		}else if(action.equals("editphone")){
			String mobile = RequestUtil.processParams(request, "mobile");
			Long uin = RequestUtil.getLong(request, "uin", -1L);
			int result = 0;
			if(uin!=-1){
				result = daService.update("update user_info_tb set  mobile=? where id=? ",
						new Object[]{mobile,uin});
			}
			AjaxUtil.ajaxOutput(response, result+"");
			return null;
		}
		String username =RequestUtil.processParams(request, "username");
		String pass =RequestUtil.processParams(request, "password");
		String version = RequestUtil.getString(request, "version");
		logger.info("user:"+username+",pass:"+pass);
		String sql = "select * from user_info_tb where id=? and md5pass=?";// and auth_flag=?";
		if(pass.length()<32){
			//md5���� �����ɹ���ԭ����md5�󣬼���'zldtingchebao201410092009'�ٴ�md5
			pass =StringUtils.MD5(pass);
			pass = StringUtils.MD5(pass +"zldtingchebao201410092009");
		}
		if(!StringUtils.isNumber(username)){
			infoMap.put("info", "fail");
			AjaxUtil.ajaxOutput(response, StringUtils.createXML(infoMap));
			return null;
		}
		Map user = daService.getPojo(sql, new Object[]{Long.valueOf(username),pass});//,ZLDType.ZLD_COLLECTOR_ROLE});
		//logger.info(user);
		
		if(user==null){
			infoMap.put("info", "�û������������");
		}else {
			Long uin = (Long)user.get("id");
			String token = StringUtils.MD5(username+pass+System.currentTimeMillis());
			infoMap.put("info", "success");
			infoMap.put("token", token);
			infoMap.put("role", user.get("auth_flag"));
			infoMap.put("name", user.get("nickname"));
			infoMap.put("shop_id", user.get("shop_id"));
			doSaveSession(uin,token,version);
			daService.update("update user_info_Tb set logon_time=? where id=?",
					new Object[]{System.currentTimeMillis()/1000,user.get("id")});
			logger.info(username+"��¼�ɹ�...");
		}
		AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
		return null;
		//http://192.168.199.239/zld/shoplogin.do?username=21629&password=111111
	}
	
	/**
	 * ����token�����ݿ���
	 * @param uin
	 * @param token
	 */
	private void doSaveSession(Long uin,String token,String version ){
		//��ɾ���г�רԱ�ϴε�¼ʱ��token
		daService.update("delete from user_session_tb where uin=? ", new Object[]{uin});
		//���汾�ε�¼��token
		daService.update("insert into user_session_tb (uin,token,create_time,version) " +
				"values (?,?,?,?)", 
				new Object[]{uin,token,System.currentTimeMillis()/1000,version});
	}
	
}
