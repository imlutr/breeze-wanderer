package ro.luca1152.balloon.utils;

import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class MapBodyBuilder {
    private static float PPM;

    public static Array<Body> buildShapes(Map map, float PPM, World world) {
        MapBodyBuilder.PPM = PPM;

        MapObjects objects = map.getLayers().get("Solid").getObjects();
        Array<Body> bodies = new Array<Body>();

        for (MapObject object : objects) {
            Shape shape;
            if (object instanceof TextureMapObject) continue;
            else if (object instanceof RectangleMapObject) shape = getRectangle((RectangleMapObject) object);
            else continue;

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            Body body = world.createBody(bodyDef);
            body.createFixture(shape, 1);
            bodies.add(body);
            shape.dispose();
        }
        return bodies;
    }

    public static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
        Rectangle rectangle = rectangleObject.getRectangle();
        PolygonShape polygon = new PolygonShape();
        Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) / PPM, (rectangle.y + rectangle.height * 0.5f) / PPM);
        polygon.setAsBox(rectangle.width * 0.5f / PPM, rectangle.height * 0.5f / PPM, size, 0.0f);
        return polygon;
    }

    public static Vector2 getPoint(RectangleMapObject pointObject) {
        MapProperties properties = pointObject.getProperties();
        return new Vector2((Float) properties.get("x") / PPM, (Float) properties.get("y") / PPM);
    }
}