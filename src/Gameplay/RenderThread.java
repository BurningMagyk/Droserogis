/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Joel Castellanos <joel@unm.edu>, 2018 - 2020
 */

package Gameplay;

import Gameplay.Entities.*;

import Gameplay.Entities.Weapons.Weapon;
import Importer.ImageResource;
import Menus.Main;
import Util.DebugEnum;
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
    private Image textureShadow = new Image("/Image/shadowTexture.png");

    private ImagePattern texturePatternBlock;
    private ImagePattern texturePatternShadow;

    private Image textureWater0 = new Image("/Image/water0.png");
    private Image textureWater1 = new Image("/Image/water1.png");
    private ImagePattern texturePatternWater0;
    private ImagePattern texturePatternWater1;

    private float cameraPosX, cameraPosY, cameraOffsetX, cameraOffsetY;
    private float cameraZoom, cameraZoomGoal, cameraZoomLerp = 0.05F;

    private float levelEditorScale = 1;

    private GraphicsContext gfx;
    private EntityCollection<Entity> entityList;

    private static final Color NEARBLACK = Color.rgb(10,4,3);
    //private static final Color NEARBLACK = Color.rgb(200,4,3);

    public RenderThread(GraphicsContext gfx, int viewWidth, int viewHeight)
    {
        this.gfx = gfx;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;

        System.out.println("view  =  ("+viewWidth+", "+viewHeight+")");

        for (int i=0; i<BACKGROUND_LAYER_COUNT; i++)
        {
            String name = "/Image/MossyWoods-Background_"+i+".png";
            //Print.purple("Loading Image: ["+name +"]");
            backgroundLayer[i] = new Image(name);
        }
        backgroundLayerOffsetY[0] = 0;
        backgroundLayerOffsetY[1] = 20;
        backgroundLayerOffsetY[2] = 60;
        backgroundLayerOffsetY[3] = 140;

        BlockType.loadBlockTypes();
    }

    public void renderAll(EntityCollection<Entity> entityList, float cameraPosX , float cameraPosY, float cameraOffsetX, float cameraOffsetY, float cameraZoom, float levelEditorScale)
    {
        this.entityList = entityList;
        this.cameraPosX = cameraPosX;
        this.cameraPosY = cameraPosY;
        this.cameraOffsetX = cameraOffsetX;
        this.cameraOffsetY = cameraOffsetY;
        this.cameraZoom = cameraZoom;

        this.levelEditorScale = levelEditorScale;

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
        gfx.restore();
        gfx.save();
        float backgroundScale = levelEditorScale + (1-levelEditorScale)/2f;

        gfx.scale(backgroundScale,backgroundScale);
        double layer3Left = 0;
        double layer3Bottom = 0;
        for (int i=0; i<BACKGROUND_LAYER_COUNT; i++)
        {
            double layerZoom = cameraZoom/(1+BACKGROUND_LAYER_COUNT-i);
            double x = 66 + viewWidth/2 - backgroundLayer[0].getWidth()/2 - (cameraPosX + cameraOffsetX)*layerZoom;
            double y = -120;
            if (i>0) y = (y - (cameraPosY + cameraOffsetY)*layerZoom/2) - backgroundLayerOffsetY[i];
            if (i == 3)
            {
                y+=50;
                layer3Left   = x;
                layer3Bottom = y+backgroundLayer[i].getHeight()-1;
            }
            //Print.blue("viewWidth="+viewWidth +"     x="+x + ", y="+y);
            gfx.drawImage(backgroundLayer[i], x,y);
        }

        double offsetX = -(cameraPosX + cameraOffsetX)*cameraZoom;
        double offsetY = -(cameraPosY + cameraOffsetY)*cameraZoom;
        texturePatternShadow = new ImagePattern(textureShadow, offsetX, offsetY, 256, 256, false);

        gfx.setFill(Color.BLACK);
        //gfx.fillRect(layer3Left, layer3Bottom, viewWidth-layer3Left, viewHeight-layer3Bottom);
        //gfx.fillRect(0, layer3Bottom, viewWidth/levelEditorScale, (viewHeight-layer3Bottom)/levelEditorScale);
        gfx.fillRect(0, layer3Bottom, viewWidth/levelEditorScale, 3000);

        //Print.purple("levelEditorScale="+levelEditorScale+"      (viewHeight-layer3Bottom)/levelEditorScale="+(viewHeight-layer3Bottom)/levelEditorScale);

        long currentNano = System.nanoTime();
        float shift =  (float)(currentNano*0.5e-8);
        texturePatternWater0 = new ImagePattern(textureWater0, offsetX+shift, offsetY+shift, 512, 512, false);
        texturePatternWater1 = new ImagePattern(textureWater1, offsetX+shift/2, offsetY-shift/2, 512, 512, false);
    }

    private void renderShadows()
    {
        gfx.restore();
        gfx.save();
        gfx.scale(levelEditorScale,levelEditorScale);
        double[] xx = new double[4];
        double[] yy = new double[4];
        gfx.setFill(texturePatternShadow);
        for (Entity entity : entityList)
        {
            if (!(entity instanceof Block)) continue;
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
//            if (true) continue;
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
            gfx.setFill(entity.getColor());

            if (entity.getShape().isTriangle())
            {
                for (int i = 0; i < 3; i++)
                {
                    xPos[i] = (entity.getVertexX(i) - cameraPosX + cameraOffsetX) * cameraZoom;
                    yPos[i] = (entity.getVertexY(i) - cameraPosY + cameraOffsetY) * cameraZoom;
                }
                //gfx.setFill(texturePatternBlock);
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
                                xxCorners[i] = (xxCorners[i] - cameraPosX + cameraOffsetX) * cameraZoom;
                                yyCorners[i] = (yyCorners[i] - cameraPosY + cameraOffsetY) * cameraZoom;
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
                        xCorners[i] = (xCorners[i] - cameraPosX + cameraOffsetX) * cameraZoom;
                        yCorners[i] = (yCorners[i] - cameraPosY + cameraOffsetY) * cameraZoom;
                    }
                    gfx.fillPolygon(xCorners, yCorners, 4);
                }
                else //not weapon
                {
                    double x = (entity.getX() - entity.getWidth() / 2 - cameraPosX + cameraOffsetX) * cameraZoom;
                    double y = (entity.getY() - entity.getHeight() / 2 - cameraPosY + cameraOffsetY) * cameraZoom;
                    double width = entity.getWidth() * cameraZoom;
                    double height = entity.getHeight() * cameraZoom;

                    if (entity instanceof Block)
                    {
                        Block block = (Block) entity;
                        if (block.isLiquid()) continue;
                        BlockType type = block.getBlockType();
                        if (type.name.equals("RECTANGLE"))// || Main.debugEnum == DebugEnum.GAMEPLAY)
                        {
                            gfx.setFill(NEARBLACK);
                            //if (Main.debugEnum == DebugEnum.GAMEPLAY) gfx.setFill(entity.getColor());
                            gfx.fillRect(x, y, width, height);
                        }
                        else
                        {
                            x-=type.left;
                            y-=type.top;
                            gfx.drawImage(block.getBlockType().getImage(), x, y);
                        }
                    }
                    else if (entity instanceof Actor)
                    {
                        Actor actor = (Actor) entity;
                        ImageResource imageResource = actor.getImage();
                        if(imageResource != null)
                        {
                            gfx.drawImage(imageResource.getImage(), x, y, -width, height);
                        }
                        else
                        {
                            gfx.setFill(entity.getColor());
                            gfx.fillRect(x, y, width, height);
                        }
                    }
                    else
                    {
                        gfx.setFill(entity.getColor());
                        gfx.fillRect(x, y, width, height);

                        // Draw text over items
                        if (entity instanceof Item)
                        {
                            for (DebugText debugText : ((Item) entity).getDebugText())
                            {
                                gfx.setFill(debugText.getColor());
                                gfx.fillText(debugText.getText(),
                                        x,y + ((0.5F - debugText.getDist()) * height / 2));
                            }
                        }
                    }
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
    }
}
