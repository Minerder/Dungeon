package contrib.entities.traps;

import contrib.components.InteractionComponent;

import core.Entity;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.utils.Point;
import core.utils.SoundPlayer;
import core.utils.components.draw.Animation;

import dslToGame.AnimationBuilder;

public class Lever extends Entity {
    private boolean pressed;
    private final Animation activated;
    private final DrawComponent dc;
    private final Trap trap;

    public Lever(Trap trap) {
        super();
        Animation idle = AnimationBuilder.buildAnimation("dungeon/traps/lever.png");
        activated = AnimationBuilder.buildAnimation("dungeon/traps/lever_activated.png");
        dc = new DrawComponent(this, idle);
        setupInteractionComponent();
        setupPositionComponent();
        this.trap = trap;
        pressed = false;
    }

    /**
     * Returns whether the lever is pressed or not
     *
     * @return True if the lever is pressed, otherwise false
     */
    public boolean getPressed() {
        return pressed;
    }

    private void setActivated() {
        if (dc != null) dc.setCurrentAnimation(activated);
        SoundPlayer.play("sounds/traps/lever.mp3");
    }

    private void setupPositionComponent() {
        PositionComponent pcc = new PositionComponent(this);
        pcc.setPosition(new Point(pcc.getPosition().x + 0.5f, pcc.getPosition().y + 0.2f));
    }

    private void setupInteractionComponent() {
        new InteractionComponent(
                this,
                1.2f,
                false,
                a -> {
                    pressed = true;
                    trap.startIdleAnimation();
                    setActivated();
                });
    }
}
