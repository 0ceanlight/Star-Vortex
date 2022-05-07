package de.tum.in.ase.eist.video;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.net.URL;

/**
 * This class handles the background music played during the game using a JavaFX {@link MediaPlayer}.
 */

public class VideoPlayer implements VideoPlayerInterface {

    private static final String VIDEO_PATH = "hyper-vortex.mp4";

    private static final double VIDEO_VOLUME = 0.5;

    private MediaPlayer videoPlayer;
    private final MediaView mediaView;

    /**
     * Constructs a new AudioPlayer by directly loading the background music and
     * crash sound files into a new MediaPlayer / AudioClip.
     */
    public VideoPlayer() {
        this.videoPlayer = new MediaPlayer(loadVideoFile(VIDEO_PATH));
        this.mediaView = new MediaView(videoPlayer);
    }

    @Override
    public void playBackgroundVideo() {
        if (isPlayingBackgroundVideo()) {
            return;
        }

        // Loop for the main video track:
//        this.videoPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        this.videoPlayer.play();
//        this.mediaView.setMediaPlayer(new MediaPlayer(loadVideoFile(VIDEO_PATH)));
    }

    @Override
    public void pauseBackgroundVideo() {
        if (isPlayingBackgroundVideo()) {
            this.videoPlayer.pause();
        }
    }

    @Override
    public void stopBackgroundVideo() {
        if (isPlayingBackgroundVideo()) {
            this.videoPlayer.stop();
        }
    }

    @Override
    public boolean isPlayingBackgroundVideo() {
        return MediaPlayer.Status.PLAYING.equals(this.videoPlayer.getStatus());
    }

    private Media loadVideoFile(String fileName) {
        return new Media(convertNameToUrl(fileName));
    }

    private String convertNameToUrl(String fileName) {
        URL musicSourceUrl = getClass().getClassLoader().getResource(fileName);
        if (musicSourceUrl == null) {
            throw new IllegalArgumentException(
                    "Please ensure that your resources folder contains the appropriate files for this exercise.");
        }
        return musicSourceUrl.toExternalForm();
    }

    public MediaPlayer getMediaPlayer() {
//        var videoSourceUrl = new File(VIDEO_PATH).toURI().toString();
//        Media media = new Media(videoSourceUrl);
//        MediaView mediaPlayer = new MediaView(new MediaPlayer(media));
//        mediaPlayer.getMediaPlayer().play();
//        stack.getChildren().add(mediaPlayer);
        return videoPlayer;
    }

    public static String getBackgroundVideoFile() {
        return VIDEO_PATH;
    }

    @Override
    public MediaView getMediaView() {
        return mediaView;
    }
}