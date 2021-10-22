package me.traox.tradeplugin;

import java.text.NumberFormat;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

public final class TradeEditHandler implements Listener {
	
	private static Events plugin;
	private FileConfiguration config = null;

    public static Economy economy = null;
    
    private static final NumberFormat numberFormat;
    
    static {
        numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(true);
    }
    
    public TradeEditHandler(Events plugin)
    {
        this.plugin = plugin;
        config = plugin.getConfig();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent e)
    {
    	if (e.getInventory().getName().equalsIgnoreCase("editing trade"))
    	{
    		if (e.getSlot() < 45 && e.getSlot() % 9 != 4 && e.getSlot() != 30 && e.getSlot() != 36 && e.getSlot() != 39 && e.getSlot() != 41)
    		{
    			if (e.getClickedInventory() != null)
    			{	
	    			if (e.getClickedInventory().getTitle().equalsIgnoreCase("editing trade"))
	    			{
	    				e.setCancelled(true);
	    			}
    			}
    		}
    	}
    }
    
    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent e)
    {
    	if (e.getInventory().getName().equalsIgnoreCase("editing trade"))
    	{
    		for (int i = 0; i < 54; i++)
    		{
    			if (i % 9 == 4 || i >= 45 || i == 30 || i == 36 || i == 39 || i == 41)
    			{
    				config.set("TradeMenuItems." + i, e.getInventory().getItem(i));
    			}
    		}
    		plugin.saveConfig();
    		e.getPlayer().sendMessage(ChatColor.GRAY + "<!> Trade menu saved!");
        	((Player)e.getPlayer()).playSound(e.getPlayer().getLocation(), Sound.LEVEL_UP, 1, 1);
    	}
    }
}
