package contrib.utils.components.skill;

import contrib.components.CollideComponent;
import contrib.components.HealthComponent;
import contrib.components.UpdateComponent;
import contrib.utils.components.collision.ICollide;
import contrib.utils.components.health.Damage;

import core.Entity;
import core.Game;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.utils.Constants;
import core.utils.Point;
import core.utils.SoundPlayer;
import core.utils.components.MissingComponentException;
import core.utils.components.draw.Animation;

import dslToGame.AnimationBuilder;

public class DamageMeleeSkill implements ISkillFunction, IUpdateFunction {
    private final String pathToTexturesOfProjectile;
    private final String pathToSoundsOfProjectile;
    private final Damage projectileDamage;
    private final Point projectileHitboxSize;
    private final ITargetSelection selectionFunction;
    private final float holdingTimeInSeconds = 0.5f;
    private float currentHoldingTimeInFrames;
    private float hitCooldownInFrames;
    private Entity ownedBy;
    private Point offSet;
    private PositionComponent projectilepc;

    public DamageMeleeSkill(
            String pathToTexturesOfProjectile,
            String pathToSoundsOfProjectile,
            Damage projectileDamage,
            Point projectileHitboxSize,
            ITargetSelection selectionFunction) {
        this.pathToTexturesOfProjectile = pathToTexturesOfProjectile;
        this.pathToSoundsOfProjectile = pathToSoundsOfProjectile;
        this.projectileDamage = projectileDamage;
        this.projectileHitboxSize = projectileHitboxSize;
        this.selectionFunction = selectionFunction;
    }

    @Override
    public void execute(Entity entity) {
        this.currentHoldingTimeInFrames = (holdingTimeInSeconds * Constants.FRAME_RATE);
        this.hitCooldownInFrames = 0;
        this.ownedBy = entity;

        Entity projectile = new Entity();
        Point aimedOn = selectionFunction.selectTargetPoint();
        PositionComponent epc =
                (PositionComponent)
                        entity.getComponent(PositionComponent.class)
                                .orElseThrow(
                                        () -> new MissingComponentException("PositionComponent"));
        this.offSet = SkillTools.getMeleeSkillOffsetPositon(epc.getPosition(), aimedOn);
        this.projectilepc =
                new PositionComponent(
                        projectile,
                        new Point(epc.getPosition().x + offSet.x, epc.getPosition().y + offSet.y));

        Animation animation = AnimationBuilder.buildAnimation(pathToTexturesOfProjectile);
        new UpdateComponent(projectile, this);
        new DrawComponent(projectile, animation);
        ICollide collide =
                (a, b, from) -> {
                    if (b != entity && hitCooldownInFrames == 0) {
                        b.getComponent(HealthComponent.class)
                                .ifPresent(
                                        hc -> {
                                            ((HealthComponent) hc)
                                                    .receiveHit(
                                                            new Damage(
                                                                    projectileDamage.damageAmount(),
                                                                    projectileDamage.damageType(),
                                                                    entity));
                                            ((HealthComponent) hc).receiveHit(projectileDamage);
                                            SkillTools.receiveKnockback(epc.getPosition(), b);
                                            this.hitCooldownInFrames = 15;
                                        });
                    }
                };
        SoundPlayer.play(pathToSoundsOfProjectile);

        new CollideComponent(
                projectile, new Point(0.25f, 0.25f), projectileHitboxSize, collide, null);
    }

    /**
     * Updates the position of the Skill, so it moves with the hero. If the holdingTime reaches 0,
     * removes the entity.
     *
     * @param entity the skill to be updated
     */
    @Override
    public void update(Entity entity) {
        if (currentHoldingTimeInFrames == 0) {
            Game.removeEntity(entity);
            return;
        }
        hitCooldownInFrames = Math.max(0, --hitCooldownInFrames);
        currentHoldingTimeInFrames = Math.max(0, --currentHoldingTimeInFrames);
        PositionComponent ownedBypc =
                (PositionComponent) ownedBy.getComponent(PositionComponent.class).orElseThrow();
        if (projectilepc == null) return;
        this.projectilepc.setPosition(
                new Point(
                        ownedBypc.getPosition().x + offSet.x,
                        ownedBypc.getPosition().y + offSet.y));
    }
}
