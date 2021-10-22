package me.traox.tradeplugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Events extends JavaPlugin {

    private static Events plugin;
    private FileConfiguration config = this.getConfig();
    
	public Events() {
		plugin = this;
	}
    
    public void onEnable()
    {
    	new TradeHandler(this);
    	new Commands(this);
    	new TradeEditHandler(this);
        plugin = this;
        if (!config.contains("TradingToggled"))
        {
        	config.set("TradingToggled", false);
        }
        if (!config.contains("TradeExpirationTime"))
        {
        	config.set("TradeExpirationTime", 5L);
        }
        if (!config.contains("TradeRecievedMessage"))
        {
        	config.set("TradeRecievedMessage", "%GRAY%<!> You recieved a trade request from \"%GREEN%%PLAYER%%GRAY%\". Type \"/trade %PLAYER%\" or %CLICKHERE%%GRAY% to accept the trade.");
        }
        if (!config.contains("TradeSentMessage"))
        {
        	config.set("TradeSentMessage", "%GRAY%<!> You sent a trade request to \"%GREEN%%PLAYER%%GRAY%\".");
        }
        if (!config.contains("TradeToExpired"))
        {
        	config.set("TradeToExpired", "%GRAY%<!> Your trade request to \"%GREEN%%PLAYER%%GRAY%\" has expired.");
        }
        if (!config.contains("TradeFromExpired"))
        {
        	config.set("TradeFromExpired", "%GRAY%<!> Your trade request from \"%GREEN%%PLAYER%%GRAY%\" has expired.");
        }
        plugin.saveConfig();
    }

	public static Events getInstance() {
		return plugin;
	}
}