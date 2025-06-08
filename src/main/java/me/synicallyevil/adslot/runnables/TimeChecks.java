package me.synicallyevil.adslot.runnables;

import me.synicallyevil.adslot.AdSlot;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TimeChecks implements Runnable {

    private AdSlot ad;

    public TimeChecks(AdSlot ad){
        this.ad = ad;
    }

    @Override
    public void run() {
        for(String s : ad.getConfig().getConfigurationSection("gui.items.rentable_items").getKeys(false)){
            int slot = Integer.parseInt(s.replace("slot-", ""));
            if(!(ad.isSlotRented(slot)))
                continue;

            if(ad.getConfig().getLong("gui.items.rentable_items." + s + ".rented.time") < System.currentTimeMillis()) {
                OfflinePlayer player = ad.getServer().getOfflinePlayer(UUID.fromString(ad.getConfig().getString("gui.items.rentable_items." + s + ".rented.rented_by")));
                if(player != null && player.isOnline())
                    player.getPlayer().sendMessage(ad.getConfigMessage("slot_expired"));

                if(ad.shouldAnnounce() && ad.isEnabled(slot)){
                    for(Player p : ad.getServer().getOnlinePlayers())
                        p.sendMessage(ad.getConfigMessage("slot_now_opened").replace("%slot%", String.valueOf(slot)));
                }

                ad.clearSlot(slot);
            }
        }

        ad.updateOpenedInventories();
    }
}
