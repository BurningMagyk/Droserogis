package Gameplay.Weapons;

public enum WeaponTraitEnum
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
    PERMANENT;

    private static WeaponTraitEnum[] concatTraits(WeaponTraitEnum[] template, WeaponTraitEnum... moreTraits)
    {
        WeaponTraitEnum[] out = new WeaponTraitEnum[template.length + moreTraits.length];
        int i = 0;
        for (; i < template.length; i++) { out[i] = template[i]; }
        for (int j = 0; j < moreTraits.length; j++, i++) { out[i] = moreTraits[j]; }
        return out;
    }

    static
    {
        final WeaponTraitEnum[] _SWORD = {
                WeaponTraitEnum.MORDHAU, WeaponTraitEnum.THRUST, WeaponTraitEnum.STAB,
                WeaponTraitEnum.THROW, WeaponTraitEnum.BLOCK, WeaponTraitEnum.PARRY };
        final WeaponTraitEnum[] SHORT_SWORD = concatTraits(_SWORD, WeaponTraitEnum.SWING);
        final WeaponTraitEnum[] LONG_SWORD = concatTraits(_SWORD, WeaponTraitEnum.SWING);
        final WeaponTraitEnum[] GREATSWORD = concatTraits(_SWORD, WeaponTraitEnum.SWING);
        final WeaponTraitEnum[] SCIMITAR = concatTraits(_SWORD, WeaponTraitEnum.SWING);
        final WeaponTraitEnum[] RAPIER = concatTraits(_SWORD, WeaponTraitEnum.QUICK, WeaponTraitEnum.THRUST,
                WeaponTraitEnum.QUICK, WeaponTraitEnum.STAB);

        final WeaponTraitEnum[] _DAGGER = { WeaponTraitEnum.THROW };
        final WeaponTraitEnum[] DAGGER = concatTraits(_DAGGER, WeaponTraitEnum.SWING, WeaponTraitEnum.THRUST,
                WeaponTraitEnum.STAB);
        final WeaponTraitEnum[] KNIFE = concatTraits(_DAGGER, WeaponTraitEnum.SWING, WeaponTraitEnum.THRUST,
                WeaponTraitEnum.STAB);
        final WeaponTraitEnum[] THROWING_KNIFE = concatTraits(_DAGGER, WeaponTraitEnum.SWING, WeaponTraitEnum.THRUST,
                WeaponTraitEnum.STAB);
        final WeaponTraitEnum[] PARRYING_DAGGER = concatTraits(_DAGGER, WeaponTraitEnum.SWING, WeaponTraitEnum.THRUST,
                WeaponTraitEnum.STAB, WeaponTraitEnum.BLOCK, WeaponTraitEnum.PARRY);
        final WeaponTraitEnum[] SICKLE = concatTraits(_DAGGER, WeaponTraitEnum.SWING);
        final WeaponTraitEnum[] ROCK = concatTraits(_DAGGER, WeaponTraitEnum.SWING, WeaponTraitEnum.STAB);

        final WeaponTraitEnum[] _AXE = {
                WeaponTraitEnum.SWING, WeaponTraitEnum.THRUST, WeaponTraitEnum.THROW };
        final WeaponTraitEnum[] BATTLEAXE = concatTraits(_AXE, WeaponTraitEnum.THROW, WeaponTraitEnum.PARRY);
        final WeaponTraitEnum[] GREATAXE = concatTraits(_AXE, WeaponTraitEnum.PARRY);
        final WeaponTraitEnum[] TROWING = concatTraits(_AXE);
        final WeaponTraitEnum[] HATCHET = concatTraits(_AXE);
        final WeaponTraitEnum[] PICKAXE = concatTraits(_AXE, WeaponTraitEnum.BLOCK);

        final WeaponTraitEnum[] _MACE = { WeaponTraitEnum.SWING };
        final WeaponTraitEnum[] MACE = concatTraits(_MACE, WeaponTraitEnum.THRUST, WeaponTraitEnum.STAB,
                WeaponTraitEnum.PARRY);
        final WeaponTraitEnum[] FLAIL = concatTraits(_MACE, WeaponTraitEnum.INSTANT, WeaponTraitEnum.SWING);
        final WeaponTraitEnum[] MORNING_STAR = concatTraits(_MACE, WeaponTraitEnum.THRUST, WeaponTraitEnum.STAB,
                WeaponTraitEnum.PARRY);
        final WeaponTraitEnum[] SHOVEL = concatTraits(_MACE, WeaponTraitEnum.THRUST, WeaponTraitEnum.STAB,
                WeaponTraitEnum.BLOCK, WeaponTraitEnum.PARRY);
        final WeaponTraitEnum[] STICK = concatTraits(_MACE, WeaponTraitEnum.THRUST, WeaponTraitEnum.STAB,
                WeaponTraitEnum.BLOCK);
        final WeaponTraitEnum[] WHIP = concatTraits(_MACE, WeaponTraitEnum.GRAB);

        final WeaponTraitEnum[] _POLEARM = {};
        final WeaponTraitEnum[] SARISSA = concatTraits(_POLEARM, WeaponTraitEnum.THRUST, WeaponTraitEnum.BRACE);
        final WeaponTraitEnum[] HALBERD = concatTraits(_POLEARM, WeaponTraitEnum.SWING, WeaponTraitEnum.THRUST,
                WeaponTraitEnum.BLOCK, WeaponTraitEnum.PARRY);
        final WeaponTraitEnum[] GLAIVE = concatTraits(_POLEARM, WeaponTraitEnum.SWING, WeaponTraitEnum.THRUST,
                WeaponTraitEnum.BLOCK, WeaponTraitEnum.PARRY);
        final WeaponTraitEnum[] WAR_SCYTHE = GLAIVE;
        final WeaponTraitEnum[] SPEAR = concatTraits(_POLEARM, WeaponTraitEnum.SWING, WeaponTraitEnum.THRUST,
                WeaponTraitEnum.STAB, WeaponTraitEnum.BRACE, WeaponTraitEnum.THROW, WeaponTraitEnum.BLOCK);
        final WeaponTraitEnum[] PITCHFORK = SPEAR;
        final WeaponTraitEnum[] QUARTERSTAFF = concatTraits(_POLEARM, WeaponTraitEnum.QUICK, WeaponTraitEnum.SWING,
                WeaponTraitEnum.THRUST, WeaponTraitEnum.BLOCK, WeaponTraitEnum.PARRY);
        final WeaponTraitEnum[] STAFF = QUARTERSTAFF, ROD = QUARTERSTAFF;
        final WeaponTraitEnum[] LANCE = concatTraits(_POLEARM, WeaponTraitEnum.THRUST, WeaponTraitEnum.BRACE,
                WeaponTraitEnum.BLOCK);
        final WeaponTraitEnum[] JAVELIN = concatTraits(_POLEARM, WeaponTraitEnum.SWING, WeaponTraitEnum.THRUST,
                WeaponTraitEnum.STAB, WeaponTraitEnum.THROW);

        final WeaponTraitEnum[] _BOW = { WeaponTraitEnum.SHOOT };
        final WeaponTraitEnum[] SHORTBOW = concatTraits(_BOW, WeaponTraitEnum.BLOCK, WeaponTraitEnum.LOAD);
        final WeaponTraitEnum[] LONGBOW = concatTraits(_BOW, WeaponTraitEnum.BLOCK, WeaponTraitEnum.LOAD);
        final WeaponTraitEnum[] WARBOW = concatTraits(_BOW, WeaponTraitEnum.BLOCK, WeaponTraitEnum.LOAD);
        final WeaponTraitEnum[] RECURVE_BOW = concatTraits(_BOW, WeaponTraitEnum.BLOCK, WeaponTraitEnum.LOAD);
        final WeaponTraitEnum[] CROSSBOW = concatTraits(_BOW, WeaponTraitEnum.BLOCK, WeaponTraitEnum.PERMANENT,
                WeaponTraitEnum.LOAD);
        final WeaponTraitEnum[] SLING = concatTraits(_BOW, WeaponTraitEnum.PERMANENT, WeaponTraitEnum.LOAD);

        final WeaponTraitEnum[] _NATURAL = {};
        final WeaponTraitEnum[] FISTS = concatTraits(_NATURAL, WeaponTraitEnum.SWING, WeaponTraitEnum.THRUST,
                WeaponTraitEnum.BLOCK, WeaponTraitEnum.PARRY, WeaponTraitEnum.GRAB);
        final WeaponTraitEnum[] FEET = concatTraits(_NATURAL, WeaponTraitEnum.SWING, WeaponTraitEnum.THRUST,
                WeaponTraitEnum.STAB);
        final WeaponTraitEnum[] CLAWS = concatTraits(_NATURAL, WeaponTraitEnum.SWING, WeaponTraitEnum.BLOCK,
                WeaponTraitEnum.PARRY);
        final WeaponTraitEnum[] TEETH = concatTraits(_NATURAL, WeaponTraitEnum.GRAB);
        final WeaponTraitEnum[] HORNS = concatTraits(_NATURAL, WeaponTraitEnum.THRUST, WeaponTraitEnum.STAB,
                WeaponTraitEnum.PARRY);
        final WeaponTraitEnum[] TAIL = concatTraits(_NATURAL, WeaponTraitEnum.SWING, WeaponTraitEnum.PARRY,
                WeaponTraitEnum.GRAB);
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