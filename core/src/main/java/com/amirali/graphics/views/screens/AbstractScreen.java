package com.amirali.graphics.views.screens;

import com.amirali.graphics.GameAssetManager;
import com.amirali.graphics.BackgroundManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.io.FileNotFoundException;

public abstract class AbstractScreen implements Screen {
    protected Stage stage;

    private Stack mainStack;
    protected Table rootTable;
    private Stack modalStack;
    private Stack toastStack;

    protected Skin skin;

    protected VideoPlayer videoPlayer;
    private Camera videoCamera;
    private SpriteBatch videoBatch;

    protected boolean showBackground() {
        return true;
    }

    @Override
    public void show() {
        ScreenViewport viewport = new ScreenViewport();
        viewport.setUnitsPerPixel(0.5f);
        stage = new Stage(viewport);
        skin = GameAssetManager.skin;

        mainStack = new Stack();
        mainStack.setFillParent(true);

        if (showBackground()) {
            videoBatch = new SpriteBatch();
            videoCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            videoCamera.position.set(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, 0);
            videoCamera.update();

            videoPlayer = VideoPlayerCreator.createVideoPlayer();
            try {
                videoPlayer.play(Gdx.files.internal(BackgroundManager.currentFile()));
                videoPlayer.setLooping(true);
                videoPlayer.setVolume(0f);
            } catch (FileNotFoundException e) {
                Gdx.app.error("Video", "Background video file not found in assets.");
            } catch (Exception e) {
                Gdx.app.error("Video", "Video player threw an exception: " + e.getMessage());
            }

            if (GameAssetManager.menuMusic != null && !GameAssetManager.menuMusic.isPlaying()) {
                GameAssetManager.menuMusic.play();
            }
        } else {
            if (GameAssetManager.menuMusic != null && GameAssetManager.menuMusic.isPlaying()) {
                GameAssetManager.menuMusic.stop();
            }
        }

        modalStack = new Stack();
        toastStack = new Stack();
        rootTable = new Table();

        mainStack.add(rootTable);
        mainStack.add(modalStack);
        mainStack.add(toastStack);

        stage.addActor(mainStack);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if (showBackground() && videoPlayer != null && videoPlayer.isPlaying()) {
            videoPlayer.update();
            Texture frame = videoPlayer.getTexture();

            if (frame != null) {
                videoCamera.update();
                videoBatch.setProjectionMatrix(videoCamera.combined);
                videoBatch.begin();
                videoBatch.draw(frame, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                videoBatch.end();
            }
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        if (videoCamera != null) {
            videoCamera.viewportWidth = width;
            videoCamera.viewportHeight = height;
            videoCamera.position.set(width / 2f, height / 2f, 0);
        }
    }

    @Override
    public void pause() {
        if (videoPlayer != null && videoPlayer.isPlaying()) {
            videoPlayer.pause();
        }
    }

    @Override
    public void resume() {
        if (videoPlayer != null && !videoPlayer.isPlaying()) {
            videoPlayer.resume();
        }
    }

    @Override
    public void hide() {
        if (videoPlayer != null) {
            videoPlayer.stop();
            videoPlayer.dispose();
            videoPlayer = null;
        }
        if (videoBatch != null) {
            videoBatch.dispose();
            videoBatch = null;
        }
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (videoPlayer != null) videoPlayer.dispose();
        if (videoBatch != null) videoBatch.dispose();
    }

    public void reloadBackgroundVideo() {
        if (!showBackground()) return;

        if (videoPlayer != null) {
            videoPlayer.stop();
            videoPlayer.dispose();
            videoPlayer = null;
        }

        videoPlayer = VideoPlayerCreator.createVideoPlayer();
        try {
            videoPlayer.play(Gdx.files.internal(BackgroundManager.currentFile()));
            videoPlayer.setLooping(true);
            videoPlayer.setVolume(0f);
        } catch (FileNotFoundException e) {
            Gdx.app.error("Video", "Background video file not found: " + BackgroundManager.currentFile());
        } catch (Exception e) {
            Gdx.app.error("Video", "Video player threw an exception: " + e.getMessage());
        }
    }

    public Stack getModalStack() {
        return modalStack;
    }

    public void openToast(String message) {
        Table wrapper = new Table();
        wrapper.pad(10).right().bottom();

        Table toast = new Table();
        toast.pad(5);
        toast.setBackground(skin.getDrawable("window"));

        Label messageLabel = new Label(message, skin);

        toast.add(messageLabel).growX();

        wrapper.add(toast).minWidth(150);
        toastStack.add(wrapper);

        toast.addAction(
            Actions.sequence(
                Actions.moveBy(0, -100),
                Actions.moveBy(0, 100, 0.5f, Interpolation.swingOut)
            )
        );

        toast.setTouchable(Touchable.enabled);
        toast.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toast.addAction(
                    Actions.sequence(
                        Actions.alpha(0, 0.75f, Interpolation.smoother),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                wrapper.remove();
                            }
                        })
                    )
                );
            }
        });
    }
    public Table createHoverButton(String text, ClickListener clickListener) {
        Table buttonTable = new Table();

        final Image leftPointer = new Image(GameAssetManager.hoverPointer);

        com.badlogic.gdx.graphics.g2d.TextureRegion rightRegion = new com.badlogic.gdx.graphics.g2d.TextureRegion(GameAssetManager.hoverPointer);
        rightRegion.flip(true, false);
        final Image rightPointer = new Image(rightRegion);
        leftPointer.setColor(1, 1, 1, 0);
        rightPointer.setColor(1, 1, 1, 0);

        TextButton button = new TextButton(text, skin);
        button.addListener(clickListener);
        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                leftPointer.setColor(1, 1, 1, 1);
                rightPointer.setColor(1, 1, 1, 1);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                leftPointer.setColor(1, 1, 1, 0);
                rightPointer.setColor(1, 1, 1, 0);
            }
        });

        float pointerSpacing = 20f;

        buttonTable.add(leftPointer).size(32, 32).padRight(pointerSpacing);
        buttonTable.add(button);
        buttonTable.add(rightPointer).size(32, 32).padLeft(pointerSpacing);

        return buttonTable;
    }
}
