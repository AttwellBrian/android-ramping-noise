package com.brianattwell.noise;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.Random;

public class NoiseGenerator {

    private final int NUM_SAMPLES = NoiseConstants.SOUND_BUFFER_LENGTH_MS * NoiseConstants.SAMPLE_RATE_HZ / 1000;
    private final byte generatedSnd[] = new byte[2 * NUM_SAMPLES];
    private AudioTrack mAudioTrack;

    public NoiseGenerator() {
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
            NoiseConstants.SAMPLE_RATE_HZ, AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
            AudioTrack.MODE_STREAM);
        mAudioTrack.play();
    }

    public void playSound(double amplitude){
        Random random = new Random();
        random.nextBytes(generatedSnd);
        for (int i = 0; i < generatedSnd.length; i++) {
            generatedSnd[i] *= amplitude;
        }
        mAudioTrack.write(generatedSnd, 0, generatedSnd.length);
    }
}
