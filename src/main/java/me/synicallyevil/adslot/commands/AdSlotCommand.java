package me.synicallyevil.adslot.commands;

import me.synicallyevil.adslot.AdSlot;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import static me.synicallyevil.adslot.utils.Utils.getColor;
import static me.synicallyevil.adslot.utils.Utils.isNumber;

public class AdSlotCommand implements CommandExecutor {

    private AdSlot ad;

    public AdSlotCommand(AdSlot ad){
        this.ad = ad;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(ad.getConfigMessage("not_a_player"));
            return true;
        }

        Player player = (Player)sender;

        if(args.length > 0) {
            if(args[0].equalsIgnoreCase("help")) {
                help(player);
                return true;

            }else if(args[0].equalsIgnoreCase("message")) {
                message(player, args);
                return true;
            }

            if(!(sender.hasPermission("adslot.admin"))) {
                player.sendMessage(ad.getConfigMessage("no_permissions"));
                return true;
            }

            if(args[0].equalsIgnoreCase("reload")) {
                ad.reloadConfig();
                ad.saveConfig();

                player.sendMessage(ad.getConfigMessage("config_reload"));

            }else if(args[0].equalsIgnoreCase("unlock") || args[0].equalsIgnoreCase("lock")){
                changeState(player, args);

            }else if(args[0].equalsIgnoreCase("announce")){
                toggleAnnounce(player);

            }else if(args[0].equalsIgnoreCase("editmessage")){
                editMessage(player, args);

            }else if(args[0].equalsIgnoreCase("set")) {
                set(player, args);

            } else if(args[0].equalsIgnoreCase("confirm")){
                kickConfirm(player);

            } else if(args[0].equalsIgnoreCase("kick")){
                kick(player, args);

            }else if(args[0].equalsIgnoreCase("edit")){
                edit(player, args);

            }else if(args[0].equalsIgnoreCase("admin")){
                adminHelp(player);

            }else if(args[0].equalsIgnoreCase("ban")){
                ban(player, args);

            }else if(args[0].equalsIgnoreCase("unban")){
                unban(player, args);

            }else
                help(player);

            return true;
        }

        Inventory inv = ad.getServer().createInventory(null, (9*3), ad.getGUITitle());

        ad.addGUIItem(inv, true, 1, 10);
        ad.addGUIItem(inv, true, 2, 12);
        ad.addGUIItem(inv, true, 3, 14);
        ad.addGUIItem(inv, true, 4, 16);

        for(int i = 0; i < 27; i++){
            if(inv.getItem(i) != null)
                continue;

            ad.addGUIItem(inv, false, 0, i);
        }

        player.openInventory(inv);

        return true;
    }

    private void help(Player player){
        for(String s : ad.getConfig().getStringList("global.messages.help.real_help"))
            player.sendMessage(getColor(s));
    }

    private void adminHelp(Player player){
        for(String s : ad.getConfig().getStringList("global.messages.help.admin_help"))
            player.sendMessage(getColor(s));
    }

    private void set(Player player, String[] args){
        if (!(args.length > 2) || !(isNumber(args[1]))) {
            player.sendMessage(ad.getConfigMessage("help.set"));
            return;
        }

        if (Integer.parseInt(args[1]) > 4 || Integer.parseInt(args[1]) < 0) {
            player.sendMessage(ad.getConfigMessage("not_a_valid_slot"));
            return;
        }

        if(ad.isSlotRented(Integer.parseInt(args[1]))){
            player.sendMessage(ad.getConfigMessage("slot_already_set"));
            return;
        }

        OfflinePlayer target = ad.getServer().getOfflinePlayer(args[2]);

        if (target == null) {
            player.sendMessage(ad.getConfigMessage("player_has_not_played_before").replace("%args%", args[2]));
            return;
        }

        ad.setSlot(target.getUniqueId(), Integer.parseInt(args[1]));
        ad.setTime(Integer.parseInt(args[1]), ad.getEssTime(Integer.parseInt(args[1])));
        ad.updateOpenedInventories();
        player.sendMessage(ad.getConfigMessage("slot_set"));
    }

    private void message(Player player, String[] args){
        StringBuilder builder = new StringBuilder();

        if(!(args.length > 1)){
            player.sendMessage(ad.getConfigMessage("help.message"));
            return;
        }

        for(int i = 1; i < args.length; i++)
            builder.append(args[i]).append(" ");

        for(String s : ad.getConfig().getConfigurationSection("gui.items.rentable_items").getKeys(false)){
            if(!ad.getConfig().getString("gui.items.rentable_items." + s + ".rented.rented_by").equals(String.valueOf(player.getUniqueId())))
                continue;

            ad.setMessage(Integer.parseInt(s.replace("slot-", "")), builder.toString());
        }

        player.sendMessage(ad.getConfigMessage("message_updated"));
    }

    private void editMessage(Player player, String[] args){
        StringBuilder builder = new StringBuilder();

        if(!(args.length > 2) || !(isNumber(args[1]))){
            player.sendMessage(ad.getConfigMessage("help.editmessage"));
            return;
        }

        if (Integer.parseInt(args[1]) > 4 || Integer.parseInt(args[1]) < 0) {
            player.sendMessage(ad.getConfigMessage("not_a_valid_slot"));
            return;
        }

        if(!(ad.isSlotRented(Integer.parseInt(args[1])))){
            player.sendMessage(ad.getConfigMessage("slot_not_rented"));
            return;
        }

        for(int i = 2; i < args.length; i++)
            builder.append(args[i]).append(" ");

        ad.setMessage(Integer.parseInt(args[1]), builder.toString());

        player.sendMessage(ad.getConfigMessage("admin_message_updated"));
    }

    /*private void removeMessage(Player player, String[] args){
        if(!(args.length > 1) || !(isNumber(args[1]))){
            player.sendMessage(ad.getConfigMessage("help.removemessage"));
            return;
        }

        if (Integer.parseInt(args[1]) > 4 || Integer.parseInt(args[1]) < 0) {
            player.sendMessage(ad.getConfigMessage("not_a_valid_slot"));
            return;
        }

        ad.setMessage(Integer.parseInt(args[1]), null);

        player.sendMessage(ad.getConfigMessage("admin_message_removed"));
    }*/

    private void kick(Player player, String[] args){
        if (!(args.length > 1) || !(isNumber(args[1]))) {
            player.sendMessage(ad.getConfigMessage("help.kick"));
            return;
        }

        if (Integer.parseInt(args[1]) > 4 || Integer.parseInt(args[1]) < 0) {
            player.sendMessage(ad.getConfigMessage("not_a_valid_slot"));
            return;
        }

        if(!(ad.isSlotRented(Integer.parseInt(args[1])))){
            player.sendMessage(ad.getConfigMessage("slot_not_rented"));
            return;
        }

        ad.getKicks().put(player, Integer.parseInt(args[1]));
        player.sendMessage(ad.getConfigMessage("slot_removed"));
        new BukkitRunnable(){

            @Override
            public void run(){
                ad.getKicks().remove(player);
            }

        }.runTaskLaterAsynchronously(ad, (30*20));
    }

    private void kickConfirm(Player player){
        if(!(ad.getKicks().containsKey(player))){
            player.sendMessage(ad.getConfigMessage("slot_removed_not_containing"));
            return;
        }

        ad.clearSlot(ad.getKicks().get(player));
        ad.updateOpenedInventories();
        player.sendMessage(ad.getConfigMessage("slot_removed_confirmed"));
    }

    private void changeState(Player player, String[] args){
        if(!(args.length > 1) || !(isNumber(args[1]))){
            player.sendMessage(ad.getConfigMessage("help.unlock_and_lock"));
            return;
        }

        if(Integer.parseInt(args[1]) > 4 || Integer.parseInt(args[1]) < 0){
            player.sendMessage(ad.getConfigMessage("not_a_valid_slot"));
            return;
        }

        ad.changeSlotState(args[0].equalsIgnoreCase("unlock"), Integer.parseInt(args[1]));
        if(args[0].equalsIgnoreCase("unlock"))
            player.sendMessage(ad.getConfigMessage("slot_unlocked"));
        else
            player.sendMessage(ad.getConfigMessage("slot_locked"));
    }

    private void edit(Player player, String[] args){
        if (!(args.length > 3) || !(isNumber(args[2]))) {
            player.sendMessage(ad.getConfigMessage("help.edit"));
            return;
        }

        if (Integer.parseInt(args[2]) > 4 || Integer.parseInt(args[2]) < 0) {
            player.sendMessage(ad.getConfigMessage("not_a_valid_slot"));
            return;
        }

        if(args[1].equalsIgnoreCase("price")){
            if(!(isNumber(args[3]))){
                player.sendMessage(ad.getConfigMessage("not_a_number").replace("%arg%", args[3]));
                return;
            }

            ad.setPrice(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            player.sendMessage(ad.getConfigMessage("price_updated"));
            ad.updateOpenedInventories();

        }else if(args[1].equalsIgnoreCase("time")){
            ad.setTime(Integer.parseInt(args[2]), args[3]);
            player.sendMessage(ad.getConfigMessage("time_updated"));
            ad.updateOpenedInventories();
        }
    }

    private void toggleAnnounce(Player player){
        if(ad.shouldAnnounce())
            player.sendMessage(ad.getConfigMessage("announce_toggled_off"));
        else
            player.sendMessage(ad.getConfigMessage("announce_toggled_on"));

        ad.setAnnounce(!ad.shouldAnnounce());
    }

    private void ban(Player player, String[] args){
        if(!(args.length > 1)){
            player.sendMessage(ad.getConfigMessage("help.ban"));
            return;
        }

        OfflinePlayer target = ad.getServer().getOfflinePlayer(args[1]);

        if(target == null){
            player.sendMessage(ad.getConfigMessage("player_has_not_played_before").replace("%args%", args[1]));
            return;
        }

        for(String s : ad.getConfig().getConfigurationSection("gui.items.rentable_items").getKeys(false)){
            if(ad.getConfig().getString("gui.items.rentable_items." + s + ".rented.rented_by").equals(String.valueOf(target.getUniqueId())))
                ad.clearSlot(Integer.parseInt(s.replace("slot-", "")));
        }

        if(target.isOnline())
            player.getPlayer().sendMessage(ad.getConfigMessage("banned_message"));

        ad.getBannedConfig().addBan(target.getUniqueId());
        player.sendMessage(ad.getConfigMessage("player_banned").replace("%args%", args[1]));
    }

    private void unban(Player player, String[] args){
        if(!(args.length > 1)){
            player.sendMessage(ad.getConfigMessage("help.ban"));
            return;
        }

        OfflinePlayer target = ad.getServer().getOfflinePlayer(args[1]);

        if(target == null){
            player.sendMessage(ad.getConfigMessage("player_has_not_played_before").replace("%args%", args[1]));
            return;
        }

        if(target.isOnline())
            player.getPlayer().sendMessage(ad.getConfigMessage("unbanned_message"));

        ad.getBannedConfig().removeBan(target.getUniqueId());
        player.sendMessage(ad.getConfigMessage("player_unbanned").replace("%args%", args[1]));
    }
}
