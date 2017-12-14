package takro;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

class Ball {
    private static final float DENSITY = 0.9f;
    private static final float FRICTION = 0.9f;
    private static final float RESTITUTION = 0.5f;
    
    private Color fillColor, strokeColor;
    private Body body;
    private float size;
    
    public Ball(TakroWorld world, float size, Vec2 pos, Vec2 vel, String color) {
        this.strokeColor = Color.web(color);
        this.fillColor = Color.web(color,0.2);
        this.size = size;

        CircleShape shape = new CircleShape();
        shape.m_radius = size/2;
//        PolygonShape shape = new PolygonShape();
//        shape.setAsBox(size/2, size/2);
        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = DENSITY;
        fd.restitution = RESTITUTION;
        fd.friction = FRICTION;
        
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.setPosition(pos);
        bd.setLinearVelocity(vel);
        this.body = world.createBody(bd);
        this.body.createFixture(fd);
        this.body.setUserData(this);
    }
    public void draw(GraphicsContext gc) {
        Vec2 center = body.getPosition();
        gc.setFill(fillColor);
        gc.setStroke(strokeColor);
        gc.setLineWidth(.01);
        gc.save();
        gc.translate(center.x, center.y);
        gc.rotate(Math.toDegrees(body.getAngle()));
        gc.fillOval(-size/2,-size/2,size,size);
        gc.strokeOval(-size/2,-size/2,size,size);
//        gc.fillRect(-size/2,-size/2,size,size);
//        gc.strokeRect(-size/2,-size/2,size,size);
        gc.strokeLine(0, 0, size/2, 0);
        gc.restore();
    }
}