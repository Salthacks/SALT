#include "aubio.h"
#include <jni.h>
#include <stdio.h>
#include "fvec.h"
#include <stdlib.h>


JNIEXPORT void JNICALL
Java_com_salthacks_salt_MainActivity_processAubio(JNIEnv *env, jobject instance, jint hop_size, jint sample_rate, jfloatArray jIn) {

	jsize jLen = (*env)->GetArrayLength(env, jIn);
	fvec_t *in = malloc(sizeof(fvec_t));
	in->length = jLen;
	jfloat *jInData = (*env)->GetFloatArrayElements(env, jIn, 0);
	in->data = jInData;

	aubio_tempo_t* tempo = new_aubio_tempo("default", (int)jLen, (int)hop_size, (int)sample_rate);
	if (!tempo) {
		printf("tempo was NULL");
		return;
	}

	// The god function!!!! 🙏
	fvec_t *out = malloc(sizeof(fvec_t));
	out->length = jLen;
	out->data = malloc(sizeof(float) * jLen);
	aubio_tempo_do (tempo, in, out);

	for (int i=0; i!=jLen; ++i) {
		printf("%f ", out->data[i]);
	}

	del_aubio_tempo (tempo);
	(*env)->ReleaseFloatArrayElements(env, jIn, jInData, 0);
	free(in);
	free(out->data);
	free(out);
}
