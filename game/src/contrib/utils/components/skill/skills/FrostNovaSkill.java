package contrib.utils.components.skill.skills;

import contrib.components.AIComponent;
import contrib.components.CollideComponent;
import contrib.components.UpdateComponent;
import contrib.entities.monster.Monster;
import contrib.utils.components.collision.ICollide;
import contrib.utils.components.skill.ISkillFunction;
import contrib.utils.components.skill.ITargetSelection;
import contrib.utils.components.skill.IUpdateFunction;

import core.Entity;
import core.Game;
import core.components.PositionComponent;
import core.components.VelocityComponent;
import core.utils.Point;
import core.utils.SoundPlayer;

import java.util.HashMap;

public class FrostNovaSkill extends Entity implements ISkillFunction, IUpdateFunction {
    private final ITargetSelection targetSelection;
    private static final HashMap<Integer, Entity> entities = new HashMap<>();
    private float holdingTimeInFrames;

    public FrostNovaSkill(ITargetSelection targetSelection) {
        this.targetSelection = targetSelection;
    }

    @Override
    public void execute(Entity entity) {
        Point target = targetSelection.selectTargetPoint();
        createFrostNova(target);
        // change texture of ground to ice
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (Game.currentLevel
                        .getTileAt(new Point(target.x + (1 - x), target.y + (1 - y)).toCoordinate())
                        .isAccessible()) {
                    Game.currentLevel
                            .getTileAt(
                                    new Point(target.x + (1 - x), target.y + (1 - y))
                                            .toCoordinate())
                            .setTexturePath("dungeon/ice/floor/floor_1.png");
                }
            }
        }
        SoundPlayer.play("sounds/skills/frostNova.mp3");
    }

    private void createFrostNova(Point pos) {
        this.holdingTimeInFrames = 200;
        Entity frostnova = new Entity();
        new PositionComponent(frostnova, pos);

        ICollide entered =
                (a, b, from) -> {
                    if (b.equals(Game.getHero().orElseThrow())) return;
                    // only continue if Entity has VelocityComponent and AIComponent
                    VelocityComponent vc =
                            (VelocityComponent)
                                    b.getComponent(VelocityComponent.class).orElse(null);
                    AIComponent ac = (AIComponent) b.getComponent(AIComponent.class).orElse(null);
                    if (vc == null || ac == null) return;

                    // reduce movement speed
                    entities.put(b.id(), b);
                    vc.setXVelocity(((Monster) b).getxSpeed() * 0.5f);
                    vc.setYVelocity(((Monster) b).getySpeed() * 0.5f);
                };

        ICollide left =
                (a, b, from) -> {
                    // only continue if Entity has VelocityComponent and AIComponent
                    VelocityComponent vc =
                            (VelocityComponent)
                                    b.getComponent(VelocityComponent.class).orElse(null);
                    AIComponent ac = (AIComponent) b.getComponent(AIComponent.class).orElse(null);
                    if (vc == null || ac == null || entities.get(b.id()) == null) return;

                    // increase movement speed
                    entities.remove(b.id());
                    vc.setXVelocity(((Monster) b).getxSpeed());
                    vc.setYVelocity(((Monster) b).getySpeed());
                };
        new CollideComponent(frostnova, new Point(-1f, -1f), new Point(3, 3), entered, left);
        new UpdateComponent(frostnova, this);
    }

    @Override
    public void update(Entity entity) {
        holdingTimeInFrames = Math.max(0, --holdingTimeInFrames);
        if (holdingTimeInFrames == 0) {
            // accelerates entities in list
            entities.forEach(
                    (key, ent) -> {
                        VelocityComponent vc =
                                (VelocityComponent)
                                        ent.getComponent(VelocityComponent.class).orElse(null);
                        if (vc == null) return;

                        vc.setXVelocity(((Monster) ent).getxSpeed());
                        vc.setYVelocity(((Monster) ent).getySpeed());
                    });
            // changes textures of ground back
            PositionComponent pc =
                    (PositionComponent) entity.getComponent(PositionComponent.class).orElseThrow();
            Point pos = pc.getPosition();
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    if (Game.currentLevel
                            .getTileAt(new Point(pos.x + (1 - x), pos.y + (1 - y)).toCoordinate())
                            .isAccessible()) {
                        Game.currentLevel
                                .getTileAt(
                                        new Point(pos.x + (1 - x), pos.y + (1 - y)).toCoordinate())
                                .setTexturePath("dungeon/default/floor/floor_1.png");
                    }
                }
            }
            Game.removeEntity(entity);
        }
    }
}
