package Gameplay;

// This class is used to hold the collection of entities.
// For the most part, it is populated by the level reader and accessed by Gameplay.
// The purpose of this class is to organize in such a way as to provide an easy interface and very efficient access.
// TODO: monsters and players should each be different subclasses of Actor.
// TODO: At least one difference between them is that players have input devices (controllers) while monsters have AI.

import Util.Print;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;

public class EntityCollection<Entity> extends AbstractCollection<Entity>
{
    private ArrayList<Block> blockList = new ArrayList<>();
    private ArrayList<Item> itemList = new ArrayList<>();     //For now, this list contains both items and players.
    private ArrayList<Actor> monsterList = new ArrayList<>();
    private ArrayList<CameraZone> cameraZoneList = new ArrayList<>();

    private ArrayList<Actor> playerList = new ArrayList<>();

    // physicsItems is a list of copies of pointers to Entities that are involved in physics.
    //    Currently, that is all items except for CameraZones.
    // Special effects may also at some point be Entities that do not have physics.
    private ArrayList<Entity> physicsItems = new ArrayList<>();

    private ArrayList[] collectionList =
    {
            cameraZoneList, monsterList, itemList, blockList
    };

    private int totalSize = 0;

    @Override public int size(){
        return totalSize;
    }
    @Override public Iterator<Entity> iterator()
    {
        return new Iterator<Entity>()
        {
            int totalIdx = -1;
            int collectionIdx = 0;
            int idx = -1;
            public boolean hasNext()
            {
                if (totalIdx < totalSize - 1) return true;
                return false;
            }

            public Entity next()
            {
                totalIdx++;
                idx++;
                while (idx >= collectionList[collectionIdx].size())
                {
                    collectionIdx++;
                    idx = 0;
                }
                return (Entity)collectionList[collectionIdx].get(idx);
            }
        };
    }


    //public Iterator<Item> getItemIterator() {return itemList.iterator();}
    public ArrayList<Item> getItemList() {return itemList;}


    //=================================================================================================================
    // Checks to make sure duplicates aren't being added.
    // Also adds the entity to a list of items if it's an Item or Actor.
    // =================================================================================================================
    @Override
    public boolean add(Entity entity)
    {
        //System.out.print("EntityCollection(");
        totalSize++;
        if (entity instanceof Actor)
        {
            //System.out.print("Actor");
            playerList.add((Actor) entity);
            itemList.add((Actor) entity);
            physicsItems.add(entity);
        }
        else if (entity instanceof Item)
        {
            itemList.add((Item) entity);
            physicsItems.add(entity);
        }
        else if (entity instanceof Block)
        {
            blockList.add((Block) entity);
            physicsItems.add(entity);
        }
        else if (entity instanceof CameraZone)
        {
            cameraZoneList.add((CameraZone) entity);
            physicsItems.add(entity);
        }
        //System.out.println(")");
        return true;
    }

    public Actor getPlayer(int i) { return playerList.get(i);}

    public ArrayList<CameraZone> getCameraZoneList() { return cameraZoneList;}
}

