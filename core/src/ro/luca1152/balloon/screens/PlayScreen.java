package ro.luca1152.balloon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import ro.luca1152.balloon.entities.Level;

public class PlayScreen extends ScreenAdapter {
    private Level level;

    @Override
    public void show() {
        level = new Level(1);
    }

    @Override
    public void render(float delta) {
        level.update(delta);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl20.glClearColor(1f, 1f, 1f, 1f);
        level.draw();
    }
}
