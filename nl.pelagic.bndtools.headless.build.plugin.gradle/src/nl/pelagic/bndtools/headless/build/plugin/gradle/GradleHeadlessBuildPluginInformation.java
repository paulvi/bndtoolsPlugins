/*
 * GNU LIBRARY GENERAL PUBLIC LICENSE
 * Version 2, June 1991
 */
package nl.pelagic.bndtools.headless.build.plugin.gradle;

import org.bndtools.api.NamedPlugin;

public class GradleHeadlessBuildPluginInformation implements NamedPlugin {
    private static final String NAME = "Gradle (by Pelagic)";
    private static final boolean ENABLED_BY_DEFAULT = false;
    private static final boolean DEPRECATED = false;

    public String getName() {
        return NAME;
    }

    public boolean isEnabledByDefault() {
        return ENABLED_BY_DEFAULT;
    }

    public boolean isDeprecated() {
        return DEPRECATED;
    }
}