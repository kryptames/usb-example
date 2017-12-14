package takro;

import org.jbox2d.common.Vec2;

public class BallFactory {
    public static Ball createBall(BallType type, TakroWorld world, Vec2 servePos) {
        switch (type) {
        case REGULAR:
            return new CircularBall(world,servePos,0.2f).setSpin(10);
        case CUBIC:
            return new RectangularBall(world,servePos,new Vec2(0.2f,0.2f)).setSpin(10);
        case BRICK:
            return new RectangularBall(world,servePos,new Vec2(0.4f,0.2f)).setSpin(10);
        case BIG:
            return new CircularBall(world,servePos,0.4f).setSpin(10);
        case FAST_SPIN:
            return new CircularBall(world,servePos,0.2f).setSpin(100);
        case FAST_SPIN_BRICK:
            return new RectangularBall(world,servePos,new Vec2(0.4f,0.2f)).setSpin(50);
        }
        return null;
    }
}
