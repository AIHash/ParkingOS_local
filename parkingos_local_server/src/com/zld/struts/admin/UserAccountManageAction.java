package com.zld.struts.admin;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.zld.AjaxUtil;
import com.zld.impl.MongoClientFactory;
import com.zld.impl.PublicMethods;
import com.zld.service.DataBaseService;
import com.zld.utils.Check;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SendMessage;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;


/**
 * �û��ʻ�
 * @author Administrator
 *
 */
public class UserAccountManageAction extends Action{

	@Autowired
	private DataBaseService daService;
	@Autowired
	private PublicMethods publicMethods;
	private Logger logger = Logger.getLogger(UserAccountManageAction.class);
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		Long comid = RequestUtil.getLong(request, "comid", -1L);
		if(comid==null){
			response.sendRedirect("login.do");
			return null;
		}
		if(action.equals("")){
			request.setAttribute("comid", request.getParameter("comid"));
			return mapping.findForward("list");
		}else if(action.equals("query")){
			//http://192.168.199.240/zld/useraccount.do?action=query&from=client&uid=10343
			String sql = "select * from com_account_tb where comid=? and type=? ";
			Long uid =RequestUtil.getLong(request, "uid",-1L);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			String from = RequestUtil.getString(request, "from");
			
			//System.out.println(sqlInfo);
			List list = null;//daService.getAll(sql+" order by id desc",new Object[]{comid,1});
			int count =0;
			String json ="{}";
			if(from.equals("")){//��̨��ѯ
				list = daService.getAll(sql+" order by id desc",new Object[]{comid,1});
				if(list!=null)
					count = list.size();
				json = JsonUtil.Map2Json(list,1,count, fieldsstr,"id");
			}else if(uid!=-1){//�ͻ���
				list = daService.getAll(sql+" and uin=? order by id desc",new Object[]{comid,1,uid});
				if(list!=null&&!list.isEmpty()){
					Map map = (Map)list.get(0);
					Map rMap = new HashMap<String, Object>();
					rMap.put("bank_name", map.get("bank_name")==null?"":map.get("bank_name"));
					rMap.put("card_number", map.get("card_number")==null?"":map.get("card_number"));
					rMap.put("user_id", map.get("user_id")==null?"":map.get("user_id"));
					rMap.put("area", map.get("area"));
					rMap.put("bank_pint", map.get("bank_pint"));
					json = StringUtils.createJson(rMap);
				}
			}
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("create")){//����ʺ�
			//http://192.168.199.240//useraccount.do?action=create
			//&comid=&uid=&card_number=&user_id=&name=&bank_name=
			//��˾��� �û���¼�˻� ���п��� ���֤�� ���� �������� 
			String from = RequestUtil.getString(request, "from");
			
			String name =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "name"));
			Long uid =RequestUtil.getLong(request, "uid",-1L);
			String card_number =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "card_number"));
			//String mobile =RequestUtil.processParams(request, "mobile");
			String bank_name =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "bank_name"));
			String user_id =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "user_id"));
			String area =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "area"));
			String bank_pint =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "bank_pint"));
			//System.err.println(request.getParameterMap());
			String mobile = "";
			Map userMap = daService.getPojo("select mobile from user_info_tb where id=?", new Object[]{uid});
			if(userMap!=null&&userMap.get("mobile")!=null)
				mobile = (String)userMap.get("mobile");
			//String note =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "note"));
			//Integer type = RequestUtil.getInteger(request, "atype", 0);
			int result = 0;
			if(comid==-1&&uid!=-1){
				comid = daService.getLong("select comid from user_info_tb where id=? ", new Object[]{uid});
			}
			if(uid!=-1&&!card_number.equals("")&&!user_id.equals("")&&!bank_name.equals("")){
				Long count = daService.getLong("select count(id) from com_account_tb where uin=? and type=? ",new Object[]{uid,1});
				if(count>0){//����
					result = daService.update("update  com_account_tb set card_number=?,bank_name=?," +
							"user_id=?,name=?,area=?,bank_pint=?,mobile=? where uin=? and type=?",
							new Object[]{card_number,bank_name,user_id,name,area,bank_pint,mobile,uid,1});
				}else {
					result = daService.update("insert into com_account_tb " +
							"(comid,uin,name,card_number,bank_name,atype,type,state,user_id,area,bank_pint,mobile)" +
							" values(?,?,?,?,?,?,?,?,?,?,?,?)",
							new Object[]{comid,uid,name,card_number,bank_name,0,1,0,user_id,area,bank_pint,mobile});
				}
				if(result==1&&from.equals("kefuset")){//��Դ�ں�̨�ͷ��������շ�Ա�˻�����Ҫ�����û���������״̬
					Long pid = RequestUtil.getLong(request, "pid", -1L);//��¼���
					Long loguid = (Long)request.getSession().getAttribute("loginuin");
					int ret = daService.update("update collector_account_pic_tb set utime=?,state=?,auditor=? where id=? ", 
							new Object[]{System.currentTimeMillis()/1000,1,loguid,pid});
					logger.info("�����˻��ϴ���¼,pid="+pid+",result:"+ret);
					if(mobile!=null&&Check.checkMobile(mobile)){
						SendMessage.sendMultiMessage(mobile, "��ϲ��������п��˺��Ѿ�����ɣ����������ˡ��������ʣ���ϵͣ���� 010-56450585 ��ͣ������");
					}
					if(ret==1){
						ret = daService.update("update user_info_tb set nickname=? where id=? ", new Object[]{name,uid});
						logger.info("���¿ͻ���Ϣuid="+uid+",result:"+ret);
					}
				}
			}
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("editacc")){
			//http://192.168.199.240//useraccount.do?action=editacc&&uid=&card_number=&user_id=&name=&bank_name=
			
			String from = RequestUtil.getString(request, "from");
			if(from.equals("")){//���Ǻ�̨���ӿͻ��ˣ������Ĳ����޸ģ�������������˺ţ��ɿͷ���绰ȷ�ϲ�����
				AjaxUtil.ajaxOutput(response,"-1");
				return null;
			}
			String name =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "name"));
			Long uid =RequestUtil.getLong(request, "uid",-1L);
			String card_number =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "card_number"));
			String bank_name =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "bank_name"));
			String user_id =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "user_id"));
			String area =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "area"));
			String bank_pint =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "bank_pint"));
			String note =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "note"));
			String mobile = "";
			String uname = "";
			Map userMap = daService.getPojo("select mobile,nickname from user_info_tb where id=?", new Object[]{uid});
			if(userMap!=null&&userMap.get("mobile")!=null){
				uname = (String)userMap.get("nickname");
				mobile = (String)userMap.get("mobile");
			}
			int result = 0;
			if(!card_number.equals("")&&!user_id.equals("")&&!bank_name.equals("")){
				result = daService.update("update  com_account_tb set card_number=?,bank_name=?," +
						"user_id=?,name=?,area=?,bank_pint=?,mobile=?,note=? where uin=? and type=?",
						new Object[]{card_number,bank_name,user_id,uname,area,bank_pint,mobile,note,uid,1});
			}
			AjaxUtil.ajaxOutput(response, result+"");
		}
		else if(action.equals("editstate")){
			Integer state = RequestUtil.getInteger(request, "state", 0);
			Long id = RequestUtil.getLong(request, "id", -1L);
			state = state==0?1:0;
			int result = 0;
			if(id!=-1)
				result = daService.update("update com_account_tb set state =? where id=?", new Object[]{state,id});
//			if(result==1&&state==0){
//				
//			}
			AjaxUtil.ajaxOutput(response, ""+result);
		}else if(action.equals("getaccount")){
			Long uid =RequestUtil.getLong(request, "uid",-1L);
			Map userMap = daService.getMap("select balance from user_info_Tb where id =? ", new Object[]{uid});
			Object balance = userMap==null?"0.0":userMap.get("balance");
			if(balance==null)
				balance  ="0.0";
			String out = RequestUtil.getString(request, "out");
			if(out.equals("json"))
				AjaxUtil.ajaxOutput(response, "{\"balance\":\""+StringUtils.formatDouble(balance)+"\"}");
			else
				AjaxUtil.ajaxOutput(response, StringUtils.formatDouble(balance)+"");
			//http://192.168.199.240/zld/useraccount.do?action=getaccount&uid=11340
		}else if(action.equals("withdraw")){//�շ�Ա��������
			//http://192.168.199.240/zld/useraccount.do?action=withdraw&uid=10343&comid=858&money=20
			Double money = RequestUtil.getDouble(request, "money", 0d);
			Long uid =RequestUtil.getLong(request, "uid",-1L);
			Long count = daService.getLong("select count(*) from parkuser_account_tb where uin= ? and create_time>? and type=?  ", 
					new Object[]{uid,TimeTools.getToDayBeginTime(),1}) ;
			if(count>2){//ÿ��ֻ������
				AjaxUtil.ajaxOutput(response, "{\"result\":-2,\"times\":"+count+"}");
				return null;
			}
			Map accMap = daService.getMap("select id,atype from com_account_tb where uin =? and type=? ",
					new Object[]{uid,1});
			Long accId = null;
			Integer target = 0;
			if(accMap!=null&&!accMap.isEmpty()){//���ڸ����˻����п�
				accId=(Long)accMap.get("id");
				target = (Integer)accMap.get("atype");
			}else {
				//û�����������˻�
				AjaxUtil.ajaxOutput(response, "{\"result\":-1,\"times\":0}");
				return null;
			}
			//���ֲ���
			boolean result =false;
			if(money>0){
				Map userMap = daService.getMap("select balance,comid from user_info_Tb where id=? ", new Object[]{uid});
				//�û����
				Double balance = Double.valueOf(userMap.get("balance")+"");
				//String name = (String)userMap.get("nickname");
				if(money<=balance){//���ֽ��������
					//�۳��ʺ����//д���������
					List<Map<String, Object>> sqlList = new ArrayList<Map<String,Object>>();
					Map<String, Object> userSqlMap = new HashMap<String, Object>();
					userSqlMap.put("sql", "update user_info_Tb set balance = balance-? where id= ?");
					userSqlMap.put("values", new Object[]{money,uid});
					Map<String, Object> withdrawSqlMap = new HashMap<String, Object>();
					withdrawSqlMap.put("sql", "insert into withdrawer_tb  (comid,amount,create_time,acc_id,uin,wtype) values(?,?,?,?,?,?)");
					withdrawSqlMap.put("values", new Object[]{userMap.get("comid"),money,System.currentTimeMillis()/1000,accId,uid,1});
					Map<String, Object> moneySqlMap = new HashMap<String, Object>();
					moneySqlMap.put("sql", "insert into parkuser_account_tb (uin,amount,create_time,type,remark,target) values(?,?,?,?,?,?)");
					moneySqlMap.put("values", new Object[]{uid,money,System.currentTimeMillis()/1000,1,"����",target});
					sqlList.add(userSqlMap);
					sqlList.add(withdrawSqlMap);
					sqlList.add(moneySqlMap);
					result = daService.bathUpdate(sqlList);
				}
				if(result)
					AjaxUtil.ajaxOutput(response, "{\"result\":1,\"times\":"+count+"}");
				else {
					AjaxUtil.ajaxOutput(response,"{\"result\":0,\"times\":"+count+"}");
				}
			}
		}else if(action.equals("acountdeail")){
			//http://192.168.199.240/zld/useraccount.do?action=acountdeail&uid=11336&stype=
			String page = request.getParameter("page");
			Long uid =RequestUtil.getLong(request, "uid",-1L);
			if(page!=null){
				Integer pageNum = RequestUtil.getInteger(request, "page", 1);
				Integer pageSize = RequestUtil.getInteger(request, "size", 20);
				Long stype=RequestUtil.getLong(request, "stype", -1L);//0:���룬1����
				String sql = "select amount money,type mtype,create_time," +
						"remark note,target from parkuser_account_tb where uin = ?  ";
				String countSql = "select count(id) from parkuser_account_tb where uin = ?  ";
				List<Object> params = new ArrayList<Object>();
				params.add(uid);
				
				if(stype>-1){
					sql +=" and type=? ";
					countSql +=" and type=? ";
					params.add(stype);
				}
				
				Long count= daService.getCount(countSql, params);
				List list = null;//daService.getPage(sql, null, 1, 20);
				if(count>0){
					list = daService.getAll(sql+" order by id desc ", params, pageNum, pageSize);
				}
				//List<Map<String, Object>> accMap = daService.getAll(, new Object[]{uid});
				//target Ŀ��(��Դ��ȥ��)��0�����п���1��֧������2:΢�ţ�3��ͣ������4������
				setAccountList(list);
				String reslut =  "{\"count\":"+count+",\"info\":"+StringUtils.createJson(list)+"}";
				AjaxUtil.ajaxOutput(response, reslut);
			}else {
//				Long uid =RequestUtil.getLong(request, "uid",-1L);
				List<Map<String, Object>> accMap = daService.getAll("select amount money,type mtype,create_time,remark note,target from parkuser_account_tb where uin = ? order by id desc ", new Object[]{uid});
				//target Ŀ��(��Դ��ȥ��)��0�����п���1��֧������2:΢�ţ�3��ͣ������4������
				setAccountList(accMap);
				AjaxUtil.ajaxOutput(response, StringUtils.createJson(accMap));
			}
		}else if(action.equals("uploadpic")){//�����ϴ��˻�ͼƬ�����ڿͷ�����ͼƬ�����շ�Ա�˻���Ϣ
			Long uid = RequestUtil.getLong(request, "uin", -1L);
			//String picurl = uploadParkuserAccountPics2Mongodb(request,uid);
			String picurl = publicMethods.uploadPicToMongodb(request, uid, "parkuser_account_pics");
			Map userMap = daService.getMap("select comid from user_info_tb where id =?", new Object[]{uid});
			if(userMap==null){
				AjaxUtil.ajaxOutput(response, "0");
				return null;
			}else {
				comid = (Long)userMap.get("comid");
			}
			if(picurl.equals("-1")){
				AjaxUtil.ajaxOutput(response, picurl);
				return null;
			}
			Map<String,Object>	pucpMap = daService.getMap("select * from collector_account_pic_tb where uin = ? ", 
					new Object[]{uid});
			int ret = 0;
			if(pucpMap!=null){
				ret = daService.update("update collector_account_pic_tb set pic_name =?,ctime=?,state=?,utime=?,auditor=?  where uin=? ", 
						new Object[]{picurl,System.currentTimeMillis()/1000,0,0L,-1L,uid});
			}else {
				ret = daService.update("insert into collector_account_pic_tb(uin,pic_name,ctime,state,comid) values(?,?,?,?,?) ", 
						new Object[]{uid,picurl,System.currentTimeMillis()/1000,0,comid});
			}
			AjaxUtil.ajaxOutput(response, ret+"");
		}
		return null;
	}
	
	private void setAccountList (List<Map<String, Object>> list){
		if(list!=null&&!list.isEmpty()){
			for(Map<String, Object> map :list){
				Integer target = (Integer)map.get("target");
				if(target!=null){
					switch (target) {
					case 0:
						map.put("target", "���п�");
						break;
					case 1:
						map.put("target", "֧����");					
						break;
					case 2:
						map.put("target", "΢��");
						break;
					case 3:
						map.put("target", "ͣ����");
						break;
					case 4:
						String note = (String)map.get("note");
						String [] notes  = note.split("_");
						map.put("note",notes[0]);
						if(notes.length==2)
							map.put("target", notes[1]);
						else
							map.put("target","");
						break;
					default:
						break;
					}
				}
			}
		}
	}
	/*
	private String uploadParkuserAccountPics2Mongodb (HttpServletRequest request,Long uin) throws Exception{
		logger.info("begin upload regist picture....");
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
				if(item.getFieldName().equals("comid")){
					if(!item.getString().equals(""))
						comId = item.getString("UTF-8");
				}
				
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
			  
		    DBCollection collection = mydb.getCollection("parkuser_account_pics");
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
	}*/
}
