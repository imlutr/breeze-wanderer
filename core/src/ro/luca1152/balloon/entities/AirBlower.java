package ro.luca1152.balloon.entities;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ro.luca1152.balloon.MyGame;
import ro.luca1152.balloon.utils.MapBodyBuilder;

public class AirBlower extends Image {
    // Explosion
    private static Vector2 closestPoint;
    private static Fixture closestFixture;
    // Constants
    private final float WIDTH = 1f, HEIGHT = 1f;
    // Box2D
    private Body body;

    public AirBlower(World world, RectangleMapObject mapObject, Camera camera) {
        super(MyGame.manager.get("textures/finish.png", Texture.class));

        // Image
        this.setSize(WIDTH, HEIGHT);
        this.setPosition(mapObject.getRectangle().x / MyGame.PPM, mapObject.getRectangle().y / MyGame.PPM);
        this.setOrigin(WIDTH / 2f, HEIGHT / 2f);
        this.getColor().a = .2f;
        this.setTouchable(Touchable.enabled);

        addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                explode(world, 32, 7f, 140f, getX() + getOriginX(), getY() + getOriginY());
                return true;
            }
        });

        // Box2D
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = MapBodyBuilder.getRectangle(mapObject);
        body.createFixture(fixtureDef);
    }

    private void explode(World world, final int numRays, float blastRadius, final float blastPower, float posX, float posY) {
        final Vector2 center = new Vector2(posX, posY);
        Vector2 rayDir = new Vector2();
        Vector2 rayEnd = new Vector2();

        for (int i = 0; i < numRays; i++) {
            float angle = (i / (float) numRays) * 360 * MathUtils.degreesToRadians;
            rayDir.set(MathUtils.sin(angle), MathUtils.cos(angle));
            rayEnd.set(center.x + blastRadius * rayDir.x, center.y + blastRadius * rayDir.y);

            // Find the closest fixture
            closestPoint = null;
            closestFixture = null;
            RayCastCallback callback = (fixture, point, normal, fraction) -> {
                closestPoint = point;
                closestFixture = fixture;
                return fraction;
            };
            world.rayCast(callback, center, rayEnd);

            // If a fixture was found in the ray cast, then apply an impulse there
            if (closestFixture != null)
                applyBlastImpulse(closestFixture.getBody(), center, closestPoint, blastPower / (float) numRays);
        }
    }

    private void applyBlastImpulse(Body body, Vector2 blastCenter, Vector2 applyPoint, float blastPower) {
        Vector2 blastDir = applyPoint.cpy().sub(blastCenter);
        float distance = blastDir.len();
        if (distance == 0) return; // So 1 / distance is valid
        float impulseMag = Math.min(blastPower * (1f / distance), blastPower * 0.5f);
        body.applyLinearImpulse(blastDir.nor().scl(impulseMag), applyPoint, true);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
