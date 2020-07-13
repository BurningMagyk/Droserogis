/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Importer;

import Gameplay.Entities.Entity;
import Gameplay.Entities.EntityCollection;
import Util.LanguageEnum;
import Util.Print;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;

public class Importer
{
    private GraphicsContext context;

    private ArrayList<Resource> images = new ArrayList<>();
    private ArrayList<FontResource> fonts = new ArrayList<>();

    private final ResourceComp COMP = new ResourceComp();

    private final String FONT_DIR = "/Fonts/";
    private final String IMAGE_DIR = "/Image/";
    private final String AUDIO_DIR = "/Audio/";

    public void setContext(GraphicsContext context)
    {
        this.context = context;
    }

    public BufferedReader getText(String path)
    {
        BufferedReader reader = null;
        InputStream input = getClass().getResourceAsStream(path);
        if (input != null)
            reader = new BufferedReader(new InputStreamReader(input));
        else Print.red("\"" + path + "\" was not imported");
        return reader;
    }

    public ImageResource getImage(String path, Color color)
    {
        System.out.println("Importer.Importer.getImage("+path+"]");
        int index = binarySearch(0, images.size() - 1,
                IMAGE_DIR + path, images);
        if (index == -1)
        {
            ImageResource resource = new ImageResource(
                     IMAGE_DIR + path, context, color);
            images.add(resource);
            images.sort(COMP);
            return resource;
        } else {
            return (ImageResource) images.get(index);
        }
    }

    public ImageResource getImage(String path)
    {
        return getImage(path, Color.PINK);
    }

    public ImageResource[] getImages(String[] paths)
    {
        ImageResource[] resources = new ImageResource[paths.length];

        for (int i = 0; i < paths.length; i++)
        {
            resources[i] = getImage(paths[i]);
        }

        return resources;
    }

    public FontResource getFont(String path, double size)
    {
        FontResource font = new FontResource(
                FONT_DIR + path, size, context);
        fonts.add(font);
        return font;
    }

    public FontResource getFont(String path, double size,
                                String pathAlt, double sizeAlt)
    {
        return new FontResource(FONT_DIR + path, size,
                FONT_DIR + pathAlt, sizeAlt, context);
    }

    public FontResource getFont(String path, String pathAlt, double size)
    {
        return getFont(path, size, pathAlt, size);
    }

    public void switchFonts(LanguageEnum languageEnum)
    {
        for (FontResource font : fonts)
        {
            font.switchFont(languageEnum == LanguageEnum.WAPANESE);
        }
    }

    public AudioResource getAudio(String path)
    {
        return new AudioResource(AUDIO_DIR + path);
    }

    private int binarySearch(int first, int last, String key, ArrayList<Resource> list)
    {
        int result;

        if (first > last)
            result = -1;
        else
        {
            int mid = (first + last) / 2;

            int comp = key.compareToIgnoreCase(list.get(mid).getPath());

            if (comp == 0)
                result = mid;
            else if (comp > 0)
                result = binarySearch(first, mid - 1, key, list);
            else
                result = binarySearch(mid + 1, last, key, list);
        }
        return result;
    }

    private class ResourceComp implements Comparator<Resource>
    {
        @Override
        public int compare(Resource res1, Resource res2)
        {
            return res2.getPath().compareToIgnoreCase(res1.getPath());
        }
    }
}