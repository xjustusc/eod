package com.boliao.eod;

import com.badlogic.gdx.Screen;
import com.boliao.eod.components.Combat;
import com.boliao.eod.components.Health;
import com.boliao.eod.components.Input;
import com.boliao.eod.components.Movement;
import com.boliao.eod.components.Transform;
import com.boliao.eod.components.ai.FsmBug;
import com.boliao.eod.components.ai.FsmPlayer;
import com.boliao.eod.components.ai.SteeringArrive;
import com.boliao.eod.components.ai.SteeringPursue;
import com.boliao.eod.components.collision.Collider;
import com.boliao.eod.components.render.SpriteHealth;
import com.boliao.eod.components.render.Sprite;
import com.boliao.eod.components.render.SpriteBam;
import com.boliao.eod.components.render.SpriteInput;
import com.boliao.eod.components.render.SpriteSheetBug;
import com.boliao.eod.components.render.SpriteSheetPlayer;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mrboliao on 16/1/17.
 */

public class PlayScreen implements Screen {
    private final int CAMSPEED = 100;

    private boolean paused = false;

    // game singletons
    Game game = Game.i();
    com.boliao.eod.GameState gameState = com.boliao.eod.GameState.i();

    // game objects list
    List<GameObject> gameObjects;

    // Renderables list
    List<com.boliao.eod.components.render.Renderable> renderables;

    // Tiled stuff
//    private TiledMap map;
//    private TmxMapLoader mapLoader;
//    private OrthogonalTiledMapRenderer mapRenderer;

    /**
     * Ctor.
     */
    public PlayScreen () {
        // init game objects list
        gameObjects = new LinkedList<GameObject>();

        init();
    }

    public void init() {
        // init house
        GameObject house = new GameObject("house");
        gameObjects.add(house);
        house.addComponent(new Transform(SETTINGS.HOUSE_POS_X, SETTINGS.HOUSE_POS_Y, 0));
        house.addComponent(new Sprite("sprites/house.png", SETTINGS.HOUSE_SIZE));
        house.init();

        // init block
        GameObject block = new GameObject("block");
        gameObjects.add(block);
        block.addComponent(new Transform(SETTINGS.BLOCK_POS_X, SETTINGS.BLOCK_POS_Y, 0));
        block.addComponent(new Sprite("sprites/block.png", SETTINGS.BLOCK_SIZE));
        block.addComponent(new Collider());
        block.init();

        // init human
        GameObject player = new GameObject("player");
        gameObjects.add(player);
        player.addComponent(new Transform(SETTINGS.PLAYER_POS_X, SETTINGS.PLAYER_POS_Y, 0));
        player.addComponent(new SpriteSheetPlayer("sprites/player.txt"));
        player.addComponent(new Collider(false, false));
        player.addComponent(new Movement());
        player.addComponent(new SteeringArrive());
        player.addComponent(new FsmPlayer());
        player.addComponent(new Input(Input.InputType.TOUCH));
        player.addComponent(new SpriteInput("sprites/x.png"));
        player.addComponent(new Health());
        player.addComponent(new SpriteHealth("sprites/healthbar.png"));
        player.init();

        // test init bug
        GameObject bug = new GameObject("bug");
        gameObjects.add(bug);
        bug.addComponent(new Transform(SETTINGS.BUG_POS_X, SETTINGS.BUG_POS_Y, 50));
        bug.addComponent(new SpriteSheetBug("sprites/bug1.txt"));
        bug.addComponent(new Movement(SETTINGS.SPEED_BUG));
        bug.addComponent(new Collider(false, false));
        bug.addComponent(new SteeringPursue(player));
        bug.addComponent(new Combat(player));
        bug.addComponent(new FsmBug());
        bug.addComponent(new SpriteBam("sprites/bam.png"));
        bug.init();

//        mapLoader = new TmxMapLoader();
//        map = mapLoader.load("level0.tmx");
//        mapRenderer = new OrthogonalTiledMapRenderer(map);
    }

    public void restart() {
        dispose();
        gameObjects.clear();

        init();
    }

    /**
     * The gameloop.
     * @param delta
     */
    @Override
    public void render(float delta) {
        if (!paused) {
            // process game object updates
            for (GameObject go: gameObjects) {
                go.update(delta);
            }

            // process collisions
            CollisionEngine.i().tick();
        }

        // process graphics
        RenderEngine.i().tick();
    }

    @Override
    public void resize(int width, int height) {
        RenderEngine.i().setViewport(width, height);
    }

    @Override
    public void show() {

    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        for (GameObject go: gameObjects) {
            go.finalize();
        }
    }
}
