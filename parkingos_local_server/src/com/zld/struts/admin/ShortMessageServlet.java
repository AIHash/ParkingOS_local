package com.zld.struts.admin;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.zld.AjaxUtil;
import com.zld.impl.CommonMethods;
import com.zld.service.DataBaseService;
import com.zld.utils.Check;
import com.zld.utils.RequestUtil;

public class ShortMessageServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1844412464330025572L;

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String arg = RequestUtil.getString(request, "args");
		System.err.println("=========================>>>>>>>>>>>���Żص���args:"+arg);
		String args[]  = arg.split(",");
		String mobile = "";//RequestUtil.getString(request, "mobile");
		Integer code =0;// RequestUtil.getInteger(request, "code",0);
		String content = "";
		String flag = "";
		if(args.length>3){
			mobile = args[2];
			content = args[3].trim();
			if(content.length()>5&&Check.isNumber(content.substring(0,6)))
				code = Integer.valueOf(content.substring(0,6));
			if(content.length()>7)
				flag = content.substring(6,8);
				
		}
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(getServletContext());
		DataBaseService daService = (DataBaseService) ctx.getBean("dataBaseService");
		CommonMethods methods = (CommonMethods)ctx.getBean("commonMethods");
		Map userMap = null;
		if(flag.equals("00")){//�շ�Ա
			userMap = daService.getPojo("select * from user_info_tb where mobile=? and auth_flag = ?", new Object[]{mobile,2});
		}else {//����
			userMap = daService.getPojo("select * from user_info_tb where mobile=? and auth_flag=?", new Object[]{mobile,4});
		}
		int result = 0;
		System.err.println("=========================>>>>>>>>>>>���Żص���mobile:"+mobile+",code:"+code);
		if(userMap!=null&&!userMap.isEmpty()){//�û���Ϣ���� 
			System.err.println(mobile+">>>>>>>>>>>���Żص����ͻ�����");
			Long uin = (Long)userMap.get("id");
			Map cMap = daService.getMap("select uin from verification_code_tb " +
					"where verification_code=?", new Object[]{code});
			Long codeuin = -1L;
			if(cMap!=null&&cMap.get("uin")!=null){
				codeuin = (Long)cMap.get("uin");
			}
			if(uin.equals(codeuin)){//��֤����ȷ��������һ������
				System.err.println(mobile+"=========================>>>>>>>>>>>���Żص�����֤����ȷ��auth_flag=1 ");
				result= daService.update("delete from verification_code_tb where uin =?",new Object[]{userMap.get("id")});
				//���³���״̬ �����ߣ������¼ʱ��
				result= daService.update("update user_info_tb set online_flag=? ,logon_time=? where id=?", new Object[]{1,System.currentTimeMillis()/1000,userMap.get("id")});
				System.out.println(">>>>>>>>>>>���Żص����ͻ�����>>>>д��ͣ��ȯ:"+methods.checkBonus(mobile, (Long)userMap.get("id")));
			}else {
				//���³���״̬ ����֤����󣬱����¼ʱ��
				System.err.println(mobile+"=========================>>>>>>>>>>>���Żص�����֤����� ,auth_flag=-1");
				result= daService.update("update user_info_tb set online_flag=? ,logon_time=? where id=?", new Object[]{-1,System.currentTimeMillis()/1000,codeuin});
			}
		}
		AjaxUtil.ajaxOutput(response,"0");
	}
}
