package me.synicallyevil.adslot.utils;

import me.synicallyevil.adslot.AdSlot;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BannedConfig {

    private List<UUID> uuidList;

    private File file;
    private FileConfiguration fc;

    public BannedConfig(AdSlot ad){
        uuidList = new ArrayList<>();

        file = new File(ad.getDataFolder(), File.separator + "bans.yml");
        fc = YamlConfiguration.loadConfiguration(file);

        loadConfig();
    }

    private void loadConfig(){
        try{
            if(!(file.exists())) {
                file.createNewFile();

                fc.createSection("Bans");

                build();
            }else{
                for(String s : fc.getStringList("Bans"))
                    uuidList.add(UUID.fromString(s));
            }
        }catch(Exception ignored){}
    }

    private void build(){
        try {
            fc.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addBan(UUID uuid){
        List<String> stuffToSave = new ArrayList<>();

        if(!uuidList.contains(uuid))
            uuidList.add(uuid);

        for(UUID u : uuidList)
            stuffToSave.add(String.valueOf(u));

        fc.set("Bans", stuffToSave);
        build();
    }

    public void removeBan(UUID uuid){
        List<String> stuffToSave = new ArrayList<>();
        uuidList.remove(uuid);

        for(UUID u : uuidList)
            stuffToSave.add(String.valueOf(u));

        fc.set("Bans", stuffToSave);
        build();
    }

    public List<UUID> getUuidList(){
        return uuidList;
    }
}
