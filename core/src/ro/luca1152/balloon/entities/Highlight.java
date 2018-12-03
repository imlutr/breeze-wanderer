package ro.luca1152.balloon.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import ro.luca1152.balloon.MyGame;

class Highlight extends Group {
    Highlight(Rectangle information, Color color) {
        // Images
        Image botLeft = new Image(MyGame.manager.get("textures/highlight-bl.png", Texture.class));
        botLeft.setSize(botLeft.getWidth() / MyGame.PPM, botLeft.getHeight() / MyGame.PPM);
        botLeft.setPosition(information.x, information.y);
        botLeft.setColor(color);
        addActor(botLeft);
        Image botRight = new Image(MyGame.manager.get("textures/highlight-br.png", Texture.class));
        botRight.setSize(botRight.getWidth() / MyGame.PPM, botRight.getHeight() / MyGame.PPM);
        botRight.setPosition(information.x + information.width - botRight.getWidth(), information.y);
        botRight.setColor(color);
        addActor(botRight);
        Image topLeft = new Image(MyGame.manager.get("textures/highlight-tl.png", Texture.class));
        topLeft.setSize(topLeft.getWidth() / MyGame.PPM, topLeft.getHeight() / MyGame.PPM);
        topLeft.setPosition(information.x, information.y + information.height - topLeft.getHeight());
        topLeft.setColor(color);
        addActor(topLeft);
        Image topRight = new Image(MyGame.manager.get("textures/highlight-tr.png", Texture.class));
        topRight.setSize(topRight.getWidth() / MyGame.PPM, topRight.getHeight() / MyGame.PPM);
        topRight.setPosition(information.x + information.width - topRight.getWidth(), information.y + information.height - topRight.getHeight());
        topRight.setColor(color);
        addActor(topRight);
    }
}