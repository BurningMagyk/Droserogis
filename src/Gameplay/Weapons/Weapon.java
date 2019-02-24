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

    private Orient defaultOrient = getDefaultOrient();
    private Orient orient = defaultOrient.copy();

    public void test() { Print.yellow("orient: " + orient.theta); }

    private Actor actor;
    private boolean ballistic = true;
    private LinkedList<Command> commandQueue = new LinkedList<>();
    private Operation currentOp;

    abstract Orient getDefaultOrient();
    Weapon(float xPos, float yPos, float width, float height)
    {
        super(xPos, yPos, width, height);

        //defaultOrient = getDefaultOrient();// = new Orient(new Vec2(1F, 0F), 0);
        //Print.yellow(getDefaultOrient() == null);
        //orient = defaultOrient.copy();

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

            if (!commandQueue.isEmpty() && (operationDone || currentOp.mayInterrupt(commandQueue.peek())))
            {
                Command nextCommand = commandQueue.remove().setStats(actor.getState(), actor.getVelocity());
                currentOp = getOperation(nextCommand, currentOp);
                if (currentOp != null)
                    currentOp.start();
            }
        }
        else if (!commandQueue.isEmpty())
        {
            Command nextCommand = commandQueue.remove().setStats(actor.getState(), actor.getVelocity());
            currentOp = getOperation(nextCommand, null);
            if (currentOp != null)
                currentOp.start();
        }
    }

    public void update(ArrayList<Item> items)
    {
        if (currentOp == null || !currentOp.mayApply()) return;
        for (Item other : items) { currentOp.apply(other); }
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

    Operation setOperation(Operation operation, Command command)
    {
        operation.setCommand(command);
        return operation;
    }
    abstract Operation getOperation(Command command, Operation currentOp);
    abstract boolean isApplicable(Command command);

    /** Called from Actor */
    public boolean addCommand(Command command)
    {
        if (commandQueue.size() < actor.getMaxCommandChain() && isApplicable(command))
        {
            commandQueue.addLast(command);
            return true;
        }
        return false;
    }

    /** Called from Actor */
    public void releaseCommand(int attackKey)
    {
        if (currentOp != null)
        {
            currentOp.letGo(attackKey);
        }
        for (Command cm : commandQueue)
        {
            cm.letGo(attackKey);
        }
    }

    public Weapon equip(Actor actor)
    {
        setTheta(defaultOrient.getTheta(), actor.getWeaponFace());
        orient.set(defaultOrient.copy());

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

    interface Operation
    {
        String getName();

        DirEnum getDir();

        void setCommand(Command command);

        void start();

        /** Returns true if the operation finished */
        boolean run(float deltaSec);

        boolean mayInterrupt(Command next);

        boolean mayApply();

        void letGo(int attackKey);

        void apply(Item other);

        enum State { WARMUP, EXECUTION, COOLDOWN, COUNTERED }
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
        private float totalTime;

        Journey(Orient start, Orient end, float totalTime)
        {
            end._reduceTheta();
            this.end = end;
            this.totalTime = totalTime;
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

            /*float distanceMagnitude = distance.getMagnitude();
            //totalTime = (float) Math.log(100 * timeMod * distanceMagnitude) / (float) Math.log(1.5) / 40;
            totalTime = timeMod * distanceMagnitude;
            return distance.getMagnitude();*/
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

    class ConditionAppCycle
    {
        ConditionApp[] conditionApps = new ConditionApp[3];
        ConditionAppCycle(ConditionApp start, ConditionApp run, ConditionApp finish)
        {
            conditionApps[0] = start;
            conditionApps[1] = run;
            conditionApps[2] = finish;
        }

        void applyStart() { apply(0); }
        void applyRun() { apply(1); }
        void applyFinish() { apply(2); }
        private void apply(int step)
        {
            if (conditionApps[step] != null) conditionApps[step].apply(actor);
        }
    }
    class ConditionApp
    {
        Actor.Condition[] conditions;
        float time;

        ConditionApp(float time, Actor.Condition... conditions)
        {
            this.conditions = conditions;
            this.time = time;
        }

        void apply(Actor actor)
        {
            for (Actor.Condition condition : conditions)
                actor.addCondition(time, condition);
        }
    }

    private float reduceTheta(float theta)
    {
        while (theta < 0) { theta += Math.PI * 2; }
        while (theta >= Math.PI * 2) { theta -= Math.PI * 2; }
        return theta;
    }

    class Melee implements Operation
    {
        Journey warmJourney, coolJourney;
        ArrayList<Tick> execJourney;

        ConditionAppCycle conditionAppCycle;

        Melee(float warmupTime, float cooldownTime,
              ConditionAppCycle statusAppCycle,
              ArrayList<Tick> execJourney)
        {
            this.execJourney = execJourney;

            warmJourney = new Journey(defaultOrient,
                    execJourney.get(0).getOrient(), warmupTime);
            coolJourney = new Journey(
                    execJourney.get(execJourney.size() - 1).getOrient(),
                    defaultOrient, cooldownTime);
            this.conditionAppCycle = statusAppCycle;
        }

        float totalSec = 0;
        Command command;
        State state = State.WARMUP;

        @Override
        public String getName() { return "melee"; }

        @Override
        public DirEnum getDir() { return command.FACE; }

        @Override
        public void setCommand(Command command) { this.command = command; }

        @Override
        public void start()
        {
            totalSec = 0;
            state = State.WARMUP;

            warmJourney.setStart(orient);

            conditionAppCycle.applyStart();

            Print.blue("Operating " + getName());
        }

        @Override
        public boolean run(float deltaSec)
        {
            conditionAppCycle.applyRun();

            totalSec += deltaSec;

            if (state == State.WARMUP)
            {
                if (warmJourney.check(totalSec, command.FACE))
                {
                    totalSec = 0;
                    state = State.EXECUTION;
                }
                return false;
            }
            else if (state == State.EXECUTION)
            {
                for (Tick tick : execJourney)
                {
                    if (tick.check(totalSec, command.FACE)) return false;
                }
                totalSec = 0;
                state = State.COOLDOWN;
                return false;
            }
            else if (state == State.COOLDOWN)
            {
                if (!coolJourney.check(totalSec, command.FACE))
                {
                    return false;
                }
            }

            totalSec = 0;
            state = State.WARMUP;

            conditionAppCycle.applyFinish();
            return true;
        }

        @Override
        public boolean mayInterrupt(Command check)
        {
            return state != State.EXECUTION;
        }

        @Override
        public boolean mayApply() { return state == State.EXECUTION; }

        @Override
        public void letGo(int attackKey)
        {
            command.letGo(attackKey);
        }

        @Override
        public void apply(Item other)
        {
            Print.yellow(getName() + ".apply(" + other + ")");
        }
    }

    class HoldableMelee extends Melee
    {
        HoldableMelee(float warmupTime, float cooldownTime,
               ConditionAppCycle statusAppCycle, ArrayList<Tick> execJourney)
        {
            super(warmupTime, cooldownTime, statusAppCycle, execJourney);
        }

        @Override
        public String getName() { return "thrust"; }

        boolean erected = false;

        @Override
        public void start()
        {
            super.start();
            erected = false;
        }

        @Override
        public boolean run(float deltaSec)
        {
            if (!erected) totalSec += deltaSec;
            conditionAppCycle.applyRun();

            if (state == State.WARMUP)
            {
                erected = false;
                if (warmJourney.check(totalSec, command.FACE))
                {
                    totalSec = 0;
                    state = State.EXECUTION;
                }
                return false;
            }
            else if (state == State.EXECUTION)
            {
                //Print.blue("erected: " + erected + ", isLetGo: " + isLetGo);
                for (Tick tick : execJourney)
                {
                    if (tick.check(totalSec, command.FACE)) return false;
                }
                if (!command.hold)
                {
                    totalSec = 0;
                    state = State.COOLDOWN;
                    command.hold = true;
                }
                erected = true;
                return false;
            }
            else if (state == State.COOLDOWN)
            {
                erected = false;
                if (!coolJourney.check(totalSec, command.FACE))
                {
                    return false;
                }
            }

            totalSec = 0;
            state = State.WARMUP;

            conditionAppCycle.applyFinish();
            return true;
        }
    }
}