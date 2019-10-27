package Gameplay;

// This class is used to hold the collection of entities.
// For the most part, it is populated by the level reader and accessed by Gameplay.
// The purpose of this class is to organize in such a way as to provide an easy interface and very efficient access.
// TODO: monsters and players should each be different subclasses of Actor.
// TODO: At least one difference between them is that players have input devices (controllers) while monsters have AI.


import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;

public class EntityCollection<Entity> extends AbstractCollection<Entity>
{
    private ArrayList<Block> blockList = new ArrayList<>();
    private ArrayList<Item> itemList = new ArrayList<>();
    private ArrayList<Actor> monsterList = new ArrayList<>();
    private ArrayList<Actor> playerList = new ArrayList<>();
    private ArrayList<CameraZone> cameraZoneList = new ArrayList<>();

    // dynamicItems is a list of copies of pointers to Entities that move (players, monsters and items).
    private ArrayList<Item> dynamicItems = new ArrayList<>();

    // physicsItems is a list of copies of pointers to Entities that are involved in physics.
    //    Currently, that is all items except for CameraZones.
    // Special effects may also be Entities that do not have physics.
    private ArrayList<Entity> physicsItems = new ArrayList<>();

    private ArrayList[] collectionList =
    {
            cameraZoneList, blockList, monsterList, playerList, itemList
    };

    private int totalSize = 0;

    @Override public int size()
    {
        return totalSize;
    }

    @Override public void clear()
    {
        blockList.clear();
        itemList.clear();
        monsterList.clear();
        cameraZoneList.clear();
        playerList.clear();
        dynamicItems.clear();
        physicsItems.clear();
        totalSize = 0;
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

    public ArrayList<Actor> getPlayerList() {return playerList;}

    public ArrayList<Item> getItemList() {return itemList;}

    public ArrayList<Item> getDynamicItems() {return dynamicItems;}

    public int getPlayerCount() {return playerList.size();}


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
            dynamicItems.add((Actor) entity);
            add((Entity) ((Actor) entity).getWeapons()[0]);
            physicsItems.add(entity);
        }
        else if (entity instanceof Item)
        {
            itemList.add((Item) entity);
            dynamicItems.add((Item) entity);
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
        }
        //System.out.println(")");
        return true;
    }




    @Override public boolean remove(Object entity)
    {
        boolean objectExists = false;
        if (entity instanceof Actor)
        {
            //System.out.print("Actor");
            objectExists = playerList.remove(entity);
            if (objectExists)
            {
                dynamicItems.remove(entity);
                physicsItems.remove(entity);
            }
        }
        else if (entity instanceof Item)
        {
            objectExists = itemList.remove(entity);
            if (objectExists)
            {
                dynamicItems.remove(entity);
                physicsItems.remove(entity);
            }
        }
        else if (entity instanceof Block)
        {
            objectExists = blockList.remove(entity);
            if (objectExists)
            {
                physicsItems.remove(entity);
            }
        }
        else if (entity instanceof CameraZone)
        {
            objectExists = cameraZoneList.remove(entity);
        }
        if (objectExists)
        {
            totalSize--;
        }
        return objectExists;
    }




    public Actor getPlayer(int i) { return playerList.get(i);}

    public ArrayList<CameraZone> getCameraZoneList() { return cameraZoneList;}

    public Entity get(int idx)
    {
        if (idx >= totalSize)
        {
            throw new IllegalArgumentException("EntityCollection.get(" + idx + ") out of bounds: totalSize=" + totalSize);
        }

        //Find the correct list
        int i = idx;
        for (int collectionIdx = 0; collectionIdx < collectionList.length; collectionIdx++)
        {

            //System.out.println("collectionList["+collectionIdx+"].size()="+ collectionList[collectionIdx].size() + ",  i="+i);
            if (i < collectionList[collectionIdx].size())
            {
                return (Entity) collectionList[collectionIdx].get(i);
            }
            i -= collectionList[collectionIdx].size();
        }

        System.out.println("cameraZoneList.size()="+cameraZoneList.size());
        System.out.println("blockList.size()="+blockList.size());
        System.out.println("itemList.size()="+itemList.size());
        System.out.println("monsterList.size()="+monsterList.size());
        System.out.println("playerList.size()="+playerList.size());

        System.out.println("dynamicItems.size()="+dynamicItems.size());
        System.out.println("physicsItems.size()="+physicsItems.size());

        throw new IllegalArgumentException("EntityCollection.get(" + idx + ") List corrupted: totalSize=" + totalSize);
    }
}

