package Gameplay.entity;

import Gameplay.entity.Weapons.Infliction;
import Gameplay.EntityCollection;
import Util.GradeEnum;
import Util.Print;
import Util.Vec2;

import java.util.ArrayList;

public abstract class Item extends Entity
{
    private static final boolean DEBUG_PHYSICS = false;
    protected float mass;
    protected int hitPoints;

    // The entities that are in contact from each of 4 directions
    protected Entity[] touchEntity = new Entity[4];

    protected float slopeJumpBuffer = 0.1F;

    protected static float gravity = 25.0F;

    protected float airDrag = 4f;
    protected float waterDrag = 12f;

    protected boolean bumpingCeiling = false;
    protected boolean inWater = false, submerged = false;
    protected boolean isGrounded()
    {
        return touchEntity[DOWN] != null;
    }

    protected void debug()
    {
        Print.blue("velocity = (" + velocity.x + ", velY: " + velocity.y+")");
    }

    protected Item(float xPos, float yPos, float width, float height, float mass, int hitPoints, String[] spritePaths)
    {
        super(xPos, yPos, width, height, ShapeEnum.RECTANGLE, spritePaths);
        this.mass = mass;

        this.hitPoints = hitPoints;
    }

    public void update(EntityCollection<Entity> entities, float deltaSec)
    {
        //Since there are not yet actual items that have acceleration, inflictions or physics, this code cannot
        // be tested. Add in when player physics is working and we are ready to actually add an item to the test
        // level that actually uses this code.
        //resetAcceleration();
        //applyInflictions();
        //applyPhysics(entities, deltaSec);
    }

    /**
     * Location and velocity carry over from frame to frame.
     * Acceleration, however exists only when there is a force.
     * Thus, each frame, we set acceleration to 0, figure out which forces
     * are acting on it and add in acceleration for those forces. */
    protected void resetAcceleration()
    {
        acceleration.x=0;
        acceleration.y=0;
        if (touchEntity[DOWN] == null) acceleration.y = gravity;
        else
        {
            if (touchEntity[DOWN].getShape() == ShapeEnum.TRIANGLE_UP_R)
            {
                acceleration.x = gravity * touchEntity[DOWN].getCosSlope();
                acceleration.y = gravity * touchEntity[DOWN].getSinSlope();
            }
            if (touchEntity[DOWN].getShape() == ShapeEnum.TRIANGLE_UP_L)
            {
                acceleration.x = gravity * touchEntity[DOWN].getCosSlope();
                acceleration.y = gravity * touchEntity[DOWN].getSinSlope();
            }
        }
    }

    public float getMass() { return mass; }
    public void setMass(float mass) { this.mass = mass; }

    protected abstract void applyInflictions();



    //=================================================================================================================
    // Drag must be between 0 (no drag) and 1 (drops speed to 0)
    //=================================================================================================================
    protected void applyDrag(Vec2 velocity, float deltaSec)
    {
        float drag = airDrag;
        if (inWater) drag = waterDrag;
        drag *= deltaSec;
        drag = Vec2.bound(drag, 0,1);
        velocity.x *= (1-drag);
        velocity.y *= (1-drag);
    }




    Vec2 triggerContacts(EntityCollection<Entity> entityList, Vec2 goal, Vec2 finalVelocity, float deltaSec)
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
            // If inside a block of liquid
            if (withBlock && ((Block) entity).isLiquid()  && entity.withinBounds(this))
            {
                inWater = true;
                // If completely inside a block of liquid
                if (entity.surrounds(this)) submerged = true;
                continue;
            }

            int[] edge = entity.getTouchEdge(this, goal);

            // Actor made no contact with the entity
            if (edge[0] < 0) continue;

            // If touching a block of liquid
            if (withBlock && ((Block) entity).isLiquid())
            {
                inWater = true;
                continue;
            }

            // Actor has touched another non-liquid entity a this point

            //TODO: understand this
            //if (!entity.setTriggered(true)) continue;

            // Normal physical collisions only occur with Blocks
            if (!withBlock) continue;

            touchEntity[edge[0]] = entity;
            if (DEBUG_PHYSICS)  if (this == entityList.getPlayer(0)) System.out.println("    ***** HIT GROUND *****: edge = {" + edge[0] + "   " + edge[1]+ "}");

            if (edge[0] == UP)
            {
                goal.y = entity.getBottomEdge(goal.x) + getHeight() / 2;

                // Colliding with down-right slope or down-left slope
                if (edge[1] == LEFT || edge[1] == RIGHT)
                {
                    if (this.bumpingCeiling)
                    {
                        //TODO: understand this
                        //Vec2 newVel = entity.applySlope(originalVel);
                        //damage((float) newVel.minus(originalVel).mag()
                        //                * ((Block) entity).getHazardRating(),
                        //       ((Block) entity).getInfMaterials());
                        //setVelocity(newVel);
                    }
                }
                // Colliding with level surface from below
                else
                {
                    damage(Math.abs(getVelocityY())
                                    * ((Block) entity).getHazardRating(),
                            ((Block) entity).getInfMaterials());
                    finalVelocity.y =0;
                    if (DEBUG_PHYSICS) if (this == entityList.getPlayer(0)) System.out.println("         Bumped head");
                }
            }
            else if (edge[0] == DOWN)
            {
                goal.y = entity.getTopEdge(goal.x) - getHeight() / 2;

                // Colliding with up-right slope or up-left slope
                if (edge[1] == LEFT || edge[1] == RIGHT)
                {
                    if (!isGrounded())
                    {
                        //TODO: Understand this
                        //Vec2 newVel = entity.applySlope(originalVel);
                        //damage((float) newVel.minus(originalVel).mag()
                        //                * ((Block) entity).getHazardRating(),
                        //        ((Block) entity).getInfMaterials());
                        //setVelocity(newVel);
                    }
                }
                // Colliding with level surface from above
                else
                {
                    damage(Math.abs(getVelocityY())
                                    * ((Block) entity).getHazardRating(),
                            ((Block) entity).getInfMaterials());
                    finalVelocity.y =0;
                    if (DEBUG_PHYSICS) if (this == entityList.getPlayer(0)) System.out.println("         Bumped Ground");
                }
            }
            else if (edge[0] == LEFT)
            {
                goal.x = entity.getRightEdge() + getWidth() / 2;
                damage(Math.abs(getVelocityX())
                                * ((Block) entity).getHazardRating(),
                        ((Block) entity).getInfMaterials());
                finalVelocity.x =0;
            }
            else if (edge[0] == RIGHT)
            {
                goal.x = entity.getLeftEdge() - getWidth() / 2;
                damage(Math.abs(getVelocityX())
                                * ((Block) entity).getHazardRating(),
                        ((Block) entity).getInfMaterials());
                finalVelocity.x =0;
            }
        }

        this.bumpingCeiling = bumpingCeiling && touchEntity[UP] != null;

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

    // This is the speed the player gets automatically when running or
    // crawling. Also used for the threshold when neutralizing velocity.
    // Fixes the glitch of not being able to run up a slope after stopping.
    float minThreshSpeed = 1E-3f;

    // Minimum speed that they go down a slope after hitting terminally low speeds
    private float minSlopeSpeed = 0.01F;
}