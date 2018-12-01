package ro.luca1152.balloon.entities;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ro.luca1152.balloon.MyGame;
import ro.luca1152.balloon.utils.MapBodyBuilder;

public class Level {
    // TiledMap
    private TiledMap tiledMap;
    private MapProperties mapProperties;
    private int mapWidth, mapHeight;

    // Box2D
    private World world;

    // Scene2D
    private Stage gameStage;

    // Entities
    private Player player;

    // Render
    private OrthogonalTiledMapRenderer mapRenderer;

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
        gameStage = new Stage(new FitViewport(10f, 10f), MyGame.batch);

        // Entities
        player = new Player(tiledMap, world);
        gameStage.addActor(player);

        // Render
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / MyGame.PPM, MyGame.batch);
    }

    public void draw() {
        MyGame.batch.setProjectionMatrix(gameStage.getCamera().combined);
        gameStage.draw();
        mapRenderer.render();
        MyGame.debugRenderer.render(world, gameStage.getCamera().combined);
    }

    public void update(float delta) {
        gameStage.act(delta);
        world.step(1 / 60f, 6, 2);
    }
}
