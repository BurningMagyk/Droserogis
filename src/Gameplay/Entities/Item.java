/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Gameplay.Entities;

import Gameplay.Entities.Weapons.Infliction;
import Util.GradeEnum;
import Util.Print;
import Util.Vec2;

import java.util.ArrayList;

public abstract class Item extends Entity
{
    GradeEnum mass;
    private int hitPoints;

    /* The entities that are in contact from each of 4 directions */
    Entity[] touchEntity = new Entity[4];

    float slopeJumpBuffer = 0.1F;

//    float gravity = 17.0F;
    float gravity = 0.25F;

//    float airDrag = 1F;
    float airDrag = 0.25F;
    float waterDrag = 10F;

    boolean bumpingCeiling = false;
    boolean inWater = false, submerged = false;
    private boolean isGrounded = false;

    void debug()
    {
        Print.blue("velX: " + getVelocityX() + ", velY: " + getVelocityY());
    }

    protected Item(float xPos, float yPos, float width, float height, GradeEnum mass, int hitPoints, ArrayList<String[]> spritePaths)
    {
        super(xPos, yPos, width, height, ShapeEnum.RECTANGLE, spritePaths);
        this.mass = mass;

        this.hitPoints = hitPoints;
    }

    public void update(EntityCollection<Entity> entities, float deltaSec)
    {
        resetAcceleration();
        applyInflictions();
        applyPhysics(entities, deltaSec);
    }

    /**
     * Location and velocity carry over from frame to frame.
     * Acceleration, however exists only when there is a force.
     * Thus, each frame, we set acceleration to 0, figure out which forces
     * are acting on it and add in acceleration for those forces. */
    protected void resetAcceleration()
    {
        setAcceleration(0,0);
        if (touchEntity[DOWN] == null) setAccelerationY(gravity);
    }

    public GradeEnum getMass() { return mass; }
    public void setMass(GradeEnum mass) { this.mass = mass; }

    protected abstract void applyInflictions();

    protected void applyPhysics(EntityCollection entities, float deltaSec)
    {
        boolean slopeLeft = false, slopeRight = false;
        if (touchEntity[DOWN] != null)
        {
            slopeLeft = !touchEntity[DOWN].getShape().getDirs()[LEFT];
            slopeRight = !touchEntity[DOWN].getShape().getDirs()[RIGHT];
        }
        applyAcceleration(getAcceleration(), deltaSec);
        Vec2 beforeDrag = applyAcceleration(determineDrag(), deltaSec);
        neutralizeVelocity(beforeDrag, slopeLeft, slopeRight);
        Vec2 beforeFriction = applyAcceleration(determineFriction(), deltaSec);
        neutralizeVelocity(beforeFriction, slopeLeft, slopeRight);
        /* Vec2 contactVelocity = */ applyVelocity(deltaSec, entities);
    }

    /**
     * Returns what the velocity was before being updated by acceleration.
     */
    Vec2 applyAcceleration(Vec2 acceleration, float deltaSec)
    {
        Vec2 v = getVelocity();
        acceleration.mul(deltaSec);

        Vec2 oldVel = getVelocity();
        setVelocity(v.add(acceleration));

        return oldVel;
    }

    /**
     * Sets velocity to zero if the acceleration was high enough to make it
     * reverse direction.
     */
    void neutralizeVelocity(Vec2 oldVel, boolean leftSlope, boolean rightSlope)
    {
        int unitPosVelX = 0;
        if (oldVel.x > 0) unitPosVelX = 1;
        else if (oldVel.x < 0) unitPosVelX = -1;

        int unitPosVelY = 0;
        if (oldVel.y > 0) unitPosVelY = 1;
        else if (oldVel.y < 0) unitPosVelY = -1;

        Vec2 newVel = getVelocity();

        int unitPosVelXNew = 0;
        if (newVel.x > 0) unitPosVelXNew = 1;
        else if (newVel.x < 0) unitPosVelXNew = -1;

        int unitPosVelYNew = 0;
        if (newVel.y > 0) unitPosVelYNew = 1;
        else if (newVel.y < 0) unitPosVelYNew = -1;

        if (unitPosVelX != unitPosVelXNew)
            setVelocityX(leftSlope ? -minSlopeSpeed : rightSlope ? minSlopeSpeed : 0);
        if (unitPosVelY != unitPosVelYNew) setVelocityY(0);

        /* This is needed so that the Actor sinks when inactive in liquid */
        if (Math.abs(getVelocityX()) < minThreshSpeed) setVelocityX(0);
        if (Math.abs(getVelocityY()) < minThreshSpeed)
            setVelocityY(inWater ? (float) minThreshSpeed : 0);
    }

    Vec2 determineDrag()
    {
        float dragX, dragY;
        if (inWater)
        {
            dragX = -waterDrag * getVelocityX();
            dragY = -waterDrag * getVelocityY();
        }
        else
        {
            dragX = -airDrag * getVelocityX();
            dragY = -airDrag * getVelocityY();
        }
        return new Vec2(dragX, dragY);
    }

    private Vec2 determineFriction()
    {
        float frictionX = 0, frictionY = 0;
        if (isGrounded)
        {
            frictionX = touchEntity[DOWN].getFriction() * getFriction();
            if (touchEntity[DOWN] != null && !touchEntity[DOWN].getShape().getDirs()[UP])
            {
                Vec2 slopeVel = touchEntity[DOWN].applySlopeX(frictionX);
                frictionX = slopeVel.x;
                frictionY = slopeVel.y;
            }
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
    private Vec2 applyVelocity(float deltaSec, EntityCollection entities)
    {
        Vec2 posOriginal = getPosition();
        Vec2 goal = getPosition();
        goal.add(getVelocity().mul(deltaSec * 60));
        /* triggerContacts() returns null if the actor does not hit anything */
        Vec2 contactVel = triggerContacts(goal, entities);
        setPosition(goal);

        /* Set velocity to be accurate to change in position */
        setVelocity(getPosition().minus(posOriginal));

        return contactVel;
    }

    Vec2 triggerContacts(Vec2 goal, EntityCollection<Entity> entityList)
    {
        Vec2 originalVel = null;
        boolean bumpingCeiling = touchEntity[UP] == null;
        float fromSlope = 0;
        if (touchEntity[DOWN] != null && !touchEntity[DOWN].getShape().getDirs()[UP])
            fromSlope = touchEntity[DOWN].applySlopeX(getVelocityX()).y;

        inWater = false; submerged = false;

        /* Used for the yPos correction when bumping down slopes */
        Entity prevTouchEntity[] = new Entity[4];
        prevTouchEntity[UP] = touchEntity[UP];
        prevTouchEntity[DOWN] = touchEntity[DOWN];
        prevTouchEntity[LEFT] = touchEntity[LEFT];
        prevTouchEntity[RIGHT] = touchEntity[RIGHT];

        touchEntity[UP] = null;
        touchEntity[DOWN] = null;
        touchEntity[LEFT] = null;
        touchEntity[RIGHT] = null;

        for (Entity entity : entityList)
        {
            if (entity == this) continue;

            boolean withBlock = entity instanceof Block;
            /* If inside a block of liquid */
            if (withBlock && ((Block) entity).isLiquid()
                    && entity.withinBounds(this))
            {
                inWater = true;
                /* If completely inside a block of liquid */
                if (entity.surrounds(this)) submerged = true;
                continue;
            }

            int[] edge = entity.getTouchEdge(this, goal);

            /* Actor made no contact with the entity */
            if (edge[0] < 0)
            {
                /* Fell off upward slope is is bumping down upward slope */
                if (fromSlope > 0 && getVelocityY() > 0)
                {
                    /* Bumping down upward slope, snap yPos to top edge of slope. */
                    if (getVelocityY() >= 0
                            && prevTouchEntity[DOWN] != null
                            && prevTouchEntity[DOWN] == entity
                            && getX() >= prevTouchEntity[DOWN].getLeftEdge()
                            && getX() <= prevTouchEntity[DOWN].getRightEdge())
                    {
                        goal.y = entity.getTopEdge(goal.x) - getHeight() / 2 + 0.01F;
                        touchEntity[DOWN] = entity;
                        isGrounded = true;
                    }
                    else
                    {
                        /* Fell off an upward slope and not jumping, correct y-velocity.
                         * Warning: Don't make moving platforms that are sloped. */
                        goal.y -= getVelocityY();
                        goal.y += fromSlope;
                        setVelocityY(fromSlope);
                    }
                }

                continue;
            }

            /* If touching a block of liquid */
            if (withBlock && ((Block) entity).isLiquid())
            {
                inWater = true;
                continue;
            }

            /* Actor has touched another non-liquid entity a this point */
            originalVel = this.getVelocity();

            if (!entity.setTriggered(true)) continue;

            /* Normal physical collisions only occur with Blocks */
            if (!withBlock) continue;

            Block block = (Block) entity;

            /* Replace touchEntity[x] only if entity here is closer */
//            if (prevTouchEntity[edge[0]] != null)
//            {
//                if (edge[0] == UP)
//                {
//                    if (block.getBottomEdge() > prevTouchEntity[edge[0]].getBottomEdge())
//                        touchEntity[edge[0]] = entity;
//                }
//                else if (edge[0] == DOWN)
//                {
//                    if (block.getTopEdge() < prevTouchEntity[edge[0]].getTopEdge())
//                        touchEntity[edge[0]] = entity;
//                }
//                else if (edge[0] == LEFT)
//                {
//                    if (block.getLeftEdge() > prevTouchEntity[edge[0]].getLeftEdge())
//                        touchEntity[edge[0]] = entity;
//                }
//                else if (edge[0] == RIGHT)
//                {
//                    if (block.getRightEdge() < prevTouchEntity[edge[0]].getRightEdge())
//                        touchEntity[edge[0]] = entity;
//                }
//            }
//            else touchEntity[edge[0]] = entity;

            touchEntity[edge[0]] = entity;

            if (edge[0] == UP)
            {
                goal.y = block.getBottomEdge(goal.x) + getHeight() / 2;

                /* Colliding with down-right slope or down-left slope */
                if (edge[1] == LEFT || edge[1] == RIGHT)
                {
                    if (this.bumpingCeiling)
                    {
                        Vec2 newVel = block.applySlope(originalVel);
                        damage((float) newVel.minus(originalVel).mag()
                                        * block.getHazardRating(),
                                block.getInfMaterials());
                        setVelocity(newVel);
                    }
                }
                /* Colliding with level surface from below */
                else
                {
                    damage(Math.abs(getVelocityY())
                                    * block.getHazardRating(),
                            block.getInfMaterials());
                    setVelocityY(0);
                }
            }
            else if (edge[0] == DOWN)
            {
                goal.y = block.getTopEdge(goal.x) - getHeight() / 2;

                /* Colliding with up-right slope or up-left slope */
                if (edge[1] == LEFT || edge[1] == RIGHT)
                {
                    if (!isGrounded)
                    {
                        Vec2 newVel = block.applySlope(originalVel);
                        damage((float) newVel.minus(originalVel).mag()
                                        * block.getHazardRating(),
                                block.getInfMaterials());
                        setVelocity(newVel);
                    }
                }
                /* Colliding with level surface from above */
                else
                {
                    damage(Math.abs(getVelocityY())
                                    * block.getHazardRating(),
                            block.getInfMaterials());
                    setVelocityY(0);
                }
            }
            else if (edge[0] == LEFT)
            {
                goal.x = block.getRightEdge() + getWidth() / 2;
                damage(Math.abs(getVelocityX())
                                * block.getHazardRating(),
                        block.getInfMaterials());
                setVelocityX(0);
            }
            else if (edge[0] == RIGHT)
            {
                goal.x = block.getLeftEdge() - getWidth() / 2;
                damage(Math.abs(getVelocityX())
                                * block.getHazardRating(),
                        block.getInfMaterials());
                setVelocityX(0);
            }
        }

        this.bumpingCeiling = bumpingCeiling && touchEntity[UP] != null;
        isGrounded = touchEntity[DOWN] != null;

        return originalVel;
    }

    /**
     * @return - false if other entities can move through it, true otherwise.
     * The Item class is the only class that returns false instead of true.
     */
    boolean setTriggered(boolean triggered)
    {
        super.setTriggered(triggered);
        return false;
    }

    void damage(GradeEnum grade, float percent)
    {
        // TODO: should use something other than .ordinal()
        if (hitPoints > 0) hitPoints -= grade.ordinal() * percent;
        if (hitPoints <= 0) destroy();
        else Print.blue("hitpoints left: " + hitPoints);
    }

    /* Only called for damage caused by colliding with Blocks */
    void damage(float amount, Infliction.InflictionType... infType)
    {
        if (amount == 0) return;
        // TODO: fix glitch where Actor gets hurt easily after successfully climbing a ledge

        //Print.blue(amount);
        damage(new Infliction(GradeEnum.getGrade(amount), infType));
    }

    public abstract void damage(Infliction inf);

    protected ArrayList<Infliction> inflictions = new ArrayList<>();

    public abstract void inflict(Infliction infliction);

    protected abstract void destroy();

    /* This is the speed the player gets automatically when running or
     * crawling. Also used for the threshold when neutralizing velocity.
     * Fixes the glitch of not being able to run up a slope after stopping. */
    double minThreshSpeed = 1E-3;

    /* Minimum speed that they go down a slope after hitting terminally low speeds */
    private float minSlopeSpeed = 0.01F;
}