/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Gameplay.Entities.Weapons;

import Gameplay.DirEnum;
import Util.Vec2;

public class Journey
{
    private Orient start, end, distance, result;
    private float totalTime;

    Journey(Orient start, Orient end, float totalTime)
    {
        result = start.copy();
        start.reduceTheta();
        this.start = start.copy();

        end.reduceTheta();
        this.end = end;
        this.totalTime = totalTime;

        float thetaDistance = end.getTheta() - start.getTheta();

        distance = new Orient(
                new Vec2(end.getX() - start.getX(),
                        end.getY() - start.getY()),
                (float) thetaDistance);
    }

    Journey makeCoolJourney(Orient start, float time)
    {
        return new Journey(start, this.start, time);
    }

    Orient getOrient() { return result.copy(); }

    boolean check(float time, DirEnum dir)
    {
        float ratio = time / totalTime;
        if (ratio >= 1.0)
        {
            result.set(end);
            return true;
        }
        result.setX(start.getX() + (distance.getX() * ratio));
        result.setY(start.getY() + (distance.getY() * ratio));
        result.setTheta(start.getTheta() + (distance.getTheta() * ratio));
        return false;
    }

    float getTotalTime() { return totalTime; }
}
