package ecs.entities;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.AnimationComponent;
import ecs.components.PositionComponent;
import ecs.components.VelocityComponent;
import graphic.Animation;
import level.elements.tile.Tile;
import tools.Point;

public class Hero extends Entity {

    /**
     * Entity with Components
     *
     * @param startPosition position at start
     */
    public Hero(Point startPosition) {
        super();
        new PositionComponent(this, startPosition);
        new PlayableComponent(this);

        new HitboxComponent(this, (a, b, c) -> System.out.println("heroCollision"));

        setupAnimationComponent();
    }

    private void setupAnimationComponent() {
        Animation idleRight = AnimationBuilder.buildAnimation("knight/idleRight");
        Animation idleLeft = AnimationBuilder.buildAnimation("knight/idleLeft");
        Animation moveRight = AnimationBuilder.buildAnimation("knight/runRight");
        Animation moveLeft = AnimationBuilder.buildAnimation("knight/runLeft");

        new AnimationComponent(this, idleLeft, idleRight);

        new VelocityComponent(this, 0.3f, 0.3f, moveLeft, moveRight);
    }

    public static void heroCollision(HitboxComponent other, Tile.Direction from) {
        System.out.println("HERO COLLISION");
    }
}
