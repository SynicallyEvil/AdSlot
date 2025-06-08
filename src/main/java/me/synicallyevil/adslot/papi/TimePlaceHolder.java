package me.synicallyevil.adslot.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.synicallyevil.adslot.AdSlot;
import me.synicallyevil.adslot.utils.DateUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TimePlaceHolder extends PlaceholderExpansion {

    private AdSlot ad;

    public TimePlaceHolder(AdSlot ad){
        this.ad = ad;
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "SynicallyEvil";
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "ad";
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if(player == null)
            return "";

        if(identifier.equalsIgnoreCase("player_1")){
            return ad.getServer().getOfflinePlayer(UUID.fromString(ad.getConfig().getString("gui.items.rentable_items.slot-1.rented.rented_by"))).getName();
        }else if(identifier.equalsIgnoreCase("player_2")){
            return ad.getServer().getOfflinePlayer(UUID.fromString(ad.getConfig().getString("gui.items.rentable_items.slot-2.rented.rented_by"))).getName();
        }else if(identifier.equalsIgnoreCase("player_3")){
            return ad.getServer().getOfflinePlayer(UUID.fromString(ad.getConfig().getString("gui.items.rentable_items.slot-3.rented.rented_by"))).getName();
        }else if(identifier.equalsIgnoreCase("player_4")){
            return ad.getServer().getOfflinePlayer(UUID.fromString(ad.getConfig().getString("gui.items.rentable_items.slot-4.rented.rented_by"))).getName();
        }

        if(identifier.equalsIgnoreCase("time_left_1")){
            long l = ad.getConfig().getLong("gui.items.rentable_items.slot-1.rented.time");

            if(l <= 0)
                return String.valueOf(l);

            return DateUtils.formatDateDiff(l);
        }else if(identifier.equalsIgnoreCase("time_left_2")){
            long l = ad.getConfig().getLong("gui.items.rentable_items.slot-2.rented.time");

            if(l <= 0)
                return String.valueOf(l);

            return DateUtils.formatDateDiff(l);
        }else if(identifier.equalsIgnoreCase("time_left_3")){
            long l = ad.getConfig().getLong("gui.items.rentable_items.slot-3.rented.time");

            if(l <= 0)
                return String.valueOf(l);

            return DateUtils.formatDateDiff(l);
        }else if(identifier.equalsIgnoreCase("time_left_4")){
            long l = ad.getConfig().getLong("gui.items.rentable_items.slot-4.rented.time");

            if(l <= 0)
                return String.valueOf(l);

            return DateUtils.formatDateDiff(l);
        }

        return null;
    }
}
