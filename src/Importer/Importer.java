package Importer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Comparator;

public class Importer
{
    private GraphicsContext context;

    private ArrayList<Resource> resources = new ArrayList<>();

    public void setContext(GraphicsContext context)
    {
        this.context = context;
    }

    public ImageResource getImage(String path, Color color)
    {
        int index = binarySearch(0, resources.size() - 1, path);
        if (index == -1)
        {
            ImageResource resource = new ImageResource(path, context, color);
            resources.add(resource);
            resources.sort(new ResourceComp());
            return resource;
        } else {
            return (ImageResource) resources.get(index);
        }
    }

    public FontResource getFont(String path, double size)
    {
        return new FontResource(path, size, context);
    }

    public FontResource getFont(String path, double size,
                                String pathAlt, double sizeAlt)
    {
        return new FontResource(path, size, pathAlt, sizeAlt, context);
    }

    private int binarySearch(int first, int last, String key)
    {
        int result;

        if (first > last)
            result = -1;
        else
        {
            int mid = (first + last) / 2;

            int comp = key.compareToIgnoreCase(resources.get(mid).path);

            if (comp == 0)
                result = mid;
            else if (comp > 0)
                result = binarySearch(first, mid - 1, key);
            else
                result = binarySearch(mid + 1, last, key);
        }
        return result;
    }

    private class ResourceComp implements Comparator<Resource>
    {
        @Override
        public int compare(Resource res1, Resource res2)
        {
            return res2.path.compareToIgnoreCase(res1.path);
        }
    }
}