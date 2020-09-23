package septogeddon.holobutton;

import java.util.function.BiConsumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class HoloButton {

	private String label;
	private BiConsumer<ActiveHoloButton, Player> clicked;
	public HoloButton(String title, BiConsumer<ActiveHoloButton, Player> click) {
		label = title;
		clicked = click;
	}
	public BiConsumer<ActiveHoloButton, Player> getListener() {
		return clicked;
	}
	public String getLabel() {
		return label;
	}
	public abstract ItemStack getIcon();
}
