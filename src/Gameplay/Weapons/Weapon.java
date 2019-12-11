package Gameplay.Weapons;

import Gameplay.*;
import Gameplay.Weapons.Inflictions.Infliction;
import Util.GradeEnum;
import Util.Vec2;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedList;

public class Weapon extends Item
{
    private DirEnum dirFace = DirEnum.RIGHT;

    private Vec2 shapeCornersOffset = getPosition();
    private final Vec2[] SHAPE_CORNERS = {
            new Vec2(-getWidth() / 2, -getHeight() / 2),
            new Vec2(+getWidth() / 2, -getHeight() / 2),
            new Vec2(+getWidth() / 2, +getHeight() / 2),
            new Vec2(-getWidth() / 2, +getHeight() / 2)};
    private Vec2[] shapeCorners_Rotated = new Vec2[4];

    private final Orient DEF_ORIENT;
    private Orient orient;

    Actor actor;
    private boolean ballistic = true;
    private LinkedList<Command> commandQueue = new LinkedList<>();
    Operation currentOp;



    public Weapon(float xPos, float yPos, float width, float height, float mass,
           WeaponType weaponType, String[] spritePaths)
    {
        super(xPos, yPos, width, height, mass, spritePaths);

        for (int i = 0; i < shapeCorners_Rotated.length; i++)
        { shapeCorners_Rotated[i] = SHAPE_CORNERS[i].clone(); }

        DEF_ORIENT = new Orient(weaponType.getDefaultOrient());
        orient = DEF_ORIENT.copy();
    }

    public Actor getActor() { return actor; }


    /*=======================================================================*/
    /*                               Polygons                                */
    /*=======================================================================*/

    public void setTheta(float theta, DirEnum opDir)
    {
        for (int i = 0; i < shapeCorners_Rotated.length; i++)
        {
            shapeCorners_Rotated[i].x = SHAPE_CORNERS[i].x;
            shapeCorners_Rotated[i].y = SHAPE_CORNERS[i].y;
        }

        Vec2.setTheta(opDir.getHoriz().getSign() * theta);

        for (Vec2 wieldDim : shapeCorners_Rotated) { wieldDim.rotate(); }
        orient.setTheta(reduceTheta(theta));
    }

    private float reduceTheta(float theta)
    {
        while (theta < 0) { theta += Math.PI * 2; }
        while (theta >= Math.PI * 2) { theta -= Math.PI * 2; }
        return theta;
    }

    private void updateCorners()
    {
        shapeCornersOffset = getPosition().clone();
    }
    private void updateCorners(Vec2 dims, DirEnum dir)
    {
        if (dir != DirEnum.UP && dir != DirEnum.DOWN)
        {
            shapeCornersOffset = new Vec2(
                    getPosition().x + dims.x * orient.getX()
                            * (currentOp == null || currentOp.getDir() == null
                            ? dir.getHoriz().getSign()
                            : currentOp.getDir().getHoriz().getSign()),
                    getPosition().y + dims.y * orient.getY());
        }

        if (dirFace != dir && currentOp == null)
        {
            setTheta(orient.getTheta(), dir);
            dirFace = dir;
        }
    }

    public void updatePosition(Vec2 p, Vec2 v, Vec2 dims, DirEnum dir)
    {
        setPosition(p);
        setVelocity(v);
        updateCorners(dims, dir);
    }

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


    /*=======================================================================*/
    /*                               Drawing                                 */
    /*=======================================================================*/

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

    private boolean isApplicable(Command command)
    {
        return true; // TODO: go through every operation available
    }

    public boolean addCommand(Command command)
    {
        if (commandQueue.size() < actor.getMaxCommandChain() && isApplicable(command))
        {
            commandQueue.addLast(command);
            return true;
        }
        return false;
    }

    public void releaseCommand(int attackKey)
    {
        if (currentOp != null) currentOp.release(attackKey);
        for (Command command : commandQueue) command.release(attackKey);
    }

    private boolean disrupted = false;
    public void disrupt()
    {
        disrupted = true;
        orient.set(DEF_ORIENT.copy());
    }

    @Override
    protected void applyInflictions()
    {

    }

    @Override
    public void inflict(Infliction infliction)
    {

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

    }

    @Override
    protected void update(ArrayList<Item> items)
    {
        /* Current operation may inflict something to the wielder */
        //if (currentOp != null)
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

        void start(Orient orient);
        boolean run(float deltaSec);
        void release(int attackKey);
        void apply(Item other);

        /* For clashing */
        boolean isEasyToBlock();
        boolean isDisruptive();

        enum State { WARMUP, EXECUTION, COOLDOWN, VOID }
    }

    class Orient
    {
        private Vec2 pos;
        private float theta;

        Orient(Vec2 pos, float theta)
        {
            this.pos = new Vec2(pos.x, pos.y);
            this.theta = theta;
        }
        Orient(float[] args) { this(new Vec2(args[0], args[1]), args[2]); }

        float getX() { return pos.x; } void setX(float x) { pos.x = x; }
        float getY() { return pos.y; } void setY(float y) { pos.y = y; }
        float getTheta() { return theta; }
        void setTheta(float theta) { this.theta = theta; }
        void addTheta(float theta) { this.theta += theta; }

        void set(Orient orient)
        {
            pos.x = orient.getX();
            pos.y = orient.getY();
            theta = orient.getTheta();
        }

        void _reduceTheta()
        {
            theta = reduceTheta(theta);
        }

        Orient copy()
        {
            return new Orient(new Vec2(pos.x, pos.y), theta);
        }

        Orient copyOppHoriz() { return new Orient(new Vec2(-pos.x,  pos.y), theta - (float) Math.PI / 2); }

        float getMagnitude()
        {
            return (float) Math.sqrt(pos.x * pos.x + pos.y * pos.y);
        }

        private float reduceTheta(float theta)
        {
            while (theta < 0) { theta += Math.PI * 2; }
            while (theta >= Math.PI * 2) { theta -= Math.PI * 2; }
            return theta;
        }
    }

    class Tick
    {
        float _sec, totalSec;
        Orient tickOrient;

        Tick(float _sec, float posX, float posY, float theta)
        {
            this._sec = _sec;
            tickOrient = new Orient(new Vec2(posX, posY), reduceTheta(theta));
        }

        void setSpeed(float speed) { totalSec = _sec * speed; }

        boolean check(float totalSec, DirEnum dir)
        {
            if (totalSec < this.totalSec)
            {
                orient.setX(tickOrient.getX());
                orient.setY(tickOrient.getY());
                setTheta(tickOrient.getTheta(), dir);
                return true;
            }
            return false;
        }

        Orient getOrient() { return tickOrient; }

        Tick getMirrorCopy(boolean horiz, boolean vert)
        {
            return new Tick(totalSec,
                    (horiz ? -1 : 1) * tickOrient.getX(),
                    (vert ? -1 : 1) * tickOrient.getY(),
                    tickOrient.getTheta()
                            - (horiz ^ vert ? (float) Math.PI / 2 : 0));
        }

        Tick getRotatedCopy(boolean up)
        {
            return new Tick(totalSec, tickOrient.getX(), tickOrient.getY(),
                    tickOrient.getTheta() + (float) (Math.PI / 2.0));
        }

        Tick getTimeModdedCopy(float add, float mult)
        {
            return new Tick((totalSec + add) * mult,
                    tickOrient.getX(), tickOrient.getY(),
                    tickOrient.getTheta());
        }
    }

    class Journey
    {
        private Orient start, end, distance;
        private float _time, totalTime;

        Journey(Orient start, Orient end, float _time)
        {
            end._reduceTheta();
            this.end = end;
            this._time = _time;
            setStart(start);
        }

        void setSpeed(float speed) { totalTime = _time * speed; }

        boolean check(float time, DirEnum dir)
        {
            float ratio = time / totalTime;
            if (ratio >= 1.0)
            {
                orient.set(end);
                setTheta(orient.getTheta(), dir);
                return true;
            }
            orient.setX(start.getX() + (distance.getX() * ratio));
            orient.setY(start.getY() + (distance.getY() * ratio));
            setTheta(start.getTheta() + (distance.getTheta() * ratio), dir);
            return false;
        }

        /** Called every time an Operation starts so that the weapon moves
         * directly from the position its Operation was called at. */
        void setStart(Orient start)
        {
            start._reduceTheta();
            this.start = start.copy();

            /* All this mess here is just for making sure it rotates in the
             * correct direction. */
            double endMinimal = Math.min(end.getTheta(), (Math.PI * 2) - end.getTheta());
            double startMinimal = Math.min(start.getTheta(), (Math.PI * 2) - start.getTheta());
            double thetaDistance;
            if (Math.abs(end.getTheta() - start.getTheta()) < endMinimal + startMinimal)
                thetaDistance = end.getTheta() - start.getTheta();
            else thetaDistance = (endMinimal + startMinimal) * start.getTheta() > end.getTheta() ? 1 : -1;

            distance = new Orient(
                    new Vec2(end.getX() - start.getX(),
                            end.getY() - start.getY()),
                    (float) thetaDistance);
        }
    }
}
