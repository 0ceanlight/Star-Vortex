package de.tum.in.ase.eist.video;

import javafx.scene.Node;
import javafx.scene.media.MediaPlayer;

/**
 * This interface specifies the handling of the music and sounds played during
 * the game.
 */
public interface VideoPlayerInterface {

    /**
     * Starts playing the background music if it's not started already.
     */
    void playBackgroundVideo();

    /**
     * Stops the background music if it is currently playing.
     */
    void stopBackgroundVideo();

    void pauseBackgroundVideo();

    /**
     * Checks if the background music is playing.
     *
     * @return true if background music is playing, false if not
     */
    boolean isPlayingBackgroundVideo();

    /**
     * Plays the crash sound effect.
     */
    // void playCrashSound();
    MediaPlayer getMediaPlayer();

    Node getMediaView();
}
