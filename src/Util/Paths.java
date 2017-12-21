package Util;

public class Paths
{
    private static String lastAttemptedPath;

    /* List of all file names in Resources */
    public final static String OPENING_BACKGROUND = "opening_background.png";
    /* TODO: Make get() add "Images/" to the strings */

    public static String get(String filename)
    {
        lastAttemptedPath = filename;
        return filename;
    }

    private void printImportFail()
    {
        Print.red("\"" + lastAttemptedPath + "\" was not imported");
    }
}