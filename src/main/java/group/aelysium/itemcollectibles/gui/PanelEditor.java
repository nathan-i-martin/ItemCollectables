package group.aelysium.itemcollectibles.gui;

import group.aelysium.itemcollectibles.lib.gui.models.GUI;
import org.bukkit.entity.Player;

public class PanelEditor extends GUI {
    public PanelEditor(String title, int rows, Player player) {
        super(title,rows,player);
    }
/*
    @Override
    void execute(InventoryClickEvent event, ItemCollectibles itemCollectibles) {
        final Player player = (Player) event.getWhoClicked();
        final int slotId = event.getRawSlot();
        final ItemStack clickedItem = event.getCurrentItem();
        boolean rightClick = false;
        boolean shiftClick = false;
        double changeAmount = 1.0;

        if(event.isLeftClick()) rightClick = false;
        else if(event.isRightClick()) rightClick = true;
        if(event.isShiftClick()) shiftClick = true;

        if(!rightClick) changeAmount = changeAmount * -1;

        if(shiftClick) changeAmount = changeAmount * 0.1;

        changeAmount = Math.roundTo(changeAmount, 3);

        Panel panel = itemCollectibles.getPanel(Integer.valueOf(event.getInventory().getItem(0).getItemMeta().getDisplayName()));

        switch (slotId) {
            case 13: // Y Position
                panel.getLocation().add(0.0,changeAmount,0.0);
                break;
            case 21: // X Position
                panel.getLocation().add(changeAmount,0.0,0.0);
                break;
            case 22: // YAW
                panel.getLocation().setYaw((float) (panel.getLocation().getYaw() + changeAmount));
                break;
            case 23: // Z Position
                panel.getLocation().add(0.0,0.0,changeAmount);
                break;
            case 30: // X Rotation
                panel.getRotation().setX(
                        Math.toRadians(
                                Math.toDegrees(panel.getRotation().getX()) + changeAmount
                        ));
                break;
            case 31: // Y Rotation
                panel.getRotation().setY(
                        Math.toRadians(
                                Math.toDegrees(panel.getRotation().getY()) + changeAmount
                        ));
                break;
            case 33: // Z Rotation
                panel.getRotation().setZ(
                        Math.toRadians(
                                Math.toDegrees(panel.getRotation().getZ()) + changeAmount
                        ));
                break;
            case 53: // Remove Panel
                panel.remove();
                break;
        }

        panel.updateArmorStand();
        this.updateInventory(panel, player, screenControl);
    }

    void updateInventory(Panel panel, HumanEntity player, ScreenControl screenControl) {

        this.setItem(0,GUI.createGuiItem(Material.COMPASS,String.valueOf(panel.getId()),"Panel ID"));
        this.setItem(1,GUI.createGuiItem(Material.STRUCTURE_VOID,"Matrix Index:",String.valueOf(panel.getMatrixIndex())));

        this.setItem(13,GUI.createGuiItem(Material.WOOL, (short) 5,"Y Position ("+panel.getLocation().getY()+")","Right Click: +1","Left Click: -1","+ Shift for finer values"));
        this.setItem(21,GUI.createGuiItem(Material.WOOL, (short) 14,"X Position ("+panel.getLocation().getX()+")","Right Click: +1","Left Click: -1","+ Shift for finer values"));
        this.setItem(22,GUI.createGuiItem(Material.DOUBLE_PLANT,"YAW ("+panel.getLocation().getYaw()+")","Right Click: +1","Left Click: -1","+ Shift for finer values"));
        this.setItem(23,GUI.createGuiItem(Material.WOOL, (short) 11,"Z Position ("+panel.getLocation().getZ()+")","Right Click: +1","Left Click: -1","+ Shift for finer values"));


        Map<String, Double> rotation = new HashMap<>(3);
        rotation.put("X", Math.toDegrees(panel.getRotation().getX()));
        rotation.put("Y", Math.toDegrees(panel.getRotation().getY()));
        rotation.put("Z", Math.toDegrees(panel.getRotation().getZ()));

        this.setItem(30,GUI.createGuiItem(Material.RED_SHULKER_BOX,"X Rotation ("+rotation.get("X")+")","Right Click: +1","Left Click: -1","+ Shift for finer values"));
        this.setItem(31,GUI.createGuiItem(Material.GREEN_SHULKER_BOX,"Y Rotation ("+rotation.get("Y")+")","Right Click: +1","Left Click: -1","+ Shift for finer values"));
        this.setItem(32,GUI.createGuiItem(Material.BLUE_SHULKER_BOX,"Z Rotation ("+rotation.get("Z")+")","Right Click: +1","Left Click: -1","+ Shift for finer values"));

        this.setItem(53,GUI.createGuiItem(Material.BARRIER,"Remove Panel","Remove this panel","Can be recovered by running", "the \"Generate\" function on this screen"));

        this.openInventory(player);
    }

    public static PanelEditor constructNew(Panel panel, Player player) {
        PanelEditor gui = new PanelEditor(
                "Panel Editor",
                6,
                player
        );
        gui.setItem(0,GUI.createGuiItem(Material.COMPASS,String.valueOf(panel.getId()),"Panel ID"));
        gui.setItem(1,GUI.createGuiItem(Material.STRUCTURE_VOID,"Matrix Index:",String.valueOf(panel.getMatrixIndex())));

        gui.setItem(13,GUI.createGuiItem(Material.WOOL, (short) 5,"Y Position ("+panel.getLocation().getY()+")","Right Click: +1","Left Click: -1","+ Shift for finer values"));
        gui.setItem(21,GUI.createGuiItem(Material.WOOL, (short) 14,"X Position ("+panel.getLocation().getX()+")","Right Click: +1","Left Click: -1","+ Shift for finer values"));
        gui.setItem(22,GUI.createGuiItem(Material.DOUBLE_PLANT,"YAW ("+panel.getLocation().getYaw()+")","Right Click: +1","Left Click: -1","+ Shift for finer values"));
        gui.setItem(23,GUI.createGuiItem(Material.WOOL, (short) 11,"Z Position ("+panel.getLocation().getZ()+")","Right Click: +1","Left Click: -1","+ Shift for finer values"));


        Map<String, Double> rotation = new HashMap<>(3);
        rotation.put("X", Math.toDegrees(panel.getRotation().getX()));
        rotation.put("Y", Math.toDegrees(panel.getRotation().getY()));
        rotation.put("Z", Math.toDegrees(panel.getRotation().getZ()));

        gui.setItem(30,GUI.createGuiItem(Material.RED_SHULKER_BOX,"X Rotation ("+rotation.get("X")+")","Right Click: +1","Left Click: -1","+ Shift for finer values"));
        gui.setItem(31,GUI.createGuiItem(Material.GREEN_SHULKER_BOX,"Y Rotation ("+rotation.get("Y")+")","Right Click: +1","Left Click: -1","+ Shift for finer values"));
        gui.setItem(32,GUI.createGuiItem(Material.BLUE_SHULKER_BOX,"Z Rotation ("+rotation.get("Z")+")","Right Click: +1","Left Click: -1","+ Shift for finer values"));

        gui.setItem(53,GUI.createGuiItem(Material.BARRIER,"Remove Panel","Remove this panel","Can be recovered by running", "the \"Generate\" function on this screen"));

        return gui;
    }*/
}
