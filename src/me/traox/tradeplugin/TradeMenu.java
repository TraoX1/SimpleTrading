package me.traox.tradeplugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

public final class TradeMenu implements CommandExecutor, Listener {
	
	private static Events plugin;
	private FileConfiguration config = null;
	private static final HashMap<UUID, Inventory> inventories = new HashMap<>();
	
    public TradeMenu(Events plugin)
    {
        this.plugin = plugin;
        config = plugin.getConfig();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
	public boolean openTrade(Player player)
	{
		final UUID uuid = player.getUniqueId();
		
		inventories.put(uuid, Bukkit.createInventory(player, 54, "Trade"));
		
		ItemStack Blank = new ItemStack(Material.STAINED_GLASS_PANE,1 , (short) 15);
		ItemMeta BlankMeta = Blank.getItemMeta();
		BlankMeta.setDisplayName(" ");
		Blank.setItemMeta(BlankMeta);
		
		ItemStack AcceptTrade = new ItemStack(Material.EMERALD, 1);
		ItemMeta AcceptTradeMeta = AcceptTrade.getItemMeta();
		AcceptTradeMeta.setDisplayName(ChatColor.GREEN + "Accept Trade");
		List<String> AcceptTradeLore = new ArrayList<>();
		AcceptTradeLore.add(ChatColor.GRAY + "Accepts the trade.");
		AcceptTradeMeta.setLore(AcceptTradeLore);
		AcceptTrade.setItemMeta(AcceptTradeMeta);
		
		ItemStack Gold1 = new ItemStack(Material.GOLD_INGOT, 1);
		ItemMeta Gold1Meta = Gold1.getItemMeta();
		Gold1Meta.setDisplayName(ChatColor.YELLOW + "Gold: 0");
		List<String> Gold1Lore = new ArrayList<>();
		Gold1Lore.add(ChatColor.GRAY + "Click to add gold.");
		Gold1Meta.setLore(Gold1Lore);
		Gold1.setItemMeta(Gold1Meta);
		
		ItemStack Gold2 = new ItemStack(Material.GOLD_INGOT, 1);
		ItemMeta Gold2Meta = Gold2.getItemMeta();
		Gold2Meta.setDisplayName(ChatColor.YELLOW + "Gold: 0");
		List<String> Gold2Lore = new ArrayList<>();
		Gold2Lore.add(ChatColor.GRAY + "This is the gold the other");
		Gold2Lore.add(ChatColor.GRAY + "player is offering.");
		Gold2Meta.setLore(Gold2Lore);
		Gold2.setItemMeta(Gold2Meta);

		if (config.contains("TradeMenuItems"))
		{
			for (int i1 = 0; i1 < 54; i1++)
			{
				if (i1 % 9 == 4 || i1 >= 45)
				{
					inventories.get(uuid).setItem(i1, config.getItemStack("TradeMenuItems." + i1));
				}
			}
		}
		else
		{
			for (int i = 0; i <= 53; i++)
			{
				if (i % 9 == 4 || i > 44 && i != 45 && i != 48 && i != 50)
				{
					inventories.get(uuid).setItem(i, Blank);
				}
				if (i == 45)
				{
					inventories.get(uuid).setItem(i, Gold1);
				}
				if (i == 48)
				{
					inventories.get(uuid).setItem(i, AcceptTrade);
				}
				if (i == 50)
				{
					inventories.get(uuid).setItem(i, Gold2);
				}
			}
		}
		
		player.openInventory(inventories.get(uuid));
		return true;
	}
	
	public static Events getInstance() {
		return plugin;
	}


	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
