package ro.luca1152.balloon.entities;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import ro.luca1152.balloon.utils.MapBodyBuilder;

class RotatingPlatform {
    private Body body;

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
        revoluteJointDef.collideConnected = false;
        revoluteJointDef.enableMotor = true;
        revoluteJointDef.maxMotorTorque = 5;
        world.createJoint(revoluteJointDef);
    }
}
