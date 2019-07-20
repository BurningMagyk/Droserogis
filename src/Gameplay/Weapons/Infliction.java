package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Gameplay.Item;
import Util.Print;
import Util.Vec2;

public class Infliction
{
    private Weapon source;
    private Weapon.Operation op;
    private Actor inflictor;

    private DirEnum dir;
    private int damage;
    private Weapon.ConditionApp conditionApp;
    // TODO: add damage-type
    //private Vec2 weaponMomentum;

    private boolean finished = false;

    Infliction(Weapon source, Weapon.Operation op, Actor inflictor,
               DirEnum dir, int damage, Weapon.ConditionApp conditionApp)
    {
        this.source = source;
        this.op = op;
        this.inflictor = inflictor;
        this.dir = dir;
        this.damage = damage;
        this.conditionApp = conditionApp;

        //float weaponMomentumMod = 0.1F; // TODO: make this value based on weapon type, attack type, and character strength
        //weaponMomentum = new Vec2(dir.getHoriz().getSign() * weaponMomentumMod, dir.getVert().getSign() * weaponMomentumMod);
    }

    void finish() { finished = true; }
    public boolean isFinished() { return finished; }

    public boolean sameSource(Infliction other) { return this.source == other.source; }

    public DirEnum getDir() { return dir; }
    public int getDamage() { return damage; }
    public void applyCondition(Actor other) { conditionApp.apply(other); }
    public boolean applyCondition(Weapon other) { return other.clash(source, op); }

    public void applyMomentum(Actor otherActor, Weapon otherWeapon, boolean deflected)
    {
        //v_after = ((m_other * v_other) + (m_this * v_this)) / (m_other + m_this)

        Vec2 velForThis, velForOther,
                weaponMomentum = source.getMomentum(op, dir, otherWeapon),
                bodyMomentum = inflictor.getVelocity().mul(inflictor.getMass()).add(otherActor.getVelocity().mul(otherActor.getMass()))
                        .div(inflictor.getMass() + otherActor.getMass());

        if (deflected)
        {
            velForThis = weaponMomentum.clone().div(-2).add(bodyMomentum);
            velForOther = weaponMomentum.clone().div(2).add(bodyMomentum);
            inflictor.stagger(dir.getOpp(), (float) velForThis.mag(), true);
        }
        else
        {
            velForThis = bodyMomentum.clone();
            velForOther = weaponMomentum.clone().add(bodyMomentum);
        }
        otherActor.stagger(dir, (float) velForOther.mag(), false);

        inflictor.setVelocity(velForThis);

        if (dir.getVert() == DirEnum.DOWN && otherActor.getState().isGrounded()
                && (otherActor.has(Actor.Condition.FORCE_CROUCH)
                || otherActor.has(Actor.Condition.NEGATE_STABILITY)
                || otherActor.has(Actor.Condition.NEGATE_ACTIVITY)))
            velForOther = new Vec2(0, velForOther.y);
        otherActor.setVelocity(velForOther);
    }

    public void cancelDamage() { damage = 0; }
    /**
     * Returns false if damage is dealt, returns true if deflected
     */
    public boolean applyDamage(Item other)
    {
        if (other instanceof Actor)
        {
            boolean[] blockRating = ((Actor) other).getBlockRating();
            /* 0 - Able to block
             * 1 - Prone
             * 2 - Pressing up
             * 3 - Using shield */
            if (!blockRating[0] || (!blockRating[3] && !source.easyToBlock())) other.damage(damage);
            else if (blockRating[1])
            {
                float weaponPos = source.getOffsetPosition().x, actorPos = other.getPosition().x;

                // TODO: may need to switch the !'s around
                if (((Actor) other).getWeaponFace().getHoriz() == DirEnum.LEFT)
                {
                    if ((weaponPos >= actorPos && !blockRating[2])
                            || (weaponPos <= actorPos && blockRating[2])) other.damage(damage);
                    else return true;
                }
                else
                {
                    if ((weaponPos <= actorPos && blockRating[2])
                            || (weaponPos >= actorPos && !blockRating[2])) other.damage(damage);
                    else return true;
                }
            }
            else
            {
                float weaponPos = source.getOffsetPosition().x, actorPos = other.getPosition().x;
                boolean facingLeft = ((Actor) other).getWeaponFace().getHoriz() == DirEnum.LEFT;
                if ((weaponPos <= actorPos && facingLeft) || (weaponPos >= actorPos && !facingLeft))
                {
                    float _weaponPos = source.getOffsetPosition().y, _actorPos = other.getPosition().y;
                    if ((_weaponPos < _actorPos && !blockRating[2]) || _weaponPos > _actorPos && blockRating[2])
                        other.damage(damage);
                    else return true;
                }
                else if (weaponPos <= actorPos || weaponPos >= actorPos) other.damage(damage);
                else return true;
            }
        }
        else other.damage(damage);
        return false;
    }

    private boolean resolved = false;
    public void resolve() { resolved = true; }
    public boolean isResolved() { return resolved; }

    @Override
    public String toString()
    {
        return "Inflicted with dir " + dir;
    }
}