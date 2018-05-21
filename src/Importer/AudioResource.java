package Importer;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URISyntaxException;
import java.net.URL;


public class AudioResource extends Resource
{
    private MediaPlayer mediaPlayer;

    AudioResource(String path)
    {
        super(path);

        URL url = getClass().getResource(path);
        if (url != null)
        {
            Media music;
            try {
                music = new Media(url.toURI().toString());
            } catch (URISyntaxException e) {
                e.printStackTrace();
                music = null;
                mediaPlayer = null;
            }
            if (music != null)
            {
                mediaPlayer = new MediaPlayer(music);

                mediaPlayer.setOnEndOfMedia(() ->
                        mediaPlayer.stop());
            }
            else printFailure();
        }
        else printFailure();
    }

    public void play()
    {
        if (mediaPlayer == null) return;
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) return;
        mediaPlayer.play();
    }

    public void stop()
    {
        if (mediaPlayer == null) return;
        mediaPlayer.stop();
    }
}
