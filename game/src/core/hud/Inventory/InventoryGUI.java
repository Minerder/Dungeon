package core.hud.Inventory;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import contrib.components.InventoryComponent;
import contrib.utils.components.item.ItemData;

import core.Game;
import core.System;
import core.systems.PlayerSystem;
import core.utils.Constants;
import core.utils.SoundPlayer;
import core.utils.controller.ScreenController;

import java.util.List;

public class InventoryGUI<T extends Actor> extends ScreenController<T> {
    private static final InventoryGUI<Actor> instance = new InventoryGUI<>(new SpriteBatch());
    private final Window inventory;
    private final DragAndDrop dragAndDrop;
    private boolean isOpen = true;
    private final int INVENTORYSLOTS_IN_A_ROW = 8;

    private InventorySlot equipmentSlot;
    private InventorySlot skillSlot1;
    private InventorySlot skillSlot2;

    /**
     * Creates an inventory GUI as big as the inventory component of the hero
     *
     * @param batch the batch which should be used to draw with
     */
    private InventoryGUI(SpriteBatch batch) {
        super(batch);
        dragAndDrop = new DragAndDrop();
        inventory = new Window("", Constants.inventorySkin);
        inventory.setResizable(false);
        add((T) inventory);
        initInventorySlots();
        closeInventory();
    }

    /** Creates all inventory slots and adds them to the inventorySlots list */
    private void initInventorySlots() {
        InventoryComponent inventoryComponent =
                (InventoryComponent)
                        Game.getHero()
                                .orElseThrow()
                                .getComponent(InventoryComponent.class)
                                .orElse(null);
        if (inventoryComponent == null) {
            this.remove((T) inventory);
            return;
        }
        int inventorySize = inventoryComponent.getMaxSize();

        InventoryDescription description = new InventoryDescription(Constants.inventorySkin);

        // Equipment slot
        equipmentSlot = new InventorySlot("skin/InventorySkin/EquipmentSlot.png");
        equipmentSlot.addListener(new InventoryDescriptionListener(description));
        inventory.add(equipmentSlot).pad(3);
        // SkillSlots
        skillSlot1 = new InventorySlot("skin/InventorySkin/SkillSlot.png");
        skillSlot1.addListener(new InventoryDescriptionListener(description));
        inventory.add(skillSlot1).pad(3);
        dragAndDrop.addTarget(new InventorySlotTarget(skillSlot1, dragAndDrop));
        skillSlot2 = new InventorySlot("skin/InventorySkin/SkillSlot.png");
        skillSlot2.addListener(new InventoryDescriptionListener(description));
        dragAndDrop.addTarget(new InventorySlotTarget(skillSlot2, dragAndDrop));
        inventory.add(skillSlot2).pad(3);
        inventory.row();

        for (int i = 1; i < inventorySize + 1; i++) {
            InventorySlot slot = new InventorySlot();
            inventory.add(slot).pad(3);
            slot.addListener(new InventoryDescriptionListener(description));
            if (i % INVENTORYSLOTS_IN_A_ROW == 0) inventory.row();
            dragAndDrop.addTarget(new InventorySlotTarget(slot, dragAndDrop));
        }
        inventory.getParent().addActor(description);
        inventory.pack();
        inventory.setPosition(
                Constants.WINDOW_WIDTH / 2f - inventory.getWidth() / 2f,
                Constants.WINDOW_HEIGHT / 2f - inventory.getHeight() / 2f);
    }

    /** Updates the inventory based on the heros inventory component */
    public void updateInventory() {
        InventoryComponent inventoryComponent =
                (InventoryComponent)
                        Game.getHero()
                                .flatMap(e -> e.getComponent(InventoryComponent.class))
                                .orElse(null);
        if (inventoryComponent == null) {
            return;
        }
        // updates the equipment slot
        if (inventoryComponent.getEquipment() != null
                && (!equipmentSlot.hasInventoryItem()
                        || !equipmentSlot
                                .getInventoryItem()
                                .getItem()
                                .equals(inventoryComponent.getEquipment()))) {
            equipmentSlot.setInventoryItem(
                    new InventoryItem(
                            inventoryComponent
                                    .getEquipment()
                                    .getInventoryTexture()
                                    .getNextAnimationTexturePath(),
                            inventoryComponent.getEquipment()));
        }
        // updates the skill slots
        if (inventoryComponent.getSkillslot1() != null
                && (!skillSlot1.hasInventoryItem()
                        || !skillSlot1
                                .getInventoryItem()
                                .getItem()
                                .equals(inventoryComponent.getSkillslot1()))) {
            skillSlot1.setInventoryItem(
                    new InventoryItem(
                            inventoryComponent
                                    .getSkillslot1()
                                    .getInventoryTexture()
                                    .getNextAnimationTexturePath(),
                            inventoryComponent.getSkillslot1()));
            dragAndDrop.addSource(new InventorySlotSource(skillSlot1, dragAndDrop));
        }
        if (inventoryComponent.getSkillslot2() != null
                && (!skillSlot2.hasInventoryItem()
                        || !skillSlot2
                                .getInventoryItem()
                                .getItem()
                                .equals(inventoryComponent.getSkillslot2()))) {
            skillSlot2.setInventoryItem(
                    new InventoryItem(
                            inventoryComponent
                                    .getSkillslot2()
                                    .getInventoryTexture()
                                    .getNextAnimationTexturePath(),
                            inventoryComponent.getSkillslot2()));
            dragAndDrop.addSource(new InventorySlotSource(skillSlot2, dragAndDrop));
        }

        List<ItemData> items = inventoryComponent.getItems();
        // removes items from list if the item is in the InventoryGUI
        // removes items from the InventoryGUI that are not present in the InventoryComponent
        for (Actor actors : inventory.getChildren()) {
            if (!(actors instanceof InventorySlot inventorySlot)) continue;
            if (actors.equals(equipmentSlot)
                    || actors.equals(skillSlot1)
                    || actors.equals(skillSlot2)) continue;
            if (inventorySlot.getInventoryItem() == null) continue;

            ItemData inventoryItem = inventorySlot.getInventoryItem().getItem();

            if (!inventoryComponent.getItems().contains(inventoryItem)) {
                inventorySlot.removeInventoryItem();
            } else {
                items.remove(inventoryItem);
            }
        }
        // adds new items to the inventory
        for (ItemData listItem : items) {
            for (Actor actors : inventory.getChildren()) {
                if (!(actors instanceof InventorySlot inventorySlot)) continue;
                if (actors.equals(equipmentSlot)
                        || actors.equals(skillSlot1)
                        || actors.equals(skillSlot2)) continue;
                if (inventorySlot.getInventoryItem() != null) continue;

                InventoryItem item =
                        new InventoryItem(
                                listItem.getInventoryTexture().getNextAnimationTexturePath(),
                                listItem);
                inventorySlot.setInventoryItem(item);
                dragAndDrop.addSource(new InventorySlotSource(inventorySlot, dragAndDrop));
                break;
            }
        }
        inventory.pack();
    }

    /** Toggles the visibility of the inventory */
    public void toggleInventory() {
        if (isOpen) closeInventory();
        else openInventory();
    }

    private void openInventory() {
        if (Game.getHero().orElseThrow().getComponent(InventoryComponent.class).isEmpty()) return;
        updateInventory();
        inventory.setVisible(true);
        isOpen = true;
        Game.systems.forEach(
                s -> {
                    if (s instanceof PlayerSystem) return;
                    s.stop();
                });
        SoundPlayer.play("sounds/items/open_inventory.mp3");
    }

    private void closeInventory() {
        inventory.setVisible(false);
        isOpen = false;
        Game.systems.forEach(System::run);
    }

    /**
     * Returns the instance of the InventoryGUI
     *
     * @return the instance of the InventoryGUI
     */
    public static InventoryGUI<Actor> getInstance() {
        return instance;
    }

    /**
     * Returns the Window of the inventory
     *
     * @return the Window of the inventory
     */
    public Window getInventoryWindow() {
        return inventory;
    }
}
