package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Gameplay.Entity;
import Gameplay.Item;
import Util.Print;
import Util.Vec2;
import javafx.scene.paint.Color;

import java.util.*;

import static Util.PolygonIntersection.isIntersect;

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
    Operation currentOp;

    @Override
    public Color getColor()
    {
        if (actor != null)
        {
            if (actor.has(Actor.Condition.NEGATE_ATTACK) || actor.has(Actor.Condition.NEGATE_BLOCK))
                return Color.CYAN;
        }
        return Color.BLACK;
    }

    abstract Orient getDefaultOrient();
    Weapon(float xPos, float yPos, float width, float height)
    {
        super(xPos, yPos, width, height);

        for (int i = 0; i < shapeCorners_Rotated.length; i++)
        { shapeCorners_Rotated[i] = shapeCorners_notRotated[i].clone(); }
    }

    @Override
    protected void update(ArrayList<Entity> entities, float deltaSec)
    {
        applyInflictions();
        if (ballistic)
        {
            super.update(entities, deltaSec);
            updateCorners();
        }
        else if (disrupted)
        {
            collidedItems.clear();
            clearInflictionsDealt();
            commandQueue.clear();
            currentOp = null;
            disrupted = false;
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
                Command nextCommand = commandQueue.peek().setStats(actor.getState(), actor.getVelocity());
                Operation nextOp = getOperation(nextCommand, currentOp);
                if (nextOp != currentOp)
                {
                    collidedItems.clear();
                    clearInflictionsDealt();
                    commandQueue.remove();
                    currentOp = nextOp;
                    if (currentOp != null) currentOp.start();
                }
            }
        }
        else if (!commandQueue.isEmpty())
        {
            Command nextCommand = commandQueue.remove().setStats(actor.getState(), actor.getVelocity());
            currentOp = getOperation(nextCommand, null);
            if (currentOp != null) currentOp.start();
        }
    }

    public void update(ArrayList<Item> items)
    {
        if (currentOp == null || !currentOp.mayApply()) return;
        for (Item other : items) { currentOp.apply(this, other); }
        currentOp.apply(this,null);
        /* The null parameter is if an operation wants to modify its
         * collidedItems list every frame. */
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

    Operation setOperation(Operation operation, Command command)
    {
        operation.setCommand(command);
        return operation;
    }
    Operation setOperation(Melee operation, Command command, boolean skipWarmup)
    {
        operation.setCommand(command);
        operation.boostWarmup(skipWarmup);
        return operation;
    }
    Operation setOperation(Melee operation, Command command, float boostSec)
    {
        operation.setCommand(command);
        operation.boostWarmup(boostSec);
        return operation;
    }
    boolean isOperating() { return currentOp != null; }
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

    ArrayList<Item> collidedItems = new ArrayList<>();
    ArrayList<Infliction> inflictionsDealt = new ArrayList<>();
    private void clearInflictionsDealt()
    {
        for (Infliction inf : inflictionsDealt) { inf.finish(); }
        inflictionsDealt.clear();
    }

    private boolean disrupted = false;
    public void disrupt()
    {
        disrupted = true;
        orient.set(defaultOrient.copy());
    }
    void disrupt(float mag)
    {
        // TODO: replace mag with static values
        disrupt();
        if (actor != null) actor.stagger(mag);
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

        void apply(Weapon _this, Item other);

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
        private Actor.Condition[] conditions;
        float time;

        ConditionApp(float time, Actor.Condition... conditions)
        {
            this.conditions = conditions;
            this.time = time;
        }
        ConditionApp(ConditionApp app, Actor.Condition... addedConditions)
        {
            conditions = new Actor.Condition[addedConditions.length + app.conditions.length];
            System.arraycopy(app.conditions, 0, conditions, 0, app.conditions.length);
            System.arraycopy(addedConditions, 0, conditions, app.conditions.length, conditions.length - app.conditions.length);
            time = app.time;
        }
        ConditionApp(ConditionApp app, float newTime)
        {
            conditions = app.conditions;
            time = newTime;
        }
        ConditionApp add(ConditionApp app) { return new ConditionApp(app, conditions); }
        ConditionApp lengthen(float newTime) { return new ConditionApp(this, newTime); }

        void apply(Actor actor) // TODO: Actor should determine what percentage of time the condition should be applied
        {
            for (Actor.Condition condition : conditions)
                actor.addCondition(time, condition);
        }
    }

    ConditionApp FORCE_STAND = new ConditionApp(0.1F, Actor.Condition.FORCE_STAND);
    //ConditionApp forceStand_long = new ConditionApp(forceStand, 0.4F);
    ConditionApp FORCE_CROUCH = new ConditionApp(0.1F, Actor.Condition.FORCE_CROUCH);
    ConditionApp FORCE_DASH = new ConditionApp(0.01F, Actor.Condition.DASH);

    ConditionApp NEGATE_RUN = new ConditionApp(0.01F, Actor.Condition.NEGATE_RUN_LEFT, Actor.Condition.NEGATE_RUN_RIGHT);
    //ConditionApp negateRun_forceStand = new ConditionApp(negateRun, Actor.Condition.FORCE_STAND);
    //ConditionApp negateRun_forceCrouch = new ConditionApp(negateRun, Actor.Condition.FORCE_CROUCH);
    ConditionApp NEGATE_WALK = new ConditionApp(0.01F, Actor.Condition.NEGATE_WALK_LEFT, Actor.Condition.NEGATE_WALK_RIGHT);
    //ConditionApp negateWalk_long = new ConditionApp(negateWalk, 0.4F);
    //ConditionApp negateWalk_forceStand = new ConditionApp(negateWalk, Actor.Condition.FORCE_STAND);
    //ConditionApp negateWalk_forceStand_long = new ConditionApp(negateWalk_forceStand, 0.4F);
    //ConditionApp negateWalk_forceCrouch = new ConditionApp(negateWalk, Actor.Condition.FORCE_CROUCH);

    private float reduceTheta(float theta)
    {
        while (theta < 0) { theta += Math.PI * 2; }
        while (theta >= Math.PI * 2) { theta -= Math.PI * 2; }
        return theta;
    }

    static final int DURING_WARMUP = -2, DURING_EXECUTION = -1, DURING_COOLDOWN = 0;
    abstract class Melee implements Operation
    {
        String name;

        Journey warmJourney, coolJourney;
        Tick[] execJourney;

        ArrayList<Integer> interruptConditions = new ArrayList<>();

        ConditionAppCycle conditionAppCycle;
        ConditionApp conditionAppInfliction;

        Melee(String name,
              Vec2 waits,
              DirEnum functionalDir,
              boolean useDirHorizFunctionally,
              int[] interruptConditions,
              ConditionAppCycle conditionAppCycle, ConditionApp conditionAppInflicion,
              Tick[] execJourney)
        {
            this.name = name;
            this.execJourney = execJourney;
            for (int cond : interruptConditions) { this.interruptConditions.add(cond); }

            float warmupTime = waits.x, cooldownTime = waits.y;
            warmJourney = new Journey(defaultOrient,
                    execJourney[0].getOrient(), warmupTime);
            coolJourney = new Journey(
                    execJourney[execJourney.length - 1].getOrient(),
                    defaultOrient, cooldownTime);
            this.functionalDir = functionalDir;
            this.useDirHorizFunctionally = useDirHorizFunctionally;
            this.conditionAppCycle = conditionAppCycle;
            this.conditionAppInfliction = conditionAppInflicion == null ? new ConditionApp(0) : conditionAppInflicion;
        }
        Melee(String name,
              Vec2 waits,
              DirEnum functionalDir,
              boolean useDirHorizFunctionally,
              int[] interruptConditions,
              ConditionAppCycle conditionAppCycle, ConditionApp conditionAppInflicion,
              Tick[] execJourney,
              Tick customWarmPos)
        {
            this(name, waits, functionalDir, useDirHorizFunctionally, interruptConditions, conditionAppCycle, conditionAppInflicion, execJourney);
            /* For kicking, the warm-up needs to start in a different spot */
            warmJourney = new Journey(customWarmPos.getOrient(),
                    execJourney[0].getOrient(), waits.x);
        }

        boolean warmBoost = false, warmSkip = false;
        float totalSec = 0, warmBoostSec = 0;
        Command command;
        State state = State.WARMUP;

        @Override
        public String getName() { return name; }

        @Override
        public DirEnum getDir() { return command.FACE; }

        @Override
        public void setCommand(Command command)
        {
            this.command = command;
            boostWarmup(0);
            boostWarmup(false);
        }

        @Override
        public void start()
        {
            state = warmSkip ? State.EXECUTION : State.WARMUP;

            totalSec = warmBoostSec;
            warmBoostSec = 0;

            warmJourney.setStart(orient);

            Print.blue("Operating " + getName());
        }

        @Override
        public boolean run(float deltaSec)
        {
            totalSec += deltaSec;

            if (state == State.WARMUP)
            {
                conditionAppCycle.applyStart();

                if (warmBoost) totalSec += deltaSec;
                if (warmJourney.check(totalSec, command.FACE))
                {
                    totalSec = 0;
                    state = State.EXECUTION;
                }
                return false;
            }
            else if (state == State.EXECUTION)
            {
                conditionAppCycle.applyRun();

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
                conditionAppCycle.applyFinish();

                if (!coolJourney.check(totalSec, command.FACE))
                {
                    return false;
                }
            }

            totalSec = 0;
            state = State.WARMUP;

            collidedItems.clear();
            clearInflictionsDealt();
            return true;
        }

        @Override
        public boolean mayInterrupt(Command check)
        {
            if (interruptConditions.isEmpty()) return false;
            if (state == State.WARMUP)
            {
                if (interruptConditions.contains(DURING_WARMUP)) return true;
                return interruptConditions.contains(check.ATTACK_KEY);
            }
            if (state == State.EXECUTION && interruptConditions.contains(DURING_EXECUTION)) return true;
            if (state == State.COOLDOWN)
            {
                if (interruptConditions.contains(DURING_COOLDOWN)) return true;
                return interruptConditions.contains(check.ATTACK_KEY);
            }
            return false;
        }

        @Override
        public boolean mayApply() { return state == State.EXECUTION; }

        @Override
        public void letGo(int attackKey) { command.letGo(attackKey); }

        DirEnum functionalDir;
        boolean useDirHorizFunctionally;

        @Override
        public void apply(Weapon _this, Item other)
        {
            if (other == null || other == _this || other == actor) return;
            if (collidedItems.contains(other)) return;

            Item target;

            /* If it's a weapon that's being wielded */
            if (other instanceof Weapon && !((Weapon) other).isBallistic())
            {
                if (!isIntersect(getShapeCorners(),
                        ((Weapon) other).getShapeCorners())) return;
                target = ((Weapon) other).actor;
            }

            /* If it's an actor, non-weapon item, or weapon that isn't being wielded */
            else
            {
                if (!isIntersect(getShapeCorners(), other)) return;
                target = other;
            }

            DirEnum dir = useDirHorizFunctionally
                    ? getDir().getHoriz().add(functionalDir) : functionalDir;
            if (dir.getCollisionPos(_this, target))
            {
                collidedItems.add(other);
                Infliction infliction = new Infliction(_this, this, actor, dir, 1, conditionAppInfliction);
                inflictionsDealt.add(infliction);
                other.inflict(infliction);
                Print.blue(" by " + this);
            }
        }

        void boostWarmup(boolean skip) { warmSkip = skip; }
        void boostWarmup(float boostSec) { warmBoostSec = boostSec; }
    }

    abstract class HoldableMelee extends Melee
    {
        HoldableMelee(String name, Vec2 waits, DirEnum functionalDir,
                      boolean useDirHorizFunctionally, int[] interruptConditions,
                      ConditionAppCycle conditionAppCycle, ConditionApp conditionAppInfliction,
                      Tick[] execJourney)
        {
            super(name, waits, functionalDir, useDirHorizFunctionally, interruptConditions,
                    conditionAppCycle, conditionAppInfliction, execJourney);
        }
        HoldableMelee(String name, Vec2 waits, DirEnum functionalDir,
                      boolean useDirHorizFunctionally, int[] interruptConditions,
                      ConditionAppCycle conditionAppCycle, ConditionApp conditionAppInfliction,
                      Tick[] execJourney, Tick customWarmPos)
        {
            super(name, waits, functionalDir, useDirHorizFunctionally, interruptConditions,
                    conditionAppCycle, conditionAppInfliction, execJourney, customWarmPos);
        }

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

            if (state == State.WARMUP)
            {
                conditionAppCycle.applyStart();

                if (warmBoost) totalSec += deltaSec;
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
                conditionAppCycle.applyRun();

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
                conditionAppCycle.applyFinish();

                erected = false;
                if (!coolJourney.check(totalSec, command.FACE))
                {
                    return false;
                }
            }

            totalSec = 0;
            state = State.WARMUP;

            collidedItems.clear();
            clearInflictionsDealt();
            return true;
        }
    }

    abstract class Rush implements Operation
    {
        ConditionAppCycle conditionAppCycle;
        ConditionApp conditionAppInfliction;
        float warmupTime, cooldownTime, execTime;

        Rush(Vec2 waits, float execTime,
                 DirEnum functionalDir, boolean useDirHorizFunctionally,
                 ConditionAppCycle conditionAppCycle, ConditionApp conditionAppInfliction)
        {
            warmupTime = waits.x; cooldownTime = waits.y;
            this.execTime = execTime;
            this.functionalDir = functionalDir;
            this.useDirHorizFunctionally = useDirHorizFunctionally;
            this.conditionAppCycle = conditionAppCycle;
            this.conditionAppInfliction = conditionAppInfliction == null ? new ConditionApp(0) : conditionAppInfliction;
        }

        float totalSec = 0;
        Command command;
        State state = State.WARMUP;

        @Override
        public String getName() { return "personal contact"; }

        @Override
        public DirEnum getDir() { return command.FACE; }

        @Override
        public void setCommand(Command command) { this.command = command; }

        @Override
        public void start()
        {
            totalSec = 0;
            state = State.WARMUP;

            Print.blue("Operating " + getName());
        }

        @Override
        public boolean run(float deltaSec)
        {
            totalSec += deltaSec;

            if (state == State.WARMUP)
            {
                conditionAppCycle.applyStart();

                if (totalSec >= warmupTime)
                {
                    totalSec = 0;
                    state = State.EXECUTION;
                }
                return false;
            }
            else if (state == State.EXECUTION)
            {
                conditionAppCycle.applyRun();

                if (execTime > 0 && totalSec >= execTime)
                {
                    totalSec = 0;
                    state = State.COOLDOWN;
                }
                return false;
            }
            else if (state == State.COOLDOWN)
            {
                conditionAppCycle.applyFinish();

                if (totalSec < cooldownTime)
                {
                    return false;
                }
            }

            totalSec = 0;
            state = State.WARMUP;

            collidedItems.clear();
            clearInflictionsDealt();
            return true;
        }

        @Override
        public boolean mayInterrupt(Command next) { return state != State.EXECUTION; }

        @Override
        public boolean mayApply() { return state == State.EXECUTION; }

        @Override
        public void letGo(int attackKey) { command.letGo(attackKey); }

        DirEnum functionalDir;
        boolean useDirHorizFunctionally;

        @Override
        public void apply(Weapon _this, Item other)
        {
            if (other == null || other == actor) return;
            for (Weapon weapon : actor.weapons) { if (other == weapon) return; }
            if (other instanceof Weapon && !((Weapon) other).isOperating()) return;
            if (collidedItems.contains(other) || !withinBounds(other)) return;

            DirEnum dir = useDirHorizFunctionally
                    ? getDir().getHoriz().add(functionalDir) : functionalDir;

            float collisionSpeed = dir.getCollisionSpeed(_this, other);
            if (collisionSpeed > 0)
            {
                collidedItems.add(other);
                Infliction infliction = new Infliction(_this, this, actor, dir, 1, conditionAppInfliction);
                inflictionsDealt.add(infliction);
                other.inflict(infliction);
                Print.yellow(" by " + this);
            }
        }
    }

    abstract class HoldableRush extends Rush
    {
        HoldableRush(Vec2 waits,
                         float minExecTime, float maxExecTime,
                         DirEnum functionalDir, boolean useDirHorizFunctionally,
                         ConditionAppCycle conditionAppCycle, ConditionApp conditionAppInfliction)
        {
            super(waits, minExecTime, functionalDir, useDirHorizFunctionally, conditionAppCycle, conditionAppInfliction);
            this.minExecTime = minExecTime;
            this.maxExecTime = maxExecTime;
        }

        boolean minDone = false;
        float minExecTime, maxExecTime;

        @Override
        public void start()
        {
            super.start();
            minDone = false;
        }

        @Override
        public boolean run(float deltaSec)
        {
            totalSec += deltaSec;

            if (state == State.WARMUP)
            {
                conditionAppCycle.applyStart();

                if (totalSec >= warmupTime)
                {
                    totalSec = 0;
                    state = State.EXECUTION;
                }
                return false;
            }
            else if (state == State.EXECUTION)
            {
                conditionAppCycle.applyRun();

                if (totalSec < minExecTime) { return false; }

                if (!command.hold || (maxExecTime > 0 && totalSec >= maxExecTime))
                {
                    totalSec = 0;
                    state = State.COOLDOWN;
                    command.hold = true;
                }
                return false;
            }
            else if (state == State.COOLDOWN)
            {
                conditionAppCycle.applyFinish();

                if (totalSec < cooldownTime)
                {
                    return false;
                }
            }

            totalSec = 0;
            state = State.WARMUP;

            collidedItems.clear();
            clearInflictionsDealt();
            return true;
        }
    }

    public abstract boolean isNatural();
    abstract boolean clash(Weapon otherWeapon, Operation otherOp);

    @Override
    protected void applyInflictions()
    {
        if (inflictions.isEmpty()) return;

        if (actor != null && isNatural())
        {
            for (int i = 0; i < inflictions.size(); i++)
            {
                Infliction inf = inflictions.get(i);
                if (inf.getDamage() != 0 && actor.hasSameInfliction(inf))
                    inf.cancelDamage();
            }
        }

        for (int i = 0; i < inflictions.size(); i++)
        {
            Infliction inf = inflictions.get(i);

            if (!inf.isResolved())
            {
                /* Infliction applied here */
                Print.yellow("Weapon: " + inf);

                if (inf.applyCondition(this))
                    inf.applyDamage(this);
                inf.resolve();
            }

            if (inf.isFinished())
            {
                inflictions.remove(inf);
                i--;
                //Print.yellow("Weapon: " + inf + " removed");
            }
        }
    }

    @Override
    public void damage(int amount)
    {
        if (isNatural()) Print.yellow("Actor: Dealt " + amount + " damage");
        else Print.yellow("Weapon: Dealt " + amount + " damage");
    }

    @Override
    public void inflict(Infliction infliction)
    {
        inflictions.add(infliction);
    }

    /*
     * (If two actors are equally powered)
     * Attacker is interrupted if hit
     * Thrust collide with swing -> thrust interrupted + thruster disabled
     * Thrust collide with thrust -> nothing
     * Swing collide with swing -> both interrupted + both disabled
     *
     * Swing collide with block -> swing interrupted + blocker staggered by 1
     * Thrust collide with block -> nothing
     *
     * (with shield - mode 1)
     * When swinging, shield only blocks swings
     * Can't thrust up
     *
     * (with shield - mode 2)
     * When swinging or thrusting, shield only blocks swings
     */
}