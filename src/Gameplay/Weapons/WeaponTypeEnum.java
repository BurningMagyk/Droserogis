package Gameplay.Weapons;

import Gameplay.Actor;

public enum WeaponTypeEnum
{
    SHORT_SWORD, LONG_SWORD, GREATSWORD, SCIMITAR, RAPIER,
    DAGGER, KNIFE, THROWING_KNIFE, PARRYING_DAGGER, SICKLE, ROCK,
    BATTLEAXE, GREATAXE, THROWING_AXE, HATCHET, PICKAXE,
    MACE, FLAIL, MORNING_STAR, SHOVEL, STICK, WHIP,
    SARISSA, HALBERD, GLAIVE, WAR_SCYTHE, SPEAR, PITCHFORK,
        QUARTERSTAFF, LANCE, JAVELIN, SCYTHE,
    SHORTBOW, LONGBOW, WARBOW, RECURVE_BOW, CROSSBOW, SLING,
    FISTS, FEET, CLAWS, TEETH, HORNS, TAIL;

    private final static int ALT_STANCE = 0, ALT_ATTACK = 1,
            ALT_SELF = 2, HOLDABLE = 3, ALT_TRAJ_A = 4, ALT_TRAJ_B = 5;

    static class Stat
    {
        final boolean STANCE, ATTACK, SELF, HOLD;
        final int TRAJ;
        ConditionAppCycle cycle;
        Stat(int ...args)
        {
            boolean[] boolVars = { false, false, false, false };
            int intVal = 0;
            for (int arg : args)
            {
                if (arg <= HOLDABLE) boolVars[arg] = true;
                else intVal = arg - ALT_TRAJ_A + 1;
            }
            STANCE = boolVars[ALT_STANCE]; // rapier thrust
            ATTACK = boolVars[ALT_ATTACK]; // sword/battleaxe swing
            SELF = boolVars[ALT_SELF]; // flail/quarterstaff swing
            TRAJ = intVal; // fists/feet swing
            HOLD = boolVars[HOLDABLE]; // polearm thrust
        }
        Stat(boolean arg1, boolean arg2,
             boolean arg3, boolean arg4, int arg5)
        {
            STANCE = arg1;
            ATTACK = arg2;
            SELF = arg3;
            HOLD = arg4;
            TRAJ = arg5;
        }
        void setConditionAppCycle(ConditionAppCycle cycle) { this.cycle = cycle; }
        Stat copy() { return new Stat(STANCE, ATTACK, SELF, HOLD, TRAJ); }
    }

    static class ConditionAppCycle
    {
        Actor actor;
        Weapon.ConditionApp[] conditionApps = new Weapon.ConditionApp[3];
        ConditionAppCycle(Weapon.ConditionApp warmup, Weapon.ConditionApp execution, Weapon.ConditionApp cooldown)
        {
            conditionApps[0] = warmup;
            conditionApps[1] = execution;
            conditionApps[2] = cooldown;
        }

        void setActor(Actor actor) { this.actor = actor; }
        void applyWarmup(float timeMod) { apply(0, timeMod); }
        void applyExecution(float timeMod) { apply(1, timeMod); }
        void applyCooldown(float timeMod) { apply(2, timeMod); }
        private void apply(int step, float timeMod)
        {
            if (conditionApps[step] != null) conditionApps[step].apply(actor, timeMod);
        }
    }

    static Weapon.ConditionApp FORCE_STAND = new Weapon.ConditionApp(0.1F, Actor.Condition.FORCE_STAND);
    //static ConditionApp forceStand_long = new ConditionApp(forceStand, 0.4F);
    static Weapon.ConditionApp FORCE_CROUCH = new Weapon.ConditionApp(0.1F, Actor.Condition.FORCE_CROUCH);
    static Weapon.ConditionApp FORCE_DASH = new Weapon.ConditionApp(0.01F, Actor.Condition.DASH);

    static Weapon.ConditionApp NEGATE_SPRINT = new Weapon.ConditionApp(0.01F, Actor.Condition.NEGATE_SPRINT_LEFT, Actor.Condition.NEGATE_SPRINT_RIGHT);
    static Weapon.ConditionApp NEGATE_RUN = new Weapon.ConditionApp(0.01F, Actor.Condition.NEGATE_RUN_LEFT, Actor.Condition.NEGATE_RUN_RIGHT);
    //static ConditionApp negateRun_forceStand = new ConditionApp(negateRun, Actor.Condition.FORCE_STAND);
    //static ConditionApp negateRun_forceCrouch = new ConditionApp(negateRun, Actor.Condition.FORCE_CROUCH);
    static Weapon.ConditionApp NEGATE_WALK = new Weapon.ConditionApp(0.01F, Actor.Condition.NEGATE_WALK_LEFT, Actor.Condition.NEGATE_WALK_RIGHT);

    static Weapon.ConditionApp FORCE_STAND__NEGATE_SPRINT=      FORCE_STAND .add(NEGATE_SPRINT);
    static Weapon.ConditionApp FORCE_STAND__NEGATE_RUN   =      FORCE_STAND .add(NEGATE_RUN );
    static Weapon.ConditionApp FORCE_STAND__NEGATE_WALK  =      FORCE_STAND .add(NEGATE_WALK);

    static Weapon.ConditionApp FORCE_CROUCH__NEGATE_RUN  =      FORCE_CROUCH.add(NEGATE_RUN );
    static Weapon.ConditionApp FORCE_CROUCH__NEGATE_WALK =      FORCE_CROUCH.add(NEGATE_WALK);

    static Weapon.ConditionApp NEGATE_WALK__LONG =              NEGATE_WALK .lengthen(0.4F  );

    static Weapon.ConditionApp FORCE_STAND__NEGATE_WALK__LONG = FORCE_STAND__NEGATE_WALK.lengthen(0.4F);

    private ConditionAppCycle commonCycle;
    private static void setCycle(WeaponTypeEnum type)
    {
        ConditionAppCycle basicCycle = new ConditionAppCycle(
                FORCE_STAND, FORCE_STAND__NEGATE_RUN, FORCE_STAND__NEGATE_RUN);
        ConditionAppCycle uppercutCycle = new ConditionAppCycle(
                FORCE_CROUCH__NEGATE_WALK, FORCE_STAND__NEGATE_RUN, NEGATE_WALK__LONG);
        ConditionAppCycle lungeCycle = new ConditionAppCycle(
                FORCE_DASH, FORCE_STAND__NEGATE_RUN, FORCE_STAND__NEGATE_WALK__LONG);
        ConditionAppCycle kickCycle = new ConditionAppCycle(
                FORCE_STAND, FORCE_STAND__NEGATE_WALK, FORCE_STAND__NEGATE_WALK);

        if (type.THRUST != null) type.THRUST.setConditionAppCycle(basicCycle);
        if (type.THRUST_UP != null) type.THRUST_UP.setConditionAppCycle(basicCycle);
        if (type.THRUST_DOWN != null) type.THRUST_DOWN.setConditionAppCycle(basicCycle);
        if (type.THRUST_DIAG_UP != null) type.THRUST_DIAG_UP.setConditionAppCycle(basicCycle);
        if (type.THRUST_DIAG_DOWN != null) type.THRUST_DIAG_DOWN.setConditionAppCycle(basicCycle);
        if (type.THRUST_LUNGE != null) type.THRUST_LUNGE.setConditionAppCycle(lungeCycle);
        if (type.STAB != null) type.STAB.setConditionAppCycle(kickCycle);
        if (type.STAB_UNTERHAU != null) type.STAB_UNTERHAU.setConditionAppCycle(uppercutCycle);
        if (type.SWING != null) type.SWING.setConditionAppCycle(basicCycle);
        if (type.SWING_UNTERHAU != null) type.SWING_UNTERHAU.setConditionAppCycle(uppercutCycle);
        if (type.SWING_UNTERHAU_CROUCH != null) type.SWING_UNTERHAU_CROUCH.setConditionAppCycle(uppercutCycle);
        if (type.SWING_UP_FORWARD != null) type.SWING_UP_FORWARD.setConditionAppCycle(basicCycle);
        if (type.SWING_UP_BACKWARD != null) type.SWING_UP_BACKWARD.setConditionAppCycle(basicCycle);
        if (type.SWING_DOWN_FORWARD != null) type.SWING_DOWN_FORWARD.setConditionAppCycle(basicCycle);
        if (type.SWING_DOWN_BACKWARD != null) type.SWING_DOWN_BACKWARD.setConditionAppCycle(basicCycle);
        if (type.SWING_LUNGE != null) type.SWING_LUNGE.setConditionAppCycle(lungeCycle);
        if (type.SWING_LUNGE_UNTERHAU != null) type.SWING_LUNGE_UNTERHAU.setConditionAppCycle(lungeCycle);
        if (type.GRAB != null) type.GRAB.setConditionAppCycle(kickCycle);
        if (type.DRAW != null) type.DRAW.setConditionAppCycle(basicCycle);
        if (type.LOAD != null) type.LOAD.setConditionAppCycle(basicCycle);
        if (type.SHOOT != null) type.SHOOT.setConditionAppCycle(basicCycle);

        ConditionAppCycle nimbleCycle = new ConditionAppCycle(
                NEGATE_SPRINT, NEGATE_SPRINT, NEGATE_SPRINT);
        if (type == DAGGER || type == FISTS || type == HORNS)
        {
            if (type.THRUST != null)            type.THRUST.setConditionAppCycle(nimbleCycle);
            if (type.THRUST_UP != null)         type.THRUST_UP.setConditionAppCycle(nimbleCycle);
            if (type.THRUST_DIAG_UP != null)    type.THRUST_DIAG_UP.setConditionAppCycle(nimbleCycle);
            if (type.STAB != null)
                type.STAB.setConditionAppCycle(
                        new ConditionAppCycle(NEGATE_SPRINT, NEGATE_SPRINT, FORCE_CROUCH__NEGATE_WALK));
            if (type.STAB_UNTERHAU != null)
                type.STAB_UNTERHAU.setConditionAppCycle(
                        new ConditionAppCycle(NEGATE_SPRINT, FORCE_STAND__NEGATE_RUN, NEGATE_WALK));
            if (type.SWING != null)             type.SWING.setConditionAppCycle(nimbleCycle);
            if (type.SWING_UNTERHAU != null)    type.SWING_UNTERHAU.setConditionAppCycle(nimbleCycle);
            if (type.SWING_UP_FORWARD != null)  type.SWING_UP_FORWARD.setConditionAppCycle(nimbleCycle);
            if (type.SWING_UP_BACKWARD != null) type.SWING_UP_BACKWARD.setConditionAppCycle(nimbleCycle);
            if (type.GRAB != null)              type.GRAB.setConditionAppCycle(basicCycle);
        }


        if (type == FEET)
        {
            type.THRUST.setConditionAppCycle(kickCycle);
            type.STAB.setConditionAppCycle(kickCycle);
            type.SWING_UNTERHAU.setConditionAppCycle(kickCycle);
        }
    }

    private static void copyStat(WeaponTypeEnum typeA, WeaponTypeEnum typeB)
    {
        typeB.THRUST                  = typeA.THRUST == null ? null : typeA.THRUST.copy();
        typeB.THRUST_UP               = typeA.THRUST_UP == null ? null : typeA.THRUST_UP.copy();
        typeB.THRUST_DOWN             = typeA.THRUST_DOWN == null ? null : typeA.THRUST_DOWN.copy();
        typeB.THRUST_DIAG_UP          = typeA.THRUST_DIAG_UP == null ? null : typeA.THRUST_DIAG_UP.copy();
        typeB.THRUST_DIAG_DOWN        = typeA.THRUST_DIAG_DOWN == null ? null : typeA.THRUST_DIAG_DOWN.copy();
        typeB.THRUST_LUNGE            = typeA.THRUST_LUNGE == null ? null : typeA.THRUST_LUNGE.copy();
        typeB.STAB                    = typeA.STAB == null ? null : typeA.STAB.copy();
        typeB.STAB_UNTERHAU           = typeA.STAB_UNTERHAU == null ? null : typeA.STAB_UNTERHAU.copy();
        typeB.SWING                   = typeA.SWING == null ? null : typeA.SWING.copy();
        typeB.SWING_UNTERHAU          = typeA.SWING_UNTERHAU == null ? null : typeA.SWING_UNTERHAU.copy();
        typeB.SWING_UNTERHAU_CROUCH   = typeA.SWING_UNTERHAU_CROUCH == null ? null : typeA.SWING_UNTERHAU_CROUCH.copy();
        typeB.SWING_UP_FORWARD        = typeA.SWING_UP_FORWARD == null ? null : typeA.SWING_UP_FORWARD.copy();
        typeB.SWING_UP_BACKWARD       = typeA.SWING_UP_BACKWARD == null ? null : typeA.SWING_UP_BACKWARD.copy();
        typeB.SWING_DOWN_FORWARD      = typeA.SWING_DOWN_FORWARD == null ? null : typeA.SWING_DOWN_FORWARD.copy();
        typeB.SWING_DOWN_BACKWARD     = typeA.SWING_DOWN_BACKWARD == null ? null : typeA.SWING_DOWN_BACKWARD.copy();
        typeB.SWING_LUNGE             = typeA.SWING_LUNGE == null ? null : typeA.SWING_LUNGE.copy();
        typeB.SWING_LUNGE_UNTERHAU    = typeA.SWING_LUNGE_UNTERHAU == null ? null : typeA.SWING_LUNGE_UNTERHAU.copy();
        typeB.GRAB                    = typeA.GRAB == null ? null : typeA.GRAB.copy();
        typeB.DRAW                    = typeA.DRAW == null ? null : typeA.DRAW.copy();
        typeB.LOAD                    = typeA.LOAD == null ? null : typeA.LOAD.copy();
        typeB.SHOOT                   = typeA.SHOOT == null ? null : typeA.SHOOT.copy();
        typeB.BLOCK                   = typeA.BLOCK;
        typeB.PARRY                   = typeA.PARRY;
    }

    Stat
        THRUST, THRUST_UP, THRUST_DOWN, THRUST_DIAG_UP, THRUST_DIAG_DOWN,
            THRUST_LUNGE,
        STAB, STAB_UNTERHAU,
        SWING, SWING_UNTERHAU, SWING_UNTERHAU_CROUCH, SWING_UP_FORWARD,
            SWING_UP_BACKWARD, SWING_DOWN_FORWARD, SWING_DOWN_BACKWARD,
            SWING_LUNGE, SWING_LUNGE_UNTERHAU,
        GRAB,
        DRAW, LOAD, SHOOT;
    boolean BLOCK, PARRY;

    static
    {
        SHORT_SWORD.THRUST                  = new Stat();
        SHORT_SWORD.THRUST_UP               = new Stat();
        SHORT_SWORD.THRUST_DOWN             = new Stat(HOLDABLE);
        SHORT_SWORD.THRUST_DIAG_UP          = new Stat();
        SHORT_SWORD.THRUST_DIAG_DOWN        = new Stat(HOLDABLE);
        SHORT_SWORD.THRUST_LUNGE            = new Stat();
        SHORT_SWORD.STAB                    = new Stat(HOLDABLE);
        SHORT_SWORD.STAB_UNTERHAU           = new Stat(HOLDABLE);
        SHORT_SWORD.SWING                   = new Stat(ALT_ATTACK);
        SHORT_SWORD.SWING_UNTERHAU          = new Stat(ALT_ATTACK);
        SHORT_SWORD.SWING_UNTERHAU_CROUCH   = new Stat(ALT_ATTACK);
        SHORT_SWORD.SWING_UP_FORWARD        = new Stat(ALT_ATTACK);
        SHORT_SWORD.SWING_UP_BACKWARD       = new Stat(ALT_ATTACK);
        SHORT_SWORD.SWING_DOWN_FORWARD      = new Stat(ALT_ATTACK);
        SHORT_SWORD.SWING_DOWN_BACKWARD     = new Stat(ALT_ATTACK);
        SHORT_SWORD.SWING_LUNGE             = new Stat();
        SHORT_SWORD.SWING_LUNGE_UNTERHAU    = new Stat();
        SHORT_SWORD.DRAW                    = null;
        SHORT_SWORD.LOAD                    = null;
        SHORT_SWORD.SHOOT                   = null;
        SHORT_SWORD.BLOCK                   = true;
        SHORT_SWORD.PARRY                   = true;
        copyStat(SHORT_SWORD, LONG_SWORD);
        copyStat(SHORT_SWORD, GREATSWORD);
        copyStat(SHORT_SWORD, SCIMITAR);
        SCIMITAR.SWING                      = new Stat();
        SCIMITAR.SWING_UNTERHAU             = new Stat();
        SCIMITAR.SWING_UNTERHAU_CROUCH      = new Stat();
        SCIMITAR.SWING_UP_FORWARD           = new Stat();
        SCIMITAR.SWING_UP_BACKWARD          = new Stat();
        SCIMITAR.SWING_DOWN_FORWARD         = new Stat();
        SCIMITAR.SWING_DOWN_BACKWARD        = new Stat();
        copyStat(SHORT_SWORD, RAPIER);
        RAPIER.THRUST                       = new Stat(ALT_STANCE);
        RAPIER.THRUST_UP                    = new Stat(ALT_STANCE);
        RAPIER.THRUST_DOWN                  = new Stat(ALT_STANCE, HOLDABLE);
        RAPIER.THRUST_DIAG_UP               = new Stat(ALT_STANCE);
        RAPIER.THRUST_DIAG_DOWN             = new Stat(ALT_STANCE, HOLDABLE);
        RAPIER.STAB                         = new Stat(ALT_STANCE, HOLDABLE);
        RAPIER.STAB_UNTERHAU                = new Stat(ALT_STANCE, HOLDABLE);
        setCycle(SHORT_SWORD);
        setCycle(LONG_SWORD);
        setCycle(GREATSWORD);
        setCycle(SCIMITAR);
        setCycle(RAPIER);

        DAGGER.THRUST                       = new Stat();
        DAGGER.THRUST_UP                    = new Stat();
        DAGGER.THRUST_DOWN                  = null;
        DAGGER.THRUST_DIAG_UP               = new Stat();
        DAGGER.THRUST_DIAG_DOWN             = null;
        DAGGER.THRUST_LUNGE                 = null;
        DAGGER.STAB                         = new Stat();
        DAGGER.STAB_UNTERHAU                = new Stat();
        DAGGER.SWING                        = new Stat(ALT_ATTACK);
        DAGGER.SWING_UNTERHAU               = new Stat(ALT_ATTACK);
        DAGGER.SWING_UNTERHAU_CROUCH        = new Stat(ALT_ATTACK);
        DAGGER.SWING_UP_FORWARD             = new Stat(ALT_ATTACK);
        DAGGER.SWING_UP_BACKWARD            = new Stat(ALT_ATTACK);
        DAGGER.SWING_DOWN_FORWARD           = null;
        DAGGER.SWING_DOWN_BACKWARD          = null;
        DAGGER.SWING_LUNGE                  = null;
        DAGGER.SWING_LUNGE_UNTERHAU         = null;
        DAGGER.DRAW                         = null;
        DAGGER.LOAD                         = null;
        DAGGER.SHOOT                        = null;
        DAGGER.BLOCK                        = false;
        DAGGER.PARRY                        = false;
        copyStat(DAGGER, KNIFE);
        copyStat(DAGGER, THROWING_KNIFE);
        copyStat(DAGGER, PARRYING_DAGGER);
        PARRYING_DAGGER.BLOCK               = true;
        PARRYING_DAGGER.PARRY               = true;
        copyStat(DAGGER, SICKLE);
        SICKLE.THRUST                       = null;
        SICKLE.THRUST_UP                    = null;
        SICKLE.THRUST_DIAG_UP               = null;
        SICKLE.SWING                        = new Stat();
        SICKLE.SWING_UNTERHAU               = new Stat();
        SICKLE.SWING_UNTERHAU_CROUCH        = new Stat();
        SICKLE.SWING_UP_FORWARD             = new Stat();
        SICKLE.SWING_UP_BACKWARD            = new Stat();
        copyStat(SICKLE, ROCK);
        setCycle(DAGGER);
        setCycle(KNIFE);
        setCycle(THROWING_KNIFE);
        setCycle(PARRYING_DAGGER);
        setCycle(SICKLE);
        setCycle(ROCK);

        BATTLEAXE.THRUST                    = new Stat();
        BATTLEAXE.THRUST_UP                 = new Stat();
        BATTLEAXE.THRUST_DOWN               = null;
        BATTLEAXE.THRUST_DIAG_UP            = new Stat();
        BATTLEAXE.THRUST_DIAG_DOWN          = null;
        BATTLEAXE.THRUST_LUNGE              = new Stat();
        BATTLEAXE.STAB                      = null;
        BATTLEAXE.STAB_UNTERHAU             = null;
        BATTLEAXE.SWING                     = new Stat(ALT_ATTACK);
        BATTLEAXE.SWING_UNTERHAU            = new Stat(ALT_ATTACK);
        BATTLEAXE.SWING_UNTERHAU_CROUCH     = new Stat(ALT_ATTACK);
        BATTLEAXE.SWING_UP_FORWARD          = new Stat(ALT_ATTACK);
        BATTLEAXE.SWING_UP_BACKWARD         = new Stat(ALT_ATTACK);
        BATTLEAXE.SWING_DOWN_FORWARD        = null;
        BATTLEAXE.SWING_DOWN_BACKWARD       = null;
        BATTLEAXE.SWING_LUNGE               = new Stat();
        BATTLEAXE.SWING_LUNGE_UNTERHAU      = new Stat();
        BATTLEAXE.DRAW                      = null;
        BATTLEAXE.LOAD                      = null;
        BATTLEAXE.SHOOT                     = null;
        BATTLEAXE.BLOCK                     = false;
        BATTLEAXE.PARRY                     = true;
        copyStat(BATTLEAXE, GREATAXE);
        copyStat(BATTLEAXE, THROWING_AXE);
        THROWING_AXE.SWING                  = new Stat();
        THROWING_AXE.SWING_UNTERHAU         = new Stat();
        THROWING_AXE.SWING_UNTERHAU_CROUCH  = new Stat();
        THROWING_AXE.SWING_UP_FORWARD       = new Stat();
        THROWING_AXE.SWING_UP_BACKWARD      = new Stat();
        THROWING_AXE.PARRY                  = false;
        copyStat(THROWING_AXE, HATCHET);
        copyStat(BATTLEAXE, PICKAXE);
        PICKAXE.BLOCK                       = true;
        PICKAXE.PARRY                       = false;
        setCycle(BATTLEAXE);
        setCycle(GREATAXE);
        setCycle(THROWING_AXE);
        setCycle(HATCHET);
        setCycle(PICKAXE);

        copyStat(SHORT_SWORD, MACE);
        MACE.STAB                           = new Stat();
        MACE.STAB_UNTERHAU                  = new Stat();
        MACE.BLOCK                          = false;
        MACE.PARRY                          = false;
        copyStat(MACE, FLAIL);
        FLAIL.SWING                         = new Stat(ALT_SELF);
        FLAIL.SWING_UNTERHAU                = new Stat(ALT_SELF);
        copyStat(MACE, MORNING_STAR);
        MORNING_STAR.PARRY                  = true;
        copyStat(SHORT_SWORD, SHOVEL);
        copyStat(MACE, STICK);
        STICK.BLOCK                         = true;
        WHIP.THRUST                         = null;
        WHIP.THRUST_UP                      = null;
        WHIP.THRUST_DOWN                    = null;
        WHIP.THRUST_DIAG_UP                 = null;
        WHIP.THRUST_DIAG_DOWN               = null;
        WHIP.THRUST_LUNGE                   = null;
        WHIP.STAB                           = null;
        WHIP.STAB_UNTERHAU                  = null;
        WHIP.SWING                          = new Stat(ALT_ATTACK);
        WHIP.SWING_UNTERHAU                 = new Stat(ALT_ATTACK);
        WHIP.SWING_UNTERHAU_CROUCH          = new Stat(ALT_ATTACK);
        WHIP.SWING_UP_FORWARD               = new Stat(ALT_ATTACK);
        WHIP.SWING_UP_BACKWARD              = new Stat(ALT_ATTACK);
        WHIP.SWING_DOWN_FORWARD             = null;
        WHIP.SWING_DOWN_BACKWARD            = null;
        WHIP.SWING_LUNGE                    = null;
        WHIP.SWING_LUNGE_UNTERHAU           = null;
        WHIP.DRAW                           = null;
        WHIP.LOAD                           = null;
        WHIP.SHOOT                          = null;
        setCycle(MACE);
        setCycle(FLAIL);
        setCycle(MORNING_STAR);
        setCycle(SHOVEL);
        setCycle(STICK);
        setCycle(WHIP);

        SARISSA.THRUST                      = new Stat();
        SARISSA.THRUST_UP                   = new Stat(HOLDABLE);
        SARISSA.THRUST_DOWN                 = new Stat(HOLDABLE);
        SARISSA.THRUST_DIAG_UP              = new Stat(HOLDABLE);
        SARISSA.THRUST_DIAG_DOWN            = new Stat(HOLDABLE);
        SARISSA.THRUST_LUNGE                = null;
        SARISSA.STAB                        = null;
        SARISSA.STAB_UNTERHAU               = new Stat(HOLDABLE);
        SARISSA.SWING                       = null;
        SARISSA.SWING_UNTERHAU              = null;
        SARISSA.SWING_UNTERHAU_CROUCH       = null;
        SARISSA.SWING_UP_FORWARD            = new Stat(ALT_ATTACK);
        SARISSA.SWING_UP_BACKWARD           = new Stat(ALT_ATTACK);
        SARISSA.SWING_DOWN_FORWARD          = null;
        SARISSA.SWING_DOWN_BACKWARD         = null;
        SARISSA.SWING_LUNGE                 = null;
        SARISSA.SWING_LUNGE_UNTERHAU        = null;
        SARISSA.DRAW                        = null;
        SARISSA.LOAD                        = null;
        SARISSA.SHOOT                       = null;
        copyStat(SHORT_SWORD, HALBERD);
        HALBERD.THRUST_UP                   = new Stat(HOLDABLE);
        HALBERD.THRUST_DIAG_UP              = new Stat(HOLDABLE);
        copyStat(SCIMITAR, GLAIVE);
        GLAIVE.THRUST_UP                    = new Stat(HOLDABLE);
        GLAIVE.THRUST_DIAG_UP               = new Stat(HOLDABLE);
        GLAIVE.SWING                        = new Stat(ALT_SELF);
        GLAIVE.SWING_UNTERHAU               = new Stat(ALT_SELF);
        copyStat(GLAIVE, WAR_SCYTHE);
        copyStat(GLAIVE, SCYTHE);
        SCYTHE.THRUST_UP                    = new Stat();
        SCYTHE.THRUST_DOWN                  = new Stat();
        SCYTHE.THRUST_DIAG_UP               = new Stat();
        SCYTHE.THRUST_DIAG_DOWN             = new Stat();
        SCYTHE.STAB                         = new Stat();
        SCYTHE.STAB_UNTERHAU                = new Stat();
        copyStat(GLAIVE, SPEAR);
        SPEAR.SWING                         = new Stat(ALT_ATTACK);
        SPEAR.SWING_UNTERHAU                = new Stat(ALT_ATTACK);
        SPEAR.SWING_UNTERHAU_CROUCH         = new Stat(ALT_ATTACK);
        copyStat(SPEAR, PITCHFORK);
        copyStat(SPEAR, QUARTERSTAFF);
        QUARTERSTAFF.THRUST_UP              = new Stat();
        QUARTERSTAFF.THRUST_DIAG_UP         = new Stat();
        copyStat(SARISSA, LANCE);
        LANCE.THRUST_UP                     = new Stat();
        LANCE.THRUST_DIAG_UP                = new Stat();
        LANCE.STAB_UNTERHAU                 = new Stat();
        LANCE.SWING_UP_FORWARD              = new Stat();
        LANCE.SWING_UP_BACKWARD             = new Stat();
        LANCE.BLOCK                         = true;
        copyStat(SPEAR, JAVELIN);
        JAVELIN.PARRY                       = false;
        setCycle(SARISSA);
        setCycle(HALBERD);
        setCycle(GLAIVE);
        setCycle(WAR_SCYTHE);
        setCycle(SCYTHE);
        setCycle(SPEAR);
        setCycle(PITCHFORK);
        setCycle(QUARTERSTAFF);
        setCycle(LANCE);
        setCycle(JAVELIN);

        copyStat(WHIP, SHORTBOW);
        SHORTBOW.SWING                      = new Stat();
        SHORTBOW.SWING_UNTERHAU             = new Stat();
        SHORTBOW.SWING_UNTERHAU_CROUCH      = new Stat();
        SHORTBOW.SWING_UP_FORWARD           = new Stat();
        SHORTBOW.SWING_UP_BACKWARD          = new Stat();
        SHORTBOW.DRAW                       = new Stat();
        SHORTBOW.SHOOT                      = new Stat();
        SHORTBOW.BLOCK                      = true;
        copyStat(SHORTBOW, LONGBOW);
        copyStat(SHORTBOW, WARBOW);
        copyStat(SHORTBOW, RECURVE_BOW);
        copyStat(SHORTBOW, CROSSBOW);
        CROSSBOW.LOAD                       = new Stat();
        copyStat(CROSSBOW, SLING);
        SLING.SWING                         = new Stat(ALT_SELF);
        SLING.SWING_UNTERHAU                = new Stat(ALT_SELF);
        SLING.SWING_UP_FORWARD              = new Stat();
        SLING.SWING_UP_BACKWARD             = new Stat();
        SLING.DRAW                          = null;
        SLING.BLOCK                         = false;
        setCycle(SHORTBOW);
        setCycle(LONGBOW);
        setCycle(WARBOW);
        setCycle(RECURVE_BOW);
        setCycle(CROSSBOW);
        setCycle(SLING);

        FISTS.THRUST                        = new Stat();
        FISTS.THRUST_UP                     = new Stat();
        FISTS.THRUST_DOWN                   = null;
        FISTS.THRUST_DIAG_UP                = new Stat();
        FISTS.THRUST_DIAG_DOWN              = null;
        FISTS.THRUST_LUNGE                  = null;
        FISTS.STAB                          = null;
        FISTS.STAB_UNTERHAU                 = null;
        FISTS.SWING                         = null;
        FISTS.SWING_UNTERHAU                = null;
        FISTS.SWING_UNTERHAU_CROUCH         = new Stat(ALT_TRAJ_A);
        FISTS.SWING_UP_FORWARD              = null;
        FISTS.SWING_UP_BACKWARD             = null;
        FISTS.SWING_DOWN_FORWARD            = null;
        FISTS.SWING_DOWN_BACKWARD           = null;
        FISTS.SWING_LUNGE                   = null;
        FISTS.SWING_LUNGE_UNTERHAU          = null;
        FISTS.DRAW                          = null;
        FISTS.LOAD                          = null;
        FISTS.SHOOT                         = null;
        FISTS.BLOCK                         = true;
        FISTS.PARRY                         = true;
        FEET.THRUST                         = new Stat();
        FEET.THRUST_UP                      = null;
        FEET.THRUST_DOWN                    = new Stat(HOLDABLE);
        FEET.THRUST_DIAG_UP                 = new Stat();
        FEET.THRUST_DIAG_DOWN               = new Stat(HOLDABLE);
        FEET.THRUST_LUNGE                   = null;
        FEET.STAB                           = new Stat(HOLDABLE);
        FEET.STAB_UNTERHAU                  = null;
        FEET.SWING                          = null;
        FEET.SWING_UNTERHAU                 = new Stat(ALT_TRAJ_B);
        FEET.SWING_UNTERHAU_CROUCH          = null;
        FEET.SWING_UP_FORWARD               = null;
        FEET.SWING_UP_BACKWARD              = null;
        FEET.SWING_DOWN_FORWARD             = null;
        FEET.SWING_DOWN_BACKWARD            = null;
        FEET.SWING_LUNGE                    = null;
        FEET.SWING_LUNGE_UNTERHAU           = null;
        FEET.DRAW                           = null;
        FEET.LOAD                           = null;
        FEET.SHOOT                          = null;
        copyStat(FISTS, CLAWS);
        CLAWS.THRUST                        = null;
        CLAWS.THRUST_UP                     = null;
        CLAWS.THRUST_DOWN                   = null;
        CLAWS.THRUST_DIAG_UP                = null;
        CLAWS.SWING                         = new Stat(ALT_ATTACK);
        CLAWS.SWING_UNTERHAU                = new Stat(ALT_ATTACK);
        CLAWS.SWING_UNTERHAU_CROUCH         = new Stat(ALT_ATTACK);
        CLAWS.SWING_UP_FORWARD              = new Stat(ALT_ATTACK);
        CLAWS.SWING_UP_BACKWARD             = new Stat(ALT_ATTACK);
        copyStat(FISTS, HORNS);
        HORNS.SWING_UNTERHAU_CROUCH         = new Stat(ALT_TRAJ_A);
        HORNS.THRUST                        = new Stat(HOLDABLE);
        HORNS.THRUST_DIAG_UP                = new Stat(HOLDABLE);
        HORNS.THRUST_UP                     = new Stat(HOLDABLE);
        HORNS.BLOCK                         = false;
        setCycle(FISTS);
        setCycle(FEET);
        setCycle(CLAWS);
        setCycle(HORNS);
    }
}
