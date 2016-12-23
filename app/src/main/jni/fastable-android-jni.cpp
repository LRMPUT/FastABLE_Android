#include <jni.h>
#include <opencv2/opencv.hpp>
#include <vector>
#include <android/log.h>

#include <sys/time.h>

#include "src/OpenABLE.h"

#define TAG "FastABLE_jni"

extern "C" {

JNIEXPORT jfloatArray JNICALL
Java_pl_poznan_put_fastable_MainActivity_computeJni(JNIEnv *env,
                                                    jobject instance,
                                                    jstring configFileJstring,
                                                    jobjectArray trainImgsArray,
                                                    jintArray trainLengthsArray,
                                                    jobjectArray testImgsArray,
                                                    jint testLength) {

    __android_log_print(ANDROID_LOG_DEBUG, TAG, "computeJni()");

    // Get configuration file path
    const char* configFile = env->GetStringUTFChars(configFileJstring, 0);
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "config File = %s", configFile);
    // Construct OpenABLE object
    OpenABLE openable(configFile);
    // Release char array
    env->ReleaseStringUTFChars(configFileJstring, configFile);

    // Convert the array
    jboolean isCopy;
    jint* trainLengths = env->GetIntArrayElements(trainLengthsArray, &isCopy);
    int trainSegm = env->GetArrayLength(trainLengthsArray);

    // Sum of description times
    double t_description = 0;
    // Number of described images
    int descImgCnt = 0;

    __android_log_print(ANDROID_LOG_DEBUG, TAG, "trainSegm = %d", trainSegm);

    std::vector<std::vector<cv::Mat>> trainDesc;
    int trainCnt = 0;
    for (int s = 0; s < trainSegm; s++)
    {
        // Add new vector for current segment
        trainDesc.push_back(std::vector<cv::Mat>());
        __android_log_print(ANDROID_LOG_DEBUG, TAG, "trainLengths[%d] = %d", s, trainLengths[s]);
        for(int i = 0; i < trainLengths[s]; ++i) {
            // Get image file name
            jstring curFile = (jstring) env->GetObjectArrayElement(trainImgsArray, trainCnt++);
            const char* curFileRaw = env->GetStringUTFChars(curFile, 0);
//            __android_log_print(ANDROID_LOG_DEBUG, TAG, "curFileRaw = %s", curFileRaw);

//            double t1 = getTickCount();
            // Read image
            cv::Mat img = cv::imread(curFileRaw, cv::IMREAD_GRAYSCALE);
            if(img.empty()){
                __android_log_print(ANDROID_LOG_ERROR, TAG, "img.size() = (%d, %d)", img.cols, img.rows);
            }
            // Compute descriptor and push to vector
            trainDesc.back().push_back(openable.global_description(img));
//            double t2 = getTickCount();
//
//            t_description += 1000.0 * (t2 - t1) / getTickFrequency();
//            ++descImgCnt;

            env->ReleaseStringUTFChars(curFile, curFileRaw);
            // Release reference (max 512 references)
            env->DeleteLocalRef(curFile);
        }
    }

    env->ReleaseIntArrayElements(trainLengthsArray, trainLengths, JNI_ABORT);

    __android_log_print(ANDROID_LOG_DEBUG, TAG, "testLength = %d", testLength);
    std::vector<cv::Mat> testDesc;
    for(int i = 0; i < testLength; ++i){
        // Get image file name
        jstring curFile = (jstring) env->GetObjectArrayElement(testImgsArray, i);
        const char* curFileRaw = env->GetStringUTFChars(curFile, 0);

        // Read image
        cv::Mat img = cv::imread(curFileRaw, cv::IMREAD_GRAYSCALE);
        if(img.empty()){
            __android_log_print(ANDROID_LOG_ERROR, TAG, "img.size() = (%d, %d)", img.cols, img.rows);
        }
        // Compute descriptor and push to vector, measure description time
        double t1 = getTickCount();
        testDesc.push_back(openable.global_description(img));
        double t2 = getTickCount();

        // Convert to milliseconds
        t_description += 1000.0 * (t2 - t1) / getTickFrequency();
        ++descImgCnt;

        env->ReleaseStringUTFChars(curFile, curFileRaw);
        // Release reference (max 512 references)
        env->DeleteLocalRef(curFile);
    }

    __android_log_print(ANDROID_LOG_DEBUG, TAG, "computation");

    // Run computation
    std::vector<cv::Mat> oaSimMat, faSimMat;
    openable.computeVisualPlaceRecognition(trainDesc,
                                           testDesc,
                                           oaSimMat,
                                           faSimMat,
                                           false /* do not save similarity matrix */);

    float tMatchOa, tMatchFa;
    int matchImgCnt;
    openable.getMatchingTimes(tMatchOa, tMatchFa, matchImgCnt);

    __android_log_print(ANDROID_LOG_DEBUG, TAG, "desc time = %f ms\n"
                                                "desc cnt = %d\n"
                                                "oa match time = %f ms\n"
                                                "fa match time = %f ms\n"
                                                "match cnt = %d",
                                                t_description,
                                                descImgCnt,
                                                tMatchOa,
                                                tMatchFa,
                                                matchImgCnt);

    // Pack results to an array
    jfloat resVals[2];
    resVals[0] = t_description + tMatchOa;
    resVals[1] = t_description + tMatchFa;
    jfloatArray res = env->NewFloatArray(2);
    env->SetFloatArrayRegion(res, 0, 2, resVals);

    __android_log_print(ANDROID_LOG_DEBUG, TAG, "end computeJni()");

    return res;
}

}