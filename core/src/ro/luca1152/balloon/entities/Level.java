package ro.luca1152.balloon.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ro.luca1152.balloon.MyGame;
import ro.luca1152.balloon.utils.MapBodyBuilder;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

@SuppressWarnings("FieldCanBeLocal")
public class Level {
    // Constants
    private final float MIN_ZOOM = .8f, MAX_ZOOM = 1.25f;

    // TiledMap
    private TiledMap tiledMap;
    private MapProperties mapProperties;
    private int mapWidth, mapHeight;

    // Box2D
    private World world;

    // Scene2D
    private Stage gameStage, cameraTextStage, staticTextStage;
    private OrthogonalTiledMapRenderer mapRenderer;

    // Labels
    private Label.LabelStyle labelStyle;

    // Fade out
    private Image fadeOut;
    private boolean shouldFadeOut = false, isFadingOut = false, shouldFadeIn = false, isFadingIn = false;

    // Restart
    public boolean restart = false, shouldRestart = false;

    // Finish
    public boolean isFinished = false;

    // Entities
    private Array<Balloon> balloons;
    private Array<Finish> finishes;
    private Array<AirBlower> airBlowers;
    private Array<Hinge> hinges;
    private Array<RotatingPlatform> rotatingPlatforms;

    private Array<Balloon> balloonsToRemove = new Array<>();

    public void draw() {
        // Prerequisites
        MyGame.batch.setProjectionMatrix(gameStage.getCamera().combined);
        mapRenderer.setView((OrthographicCamera) gameStage.getCamera());

        // Reset the color in case there are any colored Actors
        mapRenderer.getBatch().setColor(Color.WHITE);
        mapRenderer.render();

        // Draw every actor
        gameStage.draw();
        cameraTextStage.draw();
        staticTextStage.draw();

        // Shows the Box2D debug guides
//        MyGame.debugRenderer.render(world, gameStage.getCamera().combined);
    }

    public void update(float delta) {
        gameStage.act(delta);
        makeCameraFollowBalloons();
        checkIfFinished();
        updatePhysics();
        listenForCollisions();
        autoRestart();
        cameraTextStage.act(delta);
        staticTextStage.act(delta);
    }

    private void makeCameraFollowBalloons() {
        if (balloons.size != 0) {
            BoundingBox balloonsBox = getBalloonsBoundingBox(balloons);
            Vector3 centerPoint = getBalloonsCenterPoint(balloonsBox);
            gameStage.getCamera().position.slerp(centerPoint, .15f);
//            zoomTheCamera(balloonsBox, (OrthographicCamera) gameStage.getCamera());
            keepCameraWithinBounds();
            gameStage.getCamera().update();
        }
        cameraTextStage.getCamera().position.set(gameStage.getCamera().position.x * MyGame.PPM, gameStage.getCamera().position.y * MyGame.PPM, 0f);
    }

    private void checkIfFinished() {
        if (Math.abs(fadeOut.getColor().a - 1f) <= 2f / 255f)
            isFinished = true;
    }

    private void updatePhysics() {
        world.step(1 / 60f, 6, 2);
    }

    public Level(int levelNumber) {
        // TiledMap
        tiledMap = MyGame.manager.get("maps/map-" + levelNumber + ".tmx", TiledMap.class);
        mapProperties = tiledMap.getProperties();
        mapWidth = (Integer) mapProperties.get("width");
        mapHeight = (Integer) mapProperties.get("height");

        // Box2D
        world = new World(new Vector2(0, -10f), true);
        MapBodyBuilder.buildSolids(tiledMap, MyGame.PPM, world);

        // Scene2D
        gameStage = new Stage(new FitViewport(10f, 10f), MyGame.batch);
        cameraTextStage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), MyGame.batch);
        staticTextStage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), MyGame.batch);

        // Balloons
        balloons = new Array<>();
        MapObjects balloonsObjects = tiledMap.getLayers().get("Balloons").getObjects();
        for (int object = 0; object < balloonsObjects.getCount(); object++) {
            Balloon balloon = new Balloon(world, ((RectangleMapObject) balloonsObjects.get(object)).getRectangle());
            gameStage.getCamera().position.set(balloon.body.getWorldCenter(), 0f);
            balloons.add(balloon);
            gameStage.addActor(balloon);
        }

        // Air blowers
        airBlowers = new Array<>();
        if (tiledMap.getLayers().get("Air Blowers") != null) {
            MapObjects airBlowersObjects = tiledMap.getLayers().get("Air Blowers").getObjects();
            for (int object = 0; object < airBlowersObjects.getCount(); object++) {
                AirBlower airBlower = new AirBlower(world, (RectangleMapObject) airBlowersObjects.get(object));
                airBlowers.add(airBlower);
                gameStage.addActor(airBlower);
            }
        }

        // Hinges
        hinges = new Array<>();
        if (tiledMap.getLayers().get("Hinges") != null) {
            MapObjects hingesObjects = tiledMap.getLayers().get("Hinges").getObjects();
            for (int object = 0; object < hingesObjects.getCount(); object++) {
                Hinge hinge = new Hinge(world, (RectangleMapObject) hingesObjects.get(object));
                hinges.add(hinge);
            }
        }

        // Rotating platforms
        rotatingPlatforms = new Array<>();
        if (tiledMap.getLayers().get("Rotating Platforms") != null && hinges.size != 0) {
            MapObjects rotatingPlatformsObjects = tiledMap.getLayers().get("Rotating Platforms").getObjects();
            for (int object = 0; object < rotatingPlatformsObjects.getCount(); object++) {
                // Find the rotating platform's hinge
                Rectangle information = MapBodyBuilder.getInformation((RectangleMapObject) rotatingPlatformsObjects.get(object));
                Array<Hinge> hingesFound = new Array<>();
                world.QueryAABB(fixture -> {
                    if (fixture.getBody().getUserData() != null && fixture.getBody().getUserData().getClass() == Hinge.class) {
                        hingesFound.add((Hinge) fixture.getBody().getUserData());
                        return false; // Stop the search, the hinge was found
                    } else
                        return true; // Continue the search
                }, information.x, information.y, information.x + information.width, information.y + information.height);
                RotatingPlatform rotatingPlatform = new RotatingPlatform(world, (RectangleMapObject) rotatingPlatformsObjects.get(object), hingesFound.get(0));
                rotatingPlatforms.add(rotatingPlatform);
                cameraTextStage.addActor(rotatingPlatform); // Workaround since I can't manage to add it to the gameStage
            }
        }

        // Finish
        finishes = new Array<>();
        if (tiledMap.getLayers().get("Finish") != null) {
            MapObjects finishObjects = tiledMap.getLayers().get("Finish").getObjects();
            for (int object = 0; object < finishObjects.getCount(); object++) {
                Finish finish = new Finish(MapBodyBuilder.getInformation((RectangleMapObject) finishObjects.get(object)));
                gameStage.addActor(finish);
                finishes.add(finish);
            }
        }

        // Text
        labelStyle = new Label.LabelStyle(MyGame.manager.get("fonts/DIN1451-26pt.fnt", BitmapFont.class), new Color(0 / 255f, 174 / 255f, 181 / 255f, 1));
        if (tiledMap.getLayers().get("Text") != null) {
            MapObjects textObjects = tiledMap.getLayers().get("Text").getObjects();
            for (int object = 0; object < textObjects.getCount(); object++) {
                String text = (String) textObjects.get(object).getProperties().get("text");
                Rectangle information = ((RectangleMapObject) textObjects.get(object)).getRectangle();

                Label label = new Label(text, labelStyle);
                label.setPosition(information.getX(), information.getY());
                label.setSize(information.getWidth(), information.getHeight());
                label.setWrap(true);
                label.setAlignment(Align.center, Align.center);
                cameraTextStage.addActor(label);
            }
        }

        // Level number label
        Label label = new Label("#" + levelNumber, labelStyle);
        label.setPosition(Gdx.graphics.getWidth() - label.getWidth() - 5, 2);
        staticTextStage.addActor(label);

        // Fade-in effect
        Image fadeIn = new Image(MyGame.manager.get("textures/pixel.png", Texture.class));
        fadeIn.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fadeIn.setColor(MyGame.backgroundWhite);
        fadeIn.addAction(sequence(
                fadeOut(.4f),
                removeActor()
                )
        );
        staticTextStage.addActor(fadeIn);

        // Fade-out effect
        fadeOut = new Image(MyGame.manager.get("textures/pixel.png", Texture.class));
        fadeOut.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fadeOut.setColor(MyGame.backgroundWhite);
        fadeOut.getColor().a = 0f;
        staticTextStage.addActor(fadeOut);

        // Render
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / MyGame.PPM, MyGame.batch);

        // InputProcessor
        Gdx.input.setInputProcessor(new InputMultiplexer(
                gameStage,
                new InputAdapter() {
                    @Override
                    public boolean keyDown(int keycode) {
                        if (keycode == Input.Keys.R)
                            shouldRestart = true;
                        return true;
                    }
                }));
    }

    private void listenForCollisions() {
        // Kinda hacky code for fading out when the player gets inside
        // the finish point and fading back in if it leaves it
        int numBalloonsInFinishPoint = 0;
        for (Balloon balloon : balloons) {
            for (Finish finish : finishes)
                if (balloon.getCollisionBox().overlaps(finish.getCollisionBox()))
                    numBalloonsInFinishPoint++;
        }
        if (numBalloonsInFinishPoint != 0) {
            if (!isFadingOut) {
                shouldFadeOut = true;
                isFadingIn = false;
            }
        } else if (!isFadingIn) {
            shouldFadeIn = true;
            isFadingOut = false;
        }
        if (shouldFadeOut || shouldRestart) {
            shouldFadeOut = false;
            isFadingOut = true;
            removeAllActions(fadeOut);
            fadeOut.addAction(sequence(fadeIn(.4f)));
            if (shouldRestart) {
                shouldRestart = false;
                fadeOut.addAction(after(run(() -> restart = true)));
            }
        }
        if (shouldFadeIn) {
            shouldFadeIn = false;
            isFadingIn = true;
            removeAllActions(fadeOut);
            fadeOut.addAction(fadeOut(fadeOut.getColor().a));
        }
    }

    private void autoRestart() {
        for (int i = 0; i < balloons.size; i++)
            if (balloons.get(i).body.getPosition().y - mapHeight >= 1.5f)
                balloonsToRemove.add(balloons.get(i));
        balloons.removeAll(balloonsToRemove, true);
        if (balloons.size == 0)
            restart = true;
    }

    private BoundingBox getBalloonsBoundingBox(Array<Balloon> balloons) {
        Vector3 firstBalloonCenter = new Vector3(balloons.get(0).body.getWorldCenter(), 0f);
        BoundingBox box = new BoundingBox(firstBalloonCenter, firstBalloonCenter);
        if (balloons.size == 1)
            return box;
        else {
            for (int balloon = 1; balloon < balloons.size; balloon++)
                box.ext(new Vector3(balloons.get(balloon).body.getWorldCenter(), 0f));
            return box;
        }
    }

    private Vector3 getBalloonsCenterPoint(BoundingBox boundingBox) {
        Vector3 center = new Vector3();
        boundingBox.getCenter(center);
        return center;
    }

    private void zoomTheCamera(BoundingBox boundingBox, OrthographicCamera camera) {
        if (balloons.size > 1)
            camera.zoom = MathUtils.lerp(MIN_ZOOM, MAX_ZOOM, Math.min(getGreatestDistance(boundingBox) / mapWidth, 1f));
    }

    private void keepCameraWithinBounds() {
        OrthographicCamera camera = (OrthographicCamera) gameStage.getCamera();

        float mapLeft = 0f, mapRight = mapWidth;
        if (mapWidth > camera.viewportWidth) {
            mapLeft = -1;
            mapRight = mapWidth + 1;
        }
        float mapBottom = 0f, mapTop = mapHeight;
        float cameraHalfWidth = camera.viewportWidth / 2f, cameraHalfHeight = camera.viewportHeight / 2f;
        float cameraLeft = camera.position.x - cameraHalfWidth, cameraRight = camera.position.x - cameraHalfWidth;
        float cameraBottom = camera.position.y - cameraHalfHeight, cameraTop = camera.position.y + cameraHalfHeight;

        // Clam horizontal axis
        if (camera.viewportWidth > mapRight) camera.position.x = mapRight / 2f;
        else if (cameraLeft <= mapLeft) camera.position.x = mapLeft + cameraHalfWidth;
        else if (cameraRight >= mapRight) camera.position.x = mapRight - cameraHalfWidth;

        // Clamp vertical axis
        if (camera.viewportHeight > mapTop) camera.position.y = mapTop / 2f;
        else if (cameraBottom <= mapBottom) camera.position.y = mapBottom + cameraHalfHeight;
        else if (cameraTop >= mapTop) camera.position.y = mapTop - cameraHalfHeight;
    }

    private void removeAllActions(Actor actor) {
        for (int i = 0; i < actor.getActions().size; i++)
            actor.removeAction(actor.getActions().get(i));
    }

    private float getGreatestDistance(BoundingBox boundingBox) {
        return Math.max(boundingBox.getWidth(), boundingBox.getHeight());
    }
}
