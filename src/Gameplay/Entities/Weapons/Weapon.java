/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Gameplay.Entities.Weapons;

import Gameplay.*;
import Gameplay.Entities.Characters.CharacterStat;
import Gameplay.Entities.Actor;
import Gameplay.Entities.Entity;
import Gameplay.Entities.EntityCollection;
import Gameplay.Entities.Item;
import Util.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Weapon extends Item
{
    private DirEnum dirFace = DirEnum.RIGHT, dirOp = DirEnum.RIGHT;

    private Vec2 shapeCornersOffset = getPosition();
    private final Vec2[] SHAPE_CORNERS = {
            new Vec2(-getWidth() / 2, -getHeight() / 2),
            new Vec2(getWidth() / 2, -getHeight() / 2),
            new Vec2(getWidth() / 2, getHeight() / 2),
            new Vec2(-getWidth() / 2, getHeight() / 2)};
    private Vec2[] shapeCorners_Rotated = new Vec2[4];

    private final Orient DEF_ORIENT;
    private Orient orient;

    Actor actor;
    private boolean ballistic = false, idle = true;
    private Command currentCommand;
    Operation currentOp;
    private final Operation[] ops;
    private final WeaponType weaponType;
    private final WeaponStat weaponStat;
    private ArrayList<Item> collidedItems = new ArrayList<Item>();


    public Weapon(float xPos, float yPos, float width, float height, float mass,
           WeaponType weaponType, WeaponStat weaponStat, ArrayList<String[]> spritePaths)
    {
        super(xPos, yPos, width, height, mass, weaponStat.durability(), spritePaths);

        for (int i = 0; i < shapeCorners_Rotated.length; i++)
        { shapeCorners_Rotated[i] = SHAPE_CORNERS[i].copy(); }

        DEF_ORIENT = new Orient(weaponType.getDefaultOrient());
        orient = DEF_ORIENT.copy();

        this.weaponType = weaponType;
        ops = weaponType.getOps();

        this.weaponStat = weaponStat;
    }

    public String getName() { return weaponType.getName(); }
    public String getStatDataString() { return weaponStat.toDataString(); }

    public Actor getActor() { return actor; }

    public Weapon equip(Actor actor)
    {
        this.actor = actor;
        ballistic = false;
        idle = false;
        setPosition(actor.getPosition());

        orient = DEF_ORIENT.copy();
        dirOp = actor.getWeaponFace();
        updateCorners();

        return this;
    }
    public void unequip()
    {
        this.actor = null;
        ballistic = false;
        idle = true;

        updateCorners();
    }


    /*=======================================================================*/
    /*                               Polygons                                */
    /*=======================================================================*/

    public void updateCorners()
    {
        for (int i = 0; i < shapeCorners_Rotated.length; i++)
        {
            shapeCorners_Rotated[i].x = SHAPE_CORNERS[i].x;
            shapeCorners_Rotated[i].y = SHAPE_CORNERS[i].y;
        }

        float dirSign = (currentOp == null)
                ? dirFace.getHoriz().getSign() : dirOp.getHoriz().getSign();
        Vec2.setTheta(dirSign * orient.getTheta());

        for (Vec2 wieldDim : shapeCorners_Rotated) { wieldDim.rotate(); }
    }

    static float reduceTheta(float theta)
    {
        while (theta < 0) { theta += Math.PI; }
        while (theta >= Math.PI) { theta -= Math.PI; }
        return theta;
    }

    private void updateCornersOffset()
    {
        shapeCornersOffset = getPosition().copy();
    }
    private void updateCornersOffset(Vec2 dims, DirEnum dir)
    {
        if (dir != DirEnum.UP && dir != DirEnum.DOWN)
        {
            shapeCornersOffset = new Vec2(
                    getPosition().x + dims.x * orient.getX() * actor.getWeaponWidthRatio()
                            * (currentOp == null || currentOp.getDir() == null
                            ? dir.getHoriz().getSign()
                            : currentOp.getDir().getHoriz().getSign()),
                    getPosition().y + dims.y * orient.getY());
        }

        if (dirFace != dir && currentOp == null)
        {
            updateCornersOffset();
            dirFace = dir;
        }
    }

    public void updatePosition(Vec2 p, Vec2 v, Vec2 dims, DirEnum dir)
    {
        setPosition(p);
        setVelocity(v);
        updateCornersOffset(dims, dir);
    }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        updateCornersOffset();
    }


    /*=======================================================================*/
    /*                               Clashing                                */
    /*=======================================================================*/

    public Vec2[][] getClashShapeCorners()
    {
        if (idle) return null;

        if (currentOp != null && currentOp instanceof MeleeOperation)
        {
            if (currentOp.getState() == Operation.State.EXECUTION)
            {
                Orient[] tickOrients = ((MeleeOperation) currentOp).getTickOrients();
                Vec2[][] verts = new Vec2[tickOrients.length * 2 - 1][4];
                for (int i = 0; i < tickOrients.length; i++)
                {
                    Vec2[] shapeCorners = new Vec2[4];
                    for (int j = 0; j < 4; j++)
                    {
                        shapeCorners[j] = new Vec2(SHAPE_CORNERS[j]);
                    }

                    Vec2.setTheta(dirOp.getHoriz().getSign() * tickOrients[i].getTheta());
                    for (Vec2 wieldDim : shapeCorners)
                    {
                        wieldDim.rotate();

                        Vec2 cornersOffset = new Vec2(
                                getPosition().x + actor.getDefWidth() * tickOrients[i].getX() * actor.getWeaponWidthRatio()
                                        * currentOp.getDir().getHoriz().getSign(),
                                getPosition().y + actor.getDefHeight() * tickOrients[i].getY());

                        wieldDim.add(cornersOffset);
                    }

                    for (int j = 0; j < 4; j++)
                    {
                        verts[i][j] = shapeCorners[j];
                    }
                }
                for (int i = 0; i < tickOrients.length - 1; i++)
                {
                    verts[i + tickOrients.length] = new Vec2[] {
                            verts[i + 1][1], verts[i + 1][0],
                            verts[i][3], verts[i][2] };
                }
                return verts;
            }
            return new Vec2[][] {getShapeCorners()};
        }
        else if (ballistic)
        {
            return new Vec2[][] {getShapeCorners()};
        }
        return null;
    }


    /*=======================================================================*/
    /*                               Drawing                                 */
    /*=======================================================================*/

    public Vec2[] getShapeCorners()
    {
        Vec2[] corners = new Vec2[shapeCorners_Rotated.length];
        for (int i = 0; i < shapeCorners_Rotated.length; i++)
        {
            corners[i] = new Vec2(shapeCorners_Rotated[i].x + shapeCornersOffset.x,
                    shapeCorners_Rotated[i].y + shapeCornersOffset.y);
        }
        return corners;
    }
    private Vec2[] getActorCorners()
    {
        Vec2[] corners = new Vec2[4];
        float xLeft = actor.getX() - (actor.getWidth() / 2),
                xRight = actor.getX() + (actor.getWidth() / 2),
                yUp = actor.getY() - (actor.getHeight() / 2),
                yDown = actor.getY() + (actor.getHeight() / 2);
        corners[0] = new Vec2(xLeft, yUp);
        corners[1] = new Vec2(xRight, yUp);
        corners[2] = new Vec2(xRight, yDown);
        corners[3] = new Vec2(xLeft, yDown);
        return corners;
    }

    @Override
    public Color getColor()
    {
        if (actor != null)
        {
            if (actor.has(Actor.Condition.NEGATE_ATTACK)
                    || actor.has(Actor.Condition.NEGATE_BLOCK)
                    || actor.has(Actor.Condition.NEGATE_ACTIVITY))
                return Color.CYAN;
            else if (actor.isBlockingUp()) return Color.RED;
        }
        return Color.BLACK;
    }


    /*=======================================================================*/
    /*                             Application                               */
    /*=======================================================================*/

    private Operation getOperation(Command command)
    {
        MeleeOperation.MeleeEnum nextMelee = null;
        if (currentOp != null) nextMelee = currentOp.getNext(command.ENUM);
        if (nextMelee != null) return ops[nextMelee.ordinal()];
        return ops[command.ENUM.ordinal()];
    }

    public boolean currentOpActive() { return currentOp != null; }
    private boolean currentOpExec() { return currentOp != null
            && currentOp.getState() == Operation.State.EXECUTION; }

    public Command mayInterrupt(Command command, Actor.State state, boolean canStand)
    {
        if (currentOp != null && currentOp.getState() == Operation.State.WARMUP)
            return currentCommand.merge(command, state, canStand);
        return null;
    }

    private void setOperation(Operation newOp, Command command, float warmBoost)
    {
        currentCommand = command;
        currentOp = newOp;
        collidedItems.clear();
        currentOp.start(DEF_ORIENT, warmBoost,
                actor.getCharacterStat(), weaponStat, command);
    }

    public boolean addCommand(Command command, boolean combo, boolean chain)
    {
        Operation newOp = getOperation(command);

        if (newOp != null)
        {
            if (currentOp != null)
            {
                Operation.State currentState = currentOp.getState();

                if (combo)
                {
                    if (currentState != Operation.State.EXECUTION)
                    {
                        Print.blue("Combo op \"" + newOp.getName() + "\" set");
                        float boost = currentOp.interrupt(command);
                        setOperation(newOp, command, boost);
                        return true;
                    }
                }
                else if (chain)
                {
                    if (currentState.ordinal() >= Operation.State.COOLDOWN.ordinal())
                    {
                        Print.blue("Chain op \"" + newOp.getName() + "\" set");
                        float boost = currentOp.interrupt(command);
                        setOperation(newOp, command, boost);
                        return true;
                    }
                }
                else
                {
                    if (currentState != Operation.State.WARMUP)
                    {
                        Print.blue("Command \"" + command + "\" set");
                        currentCommand = command;
                        return true;
                    }
                }
            }
            else if (!chain)
            {
                Print.blue("Normal op \"" + newOp.getName() + "\" set");
                setOperation(newOp, command, 0);
                return true;
            }
        }
        return false;
    }

    public void releaseCommand(int attackKey)
    {
        if (currentOp != null) currentOp.release(attackKey);
    }

    public void interrupt()
    {
        if (currentOp != null) currentOp.interrupt(null);
    }
    public void interrupt(RushOperation.RushFinish rushFinish)
    {
        if (currentOp != null && currentOp instanceof RushOperation)
            ((RushOperation) currentOp).interrupt(rushFinish);
    }
    private void interrupt(Vec2 momentum)
    {
        if (momentum != null)
        {
            if (currentOpExec())
            {
                if (momentum.mag() > currentOp.getInfliction(actor, getMass()).getMomentum().mag())
                    interrupt();
            }
            else interrupt();

            Print.yellow("Momentum: " + momentum);
        }
    }

    @Override
    public void applyInflictions()
    {
        for (int i = 0; i < inflictions.size(); i++)
        {
            Infliction inf = inflictions.get(i);

            Print.yellow("----------Weapon----------");

            damage(inf);
            Vec2 momentum = inf.getMomentum();
            if (momentum != null)
            {
                /* Weapon got parried */
                if (actor != null)
                {
                    Vec2 momentumCopy = momentum.copy();
                    actor.addVelocity(momentumCopy.div(actor.getMass()));

                    /* Stagger actor */
                    actor.staggerParry(inf.getDamage(), inf.getDir());
                }

                interrupt(momentum.div(getMass()));
            }

            Print.yellow("--------------------------");
        }

        inflictions.clear();
    }

    @Override
    public void inflict(Infliction infliction)
    {
        if (infliction != null) inflictions.add(infliction);
        Print.yellow("Weapon: " + infliction + " added");
    }

    public int getBlockRating()
    {
        return weaponStat.blockRating();
    }
    public boolean isIdle() { return idle; }

    @Override
    public void damage(Infliction inf)
    {
        GradeEnum damage = inf.getDamage();
        if (damage != null)
        {
            //Print.yellow("Damage: " + damage);
        }
    }

    @Override
    protected void destroy()
    {
        // TODO: unequip weapon and drop
    }

    /*=======================================================================*/
    /*                                Loops                                  */
    /*=======================================================================*/

    @Override
    public void update(EntityCollection<Entity> entities, float deltaSec)
    {
        if (idle || ballistic)
        {
            resetAcceleration();
            applyPhysics(entities, deltaSec);
            updateCornersOffset(); // Weapons are drawn using their corners, unlike other items
            // So their corners need to be updated whenever they move
        }
        else
        {
            if (currentOp != null)
            {
                if (currentOp.run(deltaSec))
                {
                    if (currentOp.getName().equals("Interact")) actor.interact();

                    Print.blue("Finished \"" + currentOp.getName() + "\"");
                    currentOp = null;
                    orient = DEF_ORIENT.copy();
                }
                else
                {
                    if (currentOp instanceof RushOperation) orient = DEF_ORIENT;
                    else orient = currentOp.getOrient();
                    dirOp = currentOp.getDir();

                    /* Current operation may inflict something to the wielder */
                    actor.inflict(currentOp.getSelfInfliction());
                }

                updateClashes(entities.getWeaponList());
            }
            else orient = DEF_ORIENT.copy();
            updateCorners();
        }
    }

    private void updateClashes(ArrayList<Weapon> otherWeapons)
    {
        Vec2[][] clashShapeCorners = getClashShapeCorners();
        if (clashShapeCorners == null) return;

        for (Weapon otherWeapon : otherWeapons)
        {
            if (otherWeapon == this || otherWeapon.idle || otherWeapon.currentOp == null
                    || collidedItems.contains(otherWeapon)
                    || (!currentOpExec() && !otherWeapon.currentOpExec())) continue;

            Vec2[] otherPolygon = otherWeapon.getShapeCorners();
            for (Vec2[] polygon : clashShapeCorners)
            {
                if (PolygonIntersection.isIntersect(polygon, otherPolygon))
                {
                    clash(otherWeapon);
                    otherWeapon.clash(this);
                    break;
                }
            }
        }
    }
    private void clash(Weapon otherWeapon)
    {
        collidedItems.add(otherWeapon);
        otherWeapon.inflict(currentOp.getInfliction(actor, getMass()));
        Print.blue(this + " clashed with " + otherWeapon);
    }

    public void update(ArrayList<Item> items)
    {
        /* Apply inflictions from clashes earlier this frame */
        applyInflictions();

        /* Inflict to other item that isn't another attacking weapon */
        if (currentOpExec())
        {
            for (Item item : items)
            {
                if (item != this && item != actor && !collidedItems.contains(item)
                        && (!(item instanceof Weapon) || ((Weapon) item).idle || ((Weapon) item).ballistic)
                        && PolygonIntersection.isIntersect(currentOp instanceof RushOperation
                        ? getActorCorners() : getShapeCorners(), item))
                {
                    Infliction inf = currentOp.getInfliction(actor, getMass());
                    collidedItems.add(item);

                    /* If other item is an actor */
                    if (item instanceof Actor)
                    {
                        Actor other = (Actor) item;
                        DirEnum dir = currentOp.getDir();
                        if (currentOp instanceof RushOperation)
                        {
                            /* If other item is an actor and they're facing you */
                            if ((other.getWeaponFace().getHoriz().isOpp(dir.getHoriz()))
                                    || (other.isBlockingUp() && (dir.getVert() == DirEnum.DOWN || dir == DirEnum.NONE)
                                        && PolygonIntersection.isIntersect(getActorCorners(), other.getTopRect()))
                                    || (!other.isBlockingUp() && (dir.getVert() == DirEnum.UP || dir == DirEnum.NONE)
                                        && PolygonIntersection.isIntersect(getActorCorners(), other.getBottomRect())))
                            {
                                actor.staggerBlock(inf.getDamage(), dir);
                                ((Actor) item).getBlockingWeapon().inflict(inf);
                            }
                            else item.inflict(inf);
                        }
                        else
                        {
                            /* If other item is an actor and they're facing you */
                            if ((other.getWeaponFace().getHoriz().isOpp(dir.getHoriz()))
                                    || (other.isBlockingUp() && (dir.getVert() == DirEnum.DOWN || dir == DirEnum.NONE)
                                    && PolygonIntersection.isIntersect(getShapeCorners(), other.getTopRect()))
                                    || (!other.isBlockingUp() && (dir.getVert() == DirEnum.UP || dir == DirEnum.NONE)
                                    && PolygonIntersection.isIntersect(getShapeCorners(), other.getBottomRect())))
                            {
                                actor.staggerBlock(inf.getDamage(), dir);
                                ((Actor) item).getBlockingWeapon().inflict(inf);
                            }
                            else item.inflict(inf);
                        }
                    }

                    /* If other item is not an actor */
                    else item.inflict(inf);
                }
            }
        }
    }


    /*=======================================================================*/
    /*                            Inner Classes                              */
    /*=======================================================================*/

    interface Operation
    {
        String getName();
        DirEnum getDir();
        Infliction getInfliction(Actor actor, float mass);
        Infliction getSelfInfliction();
        State getState();
        Orient getOrient();
        float interrupt(Command command);
        MeleeOperation.MeleeEnum getNext(MeleeOperation.MeleeEnum meleeEnum);

        void start(Orient orient, float warmBoost, CharacterStat characterStat,
                   WeaponStat weaponStat, Command command);
        boolean run(float deltaSec);
        void release(int attackKey);
        void apply(Item other); // TODO: what's this for??

        /* For clashing */
        boolean isParrying();
        boolean isPermeating();

        Operation copy();

        enum State { WARMUP, EXECUTION, COOLDOWN, VOID }
    }
}