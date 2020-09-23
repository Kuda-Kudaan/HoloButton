package septogeddon.holobutton;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class ConfiguredHoloGroup extends HoloGroup {

	public ConfiguredHoloGroup(ConfigurationSection section) {
		setTitle(ChatColor.translateAlternateColorCodes('&', section.getString("title")));
		setSubtitle(ChatColor.translateAlternateColorCodes('&', section.getString("subtitle")));
		setRadius(section.getDouble("button-radius"));
		setHoverRadius(section.getDouble("button-hover-radius"));
		setTitleGap(section.getDouble("title-gap"));
		setSubtitleGap(section.getDouble("subtitle-gap"));
		setFreeze(section.getBoolean("freeze"));
		setInvincible(section.getBoolean("invincible"));
		setOpenListener(ConfiguredExecutor.fromMapList(section.getMapList("on-open")));
		setCloseListener(ConfiguredExecutor.fromMapList(section.getMapList("on-close")));
		for (Map<?,?> b : section.getMapList("buttons")) {
			getButtons().add(new ConfiguredHoloButton(b));
		}
	}
	
}
