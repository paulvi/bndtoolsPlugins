package nl.pelagic.bndtools.headless.build.plugin.gradle;

public enum CopyMode {
    /**
     * Add if not already present.
     */
    ADD,

    /**
     * Add and overwrite if already present.
     */
    REPLACE,

    /**
     * Remove if present.
     */
    REMOVE,

    /**
     * Do nothing, just check if the file exists.
     */
    CHECK
}