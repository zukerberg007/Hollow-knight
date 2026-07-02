package com.amirali.graphics;

import com.amirali.graphics.models.Game;
import com.amirali.graphics.views.modals.InventoryModal;
import com.amirali.graphics.views.modals.PauseModal;
import com.amirali.graphics.views.screens.MainMenuScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class GameProcessor implements InputProcessor {
    private final Game game;

    public GameProcessor(Game game) {
        this.game = game;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (uiManager.getScreen().getModalStack().getChildren().size > 0) {
            if (keycode == KeyBindings.INVENTORY) {
                uiManager.getScreen().getModalStack().clearChildren();
            }
            return false;
        }

        if (keycode == Input.Keys.ESCAPE) {
            game.player.resetInputs();
            PauseModal pauseModal = new PauseModal();
            pauseModal.show();
        } else if (keycode == KeyBindings.INVENTORY) {
            game.player.resetInputs();
            InventoryModal inventoryModal = new InventoryModal(game.player);
            inventoryModal.show();
        } else if (keycode == KeyBindings.MOVE_RIGHT) {
            game.player.movingRight = true;
        } else if (keycode == KeyBindings.MOVE_LEFT) {
            game.player.movingLeft = true;
        } else if (keycode == Input.Keys.DOWN) {
            game.player.isLookingDown = true;
        } else if (keycode == Input.Keys.UP) {
            game.player.isLookingUp = true;
        } else if (keycode == KeyBindings.JUMP) {
            game.player.jump();
        } else if (keycode == KeyBindings.ATTACK) {
            boolean isAirborne = !game.player.isOnGround || game.player.velocity.y > 0;
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                game.player.upSlash();
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && isAirborne) {
                game.player.downSlash();
            } else {
                game.player.attack();
            }
        } else if (keycode == KeyBindings.FOCUS) {
            game.player.startFocus();
        } else if (keycode == KeyBindings.DASH) {
            game.player.dash();
        } else if (keycode == Input.Keys.S) {
            game.player.castVengefulSpirit();
        } else if (keycode == Input.Keys.D) {
            game.player.castHowlingWraiths();
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == KeyBindings.MOVE_RIGHT) game.player.movingRight = false;
        else if (keycode == KeyBindings.MOVE_LEFT) game.player.movingLeft = false;
        else if (keycode == Input.Keys.DOWN) game.player.isLookingDown = false;
        else if (keycode == Input.Keys.UP) game.player.isLookingUp = false;
        else if (keycode == KeyBindings.FOCUS) game.player.cancelFocus();
        else if (keycode == KeyBindings.JUMP) game.player.setJumpHeld(false);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
