package com.zld.struts.admin;

import java.io.IOException;
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
import com.zld.CustomDefind;
import com.zld.service.DataBaseService;
import com.zld.utils.ExportExcelUtil;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;
/**
 * ͣ������̨����Ա��¼�����ֹ��� 
 * @author Administrator
 *
 */
public class WithDrawerManageAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	
	private Logger logger = Logger.getLogger(WithDrawerManageAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		Long comid = (Long)request.getSession().getAttribute("comid");
		if(comid==null){
			response.sendRedirect("login.do");
			return null;
		}
		if(action.equals("")){
			return mapping.findForward("list");
		}else if(action.equals("quickquery")){
			String sql = "select w.*,a.name,c.company_name cname from withdrawer_tb w left join com_account_tb a on w.acc_id=a.id " +
					"left join  com_info_tb c on w.comid=c.id where 1=1 ";
			String countSql = "select count(*) from withdrawer_tb w left join com_account_tb a on w.acc_id=a.id " +
					"left join  com_info_tb c on w.comid=c.id where 1=1 ";
			String parkback = CustomDefind.getValue("WITHDRAWERBACK");
			Object[] obj = null;
			List<Object> li = new ArrayList<Object>();
			if(StringUtils.isNotNull(parkback)){
				String []str = parkback.split(",");
				StringBuffer sb = new StringBuffer("and w.comid not in(");
				obj = new Object[str.length];
				for (int i = 0; i < str.length; i++) {
					sb.append("?,");
					obj[i] = Long.parseLong(str[i]);
					li.add(Long.parseLong(str[i]));
				}
				if(sb.length()>21){
					String s = sb.substring(0, sb.length()-1);
					s+=")";
					sql+=s;
					countSql+=s;
				}
			}
			Long count = daService.getLong(countSql,obj);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			List list = null;//daService.getPage(sql, null, 1, 20);
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			if(count>0){
				list = daService.getAll(sql+" order by id desc", li, pageNum, pageSize);
				//System.out.println(sql+":");
			}
			setComName(list);
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("query")){
			String sql = "select w.*,a.name,c.company_name cname from withdrawer_tb w left join com_account_tb a on w.acc_id=a.id " +
					"left join  com_info_tb c on w.comid=c.id where 1=1 ";
			String countSql = "select count(*) from withdrawer_tb w left join com_account_tb a on w.acc_id=a.id " +
					"left join  com_info_tb c on w.comid=c.id where 1=1 ";
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"withdraw","w",new String[]{"name","cname"});
			SqlInfo ssqlInfo = getSuperSqlInfo(request);
			List<Object> params = new ArrayList<Object>();
			if(sqlInfo!=null){
				if(ssqlInfo!=null)
					sqlInfo = SqlInfo.joinSqlInfo(sqlInfo,ssqlInfo, 2);
				countSql+=" and "+ sqlInfo.getSql();
				sql +=" and "+sqlInfo.getSql();
				params= sqlInfo.getParams();
			}else if(ssqlInfo!=null){
				countSql+=" and "+ ssqlInfo.getSql();
				sql +=" and "+ssqlInfo.getSql();
				params= ssqlInfo.getParams();
			}
			String parkback = CustomDefind.getValue("WITHDRAWERBACK");
			Object[] obj = null;
			List<Object> li = new ArrayList<Object>();
			if(StringUtils.isNotNull(parkback)){
				String []str = parkback.split(",");
				StringBuffer sb = new StringBuffer(" and w.comid not in(");
				obj = new Object[str.length];
				for (int i = 0; i < str.length; i++) {
					sb.append("?,");
					obj[i] = Long.parseLong(str[i]);
					li.add(Long.parseLong(str[i]));
				}
				if(sb.length()>21){
					String s = sb.substring(0, sb.length()-1);
					s+=")";
					sql+=s;
					countSql+=s;
					params.addAll(li);
				}
			}
			//System.out.println(sqlInfo);
			Long count= daService.getCount(countSql, params);
			List list = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql+" order by  id desc", params, pageNum, pageSize);
				//System.out.println(sql+":");
			}
			//setComName(list);
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("edit")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			Integer state = RequestUtil.getInteger(request, "state", 0);
			int result = 0;
			if(id!=-1){
				result = daService.update("update withdrawer_tb set state =?,update_time=? where id =? ",new Object[]{state,System.currentTimeMillis()/1000,id});
			}
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("excle")){
			//�������� ���ֽ��  ����ʱ�� �������� ״̬ 
			String [] heards = new String[]{"��������","����ʱ��","��������","״̬","�ʻ�����","���ֽ��","�˺�","������/֧��","������","�ֻ�","���֤��","�����˺�"};
			//String [] heards = new String[]{"��������","���ֽ��","����ʱ��","��������","״̬"};
			List<List<String>> bodyList = new ArrayList<List<String>>();
			//String sql = "select * from withdrawer_tb where state=? ";
			String sql = "select t.*,c.atype,c.card_number,c.name,c.mobile,c.area,c.bank_name,c.bank_pint,c.user_id ,c.uin from withdrawer_tb t left join com_account_tb c on t.acc_id=c.id where t.state=? and wtype=? ";
			String [] f = new String[]{"comid","create_time","update_time","state","name","amount","card_number","bank_name","area","mobile","user_id","uin"};
			
			Integer state = RequestUtil.getInteger(request, "state_start", 0);
			if(state>11){//�����Թ�����
				List<Object> publicAcc = getpublicAccount();
				heards=(String[])publicAcc.get(0);	
				sql =(String)publicAcc.get(1);
				f=(String[])publicAcc.get(2);
			}
			
			List<Object> params = new ArrayList<Object>();
			if(state>11){//�����Թ�����
				params.add(state-12);
				params.add(2);
			}else if(state>5){//������˾����
				params.add(state-6);
				params.add(0);
			}else {
				params.add(state);
				params.add(1);
			}
			String parkback = CustomDefind.getValue("WITHDRAWERBACK");
			Object[] obj = null;
			List<Object> li = new ArrayList<Object>();
			if(StringUtils.isNotNull(parkback)){
				String []str = parkback.split(",");
				StringBuffer sb = new StringBuffer("and t.comid not in(");
				obj = new Object[str.length];
				for (int i = 0; i < str.length; i++) {
					sb.append("?,");
					obj[i] = Long.parseLong(str[i]);
					li.add(Long.parseLong(str[i]));
				}
				if(sb.length()>21){
					String s = sb.substring(0, sb.length()-1);
					s+=")";
					sql+=s;
					params.addAll(li);
				}
			}
			List list = daService.getAllMap(sql+" order by id desc",params);
			if(list!=null&&list.size()>0){
				setComName(list);
				for(int i=0;i<list.size();i++){
					Map map = (Map)list.get(i);
					Integer atype =(Integer) map.get("atype");
					String info="";
					if(atype==1&&state<12){//֧����
						heards = new String[]{"��������","����ʱ��","��������","״̬","�ʻ�����","���ֽ��","֧�����˺�","�ֻ�"};
						f = new String[]{"comid","create_time","update_time","state","name","amount","card_number","mobile"};
					}
					List<String> values = new ArrayList<String>();
					for(String field : f){
						if(field.equals("bank_name")){
							values.add(map.get(field)+""+map.get("bank_pint"));
						}else if(field.equals("state")){
							int s = Integer.parseInt(map.get("state")+"");
							if(s==0){
								values.add("�ȴ����");
							}else if(s==2){
								values.add("������");
							}else if(s==3){
								values.add("��֧��");
							}else if(s==4){
								values.add("����ʧ��");
							}else if(s==5){
								values.add("�ӳٴ���");
							}
						}else {
							
							values.add(map.get(field)+"");
						}
					}
					bodyList.add(values);
				}
			}
			String fname = "��������" + com.zld.utils.TimeTools.getDate_YY_MM_DD();
			fname = StringUtils.encodingFileName(fname);
			java.io.OutputStream os;
			try {
				os = response.getOutputStream();
				response.reset();
				response.setHeader("Content-disposition", "attachment; filename="
						+ fname + ".xls");
				ExportExcelUtil importExcel = new ExportExcelUtil("��������",
						heards, bodyList);
				importExcel.createExcelFile(os);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(action.equals("getusername")){
			Long uin = RequestUtil.getLong(request, "uin", -1L);
			String name = "";
			if(uin!=-1){
				Map userMap = daService.getMap("select nickname from user_info_tb where id =?", new Object[]{uin});
				if(userMap!=null&&userMap.get("nickname")!=null)
					name = userMap.get("nickname")+"";
					
			}
			AjaxUtil.ajaxOutput(response, name);
		}else if(action.equals("multiedit")){
			String ids = RequestUtil.getString(request, "ids");
			Integer state = RequestUtil.getInteger(request, "state", -1);
			//System.err.println(ids+"=="+state);
			int result = -1;
			if(!ids.equals("")&&state!=-1){
				String [] allids = ids.split(",");
				List<Object> params = new ArrayList<Object>();
				String parmstrs = "";
				for(int i=0;i<allids.length;i++){
					if(i!=0)
						parmstrs+=",";
					parmstrs +="?";
					params.add(Long.valueOf(allids[i]));
				}
				params.add(0,System.currentTimeMillis()/1000);
				params.add(0,state);
				result = daService.update("update withdrawer_tb set state = ?,update_time = ?  where id in("+parmstrs+")", params);
			}
			
			AjaxUtil.ajaxOutput(response, result+"");
		}
		return null;
	}
	
	
	private void setComName(List list){
		if(list!=null&&!list.isEmpty()){
			List<Object> comIdList = new ArrayList<Object>();
			String params = "";
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				Long comid = (Long)map.get("comid");
				if(!comIdList.contains(comid)){
					if(i!=0)
						params +=",";
					params +="?";
					comIdList.add(comid);
				}
				Long ctime = (Long)map.get("create_time");
				Long etime = (Long)map.get("update_time");
				if(etime!=null){
					map.put("update_time", TimeTools.getTime_yyyyMMdd_HHmm(etime*1000));
				}
				Integer state = (Integer)map.get("state");
				map.put("create_time", TimeTools.getTime_yyyyMMdd_HHmm(ctime*1000));
//				String tate = "�ȴ�����";
//				if(state==2)
//					tate = "������";
//				else if(state==3)
//					tate="��֧��";
//				else if(state==4)
//					tate="����ʧ��";
				map.put("state",state);
			}
			
			List<Map<String, Object>> listMaps = daService.getAllMap("select id,company_name from com_info_tb where id in("+params+")", comIdList);
			if(listMaps!=null){
				Map<Long, Object> idNameMap = new HashMap<Long, Object>();
				for(Map<String, Object> m : listMaps){
					Long comid = (Long)m.get("id");
					idNameMap.put(comid, m.get("company_name"));
				}
				for(int i=0;i<list.size();i++){
					Map map = (Map)list.get(i);
					Long _comid = (Long)map.get("comid");
					map.put("comid", idNameMap.get(_comid));
				}
			}
			
		}
	}
	private SqlInfo getSuperSqlInfo(HttpServletRequest request){
		String name = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "name"));
		String cname = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "cname"));
		SqlInfo sqlInfo1 = null;
		SqlInfo sqlInfo2 = null;
		if(!name.equals("")){
			sqlInfo1 = new SqlInfo(" a.name like ? ",new Object[]{"%"+name+"%"});
		}
		if(!cname.equals("")){
			sqlInfo2 = new SqlInfo(" c.company_name like ?  ",new Object[]{"%"+cname+"%"});
		}
		if(sqlInfo1!=null){
			if(sqlInfo2!=null)
				sqlInfo1 = SqlInfo.joinSqlInfo(sqlInfo1, sqlInfo2, 2);
			return sqlInfo1;
		}
		return sqlInfo2;
	}
	
	
	private List<Object> getpublicAccount(){
		/*
		 * �տ����ʺ�, card_number
		�տ�������, name
		�շ�����֧��,bank_pint
		�տ�������ʡ,area
		�տ���������,city
		���㷽ʽ����ͨ��,pay_type
		�����գ��������ڣ�,pay_date
		��;������ͣ���ѣ�,use
		���,
		�շ��к�bank_no
		�շ��������� bank_name
		 */
		String [] heards = new String[]{"��������","����ʱ��","��������","״̬","���ֽ��","�տ����ʺ�","�տ�������","�տ�������ʡ","�տ���������","���㷽ʽ","������","��;","�շ��к�","�շ���������"};
		String sql = "select t.*,c.atype,c.card_number,c.name,c.mobile,c.area,c.bank_name,c.bank_pint,c.user_id ," +
				"c.uin,c.city,c.pay_type,c.pay_date,c.use,c.bank_no  " +
				"from withdrawer_tb t left join com_account_tb c on t.acc_id=c.id where t.state=? and wtype=? ";
		String [] f = new String[]{"comid","create_time","update_time","state","amount","card_number","name","area","city","pay_type","pay_date","use","bank_no","bank_name"};
		
		List<Object> publicAcc = new ArrayList<Object>();
		publicAcc.add(heards);
		publicAcc.add(sql);
		publicAcc.add(f);
		return publicAcc;
		
	}
}