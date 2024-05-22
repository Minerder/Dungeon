package contrib.utils.components.skill.skills;

import contrib.utils.components.health.Damage;
import contrib.utils.components.health.DamageType;
import contrib.utils.components.skill.DamageProjectileSkill;
import contrib.utils.components.skill.ITargetSelection;

import core.utils.Point;

public class FireballSkill extends DamageProjectileSkill {
    public FireballSkill(ITargetSelection targetSelection) {
        super(
                "skills/fireball/fireBall_Down/",
                "sounds/skills/fireball.mp3",
                0.5f,
                new Damage(1, DamageType.FIRE, null),
                new Point(0.5f, 0.5f),
                targetSelection,
                5f);
    }
}
