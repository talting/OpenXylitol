package cc.xylitol.config;

import cc.xylitol.config.configs.FriendConfig;
import cc.xylitol.config.configs.HudConfig;
import cc.xylitol.config.configs.ModuleConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectArrayList;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cc.xylitol.Client.mc;
import static java.nio.file.Files.walk;

/**
 * @author ChengFeng
 * @since 2023/3/19
 */
public class ConfigManager {
    public static final List<Config> configs = new ArrayList<>();
    public static final File dir = new File(mc.mcDataDir, "Xylitol");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ConfigManager() {
        if (!dir.exists()) {
            dir.mkdir();
        }

        configs.add(new ModuleConfig());
        configs.add(new HudConfig());
        configs.add(new FriendConfig());
    }

    public void loadConfig(String name) {
        File file = new File(dir, name);
        if (file.exists()) {
            System.out.println("Loading config: " + name);
            for (Config config : configs) {
                if (config.getName().equals(name)) {
                    try {
                        config.loadConfig(JsonParser.parseReader(new FileReader(file)).getAsJsonObject());
                        break;
                    } catch (FileNotFoundException e) {
                        System.out.println("Failed to load config: " + name);
                        e.printStackTrace();
                        break;
                    }
                }
            }
        } else {
            System.out.println("Config " + name + " doesn't exist, creating a new one...");
            saveConfig(name);
        }
    }

    public void loadUserConfig(String name) {
        File file = new File(dir, name);
        if (file.exists()) {
            System.out.println("Loading config: " + name);
            for (Config config : configs) {
                if (config.getName().equals("modules.json")) {
                    try {
                        config.loadConfig(JsonParser.parseReader(new FileReader(file)).getAsJsonObject());
                        break;
                    } catch (FileNotFoundException e) {
                        System.out.println("Failed to load config: " + name);
                        e.printStackTrace();
                        break;
                    }
                }
            }
        } else {
            System.out.println("Config " + name + " doesn't exist, creating a new one...");
            saveUserConfig(name);
        }
    }

    public void saveConfig(String name) {
        File file = new File(dir, name);

        try {
            System.out.println("Saving config: " + name);
            file.createNewFile();
            for (Config config : configs) {
                if (config.getName().equals(name)) {
                    FileUtils.writeByteArrayToFile(file, gson.toJson(config.saveConfig()).getBytes(StandardCharsets.UTF_8));
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to save config: " + name);
        }
    }

    public void saveUserConfig(String name) {
        File file = new File(dir, name);

        try {
            System.out.println("Saving config: " + name);
            file.createNewFile();
            for (Config config : configs) {
                if (config.getName().equals("modules.json")) {
                    FileUtils.writeByteArrayToFile(file, gson.toJson(config.saveConfig()).getBytes(StandardCharsets.UTF_8));
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to save config: " + name);
        }
    }

    public void loadAllConfig() {
        System.out.println("Loading all configs...");
        configs.forEach(it -> loadConfig(it.getName()));
    }

    public void saveAllConfig() {
        System.out.println("Saving all configs...");
        configs.forEach(it -> saveConfig(it.getName()));
    }

    private static final String EXTENSION = ".json";

    public List<String> getConfigs() {
        Stream<Path> filesStream;

        try {
            filesStream = walk(dir.toPath());
        } catch (IOException e) {
            return Collections.emptyList();
        }

        return filesStream // @off
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .map(Path::toString)
                .filter(s -> s.endsWith(EXTENSION))
                .map(s -> s.substring(0, s.length() - EXTENSION.length()))
                .collect(Collectors.toCollection(ObjectArrayList::new)); // @on
    }

}
