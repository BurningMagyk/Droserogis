package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Util.Print;
import Util.Vec2;

import java.util.ArrayList;

public class Natural extends Weapon
{
    public Natural(float xPos, float yPos, float width, float height, Actor actor)
    {
        super(xPos, yPos, width, height);
        equip(actor);

        defaultOrient = new Orient(
                new Vec2(0.8F, 0), 0);
        setTheta(defaultOrient.getTheta(), DirEnum.RIGHT);
        orient.set(defaultOrient.copy());

        StatusAppCycle clumpCycle = new StatusAppCycle(
                new StatusApp(0.01F, Actor.Status.CLUMPED),
                new StatusApp(0.01F, Actor.Status.CLUMPED),
                new StatusApp(0.01F, Actor.Status.CLUMPED));
        StatusAppCycle plodRunCycle = new StatusAppCycle(
                null,
                new StatusApp(0.05F, Actor.Status.PLODDED),
                null);
        StatusAppCycle selfThrowCycle = new StatusAppCycle(
                new StatusApp(0.05F, Actor.Status.RUSHED),
                new StatusApp(0.01F, Actor.Status.STAGNANT),
                new StatusApp(0.01F, Actor.Status.CLUMPED));
    }

    private class Punch extends BasicMelee
    {
        Punch(float warmupTime, float cooldownTime,
              StatusAppCycle statusAppCycle, ArrayList<Tick> horizJourney,
              ArrayList<Tick> upJourney, ArrayList<Tick> diagJourney)
        {
            super(warmupTime, cooldownTime, statusAppCycle,
                    horizJourney, upJourney, diagJourney);
        }


    }
}
