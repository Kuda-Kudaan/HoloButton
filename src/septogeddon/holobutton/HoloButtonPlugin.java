package septogeddon.holobutton;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class HoloButtonPlugin extends JavaPlugin implements Listener, Runnable {

	protected static Map<UUID, ActiveHoloGroup> activeHolos = new ConcurrentHashMap<>();
	protected EntityInterceptor interceptor;
	private Map<String, ConfiguredHoloGroup> holobuttons = new HashMap<>();
	static HoloButtonPlugin instance;
	@Override
	public void onEnable() {
		instance = this;
		getServer().getScheduler().runTaskTimer(this, this, 1L, 1L);
		getServer().getPluginManager().registerEvents(this, this);
		if (getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
			interceptor = new EntityInterceptor();
			interceptor.register(this);
		}
		try {
			reloadConfig();
		} catch (Throwable t) {
			getLogger().log(Level.SEVERE, "Failed to load config.yml", t);
		}
	}
	
	static void hide(Entity e, Player except) {
		if (instance.interceptor != null) {
			instance.interceptor.hide(e, except);
		}
	}
	
	@Override
	public void onDisable() {
		for (ActiveHoloGroup g : activeHolos.values()) {
			g.close();
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onClick(PlayerArmorStandManipulateEvent e) {
		ActiveHoloButton button = getByEntity(e.getRightClicked());
		if (button != null) {
			e.setCancelled(true);
			if (button.getParent().getPlayer() == e.getPlayer()) {
				button.dispatch(e.getPlayer());
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onClick(EntityDamageByEntityEvent e) {
		ActiveHoloButton button = getByEntity(e.getEntity());
		if (button != null) {
			e.setCancelled(true);
			if (button.getParent().getPlayer() == e.getDamager()) {
				button.dispatch((Player)e.getDamager());
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onQuit(PlayerQuitEvent e) {
		ActiveHoloGroup button = activeHolos.remove(e.getPlayer().getUniqueId());
		if (button != null) {
			button.close();
		}
	}
	
	public static ActiveHoloGroup getActiveHoloButton(Player p) {
		return activeHolos.get(p.getUniqueId());
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onClick(EntityDamageEvent e) {
		if (getByEntity(e.getEntity()) != null) e.setCancelled(true);
		else if (e.getEntity() instanceof Player) {
			ActiveHoloGroup holo = activeHolos.get(e.getEntity().getUniqueId());
			if (holo != null) {
				if (holo.getConfiguration().isInvincible()) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onMove(PlayerMoveEvent e) {
		if (e.getTo().getX() == e.getFrom().getX() &&
				e.getTo().getY() == e.getFrom().getY() &&
					e.getTo().getZ() == e.getFrom().getZ()) return;
		ActiveHoloGroup holo = activeHolos.get(e.getPlayer().getUniqueId());
		if (holo != null) {
			if (holo.getConfiguration().isFreeze()) {
				e.setCancelled(true);
			}
		}
	}
	
	@Override
	public void reloadConfig() {
		super.saveDefaultConfig();
		for (ActiveHoloGroup g : activeHolos.values()) {
			g.close();
		}
		super.reloadConfig();
		for (String key : getConfig().getKeys(false)) {
			try {
				holobuttons.put(key, new ConfiguredHoloGroup(getConfig().getConfigurationSection(key)));
			} catch (Throwable t) {
				getLogger().log(Level.SEVERE, "Failed to load holobutton \""+key+"\"", t);
			}
		}
	}
	
	static ActiveHoloButton getByEntity(Entity e) {
		for (ActiveHoloGroup b : activeHolos.values()) {
			ActiveHoloButton bu = b.getButton(e);
			if (bu != null) return bu;
		}
		return null;
	}
 	private static String prefix = ChatColor.translateAlternateColorCodes('&', "&8[&bHoloButton&8] &7");
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("open")) {
					if (args.length > 1) {
						ConfiguredHoloGroup holo = holobuttons.get(args[1]);
						if (holo == null) {
							sender.sendMessage(prefix+"Holobutton with name \""+args[1]+"\" is unavailable!");
							return true;
						}
						Player target;
						if (args.length > 2) {
							target = Bukkit.getPlayerExact(args[2]);
							if (target == null) {
								sender.sendMessage(prefix+"Player \""+args[2]+"\"is offline!");
								return true;
							}
						} else {
							if (sender instanceof Player) {
								target = (Player)sender;
							} else {
								sender.sendMessage(prefix+"Open a holobutton. Usage: /"+label+" open <holobutton name> <player name>");
								return true;
							}
						}
						holo.open(target);
						return true;
					}
					sender.sendMessage(prefix+"Open a holobutton. Usage: /"+label+" open <holobutton name> "+(sender instanceof Player ? "[player name]" : "<player name>"));
					return true;
				}
				if (args[0].equalsIgnoreCase("close")) {
					if (sender instanceof Player) {
						ActiveHoloGroup active = getActiveHoloButton((Player)sender);
						if (active != null) {
							active.close();
							sender.sendMessage(prefix+"You force-closed the holobutton!");
							return true;
						}
					}
					if (args.length > 1) {
						Player target = Bukkit.getPlayerExact(args[1]);
						if (target == null) {
							sender.sendMessage(prefix+"That player is offline!");
							return true;
						}
						ActiveHoloGroup active = getActiveHoloButton((Player)sender);
						if (active == null) {
							sender.sendMessage(prefix+"That player does not have any active holobutton!");
							return true;
						}
						active.close();
						target.sendMessage(prefix+"Your holobutton has been force-closed by "+sender.getName());
						sender.sendMessage(prefix+"You force-closed "+target.getName()+" holobutton!");
						return true;
					}
					sender.sendMessage(prefix+"Force close a holobutton. Usage: /"+label+" close <player>");
					return true;
				}
				if (args[0].equalsIgnoreCase("reload")) {
					sender.sendMessage(prefix+"Reloading...");
					reloadConfig();
					sender.sendMessage(prefix+"Reloaded!");
					return true;
				}
				if (args[0].equalsIgnoreCase("list")) {
					sender.sendMessage(prefix+"Available holobuttons: "+String.join(", ", holobuttons.keySet()));
					return true;
				}
			}
			sender.sendMessage(prefix+"HoloButton v"+getDescription().getVersion()+" by Septogeddon. Usage: /"+label+" <reload|open|close|list>");
			return true;
		} catch (Throwable t) {
			sender.sendMessage(prefix+"An error occured! Please check the console!");
			t.printStackTrace();
			return true;
		}
	}
	
	@Override
	public void run() {
		try {
			for (ActiveHoloGroup g : activeHolos.values()) {
				try {
					g.tick();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
}
