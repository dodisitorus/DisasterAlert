package com.dodi.disasteralert.utils;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dodi.disasteralert.listeners.StreamPlayerListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamPlayer2 {
    private final String TAG = "StreamPlayer";
    // default sample rate for .wav from Watson TTS
    // see https://console.bluemix.net/docs/services/text-to-speech/http.html#format
    private final int DEFAULT_SAMPLE_RATE = 22050;

    private static AudioTrack audioTrack;

    private static byte[] convertStreamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[10240];
        int i;
        while ((i = is.read(buff, 0, buff.length)) > 0) {
            baos.write(buff, 0, i);
        }

        return baos.toByteArray();
    }

    /**
     * Play the given InputStream. The stream must be a PCM audio format with a sample rate of 22050.
     *
     * @param stream the stream derived from a PCM audio source
     */
    public void playStream(InputStream stream, Activity activity, final StreamPlayerListener listener) {
        try {
            byte[] data = convertStreamToByteArray(stream);
            int headSize = 44, metaDataSize = 48;
            int destPos = headSize + metaDataSize;
            int rawLength = data.length - destPos;
            byte[] d = new byte[rawLength];
            System.arraycopy(data, destPos, d, 0, rawLength);
            initPlayer(DEFAULT_SAMPLE_RATE);
            audioTrack.write(d, 0, d.length);
            stream.close();

            // notify if audio has reached end duration
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    listener.onMarkerReached(audioTrack);
                }
            });

            if (audioTrack != null && audioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
                audioTrack.release();
            }
        } catch (IOException e2) {
            Log.e(TAG, e2.getMessage());
        }
    }

    /**
     * Interrupt the audioStream.
     */
    public void interrupt() {
        if (audioTrack != null) {
            if (audioTrack.getState() == AudioTrack.STATE_INITIALIZED
                    || audioTrack.getState() == AudioTrack.PLAYSTATE_PLAYING) {
                audioTrack.pause();
            }
            audioTrack.flush();
            audioTrack.release();
        }
    }

    /**
     * Initialize AudioTrack by getting buffersize
     *
     * @param sampleRate the sample rate for the audio to be played
     */
    private void initPlayer(final int sampleRate) {
        synchronized (this) {
            final int bufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize == AudioTrack.ERROR_BAD_VALUE) {
                throw new RuntimeException("Could not determine buffer size for audio");
            }

            audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize,
                    AudioTrack.MODE_STREAM
            );

            audioTrack.play();
        }
    }
}
