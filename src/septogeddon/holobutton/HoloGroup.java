package septogeddon.holobutton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.entity.Player;

public class HoloGroup {

	public static Set<ActiveHoloGroup> getActiveHolograms(HoloGroup g) {
		Set<ActiveHoloGroup> groups = new HashSet<>();
		for (ActiveHoloGroup gr : HoloButtonPlugin.activeHolos.values()) {
			if (gr.getConfiguration() == g) {
				groups.add(gr);
			}
		}
		return groups;
	}
	
	private double radius = 4;
	private double hoverRadius = 3;
	private double titleGap = 0.6;
	private double subtitleGap = -1.3;
	private String title;
	private String subtitle;
	private List<HoloButton> buttons = new ArrayList<>();
	private Consumer<Player> openListener, closeListener;
	private boolean freeze, invincible;
	public String getSubtitle() {
		return subtitle;
	}
	
	public boolean isFreeze() {
		return freeze;
	}
	
	public boolean isInvincible() {
		return invincible;
	}
	
	public void setFreeze(boolean freeze) {
		this.freeze = freeze;
	}
	
	public void setInvincible(boolean invincible) {
		this.invincible = invincible;
	}
	
	public Set<ActiveHoloGroup> getActiveHolograms() {
		return getActiveHolograms(this);
	}
	
	public Consumer<Player> getOpenListener() {
		return openListener;
	}
	
	public Consumer<Player> getCloseListener() {
		return closeListener;
	}
	
	public void setOpenListener(Consumer<Player> openListener) {
		this.openListener = openListener;
	}
	
	public void setCloseListener(Consumer<Player> closeListener) {
		this.closeListener = closeListener;
	}
	
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	
	public double getSubtitleGap() {
		return subtitleGap;
	}
	
	public void setSubtitleGap(double subtitleGap) {
		this.subtitleGap = subtitleGap;
	}
	
	public double getHoverRadius() {
		return hoverRadius;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public double getTitleGap() {
		return titleGap;
	}
	
	public void setHoverRadius(double hoverRadius) {
		this.hoverRadius = hoverRadius;
	}
	
	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	public void setTitleGap(double titleGap) {
		this.titleGap = titleGap;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public final ActiveHoloGroup open(Player p) {
		if (buttons.isEmpty()) throw new IllegalArgumentException("empty buttons");
		ActiveHoloGroup ex = HoloButtonPlugin.activeHolos.get(p.getUniqueId());
		if (ex != null) {
			ex.close();
		}
		ActiveHoloGroup n;
		HoloButtonPlugin.activeHolos.put(p.getUniqueId(), n = new ActiveHoloGroup(this, p));
		return n;
	}
	
	public List<HoloButton> getButtons() {
		return buttons;
	}
	
}
