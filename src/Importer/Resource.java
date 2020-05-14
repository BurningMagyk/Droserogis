package Importer;

import Util.Print;

abstract class Resource
{
    private String path;
    private final String UNCONTROLLED_PATH = "/Uncontrolled";
    private boolean uncontrolled = false;

    Resource(String path) { this.path = path; }

    String setUncontrolled()
    {
        if (!uncontrolled)
        {
            path = UNCONTROLLED_PATH + path;
            uncontrolled = true;
        }
        return path;
    }

    String setAltPath(String pathAlt)
    {
        if (uncontrolled) path = UNCONTROLLED_PATH + pathAlt;
        else path = pathAlt;
        return path;
    }

    String getPath() { return path + ""; }

    void printFailure() { Print.red("\"" + path + "\" was not imported"); }
}
