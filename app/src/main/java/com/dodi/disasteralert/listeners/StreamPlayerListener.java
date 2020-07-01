package com.dodi.disasteralert.listeners;

import android.media.AudioTrack;

public interface StreamPlayerListener {
    void onMarkerReached(AudioTrack audioTrack);
}
