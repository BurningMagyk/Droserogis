package Menus;

import Gameplay.entity.Actor;
import Util.Print;
import javafx.application.Platform;
import org.lwjgl.glfw.GLFWGamepadState;

import static org.lwjgl.glfw.GLFW.*;

public class Gamepad
{
    private final float DEADZONE = 0.05F;
    private final int i;

    private GLFWGamepadState gamepadState;
    private boolean connected = false;

    Gamepad(int i)
    {
        this.i = i;
        gamepadState = GLFWGamepadState.create();
        glfwGetGamepadState(i, gamepadState);
    }

    public void checkConnection()
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

    public void query(Actor player)
    {
        glfwGetGamepadState(i, gamepadState);

        //if (code == KeyCode.ESCAPE)
        if (gamepadState.buttons(GLFW_GAMEPAD_BUTTON_START) == 1)
        {
            glfwTerminate();
            glfwSetErrorCallback(null).free();
            Platform.exit();
            System.exit(0);
            return;
        }
        //if (code == KeyCode.ENTER && pressed)
        if (gamepadState.buttons(GLFW_GAMEPAD_BUTTON_BACK) == 1)
        {
            player.debug();
        }
        //if (code == KeyCode.A)
        player.pressLeft(gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_X) <= DEADZONE - 1);
        //if (code == KeyCode.D)
        player.pressRight(gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_X) >= 1 - DEADZONE);
        //if (code == KeyCode.W)
        player.pressUp(gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_Y) <= -DEADZONE);
        //if (code == KeyCode.S)
        player.pressDown(gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_Y) >= DEADZONE);
        //if (code == KeyCode.J)
        player.pressJump(gamepadState.buttons(GLFW_GAMEPAD_BUTTON_A) == 1);
        //if (code == KeyCode.SHIFT)
        player.pressShift(gamepadState.buttons(GLFW_GAMEPAD_BUTTON_LEFT_THUMB) == 1);
        //if (code == KeyCode.K)
        player.pressAttack(gamepadState.buttons(GLFW_GAMEPAD_BUTTON_X) == 1, Actor.ATTACK_KEY_1);
        //if (code == KeyCode.L)
        player.pressAttack(gamepadState.buttons(GLFW_GAMEPAD_BUTTON_Y) == 1, Actor.ATTACK_KEY_2);
        //if (code == KeyCode.SEMICOLON)
        player.pressAttack(gamepadState.buttons(GLFW_GAMEPAD_BUTTON_B) == 1, Actor.ATTACK_KEY_3);
        //if (code == KeyCode.U)
        player.pressAttackMod(gamepadState.buttons(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER) == 1);
    }
}