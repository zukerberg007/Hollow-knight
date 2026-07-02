package com.amirali.graphics;

import com.amirali.graphics.models.BreakableWall;
import com.amirali.graphics.models.Player;
import com.amirali.graphics.models.SolidBlock;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class TiledMapHelper {
    private TiledMap tiledMap;

    public TiledMap loadMap(String path) {
        tiledMap = new TmxMapLoader().load(path);
        return tiledMap;
    }

    public Array<SolidBlock> getSolidRectangles() {
        Array<SolidBlock> solidBlocks = new Array<>();

        MapLayer layer = tiledMap.getLayers().get("logical");
        if (layer == null) return solidBlocks;

        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                String name = object.getName();

                // --- FIX: Ignore the BossArena and Spawn points so we don't collide with them! ---
                if (name != null && (
                    name.equals("BreakableWall") ||
                        name.equals("VoidHeart") ||
                        name.equals("BossArena") ||
                        name.startsWith("Spawn")
                )) {
                    continue;
                }

                Rectangle rect = ((RectangleMapObject) object).getRectangle();

                boolean isDeadly = false;
                if (object.getProperties().containsKey("deadly")) {
                    isDeadly = object.getProperties().get("deadly", Boolean.class);
                }

                solidBlocks.add(new SolidBlock(rect.x, rect.y, rect.width, rect.height, isDeadly));
            }
        }

        return solidBlocks;
    }

    public Vector2 getPlayerSpawnPoint() {
        MapLayer layer = tiledMap.getLayers().get("logical");
        if (layer == null) return new Vector2(0, Player.GROUND_Y);
        for (MapObject object : layer.getObjects()) {
            if ("SpawnPlayer".equals(object.getName())
                || object.getProperties().containsKey("SpawnPlayer")) {
                float x = object.getProperties().get("x", Float.class);
                float y = object.getProperties().get("y", Float.class);
                return new Vector2(x, y);
            }
        }
        return new Vector2(0, Player.GROUND_Y);
    }

    public Array<Vector2> getMosscreepSpawnPoints() {
        Array<Vector2> spawns = new Array<>();
        if (tiledMap == null) return spawns;
        MapLayer layer = tiledMap.getLayers().get("logical");
        if (layer == null) return spawns;

        for (MapObject object : layer.getObjects()) {
            if ("SpawnMosscreep".equals(object.getName())
                || object.getProperties().containsKey("SpawnMosscreep")) {
                Float x = object.getProperties().get("x", Float.class);
                Float y = object.getProperties().get("y", Float.class);
                if (x != null && y != null) {
                    spawns.add(new Vector2(x, y));
                }
            }
        }
        return spawns;
    }

    public Array<Vector2> getMosquitoSpawnPoints() {
        Array<Vector2> spawns = new Array<>();
        if (tiledMap == null) return spawns;
        MapLayer layer = tiledMap.getLayers().get("logical");
        if (layer == null) return spawns;

        for (MapObject object : layer.getObjects()) {
            if ("SpawnMosquito".equals(object.getName())
                || object.getProperties().containsKey("SpawnMosquito")) {
                Float x = object.getProperties().get("x", Float.class);
                Float y = object.getProperties().get("y", Float.class);
                if (x != null && y != null) {
                    spawns.add(new Vector2(x, y));
                }
            }
        }
        return spawns;
    }

    public Array<Vector2> getHuskHornheadSpawnPoints() {
        Array<Vector2> spawns = new Array<>();
        if (tiledMap == null) return spawns;
        MapLayer layer = tiledMap.getLayers().get("logical");
        if (layer == null) return spawns;
        for (MapObject object : layer.getObjects()) {
            if ("SpawnHuskHornhead".equals(object.getName())
                || object.getProperties().containsKey("SpawnHuskHornhead")) {
                Float x = object.getProperties().get("x", Float.class);
                Float y = object.getProperties().get("y", Float.class);
                if (x != null && y != null) {
                    spawns.add(new Vector2(x, y));
                }
            }
        }
        return spawns;
    }

    public Array<Vector2> getCrystalGuardianSpawnPoints() {
        Array<Vector2> spawns = new Array<>();
        if (tiledMap == null) return spawns;
        MapLayer layer = tiledMap.getLayers().get("logical");
        if (layer == null) return spawns;
        for (MapObject object : layer.getObjects()) {
            if ("SpawnCrystalGuardian".equals(object.getName())
                || object.getProperties().containsKey("SpawnCrystalGuardian")) {
                Float x = object.getProperties().get("x", Float.class);
                Float y = object.getProperties().get("y", Float.class);
                if (x != null && y != null) {
                    spawns.add(new Vector2(x, y));
                }
            }
        }
        return spawns;
    }

    public Array<BreakableWall> getBreakableWalls() {
        Array<BreakableWall> walls = new Array<>();

        MapLayer layer = tiledMap.getLayers().get("logical");
        if (layer == null) return walls;

        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                if ("BreakableWall".equals(object.getName())) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();

                    String breakSide = "any";
                    if (object.getProperties().containsKey("breakSide")) {
                        breakSide = object.getProperties().get("breakSide", String.class);
                    }
                    boolean revealsSecret = false;
                    if (object.getProperties().containsKey("revealsSecret")) {
                        revealsSecret = object.getProperties().get("revealsSecret", Boolean.class);
                    }

                    walls.add(new BreakableWall(rect.x, rect.y, rect.width, rect.height, breakSide, revealsSecret));
                }
            }
        }
        return walls;
    }

    public Rectangle getVoidHeartBounds() {
        MapLayer layer = tiledMap.getLayers().get("logical");
        if (layer == null) return null;
        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                if ("VoidHeart".equals(object.getName())) {
                    return ((RectangleMapObject) object).getRectangle();
                }
            }
        }
        return null;
    }

    public Vector2 getZoteSpawnPoint() {
        MapLayer layer = tiledMap.getLayers().get("logical");
        if (layer == null) return null;
        for (MapObject object : layer.getObjects()) {
            if ("SpawnZote".equals(object.getName())) {
                Float x = object.getProperties().get("x", Float.class);
                Float y = object.getProperties().get("y", Float.class);
                if (x != null && y != null) {
                    return new Vector2(x, y);
                }
            }
        }
        return null;
    }

    public Vector2 getFalseKnightSpawnPoint() {
        MapLayer layer = tiledMap.getLayers().get("logical");
        if (layer == null) return null;
        for (MapObject object : layer.getObjects()) {
            if ("SpawnFalseKnight".equals(object.getName())
                || object.getProperties().containsKey("SpawnFalseKnight")) {
                Float x = object.getProperties().get("x", Float.class);
                Float y = object.getProperties().get("y", Float.class);
                if (x != null && y != null) return new Vector2(x, y);
            }
        }
        return null;
    }

    public Rectangle getBossArenaBounds() {
        MapLayer layer = tiledMap.getLayers().get("logical");
        if (layer == null) return null;
        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject && "BossArena".equals(object.getName())) {
                return ((RectangleMapObject) object).getRectangle();
            }
        }
        return null;
    }
}
