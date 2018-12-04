package ro.luca1152.balloon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import ro.luca1152.balloon.MyGame;
import ro.luca1152.balloon.entities.Level;

@SuppressWarnings("Duplicates")
public class PlayScreen extends ScreenAdapter {
    // Level
    public static float timer = 0f;
    private int levelNumber = 1;
    private Level level;

    @Override
    public void show() {
        level = new Level(levelNumber);

        // Audio
        playMusic();
    }

    private void playMusic() {
        Music music = MyGame.manager.get("audio/piano-loop.mp3", Music.class);
        music.setVolume(.03f);
        music.setLooping(true);
        music.play();
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl20.glClearColor(MyGame.backgroundWhite.r, MyGame.backgroundWhite.g, MyGame.backgroundWhite.b, MyGame.backgroundWhite.a);
        level.draw();
    }

    private void update(float delta) {
        level.update(delta);

        if (level.restart) level = new Level(levelNumber);
        else if (level.isFinished) {
            level = new Level(++levelNumber);

            // Audio
            MyGame.manager.get("audio/win.wav", Sound.class).play(.0125f);
        }

        if (levelNumber != 10)
            timer += delta;
    }
}
