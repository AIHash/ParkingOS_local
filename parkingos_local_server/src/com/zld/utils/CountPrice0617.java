package com.zld.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class CountPrice0617 {
	
	/**
	 * ����ͣ�����
	 * @param start ��ʼutcʱ��
	 * @param end ����utcʱ��
	 * @param priceMap ʱ�μƷ�1
	 * @param priceMap2 ʱ�μƷ�2 //�ֶμƷ�ʱ�����У�û��ʱ���Ʒ�1��Ϊȫ���
	 * @return
	 */
	
	public static Map<String, Object> getAccount(Long start,Long end,Map dayMap,Map nightMap){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Double hprice = 0d;//����ʱ�����շ�
		Double price = 0d;//���ص��ܼ�
		Double dayPirce = Double.valueOf(dayMap.get("price")+"");//�ռ�۸�
		Integer ftime = (Integer) dayMap.get("first_times");//�ռ����Ż�ʱ��
		Double fprice = Double.valueOf(dayMap.get("fprice")+"");//�ռ����Ż�ʱ���۸�
		Integer dft = (Integer)dayMap.get("free_time"); // �ռ����ʱ��
		Integer dfpt = (Integer)dayMap.get("fpay_type");// 1��� 0�շ�
		Integer isFullDayTime =(Integer)dayMap.get("is_fulldaytime");
		dft = dft==null?0:dft;
		dfpt = dfpt==null?0:dfpt;
		Integer nft = dft;// ҹ�����ʱ��
		Integer nfpt = dfpt;// 1��� 0�շ�
		Integer nftime = ftime;//ҹ�����Ż�ʱ��
		Double nfprice =fprice;//ҹ�����Ż�ʱ���۸�
		Double nigthPrice = null;//ҹ���۸�
		Long dayDuration =0L;//�ռ�ʱ��
		Long nightDuration =0L;//ҹ��ʱ��
		Double ymoney=0d;//�Ż�
		Long oldDuration =(end-start);
//		System.out.println("�ռ䣺"+dayMap);
//		System.out.println("ҹ�䣺"+nightMap);
		Integer btime = (Integer)dayMap.get("b_time");
		Integer etime = (Integer)dayMap.get("e_time");
		Integer dayUnit = (Integer) dayMap.get("unit");//�ռ�Ʒѵ�λ
		Integer nightUnit = dayUnit;
		if(nightMap==null){//û��ҹ��۸����ʱ���ռ��շ�ʱ��Ϊȫ��
//			btime=0;
//			etime=24;
			nigthPrice = 0.0d;//dayPirce;
			nightUnit= dayUnit ;
			nfprice=0.0d;
			nft =0;
		}else {//��ǰֻ֧������ʱ���������һ���׶α�����δʱ�����ʱ�� �����ڶ���ʱ���ǵ�һ��ʱ�εĲ���,����Ҫ��ֹʱ��
			nightUnit=(Integer) nightMap.get("unit");//ҹ��Ʒѵ�λ
			nigthPrice = Double.valueOf(nightMap.get("price")+"");
			nftime = (Integer) nightMap.get("first_times");//���Ż�ʱ��
			nfprice =Double.valueOf(nightMap.get("fprice")+"");
			nft = (Integer)nightMap.get("free_time");
			nfpt = (Integer)nightMap.get("fpay_type");
		}
		List<Long> durs = getDurations(start,end,btime,etime);
		Long times = durs.get(0);
		Long fat = durs.get(1);//ͣ��ʱ����  ҹ�� :0 , �ռ䣺1 ,ҹ�䵽�ռ�:2 , �ռ䵽ҹ��:3,
		
		dayDuration = durs.get(2)/60;//�ռ�ʱ��
		nightDuration = durs.get(3)/60;//ҹ��ʱ��
		
		if(times>0){
			Integer t1 = (etime-btime);
			hprice =Double.valueOf(dayPirce*((t1*60)/dayUnit)+nigthPrice*(((24-t1)*60)/nightUnit));
			hprice = Double.valueOf(hprice*times);
		}
		
		//�������ʱ���������Ż�
		if(fat==0||fat==2){//ҹ��,�ȴ������ʱ��,�ټ����Ż�
			//ҹ��,�ȴ������ʱ��
			if(nft>0){
				if(nightDuration<=nft){//С�����ʱ����ֱ�����
					if(nft-nightDuration<dayDuration)//ͬʱ�ٿ۳��ռ��ʱ�����Բ������ʱ��
						dayDuration = dayDuration-(nft- nightDuration);
					else {//ҹ��+�ռ䲻�����ʱ�䣬�ռ�Ҳ��Ϊ0;
						dayDuration=0L;
					}
					nightDuration=0L;
				}else if(nfpt==1){//�������ʱ��������Ϊ���ʱ����ȥ���ʱ��
					nightDuration = nightDuration-nft;
				}
			}
			//ҹ��,�ټ����Ż�
			if(nightDuration>0&&nftime>0&&nfprice>0&&nigthPrice>nfprice){
				ymoney = (nftime/nightUnit)*(nigthPrice-nfprice);
				if(nightDuration>nftime)
					ymoney = (nftime/nightDuration)*(nigthPrice-nfprice);
				else {
					ymoney = (nightDuration/nightUnit)*(nigthPrice-nfprice);
					if(nightDuration<nightUnit)
						ymoney = (nigthPrice-nfprice);
				}
			}
			if(ymoney==0&&times>0){
				ymoney = (nftime/nightUnit)*(nigthPrice-nfprice);
			}
		}else {//�ռ�,�ȴ�����һ���Ʒ�ʱ�����ǼƷ�ʱ����������ʱ,�ٴ������ʱ��,�������Ż�
			
			//�ռ�,�������ʱ��
			if(dft>0){
				if(dayDuration<=dft){//С���ʱ����ֱ�����
					dayDuration=0L;
					if(dft-dayDuration<nightDuration)//ͬʱ�ٿ۳�ҹ���ʱ�����Բ������ʱ��
						nightDuration = nightDuration-(dft-dayDuration);
					else {//ҹ��+�ռ䲻�����ʱ�䣬ҹ��Ҳ��Ϊ0;
						nightDuration=0L;
					}
				}else if(dfpt==1){//�������ʱ��������Ϊ���ʱ����ȥ���ʱ��
					dayDuration = dayDuration-dft;
				}
			}
			if(isFullDayTime!=null&&isFullDayTime==0){//�ռ�ʱ���Ƿ�ȫ,0��ȫ Ĭ�� 1����ȫ��������ϵĳ���
				//�ռ�,������һ���Ʒ�ʱ�����ǼƷ�ʱ����������ʱ
				if(dayDuration>0){
					if(dayDuration<dayUnit){//�ռ�ʱ������һ�����ѵ�λ������һ���Ʒѵ�λ��ͬʱҹ��ʱ�����ٲ�ʱ����
						if(nightDuration>(dayUnit-dayDuration)){
							nightDuration = nightDuration -(dayUnit-dayDuration);
							dayDuration = dayUnit.longValue();
						}else {
							dayDuration = dayDuration+nightDuration;
							nightDuration=0L;
						}
					}else if(dayDuration%dayUnit>0){//�ռ�ʱ�����Ǹ��ѵ�λ����������������һ���Ʒѵ�λ��ͬʱҹ��ʱ�����ٲ�ʱ����
						Long ld = dayDuration%dayUnit; //����
						if(nightDuration>(dayUnit-ld)){
							nightDuration = nightDuration -(dayUnit-ld);
							dayDuration = dayDuration + (dayUnit-ld);
						}else {
							dayDuration = dayDuration+nightDuration;
							nightDuration=0L;
						}
					}
				}
			}
			//�ռ�,�ټ����Ż�
			if(times>0){
				ymoney = (ftime/dayUnit)*(dayPirce-fprice);
			}else {
				if(dayDuration>0&&ftime>0&&fprice>0&&dayPirce>fprice){
					if(dayDuration>=ftime){
						ymoney = (ftime/dayUnit)*(dayPirce-fprice);
					}else {
						ymoney = (dayDuration/dayUnit)*(dayPirce-fprice);
						if(dayDuration%dayUnit>0&&(dayDuration/dayUnit)<(ftime/dayUnit))
							ymoney +=(dayPirce-fprice);
						if(dayDuration<dayUnit)
							ymoney = (dayPirce-fprice);
					}
				}
			}
			if(ymoney==0&&times>0){
				ymoney = (ftime/dayUnit)*(dayPirce-fprice);
			}
		}
		//�����ܼ�
		price = (dayDuration/dayUnit)*dayPirce + (nightDuration/nightUnit)*nigthPrice;
		//��ͷ��һ���Ʒѵ�λ�շ�
		if(dayDuration%dayUnit>0)
			price+=dayPirce;
		if(nightDuration%nightUnit>0)
			price +=nigthPrice;
//		if(price==0)
//			price=0.01d;
//		
		resultMap.put("btime", TimeTools.getTime_MMdd_HHmm(start*1000).substring(6));
		resultMap.put("etime", TimeTools.getTime_MMdd_HHmm(end*1000).substring(6));
		resultMap.put("total", StringUtils.formatDouble((price+hprice)));
		resultMap.put("discount",StringUtils.formatDouble(ymoney));
		resultMap.put("collect", StringUtils.formatDouble(((price+hprice)-ymoney)));
		resultMap.put("duration", StringUtils.getTimeString(oldDuration));
		return resultMap;
	}
	
	
	/**
	 * ����ͣ��ʱ��������
	 * @param start --ͣ����ʼʱ��
	 * @param end --ͣ������ʱ��
	 * @param btime --�۸�ʼСʱ
	 * @param etime --�۸����Сʱ
	 * @return [������ͣ��ʱ������(ͣ��ʱ����  0ҹ�� , 1�ռ�,2ҹ�䵽�ռ�, 3�ռ䵽ҹ��)���ռ�ʱ����ҹ��ʱ��]
	 */
	private static List<Long> getDurations(Long start,Long end,Integer btime,Integer etime){
		List<Long> durList = new ArrayList<Long>();
		Long times =0L;
		//��ʼʱ��������룬���Ӽ�1
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		calendar.setTimeInMillis(start*1000);
		if(calendar.get(Calendar.SECOND)>0)
			calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE)+1);
		calendar.set(Calendar.SECOND, 0);
		start = calendar.getTimeInMillis()/1000;
		//����ʱ��������룬ȡ��������
		calendar.setTimeInMillis(end*1000);
		calendar.set(Calendar.SECOND, 0);
		end = calendar.getTimeInMillis()/1000;
		//ʱ��
		Long duration = end-start;
		//��������
		if(duration>=24*60*60){
			times = duration/(24*60*60);
			start = start + times*24*60*60;
			//System.err.println("times:"+times);
		}
		//[������ͣ��ʱ������(ͣ��ʱ����  0ҹ�� , 1�ռ�,2ҹ�䵽�ռ�, 3�ռ䵽ҹ��)���ռ�ʱ����ҹ��ʱ��]
		durList.add(times);
		
		//�ռ�۸�ʼʱ��
		calendar.setTimeInMillis(start*1000);
		calendar.set(Calendar.HOUR_OF_DAY, btime);
		calendar.set(Calendar.MINUTE, 0);
		Long ts = calendar.getTimeInMillis()/1000;
		//�۸��ʱ��
		calendar.set(Calendar.HOUR_OF_DAY, etime);
		calendar.set(Calendar.MINUTE, 0);
		Long te = calendar.getTimeInMillis()/1000;
		//ҹ��۸�ʼ����ʱ��
		Long nts = ts+24*60*60;
		Long nte = te+24*60*60;
		//�ռ�ʱ��
		Long dduration = 0L;
		//ҹ��ʱ��
		Long nduration = 0L;
		
//		System.err.println("start:"+TimeTools.getTime_yyyyMMdd_HHmmss(start*1000));
//		System.err.println("end:"+TimeTools.getTime_yyyyMMdd_HHmmss(end*1000));
//		System.err.println("ts:"+TimeTools.getTime_yyyyMMdd_HHmmss(ts*1000));
//		System.err.println("te:"+TimeTools.getTime_yyyyMMdd_HHmmss(te*1000));
//		System.err.println("nts:"+TimeTools.getTime_yyyyMMdd_HHmmss(nts*1000));
//		System.err.println("nte:"+TimeTools.getTime_yyyyMMdd_HHmmss(nte*1000));
		
		if(start<ts){//ͣ����ʼʱ��С�ڼ۸�Ŀ�ʼʱ��
			if(end<=ts){//ͣ������ʱ��ҲС�ڼ۸�ʼʱ�䣬��۸�ʱ�� 08:00-21:00,ͣ��������07:20-07:55
				durList.add(0L);//ͣ��ʱ����  0ҹ�� 
				nduration =end-start;//ҹ��ʱ��Ϊͣ����ʼʱ��-ͣ������ʱ�䣬���ռ�ʱ��Ϊ0
//				System.err.println("night1 duration:"+nduration);
			}else {//ͣ������ʱ����ڼ۸�ʼʱ��
				durList.add(2L);//ͣ��ʱ���� 2ҹ�䵽�ռ� 
				if(end<=te){//ͣ������ʱ��С�ڼ۸����ʱ�䣬 ��۸�ʱ�� 08:00-21:00,ͣ��������07:20-19:55
					nduration = ts-start;//�ռ�ʱ��
					dduration = end-ts;//ҹ��ʱ��
//					System.err.println("night2 duration:"+nduration);
//					System.err.println("day2 duration:"+dduration);
				}else {//ͣ������ʱ����ڼ۸����ʱ�䣬
					if(end<nts){//ͣ������ʱ��С�ڵڶ���۸�ʼʱ�䣬 ��۸�ʱ�� 08:00-21:00,ͣ��������07:20-�ڶ���06:55
						nduration = (ts-start) + (end-te);//ҹ��ʱ��Ϊ���Σ�0800-0720 + 0655-2100
						dduration = te-ts;//�ռ�ʱ�� 2100-0800
//						System.err.println("night3 duration:"+nduration);
//						System.err.println("day3 duration:"+dduration);
					}
				}
			}
		}else if(start<te){//ͣ����ʼʱ��С�ڼ۸�Ľ���ʱ�� �磺�۸�ʱ�� 08:00-21:00,ͣ��������09:20-17:55
			if(end<=te){//ͣ������ʱ��С�ڼ۸����ʱ�䣬 �磺�۸�ʱ�� 08:00-21:00,ͣ��������09:20-17:55
				durList.add(1L);//ͣ��ʱ����  1�ռ� 
				dduration = end-start;//�ռ�ʱ��,1755-0920��ҹ��ʱ��Ϊ0
//				System.err.println("day4 duration:"+dduration);
			}else {//ͣ������ʱ����ڼ۸����ʱ��,�磺�۸�ʱ�� 08:00-21:00,ͣ��������09:20-22:55
				durList.add(3L);//ͣ��ʱ����  3�ռ䵽ҹ��
				if(end<=nts){//ͣ������ʱ��С�ڵڶ���۸�ʼʱ�䣬 ��۸�ʱ�� 08:00-21:00,ͣ��������09:20-�ڶ���07:55
					nduration =end-te ;//ҹ��ʱ��,0755-2100
					dduration = te-start;//�ռ�ʱ��,2100-0920
//					System.err.println("night5 duration:"+nduration);
//					System.err.println("day5 duration:"+dduration);
				}else{//ͣ������ʱ����ڵڶ���۸�ʼʱ��, ��۸�ʱ�� 08:00-21:00,ͣ��������09:20-�ڶ���08:55
					dduration = (te-start)+(end-nts);//�ռ�ʱ��������,2100-0920 + 0855-0800
					nduration = nts-te;//ҹ��ʱ��   0800-2100
//					System.err.println("night6 duration:"+nduration);
//					System.err.println("day6 duration:"+dduration+",t1:"+(te-start)+",t2:"+(end-nts));
				}
			}
		}else if(start>=te){//ͣ����ʼʱ����ڻ���ڼ۸�Ľ���ʱ�� ,��۸�ʱ�� 08:00-21:00 ͣ��������22:20-�ڶ���06:55
			if(end<=nts){//ͣ������ʱ��С�ڵڶ���۸�ʼʱ��,��۸�ʱ�� 08:00-21:00 ͣ��������22:20-�ڶ���06:55
				durList.add(0L);//ͣ��ʱ����  0ҹ�� 
				nduration = end-start;//ҹ��ʱ����0655-2220�����ռ�ʱ��Ϊ0
//				System.err.println("night7 duration:"+nduration);
			}else{//ͣ������ʱ����ڵڶ���۸�ʼʱ��,��۸�ʱ�� 08:00-21:00 ͣ��������22:20-�ڶ���09:55
				durList.add(2L);//ͣ��ʱ���� 2ҹ�䵽�ռ� 
				if(end<=nte){//ͣ������ʱ��С�ڻ���ڵڶ���۸��ʱ��,��۸�ʱ�� 08:00-21:00 ͣ��������22:20-�ڶ���20:55
					nduration = nts-start;//ҹ��ʱ��,�ڶ����0800-2220
					dduration = end-nts;//�ռ�ʱ��,2055-0800
//					System.err.println("night8 duration:"+nduration);
//					System.err.println("day8 duration:"+dduration);
				}else {//ͣ������ʱ����ڵڶ���۸��ʱ��,��۸�ʱ�� 08:00-21:00 ͣ��������22:20-�ڶ���21:55
					nduration = nts-start +end-nte;//ҹ��������,�ڶ����0800-2220 + 2155-2100
					dduration = nte-nts;//�ռ�ʱ��,2100-0800
//					System.err.println("night9 duration:"+nduration);
//					System.err.println("day9 duration:"+dduration);
				}
			}
				
		}
		durList.add(dduration);
		durList.add(nduration);
		//����:[������ͣ��ʱ������(ͣ��ʱ����  0ҹ�� , 1�ռ�,2ҹ�䵽�ռ�, 3�ռ䵽ҹ��)���ռ�ʱ����ҹ��ʱ��]
		return durList;
	}
	
	/**
	 * ����ͣ�����
	 * @param start ��ʼutcʱ��
	 * @param end ����utcʱ��
	 * @param priceMap ʱ�μƷ�1
	 * @param priceMap2 ʱ�μƷ�2 //�ֶμƷ�ʱ�����У�û��ʱ���Ʒ�1��Ϊȫ���
	 * @return
	 */
	/*public static Map<String, Object> getAccount_bak11(Long start,Long end,Map dayMap,Map nightMap){
		
		 *  �ռ䣺{price=3.00, unit=30, b_time=8, e_time=18, first_times=60, fprice=2.50, countless=5}
			ҹ�䣺{price=2.00, unit=60, b_time=18,e_time= 8,  first_times=0, fprice=0.00, countless=0}
			btime:1405581081,etime:1405581549
		 
		//System.err.println("btime:"+start+",etime:"+end);
		Double hprice = 0d;//����ʱ�����շ�
		Double price = 0d;//���ص��ܼ�
		Double dayPirce = null;//�ռ�۸�
		Double ymoney=0d;//�Ż�
		Integer countless = 0;//��ͷ�Ʒ�ʱ������λ����
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long duration = (end-start)/60;//ͣ��ʱ������λ������,ֻȡ����
		Long oldDuration =(end-start);
		//Long allduration=duration;
		if(start!=null&&end!=null&&dayMap!=null){
			//System.err.println("��ͣ��ʱ����"+duration+"����");
			dayPirce=Double.valueOf(dayMap.get("price")+"");
			//�ռ�ʱ��1
			Integer btime = (Integer)dayMap.get("b_time");
			Integer etime = (Integer)dayMap.get("e_time");
			Integer dayUnit = (Integer) dayMap.get("unit");//�ռ�Ʒѵ�λ
			countless = (Integer)dayMap.get("countless");
			Integer ftime = (Integer) dayMap.get("first_times");//���Ż�ʱ��
			Double fprice = Double.valueOf(dayMap.get("fprice")+"");
			Integer nightUnit =1; 
			Double nigthPrice = 0d;//ҹ��۸�
			Integer nft = 0;
			Integer nfpt =0;
			//û������ʱ��2ʱ��ʱ��1��ȫ��
			if(nightMap==null){//û��ҹ��۸����ʱ���ռ��շ�ʱ��Ϊȫ��
				btime=0;
				etime=24;
			}else {//��ǰֻ֧������ʱ���������һ���׶α�����δʱ�����ʱ�� �����ڶ���ʱ���ǵ�һ��ʱ�εĲ���,����Ҫ��ֹʱ��
				nightUnit=(Integer) nightMap.get("unit");//ҹ��Ʒѵ�λ
				nigthPrice = Double.valueOf(nightMap.get("price")+"");
				nft = (Integer)nightMap.get("free_time");
				nfpt = (Integer)nightMap.get("fpay_type");
				nft = nft==null?0:nft;
				nfpt = nfpt==null?0:nfpt;
			}
			resultMap.put("btime", TimeTools.getTime_MMdd_HHmm(start*1000).substring(6));
			resultMap.put("etime", TimeTools.getTime_MMdd_HHmm(end*1000).substring(6));
			Integer dft = (Integer)dayMap.get("free_time");
			Integer dfpt = (Integer)dayMap.get("fpay_type");
			dft = dft==null?0:dft;
			dfpt = dfpt==null?0:dfpt;
			//System.out.println("�ռ䣺"+dayMap);
			//System.out.println("ҹ�䣺"+nightMap);
			//�������ʱ������ѹ������ͣ����ʼ������ʱ�� 
			//if(dfpt!=0||dft!=0){//����ѹ���ʱ��������Ѽ�����ҹ����
			List<Long> seList = getStart(btime,etime,start,end,dayUnit,dft,nft,dfpt,nfpt);
			if(!seList.isEmpty()){
				start = seList.get(0);
				end = seList.get(1);
			}
			//}else{//������Ѽ�����ҹ����
				
			//}
			duration = (end-start)/60;//ͣ��ʱ������λ������,ֻȡ����
			//System.err.println("���ʱ��ȥ����ʱ����"+duration+"����");
			if(end>start){
				//ʱ������24Сʱ,�ȼ�������켰���
				if(duration>=24*60){
					Long times = duration/(24*60);
					Integer t1 = (etime-btime);
					//hprice =Double.valueOf(t1*dayPirce*(60/dayUnit)+((24-t1)*nigthPrice*60)/nightUnit);
					hprice =Double.valueOf(dayPirce*((t1*60)/dayUnit)+nigthPrice*(((24-t1)*60)/nightUnit));
					hprice = Double.valueOf(hprice*times);
//					resultMap.put("btime", TimeTools.getTime_MMdd_HHmm(start*1000));
//					resultMap.put("etime", TimeTools.getTime_MMdd_HHmm(end*1000).substring(6));
					duration=duration-times*24*60;
					ymoney=(ftime/dayUnit)*(dayPirce-fprice);
				}else {
//					resultMap.put("btime", TimeTools.getTime_MMdd_HHmm(start*1000).substring(6));
//					resultMap.put("etime", TimeTools.getTime_MMdd_HHmm(end*1000).substring(6));
				}
				Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
				calendar.setTimeInMillis(start*1000);
				//��ʼСʱ
				int bhour = calendar.get(Calendar.HOUR_OF_DAY);
				int bmin = calendar.get(Calendar.MINUTE);
				calendar.setTimeInMillis(end*1000);
				//����Сʱ
				int ehour = calendar.get(Calendar.HOUR_OF_DAY);
//				if(ehour==0&&end>start)
//					ehour=24;
				int emin = calendar.get(Calendar.MINUTE);
				//����ʱ�� 8:00-13:00\
				//System.out.println(stopInfo);
				if(ehour>=bhour){
					price = countPrice(bhour,ehour,bmin,emin,btime,etime,dayPirce,nigthPrice,dayUnit,nightUnit,duration,countless,true);
				}else {//��ʱ�� 21:00 -7:00 �����μƷ�   21:00-24:00,0:00-7:00
					Long _duration = Long.valueOf((24-bhour-1)*60+(60-bmin));
					price = countPrice(bhour,24,bmin,0,btime,etime,dayPirce,nigthPrice,dayUnit,nightUnit,_duration,countless,false);
					//�����μƷ�ʱ����һ�β��ܼ��㣬�ѵ�һ�ε���ͷ�ӵ��ڶ���ʱ���� ((24-etime)*60)%nightUnit
					_duration = Long.valueOf(ehour*60+emin)+((24-etime)*60)%nightUnit;
					price +=countPrice(0,ehour,0,emin,btime,etime,dayPirce,nigthPrice,dayUnit,nightUnit,_duration,countless,true);
				}
				//�����Żݣ�ֻ�����ռ�ʱ���ڵ��Ż�
				Double _ymoney =countFprice(btime, etime, bhour, bmin, ehour, emin, (dayPirce-fprice), ftime, dayUnit, duration, countless);
				if(ymoney<_ymoney)
					ymoney =_ymoney;
			}
		}
		if(price==0)
			price=0.01d;
		resultMap.put("total", StringUtils.formatDouble((price+hprice)));
		resultMap.put("discount",StringUtils.formatDouble(ymoney));
		resultMap.put("collect", StringUtils.formatDouble(((price+hprice)-ymoney)));
		resultMap.put("duration", StringUtils.getTimeString(oldDuration));
		//System.out.println(resultMap);
		return resultMap;//stopInfo+"��Ӧ�գ�"+(price+hprice)+",�Żݣ�"+ymoney+",ʵ�գ�"+((price+hprice)-ymoney);
	}
	*//**
	 * @param bhour ͣ����ʼ Сʱ 
	 * @param ehour ͣ������Сʱ 
	 * @param bmin  ͣ����ʼ���� 
	 * @param emin  ͣ���������� 
	 * @param btime �ռ�Ʒ�ʱ�� ��ʼСʱ
	 * @param etime �ռ�Ʒ�ʱ�� ����Сʱ
	 * @param dayPirce �ռ�Ʒ� ���� 
	 * @param nigthPrice ҹ��Ʒ� ����  
	 * @param dayUnit �ռ�Ʒѵ�λ
	 * @param nigthUnit ҹ��Ʒѵ�λ
	 * @param duration ͣ��ʱ��������
	 * @param countless ��ͷ�Ʒ�ʱ������λ����
	 * @param isFprice �Ƿ������ͷ�Ʒ�ʱ���������μƷ�ʱ����һ�β��ܼ��㣬�ѵ�һ�ε���ͷ�ӵ��ڶ���ʱ����
	 * @return ���
	 * ��ǰֻ֧������ʱ���������һ���׶α�����δʱ�����ʱ�� �����ڶ���ʱ���ǵ�һ��ʱ�εĲ���,����Ҫ��ֹʱ��
	 *//*
	private static Double countPrice(int bhour,int ehour,int bmin,int emin,int btime,
			int etime,double dayPirce,double nigthPrice,Integer dayUnit,
			Integer nigthUnit,Long duration,Integer countless,boolean isFprice){
		Double price = null;
		Double cprice=0d;//��ͷ�Ʒ�
		
		//ehour һ���Ǵ���bhuour
		if(ehour<=btime||bhour>=etime){//ȫ�ڵڶ����Ʒ�ʱ����
			if(ehour==btime){//�ռ�ʱ��7-21��ͣ����6:30:7:20,ҹ�䵥λ��120���ռ䵥λ��15  �����ʱ����10
				if(bhour==ehour){
					price = (duration/dayUnit)*dayPirce;
					if(duration%dayUnit>countless)//ҹ����ͷ�Ʒ�
						cprice=dayPirce;
				}else {
					duration = duration-emin;
					int dayTimes = emin/dayUnit;//20/15;
					price = (duration/nigthUnit)*nigthPrice+dayTimes*dayPirce;
					if(duration%nigthUnit>countless)//ҹ����ͷ�Ʒ�
						cprice=nigthPrice;
					if(emin!=0&&emin%dayUnit>countless)//�ռ���ͷ�Ʒ�   ��ͷʱ��>�ռ���ͷ�Ʒ�ʱ������ͷ�Ʒ�=ҹ��۸�
						cprice +=dayPirce;
				}
			}else {//�ռ�ʱ��7-21��ͣ����6:30:6:50
				price = (duration/nigthUnit)*nigthPrice;
				if(duration%nigthUnit>countless)//��ͷʱ��>��ͷ�Ʒ�ʱ������ͷ�Ʒ�=ҹ��۸�
					cprice=nigthPrice;
			}
		}else if(bhour<=btime){// ͣ����ʼʱ��С�ڻ�����ռ俪ʼʱ�� 
			if(bhour<btime){//ͣ����ʼʱ����ռ俪ʼʱ�� С
				if(ehour<etime){//ͣ������ʱ��С���ռ����ʱ�䣬��ҹ����ԡ��ռ�۸������
					int nightMin = ((btime-bhour-1)*60+(60-bmin));
					int nightTimes = nightMin/nigthUnit;
					int dayMin = ((ehour-btime)*60+emin);
					int dayTimes = dayMin/dayUnit;
					if(dayMin%dayUnit>countless)
						cprice=dayPirce;
					if(nightMin%nigthUnit>countless)
						cprice+=nigthPrice;
					price = nightTimes*nigthPrice+dayTimes*dayPirce;
				}else {//ͣ������ʱ������ռ����ʱ�䣬��ҹ����ԡ��ռ������۸���Ժ� ҹ�������
					int nightMin1 = ((btime-bhour-1)*60+(60-bmin));
					int nightMin2 = ((ehour-etime)*60+emin);
					int nightTimes1 = nightMin1/nigthUnit;
					int dsyTimes = ((etime-btime)*60)/dayUnit;
					int nightTimes2 = nightMin2/nigthUnit;
					if(nightMin1%nigthUnit>countless)
						cprice=nigthPrice;
					if(nightMin2%nigthUnit>countless)
						cprice +=nigthPrice;
					price = nightTimes1*nigthPrice+dsyTimes*dayPirce+nightTimes2*nigthPrice;
				}
			}else {//ͣ����ʼʱ��=�ռ俪ʼʱ�� 
				if(ehour<etime){//ͣ������ʱ��С���ռ����ʱ�䣬��ҹ����ԡ��ռ�۸������
					int dayTimes = duration.intValue()/dayUnit;
					if(duration%dayUnit>countless)
						cprice=dayPirce;
					price =dayTimes*dayPirce;
				}else {//ͣ������ʱ������ռ����ʱ�䣬��ҹ����ԡ��ռ������۸���Ժ� ҹ�������
					int dsyTimes = ((etime-btime)*60)/dayUnit;
					if(bhour==btime){
						dsyTimes = ((etime-btime)*60-bmin)/dayUnit;
					}
					if(((etime-btime)*60-bmin)%dayUnit>countless)
						cprice=dayPirce;
					int nightTimes2 = ((ehour-etime)*60+emin)/nigthUnit;
					if( ((ehour-etime)*60+emin)%nigthUnit>countless)
						cprice+=nigthPrice;
					price =dsyTimes*dayPirce+nightTimes2*nigthPrice;
				}
			}
		}else if(bhour>btime){// ͣ����ʼʱ����ռ俪ʼʱ���
			if(ehour<=etime){//��ͣ������ʱ�䳬���ռ�ʱ��
				if(ehour<etime){//ʱ��7-21��ͣ��19:19-20:12
					int dayTimes = duration.intValue()/dayUnit;
					if(duration%dayUnit>countless)//��ͷʱ��>��ͷ�Ʒ�ʱ������ͷ�Ʒ�=ҹ��۸�
						cprice=dayPirce;
					price = dayTimes*dayPirce;
				}else {//ʱ��7-21��ͣ��19:19-21:12
					duration = duration-emin;
					int dayTimes = duration.intValue()/dayUnit;
					int nightTiimes = emin/nigthUnit;
					if(emin%nigthUnit>countless)//��ͷʱ��>��ͷ�Ʒ�ʱ������ͷ�Ʒ�=ҹ��۸�
						cprice=nigthPrice;
					if(duration%dayUnit>countless)
						cprice+=dayPirce;
					price = dayTimes*dayPirce+nightTiimes*nigthPrice;
				}
			}else {
				int dayDur = ((etime-bhour-1)*60+(60-bmin));
				int dsyTimes = dayDur/dayUnit;
				int nigDur = ((ehour-etime)*60+emin);
				int nightTimes = nigDur/nigthUnit;
				if(emin%nigthUnit>countless)
					cprice=nigthPrice;
				if(dayDur%dayUnit>countless)
					cprice+=dayPirce;
				price = dsyTimes*dayPirce+nightTimes*nigthPrice;
			}
		}
		if(!isFprice)
			cprice=0d;
		//System.out.println("�ܼۣ�"+price+",��ͷ�Ʒ�:"+cprice);
		return price+cprice;	
	}
	
	private static Double countFprice(int btime,int etime,int bhour,int bmin,int ehour,int emin,
			Double price,int ftime,int dayUnit,Long duration,int countless){
		Double ymoney = 0d;
		//�ؼ�������ռ�ʱ���ڵ�ʱ��,ҹ�䲻�����Ż�
		if(bhour>ehour){//ʱ��7-21��ͣ��23:00-7:05,ftime=30,dayUnit = 15
			if(bhour<etime){
				duration = Long.valueOf((etime-bhour-1)*60+(60-bmin));
			}else if(ehour>=btime){//ʱ��7-21��ͣ��23:49-2:11,ftime=30,dayUnit = 15
				duration=Long.valueOf((ehour-btime)*60+bmin);
			}else {//ʱ��7-21��ͣ��23:49-2:11,ftime=30,dayUnit = 15
				duration=0L;
			}
		}else if(ehour<btime||bhour>=etime){
			return ymoney;
		}else if(ehour==btime){//ʱ��7-21��ͣ��6:10-7:50,ftime=30,dayUnit = 15
			if(ehour>bhour)
				duration = Long.valueOf(emin);
		}else if(ehour>btime){//ʱ��7-21��ͣ��6:10-8:50,ftime=30,dayUnit = 15,countless = 10;
			if(bhour<btime){//ʱ��7-21��ͣ��6:10-8:50,ftime=30,dayUnit = 15,countless = 10;
				duration = duration-((btime-bhour-1)*60+(60-bmin));
			}else if(bhour>=btime){
				if(ehour>=etime){//ʱ��7-21��ͣ��18:10-22:50,ftime=30,dayUnit = 15,countless = 10;
					duration = duration -((ehour-etime)*60+emin);
				}
			}
		}
		//��ʼ����
		if(duration>ftime){
			ymoney = (ftime/dayUnit)*price;
		}else {
			ymoney = (duration/dayUnit)*price;
			if(duration%dayUnit>countless)
				ymoney +=price;
		}
		return ymoney;
	}
	*//**
	 * @param btime �ռ俪ʼʱ��
	 * @param etime �ռ����ʱ��
	 * @param start ͣ����ʼʱ��
	 * @param end ͣ������ʱ�� 
	 * @param dunit �ռ�Ʒѵ�λ�����ӣ� 
	 * @param dft �ռ����ʱ��
	 * @param nft ҹ�����ʱ��
	 * @param dfpt �ռ����ʱ�����Ƿ��շ�    1��� 0�շ�
	 * @param nfpt ҹ�����ʱ�����Ƿ��շ�    1��� 0�շ�
	 * @return List<ͣ����ʼʱ�䣬ͣ������ʱ��>
	 *//*
	private static List<Long> getStart(Integer btime,Integer etime,Long start,Long end,Integer dunit,
			Integer dft,Integer nft,Integer dfpt,Integer nfpt){
		List<Long> reslut = new ArrayList<Long>();
//		if(dft==0&&nft==0){
//			return reslut;
//		}
			
		Long duration = end-start;//ԭͣ��ʱ�� ���룩
		//System.out.println("ԭ��ʼʱ��:"+TimeTools.getTime_yyyyMMdd_HHmmss(start*1000)+",ԭ����ʱ�䣺"+TimeTools.getTime_yyyyMMdd_HHmmss(end*1000));
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		calendar.setTimeInMillis(start*1000);
		int bhour = calendar.get(Calendar.HOUR_OF_DAY);//ͣ����ʼСʱ 
		int bmin  = calendar.get(Calendar.MINUTE); //ͣ����ʼ����
		calendar.setTimeInMillis(end*1000);
		int ehour = calendar.get(Calendar.HOUR_OF_DAY);//ͣ������Сʱ 
		
		if(start>end)//ͣ������ʱ��Сͣ����ʼʱ�䣬�ǿ���ҹ��ֻ�����һ���ͣ����,��ͣ������ʱ������Ϊ�����24��
			ehour=24;
		
		if(bhour<btime){//ͣ����ʼСʱ С�� �ռ俪ʼСʱ    �ռ�ʱ��7-21��ͣ��6:49-?
			if(ehour<btime){//ȫ��ҹ��ʱ���� ---�ռ�ʱ��7-21��ͣ��6:49-6:59
				if(nfpt==1){//ҹ�����ʱ�����շѣ�ͣ����ʼʱ�������һ��ҹ�����ʱ����λ��ͣ������ʱ�䲻��
					if(nft!=0&&duration<=nft*60){//ͣ����ʱ��С�ڻ�������ʱ�����Ҳ��շѣ����óɿ�ʼʱ����ڽ���ʱ�䣬�����Ͳ��շ��ˡ�
						start=end;
					}else {//ͣ����ʱ�����������ʱ����ͣ����ʼʱ�������һ�����ʱ��.
						start = start+nft*60;
					}
				}else{//ҹ�����ʱ�����շ�
					if(nft!=0&&duration<=nft*60){//ͣ����ʱ��С�ڻ�������ʱ�����Ҳ��շѣ����óɿ�ʼʱ����ڽ���ʱ�䣬�����Ͳ��շ��ˡ�
						start=end;
					}
				}
			}else if(ehour>=btime){//������ҹ��ʱ���ڣ�ֻ��ҹ��ʱ���ڵ���� ---�ռ�ʱ��7-21��ͣ��6:49-7:59
				int nlong = ((btime-bhour-1)*60)+(60-bmin);//ҹ��ʱ���ڵ�ͣ��ʱ��  ---11����
				if(nlong>=nft){//ҹ��ͣ��ʱ�����ڻ����һ��ҹ�����ʱ����λ
					if(nfpt==1)//ҹ�����ʱ�����շѣ�ͣ����ʼʱ�������һ��ҹ�����ʱ����λ��ͣ������ʱ�䲻��
						start = start+nft*60;
					else {//ҹ�����ʱ�����շ�
						if(dft!=0&&duration<=dft*60)//�����ʱ���ڲ��շ�
							start=end;
					}
				}else {//ҹ��ͣ��ʱ������һ���Ʒѵ�λ
					if(nfpt==1){
						start =getBtime(start, btime);//ҹ�����ʱ�����շѣ�ͣ����ʼʱ����ռ俪ʼʱ�俪ʼ��ͣ������ʱ�䲻��
					}else{
						if(dfpt!=0&&duration<=dfpt*60)//�����ʱ���ڲ��շ�
							start=end;
					}
				}
			}
		}else if(bhour>=btime&&bhour<etime){//ͣ����ʼСʱ ���ڻ���� �ռ俪ʼСʱ   -- �ռ�ʱ��7-21��ͣ��7:01-?
			if(ehour<etime){//ͣ������ʱ��С���ռ����ʱ����ͣ������ʱ��С���ռ����ʱ�䣬ȫ���ռ�ʱ�Σ�-- �ռ�ʱ��7-21��ͣ��7:01-20:30
				if(duration<=dft*60){//ͣ��ʱ��С�ڻ�����ռ����ʱ��
					start = end;
				}else if(duration>=dft*60){//ͣ��ʱ�������ռ����ʱ��
					if(dfpt==1){//�ռ����ʱ�����շѣ�ͣ����ʼʱ�������һ���ռ����ʱ����λ��ͣ������ʱ�䲻��
						start =start +dft*60;
					}else {//�ռ����ʱ�����շ�
						//���ǰ���֮ǰ�ķ�ʽ����
					}
				}
			}else if(ehour>=etime){//�������ռ䣬������ҹ��
				//�������ռ�ʱ���ڵ�ʱ��
				int dLong = ((etime-bhour-1)*60)+(60-bmin);
				if(dLong<=dft){//�ռ�ʱ��С�����ʱ����ȥ�����ʱ��
					start = getBtime(start, etime);
				}else if(dLong>dft){//�ռ�ʱ���������ʱ��
					if(dfpt==1){//���,ͣ����ʼʱ�������һ���ռ����ʱ����λ��ͣ������ʱ�䲻��
						start =start +dft*60;
						dLong = dLong-dft;
					}else {//�շ�
						//���ǰ���֮ǰ�ķ�ʽ����
					}
					//�����ռ䲻��һ���Ʒѵ�λ������
					if(dLong<dunit){//ͣ����ʼ�ͽ���ʱ����ǰ��һ��ʱ���ռ�һ���շѵ�λ-�ռ�ͣ��ʱ����
						start = start -(dunit-dLong)*60;
						end = end -(dunit-dLong)*60;
					}
				}
			}
		}else if(bhour>=etime){//ͣ����ʼСʱ ���ڻ���� �ռ俪ʼСʱ   -- �ռ�ʱ��7-21��ͣ��21:01-?
			if(nfpt==1){//ҹ�����ʱ�����շѣ�ͣ����ʼʱ�������һ��ҹ�����ʱ����λ��ͣ������ʱ�䲻��
				if(nft!=0&&duration<=nft*60){//ͣ����ʱ��С�ڻ�������ʱ�����Ҳ��շѣ����óɿ�ʼʱ����ڽ���ʱ�䣬�����Ͳ��շ��ˡ�
					start=end;
				}else {//ͣ����ʱ�����������ʱ����ͣ����ʼʱ�������һ�����ʱ��.
					start = start+nft*60;
				}
			}else{//ҹ�����ʱ�����շ�
				if(nft!=0&&duration<=nft*60){//ͣ����ʱ��С�ڻ�������ʱ�����Ҳ��շѣ����óɿ�ʼʱ����ڽ���ʱ�䣬�����Ͳ��շ��ˡ�
					start=end;
				}
			}
		}
		if(start>end)
			start=end;
		reslut.add(start);
		reslut.add(end);
		//System.out.println("�ֿ�ʼʱ��:"+TimeTools.getTime_yyyyMMdd_HHmmss(start*1000)+",�ֽ���ʱ�䣺"+TimeTools.getTime_yyyyMMdd_HHmmss(end*1000));
		return reslut;
	}

	private static Long getBtime(Long start,int bhour){
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		calendar.setTimeInMillis(start*1000);
		calendar.set(Calendar.HOUR_OF_DAY, bhour);
		calendar.set(Calendar.MINUTE,0);
		return calendar.getTimeInMillis()/1000;
	}*/
	
}
