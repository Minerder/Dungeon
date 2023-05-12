package starter;

import configuration.KeyboardConfig;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // start the game
        Game.loadConfig("dungeon_config.json", KeyboardConfig.class);
        Game.run(); // hier ist loop
    }
}
