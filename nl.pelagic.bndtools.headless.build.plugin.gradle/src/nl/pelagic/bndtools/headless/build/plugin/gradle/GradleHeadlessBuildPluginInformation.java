/*
 * GNU LIBRARY GENERAL PUBLIC LICENSE
 * Version 2, June 1991
 */
package nl.pelagic.bndtools.headless.build.plugin.gradle;

import org.bndtools.api.NamedPlugin;

public class GradleHeadlessBuildPluginInformation implements NamedPlugin {
    public static final String NAME = "Gradle (by Pelagic)";
    public static final boolean ENABLED_BY_DEFAULT = false;
    public static final boolean DEPRECATED = false;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isEnabledByDefault() {
        return ENABLED_BY_DEFAULT;
    }

    @Override
    public boolean isDeprecated() {
        return DEPRECATED;
    }
}