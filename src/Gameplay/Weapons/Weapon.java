package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Gameplay.Entity;
import Gameplay.Item;
import Util.Print;
import Util.Vec2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Weapon extends Item
{
    Vec2 relativePos = new Vec2(1F, 0F);
    Vec2 wieldPos;
    DirEnum dirFace = DirEnum.RIGHT;
    public Vec2 wieldDimsDefault[] = {
            new Vec2(-getWidth() / 2, -getHeight() / 2),
            new Vec2(+getWidth() / 2, -getHeight() / 2),
            new Vec2(+getWidth() / 2, +getHeight() / 2),
            new Vec2(-getWidth() / 2, +getHeight() / 2)};
    private Vec2 wieldDims[] = new Vec2[4];
    private float theta = 0;

    private boolean ballistic = true;
    private Map<Integer, Operation> keyCombos;
    private Style style = Style.DEFAULT;
    private Operation currentOp;

    Weapon(float xPos, float yPos, float width, float height)
    {
        super(xPos, yPos, width, height);
        wieldPos = getPosition();
        keyCombos = new HashMap<>();

        for (int i = 0; i < wieldDims.length; i++)
        { wieldDims[i] = wieldDimsDefault[i].clone(); }
    }

    @Override
    protected void update(ArrayList<Entity> entities, float deltaSec)
    {
        if (ballistic) super.update(entities, deltaSec);
        else if (currentOp != null && currentOp.run(deltaSec))
        {
            currentOp = null;
        }
        // to check line intersections:
        // https://stackoverflow.com/questions/4977491/determining-if-two-line-segments-intersect/4977569#4977569
    }

    public void setTheta(float theta)
    {
        for (int i = 0; i < wieldDims.length; i++)
        { wieldDims[i] = wieldDimsDefault[i].clone(); }

        Vec2.setTheta(dirFace.getHoriz().getSign() * theta);
        for (Vec2 wieldDim : wieldDims) { wieldDim.rotate(); }
        this.theta = theta;
    }

    public void updatePosition(Vec2 p, Vec2 dims, DirEnum dir)
    {
        setPosition(p);
        wieldPos = new Vec2(p.x + dims.x * relativePos.x
                * (dir.getVert() == DirEnum.UP ? 0 : dir.getHoriz().getSign()),
                p.y + dims.y * relativePos.y);
        setTheta(theta);
        dirFace = dir;
    }

    /**
     * Depending on keyCombo and currentSytle, will cause the weapon to do
     * something.
     */
    public void operate(boolean pressed, int keyCombo, int dirHoriz, int dirVert)
    {
        if (!pressed) return; /* Temporary */
        Operation op = keyCombos.get(keyCombo);
        if (op != null)
        {
            op.start(DirEnum.get(dirHoriz, dirVert));
            currentOp = op;
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
        ballistic = false;
        return this;
    }

    boolean isBallistic() { return ballistic; }
    public Vec2 getWieldPos() { return wieldPos; }
    public Vec2[] getCorners()
    {
        Vec2[] corners = wieldDims.clone();
        for (int i = 0; i < wieldDims.length; i++)
        {
            corners[i] = new Vec2(wieldDims[i].x + wieldPos.x,
                    wieldDims[i].y + wieldPos.y);
        }
        return corners;
    }
    Style getStyle() { return style; }

    void setRelativePos(Vec2 p)
    {
        relativePos.x = p.x;
        relativePos.y = p.y;
    }

    interface Operation
    {
        String getName();

        void start(DirEnum direction);

        /** Returns true if the operation finished */
        boolean run(float deltaSec);
    }

    void addOperation(Operation op, int keyCombo)
    {
        keyCombos.put(keyCombo, op);
    }

    void travel()
    {

    }
}