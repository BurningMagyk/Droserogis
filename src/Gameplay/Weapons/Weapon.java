package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Gameplay.Entity;
import Gameplay.Item;
import Util.Print;
import Util.Vec2;

import java.util.*;

public class Weapon extends Item
{
    DirEnum dirFace = DirEnum.RIGHT;

    private Vec2 shapeCornersOffset = getPosition();
    private Vec2 shapeCorners_notRotated[] = {
            new Vec2(-getWidth() / 2, -getHeight() / 2),
            new Vec2(+getWidth() / 2, -getHeight() / 2),
            new Vec2(+getWidth() / 2, +getHeight() / 2),
            new Vec2(-getWidth() / 2, +getHeight() / 2)};
    private Vec2 shapeCorners_Rotated[] = new Vec2[4];

    Orient defaultOrient = new Orient(new Vec2(1F, 0F), 0);
    Orient orient = defaultOrient.copy();

    private Actor actor;
    private boolean ballistic = true;
    private LinkedList<Operation> operationQueue = new LinkedList<>();
    private Map<Integer, Operation> keyCombos = new HashMap<>();
    private Style style = Style.DEFAULT;
    private Operation currentOp;

    Weapon(float xPos, float yPos, float width, float height)
    {
        super(xPos, yPos, width, height);

        for (int i = 0; i < shapeCorners_Rotated.length; i++)
        { shapeCorners_Rotated[i] = shapeCorners_notRotated[i].clone(); }
    }

    @Override
    protected void update(ArrayList<Entity> entities, float deltaSec)
    {
        if (ballistic) super.update(entities, deltaSec);
        else if (currentOp != null)
        {
            boolean operationDone = currentOp.run(deltaSec);
            if (operationDone) currentOp = null;

            if (!operationQueue.isEmpty() && (operationDone || currentOp.mayInterrupt()))
            {
                currentOp = operationQueue.remove();
                currentOp.start(actor.getWeaponFace());
            }
        }
        else if (!operationQueue.isEmpty())
        {
            currentOp = operationQueue.remove();
            currentOp.start(actor.getWeaponFace());
        }
    }

    public void setTheta(float theta, DirEnum opDir)
    {
        for (int i = 0; i < shapeCorners_Rotated.length; i++)
        {
            shapeCorners_Rotated[i].x = shapeCorners_notRotated[i].x;
            shapeCorners_Rotated[i].y = shapeCorners_notRotated[i].y;
        }

        Vec2.setTheta(opDir.getHoriz().getSign() * theta);

        for (Vec2 wieldDim : shapeCorners_Rotated) { wieldDim.rotate(); }
        orient.setTheta(reduceTheta(theta));
    }

    public void updatePosition(Vec2 p, Vec2 dims, DirEnum dir)
    {
        setPosition(p);
        if (dir != DirEnum.UP && dir != DirEnum.DOWN)
        {
            shapeCornersOffset = new Vec2(p.x + dims.x * orient.getX()
                    * (currentOp == null ? dir.getHoriz().getSign()
                    : currentOp.getDir().getHoriz().getSign()),
                    p.y + dims.y * orient.getY());
        }

        if (dirFace != dir && currentOp == null)
        {
            setTheta(orient.getTheta(), dir);
            dirFace = dir;
        }
    }

    /**
     * Depending on keyCombo and currentSytle, will cause the weapon to do
     * something.
     */
    public void operate(boolean pressed, int keyCombo)
    {
        if (!pressed) return; /* Temporary */
        Operation op = keyCombos.get(keyCombo);
        if (op != null)
        {
            operationQueue.addLast(op);
        }
    }

    enum Style
    {
        HALF
                {
                    boolean isValid(Weapon weapon)
                    {
                        if (weapon instanceof Sword) return true;
                        return false;
                    }
                },
        MURDER
                {
                    boolean isValid(Weapon weapon)
                    {
                        if (weapon instanceof Sword) return true;
                        return false;
                    }
                },
        DEFAULT;

        boolean isValid(Weapon weapon) { return true; }
    }

    public Weapon equip(Actor actor)
    {
        this.actor = actor;
        ballistic = false;
        return this;
    }

    void changeActorDirFace() { actor.changeDirFace(); }

    boolean isBallistic() { return ballistic; }
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
    Style getStyle() { return style; }

//    void setRelativePos(Vec2 p)
//    {
//        orient.setX(p.x);
//        orient.setY(p.y);
//    }

    interface Operation
    {
        String getName();

        DirEnum getDir();

        void start(DirEnum direction);

        /** Returns true if the operation finished */
        boolean run(float deltaSec);

        boolean mayInterrupt();

        enum State { WARMUP, EXECUTION, COOLDOWN, COUNTERED }
    }

    void setOperation(Operation op, int keyCombo)
    {
        keyCombos.put(keyCombo, op);
    }

    class Tick
    {
        float totalSec;
        Orient tickOrient;

        Tick(float totalSec, float posX, float posY, float theta)
        {
            this.totalSec = totalSec;
            tickOrient = new Orient(new Vec2(posX, posY), reduceTheta(theta));
        }

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

        Tick getRotatedCopy(boolean up)
        {
            return new Tick(totalSec, tickOrient.getX(), tickOrient.getY(),
                    tickOrient.getTheta() + (float) (Math.PI / 2.0));
        }
    }

    class Journey
    {
        private Orient start, end, distance;
        private float timeMod, totalTime;

        Journey(Orient start, Orient end, float timeMod)
        {
            end._reduceTheta();
            this.end = end;
            this.timeMod = timeMod;
            setStart(start);
        }

        boolean check(float time, DirEnum dir)
        {
            float ratio = time / totalTime;
            if (ratio >= 1.0)
            {
                orient.set(end);
                return true;
            }
            orient.setX(start.getX() + (distance.getX() * ratio));
            orient.setY(start.getY() + (distance.getY() * ratio));
            setTheta(start.getTheta() + (distance.getTheta() * ratio), dir);
            return false;
        }

        /** Called every time an Operation starts so that the weapon moves
          * directly from the position its Operation was called at. */
        float setStart(Orient start)
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

            float distanceMagnitude = distance.getMagnitude();
            //totalTime = (float) Math.log(100 * timeMod * distanceMagnitude) / (float) Math.log(1.5) / 40;
            totalTime = timeMod * distanceMagnitude;
            Print.red(totalTime);
            return distance.getMagnitude();
        }
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

        float getX() { return pos.x; } void setX(float x) { pos.x = x; }
        float getY() { return pos.y; } void setY(float y) { pos.y = y; }
        float getTheta() { return theta; }
        void setTheta(float theta) { this.theta = theta; }

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
    }

    private float reduceTheta(float theta)
    {
        while (theta < 0) { theta += Math.PI * 2; }
        while (theta >= Math.PI * 2) { theta -= Math.PI * 2; }
        return theta;
    }
}