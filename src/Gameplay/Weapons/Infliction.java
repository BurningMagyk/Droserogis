package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Gameplay.Item;
import Util.GradeEnum;
import Util.Vec2;

public class Infliction
{
    private static int ID_static = 0;
    private final int ID;

    private Weapon_old.Operation op;
    private Actor inflictor;

    private DirEnum dir;
    private GradeEnum damage;
    private float critThreshSpeed;
    private Weapon_old.ConditionApp conditionApp;
    // TODO: add damage-type
    private Vec2 momentum;
    private Vec2 offsetPosition;
    private boolean easyToBlock;

    private boolean finished = false;

    static void incrementID()
    {
        if (ID_static == Integer.MAX_VALUE) ID_static = 0;
        else ID_static++;
    }

    Infliction(Vec2 momentum, Weapon_old.Operation op, Actor inflictor,
               DirEnum dir, GradeEnum damage, float critThreshSpeed, Weapon_old.ConditionApp conditionApp,
               Vec2 offsetPosition, boolean easyToBlock)
    {
        ID = ID_static;

        this.momentum = momentum;
        this.op = op;
        this.inflictor = inflictor;
        this.dir = dir;
        this.damage = damage;
        this.conditionApp = conditionApp;
        this.offsetPosition = offsetPosition;
        this.easyToBlock = easyToBlock;

        //float weaponMomentumMod = 0.1F; // TODO: make this value based on weapon type, attack type, and character strength
        //weaponMomentum = new Vec2(dir.getHoriz().getSign() * weaponMomentumMod, dir.getVert().getSign() * weaponMomentumMod);
    }

    void finish() { finished = true; }
    public boolean isFinished() { return finished; }

    //public boolean sameSource(Infliction other) { return this.source == other.source; }
    public boolean sameAs(Infliction other) { return ID == other.ID; }

    public DirEnum getDir() { return dir; }
    public GradeEnum getDamage() { return damage; }
    //public void applyCondition(Actor other) { conditionApp.apply(other); }
    public boolean applyCondition(Weapon_old other) { return false; }//other.clash(source, op, damage); }

    public void applyMomentum(Actor otherActor, Weapon otherWeapon, boolean deflected)
    {
        //v_after = ((m_other * v_other) + (m_this * v_this)) / (m_other + m_this)

        Vec2 velForThis, velForOther,
                bodyMomentum = inflictor.getVelocity().mul(inflictor.getMass()).add(otherActor.getVelocity().mul(otherActor.getMass()))
                        .div(inflictor.getMass() + otherActor.getMass());

        if (deflected)
        {
            velForThis = momentum.clone().div(-2).add(bodyMomentum);
            velForOther = momentum.clone().div(2).add(bodyMomentum);
            inflictor.stagger(dir.getOpp(), (float) velForThis.mag(), true);
        }
        else
        {
            velForThis = bodyMomentum.clone();
            velForOther = momentum.clone().add(bodyMomentum);
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

    public void cancelDamage() { damage = GradeEnum.F; }
    /**
     * Returns false if damage is dealt, returns true if deflected
     */
    public boolean applyDamage(Item other)
    {
        GradeEnum _damage = damage;

        /* Use velocity and thresh to decide if damage should be doubled as crit */
        DirEnum travelDir = inflictor.getTravelDir();
        if (travelDir == dir && critThreshSpeed != 0)
        {
            /* If critThreshSpeed is greater than zero, the weapon relies more on momentum,
             * like a claymore. If critThreshSpeed is less than zero, the weapon relies more
             * on precision, like a rapier. If critThreshSpeed is equal to zero, then it
             * can't crit depending on travel speed, like a magic wand. */

            // TODO: the whole "less than zero" thing needs to be removed or redone
            double inflictorVel = inflictor.getVelocity().mag();
            if ((critThreshSpeed > 0 && inflictorVel > critThreshSpeed)
                    || (critThreshSpeed < 0 && inflictorVel > -critThreshSpeed))
                _damage = GradeEnum.values()[Math.max(damage.ordinal() + 2, GradeEnum.values().length - 1)];
            // TODO: decide on crit value (right now it's 2)
        }

        if (other instanceof Actor)
        {
            boolean[] blockRating = ((Actor) other).getBlockRating();
            /* 0 - Able to block
             * 1 - Prone
             * 2 - Pressing up
             * 3 - Using shield */
            if (!blockRating[0] || (!blockRating[3] && !easyToBlock)) other.damage(_damage);
            else if (blockRating[1])
            {
                float weaponPos = offsetPosition.x, actorPos = other.getPosition().x;

                // TODO: may need to switch the !'s around
                if (((Actor) other).getWeaponFace().getHoriz() == DirEnum.LEFT)
                {
                    if ((weaponPos >= actorPos && !blockRating[2])
                            || (weaponPos <= actorPos && blockRating[2])) other.damage(_damage);
                    else return true;
                }
                else
                {
                    if ((weaponPos <= actorPos && blockRating[2])
                            || (weaponPos >= actorPos && !blockRating[2])) other.damage(_damage);
                    else return true;
                }
            }
            else
            {
                float weaponPos = offsetPosition.x, actorPos = other.getPosition().x;
                boolean facingLeft = ((Actor) other).getWeaponFace().getHoriz() == DirEnum.LEFT;
                if ((weaponPos <= actorPos && facingLeft) || (weaponPos >= actorPos && !facingLeft))
                {
                    float _weaponPos = offsetPosition.y, _actorPos = other.getPosition().y;
                    if ((_weaponPos < _actorPos && !blockRating[2]) || _weaponPos > _actorPos && blockRating[2])
                        other.damage(_damage);
                    else return true;
                }
                else if (weaponPos <= actorPos || weaponPos >= actorPos) other.damage(_damage);
                else return true;
            }
        }
        else other.damage(_damage);
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