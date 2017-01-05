package com.zld.struts.parkadmin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.zld.AjaxUtil;
import com.zld.service.DataBaseService;
import com.zld.utils.Check;
import com.zld.utils.ExportExcelUtil;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;
import com.zld.utils.ZLDType;
/**
 * ͣ������̨��Ա�����¿��û�
 * @author Administrator
 *
 */
public class VipManageAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	
	private Logger logger = Logger.getLogger(VipManageAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		Long comid = (Long)request.getSession().getAttribute("comid");
		Integer role = RequestUtil.getInteger(request, "role",-1);
		request.setAttribute("role", role);
		if(comid==null){
			response.sendRedirect("login.do");
			return null;
		}
		String uid = request.getSession().getAttribute("userid")+"";
		if(comid==0)
			comid = RequestUtil.getLong(request, "comid", 0L);
		if(action.equals("")){
			request.setAttribute("comid", comid);
			return mapping.findForward("list");
		}else if(action.equals("query")){
			List arrayList = query(request,comid);
			List list = (List<Map<String, Object>>) arrayList.get(0);
			Integer pageNum = (Integer) arrayList.get(1);
			long count = Long.valueOf(arrayList.get(2)+"");
			String fieldsstr = arrayList.get(3)+"";
			setList(list);
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("exportExcel")){
			Map uin = (Map)request.getSession().getAttribute("userinfo");
			if(uin!=null&&uin.get("auth_flag")!=null){
				if(Integer.valueOf(uin.get("auth_flag")+"")==ZLDType.ZLD_ACCOUNTANT_ROLE||Integer.valueOf(uin.get("auth_flag")+"")==ZLDType.ZLD_CARDOPERATOR){
					String ret = "û��Ȩ�޵�����Ա����";
					logger.info(">>>>"+ret);
					AjaxUtil.ajaxOutput(response,ret);
					return null;
				}
			}
			List arrayList = query(request,comid);
			List<Map<String, Object>> list = (List<Map<String, Object>>) arrayList.get(0);
			List<List<String>> bodyList = new ArrayList<List<String>>();
			String [] heards = null;
			if(list!=null&&list.size()>0){
				//setComName(list);
				String [] f = new String[]{"id","p_name","mobile","uin","name","address","car_number","create_time","b_time","e_time","total","remark"};
				heards = new String[]{"���","���²�Ʒ����","�����ֻ�","�����˻�","����","��ַ","���ƺ���","����ʱ��","��ʼʱ��","����ʱ��","���","��ע"};
				for(Map<String, Object> map : list){
					List<String> values = new ArrayList<String>();
					for(String field : f){
						if("car_number".equals(field)){
							values.add(getcar(Long.valueOf(values.get(3)+"")));
						}else if("p_name".equals(field)){
							Map cpMap = daService.getMap("select p_name from product_package_tb where id =? ",new Object[]{Long.valueOf(map.get("p_name")+"")});
							values.add(cpMap.get("p_name")+"");
						}else{
							if("create_time".equals(field)||"b_time".equals(field)||"e_time".equals(field)){
								values.add(TimeTools.getTime_yyyyMMdd_HHmmss(Long.valueOf((map.get(field)+""))*1000));
							}else{
								values.add(map.get(field)+"");
							}
						}
					}
					bodyList.add(values);
				}
			}
			String fname = "��Ա����" + com.zld.utils.TimeTools.getDate_YY_MM_DD();
			fname = StringUtils.encodingFileName(fname);
			java.io.OutputStream os;
			try {
				response.reset();
				response.setHeader("Content-disposition", "attachment; filename="
						+ fname + ".xls");
				response.setContentType("application/x-download");
				os = response.getOutputStream();
				ExportExcelUtil importExcel = new ExportExcelUtil("��Ա����",
						heards, bodyList);
				importExcel.createExcelFile(os);
			} catch (IOException e) {
				e.printStackTrace();
			}
//			String json = "";
//			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("addcar")){
			String carNumber = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "carnumber"));
			String []cars = new String[]{carNumber};
			if(carNumber.indexOf(",")!=-1){
				cars = carNumber.split(",");//�������ƣ�Ҫ��Ȼ����һ���˻��¶������
			}
			if(cars.length>3){
				AjaxUtil.ajaxOutput(response, "ÿ���˻�������������");
				return null;
			}
			Long uin = RequestUtil.getLong(request, "uin", -1L);
			int ret = 0;
			if(carNumber!=null&&uin!=-1){
				//�޸�ԭ���������ǰ��µ�,δ����ģ���Ϊ��ͨ����
				//ɾ��ԭ����
//				try {
//					//�Ż���������ƺ�δ��Ļ������κβ���
//					List<String> list = daService.getAll("select car_number from car_info_tb where uin = ?",new Object[]{uin} );
//					HashSet<String> set = new HashSet<String>();
//					for (String str : list) {
//						set.add(str);
//					}
//					for (int i = 0; i < cars.length; i++) {
//						boolean b = set.add(cars[i]);
//						if(b==true){
//							int result = daService.update("update order_tb set c_type=? where uin=? and state=? and c_type=? ", new Object[]{3,uin,0,5});
//							logger.info("����Աuid��"+uid+"Ϊ��Աuin:"+uin+"��ӳ��ƺţ�"+carNumber+"���޸�ԭ���������ǰ��µ�,δ����ģ���Ϊ��ͨ������¼������"+result);
//							break;
//						}
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
				String sql = "insert into car_info_tb(uin,car_number) values(?,?)";
				Set<String> set = new HashSet<String>();
				List<Object[]> values = new ArrayList<Object[]>();
				for(String car :cars){	
					if(StringUtils.checkPlate(car)){
						set.add(car);
						Map<String, Object> map = daService.getMap("select uin from car_info_tb where car_number=? and uin<>?", new Object[]{car,uin});
						if(map!=null&&map.size()>0){
//							AjaxUtil.ajaxOutput(response, car+" ���ڴ��ڣ�");
//							return null;
							//����˻�û�е�¼��¼��û���������������¿���Ϣ������ɾ���û�
							for (Map.Entry<String, Object> entry : map.entrySet()) {
//								System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
								int dret = deleteUser(comid,Long.parseLong(entry.getValue()+""),car);
								if(dret==-1){
									AjaxUtil.ajaxOutput(response, car+" �ѹ����˱��������²�Ʒ");
									return null;
								}else if(dret==-2){
									AjaxUtil.ajaxOutput(response, car+" �ѱ����˰�");
									return null;
								}else if(dret==-3){
									AjaxUtil.ajaxOutput(response, car+" ����ɾ��ʧ��");
									return null;
								}
							}
						}
						Object []value = new Object[]{uin,car};
						values.add(value);
					}else{
						AjaxUtil.ajaxOutput(response,"���ƺŴ���");
						return null;
					}
				}
				if(cars.length!=set.size()){
					AjaxUtil.ajaxOutput(response,"���ƺ��ظ�");
					return null;
				}
				if(values.size()==0){
					AjaxUtil.ajaxOutput(response, "���ƺŴ���");
					return null;
				}
				daService.update("delete from car_info_tb where uin =? ", new Object[]{uin});
				if(!values.isEmpty()){
					try{
						ret=daService.bathInsert(sql, values, new int[]{4,12});
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			AjaxUtil.ajaxOutput(response, ret+"");
			return null;
		}else if(action.equals("create")){
			String result = buyProduct(request,comid);
			if(result.equals("1")){//����֪ͨ����
				//�����ֻ�
				String mobile =RequestUtil.processParams(request, "mobile").trim();
				//���ƺ���
				String car_number =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "car_number")).toUpperCase();
				Map<String, Object> map = daService.getMap("select company_name from com_info_tb where id=? ", new Object[]{comid});
				String company_name = (String)map.get("company_name");
				String msg = "���ش�ϲѶ���װ���"+company_name+"��Ա��Ϊ��������ͣ�����������ؽ�����ͣ���������ĳ��ƺ��롾"+car_number+"����¼��ϵͳ�������Զ�̧�˷��С��ֻ�����ͣ����APP����ʱ�ɸ������ƺţ���������Ч����˫������Ҳ���¡�����ͨ��APP�鿴�������飬�������ѣ����Ѽ�������˵���㡣��������Աר��ͣ��ȯ���ñ��ֻ��ŵ�¼ͣ����APP������ȡ��ȫ��225�ҳ���ͨ��Ŷ����¼www.tingchebao.com�������أ��˶���N��ͣ������";
				//SendMessage.sendMultiMessage(mobile, msg);
			}
			AjaxUtil.ajaxOutput(response, result);
		}else if(action.equals("edit")){
			
			Long id = RequestUtil.getLong(request, "id", -1L);
			//���ƺ���
			String car_number =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "car_number")).toUpperCase();
			if(id == -1){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			//Long count = daService.getLong("select count(*) from car_info_tb where car_number=? ", new Object[]{car_number});
			String result = editProduct(request, comid);
			//if(result.equals("1") && count == 0){
				//�����ֻ�
//				String mobile =RequestUtil.processParams(request, "mobile").trim();
//				Map<String, Object> map = daService.getMap("select company_name from com_info_tb where id=? ", new Object[]{comid});
//				String company_name = (String)map.get("company_name");
//				String msg = "���ش�ϲѶ���װ���"+company_name+"��Ա��Ϊ��������ͣ�����������ؽ�����ͣ���������ĳ��ƺ��롾"+car_number+"����¼��ϵͳ�������Զ�̧�˷��С��ֻ�����ͣ����APP����ʱ�ɸ������ƺţ���������Ч����˫������Ҳ���¡�����ͨ��APP�鿴�������飬�������ѣ����Ѽ�������˵���㡣��������Աר��ͣ��ȯ���ñ��ֻ��ŵ�¼ͣ����APP������ȡ��ȫ��225�ҳ���ͨ��Ŷ����¼www.tingchebao.com�������أ��˶���N��ͣ������";
//				SendMessage.sendMultiMessage(mobile, msg);
		//	}
			AjaxUtil.ajaxOutput(response, result);
		}else if(action.equals("delete")){
			String id =RequestUtil.processParams(request, "selids");
			Map cpMap = daService.getMap("select uin from carower_product where id =? ",new Object[]{Long.valueOf(id)});
			
			Long uin =-1L;
			if(cpMap!=null)
				uin=(Long)cpMap.get("uin");
			
			int result = daService.update("delete from carower_product where id =?", new Object[]{Long.valueOf(id)});
			if(result==1&&uin!=-1){
//				result = deleteUser(comid, uin);
			}
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("checkmobile")){
			String mobile = RequestUtil.processParams(request, "mobile").trim();
			if(!Check.checkMobile(mobile)){
				AjaxUtil.ajaxOutput(response, "1");
			}else{
				AjaxUtil.ajaxOutput(response, "0");
			}
		}else if(action.equals("getcar")){
			Long uin = RequestUtil.getLong(request, "uin", -1L);
			String cars = getcar(uin);
			AjaxUtil.ajaxOutput(response, cars);
		}
		return null;
	}

	//����˻�û�е�¼��¼��û�ð�΢�ţ�û���������������¿���Ϣ������ɾ���û�
	private int deleteUser(long comid,Long uin,String carnumber) {
		//���Ƿ����������������˰��²�Ʒ
		List<Map<String,Long>> list = daService.getAll("select p.comid from carower_product c,product_package_tb p where c.uin=? and c.e_time >? and p.id = c.pid ",
				new Object[]{uin,System.currentTimeMillis()/1000});
		if(list!=null&&list.size()>0){
			for(Map<String,Long> map:list){
				if(map.get("comid").longValue()==comid)
					return -1;
			}
			return -2;
		}
		Map userMap = daService.getMap("select balance,logon_time,wxp_openid from user_info_tb where id =?", new Object[]{uin});
		if(userMap!=null){
			Double balance = StringUtils.formatDouble(userMap.get("balance"));
			Long logoTime = (Long)userMap.get("logon_time");
			String wxp_openid = userMap.get("wxp_openid")+"";
			if(logoTime!=null||balance>0||(wxp_openid!=null&&!wxp_openid.equals("")&&!wxp_openid.equals("null"))){//������ɾ���û�
				return -2;
			}
			//��ʼɾ��
			List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
			//ɾ��������Ϣ
			Map<String, Object> carSqlMap = new HashMap<String, Object>();
			//ɾ���û���Ϣ
			Map<String, Object> userSqlMap = new HashMap<String, Object>();
			
			carSqlMap.put("sql", "delete from car_info_tb where uin = ? and car_number=?");
			carSqlMap.put("values", new Object[]{uin,carnumber});
			bathSql.add(carSqlMap);
			
//			userSqlMap.put("sql", "delete from user_info_tb where id = ?");
//			userSqlMap.put("values", new Object[]{uin});
//			bathSql.add(userSqlMap);
			boolean ret = daService.bathUpdate(bathSql);
			if(!ret)
				return -3;
		}
		
		return 1;
	}


	//ע����»�Ա 
	private String buyProduct(HttpServletRequest request, Long comid){
		//���²�Ʒ
		Long pid =RequestUtil.getLong(request, "p_name",-1L);
		//�����ֻ�
		String mobile =RequestUtil.processParams(request, "mobile").trim();
		String name = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "name").trim());
		String address = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "address").trim());
		//���ƺ���
		//String car_number =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "car_number")).toUpperCase();
		//��ʼʱ��
		String b_time =RequestUtil.processParams(request, "b_time");
		//��������
		Integer months = RequestUtil.getInteger(request, "months", 1);
		
		Integer flag = RequestUtil.getInteger(request, "flag", -1);
		//��ע
		String remark = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "remark"));
		
		//���
		Double total= 0d;//RequestUtil.getDouble(request, "total", 0d);
		
		Map pMap = daService.getMap("select limitday,price from product_package_tb where id=? ", new Object[]{pid});
		
		Double price = 0d;
		
		Long ntime = System.currentTimeMillis()/1000;
		Long btime = TimeTools.getLongMilliSecondFrom_HHMMDD(b_time)/1000;
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(btime*1000);
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+months);
		Long etime = calendar.getTimeInMillis()/1000;
		
		Long limitDay = null;//pMap.get("limitday");
		if(pMap!=null&&pMap.get("limitday")!=null){
			limitDay = (Long)pMap.get("limitday");
			price = Double.valueOf(pMap.get("price")+"");
		}
		total = months*price;
		if(limitDay!=null){
			if(limitDay<etime){//������Ч��
				return "-2";
			}
		}
		
		Map userMap = daService.getMap("select id from user_info_Tb where mobile=? and auth_flag=? ", new Object[]{mobile,4});
		
		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
		Map<String, Object> userSqlMap = new HashMap<String, Object>();
		Map<String, Object> recomSqlMap = new HashMap<String, Object>();
		Map<String, Object> carInfoMap = new HashMap<String, Object>();
	    Map<String, Object> carowerPackMap = new HashMap<String, Object>();
	    Long uin =-1L;
		if(userMap==null){//����δע��
			uin = daService.getkey("seq_user_info_tb");
			userSqlMap.put("sql", "insert into user_info_tb (id,strid,nickname,mobile,auth_flag,reg_time,media) values(?,?,?,?,?,?,?)");
			userSqlMap.put("values", new Object[]{uin,"carower_"+uin,"����",mobile,4,ntime,10});
			bathSql.add(userSqlMap);
			
			//д���¼���û�ͨ��ע���¿���Աע�ᳵ��
			recomSqlMap.put("sql", "insert into recommend_tb(nid,type,state,create_time,comid) values(?,?,?,?,?)");
			recomSqlMap.put("values", new Object[]{uin,0,0,System.currentTimeMillis(),comid});
			bathSql.add(recomSqlMap);
		}else {
			uin = (Long)userMap.get("id");
		}
		if(uin==null||uin==-1)
			return "-1";
		
		Long isBuy = daService.getLong("select count(id) from carower_product where uin=? and pid=? and e_time> ?", new Object[]{uin,pid,ntime});
		
		if(isBuy!=null&&isBuy>0)
			return "�����ѹ�����˲�Ʒ";
		
		/*Map carMap = daService.getMap("select car_number from car_info_tb where uin = ? ",new Object[]{uin});
		if(carMap==null){
			carInfoMap.put("sql", "insert into car_info_tb (uin,car_number) values(?,?)");
			carInfoMap.put("values", new Object[]{uin,car_number});
			bathSql.add(carInfoMap);
		}else {
			String ncarNumber = (String)carMap.get("car_number");
			if(!car_number.equals(ncarNumber)){
				if(flag==1){
					//��ɾ���������еĳ���
					daService.update("delete * from car_info_tb where uin =? ", new Object[]{uin});
					carInfoMap.put("sql", "update car_info_tb set car_number=? where uin=? ");
					carInfoMap.put("values", new Object[]{car_number,uin});
					bathSql.add(carInfoMap);
				}else {
					return "����ԭ���ƺţ�"+ncarNumber+",�ֳ��ƺţ�"+car_number+",ȷ���޸�Ϊ�ֳ��ƣ���ѡ�񡾱����ֳ��ơ�ѡ�";
				}
			}
			
		}*/
		carowerPackMap.put("sql", "insert into carower_product (uin,pid,create_time,b_time,e_time,total,remark,name,address) values(?,?,?,?,?,?,?,?,?)");
		carowerPackMap.put("values", new Object[]{uin,pid,ntime,btime,etime,total,remark,name,address});
		bathSql.add(carowerPackMap);
		if(daService.bathUpdate(bathSql))
			return "1";
		else {
			return "-1";
		}
	}

	//�༭���»�Ա 
	@SuppressWarnings({ "rawtypes" })
	private String editProduct(HttpServletRequest request, Long comid){
		Long id = RequestUtil.getLong(request, "id", -1L);
		//���²�Ʒ
		Long pid =RequestUtil.getLong(request, "p_name",-1L);
		//�����ֻ�
		String mobile =RequestUtil.processParams(request, "mobile").trim();
		String name = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "name").trim());
		String address = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "address").trim());
		//���ƺ���
	//	String car_number =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "car_number")).toUpperCase();
		//��ʼʱ��
		String b_time =RequestUtil.processParams(request, "b_time");
		//��������
		Integer months = RequestUtil.getInteger(request, "months", 1);
				
		Integer flag = RequestUtil.getInteger(request, "flag", -1);
		//��ע
		String remark = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "remark"));
				
		//���
		Double total= 0d;//RequestUtil.getDouble(request, "total", 0d);
				
		Map pMap = daService.getMap("select limitday,price from product_package_tb where id=? ", new Object[]{pid});
				
		Double price = 0d;
				
		Long ntime = System.currentTimeMillis()/1000;
		Long btime = TimeTools.getLongMilliSecondFrom_HHMMDD(b_time)/1000;
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(btime*1000);
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+months);
		Long etime = calendar.getTimeInMillis()/1000;
				
		Long limitDay = null;//pMap.get("limitday");
		if(pMap!=null&&pMap.get("limitday")!=null){
			limitDay = (Long)pMap.get("limitday");
			price = Double.valueOf(pMap.get("price")+"");
		}
		total = months*price;
		if(limitDay!=null){
			if(limitDay<etime){//������Ч��
				return "-2";
			}
		}
				
		Map userMap = daService.getMap("select id from user_info_Tb where mobile=? and auth_flag=? ", new Object[]{mobile,4});
				
		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
		Map<String, Object> userSqlMap = new HashMap<String, Object>();
		Map<String, Object> recomSqlMap = new HashMap<String, Object>();
		//Map<String, Object> carInfoMap = new HashMap<String, Object>();
		Map<String, Object> carowerPackMap = new HashMap<String, Object>();
		Long uin =-1L;
		if(userMap==null){//����δע��
			uin = daService.getkey("seq_user_info_tb");
			userSqlMap.put("sql", "insert into user_info_tb (id,strid,nickname,mobile,auth_flag,reg_time,media) values(?,?,?,?,?,?,?)");
			userSqlMap.put("values", new Object[]{uin,"carower_"+uin,"����",mobile,4,ntime,10});
			bathSql.add(userSqlMap);
					
			//д���¼���û�ͨ��ע���¿���Աע�ᳵ��
			recomSqlMap.put("sql", "insert into recommend_tb(nid,type,state,create_time,comid) values(?,?,?,?,?)");
			recomSqlMap.put("values", new Object[]{uin,0,0,System.currentTimeMillis(),comid});
			bathSql.add(recomSqlMap);
		}else {
			uin = (Long)userMap.get("id");
		}
		if(uin==null||uin==-1)
			return "-1";
				
		Long isBuy = daService.getLong("select count(id) from carower_product where uin=? and pid=? and e_time> ? and id!=? ", new Object[]{uin,pid,ntime,id});
				
		if(isBuy!=null&&isBuy>0)
			return "�����ѹ�����˲�Ʒ";
				
		/*Map carMap = daService.getMap("select car_number from car_info_tb where uin = ? ",new Object[]{uin});
		if(carMap==null){
			carInfoMap.put("sql", "insert into car_info_tb (uin,car_number) values(?,?)");
			carInfoMap.put("values", new Object[]{uin,car_number});
			bathSql.add(carInfoMap);
		}else {
			String ncarNumber = (String)carMap.get("car_number");
			if(!car_number.equals(ncarNumber)){
				if(flag==1){
					carInfoMap.put("sql", "update car_info_tb set car_number=? where uin=? ");
					carInfoMap.put("values", new Object[]{car_number,uin});
					bathSql.add(carInfoMap);
				}else {
					return "����ԭ���ƺţ�"+ncarNumber+",�ֳ��ƺţ�"+car_number+",ȷ���޸�Ϊ�ֳ��ƣ���ѡ�񡾱����ֳ��ơ�ѡ�";
				}
			}
					
		}*/
		carowerPackMap.put("sql", "update carower_product set uin=?,pid=?,create_time=?,b_time=?,e_time=?,total=?,remark=?,name=?,address=? where id=? ");
		carowerPackMap.put("values", new Object[]{uin,pid,ntime,btime,etime,total,remark,name,address,id});
		bathSql.add(carowerPackMap);
		if(daService.bathUpdate(bathSql))
			return "1";
		else {
			return "-1";
		}
	}

	private SqlInfo getSuperSqlInfo(HttpServletRequest request){
		String mobile = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "mobile"));
		String p_name = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "p_name"));
		String car_nubmer = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "car_number"));
		SqlInfo sqlInfo1 = null;
		SqlInfo sqlInfo2 = null;
		SqlInfo sqlInfo3 = null;
		if(!mobile.equals("")){
			sqlInfo1 = new SqlInfo(" u.mobile like ? ",new Object[]{"%"+mobile+"%"});
		}
		if(!p_name.equals("")){
			sqlInfo3 = new SqlInfo(" p.p_name like ?  ",new Object[]{"%"+p_name+"%"});
		}
		if(!car_nubmer.equals("")){
			sqlInfo2 = new SqlInfo(" c.uin in (select uin from car_info_tb where car_number like ?)  ",new Object[]{"%"+car_nubmer+"%"});
		}
		if(sqlInfo1!=null){
			if(sqlInfo2!=null)
				sqlInfo1 = SqlInfo.joinSqlInfo(sqlInfo1, sqlInfo2, 2);
			if(sqlInfo3!=null)
				sqlInfo1 = SqlInfo.joinSqlInfo(sqlInfo1, sqlInfo3, 2);
			return sqlInfo1;
		}else if(sqlInfo2!=null){
			if(sqlInfo3!=null)
				sqlInfo2 = SqlInfo.joinSqlInfo(sqlInfo2, sqlInfo3, 2);
			return sqlInfo2;
		}
		return sqlInfo3;
	}
	
	private void setList(List<Map<String,Object>> list){
		if(list != null && !list.isEmpty()){
			for(Map<String, Object> map : list){
				Long b_time = (Long)map.get("b_time");
				Long e_time = (Long)map.get("e_time");
				Integer months = Math.round((e_time - b_time)/(30*24*60*60));
				map.put("months", months);
			}
		}
	}
	private List query(HttpServletRequest request,long comid){
		ArrayList arrayList = new ArrayList();
		String sql = "select p.id p_name,c.uin,c.id,c.name,c.address,c.create_time,c.b_time ,c.e_time ,c.remark,c.total,u.mobile from " +
		"product_package_tb p,carower_product c ,user_info_tb u " +
		"where c.pid=p.id and p.comid=?  and u.id=c.uin ";
		String countSql = "select count(c.id) from product_package_tb p,carower_product c ,user_info_tb u " +
				"where c.pid=p.id and p.comid=? and  u.id=c.uin " ;
		Integer pageNum = RequestUtil.getInteger(request, "page", 1);
		Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
		String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
		SqlInfo sqlInfo = RequestUtil.customSearch(request,"c_product","c",new String[]{"mobile","car_number","p_name"});
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
		params.add(0,comid);
		
		String p_name = RequestUtil.processParams(request, "p_name_start");
		String relation = "";
		if(StringUtils.isNotNull(p_name)){
			relation = " and p.id = ?";
			params.add(params.size(),Long.valueOf(p_name));
		}
		//System.out.println(sqlInfo);
		Long count=daService.getCount(countSql+relation, params);
		List list = null;//daService.getPage(sql, null, 1, 20);
		if(count>0){
			String orderby="id";
			String sort="desc";
			String orderfield = RequestUtil.processParams(request, "orderfield");
			String reqorderby = RequestUtil.processParams(request, "orderby");
			if(StringUtils.isNotNull(orderfield))
				orderby=orderfield;
			if(StringUtils.isNotNull(orderfield))
				sort=reqorderby;
			list = daService.getAll(sql+ relation+" order by "+orderby+" "+sort, params, pageNum, pageSize);
		}
		arrayList.add(list);
		arrayList.add(pageNum);
		arrayList.add(count);
		arrayList.add(fieldsstr);
		return arrayList;
	}
	private String getcar(Long uin){
		String cars = "��";
		if(uin!=-1){
			List<Map<String, Object>> carList = daService.getAll("select car_number from car_info_tb where uin =? ", new Object[]{uin});
			if(carList!=null&&!carList.isEmpty()){
				cars = "";
				for(Map<String, Object> map :carList){
					cars += map.get("car_number")+",";
				}
				if(cars.endsWith(","))
					cars =cars.substring(0,cars.length()-1); 
			}
		}
		return cars;
	}
}