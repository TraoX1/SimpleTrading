package me.traox.tradeplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor, Listener {

	SignGui signGui;
	EditTradeMenu editTrade;
	
	private static Events plugin;
	private FileConfiguration config = null;
	
    public Commands(Events plugin)
    {
        this.plugin = plugin;
        signGui = new SignGui(plugin);
        editTrade = new EditTradeMenu(plugin);
        config = plugin.getConfig();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getCommand("toggletrading").setExecutor(this);
        plugin.getCommand("edittrade").setExecutor(this);
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("toggletrading"))
		{
			if (sender.hasPermission("tradeplugin.use.toggletrading"))
			{
				if (!config.getBoolean("TradingToggled"))
				{
					config.set("TradingToggled", true);
					sender.sendMessage(ChatColor.GREEN + "Trading has been disabled.");
				}
				else
				{
					config.set("TradingToggled", false);
					sender.sendMessage(ChatColor.GREEN + "Trading has been enabled.");
				}
				plugin.saveConfig();
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
			}
		}
		if (label.equalsIgnoreCase("edittrade"))
		{
			if (sender.hasPermission("tradeplugin.use.edittrade"))
			{
				editTrade.editTrade(((Player)sender));
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
			}
		}
		return true;
	}
}
