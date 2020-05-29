package Util;

public class Print
{
    public static void black(Object o) { print(o == null ? "null" : o.toString(), Color.BLACK); }
    public static void red(Object o) { print(o == null ? "null" : o.toString(), Color.RED); }
    public static void green(Object o) { print(o == null ? "null" : o.toString(), Color.GREEN); }
    public static void yellow(Object o) { print(o == null ? "null" : o.toString(), Color.YELLOW); }
    public static void blue(Object o) { print(o == null ? "null" : o.toString(), Color.BLUE); }
    public static void purple(Object o) { print(o == null ? "null" : o.toString(), Color.PURPLE); }
    public static void cyan(Object o) { print(o == null ? "null" : o.toString(), Color.CYAN); }
    public static void white(Object o) { print(o == null ? "null" : o.toString(), Color.WHITE); }

    private static void print(String string, Color color)
    {
        String ANSI_RESET = "\u001B[0m";
        System.out.println(color.ANSI() + string + ANSI_RESET);
        if (consoleBox != null) consoleBox.print(string);
    }

    private enum Color
    {
        BLACK
                {
                    String ANSI() { return "\u001B[30m"; }
                },
        RED
                {
                    String ANSI() { return "\u001B[31m"; }
                },
        GREEN
                {
                    String ANSI() { return "\u001B[32m"; }
                },
        YELLOW
                {
                    String ANSI() { return "\u001B[33m"; }
                },
        BLUE
                {
                    String ANSI() { return "\u001B[34m"; }
                },
        PURPLE
                {
                    String ANSI() { return "\u001B[35m"; }
                },
        CYAN
                {
                    String ANSI() { return "\u001B[36m"; }
                },
        WHITE
                {
                    String ANSI() { return "\u001B[37m"; }
                };

        String ANSI() { return ""; }
    }

    private static ConsoleBox consoleBox = null;
    public static void setConsoleBox(ConsoleBox box) { consoleBox = box; }
}
