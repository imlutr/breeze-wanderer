package ro.luca1152.balloon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import ro.luca1152.balloon.MyGame;

@SuppressWarnings("FieldCanBeLocal")
public class LoadingScreen extends ScreenAdapter {
    private final int MAP_COUNT = 3;
    private float timer = 0f;

    @Override
    public void show() {
        loadTextures();
        loadMaps();
    }

    private void loadTextures() {
        MyGame.manager.load("textures/player.png", Texture.class);
        MyGame.manager.load("textures/finish.png", Texture.class);
    }

    private void loadMaps() {
        MyGame.manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        for (int map = 1; map <= MAP_COUNT; map++)
            MyGame.manager.load("maps/map-" + map + ".tmx", TiledMap.class);
    }

    @Override
    public void render(float delta) {
        timer += delta;

        // Finished loading assets
        if (MyGame.manager.update()) {
            Gdx.app.log("LoadingScreen", "Finished loading assets in " + (int) (timer * 100) / 100f + "s.");

            // Start the game
            MyGame.instance.setScreen(MyGame.playScreen);
        }
    }
}
