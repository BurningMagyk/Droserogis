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

    private static class Stat
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
    }
}
