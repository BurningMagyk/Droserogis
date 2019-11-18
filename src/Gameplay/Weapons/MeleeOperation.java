package Gameplay.Weapons;

import Gameplay.DirEnum;
import Gameplay.Item;
import Util.GradeEnum;
import Util.Print;
import Util.Vec2;

class MeleeOperation implements Weapon.Operation
{
    @Override
    public String getName() { return name; }
    @Override
    public DirEnum getDir() { return command.FACE; }
    @Override
    public void setCommand(Command command) { this.command = command; }

    @Override
    public void start()
    //public void start(Weapon.Orient orient)
    {
        state = warmSkip ? State.EXECUTION : State.WARMUP;
        totalSec = warmBoost;
        warmBoost = 0;
        //warmJourney.setStart(orient);
        Print.blue("Operating " + getName());
    }

    @Override
    public boolean run(float deltaSec)
    {
        totalSec += deltaSec;

        if (state == State.WARMUP)
        {
            //conditionAppCycle
        }
        return false;
    }

    @Override
    public boolean mayInterrupt(Command next) {
        return false;
    }

    @Override
    public boolean mayApply() {
        return false;
    }

    @Override
    public void letGo(int attackKey) {

    }

    @Override
    public void apply(Weapon _this, Item other) {

    }

    @Override
    public boolean isEasyToBlock() {
        return false;
    }

    @Override
    public boolean isDisruptive() {
        return false;
    }

    enum MeleeEnum
    {
        THRUST, THRUST_UP, THRUST_DOWN, THRUST_DIAG_UP, THRUST_DIAG_DOWN,
        THRUST_LUNGE,
        STAB, STAB_UNTERHAU,
        SWING, SWING_UNTERHAU, SWING_UNTERHAU_CROUCH, SWING_UP_FORWARD,
        SWING_UP_BACKWARD, SWING_DOWN_FORWARD, SWING_DOWN_BACKWARD,
        SWING_LUNGE, SWING_LUNGE_UNTERHAU,
        GRAB,
        DRAW, LOAD, SHOOT;
    }

    private String name;
    private Vec2 waits;
    private DirEnum funcDir;
    private GradeEnum damage;
    private Weapon.Tick[] execJourney;
    private Weapon.Journey warmJourney, coolJourney;

    private State state = State.VOID;
    private Command command;
    private boolean warmSkip = false;
    private float totalSec = 0, warmBoost = 0;

    // TODO: conditionAppCycle set in WeaponTypeEnum (daggers would be different)
    // TODO: conditionAppInfliction should be integrated into damage
    // TODO: easyToBlock set in WeaponTypeEnum (sword thrusts harder to block than hammer thrusts)
    // TODO: disruptive set universally in WeaponTypeEnum (swings true, others false)

    MeleeOperation(
            String name,
            WeaponTypeEnum.Stat stat,
            Vec2 waits,
            DirEnum funcDir,
            GradeEnum damage,
            Weapon.Tick[] execJourney
    )
    {
        this.name = name;
        this.waits = waits.clone();
        this.funcDir = funcDir;
        this.damage = damage;
        this.execJourney = execJourney;
    }
}
