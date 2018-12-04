package ro.luca1152.balloon.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ro.luca1152.balloon.MyGame;

class Balloon extends Image {
    // Constants
    private final float WIDTH = 1.05f, HEIGHT = 1.35f;
    public boolean isDeleted = false;

    // Box2D
    Body body;

    // Collisions
    private Rectangle collisionBox;

    Balloon(World world, Rectangle rectangle) {
        super(MyGame.manager.get("textures/balloon.png", Texture.class));

        // Image
        this.setSize(WIDTH, HEIGHT);
        this.setPosition(rectangle.x / MyGame.PPM, rectangle.y / MyGame.PPM);

        // Collisions
        collisionBox = new Rectangle();
        collisionBox.set(getX(), getY(), getWidth(), getHeight());

        // Box2D
        // Top circle
        BodyDef topCircleBodyDef = new BodyDef();
        topCircleBodyDef.type = BodyDef.BodyType.DynamicBody;
        Body topCircleBody = world.createBody(topCircleBodyDef);
        FixtureDef topCircleFixtureDef = new FixtureDef();
        CircleShape topCircleShape = new CircleShape();
        topCircleShape.setPosition(new Vector2(getX() + getWidth() / 2f, getY() + getHeight() / 2f));
        topCircleShape.setRadius(WIDTH / 2f);
        topCircleFixtureDef.shape = topCircleShape;
        topCircleFixtureDef.density = 1f;
        topCircleFixtureDef.friction = .2f;
        topCircleBody.createFixture(topCircleFixtureDef);
        topCircleBody.setFixedRotation(true);
        topCircleBody.setLinearDamping(.15f);
        // Bottom circle
        BodyDef botCircleBodyDef = new BodyDef();
        botCircleBodyDef.type = BodyDef.BodyType.DynamicBody;
        Body botCircleBody = world.createBody(botCircleBodyDef);
        FixtureDef botCircleFixtureDef = new FixtureDef();
        CircleShape botCircleShape = new CircleShape();
        botCircleShape.setPosition(new Vector2(getX() + getWidth() / 2f, getY() + .15f));
        botCircleShape.setRadius((HEIGHT - WIDTH));
        botCircleFixtureDef.shape = botCircleShape;
        botCircleFixtureDef.density = 1f;
        botCircleFixtureDef.friction = .2f;
        botCircleBody.createFixture(botCircleFixtureDef);
        botCircleBody.setFixedRotation(true);
        botCircleBody.setLinearDamping(.15f);
        // Join the two bodies
        WeldJointDef jointDef = new WeldJointDef();
        jointDef.bodyA = topCircleBody;
        jointDef.bodyB = botCircleBody;
        world.createJoint(jointDef);
        // Body
        body = topCircleBody;

        // Listener
        addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // Remove the balloon if it was touched
                world.destroyBody(topCircleBody);
                world.destroyBody(botCircleBody);
                isDeleted = true;
                remove();
                return true;
            }
        });
    }

    Rectangle getCollisionBox() {
        collisionBox.set(getX(), getY(), getWidth(), getHeight());
        return collisionBox;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        body.applyForce(new Vector2(0, 18f), body.getWorldCenter(), true);
        body.getLinearVelocity().y = Math.max(body.getLinearVelocity().y, 2.5f);
        setPosition(body.getWorldCenter().x - WIDTH / 2f, body.getWorldCenter().y - HEIGHT / 2f - .15f);
        setRotation(body.getAngle() * MathUtils.radDeg);
    }
}
