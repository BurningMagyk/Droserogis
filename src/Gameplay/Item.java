package Gameplay;

import Util.Print;
import Util.Vec2;

import java.util.ArrayList;

public class Item extends Entity
{
    private int hitPoints;

    private boolean reactive = false;
    private boolean receptive = false;

    /* The entities that are in contact from each of 4 directions */
    Entity[] touchEntity = new Entity[4];

    private Collision[] collisions = new Collision[4];

    float slopeJumpBuffer = 0.1F;

    float gravity = 2;

    float airDrag = 0.25F;
    float waterDrag = 10F;

    boolean bumpingCeiling = false;
    boolean inWater = false, submerged = false;
    private boolean isGrounded = false;

    void debug()
    {
        Print.blue("velX: " + getVelocityX() + ", velY: " + getVelocityY());
    }

    Item(float xPos, float yPos, float width, float height)
    {
        super(xPos, yPos, width, height, ShapeEnum.RECTANGLE);

        // TODO: get this value from Character or Weapon
        hitPoints = 10;
    }

    void update(ArrayList<Entity> entities, float deltaSec)
    {
        resetAcceleration();
        applyPhysics(entities, deltaSec);
    }

    void resetFlags()
    {
        super.resetFlags();

        applyCollisionsHoriz();
        applyCollisionsVert();

        collisions[UP] = null;
        collisions[DOWN] = null;
        collisions[LEFT] = null;
        collisions[RIGHT] = null;
    }

    void applyCollisionsHoriz()
    {
        if (collisions[LEFT] != null)
        {
            if (collisions[LEFT].withBlock) return;
            float fromLeft = collisions[LEFT].getVelocity();
            if (collisions[RIGHT] != null)
            {
                /* Combine both collisions */
                float fromRight = collisions[RIGHT].getVelocity();
                setVelocityX(fromLeft + fromRight);
            }
            else
            {
                /* Only apply the LEFT collision */
                setVelocityX(fromLeft);
            }
        }
        if (collisions[RIGHT] != null)
        {
            if (collisions[RIGHT].withBlock) return;
            if (collisions[LEFT] == null)
            {
                /* Only apply the RIGHT collision */
                float fromRight = collisions[RIGHT].getVelocity();
                setVelocityX(fromRight);
            }
        }
    }
    void applyCollisionsVert()
    {
        if (collisions[UP] != null)
        {
            if (collisions[UP].withBlock) return;
            float fromUp = collisions[UP].getVelocity();
            if (collisions[DOWN] != null)
            {
                /* Combine both collisions */
                float fromDown = collisions[DOWN].getVelocity();
                setVelocityY(fromUp + fromDown);
            }
            else
            {
                /* Only apply the UP collision */
                setVelocityY(fromUp);
            }
        }
        if (collisions[DOWN] != null)
        {
            if (collisions[DOWN].withBlock) return;
            if (collisions[UP] == null)
            {
                /* Only apply the DOWN collision */
                float fromDown = collisions[DOWN].getVelocity();
                setVelocityY(fromDown);
            }
        }
    }

    /**
     * Location and velocity carry over from frame to frame.
     * Acceleration, however exists only when there is a force.
     * Thus, each frame, we set acceleration to 0, figure out which forces
     * are acting on it and add in acceleration for those forces. */
    void resetAcceleration()
    {
        setAcceleration(0,0);
        if (touchEntity[DOWN] == null) setAccelerationY(gravity);
    }

    private void applyPhysics(ArrayList<Entity> entities, float deltaSec)
    {
        applyAcceleration(getAcceleration(), deltaSec);
        Vec2 beforeDrag = applyAcceleration(determineDrag(), deltaSec);
        neutralizeVelocity(beforeDrag);
        Vec2 beforeFriction = applyAcceleration(determineFriction(), deltaSec);
        neutralizeVelocity(beforeFriction);
        Vec2 contactVelocity = applyVelocity(deltaSec, entities);
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
    void neutralizeVelocity(Vec2 oldVel)
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

        if (unitPosVelX != unitPosVelXNew) setVelocityX(0);
        if (unitPosVelY != unitPosVelYNew) setVelocityY(0);

        /* This is needed so that the Actor sinks when inactive in liquid */
        if (Math.abs(getVelocityX()) < minThreshSpeed) setVelocityX(0);
        if (Math.abs(getVelocityY()) < minThreshSpeed) setVelocityY(0);
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
    private Vec2 applyVelocity(float deltaSec, ArrayList<Entity> entities)
    {
        Vec2 posOriginal = getPosition();
        Vec2 goal = getPosition();
        getVelocity().mul(deltaSec);
        goal.add(getVelocity());
        /* triggerContacts() returns null if the actor does not hit anything */
        Vec2 contactVel = triggerContacts(goal, entities);
        setPosition(goal);

        /* Stop horizontal velocity from building up by setting it to match change in
         * position. Needed for jumping to work correctly and when falling off block. */
        if (touchEntity[DOWN] != null
                && !touchEntity[DOWN].getShape().getDirs()[UP])
            setVelocityY(getY() - posOriginal.y + slopeJumpBuffer);

        return contactVel;
    }

    Vec2 triggerContacts(Vec2 goal, ArrayList<Entity> entityList)
    {
        Vec2 originalVel = null;
        boolean bumpingCeiling = touchEntity[UP] == null;
        inWater = false; submerged = false;

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
            if (edge[0] < 0) continue;

            /* If touching a block of liquid */
            if (withBlock && ((Block) entity).isLiquid())
            {
                inWater = true;
                continue;
            }

            /* Actor has touched another non-liquid entity a this point */
            originalVel = this.getVelocity();

            if (!entity.setTriggered(true)) continue;

            /* This specifically stops items from physically being moved around
             * by Actors */
            if (this.getClass() == Item.class
                    && entity.getClass() == Actor.class) continue;

            touchEntity[edge[0]] = entity;

            if (edge[0] == UP)
            {
                goal.y = entity.getBottomEdge(goal.x) + getHeight() / 2;

                /* Colliding with down-right slope or down-left slope */
                if (edge[1] == LEFT || edge[1] == RIGHT)
                {
                    if (this.bumpingCeiling)
                        setVelocity(entity.applySlope(originalVel));
                }
                /* Colliding with level surface from below */
                else
                {
                    collide(entity, UP, withBlock);//setVelocityY(0);
                    if (withBlock) setVelocityY(0);
                }
            }
            else if (edge[0] == DOWN)
            {
                goal.y = entity.getTopEdge(goal.x) - getHeight() / 2;

                /* Colliding with up-right slope or up-left slope */
                if (edge[1] == LEFT || edge[1] == RIGHT)
                {
                    if (!isGrounded)
                        setVelocity(entity.applySlope(originalVel));
                }
                /* Colliding with level surface from above */
                else
                {
                    collide(entity, DOWN, withBlock);//setVelocityY(0);
                    if (withBlock) setVelocityY(0);
                }
            }
            else if (edge[0] == LEFT)
            {
                goal.x = entity.getRightEdge() + getWidth() / 2;
                collide(entity, LEFT, withBlock);//setVelocityX(0);
                if (withBlock) setVelocityX(0);
            }
            else if (edge[0] == RIGHT)
            {
                goal.x = entity.getLeftEdge() - getWidth() / 2;
                collide(entity, RIGHT, withBlock);//setVelocityX(0);
                if (withBlock) setVelocityX(0);
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

    private class Collision
    {
        boolean withBlock;
        private float totalEulers, totalMass;

        Collision(float velocityThis, float velocityOther, float massThis, float massOther, boolean withBlock)
        {
            totalEulers = velocityThis * massThis + velocityOther * massOther;
            totalMass = massThis + massOther;
            this.withBlock = withBlock;
        }

        float getVelocity() { return totalEulers / totalMass; }
    }

    void collide(Collision collision, int dir) { collisions[dir] = collision; }

    void collide(Entity other, int dir, boolean withBlock)
    {
        if (dir == UP || dir == DOWN)
        {
            float otherVel = other.getVelocityY();
            float thisVel = getVelocityY();
            /* This is falling faster or rising slower */
            if (dir == UP ? otherVel < thisVel : otherVel > thisVel) return;
            /* This is falling slower or rising faster */
            else if (collisions[dir] == null)
            {
                Collision collision = new Collision(thisVel, otherVel,
                        getMass(), other.getMass(), withBlock);
                collisions[dir] = collision;
                if (other instanceof Item)
                    ((Item) other).collide(collision, dir);
            }
        }
        else // if (dir == LEFT || dir == RIGHT)
        {
            float otherVel = other.getVelocityX();
            float thisVel = getVelocityX();
            /* This is moving right faster or moving left slower */
            if (dir == LEFT ? otherVel < thisVel : otherVel > thisVel) return;
                /* This is moving right slower or moving left faster */
            else if (collisions[dir] == null)
            {
                Collision collision = new Collision(thisVel, otherVel,
                        getMass(), other.getMass(), withBlock);
                collisions[dir] = collision;
                if (other instanceof Item)
                    ((Item) other).collide(collision, dir);
            }
        }
    }

    void takeDamage(float amount) { takeDamage((int) amount); }

    void takeDamage(int amount)
    {
        if (amount == 0) return;
        Print.green("Took " + amount + " points of damage");
        hitPoints -= amount;
    }

    /* This is the speed the player gets automatically when running or
     * crawling. Also used for the threshold when neutralizing velocity.
     * Fixes the glitch of not being able to run up a slope after stopping. */
    double minThreshSpeed = 1E-3;
}
