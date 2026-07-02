package com.amirali.graphics;

import com.amirali.graphics.models.GameData;
import com.amirali.graphics.models.Player;
import com.amirali.graphics.models.enemies.Zote;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class SaveManager {
    private static final Json json = new Json();

    static {
        json.setOutputType(JsonWriter.OutputType.json);
    }

    private static FileHandle getFile(int slotIndex) {
        return Gdx.files.local("save_slot_" + slotIndex + ".json");
    }

    public static void saveGame(GameData data, Player player, Zote zote) {
        if (data == null) return;

        data.playerX = player.position.x;
        data.heightY = player.position.y;
        data.masks = player.masks;
        data.maxMasks = player.maxMasks;
        data.soul = player.soul;
        data.hasVoidHeart = player.hasVoidHeart;
        data.hasSharpShadow = player.hasSharpShadow;
        data.hasUnbreakableStrength = player.hasUnbreakableStrength;
        data.hasQuickSlash = player.hasQuickSlash;

        int charmCount = (player.hasVoidHeart ? 1 : 0) + (player.hasSharpShadow ? 1 : 0);
        data.progress = 10 + (player.maxMasks * 10) + (charmCount * 15);
        if (data.falseKnightDefeated) data.progress += 20;

        if (zote != null) {
            data.zoteDialogueIndex = zote.mainDialogueIndex;
            data.zoteFinishedMain = zote.hasFinishedMainDialogue;
        }

        FileHandle file = getFile(data.slotIndex);
        file.writeString(json.toJson(data), false);
        System.out.println("Game saved successfully to slot " + data.slotIndex);
    }

    public static GameData loadGame(int slotIndex) {
        FileHandle file = getFile(slotIndex);
        if (!file.exists()) return null;

        try {
            return json.fromJson(GameData.class, file.readString());
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Error parsing save file for slot " + slotIndex);
            return null;
        }
    }

    public static void deleteSave(int slotIndex) {
        FileHandle file = getFile(slotIndex);
        if (file.exists()) {
            file.delete();
            System.out.println("Save slot " + slotIndex + " cleared.");
        }
    }
}
