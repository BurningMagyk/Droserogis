/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Dustin Loughrin <zapoke@gmail.com>, 2018 - 2020
 */

package Gameplay.Entities.Characters;

import Gameplay.Entities.Actor;
import Util.GradeEnum;
import Util.Print;
import Util.Vec2;

import java.util.ArrayList;
import java.util.Arrays;

import static Gameplay.Entities.Entity.SPRITE_TO_WORLD_SCALE;

public class Character
{
    private CharacterStat stat;
    private String name;
    private int level = 0;
    private GradeEnum characterGrade;
    private Vec2 dims;
    private float mass;
    private ArrayList<String[]> spritePaths;

    private int[] statEXP;
    private CharacterClass[] classes;

    public Character(String name, CharacterStat stat, CharacterClass starterClass,
                     Vec2 dims, float mass, ArrayList<String[]> spritePaths)
    {
        this.name = name;
        this.stat = stat;

        statEXP = new int[11];
        classes = new CharacterClass[40];

        levelUp(starterClass);

        this.dims = dims;
        this.mass = mass;
        this.spritePaths = spritePaths;
    }

    /*****************************************************************************/
    /****************************** Variable Access ******************************/
    /*****************************************************************************/

    public String getName() { return name;}
    public CharacterStat getStat() { return stat; }

    public void levelUp(CharacterClass newClass)
    {
        if (level >= classes.length) return;
        classes[level] = newClass;
        statEXP = newClass.adjustStatEXP(statEXP);
        level++;

        if ((level % 4) == 0)
        {
            int[] indexArray = stat.increaseGrades(sortStats(),4); // Set the number of increases here
            CharacterStat.Ability[] abilityArr = CharacterStat.Ability.values();
            for(int i = 0; i < indexArray.length; i++)
            {
                statEXP[indexArray[i]] = 0;
                Print.blue("Increased " + CharacterStat.getAbilityString(abilityArr[indexArray[i]]));
            }
            updateCharacterGrade();
            //updateAllActor();
        }
    }

    /*****************************************************************************/
    /******************************* Utility Stuff *******************************/
    /*****************************************************************************/

    private void updateCharacterGrade()
    {
        float sum = 0;
        for (GradeEnum grade : stat.getGrades())
        {
            sum += grade.ordinal();
        }
        characterGrade = GradeEnum.getGrade(sum / stat.getGrades().length);
    }

    // Returns an array of indices that sorts the stat increases for use
    private int[] sortStats()
    {
        int[] top = new int[statEXP.length];
        int[] temp = new int[statEXP.length];
        int[] holder = new int[temp.length];

        for(int i = 0; i < statEXP.length; i++) temp[i] = statEXP[i];
        Arrays.sort(temp);
        for(int i = 0; i < temp.length; i++) { holder[i] = temp[temp.length-i-1]; }

        int g = 0;
        for(int i = 0; i < holder.length; i++)
        {
            for(int l = 0; l < statEXP.length; l++)
            {
                if(holder[i] == statEXP[l])
                {
                    boolean alreadyUsed = false;
                    for(int another = 0; another < g; another++) if(l == top[another]) alreadyUsed = true;
                    if(alreadyUsed) continue;

                    top[g] = l;
                    g++;
                    break;
                }
            }
        }

        return top;
    }

    public float getWidth() { return dims.x; }
    public float getHeight() { return dims.y; }
    public float getMass() { return mass; }
    public ArrayList<String[]> getSpritePaths() { return spritePaths; }


    /*****************************************************************************/
    /*********************************** Roster **********************************/
    /*****************************************************************************/

    private static Character character_Nathan = new Character("Nathan",
            new CharacterStat("F", "F", "F", "F", "F", "F",
                    "F", "F", "F", "F", "F"),
            CharacterClass.class_Fighter,
            new Vec2(20, 40).mul(SPRITE_TO_WORLD_SCALE), 1, null);

    private static Character character_Jacob = new Character("Jacob",
            new CharacterStat("F", "F", "F", "F", "F", "F",
                    "F", "F", "F", "F", "F"),
            CharacterClass.class_Fighter,
            new Vec2(20, 40).mul(SPRITE_TO_WORLD_SCALE), 1, null);

    private static Character character_Tetsuya = new Character("Tetsuya",
            new CharacterStat("F", "F", "F", "F", "F", "F",
                    "F", "F", "F", "F", "F"),
            CharacterClass.class_Fighter,
            new Vec2(20, 40).mul(SPRITE_TO_WORLD_SCALE), 1, null);

    private static Character character_Kevin = new Character("Kevin",
            new CharacterStat("F", "F", "F", "F", "F", "F",
                    "F", "F", "F", "F", "F"),
            CharacterClass.class_Fighter,
            new Vec2(20, 40).mul(SPRITE_TO_WORLD_SCALE), 1, null);

    private static Character character_Orget = new Character("Orget",
            new CharacterStat("F", "F", "F", "F", "F", "F",
                    "F", "F", "F", "F", "F"),
            CharacterClass.class_Fighter,
            new Vec2(20, 40).mul(SPRITE_TO_WORLD_SCALE), 1, null);

    private static Character character_Vritak = new Character("Vritak",
            new CharacterStat("F", "F", "F", "F", "F", "F",
                    "F", "F", "F", "F", "F"),
            CharacterClass.class_Fighter,
            new Vec2(20, 40).mul(SPRITE_TO_WORLD_SCALE), 1, null);

    private static Character character_Let = new Character("Let",
            new CharacterStat("F", "F", "F", "F", "F", "F",
                    "F", "F", "F", "F", "F"),
            CharacterClass.class_Fighter,
            new Vec2(20, 40).mul(SPRITE_TO_WORLD_SCALE), 1, null);

    private static Character character_Lugu = new Character("Lugu",
            new CharacterStat("F", "F", "F", "F", "F", "F",
                    "F", "F", "F", "F", "F"),
            CharacterClass.class_Fighter,
            new Vec2(20, 40).mul(SPRITE_TO_WORLD_SCALE), 1, null);

    public static Character character_ = new Character("",
            new CharacterStat("F", "F", "F", "F", "F", "F",
                    "F", "F", "F", "F", "F"),
            CharacterClass.class_Fighter,
            new Vec2(20, 40).mul(SPRITE_TO_WORLD_SCALE), 1, null);

    public static Character get(String name)
    {
        if (name.equals("Nathan")) return character_Nathan;
        if (name.equals("Jacob")) return character_Jacob;
        if (name.equals("Tetsuya")) return character_Tetsuya;
        if (name.equals("Kevin")) return character_Kevin;
        if (name.equals("Orget")) return character_Orget;
        if (name.equals("Vritak")) return character_Vritak;
        if (name.equals("Let")) return character_Let;
        if (name.equals("Lugu")) return character_Lugu;
        return character_;
    }


    /*****************************************************************************/
    /******************************** Test example *******************************/
    /*****************************************************************************/

    public static void main(String[] args)
    {
        String[] grades = {
                "D-", "D",
                "B-", "C",
                "D+", "E",
                "C-", "C+",
                "C+", "D",
                "A-"
        };
        Actor lyraA = new Actor(0, 0, character_Jacob);
        CharacterStat lyraStats = new CharacterStat(grades);
        //For this test, 100 increase points for the class total (basically for %)
        CharacterClass assassin = new CharacterClass("Assassin",
                10,5,
                35,30,
                5,0,
                0,10,
                5,0,
                0);
        CharacterClass eMage = new CharacterClass("Erudian Mage",
                0,0,
                5,0,
                0,0,
                10,5,
                15,20,
                45);
        Character lyra = new Character("Lyra", lyraStats, assassin,
                new Vec2(20, 40).mul(SPRITE_TO_WORLD_SCALE), 1, null);

        boolean print = false;

        for(int level = 1; level <= 40; level++)
        {
            if(level != 1)
            {
                if(lyra.level < 12) lyra.levelUp(assassin);
                else lyra.levelUp(eMage);
            }

            if((level%4) == 0) print = true;

            if(print) System.out.println(lyra.name + ", level " + lyra.level);
            int[] exps = lyra.statEXP;

            for (int i = 0; i < lyra.classes.length; i++)
            {
                if(lyra.classes[i] == null) break;
                if(print) System.out.println("  " + (1+i) + ": " + lyra.classes[i].getName());
            }

            if(print) System.out.println("Stats:");
            GradeEnum[] lgrades = lyra.stat.getGrades();
            for (int i = 0; i < lgrades.length; i++)
            {
                if(lgrades[i] == null) break;
                if(print) System.out.print(" " + GradeEnum.getGradeString(lgrades[i]) + "(" + exps[i] + ")");
                if(((i+1)%2) == 0 && print) System.out.println();
                else if((i+1) < lgrades.length && print) System.out.print(", ");
            }
            if(print) System.out.println();

            if(print) print = false;
        }
    }
}
