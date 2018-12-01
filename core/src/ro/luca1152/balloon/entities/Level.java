package ro.luca1152.balloon.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ro.luca1152.balloon.MyGame;
import ro.luca1152.balloon.utils.MapBodyBuilder;

import java.util.ArrayList;

public class Level {
    // Booleans
    public boolean isFinished = false;

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

        // Render
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / MyGame.PPM, MyGame.batch);

        // InputProcessor
        Gdx.input.setInputProcessor(gameStage);
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
        world.step(1 / 60f, 6, 2);
    }

    private void listenForCollisions() {
        for (Balloon balloon : balloons) {
            if (balloon.getCollisionBox().overlaps(finish.getCollisionBox())) {
                isFinished = true;
            }
        }
    }
}
