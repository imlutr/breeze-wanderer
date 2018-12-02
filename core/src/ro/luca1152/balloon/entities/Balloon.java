package ro.luca1152.balloon.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ro.luca1152.balloon.MyGame;

class Balloon extends Image {
    // Constants
    private final float WIDTH = 1.2f, HEIGHT = 1.6f;

    // Collisions
    private Rectangle collisionBox;

    // Box2D
    Body body;

    Balloon(World world, Rectangle rectangle) {
        super(MyGame.manager.get("textures/player.png", Texture.class));

        // Image
        this.setSize(WIDTH, HEIGHT);
        this.setPosition(rectangle.x / MyGame.PPM, rectangle.y / MyGame.PPM);
        this.getColor().a = .2f;

        // Collisions
        collisionBox = new Rectangle();
        collisionBox.set(getX(), getY(), getWidth(), getHeight());

        // Box2D
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = createEllipse(WIDTH / 2f, HEIGHT / 2f);
        body.createFixture(fixtureDef);
        body.setTransform(getX() + getWidth() / 2f, getY() + getHeight() / 2f, 0f);
        body.setLinearDamping(.15f);

        // Listener
        addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // Remove the balloon if it was touched
                world.destroyBody(body);
                remove();
                return true;
            }
        });
    }

    private ChainShape createEllipse(float width, float height) {
        int STEPS = 12;

        ChainShape ellipse = new ChainShape();
        Vector2[] vertices = new Vector2[STEPS];

        for (int i = 0; i < STEPS; i++) {
            float t = (float) (i * 2 * Math.PI) / STEPS;
            vertices[i] = new Vector2(width * (float) Math.cos(t), height * (float) Math.sin(t));
        }

        ellipse.createLoop(vertices);
        return ellipse;
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
        setPosition(body.getWorldCenter().x - WIDTH / 2f, body.getWorldCenter().y - HEIGHT / 2f);
        setRotation(body.getAngle() * MathUtils.radDeg);
    }
}
