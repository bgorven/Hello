#include "main.h"
#include "org_bven_hello_Native.h"

#define _WIN32_DCOM
#include <iostream>
using namespace std;
#include <comdef.h>
#include <Wbemidl.h>

# pragma comment(lib, "wbemuuid.lib")

/*
 * Class:     org_bven_hello_Native
 * Method:    getGreeting
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_bven_hello_Native_getGreeting
  (JNIEnv *env, jclass) {
	return env->NewStringUTF("Hello");
}

/*
 * Class:     org_bven_hello_Native
 * Method:    sendGreeting
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_bven_hello_Native_sendGreeting
  (JNIEnv *env, jclass, jstring message) {
	const char *string = env->GetStringUTFChars(message, NULL);
	cout << string;
	env->ReleaseStringUTFChars(message, string);
}
