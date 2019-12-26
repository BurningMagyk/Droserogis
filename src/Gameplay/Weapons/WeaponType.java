package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Gameplay.Item;
import Gameplay.Weapons.Inflictions.ConditionApp;
import Gameplay.Weapons.Inflictions.ConditionAppCycle;
import Gameplay.Weapons.Inflictions.Infliction;
import Util.GradeEnum;
import Util.Print;
import Util.Vec2;

import static Gameplay.Weapons.MeleeOperation.MeleeEnum.*;
import static Gameplay.Actor.Condition.*;

public class WeaponType
{
    private final Orient DEF_ORIENT;
    private final Weapon.Operation[] OPS;

    WeaponType(Orient orient, Weapon.Operation...ops)
    {
        DEF_ORIENT = orient.copy();
        OPS = ops;
    }

    Orient getDefaultOrient() { return DEF_ORIENT; }
    Weapon.Operation[] getOps() { return OPS; }

    private static Tick[] reverse(Tick[] ticks)
    {
        Tick[] newTicks = new Tick[ticks.length];
        for (int i = 0; i < ticks.length; i++)
        {
            newTicks[ticks.length - i - 1]
                    = ticks[i].getCopy(ticks[ticks.length - i - 1].sec);
        }
        return newTicks;
    }
    private static Tick[] mirrorVert(Tick[] ticks)
    {
        Tick[] newTicks = new Tick[ticks.length];
        for (int i = 0; i < ticks.length; i++)
        {
            newTicks[i] = ticks[i].getMirrorCopy(false, true);
        }
        return newTicks;
    }

    private final static float PI2 = (float) Math.PI / 2;
    private final static float PI4 = (float) Math.PI / 4;

    private final static MeleeOperation.MeleeEnum[][] EMPTY__NEXT = {{}};
    private final static MeleeOperation.MeleeEnum[][] UNTERHAU_SWING__NEXT = {
            {SWING}, {SWING_UNTERHAU}};
    private final static MeleeOperation.MeleeEnum[][] PRONE_SWING__NEXT = {
            {SWING_PRONE}, {SWING_UP_BACKWARD}};
    private final static MeleeOperation.MeleeEnum[][] BACK_SWING_UP__NEXT = {
            {SWING_UP_FORWARD}, {SWING_UP_BACKWARD}};
    private final static MeleeOperation.MeleeEnum[][] BACK_SWING_DOWN__NEXT = {
            {SWING_DOWN_FORWARD}, {SWING_DOWN_BACKWARD}};

    private final static ConditionApp FORCE_WALK__FORCE_STAND = new ConditionApp(
            NEGATE_RUN_LEFT, NEGATE_RUN_RIGHT, FORCE_STAND);
    private final static ConditionApp LUNGE_END_CONDITION = new ConditionApp(
            NEGATE_WALK_LEFT, NEGATE_WALK_RIGHT, NEGATE_ATTACK, NEGATE_BLOCK);
    private final static ConditionApp FORCE_STILL__FORCE_STAND = new ConditionApp(
            NEGATE_WALK_LEFT, NEGATE_WALK_RIGHT, FORCE_STAND);

    private final static ConditionAppCycle STANDARD_CYCLE = new ConditionAppCycle(FORCE_WALK__FORCE_STAND);
    private final static ConditionAppCycle LUNGE_CYCLE = new ConditionAppCycle(
            new ConditionApp(FORCE_STAND), new ConditionApp(DASH), LUNGE_END_CONDITION);
    private final static ConditionAppCycle STAB_CYCLE = new ConditionAppCycle(FORCE_STILL__FORCE_STAND);

    private final static Vec2 NATURAL__PUNCH_WAITS = new Vec2(1, 1),
        NATURAL__KICK_WAITS = new Vec2(1.5F, 1.5F),
        NATURAL__RUSH_WAITS = new Vec2(0.1F, 0.1F),
        NATURAL__GRAB_WAITS = new Vec2(1F, 3F);

    private final static Tick[] NATURAL__PUNCH__EXEC = new Tick[] {
            new Tick(0.25F, 1F, -0.2F, 0),
            new Tick(0.5F, 1.1F, -0.2F, 0),
            new Tick(0.75F, 1.2F, -0.2F, 0),
            new Tick(1, 1.3F, -0.2F, 0) };
    private final static MeleeOperation NATURAL__PUNCH = new MeleeOperation(
            "Punch", EMPTY__NEXT, STANDARD_CYCLE, NATURAL__PUNCH_WAITS,
            DirEnum.RIGHT, GradeEnum.F, NATURAL__PUNCH__EXEC);

    private final static Tick[] NATURAL__UPPERCUT__EXEC = new Tick[] {
            new Tick(0.25F, 1, 0.75F, PI2),
            new Tick(0.5F, 1, 0.5F, PI2),
            new Tick(0.75F, 1, 0.25F, PI2),
            new Tick(1, 1, 0F, PI2) };
    private final static MeleeOperation NATURAL__UPPERCUT = new MeleeOperation(
            "Uppercut", EMPTY__NEXT, STAB_CYCLE, NATURAL__PUNCH_WAITS,
            DirEnum.UP, GradeEnum.F, NATURAL__UPPERCUT__EXEC);

    private final static Tick[] NATURAL__PUNCH_UP__EXEC = new Tick[] {
            new Tick(0.25F, 0, -0.25F, PI2),
            new Tick(0.5F, 0, -0.5F, PI2),
            new Tick(0.75F, 0, -0.75F, PI2),
            new Tick(1, 0, -1F, PI2) };
    private final static MeleeOperation NATURAL__PUNCH_UP = new MeleeOperation(
            "Punch up", EMPTY__NEXT, STANDARD_CYCLE, NATURAL__PUNCH_WAITS,
            DirEnum.UP, GradeEnum.F, NATURAL__PUNCH_UP__EXEC);

    private final static Tick[] NATURAL__PUNCH_DIAG_UP__EXEC = new Tick[] {
            new Tick(0.25F, 0.25F, -0.25F, -PI4),
            new Tick(0.5F, 0.5F, -0.5F, -PI4),
            new Tick(0.75F, 0.75F, -0.75F, -PI4),
            new Tick(1, 1F, -1F, -PI4) };
    private final static MeleeOperation NATURAL__PUNCH_DIAG_UP = new MeleeOperation(
            "Punch diag up", EMPTY__NEXT, STANDARD_CYCLE, NATURAL__PUNCH_WAITS,
            DirEnum.DOWNRIGHT, GradeEnum.F, NATURAL__PUNCH_DIAG_UP__EXEC);

    private final static Tick[] NATURAL__STOMP__EXEC = new Tick[] {
            new Tick(0.25F, 0.25F, 0.0F, PI2),
            new Tick(0.5F, 0.25F, 0.25F, PI2),
            new Tick(0.75F, 0.25F, 0.5F, PI2) };
    private final static MeleeOperation NATURAL__STOMP = new MeleeOperation(
            "Stomp", EMPTY__NEXT, STAB_CYCLE, NATURAL__KICK_WAITS,
            DirEnum.DOWN, GradeEnum.F, NATURAL__STOMP__EXEC);

    private final static Tick[] NATURAL__KICK_ARC__EXEC = new Tick[] {
            new Tick(0.33F, 0.0F, 0.5F, PI2),
            new Tick(0.66F, 0.25F, 0.25F, PI4),
            new Tick(1F, 0.5F, 0F, 0) };
    private final static MeleeOperation NATURAL__KICK_ARC = new MeleeOperation(
            "Kick arc", EMPTY__NEXT, STAB_CYCLE, NATURAL__KICK_WAITS,
            DirEnum.UPRIGHT, GradeEnum.F, NATURAL__KICK_ARC__EXEC);

    private final static Tick[] NATURAL__KICK_DIAG_DOWN__EXEC = new Tick[] {
            new Tick(0.25F, 0.25F, 0.25F, -PI4),
            new Tick(0.5F, 0.5F, 0.5F, -PI4),
            new Tick(0.75F, 0.75F, 0.75F, -PI4),
            new Tick(1, 1F, 1F, -PI4) };
    private final static MeleeOperation NATURAL__KICK_DIAG_DOWN = new MeleeOperation(
            "Kick diag down", EMPTY__NEXT, STANDARD_CYCLE, NATURAL__KICK_WAITS,
            DirEnum.DOWNRIGHT, GradeEnum.F, NATURAL__KICK_DIAG_DOWN__EXEC);

    private final static Tick[] NATURAL__KICK_STRAIGHT__EXEC = new Tick[] {
            new Tick(0.25F, 0.25F, 0.1F, 0),
            new Tick(0.5F, 0.5F, 0.1F, 0),
            new Tick(0.75F, 0.75F, 0.1F, 0),
            new Tick(1, 1F, 1F, 0) };
    private final static MeleeOperation NATURAL__KICK_STRAIGHT = new MeleeOperation(
            "Kick straight", EMPTY__NEXT, STAB_CYCLE, NATURAL__KICK_WAITS,
            DirEnum.RIGHT, GradeEnum.F, NATURAL__KICK_STRAIGHT__EXEC);

    private final static Tick[] NATURAL__KICK_PRONE__EXEC = new Tick[] {
            new Tick(0.25F, -0.25F, 0.1F, 0),
            new Tick(0.5F, -0.5F, 0.1F, 0),
            new Tick(0.75F, -0.75F, 0.1F, 0),
            new Tick(1, -1F, 1F, 0) };
    private final static MeleeOperation NATURAL__KICK_PRONE = new MeleeOperation(
            "Kick straight", EMPTY__NEXT, STANDARD_CYCLE, NATURAL__KICK_WAITS,
            DirEnum.LEFT, GradeEnum.F, NATURAL__KICK_PRONE__EXEC);

    private final static RushOperation NATURAL__SHOVE = new RushOperation(
            "Shove", EMPTY__NEXT, LUNGE_CYCLE, NATURAL__RUSH_WAITS,
            DirEnum.RIGHT, GradeEnum.F);

    private final static MeleeOperation NATURAL__GRAB = new MeleeOperation(
            "Grab", EMPTY__NEXT, STANDARD_CYCLE, NATURAL__GRAB_WAITS,
            DirEnum.NONE, GradeEnum.F, NATURAL__PUNCH__EXEC);

    private final static MeleeOperation NATURAL__GRAB_UP = new MeleeOperation(
            "Grab up", EMPTY__NEXT, STANDARD_CYCLE, NATURAL__GRAB_WAITS,
            DirEnum.NONE, GradeEnum.F, NATURAL__PUNCH_UP__EXEC);

    private final static MeleeOperation NATURAL__GRAB_DIAG_UP = new MeleeOperation(
            "Grab diag up", EMPTY__NEXT, STANDARD_CYCLE, NATURAL__GRAB_WAITS,
            DirEnum.NONE, GradeEnum.F, NATURAL__PUNCH_DIAG_UP__EXEC);

    private final static MeleeOperation NATURAL__GRAB_ALT = new MeleeOperation(
            "Grab alt", EMPTY__NEXT, STAB_CYCLE, NATURAL__KICK_WAITS,
            DirEnum.LEFT, GradeEnum.F, NATURAL__KICK_PRONE__EXEC);

    private static class InteractOperation implements Weapon.Operation
    {
        private State state = State.VOID;
        private Orient orient = new Orient(new Vec2(0, 0), 0);
        public String getName() { return "Interact"; }
        public DirEnum getDir() { return DirEnum.NONE; }
        public Infliction getInfliction() { return null; }
        public Infliction getSelfInfliction() { return null; }
        public State getState() { return state; }
        public Orient getOrient() { return orient; }
        public float interrupt(Command command) { state = State.VOID; return 0; }
        public MeleeOperation.MeleeEnum getNext(MeleeOperation.MeleeEnum meleeEnum) { return null; }
        public void start(Orient orient, float warmBoost, Command command) {
            state = State.WARMUP;
            Print.blue("Operating \"" + getName() + "\""); }
        public boolean run(float deltaSec) {
            if (state == State.EXECUTION) {
                interrupt(null);
                return true; }
            return false; }
        public void release(int attackKey) { if (attackKey == 3) state = State.EXECUTION; }
        public void apply(Item other) { }
        public boolean isEasyToBlock() { return false; }
        public boolean isDisruptive() { return false; }
    }

    public final static WeaponType NATURAL = new WeaponType(
            new Orient(new Vec2(0.2F, -0.1F), PI2),
            NATURAL__PUNCH, NATURAL__UPPERCUT,
            NATURAL__PUNCH_UP, null /*NATURAL__POUNCE*/,
            NATURAL__PUNCH_DIAG_UP, null /*NATURAL__POUNCE*/,
            null /*NATURAL__PUSH*/, NATURAL__STOMP, NATURAL__UPPERCUT,
            NATURAL__KICK_ARC, NATURAL__UPPERCUT, NATURAL__UPPERCUT,
            NATURAL__KICK_DIAG_DOWN, NATURAL__KICK_PRONE,
            NATURAL__KICK_STRAIGHT, NATURAL__KICK_STRAIGHT,
            null /*STOMP_FALL*/, null /*STOMP_FALL*/,
            NATURAL__SHOVE, NATURAL__GRAB, NATURAL__GRAB_UP,
            NATURAL__GRAB_DIAG_UP, NATURAL__GRAB_ALT,
            null /*POUNCE*/, null /*TACKLE*/,
            null, null, null, null, null, null, null, // empty ops (throwing)
            new InteractOperation()
    );

    private final static Vec2 SWORD__THRUST_WAITS = new Vec2(2, 2),
        SWORD__SWING_WAITS = new Vec2(1.5F, 1.5F);

    private final static Tick[] SWORD__THRUST__EXEC = new Tick[] {
            new Tick(0.25F, 0.75F, -0.1F, 0),
            new Tick(0.5F, 1, -0.1F, 0),
            new Tick(0.75F, 1.25F, -0.1F, 0),
            new Tick(1, 1.5F, -0.1F, 0) };
    private final static MeleeOperation SWORD__THRUST = new MeleeOperation(
            "Thrust", EMPTY__NEXT, STANDARD_CYCLE, SWORD__THRUST_WAITS,
            DirEnum.RIGHT, GradeEnum.F, SWORD__THRUST__EXEC);

    private final static Tick[] SWORD__THRUST_UNTERHAU__EXEC = new Tick[] {
            new Tick(0.25F, 0.75F, 0.1F, 0),
            new Tick(0.5F, 1, 0.1F, 0),
            new Tick(0.75F, 1.25F, 0.1F, 0),
            new Tick(1, 1.5F, 0.1F, 0) };
    private final static MeleeOperation SWORD__THRUST_UNTERHAU = new MeleeOperation(
            "Thrust", UNTERHAU_SWING__NEXT, STANDARD_CYCLE, SWORD__THRUST_WAITS,
            DirEnum.RIGHT, GradeEnum.F, SWORD__THRUST_UNTERHAU__EXEC);

    private final static Tick[] SWORD__THRUST_UP__EXEC = new Tick[] {
            new Tick(0.25F, 0, -0.25F, PI2),
            new Tick(0.5F, 0, -0.5F, PI2),
            new Tick(0.75F, 0, -0.75F, PI2),
            new Tick(1, 0, -1F, PI2) };
    private final static MeleeOperation SWORD__THRUST_UP = new MeleeOperation(
            "Thrust up", EMPTY__NEXT, STANDARD_CYCLE, SWORD__THRUST_WAITS,
            DirEnum.UP, GradeEnum.F, SWORD__THRUST_UP__EXEC);

    private final static Tick[] SWORD__THRUST_DOWN__EXEC = new Tick[] {
            new Tick(0.25F, 0, 0.25F, PI2),
            new Tick(0.5F, 0, 0.5F, PI2),
            new Tick(0.75F, 0, 0.75F, PI2),
            new Tick(1, 0, 1F, PI2) };
    private final static MeleeOperation SWORD__THRUST_DOWN = new MeleeOperation(
            "Thrust down", UNTERHAU_SWING__NEXT, STANDARD_CYCLE, SWORD__THRUST_WAITS,
            DirEnum.DOWN, GradeEnum.F, SWORD__THRUST_DOWN__EXEC);

    private final static Tick[] SWORD__THRUST_DIAG_UP__EXEC = new Tick[] {
            new Tick(0.25F, 0.25F, -0.25F, -PI4),
            new Tick(0.5F, 0.5F, -0.5F, -PI4),
            new Tick(0.75F, 0.75F, -0.75F, -PI4),
            new Tick(1, 1F, -1F, -PI4) };
    private final static MeleeOperation SWORD__THRUST_DIAG_UP = new MeleeOperation(
            "Thrust diag up", EMPTY__NEXT, STANDARD_CYCLE, SWORD__THRUST_WAITS,
            DirEnum.DOWNRIGHT, GradeEnum.F, SWORD__THRUST_DIAG_UP__EXEC);

    private final static Tick[] SWORD__THRUST_DIAG_DOWN__EXEC = new Tick[] {
            new Tick(0.25F, 0.25F, 0.25F, PI4),
            new Tick(0.5F, 0.5F, 0.5F, PI4),
            new Tick(0.75F, 0.75F, 0.75F, PI4),
            new Tick(1, 1F, 1F, PI4) };
    private final static MeleeOperation SWORD__THRUST_DIAG_DOWN = new MeleeOperation(
            "Thrust diag down", UNTERHAU_SWING__NEXT, STANDARD_CYCLE, SWORD__THRUST_WAITS,
            DirEnum.DOWNRIGHT, GradeEnum.F, SWORD__THRUST_DIAG_DOWN__EXEC);

    private final static Tick[] SWORD__LUNGE__EXEC = new Tick[] {
            new Tick(0.25F, 0.75F, -0.1F, 0),
            new Tick(0.5F, 1, -0.1F, 0),
            new Tick(0.75F, 1.25F, -0.1F, 0),
            new Tick(1, 1.5F, -0.1F, 0) };
    private final static MeleeOperation SWORD__LUNGE = new MeleeOperation(
            "Lunge", EMPTY__NEXT, LUNGE_CYCLE, SWORD__THRUST_WAITS,
            DirEnum.RIGHT, GradeEnum.F, SWORD__LUNGE__EXEC);

    private final static Tick[] SWORD__STAB__EXEC = new Tick[] {
            new Tick(0.25F, 1, 0F, PI2),
            new Tick(0.5F, 1, 0.25F, PI2),
            new Tick(0.75F, 1, 0.5F, PI2),
            new Tick(1, 1, 0.75F, PI2) };
    private final static MeleeOperation SWORD__STAB = new MeleeOperation(
            "Stab", UNTERHAU_SWING__NEXT, STAB_CYCLE, SWORD__THRUST_WAITS,
            DirEnum.DOWN, GradeEnum.F, SWORD__STAB__EXEC);

    private final static Tick[] SWORD__STAB_UNTERHAU__EXEC = new Tick[] {
            new Tick(0.25F, 1, 0.75F, PI2),
            new Tick(0.5F, 1, 0.5F, PI2),
            new Tick(0.75F, 1, 0.25F, PI2),
            new Tick(1, 1, 0F, PI2) };
    private final static MeleeOperation SWORD__STAB_UNTERHAU = new MeleeOperation(
            "Stab unterhau", EMPTY__NEXT, STAB_CYCLE, SWORD__THRUST_WAITS,
            DirEnum.UP, GradeEnum.F, SWORD__STAB_UNTERHAU__EXEC);

    private final static Tick[] SWORD__SWING__EXEC = new Tick[] {
            new Tick(0.04F, 1.05F, -0.7F, -0.8F),
            new Tick(0.08F, 1.4F, -0.4F, -0.4F),
            new Tick(0.12F, 1.5F, -0.1F, -0.1F),
            new Tick(0.16F, 1.4F, 0.2F, 0.2F) };
    private final static MeleeOperation SWORD__SWING = new MeleeOperation(
            "Swing", UNTERHAU_SWING__NEXT, STANDARD_CYCLE, SWORD__SWING_WAITS,
            DirEnum.DOWNRIGHT, GradeEnum.F, SWORD__SWING__EXEC);

    private final static Tick[] SWORD__SWING_UNTERHAU__EXEC = reverse(SWORD__SWING__EXEC);
    private final static MeleeOperation SWORD__SWING_UNTERHAU = new MeleeOperation(
            "Swing unterhau", EMPTY__NEXT, STANDARD_CYCLE, SWORD__SWING_WAITS,
            DirEnum.UPRIGHT, GradeEnum.F, SWORD__SWING_UNTERHAU__EXEC);
    private final static MeleeOperation SWORD__SWING_UNTERHAU_C = new MeleeOperation(
            "Swing unterhau c", SWORD__SWING_UNTERHAU, STAB_CYCLE);

    private final static MeleeOperation SWORD__SWING_AERIAL = new MeleeOperation(
            "Swing aerial", SWORD__SWING);

    private final static Tick[] SWORD__SWING_UP_FORWARD__EXEC = {
            new Tick(0.04F,  -0.8F,-0.6F, -2F),
            new Tick(0.08F,  -0.2F,-0.85F, -1.5F),
            new Tick(0.12F,  0.4F,-0.85F, -1F),
            new Tick(0.16F,  1.05F,-0.7F, -0.5F) };
    private final static MeleeOperation SWORD__SWING_UP_FORWARD = new MeleeOperation(
            "Swing up forward", BACK_SWING_UP__NEXT, STANDARD_CYCLE, SWORD__SWING_WAITS,
            DirEnum.UPRIGHT, GradeEnum.F, SWORD__SWING_UP_FORWARD__EXEC);
    private final static MeleeOperation SWORD__SWING_PRONE = new MeleeOperation(
            "Swing prone", SWORD__SWING_UP_FORWARD, PRONE_SWING__NEXT);
    private final static Tick[] SWORD__SWING_UP_BACKWARD__EXEC = reverse(SWORD__SWING_UP_FORWARD__EXEC);
    private final static MeleeOperation SWORD__SWING_UP_BACKWARD = new MeleeOperation(
            "Swing up backward", EMPTY__NEXT, STANDARD_CYCLE, SWORD__SWING_WAITS,
            DirEnum.UPLEFT, GradeEnum.F, SWORD__SWING_UP_BACKWARD__EXEC);
    private final static Tick[] SWORD__SWING_DOWN_FORWARD__EXEC = mirrorVert(SWORD__SWING_UP_FORWARD__EXEC);
    private final static MeleeOperation SWORD__SWING_DOWN_FORWARD = new MeleeOperation(
            "Swing down forward", BACK_SWING_DOWN__NEXT, STANDARD_CYCLE, SWORD__SWING_WAITS,
            DirEnum.DOWNRIGHT, GradeEnum.F, SWORD__SWING_DOWN_FORWARD__EXEC);
    private final static Tick[] SWORD__SWING_DOWN_BACKWARD__EXEC = mirrorVert(SWORD__SWING_UP_BACKWARD__EXEC);
    private final static MeleeOperation SWORD__SWING_DOWN_BACKWARD = new MeleeOperation(
            "Swing down backward", EMPTY__NEXT, STANDARD_CYCLE, SWORD__SWING_WAITS,
            DirEnum.DOWNLEFT, GradeEnum.F, SWORD__SWING_DOWN_BACKWARD__EXEC);

    public final static WeaponType SWORD = new WeaponType(
            new Orient(new Vec2(0.4F, 0.1F), -PI4),
            SWORD__THRUST, SWORD__THRUST_UNTERHAU,
            SWORD__THRUST_UP, SWORD__THRUST_DOWN,
            SWORD__THRUST_DIAG_UP, SWORD__THRUST_DIAG_DOWN,
            SWORD__LUNGE, SWORD__STAB, SWORD__STAB_UNTERHAU,
            SWORD__SWING, SWORD__SWING_UNTERHAU, SWORD__SWING_UNTERHAU_C,
            SWORD__SWING_AERIAL, SWORD__SWING_PRONE,
            SWORD__SWING_UP_FORWARD, SWORD__SWING_UP_BACKWARD,
            SWORD__SWING_DOWN_FORWARD, SWORD__SWING_DOWN_BACKWARD,
            null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, // empty ops (throwing)
            null // INTERACT
            );
}
