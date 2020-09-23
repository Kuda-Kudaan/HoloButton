package septogeddon.holobutton;

import java.util.function.BiConsumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemButton extends HoloButton {

	protected ItemStack icon;
	public ItemButton(String title, ItemStack icon, BiConsumer<ActiveHoloButton, Player> click) {
		super(title, click);
		this.icon = icon;
	}
	
	public void setIcon(ItemStack icon) {
		this.icon = icon;
	}

	@Override
	public ItemStack getIcon() {
		return icon;
	}

}
