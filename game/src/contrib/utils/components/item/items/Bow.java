package contrib.utils.components.item.items;

import contrib.components.InventoryComponent;
import contrib.components.ItemComponent;
import contrib.components.SkillComponent;
import contrib.entities.WorldItemBuilder;
import contrib.utils.components.health.Damage;
import contrib.utils.components.health.DamageType;
import contrib.utils.components.item.IOnCollect;
import contrib.utils.components.item.ItemClassification;
import contrib.utils.components.item.ItemData;
import contrib.utils.components.skill.Skill;
import contrib.utils.components.skill.SkillTools;
import contrib.utils.components.skill.skills.BouncingArrowSkill;

import core.Entity;
import core.Game;
import core.components.PositionComponent;
import core.utils.SoundPlayer;
import core.utils.components.draw.Animation;

import dslToGame.AnimationBuilder;

import java.util.Random;

public class Bow extends ItemData implements IOnCollect {
    /**
     * Creates a new random bow.
     *
     * @return A bow
     */
    public ItemData get() {
        // bow selection
        Damage damage;
        Animation inventoryTexture;
        Animation worldTexture;
        String itemName;
        String itemDesc;
        int bounceAmount = 0;
        switch (new Random().nextInt(0, 3)) {
            case 0 -> {
                damage = new Damage(1, DamageType.PHYSICAL, null);
                inventoryTexture = AnimationBuilder.buildAnimation("items/bow/bronzeBow.png");
                worldTexture = AnimationBuilder.buildAnimation("items/bow/bronzeBow.png");
                itemName = "Bronze Bow";
                itemDesc = "A bow with a loose string made out of bronze";
            }
            case 1 -> {
                damage = new Damage(2, DamageType.PHYSICAL, null);
                inventoryTexture = AnimationBuilder.buildAnimation("items/bow/silverBow.png");
                worldTexture = AnimationBuilder.buildAnimation("items/bow/silverBow.png");
                itemName = "Silver Bow";
                itemDesc = "A bow made out of silver";
            }
            case 2 -> {
                damage = new Damage(3, DamageType.PHYSICAL, null);
                inventoryTexture = AnimationBuilder.buildAnimation("items/bow/rubyBow.png");
                worldTexture = AnimationBuilder.buildAnimation("items/bow/rubyBow.png");
                itemName = "Ruby Bow";
                itemDesc = "A sturdy bow made out of ruby!";
                bounceAmount = 1;
            }
            default -> throw new IllegalStateException("Unexpected value");
        }

        Skill skill =
                new Skill(
                        new BouncingArrowSkill(
                                SkillTools::getCursorPositionAsPoint, damage, bounceAmount),
                        1);

        return new ItemData(
                ItemClassification.Basic,
                ItemKind.BOW,
                inventoryTexture,
                worldTexture,
                itemName,
                itemDesc,
                this,
                ItemData::defaultDrop,
                null,
                null,
                skill);
    }

    /**
     * Replaces the WorldItemEntity with the current item in equipment slot and updates the
     * SkillComponent. Creates a new WorldEntity for the old item in equipment slot
     *
     * @param WorldItemEntity Item that gets collected
     * @param whoCollides Entity that collects the item
     */
    @Override
    public void onCollect(Entity WorldItemEntity, Entity whoCollides) {
        InventoryComponent inventoryComponent =
                (InventoryComponent)
                        whoCollides.getComponent(InventoryComponent.class).orElseThrow();
        SkillComponent skillComponent =
                (SkillComponent) whoCollides.getComponent(SkillComponent.class).orElseThrow();
        PositionComponent positionComponent =
                (PositionComponent) whoCollides.getComponent(PositionComponent.class).orElseThrow();
        ItemComponent item =
                (ItemComponent) WorldItemEntity.getComponent(ItemComponent.class).orElseThrow();

        Game.removeEntity(WorldItemEntity);

        skillComponent.replaceSkill(
                inventoryComponent.getEquipment().getSkill(), item.getItemData().getSkill());
        WorldItemBuilder.buildWorldItem(
                inventoryComponent.getEquipment(), positionComponent.getPosition());
        inventoryComponent.setEquipment(item.getItemData());
        SoundPlayer.play("sounds/items/collect.mp3");
    }
}
