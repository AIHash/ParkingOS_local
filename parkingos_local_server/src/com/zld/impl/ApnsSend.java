package com.zld.impl;

import java.util.ArrayList;
import java.util.List;

import javapns.back.PushNotificationManager;
import javapns.back.SSLConnectionHelper;
import javapns.data.Device;
import javapns.data.PayLoad;

public class ApnsSend
{/*
    public static void main(String[] args) throws Exception
    {
        String deviceToken = "b9ae03f915a1f74d2281bd81109b4e3fbd417a6cfbd4f579f892b37b0f25ed69";
        String alert = "�ҵ�push����";//push������
        int badge = 1;//ͼ��С��Ȧ����ֵ
        String sound = "default";//����

        List<String> tokens = new ArrayList<String>();
        tokens.add(deviceToken);
        String certificatePath = "C:\\Users\\Administrator\\Desktop\\apns-dev-cert.p12";
        String certificatePassword = "tingchebao";//�˴�ע�⵼����֤�����벻��Ϊ����Ϊ������ᱨ��
        boolean sendCount = true;

        try
        {
            PushNotificationPayload payLoad = new PushNotificationPayload();
            payLoad.addAlert(alert); // ��Ϣ����
            payLoad.addBadge(badge); // iphoneӦ��ͼ����С��Ȧ�ϵ���ֵ
            if (!StringUtils.isBlank(sound))
            {
                payLoad.addSound(sound);//����
            }
            PushNotificationManager pushManager = new PushNotificationManager();
            //true����ʾ���ǲ�Ʒ�������ͷ��� false����ʾ���ǲ�Ʒ�������ͷ���
            pushManager.initializeConnection(new AppleNotificationServerBasicImpl(certificatePath, certificatePassword, true));
            List<PushedNotification> notifications = new ArrayList<PushedNotification>();
            // ����push��Ϣ
            if (sendCount)
            {
                Device device = new BasicDevice();
                device.setToken(tokens.get(0));
                PushedNotification notification = pushManager.sendNotification(device, payLoad, true);
                notifications.add(notification);
            }
            else
            {
                List<Device> device = new ArrayList<Device>();
                for (String token : tokens)
                {
                    device.add(new BasicDevice(token));
                }
                notifications = pushManager.sendNotifications(payLoad, device);
            }
            List<PushedNotification> failedNotifications = PushedNotification.findFailedNotifications(notifications);
            List<PushedNotification> successfulNotifications = PushedNotification.findSuccessfulNotifications(notifications);
            int failed = failedNotifications.size();
            int successful = successfulNotifications.size();
            pushManager.stopConnection();
            System.out.println(failed+":"+successful);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    */
    
    public static void main(String[] args) throws Exception
    {
       /* String deviceToken = "b9ae03f915a1f74d2281bd81109b4e3fbd417a6cfbd4f579f892b37b0f25ed69";//iphone�ֻ���ȡ��token
        String alert = "�ҵ�push����";//push������
        int badge = 100;//ͼ��С��Ȧ����ֵ
        String sound = "default";//����
        sendTiangouAPNS(deviceToken, alert, badge, sound, "10001");*/
        try {
            String deviceToken = "b9ae03f915a1f74d2281bd81109b4e3fbd417a6cfbd4f579f892b37b0f25ed69";
            //�����͵�iphoneӦ�ó����ʾ��      
           // PropertyConfigurator.configure("bin/log4j.properties");
          //  Logger console = Logger.getLogger(ApnsSend.class);
           // String mesgString = "{\"mtype\":\"0\",\"msgid\":\"1\",\"info\":{\"total\":\"0.0\",\"parkname\":\"���ܳ���\",\"address\":\"�����к������ϵ�����9��-d��\",\"etime\":\"1414218585\",\"state\":\"0\",\"btime\":\"1414218585\",\"parkid\":\"1475\",\"orderid\":\"176729\"}}";
    		
            PayLoad payLoad = new PayLoad();
            payLoad.addAlert("������Ϣ");
            payLoad.addBadge(1);
            payLoad.addSound("default");
            payLoad.addCustomDictionary("payload", "3339900");
            
            PushNotificationManager pushManager = PushNotificationManager.getInstance();
            String device = ""+System.currentTimeMillis();
			pushManager.addDevice(device, deviceToken);

            String host= "gateway.push.apple.com";  //ƻ�����ͷ�����
           // String host= "gateway.sandbox.push.apple.com";  //�����õ�ƻ�����ͷ�����
            int port = 2195;
            
            String certificatePath = "C:\\Users\\Administrator\\Desktop\\apns-dev-cert.p12"; //�ղ���macϵͳ�µ�����֤��
              
            String certificatePassword= "tingchebao";
            
            pushManager.initializeConnection(host, port, certificatePath,certificatePassword, SSLConnectionHelper.KEYSTORE_TYPE_PKCS12);
              
            //Send Push
            Device client = pushManager.getDevice(device);
            pushManager.sendNotification(client, payLoad); 
            pushManager.stopConnection();
            pushManager.removeDevice(device);
            System.out.println("push succeed!");
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("e.getMessage() = " + e.getMessage());
        }
           
    }
    public static void sendTiangouAPNS(String deviceToken, String message, int badge, String sound, String ab)
    {
        List<String> tokens = new ArrayList<String>();
        tokens.add(deviceToken);
       // String certificatePath = "";
        String certificatePath = "C:\\Users\\Administrator\\Desktop\\apns-dev-cert.p12";
        String certificatePassword = "tingchebao";//�˴�ע�⵼����֤�����벻��Ϊ����Ϊ������ᱨ��
      //  String certificatePassword = "123456";//�˴�ע�⵼����֤�����벻��Ϊ����Ϊ������ᱨ��
       // new ApnsSend().sendpush(tokens, message, badge, sound, certificatePath, certificatePassword, true);
    }
    /**
     * apple�����ͷ���
     * @param tokens iphone�ֻ���ȡ��token
     * @param message ������Ϣ������
     * @param count Ӧ��ͼ����С��Ȧ�ϵ���ֵ
     * @param sound ����
     * @param ab ϵͳ
     * @param certificatePath ֤��·��
     * @param certificatePassword ֤������
     * @param sendCount ��������Ⱥ�� true������ false��Ⱥ��
     */
   /* private void sendpush(List<String> tokens, String message, int badge, String sound, String certificatePath, String certificatePassword, boolean sendCount)
    {
        try
        {
            PushNotificationPayload payLoad = new PushNotificationPayload();
            payLoad.addAlert(message); // ��Ϣ����
            payLoad.addBadge(badge); // iphoneӦ��ͼ����С��Ȧ�ϵ���ֵ
            if (!StringUtils.isBlank(sound))
            {
                payLoad.addSound(sound);//����
            }
            PushNotificationManager pushManager = new PushNotificationManager();
            //true����ʾ���ǲ�Ʒ�������ͷ��� false����ʾ���ǲ�Ʒ�������ͷ���
            pushManager.initializeConnection(new AppleNotificationServerBasicImpl(certificatePath, certificatePassword, false));
            List<PushedNotification> notifications = new ArrayList<PushedNotification>();
            // ����push��Ϣ
            if (sendCount)
            {
               // log.debug("--------------------------apple ���� ��-------");
                Device device = new BasicDevice();
                device.setToken(tokens.get(0));
                PushedNotification notification = pushManager.sendNotification(device, payLoad, true);
                notifications.add(notification);
            }
            else
            {
               // log.debug("--------------------------apple ���� Ⱥ-------");
                List<Device> device = new ArrayList<Device>();
                for (String token : tokens)
                {
                    device.add(new BasicDevice(token));
                }
                notifications = pushManager.sendNotifications(payLoad, device);
            }
            List<PushedNotification> failedNotifications = PushedNotification.findFailedNotifications(notifications);
            List<PushedNotification> successfulNotifications = PushedNotification.findSuccessfulNotifications(notifications);
            int failed = failedNotifications.size();
            int successful = successfulNotifications.size();
            // pushManager.stopConnection();
            System.out.println(failed+":"+successful);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/

}
