package contrib.utils.components.skill.skills;

import contrib.utils.components.health.Damage;
import contrib.utils.components.health.DamageType;
import contrib.utils.components.skill.DamageProjectileSkill;
import contrib.utils.components.skill.ITargetSelection;
import contrib.utils.components.skill.IUpdateFunction;
import contrib.utils.components.skill.SkillTools;

import core.Entity;
import core.Game;
import core.components.PositionComponent;
import core.components.VelocityComponent;
import core.utils.Point;

public class GravityStormSkill extends DamageProjectileSkill implements IUpdateFunction {
    public GravityStormSkill(ITargetSelection selectionFunction) {
        super(
                "skills/gravityStorm",
                "sounds/skills/gravity.mp3",
                0.05f,
                new Damage(0, DamageType.PHYSICAL, null),
                new Point(0.5f, 0.5f),
                selectionFunction,
                8f);
        super.setUpdateFunction(this);
    }

    @Override
    public void update(Entity entity) {
        Entity[] entities = SkillTools.getEntitiesInRange(entity, 3f);
        if (entities == null || entities.length == 0) return;

        PositionComponent startingEntitypc =
                (PositionComponent) entity.getComponent(PositionComponent.class).orElseThrow();

        for (Entity ent : entities) {
            if (ent.equals(Game.getHero().orElseThrow())) continue;
            PositionComponent targetEntitypc =
                    (PositionComponent) ent.getComponent(PositionComponent.class).orElseThrow();
            VelocityComponent targetEntityvc =
                    (VelocityComponent) ent.getComponent(VelocityComponent.class).orElseThrow();

            Point directionalVector =
                    Point.getDirectionalVector(
                            targetEntitypc.getPosition(), startingEntitypc.getPosition());
            float offset =
                    0.2f
                            * (1
                                    / (Point.calculateDistance(
                                            startingEntitypc.getPosition(),
                                            targetEntitypc.getPosition())));
            offset = Math.min(offset, 0.2f);

            targetEntityvc.setCurrentXVelocity(
                    (targetEntityvc.getXVelocity() * 0.3f) + directionalVector.x * offset);
            targetEntityvc.setCurrentYVelocity(
                    (targetEntityvc.getYVelocity() * 0.3f) + directionalVector.y * offset);
        }
    }
}
