package com.zld.lib.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.zld.bean.AppInfo;
import com.zld.lib.constant.Constant;
import com.zld.ui.ZldNewActivity;

import android.util.Log;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadUtil {
	private static final String TAG = "UploadUtil";
	private static final int TIME_OUT = 10 * 1000; // ��ʱʱ��
	private static final String CHARSET = "utf-8"; // ���ñ���

	/**
	 * android�ϴ��ļ���������
	 * 
	 * @param file
	 *            ��Ҫ�ϴ����ļ�
	 * @param RequestURL
	 *            �����rul
	 * @return ������Ӧ������
	 */
	public static String uploadFile(InputStream is, String RequestURL) {
		String result = null;
		String BOUNDARY = UUID.randomUUID().toString(); // �߽��ʶ �������
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // ��������

		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // ����������
			conn.setDoOutput(true); // ���������
			conn.setUseCaches(false); // ������ʹ�û���
			conn.setRequestMethod("POST"); // ����ʽ
			conn.setRequestProperty("Charset", CHARSET); // ���ñ���
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
			Log.i(TAG, "-->>requestURL:" + RequestURL);
			if (is != null) {
				/**
				 * ���ļ���Ϊ�գ����ļ���װ�����ϴ�
				 */
				DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * �����ص�ע�⣺ name�����ֵΪ����������Ҫkey ֻ�����key �ſ��Եõ���Ӧ���ļ�
				 * filename���ļ������֣�������׺���� ����:abc.png
				 */
				sb.append("Content-Disposition: form-data; name=\"img\"; filename=\"" + "zhenlaidian.jpg" + "\""
						+ LINE_END);
				sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * ��ȡ��Ӧ�� 200=�ɹ� ����Ӧ�ɹ�����ȡ��Ӧ����
				 */
				int res = conn.getResponseCode();
				Log.e(TAG, "response code:" + res);
				if (res == 200) {
					Log.e(TAG, "request success");
					InputStream input = conn.getInputStream();
					StringBuffer sb1 = new StringBuffer();
					int ss;
					while ((ss = input.read()) != -1) {
						sb1.append((char) ss);
					}
					result = sb1.toString();
					Log.e(TAG, "result : " + result);
				} else {
					Log.e(TAG, "request error");
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * �ϴ��绰��¼
	 * 
	 * @param pathurl
	 * @return
	 */
	public static String uploadRecord(String pathurl) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(pathurl);
		String result = null;
		try {
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}

	public static String doGet(String url) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		String result = null;
		try {
			System.out.println("-->url=" + url);
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = new String(EntityUtils.toByteArray(response.getEntity()), "GBK");// .getContentCharSet(response.getEntity());
				System.out.println("-->url=" + url + ",result=" + result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return result;
	}

	/**
	 * �ϴ��ļ�
	 */
	public static void uploadFile(final InputStream bitmapToInputStream) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				// String request =
				// "http://192.168.199.122/zld/collectorrequest.do?action=uplogfile";
				// String url = request + "&"
				// + "token=" + AppInfo.getInstance().getToken();
				String url = Constant.requestUrl + "collectorrequest.do?action=uplogfile&" + "token="
						+ AppInfo.getInstance().getToken();
				Log.e(TAG, "�����url-->>" + url);
				String result = UploadUtil.uploadFile(bitmapToInputStream, url);
				Log.e(TAG, "�ϴ��ļ��ķ��ؽ����-->>" + result);
				// Map<String, String> resultMap =
				// StringUtils.getMapForJson(result);
				// Message msg = new Message();
				// if (resultMap != null) {
				//
				// }
			}
		}.start();
	}
	
	public static void testUploadFile()  {  
		String url = Constant.requestUrl + "collectorrequest.do?action=uplogfile&" + "token="
				+ AppInfo.getInstance().getToken();
        //����OkHttpClient����  
        OkHttpClient mOkHttpClient = new OkHttpClient();  
        File file=null;
		try {
			file = FileUtil.createSDFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
  
        //application/octet-stream ��ʾ�����Ƕ�����������֪�ļ���������  
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);  
  
        RequestBody requestBody = new MultipartBody.Builder()
        		.setType(MultipartBody.FORM) 
                .addPart(Headers.of(  
                        "Content-Disposition",  
                        "form-data; name=\"username\""),  
                        RequestBody.create(null, "***"))  
                .addPart(Headers.of(  
                        "Content-Disposition",  
                        "form-data; name=\"img\";filename=\"zhenlaidian.jpg\""), fileBody)
//                .addFormDataPart("Content-Disposition",  
//                        "form-data; name=\"img\";filename=\"zhenlaidian.jpg\"")
//                .addFormDataPart("Content-Type", "application/octet-stream; charset=utf-8")
                .build();  
//        "Content-Disposition: form-data; name=\"img\"; filename=\"" + "zhenlaidian.jpg" + "\""
//		+ LINE_END
        Request request = new Request.Builder()  
                .url(url)  
                .post(requestBody)  
                .build();  
  
        Call call = mOkHttpClient.newCall(request);  
        call.enqueue(new Callback()  
        {  
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				Log.e(TAG, "fail ");
			}

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				Log.e(TAG, "response "+arg1.body().string());
			}  
        });  
    }  
}
