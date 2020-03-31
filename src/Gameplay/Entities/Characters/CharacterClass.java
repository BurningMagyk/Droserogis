/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Dustin Loughrin <zapoke@gmail.com>, 2018 - 2020
 */

package Gameplay.Entities.Characters;

public class CharacterClass
{
    private String name;
    private int[] statEXPForLevel;

    //TODO: Abilities for certain levels?

    public CharacterClass(String name, int... sEXP)
    {
        this.name = name;
        statEXPForLevel = sEXP;
    }

    public String getName() { return name; }

    public int[] adjustStatEXP(int[] cStatEXP)
    {
        int[] newStats = new int[cStatEXP.length];
        for(int i = 0; i < cStatEXP.length; i++)
        {
            newStats[i] = cStatEXP[i] + statEXPForLevel[i];
        }
        return newStats;
    }


    /*=======================================================================*/
    /*                                Roster                                 */
    /*=======================================================================*/

    static CharacterClass class_Fighter = new CharacterClass("Fighter",
            10,5,
            35,30,
            5,0,
            0,10,
            5,0,
            0);
    static CharacterClass class_Wizard = new CharacterClass("Wizard",
            10,5,
            35,30,
            5,0,
            0,10,
            5,0,
            0);
    static CharacterClass class_Rogue = new CharacterClass("Rogue",
            10,5,
            35,30,
            5,0,
            0,10,
            5,0,
            0);
    static CharacterClass class_Ranger = new CharacterClass("Ranger",
            10,5,
            35,30,
            5,0,
            0,10,
            5,0,
            0);
}
