package ro.luca1152.balloon.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ro.luca1152.balloon.MyGame;
import ro.luca1152.balloon.utils.MapBodyBuilder;

import java.util.ArrayList;

public class Level {
    // Booleans
    public boolean isFinished = false;
    public boolean restart = false;

    // TiledMap
    private TiledMap tiledMap;
    private MapProperties mapProperties;
    private int mapWidth, mapHeight;

    // Box2D
    private World world;

    // Scene2D
    private Stage gameStage;

    // Render
    private OrthogonalTiledMapRenderer mapRenderer;

    // Entities
    private ArrayList<Balloon> balloons;
    private ArrayList<AirBlower> airBlowers;
    private Finish finish;

    Body body;

    public Level(int levelNumber) {
        // TiledMap
        tiledMap = MyGame.manager.get("maps/map-" + levelNumber + ".tmx", TiledMap.class);
        mapProperties = tiledMap.getProperties();
        mapWidth = (Integer) mapProperties.get("width");
        mapHeight = (Integer) mapProperties.get("height");

        // Box2D
        world = new World(new Vector2(0, -10f), true);
        Array<Body> solids = MapBodyBuilder.buildShapes(tiledMap, MyGame.PPM, world);

        // Scene2D
        gameStage = new Stage(new FitViewport(12f, 12f), MyGame.batch);

        // Balloons
        balloons = new ArrayList<>();
        MapObjects balloonsObjects = tiledMap.getLayers().get("Balloons").getObjects();
        for (int object = 0; object < balloonsObjects.getCount(); object++) {
            Balloon balloon = new Balloon(world, ((RectangleMapObject) balloonsObjects.get(object)).getRectangle());
            balloons.add(balloon);
            gameStage.addActor(balloon);
        }

        // Air blowers
        airBlowers = new ArrayList<>();
        if (tiledMap.getLayers().get("Air Blower") != null) {
            MapObjects airBlowersObjects = tiledMap.getLayers().get("Air Blower").getObjects();
            for (int object = 0; object < airBlowersObjects.getCount(); object++) {
                AirBlower airBlower = new AirBlower(world, (RectangleMapObject) airBlowersObjects.get(object), gameStage.getCamera());
                airBlowers.add(airBlower);
                gameStage.addActor(airBlower);
            }
        }

        // Finish
        finish = new Finish(((RectangleMapObject) tiledMap.getLayers().get("Finish").getObjects().get(0)).getRectangle());
        gameStage.addActor(finish);

        // Hinges
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(MapBodyBuilder.getPoint((RectangleMapObject) tiledMap.getLayers().get("Hinges").getObjects().get(0)));
        body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = MapBodyBuilder.getRectangle((RectangleMapObject) tiledMap.getLayers().get("Hinges").getObjects().get(0));
        body.createFixture(fixtureDef);

        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        revoluteJointDef.bodyA = body;
        revoluteJointDef.bodyB = solids.get(0);
        revoluteJointDef.collideConnected = false;
        revoluteJointDef.localAnchorA.set(0f, 0f);
        revoluteJointDef.localAnchorB.set(0f, 0f);
        revoluteJointDef.enableMotor = true;
        revoluteJointDef.maxMotorTorque = 10;
        world.createJoint(revoluteJointDef);

        // Render
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / MyGame.PPM, MyGame.batch);

        // InputProcessor
        Gdx.input.setInputProcessor(new InputMultiplexer(
                gameStage,
                new InputAdapter() {
                    @Override
                    public boolean keyDown(int keycode) {
                        if (keycode == Input.Keys.SPACE)
                            restart = true;
                        return true;
                    }
                }));
    }

    public void draw() {
        MyGame.batch.setProjectionMatrix(gameStage.getCamera().combined);
        gameStage.draw();
        mapRenderer.render();
        MyGame.debugRenderer.render(world, gameStage.getCamera().combined);
    }

    public void update(float delta) {
        gameStage.act(delta);
        listenForCollisions();
        makeCameraFollowPlayer();
        world.step(1 / 60f, 6, 2);
    }

    private void makeCameraFollowPlayer() {
        Vector2 balloonPos = balloons.get(0).body.getWorldCenter();
        gameStage.getCamera().position.lerp(new Vector3(balloonPos.x, balloonPos.y, 0f), .15f);
        keepCameraWithinBounds();
        gameStage.getCamera().update();
    }

    private void keepCameraWithinBounds() {
        OrthographicCamera camera = (OrthographicCamera) gameStage.getCamera();

        float mapLeft = 0f, mapRight = mapWidth;
        if (mapWidth > camera.viewportWidth) {
            mapLeft = -1;
            mapRight = mapWidth + 1;
        }
        float mapBottom = 0f, mapTop = mapHeight;
        float cameraHalfWidth = camera.viewportWidth / 2f, cameraHalfHeight = camera.viewportHeight / 2f;
        float cameraLeft = camera.position.x - cameraHalfWidth, cameraRight = camera.position.x - cameraHalfWidth;
        float cameraBottom = camera.position.y - cameraHalfHeight, cameraTop = camera.position.y + cameraHalfHeight;

        // Clam horizontal axis
        if (camera.viewportWidth > mapRight) camera.position.x = mapRight / 2f;
        else if (cameraLeft <= mapLeft) camera.position.x = mapLeft + cameraHalfWidth;
        else if (cameraRight >= mapRight) camera.position.x = mapRight - cameraHalfWidth;

        // Clamp vertical axis
        if (camera.viewportHeight > mapTop) camera.position.y = mapTop / 2f;
        else if (cameraBottom <= mapBottom) camera.position.y = mapBottom + cameraHalfHeight;
        else if (cameraTop >= mapTop) camera.position.y = mapTop - cameraHalfHeight;
    }

    private void listenForCollisions() {
        if (!restart)
            for (Balloon balloon : balloons) {
                if (balloon.getCollisionBox().overlaps(finish.getCollisionBox())) {
                    isFinished = true;
                }
            }
    }
}
