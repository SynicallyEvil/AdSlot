package me.synicallyevil.adslot.listeners;

import me.synicallyevil.adslot.AdSlot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;

public class InventoryListeners implements Listener {

    private AdSlot ad;

    private Integer[] slots = {10, 12, 14, 16};

    public InventoryListeners(AdSlot ad){
        this.ad = ad;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getView().getTitle().equalsIgnoreCase(ad.getGUITitle())){
            Player player = (Player)event.getWhoClicked();
            event.setCancelled(true);

            int slot = event.getRawSlot();

            if(!Arrays.asList(slots).contains(slot))
                return;

            int rentingSlot = 0;

            if(slot == slots[0])
                rentingSlot = 1;
            else if(slot == slots[1])
                rentingSlot = 2;
            else if(slot == slots[2])
                rentingSlot = 3;
            else if(slot == slots[3])
                rentingSlot = 4;

            if(rentingSlot == 0)
                return;

            if(ad.isSlotRented(rentingSlot)){
                for(String s : ad.getConfig().getStringList("gui.items.rentable_items.slot-" + rentingSlot + ".rented.commands"))
                    player.chat("/" + s.replace("%player%", ad.getConfig().getString("gui.items.rentable_items.slot-" + rentingSlot + ".rented.rented_by")));
                return;
            }

            if(!(ad.isEnabled(rentingSlot))){
                player.sendMessage(ad.getConfigMessage("slot_not_rentable"));
                return;
            }

            if(!(ad.canBuySlot(String.valueOf(player.getUniqueId())))){
                player.sendMessage(ad.getConfigMessage("cant_buy_more"));
                return;
            }

            if(ad.getBannedConfig().getUuidList().contains(player.getUniqueId())){
                player.sendMessage(ad.getConfigMessage("banned_message"));
                return;
            }

            if(!(ad.getEconomy().has(player, ad.getRawPrice(rentingSlot)))){
                player.sendMessage(ad.getConfigMessage("cant_afford"));
                return;
            }

            if(ad.getConfig().getBoolean("gui.items.rentable_items.slot-" + rentingSlot + ".permission_needed") && !player.hasPermission("adslot.buy." + rentingSlot)){
                player.sendMessage(ad.getConfigMessage("slot_needs_permission"));
                return;
            }

            ad.getEconomy().withdrawPlayer(player, ad.getRawPrice(rentingSlot));
            ad.setSlot(player.getUniqueId(), rentingSlot);
            ad.setTime(rentingSlot, ad.getEssTime(rentingSlot));
            ad.addGUIItem(event.getInventory(), true, rentingSlot, slot);
            player.sendMessage(ad.getConfigMessage("purchased_slot"));

            ad.updateOpenedInventories();
        }
    }
}
