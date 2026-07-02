package com.amirali.graphics.views.screens;

import com.amirali.graphics.GameAssetManager;
import com.amirali.graphics.GameProcessor;
import com.amirali.graphics.TiledMapHelper;
import com.amirali.graphics.models.*;
import com.amirali.graphics.models.enemies.CrystalGuardian;
import com.amirali.graphics.models.enemies.Mosscreep;
import com.amirali.graphics.models.enemies.HuskHornhead;
import com.amirali.graphics.models.enemies.Mosquito;
import com.amirali.graphics.models.enemies.Zote;
import com.amirali.graphics.uiManager;
import com.amirali.graphics.views.AnimationType;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.amirali.graphics.views.GameHud;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import static com.amirali.graphics.models.Player.BLAST_DURATION;
import com.amirali.graphics.models.enemies.FalseKnight;
import com.badlogic.gdx.math.MathUtils;

public class GameScreen extends AbstractScreen {

    private SpriteBatch batch;
    private Camera camera;
    private ScreenViewport viewport;
    private ShapeRenderer shapeRenderer;

    private int[] backgroundLayers;
    private int[] foregroundLayers;

    private GameProcessor gameProcessor;
    private final Game game;
    private GameHud hud;
    private int activeSlotIndex = 1;
    private GameData loadedData;

    public GameScreen(Game game, GameData loadedData, int slotIndex) {
        this.game = game;
        this.loadedData = loadedData;
        this.activeSlotIndex = slotIndex;
    }
    public void saveCurrentGame() {
        com.amirali.graphics.SaveManager.saveGame(loadedData, game.player, zote);
        openToast("Game Saved!");
    }

    private OrthogonalTiledMapRenderer renderer;
    private Array<SolidBlock> solidBlocks;
    private TiledMapHelper mapHelper;

    private Array<Mosscreep> mosscreeps;
    private Array<Mosquito> mosquitoes;
    private Array<HuskHornhead> huskHornheads;
    private Array<CrystalGuardian> crystalGuardians;
    private FalseKnight falseKnight;
    private Rectangle bossArena;
    private boolean bossFightActive = false;
    private boolean bossDefeatedLatch = false;
    private SolidBlock leftGate, rightGate;

    private float shakeTime = 0f, shakeMag = 0f, shakeDuration = 0.35f;
    private boolean wasPlayerDying = false;

    // --- NEW: A timer to delay the end screen so we can watch the boss fall! ---
    private float endScreenTimer = 0f;

    private Zote zote;
    private boolean isDialogueActive = false;
    private String currentDialogueText = "";
    private float dialogueTypewriterTimer = 0f;

    private Array<BreakableWall> breakableWalls;
    private TiledMapTileLayer secretCoverLayer;
    private ParticleEffect rockParticles;
    private ParticleEffect ambientParticles;

    private Rectangle voidHeartBounds;
    private boolean voidHeartCollected = false;
    private Texture voidHeartTexture;

    private com.amirali.graphics.AreaMusicManager areaMusic;

    @Override
    protected boolean showBackground() {
        return false;
    }

    @Override
    public void show() {
        super.show();

        mapHelper = new TiledMapHelper();
        TiledMap map = mapHelper.loadMap("map/hollow.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        Array<Integer> bgList = new Array<>();
        int bg2 = map.getLayers().getIndex("background2");
        int bg1 = map.getLayers().getIndex("background");
        int mainL = map.getLayers().getIndex("main");

        if (bg2 != -1) bgList.add(bg2);
        if (bg1 != -1) bgList.add(bg1);
        if (mainL != -1) bgList.add(mainL);

        backgroundLayers = new int[bgList.size];
        for (int i = 0; i < bgList.size; i++) backgroundLayers[i] = bgList.get(i);

        Array<Integer> fgList = new Array<>();
        int fgL = map.getLayers().getIndex("foreground");
        if (fgL != -1) fgList.add(fgL);

        MapLayer sLayer = map.getLayers().get("secret_cover");
        if (sLayer instanceof TiledMapTileLayer) {
            secretCoverLayer = (TiledMapTileLayer) sLayer;
            fgList.add(map.getLayers().getIndex("secret_cover"));
        }

        foregroundLayers = new int[fgList.size];
        for (int i = 0; i < fgList.size; i++) foregroundLayers[i] = fgList.get(i);

        solidBlocks = mapHelper.getSolidRectangles();

        breakableWalls = mapHelper.getBreakableWalls();
        for (BreakableWall wall : breakableWalls) {
            solidBlocks.add(wall);
        }
        game.player.hasVoidHeart = false;

        voidHeartBounds = mapHelper.getVoidHeartBounds();
        try {
            voidHeartTexture = new Texture(Gdx.files.internal("charm_8.png"));
        } catch (Exception e) {
            Gdx.app.error("Texture", "Missing charm_8.png! Using fallback.");
        }
        if (loadedData != null) {
            game.player.position.set(loadedData.playerX, loadedData.heightY);
            game.player.masks = loadedData.masks;
            game.player.maxMasks = loadedData.maxMasks;
            game.player.soul = loadedData.soul;
            game.player.hasVoidHeart = loadedData.hasVoidHeart;
            game.player.hasSharpShadow = loadedData.hasSharpShadow;
            game.player.hasUnbreakableStrength = loadedData.hasUnbreakableStrength;
            game.player.hasQuickSlash = loadedData.hasQuickSlash;
            game.player.syncBounds();
            game.player.setRespawnPosition(new Vector2(loadedData.playerX, loadedData.heightY));
        } else {
            loadedData = new GameData();
            loadedData.slotIndex = activeSlotIndex;

            Vector2 spawn = mapHelper.getPlayerSpawnPoint();
            if (spawn != null) {
                game.player.position.set(spawn);
                game.player.setRespawnPosition(spawn);
                game.player.syncBounds();
            }
        }

        areaMusic = new com.amirali.graphics.AreaMusicManager();
        areaMusic.update(game.player.position.x, 0f);

        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        viewport = new ScreenViewport(camera);
        shapeRenderer = new ShapeRenderer();

        rockParticles = new ParticleEffect();
        try {
            rockParticles.load(Gdx.files.internal("particles/rocks.p"), Gdx.files.internal("particles"));
            rockParticles.scaleEffect(3.0f);
        } catch (Exception e) {
            Gdx.app.error("Particles", "Missing particles/rocks.p file! Game is safe, but particles won't show.");
        }
        ambientParticles = new ParticleEffect();
        try {
            ambientParticles.load(Gdx.files.internal("particles/ambient_dust.p"), Gdx.files.internal("particles"));
            ambientParticles.start();
        } catch (Exception e) {
            Gdx.app.error("Particles", "Missing ambient_dust.p file!");
        }

        mosscreeps = new Array<>();
        Array<Vector2> spawns = mapHelper.getMosscreepSpawnPoints();
        for (Vector2 pos : spawns) {
            if (pos != null) {
                Mosscreep mc = new Mosscreep(pos.x, pos.y, 70, 70, 2);
                mc.setPlayer(game.player);
                mosscreeps.add(mc);
            }
        }

        mosquitoes = new Array<>();
        Array<Vector2> mosquitoSpawns = mapHelper.getMosquitoSpawnPoints();
        for (Vector2 pos : mosquitoSpawns) {
            if (pos != null) {
                Mosquito m = new Mosquito(pos.x, pos.y, 90, 60, 3);
                m.setPlayer(game.player);
                mosquitoes.add(m);
            }
        }

        huskHornheads = new Array<>();
        Array<Vector2> huskSpawns = mapHelper.getHuskHornheadSpawnPoints();
        for (Vector2 pos : huskSpawns) {
            if (pos != null) {
                HuskHornhead h = new HuskHornhead(pos.x, pos.y, 90, 60, 3);
                h.setPlayer(game.player);
                huskHornheads.add(h);
            }
        }

        crystalGuardians = new Array<>();
        Array<Vector2> cgSpawns = mapHelper.getCrystalGuardianSpawnPoints();
        for (Vector2 pos : cgSpawns) {
            if (pos != null) {
                CrystalGuardian cg = new CrystalGuardian(pos.x, pos.y, 70, 80, 5);
                cg.setPlayer(game.player);
                crystalGuardians.add(cg);
            }
        }

        Vector2 zoteSpawn = mapHelper.getZoteSpawnPoint();
        if (zoteSpawn != null) {
            zote = new Zote(zoteSpawn.x, zoteSpawn.y);
            zote.setPlayer(game.player);

            if (loadedData != null) {
                zote.mainDialogueIndex = loadedData.zoteDialogueIndex;
                zote.hasFinishedMainDialogue = loadedData.zoteFinishedMain;
            }
        }
        Vector2 fkSpawn = mapHelper.getFalseKnightSpawnPoint();
        if (fkSpawn != null) {
            falseKnight = new FalseKnight(fkSpawn.x, fkSpawn.y, 70f);
            falseKnight.setPlayer(game.player);
        }
        bossArena = mapHelper.getBossArenaBounds();

        hud = new GameHud(game.player);

        gameProcessor = new GameProcessor(game);
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(gameProcessor);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void handleCheatCodes() {
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
                if (bossArena != null) {
                    game.player.position.set(bossArena.x + bossArena.width / 2f, bossArena.y + 50f);
                } else {
                    game.player.position.set(4500f, -200f);
                }
                game.player.velocity.set(0, 0);
                game.player.syncBounds();
                openToast("Cheat Enabled: Teleported to Boss Arena!");
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
                game.player.isNoclip = !game.player.isNoclip;
                game.player.velocity.set(0, 0);
                openToast("Noclip Flight Mode: " + (game.player.isNoclip ? "ENABLED (Use Arrows/WASD)" : "DISABLED"));
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
                game.player.masks = game.player.maxMasks;
                openToast("Cheat Enabled: Emergency Health Restored!");
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
                game.player.soul = Player.MAX_SOUL;
                openToast("Cheat Enabled: Soul Vessel Refilled!");
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
                game.player.isGodMode = !game.player.isGodMode;
                openToast("God Mode (Invincibility): " + (game.player.isGodMode ? "ENABLED" : "DISABLED"));
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
                int killCount = 0;
                for (Mosscreep c : mosscreeps) { if (c.isAlive()) { c.takeDamage(99f, 0); killCount++; } }
                for (Mosquito m : mosquitoes) { if (m.isAlive()) { m.takeDamage(99f, 0); killCount++; } }
                for (HuskHornhead h : huskHornheads) { if (h.isAlive()) { h.takeDamage(99f, 0); killCount++; } }
                for (CrystalGuardian cg : crystalGuardians) { if (cg.isAlive()) { cg.takeDamage(99f, 0); killCount++; } }
                openToast("Cheat Enabled: Insta-Killed " + killCount + " Enemies on Screen!");
            }
        }
    }

    private void eraseWallTiles(Rectangle bounds) {
        TiledMap map = renderer.getMap();
        String[] layersToCheck = {"main", "foreground"};

        for (String layerName : layersToCheck) {
            MapLayer ml = map.getLayers().get(layerName);
            if (ml instanceof TiledMapTileLayer) {
                TiledMapTileLayer layer = (TiledMapTileLayer) ml;
                float tileW = layer.getTileWidth();
                float tileH = layer.getTileHeight();

                int startX = (int) (bounds.x / tileW);
                int endX   = (int) ((bounds.x + bounds.width - 1) / tileW);
                int startY = (int) (bounds.y / tileH);
                int endY   = (int) ((bounds.y + bounds.height - 1) / tileH);

                for (int x = startX; x <= endX; x++) {
                    for (int y = startY; y <= endY; y++) {
                        layer.setCell(x, y, null);
                    }
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
        if (hud != null) hud.resize(width, height);
    }

    @Override
    public void render(float delta) {

        delta = Math.min(delta, 0.05f);

        ScreenUtils.clear(0.5f, 0.5f, 0.5f, 1f);
        boolean isPaused = getModalStack().getChildren().size > 0;

        if (areaMusic != null) areaMusic.update(game.player.position.x, delta);

        if (!isPaused) {
            loadedData.playTime += delta;

            if (game.player.isDying && !wasPlayerDying) {
                loadedData.deaths++;
                wasPlayerDying = true;
            } else if (!game.player.isDying) {
                wasPlayerDying = false;
            }
            int currentKills = 0;
            boolean killedMoss = false, killedSkeet = false, killedHusk = false, killedCg = false;

            for (Mosscreep c : mosscreeps) if (!c.isAlive()) { currentKills++; killedMoss = true; }
            for (Mosquito m : mosquitoes) if (!m.isAlive()) { currentKills++; killedSkeet = true; }
            for (HuskHornhead h : huskHornheads) if (!h.isAlive()) { currentKills++; killedHusk = true; }
            for (CrystalGuardian cg : crystalGuardians) if (!cg.isAlive()) { currentKills++; killedCg = true; }

            loadedData.killedEnemiesCount = currentKills;
            com.badlogic.gdx.Preferences achPrefs = Gdx.app.getPreferences("HollowKnightAchievements");
            boolean savePrefs = false;

            if (killedMoss && killedSkeet && killedHusk && killedCg && !achPrefs.getBoolean("True Hunter", false)) {
                achPrefs.putBoolean("True Hunter", true); savePrefs = true;
                openToast("Achievement Unlocked: True Hunter!");
            }
            if (game.player.soul >= Player.MAX_SOUL && !achPrefs.getBoolean("Soul Vessel", false)) {
                achPrefs.putBoolean("Soul Vessel", true); savePrefs = true;
                openToast("Achievement Unlocked: Soul Vessel!");
            }

            // --- FIX: Safely check if False Knight's health hit 0, start the timer ---
            if (falseKnight != null && !falseKnight.isAlive() && !bossDefeatedLatch) {
                endScreenTimer += delta;

                // Wait 3.5 seconds to watch the death animation play out before cutting to the end screen
                if (endScreenTimer >= 3.5f) {
                    bossDefeatedLatch = true;
                    loadedData.falseKnightDefeated = true;

                    if (!achPrefs.getBoolean("Completion", false)) { achPrefs.putBoolean("Completion", true); savePrefs = true; }
                    if (!achPrefs.getBoolean("Defeat False Knight", false)) { achPrefs.putBoolean("Defeat False Knight", true); savePrefs = true; }
                    if (loadedData.playTime < 600f && !achPrefs.getBoolean("Speedrun", false)) {
                        achPrefs.putBoolean("Speedrun", true); savePrefs = true;
                    }

                    if (savePrefs) achPrefs.flush();
                    saveCurrentGame();

                    if (areaMusic != null) areaMusic.stop();
                    uiManager.setScreen(new EndScreen(loadedData));
                    return;
                }
            }

            if (savePrefs) achPrefs.flush();

            handleCheatCodes();

            game.update(delta, solidBlocks);

            if (voidHeartBounds != null && !voidHeartCollected) {
                boolean roomRevealed = (secretCoverLayer == null || !secretCoverLayer.isVisible());

                if (roomRevealed && game.player.getBounds().overlaps(voidHeartBounds)) {
                    voidHeartCollected = true;
                    game.player.hasVoidHeart = true;
                    openToast("Acquired: Void Heart Charm!");
                }
            }

            rockParticles.update(delta);
            if (ambientParticles != null) {
                ambientParticles.update(delta);
                ambientParticles.setPosition(camera.position.x, camera.position.y);
            }

            float nailDamage = game.player.hasUnbreakableStrength ? 2 * Player.NAIL_DAMAGE : Player.NAIL_DAMAGE;
            float spellDamage = game.player.hasVoidHeart ? 1.5f : 1.0f;
            float shadowDamage = 1.0f;

            if (zote != null) {
                zote.update(delta, solidBlocks);

                boolean nearZote = game.player.getBounds().overlaps(zote.getInteractBounds()) && zote.state == Zote.State.IDLE;

                if (nearZote && Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.UP)) {
                    if (!isDialogueActive) {
                        isDialogueActive = true;
                        zote.state = Zote.State.TALKING;
                        dialogueTypewriterTimer = 0f;

                        GameAssetManager.playRandomZoteSound();

                        if (!zote.hasFinishedMainDialogue) {
                            currentDialogueText = zote.mainDialogues[zote.mainDialogueIndex];
                        } else {
                            currentDialogueText = zote.precepts[com.badlogic.gdx.math.MathUtils.random(0, zote.precepts.length - 1)];
                        }
                    }
                }

                if (isDialogueActive) {
                    game.player.velocity.x = 0;

                    if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
                        if (!zote.hasFinishedMainDialogue) {
                            zote.mainDialogueIndex++;
                            if (zote.mainDialogueIndex >= zote.mainDialogues.length) {
                                zote.hasFinishedMainDialogue = true;
                                isDialogueActive = false;
                                zote.state = Zote.State.IDLE;
                            } else {
                                currentDialogueText = zote.mainDialogues[zote.mainDialogueIndex];
                                dialogueTypewriterTimer = 0f;
                                GameAssetManager.playRandomZoteSound();
                            }
                        } else {
                            isDialogueActive = false;
                            zote.state = Zote.State.IDLE;
                        }
                    }
                }
            }

            for (Mosscreep c : mosscreeps) c.update(delta, solidBlocks);
            for (Mosquito m : mosquitoes) m.update(delta, solidBlocks);
            for (HuskHornhead t : huskHornheads) t.update(delta, solidBlocks);
            for (CrystalGuardian cg : crystalGuardians) cg.update(delta, solidBlocks);

            if (falseKnight != null) {

                if (bossArena != null && !bossFightActive && !game.player.isDying) {
                    float playerLeft = game.player.getBounds().x;
                    float playerRight = playerLeft + game.player.getBounds().width;

                    boolean safelyInside = (playerLeft > bossArena.x + 100f) &&
                        (playerRight < bossArena.x + bossArena.width - 100f) &&
                        game.player.getBounds().overlaps(bossArena);

                    if (safelyInside) {
                        bossFightActive = true;
                        falseKnight.activate();
                        closeGates();
                    }
                }

                falseKnight.update(delta, solidBlocks);

                if (falseKnight.isAlive()) {
                    Rectangle bb = falseKnight.getActiveHitbox();

                    if (game.player.isAttacking() && bb.overlaps(game.player.getAttackBox())
                        && game.player.tryConsumeHit()) {
                        falseKnight.takeDamage(nailDamage, game.player.position.x);
                        game.player.addSoul(Player.SOUL_PER_HIT);
                    }
                    if (game.player.isDownSlashing && !game.player.pogoHit
                        && bb.overlaps(game.player.getDownSlashBox())) {
                        falseKnight.takeDamagePogo(nailDamage);
                        game.player.pogoBounce();
                        game.player.addSoul(Player.SOUL_PER_HIT);
                    }
                    if (game.player.isUpSlashing && !game.player.upHitConsumed
                        && bb.overlaps(game.player.getUpSlashBox())) {
                        falseKnight.takeDamage(nailDamage, game.player.position.x);
                        game.player.addSoul(Player.SOUL_PER_HIT);
                        game.player.upHitConsumed = true;
                    }
                    if (game.player.isDashing && game.player.hasSharpShadow
                        && falseKnight.getBounds().overlaps(game.player.getBounds()) && !game.player.isEnemyHit(falseKnight)) {
                        falseKnight.takeDamage(1f, game.player.position.x);
                        game.player.markEnemyHit(falseKnight);
                    }
                    if (game.player.isVengefulSpiritActive
                        && falseKnight.getBounds().overlaps(game.player.getVengefulSpiritBox())
                        && !game.player.isEnemyHit(falseKnight)) {
                        falseKnight.takeDamage(spellDamage, game.player.spiritPosition.x);
                        game.player.addSoul(Player.SOUL_PER_HIT);
                        game.player.markEnemyHit(falseKnight);
                    }
                    if (game.player.isHowlingWraithsActive
                        && falseKnight.getBounds().overlaps(game.player.getHowlingWraithsBox())
                        && game.player.tryHowlingWraithsHit()) {
                        falseKnight.takeDamage(spellDamage, game.player.position.x);
                        game.player.addSoul(Player.SOUL_PER_HIT);
                    }
                }

                float s = falseKnight.consumeShake();
                if (s > 0f && (s >= shakeMag || shakeTime <= 0f)) {
                    shakeTime = 0.35f;
                    shakeDuration = 0.35f;
                    shakeMag = s;
                }

            }
            if (bossFightActive && game.player.isDying && falseKnight != null) {
                bossFightActive = false;
                openGates();
                Vector2 s = mapHelper.getFalseKnightSpawnPoint();
                if (s != null) falseKnight.reset(s.x, s.y);
            }

            if (game.player.isDashing && game.player.hasSharpShadow) {
                Rectangle playerBox = game.player.getBounds();
                for (Mosscreep c : mosscreeps) {
                    if (c.isAlive() && playerBox.overlaps(c.getBounds()) && !game.player.isEnemyHit(c)) {
                        c.takeDamage(shadowDamage, game.player.position.x);
                        game.player.markEnemyHit(c);
                    }
                }
                for (Mosquito c : mosquitoes) {
                    if (c.isAlive() && playerBox.overlaps(c.getBounds()) && !game.player.isEnemyHit(c)) {
                        c.takeDamage(shadowDamage, game.player.position.x);
                        game.player.markEnemyHit(c);
                    }
                }
                for (HuskHornhead c : huskHornheads) {
                    if (c.isAlive() && playerBox.overlaps(c.getBounds()) && !game.player.isEnemyHit(c)) {
                        c.takeDamage(shadowDamage, game.player.position.x);
                        game.player.markEnemyHit(c);
                    }
                }
                for (CrystalGuardian c : crystalGuardians) {
                    if (c.isAlive() && playerBox.overlaps(c.getBounds()) && !game.player.isEnemyHit(c)) {
                        c.takeDamage(shadowDamage, game.player.position.x);
                        game.player.markEnemyHit(c);
                    }
                }
            }

            if (game.player.isAttacking()) {
                Rectangle ab = game.player.getAttackBox();

                for (BreakableWall wall : breakableWalls) {
                    if (!wall.isBroken && ab.overlaps(wall.bounds)) {

                        boolean canBreak = true;
                        float wallCenterX = wall.bounds.x + (wall.bounds.width / 2f);

                        if ("right".equals(wall.breakSide) && game.player.position.x < wallCenterX) {
                            canBreak = false;
                        } else if ("left".equals(wall.breakSide) && game.player.position.x > wallCenterX) {
                            canBreak = false;
                        }

                        if (canBreak && game.player.tryConsumeHit()) {
                            Rectangle originalBounds = new Rectangle(wall.bounds);

                            wall.takeDamage();
                            rockParticles.setPosition(originalBounds.x + originalBounds.width / 2f, originalBounds.y + originalBounds.height / 2f);
                            rockParticles.start();

                            if (GameAssetManager.wallBreakSound != null) {
                                GameAssetManager.wallBreakSound.play(1.0f);
                            }

                            if (wall.isBroken) {
                                eraseWallTiles(originalBounds);
                                if (wall.revealsSecret && secretCoverLayer != null) {
                                    secretCoverLayer.setVisible(false);
                                }
                            }
                        }
                        break;
                    }
                }
                for (Mosscreep c : mosscreeps) {
                    if (c.isAlive() && ab.overlaps(c.getBounds())) {
                        if (game.player.tryConsumeHit()) {
                            c.takeDamage(nailDamage, game.player.position.x);
                            game.player.addSoul(Player.SOUL_PER_HIT);
                        }
                        break;
                    }
                }
                for (Mosquito c : mosquitoes) {
                    if (c.isAlive() && ab.overlaps(c.getBounds())) {
                        if (game.player.tryConsumeHit()) {
                            c.takeDamage(nailDamage, game.player.position.x);
                            game.player.addSoul(Player.SOUL_PER_HIT);
                        }
                        break;
                    }
                }
                for (HuskHornhead c : huskHornheads) {
                    if (c.isAlive() && ab.overlaps(c.getBounds())) {
                        if (game.player.tryConsumeHit()) {
                            c.takeDamage(nailDamage, game.player.position.x);
                            game.player.addSoul(Player.SOUL_PER_HIT);
                        }
                        break;
                    }
                }
                for (CrystalGuardian c : crystalGuardians) {
                    if (c.isAlive() && ab.overlaps(c.getBounds())) {
                        if (game.player.tryConsumeHit()) {
                            c.takeDamage(nailDamage, game.player.position.x);
                            game.player.addSoul(Player.SOUL_PER_HIT);
                        }
                        break;
                    }
                }
                if (zote != null && ab.overlaps(zote.getBounds())) {
                    if (game.player.tryConsumeHit()) {
                        zote.takeDamage(nailDamage, game.player.position.x);
                        isDialogueActive = false;
                    }
                }
            }

            if (game.player.isDownSlashing && !game.player.pogoHit) {
                Rectangle downBox = game.player.getDownSlashBox();

                for (BreakableWall wall : breakableWalls) {
                    if (!wall.isBroken && downBox.overlaps(wall.bounds)) {

                        boolean canBreak = true;
                        float wallCenterX = wall.bounds.x + (wall.bounds.width / 2f);

                        if ("right".equals(wall.breakSide) && game.player.position.x < wallCenterX) {
                            canBreak = false;
                        } else if ("left".equals(wall.breakSide) && game.player.position.x > wallCenterX) {
                            canBreak = false;
                        }

                        if (canBreak) {
                            Rectangle originalBounds = new Rectangle(wall.bounds);

                            wall.takeDamage();
                            game.player.pogoBounce();
                            rockParticles.setPosition(originalBounds.x + originalBounds.width / 2f, originalBounds.y + originalBounds.height / 2f);
                            rockParticles.start();

                            if (GameAssetManager.wallBreakSound != null) {
                                GameAssetManager.wallBreakSound.play(1.0f);
                            }

                            if (wall.isBroken) {
                                eraseWallTiles(originalBounds);
                                if (wall.revealsSecret && secretCoverLayer != null) {
                                    secretCoverLayer.setVisible(false);
                                }
                            }
                        }
                        break;
                    }
                }
                for (Mosscreep c : mosscreeps) {
                    if (c.isAlive() && downBox.overlaps(c.getBounds())) {
                        c.takeDamagePogo(nailDamage);
                        game.player.pogoBounce();
                        game.player.addSoul(Player.SOUL_PER_HIT);
                        break;
                    }
                }
                for (SolidBlock b : solidBlocks) {
                    if (b.isDeadly && downBox.overlaps(b.bounds)) {
                        game.player.pogoBounce();
                        break;
                    }
                }
                for (Mosquito c : mosquitoes) {
                    if (c.isAlive() && downBox.overlaps(c.getBounds())) {
                        c.takeDamagePogo(nailDamage);
                        game.player.pogoBounce();
                        game.player.addSoul(Player.SOUL_PER_HIT);
                        break;
                    }
                }
                for (HuskHornhead c : huskHornheads) {
                    if (c.isAlive() && downBox.overlaps(c.getBounds())) {
                        c.takeDamagePogo(nailDamage);
                        game.player.pogoBounce();
                        game.player.addSoul(Player.SOUL_PER_HIT);
                        break;
                    }
                }
                for (CrystalGuardian c : crystalGuardians) {
                    if (c.isAlive() && downBox.overlaps(c.getBounds())) {
                        c.takeDamagePogo(nailDamage);
                        game.player.pogoBounce();
                        game.player.addSoul(Player.SOUL_PER_HIT);
                        break;
                    }
                }
                if (zote != null && downBox.overlaps(zote.getBounds())) {
                    if (game.player.tryConsumeHit()) {
                        zote.takeDamage(nailDamage, game.player.position.x);
                        isDialogueActive = false; // Hitting him breaks dialogue!
                    }
                }
            }

            if (game.player.isUpSlashing && !game.player.upHitConsumed) {
                Rectangle upBox = game.player.getUpSlashBox();

                for (BreakableWall wall : breakableWalls) {
                    if (!wall.isBroken && upBox.overlaps(wall.bounds)) {

                        boolean canBreak = true;
                        float wallCenterX = wall.bounds.x + (wall.bounds.width / 2f);

                        if ("right".equals(wall.breakSide) && game.player.position.x < wallCenterX) {
                            canBreak = false;
                        } else if ("left".equals(wall.breakSide) && game.player.position.x > wallCenterX) {
                            canBreak = false;
                        }

                        if (canBreak && game.player.tryConsumeHit()) {
                            Rectangle originalBounds = new Rectangle(wall.bounds);

                            wall.takeDamage();
                            rockParticles.setPosition(originalBounds.x + originalBounds.width / 2f, originalBounds.y + originalBounds.height / 2f);
                            rockParticles.start();

                            if (GameAssetManager.wallBreakSound != null) {
                                GameAssetManager.wallBreakSound.play(1.0f);
                            }

                            if (wall.isBroken) {
                                eraseWallTiles(originalBounds);
                                if (wall.revealsSecret && secretCoverLayer != null) {
                                    secretCoverLayer.setVisible(false);
                                }
                            }
                        }
                        break;
                    }
                }
                for (Mosscreep c : mosscreeps) {
                    if (c.isAlive() && upBox.overlaps(c.getBounds())) {
                        c.takeDamage(nailDamage, game.player.position.x);
                        game.player.addSoul(Player.SOUL_PER_HIT);
                        game.player.upHitConsumed = true;
                        break;
                    }
                }
                for (Mosquito c : mosquitoes) {
                    if (c.isAlive() && upBox.overlaps(c.getBounds())) {
                        c.takeDamage(nailDamage, game.player.position.x);
                        game.player.addSoul(Player.SOUL_PER_HIT);
                        game.player.upHitConsumed = true;
                        break;
                    }
                }
                for (HuskHornhead c : huskHornheads) {
                    if (c.isAlive() && upBox.overlaps(c.getBounds())) {
                        c.takeDamage(nailDamage, game.player.position.x);
                        game.player.addSoul(Player.SOUL_PER_HIT);
                        game.player.upHitConsumed = true;
                        break;
                    }
                }
                for (CrystalGuardian c : crystalGuardians) {
                    if (c.isAlive() && upBox.overlaps(c.getBounds())) {
                        c.takeDamage(nailDamage, game.player.position.x);
                        game.player.addSoul(Player.SOUL_PER_HIT);
                        game.player.upHitConsumed = true;
                        break;
                    }
                }
                if (zote != null && upBox.overlaps(zote.getBounds())) {
                    if (game.player.tryConsumeHit()) {
                        zote.takeDamage(nailDamage, game.player.position.x);
                        isDialogueActive = false; // Hitting him breaks dialogue!
                    }
                }
            }

            if (game.player.isVengefulSpiritActive) {
                Rectangle projBox = game.player.getVengefulSpiritBox();
                for (Mosscreep c : mosscreeps) {
                    if (c.isAlive() && projBox.overlaps(c.getBounds())) {
                        if (!game.player.isEnemyHit(c)) {
                            c.takeDamage(spellDamage, game.player.spiritPosition.x);
                            game.player.addSoul(Player.SOUL_PER_HIT);
                            game.player.markEnemyHit(c);
                        }
                    }
                }
                for (Mosquito c : mosquitoes) {
                    if (c.isAlive() && projBox.overlaps(c.getBounds())) {
                        if (!game.player.isEnemyHit(c)) {
                            c.takeDamage(spellDamage, game.player.spiritPosition.x);
                            game.player.addSoul(Player.SOUL_PER_HIT);
                            game.player.markEnemyHit(c);
                        }
                    }
                }
                for (HuskHornhead c : huskHornheads) {
                    if (c.isAlive() && projBox.overlaps(c.getBounds())) {
                        if (!game.player.isEnemyHit(c)) {
                            c.takeDamage(spellDamage, game.player.spiritPosition.x);
                            game.player.addSoul(Player.SOUL_PER_HIT);
                            game.player.markEnemyHit(c);
                        }
                    }
                }
                for (CrystalGuardian c : crystalGuardians) {
                    if (c.isAlive() && projBox.overlaps(c.getBounds())) {
                        if (!game.player.isEnemyHit(c)) {
                            c.takeDamage(spellDamage, game.player.spiritPosition.x);
                            game.player.addSoul(Player.SOUL_PER_HIT);
                            game.player.markEnemyHit(c);
                        }
                    }
                }
            }

            if (game.player.isHowlingWraithsActive) {
                Rectangle hwBox = game.player.getHowlingWraithsBox();
                for (Mosscreep c : mosscreeps) {
                    if (c.isAlive() && hwBox.overlaps(c.getBounds())) {
                        if (game.player.tryHowlingWraithsHit()) {
                            c.takeDamage(spellDamage, game.player.position.x);
                            game.player.addSoul(Player.SOUL_PER_HIT);
                            break;
                        }
                    }
                }
                for (Mosquito c : mosquitoes) {
                    if (c.isAlive() && hwBox.overlaps(c.getBounds())) {
                        if (game.player.tryHowlingWraithsHit()) {
                            c.takeDamage(spellDamage, game.player.position.x);
                            game.player.addSoul(Player.SOUL_PER_HIT);
                            break;
                        }
                    }
                }
                for (HuskHornhead c : huskHornheads) {
                    if (c.isAlive() && hwBox.overlaps(c.getBounds())) {
                        if (game.player.tryHowlingWraithsHit()) {
                            c.takeDamage(spellDamage, game.player.position.x);
                            game.player.addSoul(Player.SOUL_PER_HIT);
                            break;
                        }
                    }
                }
                for (CrystalGuardian c : crystalGuardians) {
                    if (c.isAlive() && hwBox.overlaps(c.getBounds())) {
                        if (game.player.tryHowlingWraithsHit()) {
                            c.takeDamage(spellDamage, game.player.position.x);
                            game.player.addSoul(Player.SOUL_PER_HIT);
                            break;
                        }
                    }
                }
            }

            boolean isShadowDashing = game.player.isDashing && game.player.hasSharpShadow;

            if (!isShadowDashing) {
                for (Mosscreep c : mosscreeps) {
                    if (c.isAlive() && c.getBounds().overlaps(game.player.getBounds())) {
                        if (!game.player.isDying) {
                            game.player.takeDamage(1f, c.getBounds().x);
                        }
                    }
                }
                for (Mosquito c : mosquitoes) {
                    if (c.isAlive() && c.getBounds().overlaps(game.player.getBounds())) {
                        if (!game.player.isDying) {
                            game.player.takeDamage(1f, c.getBounds().x);
                        }
                    }
                }
                for (HuskHornhead c : huskHornheads) {
                    if (c.isAlive() && c.getBounds().overlaps(game.player.getBounds())) {
                        if (!game.player.isDying) {
                            game.player.takeDamage(1f, c.getBounds().x);
                        }
                    }
                }
                for (CrystalGuardian c : crystalGuardians) {
                    if (c.isAlive() && c.getBounds().overlaps(game.player.getBounds())) {
                        if (!game.player.isDying) {
                            game.player.takeDamage(1f, c.getBounds().x);
                        }
                    }
                }
            }
            game.player.checkDeath();

            // --- NEW: Gather Player Shake ---
            float ps = game.player.consumeShakeMag();
            if (ps > 0f && (ps >= shakeMag || shakeTime <= 0f)) {
                float pt = game.player.consumeShakeTime();
                shakeTime = pt;
                shakeDuration = pt;
                shakeMag = ps;
            }
        }

        AnimationType currentAnimation = game.player.currentAnimation;
        Animation<TextureRegion> animation = GameAssetManager.animationMap.get(currentAnimation);
        TextureRegion keyFrame = animation.getKeyFrame(game.player.stateTime);
        float scale = 0.9f;
        float w = keyFrame.getRegionWidth();
        float h = keyFrame.getRegionHeight();
        float drawnW = w * scale;
        float drawnH = h * scale;
        game.player.spriteDrawnW = drawnW;
        game.player.spriteDrawnH = drawnH;

        camera.position.set(game.player.position, 0);
        if (bossFightActive && bossArena != null) clampCameraToArena();
        if (shakeTime > 0f) {
            shakeTime -= delta;

            // --- FIX: Divide by the dynamically stored duration ---
            float k = Math.max(0f, shakeTime) / Math.max(0.01f, shakeDuration);
            camera.position.x += MathUtils.random(-1f, 1f) * shakeMag * k;
            camera.position.y += MathUtils.random(-1f, 1f) * shakeMag * k;
        }
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        renderer.setView((OrthographicCamera) camera);

        renderer.render(backgroundLayers);

        batch.begin();
        if (voidHeartBounds != null && !voidHeartCollected && voidHeartTexture != null) {
            float floatY = (float) Math.sin(game.player.stateTime * 3f) * 5f;
            float drawSize = 64f;
            float drawX = voidHeartBounds.x + (voidHeartBounds.width / 2f) - (drawSize / 2f);
            batch.draw(voidHeartTexture, drawX, voidHeartBounds.y + floatY, drawSize, drawSize);
        }

        float drawOffsetX = Player.DRAW_OFFSET_X;

        if (game.player.isDashing && game.player.hasSharpShadow) batch.setColor(Color.DARK_GRAY);

        batch.draw(keyFrame, game.player.position.x + drawOffsetX, game.player.position.y, w / 2, 0, w, h,
            game.player.facingRight ? -scale : scale, scale, 0);

        batch.setColor(Color.WHITE);

        float playerCenterX = game.player.position.x + drawOffsetX + drawnW / 2f;
        float playerCenterY = game.player.position.y + drawnH / 2f;

        if (game.player.isAttacking()) {
            Animation<TextureRegion> fx;
            if (game.player.attackComboCounter % 2 == 0) {
                fx = GameAssetManager.animationMap.get(AnimationType.HOLLOW_KNIGHT_NORMAL_SLASH_EFFECT);
            } else {
                fx = GameAssetManager.animationMap.get(AnimationType.HOLLOW_KNIGHT_SLASH_ALT_EFFECT);
            }
            float elapsed = game.player.getAttackElapsed();
            float t = (elapsed / 0.20f) * fx.getAnimationDuration();
            TextureRegion frame = fx.getKeyFrame(t, false);

            float fxW = frame.getRegionWidth() * scale;
            float fxH = frame.getRegionHeight() * scale;
            float fxY = playerCenterY - fxH / 2f;
            float gap = 50;
            if (game.player.facingRight) {
                float fxX = playerCenterX + gap + fxW;
                batch.draw(frame, fxX + 25, fxY, -fxW, fxH);
            } else {
                float leftGap = drawnW * 0.05f;
                float fxX = playerCenterX - leftGap - fxW;
                batch.draw(frame, fxX -25, fxY, fxW, fxH);
            }
        }

        if (game.player.isDashing) {
            Animation<TextureRegion> dashFx = GameAssetManager.animationMap.get(AnimationType.HOLLOW_KNIGHT_DASH_EFFECT);
            float elapsed = game.player.getDashElapsed();
            float t = (elapsed / Player.DASH_DURATION) * dashFx.getAnimationDuration();
            TextureRegion frame = dashFx.getKeyFrame(t, false);

            float fxW = frame.getRegionWidth() * scale;
            float fxH = frame.getRegionHeight() * scale;
            float fxY = playerCenterY - fxH / 2f;
            float gap = 20f;

            if (game.player.hasSharpShadow) batch.setColor(0.1f, 0.1f, 0.1f, 0.9f);

            if (game.player.facingRight) {
                float fxX = playerCenterX - gap - fxW;
                batch.draw(frame, fxX + 80, fxY, fxW, fxH);
            } else {
                float fxX = playerCenterX + gap + fxW;
                batch.draw(frame, fxX, fxY, -fxW, fxH);
            }
            batch.setColor(Color.WHITE);
        }

        if (game.player.isHowlingWraithsActive) {
            Animation<TextureRegion> hwEffect = GameAssetManager.animationMap.get(AnimationType.HOLLOW_KNIGHT_HOWLING_WRAITHS_EFFECT);
            float effectTime = game.player.getHowlingWraithsTimer();
            TextureRegion frame = hwEffect.getKeyFrame(effectTime, false);

            float effectX = game.player.getHowlingWraithsBox().x;
            float effectY = game.player.getHowlingWraithsBox().y;
            float effectW = frame.getRegionWidth() * scale;
            float effectH = frame.getRegionHeight() * scale;

            float boxW = game.player.getHowlingWraithsBox().width;
            float boxH = game.player.getHowlingWraithsBox().height;

            batch.draw(frame,
                effectX + (boxW - effectW) / 2f,
                effectY + (boxH - effectH) / 2f,
                effectW, effectH);

            batch.setColor(Color.WHITE);
        }

        if (game.player.isVengefulSpiritActive) {
            float blastTimer = game.player.vengefulSpiritBlastTimer;
            float projTimer = game.player.vengefulSpiritProjectileTimer;

            if (blastTimer < BLAST_DURATION) {
                Animation<TextureRegion> blastAnim = GameAssetManager.animationMap.get(AnimationType.HOLLOW_KNIGHT_VENGEFUL_SPIRIT_BLAST);
                TextureRegion frame = blastAnim.getKeyFrame(blastTimer, false);
                if (frame != null) {
                    float fw = frame.getRegionWidth() * scale;
                    float fh = frame.getRegionHeight() * scale;
                    float originX = fw / 2f;
                    float originY = fh / 2f;
                    batch.draw(frame,
                        game.player.spiritPosition.x - originX,
                        game.player.spiritPosition.y - originY,
                        originX, originY,
                        fw, fh,
                        game.player.spiritVelocity.x > 0 ? 1 : -1,
                        1, 0);
                }
            }

            Animation<TextureRegion> projAnim = GameAssetManager.animationMap.get(AnimationType.HOLLOW_KNIGHT_VENGEFUL_SPIRIT_PROJECTILE);
            TextureRegion frame = projAnim.getKeyFrame(projTimer, true);
            if (frame != null) {
                float fw = frame.getRegionWidth() * scale;
                float fh = frame.getRegionHeight() * scale;
                float originX = fw / 2f;
                float originY = fh / 2f;
                batch.draw(frame, game.player.spiritPosition.x - originX,
                    game.player.spiritPosition.y - originY,
                    originX, originY,
                    fw, fh,
                    game.player.spiritVelocity.x > 0 ? 1 : -1,
                    1, 0);
            }
            batch.setColor(Color.WHITE);
        }

        if (game.player.isDownSlashing) {
            Animation<TextureRegion> downEffect = GameAssetManager.animationMap.get(AnimationType.HOLLOW_KNIGHT_DOWN_SLASH_EFFECT);
            float t = game.player.downSlashTimer;
            TextureRegion frame = downEffect.getKeyFrame(t, false);
            if (frame != null && t < game.player.DOWN_SLASH_DURATION) {
                float fw = frame.getRegionWidth() * scale;
                float fh = frame.getRegionHeight() * scale;
                float cx = playerCenterX;
                float cy = playerCenterY;
                float scaleX = game.player.facingRight ? 1f : -1f;
                float originX = fw / 2f;
                float originY = 0f;
                batch.draw(frame,
                    cx - originX + 40,
                    cy - fh - 20,
                    originX, originY,
                    fw, fh,
                    scaleX, 1f, 0f);
            }
        }
        if (game.player.isUpSlashing) {
            Animation<TextureRegion> upEffect = GameAssetManager.animationMap.get(AnimationType.HOLLOW_KNIGHT_UP_SLASH_EFFECT);
            float t = game.player.upSlashTimer;
            TextureRegion frame = upEffect.getKeyFrame(t, false);
            if (frame != null && t < game.player.UP_SLASH_DURATION) {
                float fw = frame.getRegionWidth() * scale;
                float fh = frame.getRegionHeight() * scale;
                float cx = playerCenterX;
                float cy = playerCenterY - 20f;
                float scaleX = game.player.facingRight ? 1f : -1f;
                float originX = fw / 2f;
                float originY = fh;
                batch.draw(frame,
                    cx - originX + 30,
                    cy + 30,
                    originX, originY,
                    fw, fh,
                    scaleX, 1f, 0f);
            }
        }

        for (Mosscreep c : mosscreeps) c.render(batch);
        for (Mosquito m : mosquitoes) m.render(batch);
        for (HuskHornhead t : huskHornheads) t.render(batch);
        for (CrystalGuardian cg : crystalGuardians) cg.render(batch);
        if (falseKnight != null) falseKnight.render(batch);

        if (zote != null) zote.render(batch);

        batch.end();

        renderer.render(foregroundLayers);

        batch.begin();
        rockParticles.draw(batch);
        batch.end();

        batch.begin();
        if (ambientParticles != null) {
            ambientParticles.draw(batch);
        }
        batch.end();

        batch.begin();
        com.badlogic.gdx.graphics.g2d.BitmapFont font = skin.getFont("default-font");
        boolean nearZote = zote != null && game.player.getBounds().overlaps(zote.getInteractBounds()) && zote.state == Zote.State.IDLE;

        if (isDialogueActive) {
            dialogueTypewriterTimer += delta;
            int charsToShow = (int) (dialogueTypewriterTimer * 30);
            String visibleText = currentDialogueText.substring(0, Math.min(currentDialogueText.length(), charsToShow));

            batch.setColor(Color.WHITE);

            font.getData().setScale(3.5f);
            font.setColor(Color.LIGHT_GRAY);
            font.draw(batch, "Zote The Mighty:", camera.position.x - 560, camera.position.y - 140);

            // Scale up the Body font
            font.getData().setScale(3f);
            font.setColor(Color.WHITE);
            font.draw(batch, visibleText, camera.position.x - 560, camera.position.y - 200, 1120, com.badlogic.gdx.utils.Align.left, true);

            // Scale up the Hint font
            font.getData().setScale(2f);
            font.setColor(Color.GOLD);
            font.draw(batch, "[Press ENTER]", camera.position.x + 380, camera.position.y - 350);

            // Reset
            font.setColor(Color.WHITE);
            font.getData().setScale(1.0f);

        } else if (nearZote) {
            font.getData().setScale(1.4f);
            font.setColor(Color.GOLD);
            font.draw(batch, "Press UP to Listen", zote.getBounds().x - 40, zote.getBounds().y + zote.getBounds().height + 80);
            font.setColor(Color.WHITE);
            font.getData().setScale(1.0f);
        }
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(Color.YELLOW);
        Rectangle pb = game.player.getBounds();
        shapeRenderer.rect(pb.x, pb.y, pb.width, pb.height);

        shapeRenderer.setColor(Color.RED);
        if (game.player.isAttacking()) {
            Rectangle ab = game.player.getAttackBox();
            shapeRenderer.rect(ab.x, ab.y, ab.width, ab.height);
        }
        if (game.player.isDownSlashing) {
            Rectangle downBox = game.player.getDownSlashBox();
            shapeRenderer.rect(downBox.x, downBox.y, downBox.width, downBox.height);
        }
        if (game.player.isUpSlashing) {
            Rectangle upBox = game.player.getUpSlashBox();
            shapeRenderer.rect(upBox.x, upBox.y, upBox.width, upBox.height);
        }
        if (game.player.isHowlingWraithsActive) {
            Rectangle hwBox = game.player.getHowlingWraithsBox();
            shapeRenderer.rect(hwBox.x, hwBox.y, hwBox.width, hwBox.height);
        }
        if (game.player.isVengefulSpiritActive) {
            Rectangle pBox = game.player.getVengefulSpiritBox();
            shapeRenderer.rect(pBox.x, pBox.y, pBox.width, pBox.height);
        }

        if (falseKnight != null && !falseKnight.isDefeated()) {

            shapeRenderer.setColor(Color.MAGENTA);
            Rectangle fkBox = falseKnight.getActiveHitbox();
            shapeRenderer.rect(fkBox.x, fkBox.y, fkBox.width, fkBox.height);
            FalseKnight.State fkState = falseKnight.getState();
            if (fkState == FalseKnight.State.SLAM_ANTIC || fkState == FalseKnight.State.SLAM_HIT || fkState == FalseKnight.State.SLAM_RECOVER) {
                shapeRenderer.setColor(Color.ORANGE);
                Rectangle maceBox = falseKnight.getMaceHitbox();
                shapeRenderer.rect(maceBox.x, maceBox.y, maceBox.width, maceBox.height);
            }
        }

        shapeRenderer.end();

        hud.render(game.player, delta);
        super.render(delta);
    }

    @Override
    public void hide() {
        super.hide();
        if (hud != null) hud.dispose();
        if (rockParticles != null) rockParticles.dispose();
        if (areaMusic != null) {
            areaMusic.dispose();
        }
        if (ambientParticles != null) ambientParticles.dispose();
    }
    private void closeGates() {
        if (bossArena == null) return;
        float t = 40f;
        leftGate  = new SolidBlock(bossArena.x - t, bossArena.y, t, bossArena.height, false);
        rightGate = new SolidBlock(bossArena.x + bossArena.width, bossArena.y, t, bossArena.height, false);
        solidBlocks.add(leftGate);
        solidBlocks.add(rightGate);
    }

    private void openGates() {
        if (leftGate  != null) { solidBlocks.removeValue(leftGate, true);  leftGate = null; }
        if (rightGate != null) { solidBlocks.removeValue(rightGate, true); rightGate = null; }
    }
    private void clampCameraToArena() {
        float halfW = camera.viewportWidth / 2f, halfH = camera.viewportHeight / 2f;
        float minX = bossArena.x + halfW, maxX = bossArena.x + bossArena.width  - halfW;
        float minY = bossArena.y + halfH, maxY = bossArena.y + bossArena.height - halfH;
        camera.position.x = (minX <= maxX) ? MathUtils.clamp(camera.position.x, minX, maxX)
            : bossArena.x + bossArena.width / 2f;
        camera.position.y = (minY <= maxY) ? MathUtils.clamp(camera.position.y, minY, maxY)
            : bossArena.y + bossArena.height / 2f;
    }
}
