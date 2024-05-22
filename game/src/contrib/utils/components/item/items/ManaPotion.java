package contrib.utils.components.item.items;

import contrib.components.InventoryComponent;
import contrib.components.SkillComponent;
import contrib.utils.components.item.IOnUse;
import contrib.utils.components.item.ItemClassification;
import contrib.utils.components.item.ItemData;

import core.Entity;
import core.utils.SoundPlayer;

import dslToGame.AnimationBuilder;

public class ManaPotion extends ItemData implements IOnUse {
    private int recoverAmount;

    /**
     * Creates a new health potion.
     *
     * @param recoverAmount amount the potion recovers
     */
    public ItemData get(int recoverAmount) {
        this.recoverAmount = recoverAmount;
        return new ItemData(
                ItemClassification.Active,
                ItemKind.POTION,
                AnimationBuilder.buildAnimation("items/potions/manaPotion.png"),
                AnimationBuilder.buildAnimation("items/potions/manaPotion.png"),
                "Mana Potion",
                "A strange liquid that smells salty. Restores " + recoverAmount + " mana",
                ItemData::defaultCollect,
                ItemData::defaultDrop,
                this,
                null,
                null);
    }

    @Override
    public void onUse(Entity e, ItemData item) {
        SkillComponent skillComponent =
                (SkillComponent) e.getComponent(SkillComponent.class).orElseThrow();
        InventoryComponent inventoryComponent =
                (InventoryComponent) e.getComponent(InventoryComponent.class).orElseThrow();
        // adds the recoverAmount to currentMana if the sum is less than maxMana else currentMana
        // gets set to maxMana
        skillComponent.setCurrentMana(
                Math.min(
                        skillComponent.getCurrentMana() + recoverAmount,
                        skillComponent.getMaxMana()));
        inventoryComponent.removeItem(item);
        SoundPlayer.play("sounds/items/potion.mp3");
    }
}
