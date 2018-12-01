package ro.luca1152.balloon.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import ro.luca1152.balloon.MyGame;
import ro.luca1152.balloon.utils.MapBodyBuilder;

class Balloon extends Image {
    private final float WIDTH = 1f, HEIGHT = 1.3f;
    public Body body;

    Balloon(TiledMap tiledMap, World world, RectangleMapObject mapObject) {
        super(MyGame.manager.get("textures/player.png", Texture.class));

        // Image
        this.setSize(WIDTH, HEIGHT);
        Vector2 position = MapBodyBuilder.getPoint(mapObject);
        this.setPosition(position.x, position.y);

        // Box2D
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = createEllipse(WIDTH / 2f, HEIGHT / 2f);
        body.createFixture(fixtureDef);
        body.setTransform(position, 0f);
        body.setLinearDamping(1f);
    }

    private ChainShape createEllipse(float width, float height) {
        int STEPS = 8;

        ChainShape ellipse = new ChainShape();
        Vector2[] vertices = new Vector2[STEPS];

        for (int i = 0; i < STEPS; i++) {
            float t = (float) (i * 2 * Math.PI) / STEPS;
            vertices[i] = new Vector2(width * (float) Math.cos(t), height * (float) Math.sin(t));
        }

        ellipse.createLoop(vertices);
        return ellipse;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        body.applyForce(new Vector2(0, 10f), body.getWorldCenter(), true);
        setPosition(body.getWorldCenter().x - getWidth() / 2f, body.getWorldCenter().y - getHeight() / 2f);
        setRotation(body.getAngle() * MathUtils.radDeg);
    }
}
