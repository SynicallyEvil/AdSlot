package me.synicallyevil.adslot;

import me.synicallyevil.adslot.commands.AdSlotCommand;
import me.synicallyevil.adslot.listeners.InventoryListeners;
import me.synicallyevil.adslot.papi.TimePlaceHolder;
import me.synicallyevil.adslot.runnables.TimeChecks;
import me.synicallyevil.adslot.utils.BannedConfig;
import me.synicallyevil.adslot.utils.DateUtils;
import me.synicallyevil.adslot.utils.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.NumberFormat;
import java.util.*;

import static me.synicallyevil.adslot.utils.Utils.getColor;

public class AdSlot extends JavaPlugin{

    private Economy economy;
    private BannedConfig banned;

    private Map<Player, Integer> kicks = new HashMap<>();

    @Override
    public void onEnable(){
        getConfig().options().copyDefaults(true);
        saveConfig();
        reloadConfig();

        banned = new BannedConfig(this);

        if(Bukkit.getPluginManager().getPlugin("Vault") != null){
            RegisteredServiceProvider<Economy> service = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

            if(service != null)
                economy = service.getProvider();
        }

        getCommand("adslot").setExecutor(new AdSlotCommand(this));

        getServer().getPluginManager().registerEvents(new InventoryListeners(this), this);

        getServer().getScheduler().runTaskTimerAsynchronously(this, new TimeChecks(this), 0, (10*20));

        if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
            new TimePlaceHolder(this).register();
            System.out.println("[AdSlot] PlaceholderAPI HOOKED!");
        }
    }

    @Override
    public void onDisable(){
        economy = null;
        kicks.clear();
    }

    public String getGUITitle(){
        return getColor(getConfig().getString("gui.title"));
    }

    public Economy getEconomy() {
        return economy;
    }

    public Map<Player, Integer> getKicks() {
        return kicks;
    }

    public String getConfigMessage(String pathname){
        return getColor(getConfig().getString("global.messages." + pathname));
    }

    public boolean shouldAnnounce(){
        return getConfig().getBoolean("global.public_announce");
    }

    public void setAnnounce(Boolean state){
        getConfig().set("global.public_announce", state);
        saveConfig();
        reloadConfig();
    }

    public void updateOpenedInventories(){
        for(Player p : getServer().getOnlinePlayers()){
            InventoryView inv = p.getOpenInventory();
            if(inv == null)
                continue;

            InventoryView view = inv;
            Inventory inventory = view.getTopInventory();

            if(!view.getTitle().equalsIgnoreCase(getGUITitle()))
                continue;

            inventory.clear();

            for(int i = 0; i < inventory.getSize(); i++){
                if(i == 10)
                    updateGUIItems(inventory, true, 1, i);
                else if(i == 12)
                    updateGUIItems(inventory, true, 2, i);
                else if(i == 14)
                    updateGUIItems(inventory, true, 3, i);
                else if(i == 16)
                    updateGUIItems(inventory, true, 4, i);
                else
                    updateGUIItems(inventory, false, 0, i);
            }
        }
    }

    public void addGUIItem(Inventory inv, boolean isRent, int rentableSlot, int slot){
        Material mat = Material.getMaterial((isRent ? getConfig().getString("gui.items.rentable_items.slot-" + rentableSlot + ".item", "AIR") : getConfig().getString("gui.items.fake_items.item", "AIR")));

        if(mat == null)
            mat = Material.AIR;

        ItemStack item;

        if(isRent)
            item = new ItemStack(mat, getAmount("rentable_items.slot-" + rentableSlot), (short)getConfig().getInt("gui.items.rentable_items.slot-" + rentableSlot + "." + (isSlotRented(rentableSlot) ? "rented" : "unrented") + ".id"));
        else
            item = new ItemStack(mat, getAmount("fake_items"), (short)getConfig().getInt("gui.items.fake_items.id"));

        ItemMeta meta = item.getItemMeta();

        if(isRent) {
            meta.setDisplayName(getDisplayName("rentable_items.slot-" + rentableSlot + "." + (isSlotRented(rentableSlot) ? "rented" : "unrented")).replace("%player%", getConfig().getString("gui.items.rentable_items.slot-" + rentableSlot + ".rented.rented_by")).replace("%price%", getPrice(rentableSlot)));

            List<String> lore = new ArrayList<>();

            if(isSlotRented(rentableSlot)){

                if(getConfig().isSet("gui.items.rentable_items.slot-" + rentableSlot + ".rented.custom_message") && !getConfig().getString("gui.items.rentable_items.slot-" + rentableSlot + ".rented.custom_message").isEmpty()) {
                    lore.add(getColor(getConfig().getString("gui.items.rentable_items.slot-" + rentableSlot + ".rented.custom_message")));
                    lore.add(" ");
                }

                for(String s : getConfig().getStringList("gui.items.rentable_items.slot-" + rentableSlot + ".rented.lore")) {
                    lore.add(getColor(s)
                            .replace("%player%", getServer().getOfflinePlayer(UUID.fromString(getConfig().getString("gui.items.rentable_items.slot-" + rentableSlot + ".rented.rented_by"))).getName())
                            .replace("%price%", getPrice(rentableSlot))
                            .replace("%time%", DateUtils.formatDateDiff(getConfig().getLong("gui.items.rentable_items.slot-" + rentableSlot + ".rented.time"))));
                }
            }else {
                for(String s : getConfig().getStringList("gui.items.rentable_items.slot-" + rentableSlot + ".unrented.lore"))
                    lore.add(getColor(s).replace("%price%", getPrice(rentableSlot)));
            }

            meta.setLore(lore);
        }else{
            meta.setDisplayName(getDisplayName("fake_items"));
            meta.setLore(getLore("fake_items"));
        }

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        if((isRent ? getGlowing("rentable_items.slot-" + rentableSlot) : getGlowing("fake_items")))
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);

        if(isRent && isSlotRented(rentableSlot) && getConfig().getBoolean("global.use_skulls_instead")){
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta sMeta = (SkullMeta)skull.getItemMeta();

            sMeta.setOwner(getServer().getOfflinePlayer(UUID.fromString(getConfig().getString("gui.items.rentable_items.slot-" + rentableSlot + ".rented.rented_by"))).getName());
            sMeta.setDisplayName(Utils.getColor("&3&l" + getServer().getOfflinePlayer(UUID.fromString(getConfig().getString("gui.items.rentable_items.slot-" + rentableSlot + ".rented.rented_by"))).getName()));

            sMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            if((getGlowing("rentable_items.slot-" + rentableSlot)))
                skull.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);

            sMeta.setLore(meta.getLore());
            skull.setItemMeta(sMeta);

            inv.setItem(slot, skull);
        }else{
            item.setItemMeta(meta);

            inv.setItem(slot, item);
        }
    }

    private void updateGUIItems(Inventory inv, boolean isRent, int rentableSlot, int slot){
        ItemStack i = inv.getItem(slot);
        ItemMeta meta = i.getItemMeta();

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        if((isRent ? getGlowing("rentable_items.slot-" + rentableSlot) : getGlowing("fake_items")))
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);

        if(!isRent){
            i.setAmount(getAmount("fake_items"));
            meta.setDisplayName(getDisplayName("fake_items"));
            meta.setLore(getLore("fake_items"));

            i.setItemMeta(meta);
            return;
        }




        i.setItemMeta(meta);
    }

    private int getAmount(String pathname){
        return getConfig().getInt("gui.items." + pathname + ".amount", 1);
    }

    private boolean getGlowing(String pathname){
        return getConfig().getBoolean("gui.items." + pathname + ".glowing", false);
    }

    private String getDisplayName(String pathname){
        return getColor(getConfig().getString("gui.items." + pathname + ".display_name"));
    }

    public boolean isSlotRented(int slot){
        return getConfig().getBoolean("gui.items.rentable_items.slot-" + slot + ".rented_status", false);
    }

    private String getPrice(int slot){
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

        return numberFormat.format(getConfig().getInt("gui.items.rentable_items.slot-" + slot + ".price", 1000000));
    }

    public int getRawPrice(int slot){
        return getConfig().getInt("gui.items.rentable_items.slot-" + slot + ".price", 1000000);
    }

    public void setPrice(int slot, int price){
        getConfig().set("gui.items.rentable_items.slot-" + slot + ".price", price);

        saveConfig();
        reloadConfig();
    }

    public void setTime(int slot, String time){
        try{
            if(isSlotRented(slot))
                getConfig().set("gui.items.rentable_items.slot-" + slot + ".rented.time", DateUtils.parseDateDiff(time, true));

            getConfig().set("gui.items.rentable_items.slot-" + slot + ".default_time", time);

            saveConfig();
            reloadConfig();
        }catch(Exception ex){
            System.out.println("Could not set time correctly.");
        }
    }

    public String getEssTime(int slot){
        return getConfig().getString("gui.items.rentable_items.slot-" + slot + ".default_time", "7days");
    }

    private List<String> getLore(String pathname){
        List<String> lore = new ArrayList<>();

        for(String s : getConfig().getStringList("gui.items." + pathname + ".lore"))
            lore.add(getColor(s));

        return lore;
    }

    public boolean canBuySlot(String name){
        if(getConfig().getBoolean("global.can_player_buy_more_slots", false))
            return true;

        for(String s : getConfig().getConfigurationSection("gui.items.rentable_items").getKeys(false)){
            if(getConfig().getString("gui.items.rentable_items." + s + ".rented.rented_by").equalsIgnoreCase(name))
                return false;
        }

        return true;
    }

    public void setSlot(UUID uuid, int rentingSlot){
        getConfig().set("gui.items.rentable_items.slot-" + rentingSlot + ".rented_status", true);
        getConfig().set("gui.items.rentable_items.slot-" + rentingSlot + ".rented.rented_by", String.valueOf(uuid));

        saveConfig();
        reloadConfig();
    }

    public void clearSlot(int slot){
        getConfig().set("gui.items.rentable_items.slot-" + slot + ".rented_status", false);
        getConfig().set("gui.items.rentable_items.slot-" + slot + ".rented.rented_by", "None");
        getConfig().set("gui.items.rentable_items.slot-" + slot + ".rented.time", 0);
        getConfig().set("gui.items.rentable_items.slot-" + slot + ".rented.custom_message", null);

        saveConfig();
        reloadConfig();
    }

    public void changeSlotState(boolean state, int slot){
        getConfig().set("gui.items.rentable_items.slot-" + slot + ".enabled", state);

        saveConfig();
        reloadConfig();
    }

    public void setMessage(int slot, String message){
        getConfig().set("gui.items.rentable_items.slot-" + slot + ".rented.custom_message", message);

        saveConfig();
        reloadConfig();
    }

    public boolean isEnabled(int slot){
        return getConfig().getBoolean("gui.items.rentable_items.slot-" + slot + ".enabled", false);
    }

    public BannedConfig getBannedConfig(){
        return this.banned;
    }
}
