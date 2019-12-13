package Gameplay.Weapons;

import Gameplay.DirEnum;
import Util.Vec2;

public class Journey
{
    private Orient start, end, distance, result;
    private float _time, totalTime;

    Journey(Orient start, Orient end, float _time)
    {
        result = start.copy();
        start._reduceTheta();
        this.start = start.copy();

        end._reduceTheta();
        this.end = end;
        this._time = _time;

        /* All this mess here is just for making sure it rotates in the
         * correct direction. */
        double endMinimal = Math.min(end.getTheta(), (Math.PI * 2) - end.getTheta());
        double startMinimal = Math.min(start.getTheta(), (Math.PI * 2) - start.getTheta());
        double thetaDistance;
        if (Math.abs(end.getTheta() - start.getTheta()) < endMinimal + startMinimal)
            thetaDistance = end.getTheta() - start.getTheta();
        else thetaDistance = (endMinimal + startMinimal) * start.getTheta() > end.getTheta() ? 1 : -1;

        distance = new Orient(
                new Vec2(end.getX() - start.getX(),
                        end.getY() - start.getY()),
                (float) thetaDistance);
    }

    Journey makeCoolJourney(Orient start, float _time)
    {
        return new Journey(start, this.start, _time);
    }

    Orient getResultOrient() { return result; }

    void setSpeed(float speed) { totalTime = _time * speed; }

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
