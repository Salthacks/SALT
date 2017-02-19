cmake_minimum_required(VERSION 3.4.1)

add_library(
    # Specifies the name of the library.
    aubio-lib
    # Sets the library as a shared library.
    SHARED
    # Provides a relative path to your source file(s).
    cvec.c
    fvec.c
    lvec.c
    mathutils.c
    vecutils.c
    io/audio_unit.c
    io/ioutils.c
    io/sink.c
    io/sink_apple_audio.c
    io/sink_sndfile.c
    io/sink_wavwrite.c
    io/source.c
    io/source_apple_audio.c
    io/source_avcodec.c
    io/source_sndfile.c
    io/source_wavread.c
    io/utils_apple_audio.c
    notes/notes.c
    onset/onset.c
    onset/peakpicker.c
    pitch/pitch.c
    pitch/pitchfcomb.c
    pitch/pitchschmitt.c
    pitch/pitchspecacf.c
    pitch/pitchyin.c
    pitch/pitchyinfft.c
    spectral/fft.c
    spectral/filterbank.c
    spectral/filterbank_mel.c
    spectral/mfcc.c
    spectral/ooura_fft8g.c
    spectral/phasevoc.c
    spectral/specdesc.c
    spectral/statistics.c
    spectral/tss.c
    synth/sampler.c
    synth/wavetable.c
    tempo/beattracking.c
    tempo/tempo.c
    temporal/a_weighting.c
    temporal/biquad.c
    temporal/c_weighting.c
    temporal/filter.c
    temporal/resampler.c
    utils/hist.c
    utils/log.c
    utils/parameter.c
    utils/scale.c
    utils/windll.c)

# Specifies a path to native header files.
include_directories(
    /main/cpp/include/

    io/
    notes/
    onset/
    pitch/
    spectral/
    synth/
    tempo/
    temporal/
    utils/)