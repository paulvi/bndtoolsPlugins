package nl.pelagic.bndtools.headless.build.plugin.gradle.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bndtools.api.NamedPlugin;
import org.bndtools.versioncontrol.ignores.manager.api.VersionControlIgnoresManager;
import org.junit.Ignore;
import org.osgi.service.component.annotations.Component;

@Ignore
@Component
public class MockVersionControlIgnoresManager implements VersionControlIgnoresManager {
    @Override
    public String sanitiseGitIgnoreGlob(boolean rooted, String ignoreGlob, boolean directory) {
        /* trim */
        String newPath = ignoreGlob.trim();

        /* replace all consecutive slashes with a single slash */
        newPath = newPath.replaceAll("/+", "/");

        /* remove all leading slashes */
        newPath = newPath.replaceAll("^/+", "");

        /* remove all trailing slashes */
        newPath = newPath.replaceAll("/+$", "");

        return String.format("%s%s%s", rooted ? "/" : "", newPath, directory ? "/" : "");
    }

    @Override
    public Collection<NamedPlugin> getAllPluginsInformation() {
        throw new IllegalStateException();
    }

    @Override
    public void addIgnores(Set<String> plugins, File dstDir, String ignores) {
        throw new IllegalStateException();
    }

    List<IgnoreRecord> ignores = new LinkedList<>();

    @Override
    public void addIgnores(Set<String> plugins, File dstDir, List<String> ignoredEntries) {
        ignores.add(new IgnoreRecord(plugins, dstDir, ignoredEntries));
        File gitignore = new File(dstDir, ".gitignore");
        try {
            if (!gitignore.exists()) {
                gitignore.createNewFile();
            }
            Files.write(gitignore.toPath(), ignoredEntries, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createProjectIgnores(Set<String> plugins, File projectDir, Map<String,String> sourceOutputLocations, String targetDir) {
        throw new IllegalStateException();
    }

    @Override
    public Set<String> getPluginsForProjectRepositoryProviderId(String repositoryProviderId) {
        throw new IllegalStateException();
    }
}