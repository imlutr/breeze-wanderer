package ro.luca1152.balloon.utils;

import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import ro.luca1152.balloon.MyGame;

public class MapBodyBuilder {
    private static float PPM;

    public static Array<Body> buildSolids(Map map, float PPM, World world) {
        MapBodyBuilder.PPM = PPM;

        Array<Body> bodies = new Array<Body>();

        if (map.getLayers().get("Solid") == null)
            return bodies;

        MapObjects objects = map.getLayers().get("Solid").getObjects();

        for (MapObject object : objects) {
            Shape shape;
            if (object instanceof TextureMapObject) continue;
            else if (object instanceof RectangleMapObject) shape = getRectangle((RectangleMapObject) object);
            else continue;

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(getPoint((RectangleMapObject) object));
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
        polygon.setAsBox(rectangle.width * 0.5f / PPM, rectangle.height * 0.5f / PPM);
        return polygon;
    }

    public static Vector2 getPoint(RectangleMapObject rectObject) {
        Rectangle rectangle = rectObject.getRectangle();
        return new Vector2((rectangle.x + rectangle.width / 2f) / PPM, (rectangle.y + rectangle.height / 2f) / PPM);
    }

    public static Rectangle getInformation(RectangleMapObject rectangleObject) {
        Rectangle rectangle = new Rectangle(rectangleObject.getRectangle());
        rectangle.width /= MyGame.PPM;
        rectangle.height /= MyGame.PPM;
        rectangle.x /= MyGame.PPM;
        rectangle.y /= MyGame.PPM;
        return rectangle;
    }
}