package septogeddon.holobutton;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;

class EntityInterceptor {

	public void hide(Entity e, Player except) {
		PacketContainer container = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
		StructureModifier<int[]> ints = container.getIntegerArrays();
		ints.writeSafely(0, new int[] {e.getEntityId()});
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p != except) {
				try {
					ProtocolLibrary.getProtocolManager().sendServerPacket(p, container);
				} catch (InvocationTargetException e1) {
				}
			}
		}
	}
	public void register(HoloButtonPlugin pl) {
		PacketAdapter adapter = new PacketAdapter(pl, ListenerPriority.LOWEST, PacketType.Play.Server.SPAWN_ENTITY, PacketType.Play.Server.SPAWN_ENTITY_LIVING) {
			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer container = event.getPacket();
				StructureModifier<Integer> ints = container.getIntegers();
				Integer entityId = ints.readSafely(0);
				if (entityId != null) {
					for (ActiveHoloGroup gr : HoloButtonPlugin.activeHolos.values()) {
						if (gr.isOwnedEntity(entityId)) {
							if (!gr.getPlayer().getUniqueId().equals(event.getPlayer().getUniqueId())) {
								event.setCancelled(true);
							}
							break;
						}
					}
				}
			}
		};
		ProtocolLibrary.getProtocolManager().addPacketListener(adapter);
	}
	
}
