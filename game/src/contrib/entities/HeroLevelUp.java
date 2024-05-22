package contrib.entities;

import contrib.components.HealthComponent;
import contrib.components.SkillComponent;
import contrib.utils.components.xp.ILevelUp;

import core.Game;
import core.utils.SoundPlayer;

public class HeroLevelUp implements ILevelUp {
    @Override
    public void onLevelUp(long nexLevel) {
        System.out.println("Hero has reached Lvl " + nexLevel + "! You gained 1 extra hitpoint.");

        HealthComponent hc =
                (HealthComponent)
                        Game.getHero()
                                .orElseThrow()
                                .getComponent(HealthComponent.class)
                                .orElseThrow();
        SkillComponent sc =
                (SkillComponent)
                        Game.getHero()
                                .orElseThrow()
                                .getComponent(SkillComponent.class)
                                .orElseThrow();
        hc.setMaximalHealthpoints(hc.getMaximalHealthpoints() + 1);
        hc.setCurrentHealthpoints(hc.getMaximalHealthpoints());

        sc.setMaxMana(sc.getMaxMana() + 1);

        switch ((int) nexLevel) {
            case 5 -> System.out.println("You have reached Lvl 5 now you can now find blue books!");
            case 10 -> System.out.println(
                    "You have reached Lvl 10 now you can now find gray books!");
        }
        SoundPlayer.play("sounds/levelup/levelup.mp3");
    }
}
