package contrib.entities.traps;

import contrib.components.CollideComponent;
import contrib.components.HealthComponent;

import core.components.PlayerComponent;
import core.utils.Point;
import core.utils.SoundPlayer;

public class SpikeTrap extends Trap {
    private boolean activated;
    private final Lever lever;

    /** Creates a new SpikeTrap which damages the player when he steps on it */
    public SpikeTrap() {
        super("dungeon/traps/spikeTrap/spikes_1.png", "dungeon/traps/spikeTrap/spikes_4.png");
        setupCollideComponent();
        setupPositionComponent();
        setupPositionComponent();
        lever = new Lever(this);
        activated = false;
    }

    private void setupCollideComponent() {
        new CollideComponent(
                this,
                CollideComponent.DEFAULT_OFFSET,
                new Point(1, 1),
                (a, b, from) -> {
                    if (b.getComponent(PlayerComponent.class).isEmpty()) return;
                    if (b.getComponent(HealthComponent.class).isEmpty()) return;
                    if (lever.getPressed()) return;
                    HealthComponent hc =
                            (HealthComponent) b.getComponent(HealthComponent.class).get();
                    if (!activated) {
                        hc.setCurrentHealthpoints(hc.getCurrentHealthpoints() - 2);
                    } else {
                        hc.setCurrentHealthpoints(hc.getCurrentHealthpoints() - 1);
                    }
                    activated = true;
                    setActivationAnimation();
                    SoundPlayer.play("sounds/traps/spike.mp3");
                },
                null);
    }
}
