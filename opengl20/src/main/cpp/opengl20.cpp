#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_sgf_opengl20_CppOpenGLHandler_getStringForJni(JNIEnv *env, jobject thiz) {
    std::string str = "get String for cpp openGl 2.0";
    return env->NewStringUTF(str.c_str());
}