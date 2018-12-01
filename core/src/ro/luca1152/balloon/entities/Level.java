package ro.luca1152.balloon.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ro.luca1152.balloon.MyGame;
import ro.luca1152.balloon.utils.MapBodyBuilder;

import java.util.ArrayList;

public class Level {
    // TiledMap
    private TiledMap tiledMap;
    private MapProperties mapProperties;
    private int mapWidth, mapHeight;

    // Box2D
    private World world;

    // Scene2D
    private Stage gameStage;

    private static Vector2 closestPoint;

    // Render
    private OrthogonalTiledMapRenderer mapRenderer;
    private static Fixture closestFixture;
    // Entities
    private Balloon balloon;
    private ArrayList<Vector2> al = new ArrayList<>();

    public Level(int levelNumber) {
        // TiledMap
        tiledMap = MyGame.manager.get("maps/map-" + levelNumber + ".tmx", TiledMap.class);
        mapProperties = tiledMap.getProperties();
        mapWidth = (Integer) mapProperties.get("width");
        mapHeight = (Integer) mapProperties.get("height");

        // Box2D
        world = new World(new Vector2(0, -10f), true);
        MapBodyBuilder.buildShapes(tiledMap, MyGame.PPM, world);

        // Scene2D
        gameStage = new Stage(new FitViewport(15f, 15f), MyGame.batch);

        // Entities
        MapObjects objects = tiledMap.getLayers().get("Balloons").getObjects();
        for (int object = 0; object < objects.getCount(); object++) {
            balloon = new Balloon(tiledMap, world, (RectangleMapObject) objects.get(object));
            gameStage.addActor(balloon);
        }

        // Render
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / MyGame.PPM, MyGame.batch);

        // InputProcessor
        setInputProcessor();
    }

    private void setInputProcessor() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector3 position = new Vector3(screenX, screenY, 0);
                gameStage.getCamera().unproject(position);
                explode(64, 6f, 150f, position.x, position.y);
                return true;
            }
        });
    }

    private void explode(final int numRays, float blastRadius, final float blastPower, float posX, float posY) {
        final Vector2 center = new Vector2(posX, posY);
        Vector2 rayDir = new Vector2();
        Vector2 rayEnd = new Vector2();

        for (int i = 0; i < numRays; i++) {
            float angle = (i / (float) numRays) * 360 * MathUtils.degreesToRadians;
            rayDir.set(MathUtils.sin(angle), MathUtils.cos(angle));
            rayEnd.set(center.x + blastRadius * rayDir.x, center.y + blastRadius * rayDir.y);

            closestPoint = null;
            closestFixture = null;

            RayCastCallback callback = (fixture, point, normal, fraction) -> {
                closestPoint = point;
                closestFixture = fixture;
                return fraction;
            };

            world.rayCast(callback, center, rayEnd);
            if (closestFixture != null) {
                al.add(center.cpy());
                al.add(closestPoint.cpy());
                applyBlastImpulse(closestFixture.getBody(), center, closestPoint, blastPower / (float) numRays);
            }
        }
    }

    private void applyBlastImpulse(Body body, Vector2 blastCenter, Vector2 applyPoint, float blastPower) {
        Vector2 blastDir = applyPoint.cpy().sub(blastCenter);
        float distance = blastDir.len();
        if (distance == 0) return;

        float invDistance = 1f / distance;
        float impulseMag = Math.min(blastPower * invDistance, blastPower * 0.5f); //Not physically correct

        body.applyLinearImpulse(blastDir.nor().scl(impulseMag), applyPoint, true);
    }

    public void draw() {
        MyGame.batch.setProjectionMatrix(gameStage.getCamera().combined);
//        gameStage.draw();
        mapRenderer.render();
        MyGame.debugRenderer.render(world, gameStage.getCamera().combined);

        MyGame.shapeRenderer.setProjectionMatrix(gameStage.getCamera().combined);
        MyGame.shapeRenderer.setColor(Color.RED);
        MyGame.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < al.size(); i += 2)
            MyGame.shapeRenderer.line(al.get(i), al.get(i + 1));
        MyGame.shapeRenderer.end();
    }

    public void update(float delta) {
        gameStage.act(delta);
        world.step(1 / 60f, 6, 2);
    }
}
