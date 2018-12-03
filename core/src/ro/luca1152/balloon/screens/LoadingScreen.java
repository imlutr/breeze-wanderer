package ro.luca1152.balloon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import ro.luca1152.balloon.MyGame;

import static com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;

@SuppressWarnings({"FieldCanBeLocal", "Duplicates"})
public class LoadingScreen extends ScreenAdapter {
    private final int MAP_COUNT = 4;
    private float timer = 0f;

    @Override
    public void show() {
        loadTextures();
        loadMaps();
        loadFonts();
    }

    private void loadTextures() {
        MyGame.manager.load("textures/balloon.png", Texture.class);
        MyGame.manager.load("textures/highlight-bl.png", Texture.class);
        MyGame.manager.load("textures/highlight-br.png", Texture.class);
        MyGame.manager.load("textures/highlight-tl.png", Texture.class);
        MyGame.manager.load("textures/highlight-tr.png", Texture.class);
        MyGame.manager.load("textures/finish-center.png", Texture.class);
        MyGame.manager.load("textures/fan.png", Texture.class);
        MyGame.manager.load("textures/pixel.png", Texture.class);
    }

    private void loadMaps() {
        MyGame.manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        for (int map = 1; map <= MAP_COUNT; map++)
            MyGame.manager.load("maps/map-" + map + ".tmx", TiledMap.class);
    }

    private void loadFonts() {
        MyGame.manager.load("fonts/DIN1451-26pt.fnt", BitmapFont.class);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl20.glClearColor(MyGame.backgroundWhite.r, MyGame.backgroundWhite.g, MyGame.backgroundWhite.b, MyGame.backgroundWhite.a);
    }

    private void update(float delta) {
        timer += delta;

        // Finished loading assets
        if (MyGame.manager.update()) {
            Gdx.app.log("LoadingScreen", "Finished loading assets in " + (int) (timer * 100) / 100f + "s.");

            smoothTextures();

            // Start the game
            MyGame.instance.setScreen(MyGame.playScreen);
        }
    }

    private void smoothTextures() {
        MyGame.manager.get("textures/balloon.png", Texture.class).setFilter(Linear, Linear);
    }
}
