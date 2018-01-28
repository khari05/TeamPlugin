package com.mrcoderboy345.teamplugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;

/**
 * author: MrCoderBoy345
 * TeamPlugin for Nukkit
 */
public class MainClass extends PluginBase implements Listener{

    private Map<String,TextFormat> colorMap;
    private Map<String,Integer> deathCount;
    Boolean deathCountEnabled = false;
    private void initDeathCount(){
        deathCount = new HashMap<String,Integer>();
        deathCount.put("blue", 0);
        deathCount.put("red", 0);
    }
    private void initColorMap() {
        colorMap = new HashMap<String,TextFormat>();
        colorMap.put("blue", TextFormat.BLUE);
        colorMap.put("purple", TextFormat.DARK_PURPLE);
        colorMap.put("red", TextFormat.RED);
        colorMap.put("green", TextFormat.GREEN);
        colorMap.put("aqua", TextFormat.AQUA);
        colorMap.put("reset", TextFormat.WHITE);
    }

    @Override
    public void onLoad() {
        this.getLogger().info(TextFormat.WHITE + "I've been loaded!");
    }

    @Override
    public void onEnable() {
        this.getLogger().info(TextFormat.BLUE + "I've been enabled!");
        this.getLogger().info(TextFormat.BLUE + "TeamPlugin v1.0");
        this.getLogger().info(TextFormat.BLUE + "By MrCoderBoy345");

        this.getLogger().info(String.valueOf(this.getDataFolder().mkdirs()));

        this.getServer().getPluginManager().registerEvents(this, this);


        initColorMap();
        initDeathCount();

    }

    @Override
    public void onDisable() {
        this.getLogger().info(TextFormat.DARK_RED + "I've been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.getLogger().info("Command received:" + command.getName());
        if (command.getName().toLowerCase().equals("team")) {
            Player player = (Player) sender;
            if (args.length == 2) {
                player = this.getServer().getPlayer(args[1]);
            }
                switch(args[0]){
                    case "red":
                        player.setNameTag(TextFormat.RED + "[Red Team] " + TextFormat.WHITE + player.getName());
                        player.namedTag.putString("team", "red");
                        break;
                    case "blue":
                        player.setNameTag(TextFormat.BLUE + "[Blue Team] " + TextFormat.WHITE + player.getName());
                        player.namedTag.putString("team", "blue");
                        break;
                    case "clear":
                        player.setNameTag(TextFormat.WHITE + player.getName());
                        player.namedTag.putString("team", "");
                        break;
                }
            }
        if (command.getName().toLowerCase().equals("rank")) {
            Player player = (Player) sender;
            if (args.length > 0){
                        if (args[0].equals("admin")){
                            player.setNameTag(TextFormat.BLUE + "[Admin]" + player.getNameTag());
                        } else  if (args[0].equals("reset")){
                            player.setNameTag(TextFormat.WHITE + player.getName());
                        } else {
                            player.setNameTag("[" + args[0] + "]" + player.getName());
                        }
            }
        }
        if (command.getName().toLowerCase().equals("textcolor")){
            Player player = (Player) sender;
            if (args.length > 0){
                if(colorMap.containsKey(args[0])){
                    player.setNameTag(colorMap.get(args[0]) + player.getNameTag());
                    player.namedTag.putString("chatcolor", args[0].toLowerCase()); 
                } else{
                    player.setNameTag(TextFormat.WHITE + player.getNameTag());
                    player.namedTag.putString("chatcolor", "reset");
                } 
            }
        }
        if (command.getName().toLowerCase().equals("nickname")) {
            Player player = (Player) sender;
            if (args.length > 0){
                 player.setNameTag(args[0]);
            }
        }
        if (command.getName().toLowerCase().equals("deathcount")) {
            if (args.length > 0){
                this.getLogger().info("args0:" + args[0]);
                if (args[0].equals("on")){
                    broadcastMessage("Deaths:\n" + TextFormat.BLUE + "Team Blue[" + TextFormat.RESET + deathCount.get("blue") + TextFormat.BLUE + "]" + TextFormat.RED + " Team Red[" + TextFormat.RESET + deathCount.get("red") + TextFormat.RED + "]");
                    deathCountEnabled = true;
                } else if (args[0].equals("off")){
                    deathCountEnabled = false;
                } else if (args[0].equals("reset")){
                deathCount.put("blue",0);
                deathCount.put("red",0);   
                }
            }
        }

        return true;
    }     
    
    @EventHandler
    public void EntityDamageEvent(EntityDamageEvent ev){
        if (ev.getEntity() instanceof Player) {
            Player player = (Player) ev.getEntity();
            if(ev instanceof EntityDamageByEntityEvent){
                Player damager = (Player)((EntityDamageByEntityEvent) ev).getDamager();
                if (player.namedTag.getString("team") == damager.namedTag.getString("team")) {
                    ev.setCancelled();
                }
            }
            
        }
    }
    @EventHandler
    public void PlayerChatEvent(PlayerChatEvent ev) {
        Player player = ev.getPlayer();
        String message = "<" + player.getNameTag() + TextFormat.RESET + "> " + colorMap.get(player.namedTag.getString("chatcolor"))+ ev.getMessage();
        this.getServer().broadcastMessage(message, ev.getRecipients());
       ev.setCancelled();  
    }
    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent ev){
        if (deathCountEnabled){
            Player player = ev.getEntity();
            Integer newDeathCount = deathCount.get(player.namedTag.getString("team")) + 1;
            deathCount.put(player.namedTag.getString("team"),newDeathCount);
            
            broadcastMessage("Deathcount:\n" + TextFormat.BLUE + "Team Blue[" + deathCount.get("blue") + "]" + TextFormat.RED + "Team Red[" + deathCount.get("red") + "]");
             if (newDeathCount == 20){
                 if (player.namedTag.getString("team").equals("red")){
                    broadcastMessage("Team " + TextFormat.BLUE + "Blue " + TextFormat.RESET + "Won!");
                 } else {
                    broadcastMessage("Team " + TextFormat.RED + "Red " + TextFormat.RESET + "Won!"); 
                 }
             }
        }
    }

    private void broadcastMessage(String message){
        Set<CommandSender> recipients = new HashSet<>();
        for (Player playerRecipient : this.getServer().getOnlinePlayers().values()) {
            recipients.add((CommandSender) playerRecipient);
        }
        this.getServer().broadcastMessage(message,recipients);
    }
}