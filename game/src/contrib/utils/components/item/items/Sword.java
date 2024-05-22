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
import contrib.utils.components.skill.skills.MeeleSwordSkill;

import core.Entity;
import core.Game;
import core.components.PositionComponent;
import core.utils.SoundPlayer;
import core.utils.components.draw.Animation;

import dslToGame.AnimationBuilder;

import java.util.Random;

public class Sword extends ItemData implements IOnCollect {
    private int swordTypeNumber = -1;

    /**
     * Returns a specific sword
     *
     * @param swordType Sword type selection: 0 Bronze, 1 Silver, 2 Ruby
     * @return a sword
     */
    public ItemData getSpecific(int swordType) {
        this.swordTypeNumber = swordType;
        return get();
    }

    /**
     * Generates a new random sword
     *
     * @return A sword
     */
    public ItemData get() {
        Random r = new Random();
        int random;
        if (swordTypeNumber == -1) {
            random = r.nextInt(0, 3);
        } else {
            random = swordTypeNumber;
        }

        // sword selection
        Damage damage;
        Animation inventoryTexture;
        Animation worldTexture;
        String itemName;
        String itemDesc;
        switch (random) {
            case 0 -> {
                damage = new Damage(1, DamageType.PHYSICAL, null);
                inventoryTexture = AnimationBuilder.buildAnimation("items/sword/bronzeSword.png");
                worldTexture = AnimationBuilder.buildAnimation("items/sword/bronzeSword.png");
                itemName = "Bronze sword";
                itemDesc = "A blunt sword made out of bronze";
            }
            case 1 -> {
                damage = new Damage(2, DamageType.PHYSICAL, null);
                inventoryTexture = AnimationBuilder.buildAnimation("items/sword/silverSword.png");
                worldTexture = AnimationBuilder.buildAnimation("items/sword/silverSword.png");
                itemName = "Silver sword";
                itemDesc = "A sword made out of silver";
            }
            case 2 -> {
                damage = new Damage(3, DamageType.PHYSICAL, null);
                inventoryTexture = AnimationBuilder.buildAnimation("items/sword/rubySword.png");
                worldTexture = AnimationBuilder.buildAnimation("items/sword/rubySword.png");
                itemName = "Ruby sword";
                itemDesc = "A sharp sword made out of ruby!";
            }
            default -> throw new IllegalStateException("Unexpected value: " + r.nextInt(0, 3));
        }

        Skill skill =
                new Skill(new MeeleSwordSkill(SkillTools::getCursorPositionAsPoint, damage), 1);

        return new ItemData(
                ItemClassification.Basic,
                ItemKind.SWORD,
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
