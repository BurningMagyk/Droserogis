package Gameplay.Weapons;

import Util.Vec2;

public class WeaponType
{
    private final float[] DEF_ORIENT;

    public WeaponType(Vec2 orientPos, float orientTheta)
    {
        DEF_ORIENT = new float[]{ orientPos.x, orientPos.y, orientTheta };
    }

    float[] getDefaultOrient() { return DEF_ORIENT; }
}
