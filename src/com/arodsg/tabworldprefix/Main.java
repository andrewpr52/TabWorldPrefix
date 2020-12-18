package com.arodsg.tabworldprefix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener, TabCompleter {
	Map<String, ChatColor> colorsMap = new HashMap<String, ChatColor>();
	{
		colorsMap.put("aqua", ChatColor.AQUA);
		colorsMap.put("black", ChatColor.BLACK);
		colorsMap.put("blue", ChatColor.BLUE);
		colorsMap.put("darkaqua", ChatColor.DARK_AQUA);
		colorsMap.put("darkblue", ChatColor.DARK_BLUE);
		colorsMap.put("darkgray", ChatColor.DARK_GRAY);
		colorsMap.put("darkgreen", ChatColor.DARK_GREEN);
		colorsMap.put("darkpurple", ChatColor.DARK_PURPLE);
		colorsMap.put("darkred", ChatColor.DARK_RED);
		colorsMap.put("gold", ChatColor.GOLD);
		colorsMap.put("gray", ChatColor.GRAY);
		colorsMap.put("green", ChatColor.GREEN);
		colorsMap.put("lightpurple", ChatColor.LIGHT_PURPLE);
		colorsMap.put("red", ChatColor.RED);
		colorsMap.put("white", ChatColor.WHITE);
		colorsMap.put("yellow", ChatColor.YELLOW);
	}
	
	Map<String, ChatColor> stylesMap = new HashMap<String, ChatColor>();
	{
		stylesMap.put("bold", ChatColor.BOLD);
		stylesMap.put("default", ChatColor.RESET);
		stylesMap.put("italic", ChatColor.ITALIC);
		// These styles aren't working. They get reset to default on the player list after ~10 seconds.
			// stylesMap.put("strike", ChatColor.STRIKETHROUGH);
			// stylesMap.put("underline", ChatColor.UNDERLINE);
	}
	
    @Override
    public void onEnable() {
//    	saveDefaultConfig();
    	getServer().getPluginManager().registerEvents(this, this);
    }
    
    @Override
    public void onDisable() {
    	
    }
    
    public void setAllPlayerListPrefixes() {
    	for (Player player : Bukkit.getOnlinePlayers()) {
    		setPlayerListPrefix(player);
    	}
    }
    
    public String setPlayerListPrefix(Player player) {
    	GameMode playerGameMode = player.getGameMode();
    	return setPlayerListPrefix(player, playerGameMode);
    }
    
    public String setPlayerListPrefix(Player player, GameMode playerGameMode) {
    	String playerWorldName = player.getWorld().getName();
    	String newPlayerName = player.getName();
        
    	if(this.getConfig().getConfigurationSection("worlds").getKeys(false).contains(playerWorldName)) {
    		String worldPrefix = this.getConfig().getString("worlds." + playerWorldName + ".prefix");
    		String prefixColorString = this.getConfig().getString("worlds." + playerWorldName + ".color");
    		String prefixStyleString = this.getConfig().getString("worlds." + playerWorldName + ".style");
    		
    		ChatColor prefixColor = colorsMap.containsKey(prefixColorString) ? colorsMap.get(prefixColorString) : ChatColor.WHITE;
    		
    		if (playerGameMode != GameMode.SPECTATOR) {
	    		if (stylesMap.containsKey(prefixStyleString) && !prefixStyleString.equalsIgnoreCase("default")) {
	    			ChatColor prefixStyle = stylesMap.get(prefixStyleString);
	    			newPlayerName = "[" + "" + prefixColor + "" + prefixStyle + "" + worldPrefix + ChatColor.RESET + "] " + newPlayerName;
	    		}
	    		else {
	    			newPlayerName = "[" + "" + prefixColor + "" + worldPrefix + ChatColor.RESET + "] " + newPlayerName;
	    		}
    		}
    		else { // GameMode is spectator, needs special styling to keep the full name italic like Minecraft does by default
    			if (stylesMap.containsKey(prefixStyleString) && !prefixStyleString.equalsIgnoreCase("default")) {
	    			ChatColor prefixStyle = stylesMap.get(prefixStyleString);
	    			newPlayerName = "[" + ChatColor.RESET + prefixColor + prefixStyle + ChatColor.ITALIC + worldPrefix + ChatColor.RESET + ChatColor.ITALIC + "] " + newPlayerName;
	    		}
	    		else {
	    			newPlayerName = "[" + ChatColor.RESET + prefixColor + ChatColor.ITALIC + worldPrefix + ChatColor.RESET + ChatColor.ITALIC + "] " + newPlayerName;
	    		}
    		}
    	}
    	
    	player.setPlayerListName(newPlayerName);
    	
    	return newPlayerName;
    }
    
    public Boolean doesWorldExist(String worldName) {
    	for(World world : Bukkit.getServer().getWorlds()) {
    		if(world.getName().equalsIgnoreCase(worldName)) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
    	Player player = (Player) event.getPlayer();
    	setPlayerListPrefix(player);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorld(PlayerChangedWorldEvent event) {
        Player player = (Player) event.getPlayer();
        setPlayerListPrefix(player);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
    	Player player = event.getPlayer();
    	GameMode newGameMode = event.getNewGameMode();
    	
    	setPlayerListPrefix(player, newGameMode);
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		ArrayList<String> options = new ArrayList<String>();
		ArrayList<String> tabCompletionList = new ArrayList<String>();
		
        if (sender instanceof Player) {
        	if (command.getName().equalsIgnoreCase("twp")) {
                if (args.length == 1) {
                	String argToComplete = args[0];
                	
                	options.add("delete");
                	options.add("edit");
                	options.add("list");
                	options.add("set");
                	
                	for(String option : options) {
                		if(option.startsWith(argToComplete.toLowerCase())) {
                			tabCompletionList.add(option);
                		}
                	}
                	
                    return tabCompletionList;
                }
                else if (args.length == 2) {
                	String argToComplete = args[1];
                	
                	if (args[0].equalsIgnoreCase("delete")) {
                		for(String worldName : this.getConfig().getConfigurationSection("worlds").getKeys(false)) {
                			options.add(worldName);
                		}
                		
                		for(String option : options) {
                    		if(option.startsWith(argToComplete.toLowerCase())) {
                    			tabCompletionList.add(option);
                    		}
                    	}
                		
                		return tabCompletionList;
                	}
                	else if (args[0].equalsIgnoreCase("edit")) {
                		for(World world : Bukkit.getServer().getWorlds()) {
                			options.add(world.getName());
                		}
                		
                		for(String option : options) {
                    		if(option.startsWith(argToComplete.toLowerCase())) {
                    			tabCompletionList.add(option);
                    		}
                    	}
                    	
                    	return tabCompletionList;
                	}
                	else if (args[0].equalsIgnoreCase("set")) {
                		for(World world : Bukkit.getServer().getWorlds()) {
                			options.add(world.getName());
                		}
                		
                		for(String option : options) {
                    		if(option.startsWith(argToComplete.toLowerCase())) {
                    			tabCompletionList.add(option);
                    		}
                    	}
                		
                		return tabCompletionList;
                	}
                }
                else if (args.length == 3) {
                	String argToComplete = args[2];
                	
                	if (args[0].equalsIgnoreCase("edit")) {
                		options.add("color");
                		options.add("prefix");
                		options.add("style");
                		
                		for(String option : options) {
                    		if(option.startsWith(argToComplete.toLowerCase())) {
                    			tabCompletionList.add(option);
                    		}
                    	}
                    	
                    	return tabCompletionList;  
                	}
                	else {
                		return null;
                	}
                }
                else if (args.length == 4) {
                	String argToComplete = args[3];
                	
                	if (args[0].equalsIgnoreCase("edit")) {
                		if (args[2].equalsIgnoreCase("color")) {
                			for(String color : colorsMap.keySet()) {
                				options.add(color);
                			}
                			
                			for(String option : options) {
                        		if(option.startsWith(argToComplete.toLowerCase())) {
                        			tabCompletionList.add(option);
                        		}
                        	}
                			
                			return tabCompletionList;
                		}
                		else if (args[2].equalsIgnoreCase("style")) {
                			for(String style : stylesMap.keySet()) {
                				options.add(style);
                			}
                			
                			for(String option : options) {
                        		if(option.startsWith(argToComplete.toLowerCase())) {
                        			tabCompletionList.add(option);
                        		}
                        	}
                			
                			return tabCompletionList;
                		}
                		else {
                			return null;
                		}
                	}
                	else if (args[0].equalsIgnoreCase("set")) {
                		for(String color : colorsMap.keySet()) {
                			options.add(color);
                		}
                		
                		for(String style : stylesMap.keySet()) {
                			options.add(style);
                		}
                		
                		Collections.sort(options);
                		
                		for(String option : options) {
                    		if(option.startsWith(argToComplete.toLowerCase())) {
                    			tabCompletionList.add(option);
                    		}
                    	}
                		
                		return tabCompletionList;
                	}
                	else {
                		return null;
                	}
                }
                else if (args.length == 5) {
                	String argToComplete = args[4];
                	
                	if (args[0].equalsIgnoreCase("set")) {
                		if (colorsMap.containsKey(args[3])) {
                			for(String style : stylesMap.keySet()) {
                				options.add(style);
                    		}
                			
                			for(String option : options) {
                        		if(option.startsWith(argToComplete.toLowerCase())) {
                        			tabCompletionList.add(option);
                        		}
                        	}
                			
                			return tabCompletionList;
                		}
                		else if (stylesMap.containsKey(args[3])) {
                			for(String color : colorsMap.keySet()) {
                				options.add(color);
                    		}
                			
                			for(String option : options) {
                        		if(option.startsWith(argToComplete.toLowerCase())) {
                        			tabCompletionList.add(option);
                        		}
                        	}
                			
                			return tabCompletionList;
                		}
                		else {
                    		return null;
                    	}
                	}
                	else {
                		return null;
                	}
                }
                else {
                	return null;
                }
                
                return null;
        	}
        	
        	return null;
        }
        
		return null;
    }
    
    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
    	if (command.getName().equalsIgnoreCase("twp") && args.length > 0) {
    		if (args[0].equalsIgnoreCase("list")) {
            	Set<String> worldsSet = this.getConfig().getConfigurationSection("worlds").getKeys(false);

        		sender.sendMessage(ChatColor.BOLD + "TabWorldPrefix List");
            	sender.sendMessage(ChatColor.ITALIC + "worldname - prefix");
            	sender.sendMessage("--------------------");
            	
            	for (Object object : worldsSet) {
            		String worldName = object.toString();
            		String prefixColorString = this.getConfig().getString("worlds." + worldName + ".color");
            		ChatColor prefixColor = colorsMap.containsKey(prefixColorString) ? colorsMap.get(prefixColorString) : ChatColor.WHITE;
            		String prefixStyleString = this.getConfig().getString("worlds." + worldName + ".style");
            		
            		if (stylesMap.containsKey(prefixStyleString) && !prefixStyleString.equalsIgnoreCase("default")) {
            			ChatColor prefixStyle = stylesMap.get(prefixStyleString);
            			sender.sendMessage(worldName + " - " + prefixColor + prefixStyle + this.getConfig().getString("worlds." + worldName + ".prefix") + ChatColor.RESET);
            		}
            		else {
            			sender.sendMessage(worldName + " - " + prefixColor + this.getConfig().getString("worlds." + worldName + ".prefix") + ChatColor.RESET);
            		}
            	}
            	
            	return true;
            }
            else if (args[0].equalsIgnoreCase("set")) {
            	if(args.length == 3 || args.length == 4 || args.length == 5) {
            		String worldName = args[1];
            		
            		if(doesWorldExist(worldName)) {
	            		String worldPrefix = args[2];
	            		this.getConfig().set("worlds." + worldName + ".prefix", worldPrefix);
	            		
	            		if (args.length == 4) {
	            			if (colorsMap.containsKey(args[3])) {
	    	        			this.getConfig().set("worlds." + worldName + ".color", args[3]);
	            			}
	            			else if (stylesMap.containsKey(args[3])) {
	            				this.getConfig().set("worlds." + worldName + ".style", args[3]);
	            			}
	            			else {
	            				sender.sendMessage("Invalid color or style argument.");
	    	                	sender.sendMessage("Usage: /twp set [worldname] [prefix] {color/style} {color/style}");
	    	                	return false;
	            			}
	            		}
	            		else if(args.length == 5) {
	            			if (colorsMap.containsKey(args[3]) && stylesMap.containsKey(args[4])) {
	    	        			this.getConfig().set("worlds." + worldName + ".color", args[3]);
	    	        			this.getConfig().set("worlds." + worldName + ".style", args[4]);
	            			}
	            			else if (stylesMap.containsKey(args[3]) && colorsMap.containsKey(args[4])) {
	            				this.getConfig().set("worlds." + worldName + ".style", args[3]);
	            				this.getConfig().set("worlds." + worldName + ".color", args[4]);
	            			}
	            			else {
	            				sender.sendMessage("Invalid color or style argument.");
	    	                	sender.sendMessage("Usage: /twp set [worldname] [prefix] {color/style} {color/style}");
	    	                	return false;
	            			}
	            		}
	            		
	            		this.saveConfig();
	            		
	            		setAllPlayerListPrefixes();
	            		sender.sendMessage("Set prefix " + ChatColor.ITALIC + worldPrefix + ChatColor.RESET + " for world " + ChatColor.ITALIC + worldName);
	                	
	    	            return true;
            		}
            		else {
            			sender.sendMessage("World " + ChatColor.ITALIC + worldName + ChatColor.RESET + " could not be found.");
            			return false;
            		}
                }
                else {
                	sender.sendMessage("Incorrect number of arguments.");
                	return false;
                }
            }
            else if (args[0].equalsIgnoreCase("edit")) {
            	if(args.length == 4) {
            		if(this.getConfig().getConfigurationSection("worlds").getKeys(false).contains(args[1])) {
    	        		if(args[2].equalsIgnoreCase("prefix")) {
    		        		String worldName = args[1];
    		        		String prefixString = args[3];
    		        		
    		        		this.getConfig().set("worlds." + worldName + ".prefix", prefixString);
    		        		
    		        		setAllPlayerListPrefixes();
    		        		this.saveConfig();
    		        		
    		        		sender.sendMessage("Set prefix " + ChatColor.ITALIC + prefixString + ChatColor.RESET + " for world " + ChatColor.ITALIC + worldName);
    		            	
    			            return true;
    	        		}
    	        		else if(args[2].equalsIgnoreCase("color")) {
    		        		String worldName = args[1];
    		        		String prefixColorString = args[3];
    		        		ChatColor prefixColor;
    		        		
    		        		if (colorsMap.containsKey(prefixColorString)) {
    		    				this.getConfig().set("worlds." + worldName + ".color", prefixColorString);
    		    				prefixColor = colorsMap.get(prefixColorString);
    		    			}
    		    			else {
    		    				sender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RESET + " Invalid color specified");
    		    				return false;
    		    			}
    		        		
    		        		setAllPlayerListPrefixes();
    		        		this.saveConfig();
    		        		
    		        		sender.sendMessage("Set color " + prefixColor + prefixColorString + ChatColor.RESET + " for world " + ChatColor.ITALIC + worldName);
    		            	
    			            return true;
    	        		}
    	        		else if(args[2].equalsIgnoreCase("style")) {
    		        		String worldName = args[1];
    		        		String prefixStyleString = args[3];
    		        		ChatColor prefixStyle;
    		        		
    		        		if (stylesMap.containsKey(prefixStyleString)) {
    		    				this.getConfig().set("worlds." + worldName + ".style", prefixStyleString);
    		    				prefixStyle = stylesMap.get(prefixStyleString);
    		    			}
    		    			else {
    		    				sender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RESET + " Invalid style specified");
    		    				return false;
    		    			}
    		        		
    		        		this.saveConfig();
    		        		
    		        		setAllPlayerListPrefixes();
    		        		sender.sendMessage("Set style " + prefixStyle + prefixStyleString + ChatColor.RESET + " for world " + ChatColor.ITALIC + worldName);
    		            	
    			            return true;
    	        		}
    	        		else {
    	        			sender.sendMessage("Invalid argument.");
    	                	sender.sendMessage("Usage: /twp edit [worldname] [prefix/color/style] [value]");
    	                	return false;
    	        		}
            		}
            		else {
            			sender.sendMessage("World " + ChatColor.ITALIC + args[1] + ChatColor.RESET + " could not be found.");
                    	sender.sendMessage("Usage: /twp edit [worldname] [prefix/color/style] [value]");
                    	return false;
            		}
                }
                else {
                	sender.sendMessage("Incorrect number of arguments.");
                	sender.sendMessage("Usage: /twp edit [worldname] [prefix/color/style] [value]");
                	return false;
                }
            }
            else if (args[0].equalsIgnoreCase("delete")) {
            	if(args.length == 2) {
            		String worldName = args[1];
            		
            		if(this.getConfig().getConfigurationSection("worlds").getKeys(false).contains(worldName)) {
	            		this.getConfig().set("worlds." + worldName, null);
	            		this.saveConfig();
	            		
	            		setAllPlayerListPrefixes();
	            		sender.sendMessage("Deleted prefix for world " + ChatColor.ITALIC + worldName);
	                	
	    	            return true;
            		}
            		else {
            			sender.sendMessage("World " + ChatColor.ITALIC + worldName + ChatColor.RESET + " could not be found.");
            			sender.sendMessage("Usage: /twp delete [worldname]");
                    	return false;
            		}
                }
                else {
                	sender.sendMessage("Incorrect number of arguments.");
                	sender.sendMessage("Usage: /twp delete [worldname]");
                	return false;
                }
            }
            else {
            	sender.sendMessage("Invalid argument.");
            	return false;
            }
    	}
    	
        return false;
    }
}