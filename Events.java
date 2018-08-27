package me.SrBigote.lobbymg;

import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.inventivetalent.bossbar.BossBarAPI;

import es.eltrueno.npc.TruenoNPC;
import es.eltrueno.npc.event.TruenoNPCInteractEvent;
import me.SrBigote.lobbymg.utils.BungeeUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.TextComponent;

public class Events implements Listener {

	private HashMap<UUID, Integer> staffpunchcooldownTime = new HashMap<>();
	private HashMap<UUID, BukkitRunnable> staffpunchcooldownTask = new HashMap<>();
	private HashMap<UUID, Integer> npcCooldownTime = new HashMap<>();
	private HashMap<UUID, BukkitRunnable> npcCooldownTask = new HashMap<>();

	String prefix = "§e§lLobbyMG §8| ";
	public static int timer = 25;
	Main plugin = Main.getInstance();

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

		if ((!(event.getDamager() instanceof Player)) || (!(event.getEntity() instanceof Player))) {
			return;
		}
		Player victim = (Player) event.getEntity();
		Player attacker = (Player) event.getDamager();

		if ((!attacker.hasPermission("lobbymg.punchstaff")) || (!victim.hasPermission("lobbymg.staff"))) {
			return;
		}
		if (staffpunchcooldownTime.containsKey(attacker.getUniqueId())) {
			attacker.sendMessage(prefix + "§cEspera " + ChatColor.YELLOW + staffpunchcooldownTime.get(attacker.getUniqueId())
					+ " §csegundos para lanzar a otro STAFF!");
			attacker.playSound(attacker.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
			return;
		}

		String puncher = prefix + "%vault_rankprefix% §e%player_name% §7lanzó a ";
		String puncherph = PlaceholderAPI.setPlaceholders(attacker, puncher);

		String punched = "%vault_rankprefix% §e%player_name% §7por los §bcielos§7!";
		String punchedph = PlaceholderAPI.setPlaceholders(victim, punched);

		Bukkit.broadcastMessage(puncherph + punchedph);
		victim.getWorld().playSound(victim.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
		victim.setVelocity(new Vector(0.0D, 2.5D, 0.0D));

		Firework fw = victim.getWorld().spawn(victim.getLocation(), Firework.class);

		FireworkMeta fm = fw.getFireworkMeta();
		Builder builder = FireworkEffect.builder();

		fm.addEffect(builder.flicker(false).withColor(Color.BLUE).build());
		fm.addEffect(builder.trail(false).build());
		fm.addEffect(builder.withFade(Color.ORANGE).build());
		fm.addEffect(builder.with(Type.BURST).build());
		fm.setPower(1);
		fw.setFireworkMeta(fm);
		victim.getWorld().playEffect(victim.getLocation(), Effect.MOBSPAWNER_FLAMES, 4);
		victim.getWorld().playEffect(victim.getLocation(), Effect.MOBSPAWNER_FLAMES, 4);
		attacker.getWorld().playEffect(victim.getLocation(), Effect.MOBSPAWNER_FLAMES, 4);
		attacker.getWorld().playEffect(victim.getLocation(), Effect.MOBSPAWNER_FLAMES, 4);

		staffpunchcooldownTime.put(attacker.getUniqueId(), 15);
		staffpunchcooldownTask.put(attacker.getUniqueId(), new BukkitRunnable() {
			public void run() {
				staffpunchcooldownTime.put(attacker.getUniqueId(), staffpunchcooldownTime.get(attacker.getUniqueId()) - 1);
				if (staffpunchcooldownTime.get(attacker.getUniqueId()) == 0) {
					staffpunchcooldownTime.remove(attacker.getUniqueId());
					staffpunchcooldownTask.remove(attacker.getUniqueId());
					cancel();
				}
			}
		});

		staffpunchcooldownTask.get(attacker.getUniqueId()).runTaskTimer(plugin, 20, 20);
	}

	@EventHandler
	public void chatFormat(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();

		String chat = "%vault_rankprefix% §e%player_name% §8» ";
		String chatph = PlaceholderAPI.setPlaceholders(p, chat);

		String defaultchat = "§7%player_name% §8» ";
		String defaultchatph = PlaceholderAPI.setPlaceholders(p, defaultchat);

		if (p.hasPermission("lobbymg.chat")) {
			event.setFormat(
					chatph + ChatColor.WHITE + (ChatColor.translateAlternateColorCodes('&', event.getMessage())));
		} else {
			event.setFormat(defaultchatph + ChatColor.GRAY + (event.getMessage()));
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {

		Player player = event.getPlayer();
		ItemStack itemhand = player.getInventory().getItemInHand();
		ItemMeta itemhandmeta = itemhand.getItemMeta();
		Action action = event.getAction();

		if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {

			if (itemhand.getType() == Material.COMPASS) {
				if (itemhandmeta.hasDisplayName()) {
					player.performCommand("menu servers");
					player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);

				}
			}
		}
	}

	public static void portalBoost(Player player) {
		Vector velocity = player.getLocation().getDirection();
		player.setVelocity(velocity.multiply(1.5F));
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		Material m = player.getLocation().getBlock().getType();
		if (m == Material.PORTAL) {
			portalBoost(player);
			new BukkitRunnable() {

				@Override
				public void run() {
					player.performCommand("menu servers");
				}
			}.runTaskLater(plugin, 10);

		}
	}

	@EventHandler
	public void onNPCInteract(TruenoNPCInteractEvent ev) {
		TruenoNPC npc = ev.getNPC();
		Player player = ev.getPlayer();
		int npcId = npc.getNpcID();

		String server = plugin.getConfig().getString("NPCs." + npcId + ".server");
		BungeeUtil.connect(server, player);

		if (npcCooldownTime.containsKey(player.getUniqueId())) {
			player.sendMessage(prefix + "§cEspera " + ChatColor.YELLOW + npcCooldownTime.get(player.getUniqueId())
					+ " §csegundos para usar de nuevo el §9§lNPC");
			player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
			return;
		}

		npcCooldownTime.put(player.getUniqueId(), 10);
		npcCooldownTask.put(player.getUniqueId(), new BukkitRunnable() {
			public void run() {
				npcCooldownTime.put(player.getUniqueId(), npcCooldownTime.get(player.getUniqueId()) - 1);
				if (npcCooldownTime.get(player.getUniqueId()) == 0) {
					npcCooldownTime.remove(player.getUniqueId());
					npcCooldownTask.remove(player.getUniqueId());
					cancel();
				}
			}
		});
		npcCooldownTask.get(player.getUniqueId()).runTaskTimer(plugin, 20, 20);
		player.sendMessage(prefix + ChatColor.GREEN + "Conectando a: " + ChatColor.LIGHT_PURPLE + server);

	}

	@EventHandler
	public void puchPlate(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		Location loc = player.getLocation();
		if (loc.getWorld().getBlockAt(loc).getType().equals(Material.STONE_PLATE)) {
			punchplate(player);

		}
	}

	public static void punchplate(Player player) {

		player.setVelocity(player.getLocation().getDirection().multiply(2.3F)); // Velocidad
		player.setVelocity(new Vector(player.getVelocity().getX(), 0.8D, player.getVelocity().getZ())); // altura
		player.playSound(player.getLocation(), Sound.CHICKEN_HURT, 2.0F, 1.0F);
		player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 2.0F, 1.0F);
		player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 4);
		player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 4);
		player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 4);
		player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 4);
		player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.EGG));
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		event.blockList().clear();
	}
	
	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();

		if (!p.hasPermission("lobbymg.admin")) {
			e.setCancelled(true);
			p.sendMessage(prefix + ChatColor.RED + "No puedes poner bloques aqui.");
		}

	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();

		if (!p.hasPermission("lobbymg.admin")) {
			e.setCancelled(true);
			p.sendMessage(prefix + ChatColor.RED + "No puedes quitar bloques aqui.");
		}

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onJoinBossBar(PlayerJoinEvent e) {
		if (plugin.getConfig().getBoolean("JoinBossBar.Enable")) {
			Player p = e.getPlayer();
			String message = plugin.getConfig().getString("JoinBossBar.Message");
			String messageph = (ChatColor.translateAlternateColorCodes('&', message));

			String bossmessage = PlaceholderAPI.setPlaceholders(p, messageph);

			String bc = plugin.getConfig().getString("JoinBossBar.Color");
			String bs = plugin.getConfig().getString("JoinBossBar.Style");
			int time = plugin.getConfig().getInt("JoinBossBar.Time");

			BossBarAPI.addBar(p, new TextComponent(bossmessage), BossBarAPI.Color.valueOf(bc),

					BossBarAPI.Style.valueOf(bs),

					1.0F, time, 2L, new BossBarAPI.Property[0]);
		}
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();

		if (((event.getMessage().equals("/?")) && (!p.hasPermission("lobbymg.admin")))
				|| ((event.getMessage().equalsIgnoreCase("/help")) && (!p.hasPermission("lobbymg.admin")))
				|| ((event.getMessage().equalsIgnoreCase("/bukkit:plugins")) && (!p.hasPermission("lobbymg.admin")))
				|| ((event.getMessage().equalsIgnoreCase("/bukkit:?")) && (!p.hasPermission("lobbymg.admin")))
				|| ((event.getMessage().equalsIgnoreCase("/bukkit:help")) && (!p.hasPermission("lobbymg.admin")))
				|| ((event.getMessage().equalsIgnoreCase("/icanhasbukkit")) && (!p.hasPermission("lobbymg.admin")))
				|| ((event.getMessage().equalsIgnoreCase("/version")) && (!p.hasPermission("lobbymg.admin")))
				|| ((event.getMessage().equalsIgnoreCase("/ver")) && (!p.hasPermission("lobbymg.admin")))
				|| ((event.getMessage().equalsIgnoreCase("/bukkit:ver")) && (!p.hasPermission("lobbymg.admin")))
				|| ((event.getMessage().equalsIgnoreCase("/bukkit:about")) && (!p.hasPermission("lobbymg.admin")))
				|| ((event.getMessage().equalsIgnoreCase("/bukkit:pl")) && (!p.hasPermission("lobbymg.admin")))
				|| ((event.getMessage().contains("/bukkit:msg")) && (!p.hasPermission("lobbymg.admin")))
				|| ((event.getMessage().contains("/bukkit:kill")) && (!p.hasPermission("lobbymg.admin")))
				|| ((event.getMessage().contains("/bukkit:tell")) && (!p.hasPermission("lobbymg.admin")))
				|| ((event.getMessage().equalsIgnoreCase("/bukkit:w")) && (!p.hasPermission("lobbymg.admin")))
				|| ((event.getMessage().contains("/bukkit:me")) && (!p.hasPermission("lobbymg.admin")))
				|| ((event.getMessage().equalsIgnoreCase("/pl")) && (!p.hasPermission("lobbymg.admin")))
				|| ((event.getMessage().equalsIgnoreCase("/plugins")) && (!p.hasPermission("lobbymg.admin")))
				|| ((event.getMessage().equalsIgnoreCase("/about")) && (!p.hasPermission("lobbymg.admin")))) {
			event.setCancelled(true);
			p.sendMessage(ChatColor.GREEN
					+ "¡No tienes permiso para hacer esto! La mayoria de los plugins que usa nuestra network son personalizados y no están disponible para descargarlos.");
		}
	}

}
