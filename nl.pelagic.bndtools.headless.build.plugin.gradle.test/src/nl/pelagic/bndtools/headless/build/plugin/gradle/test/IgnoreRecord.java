package nl.pelagic.bndtools.headless.build.plugin.gradle.test;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;

@Ignore
public class IgnoreRecord {
    public Set<String> plugins;
    public File dstDir;
    public List<String> ignoredEntries;

    public IgnoreRecord(Set<String> plugins, File dstDir, List<String> ignoredEntries) {
        super();
        this.plugins = plugins;
        this.dstDir = dstDir;
        this.ignoredEntries = ignoredEntries;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dstDir == null) ? 0 : dstDir.hashCode());
        result = prime * result + ((ignoredEntries == null) ? 0 : ignoredEntries.hashCode());
        result = prime * result + ((plugins == null) ? 0 : plugins.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IgnoreRecord other = (IgnoreRecord) obj;
        if (dstDir == null) {
            if (other.dstDir != null)
                return false;
        } else if (!dstDir.equals(other.dstDir))
            return false;
        if (ignoredEntries == null) {
            if (other.ignoredEntries != null)
                return false;
        } else if (!ignoredEntries.equals(other.ignoredEntries))
            return false;
        if (plugins == null) {
            if (other.plugins != null)
                return false;
        } else if (!plugins.equals(other.plugins))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("IgnoreRecord [plugins=");
        builder.append(plugins);
        builder.append(", dstDir=");
        builder.append(dstDir);
        builder.append(", ignoredEntries=");
        builder.append(ignoredEntries);
        builder.append("]");
        return builder.toString();
    }
}
