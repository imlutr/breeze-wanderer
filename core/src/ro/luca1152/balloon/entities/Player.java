package ro.luca1152.balloon.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import ro.luca1152.balloon.MyGame;
import ro.luca1152.balloon.utils.MapBodyBuilder;

public class Player extends Image {
    public Player(TiledMap tiledMap, World world) {
        super(MyGame.manager.get("textures/player.png", Texture.class));

        // Image
        this.setSize(1f, 1f);
        Vector2 position = MapBodyBuilder.getPoint((RectangleMapObject) tiledMap.getLayers().get("Player").getObjects().get(0));
        this.setPosition(position.x, position.y);
    }
}
