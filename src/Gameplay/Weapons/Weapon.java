package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Gameplay.Entity;
import Gameplay.Item;
import Util.Print;
import Util.Vec2;

import java.util.*;

public abstract class Weapon extends Item
{
    private DirEnum dirFace = DirEnum.RIGHT;

    private Vec2 shapeCornersOffset = getPosition();
    private Vec2[] shapeCorners_notRotated = {
            new Vec2(-getWidth() / 2, -getHeight() / 2),
            new Vec2(+getWidth() / 2, -getHeight() / 2),
            new Vec2(+getWidth() / 2, +getHeight() / 2),
            new Vec2(-getWidth() / 2, +getHeight() / 2)};
    private Vec2[] shapeCorners_Rotated = new Vec2[4];

    Orient defaultOrient = new Orient(new Vec2(1F, 0F), 0);
    Orient orient = defaultOrient.copy();

    public void test() { Print.yellow("orient: " + orient.theta); }

    private Actor actor;
    private boolean ballistic = true;
    private LinkedList<Command> commandQueue = new LinkedList<>();
    private HashMap<Integer, Operation>[] keyCombos
            = new HashMap[OpContext.values().length];
    private Style style = Style.DEFAULT;
    private Operation currentOp;

    Weapon(float xPos, float yPos, float width, float height)
    {
        super(xPos, yPos, width, height);

        for (int i = 0; i < keyCombos.length; i++)
        { keyCombos[i] = new HashMap<>(); }

        for (int i = 0; i < shapeCorners_Rotated.length; i++)
        { shapeCorners_Rotated[i] = shapeCorners_notRotated[i].clone(); }
    }

    @Override
    protected void update(ArrayList<Entity> entities, float deltaSec)
    {
        if (ballistic)
        {
            super.update(entities, deltaSec);
            updateCorners();
        }
        else if (currentOp != null)
        {
            boolean operationDone = currentOp.run(deltaSec);
            if (operationDone)
            {
                currentOp = null;
            }

            if (!commandQueue.isEmpty() && (operationDone || currentOp.mayInterrupt()))
            {
                currentOp = getOperation(commandQueue.remove(), currentOp);
                currentOp.start(actor.getWeaponFace());
            }
        }
        else if (!commandQueue.isEmpty())
        {
            currentOp = getOperation(commandQueue.remove(), null);
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
                    * (currentOp == null ? dir.getHoriz().getSign()
                    : currentOp.getDir().getHoriz().getSign()),
                    getPosition().y + dims.y * orient.getY());
        }

        if (dirFace != dir && currentOp == null)
        {
            setTheta(orient.getTheta(), dir);
            dirFace = dir;
        }
    }
    public void updatePosition(Vec2 p, Vec2 dims, DirEnum dir)
    {
        setPosition(p);
        updateCorners(dims, dir);
    }

    abstract Operation getOperation(Command command, Operation currentOp);

    public void addCommand(Command command)
    {
        if (commandQueue.size() < actor.getMaxCommandChain())
        {
            commandQueue.addLast(command);
        }
    }

    public void releaseCommand (int attackKey)
    {
        if (currentOp != null)
        {
            currentOp.letGo(attackKey);
        }
        for (Command cm : commandQueue) { cm.letGo(attackKey); }
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

    public enum OpContext
    {
        STANDARD { int ID() { return 0; } },
        LUNGE { int ID() { return 1; } },
        LOW { int ID() { return 2; } },
        FREE { int ID() { return 3; } };
        int ID() { return -1; }
    }

    public Weapon equip(Actor actor)
    {
        this.actor = actor;
        ballistic = false;
        return this;
    }
    public Weapon unequip(float theta, Vec2 posOffset)
    {
        actor = null;
        ballistic = true;
        setTheta(0, DirEnum.NONE);
        setPosition(getPosition().add(shapeCornersOffset));
        updateCorners(new Vec2(0, 0), DirEnum.NONE);
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

    interface Operation
    {
        String getName();

        DirEnum getDir();

        void start(DirEnum direction);

        /** Returns true if the operation finished */
        boolean run(float deltaSec);

        boolean mayInterrupt();

        void letGo(int attackKey);

        enum State { WARMUP, EXECUTION, COOLDOWN, COUNTERED }
    }

    void setOperation(Operation op, int[] keyCombo, OpContext... status)
    {
        for (OpContext s : status)
        {
            for (int k : keyCombo)
            {
                keyCombos[s.ID()].put(k, op);
            }
        }
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
    }

    class StatusAppCycle
    {
        StatusApp[] statusApps = new StatusApp[3];
        StatusAppCycle(StatusApp start, StatusApp run, StatusApp finish)
        {
            statusApps[0] = start;
            statusApps[1] = run;
            statusApps[2] = finish;
        }

        void applyStart() { apply(0); }
        void applyRun() { apply(1); }
        void applyFinish() { apply(2); }
        private void apply(int step)
        {
            if (statusApps[step] != null) statusApps[step].apply(actor);
        }
    }
    class StatusApp
    {
        Actor.Status[] status;
        float time;

        StatusApp(float time, Actor.Status... status)
        {
            this.status = status;
            this.time = time;
        }

        void apply(Actor actor)
        {
            for (Actor.Status status : this.status)
                actor.addStatus(time, status);
        }
    }

    private float reduceTheta(float theta)
    {
        while (theta < 0) { theta += Math.PI * 2; }
        while (theta >= Math.PI * 2) { theta -= Math.PI * 2; }
        return theta;
    }

    class BasicMelee implements Operation
    {
        Journey[] warmJourney, coolJourney;
        int hau = 0;
        ArrayList<Tick>[] execJourney;

        StatusAppCycle statusAppCycle;

        BasicMelee(float warmupTime, float cooldownTime,
                   StatusAppCycle statusAppCycle,
                   ArrayList<Tick>... execJourney)
        {
            this.execJourney = execJourney;

            warmJourney = new Journey[execJourney.length];
            coolJourney = new Journey[execJourney.length];
            for (int i = 0; i < execJourney.length; i++)
            {
                warmJourney[i] = new Journey(defaultOrient,
                        execJourney[i].get(0).getOrient(), warmupTime);
                coolJourney[i] = new Journey(
                        execJourney[i].get(execJourney[i].size() - 1).getOrient(),
                        defaultOrient, cooldownTime);
            }
            this.statusAppCycle = statusAppCycle;
        }

        float totalSec = 0;
        DirEnum dir;
        State state = State.WARMUP;

        @Override
        public String getName() { return "basic_melee"; }

        @Override
        public DirEnum getDir() { return dir; }

        @Override
        public void start(DirEnum direction)
        {
            dir = direction;
            totalSec = 0;
            state = State.WARMUP;

            float hauDist = Float.MAX_VALUE;
            for (int i = 0; i < warmJourney.length; i++)
            {
                float currDist = warmJourney[i].setStart(orient);
                if (currDist < hauDist)
                {
                    hauDist = currDist;
                    hau = i;
                }
            }

            statusAppCycle.applyStart();

            Print.blue("Operating " + getName() + " using " + getStyle()
                    + " as " + hau);
        }

        @Override
        public boolean run(float deltaSec)
        {
            statusAppCycle.applyRun();

            totalSec += deltaSec;

            if (state == State.WARMUP)
            {
                if (warmJourney[hau].check(totalSec, dir))
                {
                    totalSec = 0;
                    state = State.EXECUTION;
                }
                return false;
            }
            else if (state == State.EXECUTION)
            {
                for (Tick tick : execJourney[hau])
                {
                    if (tick.check(totalSec, dir)) return false;
                }
                totalSec = 0;
                state = State.COOLDOWN;
                return false;
            }
            else if (state == State.COOLDOWN)
            {
                if (!coolJourney[hau].check(totalSec, dir))
                {
                    return false;
                }
            }

            totalSec = 0;
            state = State.WARMUP;

            statusAppCycle.applyFinish();
            return true;
        }

        @Override
        public boolean mayInterrupt()
        {
            if (state == State.EXECUTION) return false;

            statusAppCycle.applyFinish();
            return true;
        }

        @Override
        public void letGo(int attackKey) { }
    }
}