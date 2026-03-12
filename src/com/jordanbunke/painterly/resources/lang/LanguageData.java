package com.jordanbunke.painterly.resources.lang;

import com.jordanbunke.delta_time.error.GameError;
import com.jordanbunke.json.JSONPair;
import com.jordanbunke.json.JSONReader;
import com.jordanbunke.painterly.resources.ResourceCategory;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.resources.ResourceReader;
import com.jordanbunke.painterly.resources.ResourceVariables;
import com.jordanbunke.painterly.settings.Settings;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static com.jordanbunke.painterly.settings.Settings.SettingID.SET_ID_LANGUAGE;

public final class LanguageData {
    private static final Path TEXT_FOLDER = Path.of("text");
    private static final Map<Language, LanguageData> languageDataMap;

    static {
        languageDataMap = new HashMap<>();
        reload();
    }

    /**
     * Invoked when the language setting is updated
     * */
    public static void reload() {
        languageDataMap.clear();

        for (Language l : Language.values())
            languageDataMap.put(l, new LanguageData(l));
    }

    public static String retrieveTooltip(final ResourceCode resourceCode) {
        return retrieve(resourceCode, ResourceCategory.TOOLTIP, true);
    }

    public static String retrieveUIText(final ResourceCode resourceCode) {
        return retrieve(resourceCode, ResourceCategory.UI_TEXT, true);
    }

    public static String retrieveValue(final ResourceCode resourceCode) {
        return retrieve(resourceCode, ResourceCategory.VALUE, false);
    }

    public static String retrieve(
            final ResourceCode resourceCode,
            final ResourceCategory resourceCategory,
            final boolean requiresParsing
    ) {
        final Language l = getCurrentLanguage();
        final String unparsed = retrieveFromLanguage(l, resourceCode, resourceCategory);
        return requiresParsing
                ? ResourceVariables.parse(unparsed) : unparsed;
    }

    private static String retrieveFromLanguage(
            final Language l, final ResourceCode resourceCode,
            final ResourceCategory resourceCategory
    ) {
        final LanguageData dataset = languageDataMap.get(l);
        final Map<String, String> map = dataset.mapFromCategory(resourceCategory);
        final String resourceID = resourceCode.id();

        if (map.containsKey(resourceID))
            return map.get(resourceID);
        else {
            final Language defaultLanguage = getDefaultLanguage();
            if (!l.equals(defaultLanguage))
                return retrieveFromLanguage(defaultLanguage,
                        resourceCode, resourceCategory);

            // TODO - error case
            return null;
        }
    }

    private static Language getDefaultLanguage() {
        return Settings.getDefaultValue(SET_ID_LANGUAGE, Language.class);
    }

    private static Language getCurrentLanguage() {
        return Settings.get(SET_ID_LANGUAGE, Language.class);
    }

    private final Language language;
    private final Map<ResourceCategory, Map<String, String>> categoryMaps;

    LanguageData(final Language language) {
        this.language = language;

        categoryMaps = new HashMap<>();

        for (ResourceCategory category : ResourceCategory.values()) {
            categoryMaps.put(category, new HashMap<>());
            read(category);
        }
    }

    private void read(final ResourceCategory category) {
        final Path file = TEXT_FOLDER.resolve(language.code() + category.suffix() + ".json");
        final Map<String, String> map = categoryMaps.get(category);

        try {
            final String content = ResourceReader.read(file);
            final JSONPair[] pairs = JSONReader.readObject(content);

            for (JSONPair pair : pairs)
                map.put(pair.key(), pair.value().toString());
        } catch (Exception e) {
            GameError.send("Unable to read the " + language.formattedName() +
                    " " + category.formattedName() + " file");
        }
    }

    private Map<String, String> mapFromCategory(final ResourceCategory category) {
        return categoryMaps.get(category);
    }
}
