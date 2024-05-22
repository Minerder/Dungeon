package core.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.GdxRuntimeException;

import contrib.utils.components.skill.SkillTools;

import core.Entity;
import core.components.PositionComponent;

import java.util.logging.Logger;

public class SoundPlayer {
    private static final Logger LOGGER = Logger.getLogger(SoundPlayer.class.getName());
    private static final float VOLUME = 0.2f;

    /**
     * Plays the sound at the given path.
     *
     * @param path The path to the sound file
     */
    public static void play(String path) {
        try {
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
            sound.play(VOLUME);
            LOGGER.info("Playing sound: " + path);
        } catch (GdxRuntimeException e) {
            LOGGER.warning("Sound file \"" + path + "\" couldn't be found!");
        }
    }

    /**
     * Plays the sound at the given path, panned according to the direction of the given entity.
     *
     * @param path The path to the sound file
     * @param entity The entity in whose direction the sound should be panned
     */
    public static void playPanned(String path, Entity entity) {
        PositionComponent entityPc =
                (PositionComponent) entity.getComponent(PositionComponent.class).orElseThrow();
        float heroX = SkillTools.getHeroPosition().x;
        float entityX = entityPc.getPosition().x;
        float maxDif = 10;
        float pan = Math.max(-1, Math.min((entityX - heroX) / maxDif, 1));
        try {
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
            sound.play(VOLUME, 1, pan);
            LOGGER.info("Playing sound: " + path);
        } catch (GdxRuntimeException e) {
            LOGGER.warning("Sound file \"" + path + "\" couldn't be found!");
        }
    }

    /**
     * Loops the sound at the given path.
     *
     * @param path The path to the sound file
     */
    public static Sound loop(String path) {
        Sound sound = null;
        try {
            sound = Gdx.audio.newSound(Gdx.files.internal(path));
            sound.loop(VOLUME);
            LOGGER.info("Playing sound: " + path);
        } catch (GdxRuntimeException e) {
            LOGGER.warning("Sound file \"" + path + "\" couldn't be found!");
        }
        return sound;
    }
}
