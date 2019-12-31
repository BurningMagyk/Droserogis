package Gameplay.Weapons;

import Gameplay.*;
import Util.PolygonIntersection;
import Util.Print;
import Util.Vec2;
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
    private ArrayList<Item> collidedItems = new ArrayList<Item>();


    public Weapon(float xPos, float yPos, float width, float height, float mass,
           WeaponType weaponType, String[] spritePaths)
    {
        super(xPos, yPos, width, height, mass, spritePaths);

        for (int i = 0; i < shapeCorners_Rotated.length; i++)
        { shapeCorners_Rotated[i] = SHAPE_CORNERS[i].copy(); }

        DEF_ORIENT = new Orient(weaponType.getDefaultOrient());
        orient = DEF_ORIENT.copy();

        ops = weaponType.getOps();
    }

    public Actor getActor() { return actor; }

    public void equip(Actor actor)
    {
        this.actor = actor;
        ballistic = false;
        idle = false;
        setPosition(actor.getPosition());

        orient = DEF_ORIENT.copy();
        dirOp = actor.getWeaponFace();
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
                                getPosition().x + actor.getWidth() * tickOrients[i].getX() * actor.getWeaponWidthRatio()
                                        * currentOp.getDir().getHoriz().getSign(),
                                getPosition().y + actor.getHeight() * tickOrients[i].getY());

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

    @Override
    public Color getColor()
    {
        if (actor != null)
        {
            if (actor.has(Actor.Condition.NEGATE_ATTACK)
                    || actor.has(Actor.Condition.NEGATE_BLOCK)
                    || actor.has(Actor.Condition.NEGATE_ACTIVITY))
                return Color.CYAN;
            else if (actor.tryingToBlock()) return Color.RED;
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
        currentOp.start(DEF_ORIENT, warmBoost, command);
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
    private void interrupt(Vec2 speed)
    {
        Print.blue("Interrupt at speed " + speed);
    }

    @Override
    protected void applyInflictions()
    {
        for (int i = 0; i < inflictions.size(); i++)
        {
            Infliction inf = inflictions.get(i);

            damage(inf);
            Vec2 momentum = inf.getMomentum();
            if (momentum != null) interrupt(momentum.div(getMass()));
        }

        inflictions.clear();
    }

    @Override
    public void inflict(Infliction infliction)
    {
        if (infliction != null) inflictions.add(infliction);
        Print.yellow("Weapon: " + infliction + " added");
    }

    @Override
    public boolean easyToBlock()
    {
        return false;
    }

    private int blockRating = 0;
    public int getBlockRating()
    {
        return blockRating;
    }

    @Override
    public void damage(Infliction inf)
    {

    }


    /*=======================================================================*/
    /*                                Loops                                  */
    /*=======================================================================*/

    @Override
    protected void update(EntityCollection<Entity> entities, float deltaSec)
    {
        applyInflictions();

        if (currentOp != null)
        {
            if (currentOp.run(deltaSec))
            {
                Print.blue("Finished \"" + currentOp.getName() + "\"");
                currentOp = null;
                orient = DEF_ORIENT.copy();
                collidedItems.clear();
            }
            else
            {
                if (currentOp instanceof RushOperation) orient = DEF_ORIENT;
                else orient = currentOp.getOrient();
                dirOp = currentOp.getDir();
            }

            updateClashes(entities.getWeaponList());
        }
        else orient = DEF_ORIENT.copy();
        updateCorners();
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
                    collidedItems.add(otherWeapon);
                    otherWeapon.inflict(currentOp.getInfliction());
                    Print.blue(this + " clashed with " + otherWeapon);
                    break;
                }
            }
        }
    }

    public void update(ArrayList<Item> items)
    {
        /* Apply inflictions from clashes earlier this frame */
        applyInflictions();

        /* Current operation may inflict something to the wielder */
        if (currentOpExec())
        {
            for (Item item : items)
            {
                if (item != actor && !collidedItems.contains(item)
                        && PolygonIntersection.isIntersect(getShapeCorners(), item))
                {
                    item.inflict(currentOp.getInfliction());
                    collidedItems.add(item);
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
        Infliction getInfliction();
        Infliction getSelfInfliction();
        State getState();
        Orient getOrient();
        float interrupt(Command command);
        MeleeOperation.MeleeEnum getNext(MeleeOperation.MeleeEnum meleeEnum);

        void start(Orient orient, float warmBoost, Command command);
        boolean run(float deltaSec);
        void release(int attackKey);
        void apply(Item other);

        /* For clashing */
        boolean isEasyToBlock();
        boolean isDisruptive();

        Operation copy();

        enum State { WARMUP, EXECUTION, COOLDOWN, VOID }
    }
}