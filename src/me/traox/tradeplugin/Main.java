package me.traox.tradeplugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
	
	private static Main plugin;
	
	public Main() {
		plugin = this;
	}
	
	@Override
	public void onEnable() {
		// your on-enable code
	}
	
	@Override
	public void onDisable() {
		// your on-disable code
	}
	
	public static Main getInstance() {
		return plugin;
	}

}
