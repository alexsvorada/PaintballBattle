package pb.ajneb97.logic;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import net.md_5.bungee.api.ChatColor;
import pb.ajneb97.PaintballBattle;
import pb.ajneb97.utils.ItemsUtils;

public class InventarioKillstreaks{

	int taskID;
	private PaintballBattle plugin;
	public InventarioKillstreaks(PaintballBattle plugin) {
		this.plugin = plugin;
	}
	
	public void actualizarInventario(final Player jugador,final PaintballMatch paintballMatch) {
		BukkitScheduler sh = Bukkit.getServer().getScheduler();
		final FileConfiguration config = plugin.getConfig();
		final FileConfiguration messages = plugin.getMessages();
		taskID = sh.scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				if(!update(jugador,config,messages, paintballMatch)) {
					Bukkit.getScheduler().cancelTask(taskID);
					return;
				}
			}
		}, 0L, 20L);
	}
	
	protected boolean update(Player jugador, FileConfiguration config, FileConfiguration messages, PaintballMatch paintballMatch) {
		String pathInventory = ChatColor.translateAlternateColorCodes('&', ChatColor.translateAlternateColorCodes('&', config.getString("killstreaks_inventory_title")));
		String pathInventoryM = ChatColor.stripColor(pathInventory);
		Inventory inv = jugador.getOpenInventory().getTopInventory();
		if(paintballMatch == null) {
			return false;
		}
		PaintballPlayer j = paintballMatch.getJugador(jugador.getName());
		if(j == null) {
			return false;
		}
		if(inv != null && ChatColor.stripColor(jugador.getOpenInventory().getTitle()).equals(pathInventoryM)) {
			for(String key : config.getConfigurationSection("killstreaks_items").getKeys(false)) {
				ItemStack item = ItemsUtils.crearItem(config, "killstreaks_items."+key);
				
				Killstreak k = j.getKillstreak(key);
				if(k != null) {
					ItemMeta meta = item.getItemMeta();
					List<String> lore = new ArrayList<String>();
					lore.add(ChatColor.translateAlternateColorCodes('&', messages.getString("killstreakCurrentlyActive").replace("%time%", k.getTiempo()+"")));
					meta.setLore(lore);
					item.setItemMeta(meta);
				}
				int slot = Integer.valueOf(config.getString("killstreaks_items."+key+".slot"));
				if(slot != - 1) {
					inv.setItem(slot, item);
				}	
			}
			return true;
		}else {
			return false;
		}
	}

}
