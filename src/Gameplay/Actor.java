package Gameplay;

import Gameplay.Characters.CharacterStat;
import Gameplay.Weapons.*;
import Gameplay.Weapons.Inflictions.ConditionApp;
import Gameplay.Weapons.Inflictions.Infliction;
import Util.GradeEnum;
import Util.Print;
import Util.Vec2;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Key Commands:
 *   W,A,S,D: move left, right, up, down
 *   J      : jump
 *   ESC    : exit game
 *   Arrow keys pan the camera. Q and E zoom in and out.
 *   Crouch : press down when on surface.
 *   When crouching, A and D: player crawl.
 *   If player jumps and holds down an arrow as he comes into contact with the wall, he'll stick to it.
 *   If player presses jump and presses one of the other movement keys, he'll jump off the wall.
 */
public class Actor extends Item
{
    public enum EnumType
    {
        Igon
                {
                    public float width()  {return 20 * SPRITE_TO_WORLD_SCALE;}
                    public float height() {return 40 * SPRITE_TO_WORLD_SCALE;}
                    public float mass() {return 1;}
                    public String[] spritePaths() {return new String[]{"super_neckbeard.png"};}
                    public CharacterStat createPlayerStat()
                    {
                        return new CharacterStat(
                                "C", "C", "C", "C", "C", "C", "C", "C", "C", "C", "C");
                    }
                    public WeaponStat createNaturalWeaponStat()
                    {
                        return new WeaponStat("C",
                                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR");
                    }
                    public float naturalWeaponMass() {return mass() * 0.1f;}
                },
        Lyra
                {
                    public float width()  {return 20 * SPRITE_TO_WORLD_SCALE;}
                    public float height() {return 40 * SPRITE_TO_WORLD_SCALE;}
                    public float mass() {return 1;}
                    public String[] spritePaths() {return null;}
                    public CharacterStat createPlayerStat()
                    {
                        return new CharacterStat(
                                "D-", "D", "B-", "C", "D+", "E", "C-", "C+", "C+", "D", "A-");
                    }
                    public WeaponStat createNaturalWeaponStat()
                    {
                        return new WeaponStat("C",
                                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR");
                    }
                    public float naturalWeaponMass() {return mass() * 0.1f;}
                },
        Zuzen
                {
                    public float width()  {return 18 * SPRITE_TO_WORLD_SCALE;}
                    public float height() {return 35 * SPRITE_TO_WORLD_SCALE;}
                    public float mass() {return 0.79f;}
                    public String[] spritePaths() {return null;}
                    public CharacterStat createPlayerStat()
                    {
                        return new CharacterStat(
                                "C", "C", "C", "C", "C", "C", "C", "C", "C", "C", "C");
                    }
                    public WeaponStat createNaturalWeaponStat()
                    {
                        return new WeaponStat("C",
                                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR");
                    }
                    public float naturalWeaponMass() {return mass() * 0.1f;}
                };


        public abstract float width();
        public abstract float height();
        public abstract float mass();
        public abstract String[] spritePaths();
        public abstract CharacterStat createPlayerStat();
        public abstract WeaponStat createNaturalWeaponStat();
        public abstract float naturalWeaponMass();
    }

    private EnumType actorType;

    private final float
            NORMAL_GRAVITY = gravity,
            REDUCED_GRAVITY = NORMAL_GRAVITY * 0.7F,
            WEAK_GRAVITY = NORMAL_GRAVITY * 0.1F,
            GREATER_GRAVITY = NORMAL_GRAVITY / 0.5F;

    private float NORMAL_FRICTION, GREATER_FRICTION, REDUCED_FRICTION;

    private CharacterStat charStat;

    // TODO: these values need to be balanced for MVP
    private float[] STAGGER_MAG_MOD = new float[] { 1.25F, 1.5F, 1.75F };

    /* The horizontal direction that the player intends to move towards */
    private int dirHoriz = -1;
    /* The horizontal direction that the player intends to face towards.
     * This does not need to keep track of vertical direction. */
    private int dirFace = RIGHT;
    /* The vertical direction that the player intents to move towards */
    private int dirVert = -1;

    private State state = State.FALL;

    private LateSurface[] touchLateSurface = new LateSurface[4];

    /* The only purpose of this class is for climbing stairs */
    private class PrevGround
    {
        Entity ground = null; float pos;
        float getTopEdge() { return ground.getTopEdge(pos); }
    }
    private PrevGround prevGround = new PrevGround();

    /* The only purpose of these is for camera orientation */
    private boolean fromWall = false;
    private float fromGround = getY();

    private boolean
            pressingLeft = false, pressingRight = false,
            pressingUp = false, pressingDown = false, pressingShift = false;
    // change to more when other attack buttons get implemented
    private boolean[] pressingAttack = new boolean[4];

    private enum WeaponSlot { NATURAL, SECONDARY, PRIMARY }
    public Weapon[] weapons = new Weapon[WeaponSlot.values().length];
    private float[] conditions = new float[Condition.values().length];
    private boolean[] conditionsB = new boolean[Condition.values().length];


    public Actor(float xPos, float yPos, EnumType type)
    {
        super(xPos, yPos, type.width(), type.height(), type.mass(), type.spritePaths());

        ORIGINAL_WIDTH = type.width();
        ORIGINAL_HEIGHT = type.height();

        this.actorType = type;
        this.charStat = type.createPlayerStat();
        System.out.println("Actor("+type+")"+this.charStat);
        setCharacterStats();

        //weapons[0] = new Natural(type.createNaturalWeaponStat(), charStat, xPos, yPos, 0.2F, 0.1F, type.mass() * 0.1F, this, null);
        //weapons[0] = new Natural(this, xPos, yPos);
        weapons[WeaponSlot.NATURAL.ordinal()] = new Weapon(getX(), getY(), 0.2F, 0.1F,
                type.mass() * 0.1F, new WeaponType(
                        new Vec2(0.75F, 0.25F), (float )Math.PI * 3 / 4), null);
        weapons[WeaponSlot.NATURAL.ordinal()].equip(this);
    }

    public EnumType getActorType() { return actorType;}

    @Override
    public Color getColor()
    {
        return state.getColor();
        /*if (has(Condition.NEGATE_ACTIVITY)) return Color.RED;
        if (has(Condition.NEGATE_STABILITY)) return Color.ORANGE;
        if (!canWalk()) return Color.GOLD;
        if (!canRun()) return Color.GREEN;
        return Color.CORNFLOWERBLUE;*/
    }

    public Weapon[] getWeapons()
    {
        return weapons;
    }

    public CharacterStat getCharacterStat() { return charStat;}

    protected void update(EntityCollection<Entity> entities, float deltaSec)
    {
        resetAcceleration();
        applyInflictions();
        act(deltaSec);
        applyPhysics(entities, deltaSec);
        countdownCondition(deltaSec);
    }
    protected void update(ArrayList<Item> items)
    {
        /* Does nothing maybe? */
    }

    /**
     * Called every frame to update the Actor's will.
     */
    private void act(float deltaSec)
    {
        float vx = getVelocityX(), vy = getVelocityY();

        /* Late surfaces acts wacky when not airborne. */
        if (pressedJumpTime > 0 && state.isAirborne())
        {
            if (touchLateSurface[DOWN] != null && touchLateSurface[DOWN].valid())
            {
                float lateVelY = touchLateSurface[DOWN].getLateVel().y;
                if (canJump()) {
                    setVelocityY(touchLateSurface[DOWN].getShape().getDirs()[UP]
                            ? -jumpVel + lateVelY : -jumpVel - slopeJumpBuffer + lateVelY);
                }
                pressedJumpTime = 0F;
            }
            else if (touchLateSurface[LEFT] != null && touchLateSurface[LEFT].valid())
            {
                Vec2 lateVel = touchLateSurface[LEFT].getLateVel();
                if (dirHoriz == RIGHT)
                {
                    if (canJump()) {
                        setVelocityX(jumpVel * 0.70712F + lateVel.x); // sin(45)
                        setVelocityY(-jumpVel * 0.70712F + lateVel.y); // cos(45)
                    }
                    pressedJumpTime = 0F;
                }
                else if (dirVert == UP)
                {
                    if (canJump()) {
                        setVelocityX(jumpVel * 0.34202F + lateVel.x); // sin(20)
                        setVelocityY(-jumpVel * 0.93969F + lateVel.y); // cos(20)
                    }
                    pressedJumpTime = 0F;
                }
                else if (dirVert == DOWN)
                {
                    if (canJump()) setVelocityX(jumpVel + lateVel.x);
                    pressedJumpTime = 0;
                }
            }
            else if (touchLateSurface[RIGHT] != null && touchLateSurface[RIGHT].valid())
            {
                Vec2 lateVel = touchLateSurface[RIGHT].getLateVel();
                if (dirHoriz == LEFT)
                {
                    if (canJump()) {
                        setVelocityX(-jumpVel * 0.70712F + lateVel.x); // sin(45)
                        setVelocityY(-jumpVel * 0.70712F + lateVel.y); // cos(45)
                    }
                    pressedJumpTime = 0F;
                }
                else if (dirVert == UP)
                {
                    if (canJump()) {
                        setVelocityX(-jumpVel * 0.34202F + lateVel.x); // sin(20)
                        setVelocityY(-jumpVel * 0.93969F + lateVel.y); // cos(20)
                    }
                    pressedJumpTime = 0F;
                }
                else if (dirVert == DOWN)
                {
                    if (canJump()) setVelocityX(-jumpVel + lateVel.x);
                    pressedJumpTime = 0F;
                }
            }
        }

        if (state.isGrounded())
        {
            /* If the entity being stood on is an upward-slope triangle */
            if (!touchEntity[DOWN].getShape().getDirs()[UP])
            {
                float slopeAccel = gravity * getMass();
                if (state == State.CROUCH || state == State.CRAWL) slopeAccel /= 2;
                setAcceleration(touchEntity[DOWN].applySlopeY(slopeAccel));
            }

            float accel, topSpeed;
            MoveType moveType = getMoveType();
            if (state.isLow())
            {
                accel = moveType == MoveType.STILL ? 0 : crawlAccel;
                topSpeed = getTopSpeed(moveType, true);
            }
            else
            {
                accel = moveType == MoveType.STILL ? 0 : runAccel;
                topSpeed = getTopSpeed(moveType,false);
            }

            if (dirHoriz == LEFT)
            {
                if (state == State.SLIDE) { if (vx > 0) addAccelerationX(-accel); }
                else if (vx > -topSpeed)
                {
                    addAccelerationX(-accel);
                    addVelocityX((float) -minThreshSpeed * 1.5F);
                }
                if (has(Condition.DASH) && getVelocityX() > -rushSpeed
                        && !has(Condition.NEGATE_WALK_LEFT) && !has(Condition.NEGATE_WALK_RIGHT))
                {
                    setVelocityX(-rushSpeed);
                    addCondition(dashRecoverTime, Condition.NEGATE_WALK_LEFT, Condition.NEGATE_WALK_RIGHT);
                }
            }
            else if (dirHoriz == RIGHT)
            {
                if (state == State.SLIDE) { if (vx < 0) addAccelerationX(accel); }
                else if (vx < topSpeed)
                {
                    addAccelerationX(accel);
                    addVelocityX((float) minThreshSpeed * 1.5F);
                }
                if (has(Condition.DASH) && getVelocityX() < rushSpeed
                        && !has(Condition.NEGATE_WALK_LEFT) && !has(Condition.NEGATE_WALK_RIGHT))
                {
                    setVelocityX(rushSpeed);
                    addCondition(dashRecoverTime, Condition.NEGATE_WALK_LEFT, Condition.NEGATE_WALK_RIGHT);
                }
            }

            /* Going up stairs */
            float stairTopEdge = touchEntity[DOWN].getTopEdge(prevGround.pos);
            if (prevGround.ground != null && prevGround.ground != touchEntity[DOWN]
                    && prevGround.getTopEdge() > stairTopEdge)
            {
                float diff = (prevGround.getTopEdge() - stairTopEdge) / getHeight();
                if (diff < stairRecoverTime[0])
                {
                    if (diff < stairRecoverTime[1])
                    {
                        addCondition(stairRecoverTime[2], Condition.NEGATE_SPRINT_LEFT, Condition.NEGATE_SPRINT_RIGHT);
                    }
                    else addCondition(stairRecoverTime[2], Condition.NEGATE_RUN_LEFT, Condition.NEGATE_RUN_RIGHT);
                }
                else addCondition(stairRecoverTime[2], Condition.NEGATE_RUN_LEFT, Condition.NEGATE_RUN_RIGHT, Condition.FORCE_CROUCH);
            }

            fromWall = false;
            fromGround = getY();

            if (pressedJumpTime > 0)
            {
                if (canJump()) {
                    addVelocityY(touchEntity[DOWN].getShape().getDirs()[UP]
                            ? -jumpVel : -jumpVel - slopeJumpBuffer);
                }
                pressedJumpSurface = touchEntity[DOWN];
                pressedJumpTime = 0F;
            }
        }

        else if (state.isAirborne())
        {
            suspendedMovement(vx, airSpeed, airAccel);
        }

        else if (state == State.SWIM)
        {
            suspendedMovement(vx, swimSpeed, swimAccel);
            if (dirVert == UP)
            {
                if (vy > -swimSpeed) addAccelerationY(-swimAccel);
            }
            else if (dirVert == DOWN)
            {
                if (vy < swimSpeed) addAccelerationY(swimAccel);
            }
        }

        else if (state.isOnWall())
        {
            if (pressedJumpTime > 0)
            {
                if (touchEntity[LEFT] != null)
                {
                    if (dirHoriz == RIGHT)
                    {
                        if (canJump()) {
                            addVelocityX(jumpVel * 0.70712F); // sin(45)
                            addToVelocityY(-jumpVel * 0.70712F, 2,false); // cos(45)
                            fromWall = true;
                        }
                        pressedJumpTime = 0F;
                    }
                    else if (dirVert == UP)
                    {
                        if (canJump()) {
                            addVelocityX(jumpVel * 0.34202F); // sin(20)
                            addToVelocityY(-jumpVel * 0.93969F, 2,false); // cos(20)
                            fromWall = true;
                        }
                        pressedJumpTime = 0F;
                    }
                    else if (dirVert == DOWN)
                    {
                        if (canJump())
                        {
                            addVelocityX(jumpVel);
                            fromWall = true;
                        }
                        pressedJumpTime = 0F;
                    }
                    pressedJumpSurface = touchEntity[LEFT];
                }
                else // if (touchEntity[RIGHT] != null)
                {
                    if (dirHoriz == LEFT)
                    {
                        if (canJump()) {
                            addVelocityX(-jumpVel * 0.70712F); // sin(45)
                            addToVelocityY(-jumpVel * 0.70712F, 2,false); // cos(45)
                            fromWall = true;
                        }
                        pressedJumpTime = 0F;
                    }
                    else if (dirVert == UP)
                    {
                        if (canJump()) {
                            addVelocityX(-jumpVel * 0.34202F); // sin(20)
                            addToVelocityY(-jumpVel * 0.93969F, 2,false); // cos(20)
                            fromWall = true;
                        }
                        pressedJumpTime = 0F;
                    }
                    else if (dirVert == DOWN)
                    {
                        if (canJump())
                        {
                            addVelocityX(-jumpVel);
                            fromWall = true;
                        }
                        pressedJumpTime = 0F;
                    }
                    pressedJumpSurface = touchEntity[RIGHT];
                }
            }

            float velY = getVelocityY();
            if (dirHoriz != -1
                    && (dirVert == UP || touchEntity[dirHoriz] != null)
                    && velY >= -maxClimbSpeed && velY <= maxStickSpeed)
            {
                addAccelerationY(-climbAccel * (velY > 0 && canRun() ? 5 : 1)); // TODO: decide this value (right now it's 5)

                /* Ledge-climbing */
                int _dirHoriz = -1;
                if (touchEntity[dirHoriz] != null) _dirHoriz = dirHoriz;
                else if (touchEntity[LEFT] != null && touchEntity[LEFT] instanceof Block) _dirHoriz = LEFT;
                else if (touchEntity[RIGHT] != null && touchEntity[RIGHT] instanceof Block) _dirHoriz = RIGHT;
                if (_dirHoriz != -1
                        && getPosition().y - (getHeight() / 2)
                        < touchEntity[_dirHoriz].getPosition().y - (touchEntity[_dirHoriz].getHeight() / 2)
                        && Math.abs(velY) < walkSpeed
                        && velY >= 0)
                {
                    addCondition(climbLedgeTime, Condition.NEGATE_STABILITY);
                    float xPos = touchEntity[_dirHoriz].getPosition().x
                            + ((touchEntity[_dirHoriz].getWidth() / 2) * (_dirHoriz == LEFT ? 1 : -1));
                    float yPos = touchEntity[_dirHoriz].getPosition().y
                            - (touchEntity[_dirHoriz].getHeight() / 2) - (getHeight() / 2);
                    setPosition(new Vec2(xPos, yPos));
                }
            }
        }

        if (pressedJumpTime > 0)
        {
            pressedJumpTime -= deltaSec;
            if (pressedJumpTime < 0) pressedJumpTime = 0F;
        }
        /* If pressedJumpTime is -1, that means the player let go of the
         * jump key while airborne and its effect on the player's movement
         * already occurred this frame. */
        else if (pressedJumpTime == -1)
        {
            pressedJumpTime = 0F;
            if (state == State.RISE) gravity = GREATER_GRAVITY;
        }

        /* Cap overall speed */
        if (getVelocityX() > maxTotalSpeed) setVelocityX(maxTotalSpeed);
        else if (getVelocityX() < -maxTotalSpeed) setVelocityX(-maxTotalSpeed);
        if (getVelocityY() > maxTotalSpeed) setVelocityY(maxTotalSpeed);
        else if (getVelocityY() < -maxTotalSpeed) setVelocityY(-maxTotalSpeed);

        /* When travelling on a ramp, they get reduced gravity */
        if (touchEntity[DOWN] != null
                && !touchEntity[DOWN].getShape().getDirs()[UP]
                && dirHoriz != -1) {
            //gravity = WEAK_GRAVITY;
            //gravity = REDUCED_GRAVITY;
        }
        /* When starting to fall, they lose reduced/strong gravity */
        else if (getVelocityY() >= 0)
        {
            /* If in water, the gravity is weak when still,
             * zero when swimming. */
            if (inWater){
                if (dirHoriz == -1 && dirVert == -1)
                    gravity = WEAK_GRAVITY;
                else gravity = 0;
            } else gravity = NORMAL_GRAVITY;
        }

        if (willTumble()) addCondition(minTumbleTime, Condition.NEGATE_ACTIVITY);

        if (has(Condition.NEGATE_ACTIVITY))
        {
            float condTime = conditions[Condition.NEGATE_ACTIVITY.ordinal()];
            addCondition(condTime, Condition.NEGATE_STABILITY);
            addCondition(condTime, Condition.NEGATE_ATTACK);
            addCondition(condTime, Condition.NEGATE_BLOCK);
            if (conditionsB[Condition.NEGATE_ACTIVITY.ordinal()])
            {
                addCondition(Condition.NEGATE_STABILITY);
                addCondition(Condition.NEGATE_ATTACK);
                addCondition(Condition.NEGATE_BLOCK);
            }
        }

        /* FORCE_CROUCH and NEGATE_WALK conditions must remain longer than NEGATE_STABILITY */
        if (has(Condition.NEGATE_STABILITY))
        {
            float condTime = conditions[Condition.NEGATE_STABILITY.ordinal()] + proneRecoverTime;
            addCondition(condTime, Condition.FORCE_CROUCH);
            addCondition(condTime, Condition.NEGATE_WALK_LEFT);
            addCondition(condTime, Condition.NEGATE_WALK_RIGHT);
        }

        prevGround.ground = touchEntity[DOWN];
        prevGround.pos = getX();
    }

    private float getTopSpeed(MoveType moveType, boolean low)
    {
        switch (moveType)
        {
            case WALK: return low ? crawlSpeed : walkSpeed;
            case RUN: return low ? crawlSpeed : runSpeed;
            case SPRINT: return low ? lowerSprintSpeed : sprintSpeed;
        }
        return 0;
    }
    private float getTopSpeed(boolean low) { return getTopSpeed(getMoveType(), low); }
    public float getTopSpeed() { return getTopSpeed(false); }

    private enum MoveType { STILL, WALK, RUN, SPRINT }
    private MoveType getMoveType()
    {
        if (!has(Condition.NEGATE_STABILITY) && !has(Condition.NEGATE_ACTIVITY))
        {
            if (dirHoriz == LEFT && !has(Condition.NEGATE_WALK_LEFT))
            {
                if (dirFace == dirHoriz && !has(Condition.NEGATE_RUN_LEFT))
                {
                    if (pressingShift && !has(Condition.NEGATE_SPRINT_LEFT))
                        return MoveType.SPRINT;
                    return MoveType.RUN;
                }
                return MoveType.WALK;
            }
            if (dirHoriz == RIGHT && !has(Condition.NEGATE_WALK_RIGHT))
            {
                if (dirFace == dirHoriz && !has(Condition.NEGATE_RUN_RIGHT))
                {
                    if (pressingShift && !has(Condition.NEGATE_SPRINT_RIGHT))
                        return MoveType.SPRINT;
                    return MoveType.RUN;
                }
                return MoveType.WALK;
            }
        }
        return MoveType.STILL;
    }

    private boolean canWalk() {
        return !has(Condition.NEGATE_WALK_LEFT)
                && !has(Condition.NEGATE_WALK_RIGHT);
    }
    private boolean canRun()
    {
        return canWalk()
                && !has(Condition.NEGATE_RUN_LEFT)
                && !has(Condition.NEGATE_RUN_RIGHT);
    }
    private boolean canJump()
    {
        return canWalk()
                && !has(Condition.NEGATE_ACTIVITY)
                && !has(Condition.NEGATE_STABILITY)
                && !has(Condition.FORCE_CROUCH);
    }

    public boolean[] getBlockRating()
    {
        boolean able = !has(Condition.NEGATE_ACTIVITY) && !has(Condition.NEGATE_BLOCK);
        boolean prone = has(Condition.NEGATE_STABILITY);
        boolean shield = false; // TODO: set to true if shield is equipped
        return new boolean[] { able, prone, pressingUp, shield };
    }

    void applyPhysics(EntityCollection<Entity> entities, float deltaSec)
    {
        boolean slopeLeft = false, slopeRight = false;
        if (touchEntity[DOWN] != null && touchEntity[DOWN].getShape().getDirs()[DOWN])
        {
            slopeLeft = !touchEntity[DOWN].getShape().getDirs()[LEFT];
            slopeRight = !touchEntity[DOWN].getShape().getDirs()[RIGHT];
        }
        applyAcceleration(getAcceleration(), deltaSec);
        Vec2 beforeDrag = applyAcceleration(determineDrag(), deltaSec);
        neutralizeVelocity(beforeDrag, slopeLeft, slopeRight);
        Vec2 beforeFriction = applyAcceleration(determineFriction(), deltaSec);
        neutralizeVelocity(beforeFriction, slopeLeft, slopeRight);
        Vec2 contactVelocity = applyVelocity(deltaSec, entities);
        if (setState(determineState()) && contactVelocity != null)
            addVelocityY(-Math.abs(contactVelocity.x));
    }

    /* Used for airborne and swimming, horizontal */
    private void suspendedMovement(float vx, float topAirSpeed, float airAccel) {
        if (dirHoriz == LEFT)
        {
            if (vx > -topAirSpeed) addAccelerationX(-airAccel);
        }
        else if (dirHoriz == RIGHT)
        {
            if (vx < topAirSpeed) addAccelerationX(airAccel);
        }
    }

    private Vec2 determineFriction()
    {
        float frictionX = 0, frictionY = 0;
        if (state.isGrounded())
        {
            boolean exceedSprint, exceedRunL, exceedRunR, exceedWalkL, exceedWalkR;
            if (state.isLow())
            {
                exceedSprint = getVelocityX() > lowerSprintSpeed || getVelocityX() < -lowerSprintSpeed;
                exceedRunR = getVelocityX() > crawlSpeed; exceedRunL = getVelocityX() < -crawlSpeed;
                exceedWalkR = getVelocityX() > crawlSpeed; exceedWalkL = getVelocityX() < -crawlSpeed;
            }
            else
            {
                exceedSprint = getVelocityX() > sprintSpeed || getVelocityX() < -sprintSpeed;
                exceedRunR = getVelocityX() > runSpeed; exceedRunL = getVelocityX() < -runSpeed;
                exceedWalkR = getVelocityX() > walkSpeed; exceedWalkL = getVelocityX() < -walkSpeed;
            }

            if (dirHoriz == -1
                    || exceedSprint
                    || (exceedRunR && has(Condition.NEGATE_SPRINT_RIGHT))
                    || (exceedRunL && has(Condition.NEGATE_SPRINT_LEFT))
                    || (exceedWalkR && has(Condition.NEGATE_RUN_RIGHT))
                    || (exceedWalkL && has(Condition.NEGATE_RUN_LEFT))
                    || (getVelocityX() > 0  && (dirHoriz == LEFT || has(Condition.NEGATE_WALK_RIGHT)))
                    || (getVelocityX() < 0  && (dirHoriz == RIGHT || has(Condition.NEGATE_WALK_LEFT)))
                    || state == State.SLIDE
                    || has(Condition.NEGATE_ACTIVITY)
                    || has(Condition.NEGATE_STABILITY))
            {
                frictionX = touchEntity[DOWN].getFriction() * getFriction();
                if (touchEntity[DOWN] != null && !touchEntity[DOWN].getShape().getDirs()[UP])
                {
                    Vec2 slopeVel = touchEntity[DOWN].applySlopeX(frictionX);
                    frictionX = slopeVel.x;
                    frictionY = slopeVel.y;
                }
            }
        }
        else if (state.isOnWall())
        {
            /* Don't apply friction if climbing up a wall */
            if (getVelocityY() < 0) return new Vec2(frictionX, frictionY);

            if (dirHoriz != -1 || dirVert != -1)
                frictionY = touchEntity[touchEntity[LEFT] != null
                        ? LEFT : RIGHT].getFriction() * getFriction();
        }

        if (getVelocityX() > 0) frictionX = -frictionX;
        if (getVelocityY() > 0) frictionY = -frictionY;

        return new Vec2(frictionX, frictionY);
    }

    /**
     *  Returns the velocity upon hitting a surface.
     *  Useful for determining how much vertical velocity is generated by
     *  running into another entity.
     */
    private Vec2 applyVelocity(float deltaSec, EntityCollection<Entity> entities)
    {
        Vec2 posOriginal = getPosition();
        Vec2 goal = getPosition();
        getVelocity().mul(deltaSec);
        goal.add(getVelocity());
        /* triggerContacts() returns null if the actor does not hit anything */
        Vec2 contactVel = triggerContacts(deltaSec, goal, entities);
        setPosition(goal);

        /* Stop horizontal velocity from building up by setting it to match change in
         * position. Needed for jumping to work correctly and when falling off block. */
        if (touchEntity[DOWN] != null
                && !touchEntity[DOWN].getShape().getDirs()[UP])
            setVelocityY(getY() - posOriginal.y + slopeJumpBuffer);

        return contactVel;
    }

    @Override
    public void setPosition(Vec2 p)
    {
        for (Weapon weapon : weapons)
        {
            if (weapon != null) weapon.updatePosition(p, getVelocity(), getDims(), getWeaponFace());
        }
        super.setPosition(p);
    }

    public void pressLeft(boolean pressed)
    {
        if (pressed)
        {
            /* If you're on a wall, it changes your secondary direction */
            if (state.isOnWall()) dirFace = LEFT;
            /* It changes your primary direction regardless */
            dirHoriz = LEFT;
            /* If you're not forcing a secondary direction,
             * this will change it */
            if (dirFace != LEFT && !pressingRight) dirFace = LEFT;
        }
        /* If you release the key when already moving left */
        else if (dirHoriz == LEFT)
        {
            if (pressingRight) dirHoriz = RIGHT;
            else dirHoriz = -1;
            /* If you release the key when already moving left with a wall */
            if (state.isOnWall())
            {
                if (pressingRight) dirFace = RIGHT;
                else dirFace = LEFT;
            }
        }
        pressingLeft = pressed;
    }
    public void pressRight(boolean pressed)
    {
        if (pressed)
        {
            /* If you're on a wall, it changes your secondary direction */
            if (state.isOnWall()) dirFace = RIGHT;
            /* It changes your primary direction regardless */
            dirHoriz = RIGHT;
            /* If you're not forcing a secondary direction,
             * this will change it */
            if (dirFace != RIGHT && !pressingLeft) dirFace = RIGHT;
        }
        /* If you release the key when already moving right */
        else if (dirHoriz == RIGHT)
        {
            if (pressingLeft) dirHoriz = LEFT;
            else dirHoriz = -1;
            /* If you release the key when already moving right with a wall */
            if (state.isOnWall())
            {
                if (pressingLeft) dirFace = LEFT;
                else dirFace = RIGHT;
            }
        }
        pressingRight = pressed;
    }
    public void pressUp(boolean pressed)
    {
        if (pressed) dirVert = UP;
        else if (dirVert == UP)
        {
            if (pressingDown) dirVert = DOWN;
            else dirVert = -1;
        }
        pressingUp = pressed;
    }
    public void pressDown(boolean pressed)
    {
        if (pressed) dirVert = DOWN;
        else if (dirVert == DOWN)
        {
            if (pressingUp) dirVert = UP;
            else dirVert = -1;
        }
        pressingDown = pressed;
    }
    public void pressShift(boolean pressed) { pressingShift = pressed; }

    private boolean pressingJump = false;
    private float pressedJumpTime = 0F;
    private Entity pressedJumpSurface;
    public void pressJump(boolean pressed)
    {
        if (pressed && !pressingJump && canJump())
        {
            pressedJumpTime = 1F; // TODO: decide this value
            addCondition(0.3F, Condition.FORCE_STAND);
        }
        else if (!pressed) pressedJumpTime = -1F;
        pressingJump = pressed;
    }

    public void debug() { Print.yellow(getVelocityX()); }

    public final static int ATTACK_KEY_1 = 1, ATTACK_KEY_2 = 2, ATTACK_KEY_3 = 3,
            ATTACK_KEY_MOD = ATTACK_KEY_3;
    public void pressAttackMod(boolean pressed)
    {
        pressingAttack[0] = pressed;
        pressAttack(pressed, 0);
    }

    public void pressAttack(boolean pressed, int attackKey)
    {
        if (!pressed)
        {
            /* If releasing a key */
            for (int i = 0; i < weapons.length; i++)
            {
                if (weapons[i] == null) continue;
                weapons[i].releaseCommand(attackKey);
            }
        }
        /* If holding down so long that the key listener spams signals */
        else if (!has(Condition.NEGATE_ATTACK) && !pressingAttack[attackKey])
        {
            int usingAttackMod = pressingAttack[0] ? ATTACK_KEY_MOD : 0;
            Command command = new Command(attackKey + usingAttackMod,
                    getWeaponFace(), DirEnum.get(dirHoriz, dirVert));

            /* Find a warming weapon and its command, see if it makes a combo */
            Command combo = null;
            for (int i = weapons.length - 1; i >= 0; i--)
            {
                if (weapons[i] == null) continue;
                combo = weapons[i].mayInterrupt(command);
                if (combo != null) break;
            }

            /* If there was a combo, see if it works */
            boolean comboSuccess = false;
            if (combo != null)
            {
                for (int i = weapons.length - 1; i >= 0; i--)
                {
                    if (weapons[i] == null) continue;
                    comboSuccess = weapons[i].addCommand(combo, true, false);
                    if (comboSuccess) break;
                }
            }

            /* If there was no combo or the combo failed, see if the command
             * works as a chain */
            boolean chainSuccess = false;
            if (combo == null || !comboSuccess)
            {
                for (int i = weapons.length - 1; i >= 0; i--)
                {
                    if (weapons[i] == null) continue;
                    chainSuccess = weapons[i].addCommand(command, false, true);
                    if (chainSuccess) break;
                }
            }

            /* If the chain failed, see if it works the normal way  */
            if (!chainSuccess)
            {
                for (int i = weapons.length - 1; i >= 0; i--)
                {
                    if (weapons[i] == null) continue;
                    if (weapons[i].addCommand(command, false, false)) break;
                }
            }
        }

        pressingAttack[attackKey] = pressed;
    }

    public DirEnum getWeaponFace()
    {
        if (state.isOnWall())
        {
            if (touchEntity[LEFT] != null)
                return DirEnum.get(RIGHT, dirVert);
            if (touchEntity[RIGHT] != null)
                return DirEnum.get(LEFT, dirVert);
            if (touchLateSurface[LEFT] != null)
                return DirEnum.get(RIGHT, dirVert);
            if (touchLateSurface[RIGHT] != null)
                return DirEnum.get(LEFT, dirVert);

        }
        return DirEnum.get(dirFace < 0
                ? dirHoriz : dirFace, dirVert);
    }

    private Weapon getBlockingWeapon()
    {
        int maxRating = 0, maxRatingIndex = 0;
        for (int i = weapons.length - 1; i >= 0; i--)
        {
            if (weapons[i] == null) continue;
            int blockRating = weapons[i].getBlockRating();
            if (blockRating > maxRating)
            {
                maxRating = blockRating;
                maxRatingIndex = i;
            }
        }
        return weapons[maxRatingIndex];
    }

    public void changeDirFace()
    {
        dirHoriz = opp(dirHoriz);
        dirFace = opp(dirFace);
        for (Weapon weapon : weapons)
        {
            if (weapon != null)
                weapon.updatePosition(getPosition(), getVelocity(), getDims(), getWeaponFace());
        }
    }

    private State determineState()
    {
        if (submerged || (inWater && touchLateSurface[DOWN] == null))
        {
            setWidth(ORIGINAL_WIDTH);
            setHeight(ORIGINAL_HEIGHT);
            return State.SWIM;
        }
        else if (touchEntity[DOWN] != null)
        {
            if (has(Condition.NEGATE_STABILITY) || has(Condition.NEGATE_ACTIVITY)) // prone
            {
                /* Width and height are switched */
                setHeight(ORIGINAL_WIDTH);
                setWidth(ORIGINAL_HEIGHT);
                return State.SLIDE;
            }
            else if ((dirVert == DOWN && !has(Condition.FORCE_STAND)) // crouch
                    || has(Condition.FORCE_CROUCH))
            {
                setWidth(ORIGINAL_WIDTH);
                setHeight(ORIGINAL_HEIGHT / 2);

                if (Math.abs(getVelocityX()) > maxLowerGroundSpeed)
                    return State.SLIDE;
                if (dirHoriz != -1)
                {
                    if (Math.abs(getVelocityX()) > crawlSpeed
                            && getMoveType() == MoveType.SPRINT) return State.LOWER_SPRINT;
                    return State.CRAWL;
                }
                return State.CROUCH;
            }
            else // stand
            {
                setWidth(ORIGINAL_WIDTH);
                setHeight(ORIGINAL_HEIGHT);
            }

            if (getVelocityX() > 0 && touchEntity[RIGHT] != null)
            {
                if ((dirVert == UP || dirHoriz == RIGHT)
                        && touchEntity[RIGHT] instanceof Block)
                    return State.WALL_CLIMB;
                else return State.STAND;
            }
            if (getVelocityX() < 0 && touchEntity[LEFT] != null)
            {
                if ((dirVert == UP || dirHoriz == LEFT)
                        && touchEntity[LEFT] instanceof Block)
                    return State.WALL_CLIMB;
                else return State.STAND;
            }

            if (dirHoriz != -1)
            {
                if (Math.abs(getVelocityX()) > runSpeed
                        && getMoveType() == MoveType.SPRINT) return State.SPRINT;
                return State.RUN;
            }
            return State.STAND;
        }
        else if (touchEntity[LEFT] != null || touchEntity[RIGHT] != null)
        {
            if (getVelocityY() < 0) return getVelocityY() > -maxClimbSpeed ? State.WALL_CLIMB : State.RISE;
            return getVelocityY() < maxClimbSpeed ? State.WALL_STICK : State.FALL;
        }
        else
        {
            if (getVelocityY() < 0)
            {
                if (!has(Condition.FORCE_STAND) && dirVert == DOWN)
                {
                    setWidth(ORIGINAL_WIDTH);
                    setHeight(ORIGINAL_HEIGHT / 2);
                }
                else
                {
                    setWidth(ORIGINAL_WIDTH);
                    setHeight(ORIGINAL_HEIGHT);
                }
                return State.RISE;
            }
            else return State.FALL;
        }
    }

    /**
     *  Returns true if Actor transitioned from RISE to WALL_CLIMB
     */
    boolean setState(State state)
    {
        if (this.state == state) return false;
        else if (state == State.RISE) gravity = REDUCED_GRAVITY;
        else if (state == State.WALL_CLIMB && getVelocityY() < 0)
            gravity = REDUCED_GRAVITY;

        if (this.state == State.RISE && state == State.WALL_CLIMB)
        {
            //Print.blue(this.state + " -> " + state);
            this.state = state;
            return true;
        }

        if (state == State.CROUCH) setFriction(GREATER_FRICTION);
        else if (state == State.SLIDE) setFriction(REDUCED_FRICTION);
        else setFriction(NORMAL_FRICTION);

        //Print.blue(this.state + " -> " + state);

        this.state = state;
        return false;
    }

    public void equip(Weapon weapon)
    {
        //weapons[1] = weapon.equip(this);
    }

    //===============================================================================================================
    //  triggerContacts(Vec2 goal, ArrayList<Entity> entityList)
    //
    // This method iterates through entityList and assigns each element of the class array touchEntity[] to null
    //  (if this actor is not touching anything on that edge) or to an entity reference of whatever entity it is
    //  touching.
    //
    // The given Vec2 goal is the location where this actor would move if it does not hit anything.
    //   This method changes the location in goal so that the object overlap anything.
    //
    // If a collision is detected, this actor's velocity adjusted accordingly.
    //
    // The method returns null if this actor's velocity is unchanged. Otherwise, it returns this actors original
    // velocity.
    //===============================================================================================================
    private Vec2 triggerContacts(float deltaSec, Vec2 goal, EntityCollection<Entity> entityList)
    {
        for (int i = 0; i < touchLateSurface.length; i++)
        {
            if (touchLateSurface[i] != null
                    && touchLateSurface[i].countdown(deltaSec))
                touchLateSurface[i] = null;
            else if (touchLateSurface[i] == null && touchEntity[i] != null)
                touchLateSurface[i] = new LateSurface(touchEntity[i], getVelocity());
        }

        return super.triggerContacts(goal, entityList);
    }

    /**
     * @param cameraZoneList -
     * @return - The zoom value that this Actor should have based on what
     *          camera zone it's inside of. Only applicable if being controlled
     *          by a player.
     */
    float getZoom(ArrayList<CameraZone> cameraZoneList)
    {
        float sum = 0;
        ArrayList<Float> distances = new ArrayList<>();
        ArrayList<Float> zooms = new ArrayList<>();
        for (CameraZone zone : cameraZoneList)
        {
            if (zone.surrounds(this))
            {
                float distance = zone.getDistanceFromEdge(this);
                sum += distance;
                distances.add(distance);
                zooms.add(zone.getZoom());
            }
        }
        if (sum == 0) return -1; // No camera zones in bounds
        float totalZoom = 0;
        for (int i = 0; i < distances.size(); i++)
        {
            totalZoom += (distances.get(i) / sum) * zooms.get(i);
        }
        return totalZoom;
    }

    boolean shouldVertCam()
    {
        return state.isGrounded() || state.isOnWall() || state == State.SWIM
                || fromWall || getY() - getHeight() > fromGround;
    }

    public int getSpeedRating()
    {
        double speed = Math.abs(getVelocity().mag());
        if (speed > sprintSpeed) return 3;
        if (speed > runSpeed) return 2;
        if (speed > walkSpeed) return 1;
        return 0;
    }
    public DirEnum getTravelDir()
    {
        Vec2 vel = getVelocity();
        if (vel.x == 0)
        {
            if (vel.y == 0) return DirEnum.NONE;
            if (vel.y > 0) return DirEnum.DOWN;
            else /* vel.y < 0 */ return DirEnum.UP;
        }
        if (vel.x > 0)
        {
            if (vel.y == 0) return DirEnum.RIGHT;
            if (vel.y > 0) return DirEnum.DOWNRIGHT;
            else /* vel.y < 0 */ return DirEnum.UPRIGHT;
        }
        else // if (vel.x < 0)
        {
            if (vel.y == 0) return DirEnum.LEFT;
            if (vel.y > 0) return DirEnum.DOWNLEFT;
            else /* vel.y < 0 */ return DirEnum.UPLEFT;
        }
    }

    //================================================================================================================
    // State
    //================================================================================================================
    public enum State
    {
        RISE
                {
                    public boolean isAirborne() { return true; }
                    Color getColor() { return Color.CORNFLOWERBLUE; }
                },
        FALL
                {
                    public boolean isAirborne() { return true; }
                    Color getColor() { return Color.CYAN; }
                },
        STAND
                {
                    public boolean isGrounded() { return true; }
                    Color getColor() { return Color.MAROON; }
                },
        RUN
                {
                    public boolean isGrounded() { return true; }
                    Color getColor() { return Color.BROWN; }
                },
        SPRINT
                {
                    public boolean isGrounded() { return true; }
                    Color getColor() { return Color.DARKGRAY; }
                    public boolean isSprint() { return true; }
                },
        WALL_STICK
                {
                    public boolean isOnWall() { return true; }
                    Color getColor() { return Color.GREENYELLOW; }
                },
        WALL_CLIMB
                {
                    public boolean isOnWall() { return true; }
                    Color getColor() { return Color.LIGHTGREEN; }
                },
        CROUCH
                {
                    public boolean isGrounded() { return true; }
                    public boolean isLow() { return true; }
                    Color getColor() { return Color.RED; }
                },
        CRAWL
                {
                    public boolean isGrounded() { return true; }
                    public boolean isLow() { return true; }
                    Color getColor() { return Color.HOTPINK; }
                },
        LOWER_SPRINT
                {
                    public boolean isGrounded() { return true; }
                    public boolean isLow() { return true; }
                    public boolean isSprint() { return true; }
                    Color getColor() { return Color.DEEPPINK; }
                },
        SLIDE
                {
                    public boolean isGrounded() { return true; }
                    public boolean isLow() { return true; }
                    Color getColor() { return Color.PINK; }
                },
        SWIM
                {
                    Color getColor() { return Color.BLUE; }
                };

        public boolean isOnWall() { return false; }
        public boolean isAirborne() { return false; }
        public boolean isGrounded() { return false; }
        public boolean isLow() { return false; }
        public boolean isSprint() { return false; }
        Color getColor() { return Color.BLACK; }
    }
    public State getState() { return state; }

    //================================================================================================================
    // Condition
    //================================================================================================================
    public enum Condition
    {
        NEGATE_ATTACK, NEGATE_BLOCK,
        NEGATE_SPRINT_LEFT, NEGATE_SPRINT_RIGHT,
        NEGATE_RUN_LEFT, NEGATE_RUN_RIGHT,
        NEGATE_WALK_LEFT, NEGATE_WALK_RIGHT,
        NEGATE_STABILITY, NEGATE_ACTIVITY,
        DASH,
        FORCE_STAND, FORCE_CROUCH
    }
    private void addConditionApp(Infliction inf)
    {
        // TODO: check for immunities and resistances here using condType to cancel effect or modify .getTime()

        ConditionApp condApp = inf.getConditionApp();
        if (condApp != null)
        {
            if (condApp.isSinglet())
                addCondition(condApp.getConditions());
            else addCondition(condApp.getTime(), condApp.getConditions());
        }
    }
    public void addCondition(float time, Condition... conditions)
    {
        if (time < 0) addCondition(conditions);
        else
        {
            for (Condition cond : conditions)
            {
                if (this.conditions[cond.ordinal()] < time)
                    this.conditions[cond.ordinal()] = time;
            }
        }
    }
    public void addCondition(Condition... conditions)
    {
        for (Condition cond : conditions)
        {
            conditionsB[cond.ordinal()] = true;
        }
    }
    public boolean has(Condition cond)
    {
        return conditionsB[cond.ordinal()] || conditions[cond.ordinal()] > 0;
    }

    private boolean willTumble()
    {
        double vel = Math.abs(getVelocityX());
        if (has(Condition.NEGATE_STABILITY) || has(Condition.NEGATE_ACTIVITY))
        {
            return vel > walkSpeed;
        }
        if (state.isGrounded())
        {
            if (state.isLow())
            {
                if (state == State.SLIDE) return vel > maxSlideSpeed;
                return vel > maxLowerGroundSpeed;
            }
            return vel > maxGroundSpeed;
        }
        return false;
    }

    private void countdownCondition(float deltaSec)
    {
        boolean wasTumbling = has(Condition.NEGATE_ACTIVITY) && (state.isGrounded() || state.isOnWall());

        for (int i = 0; i < conditions.length; i++)
        {
            if (conditions[i] > 0)
            {
                conditions[i] -= deltaSec;
                if (conditions[i] < 0) conditions[i] = 0;
            }
        }

        if (wasTumbling)
        {
            if (Math.abs(getVelocityX()) > walkSpeed) addCondition(0.01F, Condition.NEGATE_ACTIVITY);
            else if (!has(Condition.NEGATE_ACTIVITY))
            {
                /* Dodging while knocked down */
                if (Math.abs(getVelocity().mag()) <= walkSpeed)
                {
                    if (dirHoriz == LEFT && getVelocityX() > -rushSpeed / 1.5F) setVelocityX(-rushSpeed / 1.5F);
                    else if (dirHoriz == RIGHT && getVelocityX() < rushSpeed / 1.5F) setVelocityX(rushSpeed / 1.5F);
                }
            }
        }
    }

    private float getStaggerMagMod(float mag)
    {
        if (mag < walkSpeed) return 1;
        else if (mag < runSpeed) return STAGGER_MAG_MOD[0];
        else if (mag < rushSpeed) return STAGGER_MAG_MOD[1];
        else return STAGGER_MAG_MOD[2];
    }

    /* Called when player is hit or blocked */
    public void stagger(DirEnum dir, float mag, boolean operator)
    {
        float _staggerRecoverTime = getStaggerMagMod(mag) * staggerRecoverTime;

        if (operator) /* When player gets blocked */
        {
            float staggerBlockedTime = _staggerRecoverTime * staggerBlockMod;
            addCondition(staggerBlockedTime, Condition.NEGATE_ATTACK, Condition.NEGATE_BLOCK);
        }

        /* When player is hit */
        if (has(Condition.NEGATE_ACTIVITY))
        {
            addCondition(_staggerRecoverTime, Condition.NEGATE_ACTIVITY);
            Print.green("ouch!");
        }
        else if (has(Condition.NEGATE_STABILITY))
        {
            addCondition(_staggerRecoverTime, Condition.NEGATE_ACTIVITY);
        }
        else if (state.isGrounded())
        {
            DirEnum horiz = dir.getHoriz(), vert = dir.getVert();

            if (vert == DirEnum.UP)
            {
                if (has(Condition.NEGATE_RUN_LEFT) || has(Condition.NEGATE_RUN_RIGHT))
                    addCondition(_staggerRecoverTime, Condition.FORCE_STAND);
            }
            else if (vert == DirEnum.DOWN)
            {
                if (state.isLow()) addCondition(_staggerRecoverTime, Condition.NEGATE_STABILITY);
                else if (has(Condition.NEGATE_RUN_LEFT) || has(Condition.NEGATE_RUN_RIGHT))
                    addCondition(_staggerRecoverTime, Condition.FORCE_CROUCH);
            }

            if (horiz == DirEnum.LEFT)
            {
                if (has(Condition.NEGATE_WALK_RIGHT)) addCondition(_staggerRecoverTime, Condition.NEGATE_STABILITY);
                else if (has(Condition.NEGATE_RUN_RIGHT)) addCondition(_staggerRecoverTime, Condition.NEGATE_WALK_RIGHT);
                else addCondition(_staggerRecoverTime, Condition.NEGATE_RUN_RIGHT);
            }
            else if (horiz == DirEnum.RIGHT)
            {
                if (has(Condition.NEGATE_WALK_LEFT)) addCondition(_staggerRecoverTime, Condition.NEGATE_STABILITY);
                else if (has(Condition.NEGATE_RUN_LEFT)) addCondition(_staggerRecoverTime, Condition.NEGATE_WALK_LEFT);
                else addCondition(_staggerRecoverTime, Condition.NEGATE_RUN_LEFT);
            }
            else if (vert != DirEnum.NONE)
            {
                if (has(Condition.NEGATE_WALK_LEFT) || has(Condition.NEGATE_WALK_RIGHT))
                    addCondition(_staggerRecoverTime, Condition.NEGATE_STABILITY);
                else if (!canRun()) addCondition(_staggerRecoverTime, Condition.NEGATE_WALK_LEFT, Condition.NEGATE_WALK_RIGHT);
                else addCondition(_staggerRecoverTime, Condition.NEGATE_RUN_LEFT, Condition.NEGATE_RUN_RIGHT);
            }
        }

        for (Weapon weapon : weapons) { weapon.disrupt(); }
    }

    /* Called when player's attack is parried */
    public void stagger(GradeEnum grade)
    {
        float gradeInflucence;
        if (grade.ordinal() <= GradeEnum.E.ordinal()) gradeInflucence = 1;
        else if (grade.ordinal() <= GradeEnum.C.ordinal()) gradeInflucence = STAGGER_MAG_MOD[0];
        else if (grade.ordinal() <= GradeEnum.A.ordinal()) gradeInflucence = STAGGER_MAG_MOD[1];
        else gradeInflucence = STAGGER_MAG_MOD[2];

        addCondition(staggerRecoverTime * staggerParryMod * gradeInflucence,
                Condition.NEGATE_ATTACK, Condition.NEGATE_BLOCK);
    }

    /* Called when player lands too hard */
    void stagger(float amount)
    {
        pressedJumpSurface = null;
        if (amount <= landingThresh[0]) return;

        addCondition(staggerRecoverTime,
                Condition.NEGATE_ATTACK, Condition.NEGATE_BLOCK,
                Condition.NEGATE_WALK_LEFT, Condition.NEGATE_WALK_RIGHT,
                amount > landingThresh[1] ? Condition.NEGATE_ACTIVITY : !canWalk()
                        ? Condition.NEGATE_STABILITY : Condition.FORCE_CROUCH);
        pressedJumpTime = 0F;
    }

    @Override
    public void damage(Infliction inf)
    {
        GradeEnum damageGrade = inf.getDamage();
        if (damageGrade != null)
            Print.yellow("Actor: Dealt " + damageGrade + " damage of type " + inf.getType());
    }

    @Override
    public boolean easyToBlock() { return false; } // TODO: return true if wielding a shield
    public boolean tryingToBlock() { return pressingUp; }

    @Override
    protected void applyInflictions()
    {
        for (int i = 0; i < inflictions.size(); i++)
        {
            Infliction inf = inflictions.get(i);

            damage(inf);
            addConditionApp(inf);
            Vec2 momentum = inf.getMomentum();
            if (momentum != null) addVelocity(momentum.div(getMass()));
        }
    }

    @Override
    public void inflict(Infliction infliction)
    {
        inflictions.add(infliction);
        Print.yellow("Actor: " + infliction + " added");
    }

    boolean setTriggered(boolean triggered)
    {
        super.setTriggered(triggered);
        return true;
    }

    private class LateSurface
    {
        private Entity entity;
        private Vec2 lateVel;
        private float duration = 0.15F; // TODO: decide this value

        LateSurface(Entity entity, Vec2 lateVel)
        {
            this.entity = entity;
            this.lateVel = lateVel;
        }

        boolean countdown(float deltaSec)
        {
            duration -= deltaSec;
            if (duration <= 0) return true;
            return false;
        }

        Vec2 getLateVel() { return lateVel; }

        ShapeEnum getShape() { return entity.getShape(); }

        boolean valid() { return entity != pressedJumpSurface; }
    }

    /*=======================================================================*/
    /* Variables that are set by the character's stats                       */
    /*=======================================================================*/

    private final float ORIGINAL_WIDTH, ORIGINAL_HEIGHT;

    /* This is the highest speed the player can be running or sprinting before
     * changing their state to TUMBLE. */
    private float maxGroundSpeed = 0.25F;

    /* This is the highest speed the player can get from walking alone.
     * They can go faster while walking with the help of external influences,
     * such as going down a slope or being pushed by a faster object. */
    private float walkSpeed = 0.04F;

    private float rushSpeed = 0.3F;

    /* This is the highest speed the player can get from sprinting alone.
     * They can go faster while sprinting with the help of external influences,
     * such as going down a slope or being pushed by a faster object. */
    private float sprintSpeed = 0.13F;

    /* This is the highest speed the player can get from running alone.
     * They can go faster while running with the help of external influences,
     * such as going down a slope or being pushed by a faster object. */
    private float runSpeed = 0.08F;

    /* This is the acceleration that is applied to the player when dirPrimary
     * is not null and the player is running or sprinting on the ground. */
    private float runAccel = 0.4F;

    /* This is the highest speed the player can be crawling or crouching before
     * changing their state to TUMBLE or SLIDE. */
    private float maxLowerGroundSpeed = 0.15F;

    /* This is the highest speed the player can get from sprinting on their
     * hands alone. They can go faster while sprinting on their hands with the
     * help of external influences, such as going down a slope or being pushed
     * by a faster object. */
    private float lowerSprintSpeed = 0.10F;

    /* This is the highest speed the player can get from crawling alone.
     * They can go faster while crawling with the help of external influences,
     * such as going down a slope or being pushed by a faster object. */
    private float crawlSpeed = 0.05F;

    /* This is the acceleration that is applied to the player when dirPrimary
     * is not null and the player is crawling on the ground. */
    private float crawlAccel = 0.3F;

    /* This is the highest speed the player can be sliding before changing
     * their state to TUMBLE. */
    private float maxSlideSpeed = 0.3F;

    /* This is the highest speed the player can be climbing before changing
     * their state to RISE. */
    private float maxClimbSpeed = 1F;

    /* This is the highest speed the player can be skidding down a wall before
     * changing their state to FALL. */
    private float maxStickSpeed = 1.5F;

    /* This is the acceleration that is applied to the player when on a wall.
     * Most characters should use their crawlAccel value for this, unless they
     * know how to climb without needing a running start. */
    private float climbAccel = crawlAccel;

    /* How long it takes to climb over a ledge after grabbing it */
    private float climbLedgeTime = 1;

    private float[] stairRecoverTime = { 0.05F, 0.05F, 0.05F };

    /* This is the highest speed the player can move.
     * (In the air or anywhere) */
    private float maxTotalSpeed = 5F;

    /* This is the highest speed the player can get from moving themselves in
     * the air. They can go faster in the air with the help of external
     * influences such as wind or being pushed by a faster object. */
    private float airSpeed = 0.2F;

    /* This is the acceleration that is applied to the player when dirPrimary
     * is not null and the player is airborne. */
    private float airAccel = 0.1F;

    /* This is the highest speed the player can move in water. */
    private float swimSpeed = 3F;

    /* A variable "topSwimSpeed" won't be needed because the drag underwater
     * will be high enough to cap the player's speed. */

    /* This is the acceleration that is applied to the player when in water. */
    private float swimAccel = 0.3F;

    /* The velocity used to jump */
    private float jumpVel = 0.4F;

    /* How long dashing negates walking */
    private float dashRecoverTime = 1;

    /* How long it takes to get up from being prone */
    private float proneRecoverTime = 1;

    /* How long the player tumbles */
    private float minTumbleTime = 1F;

    // friction

    /* How long the player staggers */
    private float staggerRecoverTime = 2;

    /* Modifies staggerRecoverTime when parried.
     * Want this to be low but would never fall below 1.0F */
    private float staggerParryMod = 2F;

    /* Modifies staggerRecoverTime when blocking.
    *  Want this to be low and would never go over 1.0F */
    private float staggerBlockMod = 0.5F;

    float[] landingThresh = { 0.5F, 1F };

    private void setCharacterStats()
    {
        airSpeed = charStat.airSpeed();
        swimSpeed = charStat.swimSpeed();
        crawlSpeed = charStat.crawlSpeed();
        walkSpeed = charStat.walkSpeed();
        runSpeed = charStat.runSpeed();
        lowerSprintSpeed = charStat.lowerSprintSpeed();
        sprintSpeed = charStat.sprintSpeed();
        rushSpeed = charStat.rushSpeed();

        maxClimbSpeed = charStat.maxClimbSpeed();
        maxStickSpeed = charStat.maxStickSpeed();
        maxSlideSpeed = charStat.maxSlideSpeed();
        maxLowerGroundSpeed = charStat.maxLowerGroundSpeed();
        maxGroundSpeed = charStat.maxGroundSpeed();
        maxTotalSpeed = charStat.maxTotalSpeed();

        airAccel = charStat.airAccel();
        swimAccel = charStat.swimAccel();
        crawlAccel = charStat.crawlAccel();
        climbAccel = charStat.climbAccel() / getMass();
        runAccel = charStat.runAccel();

        jumpVel = charStat.jumpVel() / getMass();

        climbLedgeTime = charStat.climbLedgeTime() / getMass();
        stairRecoverTime = charStat.stairRecoverTime();
        dashRecoverTime = charStat.dashRecoverTime();
        minTumbleTime = charStat.minTumbleTime();

        proneRecoverTime = charStat.proneRecoverTime();
        staggerRecoverTime = charStat.staggerRecoverTime();
        staggerParryMod = charStat.staggerParryMod();
        staggerBlockMod = charStat.staggerBlockMod();

        landingThresh = charStat.landingThresh();

        NORMAL_FRICTION = charStat.friction();
        GREATER_FRICTION = NORMAL_FRICTION * 3;
        REDUCED_FRICTION = NORMAL_FRICTION / 3;
        setFriction(NORMAL_FRICTION);
    }
}