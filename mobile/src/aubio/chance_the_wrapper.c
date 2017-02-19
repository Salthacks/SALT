#include "aubio.h"
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>

JNIEXPORT jdouble JNICALL
Java_com_salthacks_salt_MainActivity_processAubio(JNIEnv *env, jobject instance, jint in_size, jint hop_size, jint sample_rate, jfloatArray jIn) {

    const uint_t HOPSIZE = (uint_t) hop_size;
    const uint_t INSIZE = (uint_t) in_size;

	jsize jLen = (*env)->GetArrayLength(env, jIn);
	jfloat * const jInData = (*env)->GetFloatArrayElements(env, jIn, 0);

    aubio_tempo_t* tempo = new_aubio_tempo("default", INSIZE, HOPSIZE, (uint_t)sample_rate);

	if (!tempo) {
		printf("tempo was NULL");
		return 0;
	}

    fvec_t *in = new_fvec(HOPSIZE);
    fvec_t *out = new_fvec(1);

    uint_t copyI = 0;
    uint64_t bpmSum = 0;
    uint_t ticksSinceLastBeat = 0;
    double periodSum = 0;

    while ((copyI + hop_size) < jLen) {
        for (int i = 0; i != HOPSIZE; ++i) {
            in->data[i] = jInData[copyI++];
        }
        aubio_tempo_do (tempo, in, out);
        bpmSum += aubio_tempo_get_bpm(tempo);
        periodSum += aubio_tempo_get_period_s(tempo);
    }
    uint_t numIterations = (copyI/hop_size);
    float bpm = bpmSum / (double)numIterations;
    double period = periodSum / (double)numIterations;

	del_aubio_tempo (tempo);
	(*env)->ReleaseFloatArrayElements(env, jIn, jInData, 0);
    del_fvec(out);
    del_fvec(in);

    return period;
}

