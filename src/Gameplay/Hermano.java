package Gameplay;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Hermano extends Application implements EventHandler<ActionEvent>
{


  private static final int WINDOW_WIDTH = 600;
  private static final int WINDOW_HEIGHT = 600;

  private static final int DRAW_WIDTH = WINDOW_WIDTH - 20;
  private static final int DRAW_HEIGHT = WINDOW_HEIGHT - 50;

  //(0,0) world space is the lower left corner of the screen.
  private float pixelsPerMeter = 10;

  private Canvas canvas;
  private GraphicsContext gtx;
  private boolean mouseIsDown = false;
  private Vec2 force;
  private static final Vec2 UP = new Vec2(0,1);
  private static final Vec2 RIGHT = new Vec2(1,0);
  private static final Vec2 LEFT = new Vec2(-1,0);
  private static final Vec2 ZERO = new Vec2(0,0);
  private boolean makeStandRight = false;
  private boolean makeStandLeft = false;
  private boolean standing = false;
  private boolean walkRight = false;
  private boolean walkLeft = false;

  //Box2D Objects
  private World world;
  private float timeStep = 1.0f/60.0f;
  private int velocityIterations = 6;
  private int positionIterations = 2;
  private Body[] bodyRight = new Body[10];
  private Body[] bodyLeft = new Body[10];
  private Body[] bodyList;

  private float boxSize = 1;
  private int boxSizePixels = (int)(boxSize*pixelsPerMeter);

  @Override
  public void start(Stage primaryStage)
  {
    primaryStage.setTitle("Hermano!");

    //Group root = new Group();
    canvas = new Canvas(DRAW_WIDTH, DRAW_HEIGHT);
    gtx = canvas.getGraphicsContext2D();

    canvas.setOnMousePressed(new EventHandler<MouseEvent>()
    {
      public void handle(MouseEvent event)
      {
        System.out.println("Mouse down");
        mouseIsDown = true;
        float x = getWorldX(event.getX());
        float y = getWorldY(event.getY());

        Vec2 position = bodyList[0].getPosition();
        force = new Vec2(x-position.x, y-position.y);
        force.normalize();
        force = force.mul(100);
        makeStandRight=false;
      }
    });


    canvas.setOnMouseReleased(new EventHandler<MouseEvent>()
    {
      public void handle(MouseEvent event)
      {
        System.out.println("Mouse up");
       mouseIsDown = false;
      }
    });


    canvas.setOnMouseDragged(new EventHandler<MouseEvent>()
    {
      public void handle(MouseEvent event)
      { //if (!mouseIsDown) return;
        //System.out.println(event.getX()+", " + event.getY());
        float x = getWorldX(event.getX());
        float y = getWorldY(event.getY());

        Vec2 position = bodyList[0].getPosition();
        force = new Vec2(x-position.x, y-position.y);
        force.normalize();
        force = force.mul(100);



      }
    });



    //canvas.requestFocus();

    //root.getChildren().add(canvas);
    //primaryStage.setScene(new Scene(root));
    //primaryStage.show();


    //StackPane drawPane = new StackPane();
    //drawPane.setStyle("-fx-background-color: #830303;");
    //drawPane.setMinSize(DRAW_WIDTH, DRAW_HEIGHT);

    StackPane root = new StackPane();


    root.getChildren().add(canvas);
    Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
    scene.setOnKeyPressed(new EventHandler<KeyEvent>()
    {
      public void handle(KeyEvent event)
      {
        //System.out.println("Keypressed: "+ event.getCode());
        if (event.getCode() == KeyCode.RIGHT)
        { makeStandRight = true;
          walkRight = true;

          makeStandLeft = false;
          walkLeft = false;
          if (bodyList == bodyLeft)
          {
            bodyList = bodyRight;
            Vec2 position = bodyLeft[0].getPosition();
            if (position.y<6) position.y=6;
            bodyRight[0].setTransform(position,0);
            bodyRight[1].setTransform(new Vec2(position.x,position.y-1),0);
            bodyRight[2].setTransform(new Vec2(position.x,position.y-2),0);
            bodyRight[3].setTransform(new Vec2(position.x,position.y-3),0);
            bodyRight[4].setTransform(new Vec2(position.x,position.y-4),0);
            bodyRight[5].setTransform(new Vec2(position.x,position.y-5),0);
            bodyRight[6].setTransform(new Vec2(position.x+1,position.y-1),0);
            bodyRight[7].setTransform(new Vec2(position.x+2,position.y-1),0);
            bodyRight[8].setTransform(new Vec2(position.x+1,position.y-4),0);
            bodyRight[9].setTransform(new Vec2(position.x+1,position.y-5),0);


            bodyLeft[0].setTransform(new Vec2(-100,position.y-1),0);
            bodyLeft[1].setTransform(new Vec2(-100,position.y-1),0);
            bodyLeft[2].setTransform(new Vec2(-100,position.y-2),0);
            bodyLeft[3].setTransform(new Vec2(-100,position.y-3),0);
            bodyLeft[4].setTransform(new Vec2(-100,position.y-4),0);
            bodyLeft[5].setTransform(new Vec2(-100,position.y-5),0);
            bodyLeft[6].setTransform(new Vec2(-101,position.y-1),0);
            bodyLeft[7].setTransform(new Vec2(-102,position.y-1),0);
            bodyLeft[8].setTransform(new Vec2(-101,position.y-4),0);
            bodyLeft[9].setTransform(new Vec2(-101,position.y-5),0);
          }
        }
        else if (event.getCode() == KeyCode.LEFT)
        { makeStandLeft = true;
          walkLeft = true;

          makeStandRight = false;
          walkRight = false;
          if (bodyList == bodyRight)
          {
            bodyList = bodyLeft;
            Vec2 position = bodyRight[0].getPosition();
            if (position.y<6) position.y=6;
            bodyLeft[0].setTransform(position,0);
            bodyLeft[1].setTransform(new Vec2(position.x,position.y-1),0);
            bodyLeft[2].setTransform(new Vec2(position.x,position.y-2),0);
            bodyLeft[3].setTransform(new Vec2(position.x,position.y-3),0);
            bodyLeft[4].setTransform(new Vec2(position.x,position.y-4),0);
            bodyLeft[5].setTransform(new Vec2(position.x,position.y-5),0);
            bodyLeft[6].setTransform(new Vec2(position.x-1,position.y-1),0);
            bodyLeft[7].setTransform(new Vec2(position.x-2,position.y-1),0);
            bodyLeft[8].setTransform(new Vec2(position.x-1,position.y-4),0);
            bodyLeft[9].setTransform(new Vec2(position.x-1,position.y-5),0);


            bodyRight[0].setTransform(new Vec2(-100,position.y-1),0);
            bodyRight[1].setTransform(new Vec2(-100,position.y-1),0);
            bodyRight[2].setTransform(new Vec2(-100,position.y-2),0);
            bodyRight[3].setTransform(new Vec2(-100,position.y-3),0);
            bodyRight[4].setTransform(new Vec2(-100,position.y-4),0);
            bodyRight[5].setTransform(new Vec2(-100,position.y-5),0);
            bodyRight[6].setTransform(new Vec2(-99,position.y-1),0);
            bodyRight[7].setTransform(new Vec2(-98,position.y-1),0);
            bodyRight[8].setTransform(new Vec2(-99,position.y-4),0);
            bodyRight[9].setTransform(new Vec2(-99,position.y-5),0);
          }
        }
      }
    });


    scene.setOnKeyReleased(new EventHandler<KeyEvent>()
    {
      public void handle(KeyEvent event)
      {
        System.out.println("Keyreleased: "+ event.getCode());
        makeStandRight = true;
        walkRight = false;
        makeStandLeft = false;
        walkLeft = false;
      }
    });


    primaryStage.setScene(scene);
    primaryStage.show();


    //============================= box2D ========================
    // Static Body
    Vec2  gravity = new Vec2(0,-9.8f);
    world = new World(gravity);
    BodyDef groundBodyDef = new BodyDef();
    BodyDef wallDef1 = new BodyDef();
    BodyDef wallDef2 = new BodyDef();

    groundBodyDef.position.set(0, -0.5f);
    wallDef1.position.set(-0.5f, (DRAW_HEIGHT/pixelsPerMeter)/2);
    wallDef2.position.set((DRAW_WIDTH/pixelsPerMeter)+0.5f, (DRAW_HEIGHT/pixelsPerMeter)/2);


    Body groundBody = world.createBody(groundBodyDef);
    Body wallBody1 = world.createBody(wallDef1);
    Body wallBody2 = world.createBody(wallDef2);

    PolygonShape groundBox = new PolygonShape();
    PolygonShape wallBox = new PolygonShape();

    float groundWidth = DRAW_WIDTH/pixelsPerMeter;
    float groundThickness = 1;
    float groundDensity = 1;
    groundBox.setAsBox(groundWidth, groundThickness);
    wallBox.setAsBox(groundThickness,DRAW_HEIGHT/pixelsPerMeter);


    //groundBox.setAsBox(groundWidth, groundThickness, new Vec2(0,-groundThickness),0);
    groundBody.createFixture(groundBox,groundDensity);
    wallBody1.createFixture(wallBox,groundDensity);
    wallBody2.createFixture(wallBox,groundDensity);

    // Dynamic Body
    float centerX = (DRAW_WIDTH/pixelsPerMeter)/2;
    bodyRight[0] = makeBodyPart(centerX, 55);
    bodyRight[1] = makeBodyPart(centerX, 54);
    bodyRight[2] = makeBodyPart(centerX, 53);
    bodyRight[3] = makeBodyPart(centerX, 52);
    bodyRight[4] = makeBodyPart(centerX, 51);
    bodyRight[5] = makeBodyPart(centerX, 50);

    bodyRight[6] = makeBodyPart(centerX+1, 54);
    bodyRight[7] = makeBodyPart(centerX+2, 54);

    bodyRight[8] = makeBodyPart(centerX+1, 51);
    bodyRight[9] = makeBodyPart(centerX+1, 50);

    addJoint(bodyRight,0,1);
    addJoint(bodyRight,1,2);
    addJoint(bodyRight,2,3);
    addJoint(bodyRight,3,4);
    addJoint(bodyRight,4,5);
    addJoint(bodyRight,1,6);
    addJoint(bodyRight,6,7);
    addJoint(bodyRight,4,8);
    addJoint(bodyRight,8,9);

    bodyList = bodyRight;

    bodyLeft[0] = makeBodyPart(-100, 55);
    bodyLeft[1] = makeBodyPart(-100, 54);
    bodyLeft[2] = makeBodyPart(-100, 53);
    bodyLeft[3] = makeBodyPart(-100, 52);
    bodyLeft[4] = makeBodyPart(-100, 51);
    bodyLeft[5] = makeBodyPart(-100, 50);

    bodyLeft[6] = makeBodyPart(-101, 54);
    bodyLeft[7] = makeBodyPart(-102, 54);

    bodyLeft[8] = makeBodyPart(-101, 51);
    bodyLeft[9] = makeBodyPart(-101, 50);

    addJoint(bodyLeft,0,1);
    addJoint(bodyLeft,1,2);
    addJoint(bodyLeft,2,3);
    addJoint(bodyLeft,3,4);
    addJoint(bodyLeft,4,5);
    addJoint(bodyLeft,1,6);
    addJoint(bodyLeft,6,7);
    addJoint(bodyLeft,4,8);
    addJoint(bodyLeft,8,9);


    MainGameLoop mainGameLoop = new MainGameLoop();
    mainGameLoop.start();
  }


  private Body makeBodyPart(float x, float y)
  {
    BodyDef bodyDef = new BodyDef();
    bodyDef.type = BodyType.DYNAMIC;
    bodyDef.position.set(x, y);
    Body body = world.createBody(bodyDef);
    PolygonShape dynamicBox = new PolygonShape();
    dynamicBox.setAsBox(boxSize/2, boxSize/2);
    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = dynamicBox;
    fixtureDef.density = 1;
    fixtureDef.friction = 0.3f;
    body.createFixture(fixtureDef);
    return body;
  }

  private void addJoint(Body[] body, int i, int k)
  {
    RevoluteJointDef jointDef = new RevoluteJointDef();
    jointDef.initialize(body[i], body[k], body[i].m_xf.p);
    RevoluteJoint joint = (RevoluteJoint) world.createJoint(jointDef);
    joint.enableLimit(true);
    joint.setLimits(0,3.14f/4);
  }

  public static float magnitudeSqu(Vec2 v)
  {
    return v.x*v.x + v.y*v.y;
  }


  @Override
  public void handle(ActionEvent event)
  {
    Object source = event.getSource();
  }


  private int getScreenX(float x)
  {
    return (int)Math.round(x*pixelsPerMeter);
  }

  private int getScreenY(float y)
  {
    return DRAW_HEIGHT - (int)Math.round(y*pixelsPerMeter);
  }

  private float getWorldX(double x) { return (float)(x/pixelsPerMeter); }

  private float getWorldY(double y) {  return (float)((DRAW_HEIGHT - y)/pixelsPerMeter); }

  public static void main(String[] args)
  {
    launch(args);
  }


  class MainGameLoop extends AnimationTimer
  {
    public void handle(long now)
    {

      gtx.setFill(Color.WHITE);
      gtx.fillRect(0, 0, DRAW_WIDTH, DRAW_HEIGHT);

      gtx.setFill(Color.DARKRED);
      gtx.setStroke(Color.BLUE);
      gtx.setLineWidth(5);


      if (!standing)
      { world.step(timeStep, velocityIterations, positionIterations);
      }

      for (int i=0; i<bodyList.length; i++)
      {
        Vec2 position = bodyList[i].getPosition();
        if (i==0)
        {
          if (mouseIsDown) bodyList[0].applyForce(force,position);
          else if (makeStandRight || makeStandLeft)
          {
            if (position.y < 6)
            {
              //System.out.println("stand");
              bodyList[0].applyForce(UP.mul(100),position);
            }
            else
            {
              float speed = magnitudeSqu(bodyList[0].getLinearVelocity());
              if (walkRight)
              {
                bodyList[0].applyForce(RIGHT.mul(50),position);
              }
              else if (walkLeft)
              {
                bodyList[0].applyForce(LEFT.mul(50),position);
              }
              else if (speed > 0.01)
              {
                bodyList[0].setLinearVelocity(ZERO);
                bodyList[0].setAngularVelocity(0);
              }
              else
              {
                //makeStandRight=false;
              }
              //standing = true;
              //for (int k=0; k<bodyList.length; k++)
              //{
              //  bodyList[k].setLinearVelocity(ZERO);
              //  bodyList[k].setAngularVelocity(0);
              //}
            }
          }
          gtx.setFill(Color.DARKRED);
        }
        else gtx.setFill(Color.GREEN);

        int x = getScreenX(position.x)-boxSizePixels/2;
        int y = getScreenY(position.y)-boxSizePixels/2;
        gtx.fillRect(x, y, boxSizePixels,boxSizePixels);


      }

    }
  }
}

