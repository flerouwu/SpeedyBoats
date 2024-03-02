package de.sivery.speedyboats;

import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class SpeedyBoats extends JavaPlugin {
    FileConfiguration config;
    Logger logger = getLogger();
    Server server = getServer();

    public void onEnable() {
        logger.info("Loading plugin...");
        logger.info("Registering event listener...");
        server.getPluginManager().registerEvents(new BoatListener(this), this);

        logger.info("Loading config...");
        saveResource("config.yml", false);
        config = getConfig();

        ConfigurationSection engines = config.getConfigurationSection("engines");
        if (engines != null) {
            logger.info("Registering crafting recipes...");
            engines.getKeys(false).forEach(key -> {
                ConfigurationSection section = engines.getConfigurationSection(key);
                assert section != null;

                Engine engine = Engine.FromConfig(this, key, section);
                if (engine == null) {
                    logger.info("Something went wrong trying to register " + key);
                } else {
                    logger.info("Registered engine with key " + engine.key);
                }
            });
        } else {
            logger.warning("No engines specified in config!");
        }

        logger.info("Done!");
    }
}
