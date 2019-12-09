package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Gameplay.Item;
import Util.GradeEnum;
import Util.Vec2;

public class Natural extends Weapon_old
{
    private Operation PUNCH, PUNCH_UP, PUNCH_DIAG, PUSH, HAYMAKER, UPPERCUT,
            SHOVE, STOMP, STOMP_FALL, KICK, KICK_ARC, KICK_AERIAL,
            KICK_AERIAL_DIAG, GRAB, GRAB_LOW, TACKLE;

    private ConditionAppCycle basicCycle, pushCycle, uppercutCycle, shoveCycle,
            kickCycle, aerialCycle, lowCycle, tackleCycle;
    private Tick[][] punchJourneys, kickJourneys;
    private Tick[] grabJourney;

    private final int iPunch = 0, iKick = 1, iGrab = 2, iRush = 3;

    private static final String[] SPRITE_PATHS = null;

    //public Natural(CharacterStat charStat,
    //               float xPos, float yPos, float width, float height, float mass, Actor actor)
    public Natural(Actor actor, float xPos, float yPos)
    {
        super(xPos, yPos, 0.2f, 0.1f, actor.getActorType().naturalWeaponMass(), SPRITE_PATHS);

        WeaponStat naturalWeaponStats = actor.getActorType().createNaturalWeaponStat();
        setWeaponStats(naturalWeaponStats);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///                                                CONDITIONS                                               ///
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ConditionApp FORCE_STAND__NEGATE_RUN   =      FORCE_STAND .add(NEGATE_RUN );
        ConditionApp FORCE_STAND__NEGATE_WALK  =      FORCE_STAND .add(NEGATE_WALK);

        ConditionApp FORCE_CROUCH__NEGATE_RUN  =      FORCE_CROUCH.add(NEGATE_RUN );
        ConditionApp FORCE_CROUCH__NEGATE_WALK =      FORCE_CROUCH.add(NEGATE_WALK);

        ConditionApp NEGATE_WALK__LONG =              NEGATE_WALK .lengthen(0.4F  );

        ConditionApp FORCE_STAND__NEGATE_WALK__LONG = FORCE_STAND__NEGATE_WALK.lengthen(0.4F);

        basicCycle = new ConditionAppCycle(
                FORCE_STAND, FORCE_STAND__NEGATE_RUN, FORCE_STAND__NEGATE_RUN);
        pushCycle = new ConditionAppCycle(
                null, null, FORCE_STAND__NEGATE_WALK);
        uppercutCycle = new ConditionAppCycle(
                FORCE_CROUCH__NEGATE_WALK, FORCE_STAND__NEGATE_RUN, FORCE_STAND__NEGATE_RUN);
        shoveCycle = new ConditionAppCycle(null, null, null);
        kickCycle = new ConditionAppCycle(
                FORCE_STAND, FORCE_STAND__NEGATE_WALK, FORCE_STAND__NEGATE_WALK);
        aerialCycle = new ConditionAppCycle(
                FORCE_CROUCH, FORCE_CROUCH, /*FORCE_PRONE*/FORCE_CROUCH);
        lowCycle = new ConditionAppCycle(
                FORCE_CROUCH__NEGATE_WALK, FORCE_CROUCH__NEGATE_WALK, FORCE_STAND__NEGATE_WALK);
        tackleCycle = new ConditionAppCycle(
                FORCE_STAND.add(FORCE_DASH), NEGATE_RUN, FORCE_CROUCH__NEGATE_WALK);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///                                                JOURNEYS                                                 ///
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        punchJourneys = new Tick[][] {
                new Tick[] { /* PUNCH */
                        new Tick(0.05F, 0.7F, -0.2F, 0F),
                        new Tick(0.08F, 1.2F, -0.2F, 0F),
                        new Tick(0.13F, 1.7F, -0.2F, 0F) },
                new Tick[] { /* PUNCH_UP */
                        new Tick(0.05F, 0.4F, -0.1F, (float) -Math.PI / 2),
                        new Tick(0.08F, 0.4F, -0.4F, (float) -Math.PI / 2),
                        new Tick(0.13F, 0.4F, -0.8F, (float) -Math.PI / 2) },
                new Tick[] { /* PUNCH_DIAG */
                        new Tick(0.06F, 0.8F, -0.35F, (float) -Math.PI / 4),
                        new Tick(0.10F, 1.2F, -0.6F, (float) -Math.PI / 4),
                        new Tick(0.16F, 1.6F, -0.85F, (float) -Math.PI / 4) },
                new Tick[] { /* HAYMAKER */
                        new Tick(0.05F, 1.4F, -0.4F, (float) Math.PI / 4F) },
                new Tick[] { /* UPPERCUT */
                        new Tick(0.04F, 1.4F, 0.2F, 0.2F),
                        new Tick(0.08F, 1.5F, -0.1F, -0.1F),
                        new Tick(0.12F, 1.4F, -0.4F, -0.4F),
                        new Tick(0.16F, 1.05F, -0.7F, -0.8F) }
        };

        kickJourneys = new Tick[][] {
                new Tick[] { /* STOMP */
                        new Tick(0.04F, 0.7F, 0F, 0),
                        new Tick(0.08F, 0.7F, 0.2F, 0),
                        new Tick(0.12F, 0.7F, 0.4F, 0) },
                new Tick[] { /* KICK */
                        new Tick(0.07F, 0.8F, 0F, 0F),
                        new Tick(0.11F, 1.3F, 0F, 0F),
                        new Tick(0.17F, 1.9F, 0F, 0F) },
                new Tick[] { /* KICK_ARC */
                        new Tick(0.05F, 0.5F, 0.4F, 0F),
                        new Tick(0.09F, 1.1F, 0.2F, (float) Math.PI / 4),
                        new Tick(0.14F, 1.7F, 0F, (float) Math.PI / 2) },
                new Tick[] { /* KICK_AERIAL */
                        new Tick(0.05F, 0.8F, 0F, 0F),
                        new Tick(0.10F, 1.3F, 0F, 0F),
                        new Tick(0.15F, 1.9F, 0F, 0F) },
                new Tick[] { /* KICK_AERIAL_DIAG */
                        new Tick(0.05F, 0.5F, 0F, (float) Math.PI / 4F),
                        new Tick(0.10F, 0.8F, 0.1F, (float) Math.PI / 4F),
                        new Tick(0.15F, 1.2F, 0.2F, (float) Math.PI / 4F) },
        };

        grabJourney = new Tick[] {
                new Tick(0.05F, 0.7F, -0.2F, (float) Math.PI / 2F),
                new Tick(0.08F, 1.2F, -0.2F, (float) Math.PI / 2F),
                new Tick(0.13F, 1.7F, -0.2F, (float) Math.PI / 2F) };

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        equip(actor);
    }


    @Override
    Operation getOperation(Command command, Operation currentOp)
    {
        if (command.ATTACK_KEY == Actor.ATTACK_KEY_1)
        {
            if (command.TYPE == Command.StateType.LOW)
                return setOperation(UPPERCUT, command);
            /* if (command.TYPE != Command.StateType.LOW) */
            if (command.DIR == DirEnum.UP)
                return setOperation(PUNCH_UP, command);
            if (command.DIR.getVert() == DirEnum.UP
                    && command.DIR.getHoriz().getSign() != 0)
                return setOperation(PUNCH_DIAG, command);
            if (command.SPRINT) return setOperation(PUSH, command);
            return setOperation(PUNCH, command);
        }

        if (command.ATTACK_KEY == Actor.ATTACK_KEY_2)
        {
            if (command.TYPE == Command.StateType.LOW)
                return setOperation(UPPERCUT, command);
            if (command.SPRINT) return setOperation(SHOVE, command);
            return setOperation(HAYMAKER, command);
        }

        if (command.ATTACK_KEY == Actor.ATTACK_KEY_3)
        {
            if (command.TYPE == Command.StateType.STANDARD)
            {
                if (command.DIR.getHoriz() == DirEnum.NONE)
                    return setOperation(STOMP, command);
                return setOperation(KICK_ARC, command);
            }
            if (command.TYPE == Command.StateType.FREE)
            {
                if (command.DIR.getVert() == DirEnum.DOWN)
                {
                    if (command.DIR.getHoriz() == DirEnum.NONE)
                        return setOperation(STOMP_FALL, command);
                    return setOperation(KICK_AERIAL_DIAG, command);
                }
                return setOperation(KICK_AERIAL, command);
            }
        }

        if (command.ATTACK_KEY == Actor.ATTACK_KEY_1 + Actor.ATTACK_KEY_MOD)
        {
            if (command.TYPE == Command.StateType.LOW)
                return setOperation(GRAB_LOW, command);
            if (command.TYPE == Command.StateType.STANDARD)
            {
                if (command.SPRINT)
                    return setOperation(TACKLE, command);
                return setOperation(GRAB, command);
            }
            if (command.TYPE == Command.StateType.FREE)
                return setOperation(TACKLE, command);
        }

        if (command.ATTACK_KEY == Actor.ATTACK_KEY_3 + Actor.ATTACK_KEY_MOD)
        {
            return setOperation(KICK, command);
        }

        System.out.println(WeaponTypeEnum_old.SHORT_SWORD.DRAW);

        return null;
    }

    @Override
    boolean isApplicable(Command command) { return true; }

    @Override
    boolean clash(Weapon_old otherWeapon, Operation otherOp, GradeEnum damage)
    {
        //Print.green(this + " clashed by " + otherWeapon + " using " + otherOp);

        if (currentOp != null)
        {
            if (!currentOp.isDisruptive())
            {
                if (otherOp != null)
                {
                    if (otherOp.isDisruptive())
                    {
                        disrupt(damage);//Print.green("Natural: Interrupted us");
                        return true;
                    }
                    else return false;//Print.green("Natural Uninterrupted");
                }
                else return false;//Print.green("Natural: Uninterrupted");
            }
            else // if (currentOp.isDisruptive())
            {
                if (otherOp != null)
                {
                    if (otherOp.isDisruptive())
                    {
                        disrupt(damage);//Print.green("Natural: Interrupted us, interrupted them");
                        return true;
                    }
                    else return false;//Print.green("Natural: Interrupted them");
                }
                else return false;//Print.green("Natural: Uninterrupted");
            }
        }

        return false;
    }

    @Override
    Vec2 getMomentum(Operation operation, DirEnum dir, Weapon_old other)
    {
        return new Vec2(dir.getHoriz().getSign() * getMass(), dir.getVert().getSign() * getMass());
    }

    @Override
    public int getBlockRating() { return 1; }

    @Override
    Orient getDefaultOrient()
    {
        return new Orient(new Vec2(0.8F, 0), 0);
    }


    @Override
    void setup()
    {
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///                                                SPEEDS                                                   ///
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        for (Tick[] journey : punchJourneys)
        {
            for (int i = 0; i < journey.length; i++)
            {
                journey[i].setSpeed(speeds[iPunch]);
            }
        }
        for (Tick[] journey : kickJourneys)
        {
            for (int i = 0; i < journey.length; i++)
            {
                journey[i].setSpeed(speeds[iKick]);
            }
        }

        for (int i = 0; i < grabJourney.length; i++)
        {
            grabJourney[i].setSpeed(speeds[iGrab]);
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///                                                CLASSES                                                  ///
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        class Punch extends Melee {
            Punch(Vec2 waits, DirEnum functionalDir, boolean useDirHorizFunctionally,
                  ConditionAppCycle statusAppCycle, Tick[] execJourney) {
                super("punch", waits, functionalDir, useDirHorizFunctionally, new int[]{DURING_COOLDOWN},
                        damages[iPunch], critThreshSpeeds[iPunch],
                        false, false, statusAppCycle, null, execJourney); }
            Punch(Vec2 waits, DirEnum functionalDir,
                  ConditionAppCycle statusAppCycle, Tick[] execJourney) {
                super("haymaker", waits, functionalDir, true, new int[]{},
                        damages[iPunch], critThreshSpeeds[iPunch],
                        true, true, statusAppCycle, null, execJourney); } }

        class Kick extends Melee {
            Kick(Vec2 waits, DirEnum functionalDir, boolean useDirHorizFunctionally, ConditionAppCycle statusAppCycle, Tick[] execJourney) {
                super("kick", waits, functionalDir, useDirHorizFunctionally, new int[]{DURING_COOLDOWN},
                        damages[iKick], critThreshSpeeds[iKick],
                        true, true, statusAppCycle, null, execJourney, execJourney[0]); }
            public void start() {
                super.start();
                Tick footPosition = new Tick(0, 0.7F, 0.4F, 0);
                footPosition.check(-1, command.FACE);
                warmJourney.setStart(footPosition.getOrient());
            }
        }

        class KickAerial extends HoldableRush {
            KickAerial(Vec2 waits, float minExecTime, float maxExecTime, DirEnum functionalDir, boolean useDirHorizFunctionally,
                       ConditionAppCycle conditionAppCycle) {
                super(waits, minExecTime, maxExecTime, functionalDir, useDirHorizFunctionally,
                        damages[iKick], critThreshSpeeds[iKick],
                        conditionAppCycle, null); }

            @Override
            public void apply(Weapon_old _this, Item other)
            {
                if (other == null)
                {
                    if (actor.getState() == Actor.State.SWIM
                            || actor.getState().isGrounded()
                            || actor.getState().isOnWall())
                        state = State.COOLDOWN;
                }
                super.apply(_this, other);
            }
        }

        class Tackle extends Rush {
            Tackle(Vec2 waits, float execTime, ConditionAppCycle conditionAppCycle) {
                super(waits, execTime, DirEnum.NONE, true,
                        damages[iRush], critThreshSpeeds[iRush],
                        conditionAppCycle, null); }

            @Override
            public void apply(Weapon_old _this, Item other)
            {
                if (other == null)
                {
                    if (totalSec < execTime) return;
                    if (actor.getState() == Actor.State.SWIM
                            || actor.getState().isGrounded())
                        state = State.COOLDOWN;
                }
                super.apply(_this, other);
            }
        }

        class _HoldableRush extends HoldableRush {
            _HoldableRush(Vec2 waits, float minExecTime, float maxExecTime, ConditionAppCycle conditionAppCycle) {
                super(waits, minExecTime, maxExecTime, DirEnum.NONE, true,
                        damages[iRush], critThreshSpeeds[iRush],
                        conditionAppCycle, null); } }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///                                                ATTACKS                                                  ///
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        PUNCH = new Punch(new Vec2(0.4F, 0.3F), DirEnum.NONE, true, basicCycle, punchJourneys[0]);
        PUNCH_UP = new Punch(new Vec2(0.4F, 0.3F), DirEnum.UP, false, basicCycle, punchJourneys[1]);
        PUNCH_DIAG = new Punch(new Vec2(0.4F, 0.3F), DirEnum.UP, true, basicCycle, punchJourneys[2]);
        PUSH = new _HoldableRush(new Vec2(0.1F, 0.1F), 0.2F, 0, pushCycle);
        HAYMAKER = new Punch(new Vec2(0.3F, 0.3F), DirEnum.NONE, basicCycle, punchJourneys[3]);
        UPPERCUT = new Punch(new Vec2(0.3F, 0.4F), DirEnum.UP, true, uppercutCycle, punchJourneys[4]);
        SHOVE = new _HoldableRush(new Vec2(0.1F, 0.1F), 0.2F, 0, shoveCycle);

        STOMP = new Kick(new Vec2(0.4F, 0.1F), DirEnum.DOWN, false, kickCycle, kickJourneys[0]);
        STOMP_FALL = new KickAerial(new Vec2(0.1F, 0.1F), 0.5F, 0, DirEnum.DOWN, false, kickCycle);
        KICK = new Kick(new Vec2(0.3F, 0.4F), DirEnum.NONE, true, kickCycle, kickJourneys[1]);
        KICK_ARC = new Kick(new Vec2(0.3F, 0.4F), DirEnum.UP, true, kickCycle, kickJourneys[2]);
        KICK_AERIAL = new KickAerial(new Vec2(0.2F, 0.2F), 0.5F, 0, DirEnum.NONE, true, aerialCycle);
        KICK_AERIAL_DIAG = new KickAerial(new Vec2(0.2F, 0.2F), 0.5F, 0, DirEnum.DOWN, true, aerialCycle);

        GRAB = new Punch(new Vec2(0.3F, 0.4F), DirEnum.NONE, true, basicCycle, grabJourney);
        GRAB_LOW = new Punch(new Vec2(0.4F, 0.4F), DirEnum.NONE, true, lowCycle, grabJourney);
        TACKLE = new Tackle(new Vec2(0.1F, 0.1F), 0.1F, tackleCycle);
    }

    @Override
    public boolean easyToBlock()
    {
        if (currentOp != null) return currentOp.isEasyToBlock();
        return true;
    }
}