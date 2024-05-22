package contrib.utils.components.skill.skills;

import contrib.utils.components.health.Damage;
import contrib.utils.components.skill.DamageMeleeSkill;
import contrib.utils.components.skill.ITargetSelection;

import core.utils.Point;

public class MeeleSwordSkill extends DamageMeleeSkill {
    public MeeleSwordSkill(ITargetSelection selectionFunction, Damage dmg) {
        super(
                "character/knight/attack",
                "sounds/skills/sword.mp3",
                dmg,
                new Point(1, 1),
                selectionFunction);
    }
}
