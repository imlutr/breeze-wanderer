package ro.luca1152.balloon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;

public class PlayScreen extends ScreenAdapter {
    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl20.glClearColor(1f, 1f, 1f, 1f);
    }
}
