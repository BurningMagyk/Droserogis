/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Gameplay.Entities.Weapons;

import Gameplay.Entities.Actor;
import Gameplay.Entities.Item;
import Util.GradeEnum;
import Util.Print;
import Util.Vec2;

public class Armor extends Item
{
    private Actor actor = null;
    private boolean idle = true;

    protected Armor(float xPos, float yPos, float width, float height, float mass,
                    ArmorStat armorStat, String[] spritePaths) {
        super(xPos, yPos, width, height, mass, armorStat.getDurability(), spritePaths);
    }

    public Armor equip(Actor actor)
    {
        this.actor = actor;
        idle = false;
        setPosition(actor.getPosition());

        return this;
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
            Vec2 momentum = inf.getMomentum();
            if (momentum != null && actor == null)
            {
                addVelocity(momentum.div(getMass()));
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

    public boolean isIdle() { return idle; }
}
