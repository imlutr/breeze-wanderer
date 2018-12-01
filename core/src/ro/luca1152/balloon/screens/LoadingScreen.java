package ro.luca1152.balloon.screens;

import com.badlogic.gdx.ScreenAdapter;
import ro.luca1152.balloon.MyGame;

public class LoadingScreen extends ScreenAdapter {
    @Override
    public void show() {
        MyGame.instance.setScreen(MyGame.playScreen);
    }
}
