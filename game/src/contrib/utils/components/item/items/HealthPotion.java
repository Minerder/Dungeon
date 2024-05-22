package contrib.utils.components.item.items;

import contrib.components.HealthComponent;
import contrib.components.InventoryComponent;
import contrib.utils.components.item.IOnUse;
import contrib.utils.components.item.ItemClassification;
import contrib.utils.components.item.ItemData;

import core.Entity;
import core.utils.SoundPlayer;

import dslToGame.AnimationBuilder;

public class HealthPotion extends ItemData implements IOnUse {
    private int healingAmount;

    /**
     * Creates a new health potion.
     *
     * @param healingAmount amount the potion heals
     */
    public ItemData get(int healingAmount) {
        this.healingAmount = healingAmount;
        return new ItemData(
                ItemClassification.Active,
                ItemKind.POTION,
                AnimationBuilder.buildAnimation("items/potions/healthPotion.png"),
                AnimationBuilder.buildAnimation("items/potions/healthPotion.png"),
                "Health Potion",
                "A potion that looks delicious. Restores " + healingAmount + " health.",
                ItemData::defaultCollect,
                ItemData::defaultDrop,
                this,
                null,
                null);
    }

    @Override
    public void onUse(Entity e, ItemData item) {
        HealthComponent healthComponent =
                (HealthComponent) e.getComponent(HealthComponent.class).orElseThrow();
        InventoryComponent inventoryComponent =
                (InventoryComponent) e.getComponent(InventoryComponent.class).orElseThrow();
        // adds the healingAmount to currentHealth if the sum is less than maxHealth else
        // currentHealth gets set to maxHealth
        healthComponent.setCurrentHealthpoints(
                Math.min(
                        healthComponent.getCurrentHealthpoints() + healingAmount,
                        healthComponent.getMaximalHealthpoints()));
        inventoryComponent.removeItem(item);
        SoundPlayer.play("sounds/items/potion.mp3");
    }
}
