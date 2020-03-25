/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Menus;

import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

class VersusMenu implements Menu
{
    VersusMenu(final GraphicsContext context)
    {

    }

    @Override
    public MenuEnum animateFrame(int framesToGo) {
        return MenuEnum.GAMEPLAY;
    }

    @Override
    public void key(boolean pressed, KeyCode code) {

    }

    @Override
    public void mouse(boolean pressed, MouseButton button, int x, int y) {

    }

    @Override
    public void mouse(int x, int y) {

    }

    @Override
    public Image getBackground() {
        return null;
    }

    @Override
    public void startMedia() {

    }

    @Override
    public void stopMedia() {

    }

    @Override
    public void reset(Group group) {

    }

    @Override
    public void setup(Group group) {

    }
}
