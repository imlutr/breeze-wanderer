package ro.luca1152.balloon.entities;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import ro.luca1152.balloon.utils.MapBodyBuilder;

class Hinge {
    Body body;

    Hinge(World world, RectangleMapObject mapObject) {
        // Box2D
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(MapBodyBuilder.getPoint(mapObject));
        body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = MapBodyBuilder.getRectangle(mapObject);
        fixtureDef.density = 1f;
        body.createFixture(fixtureDef);
        body.setUserData(this);
    }
}
