package contrib.utils.components.skill.skills;

import contrib.utils.components.health.Damage;
import contrib.utils.components.health.DamageType;
import contrib.utils.components.skill.DamageProjectileSkill;
import contrib.utils.components.skill.ITargetSelection;

import core.utils.Point;

public class HomingSparkSkill extends DamageProjectileSkill {
    public HomingSparkSkill(ITargetSelection selectionFunction) {
        super(
                "skills/spark/right",
                "sounds/skills/spark.mp3",
                0.5f,
                new Damage(1, DamageType.MAGIC, null),
                new Point(0.5f, 0.5f),
                selectionFunction,
                5f);
    }
}
