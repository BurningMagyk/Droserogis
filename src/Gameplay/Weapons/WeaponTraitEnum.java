package Gameplay.Weapons;

public enum WeaponTraitEnum
{
    MORDHAU,

    SWING,
    THRUST,
    STAB,
    GRAB,
    BRACE,

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
        final WeaponTraitEnum[] SWORD = {
                WeaponTraitEnum.MORDHAU, WeaponTraitEnum.THRUST, WeaponTraitEnum.STAB,
                WeaponTraitEnum.THROW, WeaponTraitEnum.BLOCK, WeaponTraitEnum.PARRY };
        final WeaponTraitEnum[] SHORT_SWORD = concatTraits(SWORD, WeaponTraitEnum.SWING);
        final WeaponTraitEnum[] LONG_SWORD = concatTraits(SWORD, WeaponTraitEnum.SWING);
        final WeaponTraitEnum[] GREATSWORD = concatTraits(SWORD, WeaponTraitEnum.SWING);
        final WeaponTraitEnum[] SCIMITAR = concatTraits(SWORD, WeaponTraitEnum.SWING);
        final WeaponTraitEnum[] RAPIER = concatTraits(SWORD, WeaponTraitEnum.QUICK, WeaponTraitEnum.THRUST,
                WeaponTraitEnum.QUICK, WeaponTraitEnum.STAB);

        final WeaponTraitEnum[] DAGGER = { WeaponTraitEnum.THROW };

        final WeaponTraitEnum[] AXE = {
                WeaponTraitEnum.SWING, WeaponTraitEnum.THRUST, WeaponTraitEnum.THROW };

        final WeaponTraitEnum[] MACE = { WeaponTraitEnum.SWING };

        final WeaponTraitEnum[] BOW = { WeaponTraitEnum.SHOOT };


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
 Quarterstaff - swing (quick), thrust, stab, block/parry
 Lance - thrust, brace, block
 Javelin - swing, thrust, stab, throw
 Staff/rod - swing, thrust, stab, block
 Scythe - swing, block/parry

Bow - shoot
 Shortbow - block, load
 Longbow - block, load
 Warbow - block, load
 Crossbow - block, load (permanent)
 Recurve bow - block, load
 Sling - load (permanent)
 Pebble

Natural
 Fists - swing, thrust, block/parry, grab
 Feet - swing, thrust, stab
 Claws - swing, block/parry
 Teeth - grab
 Horns - thrust, stab, parry
 Tail - swing, parry, grab

 */