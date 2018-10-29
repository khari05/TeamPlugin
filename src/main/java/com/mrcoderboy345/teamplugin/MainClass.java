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
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.NukkitRunnable;
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
        colorMap.put("reset", TextFormat.RESET);
        colorMap.put("none", TextFormat.RESET);
        colorMap.put(null, TextFormat.RESET);
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
            Player player;
            if (args.length == 2) {
                player = this.getServer().getPlayer(args[1]);
            } else {
                player = (Player) sender;
            }
               player.despawnFromAll();
                switch(args[0]){
                    case "red":
                        player.setNameTag(TextFormat.RED + "[Red Team] " + TextFormat.WHITE + player.getName());
                        player.namedTag.putString("team", "red");
                        break;
                    case "blue":
                        player.setNameTag(TextFormat.BLUE + "[Blue Team] " + TextFormat.WHITE + player.getName());
                        player.namedTag.putString("team", "playerblue");
                        break;
                    case "reset":
                        player.setNameTag(TextFormat.WHITE + player.getName());
                        player.namedTag.putString("team", "");
                        break;
                }
                new NukkitRunnable(){
                    @Override
                    public void run(){
                        player.spawnToAll();
                    }
                }.runTaskLater(this, 60);
            }
        if (command.getName().toLowerCase().equals("rank")) {
            Player player;
            if (args.length == 2){
                player = this.getServer().getPlayer(args[1]);
            } else {
                player = (Player) sender;
            }
            if (sender.isOp()){
                if (args.length > 0){
                    player.despawnFromAll();
                    if (args[0].equals("admin")){
                        player.setNameTag(TextFormat.BLUE + "[Admin]" + colorMap.get(player.namedTag.getString("chatcolor")) + player.namedTag.getString("nickname"));
                        player.namedTag.putString("rank", "admin");
                    } else  if (args[0].equals("reset")){
                        player.setNameTag(TextFormat.WHITE + player.getName());
                        player.namedTag.putString("rank", "none");
                    } else  if (args[0].equals("dev")){
                        player.setNameTag(TextFormat.RED + "[Developer]" + colorMap.get(player.namedTag.getString("chatcolor")) + player.namedTag.getString("nickname"));
                        player.namedTag.putString("rank", "dev");
                    } else {
                        player.setNameTag("[" + args[0] + "]" + colorMap.get(player.namedTag.getString("chatcolor")) + player.namedTag.getString("nickname"));
                        player.namedTag.putString("rank", args[0]);
                    }
                    new NukkitRunnable(){
                        @Override
                        public void run(){
                            player.spawnToAll();
                        }
                    }.runTaskLater(this, 60);
                }

            }
        }
        if (command.getName().toLowerCase().equals("textcolor")){
            Player player = (Player) sender;
            if (args.length > 0){
                player.despawnFromAll();
                if(colorMap.containsKey(args[0])){
                    player.setNameTag(colorMap.get(args[0]) + player.getNameTag());
                    player.namedTag.putString("chatcolor", args[0].toLowerCase());
                } else{
                    player.setNameTag(TextFormat.WHITE + player.getNameTag());
                    player.namedTag.putString("chatcolor", "none");
                } 
                new NukkitRunnable(){
                    @Override
                    public void run(){
                        player.spawnToAll();
                    }
                }.runTaskLater(this, 60);
            }
        }
        if (command.getName().toLowerCase().equals("nickname")){
            Player player = (Player) sender;
            if (args.length > 0){
                if(args[0].equals("reset")){
                    player.despawnFromAll();
                    player.setNameTag(player.getName());
                    player.namedTag.putString("nickname", player.getName());
                } else{
                    player.despawnFromAll();
                    player.setNameTag(args[0]);
                    player.namedTag.putString("nickname", args[0]);
                }
                new NukkitRunnable(){
                    @Override
                    public void run(){
                        player.spawnToAll();
                    }
                }.runTaskLater(this, 60);
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
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent ev){
        if (ev.getEntity() instanceof Player) {
            Player player = (Player) ev.getEntity();
            if(ev.getDamager() instanceof Player){
                Player damager = (Player) ev.getDamager();
                if (player.namedTag.getString("team") == damager.namedTag.getString("team")) {
                    if (player.namedTag.getString("team")!=""){
                    ev.setCancelled();
                    }
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
    @EventHandler
    public void onJoin(PlayerJoinEvent ev){
        Player player = ev.getPlayer();
        player.despawnFromAll();
        new NukkitRunnable(){
        
            @Override
            public void run() {
                String playerName;
                String chatcolor;
                String rank;
        
                if (player.namedTag.getString("chatcolor").equals(null) || player.namedTag.getString("chatcolor").equals("")){
                    player.namedTag.putString("chatcolor", "none");
                    chatcolor = "none";
                } else{
                    chatcolor = player.namedTag.getString("chatcolor");
                }
        
                if (player.namedTag.getString("nickname").equals("") || player.namedTag.getString("nickname").equals(null)){
                    playerName = player.getName();
                } else{
                    playerName = player.namedTag.getString("nickname");
                }
        
                if (player.namedTag.getString("rank").equals(null) || player.namedTag.getString("rank").equals("")){
                    player.namedTag.putString("rank", "none");
                    rank = "none";
                }
                String nametag = colorMap.get(chatcolor) + playerName;
                if (player.namedTag.getString("team").equals("red")){
                    nametag = TextFormat.RED + "[Red Team] " + TextFormat.WHITE + playerName;
                }
                if (player.namedTag.getString("team").equals("blue")){
                    nametag = TextFormat.BLUE + "[Blue Team] " + TextFormat.WHITE + playerName;
                }
        
                rank = player.namedTag.getString("rank");
                if (rank.equals("none") ){
                } else if (rank.equals("admin")){
                    nametag = TextFormat.BLUE + "[Admin]" + colorMap.get(chatcolor) + playerName;
                } else if (rank.equals("dev")){
                    nametag = TextFormat.RED + "[Developer]" + colorMap.get(chatcolor) + playerName;
                } else{
                    nametag = "[" + rank + "]" + colorMap.get(player.namedTag.getString("chatcolor")) + playerName;
                }
                player.setNameTag(nametag);
                player.setDisplayName(nametag);
                player.spawnToAll();
            }
        }.runTaskLater(this, 300);
        

    }
    private void broadcastMessage(String message){
        Set<CommandSender> recipients = new HashSet<>();
        for (Player playerRecipient : this.getServer().getOnlinePlayers().values()) {
            recipients.add((CommandSender) playerRecipient);
        }
        this.getServer().broadcastMessage(message,recipients);
    }
}