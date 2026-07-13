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

    public static String readChangelog() {
        return readFile(getCurrentLanguage(), ResourceCategory.CHANGELOG);
    }

    public static String readLicense() {
        return readFile(getCurrentLanguage(), ResourceCategory.LICENSE);
    }

    public static String readRoadmap() {
        return readFile(getCurrentLanguage(), ResourceCategory.ROADMAP);
    }

    public static String retrieveTooltip(final ResourceCode resourceCode) {
        return retrieveFromJSON(resourceCode, ResourceCategory.TOOLTIP, true);
    }

    public static String retrieveUIText(final ResourceCode resourceCode) {
        return retrieveFromJSON(resourceCode, ResourceCategory.UI_TEXT, true);
    }

    public static String retrieveValue(final ResourceCode resourceCode) {
        return retrieveFromJSON(resourceCode, ResourceCategory.VALUE, false);
    }

    private static String retrieveFromJSON(
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

        for (ResourceCategory category : ResourceCategory.jsonCategories()) {
            categoryMaps.put(category, new HashMap<>());
            readJSONCategory(category);
        }
    }

    private void readJSONCategory(final ResourceCategory category) {
        final String content = readFile(language, category);
        final Map<String, String> map = categoryMaps.get(category);
        final JSONPair[] pairs = JSONReader.readObject(content);

        if (pairs == null)
            return;

        for (JSONPair pair : pairs)
            map.put(pair.key(), pair.value().toString());
    }

    private Map<String, String> mapFromCategory(final ResourceCategory category) {
        return categoryMaps.get(category);
    }

    private static String readFile(
            final Language language, final ResourceCategory category
    ) {
        final Path file = TEXT_FOLDER.resolve(
                Path.of(language.code(), category.filename()));

        try {
            return ResourceReader.read(file);
        } catch (NullPointerException npe) {
            GameError.send("Unable to read the " + language.formattedName() +
                    " " + category.formattedName() + " file");
        }

        return "";
    }
}
