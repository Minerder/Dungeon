package contrib.utils.components.skill;

import contrib.components.CollideComponent;
import contrib.components.HealthComponent;
import contrib.components.ProjectileComponent;
import contrib.components.UpdateComponent;
import contrib.utils.components.collision.ICollide;
import contrib.utils.components.health.Damage;

import core.Entity;
import core.Game;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.components.VelocityComponent;
import core.utils.Point;
import core.utils.SoundPlayer;
import core.utils.components.MissingComponentException;
import core.utils.components.draw.Animation;

import dslToGame.AnimationBuilder;

public abstract class DamageProjectileSkill implements ISkillFunction {

    private final String pathToTexturesOfProjectile;
    private final String pathToSoundsOfProjectile;
    private final float projectileSpeed;
    private final float projectileRange;
    private final Damage projectileDamage;
    private final Point projectileHitboxSize;
    private final int bounceAmount;
    private final ITargetSelection selectionFunction;
    private IUpdateFunction updateFunction;

    public DamageProjectileSkill(
            String pathToTexturesOfProjectile,
            String pathToSoundsOfProjectile,
            float projectileSpeed,
            Damage projectileDamage,
            Point projectileHitboxSize,
            ITargetSelection selectionFunction,
            float projectileRange,
            int bounceAmount) {
        this.pathToTexturesOfProjectile = pathToTexturesOfProjectile;
        this.pathToSoundsOfProjectile = pathToSoundsOfProjectile;
        this.projectileDamage = projectileDamage;
        this.projectileSpeed = projectileSpeed;
        this.projectileRange = projectileRange;
        this.projectileHitboxSize = projectileHitboxSize;
        this.selectionFunction = selectionFunction;
        this.bounceAmount = bounceAmount;
    }

    public DamageProjectileSkill(
            String pathToTexturesOfProjectile,
            String pathToSoundsOfProjectile,
            float projectileSpeed,
            Damage projectileDamage,
            Point projectileHitboxSize,
            ITargetSelection selectionFunction,
            float projectileRange) {
        this.pathToTexturesOfProjectile = pathToTexturesOfProjectile;
        this.pathToSoundsOfProjectile = pathToSoundsOfProjectile;
        this.projectileDamage = projectileDamage;
        this.projectileSpeed = projectileSpeed;
        this.projectileRange = projectileRange;
        this.projectileHitboxSize = projectileHitboxSize;
        this.selectionFunction = selectionFunction;
        this.bounceAmount = 0;
    }

    @Override
    public void execute(Entity entity) {
        Entity projectile = new Entity();
        PositionComponent epc =
                (PositionComponent)
                        entity.getComponent(PositionComponent.class)
                                .orElseThrow(
                                        () -> new MissingComponentException("PositionComponent"));

        new PositionComponent(projectile, epc.getPosition());

        Animation animation = AnimationBuilder.buildAnimation(pathToTexturesOfProjectile);
        new DrawComponent(projectile, animation);

        Point aimedOn = selectionFunction.selectTargetPoint();
        Point targetPoint =
                SkillTools.calculateLastPositionInRange(
                        epc.getPosition(), aimedOn, projectileRange);
        Point velocity =
                SkillTools.calculateVelocity(epc.getPosition(), targetPoint, projectileSpeed);

        new VelocityComponent(projectile, velocity.x, velocity.y, animation, animation);
        ProjectileComponent pc =
                new ProjectileComponent(projectile, epc.getPosition(), targetPoint, bounceAmount);
        new UpdateComponent(projectile, updateFunction);
        ICollide collide =
                (a, b, from) -> {
                    if (b != entity && projectileDamage.damageAmount() > 0) {
                        b.getComponent(HealthComponent.class)
                                .ifPresent(
                                        hc -> {
                                            ((HealthComponent) hc)
                                                    .receiveHit(
                                                            new Damage(
                                                                    projectileDamage.damageAmount(),
                                                                    projectileDamage.damageType(),
                                                                    entity));
                                            SkillTools.receiveKnockback(pc.getStartPosition(), b);
                                            Game.removeEntity(projectile);
                                        });
                    }
                };
        SoundPlayer.play(pathToSoundsOfProjectile);

        new CollideComponent(
                projectile, new Point(0.25f, 0.25f), projectileHitboxSize, collide, null);
    }

    public void setUpdateFunction(IUpdateFunction updateFunction) {
        this.updateFunction = updateFunction;
    }
}
