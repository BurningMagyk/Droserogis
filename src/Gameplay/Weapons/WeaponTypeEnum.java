package Gameplay.Weapons;

public enum WeaponTypeEnum
{
    _SWORD,     SHORT_SWORD, LONG_SWORD, GREATSWORD, SCIMITAR, RAPIER,
    _DAGGER,    DAGGER, KNIFE, THROWING_KNIFE, PARRYING_DAGGER, SICKLE, ROCK,
    _AXE,       BATTLEAXE, GREATAXE, THROWING_AXE, HATCHET, PICKAXE,
    _MACE,      MACE, FLAIL, MORNING_STAR, SHOVEL, STICK, WHIP,
    _POLEARM,   SARISSA, HALBERD, GLAIVE, WAR_SCYTHE, SPEAR, PITCHFORK,
                QUARTERSTAFF, STAFF, LANCE, JAVELIN, SCYTHE,
    _BOW,       SHORTBOW, LONGBOW, WARBOW, RECURVE_BOW, CROSSBOW, SLING,
    _NATURAL,   FISTS, FEET, CLAWS, TEETH, HORNS, TAIL;

    private final static int ALT_STANCE = 0, ALT_ATTACK = 1, ALT_SELF = 2, HOLDABLE = 3;

    static class Stat
    {
        final boolean STANCE, ATTACK, SELF, HOLD;
        Stat(int ...args)
        {
            boolean[] var = { false, false, false, false };
            for (int arg : args) { var[arg] = true; }
            STANCE = var[ALT_STANCE]; // rapier thrust
            ATTACK = var[ALT_ATTACK]; // sword/battleaxe swing
            SELF = var[ALT_SELF]; // flail/quarterstaff swing
            HOLD = var[HOLDABLE]; // polearm thrust
        }
        Stat(boolean arg1, boolean arg2, boolean arg3, boolean arg4)
        {
            STANCE = arg1;
            ATTACK = arg2;
            SELF = arg3;
            HOLD = arg4;
        }
        Stat copy() { return new Stat(STANCE, ATTACK, SELF, HOLD); }
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
        typeB.DRAW                    = typeA.DRAW == null ? null : typeA.DRAW.copy();
        typeB.LOAD                    = typeA.LOAD == null ? null : typeA.LOAD.copy();
        typeB.SHOOT                   = typeA.SHOOT == null ? null : typeA.SHOOT.copy();
    }

    Stat
        THRUST, THRUST_UP, THRUST_DOWN, THRUST_DIAG_UP, THRUST_DIAG_DOWN,
            THRUST_LUNGE,
        STAB, STAB_UNTERHAU,
        SWING, SWING_UNTERHAU, SWING_UNTERHAU_CROUCH, SWING_UP_FORWARD,
            SWING_UP_BACKWARD, SWING_DOWN_FORWARD, SWING_DOWN_BACKWARD,
            SWING_LUNGE, SWING_LUNGE_UNTERHAU,
        DRAW, LOAD, SHOOT;

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
        copyStat(SHORT_SWORD, LONG_SWORD);
        copyStat(SHORT_SWORD, GREATSWORD);
        copyStat(SHORT_SWORD, SCIMITAR);
        copyStat(SHORT_SWORD, RAPIER);
        RAPIER.THRUST                       = new Stat(ALT_STANCE);
        RAPIER.THRUST_UP                    = new Stat(ALT_STANCE);
        RAPIER.THRUST_DOWN                  = new Stat(ALT_STANCE, HOLDABLE);
        RAPIER.THRUST_DIAG_UP               = new Stat(ALT_STANCE);
        RAPIER.THRUST_DIAG_DOWN             = new Stat(ALT_STANCE, HOLDABLE);
        RAPIER.STAB                         = new Stat(ALT_STANCE, HOLDABLE);
        RAPIER.STAB_UNTERHAU                = new Stat(ALT_STANCE, HOLDABLE);

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
        copyStat(DAGGER, LONG_SWORD);
        copyStat(DAGGER, KNIFE);
        copyStat(DAGGER, THROWING_KNIFE);
        copyStat(DAGGER, PARRYING_DAGGER);
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

        BATTLEAXE.THRUST                    = new Stat();
        BATTLEAXE.THRUST_UP                 = new Stat();
        BATTLEAXE.THRUST_DOWN               = null;
        BATTLEAXE.THRUST_DIAG_UP            = new Stat();
        BATTLEAXE.THRUST_DIAG_DOWN          = new Stat();
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
        copyStat(BATTLEAXE, GREATAXE);
        GREATAXE.SWING                      = new Stat();
        GREATAXE.SWING_UNTERHAU             = new Stat();
        GREATAXE.SWING_UNTERHAU_CROUCH      = new Stat();
        GREATAXE.SWING_UP_FORWARD           = new Stat();
        GREATAXE.SWING_UP_BACKWARD          = new Stat();
        copyStat(GREATAXE, THROWING_AXE);
        copyStat(GREATAXE, HATCHET);
        copyStat(BATTLEAXE, PICKAXE);

        MACE.SWING                          = new Stat();
    }
}
