package sayTheSpire;

import java.io.FileReader;
import java.io.FileWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.evacipated.cardcrawl.modthespire.lib.ConfigUtils;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import sayTheSpire.ui.input.InputConfig;

public class STSConfig {

    private static final Logger logger = LogManager.getLogger(STSConfig.class.getName());

    private Toml settingsToml;
    private InputConfig inputConfig;

    public STSConfig() {
        File dir = new File(getDirectoryPath());
        dir.mkdirs();
        this.loadInput();
        this.loadSettings();
    }

    private void loadInput() {
        try {
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(new FileReader(this.getInputFilePath()));
            this.inputConfig = new InputConfig(obj);
            logger.info("Input settings file loaded successfully.");
        } catch (Exception e) {
            logger.info("Issue loading input mappings file.");
            e.printStackTrace();
            this.inputConfig = new InputConfig();
        }
    }

    private void loadSettings() {
        File file = new File(getSettingsFilePath());
        HashMap<String, Object> defaults = this.getDefaults();
        try {
            HashMap<String, Object> fileSettings = (HashMap<String, Object>) new Toml().read(file).toMap();
            merge(defaults, fileSettings);
            this.settingsToml = new Toml().read(new TomlWriter().write(defaults));
            logger.info("Config loaded from existing file.");
        } catch (Exception e) {
            logger.info("No config file found, using defaults.");
            this.settingsToml = new Toml().read(new TomlWriter().write(defaults));
        }
    }

    public static void merge(HashMap<String, Object> base, HashMap<String, Object> merger) {
        for (Map.Entry<String, Object> entry : merger.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (base.containsKey(key) && base.get(key) instanceof HashMap && value instanceof HashMap) {
                merge((HashMap<String, Object>) base.get(key), (HashMap<String, Object>) value);
            } else {
                base.put(key, value);
            }
        }
    }

    public void save() throws IOException {
        try (FileWriter file = new FileWriter(getSettingsFilePath())) {
            TomlWriter writer = new TomlWriter();
            writer.write(this.settingsToml.toMap(), file);
            file.flush();
            logger.info("Successfully wrote settings file.");
        } catch (Exception e) {
            logger.error("Issue writing to settings file.");
            e.printStackTrace();
        }
        try (FileWriter file = new FileWriter(getInputFilePath())) {
            file.write(this.getInputConfig().toJSONObject().toJSONString());
            file.flush();
            logger.info("Successfully wrote input mappings file.");
        } catch (Exception e) {
            logger.error("Error writing to input mappings file.");
            e.printStackTrace();
        }
    }

    public static HashMap<String, Object> getDefaults() {
        HashMap<String, Object> defaults = new HashMap();

        HashMap<String, Object> resourceDefaults = new HashMap();
        resourceDefaults.put("dispose_resource_files", true);
        resourceDefaults.put("unload_native_libs", true);

        HashMap<String, Object> uiDefaults = new HashMap();
        uiDefaults.put("read_positions", true);
        uiDefaults.put("read_banner_text", true);
        uiDefaults.put("read_proceed_text", true);
        HashMap<String, Object> mapDefaults = new HashMap();
        mapDefaults.put("read_reversed_paths", true);

        HashMap<String, Object> combatDefaults = new HashMap();
        combatDefaults.put("block_text", true);
        combatDefaults.put("buff_debuff_text", true);

        HashMap<String, Object> inputDefaults = new HashMap();
        inputDefaults.put("virtual_input", true);

        HashMap<String, Object> advancedDefaults = new HashMap();
        advancedDefaults.put("use_updated_card_description", false);

        defaults.put("resources", resourceDefaults);
        defaults.put("ui", uiDefaults);
        defaults.put("map", mapDefaults);
        defaults.put("combat", combatDefaults);
        defaults.put("input", inputDefaults);
        defaults.put("advanced", advancedDefaults);
        return defaults;
    }

    public static String getDirectoryPath() {
        return ConfigUtils.CONFIG_DIR + File.separator + "sayTheSpire" + File.separator;
    }

    public static String getInputFilePath() {
        return getDirectoryPath() + "input.json";
    }

    public InputConfig getInputConfig() {
        return this.inputConfig;
    }

    public static String getSettingsFilePath() {
        return getDirectoryPath() + "settings.ini";
    }

    public String getString(String key) {
        return this.settingsToml.getString(key);
    }

    public String getString(String key, String defaultValue) {
        return this.settingsToml.getString(key, defaultValue);
    }

    public Long getLong(String key) {
        return this.settingsToml.getLong(key);
    }

    public Long getLong(String key, Long defaultValue) {
        return this.settingsToml.getLong(key, defaultValue);
    }

    public Boolean getBoolean(String key) {
        return this.settingsToml.getBoolean(key);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        return this.settingsToml.getBoolean(key, defaultValue);
    }

    public Double getDouble(String key) {
        return this.settingsToml.getDouble(key);
    }

    public Double getDouble(String key, Double defaultValue) {
        return this.settingsToml.getDouble(key, defaultValue);
    }
}
