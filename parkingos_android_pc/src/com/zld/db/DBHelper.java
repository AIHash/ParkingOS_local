package com.zld.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME="tingchebao.db";
	public static final String IMAGE_TABLE="orderimg";
	public static final String ACCOUNT_INFO="accountinfo";
	public static final String CAMERA_INFO="camerainfo";
	public static final String LED_INFO="ledinfo";
	public static final int VERSION=3;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null,VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/*
		 * id 
		 * account �˻�
		 * orderid ����id
		 * lefttop ͼƬ���Ͻ�x����
		 * rightbottom ͼƬ���Ͻ�y����
		 * type ͨ������
		 * width ͼƬ��
		 * height ͼƬ��
		 * imgpath ͼƬ·��
		 */
		String sqlCreate = "create table if not exists "+IMAGE_TABLE+
				" (id integer PRIMARY KEY AUTOINCREMENT,account text,carnumber text,orderid text,lefttop text,"
				+ "rightbottom text,type text,width text,height text,imghomepath text,imgexitpath text,homeimgup text,exitimgup text) ";
		db.execSQL(sqlCreate);

		String sqlCreateAccount = "create table if not exists "+ACCOUNT_INFO+
				" (id integer PRIMARY KEY AUTOINCREMENT,account text,username text,password text) ";
		db.execSQL(sqlCreateAccount);

		String sqlCreateCamera = "create table if not exists "+CAMERA_INFO+
				" (id integer PRIMARY KEY AUTOINCREMENT,"
				+ "cameraid text,cameraname text,cameraip text,passtype text,passname text,passid text) ";
		db.execSQL(sqlCreateCamera);
		String sqlCreateLED = 
				"create table if not exists "+LED_INFO+""
						+ "(id integer PRIMARY KEY AUTOINCREMENT,"
						+ "ledid text,ledip text,ledport text,leduid text,"
						+ "movemode text,movespeed text,dwelltime text,ledcolor text,"
						+ "showcolor text,typeface text,typesize text,matercont text,"
						+ "passid text,passtype text,passname text,width text,height text,type text,rsport text) ";
		db.execSQL(sqlCreateLED);	

		System.out.println("onCreate....");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + IMAGE_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_INFO);
		db.execSQL("DROP TABLE IF EXISTS " + CAMERA_INFO);
		db.execSQL("DROP TABLE IF EXISTS " + LED_INFO);
		onCreate(db); 
		System.out.println("onUpgrade....");
	}
}
