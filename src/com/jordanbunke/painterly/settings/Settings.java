package com.jordanbunke.painterly.settings;

import com.jordanbunke.delta_time.error.GameError;
import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.utility.Version;
import com.jordanbunke.json.JSONBuilder;
import com.jordanbunke.json.JSONPair;
import com.jordanbunke.json.JSONReader;
import com.jordanbunke.painterly.ProgramInfo;
import com.jordanbunke.painterly.resources.lang.Language;
import com.jordanbunke.painterly.util.Colors;
import com.jordanbunke.painterly.util.Constants;
import com.jordanbunke.painterly.util.EnumUtils;
import com.jordanbunke.painterly.util.OSUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.jordanbunke.painterly.settings.Settings.SettingID.*;

public final class Settings {
    private static final Path SETTINGS_FILE;

    private static final Map<SettingID, Setting<?>> settingsMap;

    public enum SettingID {
        SET_ID_VERSION,
        SET_ID_FULLSCREEN,
        SET_ID_LANGUAGE,
        SET_ID_THEME
        // TODO - additional settings
        ;

        private static final String prefix = "SET_ID_";

        static SettingID fromString(final String key) {
            return EnumUtils.stream(SettingID.class)
                    .filter(s -> s.get().equals(key))
                    .findAny().orElse(null);
        }

        public String get() {
            if (this == SET_ID_VERSION)
                return "last-opened-version";

            return EnumUtils.formattedNameNoPrefix(this, prefix);
        }
    }

    static {
        SETTINGS_FILE = determineSettingsFile();

        settingsMap = new HashMap<>();
        initialize();

        Runtime.getRuntime().addShutdownHook(new Thread(Settings::write));
    }

    private static Path determineSettingsFile() {
        final Path internal = Constants.INTERNAL_SETTINGS_FILEPATH;
        final String name = ProgramInfo.PROGRAM_NAME,
                unixFriendlyName = name.toLowerCase().replace(" ", "-");

        if (OSUtils.isWindows()) {
            final String appData = System.getenv("APPDATA");
            return Path.of(appData, name).resolve(internal);
        }

        if (OSUtils.isMacOS()) {
            return Path.of(System.getProperty("user.home"),
                            "Library", "Application Support", unixFriendlyName)
                    .resolve(internal);
        }

        // Assume Linux/Unix
        final String xdgConfig = System.getenv("XDG_CONFIG_HOME");
        if (xdgConfig != null && !xdgConfig.isBlank())
            return Path.of(xdgConfig, unixFriendlyName).resolve(internal);
        else
            return Path.of(System.getProperty("user.home"),
                    ".config", unixFriendlyName).resolve(internal);
    }

    private static void initialize() {
        addSetting(new Setting<>(Version.class, SET_ID_VERSION,
                Version::parse, new Version(1, 0, 0)));
        addSetting(new Setting<>(Boolean.class, SET_ID_FULLSCREEN,
                Boolean::parseBoolean, false));
        addSetting(new Setting<>(Language.class, SET_ID_LANGUAGE,
                Language::fromCode, Objects::nonNull,
                Language::code, Language.ENGLISH));
        addSetting(new Setting<>(Colors.Theme.class, SET_ID_THEME,
                Colors.Theme::fromID, Objects::nonNull,
                Colors.Theme::id, Colors.Theme.DEFAULT));
        // TODO - initialize additional settings
    }

    private static <T> void addSetting(final Setting<T> setting) {
        settingsMap.put(setting.id, setting);
    }

    public static void read() {
        final String file = FileIO.readFile(SETTINGS_FILE);

        if (file == null)
            return;

        final JSONPair[] pairs = JSONReader.readObject(file);

        if (pairs == null)
            return;

        for (JSONPair pair : pairs) {
            final String key = pair.key();
            final SettingID id = SettingID.fromString(key);

            if (settingsMap.containsKey(id)) {
                final String valueString = String.valueOf(pair.value());
                final Setting<?> setting = settingsMap.get(id);

                setting.read(valueString);
            }
        }
    }

    public static void write() {
        final Path settingsFolder = SETTINGS_FILE.getParent();

        if (!settingsFolder.toFile().exists())
            FileIO.safeMakeDirectory(settingsFolder);
        else if (!settingsFolder.toFile().isDirectory()) {
            try {
                Files.delete(settingsFolder);
                FileIO.safeMakeDirectory(settingsFolder);
            } catch (IOException ioe) {
                GameError.send("Couldn't delete file at " + settingsFolder +
                        " needed to clear space for the settings folder. " +
                        "Could not write " + ProgramInfo.PROGRAM_NAME +
                        " settings.");
                return;
            }
        }

        final JSONBuilder jb = new JSONBuilder();

        settingsMap.keySet().stream()
                .sorted(Comparator.comparing(SettingID::get))
                .filter(id -> settingsMap.get(id).value != null)
                .map(id -> {
                    final String key = id.get();
                    final Setting<?> setting = settingsMap.get(id);
                    final Object value = setting.value;

                    if (validJSONDataType(value))
                        return new JSONPair(key, value);

                    return new JSONPair(key, setting.writer.apply(value));
                }).forEach(jb::add);

        FileIO.writeFile(SETTINGS_FILE, jb.write());
    }

    private static boolean validJSONDataType(final Object value) {
        return value == null || value instanceof Double ||
                value instanceof Integer || value instanceof Boolean;
    }

    public static void reset(final SettingID id) {
        if (settingsMap.containsKey(id))
            settingsMap.get(id).reset();
    }

    public static void set(final SettingID id, final Object value) {
        if (settingsMap.containsKey(id))
            settingsMap.get(id).set(value);
    }

    public static <T> T get(final SettingID id, final Class<T> type) {
        if (!settingsMap.containsKey(id))
            return null;

        final Setting<?> setting = settingsMap.get(id);

        if (type.isAssignableFrom(setting.type))
            return type.cast(setting.get());

        return null;
    }

    public static <T> T getDefaultValue(final SettingID id, final Class<T> type) {
        if (!settingsMap.containsKey(id))
            return null;

        final Setting<?> setting = settingsMap.get(id);

        if (type.isAssignableFrom(setting.type))
            return type.cast(setting.getDefaultValue());

        return null;
    }

//    private static <T> T retrieveValue(
//            final SettingID id,
//            final Class<T> type,
//            final Function<Setting<T>, T> getter
//    ) {
//        if (!settingsMap.containsKey(id))
//            return null;
//
//        final Setting<?> setting = settingsMap.get(id);
//
//        if (type.isAssignableFrom(setting.type)) {
//            final Setting<T> typedSetting = type.cast(setting);
//            return getter.apply(typedSetting);
//        }
//
//        return null;
//    }

    private static class Setting<T> {
        private final Class<T> type;
        private final SettingID id;
        private final Function<String, T> parser;
        private final Predicate<T> validator;
        private final Function<Object, String> writer;
        private final T defaultValue;
        private T value;

        Setting(
                final Class<T> type, final SettingID id,
                final Function<String, T> parser,
                final Predicate<T> validator,
                final Function<T, String> writer,
                final T defaultValue
        ) {
            this.type = type;
            this.id = id;
            this.parser = parser;
            this.validator = validator;
            this.writer = o -> {
                if (this.type.isInstance(o)) {
                    final T cast = this.type.cast(o);
                    return writer.apply(cast);
                }

                return String.valueOf(o);
            };
            this.defaultValue = defaultValue;

            value = defaultValue;
        }

        Setting(
                final Class<T> type, final SettingID id,
                final Function<String, T> parser, final T defaultValue
        ) {
            this(type, id, parser, Objects::nonNull, String::valueOf, defaultValue);
        }

        private void read(final String valueString) {
            set(parser.apply(valueString));
        }

        private void set(final Object value) {
            if (type.isInstance(value)) {
                final T cast = type.cast(value);

                if (validator.test(cast))
                    this.value = cast;
            }
        }

        private void reset() {
            value = defaultValue;
        }

        private T get() {
            return value;
        }

        private T getDefaultValue() {
            return defaultValue;
        }

        @Override
        public String toString() {
            return type.getSimpleName() + " " + id + " = " + writer.apply(value);
        }
    }
}
