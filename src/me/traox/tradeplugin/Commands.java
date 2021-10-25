package me.traox.tradeplugin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

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
        plugin.getCommand("blacklisttradeslot").setExecutor(this);
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
		if (label.equalsIgnoreCase("blacklisttradeslot"))
		{
			if (sender.hasPermission("tradeplugin.use.blacklisttradeslot"))
			{
				if (args.length == 2)
				{
					if (args[0].equalsIgnoreCase("add"))
					{
						int slot = 0;
						try {
							slot = Integer.parseInt(args[1]);
						}
						catch (Exception e)
						{
							sender.sendMessage(ChatColor.RED + "That is not a valid slot!");
						}
						if (slot > 35 || slot < 0)
						{
							sender.sendMessage(ChatColor.RED + "That is not a valid slot!");
						}
						else
						{
							List<Integer> slots = config.contains("blacklistedtradeslots") ? config.getIntegerList("blacklistedtradeslots"): new ArrayList<Integer>();
							if (!slots.contains(slot))
							{
								slots.add(slot);
								config.set("blacklistedtradeslots", slots);
								sender.sendMessage(ChatColor.GREEN + "Added slot " + slot  + " to the blacklist.");
								plugin.saveConfig();
							}
							else
							{
								sender.sendMessage(ChatColor.RED + "This slot has already been blacklisted!");
							}
						}
					}
					else if (args[0].equalsIgnoreCase("remove"))
					{
						int slot = 0;
						try {
							slot = Integer.parseInt(args[1]);
						}
						catch (Exception e)
						{
							sender.sendMessage(ChatColor.RED + "That is not a valid slot!");
						}
						if (slot > 35 || slot < 0)
						{
							sender.sendMessage(ChatColor.RED + "That is not a valid slot!");
						}
						else
						{
							List<Integer> slots = config.contains("blacklistedtradeslots") ? config.getIntegerList("blacklistedtradeslots"): new ArrayList<Integer>();
							if (slots.contains(slot))
							{
								slots.remove(slot);
								config.set("blacklistedtradeslots", slots);
								sender.sendMessage(ChatColor.GREEN + "Removed slot " + slot  + " to the blacklist.");
								plugin.saveConfig();
							}
							else
							{
								sender.sendMessage(ChatColor.RED + "That slot is not currently on the blacklist!");
							}
						}
					}
					else
					{
						sender.sendMessage(ChatColor.RED + "Please use the valid command format \"/blacklisttradeslot (add/remove) (slot)\"!");
					}
				}
				else if (args.length == 1)
				{
					sender.sendMessage(ChatColor.RED + "Please use the valid command format \"/blacklisttradeslot (add/remove) (slot)\"!");
				}
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
			}
		}
		return true;
	}
}
