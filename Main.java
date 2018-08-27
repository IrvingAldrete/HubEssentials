package me.SrBigote.lobbymg;

import java.util.ArrayList;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.inventivetalent.bossbar.BossBarAPI;
import es.eltrueno.npc.TruenoNPCApi;
import es.eltrueno.npc.skin.TruenoNPCSkin;
import es.eltrueno.npc.skin.TruenoNPCSkinBuilder;
import me.SrBigote.lobbymg.Commands.Commands;
import me.SrBigote.lobbymg.utils.ActionBarAPI;
import me.SrBigote.lobbymg.utils.Tab;
import me.SrBigote.lobbymg.utils.Titles;
import me.SrBigote.lobbymg.utils.Util;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.ChatComponentText;

public class Main extends JavaPlugin implements Listener {

	private static Main plugin;
	public boolean placeholders = false;
	String prefix = "§e§lLobbyMG §8| ";
	public Commands commands;

	public void onEnable() {
		setInstance(this);
		loadConfig();
		loadNPCS();
		super.onEnable();
		commands = new Commands();
		commands.onEnable();
		bossbar();
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(new Events(), this);
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "LobbyMG by SrBigote loaded succesfully");

		if (setupPlaceHolderAPI()) {
			getServer().getConsoleSender().sendMessage("PlaceHolderAPI hooked!");
		}
	}
	
	public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> gameStop(player));
		this.getServer().getConsoleSender().sendMessage(ChatColor.RED + "LobbyMG by SrBigote unloaded succesfully");

	}

	public static Main getInstance() {
		return plugin;
	}

	public static void setInstance(Main plugin) {
		Main.plugin = plugin;
	}

	private boolean setupPlaceHolderAPI() {
		if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			this.placeholders = true;
		}
		return this.placeholders;
	}

	private void gameStop(Player player) {
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
	}

	public void loadConfig() {
		this.getConfig().options().copyDefaults(true);
		saveConfig();
	}

	// Scoreboard

	public void addscoreboard(final Player player) {

		ArrayList<String> scoreboard = new ArrayList<String>();

		final ScoreboardManager manager = Bukkit.getScoreboardManager();
		final Scoreboard board = manager.getNewScoreboard();

		Objective objective = board.registerNewObjective("Scoreboard", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		Team rango = board.registerNewTeam("Rango");
		rango.addEntry("§fRango: ");
		objective.getScore("§fRango: ").setScore(9);

		Team users = board.registerNewTeam("Users");
		users.addEntry("§fUsuarios: ");
		objective.getScore("§fUsuarios: ").setScore(6);

		Team usertag = board.registerNewTeam("usertag");
		usertag.setPrefix("§7");

		Team viptag = board.registerNewTeam("vip");
		viptag.setPrefix("§a§lVIP §e");

		Team vipplustag = board.registerNewTeam("vip+");
		vipplustag.setPrefix("§a§lVIP§6§l+ §e");

		Team ultrattag = board.registerNewTeam("ultratag");
		ultrattag.setPrefix("§b§lULTRA §e");

		Team yttag = board.registerNewTeam("yttag");
		yttag.setPrefix("§f§lY§c§lT §e");

		Team famosotag = board.registerNewTeam("famosotag");
		famosotag.setPrefix("§d§lFAMOSO §e");

		Team buildertag = board.registerNewTeam("buildertag");
		buildertag.setPrefix("§3§lBUILDER §e");

		Team ayudantetag = board.registerNewTeam("ayudantetag");
		ayudantetag.setPrefix("§9§lAYUDANTE §e");

		Team modtag = board.registerNewTeam("modtag");
		modtag.setPrefix("§2§lMOD §e");

		Team admintag = board.registerNewTeam("admintag");
		admintag.setPrefix("§c§lADMIN §e");

		Team devtag = board.registerNewTeam("devtag");
		devtag.setPrefix("§5§lDEV §e");

		Team ownertag = board.registerNewTeam("ownertag");
		ownertag.setPrefix("§4§lDueño §e");

		// NameTags Updater
		new BukkitRunnable() {

			@Override
			public void run() {
				for (Player online : Bukkit.getOnlinePlayers()) {

					String rank = "%vault_rank%";
					String rankph = PlaceholderAPI.setPlaceholders(online, rank);

					if (rankph.equals("Vip")) {
						viptag.addEntry(online.getName());
						online.setPlayerListName("§a§lVIP §e" + online.getDisplayName());
					} else if (rankph.equals("Vip+")) {
						vipplustag.addEntry(online.getName());
						online.setPlayerListName("§a§lVIP§6§l+ §e" + online.getDisplayName());
					} else if (rankph.equals("Ultra")) {
						ultrattag.addEntry(online.getName());
						online.setPlayerListName("§b§lULTRA §e" + online.getDisplayName());
					} else if (rankph.equals("Youtuber")) {
						yttag.addEntry(online.getName());
						online.setPlayerListName("§f§lY§c§lT §e" + online.getDisplayName());
					} else if (rankph.equals("Famoso")) {
						famosotag.addEntry(online.getName());
						online.setPlayerListName("§d§lFAMOSO §e" + online.getDisplayName());
					} else if (rankph.equals("Builder")) {
						buildertag.addEntry(online.getName());
						online.setPlayerListName("§3§lBUILDER §e" + online.getDisplayName());
					} else if (rankph.equals("Ayudante")) {
						ayudantetag.addEntry(online.getName());
						online.setPlayerListName("§9§lAYUDANTE §e" + online.getDisplayName());
					} else if (rankph.equals("Mod")) {
						modtag.addEntry(online.getName());
						online.setPlayerListName("§2§lMOD §e" + online.getDisplayName());
					} else if (rankph.equals("Admin")) {
						admintag.addEntry(online.getName());
						online.setPlayerListName("§c§lADMIN §e" + online.getDisplayName());
					} else if (rankph.equals("Dev")) {
						devtag.addEntry(online.getName());
						online.setPlayerListName("§5§lDEV §e" + online.getDisplayName());
					} else if (rankph.equals("Dueño")) {
						ownertag.addEntry(online.getName());
						online.setPlayerListName("§4§lDueño §e" + online.getDisplayName());
					} else {
						usertag.addEntry(online.getName());
					}

				}

			}
		}.runTaskTimerAsynchronously(this, 0, 20);

		Score score12 = objective.getScore("§6");
		score12.setScore(12);

		Score score11 = objective.getScore("§f» " + ChatColor.GREEN + player.getDisplayName());
		score11.setScore(11);

		Score score10 = objective.getScore("§8");
		score10.setScore(10);

		Score score8 = objective.getScore("§7");
		score8.setScore(8);

		Score score7 = objective.getScore("§fLobby: §a#1");
		score7.setScore(7);

		Score score5 = objective.getScore("§f");
		score5.setScore(5);

		Score score4 = objective.getScore("§fTienda:");
		score4.setScore(4);

		Score score3 = objective.getScore("§f» §aTienda.MineGlow.Net ");
		score3.setScore(3);

		Score score2 = objective.getScore("");
		score2.setScore(2);

		Score score = objective.getScore(ChatColor.YELLOW + "    mc.mineglow.net");
		score.setScore(1);

		scoreboard.clear();
		scoreboard.add("1");

		new BukkitRunnable() {
			@Override
			public void run() {
				Integer i = scoreboard.size();
				if (i == 1) {
					objective.setDisplayName(Colors("&f&lMineGlow"));
					scoreboard.add("A");
				}
				if (i == 2) {
					objective.setDisplayName(Colors("&e&lMineGlow"));
					scoreboard.add("B");
				}
				if (i == 3) {
					objective.setDisplayName(Colors("&6&lMineGlow"));
					scoreboard.clear();
					scoreboard.add("1");
				}
			}
		}.runTaskTimerAsynchronously(this, 0, 5);

		// Scorevar updater

		new BukkitRunnable() {
			@Override
			public void run() {
				String rank = "%vault_rank%";
				String rankph = PlaceholderAPI.setPlaceholders(player, rank);
				String onlinep = "%bungee_total%";
				String onlinepph = PlaceholderAPI.setPlaceholders(player, onlinep);

				rango.setSuffix(ChatColor.GREEN + rankph);
				users.setSuffix(ChatColor.GREEN + onlinepph);
			}
		}.runTaskTimerAsynchronously(this, 0, 20);
		player.setScoreboard(board);
	}
	// end scoreboard

	public static String Colors(String text) {
		return text.replaceAll("&", "§");
	}

	public void addactionbar(final Player p) { // Actionbar animation
		ArrayList<String> actionbar = new ArrayList<String>();
		actionbar.clear();
		actionbar.add("1");

		new BukkitRunnable() {

			@Override
			public void run() {
				Integer i = actionbar.size();

				if (i == 1) {
					ActionBarAPI.sendActionbar(p, "§c§l-50% §eInaguración §b§l➜ §dTienda.MineGlow.Net");
					actionbar.add("A");
				}
				if (i == 2) {
					ActionBarAPI.sendActionbar(p, "§6§l-50% §eInaguración §b§l➜ §dTienda.MineGlow.Net");
					actionbar.add("B");
				}
				if (i == 3) {
					ActionBarAPI.sendActionbar(p, "§b§l-50% §eInaguración §b§l➜ §dTienda.MineGlow.Net");
					actionbar.clear();
					actionbar.add("1");
				}

			}
		}.runTaskTimerAsynchronously(this, 0, 20);

	}

	public void addtablist(final Player p) { // Tablist animation
		ArrayList<String> tablist = new ArrayList<String>();
		tablist.clear();
		tablist.add("1");

		new BukkitRunnable() {

			@Override
			public void run() {

				Integer i = tablist.size();

				if (i == 1) {
					Object h1 = new ChatComponentText("\n§e§lMineGlow Network\n§aMc.MineGlow.Net\n");
					Object f1 = new ChatComponentText(
							"\n§c§l» §9§lTIENDA §etienda.mineglow.net §8| §e@MineGlow §9§lTWITTER §c§l«");
					Tab.Tablist(p, h1, f1);
					tablist.add("A");
				}
				if (i == 2) {
					Object h1 = new ChatComponentText("\n§6§lMineGlow Network\n§aMc.MineGlow.Net\n");
					Object f1 = new ChatComponentText(
							"\n§6§l» §9§lTIENDA §etienda.mineglow.net §8| §e@MineGlow §9§lTWITTER §6§l«");
					Tab.Tablist(p, h1, f1);
					tablist.clear();
					tablist.add("1");
				}

			}

		}.runTaskTimerAsynchronously(this, 0, 20);
	}

	public static void bossbar() { // Bossbar Announcer

		if (plugin.getConfig().getBoolean("BossBarAnnouncer.Enable")) {
			int interval = plugin.getConfig().getInt("BossBarAnnouncer.interval");
			int size = plugin.getConfig().getStringList("BossBarAnnouncer.Messages").size();
			Random random = new Random();

			new BukkitRunnable() {
				public void run() {
					int id = random.nextInt(size);
					for (Player p : Bukkit.getOnlinePlayers()) {
						String message = (String) plugin.getConfig().getStringList("BossBarAnnouncer.Messages").get(id);
						String messageph = (ChatColor.translateAlternateColorCodes('&', message));
						String bossanouncer = PlaceholderAPI.setPlaceholders(p, messageph);

						String bc = plugin.getConfig().getString("BossBarAnnouncer.Color");
						String bs = plugin.getConfig().getString("BossBarAnnouncer.Style");
						int time = plugin.getConfig().getInt("BossBarAnnouncer.Time");

						BossBarAPI.addBar(p, new TextComponent(bossanouncer), BossBarAPI.Color.valueOf(bc),

								BossBarAPI.Style.valueOf(bs),

								1.0F, time, 2L, new BossBarAPI.Property[0]);

					}

				}
			}.runTaskTimerAsynchronously(Main.getInstance(), 0L, interval);

		}

	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.setGameMode(GameMode.ADVENTURE);
		Location spawn = Util.get().stringToLocation(plugin.getConfig().getString("spawn"));
		player.setHealth(20);
		player.setFoodLevel(20);
		JoinItems.giveItems(player);

		addtablist(player);
		addscoreboard(player);
		addactionbar(player);

		if (player.hasPermission("lobbymg.joinmessage")) {

			if (spawn != null) {
				player.getWorld().strikeLightningEffect(spawn);
			}

			String joinmessage = "%vault_rankprefix% §e%player_name% §b¡Se ha unido al lobby!";
			String joinmessageph = PlaceholderAPI.setPlaceholders(event.getPlayer(), joinmessage);

			for (Player online : Bukkit.getOnlinePlayers()) {
				Titles.sendTitle(online, "", joinmessageph);
				online.playSound(online.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
			}

			event.setJoinMessage(joinmessageph);
		} else {
			event.setJoinMessage(null);
		}
		Titles.sendTitle(player, getConfig().getString("Title"), getConfig().getString("SubTitle"));

		if (spawn != null) {
			player.teleport(spawn);
		}
	}

	@EventHandler

	public void onLeave(PlayerQuitEvent event) {
		event.setQuitMessage(null);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {

		Player player = event.getPlayer();
		Location spawn = Util.get().stringToLocation(plugin.getConfig().getString("spawn"));
		if (spawn != null) {
			if (player.getWorld().equals(spawn.getWorld())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onVoidEnter(PlayerMoveEvent e) {
		Player jugador = e.getPlayer();
		Location spawn = Util.get().stringToLocation(plugin.getConfig().getString("spawn"));
		if (spawn != null) {
			if (e.getTo().getBlockY() < -1) {
				jugador.teleport(spawn);
				jugador.playSound(spawn, Sound.ENDERMAN_TELEPORT, 1, 1);
				jugador.getWorld().playEffect(spawn, Effect.ENDER_SIGNAL, 2);
			}
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {

		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Location spawn = Util.get().stringToLocation(plugin.getConfig().getString("spawn"));
			if (spawn != null) {
				if (player.getWorld().equals(spawn.getWorld())) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void foodChangeEvent(FoodLevelChangeEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			Player player = (Player) event.getEntity();
			Location spawn = Util.get().stringToLocation(plugin.getConfig().getString("spawn"));
			if (spawn != null) {
				if (player.getWorld().equals(spawn.getWorld())) {
					event.setCancelled(true);
					if (player.getFoodLevel() < 19.0D) {
						player.setFoodLevel(20);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void WeatherChangeEvent(WeatherChangeEvent event) {
		if (!event.toWeatherState()) {
			return;
		}
		Location spawn = Util.get().stringToLocation(plugin.getConfig().getString("spawn"));
		if (spawn != null) {
			if (event.getWorld().equals(spawn.getWorld())) {
				event.setCancelled(true);
				event.getWorld().setWeatherDuration(0);
				event.getWorld().setThundering(false);
			}
		}
	}

	public void clearEntities() {
		
		Location spawn = Util.get().stringToLocation(plugin.getConfig().getString("spawn"));
		if (spawn != null) {
			
			new BukkitRunnable() {

				@Override
				public void run() {
					for (Entity entity : spawn.getWorld().getEntities()) {
						if (!(entity instanceof LivingEntity)) {
							entity.remove();
							plugin.getServer().getConsoleSender().sendMessage("LIMPIO");
						}
					}
				}
			}.runTaskTimer(plugin, 20, 60);
		}
	}

	public void loadNPCS() {

		if (this.getConfig().isConfigurationSection("NPCs")) {

			for (String npcid : this.getConfig().getConfigurationSection("NPCs").getKeys(false)) {
				String npcskin = this.getConfig().getString("NPCs." + npcid + ".skin");
				String locstring = this.getConfig().getString("NPCs." + npcid + ".loc");

				Location loc = Util.get().stringToLocation(locstring);
				TruenoNPCSkin skin = TruenoNPCSkinBuilder.fromUsername(this, npcskin);
				TruenoNPCApi.createNPC(this, loc, skin);

			}
		} else {
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "You dont have any NPC setted");
		}

	}
}
