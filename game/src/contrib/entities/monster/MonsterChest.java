package contrib.entities.monster;

import contrib.components.AIComponent;
import contrib.components.CollideComponent;
import contrib.components.HealthComponent;
import contrib.components.InteractionComponent;
import contrib.entities.EntityFactory;
import contrib.utils.components.ai.fight.CollideAI;
import contrib.utils.components.ai.idle.StaticRadiusWalk;
import contrib.utils.components.ai.transition.RangeTransition;
import contrib.utils.components.health.IOnDeathFunction;
import contrib.utils.components.interaction.IInteraction;
import core.Entity;
import core.components.DrawComponent;
import core.components.PlayerComponent;
import core.components.PositionComponent;
import core.utils.Point;
import dslToGame.AnimationBuilder;

import java.util.logging.Logger;

public class MonsterChest extends Monster implements IOnDeathFunction, IInteraction {

    private static final Logger LOGGER = Logger.getLogger(Monster.class.getName());

    /** Creates a new MonsterChest. */
    public MonsterChest() {
        super(
                5,
                0.15f,
                0.15f,
                15,
                "objects/treasurechest/monsterChest",
                "objects/treasurechest/monsterChest",
                "objects/treasurechest/chest_full_open_anim_f0.png",
                "objects/treasurechest/chest_full_open_anim_f0.png");
        setupPositionComponent();
        setupVelocityComponent();
        setupHealthComponent();
        setupDrawComponent();
        setupCollideComponent();
        setupXPComponent();
        setupInteractionComponent();
    }

    @Override
    protected void setupAIComponent() {
        new AIComponent(
                this, new CollideAI(2f), new StaticRadiusWalk(2f, 1), new RangeTransition(3f));
    }

    @Override
    protected void setupHealthComponent() {
        super.setupHealthComponent();
        HealthComponent hc =
                (HealthComponent) this.getComponent(HealthComponent.class).orElseThrow();
        hc.setOnDeath(this);
    }

    @Override
    protected void setupCollideComponent() {
        new CollideComponent(
                this,
                CollideComponent.DEFAULT_OFFSET,
                new Point(0.75f, 0.75f),
                ((a, b, from) -> {
                    if (b.getComponent(PlayerComponent.class).isEmpty()) return;
                    if (b.getComponent(HealthComponent.class).isEmpty()) return;
                    HealthComponent hc =
                            (HealthComponent) b.getComponent(HealthComponent.class).get();
                    hc.setCurrentHealthpoints(hc.getCurrentHealthpoints() - 1);
                }),
                null);
    }

    private void setupInteractionComponent() {
        final float defaultInteractionRadius = 1f;
        new InteractionComponent(this, defaultInteractionRadius, false, this);
    }

    /**
     * Function that is performed when an entity dies. Creates a chest at the position of the
     * entity.
     *
     * @param entity Entity that has died
     */
    @Override
    public void onDeath(Entity entity) {
        LOGGER.info("MonsterChest died, creating chest at MonsterChest position");
        Entity chest = EntityFactory.getChest();
        PositionComponent chestPc =
                (PositionComponent) chest.getComponent(PositionComponent.class).orElseThrow();
        PositionComponent pc =
                (PositionComponent) this.getComponent(PositionComponent.class).orElseThrow();
        chestPc.setPosition(pc.getPosition());
    }

    /**
     * Called when the entity is interacted with. Gives the entity an AIComponent and changes the
     * drawComponent.
     *
     * @param entity Entity that is interacted with
     */
    @Override
    public void onInteraction(Entity entity) {
        LOGGER.info("MonsterChest interacted with, adding AIComponent and changing drawComponent");
        setupAIComponent();
        this.removeComponent(DrawComponent.class);
        new DrawComponent(
                this, AnimationBuilder.buildAnimation("objects/treasurechest/monsterChest"));
    }
}
