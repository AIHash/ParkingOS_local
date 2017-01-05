package com.zld.struts.request;

import java.util.ArrayList;
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
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;
/**
 * ���ݾ�γ�ȼ�����Χ500�׷�Χ��ͣ����
 * @author Administrator
 *
 */
public class GetParkByLLAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	@Autowired
	private PublicMethods publicMethods;
	private Logger logger = Logger.getLogger(GetParkByLLAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Double lon = RequestUtil.getDouble(request, "lon", 0d);
		Double lat = RequestUtil.getDouble(request, "lat", 0d);
		if(lon==0||lat==0)
			return null;
		//500�׾�γ��ƫ����
//		double lon1 = 0.009536;
//		double lat1 = 0.007232; 
		double lon1 = 0.008036;
		double lat1 = 0.005032; 
		String sql = "select * from com_info_tb where longitude between ? and ? and latitude between ? and ? and state=?";
		List<Object> params = new ArrayList<Object>();
		params.add(lon-lon1);
		params.add(lon+lon1);
		params.add(lat-lat1);
		params.add(lat+lat1);
		params.add(0);
		List list = null;//daService.getPage(sql, null, 1, 20);
		list = daService.getAll(sql, params, 0, 0);
		String info = "{}";//"[";
		double d = 100d;
		Integer total = 0;
		String parkName ="";
		double slon = 0.0;
		double slat = 0.0; 
		Long parkId=-1L;
		Integer snumber = 0;
		if(list!=null&&list.size()>0){
			info ="{\"count\":\""+list.size()+"\",";
			for(int i=0;i<list.size();i++){
				Map map =(Map) list.get(i);
				total +=(Integer)map.get("share_number");
				double lon2 = Double.valueOf(map.get("longitude")+"");
				double lat2 = Double.valueOf(map.get("latitude")+"");
				double distance = StringUtils.distanceByLnglat(lon,lat,lon2,lat2);
				if(distance<d){
					d=distance;
					parkName = (String)map.get("company_name");
					slon = lon2;
					slat=  lat2;
					parkId = (Long)map.get("id");
					snumber =(Integer)map.get("share_number");
				}
			}
			Long unumber  = daService.getLong("select count(*) count  from order_tb where state=? and comid =?",new Object[]{0,parkId});
			
			//String price = getPrice(parkId);
			
			Map priceMap = publicMethods.getPriceMap(parkId);
			String _price = "0";
			if(priceMap!=null){
				int pay_type = (Integer)priceMap.get("pay_type");
				Double price = Double.valueOf(priceMap.get("price")+"");
				_price = price+"Ԫ/��";
				Integer unit = (Integer)priceMap.get("unit");
				if(pay_type==0){//��ʱ��
					_price =price+"Ԫ/"+unit+"����";
				}else {
					if(unit!=null&&unit>0){
						if(unit>60){
							String t = "";
							if(unit%60==0)
								t = unit/60+"Сʱ";
							else
								t = unit/60+"Сʱ "+unit%60+"����";
							_price =priceMap.get("price")+"Ԫ/"+t;
						}else {
							_price = priceMap.get("price")+"Ԫ/"+unit+"����";
						}
					}else {
						_price = priceMap.get("price")+"Ԫ/��";
					}
				}
			}
			Long free=(snumber-unumber);
			if(free<0)
				free=0L;
			info +="\"total\":\""+total+"\",\"suggest\":\""+parkName+"\",\"snumber\":\""+free+"\",\"lon\":\""+slon+"\",\"lat\":\""+slat+"\",\"id\":\""+parkId+"\",\"price\":\""+_price+"\"}";
		}
		
		//info +="]";
		//System.out.println(info);
		AjaxUtil.ajaxOutput(response, info);
		//http://127.0.0.1/zld/searchpark.do?lon=116.318512&lat=40.042214
		return null;
	}
	/**
	 * ȡ��Сʱ�۸�
	 * @param parkId
	 * @return
	 */
	/*private String getPrice(Long parkId){
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		//��ʼСʱ
		int bhour = calendar.get(Calendar.HOUR_OF_DAY);
		List<Map> priceList=daService.getAll("select * from price_tb where comid=? " +
				"and state=? and pay_type=? order by id desc", new Object[]{parkId,0,0});
		if(priceList==null||priceList.size()==0){//û�а�ʱ�β���
			//�鰴�β���
			priceList=daService.getAll("select * from price_tb where comid=? " +
					"and state=? and pay_type=? order by id desc", new Object[]{parkId,0,1});
			if(priceList==null||priceList.size()==0){//û�а��β��ԣ�������ʾ
				return "0Ԫ/��";
			}else {//�а��β��ԣ�ֱ�ӷ���һ�ε��շ�
				Map timeMap =priceList.get(0);
				return timeMap.get("price")+"Ԫ/��";
			}
			//�����Ÿ�����Ա��ͨ�����úü۸�
		}else {//�Ӱ�ʱ�μ۸�����зּ���ռ��ҹ���շѲ���
			if(priceList.size()>0){
				System.out.println(priceList);
				for(Map map : priceList){
					Integer btime = (Integer)map.get("b_time");
					Integer etime = (Integer)map.get("e_time");
					Double price = Double.valueOf(map.get("price")+"");
					Double fprice = Double.valueOf(map.get("fprice")+"");
					Integer ftime = (Integer)map.get("first_times");
					if(ftime!=null&&ftime>0){
						if(fprice>0)
							price = fprice;
					}
					if(btime<etime){//�ռ� 
						if(bhour>=btime&&bhour<etime){
							return price+"Ԫ/"+map.get("unit")+"����";
						}
					}else {
						if(bhour>=btime||bhour<etime){
							return price+"Ԫ/"+map.get("unit")+"����";
						}
					}
				}
			}
		}
		return "0.0Ԫ/Сʱ";
	}*/
/*	private String getPrice(Long parkId){
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		//��ʼСʱ
		int bhour = calendar.get(Calendar.HOUR_OF_DAY);
		List<Map> priceList=daService.getAll("select * from price_tb where comid=? " +
				"and state=? and pay_type=? order by id desc", new Object[]{parkId,0,0});
		if(priceList==null||priceList.size()==0){//û�а�ʱ�β���
			//�鰴�β���
			priceList=daService.getAll("select * from price_tb where comid=? " +
					"and state=? and pay_type=? order by id desc", new Object[]{parkId,0,1});
			if(priceList==null||priceList.size()==0){//û�а��β��ԣ�������ʾ
				return "0Ԫ/��";
			}else {//�а��β��ԣ�ֱ�ӷ���һ�ε��շ�
				Map timeMap =priceList.get(0);
				return timeMap.get("price")+"/Ԫ��";
			}
			//�����Ÿ�����Ա��ͨ�����úü۸�
		}else {//�Ӱ�ʱ�μ۸�����зּ���ռ��ҹ���շѲ���
			if(priceList.size()>0){
				//System.out.println(priceList);
				for(Map map : priceList){
					Integer btime = (Integer)map.get("b_time");
					Integer etime = (Integer)map.get("e_time");
					if(btime<etime){//�ռ� 
						if(bhour>=btime&&bhour<=etime){
							
							return map.get("price")+"Ԫ/"+map.get("unit")+"����";
						}
					}else {
						if(bhour>btime||bhour<etime){
							return map.get("price")+"Ԫ/"+map.get("unit")+"����";
						}
					}
				}
			}
		}
		return "0.0Ԫ/Сʱ";
	}*/

}