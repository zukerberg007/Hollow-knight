package com.amirali.graphics.views;

public enum AnimationType {
    HOLLOW_KNIGHT_IDLE("animations/Idle.png", 9, 1, 9),
    HOLLOW_KNIGHT_IDLE_HURT("animations/Idle Hurt.png",12, 1, 12),
    HOLLOW_KNIGHT_RUN("animations/Run.png", 13, 1, 13),
    HOLLOW_KNIGHT_JUMP("animations/Airborne.png", 12, 1, 12),
    HOLLOW_KNIGHT_MASK_SHINE_FRAMES("animations/HUD/FilledHealthShine.png", 5, 1, 5),
    HOLLOW_KNIGHT_MASK_BREAK_FRAMES("animations/HUD/BreakHealth.png", 6, 1, 6),
    HOLLOW_KNIGHT_NORMAL_SLASH("animations/Slash.png", 5, 1, 5),
    HOLLOW_KNIGHT_NORMAL_SLASH_EFFECT("animations/Effects/SlashEffect.png", 4, 1, 12, new int[]{6, 7, 9 , 11}),
    HOLLOW_KNIGHT_SLASH_ALT("animations/SlashAlt.png", 5, 1, 5),
    HOLLOW_KNIGHT_SLASH_ALT_EFFECT("animations/Effects/SlashEffectAlt.png", 4, 1, 12, new int[]{6, 7, 9 , 11}),
    HOLLOW_KNIGHT_UP_SLASH_EFFECT("animations/Effects/UpSlashEffect.png", 6, 1,6),
    HOLLOW_KNIGHT_DOWN_SLASH_EFFECT("animations/Effects/DownSlashEffect.png", 6, 1, 6),
    HOLLOW_KNIGHT_MASK_REFILL_FRAMES("animations/HUD/HealthRefill.png", 5, 1, 5),
    HOLLOW_KNIGHT_DASH_FRAMES("animations/Dash.png", 12, 1, 12),
    HOLLOW_KNIGHT_LOOKING_DOWN_FRAMES("animations/LookDown.png", 6, 1, 6),
    HOLLOW_KNIGHT_LOOKING_UP_FRAMES("animations/LookUp.png", 6, 1, 6),
    HOLLOW_KNIGHT_LANDING_FRAMES("animations/Landing.png", 4, 1, 4),
    HOLLOW_KNIGHT_DASH_EFFECT("animations/Effects/Dash Effect.png", 8, 1, 8),
    HOLLOW_KNIGHT_DOUBLE_JUMP_FRAMES("animations/Double Jump.png", 8, 1, 8),
    HOLLOW_KNIGHT_FALL("animations/Fall.png", 6, 1, 6),
    HOLLOW_KNIGHT_DOWN_SLASH("animations/DownSlash.png", 5, 1, 5),
    HOLLOW_KNIGHT_UP_SLASH("animations/UpSlash.png", 5, 1, 5),
    HOLLOW_KNIGHT_WALL_SLIDE("animations/Wall Slide.png", 4, 1, 4),
    HOLLOW_KNIGHT_WALL_JUMP("animations/Walljump.png", 9, 1, 9),
    HOLLOW_KNIGHT_FOCUS("animations/Focus.png", 16, 1, 16),
    HOLLOW_KNIGHT_DEATH("animations/Death.png", 18, 1, 18),
    HOLLOW_KNIGHT_HOWLING_WRAITHS("animations/Scream.png", 7, 1, 7),
    HOLLOW_KNIGHT_HOWLING_WRAITHS_EFFECT("animations/Effects/SoulScream.png", 13, 1, 13),
    HOLLOW_KNIGHT_VENGEFUL_SPIRIT("animations/Fireball Cast.png", 9, 1, 9),
    HOLLOW_KNIGHT_VENGEFUL_SPIRIT_BLAST("animations/Effects/BlastSoul.png", 5, 1, 5),
    HOLLOW_KNIGHT_VENGEFUL_SPIRIT_PROJECTILE("animations/Projectile/SoulBall.png", 4, 1, 4),
    HOLLOW_KNIGHT_SHADOW_BALL("animations/Projectile/ShadowBall.png", 6, 1, 6),
    HOLLOW_KNIGHT_SHADOW_SCREAM("animations/Effects/ShadowScream.png", 14, 1, 14),
    MOSSCREEP_WALK_FRAMES("animations/Mosscreep/Walk.png", 3, 1, 3),
    MOSSCREEP_TURN_FRAMES("animations/Mosscreep/Turn.png", 3, 1, 3),
    MOSSCREEP_DEATH_AIR_FRAMES("animations/Mosscreep/Death Air.png", 4, 1, 4),
    MOSSCREEP_DEATH_LANDING_FRAMES("animations/Mosscreep/Death Land.png", 2, 1, 2),
    MOSQUITO_IDLE("animations/Mosquito/Idle.png", 8, 1, 8),
    MOSQUITO_ANTICIPATE("animations/Mosquito/Attack Anticipate.png", 6, 1, 6),
    MOSQUITO_ATTACK("animations/Mosquito/Attack.png", 3, 1, 3),
    MOSQUITO_DEATH_AIR("animations/Mosquito/Death Air.png", 3, 1, 3),
    MOSQUITO_DEATH_LAND("animations/Mosquito/Death Land.png", 2, 1, 2),
    MOSQUITO_TURN_L_TO_R("animations/Mosquito/Turn.png", 2, 1, 2),
    MOSQUITO_TURN_R_TO_L("animations/Mosquito/Turn2.png", 2, 1, 2),
    HUSK_HORNHEAD_WALK("animations/Husk_Hornhead/Walk.png", 7, 1, 7),
    HUSK_HORNHEAD_IDLE("animations/Husk_Hornhead/Idle.png", 6, 1, 6),
    HUSK_HORNHEAD_TURN("animations/Husk_Hornhead/Turn.png", 2, 1, 2),
    HUSK_HORNHEAD_ANTICIPATE("animations/Husk_Hornhead/Attack Anticipate.png", 5, 1, 5),
    HUSK_HORNHEAD_LUNGE("animations/Husk_Hornhead/Attack Lunge.png", 12, 1, 12),
    HUSK_HORNHEAD_DEATH_AIR("animations/Husk_Hornhead/Death Air.png", 1, 1, 1),
    HUSK_HORNHEAD_DEATH_LAND("animations/Husk_Hornhead/Death Land.png", 8, 1, 8),
    CRYSTAL_GUARDIAN_IDLE("animations/Crystallized/Idle.png", 5, 1, 5),
    CRYSTAL_GUARDIAN_EVADE("animations/Crystallized/Evade.png", 7, 1, 7),
    CRYSTAL_GUARDIAN_SHOOT("animations/Crystallized/Shoot.png", 7, 1, 7),
    CRYSTAL_GUARDIAN_RUN("animations/Crystallized/Run.png", 6, 1, 6),
    CRYSTAL_GUARDIAN_TURN("animations/Crystallized/Turn.png", 3, 1, 3),
    CRYSTAL_GUARDIAN_DEATH_AIR("animations/Crystallized/Death Air.png", 3, 1, 3),
    CRYSTAL_GUARDIAN_DEATH_LAND("animations/Crystallized/Death Land.png", 3, 1, 3),
    CRYSTAL_GUARDIAN_LASER("animations/Effects/CrystalLaser.png", 1, 1, 1),
    ZOTE_IDLE("animations/Zote/Idle.png", 5, 1, 5),
    ZOTE_TALK("animations/Zote/Talk.png", 5, 1, 5),
    ZOTE_TURN("animations/Zote/Turn.png", 2, 1, 2),
    ZOTE_ATTACK("animations/Zote/Attack.png",4 , 1, 4),
    ZOTE_FALL("animations/Zote/Fall.png", 5, 1, 5),
    ZOTE_GET_UP("animations/Zote/Get Up.png", 4, 1, 4),
    ZOTE_ROLL("animations/Zote/Roll.png", 3, 1, 3),
    FALSE_KNIGHT_IDLE("animations/False_knight/Idle.png", 5, 1, 5),
    FALSE_KNIGHT_BODY("animations/False_knight/Body.png", 5, 1, 5),
    FALSE_KNIGHT_RUN("animations/False_knight/Run.png", 5, 1, 5),
    FALSE_KNIGHT_ATTACK_ANTIC("animations/False_knight/Attack Antic.png", 6, 1, 6),
    FALSE_KNIGHT_ATTACK("animations/False_knight/Attack.png", 3, 1, 3),
    FALSE_KNIGHT_ATTACK_RECOVER("animations/False_knight/Attack Recover.png", 5, 1, 5),
    FALSE_KNIGHT_JUMP_ANTIC("animations/False_knight/Jump Antic.png", 3, 1, 3),
    FALSE_KNIGHT_JUMP("animations/False_knight/Jump.png", 4, 1, 4),
    FALSE_KNIGHT_JUMP_ATTACK("animations/False_knight/Jump Attack.png", 8, 1, 8),
    FALSE_KNIGHT_LAND("animations/False_knight/Land.png", 5, 1, 5),
    FALSE_KNIGHT_STUN_RECOVER("animations/False_knight/Stun Recover.png", 6, 1, 6),
    FALSE_KNIGHT_DEATH_HIT("animations/False_knight/DeathHit.png", 3, 1, 3),
    FALSE_KNIGHT_DEATH_FALL("animations/False_knight/DeathFall.png", 3, 1, 3),
    FALSE_KNIGHT_DEATH_LAND("animations/False_knight/DeathLand.png", 11, 1, 11),
    FALSE_KNIGHT_RUN_ANTIC("animations/False_knight/Run Antic.png", 2, 1, 2),
    FALSE_KNIGHT_TURN("animations/False_knight/Turn.png",      2, 1, 2),
    FALSE_KNIGHT_SHOCKWAVE("animations/Effects/Shockwave.png", 8, 1, 8),
        ;

    public final String path;
    public final int frameCount;
    public final int rowCount;
    public final int colCount;
    public final int[] activeCols;

    AnimationType(String path, int frameCount, int rowCount, int colCount) {
        this(path, frameCount, rowCount, colCount, null);
    }

    AnimationType(String path, int frameCount, int rowCount, int colCount, int[] activeCols) {
        this.path = path;
        this.frameCount = frameCount;
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.activeCols = activeCols;
    }
}
