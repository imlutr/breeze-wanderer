package ro.luca1152.balloon.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import ro.luca1152.balloon.MyGame;
import ro.luca1152.balloon.screens.LoadingScreen;
import ro.luca1152.balloon.utils.MapBodyBuilder;

@SuppressWarnings("FieldCanBeLocal")
class RotatingPlatform extends Group {
    private Body body;
    RevoluteJoint joint;

    RotatingPlatform(World world, RectangleMapObject mapObject, Hinge hinge) {
        // Box2D
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(MapBodyBuilder.getPoint(mapObject));
        body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = MapBodyBuilder.getRectangle(mapObject);
        fixtureDef.density = 1f;
        body.createFixture(fixtureDef);

        // Create the joint
        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        revoluteJointDef.bodyA = hinge.body;
        revoluteJointDef.bodyB = body;
        revoluteJointDef.localAnchorA.set(0f, 0f);
        revoluteJointDef.localAnchorA.set(0f, 0f);
        revoluteJointDef.collideConnected = false;
        revoluteJointDef.enableMotor = true;
        revoluteJointDef.maxMotorTorque = 2f;
        revoluteJointDef.enableLimit = false;
        joint = (RevoluteJoint) world.createJoint(revoluteJointDef);

        // Actor
        Rectangle information = mapObject.getRectangle();
        setPosition(information.x, information.y);
        setSize(information.width, information.height);
        setOrigin(information.width / 2f, information.height / 2f);

        // Joint
        Image jointImage = new Image(MyGame.manager.get("textures/joint.png", Texture.class));
        jointImage.setPosition(getOriginX() - jointImage.getWidth() / 2f, getOriginY() - jointImage.getHeight() / 2f);
        addActor(jointImage);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        LoadingScreen.platformNinePatch.draw(batch, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), 1, 1, joint.getJointAngle() * MathUtils.radDeg);
    }
}
