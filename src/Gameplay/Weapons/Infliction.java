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
    private Vec2 weaponMomentum;

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

        float weaponMomentumMod = 0.1F; // TODO: make this value based on weapon type, attack type, and character strength
        weaponMomentum = new Vec2(dir.getHoriz().getSign() * weaponMomentumMod, dir.getVert().getSign() * weaponMomentumMod);
    }

    void finish() { finished = true; }
    public boolean isFinished() { return finished; }

    public boolean sameSource(Infliction other) { return this.source == other.source; }

    public DirEnum getDir() { return dir; }
    public int getDamage() { return damage; }
    public void applyCondition(Actor other) { conditionApp.apply(other); }
    public void applyCondition(Weapon other) { other.clash(source, op); }

    public void applyMomentum(Actor other)
    {
        //v_after = ((m_other * v_other) + (m_this * v_this)) / (m_other + m_this)

        Vec2 finalVelocity = inflictor.getVelocity().mul(inflictor.mass).add(other.getVelocity().mul(other.mass))
                .mul(1F / inflictor.mass + other.mass);

        inflictor.setVelocity(finalVelocity);
        Vec2 finalVelocityPlusWeapon = finalVelocity.add(weaponMomentum);
        if (dir.getVert() == DirEnum.DOWN && other.getState().isGrounded()
                && (other.has(Actor.Condition.FORCE_CROUCH)
                || other.has(Actor.Condition.NEGATE_STABILITY)
                || other.has(Actor.Condition.NEGATE_ACTIVITY)))
            finalVelocityPlusWeapon = new Vec2(0, finalVelocityPlusWeapon.y);
        other.setVelocity(finalVelocity.add(finalVelocityPlusWeapon));

        other.stagger(dir, (float) finalVelocityPlusWeapon.mag());
    }

    public void cancelDamage() { damage = 0; }
    public void applyDamage(Item other) { other.damage(damage); }

    private boolean resolved = false;
    public void resolve() { resolved = true; }
    public boolean isResolved() { return resolved; }

    @Override
    public String toString()
    {
        return "Inflicted with dir " + dir;
    }
}