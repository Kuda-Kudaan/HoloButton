package septogeddon.holobutton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ConfiguredExecutor implements Consumer<Player> {

	public static BiConsumer<ActiveHoloButton, Player> fromUnknownList(boolean close, List<?> list) {
		if (list == null) return null;
		List<ConfiguredExecutor> executors = new ArrayList<>(list.size());
		for (Object map : list) {
			if (map instanceof Map) executors.add(new ConfiguredExecutor((Map<?,?>)map));
		}
		return (button,player)->{
			if (close) {
				button.getParent().close();
			}
			for (ConfiguredExecutor exec : executors) {
				exec.accept(player);
			}
			
		};
	}
	public static Consumer<Player> fromMapList(List<Map<?,?>> list) {
		if (list == null) return null;
		List<ConfiguredExecutor> executors = new ArrayList<>(list.size());
		for (Map<?,?> map : list) {
			executors.add(new ConfiguredExecutor(map));
		}
		return player->{
			for (ConfiguredExecutor exec : executors) {
				exec.accept(player);
			}
		};
	}
	private ExecutorType type;
	private String value;
	public ConfiguredExecutor(Map<?,?> map) {
		type = ExecutorType.valueOf(map.get("type").toString());
		value = map.get("value").toString();
	}
	
	@Override
	public void accept(Player t) {
		String value = this.value.replace("{player}", t.getName());
		if (type == ExecutorType.CONSOLE_COMMAND) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value);
		} else if (type == ExecutorType.COMMAND) {
			Bukkit.dispatchCommand(t, value);
		} else if (type == ExecutorType.CHAT) {
			t.chat(value);
		}
	}
	
	public static enum ExecutorType {
		CONSOLE_COMMAND, COMMAND, CHAT;
	}
}
