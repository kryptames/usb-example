package takro;

import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Basket {
    private Color strokeColor;
    private Body body;
    private float width, height;
    
    public Basket(TakroWorld world, Vec2 pos, float width, float height, String color) {
        this.strokeColor = Color.web(color);
        this.width = width;
        this.height = height;
        
        BodyDef bd = new BodyDef();
        bd.type = BodyType.STATIC;
        bd.position.set(pos);
        this.body = world.createBody(bd);

        EdgeShape shape = new EdgeShape();
        FixtureDef fd = new FixtureDef();
        
        // left
        shape.set(new Vec2(-width/2,0), new Vec2(-width/2,-height));
        fd.shape = shape;
        body.createFixture(fd);
        
        // bottom
        shape.set(new Vec2(-width/2,-height), new Vec2(width/2,-height));
        fd.shape = shape;
        body.createFixture(fd);
        
        // right
        shape.set(new Vec2(width/2,-height), new Vec2(width/2,0));
        fd.shape = shape;
        body.createFixture(fd);
    }
    
    public void draw(GraphicsContext gc) {
        gc.setStroke(strokeColor);
        gc.setLineWidth(0.02);
        Vec2 pos = body.getPosition();
        gc.strokeLine(pos.x-width/2, pos.y, pos.x-width/2, pos.y-height);
        gc.strokeLine(pos.x-width/2, pos.y-height, pos.x+width/2, pos.y-height);
        gc.strokeLine(pos.x+width/2, pos.y-height, pos.x+width/2, pos.y);
    }
}
