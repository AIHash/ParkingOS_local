package com.zld.schedule;

import java.util.Calendar;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.zld.service.DataBaseService;
import com.zld.utils.ParkingMap;

public class ParkSchedule extends TimerTask {
	
	DataBaseService dataBaseService;
	
	public ParkSchedule(DataBaseService dataBaseService ){
		this.dataBaseService = dataBaseService;
	}

	private static Logger log = Logger.getLogger(ParkSchedule.class);

	@Override
	public void run() {
		// TODO Auto-generated method stub

		Calendar calendar = Calendar.getInstance();
		log.info("��ʼ��ʱ����");
		//loadParking();
		//ÿ���µĵ�һ��Ӌ��
		if(calendar.get(Calendar.DAY_OF_MONTH)==1){
			log.info("��ʼִ��");
			//start();
		}else {
			//start();
			log.info("����ִ��ʱ�䣡");
		}
		//��ճ������û������� 
		ParkingMap.clearMap();
//		start();
	}

	/**
	 * ��ʼͳ��
	 */
	/*@SuppressWarnings("rawtypes")
	private void start(){
		File file = new File(
				"/data/jtom/webapps/zld/parkpics");
		Map<String, String> extMap = new HashMap<String, String>();
		extMap.put(".jpg", "image/jpeg");
		extMap.put(".jpeg", "image/jpeg");
		extMap.put(".png", "image/png");
		extMap.put(".gif", "image/gif");
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			// System.err.println(files.length);
			InputStream is = null;
			BufferedInputStream in = null;
			ByteArrayOutputStream byteout = null;
			int i=0;
			try {
				for (File f : files) {
					String fileName = f.getName();
					Long comid=0L;
					Long ctime=0L;
					try {
						comid = Long.valueOf(fileName.split("_")[0]);
						ctime = Long.valueOf(fileName.split("_")[1].substring(0, 10));
					} catch (Exception e) {
						continue;
					}
					String file_ext = fileName.substring(fileName
							.lastIndexOf("."));

					is = new FileInputStream(f);
					in = new BufferedInputStream(is);
					byteout = new ByteArrayOutputStream(1024);

					byte[] temp = new byte[1024];
					int bytesize = 0;
					while ((bytesize = in.read(temp)) != -1) {
						byteout.write(temp, 0, bytesize);
					}

					byte[] content = byteout.toByteArray();
					DB mydb = MongoClientFactory.getInstance()
							.getMongoDBBuilder("zld");
					mydb.requestStart();

					DBCollection collection = mydb.getCollection("park_pics");
					// DBCollection collection =
					// mydb.getCollection("records_test");

					BasicDBObject document = new BasicDBObject();
					document.put("comid", comid);
					document.put("ctime", ctime);
					document.put("type", extMap.get(file_ext));
					document.put("content", content);
					document.put("filename", fileName);
					// ��ʼ����
					mydb.requestStart();
					collection.insert(document);
					// ��������
					mydb.requestDone();
					//in.close();
					//is.close();
					//byteout.close();
					i++;
					System.out.println(i);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (in != null)
						in.close();
					if (byteout != null)
						byteout.close();
					if (is != null)
						is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}*/
	/**
	 * ����ȫ��ͣ����
	 */
	/*private void loadParking () {
		log.info("��ʼ����ͣ����....");
		ParkingMap.init(dataBaseService);
		log.info("ͣ����������ϣ���"+ParkingMap.getParkingNumber()+"��ͣ����");
	}*/
	
}
