package Gameplay.Characters;

import Gameplay.entity.Actor;
import Util.GradeEnum;

import java.util.Arrays;

public class Character
{
    private static int uniqueID = 0;
    private int myUID;
    private Actor actor;
    private CharacterStat stats;
    private String name;
    private int level;
    private GradeEnum characterGrade;

    private int statEXP[];
    private String baseStats[]; //In order listed in the CharacterStat class
    private CharacterClass classes[];

    public Character(String n, Actor a, CharacterStat cs, String bStats[], CharacterClass starterClass)
    {
        actor = a;
        stats = cs;
        baseStats = bStats;
        level = 0;

        statEXP = new int[11];
        classes = new CharacterClass[40];

        name = n;
        myUID = uniqueID;
        uniqueID++;

        stats.setGrades(bStats);
        levelUp(starterClass);
        //updateAllActor();
    }

    /*****************************************************************************/
    /****************************** Variable Access ******************************/
    /*****************************************************************************/

    public int getUID() { return myUID; }
    public String getName() { return name;}
    public Actor getActor() { return actor;}
    public GradeEnum[] getStats() { return stats.getGrades(); }

    public void levelUp(CharacterClass newClass)
    {
        if(level >= classes.length) return;
        classes[level] = newClass;
        statEXP = newClass.adjustStatEXP(statEXP);
        level++;

        if((level%4) == 0)
        {
            int indexArray[] = stats.increaseGrades(sortStats(),4); //Set the number of increases here
            CharacterStat.Ability abilityArr[] = CharacterStat.Ability.values();
            for(int i = 0; i < indexArray.length; i++)
            {
                statEXP[indexArray[i]] = 0;
                System.out.println("Increased " + CharacterStat.getAbilityString(abilityArr[indexArray[i]]));
            }
            updateCharacterGrade();
            //updateAllActor();
        }
    }

    /*Need to be public variables or make setters
    public void updateAllActor()
    {
        //actor.mass = stats.mass();
        //actor.width = stats.width();
        //actor.height =  stats.height();

        //float airSpeed() { return agility(0.4F, dexterity(0.6,0)); } - For two? (40%/60% split)

        actor.airSpeed = stats.airSpeed();
        actor.swimSpeed = stats.swimSpeed();
        actor.crawlSpeed = stats.crawlSpeed();
        actor.walkSpeed = stats.walkSpeed();
        actor.runSpeed = stats.runSpeed();
        actor.lowerSprintSpeed = stats.lowerSprintSpeed();
        actor.sprintSpeed = stats.sprintSpeed();
        actor.rushSpeed = stats.rushSpeed();

        actor.maxClimbSpeed = stats.maxClimbSpeed();
        actor.maxStickSpeed = stats.maxStickSpeed();
        actor.maxSlideSpeed = stats.maxSlideSpeed();
        actor.maxLowerGroundSpeed = stats.maxLowerGroundSpeed();
        actor.maxGroundSpeed = stats.maxGroundSpeed();
        actor.maxTotalSpeed = stats.maxTotalSpeed();

        actor.airAccel = stats.airAccel();
        actor.swimAccel = stats.swimAccel();
        actor.crawlAccel = stats.crawlAccel();
        actor.climbAccel = stats.climbAccel();
        actor.runAccel = stats.runAccel();

        actor.jumpVel = stats.jumpVel();

        actor.climbLedgeTime = stats.climbLedgeTime();
        actor.dashRecoverTime = stats.dashRecoverTime();
        actor.minTumbleTime = stats.minTumbleTime();

        actor.proneRecoverTime = stats.proneRecoverTime();
        actor.staggerAttackedTime = stats.staggerAttackedTime();
        actor.staggerBlockedMod = stats.staggerBlockedMod();

        // friction

        actor.maxCommandChain = (int) stats.maxCommandChain();
    }*/

    /*****************************************************************************/
    /******************************* Utility Stuff *******************************/
    /*****************************************************************************/

    private void updateCharacterGrade() { characterGrade = GradeEnum.B; }

    //Returns an array of indices that sorts the stat increases for use
    private int[] sortStats()
    {
        int top[] = new int[statEXP.length];
        int temp[] = new int[statEXP.length];
        int holder[] = new int[temp.length];

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

    public static void main(String[] args)
    {
        String grades[] = {
                "D-", "D",
                "B-", "C",
                "D+", "E",
                "C-", "C+",
                "C+", "D",
                "A-"
        };
        Actor lyraA = new Actor(0, 0, Actor.EnumType.Lyra);
        CharacterStat lyraStats = new CharacterStat(grades);
        //For this test, 100 increase points for the class total (basically for %)
        CharacterClass assassin = new CharacterClass("Assassin",
                new int[] {
                        10,5,
                        35,30,
                        5,0,
                        0,10,
                        5,0,
                        0
        });
        CharacterClass eMage = new CharacterClass("Erudian Mage",
                new int[] {
                        0,0,
                        5,0,
                        0,0,
                        10,5,
                        15,20,
                        45
                });
        Character lyra = new Character("Lyra",lyraA,lyraStats,grades,assassin);

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
            int exps[] = lyra.statEXP;

            for (int i = 0; i < lyra.classes.length; i++)
            {
                if(lyra.classes[i] == null) break;
                if(print) System.out.println("  " + (1+i) + ": " + lyra.classes[i].getClassName());
            }

            if(print) System.out.println("Stats:");
            GradeEnum lgrades[] = lyra.stats.getGrades();
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
