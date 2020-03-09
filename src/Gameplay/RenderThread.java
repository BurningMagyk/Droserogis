package Gameplay;

import Gameplay.Weapons.Weapon;
import Importer.ImageResource;
import Util.Print;
import Util.Vec2;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

//TODO, make extend thread and use as thread
public class RenderThread
{
    private int viewWidth, viewHeight;

    private final int BACKGROUND_LAYER_COUNT = 4;
    private Image[] backgroundLayer = new Image[BACKGROUND_LAYER_COUNT];
    private int[] backgroundLayerOffsetY = new int[BACKGROUND_LAYER_COUNT];
    private Image textureBlock = new Image("/Image/SkullTexture.png");
    private Image textureShadow = new Image("/Image/shadowTexture.png");
    private Image textureGround = new Image("/Image/ground.png");

    private ImagePattern texturePatternBlock;
    private ImagePattern texturePatternShadow;
    private ImagePattern texturePatternGround;

    private Image textureWater0 = new Image("/Image/water0.png");
    private Image textureWater1 = new Image("/Image/water1.png");
    private ImagePattern texturePatternWater0;
    private ImagePattern texturePatternWater1;

    private float cameraX, cameraY;
    private float cameraZoom, cameraZoomGoal, cameraZoomLerp = 0.05F;

    private GraphicsContext gfx;
    private EntityCollection<Entity> entityList;

    public RenderThread(GraphicsContext gfx, int viewWidth, int viewHeight)
    {
        this.gfx = gfx;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;

        System.out.println("view  =  ("+viewWidth+", "+viewHeight+")");

        for (int i=0; i<BACKGROUND_LAYER_COUNT; i++)
        {
            String name = "/Image/MossyWoods-Background_"+i+".png";
            Print.purple("Loading Image: ["+name +"]");
            backgroundLayer[i] = new Image(name);
        }
        backgroundLayerOffsetY[0] = 0;
        backgroundLayerOffsetY[1] = 20;
        backgroundLayerOffsetY[2] = 60;
        backgroundLayerOffsetY[3] = 140;
    }

    public void renderAll(EntityCollection<Entity> entityList, float cameraPosX , float cameraPosY, float cameraZoom)
    {
        this.entityList = entityList;
        this.cameraX = cameraPosX;
        this.cameraY = cameraPosY;
        this.cameraZoom = cameraZoom;

        renderBackground();
        renderShadows();
        renderEntities();
        renderSecondWaterLayer();
    }


    //=================================================================================================================
    // The 2.5D background is drawn in 4 layers with increasing parallax shift from back to front.
    // If the front most image layer does not reach the bottom of the draw area, then the lower section is tiled with
    //    texturePatternGround.
    //
    //=================================================================================================================
    private void renderBackground()
    {
        //gfx.setFill(Color.GREY);
        //gfx.fillRect(0, 0, viewWidth, viewHeight);


        //System.out.println("\n=======");
        //double worldWidth = (cameraZoom*(entityList.getBoundsRight() - entityList.getBoundsLeft()));

        //double frontMostLayerWidth = backgroundLayer[BACKGROUND_LAYER_COUNT-1].getWidth();
        //Print.purple("frontMostLayerWidth =" + frontMostLayerWidth +"      worldWidth="+worldWidth);

        double layer3Left = 0;
        double layer3Bottom = 0;

        for (int i=0; i<BACKGROUND_LAYER_COUNT; i++)
        {
            double layerCenter = backgroundLayer[i].getWidth()/2.0;


            double layerShift = 95f/(1+BACKGROUND_LAYER_COUNT-i);
            double x = -400 + viewWidth/2 - backgroundLayer[0].getWidth()/2 - cameraX*layerShift;
            double y = 0;
            if (i>0) y = (-100 -cameraY*layerShift/2) - backgroundLayerOffsetY[i];
            if (i == 3)
            {
                layer3Left   = x;
                layer3Bottom = y+backgroundLayer[i].getHeight()-1;
            }
            //Print.blue("viewWidth="+viewWidth +"     x="+x + ", y="+y);
            gfx.drawImage(backgroundLayer[i], x,y);
        }

        double offsetX = (-cameraX)*cameraZoom;
        double offsetY = (-cameraY)*cameraZoom;
        texturePatternBlock = new ImagePattern(textureBlock, offsetX, offsetY, 256, 256, false);
        texturePatternShadow = new ImagePattern(textureShadow, offsetX, offsetY, 256, 256, false);
        texturePatternGround = new ImagePattern(textureGround, layer3Left, layer3Bottom, 512, 512, false);

        gfx.setFill(texturePatternGround);
        //gfx.fillRect(layer3Left, layer3Bottom, viewWidth-layer3Left, viewHeight-layer3Bottom);
        gfx.fillRect(0, layer3Bottom, viewWidth, viewHeight-layer3Bottom);

        long currentNano = System.nanoTime();
        float shift =  (float)(currentNano*0.5e-8);
        texturePatternWater0 = new ImagePattern(textureWater0, offsetX+shift, offsetY+shift, 512, 512, false);
        texturePatternWater1 = new ImagePattern(textureWater1, offsetX+shift/2, offsetY-shift/2, 512, 512, false);

    }

    private void renderShadows()
    {/*
        double[] xx = new double[4];
        double[] yy = new double[4];
        gfx.setFill(texturePatternShadow);
        for (Entity entity : entityList)
        {
            if ((entity instanceof Block) == false) continue;
            Block block = ((Block) entity);
            double x = (entity.getX() - entity.getWidth() / 2 - cameraPosX + cameraOffsetX) * cameraZoom;
            double y = (entity.getY() - entity.getHeight() / 2 - cameraPosY + cameraOffsetY) * cameraZoom;
            double width = entity.getWidth() * cameraZoom;
            float height = entity.getHeight() * cameraZoom;

            if (block.isLiquid())
            {
                gfx.setFill(texturePatternWater0);
                gfx.fillRect(x, y, width, height);
                gfx.setFill(texturePatternShadow);
                continue;
            }

            Entity.ShapeEnum shape = entity.getShape();
            double shadowL = (x-viewWidth/2)/30.0;
            double shadowR = (x+width-viewWidth/2)/30.0;
            //Top surface
            if (shape != Entity.ShapeEnum.TRIANGLE_UP_L && shape != Entity.ShapeEnum.TRIANGLE_UP_R)
            {
                xx[0] = x + width;           yy[0] = y;
                xx[1] = x;                   yy[1] = y;
                xx[2] = x + shadowL;         yy[2] = y - 24;
                xx[3] = x + width + shadowR; yy[3] = y - 24;
                gfx.fillPolygon(xx, yy, 4);
            }

            //Top surface
            else if (shape == Entity.ShapeEnum.TRIANGLE_UP_L )
            {
                xx[0] = x;                     yy[0] = y+height;
                xx[1] = x + width;             yy[1] = y;
                xx[2] = x + width;             yy[2] = y - 24;
                xx[3] = x;                     yy[3] = y + height - 24;
                gfx.fillPolygon(xx, yy, 4);
            }

            //Top surface
            else if (shape == Entity.ShapeEnum.TRIANGLE_UP_R)
            {
                double triShadow = Math.min(shadowL, 0);
                if (x>viewWidth/2) triShadow = Math.max(shadowR, 0);
                xx[0] = x + width;             yy[0] = y+height;
                xx[1] = x;                     yy[1] = y;
                xx[2] = x;                     yy[2] = y - 24;
                xx[3] = x + width;             yy[3] = y + height - 24;
                gfx.fillPolygon(xx, yy, 4);
            }


            if (shadowL<0)
            {
                if (shape != Entity.ShapeEnum.TRIANGLE_DW_L && shape != Entity.ShapeEnum.TRIANGLE_UP_L)
                {
                    xx[0] = x + shadowL;       yy[0] = y - 24;
                    xx[1] = x;                 yy[1] = y;
                    xx[2] = x;                 yy[2] = y + height;
                    xx[3] = x + shadowL;       yy[3] = y + height - 24;
                    if (shape == Entity.ShapeEnum.TRIANGLE_UP_R) yy[1] = yy[0];
                    gfx.fillPolygon(xx, yy, 4);
                }
            }
            if (shadowR>0)
            {
                if (shape != Entity.ShapeEnum.TRIANGLE_DW_R && shape != Entity.ShapeEnum.TRIANGLE_UP_R)
                {
                    xx[0] = x + width;             yy[0] = y;
                    xx[1] = x + width + shadowR;   yy[1] = y - 24;
                    xx[2] = x + width + shadowR;   yy[2] = y + height - 24;
                    xx[3] = x + width;             yy[3] = y + height;
                    if (shape == Entity.ShapeEnum.TRIANGLE_UP_L) yy[0] = yy[1];
                    gfx.fillPolygon(xx, yy, 4);
                }
            }
        }
        */
    }




    //===============================================================================================================
    // Until we utilize sprites, we'll test the game by drawing shapes that match the
    // blocks' hitboxes. The blocks' colors will help indicate what state they're in.
    //===============================================================================================================
    private void renderEntities()
    {
        double[] xPos = new double[3];
        double[] yPos = new double[3];
        for (Entity entity : entityList)
        {
            ImageResource sprite = entity.getSprite();
            //if (entity instanceof Actor)
            //{
            //    System.out.println(entity + "   sprite="+sprite);
            //}

            gfx.setFill(entity.getColor());

            if (entity.getShape().isTriangle())
            {
                for (int i = 0; i < 3; i++)
                {
                    xPos[i] = viewWidth/2  + (entity.getVertexX(i) - cameraX) * cameraZoom;
                    yPos[i] = viewHeight/2 + (entity.getVertexY(i) - cameraY) * cameraZoom;
                }
                gfx.setFill(texturePatternBlock);
                gfx.fillPolygon(xPos, yPos, 3);
            }
            else if (entity.getShape() == Entity.ShapeEnum.RECTANGLE)
            {
                if (entity instanceof Weapon)
                {
                    Vec2[][] cc = ((Weapon) entity).getClashShapeCorners();
                    if (cc != null)
                    {
                        gfx.setFill(Color.rgb(120, 170, 170));
                        for (int j = 0; j < cc.length; j++)
                        {
                            double[] xxCorners = {cc[j][0].x, cc[j][1].x, cc[j][2].x, cc[j][3].x};
                            double[] yyCorners = {cc[j][0].y, cc[j][1].y, cc[j][2].y, cc[j][3].y};
                            for (int i = 0; i < xxCorners.length; i++)
                            {
                                xxCorners[i] = viewWidth/2 + (xxCorners[i] - cameraX ) * cameraZoom;
                                yyCorners[i] = viewHeight/2 + (yyCorners[i] - cameraY ) * cameraZoom;
                            }
                            gfx.fillPolygon(xxCorners, yyCorners, 4);
                        }
                    }

                    gfx.setFill(entity.getColor());
                    Vec2[] c = ((Weapon) entity).getShapeCorners();
                    double[] xCorners = {c[0].x, c[1].x, c[2].x, c[3].x};
                    double[] yCorners = {c[0].y, c[1].y, c[2].y, c[3].y};
                    for (int i = 0; i < xCorners.length; i++)
                    {
                        xCorners[i] = viewWidth/2 + (xCorners[i] - cameraX) * cameraZoom;
                        yCorners[i] = viewHeight/2 + (yCorners[i] - cameraY) * cameraZoom;
                    }
                    gfx.fillPolygon(xCorners, yCorners, 4);
                }
                else
                {
                    double x = viewWidth/2 + (entity.getX() - entity.getWidth() / 2 - cameraX) * cameraZoom;
                    double y = viewHeight/2 + (entity.getY() - entity.getHeight() / 2 - cameraY) * cameraZoom;
                    double width = entity.getWidth() * cameraZoom;
                    double height = entity.getHeight() * cameraZoom;

                    if (entity instanceof Block)
                    {
                        if (((Block) entity).isLiquid()) continue;
                        else gfx.setFill(texturePatternBlock);
                    }
                    else gfx.setFill(entity.getColor());

                    gfx.fillRect(x, y, width, height);
                }
            }

            /* Draws vertical and horizontal lines through the camera for debugging */
            //gfx.setFill(Color.BLACK);
            //gfx.strokeLine(0, viewHeight / 2F, viewWidth, viewHeight / 2F);
            //gfx.strokeLine(viewWidth / 2F, 0, viewWidth / 2F, viewHeight);
        }
    }




    private void renderSecondWaterLayer()
    {
        /*
        for (Entity entity : entityList)
        {
            if (entity instanceof Block)
            {
                if (((Block) entity).isLiquid())
                {
                    double x = (entity.getX() - entity.getWidth() / 2 - cameraPosX + cameraOffsetX) * cameraZoom;
                    double y = (entity.getY() - entity.getHeight() / 2 - cameraPosY + cameraOffsetY) * cameraZoom;
                    double width = entity.getWidth() * cameraZoom;
                    double height = entity.getHeight() * cameraZoom;
                    gfx.setFill(texturePatternWater1);
                    gfx.fillRect(x, y, width, height);
                }
            }
        }

         */
    }

}
