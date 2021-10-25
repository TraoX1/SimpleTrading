package me.traox.tradeplugin;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;

public final class TradeHandler implements CommandExecutor, Listener {
	
	private static Events plugin;
	private FileConfiguration config = null;
	TradeMenu trade;
	
	SignGui signGui;
	
	HashMap<UUID, UUID> tradeReqs = new HashMap<>();
	
	HashMap<UUID, UUID> traders = new HashMap<>();
	
	HashMap<UUID, List<ItemStack>> offeredItems = new HashMap<>();

	HashMap<UUID, Boolean> accepted = new HashMap<>();
	
	HashMap<UUID, Double> offeredCoins = new HashMap<>();
	
	HashMap<UUID, Long> clicks = new HashMap<>();

    public static Economy economy = null;
    
    private static final NumberFormat numberFormat;
    
    static {
        numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(true);
    }
    
    public TradeHandler(Events plugin)
    {
        this.plugin = plugin;
        RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        config = plugin.getConfig();
        signGui = new SignGui(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        trade = new TradeMenu(plugin);
        plugin.getCommand("trade").setExecutor(this);
    }

	public String getMessage(String message, String[] args)
	{
		String newMessage = message;
		newMessage = newMessage.replaceAll("%BLACK%", ChatColor.BLACK.toString());
		newMessage = newMessage.replaceAll("%DARKBLUE%", ChatColor.DARK_BLUE.toString());
		newMessage = newMessage.replaceAll("%DARKGREEN%", ChatColor.DARK_GREEN.toString());
		newMessage = newMessage.replaceAll("%DARKAQUA%", ChatColor.DARK_AQUA.toString());
		newMessage = newMessage.replaceAll("%DARKRED%", ChatColor.DARK_RED.toString());
		newMessage = newMessage.replaceAll("%DARKPURPLE%", ChatColor.DARK_PURPLE.toString());
		newMessage = newMessage.replaceAll("%GOLD%", ChatColor.GOLD.toString());
		newMessage = newMessage.replaceAll("%GRAY%", ChatColor.GRAY.toString());
		newMessage = newMessage.replaceAll("%DARKGRAY%", ChatColor.DARK_GRAY.toString());
		newMessage = newMessage.replaceAll("%BLUE%", ChatColor.BLUE.toString());
		newMessage = newMessage.replaceAll("%BLUE%", ChatColor.BLUE.toString());
		newMessage = newMessage.replaceAll("%GREEN%", ChatColor.GREEN.toString());
		newMessage = newMessage.replaceAll("%AQUA%", ChatColor.AQUA.toString());
		newMessage = newMessage.replaceAll("%RED%", ChatColor.RED.toString());
		newMessage = newMessage.replaceAll("%LIGHTPURPLE%", ChatColor.LIGHT_PURPLE.toString());
		newMessage = newMessage.replaceAll("%YELLOW%", ChatColor.YELLOW.toString());
		newMessage = newMessage.replaceAll("%WHITE%", ChatColor.WHITE.toString());
		newMessage = newMessage.replaceAll("%BOLD%", ChatColor.BOLD.toString());
		newMessage = newMessage.replaceAll("%ITALIC%", ChatColor.ITALIC.toString());
		newMessage = newMessage.replaceAll("%UNDERLINE%", ChatColor.UNDERLINE.toString());
		newMessage = newMessage.replaceAll("%STRIKETHROUGH%", ChatColor.STRIKETHROUGH.toString());
		newMessage = newMessage.replaceAll("%MAGIC%", ChatColor.MAGIC.toString());
		newMessage = newMessage.replaceAll("%PLAYER%", args[0]);
		
		return newMessage;
	}
	
	public BaseComponent getMessageComponent(String message, String[] args)
	{
		String newMessage = getMessage(message, args);
		TextComponent clickMessage = new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + "Click Here");
		clickMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, args[1]));
		TextComponent part1 = new TextComponent(newMessage.split("%CLICKHERE%")[0]);
		TextComponent part2 = new TextComponent(newMessage.split("%CLICKHERE%")[1]);
		part1.addExtra(clickMessage);
		part1.addExtra(part2);
		BaseComponent base = part1;
		return base;
	}
    
    public int getTradeSlot(int slot)
    {
    	if (slot % 9 < 4 && slot < 44)
    	{
    		if (slot < 9)
    		{
    			return slot;
    		}
    		else if (slot >= 9 && slot < 17)
    		{
    			return slot - 5;
    		}
    		else if (slot >= 17 && slot < 26)
    		{
    			return slot - 10;
    		}
    		else if (slot >= 26 && slot < 35)
    		{
    			return slot - 15;
    		}
    		else if (slot >= 35 && slot < 44)
    		{
    			return slot - 20;
    		}
    	}
    	return -1;
    }
    
    public int indexToTradeSlot(int i)
    {
    	if (i < 4)
    	{
    		return i;
    	}
    	else if (i >= 4 && i < 8)
    	{
    		return i + 5;
    	}
    	else if (i >= 8 && i < 12)
    	{
    		return i + 10;
    	}
    	else if (i >= 12 && i < 16)
    	{
    		return i + 15;
    	}
    	else if (i >= 16)
    	{
    		return i + 20;
    	}
    	return 0;
    }
    
    
    public void cancelTrade(Player player, Player player2, boolean first)
    {
    	boolean player1items = false;
    	
    	if (offeredItems.get(player.getUniqueId()) != null)
    	{
			for (ItemStack item : offeredItems.get(player.getUniqueId()))
			{
				player.getInventory().addItem(item);
			}
			player1items = true;
    	}
    	Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
    	    @Override
    	    public void run() {
    	    	if (first && player2.getOpenInventory() != null)
    	    	{
    	    		if (player2.getOpenInventory().getTitle().equalsIgnoreCase("trade"))
    	    		{
    	    			player2.closeInventory();
    	    			player.sendMessage(ChatColor.RED + "The trade was cancelled.");
    	    			player2.sendMessage(ChatColor.RED + "The trade was cancelled.");
    	    		}
    	    	}
    	    }
    	}, 1L);
		
		if (player.getOpenInventory().getTitle().equalsIgnoreCase("trade") && !player1items)
		{
			for (ItemStack item : offeredItems.get(player.getUniqueId()))
			{
				player.getInventory().addItem(item);
			}
		}
		
		if (offeredItems.get(player2.getUniqueId()) != null)
		{
			for (ItemStack item : offeredItems.get(player2.getUniqueId()))
			{
				player2.getInventory().addItem(item);
			}
		}

		if (offeredCoins.get(player.getUniqueId()) != null)
		{
			economy.depositPlayer(player, offeredCoins.get(player.getUniqueId()));
			economy.depositPlayer(player2, offeredCoins.get(player2.getUniqueId()));
		}
		
		offeredCoins.remove(player.getUniqueId());
		offeredCoins.remove(player2.getUniqueId());
		
		offeredItems.remove(player.getUniqueId());
		traders.remove(traders.get(player.getUniqueId()));
		traders.remove(player.getUniqueId());
    }
    
    
    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent e)
    {
    	if (traders.containsKey( e.getPlayer().getUniqueId()))
    	{
    		cancelTrade((Player)e.getPlayer(), Bukkit.getPlayer(traders.get(e.getPlayer().getUniqueId())), true);
    	}
    }
    
    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent e)
    {
    	if (e.getInventory().getName().equalsIgnoreCase("trade") && traders.get(e.getPlayer().getUniqueId()) != null)
    	{
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
        	    @Override
        	    public void run() {
        	    	if (traders.get(e.getPlayer().getUniqueId()) != null)
        	    	{
        	    		if (e.getPlayer().getOpenInventory() != null)
        	    		{
        	    			if (!e.getPlayer().getOpenInventory().getTitle().equalsIgnoreCase("trade"))
        	    			{
    		        	    	if (Bukkit.getPlayer(traders.get(e.getPlayer().getUniqueId())).getOpenInventory() != null)
    		        	    	{
    		        	    		cancelTrade((Player)e.getPlayer(), Bukkit.getPlayer(traders.get(e.getPlayer().getUniqueId())), true);
    		        	    	}
        	    			}
        	    		}
        	    	}
        	    }
        	}, 1L);
    	}
    }
    
    public void updateTradeMenu(Player player, Player player2, List<ItemStack> items)
    {
    	player2 = Bukkit.getPlayer(traders.get(player.getUniqueId()));
		if (player.getOpenInventory().getTitle() != null && player2.getOpenInventory().getTitle() != null)
		{
			if (player.getOpenInventory().getTitle().equalsIgnoreCase("trade") && player2.getOpenInventory().getTitle().equalsIgnoreCase("trade"))
			{
				accepted.put(player.getUniqueId(), false);
				accepted.put(player2.getUniqueId(), false);
		    	int i = 0;
		    	for (int i2 = 0; i2 <= 44; i2++)
		    	{
		    		if (getTradeSlot(i2) != -1)
		    		{
		    			player.getOpenInventory().setItem(i2, new ItemStack(Material.AIR));
		    		}
		    	}
		    	for (int i2 = 0; i2 <= 44; i2++)
		    	{
		    		if (getTradeSlot(i2) != -1)
		    		{
		    			player2.getOpenInventory().setItem(i2 + 5, new ItemStack(Material.AIR));
		    		}
		    	}
		    	for (ItemStack item : items)
		    	{
		    		player.getOpenInventory().setItem(indexToTradeSlot(i), item);
		    		i++;
		    	}
		    	
		    	int i2 = 0;
		    	for (ItemStack item : items)
		    	{
		    		player2.getOpenInventory().setItem(indexToTradeSlot(i2) + 5, item);
		    		i2++;
		    	}
		    	
				
				if (!config.contains("TradeMenuItems"))
				{
					
					
					ItemStack Gold1 = new ItemStack(Material.GOLD_INGOT, 1);
					ItemMeta Gold1Meta = Gold1.getItemMeta();
					try
					{
						Gold1Meta.setDisplayName(ChatColor.YELLOW + "Gold: " + numberFormat.format(offeredCoins.get(player.getUniqueId())));
					}
					catch (Exception e)
					{
						Gold1Meta.setDisplayName(ChatColor.YELLOW + "Gold: 0");
					}
					List<String> Gold1Lore = new ArrayList<>();
					Gold1Lore.add(ChatColor.GRAY + "Click to add gold.");
					if (offeredCoins.containsKey(player.getUniqueId()))
					{
						if (offeredCoins.get(player.getUniqueId()) > 0)
						{
							Gold1Meta.addEnchant(Enchantment.getById(1), 0, false);
							Gold1Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
						}
					}
					Gold1Meta.setLore(Gold1Lore);
					Gold1.setItemMeta(Gold1Meta);
					
					ItemStack Gold2 = new ItemStack(Material.GOLD_INGOT, 1);
					ItemMeta Gold2Meta = Gold2.getItemMeta();
					try
					{
						Gold2Meta.setDisplayName(ChatColor.YELLOW + "Gold: " + numberFormat.format(offeredCoins.get(player.getUniqueId())));
					}
					catch (Exception e)
					{
						Gold2Meta.setDisplayName(ChatColor.YELLOW + "Gold: 0");
					}
					List<String> Gold2Lore = new ArrayList<>();
					Gold2Lore.add(ChatColor.GRAY + "This is the gold the other");
					Gold2Lore.add(ChatColor.GRAY + "player is offering.");
					if (offeredCoins.containsKey(player.getUniqueId()))
					{
						if (offeredCoins.get(player.getUniqueId()) > 0)
						{
							Gold2Meta.addEnchant(Enchantment.getById(1), 0, false);
							Gold2Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
						}
					}
					Gold2Meta.setLore(Gold2Lore);
					Gold2.setItemMeta(Gold2Meta);
			    	
					ItemStack AcceptTrade = new ItemStack(Material.EMERALD, 1);
					ItemMeta AcceptTradeMeta = AcceptTrade.getItemMeta();
					AcceptTradeMeta.setDisplayName(ChatColor.GREEN + "Accept Trade");
					List<String> AcceptTradeLore = new ArrayList<>();
					AcceptTradeLore.add(ChatColor.GRAY + "Accepts the trade.");
					AcceptTradeMeta.setLore(AcceptTradeLore);
					AcceptTrade.setItemMeta(AcceptTradeMeta);
					
					
					player.getOpenInventory().setItem(45, Gold1);
					player2.getOpenInventory().setItem(50, Gold2);
				
					player.getOpenInventory().setItem(48, AcceptTrade);
					player2.getOpenInventory().setItem(48, AcceptTrade);
				}
				else
				{
					
					player.getOpenInventory().setItem(48, config.getItemStack("TradeMenuItems.48").clone());
					player2.getOpenInventory().setItem(48, config.getItemStack("TradeMenuItems.48").clone());
					
					ItemStack gold1 = new ItemStack(Material.AIR);
					ItemStack gold2 = new ItemStack(Material.AIR);
					ItemStack gold3 = new ItemStack(Material.AIR);
					ItemStack gold4 = new ItemStack(Material.AIR);
					if (offeredCoins.containsKey(player.getUniqueId()))
					{
						if (offeredCoins.get(player.getUniqueId()) > 0)
						{
							gold1 = config.getItemStack("TradeMenuItems.36").clone();
							ItemMeta gold1Meta = gold1.getItemMeta();
							if (!gold1Meta.hasDisplayName())
							{
								gold1Meta.setDisplayName(gold1.getType().toString().replace('_', ' ').toLowerCase());
							}
							gold1Meta.setDisplayName(gold1.getItemMeta().getDisplayName() +  numberFormat.format(offeredCoins.get(player.getUniqueId())));
							gold1.setItemMeta(gold1Meta);
							gold2 = config.getItemStack("TradeMenuItems.41").clone();
							ItemMeta gold2Meta = gold1.getItemMeta();
							if (!gold2Meta.hasDisplayName())
							{
								gold2Meta.setDisplayName(gold2.getType().toString().replace('_', ' ').toLowerCase());
							}
							gold2Meta.setDisplayName(gold2.getItemMeta().getDisplayName() +  numberFormat.format(offeredCoins.get(player.getUniqueId())));
							gold2.setItemMeta(gold2Meta);
						}
						else
						{
							gold1 = config.getItemStack("TradeMenuItems.45").clone();
							ItemMeta gold1Meta = gold1.getItemMeta();
							if (!gold1Meta.hasDisplayName())
							{
								gold1Meta.setDisplayName(gold1.getType().toString().replace('_', ' ').toLowerCase());
							}
							gold1Meta.setDisplayName(gold1.getItemMeta().getDisplayName() + 0);
							gold1.setItemMeta(gold1Meta);
							gold2 = config.getItemStack("TradeMenuItems.50").clone();
							ItemMeta gold2Meta = gold2.getItemMeta();
							if (!gold2Meta.hasDisplayName())
							{
								gold2Meta.setDisplayName(gold2.getType().toString().replace('_', ' ').toLowerCase());
							}
							gold2Meta.setDisplayName(gold2.getItemMeta().getDisplayName() + 0);
							gold2.setItemMeta(gold2Meta);
						}
					}
					else
					{
						gold1 = config.getItemStack("TradeMenuItems.45").clone();
						ItemMeta gold1Meta = gold1.getItemMeta();
						if (!gold1Meta.hasDisplayName())
						{
							gold1Meta.setDisplayName(gold1.getType().toString().replace('_', ' ').toLowerCase());
						}
						gold1Meta.setDisplayName(gold1.getItemMeta().getDisplayName() + 0);
						gold1.setItemMeta(gold1Meta);
						gold2 = config.getItemStack("TradeMenuItems.50").clone();
						ItemMeta gold2Meta = gold2.getItemMeta();
						if (!gold2Meta.hasDisplayName())
						{
							gold2Meta.setDisplayName(gold2.getType().toString().replace('_', ' ').toLowerCase());
						}
						gold2Meta.setDisplayName(gold2.getItemMeta().getDisplayName() + 0);
						gold2.setItemMeta(gold2Meta);
					}
					
					player.getOpenInventory().setItem(45, gold1);
					player2.getOpenInventory().setItem(50, gold2);
					
					if (offeredCoins.containsKey(player2.getUniqueId()))
					{
						if (offeredCoins.get(player2.getUniqueId()) > 0)
						{
							gold3 = config.getItemStack("TradeMenuItems.36").clone();
							ItemMeta gold3Meta = gold3.getItemMeta();
							if (!gold3Meta.hasDisplayName())
							{
								gold3Meta.setDisplayName(gold3.getType().toString().replace('_', ' ').toLowerCase());
							}
							gold3Meta.setDisplayName(gold3.getItemMeta().getDisplayName() +  numberFormat.format(offeredCoins.get(player2.getUniqueId())));
							gold3.setItemMeta(gold3Meta);
							gold4 = config.getItemStack("TradeMenuItems.41").clone();
							ItemMeta gold4Meta = gold3.getItemMeta();
							if (!gold4Meta.hasDisplayName())
							{
								gold4Meta.setDisplayName(gold4.getType().toString().replace('_', ' ').toLowerCase());
							}
							gold4Meta.setDisplayName(gold4.getItemMeta().getDisplayName() +  numberFormat.format(offeredCoins.get(player2.getUniqueId())));
							gold4.setItemMeta(gold4Meta);
						}
						else
						{
							gold3 = config.getItemStack("TradeMenuItems.45").clone();
							ItemMeta gold3Meta = gold3.getItemMeta();
							if (!gold3Meta.hasDisplayName())
							{
								gold3Meta.setDisplayName(gold3.getType().toString().replace('_', ' ').toLowerCase());
							}
							gold3Meta.setDisplayName(gold3.getItemMeta().getDisplayName() + 0);
							gold3.setItemMeta(gold3Meta);
							gold4 = config.getItemStack("TradeMenuItems.50").clone();
							ItemMeta gold4Meta = gold4.getItemMeta();
							if (!gold4Meta.hasDisplayName())
							{
								gold4Meta.setDisplayName(gold4.getType().toString().replace('_', ' ').toLowerCase());
							}
							gold4Meta.setDisplayName(gold4.getItemMeta().getDisplayName() + 0);
							gold4.setItemMeta(gold4Meta);
						}
					}
					else
					{
						gold3 = config.getItemStack("TradeMenuItems.45").clone();
						ItemMeta gold3Meta = gold3.getItemMeta();
						if (!gold3Meta.hasDisplayName())
						{
							gold3Meta.setDisplayName(gold3.getType().toString().replace('_', ' ').toLowerCase());
						}
						gold3Meta.setDisplayName(gold3.getItemMeta().getDisplayName() + 0);
						gold3.setItemMeta(gold3Meta);
						gold4 = config.getItemStack("TradeMenuItems.50").clone();
						ItemMeta gold4Meta = gold4.getItemMeta();
						if (!gold4Meta.hasDisplayName())
						{
							gold4Meta.setDisplayName(gold4.getType().toString().replace('_', ' ').toLowerCase());
						}
						gold4Meta.setDisplayName(gold4.getItemMeta().getDisplayName() + 0);
						gold4.setItemMeta(gold4Meta);
					}
					
					player2.getOpenInventory().setItem(45, gold3);
					player.getOpenInventory().setItem(50, gold4);
				}
	    	}
    	}
    }
    
    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent e)
    {
    	if (e.getClickedInventory() != null)
    	{
			List<ItemStack> items = new ArrayList<>();
			items = offeredItems.get(e.getWhoClicked().getUniqueId());
    		if (e.getClickedInventory().getName().equalsIgnoreCase("trade"))
    		{
    			if (traders.containsKey(e.getWhoClicked().getUniqueId()))
    			{
	    			boolean fullInv = true;
	    			for (ItemStack item : e.getWhoClicked().getInventory())
	    			{
	    				if (item == null)
	    				{
	    					fullInv = false;
	    					break;
	    				}
	    			}
	    			e.setCancelled(true);
	    			if (getTradeSlot(e.getSlot()) != -1 && e.getClickedInventory().getItem(e.getSlot()) != null && !fullInv && items.size() > 0)
	    			{
	    				
						((Player)e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.ITEM_PICKUP, 1, 1);
						Bukkit.getPlayer(traders.get(e.getWhoClicked().getUniqueId())).playSound(Bukkit.getPlayer(traders.get(e.getWhoClicked().getUniqueId())).getLocation(), Sound.ITEM_PICKUP, 1, 1);
	    				
	    				items.remove(getTradeSlot(e.getSlot()));
	    				offeredItems.replace(e.getWhoClicked().getUniqueId(), items);
	    				e.getWhoClicked().getInventory().addItem(e.getClickedInventory().getItem(e.getSlot()));
	    				updateTradeMenu((Player)e.getWhoClicked(), Bukkit.getPlayer(tradeReqs.get(((Player)e.getWhoClicked()).getUniqueId())), items);
	    			}
	
	    			ItemStack AcceptTrade = new ItemStack(Material.EMERALD, 1);
	    			ItemMeta AcceptTradeMeta = AcceptTrade.getItemMeta();
	    			AcceptTradeMeta.setDisplayName(ChatColor.GREEN + "Accept Trade");
	    			List<String> AcceptTradeLore = new ArrayList<>();
	    			AcceptTradeLore.add(ChatColor.GRAY + "Accepts the trade.");
	    			AcceptTradeMeta.setLore(AcceptTradeLore);
	    			AcceptTrade.setItemMeta(AcceptTradeMeta);
	    			
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
	    			AcceptedTradeMeta.addEnchant(Enchantment.getById(1), 0, false);
	    			AcceptedTradeMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
	    			AcceptedTradeMeta.setLore(AcceptedTradeLore);
	    			AcceptedTrade.setItemMeta(AcceptedTradeMeta);
	    			
	    			
	    			
	    			if (e.getSlot() == 45)
	    			{
	    				
	    				if (!clicks.containsKey(e.getWhoClicked().getUniqueId()))
	    				{
	    					clicks.put(e.getWhoClicked().getUniqueId(), System.currentTimeMillis());
	    				}
	    				
	    				if ((System.currentTimeMillis() - clicks.get(e.getWhoClicked().getUniqueId())) == 0 || (System.currentTimeMillis() - clicks.get(e.getWhoClicked().getUniqueId())) > 8)
	    				{
	    					clicks.put(e.getWhoClicked().getUniqueId(), System.currentTimeMillis());
		    				signGui.open((Player)e.getWhoClicked(), new String[] { "", "^^^^^^^^^^^^^^", "Type the amount", "of coins to add." }, new SignGui.SignGUIListener() {
		    		            @Override
		    		            public void onSignDone(Player player, String[] lines) {
		    		            	double coins = 0;
		    		            	if (traders.containsKey(player.getUniqueId()))
		    						{
		    							if (Bukkit.getPlayer(traders.get(player.getUniqueId())).getOpenInventory() != null)
		    							{
		    								if (Bukkit.getPlayer(traders.get(player.getUniqueId())).getOpenInventory().getTitle().equalsIgnoreCase("trade"))
		    								{
		    									try
		    		    		            	{
		    		    		            		coins = Math.round(Double.parseDouble(lines[0].replace('\"', ' ')));
		    		    		            	}
		    		    		            	catch (Exception e)
		    		    		            	{
		    		    		            		player.sendMessage(ChatColor.RED + "Unable to read number!");
		    		    		            	}
		    		    		            	if (coins >= 0)
		    		    		            	{
		    		    		            		if (economy.getBalance(player) >= coins)
		    		    		            		{
		    		    		            			economy.depositPlayer(player, offeredCoins.get(player.getUniqueId()));
		    			    		            		economy.withdrawPlayer(player, coins);
		    			    		            		offeredCoins.put(player.getUniqueId(), coins);
		    		    		            		}
		    		    		            		else
		    		    		            		{
		    		    		            			player.sendMessage(ChatColor.RED + "You do not have enough coins!");
		    		    		            		}
		    		    		            	}
		    		    		            	else
		    		    		            	{
		    		    		            		player.sendMessage(ChatColor.RED + "You must type a postive number!");
		    		    		            	}
		    		    		            	trade.openTrade(player);
		    	    		            		updateTradeMenu(player, Bukkit.getPlayer(traders.get(player.getUniqueId())), offeredItems.get(player.getUniqueId()));
		    	    		            		updateTradeMenu(Bukkit.getPlayer(traders.get(player.getUniqueId())), player, offeredItems.get(traders.get(player.getUniqueId())));
		    	    		            		player.updateInventory();
		    	    		            		Bukkit.getPlayer(traders.get(player.getUniqueId())).updateInventory();
		    								}
		    							}
		    						}
		    		            }
		    		        });
	    				}
	    			}
	    			
	    			if (e.getSlot() == 48)
	    			{
	    				if (!accepted.get(e.getWhoClicked().getUniqueId()) && accepted.get(traders.get(e.getWhoClicked().getUniqueId())))
	    				{
	    					int freeSlots = 0;
	    					for (ItemStack item : e.getWhoClicked().getInventory())
	    					{
	    						if (item == null)
	    						{
	    							freeSlots++;
	    						}
	    					}
	    					if (freeSlots >= offeredItems.get(traders.get(e.getWhoClicked().getUniqueId())).size())
	    					{
		    		        	Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
		    		        	    @Override
		    		        	    public void run() {
		    	    					Player player = (Player) e.getWhoClicked();
		    	    					Player player2 = Bukkit.getPlayer(traders.get(player.getUniqueId()));
		    	    					
		    	    					player.sendMessage(ChatColor.GREEN + "Trade completed with " + player2.getName() + ".");
		    	    					
		    	    					player2.sendMessage(ChatColor.GREEN + "Trade completed with " + player.getName() + ".");
		    	    					
		    	    					if (offeredItems.containsKey(player2.getUniqueId()))
		    		        			{
			    	    					for (ItemStack item : offeredItems.get(player2.getUniqueId()))
			    	    					{
			    	    						player.getInventory().addItem(item);
			    	    						if (item.hasItemMeta())
			    	    						{
			    	    							if (item.getItemMeta().hasDisplayName())
			    	    							{
			    	    								player.sendMessage(ChatColor.DARK_GRAY + " + " + item.getItemMeta().getDisplayName() + ChatColor.DARK_GRAY + " x " + item.getAmount());
			    	    								player2.sendMessage(ChatColor.DARK_GRAY + " - " + item.getItemMeta().getDisplayName() + ChatColor.DARK_GRAY + " x " + item.getAmount());
			    	    							}
			    	    							else
			    	    							{
			    	    								player.sendMessage(ChatColor.DARK_GRAY + " + " + ChatColor.WHITE + item.getType().toString().replace('_', ' ').toLowerCase() + ChatColor.DARK_GRAY + " x " + item.getAmount());
			    	    								player2.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.WHITE + item.getType().toString().replace('_', ' ').toLowerCase() + ChatColor.DARK_GRAY + " x " + item.getAmount());
			    	    							}
			    	    						}
			    	    						else
			    	    						{
			    	    							player.sendMessage(ChatColor.DARK_GRAY + " + " + ChatColor.WHITE + item.getType().toString().replace('_', ' ').toLowerCase() + ChatColor.DARK_GRAY + " x " + item.getAmount());
			    	    							player2.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.WHITE + item.getType().toString().replace('_', ' ').toLowerCase() + ChatColor.DARK_GRAY + " x " + item.getAmount());
			    	    						}
			    	    					}
		    		        			}
		    	    					
		    		        			if (offeredItems.containsKey(player.getUniqueId()))
    		        					{
			    	    					for (ItemStack item : offeredItems.get(player.getUniqueId()))
			    	    					{
			    	    						player2.getInventory().addItem(item);
			    	    						if (item.hasItemMeta())
			    	    						{
			    	    							if (item.getItemMeta().hasDisplayName())
			    	    							{
			    	    								player2.sendMessage(ChatColor.DARK_GRAY + " + " + item.getItemMeta().getDisplayName() + ChatColor.DARK_GRAY + " x " + item.getAmount());
			    	    								player.sendMessage(ChatColor.DARK_GRAY + " - " + item.getItemMeta().getDisplayName() + ChatColor.DARK_GRAY + " x " + item.getAmount());
			    	    							}
			    	    							else
			    	    							{
			    	    								player2.sendMessage(ChatColor.DARK_GRAY + " + " + ChatColor.WHITE + item.getType().toString().replace('_', ' ').toLowerCase() + ChatColor.DARK_GRAY + " x " + item.getAmount());
			    	    								player.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.WHITE + item.getType().toString().replace('_', ' ').toLowerCase() + ChatColor.DARK_GRAY + " x " + item.getAmount());
			    	    							}
			    	    						}
			    	    						else
			    	    						{
			    	    							player2.sendMessage(ChatColor.DARK_GRAY + " + " + ChatColor.WHITE + item.getType().toString().replace('_', ' ').toLowerCase() + ChatColor.DARK_GRAY + " x " + item.getAmount());
			    	    							player.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.WHITE + item.getType().toString().replace('_', ' ').toLowerCase() + ChatColor.DARK_GRAY + " x " + item.getAmount());
			    	    						}
			    	    					}
    		        					}
		    	    					
    		        					if (offeredCoins.containsKey(player2.getUniqueId()))
    		        					{
    		        						if (offeredCoins.get(player2.getUniqueId()) > 0)
			    	    					{
    		        							player.sendMessage(ChatColor.DARK_GRAY + " + " + ChatColor.GOLD + numberFormat.format(offeredCoins.get(player2.getUniqueId())) + " coins");
    		        							player2.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + numberFormat.format(offeredCoins.get(player2.getUniqueId())) + " coins");
			    	    					}
    		        					}
		    	    					
    		        					if (offeredCoins.containsKey(player.getUniqueId()))
    		        					{
			    	    					if (offeredCoins.get(player.getUniqueId()) > 0)
			    	    					{
			    	    						player2.sendMessage(ChatColor.DARK_GRAY + " + " + ChatColor.GOLD + numberFormat.format(offeredCoins.get(player.getUniqueId())) + " coins");
			    	    						player.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + numberFormat.format(offeredCoins.get(player.getUniqueId())) + " coins");
			    	    					}
    		        					}
		    	    					
    		        					if (offeredCoins.containsKey(player.getUniqueId()))
    		        					{
    		        						economy.depositPlayer(player2, offeredCoins.get(player.getUniqueId()));
    		        						offeredCoins.remove(player.getUniqueId());
    		        					}
    		        					if (offeredCoins.containsKey(player2.getUniqueId()))
    		        					{
    		        						economy.depositPlayer(player, offeredCoins.get(player2.getUniqueId()));
    		        						offeredCoins.remove(player2.getUniqueId());
    		        					}
		    	    					offeredItems.remove(player.getUniqueId());
		    	    					offeredItems.remove(player2.getUniqueId());
		    	    					updateTradeMenu(player, player2, new ArrayList<>());
		    	    					updateTradeMenu(player2, player, new ArrayList<>());
		    	    					player.closeInventory();
		    	    					player2.closeInventory();
		    	    					
		    	    					player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
		    	    					
		    	    					player2.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
		    	    					
		    		        	    }
		    		        	}, 1L);
	    					}
	    					else
	    					{
	    						e.getWhoClicked().sendMessage(ChatColor.RED + "Your inventory does not have enough space!");
	    					}
	    					
	    				}
	    				else if (!accepted.get(e.getWhoClicked().getUniqueId()) && !accepted.get(traders.get(e.getWhoClicked().getUniqueId())))
	    				{
	    					if ((offeredItems.get(e.getWhoClicked().getUniqueId()).size() > 0 || offeredCoins.get(e.getWhoClicked().getUniqueId()) > 0) || (offeredItems.get(traders.get(e.getWhoClicked().getUniqueId())).size() > 0 || offeredCoins.get(traders.get(e.getWhoClicked().getUniqueId())) > 0))
	    					{
		    					int freeSlots = 0;
		    					for (ItemStack item : e.getWhoClicked().getInventory())
		    					{
		    						if (item == null)
		    						{
		    							freeSlots++;
		    						}
		    					}
		    					if (freeSlots >= offeredItems.get(traders.get(e.getWhoClicked().getUniqueId())).size())
		    					{
		    						accepted.put(e.getWhoClicked().getUniqueId(), true);
			    		        	Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			    		        	    @Override
			    		        	    public void run() {
			    	    					Player player = (Player) e.getWhoClicked();
			    	    					Player player2 = Bukkit.getPlayer(traders.get(player.getUniqueId()));
			    	    					
			    	    					if (!config.contains("TradeMenuItems"))
			    	    					{
			    	    						e.getClickedInventory().setItem(48, AcceptedTrade);
			    	    					}
			    	    					else
			    	    					{
			    	    						e.getClickedInventory().setItem(48, config.getItemStack("TradeMenuItems.39").clone());
			    	    					}
			    	    					
			    	    					if (player2.getOpenInventory() != null)
			    	    					{
			    	    						if (player2.getOpenInventory().getTitle().equalsIgnoreCase("trade"))
			    	    						{
					    	    					if (!config.contains("TradeMenuItems"))
					    	    					{
					    	    						player2.getOpenInventory().setItem(48, OtherAcceptedTrade);
					    	    					}
					    	    					else
					    	    					{
					    	    						player2.getOpenInventory().setItem(48, config.getItemStack("TradeMenuItems.30").clone());
					    	    					}
			    	    						}
			    	    					}
			    	    					
			    	    					player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
			    	    					
			    	    					player2.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
			    	    					
			    		        	    }
			    		        	}, 5L);
		    					}
		    					else
		    					{
		    						e.getWhoClicked().sendMessage(ChatColor.RED + "Your inventory does not have enough space!");
		    					}
	    					}
	    					else
	    					{
	    						e.getWhoClicked().sendMessage(ChatColor.RED + "There is nothing to trade!");
	    					}
	    				}
	    				else if (accepted.get(e.getWhoClicked().getUniqueId()) && !accepted.get(traders.get(e.getWhoClicked().getUniqueId())))
	    				{
	    					accepted.put(e.getWhoClicked().getUniqueId(), false);
	    		        	Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	    		        	    @Override
	    		        	    public void run() {
	    	    					Player player = (Player) e.getWhoClicked();
	    	    					Player player2 = Bukkit.getPlayer(traders.get(player.getUniqueId()));
	    	    					
	    	    					if (!config.contains("TradeMenuItems"))
	    	    					{
	    	    						e.getClickedInventory().setItem(48, AcceptTrade);
	    	    					}
	    	    					else
	    	    					{
	    	    						e.getClickedInventory().setItem(48, config.getItemStack("TradeMenuItems.48").clone());
	    	    					}
	    	    					
	    	    					if (player2.getOpenInventory() != null)
	    	    					{
	    	    						if (player2.getOpenInventory().getTitle().equalsIgnoreCase("trade"))
	    	    						{
	    	    							if (!config.contains("TradeMenuItems"))
			    	    					{
	    	    								player2.getOpenInventory().setItem(48, AcceptTrade);
			    	    					}
	    	    							else
	    	    							{
	    	    								player2.getOpenInventory().setItem(48, config.getItemStack("TradeMenuItems.48").clone());
	    	    							}
	    	    						}
	    	    					}
	    	    					
	    	    					player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
	    	    					
	    	    					player2.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
	    	    					
	    		        	    }
	    		        	}, 5L);
	    				}
	    			}
    			}
    		}
    		if (e.getWhoClicked().getOpenInventory() != null)
    		{
    			if (e.getWhoClicked().getOpenInventory().getTitle().equalsIgnoreCase("trade"))
    			{
    				e.setCancelled(true);
    				if (!clicks.containsKey(e.getWhoClicked().getUniqueId()))
    				{
    					clicks.put(e.getWhoClicked().getUniqueId(), System.currentTimeMillis());
    				}
    				if ((System.currentTimeMillis() - clicks.get(e.getWhoClicked().getUniqueId())) == 0 || (System.currentTimeMillis() - clicks.get(e.getWhoClicked().getUniqueId())) > 8)
    				{
    					clicks.put(e.getWhoClicked().getUniqueId(), System.currentTimeMillis());
	    				if (!e.getClickedInventory().getTitle().equalsIgnoreCase("trade") && items != null)
	    				{
	    					if (items.size() < 20)
	    					{
		    					if (e.getClickedInventory().getItem(e.getSlot()) != null && e.getClick() != ClickType.MIDDLE && e.getClick() != ClickType.NUMBER_KEY)
		    					{
		    						if (traders.containsKey(e.getWhoClicked().getUniqueId()))
		    						{
			    						if (Bukkit.getPlayer(traders.get(e.getWhoClicked().getUniqueId())).getOpenInventory() != null)
			    						{
				    						if (Bukkit.getPlayer(traders.get(e.getWhoClicked().getUniqueId())).getOpenInventory().getTitle().equalsIgnoreCase("trade"))
				    						{
				    							if (config.contains("blacklistedtradeslots"))
				    							{
				    								if (config.getIntegerList("blacklistedtradeslots").contains(e.getSlot()))
				    								{
				    									e.getWhoClicked().sendMessage(ChatColor.RED + "You cannot trade this item!");
				    								}
				    								else
				    								{
				    									((Player)e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.ITEM_PICKUP, 1, 1);
							        					Bukkit.getPlayer(traders.get(e.getWhoClicked().getUniqueId())).playSound(Bukkit.getPlayer(traders.get(e.getWhoClicked().getUniqueId())).getLocation(), Sound.ITEM_PICKUP, 1, 1);
							    						
							    						items.add(e.getClickedInventory().getItem(e.getSlot()));
							    						offeredItems.replace(e.getWhoClicked().getUniqueId(), items);
							    						e.getWhoClicked().getInventory().setItem(e.getSlot(), new ItemStack(Material.AIR));
							    						updateTradeMenu((Player)e.getWhoClicked(), Bukkit.getPlayer(tradeReqs.get(((Player)e.getWhoClicked()).getUniqueId())), items);
				    								}
				    							}
				    							else
				    							{
						    						((Player)e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.ITEM_PICKUP, 1, 1);
						        					Bukkit.getPlayer(traders.get(e.getWhoClicked().getUniqueId())).playSound(Bukkit.getPlayer(traders.get(e.getWhoClicked().getUniqueId())).getLocation(), Sound.ITEM_PICKUP, 1, 1);
						    						
						    						items.add(e.getClickedInventory().getItem(e.getSlot()));
						    						offeredItems.replace(e.getWhoClicked().getUniqueId(), items);
						    						e.getWhoClicked().getInventory().setItem(e.getSlot(), new ItemStack(Material.AIR));
						    						updateTradeMenu((Player)e.getWhoClicked(), Bukkit.getPlayer(tradeReqs.get(((Player)e.getWhoClicked()).getUniqueId())), items);
				    							}
				    						}
			    						}
		    						}
		    					}
	    					}
	    					else
	    					{
	    						e.getWhoClicked().sendMessage(ChatColor.RED + "The trade menu is currently full!");
	    					}
	    				}
    				}
    			}
    		}
    	}
    }
    
    
    
    
    
    
    
    
    

    
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("trade"))
		{
			if (!config.getBoolean("TradingToggled"))
			{
				if (args.length > 0)
				{
					if (Bukkit.getPlayer(args[0]) != null)
					{
						if (!traders.containsKey(((Player)sender).getUniqueId()))
						{
							if (Bukkit.getPlayer(args[0]).getUniqueId() != ((Player)sender).getUniqueId())
							{
								Player player = Bukkit.getPlayer(args[0]);
								if (tradeReqs.get(((Player)sender).getUniqueId()) == player.getUniqueId())
								{
									tradeReqs.remove(((Player)sender).getUniqueId());
									tradeReqs.remove(player.getUniqueId());
									traders.put(player.getUniqueId(), ((Player)sender).getUniqueId());
									traders.put(((Player)sender).getUniqueId(), player.getUniqueId());
									accepted.put(((Player)sender).getUniqueId(), false);
									accepted.put(player.getUniqueId(), false);
									offeredItems.put(((Player)sender).getUniqueId(), new ArrayList<>());
									offeredItems.put(player.getUniqueId(), new ArrayList<>());
									offeredCoins.put(player.getUniqueId(), 0.0);
									offeredCoins.put(((Player)sender).getUniqueId(), 0.0);
									sender.sendMessage(ChatColor.GREEN + "You are now trading with " + player.getName() + ".");
									trade.openTrade((Player)sender);
									player.sendMessage(ChatColor.GREEN + "You are now trading with " + sender.getName() + ".");
									trade.openTrade(player);
									
								}
								else
								{
									if (!tradeReqs.containsKey(player.getUniqueId()))
									{
										tradeReqs.put(player.getUniqueId(), ((Player)sender).getUniqueId());
										String[] args2 = {((Player)sender).getName(), ("/trade " + ((Player)sender).getName())};
										String[] args3 = {args[0]};
										sender.sendMessage(getMessage(config.getString("TradeSentMessage"), args3));
		    		        	    	player.spigot().sendMessage(getMessageComponent(config.getString("TradeRecievedMessage"), args2));
										Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				    		        	    @Override
				    		        	    public void run() {
				    		        	    	
				    		        	    	if (tradeReqs.containsKey(player.getUniqueId()) || tradeReqs.containsKey(((Player)sender).getUniqueId()))
				    		        	    	{
				    		        	    		sender.sendMessage(getMessage(config.getString("TradeToExpired"), args3));
				    		        	    		player.sendMessage(getMessage(config.getString("TradeFromExpired"), args2));
					    		        	    	
					    		        	    	tradeReqs.remove(((Player)sender).getUniqueId());
					    							tradeReqs.remove(player.getUniqueId());
				    		        	    	}
				    	    					
				    		        	    }
				    		        	}, config.getLong("TradeExpirationTime") * 20L);
									}
									else
									{
										sender.sendMessage(ChatColor.RED + "This player already has an outgoing trade request!");
									}
								}
							}
							else
							{
								sender.sendMessage(ChatColor.RED + "You cannot trade yourself!");
							}
						}
						else
						{
							sender.sendMessage(ChatColor.RED + "You are already trading someone!");
						}
					}
					else
					{
						sender.sendMessage(ChatColor.RED + "Please enter a valid player name to trade!");
					}
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "Please enter a player name to trade!");
				}
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "Trading is currently disabled!");
			}
		}
		return true;
	}
}
