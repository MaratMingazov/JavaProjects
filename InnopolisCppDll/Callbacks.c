#include <stdio.h>

#include "lips_aj_jni_Callbacks.h"

//using namespace std;

JNIEXPORT void JNICALL Java_lips_aj_jni_Callbacks_nativeMethod
(JNIEnv *env, jobject obj, jint depth){
	jclass cls = (*env)->GetObjectClass(env, obj);
	jmethodID methodId = (*env)->GetMethodID(env, cls, "callback", "(I)V");
	if(methodId == 0) return;
	printf("In C, depth = %d, about to enter Java\n", (int)depth);
	(*env)->CallVoidMethod(env, obj, methodId, depth);
	printf("In C, depth = %d, back from Java\n", (int)depth);
	return;
}


