package Importer;

import Util.Print;

abstract class Resource
{
    final String path;

    Resource(String path)
    {
        this.path = path;
    }

    void printFailure()
    {
        Print.red("\"" + path + "\" was not imported");
    }
}
