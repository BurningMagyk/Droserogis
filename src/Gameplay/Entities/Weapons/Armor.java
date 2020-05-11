/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Gameplay.Entities.Weapons;

import Gameplay.DirEnum;
import Gameplay.Entities.Actor;
import Gameplay.Entities.Item;
import Util.GradeEnum;
import Util.Print;
import Util.Vec2;

import java.util.ArrayList;

public class Armor extends Item
{
    private Actor actor = null;
    private boolean idle = true;

    protected Armor(float xPos, float yPos, float width, float height, GradeEnum mass,
                    ArmorStat armorStat, ArrayList<String[]> spritePaths) {
        super(xPos, yPos, width, height, mass, armorStat.getDurability(), spritePaths);
    }

    public Armor equip(Actor actor)
    {
        this.actor = actor;
        idle = false;
        setPosition(actor.getPosition());

        return this;
    }

    public void unequip()
    {
        this.actor = null;
        idle = true;
    }

    public void updatePosition(Vec2 p, Vec2 v)
    {
        setPosition(p);
        setVelocity(v);
    }

    public GradeEnum getResistanceTo(Infliction inf)
    {
        // TODO: here is where we use weaponStat
        return GradeEnum.F;
    }

    @Override
    protected void applyInflictions()
    {
        for (int i = 0; i < inflictions.size(); i++)
        {
            Infliction inf = inflictions.get(i);

            Print.yellow("----------Armor-----------");

            damage(inf);
            GradeEnum momentum = inf.getMomentum();
            DirEnum dir = inf.getDir();

            if (momentum != null && actor == null)
            {
                if (dir.getHoriz() != DirEnum.NONE && dir.getVert() != DirEnum.NONE)
                {
                    float speed = GradeEnum.gradeToVel(momentum) * 0.7071F;
                    addVelocityX(speed * dir.getHoriz().getSign());
                    addVelocityY(speed * dir.getVert().getSign());
                }
                else
                {
                    if (dir.getHoriz() != DirEnum.NONE)
                        addVelocityX(GradeEnum.gradeToVel(momentum));
                    else if (dir.getVert() != DirEnum.NONE)
                        addVelocityY(GradeEnum.gradeToVel(momentum));
                }

                Print.yellow("Momentum: " + momentum);
            }

            Print.yellow("--------------------------");
        }

        inflictions.clear();
    }

    @Override
    public void damage(Infliction inf)
    {
        GradeEnum damage = inf.getDamage();
        if (damage != null)
        {
            GradeEnum newDamage = GradeEnum.getGrade(
                    damage.ordinal() - getResistanceTo(inf).ordinal());
            Print.yellow("Damage: " + newDamage);
        }
    }

    @Override
    public void inflict(Infliction infliction)
    {
        if (infliction != null) inflictions.add(infliction);
        Print.yellow("Armor: " + infliction + " added");
    }

    @Override
    protected void destroy()
    {
        // TODO: unequip armor and drop
    }

    public boolean isIdle() { return idle; }
}
