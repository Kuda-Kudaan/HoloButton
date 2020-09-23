package septogeddon.holobutton;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class ActiveHoloButton {

	private HoloButton button;
	private ArmorStand armorstand;
	private ActiveHoloGroup parent;
	float targetOffset;
	float offset;
	ActiveHoloButton(ActiveHoloGroup parent, HoloButton button) {
		this.parent = parent;
		this.button = button;
		targetOffset = offset = parent.getStartOffset();
		Location loc = parent.getPlayer().getLocation();
		Vector direction = new Vector(-Math.sin(Math.toRadians(loc.getYaw())) * parent.getRadius(), 0, Math.cos(Math.toRadians(loc.getYaw())) * parent.getRadius());
		spawn(loc.clone().add(direction));
	}
	
	void move(Location viewer, float offset, double radius) {
		viewer = viewer.clone();
		viewer.add(Math.sin(Math.toRadians(offset)) * radius, 0, Math.cos(Math.toRadians(offset)) * radius);
		viewer.setYaw(-offset+180);
		armorstand.teleport(viewer);
	}
	
	void remove() {
		if (armorstand != null) {
			armorstand.remove();
		}
	}
	
	void spawn(Location viewer) {
		if (armorstand != null) armorstand.remove();
		armorstand = (ArmorStand) viewer.getWorld().spawnEntity(viewer, EntityType.ARMOR_STAND);
		HoloButtonPlugin.hide(armorstand, parent.getPlayer());
		armorstand.setVisible(false);
		armorstand.setHelmet(button.getIcon());
		armorstand.setHeadPose(new EulerAngle(0, 0, 0));
		armorstand.setGravity(false);
		String label = button.getLabel();
		if (label != null) {
			armorstand.setCustomName(label);
			armorstand.setCustomNameVisible(true);
		}
	}
	
	public ActiveHoloGroup getParent() {
		return parent;
	}
	
	public void dispatch(Player player) {
		button.getListener().accept(this, player);
	}
	
	public void setLabel(String label) {
		armorstand.setCustomName(label);
		if (label == null) armorstand.setCustomNameVisible(false);
	}

	
	ArmorStand getArmorstand() {
		return armorstand;
	}
	
	public HoloButton getConfiguration() {
		return button;
	}
	
}
