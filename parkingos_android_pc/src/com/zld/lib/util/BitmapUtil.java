package com.zld.lib.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

public class BitmapUtil {

	/** 
	 *  ����ͼƬ  
	 * @param bm ��Ҫת����bitmap 
	 * @param newWidth�µĿ� 
	 * @param newHeight�µĸ�   
	 * @return ָ����ߵ�bitmap 
	 */ 
	public static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){   
		if(bm == null){
			return null;
		}
		// ���ͼƬ�Ŀ��   
		int width = bm.getWidth();   
		int height = bm.getHeight();   
		// �������ű���   
		float scaleWidth = ((float) newWidth) / width;   
		float scaleHeight = ((float) newHeight) / height;   
		// ȡ����Ҫ���ŵ�matrix����   
		Matrix matrix = new Matrix();   
		matrix.postScale(scaleWidth, scaleHeight);   
		// �õ��µ�ͼƬ   www.2cto.com
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);   
		return newbm;   
	}  

	public static Bitmap compressBitmap(Bitmap image){
		ByteArrayOutputStream baos =  new  ByteArrayOutputStream();  
		image.compress(Bitmap.CompressFormat.JPEG,  100 , baos);
		int  options =  100 ;  
		while  ( baos.toByteArray().length /  1024 > 32 ) {  
			baos.reset();
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -=  10 ;
		}  
		ByteArrayInputStream isBm =  new  ByteArrayInputStream(baos.toByteArray());  
		Bitmap bitmap = BitmapFactory.decodeStream(isBm,  null ,  null );
		return bitmap; 
	}

	// ѹ��ͼƬ
	public static Bitmap comp(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// �ж����ͼƬ����1M,����ѹ������������ͼƬ��BitmapFactory.decodeStream��ʱ���
			baos.reset();// ����baos�����baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// ����ѹ��50%����ѹ��������ݴ�ŵ�baos��
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// ��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��
		newOpts.inJustDecodeBounds = true;
		newOpts.inPreferredConfig = Config.RGB_565;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// ���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ
		float hh = 200f;// �������ø߶�Ϊ800f
		float ww = 240f;// �������ÿ��Ϊ480f
		// ���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��
		int be = 1;// be=1��ʾ������
		if (w > h && w > ww) {// �����ȴ�Ļ����ݿ�ȹ̶���С���� 
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// ����߶ȸߵĻ����ݿ�ȹ̶���С����
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// �������ű���
		// ���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// ѹ���ñ�����С���ٽ�������ѹ��
	}

	// ����ѹ����ѹ��ͼƬ�е���
	private static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// ����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
		int options = 100;
		while (baos.toByteArray().length / 1024 > 10) { // ѭ���ж����ѹ����ͼƬ�Ƿ����10kb,���ڼ���ѹ��
			baos.reset();// ����baos�����baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// ����ѹ��options%����ѹ��������ݴ�ŵ�baos��
			options -= 10;// ÿ�ζ�����10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// ��ѹ���������baos��ŵ�ByteArrayInputStream��
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// ��ByteArrayInputStream��������ͼƬ
		return bitmap;
	}

	public static void recyBitmap(Bitmap bitmap) {
		// TODO Auto-generated method stub
		Log.e("BitmapUtil","bitmap:"+bitmap+"ͼƬ�Ƿ���գ�"+!bitmap.isRecycled());
		if(bitmap != null && !bitmap.isRecycled()){
			bitmap.recycle();  
			bitmap = null;  
			System.gc();
		}
	}
}
