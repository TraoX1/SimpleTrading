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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

public final class EditTradeMenu implements CommandExecutor, Listener {
	
	private static Events plugin;
	private FileConfiguration config = null;
	private static final HashMap<UUID, Inventory> inventories = new HashMap<>();
	
    public EditTradeMenu(Events plugin)
    {
        this.plugin = plugin;
        config = plugin.getConfig();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
	public boolean editTrade(Player player)
	{
		final UUID uuid = player.getUniqueId();
		
		inventories.put(uuid, Bukkit.createInventory(player, 54, "Editing Trade"));
		
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
		Gold1Meta.setDisplayName(ChatColor.YELLOW + "Gold: ");
		List<String> Gold1Lore = new ArrayList<>();
		Gold1Lore.add(ChatColor.GRAY + "Click to add gold.");
		Gold1Meta.setLore(Gold1Lore);
		Gold1.setItemMeta(Gold1Meta);
		
		ItemStack Gold2 = new ItemStack(Material.GOLD_INGOT, 1);
		ItemMeta Gold2Meta = Gold2.getItemMeta();
		Gold2Meta.setDisplayName(ChatColor.YELLOW + "Gold: ");
		List<String> Gold2Lore = new ArrayList<>();
		Gold2Lore.add(ChatColor.GRAY + "This is the gold the other");
		Gold2Lore.add(ChatColor.GRAY + "player is offering.");
		Gold2Meta.setLore(Gold2Lore);
		Gold2.setItemMeta(Gold2Meta);
		
		ItemStack Gold3 = new ItemStack(Material.GOLD_INGOT, 1);
		ItemMeta Gold3Meta = Gold3.getItemMeta();
		Gold3Meta.setDisplayName(ChatColor.YELLOW + "Gold: ");
		List<String> Gold3Lore = new ArrayList<>();
		Gold3Meta.addEnchant(Enchantment.getById(0), 0, false);
		Gold3Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		Gold3Lore.add(ChatColor.GRAY + "Click to add gold.");
		Gold3Meta.setLore(Gold3Lore);
		Gold3.setItemMeta(Gold3Meta);
		
		ItemStack Gold4 = new ItemStack(Material.GOLD_INGOT, 1);
		ItemMeta Gold4Meta = Gold4.getItemMeta();
		Gold4Meta.setDisplayName(ChatColor.YELLOW + "Gold: ");
		List<String> Gold4Lore = new ArrayList<>();
		Gold4Meta.addEnchant(Enchantment.getById(0), 0, false);
		Gold4Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		Gold4Lore.add(ChatColor.GRAY + "This is the gold the other");
		Gold4Lore.add(ChatColor.GRAY + "player is offering.");
		Gold4Meta.setLore(Gold4Lore);
		Gold4.setItemMeta(Gold4Meta);
		
		ItemStack TradeSlot = new ItemStack(Material.BARRIER, 1);
		ItemMeta TradeSlotMeta = TradeSlot.getItemMeta();
		TradeSlotMeta.setDisplayName(ChatColor.RED + "Trade Slot");
		List<String> TradeSlotLore = new ArrayList<>();
		TradeSlotLore.add(ChatColor.GRAY + "This is where the player's items");
		TradeSlotLore.add(ChatColor.GRAY + "they are trading will go.");
		TradeSlotMeta.setLore(TradeSlotLore);
		TradeSlot.setItemMeta(TradeSlotMeta);

		ItemStack OtherAcceptedTrade = new ItemStack(Material.STAINED_CLAY, 1, (short)13);
		ItemMeta OtherAcceptedTradeMeta = OtherAcceptedTrade.getItemMeta();
		OtherAcceptedTradeMeta.setDisplayName(ChatColor.YELLOW + "Other Player Accepted");
		List<String> OtherAcceptedTradeLore = new ArrayList<>();
		OtherAcceptedTradeLore.add(ChatColor.GRAY + "Other player has accepted");
		OtherAcceptedTradeLore.add(ChatColor.GRAY + "the trade, click here to");
		OtherAcceptedTradeLore.add(ChatColor.GRAY + "accept the trade.");
		OtherAcceptedTradeMeta.setLore(OtherAcceptedTradeLore);
		OtherAcceptedTrade.setItemMeta(OtherAcceptedTradeMeta);
		
		
		ItemStack AcceptedTrade = new ItemStack(Material.STAINED_CLAY, 1, (short)5);
		ItemMeta AcceptedTradeMeta = AcceptedTrade.getItemMeta();
		AcceptedTradeMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Accepted Trade");
		List<String> AcceptedTradeLore = new ArrayList<>();
		AcceptedTradeLore.add(ChatColor.GRAY + "You accepted the trade. Waiting");
		AcceptedTradeLore.add(ChatColor.GRAY + "for the other player to accept.");
		AcceptedTradeLore.add(ChatColor.GRAY + "Click again to unaccept the trade.");
		AcceptedTradeMeta.addEnchant(Enchantment.getById(0), 0, false);
		AcceptedTradeMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		AcceptedTradeMeta.setLore(AcceptedTradeLore);
		AcceptedTrade.setItemMeta(AcceptedTradeMeta);
		
		
		if (config.contains("TradeMenuItems"))
		{
			for (int i1 = 0; i1 < 54; i1++)
			{
				if (i1 % 9 == 4 || i1 >= 45)
				{
					ItemStack item = config.getItemStack("TradeMenuItems." + i1);
					inventories.get(uuid).setItem(i1, item);
				}
				else if (i1 != 36 && i1 != 39 && i1 != 41 && i1 != 30)
				{
					inventories.get(uuid).setItem(i1, TradeSlot);
				}
				else if (i1 == 36)
				{
					ItemStack item = config.getItemStack("TradeMenuItems." + i1);
					inventories.get(uuid).setItem(i1, item);
				}
				else if (i1 == 39)
				{
					ItemStack item = config.getItemStack("TradeMenuItems." + i1);
					inventories.get(uuid).setItem(i1, item);
				}
				else if (i1 == 41)
				{
					ItemStack item = config.getItemStack("TradeMenuItems." + i1);
					inventories.get(uuid).setItem(i1, item);
				}
				else if (i1 == 30)
				{
					ItemStack item = config.getItemStack("TradeMenuItems." + i1);
					inventories.get(uuid).setItem(i1, item);
				}
			}
		}
		else
		{
			for (int i = 0; i <= 53; i++)
			{
				if (i % 9 != 4 && i < 45 && i != 36 && i != 39 && i != 41 && i != 30)
				{
					inventories.get(uuid).setItem(i, TradeSlot);
				}
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
				if (i == 36)
				{
					inventories.get(uuid).setItem(i, Gold3);
				}
				if (i == 39)
				{
					inventories.get(uuid).setItem(i, AcceptedTrade);
				}
				if (i == 41)
				{
					inventories.get(uuid).setItem(i, Gold4);
				}
				if (i == 30)
				{
					inventories.get(uuid).setItem(i, OtherAcceptedTrade);
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
