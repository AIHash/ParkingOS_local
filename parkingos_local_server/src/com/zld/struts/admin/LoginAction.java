package com.zld.struts.admin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.zld.service.DataBaseService;
import com.zld.service.PgOnlyReadService;
import com.zld.utils.Check;
import com.zld.utils.RequestUtil;
import com.zld.utils.ZLDType;
/**
 * ��¼���ܹ���Ա��ͣ������̨����Ա������Ƚ�ɫ���Ե�¼ 
 * @author Administrator
 *
 */
public class LoginAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	
	@Autowired
	private PgOnlyReadService pgOnlyReadService;
	
	private Logger logger = Logger.getLogger(LoginAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String username =RequestUtil.processParams(request, "username");
		String pass =RequestUtil.processParams(request, "pass");
		String sql = "select * from user_info_tb where ";//";
		Object [] values = null;
		if(Check.checkUin(username)){
			values=new Object[]{Long.valueOf(username),pass};
			sql+=" id=? and password=?" ;
		}else{
			values=new Object[]{username,pass};
			sql +=" strid=? and password=? ";
		}
		String target = "success";
		Map user = daService.getPojo(sql, values);
		if(user==null){
			request.setAttribute("errormessage", "�ʺŻ����벻��ȷ!");
			request.setAttribute("username", username);
			return mapping.findForward("fail");
		}
		if("admin".equals(username)){
//			request.getSession().setAttribute("comid",user.get("comid"));
			request.getSession().setAttribute("userinfo",user);
			request.getSession().setAttribute("loginuin",user.get("id"));
			request.getSession().setAttribute("userid", username);
			request.getSession().setAttribute("nickname", user.get("nickname"));
			return mapping.findForward("config");
		}
		Long role = Long.valueOf(user.get("auth_flag").toString());
		//role: 0�ܹ���Ա��1ͣ������̨����Ա ��2�����շ�Ա��3����4����  5�г�רԱ 6¼��Ա
		if(role.intValue()==ZLDType.ZLD_COLLECTOR_ROLE||role.intValue()==ZLDType.ZLD_CAROWER_ROLE||role.intValue() == ZLDType.ZLD_KEYMEN){//�����շ�Ա���������ܵ�¼��̨
			request.setAttribute("errormessage", "û�в�ѯ��̨����Ȩ�ޣ�����ϵ����Ա!");
			target="fail";
		}else if(role.intValue()==ZLDType.ZLD_PARKADMIN_ROLE){
			target ="parkmanage";
			Long count = pgOnlyReadService.getLong(
							"select count(id) from com_info_tb where state=? and id=? ",
							new Object[] { 0, user.get("comid") });
			if(count == 0){
				request.setAttribute("errormessage", "���������ڻ��߳���δͨ�����!");
				target="fail";
			}
		}else if(role.intValue()==ZLDType.ZLD_ACCOUNTANT_ROLE){
			target ="finance";
		}else if(role.intValue()==ZLDType.ZLD_CARDOPERATOR){
			target ="cardoperator";
		}else if(role.intValue()==ZLDType.ZLD_MARKETER){//�г�רԱ ��¼��̨
			request.getSession().setAttribute("marketerid",user.get("id"));
			target ="marketer";
		}else if(role.intValue()==ZLDType.ZLD_RECORDER||role.intValue()==ZLDType.ZLD_KEFU||role.intValue()==ZLDType.ZLD_QUERYKEFU){
			target = "recorder";
		}
		request.getSession().setAttribute("role",role );
		request.getSession().setAttribute("comid",user.get("comid"));
		request.getSession().setAttribute("userinfo",user);
		request.getSession().setAttribute("loginuin",user.get("id"));
		request.getSession().setAttribute("userid", username);
		request.getSession().setAttribute("nickname", user.get("nickname"));
		
//		List<Object[]> valuesList = ReadFile.praseFile();
//		int result = daService.bathInsert("insert into com_info_tb (longitude,latitude,company_name,address,type) values(?,?,?,?,?)",
//				valuesList, new int[]{3,3,12,12,4});
//		System.out.println(result);
		return mapping.findForward(target);
	}

}