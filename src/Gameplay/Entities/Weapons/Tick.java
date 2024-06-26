/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Gameplay.Entities.Weapons;

import Gameplay.DirEnum;
import Util.Vec2;

public class Tick
{
    float sec;
    Orient tickOrient;
    boolean used = false;

    Tick(float sec, Orient tickOrient)
    {
        this.sec = sec;
        this.tickOrient = tickOrient.copy();
    }
    Tick(float sec, float posX, float posY, float theta)
    {
        this.sec = sec;
        tickOrient = new Orient(new Vec2(posX, posY), Weapon.reduceTheta(theta));
    }

    boolean check(float totalSec)
    {
        if (totalSec < this.sec || !used)
        {
            used = true;
            return true;
        }
        return false;
    }

    void reset() { used = false; }

    Orient getOrient() { return tickOrient.copy(); }

    Tick getMirrorCopy(boolean horiz, boolean vert)
    {
        return new Tick(sec,
                (horiz ? -1 : 1) * tickOrient.getX(),
                (vert ? -1 : 1) * tickOrient.getY(),
                tickOrient.getTheta()
                        - (horiz ^ vert ? (float) Math.PI / 2 : 0));
    }

    Tick getCopy(float totalSec) { return new Tick(totalSec, tickOrient); }
    Tick getCopy() { return new Tick(sec, tickOrient.copy()); }

    Tick getRotatedCopy(boolean up)
    {
        return new Tick(sec, tickOrient.getX(), tickOrient.getY(),
                tickOrient.getTheta() + (float) (Math.PI / 2.0));
    }

    Tick getTimeModdedCopy(float add, float mult)
    {
        return new Tick((sec + add) * mult,
                tickOrient.getX(), tickOrient.getY(),
                tickOrient.getTheta());
    }
}
