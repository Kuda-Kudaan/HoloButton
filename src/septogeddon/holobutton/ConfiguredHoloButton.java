package septogeddon.holobutton;

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ConfiguredHoloButton extends ItemButton {

	public ConfiguredHoloButton(Map<?,?> map) {
		super(ChatColor.translateAlternateColorCodes('&', map.get("label").toString()), new ItemStack(Material.valueOf(map.get("item").toString())), ConfiguredExecutor.fromUnknownList(map.containsKey("close") ? (Boolean)map.get("close") : false, (List<?>) map.get("executors")));
		ItemMeta meta = getIcon().getItemMeta();
		if (meta instanceof SkullMeta && map.containsKey("skull-owner")) {
			((SkullMeta) meta).setOwner(map.get("skull-owner").toString());
			getIcon().setItemMeta(meta);
		}
	}
}
