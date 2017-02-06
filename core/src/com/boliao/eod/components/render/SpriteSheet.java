package com.boliao.eod.components.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.boliao.eod.GameObject;
import com.boliao.eod.RenderEngine;
import com.boliao.eod.SETTINGS;
import com.boliao.eod.components.*;

/**
 * Created by mrboliao on 24/1/17.
 */

public class SpriteSheet extends Component implements Renderable {
    private static final String TAG = "SpriteSheet:C;R";

    public enum Sequence {RUN, MELEE}
    protected Sequence sequence;
    protected int startFrame, endFrame;

    protected Transform transform;
    protected TextureAtlas spriteSheet;
    protected Array<com.badlogic.gdx.graphics.g2d.Sprite> sprites;
    protected com.badlogic.gdx.graphics.g2d.Sprite currSprite;
    protected int currSpriteIndex = 0;
    protected boolean isAnimated = false;
    protected float animationElapsedTime = 0;

    /**
     * Use the same name
     * @param spritePath
     * @param size
     */
    public SpriteSheet(String name, String spritePath, int size) {
        super(name);

        // init spritesheet
        spriteSheet = new TextureAtlas(spritePath);
        sprites = spriteSheet.createSprites();
        currSprite = sprites.get(0);
        startFrame = endFrame = 0;

        // init sprites
        for (com.badlogic.gdx.graphics.g2d.Sprite sprite: sprites) {
            //sprite.setOriginCenter();
            sprite.setSize(size, size);
            //sprite.setScale(0.1f);
            sprite.setOriginCenter();
        }

        // add to render engine
        RenderEngine.i().addRenderable(this);
    }

    public SpriteSheet (String spritePath, int size) {
        this("SpriteSheet", spritePath, size);
    }

    public SpriteSheet(String spritePath) {
        this(spritePath, SETTINGS.SPRITE_SIZE);
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        // setup links
        transform = (Transform) owner.getComponent("Transform");
    }

    @Override
    public Rectangle getBoundingBox() {
        return currSprite.getBoundingRectangle();
    }

    public void setSequence(Sequence seq) {
        this.sequence = seq;
        switch(seq) {
            case RUN:
                startFrame = 0;
                endFrame = 2;
                break;

            case MELEE:
                startFrame = 3;
                endFrame = 9;
                break;
        }
        currSpriteIndex = startFrame;
    }

    @Override
    public void update(float delta) {
        // set position
        for (com.badlogic.gdx.graphics.g2d.Sprite sprite: sprites) {
            sprite.setRotation(transform.getRot());
            sprite.setCenter(transform.getX(), transform.getY());
        }

        // do animation
        if (isAnimated) {
            animationElapsedTime += delta;
            if (animationElapsedTime > SETTINGS.ANIM_FRAME_TIME) {
                currSpriteIndex = (currSpriteIndex == endFrame) ? startFrame : ++currSpriteIndex;
                currSprite = sprites.get(currSpriteIndex);
                animationElapsedTime = 0;
            }
        }
    }

    public void onAnimation(Sequence seq) {
        isAnimated = true;
        setSequence(seq);
    }

    public void offAnimation() {
        isAnimated = false;
    }

    public void draw() {
        currSprite.draw(RenderEngine.i().getSpriteBatch());
    }

    @Override
    public void finalize() {
        super.finalize();

        // opengl textures are not auto deleted
        for (com.badlogic.gdx.graphics.g2d.Sprite sprite: sprites) {
            sprite.getTexture().dispose();
        }
    }
}