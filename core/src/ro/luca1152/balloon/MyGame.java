package ro.luca1152.balloon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ro.luca1152.balloon.screens.LoadingScreen;

public class MyGame extends Game {
    // Tools
    public static SpriteBatch batch;
    public static AssetManager manager;
    public static MyGame instance;

    // Screens
    public static LoadingScreen loadingScreen;
    public static PlayScreen playScreen;

    @Override
    public void create() {
        // Tools
        batch = new SpriteBatch();
        manager = new AssetManager();
        instance = this;

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
