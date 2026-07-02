package com.amirali.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;

import java.util.HashMap;

public class AreaMusicManager {

    public enum Area {
        GREEN_PATH("audio/Audio_Files/Audio_Files/S5 Green Path Main.wav"),
        FORGOTTEN_CROSSROADS("S19 Crossroads Main.wav"),
        CRYSTAL_PEAK("audio/Audio_Files/Audio_Files/S26 Crystal MAIN.wav");

        public final String file;

        Area(String file) {
            this.file = file;
        }
    }

    private static final float GREEN_PATH_MAX_X = 5900f;
    private static final float CRYSTAL_PEAK_MIN_X = 20800f;

    private static final float FADE_DURATION = 1.5f;

    private final HashMap<Area, Music> tracks = new HashMap<>();
    private final Preferences prefs = Gdx.app.getPreferences("HollowKnightSettings");

    private Area currentArea;
    private Music currentTrack;
    private Music fadingOutTrack;
    private float fadeTimer = FADE_DURATION;

    public static Area areaAt(float x) {
        if (x < GREEN_PATH_MAX_X) return Area.GREEN_PATH;
        if (x >= CRYSTAL_PEAK_MIN_X) return Area.CRYSTAL_PEAK;
        return Area.FORGOTTEN_CROSSROADS;
    }

    public void update(float playerX, float delta) {
        Area area = areaAt(playerX);
        if (area != currentArea) {
            switchTo(area);
        }

        float target = targetVolume();
        float progress = Math.min(fadeTimer / FADE_DURATION, 1f);
        fadeTimer += delta;

        if (currentTrack != null) {
            currentTrack.setVolume(target * progress);
        }
        if (fadingOutTrack != null) {
            if (progress >= 1f) {
                fadingOutTrack.stop();
                fadingOutTrack = null;
            } else {
                fadingOutTrack.setVolume(target * (1f - progress));
            }
        }
    }

    private void switchTo(Area area) {
        if (fadingOutTrack != null) {
            fadingOutTrack.stop();
        }
        fadingOutTrack = currentTrack;

        currentArea = area;
        currentTrack = getTrack(area);
        fadeTimer = (fadingOutTrack == null) ? FADE_DURATION : 0f;

        if (currentTrack != null) {
            currentTrack.setVolume(fadingOutTrack == null ? targetVolume() : 0f);
            currentTrack.play();
        }
    }

    private Music getTrack(Area area) {
        Music track = tracks.get(area);
        if (track == null && !tracks.containsKey(area)) {
            try {
                track = Gdx.audio.newMusic(Gdx.files.internal(area.file));
                track.setLooping(true);
            } catch (Exception e) {
                Gdx.app.error("Audio", "Could not load " + area.file);
            }
            tracks.put(area, track);
        }
        return track;
    }

    private float targetVolume() {
        if (!prefs.getBoolean("musicOn", true)) return 0f;
        return prefs.getFloat("musicVol", 0.5f);
    }

    public void stop() {
        for (Music track : tracks.values()) {
            if (track != null) track.stop();
        }
        currentTrack = null;
        fadingOutTrack = null;
    }

    public void dispose() {
        for (Music track : tracks.values()) {
            if (track != null) track.dispose();
        }
        tracks.clear();
        currentTrack = null;
        fadingOutTrack = null;
    }
}
