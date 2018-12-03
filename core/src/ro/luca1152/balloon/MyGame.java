package ro.luca1152.balloon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import ro.luca1152.balloon.screens.LoadingScreen;
import ro.luca1152.balloon.screens.PlayScreen;

@SuppressWarnings("WeakerAccess")
public class MyGame extends Game {
    // Constants
    public static final float PPM = 64f; // Pixels per meter
    public static final Color finishGreen = new Color(0 / 255f, 181 / 255f, 60 / 255f, 1f);

    // Tools
    public static MyGame instance;
    public static SpriteBatch batch;
    public static ShapeRenderer shapeRenderer;
    public static Box2DDebugRenderer debugRenderer;
    public static AssetManager manager;

    // Screens
    public static LoadingScreen loadingScreen;
    public static PlayScreen playScreen;

    @Override
    public void create() {
        // Box2D
        Box2D.init();

        // Tools
        instance = this;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        debugRenderer = new Box2DDebugRenderer();
        manager = new AssetManager();

        // Screens
        loadingScreen = new LoadingScreen();
        playScreen = new PlayScreen();

        setScreen(loadingScreen);
    }

    @Override
    public void dispose() {
        batch.dispose();
        manager.dispose();
    }
}
