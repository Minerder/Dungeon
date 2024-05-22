package contrib.utils.components.item.items;

import contrib.components.InventoryComponent;
import contrib.components.ItemComponent;
import contrib.components.SkillComponent;
import contrib.components.XPComponent;
import contrib.entities.WorldItemBuilder;
import contrib.utils.components.item.IOnCollect;
import contrib.utils.components.item.IOnDrop;
import contrib.utils.components.item.ItemClassification;
import contrib.utils.components.item.ItemData;
import contrib.utils.components.skill.Skill;
import contrib.utils.components.skill.SkillTools;
import contrib.utils.components.skill.skills.FireballSkill;
import contrib.utils.components.skill.skills.FrostNovaSkill;
import contrib.utils.components.skill.skills.GravityStormSkill;
import contrib.utils.components.skill.skills.HomingSparkSkill;

import core.Entity;
import core.Game;
import core.utils.Point;
import core.utils.SoundPlayer;
import core.utils.components.draw.Animation;

import dslToGame.AnimationBuilder;

import java.util.Random;

public class Book extends ItemData implements IOnDrop, IOnCollect {
    /**
     * Creates a new random book with respect to the current level of hero.
     *
     * @return A book
     */
    public ItemData get() {
        int randMax = 2;
        // respects hero level when generating a book
        XPComponent xc =
                (XPComponent)
                        Game.getHero().orElseThrow().getComponent(XPComponent.class).orElseThrow();
        if (xc.getCurrentLevel() >= 10) {
            randMax += 2;
        } else if (xc.getCurrentLevel() >= 5) {
            randMax++;
        }

        // book selection
        Animation inventoryTexture;
        Animation worldTexture;
        String itemName;
        String itemDesc;
        Skill skill;
        switch (new Random().nextInt(0, randMax)) {
            case 0 -> {
                skill = new Skill(new FireballSkill(SkillTools::getCursorPositionAsPoint), 1, 1);
                inventoryTexture = AnimationBuilder.buildAnimation("items/book/redBook.png");
                worldTexture = AnimationBuilder.buildAnimation("items/book/redBook.png");
                itemName = "A red book";
                itemDesc = "Teaches you how to throw a fireball that deals 1 dmg and costs 1 mana";
            }
            case 1 -> {
                skill =
                        new Skill(
                                new HomingSparkSkill(
                                        () ->
                                                SkillTools.getNearestEntityPosition(
                                                        Game.getHero().orElseThrow())),
                                1,
                                2);
                inventoryTexture = AnimationBuilder.buildAnimation("items/book/yellowBook.png");
                worldTexture = AnimationBuilder.buildAnimation("items/book/yellowBook.png");
                itemName = "A yellow book";
                itemDesc =
                        "Describes a spark that is attracted by monsters. Deals 1 dmg and costs 2 mana";
            }
            case 2 -> {
                skill = new Skill(new FrostNovaSkill(SkillTools::getHeroPosition), 10, 2);
                inventoryTexture = AnimationBuilder.buildAnimation("items/book/blueBook.png");
                worldTexture = AnimationBuilder.buildAnimation("items/book/blueBook.png");
                itemName = "A blue book";
                itemDesc =
                        "Teaches you how to freeze the ground beneath you. Slows monsters and costs 2 mana";
            }
            case 3 -> {
                skill =
                        new Skill(
                                new GravityStormSkill(SkillTools::getCursorPositionAsPoint), 20, 5);
                inventoryTexture = AnimationBuilder.buildAnimation("items/book/grayBook.png");
                worldTexture = AnimationBuilder.buildAnimation("items/book/grayBook.png");
                itemName = "A gray book";
                itemDesc =
                        "Contains information about a powerful storm that attracts nearby enemies. Costs 5 mana";
            }
            default -> throw new IllegalStateException("Unexpected value");
        }

        return new ItemData(
                ItemClassification.Basic,
                ItemKind.BOOK,
                inventoryTexture,
                worldTexture,
                itemName,
                itemDesc,
                this,
                this,
                null,
                null,
                skill);
    }

    /**
     * Places book into empty skill slot and adds skill to SkillComponent. If both skill slots are
     * full book is placed into inventory when inventory has enough space
     *
     * @param WorldItemEntity item Entity
     * @param whoCollides Entity that collects the item
     */
    @Override
    public void onCollect(Entity WorldItemEntity, Entity whoCollides) {
        InventoryComponent inventory =
                (InventoryComponent)
                        whoCollides.getComponent(InventoryComponent.class).orElseThrow();
        SkillComponent skillComponent =
                (SkillComponent) whoCollides.getComponent(SkillComponent.class).orElseThrow();
        ItemComponent item =
                (ItemComponent) WorldItemEntity.getComponent(ItemComponent.class).orElseThrow();

        if (inventory.getSkillslot1() == null) {
            inventory.setSkillslot1(item.getItemData());
            skillComponent.setSkill(item.getItemData().getSkill(), 1);
            Game.removeEntity(WorldItemEntity);
        } else if (inventory.getSkillslot2() == null) {
            inventory.setSkillslot2(item.getItemData());
            skillComponent.setSkill(item.getItemData().getSkill(), 2);
            Game.removeEntity(WorldItemEntity);
        }
        SoundPlayer.play("sounds/items/collect.mp3");
    }

    /**
     * Removes the book out of the InventoryComponent and creates a new WorldItem. If the book is in
     * a skill slot the skill slot gets emptied and removes skill from SkillComponent
     *
     * @param user entity that drops the item
     * @param which item
     * @param position of user
     */
    @Override
    public void onDrop(Entity user, ItemData which, Point position) {
        InventoryComponent inventoryComponent =
                (InventoryComponent) user.getComponent(InventoryComponent.class).orElseThrow();
        SkillComponent skillComponent =
                (SkillComponent) user.getComponent(SkillComponent.class).orElse(null);

        if (skillComponent != null
                && inventoryComponent.getSkillslot1() != null
                && inventoryComponent.getSkillslot1().equals(which)) {
            inventoryComponent.setSkillslot1(null);
            skillComponent.removeSkill(which.getSkill());
        } else if (skillComponent != null
                && inventoryComponent.getSkillslot2() != null
                && inventoryComponent.getSkillslot2().equals(which)) {
            inventoryComponent.setSkillslot2(null);
            skillComponent.removeSkill(which.getSkill());
        } else {
            inventoryComponent.removeItem(which);
        }
        WorldItemBuilder.buildWorldItem(which, position);
    }
}
