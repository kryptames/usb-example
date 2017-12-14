package takro;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import practicum.McuWithPeriBoard;

class Player {
    private static final float SMOOTHING_FACTOR = 0.95f;
    private static final float HOVER_HEIGHT = 0.5f;
    private static final Vec2[] SHAPE_VERTICES = {
            new Vec2(-0.80f, 0.10f),
            new Vec2(-1.00f, 0.00f),
            new Vec2( 1.00f, 0.00f),
            new Vec2( 0.80f, 0.10f),
    };
    
    private float leftBound, rightBound;
    private McuWithPeriBoard controller;
    private Color strokeColor, fillColor;
    private Body body;
    private int minValue, maxValue;
    private double[] xpoints;
    private double[] ypoints;
    
    public Player(TakroWorld world, McuWithPeriBoard controller, 
            int minValue, int maxValue, String color) {
        this.controller = controller;
        this.strokeColor = Color.web(color);
        this.fillColor = Color.web(color, 0.2);
        this.minValue = minValue;
        this.maxValue = maxValue;
        
        Vec2[] bound = world.getBound();
        this.leftBound = bound[0].x;
        this.rightBound = bound[1].x;

        BodyDef bd = new BodyDef();
        bd.type = BodyType.KINEMATIC;
        bd.setPosition(new Vec2(0,HOVER_HEIGHT));

        PolygonShape box = new PolygonShape();
        box.set(SHAPE_VERTICES,SHAPE_VERTICES.length);
        FixtureDef fd = new FixtureDef();
        fd.shape = box;

        this.body = world.createBody(bd);
        this.body.createFixture(fd);
        this.body.setUserData(this);

        xpoints = new double[SHAPE_VERTICES.length];
        ypoints = new double[SHAPE_VERTICES.length];
        for (int i=0; i<SHAPE_VERTICES.length; i++) {
            xpoints[i] = SHAPE_VERTICES[i].x;
            ypoints[i] = SHAPE_VERTICES[i].y;
        }
    }
    public void update(float dt) {
        Vec2 pos = body.getPosition();
        float x;
        
        // map range [minValue,maxValue] on to [leftBound,rightBound]
        x = ((float)controller.getLight()-minValue)/(maxValue-minValue)*(rightBound-leftBound)
          + leftBound;
        
        float new_x = pos.x*SMOOTHING_FACTOR + x*(1-SMOOTHING_FACTOR);
        float dx = new_x - pos.x;
        
        // Box2D does not allow body's position to be set directly to avoid instant teleport,
        // so we must set its velocity instead.
        body.setLinearVelocity(new Vec2(dx/dt,0));
    }
    public void draw(GraphicsContext gc) {
        Vec2 pos = body.getPosition();
        gc.save();
        gc.translate(pos.x, pos.y);
        gc.setStroke(strokeColor);
        gc.setFill(fillColor);
        gc.setLineWidth(.01);
        gc.fillPolygon(xpoints,ypoints,SHAPE_VERTICES.length);
        gc.strokePolygon(xpoints,ypoints,SHAPE_VERTICES.length);
        gc.restore();
    }
}