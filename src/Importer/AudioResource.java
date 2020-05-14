/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Importer;

import Util.Print;
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
        if (url != null) setAudio(url);
        else
        {
            url = getClass().getResource(setUncontrolled());
            if (url != null) setAudio(url);
            else printFailure();
        }
    }

    private void setAudio(URL url)
    {
        Media audio = new Media(url.toExternalForm());
        if (audio != null)
        {
            mediaPlayer = new MediaPlayer(audio);

            mediaPlayer.setOnEndOfMedia(() ->
                    mediaPlayer.stop());
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
