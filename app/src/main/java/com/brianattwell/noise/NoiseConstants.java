package com.brianattwell.noise;

public interface NoiseConstants {
    double MINIMUM_AMPLITUDE = 0.01;
    double AMPLITUDE_RAMP_DURATION_MS = 1000 * 60 * 15.0;
    int NOISE_START_DELAY = 1000 * 60 * 60 * 5;
    /**
     * Sample rate that white noise will get generated at. In order to be true white noise this needs to be 40 kHZ.
     * But I don't like true white noise.
     */
    int SAMPLE_RATE_HZ = 5000;
    int SOUND_BUFFER_LENGTH_MS = 500;
}
