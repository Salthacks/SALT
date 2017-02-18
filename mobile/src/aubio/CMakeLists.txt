cmake_minimum_required(VERSION 3.4.1)

add_library(
    # Specifies the name of the library.
    aubio-lib

    # Sets the library as a shared library.
    SHARED

    # Provides a relative path to your source file(s).
    src/aubio/cvec.c
    src/aubio/fvec.c
    src/aubio/lvec.c
    src/aubio/mathutils.c
    src/aubio/vecutils.c

    src/aubio/io/audio_unit.c
    src/aubio/io/ioutils.c
    src/aubio/io/sink.c
    src/aubio/io/sink_apple_audio.c
    src/aubio/io/sink_sndfile.c
    src/aubio/io/sink_wavwrite.c
    src/aubio/io/source.c
    src/aubio/io/source_apple_audio.c
    src/aubio/io/avcodec.c
    src/aubio/io/source_sndfile.c
    src/aubio/io/source_wavread.c
    src/aubio/io/utils_apple_audio.c

    src/aubio/notes/notes.c

    src/aubio/onset/onset.c
    src/aubio/onset/peakpicker.c

    src/aubio/pitch/pitch.c
    src/aubio/pitch/pitchfcomb.c
    src/aubio/pitch/pitchschmitt.c
    src/aubio/pitch/pitchspecacf.c
    src/aubio/pitch/pitchyin.c
    src/aubio/pitch/pitchyinfft.c

    src/aubio/spectral/fft.c
    src/aubio/spectral/filterbank.c
    src/aubio/spectral/filterbank_mel.c
    src/aubio/spectral/mfcc.c
    src/aubio/spectral/ooura_fft8g.c
    src/aubio/spectral/phasevoc.c
    src/aubio/spectral/specdesc.c
    src/aubio/spectral/statistics.c
    src/aubio/spectral/tss.c

    src/aubio/synth/sampler.c
    src/aubio/synth/wavetable.c

    src/aubio/tempo/beattracking.c
    src/aubio/tempo/tempo.c

    src/aubio/temporal/a_weighting.c
    src/aubio/temporal/biquad.c
    src/aubio/temporal/c_weighting.c
    src/aubio/temporal/filter.c
    src/aubio/temporal/resampler.c

    src/aubio/utils/hist.c
    src/aubio/utils/log.c
    src/aubio/utils/parameter.c
    src/aubio/utils/scale.c
    src/aubio/utils/windll.c
)

# Specifies a path to native header files.
include_directories
(
    src/main/cpp/include/
    src/aubio/
    src/aubio/io/
    src/aubio/notes/
    src/aubio/onset/
    src/aubio/pitch/
    src/aubio/spectral/
    src/aubio/synth/
    src/aubio/tempo/
    src/aubio/temporal/
    src/aubio/utils/
)