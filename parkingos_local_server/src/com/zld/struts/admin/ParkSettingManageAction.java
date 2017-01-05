package com.zld.struts.admin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.zld.AjaxUtil;
import com.zld.service.DataBaseService;
import com.zld.utils.RequestUtil;
import com.zld.utils.TimeTools;
/**
 * �ܹ���Ա   ͣ����ע���޸�ɾ����
 * @author Administrator
 *
 */
public class ParkSettingManageAction extends Action {

	@Autowired
	private DataBaseService daService;
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		Integer parkId = RequestUtil.getInteger(request, "id", 0);
		if(action.equals("")){
			request.setAttribute("parkid", parkId);
			Map<String, Object> parkMap = daService.getPojo("select * from com_info_tb where id=?",
					new Object[]{parkId});
			String info="";
//			Integer iscancel = (Integer)parkMap.get("iscancel");
//			String mg = "��ȥ��";
//			String bmg = "ȥ��ȡ����ť";
//			if(iscancel!=null&&iscancel==1){
//				bmg = "����ȡ����ť";
//				mg = "��ȥ��";
//			}
			if(parkMap!=null)
				info ="���ƣ�"+parkMap.get("company_name")+"����ַ��"+parkMap.get("address")+"<br/>����ʱ�䣺"
			+TimeTools.getTime_yyyyMMdd_HHmm((Long)parkMap.get("create_time")*1000)+"����λ������"+parkMap.get("parking_total")
			+"������λ��"+parkMap.get("share_number")+"����γ�ȣ�("+parkMap.get("longitude")+","+parkMap.get("latitude")+")";
			request.setAttribute("parkinfo", info);
			//request.setAttribute("iscancel", info);
			return mapping.findForward("list");
		}else if(action.equals("setcancel")){
			Integer type = RequestUtil.getInteger(request, "iscancel", 0);
			int result = daService.update("update com_info_tb set iscancel=? where id=?",
					new Object[]{type,parkId});
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("parkclientset")){
			Map<String, Object> parkMap = daService.getPojo("select * from com_info_tb where id=?",
					new Object[]{parkId});
			Integer isshowepay = (Integer)parkMap.get("isshowepay");
			String mg = "��ʾ";
			String bmg = "�������ʾ";
			if(isshowepay!=null&&isshowepay==0){
				bmg = "�����ʾ";
				mg = "����ʾ";
			}
			Integer iscancel = (Integer)parkMap.get("iscancel");
			String mg2 = "��ȥ��";
			String bmg2 = "ȥ��ȡ����ť";
			if(iscancel!=null&&iscancel==1){
				bmg2 = "����ȡ����ť";
				mg2 = "��ȥ��";
			}
			request.setAttribute("mg", mg);
			request.setAttribute("bmg", bmg);
			request.setAttribute("parkid", parkId);
			request.setAttribute("isshowepay", isshowepay);
			request.setAttribute("mg2", mg2);
			request.setAttribute("bmg2", bmg2);
			request.setAttribute("iscancel", iscancel);
			return mapping.findForward("parkclientset");
		}else if(action.equals("setisshow")){
			Integer type = RequestUtil.getInteger(request, "isshow", 0);
			int result = daService.update("update com_info_tb set isshowepay=? where id=?",
					new Object[]{type,parkId});
			AjaxUtil.ajaxOutput(response, result+"");
		}
		return null;
	}

}
