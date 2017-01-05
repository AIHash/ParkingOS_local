#ifndef TOOLS_H_H
#define TOOLS_H_H

#include<jni.h>
#include <android/log.h>
		
#define LOG_TAG "ffmpeg" // ������Զ����LOG�ı�ʶ
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)



void timeBegin(int i);
void timeEnd(int i);

#define FOUR4_BYTE_BOUNDARY_LEN(len)  (((len)*3+3)/4*4)
#endif
