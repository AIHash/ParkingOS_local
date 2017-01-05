package com.zld.struts.request;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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

import pay.Constants;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.zld.AjaxUtil;
import com.zld.CustomDefind;
import com.zld.impl.CommonMethods;
import com.zld.impl.MongoClientFactory;
import com.zld.impl.PublicMethods;
import com.zld.service.DataBaseService;
import com.zld.service.LogService;
import com.zld.service.PgOnlyReadService;
import com.zld.utils.HttpProxy;
import com.zld.utils.OrderSortCompare;
import com.zld.utils.ParkingMap;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;
/**
 * ͣ�����շ�Ա����������λ�����۴����
 * @author Administrator
 *
 */
public class CollectorRequestAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	@Autowired
	private LogService logService;
	@Autowired
	private PublicMethods publicMethods;
	@Autowired
	private PgOnlyReadService pService;
	@Autowired 
	private CommonMethods commonMethods;
	
	private Logger logger = Logger.getLogger(CollectorRequestAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String token =RequestUtil.processParams(request, "token");
		String action =RequestUtil.processParams(request, "action");
		String out= RequestUtil.processParams(request, "out");
		Map<String,Object> infoMap = new HashMap<String, Object>();
		Long comId =null;
		Long uin = null;
		response.setContentType("application/json");
		Long authFlag = 0L;
		if(token.equals("")){
			infoMap.put("info", "no token");
		}else {
			Map comMap = daService.getPojo("select * from user_session_tb where token=?", new Object[]{token});
			if(comMap!=null&&comMap.get("comid")!=null){
				comId=(Long)comMap.get("comid");
				uin =(Long) comMap.get("uin");
				authFlag = daService.getLong("select auth_flag from user_info_tb where id =? ", new Object[]{uin});
			}else {
				infoMap.put("info", "token is invalid");
			}
		}
		logger.info("token="+token+",comid="+comId+",action="+action+",uin="+uin);
		/*tokenΪ�ջ�token��Чʱ�����ش���		 */
		if(token.equals("")||comId==null||uin==null){
			if(out.equals("json"))
				AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
			else
				AjaxUtil.ajaxOutput(response, StringUtils.createXML(infoMap));
			return null;
			
		}
		if(action.equals("myinfo")){
			AjaxUtil.ajaxOutput(response, myInfo(uin));
			return null;
			//test:http://127.0.0.1/zld/collectorrequest.do?action=myinfo&token=0dc591f7ddda2d6fb73cd8c2b4e4a372
		}else if(action.equals("comparks")){
			//http://127.0.0.1/zld/collectorrequest.do?action=comparks&out=josn&token=5f0c0edb1cc891ac9c3fa248a28c14d5
			String result =getComParks(comId);
			result = result.replace("null", "");
			AjaxUtil.ajaxOutput(response, result);
		}else if(action.equals("autoup")){//�Զ�̧��
			String ret = autoUp(request,comId,uin);
			AjaxUtil.ajaxOutput(response, ret);
			//http://127.0.0.1/zld/collectorrequest.do?action=autoup&price=&carnumber=&token=0dc591f7ddda2d6fb73cd8c2b4e4a372
			return null;
		}else if(action.equals("uplogfile")){
			AjaxUtil.ajaxOutput(response,uploadPadLogFile(request,comId));
			return null;
		}else if(action.equals("uplogs")){//�ϴ�ƽ����־
			//http://127.0.0.1/zld/collectorrequest.do?action=uplogs&token=&logs=111222frfewae
			String logs = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "logs"));
			logger.error("uplogs:"+logs);
			Writer writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(new File("c:\\padlogs.txt"),true));
				writer.write(logs+"\n");
				writer.flush();
				writer.close();
				logger.error("uplogs:д��ɹ�");
				AjaxUtil.ajaxOutput(response, "1");
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(writer!=null)
					writer.close();
			}
			AjaxUtil.ajaxOutput(response, "1");
			return null;
		}
		else if(action.equals("toshare")){//����λ
			Integer number = RequestUtil.getInteger(request, "s_number", -1);
			boolean isCanLalaRecord = ParkingMap.isCanRecordLaLa(uin);
			if(number!=-1){
				doShare(comId, uin,number,infoMap,isCanLalaRecord);
			}else {
				infoMap.put("info", "fail");
				infoMap.put("message", "�����������Ϸ�!");
			}
			//test:http://127.0.0.1/zld/collectorrequest.do?action=toshare&token=d450ea04d67bf0b428ea1204675d5b53&s_number=800

		}else if(action.equals("tosale")){//���۴���
			Long orderId = RequestUtil.getLong(request, "orderid", -1L);
			Integer houer = RequestUtil.getInteger(request, "hour", 0);
			if(orderId!=-1&&houer>0){
				doSale(comId, uin,houer, orderId,infoMap);
			}else {
				infoMap.put("info", "����û�ж�����Ż��Ż�Сʱ!");
			}
			//test:http://127.0.0.1/zld/collectorrequest.do?action=tosale&token=d450ea04d67bf0b428ea1204675d5b53&orderid=1&hour=1
		}else if(action.equals("orderdetail")){//��������
			orderDetail(request,comId,infoMap);
			//test:http://127.0.0.1/zld/collectorrequest.do?action=orderdetail&token=d450ea04d67bf0b428ea1204675d5b53&orderid=151
		}else if(action.equals("currorders")){//��ǰ����
			String result = currOrders(request,uin,comId,out,infoMap);
			System.out.println(result);
			AjaxUtil.ajaxOutput(response,result);
			return null;
			//test:http://127.0.0.1/zld/collectorrequest.do?action=currorders&token=4bad81d8d7993446265a155318182dee&page=1&size=10&out=json
		}else if(action.equals("orderhistory")){//��ʷ����
			String result = orderHistory(request,comId,out);
			AjaxUtil.ajaxOutput(response, result);
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=orderhistory&day=last&uid=10828&ptype=0&token=ec5c8185dae6f48c03c43785fe17be22&uin=10824&page=1&size=10&out=json
		}else if(action.equals("ordercash")){//�ֽ��շ�
			Long orderId = RequestUtil.getLong(request, "orderid", -1L);
			Double total = RequestUtil.getDouble(request, "total", 0d);
			String imei  =  RequestUtil.getString(request, "imei");
			logger.info("ordercash>>>>>:orderid:"+orderId);
			if(orderId!=-1){
				Map orderMap = daService.getPojo("select * from order_tb where id=?", new Object[]{orderId});
				if(orderMap.get("total")!=null) {
					Double prepay = StringUtils.formatDouble(orderMap.get("total"));
					boolean result = prepayRefund(orderMap,prepay);
					//Ԥ֧������˿�
					logger.info("�ֽ����Ԥ�����˿�:"+result+",orderid:"+orderId);
				}
				
				List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
				//���¶���״̬���շѳɹ�
				Map<String, Object> orderSqlMap = new HashMap<String, Object>();
				orderSqlMap.put("sql", "update order_tb set state=?,total=?,end_time=?,pay_type=?,imei=?,uid=? where id=?");
				orderSqlMap.put("values", new Object[]{1,total,System.currentTimeMillis()/1000,1,imei,uin,orderId});
				bathSql.add(orderSqlMap);
				
				//�ֽ���ϸ
				Map<String, Object> cashsqlMap =new HashMap<String, Object>();
				cashsqlMap.put("sql", "insert into parkuser_cash_tb(uin,amount,type,orderid,create_time) values(?,?,?,?,?)");
				cashsqlMap.put("values",  new Object[]{uin,total,0,orderId,System.currentTimeMillis()/1000});
				bathSql.add(cashsqlMap);
				
				boolean b = daService.bathUpdate(bathSql);
				logger.info("ordercash>>>>ordreid:"+orderId+",b:"+b);
				if(b){
					infoMap.put("info", "�ֽ��շѳɹ�!");
					//���¶�����Ϣ�е�״̬ 
					daService.update("update order_message_tb set state=? where orderid=?", 
							new Object[]{2,orderId});
					if(out.equals("json")){
						AjaxUtil.ajaxOutput(response, "1");
						return null;
					}
				}else {
					infoMap.put("info", "�ֽ��շ�ʧ��!");
					if(out.equals("json")){
						AjaxUtil.ajaxOutput(response, "-1");
						return null;
					}
				}
			}
			//test:http://127.0.0.1/zld/collectorrequest.do?action=ordercash&token=5286f078c6d2ecde9b30929f77771149&orderid=787824
		}else if(action.equals("freeorder")){//HD�棬��ѷ���
			Long orderId = RequestUtil.getLong(request, "orderid", -1L);
			Long out_passid = RequestUtil.getLong(request, "passid", -1L);//����ͨ��id
			Long isPolice = RequestUtil.getLong(request, "isPolice", -1L);//�Ƿ������
			Long freereasons = RequestUtil.getLong(request, "freereasons", -1L);//���ԭ��
			if(orderId != -1){
				logger.info("�շ�Ա��"+uin+"�Ѷ�����"+orderId+"��Ϊ��ѷ��У�ispolice:"+isPolice);
				int result =0;
				Map map = daService.getPojo("select * from order_tb where id=? ", new Object[]{orderId});
				logger.info(map);
				if(isPolice==1){
					result = daService.update("update order_tb set total=?,state=?,end_time=?,out_passid=?,uid=?,freereasons=? where id=? ", new Object[]{0,1,System.currentTimeMillis()/1000,out_passid,uin,freereasons,orderId});
				}else{
					//����Ӧ�ռ۸�
					Integer pid = (Integer)map.get("pid");
					Integer car_type = (Integer)map.get("car_type");//0��ͨ�ã�1��С����2����
					Long start= (Long)map.get("create_time");
					Long end =  System.currentTimeMillis()/1000;
					Double total  = 0d;
					if(map.get("end_time") != null){
						end = (Long)map.get("end_time");
					}
					Map ordermap = commonMethods.getOrderInfo(orderId, -1L, end);
					total = Double.valueOf(ordermap.get("aftertotal") + "");
					logger.info(total);
					result = daService.update("update order_tb set total=?,state=?,end_time=?,pay_type=?,out_passid=?,uid=?,freereasons=? where id=? and state=? ", new Object[]{total,1,System.currentTimeMillis()/1000,8,out_passid,uin,freereasons,orderId,1});
				}
				logger.info("&&&&&&"+result);
				int r = daService.update("update parkuser_cash_tb set amount=? where orderid=? and type=? ",
						new Object[] { 0, orderId, 0 });
				logger.info("freeorder>>>>�ֽ��շ���ϸ��Ϊ0��orderid:"+orderId+",r:"+r);
				if(result == 1){
					AjaxUtil.ajaxOutput(response, "1");
				}else{
					AjaxUtil.ajaxOutput(response, "0");
				}
				logger.info("��ѷ����ˡ�������");
				if(result==1){
					logger.info("���������ϴ���������");
					if(map.get("line_id")!=null)
//						new Thread(){
//							@Override
//							public void run() {
//								publicMethods.uploadOrder2Line(orderId,2,2);
//							};
//						}.start();
							
						daService.update("update order_tb set sync_state=? where id = ? and sync_state<3", new Object[]{4,orderId});//��ѵ�sync_state���ó�4   �������ϴ�����Ѱ���ѵı�Ǹ��ǵ���
//						publicMethods.uploadOrder2Line(orderId,2,2);
				}
			}
			//http://192.168.199.239/zld/collectorrequest.do?action=freeorder&token=7d4860ef99bd70d5c91af535bb2c5065&orderid=1
		}else if(action.equals("cominfo")){//��˾��Ϣ
			Map comMap = daService.getPojo("select * from com_info_tb where id=?", new Object[]{comId});
			List<Map<String, Object>> picMap = daService.getAll("select picurl from com_picturs_tb where comid=? order by id desc limit ? ",
					new Object[]{comId,1});
			String picUrls = "";
			if(picMap!=null&&!picMap.isEmpty()){
				for(Map<String, Object> map : picMap){
					picUrls +=map.get("picurl")+";";
				}
				if(picUrls.endsWith(";"))
					picUrls = picUrls.substring(0,picUrls.length()-1);
			}
			if(comMap!=null&&comMap.get("id")!=null){
				String mobile = (String)comMap.get("mobile");
				String phone = (String)comMap.get("phone");
				Integer city = (Integer)comMap.get("city");
				if(phone==null||phone.equals(""))
					phone = mobile;
				Map priceMap = getPriceMap(comId);
				String timeBetween = "";
				Double price = 0d;
				if(priceMap!=null){
					Integer payType = (Integer)priceMap.get("pay_type");
					if(payType==0){
						Integer start = (Integer)priceMap.get("b_time");
						Integer end = (Integer)priceMap.get("e_time");
						if(start<10&&end<10)
							timeBetween = "0"+start+":00-0"+end+":00";
						else if(start<10&&end>9){
							timeBetween = "0"+start+":00-"+end+":00";
						}else if(start>9){
							timeBetween = start+":00-"+end+":00";
						}
					}else {
						timeBetween = "00:00-24:00";
					}
					if(priceMap.get("price")!=null)
						price = Double.valueOf(priceMap.get("price")+"");
				}
				Integer parkType = (Integer)comMap.get("parking_type");
				parkType = parkType==null?0:parkType;
				String ptype = "����";
				if(parkType==1)
					ptype="����";
				else if(parkType==2){
					ptype="ռ��";
				}
				Integer stopType = (Integer)comMap.get("stop_type");
				String sType = "ƽ������";
				if(stopType==1)
					sType="��������";
				infoMap.put("name", comMap.get("company_name"));
				infoMap.put("address", comMap.get("address"));
				infoMap.put("parkingtotal", comMap.get("parking_total"));
				infoMap.put("parktype",ptype);
				infoMap.put("phone", phone);
				infoMap.put("timebet", timeBetween);
				infoMap.put("price", price);
				infoMap.put("stoptype", sType);
				infoMap.put("service", "�˹�����");
				infoMap.put("id", comId);
				infoMap.put("resume", comMap.get("resume")==null?"":comMap.get("resume"));
				infoMap.put("longitude", comMap.get("longitude"));
				infoMap.put("latitude", comMap.get("latitude"));
				infoMap.put("isfixed", comMap.get("isfixed"));
				infoMap.put("picurls",picUrls);
				List<Map<String, Object>> carTypeList = commonMethods.getCarType(comId);
				String carTypes = StringUtils.createJson(carTypeList);
				carTypes = carTypes.replace("value_no", "id").replace("value_name", "name");
				infoMap.put("car_type", comMap.get("car_type"));
				infoMap.put("allCarTypes", carTypes);
				infoMap.put("passfree", comMap.get("passfree"));
				infoMap.put("ishdmoney", comMap.get("ishdmoney"));
				infoMap.put("ishidehdbutton", comMap.get("ishidehdbutton"));
				infoMap.put("currentTimeMillis", System.currentTimeMillis());
//				infoMap.put("issuplocal", comMap.get("issup_local"));
				infoMap.put("issuplocal", 0);
				infoMap.put("fullset",comMap.get("full_set"));//��λ�����ܷ����
				infoMap.put("leaveset",comMap.get("leave_set"));//����ʶ��ʶ��̧������  ���е��¿�����û���շѣ����շѣ���
				List list = daService.getAll("select id as value_no,name as value_name from free_reasons_tb where comid=? order by sort , id desc ", new Object[]{comId});
				infoMap.put("freereasons",list);
				infoMap.put("liftreason",getLiftReason(comId));
				String swith="1publicMethods.getCollectMesgSwith()";
				if("1".equals(swith)){
					if(city!=null&&city==110000)//0605��֪ͨ�������շ�Ա
						infoMap.put("mesgurl", "collectmesg.png");
					else {//֪ͨ����������շ�Ա
						infoMap.put("mesgurl", "collectmesg_jn.png");
					}
				}
			}else {
				infoMap.put("info", "token is invalid");
			}
			//System.err.println(infoMap);
			//test:http://127.0.0.1/zld/collectorrequest.do?action=cominfo&token=85cd74cfe5b40b57ae04f9d2b8e24e15&out=json
		}else if(action.equals("corder")){//һ����ѯ
			/*
			 * ������Ŀǰͣ�˶��ٳ�������ǰ����������
				�������볡��������������ʷ����������
				�����Ѿ��յ�����������ʷ�����ܽ�
			 */
			Long btime = TimeTools.getToDayBeginTime();
			Long etime = System.currentTimeMillis()/1000;
			Long ccount =0L;//��ǰ������
			int ocount =0;//�ѽ��㶩����
			Long tcount = 0L;//���յ�ǰ������
			Double total =0d;
			List<Map<String, Object>> orderList = daService.getAll("select  total,state from order_tb where comid=? and end_time between ? and ? and state=? ",
					new Object[]{comId,btime,etime,1});
			if(orderList!=null){
				ocount=orderList.size();
				for(Map<String,Object> map: orderList){
					total += Double.valueOf(map.get("total")+"");
				}
			}
			ccount = daService.getLong("select count(*) from order_tb where comid=? and state=? ",
					new Object[]{comId,0});
			tcount = daService.getLong("select count(1) from order_tb where comid=? and state=? " +
					"and create_time between ? and ?", new Object[]{comId,0,btime,etime});
			AjaxUtil.ajaxOutput(response, "{\"ccount\":\""+ccount+"\",\"ocount\":\""+ocount+"\",\"tcount\":\""+tcount+"\",\"total\":\""+StringUtils.formatDouble(total)+"\"}");		
			//test:http://127.0.0.1/zld/collectorrequest.do?action=corder&token=b4e6727f914157c8745f6f2c023c8c96
			return null;
		}
		else if(action.equals("score")){//�������
			//test:http://127.0.0.1/zld/collectorrequest.do?action=score&token=2f73bdccceecbbf436b61aca464af21b&week=toweek&detail=   toweek&detail=total
			//detail:  toweek:���ܻ��� ,total:�ܻ��� ,�գ���ѯ����
			//week:�ܣ�last:�������У�toweek���������� ,�գ�����
			
			AjaxUtil.ajaxOutput(response, "[]");
			return null;
			
			/*String type = RequestUtil.processParams(request, "week");
			String detail = RequestUtil.processParams(request, "detail");
			Long btime =TimeTools.getLongMilliSecondFrom_HHMMDD(StringUtils.getMondayOfThisWeek())/1000;
			Long etime=System.currentTimeMillis()/1000;
			if(detail.equals("")){
				if(type.equals("last")){
					etime = btime;
					btime = btime-7*24*60*60;
				}
				List scroeList = daService.getAll("select sum(lala_scroe+nfc_score+praise_scroe+pai_score+online_scroe+recom_scroe) score,uin from collector_scroe_tb where create_time between ? and ? group by uin order by score desc",
						new Object[]{btime,etime-1});
				List<Map<String, Object>> resultList = setScroeList(scroeList);
				setSort(resultList);
				AjaxUtil.ajaxOutput(response, StringUtils.createJson(resultList));
				return null;
			}else if(detail.equals("toweek")){//���ܻ�������
				infoMap = daService.getMap("select sum(lala_scroe) lala_scroe,sum(nfc_score) nfc_score,sum(praise_scroe) praise_scroe," +
						"sum(pai_score) pai_score ,sum(online_scroe) online_scroe,uin,sum(recom_scroe) recom_scroe from collector_scroe_tb where uin=? and create_time between ? and ? group by uin",
						new Object[]{uin,btime,etime});
				if(infoMap!=null){
					Long ls = (Long)infoMap.get("lala_scroe");
					Long ss = (Long)infoMap.get("praise_scroe");
					Double ps = StringUtils.formatDouble(infoMap.get("pai_score"));
					Long rs = (Long)infoMap.get("recom_scroe");
					Double os = 0d;
					Double ns = 0d;//
					if(infoMap.get("online_scroe")!=null)
						os = Double.valueOf(infoMap.get("online_scroe")+"");
					if(infoMap.get("nfc_score")!=null)
						ns = Double.valueOf(infoMap.get("nfc_score")+"");
					if(ls==null)
						ls =0L;
					if(ss==null)
						ss=0L;
					infoMap.put("lala_scroe", ls);
					infoMap.put("nfc_score", StringUtils.formatDouble(ns));
					infoMap.put("praise_scroe", ss);
					infoMap.put("sign_score", ps);
					infoMap.put("online_scroe", os);
					infoMap.put("recom_scroe", rs);
					infoMap.put("score", StringUtils.formatDouble(ls+ss+ns+os+rs+ps));
					infoMap.put("cashscore", 0);//�Ѷһ����֣�***Ԥ���ӿ�****
				}
			}else if(detail.equals("total")){//��ʷ��������
				infoMap = daService.getMap("select sum(lala_scroe) lala_scroe,sum(nfc_score) nfc_score," +
						"sum(praise_scroe) praise_scroe,sum(pai_score) pai_score ,sum(online_scroe) online_scroe,uin,sum(recom_scroe) recom_scroe " +
						" from collector_scroe_tb where uin=? group by uin", new Object[]{uin});
				if(infoMap!=null){
					Long ls = (Long)infoMap.get("lala_scroe");
					Long ss = (Long)infoMap.get("praise_scroe");
					Double ps = StringUtils.formatDouble(infoMap.get("pai_score"));
					Long rs = (Long)infoMap.get("recom_scroe");
					Double os = 0d;
					Double ns = 0d;//
					if(infoMap.get("online_scroe")!=null)
						os = Double.valueOf(infoMap.get("online_scroe")+"");
					if(infoMap.get("nfc_score")!=null)
						ns = Double.valueOf(infoMap.get("nfc_score")+"");
					if(ls==null)
						ls =0L;
					if(ss==null)
						ss=0L;
					infoMap.put("lala_scroe", ls);
					infoMap.put("nfc_score", StringUtils.formatDouble(ns));
					infoMap.put("praise_scroe", ss);
					infoMap.put("sign_score", ps);
					infoMap.put("online_scroe", os);
					infoMap.put("recom_scroe", rs);
					infoMap.put("score", StringUtils.formatDouble(ls+ss+ns+os+rs+ps));
					infoMap.put("cashscore", 0);//�Ѷһ����֣�***Ԥ���ӿ�****
				}
			}
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
			return null;*/
			//test:http://127.0.0.1/zld/collectorrequest.do?action=score&token=2f73bdccceecbbf436b61aca464af21b&week=toweek&detail=to   toweek&detail=total
			//(toweek,total)
			
		}else if(action.equals("getparkaccount")){
			
			Map parkAccountMap  = daService.getMap("select sum(amount) amount from park_account_tb where create_time>? and uid =? and type=? ", 
					new Object[]{TimeTools.getToDayBeginTime(),uin,0});
			Double total = 0d;
			if(parkAccountMap!=null){
				total =StringUtils.formatDouble(parkAccountMap.get("amount"));
			}
			AjaxUtil.ajaxOutput(response, ""+total);
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=getparkaccount&token=4182e6ad895208c3d4829d447e0c61b7
		}else if(action.equals("getpadetail")){//�˻���ϸ
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "size", 20);
			Long stype=RequestUtil.getLong(request, "stype", -1L);//0:���룬1����
			String sql = "select create_time ,remark r,amount money,type mtype  from park_account_tb where comid=?";
			String countSql = "select count(id)  from park_account_tb where comid=?";
			List<Object> params = new ArrayList<Object>();
			params.add(comId);
			
			if(stype>-1){
				if(stype==1){//����
					sql +=" and type=? ";
					countSql +=" and type=? ";
					params.add(stype);
				}else {//�����ͣ�������� 
					sql +=" and type in(?,?) ";
					countSql +=" and type in(?,?) ";
					params.add(stype);
					params.add(2L);
				}
			}
			Long count= daService.getCount(countSql, params);
			List pamList = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				pamList = daService.getAll(sql+" order by id desc ", params, pageNum, pageSize);
			}
			
			if(pamList!=null&&!pamList.isEmpty()){
				for(int i=0;i<pamList.size();i++){
					Map map = (Map)pamList.get(i);
					Integer type = (Integer)map.get("mtype");
					String remark = (String)map.get("r");
					if(type==0){
						if(remark.indexOf("_")!=-1){
							map.put("note", remark.split("_")[0]);
							map.put("target", remark.split("_")[1]);
						}
					}else if(type==1){
						map.put("note", "����");
						map.put("target", "���п�");
					}else if(type==2){
						map.put("note", "����");
						map.put("target", "ͣ����");
						map.put("mtype", 1);
					}
					map.remove("r");
				}
			}
			String reslut =  "{\"count\":"+count+",\"info\":"+StringUtils.createJson(pamList)+"}";
			AjaxUtil.ajaxOutput(response, reslut);
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=getpadetail&token=c5ea6e5fd0acdf97a262f7f86c31f3ae
		}else if(action.equals("withdraw")){//������������
			//http://192.168.199.240/zld/collectorrequest.do?action=withdraw&uid=10343&comid=858&money=20
			Double money = RequestUtil.getDouble(request, "money", 0d);
			Long count = daService.getLong("select count(*) from park_account_tb where comid= ? and create_time>? and type=?  ", 
					new Object[]{comId,TimeTools.getToDayBeginTime(),1}) ;
			if(count>2){//ÿ��ֻ������
				AjaxUtil.ajaxOutput(response, "{\"result\":-2,\"times\":"+count+"}");
				return null;
			}
			
			List<Map> accList = daService.getAll("select id,type from com_account_tb where comid =? and type in(?,?) and state =? order by id desc",
					new Object[]{comId,0,2,0});
			Long accId = null;
			Integer type =0;
			if(accList!=null&&!accList.isEmpty()){
				accId = null;
				for(Map m: accList){
					type = (Integer)m.get("type");
					if(type!=null&&type==2){
						accId =  (Long)m.get("id");	
						break;
					}
				}
				if(accId==null)
					accId=(Long)accList.get(0).get("id");
			}else{
				//û�����������˻�
				AjaxUtil.ajaxOutput(response, "{\"result\":-1,\"times\":0}");
				return null;
			}
			//���ֲ���
			boolean result =false;
			if(money>0){
				Map userMap = daService.getMap("select money from com_info_Tb where id=? ", new Object[]{comId});
				//�û����
				Double balance =StringUtils.formatDouble(userMap.get("money"));
				if(money<=balance){//���ֽ��������
					//�۳��ʺ����//д���������
					List<Map<String, Object>> sqlList = new ArrayList<Map<String,Object>>();
					Map<String, Object> userSqlMap = new HashMap<String, Object>();
					userSqlMap.put("sql", "update com_info_Tb set money = money-? where id= ?");
					userSqlMap.put("values", new Object[]{money,comId});
					Map<String, Object> withdrawSqlMap = new HashMap<String, Object>();
					withdrawSqlMap.put("sql", "insert into withdrawer_tb  (comid,amount,create_time,acc_id,uin,wtype) values(?,?,?,?,?,?)");
					withdrawSqlMap.put("values", new Object[]{comId,money,System.currentTimeMillis()/1000,accId,uin,type});
					Map<String, Object> moneySqlMap = new HashMap<String, Object>();
					moneySqlMap.put("sql", "insert into park_account_tb (comid,amount,create_time,type,remark,uid,source) values(?,?,?,?,?,?,?)");
					moneySqlMap.put("values", new Object[]{comId,money,System.currentTimeMillis()/1000,1,"����",uin,5});
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
		}else if(action.equals("getpaccount")){//��ѯͣ�����˻��ܶ�
			Map comMap = daService.getMap("select money from com_info_tb where id=?", new Object[]{comId});
			Double total = 0d;
			if(comMap!=null){
				total = StringUtils.formatDouble(comMap.get("money"));
			}
			AjaxUtil.ajaxOutput(response, total+"");
			//http://127.0.0.1/zld/collectorrequest.do?action=getpaccount&token=17ad4f0a3cbdce40c56595f00d7666bc
			return null;
		}else if(action.equals("getparkbank")){
			Map comaMap  = daService.getMap("select id,card_number,name,mobile,bank_name,area,bank_pint,user_id from com_account_tb where comid=? and type=? order by id desc ",new Object[]{comId,0});
			String ret =StringUtils.createJson(comaMap);
			AjaxUtil.ajaxOutput(response, ret.replace("null", ""));
			//http://127.0.0.1/zld/collectorrequest.do?action=getparkbank&token=17ad4f0a3cbdce40c56595f00d7666bc
			return null;
		}else if(action.equals("addparkbank")){
			String name =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "name"));
//			Long uin =RequestUtil.getLong(request, "uin",-1L);
			String card_number =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "card_number"));
			String mobile =RequestUtil.processParams(request, "mobile");
			String userId =RequestUtil.processParams(request, "user_id");
			String bank_name =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "bank_name"));
			String area =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "area"));
			String bank_point =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "bank_pint"));
			int result = 0;
			if(!card_number.equals("")&&!mobile.equals("")&&!bank_name.equals("")){
				result = daService.update("insert into com_account_tb (comid,uin,name,card_number,mobile," +
						"bank_name,atype,area,bank_pint,type,state,user_id)" +
						" values(?,?,?,?,?,?,?,?,?,?,?,?)",
						new Object[]{comId,uin,name,card_number,mobile,bank_name,0,area,bank_point,0,0,userId});
			}
			//http://127.0.0.1/zld/collectorrequest.do?action=addparkbank&token=aa9a48d2f41bb2722f29c8714cbc754c
			//&name=&card_number=&mobile=&bank_name=&area=&bank_point=&atype=&note=
			logger.info(result);
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("editpbank")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			String name =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "name"));
			String card_number =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "card_number"));
			String mobile =RequestUtil.processParams(request, "mobile");
			String bank_name =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "bank_name"));
			String area =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "area"));
			String bank_point =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "bank_pint"));
			String userId =RequestUtil.processParams(request, "user_id");
			Integer atype = RequestUtil.getInteger(request, "atype", 0);//0���п���1֧������2΢��
			int result = 0;
			if(!card_number.equals("")&&!mobile.equals("")&&!bank_name.equals("")&&id!=-1){
				result = daService.update("update com_account_tb set name=?,card_number=?,mobile=?,bank_name=?," +
						"area=?,bank_pint=?,atype=?,user_id=? where id = ? and type=? ",
						new Object[]{name,card_number,mobile,bank_name,area,bank_point,atype,userId,id,0});
			}
			//http://127.0.0.1/zld/collectorrequest.do?action=editpbank&token=aa9a48d2f41bb2722f29c8714cbc754c
			//&name=&card_number=&mobile=&bank_name=&area=&bank_point=&atype=&note=user_id=&id=
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("uploadll")){//upload lat and lon, �շ�Ա�ϴ���γ��
			Double lon = RequestUtil.getDouble(request, "lon", 0d);
			Double lat = RequestUtil.getDouble(request, "lat", 0d);
			if(lat==0||lon==0){
				AjaxUtil.ajaxOutput(response, "0");
				return null;
			}
			Map comMap = daService.getMap("select longitude,latitude from com_info_Tb where id =? ", new Object[]{comId});
			Double lon1 = Double.valueOf(comMap.get("longitude")+"");
			Double lat1 = Double.valueOf(comMap.get("latitude")+"");
			Double distance = StringUtils.distance(lon, lat, lon1, lat1);
			Integer isOnseat = 0;
			if(distance<500){//�ڳ���500�׷�Χ��ʱ����Ϊ��λ��
				isOnseat = 1;
			}
			Long ntime = System.currentTimeMillis()/1000;
			logger.info(">>>>>parkuser distance,uin:"+uin+",dis:"+distance+",authflag:"+authFlag);
			//�����շ�Ա��λ��Ϣ��23��ʾ��λ
			if(authFlag!=13){//����Ա�ϴ�����״̬
				daService.update("update user_info_tb set online_flag =? where id=? ", new Object[]{22+isOnseat,uin});
			}
			//д��λ���ϴ���־ 
			int result = daService.update("insert into user_local_tb (uid,lon,lat,distance,is_onseat,ctime) values(?,?,?,?,?,?)",
					new Object[]{uin,lon,lat,distance,isOnseat,ntime});
			Long count = daService.getLong("select count(id) from user_info_Tb where comid =? and online_flag=? ", new Object[]{comId,23});
			if(count>0){//���շ�Ա��λ,���³����Ƿ����շ�Ա��λ��־
				daService.update("update com_info_tb set is_hasparker=?, update_time=? where id = ? and is_hasparker=? ", new Object[]{1,ntime,comId,0});
			}else {
				daService.update("update com_info_tb set is_hasparker=?, update_time=? where id = ? and is_hasparker=?", new Object[]{0,ntime,comId,1});
			}
			AjaxUtil.ajaxOutput(response, ""+result);
			//http://127.0.0.1/zld/collectorrequest.do?action=uploadll&token=aa9a48d2f41bb2722f29c8714cbc754c&lon=&lat=
			return null;
		}else if(action.equals("reguser")){//�շ�Ա�Ƽ�����
			String carNumber =AjaxUtil.decodeUTF8( RequestUtil.getString(request, "carnumber"));
			carNumber = carNumber.toUpperCase().trim();
			carNumber = carNumber.replace("I", "1").replace("O", "0");
			String mobile = RequestUtil.getString(request, "mobile");
			if(!carNumber.equals("")){
				if(mobile.equals("")){//��֤���ƺ�
					Long count = daService.getLong("select count(id) from car_info_tb where car_number=?", new Object[]{carNumber});
					if(count>0){
						AjaxUtil.ajaxOutput(response, "-1");
						return null;
					}
				}else {//ע�ᳵ����ͬʱֻ��֤�ֻ���
					Long count = daService.getLong("select count(id) from user_info_tb where mobile=? and auth_flag=? ", new Object[]{mobile,4});
					if(count>0){
						AjaxUtil.ajaxOutput(response, "-1");
						return null;
					}
					//д�û�����
					List<Map<String, Object>> sqlList = new ArrayList<Map<String,Object>>();
					//�û���Ϣ
					Map<String, Object> userSqlMap = new HashMap<String, Object>();
					//��һ���û��˺�
					Long key = daService.getkey("seq_user_info_tb");
					userSqlMap.put("sql", "insert into user_info_tb (id,nickname,strid,mobile,reg_time,comid,auth_flag,recom_code,media) " +
							"values(?,?,?,?,?,?,?,?,?)");
					userSqlMap.put("values", new Object[]{key,"����","zlduser"+key,mobile,System.currentTimeMillis()/1000,0,4,uin,999});
					sqlList.add(userSqlMap);
					//������Ϣ
					Map<String, Object> carSqlMap = new HashMap<String, Object>();
					carSqlMap.put("sql", "insert into car_info_tb(uin,car_number) values(?,?)");
					carSqlMap.put("values", new Object[]{key,carNumber});
					sqlList.add(carSqlMap);
					//�Ƽ���Ϣ
					Map<String, Object> recomSqlMap = new HashMap<String, Object>();
					recomSqlMap.put("sql", "insert into recommend_tb (pid,nid,type,state,create_time) values(?,?,?,?,?)");
					recomSqlMap.put("values", new Object[]{uin,key,0,0,System.currentTimeMillis()/1000});
					sqlList.add(recomSqlMap);
					
					boolean ret = daService.bathUpdate(sqlList);
					if(!ret){
						AjaxUtil.ajaxOutput(response, "-2");
						return null;
					}else {//��������30Ԫͣ��ȯ
						//�Ƽ��������շ�Ա��1��
						//logService.updateScroe(5, uin, comId);
						Long ntime = System.currentTimeMillis()/1000;
						int result=publicMethods.backNewUserTickets(ntime, key);// daService.bathInsert(tsql, values, new int[]{4,4,4,4,4});
						if(result==0){
							String bsql = "insert into bonus_record_tb (bid,ctime,mobile,state,amount) values(?,?,?,?,?) ";
							Object [] values = new Object[]{999,ntime,mobile,0,10};//�Ǽ�Ϊδ��ȡ�������¼ʱд��ͣ��ȯ���ж��Ƿ��Ǻ�������
							logger.info(">>>>>>>>�շ�Ա�Ƽ�����("+mobile+")����30Ԫͣ��ȯ��д������¼����¼ʱ������"+daService.update(bsql,values));
						
						}
						int	eb = daService.update("insert into user_profile_tb (uin,low_recharge,limit_money,auto_cash," +
								"create_time,update_time) values(?,?,?,?,?,?)", 
								new Object[]{key,10,25,1,ntime,ntime});
						
						logger.info("�˻�:"+uin+",�ֻ���"+mobile+",��ע���û�(�����շ�Ա�Ƽ�)��д����ͣ��ȯ"+result+"��,�Զ�֧��д�룺"+eb);
						String mesg ="��ʵ���ͣ���ѣ���������ͣ������ͣ���������Żݣ�8ԪǮͣ5�γ������ص�ַ�� http://t.cn/RZJ4UAv ��ͣ������";
//						SendMessage.sendMultiMessage(mobile, mesg);
					}
				}
			}else {
				AjaxUtil.ajaxOutput(response, "0");
				return null;
			}
			AjaxUtil.ajaxOutput(response, "1");
			//http://127.0.0.1/zld/collectorrequest.do?action=reguser&token=6ed161cde6c7149de49d72719f2eb39b&mobile=15801482645&carnumber=123456
			return null;
		}else if(action.equals("regcolmsg")){//�����Ƽ�����
			System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>>>�����Ƽ�����+"+uin);
			Long tid = daService.getkey("seq_transfer_url_tb");
			//String url = "http://192.168.199.240/zld/turl?p="+tid;
			String url = "http://s.tingchebao.com/zld/turl?p="+tid;
			int result = daService.update("insert into transfer_url_tb(id,url,ctime,state) values (?,?,?,?)",
					new Object[]{tid,"regparker.do?action=toregpage&recomcode="+uin,
							System.currentTimeMillis()/1000,0});
			
			if(result!=1)
				url="�Ƽ�ʧ��!";
			if(out.equals("json")){
				AjaxUtil.ajaxOutput(response, "{\"url\":\""+url+"\"}");
			}else {
				AjaxUtil.ajaxOutput(response,url);
			}
			//http://127.0.0.1/zld/collectorrequest.do?action=regcarmsg&token=6ed161cde6c7149de49d72719f2eb39b
			return null;
		}else if(action.equals("recominfo")){//�����շ�Ա�Ƽ���¼
			Integer rtype = RequestUtil.getInteger(request, "type", 0);//0:������1:����
			List<Map<String, Object>> list =null;
			if(rtype==0){
				list = daService.getAll("select c.nid,u.mobile uin,c.state,c.money from recommend_tb c left join user_info_tb u on c.nid=u.id where pid=? and c.type=? order by c.id desc ",new Object[]{uin,rtype});
			}else {
				list  =daService.getAll("select nid uin,state,money from recommend_tb where pid=? and type=? order by id desc",new Object[]{uin,rtype});
			}
			if(list!=null&&!list.isEmpty()){
				for(Map<String, Object> map :list){
					Integer state = (Integer)map.get("state");
					if(state==null||state!=1)
						continue;
					Double money = StringUtils.formatDouble(map.get("money"));
					if(rtype==0&&money==0)
						map.put("money", 5);
					else if(rtype==1&&money==0){
						map.put("money", 30);
					}
				}
			}
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(list));
			//http://127.0.0.1/zld/collectorrequest.do?action=recominfo&token=40ffacdad78acf0c43e0aabae9712602
			return null;
		}else if(action.equals("getmesg")){
			Long maxid = RequestUtil.getLong(request, "maxid", -1L);
			Integer page = RequestUtil.getInteger(request, "page", 1);
			if(maxid>-1){
				Long count = daService.getLong("select count(ID) from parkuser_message_tb where uin=? and id>?", new Object[]{uin,maxid});
				AjaxUtil.ajaxOutput(response, count+"");
			}else{
				List<Object> params = new ArrayList<Object>();
				params.add(uin);
				List<Map<String, Object>> list = daService.getAll("select * from parkuser_message_tb where uin=? order by id desc",
						params,page,10);
				AjaxUtil.ajaxOutput(response, StringUtils.createJson(list));
			}
			//http://127.0.0.1/zld/carowner.do?action=getmesg&token=&page=-1&maxid=0
			return null;
		}else if(action.equals("getincome")){//�����շ�Աһ��ʱ�����ֽ���������ֻ�������
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			Long logonTime = RequestUtil.getLong(request, "logontime", -1L);//20150618���ϴ����¼ʱ�� 
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime= df2.format(System.currentTimeMillis());
			Long b = System.currentTimeMillis()/1000;
			Long e =b;
			if(logonTime!=-1){
				b = logonTime;
				//logger.info(b);
				b = (logonTime/60)*60;
			}else {
				if(btime.equals("")){
					btime = nowtime;
				}
				if(etime.equals("")){
					etime = nowtime;
				}
				b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
				e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
			}
			//logger.info(b);
			String sql = "select sum(total) money,pay_type from order_tb where uid=? and c_type=? and state=? and end_time between ? and ? group by pay_type order by pay_type desc ";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = daService.getAll(sql, new Object[]{uin,3,1,b,e});
			Map<String, Object> map = new HashMap<String, Object>();
			for(Map<String, Object> map2 : list){
				Integer pay_type = (Integer)map2.get("pay_type");
				if(pay_type == 2){//�ֻ�֧��
					map.put("mobilepay", map2.get("money"));
				}else if(pay_type == 1){//�ֽ�֧��
					map.put("cashpay", map2.get("money"));
				}
			}
			//logger.info(map);
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(map));
			//http://127.0.0.1/zld/collectorrequest.do?action=getincome&token=15d1bb15b8dcb99aa7dbe0adc9797162&btime=2012-12-28
		}else if(action.equals("getnewincome")){//�����շ�Աһ��ʱ�����ֽ���������ֻ�������
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			Long logonTime = RequestUtil.getLong(request, "logontime", -1L);//20150618���ϴ����¼ʱ�� 
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			Long worksiteid = RequestUtil.getLong(request, "worksite_id",-1L);
			Long comid = RequestUtil.getLong(request, "comid",-1L);
			String nowtime= df2.format(System.currentTimeMillis());
			Long b = System.currentTimeMillis()/1000;
			Long e =b;
			if(logonTime!=-1){
				b = logonTime;
				//logger.info(b);
				b = (logonTime/60)*60;
			}else {
				if(btime.equals("")){
					btime = nowtime;
				}
				if(etime.equals("")){
					etime = nowtime;
				}
				b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
				e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
			}
			Map<String, Object> map = new HashMap<String, Object>();
//			if(worksiteid!=-1){
				Map ret = daService.getMap(
						"select * from parkuser_work_record_tb where end_time is null and uid=? and worksite_id = ?",
						new Object[] {uin,worksiteid});
				if(ret!=null){
					b = Long.valueOf(ret.get("start_time")+"");
					map.put("start_time", b);
					if(ret.get("end_time")==null){
						e=Long.MAX_VALUE;
					}else{
						e=Long.valueOf(ret.get("end_time")+"");
					}
				}else{
					Long bLong = System.currentTimeMillis()/1000;
					int workret = daService.update("insert into parkuser_work_record_tb(start_time,uid,worksite_id) values(?,?,?)",
							new Object[]{bLong,uin,worksiteid});
					if(workret==1){
						map.put("start_time", b);
					}
					logger.error("collectorlogin>>>>>:ͨ���������ϰࣺuid:"+uin+",worksiteid:"+worksiteid+",r:"+workret);
				}
//			}
			Map cash = daService.getMap("select sum(b.amount)money from order_tb a,parkuser_cash_tb b where  a.end_time between" +
					" ? and ? and a.state=? and a.uid=? and a.id=b.orderid and b.type=?",new Object[]{b,e,1,uin,0});
			Double money =0d;
			if(cash!=null&&cash.get("money")!=null){
				money = Double.valueOf(cash.get("money")+"");
			}
			map.put("cashpay", StringUtils.formatDouble(money));
			Double pmoney = 0d;
			
			Map park = daService.getMap( "select sum(amount) total from park_account_tb where create_time between ? and ? " +
					" and type= ? and source=? and uid=? and comid=? ",new Object[]{b,e,0,0,uin,comid});
			if(park!=null&&park.get("total")!=null)
				pmoney += Double.valueOf(park.get("total")+"");
			
			Map parkuser = daService.getMap( "select sum(amount) total from parkuser_account_tb where create_time between ? and ? " +
					" and type= ? and uin = ? and target =?",new Object[]{b,e,0,uin,4});
			if(parkuser!=null&&parkuser.get("total")!=null)
				pmoney += Double.valueOf(parkuser.get("total")+"");
			
			map.put("mobilepay", StringUtils.formatDouble(pmoney));
			
			//logger.info(map);
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(map));
			//http://127.0.0.1/zld/collectorrequest.do?action=getincome&token=15d1bb15b8dcb99aa7dbe0adc9797162&btime=2012-12-28
		}else if(action.equals("querycarpics")){//����һ���µĳ��ƻ���
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			String sql = "select distinct(car_number) from order_tb where comid=? and c_type=? and create_time between ? and ? ";
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime= df2.format(System.currentTimeMillis());
			Long endTime = TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(nowtime+" 23:59:59");
			Long beginTime = endTime - 30*24*60*60;
			list = daService.getAll(sql, new Object[]{comId,2,beginTime,endTime});
			String result = StringUtils.createJson(list);
			AjaxUtil.ajaxOutput(response, result);
			return null;
			//http://192.168.10.239/zld/collectorrequest.do?action=querycarpics&token=
		}else if(action.equals("incomanly")){//����ͳ��
			//0�Լ�,1����
			Integer acctype = RequestUtil.getInteger(request, "acctype", 0);
			//0ͣ���ѣ�1���� ��2����,3 ȫ��
			Integer income = RequestUtil.getInteger(request, "incom", 0);
			//0���죬1���죬2���ܣ�3����
			Integer datetype = RequestUtil.getInteger(request, "datetype", 0);
			Integer page = RequestUtil.getInteger(request, "page", 1);
			page = page<1?1:page;
			List<Object> params = new ArrayList<Object>();
			
			String sql = "";
			String totalSql = "";
			if(acctype==0){//0�Լ�,1����
				sql +="select amount money,type mtype,create_time," +
						"remark note,target from parkuser_account_tb where uin=? ";
				totalSql = "select sum(amount) total from parkuser_account_tb where uin=?";
				params.add(uin);
			}else if(acctype==1){
				sql +=" select create_time ,remark r,amount money,type mtype  from park_account_tb where comid=? ";
				totalSql = "select sum(amount) total from park_account_tb where comid=?";
				params.add(comId);
			}
			if(income==0){//0ͣ����
				if(acctype==0){//0�Լ�,1����
					sql +=" and type=? and target=? ";
					totalSql +=" and type=? and target=? ";
					params.add(0);
					params.add(4);
				}else if(acctype==1){
					sql +=" and type= ? ";
					totalSql +=" and type= ? ";
					params.add(0);
				}
			}else if(income==1){//1���� 
				if(acctype==0){//0�Լ�,1����
					sql +=" and type=? and target=? and amount =? ";
					totalSql +=" and type=? and target=? and amount =? ";
					params.add(0);
					params.add(3);
					params.add(2d);
				}else if(acctype==1){
					sql +=" and type= ? ";
					totalSql +=" and type= ? ";
					params.add(2);
				}
			}else if(income==2){//2����
				if(acctype==0){//0�Լ�,1����
					sql +=" and type=? and target=? and amount >? ";
					totalSql +=" and type=? and target=? and amount >? ";
					params.add(0);
					params.add(3);
					params.add(2d);
				}else if(acctype==1){
					sql +=" and type= ? ";
					totalSql +=" and type= ? ";
					params.add(3);
				}
			}
			
			Long btime = TimeTools.getToDayBeginTime();
			Long etime = btime+24*60*60;
			if(datetype==1){
				etime = btime ;
				btime = btime-24*60*60;
			}else if(datetype==2){
				btime = TimeTools.getWeekStartSeconds();
			}else if(datetype==3){
				btime = TimeTools.getMonthStartSeconds();
			}
			sql +=" and create_time between ? and ? order by create_time desc";
			totalSql +=" and create_time between ? and ? ";
			params.add(btime);
			params.add(etime);
//			System.out.println(sql);
//			System.out.println(totalSql);
			System.err.println(">>>>>>incomanly:"+sql+":"+params);
			Map totalMap = daService.getMap(totalSql, params);
			List reslutList = daService.getAll(sql, params,page,20);	
			setAccountList(reslutList,acctype);
			String total = totalMap.get("total")+"";
			if(total.equals("null"))
				total = "0.0";
			String reslut =  "{\"total\":\""+total+"\",\"info\":"+StringUtils.createJson(reslutList)+"}";
			System.err.println(reslut);
			AjaxUtil.ajaxOutput(response, reslut);
			return null;
			//http://192.168.199.240/zld/collectorrequest.do?action=incomanly&acctype=1&incom=2&datetype=2&page=1&token=6d5d6a1bd45b5dafd2294e99cf9c91c9
		}else if(action.equals("invalidorders")){
			Long invalid_order = RequestUtil.getLong(request, "invalid_order", 0L);
			int result = daService.update("update com_info_tb set invalid_order=invalid_order+? where id=?", new Object[]{invalid_order, comId});
			AjaxUtil.ajaxOutput(response, result + "");
			return null;
			//http://192.168.199.239/zld/collectorrequest.do?action=invalidorders&invalid_order=-1&token=198f697eb27de5515e91a70d1f64cec7
		}else if(action.equals("bindworksite")){//�շ�Ա�󶨹���վ
			Long wid = RequestUtil.getLong(request, "wid", -1L);
			logger.info(">>>>disbind,wid:"+wid);
			out="json";
			int ret =0;
			if(uin!=-1){
				if(wid==-1){//���
					ret = daService.update("delete from user_worksite_tb where uin = ? ", new Object[]{uin});
					logger.info(">>>>disbind  �շ�Ա���   worksite,user:"+uin+"ret:"+ret);
					ret = 1;
				}else {//��
					//��ǰ�ȴ���������վ���¸�
					ret = daService.update("delete from user_worksite_tb where uin = ?  ", new Object[]{uin});
					logger.info(">>>>bind �շ�Ա�ϸڣ�ɾ��ԭ���ڵĹ���վ:"+ret);
					//ɾ��ԭ���շ�Ա
					Map oldMap = daService.getMap("select uin from user_worksite_tb where worksite_id=? ", new Object[]{wid});
					if(oldMap!=null){
						ret = daService.update("delete from user_worksite_tb where worksite_id = ?  ", new Object[]{wid});
						if(ret>0){
							Long uid =(Long)oldMap.get("uin");
							if(uid!=null&&uid>0)
								ret = daService.update("insert into order_message_tb(message_type,state,uin)" +
									" values(?,?,?)", new Object[]{4,0,uid});//����Ϣ���շ�Ա��֪ͨ���Ѳ��ڸ�
							logger.info(">>>>disbind �շ�Ա�ϸڣ�ԭ�շ�Ա�¸�  worksite,delete old user:"+uid+",ret:"+ret);
						}
					}
					//���շ�Ա
					ret = daService.update("insert into user_worksite_tb (worksite_id,uin) values(?,?)",new Object[]{wid,uin});
					logger.info(">>>>bind worksite,�շ�Ա�ϸ�  bind new user:"+uin+", ret="+ret);
				}
			}
			infoMap.put("result", ret+"");
			//collectorrequest.do?action=bindworksite&wid=&token=198f697eb27de5515e91a70d1f64cec7
		}else if(action.equals("gooffwork")){
			//�շ�Ա�°�
			Long  worksiteid =RequestUtil.getLong(request, "worksiteid",-1L);
			long endtime = System.currentTimeMillis() / 1000;
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:00");
//			String d = sdf.format(System.currentTimeMillis());
//			long endtime = sdf.parse(d).getTime()/1000;
			List user = daService.getAll("select * from parkuser_work_record_tb where worksite_id=? and uid = ? and end_time is null", new Object[] {
					worksiteid, uin });
			int result = daService
					.update("update parkuser_work_record_tb set end_time=? where worksite_id = ? and uid = ? and end_time is null",
							new Object[] { endtime,
									worksiteid, uin });
			if(result==1){
				uploadWork((Long)((Map)user.get(0)).get("id"));
			}
			logger.info("gooffwork>>>>>�°�result��"+result+",uin:"+uin+",worksiteid:"+worksiteid);
			if(result > 0){
				AjaxUtil.ajaxOutput(response, "1");
			}else{
				AjaxUtil.ajaxOutput(response, "-1");
			}
			return null;
		}else if(action.equals("akeycheckaccount")){
			String ret = "{";
			Long b = TimeTools.getToDayBeginTime();
			Long e = System.currentTimeMillis()/1000+60;
			Double parkmoney = 0d;
			Double parkusermoney = 0d;
			Double cashmoney = 0d;
			Long ordertotal = 0L;
			Long epayordertotal = 0L;
			Double ordertotalmoney = 0d;
			Double epaytotalmoney = 0d;
			//һ������   1:��������Ա    2�շ�Ա
			Map park = daService.getMap( "select sum(amount) total from park_account_tb where create_time between ? and ? " +
					" and type <> ? and uid=? and comid=? ",new Object[]{b,e,1,uin,comId});
			if(park!=null&&park.get("total")!=null)
				parkmoney = Double.valueOf(park.get("total")+"");//�����˻����루������Դ��
			
			Map parkuser = daService.getMap( "select sum(amount) total from parkuser_account_tb where create_time between ? and ? " +
					" and type= ? and uin = ? ",new Object[]{b,e,0,uin});
			if(parkuser!=null&&parkuser.get("total")!=null)
				parkusermoney = Double.valueOf(parkuser.get("total")+"");//�շ�Ա�˻����루������Դ��
			
			Map cash = daService.getMap( "select sum(amount) total from parkuser_cash_tb where create_time between ? and ? " +
					" and uin=? ",new Object[]{b,e,uin});
			if(cash!=null&&cash.get("total")!=null)
				cashmoney = Double.valueOf(cash.get("total")+"");//�շ�Ա�ֽ�����
			Map ordertotalMap = daService.getMap( "select count(*) scount,sum(total) total from order_tb where end_time between ? and ? " +
					" and uid=? and state=?",new Object[]{b,e,uin,1});//�ܵĶ���
			if(ordertotalMap!=null){
				if(ordertotalMap.get("total")!=null)
					ordertotalmoney = Double.valueOf(ordertotalMap.get("total")+"");
				if(ordertotalMap.get("scount")!=null)
					ordertotal = Long.valueOf(ordertotalMap.get("scount")+"");
			}
			Map epayordertotalMap = daService.getMap( "select count(*) scount,sum(total) total from order_tb where end_time between ? and ? " +
					" and uid=? and c_type=? and state=?",new Object[]{b,e,uin,4,1});//ֱ������
			if(epayordertotalMap!=null){
				if(epayordertotalMap.get("total")!=null)
					epaytotalmoney = Double.valueOf(epayordertotalMap.get("total")+"");
				if(epayordertotalMap.get("scount")!=null)
					epayordertotal = Long.valueOf(epayordertotalMap.get("scount")+"");
			}
			ret+="\"totalmoney\":\""+StringUtils.formatDouble((parkmoney+parkusermoney+cashmoney))+"\",\"mobilemoney\":\""+StringUtils.formatDouble((parkmoney+parkusermoney))+
			"\",\"cashmoney\":\""+StringUtils.formatDouble(cashmoney)+"\",\"mycount\":\""+StringUtils.formatDouble(parkusermoney)+"\",\"parkaccout\":\""+StringUtils.formatDouble(parkmoney)+"\",\"timeordercount\":\""+
			(ordertotal-epayordertotal)+"\",\"timeordermoney\":\""+StringUtils.formatDouble((ordertotalmoney-epaytotalmoney))+
			"\",\"epayordercount\":\""+epayordertotal+"\",\"epaymoney\":\""+StringUtils.formatDouble(epaytotalmoney)+"\"}";
			logger.info("akeycheckaccount>>>��"+ret);
			AjaxUtil.ajaxOutput(response, ret);
			return null;
		}else if(action.equals("getparkdetail")){
			//����Ա�鿴����������ͣ������ϸ
			String ret="{";
			Long b = TimeTools.getToDayBeginTime();
			Long e = System.currentTimeMillis()/1000;
//			long b=1436544000;,e=1435916665;
			Double mmoney = 0d;
			Double cashmoney = 0d;
			Double total = 0d;
			if(authFlag==1){
				ArrayList list1 = new ArrayList();
				ArrayList list2 = new ArrayList();
				list1.add(b);
				list1.add(e);
				list1.add(0);
				list1.add(0);
				list1.add(comId);
				list2.add(b);
				list2.add(e);
				list2.add(0);
				list2.add(4);
				list2.add("ͣ����%");
				list2.add(comId);
				List park = daService.getAllMap( "select b.nickname, sum(a.amount) total,a.uid from park_account_tb a,user_info_tb b where a.create_time between ? and ? " +
						" and a.type= ? and a.source=? and a.comid=? and a.uid=b.id group by a.uid,b.nickname ",list1);//�����˻�ͣ����
				
				List parkuser = daService.getAllMap( "select b.nickname,sum(a.amount) total,a.uin uid from parkuser_account_tb a,user_info_tb b where a.create_time between ? and ? " +
						" and a.type= ? and a.target=? and a.remark like ? and a.uin=b.id and a.uin in (select id from user_info_tb where comid=?) group by a.uin,b.nickname",list2);//�շ�Ա�˻�ͣ����
				TreeSet<Long> set = new TreeSet<Long>();
				if(park!=null&&park.size()>0){
					if(parkuser!=null&&parkuser.size()>0)
						park.addAll(parkuser);
					for (int i = 0; i < park.size(); i++) {
//						System.out.println(park.size());
						Map obj1 = (Map)park.get(i);
						Long id1 = Long.valueOf(obj1.get("uid")+"");
						set.add(id1);
						Map cash = daService.getMap( "select sum(amount) total from parkuser_cash_tb where create_time between ? and ? " +
								" and uin=? ",new Object[]{b,e,id1});
						if(cash!=null&&cash.get("total")!=null){
							double cmoney = Double.valueOf(cash.get("total")+"");
							cashmoney+=cmoney;
							obj1.put("cash",StringUtils.formatDouble(cmoney ));//�շ�Ա�ֽ�����
						}else{
							obj1.put("cash",0.00);//�շ�Ա�ֽ�����
						}
						double ummoney = Double.valueOf(obj1.get("total")+"");
						mmoney+=ummoney;
						for (int j = i+1; j < park.size(); j++) {
//							System.out.println(park.size());
							Map obj2 = (Map)park.get(j);
							long id2 = Long.valueOf(obj2.get("uid")+"");
							if(id1==id2){
								double total1 =Double.valueOf(obj2.get("total")+"");
								mmoney+=total1;
								obj1.put("total", StringUtils.formatDouble(total1+ummoney));
								park.remove(j);
							}
						}
					}
				}else{
					park = parkuser;
					for (Object object : parkuser) {
						Map obj = (Map)object;
						Map cash = daService.getMap( "select sum(amount) total from parkuser_cash_tb where create_time between ? and ? " +
								" and uin=? ",new Object[]{b,e,Long.valueOf(obj.get("uid")+"")});
						set.add(Long.valueOf(obj.get("uid")+""));
						if(cash!=null&&cash.get("total")!=null){
							double cmoney = Double.valueOf(cash.get("total")+"");
							cashmoney+=cmoney;
						}
						if(obj.get("total")!=null)
							mmoney+=Double.valueOf(obj.get("total")+"");
					}
				}
				List user = daService.getAll( "select id, nickname from user_info_tb where comid=?",new Object[]{comId});
				for (Object object : user) {
					Map obj = (Map)object;
					if(set.add(Long.valueOf(obj.get("id")+""))){
						Map cash = daService.getMap( "select sum(amount) total from parkuser_cash_tb where create_time between ? and ? " +
								" and uin=? ",new Object[]{b,e,Long.valueOf(obj.get("id")+"")});
						if(cash!=null&&cash.get("total")!=null){
							double cmoney = Double.valueOf(cash.get("total")+"");
							cashmoney+=cmoney;
							Map tmap = new TreeMap();
							tmap.put("nickname",obj.get("nickname"));
							tmap.put("total",0.0);
							tmap.put("uid",obj.get("id"));
							tmap.put("cash",StringUtils.formatDouble(cmoney ));//�շ�Ա�ֽ�����
							park.add(tmap);
						}
					}
				}
				total=cashmoney+mmoney;
				String detail = StringUtils.createJson(park);
				ret+="\"total\":\""+StringUtils.formatDouble(total)+"\",\"mmoeny\":\""+StringUtils.formatDouble(mmoney)+
				"\",\"cashmoney\":\""+StringUtils.formatDouble(cashmoney)+"\",\"detail\":"+detail+"}";
			}else{
				//��û��Ȩ�޲鿴
				AjaxUtil.ajaxOutput(response, "-1");
			}
			logger.info("getparkdetail>>>>��"+ret);
			AjaxUtil.ajaxOutput(response, ret);
			return null;
		}else if(action.equals("countprice")){
			Long btime = RequestUtil.getLong(request, "btime", -1L);
			Long etime = RequestUtil.getLong(request, "etime", -1L);
			Map<String,Object> info = new HashMap<String,Object>();
			String ret = publicMethods.getPrice(btime, etime, comId, 0);
			info.put("total", ret);
			ret = StringUtils.createJson(info);
			AjaxUtil.ajaxOutput(response, ret);
			return null;
		}else if(action.equals("rewardscore")){
			Double remainscore = 0d;//ʣ�����
			Long rank = 0L;//���а�
			Double todayscore = 0d;//���ջ���
			Long btime = TimeTools.getToDayBeginTime();
			Map<String, Object> scoreMap = daService
					.getMap("select reward_score from user_info_tb where id=? ",
							new Object[] { uin });
			if(scoreMap != null){
				remainscore = Double.valueOf(scoreMap.get("reward_score") + "");
			}
			Long scoreCount = daService.getLong("select count(*) from reward_account_tb where create_time> ? and type=? and uin=? ",
							new Object[] { btime, 0, uin });
			if(scoreCount > 0){
				List<Map<String, Object>> scoreList = daService
						.getAll("select uin,sum(score) score from reward_account_tb where create_time> ? and type=? group by uin order by score desc ",
								new Object[] { btime, 0 });
				for(Map<String, Object> map : scoreList){
					Long uid = (Long)map.get("uin");
					rank++;
					if(uid.intValue() == uin.intValue()){
						todayscore = Double.valueOf(map.get("score") + "");
						break;
					}
				}
			}
			infoMap.put("todayscore", todayscore);
			infoMap.put("rank", rank);
			infoMap.put("remainscore", remainscore);
			infoMap.put("ticketurl", "http://mp.weixin.qq.com/s?__biz=MzA4MTAxMzA2Mg==&mid=208427604&idx=1&sn=a3de34b678869c4bbe54547396fcb2a3#rd");
			infoMap.put("scoreurl", "http://mp.weixin.qq.com/s?__biz=MzA4MTAxMzA2Mg==&mid=208445618&idx=1&sn=b4d99d5233921ae53c847165c62dec2b#rd");
			AjaxUtil.ajaxOutput(response,StringUtils.createJson(infoMap));
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=rewardscore&token=5286f078c6d2ecde9b30929f77771149
		}else if(action.equals("rscorerank")){//�������а�
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "size", 20);
			Long btime = TimeTools.getToDayBeginTime();
			List<Object> params = new ArrayList<Object>();
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			String sql = "select uin,sum(score) score from reward_account_tb where create_time>=? and type=? group by uin order by score desc ";
			String countsql = "select count(distinct uin) from reward_account_tb where create_time>=? and type=? ";
			params.add(btime);
			params.add(0);
			Long total = daService.getCount(countsql, params);
			if(total > 0){
				list = daService.getAll(sql, params, pageNum, pageSize);
				setinfo(list, pageNum, pageSize);
			}
			String result = "{\"count\":"+total+",\"info\":"+StringUtils.createJson(list)+"}";
			AjaxUtil.ajaxOutput(response,result);
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=rscorerank&token=5286f078c6d2ecde9b30929f77771149
		}else if(action.equals("rewardrank")){
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "size", 20);
			List<Object> params = new ArrayList<Object>();
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			String monday = StringUtils.getMondayOfThisWeek();
			Long btime = TimeTools.getLongMilliSecondFrom_HHMMDD(monday)/1000;
			String sql = "select uid uin,sum(money) money from parkuser_reward_tb where ctime>=? group by uid order by money desc ";
			String countsql = "select count(distinct uid) from parkuser_reward_tb where ctime>=? ";
			params.add(btime);
			Long total = daService.getCount(countsql, params);
			if(total > 0){
				list = daService.getAll(sql, params, pageNum, pageSize);
				setinfo(list, pageNum, pageSize);
			}
			String result = "{\"count\":"+total+",\"info\":"+StringUtils.createJson(list)+"}";
			AjaxUtil.ajaxOutput(response,result);
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=rewardrank&token=5286f078c6d2ecde9b30929f77771149
		}else if(action.equals("bonusinfo")){
			String bonusinfo = CustomDefind.SENDTICKET;
			JSONArray jsonArray = JSONArray.fromObject(bonusinfo);
			for(int i=0; i<jsonArray.size(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				int type = jsonObject.getInt("type");
				int bmoney = jsonObject.getInt("bmoney");
				double score = jsonObject.getDouble("score");
				if(type == 1 && bmoney == 5){
					Long btime = TimeTools.getToDayBeginTime();
					Long count = daService.getLong("select count(*) from reward_account_tb r,ticket_tb t where r.ticket_id=t.id and r.type=? and r.target=? and r.create_time>? and t.money=? and r.uin=? ",
									new Object[] { 1, 2, btime, 5, uin });
					score = score * (count + 1);
					logger.info("���շ�����Ԫȯ����count:"+count+",uid:"+uin+",today:"+btime+",��һ�����ѻ��֣�score��"+score);
					jsonObject.put("score", score);
					if(count >=10){
						jsonObject.put("limit", 1);
					}else{
						jsonObject.put("limit", 0);
					}
					break;
				}
			}
			logger.info("bonusinfo:"+jsonArray.toString()+",uin:"+uin);
			AjaxUtil.ajaxOutput(response,jsonArray.toString());
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=bonusinfo&token=67579fd93b96ad32ced2584b54d8454f
		}else if(action.equals("sendticket")){
			Integer bmoney = RequestUtil.getInteger(request, "bmoney", 0);//���
			Double score = RequestUtil.getDouble(request, "score", 0d);//���Ļ���
			String uins = RequestUtil.processParams(request, "uins");//�����˺�
			logger.info("sendticket>>>�շ�Ա:"+uin+",bmoney:"+bmoney+",score:"+score+",uins:"+uins);
			String ids[] = uins.split(",");
			if(ids.length == 0 || uins.length() == 0){
				AjaxUtil.ajaxOutput(response, "-2");//δѡ����
				return null;
			}
			Long ctime = System.currentTimeMillis()/1000;
			Map<String, Object> userMap = daService.getMap(
					"select id,nickname,reward_score from user_info_tb where id=? ",
					new Object[] { uin });
			Map<String, Object> comMap = daService.getMap(
					"select company_name from com_info_tb where id=? ",
					new Object[] { comId });
			List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
			//�����˻�
			Map<String, Object> scoreSqlMap = new HashMap<String, Object>();
			Long exptime = ctime + 24*60*60;
			for(int i = 0; i<ids.length; i++){
				//дȯ
				Map<String, Object> ticketSqlMap = new HashMap<String, Object>();
				//������ϸ
				Map<String, Object> scoreAccountSqlMap = new HashMap<String, Object>();
				
				Long cuin = Long.valueOf(ids[i]);
				String carNumber = publicMethods.getCarNumber(cuin);
				Long ticketId = daService.getkey("seq_ticket_tb");
				
				ticketSqlMap.put("sql", "insert into ticket_tb (id,create_time,limit_day,money,state,uin,type,comid) values(?,?,?,?,?,?,?,?)");
				ticketSqlMap.put("values", new Object[]{ticketId,TimeTools.getToDayBeginTime(),TimeTools.getToDayBeginTime()+16*24*60*60-1,bmoney,0,cuin,1,comId});
				bathSql.add(ticketSqlMap);
				
				scoreAccountSqlMap.put("sql", "insert into reward_account_tb (uin,score,type,create_time,remark,target,ticket_id) values(?,?,?,?,?,?,?)");
				scoreAccountSqlMap.put("values", new Object[]{uin,score,1,ctime,"ͣ��ȯ "+carNumber,2,ticketId});
				bathSql.add(scoreAccountSqlMap);
			}
			Double allscore = StringUtils.formatDouble(score * ids.length);
			logger.info("sendticket>>>�շ�Ա:"+uin+",allscore:"+allscore);
			Double reward_score = Double.valueOf(userMap.get("reward_score") + "");
			if(reward_score < allscore){
				AjaxUtil.ajaxOutput(response, "-3");//���ֲ���
				logger.info("sendticket>>>���ͻ��ֲ��㣬�շ�Ա:"+uin+",allscore:"+allscore+",reward_score:"+reward_score);
				return null;
			}
			if(allscore > 0 && bathSql.size() > 0){
				scoreSqlMap.put("sql", "update user_info_tb set reward_score=reward_score-? where id=? ");
				scoreSqlMap.put("values", new Object[]{allscore, uin});
				bathSql.add(scoreSqlMap);
			}
			boolean b = daService.bathUpdate(bathSql);
			logger.info("sendticket>>>�շ�Ա��"+uin+",b:"+b);
			if(b){
				for(int i = 0;i<ids.length; i++){
					Long cuin = Long.valueOf(ids[i]);
					logService.insertUserMesg(5, cuin,"����ͣ�����շ�Ա" + userMap.get("nickname") + "����������"
									+ bmoney + "Ԫ" + comMap.get("company_name")
									+ "ר��ȯ���������ҳ���ͣ����", "ͣ��ȯ����");
				}
				sendWXMsg(ids, userMap, comMap, bmoney);
				AjaxUtil.ajaxOutput(response, "1");
			}else{
				AjaxUtil.ajaxOutput(response, "-1");
			}
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=sendticket&token=5286f078c6d2ecde9b30929f77771149&bmoney=3&score=1&uins=21616,21577,21554
		}else if(action.equals("sendbonus")){
			Integer bmoney = RequestUtil.getInteger(request, "bmoney", 0);//���
			Integer bnum = RequestUtil.getInteger(request, "bnum", 0);//����
			Double score = RequestUtil.getDouble(request, "score", 0d);//���Ļ���
			logger.info("sendbonus>>>�շ�Ա��"+uin+",bmoney:"+bmoney+",bnum:"+bnum+",score:"+score);
			Map<String, Object> comMap = daService.getMap("select company_name from com_info_tb where id=? ", new Object[]{comId});
			Map<String, Object> userMap = daService.getMap(
					"select id,nickname,reward_score from user_info_tb where id=? ",
					new Object[] { uin });
			Double reward_score = Double.valueOf(userMap.get("reward_score") + "");
			if(reward_score < score){
				infoMap.put("result", -3);
				AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));//���ֲ���
				logger.info("sendticket>>>���ͻ��ֲ��㣬�շ�Ա:"+uin+",score:"+score+",reward_score:"+reward_score);
				return null;
			}
			Long ctime = System.currentTimeMillis()/1000;
			Long exptime = ctime + 24*60*60;
			Long bonusId = daService.getkey("seq_order_ticket_tb");
			int result = daService.update("insert into order_ticket_tb (id,uin,order_id,money,bnum,ctime,exptime,bwords,type) values(?,?,?,?,?,?,?,?,?)",
							new Object[] { bonusId, uin, -1, bmoney, bnum, ctime, exptime, "ף��һ·������!", 2 });
			logger.info("sendbonus>>>:�շ�Ա"+uin+",result:"+result);
			if(result == 1){
				infoMap.put("result", 1);
				infoMap.put("bonusid", bonusId);
				infoMap.put("cname", comMap.get("company_name"));
			}else{
				infoMap.put("result", -1);
			}
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=sendbonus&token=5286f078c6d2ecde9b30929f77771149&bmoney=12&bnum=8&score=1
		}else if(action.equals("sendsuccess")){
			Long bonusId = RequestUtil.getLong(request, "bonusid", -1L);
			Double score = RequestUtil.getDouble(request, "score", 15d);
			Long ctime = System.currentTimeMillis()/1000;
			logger.info("sendsuccess>>>������ͳɹ��ص�:bonusid:"+bonusId+",uin:"+uin+",score:"+score);
			if(bonusId != -1){
				Long count = daService.getLong("select count(*) from reward_account_tb where orderticket_id=? ", new Object[]{bonusId});
				logger.info("sendsuccess>>>������ͳɹ��ص�:bonusid:"+bonusId+",uin:"+uin+",count:"+count+",score:"+score);
				if(count == 0){
					Map<String, Object> userMap = daService.getMap(
							"select id,nickname,reward_score from user_info_tb where id=? ",
							new Object[] { uin });
					Double reward_score = Double.valueOf(userMap.get("reward_score") + "");
					logger.info("sendsuccess>>>������ͳɹ��ص�:bonusid:"+bonusId+",uin:"+uin+",score:"+score+",ʣ�����reward_score:"+reward_score+",�˴����Ļ���score:"+score);
					if(reward_score > score){
						List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
						//�����˻�
						Map<String, Object> scoreSqlMap = new HashMap<String, Object>();
						//������ϸ
						Map<String, Object> scoreAccountSqlMap = new HashMap<String, Object>();
						
						scoreAccountSqlMap.put("sql", "insert into reward_account_tb (uin,score,type,create_time,remark,target,orderticket_id) values(?,?,?,?,?,?,?)");
						scoreAccountSqlMap.put("values", new Object[]{uin,score,1,ctime,"��� ",1,bonusId});
						bathSql.add(scoreAccountSqlMap);
						
						scoreSqlMap.put("sql", "update user_info_tb set reward_score=reward_score-? where id=? ");
						scoreSqlMap.put("values", new Object[]{score, uin});
						bathSql.add(scoreSqlMap);
						boolean b = daService.bathUpdate(bathSql);
						logger.info("sendsuccess>>>������ͳɹ��ص�:bonusid:"+bonusId+",uin:"+uin+",b:"+b);
						if(b){
							AjaxUtil.ajaxOutput(response, "1");
							return null;
						}
					}
				}
			}
			AjaxUtil.ajaxOutput(response, "-1");
			return null;
		}else if(action.equals("rewardlist")){
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "size", 20);
			List<Object> params = new ArrayList<Object>();
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			Long btime = TimeTools.getToDayBeginTime() - 6 * 24 * 60 * 60;
			params.add(uin);
			params.add(btime);
			String sql = "select uin,count(*) rcount,sum(money) rmoney from parkuser_reward_tb where uid=? and ctime>? group by uin order by rcount desc";
			String countsql = "select count(distinct uin) from parkuser_reward_tb where uid=? and ctime>? ";
			Long count = daService.getCount(countsql, params);
			if(count > 0){
				list = daService.getAll(sql, params, pageNum, pageSize);
				setCarNumber(list);
			}
			String result = "{\"count\":"+count+",\"info\":"+StringUtils.createJson(list)+"}";
			AjaxUtil.ajaxOutput(response,result);
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=rewardlist&token=116a87809926db5c477a9a1a58488ec1
		}else if(action.equals("parkinglist")){
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "size", 20);
			Long btime = TimeTools.getToDayBeginTime() - 6* 24 * 60 *60;
			List<Object> params = new ArrayList<Object>();
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			String sql = "select uin,count(*) pcount from order_tb where state=? and uid=? and end_time>? and uin is not null group by uin order by pcount desc";
			String countsql = "select count(distinct uin) from order_tb where state=? and uid=? and end_time>? and uin is not null ";
			params.add(1);
			params.add(uin);
			params.add(btime);
			Long count = daService.getCount(countsql, params);
			if(count > 0){
				list = daService.getAll(sql, params, pageNum, pageSize);
				setCarNumber(list);
			}
			String result = "{\"count\":"+count+",\"info\":"+StringUtils.createJson(list)+"}";
			AjaxUtil.ajaxOutput(response,result);
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=parkinglist&token=116a87809926db5c477a9a1a58488ec1
		}else if(action.equals("sweepticket")){
			Double score = RequestUtil.getDouble(request, "score", 0d);//���Ļ���
			Integer bmoney = RequestUtil.getInteger(request, "bmoney", 0);//���
			Long ticketId = daService.getkey("seq_ticket_tb");
			logger.info("sweepticket>>>�շ�Ա��"+uin+",score:"+score+",bmoney:"+bmoney+",ticketId:"+ticketId);
			Map<String, Object> userMap = daService.getMap(
					"select id,nickname,reward_score from user_info_tb where id=? ",
					new Object[] { uin });
			Map<String, Object> comMap = daService.getMap("select company_name from com_info_tb where id=? ", new Object[]{comId});
			Double reward_score = Double.valueOf(userMap.get("reward_score") + "");
			if(reward_score < score){
				infoMap.put("result", -3);
				AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));//���ֲ���
				logger.info("sendticket>>>���ͻ��ֲ��㣬�շ�Ա:"+uin+",score:"+score+",reward_score:"+reward_score);
				return null;
			}
			Long ctime = System.currentTimeMillis()/1000;
			String code = null;
			Long ticketids[] = new Long[]{ticketId};
			String []codes = StringUtils.getGRCode(ticketids);
			if(codes.length > 0){
				code = codes[0];
			}
			logger.info("sweepticket>>>�շ�Ա��"+uin+",code:"+code);
			List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
			//��ά��
			Map<String, Object> codeSqlMap = new HashMap<String, Object>();
			
			Map<String, Object> ticketSqlMap = new HashMap<String, Object>();
			
			codeSqlMap.put("sql", "insert into qr_code_tb(comid,uid,ctime,type,state,code,isuse,ticketid,score) values(?,?,?,?,?,?,?,?,?)");
			codeSqlMap.put("values", new Object[] { comId, uin, ctime, 6, 0, code, 1, ticketId, score });
			bathSql.add(codeSqlMap);
			
			ticketSqlMap.put("sql", "insert into ticket_tb(id,create_time,limit_day,money,state,comid,type) values(?,?,?,?,?,?,?)");
			ticketSqlMap.put("values", new Object[] {ticketId, TimeTools.getToDayBeginTime(),TimeTools.getToDayBeginTime()+16*24*60*60-1, bmoney, 0, comId, 1});
			bathSql.add(ticketSqlMap);
			
			boolean b = daService.bathUpdate(bathSql);
			logger.info("sweepticket>>>�շ�Ա��"+uin+",ticketId:"+ticketId+",code:"+code+",b:"+b);
			if(b){
				String url = "http://"+Constants.WXPUBLIC_S_DOMAIN+"/zld/qr/c/"+code;
				infoMap.put("result", 1);
				infoMap.put("code", url);
				infoMap.put("ticketid", ticketId);
				infoMap.put("cname", comMap.get("company_name"));
			}else{
				infoMap.put("result", -1);
			}
			AjaxUtil.ajaxOutput(response,StringUtils.createJson(infoMap));
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=sweepticket&bmoney=3&score=1&token=5286f078c6d2ecde9b30929f77771149
		}else if(action.equals("deductscore")){//��΢���﷢�ͣ��û������ȡ
			Double score = RequestUtil.getDouble(request, "score", 0d);//���Ļ���
			Long ticketid = RequestUtil.getLong(request, "ticketid", -1L);
			logger.info("ticketid:"+ticketid+",score:"+ticketid+",uin:"+uin);
			if(score == 0 || ticketid == -1){
				infoMap.put("result", -1);
			}
			Map<String, Object> userMap = daService.getMap(
					"select id,nickname,reward_score from user_info_tb where id=? ",
					new Object[] { uin });
			Map<String, Object> comMap = daService.getMap("select company_name from com_info_tb where id=? ", new Object[]{comId});
			Double reward_score = Double.valueOf(userMap.get("reward_score") + "");
			if(reward_score < score){
				infoMap.put("result", -3);
				AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));//���ֲ���
				logger.info("deductscore>>>���ͻ��ֲ��㣬�շ�Ա:"+uin+",score:"+score+",reward_score:"+reward_score);
				return null;
			}
			List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
			
			Map<String, Object> scoreSqlMap = new HashMap<String, Object>();
			//������ϸ
			Map<String, Object> scoreAccountSqlMap = new HashMap<String, Object>();
			
			scoreAccountSqlMap.put("sql", "insert into reward_account_tb (uin,score,type,create_time,remark,target,ticket_id) values(?,?,?,?,?,?,?)");
			scoreAccountSqlMap.put("values", new Object[]{uin,score,1,System.currentTimeMillis()/1000,"ͣ��ȯ �û�΢�ŵ����ȡ",2,ticketid});
			bathSql.add(scoreAccountSqlMap);
			
			scoreSqlMap.put("sql", "update user_info_tb set reward_score=reward_score-? where id=? ");
			scoreSqlMap.put("values", new Object[]{score, uin});
			bathSql.add(scoreSqlMap);
			
			boolean b = daService.bathUpdate(bathSql);
			logger.info("uin:"+uin+",b:"+b);
			if(b){
				infoMap.put("result", 1);
			}
			AjaxUtil.ajaxOutput(response,StringUtils.createJson(infoMap));
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=deductscore&score=1&ticketid=&token=
		}else if(action.equals("todayaccount")){//�����˻������֡����Ͳ�ѯ
			Long b = TimeTools.getToDayBeginTime();
			Long e = System.currentTimeMillis()/1000+60;
			Double parkmoney = 0d;
			Double parkusermoney = 0d;
//			Double cashmoney = 0d;
			Double rewardmoney = 0d;
			Double todayscore = 0d;//ʣ�����
			Long todayin = 0L;//�����볡����
			Long todayout = 0L;//���ճ�������
			//һ������   1:��������Ա    2�շ�Ա
			Map park = daService.getMap( "select sum(amount) total from park_account_tb where create_time between ? and ? " +
					" and type <> ? and uid=? and comid=? ",new Object[]{b,e,1,uin,comId});
			if(park!=null&&park.get("total")!=null){
				parkmoney = Double.valueOf(park.get("total")+"");//�����˻����루������Դ��
			}
			
			Map parkuser = daService.getMap( "select sum(amount) total from parkuser_account_tb where create_time between ? and ? " +
					" and type= ? and uin = ? ",new Object[]{b,e,0,uin});
			if(parkuser!=null&&parkuser.get("total")!=null){
				parkusermoney = Double.valueOf(parkuser.get("total")+"");//�շ�Ա�˻����루������Դ��
			}
			
			/*Map cash = daService.getMap( "select sum(amount) total from parkuser_cash_tb where create_time between ? and ? " +
					" and uin=? ",new Object[]{b,e,uin});
			if(cash!=null&&cash.get("total")!=null){
				cashmoney = Double.valueOf(cash.get("total")+"");//�շ�Ա�ֽ�����
			}*/
			
			Map reward = daService.getMap("select sum(money) total from parkuser_reward_tb where ctime between ? and ? and uid=? ",
							new Object[] { b, e, uin });
			if(reward != null && reward.get("total") != null){
				rewardmoney = Double.valueOf(reward.get("total") + "");
			}
			
			Map score = daService.getMap("select reward_score from user_info_tb where id=? ", new Object[] { uin });
			if(score != null && score.get("reward_score") != null){
				todayscore = Double.valueOf(score.get("reward_score") + "");
			}
			
			todayin = daService.getLong("select count(1) from order_tb where comid=? " +
					"and create_time between ? and ?", new Object[]{comId,b,e});
			
			todayout = daService.getLong("select count(1) from order_tb where comid=? and state=? " +
					"and end_time between ? and ?", new Object[]{comId,1,b,e});
			
			infoMap.put("mobilemoney", StringUtils.formatDouble(parkmoney + parkusermoney));
			infoMap.put("rewardmoney", StringUtils.formatDouble(rewardmoney));
			infoMap.put("todayscore", StringUtils.formatDouble(todayscore));
			infoMap.put("todayin", todayin);
			infoMap.put("todayout", todayout);
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=todayaccount&token=5286f078c6d2ecde9b30929f77771149
		}else if(action.equals("remainscore")){
			Double todayscore = 0d;//ʣ�����
			Map score = daService.getMap("select reward_score from user_info_tb where id=? ", new Object[] { uin });
			if(score != null && score.get("reward_score") != null){
				todayscore = Double.valueOf(score.get("reward_score") + "");
			}
			infoMap.put("score", todayscore);
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
			return null;
		}else if(action.equals("queryaccount")){//���ݳ��Ʋ�ѯ�ڸó������˻���ϸ
			String carnumber = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "carnumber"));
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "size", 20);
			if(carnumber.equals("")){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			Long ntime = System.currentTimeMillis()/1000;
			Map<String, Object> carMap = pService.getMap(
					"select uin from car_info_tb where car_number=? ",
					new Object[] { carnumber });
			if(carMap == null || carMap.get("uin") == null){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			List<Map<String, Object>> carList = pService
					.getAll("select car_number from car_info_tb where uin=? and state=? ",
							new Object[] { carMap.get("uin"), 1 });
			String cnum = "�ó�����"+carList.size()+"������:/n";
			for(int i = 0; i<carList.size(); i++){
				Map<String, Object> map = carList.get(i);
				if(i == 0){
					cnum += map.get("car_number");
				}else{
					cnum += "," + map.get("car_number");
				}
			}
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			List<Object> params = new ArrayList<Object>();
			String sql = "select a.*,o.car_number carnumber from parkuser_account_tb a,order_tb o where a.orderid=o.id and o.uid=? and o.uin=? and a.type=? and a.create_time between ? and ? order by a.create_time desc";
			String sqlcount = "select count(a.*) from parkuser_account_tb a,order_tb o where a.orderid=o.id and o.uid=? and o.uin=? and a.type=? and a.create_time between ? and ? ";
			params.add(uin);
			params.add(carMap.get("uin"));
			params.add(0);
			params.add(ntime - 30*24*60*60);
			params.add(ntime);
			Long count = pService.getCount(sqlcount, params);
			if(count > 0){
				list = pService.getAll(sql, params, pageNum, pageSize);
				setRemark(list);
			}
			String reslut =  "{\"count\":"+count+",\"carinfo\":\""+cnum+"\",\"info\":"+StringUtils.createJson(list)+"}";
			AjaxUtil.ajaxOutput(response, reslut);
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=queryaccount&token=2dd4b1b320225dfd4fc44ad6b53fa734&carnumber=��QLL122
		}else if(action.equals("posincome")){//pos�����ɶ���
			AjaxUtil.ajaxOutput(response, posIncome(request,comId,uin));
			//http://127.0.0.1/zld/collectorrequest.do?action=posincome&token=2dd4b1b320225dfd4fc44ad6b53fa734&carnumber=��QLL122
			
		}else if(action.equals("liftrodrecord")){//̧�˼�¼
			String result = liftRod(request,uin,comId);
			AjaxUtil.ajaxOutput(response, result);
			//http://127.0.0.1/zld/collectorrequest.do?action=liftrodrecord&token=d481a6fb58e758c3f0ef9aa7c4bdff29&passid=13
		}else if(action.equals("liftrodreason")){//̧�˼�¼������ԭ��
			String result = liftRodReason(request);
			AjaxUtil.ajaxOutput(response, result);
			//http://127.0.0.1/zld/collectorrequest.do?action=liftrodreason&token=d481a6fb58e758c3f0ef9aa7c4bdff29&lrid=3&reason=1
		}else if(action.equals("liftroduppic")){//̧�˼�¼���ϴ�ͼƬ
			String result = liftRodPic(request);
			AjaxUtil.ajaxOutput(response, result);
			//http://127.0.0.1/zld/collectorrequest.do?action=liftroduppic&token=a0b952263fbb0a264194a1443c71174d&lrid=3
		}
		
		if(out.equals("json")){
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
		}else
			AjaxUtil.ajaxOutput(response, StringUtils.createXML(infoMap));
		return null;
	}
	
	private String liftRodReason(HttpServletRequest request) {
		Integer reason = RequestUtil.getInteger(request, "reason", -1);
		Long lrid = RequestUtil.getLong(request, "lrid", -1L);
		if(lrid==-1){
			return  "{result:-1,errmsg:\"�������Ϊ�գ�\"}";
		}
		String sql = "update lift_rod_tb set reason=? where id=?";
		int ret = daService.update(sql, new Object[]{reason,lrid});
		logger.info(">>>>>>>>>>lrid:"+lrid+",reason:"+reason+",update lift_rod_tb,ret:"+ret);
		return  "{result:\""+ret+"\",errmsg:\"�����ɹ���\"}";
	}

	private String liftRod(HttpServletRequest request, Long uin, Long comId) {
		Long key = daService.getkey("seq_lift_rod_tb");
		Long pass_id = RequestUtil.getLong(request, "passid", -1L);//���ͨ��id
		Integer reason = RequestUtil.getInteger(request, "reason", -1);
		String sql = "insert into lift_rod_tb (id,comid,uin,ctime,pass_id,reason) values(?,?,?,?,?,?)";
		int ret = daService.update(sql, new Object[]{key,comId,uin,System.currentTimeMillis()/1000,pass_id,reason});
		logger.info(">>>>>>>>>>"+comId+","+uin+",upload lift rod,insert into db ret:"+ret);
		if(ret==1)
			return  "{\"result\":\""+ret+"\",\"errmsg\":\"�����ɹ���\",lrid:\""+key+"\"}";
		else {
			return  "{\"result\":\""+ret+"\",\"errmsg\":\"����ʧ�ܣ�\"}";
		}
	}

	//�ϴ�̧�˼�¼
	private String liftRodPic(HttpServletRequest request) throws Exception{
		Long ntime = System.currentTimeMillis()/1000;
		Long lrid = RequestUtil.getLong(request, "lrid", -1L);
		logger.info("begin upload lift rod picture....lrid:"+lrid);
		Map<String, String> extMap = new HashMap<String, String>();
	    extMap.put(".jpg", "image/jpeg");
	    extMap.put(".jpeg", "image/jpeg");
	    extMap.put(".png", "image/png");
	    extMap.put(".gif", "image/gif");
	    extMap.put(".webp", "image/webp");
		if(lrid==-1){
			return  "{\"result\":\"-1\",\"errmsg\":\"�������Ϊ�գ�\"}";
		}
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
			return "{\"result\":\"1\",\"errmsg\":\"���Ʊ���ɹ���\"}";
		}
		String filename = ""; // �ϴ��ļ����浽���������ļ���
		InputStream is = null; // ��ǰ�ϴ��ļ���InputStream����
		FileOutputStream outer = null;
		// ѭ�������ϴ��ļ�
		for (FileItem item : items){
			// ������ͨ�ı���
			if (!item.isFormField()){
				// �ӿͻ��˷��͹������ϴ��ļ�·���н�ȡ�ļ���
				filename = item.getName().substring(item.getName().lastIndexOf("\\")+1);
				is = item.getInputStream(); // �õ��ϴ��ļ���InputStream����
				logger.info("filename:"+item.getName()+",stream:"+is);
			}else{
				continue;
			}
			String file_ext =filename.substring(filename.lastIndexOf(".")).toLowerCase();// ��չ��
			String picurl = lrid + "_"+ System.currentTimeMillis()/1000 + file_ext;
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
				
				 String f = CustomDefind.PIC+getCollectionName(System.currentTimeMillis());
			 	    File file = new File(f);
			 	    if(!file.exists()){
			 	    	file.mkdirs();
			 	    }
			 	    String fileName = f+"\\"+picurl;
			 	    outer = new FileOutputStream(fileName);  
			        outer.write(content);  
			        outer.close(); 
				 
			        
			        
//				DB mydb = MongoClientFactory.getInstance().getMongoDBBuilder("zld");
//				mydb.requestStart();
//				
//				DBCollection collection = mydb.getCollection("lift_rod_pics");
//				//  DBCollection collection = mydb.getCollection("records_test");
//				
//				BasicDBObject document = new BasicDBObject();
//				document.put("lrid", lrid);
//				document.put("ctime", ntime);
//				document.put("type", extMap.get(file_ext));
//				document.put("content", content);
//				document.put("filename", picurl);
//				//��ʼ����
//				//��������
//				mydb.requestStart();
//				collection.insert(document);
//				//��������
//				mydb.requestDone();
				in.close();        
				is.close();
				byteout.close();
				String sql = "update lift_rod_tb set img=?,sync_state = ? where id =?";
				int ret = daService.update(sql, new Object[]{picurl,0,lrid});
				logger.info(">>>>>>>>>>orderId:"+lrid+",filename:"+picurl+", update lift_rod_tb, ret:"+ret);
			} catch (Exception e) {
				e.printStackTrace();
				return "{\"result\":\"0\",\"errmsg\":\"ͼƬ�ϴ�ʧ�ܣ�\"}";
			}finally{
				if(outer!=null)
					outer.close();
				if(in!=null)
					in.close();
				if(byteout!=null)
					byteout.close();
				if(is!=null)
					is.close();
			}
		}
		return "{\"result\":\"1\",\"errmsg\":\"�ϴ��ɹ���\"}";
	}
	public static String getCollectionName(Long milliSeconds) {
		String date =TimeTools.getTimeStr_yyyy_MM_dd(milliSeconds);
		String[] strdate = date.split("-");
//		int str = (Integer.parseInt(strdate[2]))/3;
		return strdate[0]+strdate[1]+strdate[2];
	}
	//pos�����ɶ���
	private String posIncome(HttpServletRequest request,Long comId,Long uid) {
		String carNumber=AjaxUtil.decodeUTF8(RequestUtil.getString(request, "carnumber"));
		Long uin =-1L;
		if(!carNumber.equals("")){//������ĳ��ƺ�
			Map carMap = daService.getMap("select * from car_info_tb where car_number=? and state=? ", new Object[]{carNumber,1});
			if(carMap!=null&&carMap.get("uin")!=null)
				uin = (Long)carMap.get("uin");
		}else {
			carNumber=null;
		}
		Long ctime = System.currentTimeMillis()/1000;
		String btime = TimeTools.getTime_MMdd_HHmm(ctime*1000);
		String imei  =  RequestUtil.getString(request, "imei");
		String codes[] = StringUtils.getGRCode(new Long[]{123456L});
		Long orderId = daService.getkey("seq_order_tb");
		logger.info("posaddorder,uid:"+uid+",comid:"+comId+",carnumber:"+carNumber+",uuid:"+codes[0]);
		int result = daService.update("insert into order_tb (id,comid,uin,state,create_time,nfc_uuid,c_type,uid,imei,car_number) " +
				"values(?,?,?,?,?,?,?,?,?,?)",new Object[]{orderId,comId,uin,0,ctime,codes[0],0,uid,imei,carNumber});
		if(result==1){
			return "{\"result\":\"1\",\"errmsg\":\"�����ɹ������ڴ�ӡ����ƾ��...\",\"qrcode\":\"qr/c/"+codes[0]+"\",\"orderid\":\""+orderId+"\",\"btime\":\""+btime+"\"}";
		}
		return "{\"result\":\"0\",\"errmsg\":\"�������������²���\",\"qrcode\":\"\",\"orderid\":\"\"}";
	}


	private String currOrders(HttpServletRequest request,Long uin,Long comId,String out,
			Map<String, Object> infoMap) {
		Integer pageNum = RequestUtil.getInteger(request, "page", 1);
		Integer pageSize = RequestUtil.getInteger(request, "size", 20);
		List<Object> params = new ArrayList<Object>();
		params.add(0);
		params.add(comId);
		Long _total = daService.getLong("select count(*) from order_tb where state=? and comid=? ", 
				new Object[]{0,comId});
		//��ͣ������
		List<Map<String,Object>> list = daService.getAll("select * from order_tb where state=? and comid=? order by id desc ",//and create_time>?",
				params, pageNum, pageSize);
		
		//�鲴������
		List<Map<String,Object>> csList =null;// daService.getAll("select c.id,c.state,c.buid,c.euid,c.car_number,c.btime,c.start_time,t.next_price,t.max_price  " +
				//"from carstop_order_tb c left join car_stops_tb t on c.cid = t.id where (c.buid=? and c.state in(?,?)) or (c.euid=? and c.state in(?,?)) ",
				//new Object[]{uin,1,2,uin,5,6});
		
		
		//logger.info("currentorder:"+_total);
		List<Map<String, Object>> infoMaps = new ArrayList<Map<String,Object>>();
		Double ptotal = 0d;
		Long end=System.currentTimeMillis()/1000;
		if(list!=null&&list.size()>0){
			for(Map map : list){
				Map<String, Object> info = new HashMap<String, Object>();
				Long uid = (Long)map.get("uin");
				String carNumber = "���ƺ�δ֪";
				if(map.get("car_number")!=null&&!"".equals((String)map.get("car_number"))){
					carNumber = (String)map.get("car_number");
				}else {
					if(uid!=-1){
						carNumber = publicMethods.getCarNumber(uid);
					}
				}
				info.put("carnumber", carNumber);
				Long start= (Long)map.get("create_time");
				
				Integer pid = (Integer)map.get("pid");
				Integer car_type = (Integer)map.get("car_type");//0��ͨ�ã�1��С����2����
				end = System.currentTimeMillis()/1000;
				if(pid>-1){
					info.put("total",publicMethods.getCustomPrice(start, end, pid));
				}else {
					info.put("total",publicMethods.getPrice(start, end, comId, car_type));	
				}
				info.put("id", map.get("id"));
				info.put("type", "order");
				info.put("state","-1");
				info.put("duration", "��ͣ "+StringUtils.getTimeString(start,end));
				info.put("btime", TimeTools.getTime_yyyyMMdd_HHmm(start*1000));
				infoMaps.add(info);
			}
		}
		
		if(csList!=null&&!csList.isEmpty()){
			for(Map<String, Object> map : csList){
				Map<String, Object> info = new HashMap<String, Object>();
				info.put("id", map.get("id"));
				Long start = (Long)map.get("btime");
				Integer state = (Integer)map.get("state");
				if(state>2){
					info.put("duration", "��ͣ "+StringUtils.getTimeString(start,end));
					info.put("btime", TimeTools.getTime_yyyyMMdd_HHmm(start*1000));
					Double nprice = Double.valueOf(map.get("next_price")+"");//ʱ��
					Object tp = map.get("max_price");//��߼�
					Double tprice =-1d;
					if(tp!=null)
						tprice = Double.valueOf(tp.toString());
					Long h = StringUtils.getHour(start, end);
					Double total = StringUtils.formatDouble(h*nprice);
					if(tprice!=-1&&total>tprice)
						total = tprice;
					info.put("total",total);
				}else {
					start = (Long)map.get("start_time");
					info.put("total","0.0");
					info.put("btime",TimeTools.getTime_yyyyMMdd_HHmm(start*1000));
					info.put("duration","���ڽӳ�");
				}
				info.put("carnumber", map.get("car_number"));
				info.put("state", map.get("state"));
				infoMaps.add(info);
			}
		}
		Collections.sort(infoMaps,new OrderSortCompare());
		
		String result = "";
		ptotal = StringUtils.formatDouble(ptotal);
		if(out.equals("json")){
			result = "{\"count\":"+_total+",\"price\":"+ptotal+",\"info\":"+StringUtils.createJson(infoMaps)+"}";
		}else {
			result = StringUtils.createXML(infoMaps,_total);
		}
		return result;
	}


	private void orderDetail(HttpServletRequest request,Long comId,
			Map<String, Object> infoMap) {

		Long orderId = RequestUtil.getLong(request, "orderid", -1L);
		if(orderId!=-1){
			Map orderMap = daService.getPojo("select * from order_tb where id=?", new Object[]{orderId});
			Long start= (Long)orderMap.get("create_time");
			Long end= System.currentTimeMillis()/1000;
			Integer car_type = (Integer)orderMap.get("car_type");//0��ͨ�ã�1��С����2����
			if(orderMap.get("end_time")!=null)
				end = (Long)orderMap.get("end_time");
			Integer state = (Integer)orderMap.get("state");
			String _state="δ����";
			if(state==1)
				_state="�ѽ���";
			Long uid = (Long)orderMap.get("uin");
			Map userMap = daService.getMap("select mobile from user_info_Tb where id=?", new Object[]{uid});
			
			String mobile = "";
			if(userMap!=null&&userMap.get("mobile")!=null){
				mobile = userMap.get("mobile")+"";
			}
			if(orderMap!=null&&Integer.valueOf(orderMap.get("c_type")+"")==4){
				infoMap.put("showepay", "ֱ��֧��");
			}
			
			String carNumber =orderMap.get("car_number")+"";
			 if(StringUtils.isNumber(carNumber)){
			    	carNumber = "���ƺ�δ֪";
			    }
			if(carNumber.equals("null")||carNumber.equals("")){
				carNumber =publicMethods.getCarNumber(uid);
			}
			if("".equals(carNumber.trim())||"���ƺ�δ֪".equals(carNumber.trim()))
				carNumber ="null";
			if(orderMap.get("total")!=null)
				infoMap.put("prepay", StringUtils.formatDouble(orderMap.get("total")));
			Integer pid = (Integer)orderMap.get("pid");
			if(pid>-1){
				infoMap.put("total",publicMethods.getCustomPrice(start, end, pid));
			}else {
				infoMap.put("total",publicMethods.getPrice(start, end, comId, car_type));	
			}
			if(orderMap.get("state")!=null&&Integer.valueOf(orderMap.get("state")+"")==1){
				infoMap.put("total", StringUtils.formatDouble(orderMap.get("total")));
			}
			if(orderMap.get("c_type")!=null&&Integer.valueOf(orderMap.get("c_type")+"")==4){
				infoMap.put("total", StringUtils.formatDouble(orderMap.get("total")));
			}
			infoMap.put("orderid", orderId);
			infoMap.put("begin", start);
			infoMap.put("end", end);
			infoMap.put("state",_state);
			infoMap.put("mobile", mobile);
			infoMap.put("carnumber", carNumber);
		}else {
			infoMap.put("info", "�޴˶�����Ϣ");
		}
	}

	private void setRemark(List<Map<String, Object>> list){
		if(list != null && !list.isEmpty()){
			for(Map<String, Object> map : list){
				if(map.get("remark") != null){
					String remark = (String)map.get("remark");
					remark = remark.split("_")[0];
					map.put("remark", remark);
				}
			}
		}
	}
	
	/**
	 * ��ѯ��λ��Ϣ
	 * @param comId
	 * @return
	 */
	private String getComParks(Long comId) {
		daService.update("update com_park_tb set state =?,order_id=? where order_id in (select id from order_tb where state=? and id in(select order_id from com_park_tb where comid=?)) ", new Object[]{0,-1L,1,comId});
		List<Map<String, Object>> list = daService.getAll("select c.cid,c.state,o.id orderid,o.car_number,o.create_time btime,o.uin, o.end_time etime " +
				"from com_park_tb c left join order_tb o on c.order_id = o.id where c.comid=? order by c.id", new Object[]{comId});
		if(list!=null&&!list.isEmpty()){
			return StringUtils.createJson(list);
		}
		return "{}";
	}

	private void setCarNumber(List<Map<String, Object>> list){
		List<Object> uins = new ArrayList<Object>();
		for(Map<String, Object> map : list){
			uins.add(map.get("uin"));
		}
		if(!uins.isEmpty()){
			String preParams  ="";
			for(Object uin : uins){
				if(preParams.equals(""))
					preParams ="?";
				else
					preParams += ",?";
			}
			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
			resultList = daService.getAllMap("select u.id,car_number from user_info_tb u left join car_info_tb c on u.id=c.uin where u.id in ("
									+ preParams + ")", uins);
			List<Object> binduins = new ArrayList<Object>();
			List<Object> nobinduins = new ArrayList<Object>();
			for(Map<String, Object> map : resultList){
				Long uin = (Long)map.get("id");
				if(!binduins.contains(uin)){
					for(Map<String, Object> map2 : list){
						Long id = (Long)map2.get("uin");
						if(uin.intValue() == id.intValue()){
							if(map.get("car_number") != null){
								map2.put("carnumber", map.get("car_number"));
							}
						}
					}
					binduins.add(uin);
				}
			}
		}
	}
	
	private void sendWXMsg(String[] ids, Map userMap,Map comMap,Integer money){
		Long exptime = TimeTools.getToDayBeginTime()+16*24*60*60;
		String exp = TimeTools.getTimeStr_yyyy_MM_dd(exptime * 1000);
		List<Object> uins = new ArrayList<Object>();
		List<Map<String, Object>> openids = new ArrayList<Map<String, Object>>();
		
		for(int i=0;i<ids.length; i++){
			Long uin = Long.valueOf(ids[i]);
			uins.add(uin);
		}
		if(!uins.isEmpty()){
			String preParams  ="";
			for(Object uin : uins){
				if(preParams.equals(""))
					preParams ="?";
				else
					preParams += ",?";
			}
			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
			List<Object> binduins = new ArrayList<Object>();
			List<Object> nobinduins = new ArrayList<Object>();//�����˻�
			resultList = daService.getAllMap(
					"select id,wxp_openid from user_info_tb where id in (" + preParams + ") ", uins);
			for(Map<String, Object> map : resultList){
				Map<String, Object> map2 = new HashMap<String, Object>();
				Long uin = (Long)map.get("id");
				if(map.get("wxp_openid") != null){
					map2.put("openid", map.get("wxp_openid"));
					map2.put("bindflag", 1);
					openids.add(map2);
				}
				binduins.add(uin);
			}
			for(Object object: uins){
				if(!binduins.contains(object)){
					nobinduins.add(object);
				}
			}
			logger.info("sendWXMsg>>>�����˻���"+nobinduins.toString());
			if(!nobinduins.isEmpty()){
				preParams  ="";
				for(Object uin : nobinduins){
					if(preParams.equals(""))
						preParams ="?";
					else
						preParams += ",?";
				}
				resultList = daService.getAllMap(
						"select openid from wxp_user_tb where uin in (" + preParams + ") ", nobinduins);
				for(Map<String, Object> map : resultList){
					Map<String, Object> map2 = new HashMap<String, Object>();
					if(map.get("openid") != null){
						map2.put("openid", map.get("openid"));
						map2.put("bindflag", 0);
						openids.add(map2);
					}
				}
			}
			logger.info("sendWXMsg>>>:����Ϣ��openid:"+openids.toString());
			if(openids.size() > 0){
				for(Map<String, Object> map : openids){
					try {
						String openid = (String)map.get("openid");
						Integer bindflag = (Integer)map.get("bindflag");
						
						String url = "http://"+Constants.WXPUBLIC_REDIRECTURL+"/zld/wxpaccount.do?action=toticketpage&openid="+openid;
						if(bindflag == 0){
							url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+Constants.WXPUBLIC_APPID+"&redirect_uri=http%3a%2f%2f"+Constants.WXPUBLIC_REDIRECTURL+"%2fzld%2fwxpaccount.do&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
						}
						Map<String, String> baseinfo = new HashMap<String, String>();
						List<Map<String, String>> orderinfo = new ArrayList<Map<String,String>>();
						String first = "��ϲ����շ�Ա"+userMap.get("nickname")+"("+userMap.get("id")+")���͵�"+comMap.get("company_name")+"ר��ȯ";
						String remark = "����鿴���飡";
						String remark_color = "#000000";
						baseinfo.put("url", url);
						baseinfo.put("openid", openid);
						baseinfo.put("top_color", "#000000");
						baseinfo.put("templeteid", Constants.WXPUBLIC_TICKET_ID);
						Map<String, String> keyword1 = new HashMap<String, String>();
						keyword1.put("keyword", "coupon");
						keyword1.put("value", money+"Ԫ");
						keyword1.put("color", "#000000");
						orderinfo.add(keyword1);
						Map<String, String> keyword2 = new HashMap<String, String>();
						keyword2.put("keyword", "expDate");
						keyword2.put("value", exp);
						keyword2.put("color", "#000000");
						orderinfo.add(keyword2);
						Map<String, String> keyword3 = new HashMap<String, String>();
						keyword3.put("keyword", "remark");
						keyword3.put("value", remark);
						keyword3.put("color", remark_color);
						orderinfo.add(keyword3);
						Map<String, String> keyword4 = new HashMap<String, String>();
						keyword4.put("keyword", "first");
						keyword4.put("value", first);
						keyword4.put("color", "#000000");
						orderinfo.add(keyword4);
//						publicMethods.sendWXTempleteMsg(baseinfo, orderinfo);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					
				}
			}
		}
	}
	
	private void setinfo(List<Map<String, Object>> list,Integer pageNum,Integer pageSize){
		List<Object> uids = new ArrayList<Object>();
		Integer sort = (pageNum - 1)*pageSize;//����
		for(Map<String, Object> map : list){
			Long uin = (Long)map.get("uin");
			uids.add(uin);
			
			sort++;
			map.put("sort", sort);
		}
		if(!uids.isEmpty()){
			String preParams  ="";
			for(Object uid : uids){
				if(preParams.equals(""))
					preParams ="?";
				else
					preParams += ",?";
			}
			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
			resultList = daService.getAllMap(
							"select u.id,nickname,company_name from user_info_tb u,com_info_tb c where u.comid=c.id and u.id in ("
									+ preParams + ") ", uids);
			for(Map<String, Object> map : resultList){
				Long id = (Long)map.get("id");
				String nickname = null;
				String cname = null;
				if(map.get("nickname") != null && ((String)map.get("nickname")).length() > 0){
					nickname = ((String)map.get("nickname")).substring(0, 1);
					for(int i=1;i<((String)map.get("nickname")).length();i++){
						nickname += "*";
					}
				}
				if(map.get("company_name") != null && ((String)map.get("company_name")).length() > 0){
					cname = ((String)map.get("company_name")).substring(0, 1);
					cname += "****ͣ����";
				}
				for(Map<String, Object> map2: list){
					Long uid = (Long)map2.get("uin");
					if(id.intValue() == uid.intValue()){
						map2.put("nickname", nickname);
						map2.put("cname", cname);
					}
				}
			}
		}
	}
	private String autoUp(HttpServletRequest request,Long comId,Long uid) {
		String carNumber = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "carnumber"));
		String cardno = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "cardno"));
		//�������̣����ݳ��Ʋ鶩����
		/*
		 * 1:�ж��������Ƿ���Ԥ֧�� ��
		 * 		������Ԥ���������Ƿ���㣬
		 * 			�������㣺���أ�{state:1,orderid,btime,etime,carnumber,duration,total}
		 * 			�������㣺����   {state:2,prefee,total,collect}
		 * 		������Ԥ�������Ƿ��ǻ�Ա
		 * 			������Ա ������Ƿ����
		 * 				�������㣬�Ƿ��Զ�����  
		 * 					�����ǣ����أ�{state:1,orderid,btime,etime,carnumber,duration,total}
		 * 					���������ֽ𷵻أ�{state:0,orderid,btime,etime,carnumber,duration,total}
		 * 				���������� �� ���ֽ� ���أ�{state:0,orderid,btime,etime,carnumber,duration,total}
		 * 			�����ǻ�Ա :���ֽ𣺷��أ�{state:0,orderid,btime,etime,carnumber,duration,total}
		 * 2���޶��������ɶ�����
		 * 		������Ա ������Ƿ����
		 * 				�������㣬�Ƿ��Զ�����  
		 * 					�����ǣ����أ�{state:1,orderid,btime,etime,carnumber,duration,total}
		 * 					���������ֽ𷵻أ�{state:0,orderid,btime,etime,carnumber,duration,total}
		 * 				���������� �� ���ֽ� ���أ�{state:0,orderid,btime,etime,carnumber,duration,total}
		 * 		�����ǻ�Ա :���ֽ𣺷��أ�{state:0,orderid,btime,etime,carnumber,duration,total}
		 *
		 */
		Double price = RequestUtil.getDouble(request, "price", 0d);
		//System.out.println(carNumber);
		String result = "{}";
		//���ɶ���������
		if(comId==null||uid==null||uid==-1||comId==-1){
			result="{\"state\":\"-3\",\"errmsg\":\"û��ͣ�������շ�Ա��Ϣ�������µ�¼!\"}";
			return result;
		}
		Long uin = -1L;
		if(!carNumber.equals("")){
			Map carMap = daService.getMap("select uin from car_info_tb where car_number=?", new Object[]{carNumber});
			if(carMap!=null)
				uin = (Long)carMap.get("uin");
		}
		boolean isvip = true;//��Ա
		if(uin==null||uin==-1) {
			result="{\"state\":\"-1\",\"errmsg\":\"����δע��!\",\"orderid\":\"\"}";
			isvip=false;
			uin = -1L;
		}
		//�鶩��:
		Map<String,Object> orderMap =null;
		Long orderId = null;
		boolean isOrder= false;
		if("".equals(carNumber)&&!"".equals(cardno)){//���ٵ�����������
			String uuid = comId+"_"+cardno;
			Long ncount  = daService.getLong("select count(*) from com_nfc_tb where nfc_uuid=? and state=?", 
					new Object[]{uuid,0});
			if(ncount==0){
				logger.info("����ͨˢ��...���ţ�"+uuid+",δע��....");
				result="{\"state\":\"-10\",\"errmsg\":\"����û��ע��!\",\"orderid\":\"-1\"}";
			}
			orderMap = daService.getMap("select * from order_tb where comid=? and nfc_uuid=? and state=? ", new Object[]{comId,uuid,0});
			if(orderMap==null||orderMap.isEmpty()){
				if(price<0){
					result="{\"state\":\"-2\",\"errmsg\":\"�۸񲻶�:"+price+"!\",\"orderid\":\"-1\"}";
				}else {
					//���ɶ���
					Long ntime = System.currentTimeMillis()/1000;
					orderId = daService.getkey("seq_order_tb");
					int ret = daService.update("insert into order_tb (id,create_time,end_time,comid,uin,state,pay_type,c_type,uid,nfc_uuid,type,total) values(?,?,?,?,?,?,?,?,?,?,?,?)", 
							new Object[]{orderId,ntime,ntime+60,comId,uin,1,1,3,uid,uuid,2,0.0});
					if(ret!=1){//����д�����
						result="{\"state\":\"-4\",\"errmsg\":\"���ɶ���ʧ��!\",\"orderid\":\""+orderId+"\"}";
					}
					if(ncount>0)
						return "{\"state\":\"-11\",\"errmsg\":\"����δԤ֧��!\",\"orderid\":\"\"}";
				}
			}else {
				orderId = (Long)orderMap.get("id");
				Double prePay = StringUtils.formatDouble(orderMap.get("total"));
				uin = (Long)orderMap.get("uin");
				Long ouid = (Long)orderMap.get("uid");
				logger.info("orderid:"+orderId+",prepay:"+prePay+",uin:"+uin+",total:"+price);
				if(uid!=null&&ouid!=null&&!ouid.equals(uid)){
					daService.update("update order_tb set uid=? where id =? ", new Object[]{uid,orderId});
				}
				if(prePay>0){//��Ԥ֧�� 
					Integer ret = 1;//publicMethods.doPrePayOrder(orderMap, price);
					if(ret==1){//֧���ɹ�
						if(prePay>=price){//������
							orderMap = daService.getMap("select * from order_tb where id=? ", new Object[]{orderId});
							result=getThirdCardOrderInfo(orderMap);//"{\"state\":\"1\"}";//{state:1,orderid,btime,etime,carnumber,duration,total}
						}else {
							result="{\"state\":\"2\",\"prefee\":\""+prePay+"\",\"total\":\""+price+"\",\"collect\":\""+StringUtils.formatDouble((price-prePay))+"\"}";
						}
					}
				}else {
					if(!isvip){
						daService.update("update order_tb set state=? ,total=?,end_time=?,pay_type=? where id = ? ", new Object[]{1,price,System.currentTimeMillis()/1000,1,orderId});
						if(ncount>0)
							return "{\"state\":\"-11\",\"errmsg\":\"����δԤ֧��!\",\"orderid\":\"\"}";
					}
				}
			}
			
		}else {//����ͨ���ƽ���
			orderMap = daService.getMap("select * from order_tb where comid=? and car_number=? and state=? ", new Object[]{comId,carNumber,0});
			if(orderMap!=null){//�ж���
				orderId = (Long)orderMap.get("id");
				Double prePay = StringUtils.formatDouble(orderMap.get("total"));
				logger.info("����ͨ>>>>orderid:"+orderId+",uin:"+uin+",prePay:"+prePay+",price:"+price+",isvip:"+isvip);
				uin = (Long)orderMap.get("uin");
				Long ouid = (Long)orderMap.get("uid");
				if(uid!=null&&ouid!=null&&!ouid.equals(uid)){
					daService.update("update order_tb set uid=? where id =? ", new Object[]{uid,orderId});
				}
				if(prePay>0){//��Ԥ֧�� 
					Integer ret = 1;//publicMethods.doPrePayOrder(orderMap, price);
					if(ret==1){//֧���ɹ�
						if(prePay>=price){//������
							orderMap = daService.getMap("select * from order_tb where id=? ", new Object[]{orderId});
							result=getOrderInfo(orderMap);//"{\"state\":\"1\"}";//{state:1,orderid,btime,etime,carnumber,duration,total}
						}else {
							result="{\"state\":\"2\",\"prefee\":\""+prePay+"\",\"total\":\""+price+"\",\"collect\":\""+StringUtils.formatDouble((price-prePay))+"\"}";
						}
					}
					return result;
				}else {//��Ԥ֧��
					isOrder= true;
				}
				if(!isvip){
					daService.update("update order_tb set state=? ,total=?,end_time=?,pay_type=? where id = ? ", new Object[]{1,price,System.currentTimeMillis()/1000,1,orderId});
					return result;
				}
			}else{//�޶���
				//�鳵��
				if(price<0){
					result="{\"state\":\"-2\",\"errmsg\":\"�۸񲻶�:"+price+"!\",\"orderid\":\""+orderId+"\"}";
				}else {
					//���ɶ���
					Long ntime = System.currentTimeMillis()/1000;
					orderId = daService.getkey("seq_order_tb");
					String sql = "insert into order_tb (id,create_time,comid,uin,state,c_type,uid,car_number,type) values(?,?,?,?,?,?,?,?,?)";
					Object [] values =new Object[]{orderId,ntime,comId,uin,0,3,uid,carNumber,1};
					if(uin==-1){//�ǻ�Ա
						sql ="insert into order_tb (id,create_time,comid,uin,state,total,end_time,pay_type,c_type,uid,car_number,type) values(?,?,?,?,?,?,?,?,?,?,?,?)";
						values =new Object[]{orderId,ntime,comId,uin,1,price,ntime+60,1,3,uid,carNumber,1};
					}
					int ret = daService.update(sql, values)	;
					if(ret==1){//������д��
						isOrder = true;
					}else {
						result="{\"state\":\"-4\",\"errmsg\":\"���ɶ���ʧ��!\",\"orderid\":\""+orderId+"\"}";
						return result;
					}
				}
			}
			if(isOrder&&uin!=-1){//�ж���Ҫ֧�� --->>>>
				//�����ͣ��ȯ
				Map tempMap = null;//publicMethods.useTickets(uin, price, comId,uid,0);
				Long ticketId = null;
				if(tempMap!=null){
					ticketId = (Long)tempMap.get("id");
				}
				//�鵱ǰ����
				tempMap = daService.getMap("select * from order_tb where id =? ", new Object[]{orderId});
				
				//�Զ�֧������
				int isautopay = isAutoPay(uin,price);
				if(isautopay==-1){//����δ�����Զ�֧��
					result="{\"state\":\"-8\",\"errmsg\":\"����δ�����Զ�֧��!\",\"orderid\":\""+orderId+"\",\"carnumber\":\""+carNumber+"\",\"total\":\""+price+"\"}";
					daService.update("update order_tb set state=? ,total=?,pay_type=?,end_time=?  where id = ? ", new Object[]{1,price,1,System.currentTimeMillis()/1000,orderId});
					return result;
				}else if(isautopay==-2){//���������Զ�֧���޶�
					result="{\"state\":\"-9\",\"errmsg\":\"���������Զ�֧���޶�!\",\"orderid\":\""+orderId+"\",\"carnumber\":\""+carNumber+"\",\"total\":\""+price+"\"}";
					daService.update("update order_tb set state=? ,total=?,pay_type=?,end_time=?  where id = ? ", new Object[]{1,price,1,System.currentTimeMillis()/1000,orderId});
					return result;
				}
				//���㶩��
				int re = 5;//publicMethods.payOrder(tempMap, price, uin, 2,0,ticketId,null);
				logger.info(">>>>>>>>>>>>�����˻�֧�� ��"+re+",orderid:"+orderId);
				if(re==5){//����ɹ�
					tempMap = daService.getMap("select * from order_tb where id =? ", new Object[]{orderId});
					result=getOrderInfo(tempMap);//"{\"state\":\"1\",\"errmsg\":\"����֧���ɹ�!\"}";//{state:1,orderid,btime,etime,carnumber,duration,total}
				}else{
					switch (re) {
					case -8://��֧���������ظ�֧��
						result="{\"state\":\"-5\",\"errmsg\":\"��֧���������ظ�֧��!\",\"orderid\":\""+orderId+"\"}";
						break;
					case -7://֧��ʧ��
						result="{\"state\":\"-6\",\"errmsg\":\"֧��ʧ��!\",\"orderid\":\""+orderId+"\"}";						
						break;
					case -12://����
						result="{\"state\":\"-7\",\"errmsg\":\"����!\",\"orderid\":\""+orderId+"\",\"carnumber\":\""+carNumber+"\",\"total\":\""+price+"\"}";
						daService.update("update order_tb set state=? ,total=?,pay_type=?,end_time=?  where id = ? ", new Object[]{1,price,1,System.currentTimeMillis()/1000,orderId});
						break;
					default:
						result="{\"state\":\"-6\",\"errmsg\":\"֧��ʧ��!\",\"orderid\":\""+orderId+"\"}";						
						break;
					}
				}
			}
		}
		//Ԥ֧�����㣺result="{\"result\":\"2\",\"prefee\":\""+prefee+"\",\"total\":\""+money+"\",\"collect\":\""+(money-prefee)+"\"}";
		//����ɹ���{"total":"79.4","duration":"5�� 18Сʱ24����","carnumber":"��AFY123","etime":"10:38","state":"2","btime":"16:14","orderid":"786636"} 
		if(result.equals("{}"))
			result="{\"state\":\"-6\",\"errmsg\":\"֧��ʧ��!\",\"orderid\":\""+orderId+"\"}";
		logger.info(">>>>>>����ͨ:"+result);
		return result;
	}
	/**�Ƿ��Զ�֧��***/
	private int isAutoPay(Long uin, Double price) {
		//�鳵�����ã��Ƿ��������Զ�֧����û������ʱ��Ĭ��25Ԫ�����Զ�֧�� 
		Integer autoCash=1;
		Map upMap = daService.getPojo("select auto_cash,limit_money from user_profile_tb where uin =?", new Object[]{uin});
		Integer limitMoney =25;
		if(upMap!=null&&upMap.get("auto_cash")!=null){//�������Զ�֧������
			autoCash= (Integer)upMap.get("auto_cash");
			limitMoney = (Integer)upMap.get("limit_money");
			if(autoCash!=null&&autoCash==1){//�������Զ�֧��
				if(limitMoney==-1)//�����Ͻ��
					return 1;
				else if(price>limitMoney){//�����������Զ�֧���޶�
					return -2;
				}
			}else//�����˲��Զ�֧��
				return -1;
		}
		//����û���Զ�֧�����ã����ؿ�֧��
		return 1;
	}
	private String getOrderInfo(Map orderMap){
		Long btime = (Long)orderMap.get("create_time");
		Long etime = (Long)orderMap.get("end_time");
		String dur = StringUtils.getTimeString(btime,etime);
		String bt = TimeTools.getTime_yyyyMMdd_HHmm(btime*1000).substring(11);
		String et = TimeTools.getTime_yyyyMMdd_HHmm(etime*1000).substring(11);
		String ret = "{\"state\":\"1\",\"orderid\":\""+orderMap.get("id")+"\",\"btime\":\""+bt+"\",\"etime\":\""+et+"\"," +
				"\"carnumber\":\""+orderMap.get("car_number")+"\",\"duration\":\""+dur+"\",\"total\":\""+orderMap.get("total")+"\"}";
		return ret;
	}
	
	private String getThirdCardOrderInfo(Map orderMap){
		Long btime = (Long)orderMap.get("create_time");
		Long etime = (Long)orderMap.get("end_time");
		String dur = StringUtils.getTimeString(btime,etime);
		String bt = TimeTools.getTime_yyyyMMdd_HHmm(btime*1000).substring(11);
		String et = TimeTools.getTime_yyyyMMdd_HHmm(etime*1000).substring(11);
		String uuid = (String)orderMap.get("nfc_uuid");
		if(uuid!=null&&uuid.indexOf("_")!=-1)
			uuid = uuid.split("_")[1];
		else {
			uuid = "";
		}
		String ret = "{\"state\":\"1\",\"orderid\":\""+orderMap.get("id")+"\",\"btime\":\""+bt+"\",\"etime\":\""+et+"\"," +
				"\"carnumber\":\""+uuid+"\",\"duration\":\""+dur+"\",\"total\":\""+orderMap.get("total")+"\"}";
		return ret;
	}

	
	/**
	 * ����λ
	 * @param comId ͣ�������
	 * @param uin �ͻ���� 
	 * @param number ������
	 * @param infoMap ���ؽ��
	 */
	private void doShare(Long comId,Long uin,Integer number,Map<String,Object> infoMap,boolean isCanLalaRecord){
		//���¹�˾����ͣ�����ķ���������
		if(comId!=null&&uin!=null){
			int result = daService.update("update com_info_tb set share_number =?,update_time=? where id=?",
					new Object[]{number,System.currentTimeMillis()/1000,comId});
			//���㷵�ؿ�������
			if(result==1){
//				if(isCanLalaRecord)
//					doCollectorSort(number,uin,comId);
				//��ѯ��ǰδ����Ķ�����������������ռ��λ����
				Long count = daService.getLong("select count(*) from order_tb where comid=? and state=? ",//and create_time>?",
						new Object[]{comId,0});//,TimeTools.getToDayBeginTime()});
				infoMap.put("info", "success");
				infoMap.put("busy", count+"");
				logService.updateShareLog(comId, uin, number);
			}else {
				infoMap.put("info", "fail");
				infoMap.put("message", "����λʧ�ܣ����Ժ�����!");
			}
		}else {
			infoMap.put("info", "fail");
			infoMap.put("message", "��˾��Ա�����Ϸ�!");
		}
	}
	
	/*private  void doCollectorSort(Integer number,Long uin,Long comId){
		
		Long time = System.currentTimeMillis()/1000;
		boolean isLala  = false;
		try {
			isLala = publicMethods.isCanLaLa(number, uin, time);
		} catch (Exception e) {
			logger.info("memcacahe error:"+e.getMessage());
			isLala=ParkingMap.isCanRecordLaLa(uin);
		}
		if(isLala){
			logService.updateScroe(1, uin,comId);
		}
	}*/
	
	/**
	 * ���۴���
	 * @param comId ͣ�������
	 * @param uin �ͻ���� 
	 * @param hour  �Ż�Сʱ 
	 * @param orderId �������
	 * @param infoMap ���ؽ��
	 */
	private void doSale(Long comId,Long uin,Integer hour,Long orderId,Map<String,Object> infoMap){
		//���¶�����Ľ�ͣ�������ܶ�����������,
		Map orderMap = daService.getPojo("select * from order_tb where id=?", new Object[]{orderId});
		if(orderMap!=null){
			Long cid  = (Long)orderMap.get("comid");
			Long uid = (Long)orderMap.get("uin");
			if(cid.intValue()==comId.intValue()){//��֤�����Ƿ���ȷ 
				Double total = getPrice(hour, comId);
				//���¶������
				List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
				Map<String, Object> orderSqlMap = new HashMap<String, Object>();
				orderSqlMap.put("sql", "update order_tb set total = total-? where id =?");
				orderSqlMap.put("values", new Object[]{total,orderId});
				bathSql.add(orderSqlMap);
				//����ͣ�����ܶ���
				Map<String, Object> comSqlMap = new HashMap<String, Object>();
				comSqlMap.put("sql", "update com_info_tb set " +
						"total_money=total_money-? ,money=money-? where id=?");
				comSqlMap.put("values", new Object[]{total,total,comId});
				bathSql.add(comSqlMap);
				//���³������
				Map<String, Object> userSqlMap = new HashMap<String, Object>();
				userSqlMap.put("sql", "update user_info_Tb set balance = balance+? where id =?");
				userSqlMap.put("values", new Object[]{total,uid});
				bathSql.add(userSqlMap);
				boolean result = daService.bathUpdate(bathSql);
				if(result){//�������ɹ�ʱ��������Ϣ����дϵͳ��־
					infoMap.put("info", "success");
					infoMap.put("message", "�Żݳɹ�!");
					//дϵͳ��־
					doLog(comId, uin, TimeTools.gettime()+",�Ż��˶�������ţ�"+orderId+",�Żݽ�"+total,2);
					//дϵͳ��Ϣ����������ͨ��ˢ����Ϣȡ��
					doMessage(uid, TimeTools.gettime()+",���Ķ���(��ţ�"+orderId+")�Ż���"+total+"Ԫ,�Ѿ����������������ա�");
				}else {
					infoMap.put("info", "fail");
					infoMap.put("message", "�Ż�ʧ�ܣ����Ժ�����!");
				}
			}
		}else{
			infoMap.put("info", "fail");
			infoMap.put("message", "��������!");
		}
			
		//���������ˮ
		//д������־ 
	}

	private Double getPrice (Integer hour,Long comId){
		//�����Żݽ��
		Map priceMap = daService.getPojo("select * from price_tb where comid=?" +
				" and state=? order by id desc",new Object[]{comId,1});
		Double price = 0d;
		if(priceMap!=null){
			Integer payType = (Integer)priceMap.get("pay_type");
			price = Double.valueOf(priceMap.get("price")+"");
			switch (payType) {
			case 0://�ֶ�
				Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
				calendar.setTimeInMillis(System.currentTimeMillis());
				//��ʼСʱ
				int nhour = calendar.get(Calendar.HOUR_OF_DAY);
				Integer bTime = (Integer)priceMap.get("b_time");
				Integer eTime = (Integer)priceMap.get("e_time");
				//��ǰʱ���ڷֶ�������
				if(nhour>bTime&&nhour<eTime)
					price = price*hour;
				break;
			case 2://��ʱ�䵥λ
				Integer unit = (Integer)priceMap.get("unit");
				price = hour*60/unit*price;
				break;		
			default:
				break;
			}
		}
		return price;
		
	}
	/**
	 * //дϵͳ��Ϣ�����շ�Ա��ʱȡ��Ϣ��
	 * @param uin
	 * @param body
	 */
	private void doMessage(Long uin,String body){
		daService.update("insert into message_tb (type,uin,create_time,content,state) values (?,?,?,?,?)", 
				new Object[]{1,uin,System.currentTimeMillis()/1000,body,0});
	}
	/*
	 * дϵͳ��־ 
	 */
	private void doLog(Long comid,Long uin,String log,Integer type){
		logService.updateOrderLog(comid, uin, log, type);
	}
	/**
	 * ���㶩�����
	 * @param start
	 * @param end
	 * @param comId
	 * @return �������_�Ƿ��Ż�
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map getPriceMap(Long comId){
		Map priceMap1=null;
		List<Map> priceList=daService.getAll("select * from price_tb where comid=? " +
				"and state=? order by id desc", new Object[]{comId,0});
		if(priceList==null||priceList.size()==0){
			//�����Ÿ�����Ա��ͨ�����úü۸�
		}else {
			priceMap1=priceList.get(0);
			boolean pm1 = false;//�ҵ�map1,�����ǽ���ʱ����ڿ�ʼʱ��
			Integer payType = (Integer)priceMap1.get("pay_type");
			if(payType==0&&priceList.size()>1){
				for(Map map : priceList){
					if(pm1)
						break;
					payType = (Integer)map.get("pay_type");
					Integer btime = (Integer)map.get("b_time");
					Integer etime = (Integer)map.get("e_time");
					if(payType==0&&etime>btime){
						if(!pm1){
							priceMap1 = map;
							pm1=true;
						}
					}
				}
			}
		}
		return priceMap1;	
	}
	
	private List<Map<String, Object >> setScroeList(List<Map> list){
		List<Map<String, Object >> templiList = new ArrayList<Map<String, Object >>();
		List<Object> uins = new ArrayList<Object>();
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				if(map.get("uin")!=null){
					Long uin = (Long)map.get("uin");
//					if(!uins.contains(uin))
						uins.add(uin);
				}
			}
		}
		if(!uins.isEmpty()){
			String preParams  ="";
			for(Object uin : uins){
				if(preParams.equals(""))
					preParams ="?";
				else
					preParams += ",?";
			}
			uins.add(0);
			List<Map<String, Object>> resultList = daService.getAllMap("select u.id,u.mobile ,u.nickname as uname,c.company_name cname ," +
					"c.uid from user_info_tb u,com_info_tb c" +
					" where u.comid=c.id and  u.id in ("+preParams+")  and c.state=?", uins);
			
			//Map<String ,Object> markerMap = new HashMap<String ,Object>();
			if(resultList!=null&&!resultList.isEmpty()){
				
				for(int i=0;i<list.size();i++){
					Map map1 = (Map)list.get(i);
					for(Map<String,Object> map: resultList){
						Long uin = (Long)map.get("id");
						if(map1.get("uin").equals(uin)){
							templiList.add(map1);
							map1.put("nickname", "-");
							String cname = (String)map.get("cname");
							if(cname.length() > 1){
								String hidecname = "***";
								/*for(int j=0;j<cname.length()-2;j++){
									hidecname += "*";
								}*/
								hidecname =cname.substring(0, 1) +hidecname + cname.substring(cname.length()-1, cname.length());
								cname = hidecname;
							}
							map1.put("cname", cname);
							map1.put("score", StringUtils.formatDouble(map1.get("score")));
							break;
						}
					}
				}
			}
		}
		return templiList;
	}
	
	private String myInfo(Long uin){
		Map userMap = daService.getMap("select id, nickname,auth_flag,mobile from user_info_tb where id=?",new Object[]{uin});
		String info="";
		if(userMap!=null){
			Long count = daService.getLong("select Count(id) from collector_account_pic_tb where uin=? and state=? ", new Object[]{uin,0});
			Long role = (Long)userMap.get("auth_flag");
			String _role = "�շ�Ա";
			if(role==1)
				_role = "����Ա";
			return "{\"name\":\""+userMap.get("nickname")+"\",\"uin\":\""+userMap.get("id")+
					"\",\"role\":\""+_role+"\",\"mobile\":\""+userMap.get("mobile")+"\",\"pic\":\""+count+"\"}";
		}
		return "{}";
	}
	
	private void setSort(List list){
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				map.put("sort", i+1);
			}
		}
	}
	
	private void setAccountList (List<Map<String, Object>> list,Integer ptype){
		if(list!=null&&!list.isEmpty()){
			if(ptype==0){
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
			}else if(ptype==1){
				if(list!=null&&!list.isEmpty()){
					for(int i=0;i<list.size();i++){
						Map map = (Map)list.get(i);
						Integer type = (Integer)map.get("mtype");
						String remark = (String)map.get("r");
						if(type==0){
							if(remark.indexOf("_")!=-1){
								map.put("note", remark.split("_")[0]);
								map.put("target", remark.split("_")[1]);
							}
						}else if(type==1){
							map.put("note", "����");
							map.put("target", "���п�");
						}else if(type==2){
							map.put("note", "����");
							map.put("target", "ͣ����");
						}
						map.remove("r");
					}
				}
			}
			
		}
	}
	
	/*
	 * ���ó�����Ƭ����
	 */
	private void setPicParams(List list){
		List<Object> orderids = new ArrayList<Object>();
		if(list != null && !list.isEmpty()){
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				orderids.add(map.get("id"));
			}
		}
		if(!orderids.isEmpty()){
			String preParams  ="";
			for(Object orderid : orderids){
				if(preParams.equals(""))
					preParams ="?";
				else
					preParams += ",?";
			}
			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
			resultList = daService.getAllMap("select * from car_picturs_tb where orderid in ("+preParams+") order by pictype", orderids);
			if(!resultList.isEmpty()){
				for(int i=0;i<list.size();i++){
					Map map1 = (Map)list.get(i);
					Long id=(Long)map1.get("id");
					for(Map<String,Object> map: resultList){
						Long orderid = (Long)map.get("orderid");
						if(id.intValue()==orderid.intValue()){
							Integer pictype = (Integer)map.get("pictype");
							if(pictype == 0){
								map1.put("lefttop", map.get("lefttop"));
								map1.put("rightbottom", map.get("rightbottom"));
								map1.put("width", map.get("width"));
								map1.put("height", map.get("height"));
								break;
							}
						}
					}
				}
			}
		}
	}
	/**
	 * 
	 * @param orderMap   ����
	 * @param prepaymoney   Ԥ֧�����
	 * @return
	 */
	private boolean prepayRefund(Map orderMap , Double prepaymoney){
		Long orderId = (Long)orderMap.get("id");
		Map<String, Object> ticketMap = daService.getMap(
				"select * from ticket_tb where orderid=? order by utime limit ?",
				new Object[] { orderId,1});
		DecimalFormat dFormat = new DecimalFormat("#.00");
		Double back = 0.0;
		List<Map<String, Object>> backSqlList = new ArrayList<Map<String,Object>>();
		if(ticketMap != null){
			logger.info(">>>>>>>>>>>>ʹ�ù�ȯ��ticketid:"+ticketMap.get("id")+",orderid="+orderId);
			Integer money = (Integer)ticketMap.get("money");
			Double umoney = Double.valueOf(ticketMap.get("umoney")+"");
			umoney = Double.valueOf(dFormat.format(umoney));
			back = Double.valueOf(dFormat.format(prepaymoney - umoney));
			logger.info(">>>>>>>>>>>Ԥ֧�����prefee��"+prepaymoney+",ʹ��ȯ�Ľ��umoney��"+umoney+",Ӧ�˿��"+back+",orderid:"+orderId);
			Map<String, Object> tcbAccountsqlMap = new HashMap<String, Object>();
			tcbAccountsqlMap.put("sql", "insert into tingchebao_account_tb(amount,type,create_time,remark,utype,orderid) values(?,?,?,?,?,?)");
			tcbAccountsqlMap.put("values", new Object[]{umoney,0,System.currentTimeMillis() / 1000 ,"ͣ��ȯ������", 6, orderId });
			backSqlList.add(tcbAccountsqlMap);
		}else{
			logger.info(">>>>>>>>>>>>û��ʹ�ù�ȯ>>>>>>>>>>>>>orderid:"+orderId);
			back = Double.valueOf(dFormat.format(prepaymoney));
		}
		Long uin = (Long)orderMap.get("uin");
		if(back > 0){
			Map count = daService.getPojo("select * from user_info_tb where id=? ", new Object[]{uin});
			Map<String, Object> usersqlMap = new HashMap<String, Object>();
			if(count != null){//��ʵ�ʻ�
				usersqlMap.put("sql", "update user_info_tb set balance=balance+? where id=? ");
				usersqlMap.put("values", new Object[]{back,uin});
				backSqlList.add(usersqlMap);
			}else{//�����˻�
				usersqlMap.put("sql", "update wxp_user_tb set balance=balance+? where uin=? ");
				usersqlMap.put("values", new Object[]{back,uin});
				backSqlList.add(usersqlMap);
			}
			Map<String, Object> userAccountsqlMap = new HashMap<String, Object>();
			userAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,orderid) values(?,?,?,?,?,?,?)");
			userAccountsqlMap.put("values", new Object[]{uin,back,0,System.currentTimeMillis() / 1000 - 2,"�ֽ����Ԥ֧��Ԥ֧������", 12, orderId });
			backSqlList.add(userAccountsqlMap);
			boolean b = daService.bathUpdate(backSqlList);
			logger.info(">>>>>>>>>>Ԥ֧����������"+b+",orderid:"+orderId);
			try {
				String openid = "";
				if(count!=null)
					openid = count.get("wxp_openid")+"";
				if(!StringUtils.isNotNull(openid)){
					Map wx = daService.getPojo("select * from wxp_user_tb where uin=? ", new Object[]{uin});
					openid = wx.get("openid")+"";
				}
				if(!openid.equals("")){
					logger.info(">>>>>>>>>>>Ԥ֧�����ֽ���㶩���˻�Ԥ֧����   ΢������Ϣ,uin:"+uin+",openid:"+openid);
					String first = "���ֽ���㣬Ԥ֧���˿�";
					Map<String, String> baseinfo = new HashMap<String, String>();
					List<Map<String, String>> orderinfo = new ArrayList<Map<String,String>>();
					String url = "http://"+Constants.WXPUBLIC_REDIRECTURL+"/zld/wxpaccount.do?action=balance&openid="+openid;
					baseinfo.put("url", url);
					baseinfo.put("openid", openid);
					baseinfo.put("top_color", "#000000");
					baseinfo.put("templeteid", Constants.WXPUBLIC_BACK_NOTIFYMSG_ID);
					Map<String, String> keyword1 = new HashMap<String, String>();
					keyword1.put("keyword", "orderProductPrice");
					keyword1.put("value",back+"Ԫ");
					keyword1.put("color", "#000000");
					orderinfo.add(keyword1);
					Map<String, String> keyword2 = new HashMap<String, String>();
					keyword2.put("keyword", "orderProductName");
					keyword2.put("value", "Ԥ֧���˿�");
					keyword2.put("color", "#000000");
					orderinfo.add(keyword2);
					Map<String, String> keyword3 = new HashMap<String, String>();
					keyword3.put("keyword", "orderName");
					keyword3.put("value", orderId+"");
					keyword3.put("color", "#000000");
					orderinfo.add(keyword3);
					Map<String, String> keyword4 = new HashMap<String, String>();
					keyword4.put("keyword", "Remark");
					keyword4.put("value", "���������˻���");
					keyword4.put("color", "#000000");
					orderinfo.add(keyword4);
					Map<String, String> keyword5 = new HashMap<String, String>();
					keyword5.put("keyword", "first");
					keyword5.put("value", first);
					keyword5.put("color", "#000000");
					orderinfo.add(keyword5);
//					publicMethods.sendWXTempleteMsg(baseinfo, orderinfo);
				}
			} catch (Exception e) {
				logger.info("�˻سɹ�����Ϣ����ʧ��");
				e.printStackTrace();
				return true;
			}
			logger.info("�˻سɹ� ....");	
			
			return true;
		}else{
			logger.info(">>>>>>>>>>>>>>>�˻����backС��0��orderid��"+orderId);
			return false;
		}
	}
	public void uploadWork(Long id ){
		 Map map = daService.getMap("select * from parkuser_work_record_tb where id = ?",new Object[]{id});
		 String ret = null;
		 if(map!=null&&map.size()>0){
			 String work = StringUtils.createJson(map);
	       	 HttpProxy httpProxy = new HttpProxy();
	       	 Map parammap = new HashMap();
	       	 parammap.put("work", work);
	       	 try {
	       		String token = null;
     			Map session = daService.getMap("select * from  sync_time_tb where id = ? ", new Object[]{1});
     			if(session!=null&&session.get("token")!=null){
     				token = session.get("token")+"";
     			}
     			parammap.put("token", token);
	       		 ret = httpProxy.doPost(CustomDefind.DOMAIN+"/syncInter.do?action=uploadWork2Line", parammap);
	       		 
					if(ret!=null){
						if(ret.startsWith("1")){
							int r = daService.update("update parkuser_work_record_tb set sync_state=?,line_id=? where id = ?", new Object[]{1,Long.parseLong(ret.split("_")[2]+""),id});
						}else{
							int r = daService.update("update parkuser_work_record_tb set sync_state=? where id = ?", new Object[]{0,id});
						}
					}else{
						int r = daService.update("update parkuser_work_record_tb set sync_state=? where id = ?", new Object[]{0,id});
					}
				} catch (Exception e) {
					int r = daService.update("update parkuser_work_record_tb set sync_state=? where id = ?", new Object[]{0,id});
					e.printStackTrace();
				}
				System.out.println(ret);
			 }
	}
	private String getLiftReason(Long comid) {
		String reason = CustomDefind.getValue("LIFTRODREASON" + comid);
		String ret = "[";
		if(reason!=null){
			String res[] = reason.split("\\|");
			for(int i=0;i<res.length;i++){
				ret+="{value_no:"+i+",value_name:\""+res[i]+"\"},";
			}
		}
		if(ret.endsWith(","))
			ret = ret.substring(0,ret.length()-1);
		ret +="]";
		return ret;
	}
	/**
	 * ��ѯ��ʷ����
	 * @param request
	 * @param comId
	 * @param out
	 * @return
	 */
	private String orderHistory(HttpServletRequest request,Long comId,String out) {
		Map<String, Object> infoMap = new HashMap<String, Object>();
		String result="";
		Integer pageNum = RequestUtil.getInteger(request, "page", 1);
		Integer pageSize = RequestUtil.getInteger(request, "size", 20);
		Long _uid = RequestUtil.getLong(request, "uid", -1L);
		String day = RequestUtil.processParams(request, "day");
		String ptype = RequestUtil.getString(request, "ptype");//֧����ʽ
		List<Object> params = new ArrayList<Object>();
		params.add(0);
		params.add(comId);
//		Map com = daService.getMap( "select isshowepay from com_info_tb where id=? and isshowepay=?",new Object[]{comId,1});
//		if(com!=null&&com.get("isshowepay")!=null){
//			params.add(5);//ֱ������������
//		}else{
//			params.add(4);//ֱ������������
//		}
//		params.add(5);//�޸�Ŀǰ�¿���������ʾ
		String countSql = "select count(*) from order_tb where state>? and comid=? ";
		String sql = "select * from order_tb where state>?  and comid=?  ";//order by id desc ";
		String priceSql = "select sum(total) total,uid from order_tb where state>?  and comid=? ";
//		String countSql = "select count(*) from order_tb where state>? and comid=? and c_type<? ";
//		String sql = "select * from order_tb where state>?  and comid=? and c_type<?  ";//order by id desc ";
//		String priceSql = "select sum(total) total,uid from order_tb where state>?  and comid=? and c_type<? ";
		Long time = TimeTools.getToDayBeginTime();
		if(_uid!=-1){
			sql +=" and uid=? and end_time between ? and ?";
			countSql+=" and uid=? and end_time between ? and ?";
			priceSql +=" and uid=? and end_time between ? and ?";
			params.add(_uid);
			Long btime = time;
			if(day.equals("last")){
				params.add(btime-24*60*60);
				params.add(btime);
			}else {
				params.add(btime);
				params.add(btime+24*60*60);
			}
			if(ptype.equals("2")){//�ֻ�֧��
				sql +=" and pay_type=? ";
				countSql+=" and pay_type=? ";
				priceSql +=" and pay_type=? ";
				params.add(2);
			}else if(ptype.equals("3")){//����֧��
				sql +=" and pay_type=? ";
				countSql+=" and pay_type=? ";
				priceSql +=" and pay_type=? ";
				params.add(3);
			}else if(ptype.equals("4")){//ֱ������
				sql +=" and c_type=? ";
				countSql+=" and c_type=? ";
				priceSql +=" and c_type=? ";
				params.add(4);
			}
		}
		Long _total = daService.getCount(countSql,params);
		Object totalPrice = "0";
		Map pMap  = daService.getMap(priceSql+" group by uid ", params);
		if(pMap!=null&&pMap.get("total")!=null){
			totalPrice=pMap.get("total");
		}
		List<Map> list = daService.getAll(sql +" order by end_time desc ",// and create_time>?",
				params, pageNum, pageSize);
		logger.error("historyorder:"+_total+",totalprice:"+totalPrice);
		setPicParams(list);
		Integer ismonthuser = 0;//�ж��Ƿ��¿��û�
		if(list!=null&&list.size()>0){
			List<Map<String, Object>> infoMaps = new ArrayList<Map<String,Object>>();
			for(Map map : list){
				Map<String, Object> info = new HashMap<String, Object>();
				Long uid = (Long)map.get("uin");
				info.put("uin", map.get("uin"));
				String carNumber = "���ƺ�δ֪";
				if(map.get("car_number")!=null&&!"".equals((String)map.get("car_number"))){
					carNumber = map.get("car_number")+"";
					if(StringUtils.isNumber(carNumber)){
						carNumber = "���ƺ�δ֪";
					}
				}else {
					if(uid!=-1){
						carNumber = publicMethods.getCarNumber(uid);
					}
				}
				info.put("carnumber", carNumber);
				Long start= (Long)map.get("create_time");
				Long end= (Long)map.get("end_time");
				Double total =StringUtils.formatDouble(map.get("total"));// countPrice(start, end, comId);
				info.put("total", StringUtils.formatDouble(total));
				info.put("id", map.get("id"));
				info.put("state", map.get("state"));
				info.put("ptype", map.get("pay_type"));
				if(map.get("c_type")!=null&&Integer.valueOf(map.get("c_type")+"")==4){
					info.put("duration", "ֱ��֧��");
				}else {
					info.put("duration", "ͣ�� "+StringUtils.getTimeString(start,end));
				}
				info.put("btime", TimeTools.getTime_yyyyMMdd_HHmm(start*1000));
				//�ж��Ƿ����¿��û�
//				boolean b = publicMethods.isMonthUser(uid, comId);
				info.put("ctype", map.get("c_type"));
				if(Long.parseLong(map.get("c_type")+"")==5){
					ismonthuser = 1;//���¿��û�
				}else{
					ismonthuser = 0;//�����¿��û�
				}
				info.put("ismonthuser", ismonthuser);
				info.put("car_type", map.get("car_type"));
				//������Ƭ�������ã�HD����Ҫ��
				info.put("lefttop", map.get("lefttop"));
				info.put("rightbottom", map.get("rightbottom"));
				info.put("width", map.get("width"));
				info.put("height", map.get("height"));
				infoMaps.add(info);
			}
			if(out.equals("json")){
				result = "{\"count\":"+_total+",\"price\":"+totalPrice+",\"info\":"+StringUtils.createJson(infoMaps)+"}";
			}else {
				result = StringUtils.createXML(infoMaps,_total);
			}
		}else {
			infoMap.put("info", "û�м�¼");
			result = StringUtils.createJson(infoMap);
		}
		return result;
	}
	
	public String uploadPadLogFile (HttpServletRequest request,Long comid) throws Exception{
		logger.error(">>>>>begin upload logfile....");
		request.setCharacterEncoding("UTF-8"); // ���ô�����������ı����ʽ
		DiskFileItemFactory  factory = new DiskFileItemFactory(); // ����FileItemFactory����
		factory.setSizeThreshold(64*4096*1024);
		ServletFileUpload upload = new ServletFileUpload(factory);
		// �������󣬲��õ��ϴ��ļ���FileItem����
		upload.setSizeMax(64*4096*1024);
		List<FileItem> items = null;
		try {
			items =upload.parseRequest(request);
		} catch (FileUploadException e) {
			e.printStackTrace();
			 return "{\"state\":\"0\"}";
		}
		InputStream is = null; // ��ǰ�ϴ��ļ���InputStream����
		// ѭ�������ϴ��ļ�
		File filedir = new File("c:\\padlogs");
		if(!filedir.exists()){
			filedir.mkdir();
		}
		String filename = "c:\\padlogs\\padlog_"+System.currentTimeMillis()+".txt";
//		File file = new File(filename);
//		if(!file.exists()){
//			file.createNewFile();
//		}
		try {
			for (FileItem item : items){
				if(!item.isFormField()){  
                    //д���ļ�  
                   // File file = new File(filename);  
                    item.write(new File(filename));  
                    logger.error(">>>>> upload logfile over....");
                    return "{\"state\":\"1\"}";
                }
            }//end of for  
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		daService.update("update com_info_tb set navi=? where id = ? ", new Object[]{1,comid});
		return "{\"state\":\"0\"}";
	}
}
