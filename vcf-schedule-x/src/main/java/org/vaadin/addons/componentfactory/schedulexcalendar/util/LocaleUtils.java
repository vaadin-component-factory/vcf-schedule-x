/*
 * Copyright 2025 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.vaadin.addons.componentfactory.schedulexcalendar.util;

import java.util.Locale;
import java.util.Set;

/**
 * Utility class for working with {@link java.util.Locale} objects.
 * <p>
 * This class provides methods to:
 * <ul>
 * <li>Convert {@code Locale} to Schedule-X compatible language tags (e.g., "en-US", "fr-FR")</li>
 * <li>Validate that a given {@code Locale} is supported by Schedule-X</li>
 * <li>Handle edge cases like Serbian with script variants ("sr-Latn-RS", "sr-RS")</li>
 * </ul>
 *
 * @see java.util.Locale
 */
public class LocaleUtils {

  private static final Set<String> SUPPORTED_LOCALES =
      Set.of("ca-ES", "zh-CN", "zh-TW", "hr-HR", "cs-CZ", "da-DK", "nl-NL", "en-GB", "en-US",
          "et-EE", "fi-FI", "fr-FR", "fr-CH", "de-DE", "he-IL", "id-ID", "it-IT", "ja-JP", "ko-KR",
          "ky-KG", "lt-LT", "mk-MK", "pl-PL", "pt-BR", "ro-RO", "ru-RU", "sr-Latn-RS", "sr-RS",
          "sk-SK", "sl-SI", "es-ES", "sv-SE", "tr-TR", "uk-UA");

  /**
   * Validates whether the given {@link Locale} is supported by Schedule-X.
   * <p>
   * If the locale is not supported, throws an {@link IllegalArgumentException} with an informative
   * message.
   *
   * @param locale the {@code Locale} to validate
   * @throws IllegalArgumentException if the locale is not supported by Schedule-X
   */
  public static void validateLocale(Locale locale) {
    String code = toScheduleXLocale(locale);
    if (!SUPPORTED_LOCALES.contains(code)) {
      throw new IllegalArgumentException("InvalidLocaleError: unsupported locale '" + code + "'");
    }
  }

  /**
   * Converts the given {@link Locale} to a Schedule-X compatible language tag (e.g., "en-US",
   * "fr-FR").
   * <p>
   * Special handling is included for Serbian to differentiate between Latin and Cyrillic scripts.
   *
   * @param locale the {@code Locale} to convert (must not be {@code null})
   * @return a language tag string compatible with Schedule-X (e.g., "en-US")
   */
  public static String toScheduleXLocale(Locale locale) {
    // Handles sr-Latn-RS correctly
    if ("sr".equals(locale.getLanguage()) && "Latn".equals(locale.getScript())) {
      return "sr-Latn-" + locale.getCountry();
    }
    return locale.toLanguageTag();
  }

  /**
   * Checks whether the given {@link Locale} is supported by Schedule-X.
   *
   * @param locale the {@code Locale} to check
   * @return {@code true} if the locale is supported, {@code false} otherwise
   */
  public static boolean isSupported(Locale locale) {
    return SUPPORTED_LOCALES.contains(toScheduleXLocale(locale));
  }
}

