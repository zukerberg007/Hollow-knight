package com.amirali.graphics.models;

public class GameData {
    public int slotIndex;
    public int progress;

    public float playerX;
    public float heightY;

    public int masks;
    public int maxMasks;
    public int soul;

    public boolean hasVoidHeart;
    public boolean hasSharpShadow;
    public boolean hasUnbreakableStrength;
    public boolean hasQuickSlash;

    public int zoteDialogueIndex;
    public boolean zoteFinishedMain;

    public boolean falseKnightDefeated = false;
    public int     deaths             = 0;
    public float   playTime           = 0f;
    public String  killedEnemyTypes   = "";
    public int     killedEnemiesCount = 0;

    public GameData() {}
}
