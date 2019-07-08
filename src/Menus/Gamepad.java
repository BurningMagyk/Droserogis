package Menus;

import Gameplay.Actor;
import Util.Print;
import org.lwjgl.glfw.GLFWGamepadState;

import static org.lwjgl.glfw.GLFW.*;

public class Gamepad
{
    private GLFWGamepadState gamepadState;
    private boolean connected = false;

    public void checkConnection(int i)
    {
        if (connected)
        {
            if (!glfwJoystickPresent(i) || !glfwJoystickIsGamepad(i))
            {
                connected = false;
                Print.yellow("Gamepad " + i + " disconnected");
            }
        }
        else
        {
            if (glfwJoystickPresent(i) && glfwJoystickIsGamepad(i))
            {
                connected = true;
                Print.blue("Gamepad " + i + " connected");
            }
        }
    }

    public void query(Actor actor)
    {

    }
}