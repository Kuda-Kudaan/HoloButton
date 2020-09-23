package septogeddon.holobutton;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ActiveHoloGroup {

	private Player player;
	private List<ActiveHoloButton> buttons = new ArrayList<>();
	private double radius;
	private double closestRadius;
	private float startOffset = 0;
	private float endOffset = 0;
	private double titleGap;
	private double subtitleGap;
	private HoloGroup parent;
	private ArmorStand titleAs;
	private ArmorStand subtitleAs;
	private ActiveHoloButton hoverred;
	ActiveHoloGroup(HoloGroup g, Player p) {
		radius = g.getRadius();
		titleGap = g.getTitleGap();
		subtitleGap = g.getSubtitleGap();
		closestRadius = g.getHoverRadius();
		parent = g;
		player = p;
		Location loc = p.getLocation();
		endOffset = OffsetStrategy.getTargetOffset(0, g.getButtons().size());
		startOffset = -loc.getYaw();
		for (HoloButton b : g.getButtons()) {
			ActiveHoloButton bx = new ActiveHoloButton(this, b);
			buttons.add(bx);
		}
		String title = g.getTitle();
		if (title != null) {
			setTitle(title);
		}
		String subtitle = g.getSubtitle();
		if (subtitle != null) {
			setSubtitle(subtitle);
		}
		if (g.getOpenListener() != null) {
			g.getOpenListener().accept(p);
		}
	}
	float getStartOffset() {
		return startOffset;
	}
	double getRadius() {
		return radius;
	}
	public Player getPlayer() {
		return player;
	}
	public void setSubtitle(String subtitle) {
		if (subtitleAs != null) {
			if (subtitle == null) {
				subtitleAs.remove();
				return;
			}
			subtitleAs.setCustomName(subtitle);
			return;
		}
		Location loc = player.getLocation();
		Vector direction = new Vector(-Math.sin(Math.toRadians(loc.getYaw())) * radius, parent.getSubtitleGap(), Math.cos(Math.toRadians(loc.getYaw())) * radius);
		subtitleAs = (ArmorStand)player.getWorld().spawnEntity(loc.clone().add(direction), EntityType.ARMOR_STAND);
		HoloButtonPlugin.hide(subtitleAs, player);
		subtitleAs.setVisible(false);
		subtitleAs.setCustomName(subtitle);
		subtitleAs.setCustomNameVisible(true);
		subtitleAs.setGravity(false);
	}
	public void setTitle(String title) {
		if (titleAs != null) {
			if (title == null) {
				titleAs.remove();
				return;
			}
			titleAs.setCustomName(title);
			return;
		}
		Location loc = player.getLocation();
		Vector direction = new Vector(-Math.sin(Math.toRadians(loc.getYaw())) * radius, parent.getTitleGap(), Math.cos(Math.toRadians(loc.getYaw())) * radius);
		titleAs = (ArmorStand)player.getWorld().spawnEntity(loc.clone().add(direction), EntityType.ARMOR_STAND);
		HoloButtonPlugin.hide(titleAs, player);
		titleAs.setVisible(false);
		titleAs.setCustomName(title);
		titleAs.setCustomNameVisible(true);
		titleAs.setGravity(false);
	}
	boolean isOwnedEntity(int id) {
		if (titleAs != null && titleAs.getEntityId() == id) return true;
		if (subtitleAs != null && subtitleAs.getEntityId() == id) return true;
		for (ActiveHoloButton b : buttons) {
			if (b.getArmorstand() != null && b.getArmorstand().getEntityId() == id) return true;
		}
		return false;
	}
	ActiveHoloButton getButton(Entity e) {
		for (ActiveHoloButton b : buttons) {
			if (b.getArmorstand() == e) {
				return b;
			}
		}
		return null;
	}
	public ActiveHoloButton getHovered() {
		return hoverred;
	}
	public HoloGroup getConfiguration() {
		return parent;
	}
	public List<ActiveHoloButton> getButtons() {
		return new ArrayList<>(buttons);
	}
	public ActiveHoloButton addButton(HoloButton b) {
		ActiveHoloButton bu;
		buttons.add(bu = new ActiveHoloButton(this, b));
		return bu;
	}
	public void clear() {
		for (ActiveHoloButton b : buttons) b.remove();
		buttons.clear();
	}
	public boolean removeButton(HoloButton b) {
		boolean success = false;
		for (ActiveHoloButton bu : getButtons()) {
			if (bu.getConfiguration() == b) {
				bu.remove();
				buttons.remove(bu);
				success = true;
			}
		}
		return success;
	}
	public void close() {
		HoloButtonPlugin.activeHolos.remove(player.getUniqueId(), this);
		for (ActiveHoloButton b : buttons) {
			b.remove();
		}
		if (titleAs != null) {
			titleAs.remove();
			titleAs = null;
		}
		if (subtitleAs != null) {
			subtitleAs.remove();
			subtitleAs = null;
		}
		if (parent.getCloseListener() != null && player.isOnline()) {
			parent.getCloseListener().accept(player);
		}
	}
	void tick() {
		Location loc = player.getLocation();
		if (titleAs != null) {
			Vector direction;
			if (hoverred != null) {
				float off = hoverred.targetOffset;
				direction = new Vector(Math.sin(Math.toRadians(off)) * radius, titleGap, Math.cos(Math.toRadians(off)) * radius);
			} else {
				direction = new Vector(-Math.sin(Math.toRadians(loc.getYaw())) * radius, titleGap, Math.cos(Math.toRadians(loc.getYaw())) * radius);
			}
			titleAs.teleport(loc.clone().add(direction));
		}
		if (subtitleAs != null) {
			Vector direction;
			if (hoverred != null) {
				float off = hoverred.targetOffset;
				direction = new Vector(Math.sin(Math.toRadians(off)) * radius, subtitleGap, Math.cos(Math.toRadians(off)) * radius);
			} else {
				direction = new Vector(-Math.sin(Math.toRadians(loc.getYaw())) * radius, subtitleGap, Math.cos(Math.toRadians(loc.getYaw())) * radius);
			}
			subtitleAs.teleport(loc.clone().add(direction));
		}
		hoverred = null;
		double yawDifference = (-loc.getYaw()-startOffset);
		if (yawDifference <= endOffset + 15 || yawDifference >= -endOffset - 15) {
//			if (yawDifference > endOffset + 15) {
//				startOffset += endOffset - yawDifference;
//			} else if (yawDifference < -endOffset - 15) {
//				startOffset -= endOffset - yawDifference;
//			}
		}
		double bounding = OffsetStrategy.getNearestBoundingBox(buttons.size());
		ActiveHoloButton hover = null;
		boolean move = false;
		for (int i = 0; i < buttons.size(); i++) {
			ActiveHoloButton b = buttons.get(i);
			b.targetOffset = OffsetStrategy.getTargetOffset(i, buttons.size()) + startOffset;
			if (b.targetOffset < b.offset - 1) {
				b.offset -= Math.max(1, (b.offset-b.targetOffset) * (5f/100f));
				move = true;
			} else if (b.targetOffset > b.offset + 1) {
				b.offset += Math.max(1, (b.targetOffset-b.offset) * (5f/100f));
				move = true;
			}
			double distance = Math.sqrt(Math.pow(Math.sin(Math.toRadians(loc.getYaw())) - Math.sin(Math.toRadians(-b.offset)), 2) + Math.pow(Math.cos(Math.toRadians(loc.getYaw())) - Math.cos(Math.toRadians(-b.offset)), 2));
			double radiusNew = distance < bounding ? closestRadius : radius;
			if (distance < bounding) {
				hover = b;
				b.getArmorstand().setCustomNameVisible(b.getArmorstand().getCustomName() != null);
			} else {
				b.getArmorstand().setCustomNameVisible(false);
			}
			b.move(player.getLocation(), b.offset, radiusNew);
		}
		if (!move) {
			hoverred = hover;
		}
	}
}
