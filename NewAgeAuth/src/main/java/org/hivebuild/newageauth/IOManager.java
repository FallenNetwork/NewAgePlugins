package org.hivebuild.newageauth;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * 
 * The IOManager Handles all Input and Output
 * 
 * @author efreak
 * 
 */

public class IOManager {

	private static final Configuration config;
	private static final Plugin plugin;
	public static String prefix = ChatColor.DARK_RED + "[NewAgeAuth] " + ChatColor.WHITE;
	public static String error = ChatColor.RED + "[Error] " + ChatColor.WHITE;
	public static String warning = ChatColor.YELLOW + "[Warning] " + ChatColor.WHITE;
	private static boolean color = true;

	static {
		plugin = NewAgeAuth.getInstance();
		config = NewAgeAuth.getConfiguration();
	}

	public void init() {
		color = config.getBoolean("IO.ColoredLogs");
		prefix = color(config.getString("IO.Prefix")) + " " + ChatColor.WHITE;
		error = color(config.getString("IO.Error")) + " " + ChatColor.WHITE;
		warning = color(config.getString("IO.Warning")) + " " + ChatColor.WHITE;
	}

	public void sendConsole(String msg) {
		if (config.getBoolean("IO.Show-Prefix")) plugin.getServer().getConsoleSender().sendMessage(color(prefix + msg));
		else plugin.getServer().getConsoleSender().sendMessage(color(msg));
	}

	public void sendConsole(String msg, boolean showPrefix) {
		if (showPrefix) plugin.getServer().getConsoleSender().sendMessage(color(prefix + msg));
		else plugin.getServer().getConsoleSender().sendMessage(color(msg));
	}

	public void sendConsoleWarning(String msg) {
		if (config.getBoolean("IO.Show-Prefix")) plugin.getServer().getConsoleSender().sendMessage(color(prefix + warning + ChatColor.YELLOW + msg));
		else plugin.getServer().getConsoleSender().sendMessage(color(warning + msg));
	}

	public void sendConsoleWarning(String msg, boolean showPrefix) {
		if (showPrefix) plugin.getServer().getConsoleSender().sendMessage(color(prefix + warning + ChatColor.YELLOW + msg));
		else plugin.getServer().getConsoleSender().sendMessage(color(warning + msg));
	}

	public void sendConsoleError(String msg) {
		if (config.getBoolean("IO.Show-Prefix")) plugin.getServer().getConsoleSender().sendMessage(color(prefix + error + ChatColor.RED + msg));
		else plugin.getServer().getConsoleSender().sendMessage(color(error + msg));
	}

	public void sendConsoleError(String msg, boolean showPrefix) {
		if (showPrefix) plugin.getServer().getConsoleSender().sendMessage(color(prefix + error + ChatColor.RED + msg));
		else plugin.getServer().getConsoleSender().sendMessage(color(error + msg));
	}

	public void send(CommandSender sender, String msg) {
		if (config.getBoolean("IO.Show-Prefix")) sender.sendMessage(parseColor(prefix + msg));
		else sender.sendMessage(parseColor(msg));
	}

	public void send(CommandSender sender, String msg, boolean showPrefix) {
		if (showPrefix) sender.sendMessage(parseColor(prefix + msg));
		else sender.sendMessage(parseColor(msg));
	}

	public void sendWarning(CommandSender sender, String msg) {
		if (config.getBoolean("IO.Show-Prefix")) sender.sendMessage(parseColor(prefix + warning + ChatColor.YELLOW + msg));
		else sender.sendMessage(parseColor(warning + msg));
	}

	public void sendWarning(CommandSender sender, String msg, boolean showPrefix) {
		if (showPrefix) sender.sendMessage(parseColor(prefix + warning + ChatColor.YELLOW + msg));
		else sender.sendMessage(parseColor(warning + msg));
	}

	public void sendError(CommandSender sender, String msg) {
		if (config.getBoolean("IO.Show-Prefix")) sender.sendMessage(parseColor(prefix + error + ChatColor.RED + msg));
		else sender.sendMessage(parseColor(error + msg));
	}

	public void sendError(CommandSender sender, String msg, boolean showPrefix) {
		if (showPrefix) sender.sendMessage(parseColor(prefix + error + ChatColor.RED + msg));
		else sender.sendMessage(parseColor(warning + msg));
	}

	public String color(String msg) {
		if (color) return parseColor(msg);
		else return remColor(msg);
	}

	public static String parseColor(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public static String remColor(String message) {
		return ChatColor.stripColor(message);
	}
}
