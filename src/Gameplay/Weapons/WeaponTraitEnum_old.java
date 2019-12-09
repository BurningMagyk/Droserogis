package Gameplay.Weapons;

/************************************************************************
 * ----------------------------DEPRECATED--------------------------------
 ************************************************************************/

public enum WeaponTraitEnum_old
{
    MORDHAU,

    SWING,
    THRUST,
    STAB,
    BRACE,
    GRAB,

    BLOCK,
    PARRY,

    LOAD,
    THROW,
    SHOOT,

    QUICK,
    INSTANT,
    PERMANENT,
    NEARBY,
    DISABLE_UP,
    DISABLE_DOWN;

    private static WeaponTraitEnum_old[] concatTraits(WeaponTraitEnum_old[] template, WeaponTraitEnum_old... moreTraits)
    {
        WeaponTraitEnum_old[] out = new WeaponTraitEnum_old[template.length + moreTraits.length];
        int i = 0;
        for (; i < template.length; i++) { out[i] = template[i]; }
        for (int j = 0; j < moreTraits.length; j++, i++) { out[i] = moreTraits[j]; }
        return out;
    }



    static
    {
        final WeaponTraitEnum_old[] _SWORD = {
                WeaponTraitEnum_old.MORDHAU, WeaponTraitEnum_old.THRUST, WeaponTraitEnum_old.STAB,
                WeaponTraitEnum_old.THROW, WeaponTraitEnum_old.BLOCK, WeaponTraitEnum_old.PARRY };
        final WeaponTraitEnum_old[] SHORT_SWORD = concatTraits(_SWORD, WeaponTraitEnum_old.SWING);
        final WeaponTraitEnum_old[] LONG_SWORD = concatTraits(_SWORD, WeaponTraitEnum_old.SWING);
        final WeaponTraitEnum_old[] GREATSWORD = concatTraits(_SWORD, WeaponTraitEnum_old.SWING);
        final WeaponTraitEnum_old[] SCIMITAR = concatTraits(_SWORD, WeaponTraitEnum_old.SWING);
        final WeaponTraitEnum_old[] RAPIER = concatTraits(_SWORD, WeaponTraitEnum_old.QUICK, WeaponTraitEnum_old.THRUST,
                WeaponTraitEnum_old.QUICK, WeaponTraitEnum_old.STAB);

        final WeaponTraitEnum_old[] _DAGGER = { WeaponTraitEnum_old.THROW };
        final WeaponTraitEnum_old[] DAGGER = concatTraits(_DAGGER, WeaponTraitEnum_old.SWING, WeaponTraitEnum_old.THRUST,
                WeaponTraitEnum_old.STAB);
        final WeaponTraitEnum_old[] KNIFE = concatTraits(_DAGGER, WeaponTraitEnum_old.SWING, WeaponTraitEnum_old.THRUST,
                WeaponTraitEnum_old.STAB);
        final WeaponTraitEnum_old[] THROWING_KNIFE = concatTraits(_DAGGER, WeaponTraitEnum_old.SWING, WeaponTraitEnum_old.THRUST,
                WeaponTraitEnum_old.STAB);
        final WeaponTraitEnum_old[] PARRYING_DAGGER = concatTraits(_DAGGER, WeaponTraitEnum_old.SWING, WeaponTraitEnum_old.THRUST,
                WeaponTraitEnum_old.STAB, WeaponTraitEnum_old.BLOCK, WeaponTraitEnum_old.PARRY);
        final WeaponTraitEnum_old[] SICKLE = concatTraits(_DAGGER, WeaponTraitEnum_old.SWING);
        final WeaponTraitEnum_old[] ROCK = concatTraits(_DAGGER, WeaponTraitEnum_old.SWING, WeaponTraitEnum_old.STAB);

        final WeaponTraitEnum_old[] _AXE = {
                WeaponTraitEnum_old.SWING, WeaponTraitEnum_old.THRUST, WeaponTraitEnum_old.THROW };
        final WeaponTraitEnum_old[] BATTLEAXE = concatTraits(_AXE, WeaponTraitEnum_old.THROW, WeaponTraitEnum_old.PARRY);
        final WeaponTraitEnum_old[] GREATAXE = concatTraits(_AXE, WeaponTraitEnum_old.PARRY);
        final WeaponTraitEnum_old[] TROWING = concatTraits(_AXE);
        final WeaponTraitEnum_old[] HATCHET = concatTraits(_AXE);
        final WeaponTraitEnum_old[] PICKAXE = concatTraits(_AXE, WeaponTraitEnum_old.BLOCK);

        final WeaponTraitEnum_old[] _MACE = { WeaponTraitEnum_old.SWING };
        final WeaponTraitEnum_old[] MACE = concatTraits(_MACE, WeaponTraitEnum_old.THRUST, WeaponTraitEnum_old.STAB,
                WeaponTraitEnum_old.PARRY);
        final WeaponTraitEnum_old[] FLAIL = concatTraits(_MACE, WeaponTraitEnum_old.INSTANT, WeaponTraitEnum_old.SWING);
        final WeaponTraitEnum_old[] MORNING_STAR = concatTraits(_MACE, WeaponTraitEnum_old.THRUST, WeaponTraitEnum_old.STAB,
                WeaponTraitEnum_old.PARRY);
        final WeaponTraitEnum_old[] SHOVEL = concatTraits(_MACE, WeaponTraitEnum_old.THRUST, WeaponTraitEnum_old.STAB,
                WeaponTraitEnum_old.BLOCK, WeaponTraitEnum_old.PARRY);
        final WeaponTraitEnum_old[] STICK = concatTraits(_MACE, WeaponTraitEnum_old.THRUST, WeaponTraitEnum_old.STAB,
                WeaponTraitEnum_old.BLOCK);
        final WeaponTraitEnum_old[] WHIP = concatTraits(_MACE, WeaponTraitEnum_old.GRAB);

        final WeaponTraitEnum_old[] _POLEARM = {};
        final WeaponTraitEnum_old[] SARISSA = concatTraits(_POLEARM, WeaponTraitEnum_old.THRUST, WeaponTraitEnum_old.BRACE);
        final WeaponTraitEnum_old[] HALBERD = concatTraits(_POLEARM, WeaponTraitEnum_old.SWING, WeaponTraitEnum_old.THRUST,
                WeaponTraitEnum_old.BLOCK, WeaponTraitEnum_old.PARRY);
        final WeaponTraitEnum_old[] GLAIVE = concatTraits(_POLEARM, WeaponTraitEnum_old.SWING, WeaponTraitEnum_old.THRUST,
                WeaponTraitEnum_old.BLOCK, WeaponTraitEnum_old.PARRY);
        final WeaponTraitEnum_old[] WAR_SCYTHE = GLAIVE;
        final WeaponTraitEnum_old[] SPEAR = concatTraits(_POLEARM, WeaponTraitEnum_old.SWING, WeaponTraitEnum_old.THRUST,
                WeaponTraitEnum_old.STAB, WeaponTraitEnum_old.BRACE, WeaponTraitEnum_old.THROW, WeaponTraitEnum_old.BLOCK);
        final WeaponTraitEnum_old[] PITCHFORK = SPEAR;
        final WeaponTraitEnum_old[] QUARTERSTAFF = concatTraits(_POLEARM, WeaponTraitEnum_old.QUICK, WeaponTraitEnum_old.SWING,
                WeaponTraitEnum_old.THRUST, WeaponTraitEnum_old.BLOCK, WeaponTraitEnum_old.PARRY);
        final WeaponTraitEnum_old[] STAFF = QUARTERSTAFF, ROD = QUARTERSTAFF;
        final WeaponTraitEnum_old[] LANCE = concatTraits(_POLEARM, WeaponTraitEnum_old.THRUST, WeaponTraitEnum_old.BRACE,
                WeaponTraitEnum_old.BLOCK);
        final WeaponTraitEnum_old[] JAVELIN = concatTraits(_POLEARM, WeaponTraitEnum_old.SWING, WeaponTraitEnum_old.THRUST,
                WeaponTraitEnum_old.STAB, WeaponTraitEnum_old.THROW);

        final WeaponTraitEnum_old[] _BOW = { WeaponTraitEnum_old.SHOOT };
        final WeaponTraitEnum_old[] SHORTBOW = concatTraits(_BOW, WeaponTraitEnum_old.BLOCK, WeaponTraitEnum_old.LOAD);
        final WeaponTraitEnum_old[] LONGBOW = concatTraits(_BOW, WeaponTraitEnum_old.BLOCK, WeaponTraitEnum_old.LOAD);
        final WeaponTraitEnum_old[] WARBOW = concatTraits(_BOW, WeaponTraitEnum_old.BLOCK, WeaponTraitEnum_old.LOAD);
        final WeaponTraitEnum_old[] RECURVE_BOW = concatTraits(_BOW, WeaponTraitEnum_old.BLOCK, WeaponTraitEnum_old.LOAD);
        final WeaponTraitEnum_old[] CROSSBOW = concatTraits(_BOW, WeaponTraitEnum_old.BLOCK, WeaponTraitEnum_old.PERMANENT,
                WeaponTraitEnum_old.LOAD);
        final WeaponTraitEnum_old[] SLING = concatTraits(_BOW, WeaponTraitEnum_old.PERMANENT, WeaponTraitEnum_old.LOAD);

        final WeaponTraitEnum_old[] _NATURAL = {};
        final WeaponTraitEnum_old[] FISTS = concatTraits(_NATURAL, WeaponTraitEnum_old.SWING, WeaponTraitEnum_old.THRUST,
                WeaponTraitEnum_old.BLOCK, WeaponTraitEnum_old.PARRY, WeaponTraitEnum_old.GRAB);
        final WeaponTraitEnum_old[] FEET = concatTraits(_NATURAL, WeaponTraitEnum_old.SWING, WeaponTraitEnum_old.THRUST,
                WeaponTraitEnum_old.STAB);
        final WeaponTraitEnum_old[] CLAWS = concatTraits(_NATURAL, WeaponTraitEnum_old.DISABLE_UP, WeaponTraitEnum_old.SWING,
                WeaponTraitEnum_old.DISABLE_DOWN, WeaponTraitEnum_old.BLOCK, WeaponTraitEnum_old.PARRY);
        final WeaponTraitEnum_old[] TEETH = concatTraits(_NATURAL, WeaponTraitEnum_old.GRAB);
        final WeaponTraitEnum_old[] HORNS = concatTraits(_NATURAL, WeaponTraitEnum_old.THRUST, WeaponTraitEnum_old.STAB,
                WeaponTraitEnum_old.PARRY);
        final WeaponTraitEnum_old[] TAIL = concatTraits(_NATURAL, WeaponTraitEnum_old.SWING, WeaponTraitEnum_old.PARRY,
                WeaponTraitEnum_old.GRAB);
    }



}

/*

Sword - murder stance, block/parry, throw, thrust, stab
 Short sword - swing
 Long sword - swing
 Greatsword - swing
 Scimitar - swing
 Rapier - thrust (quick), stab (quick)

Dagger - throw
 Knife - swing, thrust, stab
 Throwing - swing, thrust, stab
 Parry - swing, thrust, stab, block/parry
 Sickle - swing
 Rock - swing, stab

Axe - throw, swing, thrust
 Battleaxe - throw, parry
 Greataxe - parry
 Throwing
 Hatchet
 Pickaxe - block

Mace - swing
 Flail - swing (instant)
 Morning star - thrust, stab, parry
 Shovel - thrust, stab, block/parry
 Stick - swing, thrust, block
 Whip - grab

Polearm
 Sarissa - thrust, brace
 Halberd - swing, thrust, block/parry
 Glaive/war scythe - swing, thrust, block/parry
 Spear/pitchfork - swing, thrust, stab, brace, throw, block
 Quarterstaff/staff/rod - swing (quick), thrust, stab, block/parry
 Lance - thrust, brace, block
 Javelin - swing, thrust, stab, throw
 Scythe - swing, block/parry

Bow - shoot
 Shortbow - block, load
 Longbow - block, load
 Warbow - block, load
 Recurve bow - block, load
 Crossbow - block, load (permanent)
 Sling - load (permanent)

Natural
 Fists - swing, thrust, block/parry, grab
 Feet - swing, thrust, stab
 Claws - swing, block/parry
 Teeth - grab
 Horns - thrust, stab, parry
 Tail - swing, parry, grab

 */