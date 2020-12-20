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
    private GradeEnum mass;
    private ArrayList<String[]> spritePaths;

    private int[] statEXP;
    private CharacterClass[] classes;

    public Character(String name, CharacterStat stat, CharacterClass starterClass,
                     Vec2 dims, GradeEnum mass, ArrayList<String[]> spritePaths)
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
    public GradeEnum getMass() { return mass; }
    public ArrayList<String[]> getSpritePaths() { return spritePaths; }


    /*****************************************************************************/
    /*********************************** Roster **********************************/
    /*****************************************************************************/

    private static Character character_Chad = new Character("Chad",
            new CharacterStat("A", "A", "A", "A", "A", "A",
                    "A", "A", "A", "A", "A"),
            CharacterClass.class_Fighter,
            new Vec2(20, 49).mul(SPRITE_TO_WORLD_SCALE), GradeEnum.D, null);

    private static Character character_Nathan = new Character("Nathan",
            new CharacterStat("F", "F", "F", "F", "F", "F",
                    "F", "F", "F", "F", "F"),
            CharacterClass.class_Fighter,
            new Vec2(20, 40).mul(SPRITE_TO_WORLD_SCALE), GradeEnum.D, null);

    private static Character character_Jacob = new Character("Jacob",
            new CharacterStat("D", "D", "D", "D", "D", "D",
                    "D", "D", "D", "D", "D"),
            CharacterClass.class_Fighter,
            new Vec2(10, 30).mul(SPRITE_TO_WORLD_SCALE), GradeEnum.D, getSpritePaths("Jacob"));

    private static Character character_Tetsuya = new Character("Tetsuya",
            new CharacterStat("F", "F", "F", "F", "F", "F",
                    "F", "F", "F", "F", "F"),
            CharacterClass.class_Fighter,
            new Vec2(20, 40).mul(SPRITE_TO_WORLD_SCALE), GradeEnum.D, null);

    private static Character character_Kevin = new Character("Kevin",
            new CharacterStat("F", "F", "F", "F", "F", "F",
                    "F", "F", "F", "F", "F"),
            CharacterClass.class_Fighter,
            new Vec2(20, 40).mul(SPRITE_TO_WORLD_SCALE), GradeEnum.D, null);

    private static Character character_Orget = new Character("Orget",
            new CharacterStat("F", "F", "F", "F", "F", "F",
                    "F", "F", "F", "F", "F"),
            CharacterClass.class_Fighter,
            new Vec2(20, 40).mul(SPRITE_TO_WORLD_SCALE), GradeEnum.D, null);

    private static Character character_Vritak = new Character("Vritak",
            new CharacterStat("F", "F", "F", "F", "F", "F",
                    "F", "F", "F", "F", "F"),
            CharacterClass.class_Fighter,
            new Vec2(20, 40).mul(SPRITE_TO_WORLD_SCALE), GradeEnum.D, null);

    private static Character character_Let = new Character("Let",
            new CharacterStat("F", "F", "F", "F", "F", "F",
                    "F", "F", "F", "F", "F"),
            CharacterClass.class_Fighter,
            new Vec2(20, 40).mul(SPRITE_TO_WORLD_SCALE), GradeEnum.D, null);

    private static Character character_Lugu = new Character("Lugu",
            new CharacterStat("F", "F", "F", "F", "F", "F",
                    "F", "F", "F", "F", "F"),
            CharacterClass.class_Fighter,
            new Vec2(20, 40).mul(SPRITE_TO_WORLD_SCALE), GradeEnum.D, null);

    public static Character character_ = new Character("",
            new CharacterStat("F", "F", "F", "F", "F", "F",
                    "F", "F", "F", "F", "F"),
            CharacterClass.class_Fighter,
            new Vec2(20, 40).mul(SPRITE_TO_WORLD_SCALE), GradeEnum.D, null);

    public static Character get(String name)
    {
        if (name.equals("Chad")) return character_Chad;
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
    /******************************** Sprite Paths *******************************/
    /*****************************************************************************/

    public enum SpriteType
    {
        IDLE { public int count() { return 1; } },
        IDLE_BLOCKING { public int count() { return 1; } },
        PRONE { public int count() { return 1; }
            public boolean horizFlush() { return true; } },
        PRONE_BLOCKING { public int count() { return 1; }
            public boolean horizFlush() { return true; } },
        PRONE_ABRUPT { public int count() { return 1; }
            public boolean horizFlush() { return true; } },
        CROUCH { public int count() { return 1; } },
        CRAWL { public int count() { return 4; } },
        WALK { public int count() { return 10; } },
        WALK_BLOCKING { public int count() { return 10; } },
        RUN { public int count() { return 8; } },
        CLIMB_WALL { public int count() { return 4; }
            public boolean horizFlush() { return true; } },
        CLIMB_LEDGE { public int count() { return 2; }
            public boolean vertExt(boolean up) { return !up; } },
        JUMP { public int count() { return 2; } },
        PUNCH { public int count() { return 6; } },
        PUNCH_DIAG { public int count() { return 6; }
            public boolean horizExt(boolean left) { return !left; }
            public boolean vertExt(boolean up) { return up; } },
        PUNCH_UP { public int count() { return 6; }
            public boolean vertExt(boolean up) { return up; } },
        UPPERCUT { public int count() { return 6; }
            public boolean vertExt(boolean up) { return up; } },
        STOMP { public int count() { return 4; } },
        STOMP_FALL { public int count() { return 4; } },
        KICK_ARC { public int count() { return 6; }
            public boolean horizExt(boolean left) { return true; } },
        KICK_AERIAL { public int count() { return 4; } },
        KICK_PRONE { public int count() { return 4; }
            public boolean horizFlush() { return true; } },
        SHOVE { public int count() { return 8; } };
        public int count() { return 0; }

        public boolean vertExt(boolean up) { return false; }
        public boolean horizExt(boolean left) { return false; }
        // flush against the right
        public boolean horizFlush() { return false; }
    }

    public final static int SPRITE_RES = 32;

    private static String[] getSpriteArray(String name, SpriteType spriteType)
    {
        String[] spriteArray = new String[spriteType.count()];
        String typeName = spriteType.name().toLowerCase();
        for (int i = 0; i < spriteType.count(); i++)
        {
            int typeIndex_ = spriteType.ordinal() + 1;
            String typeIndex = typeIndex_ < 10
                    ? "0" + typeIndex_ : "" + typeIndex_;

            if (spriteType == SpriteType.JUMP)
            {
                spriteArray[i] = "Characters/" + name + "/"
                        + typeIndex + "_" + typeName + "/"
                        + ((i == 0) ? "rise" : "fall") + ".png";
            }
            else
            {
                int i_ = i + 1;
                String imageIndex = i_ < 10 ? "0" + i_ : "" + i_;
                spriteArray[i] = "Characters/" + name + "/"
                        + typeIndex + "_" + typeName + "/"
                        + typeName + "_" + imageIndex + ".png";
            }
        }
        return spriteArray;
    }

    private static ArrayList<String[]> getSpritePaths(String name)
    {
        ArrayList<String[]> spritePaths = new ArrayList<>();
        SpriteType[] spriteTypes = SpriteType.values();

        for (int i = 0; i < spriteTypes.length; i++)
        {
            spritePaths.add(getSpriteArray(name, spriteTypes[i]));
        }
        return spritePaths;
    }

    private static ArrayList<String[]> getJacobSpritePaths()
    {
        ArrayList<String[]> spritePaths = new ArrayList<>();

        for (int i = 0; i < SpriteType.values().length; i++)
        {

        }

//        String[] spriteArray;
//
//        int idleFrames = 60;
//        spriteArray = new String[idleFrames];
//        for(int i = 0 ; i < idleFrames; i++){
//            String imageIndex = i + 1 < 10 ? "0" + (i + 1) : "" + (i + 1);
//            spriteArray[i] = "Characters/Jacob/idle/Idle00" + imageIndex + ".png";
//        }
//        spritePaths.add(spriteArray);
//
//        int walkFrames = 35;
//        spriteArray = new String[walkFrames];
//        for(int i = 0; i < walkFrames; i++){
//            String imageIndex = i + 1 < 10 ? "0" + (i + 1) : "" + (i + 1);
//            spriteArray[i] = "Characters/Jacob/walk/Walk00" + imageIndex + ".png";
//        }
//        spritePaths.add(spriteArray);
//
//        int runFrames = 22;
//        spriteArray = new String[runFrames];
//        for(int i = 0 ; i < runFrames; i++){
//            String imageIndex = i + 1 < 10 ? "0" + (i + 1) : "" + (i + 1);
//            spriteArray[i] = "Characters/Jacob/run/Run00" + imageIndex + ".png";
//        }
//        spritePaths.add(spriteArray);
//
//        int jumpFrames = 15;
//        spriteArray = new String[jumpFrames];
//        for(int i = 0 ; i < jumpFrames; i++){
//            String imageIndex = i + 1 < 10 ? "0" + (i + 1) : "" + (i + 1);
//            spriteArray[i] = "Characters/Jacob/jump/Jump00" + imageIndex + ".png";
//        }
//        spritePaths.add(spriteArray);
//
//
//        spriteArray = new String[idleFrames];
//        for(int i = 0 ; i < idleFrames; i++){
//            String imageIndex = i + 1 < 10 ? "0" + (i + 1) : "" + (i + 1);
//            spriteArray[i] = "Characters/Jacob/idle/IdleNormal00" + imageIndex + ".png";
//        }
//        spritePaths.add(spriteArray);
//
//        spriteArray = new String[walkFrames];
//        for(int i = 0; i < walkFrames; i++){
//            String imageIndex = i + 1 < 10 ? "0" + (i + 1) : "" + (i + 1);
//            spriteArray[i] = "Characters/Jacob/walk/WalkNormal00" + imageIndex + ".png";
//        }
//        spritePaths.add(spriteArray);
//
//        spriteArray = new String[runFrames];
//        for(int i = 0 ; i < runFrames; i++){
//            String imageIndex = i + 1 < 10 ? "0" + (i + 1) : "" + (i + 1);
//            spriteArray[i] = "Characters/Jacob/run/RunNormal00" + imageIndex + ".png";
//        }
//        spritePaths.add(spriteArray);
//
//        spriteArray = new String[jumpFrames];
//        for(int i = 0 ; i < jumpFrames; i++){
//            String imageIndex = i + 1 < 10 ? "0" + (i + 1) : "" + (i + 1);
//            spriteArray[i] = "Characters/Jacob/jump/JumpNormal00" + imageIndex + ".png";
//        }
//        spritePaths.add(spriteArray);

        return spritePaths;
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
                new Vec2(20, 40).mul(SPRITE_TO_WORLD_SCALE), GradeEnum.D, null);

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
