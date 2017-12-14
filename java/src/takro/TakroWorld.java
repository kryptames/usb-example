package takro;

import java.util.LinkedList;
import java.util.List;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import practicum.McuWithPeriBoard;

class TakroWorld extends World implements ContactListener {
    private static final Vec2 GRAVITY = new Vec2(0.0f,-9.8f);
    private static final Vec2 SERVING_POSITION = new Vec2(2,5);
    private static final Vec2 SERVING_VELOCITY = new Vec2(-4,0);
    private Canvas canvas;
    private Affine transform;
    private List<Ball> balls;
    private List<Player> players;
    private List<Basket> baskets;

    public TakroWorld(Canvas canvas) {
        super(GRAVITY);
        this.canvas = canvas;
        this.balls = new LinkedList<Ball>();
        this.players = new LinkedList<Player>();
        this.baskets = new LinkedList<Basket>();
        
        // Prepare an affine transform to translate and scale canvas coordinate system
        // such that 
        // - The origin (0,0) is located at the bottom center of the screen
        // - Positive y-axis goes upward (similar to math system)
        // - One meter is equivalent to 100 pixels on screen
        //
        //              ^ +y
        //              |
        //              |
        //              |
        //              |
        // -x           | (0,0)     +x
        // <------------+------------>
        //
        this.transform = new Affine();
        this.transform.appendTranslation(this.canvas.getWidth()/2,this.canvas.getHeight());
        this.transform.appendScale(100,-100);

        this.baskets.add(new Basket(this,new Vec2(2.5f,4),2,2,"black"));
        createFourWalls();
        
        setContactListener(this);
    }
    public Vec2 worldToScreen(Vec2 point) {
        Point2D worldPos = transform.transform(point.x, point.y);
        return new Vec2((float)worldPos.getX(), (float)worldPos.getY());
    }
    public Vec2 screenToWorld(Vec2 point) {
        Point2D screenPos;
        try {
            screenPos = transform.inverseTransform(point.x, point.y);
            return new Vec2((float)screenPos.getX(),(float)screenPos.getY());
        } catch (NonInvertibleTransformException e) {
            return null;
        }
    }
    public Vec2[] getBound() {
        // returns [(x1,y1),(x2,y2)] where
        // - (x1,y1) is the bottom left coordinates
        // - (x2,y2) is the top right coordinates
        Vec2 bottomLeft = screenToWorld(
                new Vec2(0,(float)canvas.getHeight()));
        Vec2 topRight = screenToWorld(
                new Vec2((float)canvas.getWidth(),0));
        Vec2[] bound = {bottomLeft,topRight};
        return bound;
    }
    private void createFourWalls() {
        Vec2 topLeft = screenToWorld(
                new Vec2(0,0));
        Vec2 topRight = screenToWorld(
                new Vec2((float)canvas.getWidth(),0));
        Vec2 bottomLeft = screenToWorld(
                new Vec2(0,(float)canvas.getHeight()));
        Vec2 bottomRight = screenToWorld(
                new Vec2((float)canvas.getWidth(),(float)canvas.getHeight()));
        addWall(bottomLeft,bottomRight); // floor
        addWall(topLeft,bottomLeft);     // left wall
        addWall(topRight,bottomRight);   // right wall
        addWall(topLeft,topRight);       // ceiling
    }
    public Body addWall(Vec2 start, Vec2 end) {
        BodyDef bd = new BodyDef();
        bd.type = BodyType.STATIC;
        bd.position.set(new Vec2(0,0));
        EdgeShape shape = new EdgeShape();
        shape.set(start,end);
        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        Body body = createBody(bd);
        body.createFixture(fd);
        return body;
    }
    public void addPlayer(McuWithPeriBoard controller, int minValue, int maxValue, String color) {
        players.add(new Player(this,controller,minValue,maxValue,color));
    }
    public void serveBall() {
        balls.add(new Ball(this, 0.2f, SERVING_POSITION, SERVING_VELOCITY, "green"));
    }
    public void update(float dt) {
        this.step(dt, 8, 3);  // 8 and 3 are recommended by JBox2D
        for (Player p: players)
            p.update(dt);
    }
    public void draw() {
        GraphicsContext gc = this.canvas.getGraphicsContext2D();
        gc.clearRect(0,0,this.canvas.getWidth(),this.canvas.getHeight());
        gc.save();
        gc.setTransform(transform);
        for (Ball b: this.balls)
            b.draw(gc);
        for (Player p: this.players)
            p.draw(gc);
        for (Basket b: this.baskets)
            b.draw(gc);
        gc.restore();        
    }
    @Override
    public void beginContact(Contact contact) {
    }
    @Override
    public void endContact(Contact contact) {
        Body o1 = contact.getFixtureA().getBody();
        Body o2 = contact.getFixtureB().getBody();
        Body ball;
        
        if (o1.getUserData() instanceof Player && o2.getUserData() instanceof Ball)
            ball = o2;
        else if (o1.getUserData() instanceof Ball && o2.getUserData() instanceof Player)
            ball = o1;
        else
            return;
        
        // when hitting the player's paddle, accelerate the ball,
        // but make sure the ball is going up (i.e., it is above the player)
        if (ball.getLinearVelocity().y > 0)
            ball.getLinearVelocity().mulLocal(2.5f);
    }
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
    @Override
    public void preSolve(Contact contact, Manifold manifold) {
    }
}