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

    static class Stat
    {
        final boolean QUICK, PERSIST;
        Stat(boolean quick, boolean persist)
        {
            // chains with its brother attack (rapier thrust, battleaxe swing)
            QUICK = quick;

            // chains with itself (flail and quarterstaff swing) or is
            // holdable (spear thrust)
            PERSIST = persist;
        }
        Stat copy() { return new Stat(QUICK, PERSIST); }
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
        SHORT_SWORD.THRUST                  = new Stat(false, false);
        SHORT_SWORD.THRUST_UP               = new Stat(false, false);
        SHORT_SWORD.THRUST_DOWN             = new Stat(false, true);
        SHORT_SWORD.THRUST_DIAG_UP          = new Stat(false, false);
        SHORT_SWORD.THRUST_DIAG_DOWN        = new Stat(false, true);
        SHORT_SWORD.THRUST_LUNGE            = new Stat(false, false);
        SHORT_SWORD.STAB                    = new Stat(false, true);
        SHORT_SWORD.STAB_UNTERHAU           = new Stat(false, true);
        SHORT_SWORD.SWING                   = new Stat(true, false);
        SHORT_SWORD.SWING_UNTERHAU          = new Stat(true, false);
        SHORT_SWORD.SWING_UNTERHAU_CROUCH   = new Stat(true, false);
        SHORT_SWORD.SWING_UP_FORWARD        = new Stat(true, false);
        SHORT_SWORD.SWING_UP_BACKWARD       = new Stat(true, false);
        SHORT_SWORD.SWING_DOWN_FORWARD      = new Stat(true, false);
        SHORT_SWORD.SWING_DOWN_BACKWARD     = new Stat(true, false);
        SHORT_SWORD.SWING_LUNGE             = new Stat(false, false);
        SHORT_SWORD.SWING_LUNGE_UNTERHAU    = new Stat(false, false);
        SHORT_SWORD.DRAW                    = null;
        SHORT_SWORD.LOAD                    = null;
        SHORT_SWORD.SHOOT                   = null;
        copyStat(SHORT_SWORD, LONG_SWORD);
        copyStat(SHORT_SWORD, GREATSWORD);
        copyStat(SHORT_SWORD, SCIMITAR);
    }
}
