/*
 * GNU LIBRARY GENERAL PUBLIC LICENSE
 * Version 2, June 1991
 */
package nl.pelagic.bndtools.headless.build.plugin.gradle;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.bndtools.api.NamedPlugin;
import org.bndtools.headless.build.manager.api.HeadlessBuildPlugin;
import org.bndtools.versioncontrol.ignores.manager.api.VersionControlIgnoresManager;
import org.osgi.framework.BundleContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;
import aQute.bnd.build.Workspace;
import aQute.bnd.osgi.Constants;

@Component
public class GradleHeadlessBuildPlugin implements HeadlessBuildPlugin {
    private final AtomicReference<VersionControlIgnoresManager> versionControlIgnoresManager = new AtomicReference<VersionControlIgnoresManager>();

    @Reference(type = '?')
    public void setVersionControlIgnoresManager(VersionControlIgnoresManager versionControlIgnoresManager) {
        this.versionControlIgnoresManager.set(versionControlIgnoresManager);
    }

    public void unsetVersionControlIgnoresManager(VersionControlIgnoresManager versionControlIgnoresManager) {
        this.versionControlIgnoresManager.compareAndSet(versionControlIgnoresManager, null);
    }

    @Activate
    public void activate(BundleContext bundleContext) {
        copier = new BundleResourceCopier(bundleContext.getBundle());
    }

    @Deactivate
    public void deactivate() {
        copier = null;
    }

    private BundleResourceCopier copier = null;

    /*
     * HeadlessBuildPlugin
     */

    @Override
    public NamedPlugin getInformation() {
        return new GradleHeadlessBuildPluginInformation();
    }

    @Override
    public void setup(boolean cnf, File projectDir, boolean add, Set<String> enabledIgnorePlugins) throws IOException {
        setup(cnf, projectDir, add, enabledIgnorePlugins, null);
    }

    @Override
    public void setup(boolean cnf, File projectDir, boolean add, Set<String> enabledIgnorePlugins, List<String> warnings) throws IOException {
        if (!cnf) {
            return;
        }

        /* cnf */

        File workspaceRoot = projectDir.getParentFile();

        String baseDir = "templates/root/";
        Collection<File> files1 = copier.addOrRemoveDirectory(workspaceRoot, baseDir, "/", add ? CopyMode.REPLACE : CopyMode.REMOVE);

        baseDir = "templates/cnf/";
        Collection<File> files2 = copier.addOrRemoveDirectory(projectDir, baseDir, "/", add ? CopyMode.REPLACE : CopyMode.REMOVE);

        files1.addAll(files2);

        if (warnings != null) {
            for (File file : files1) {
                String warning;
                if (add) {
                    warning = String.format("Existing Gradle build file %s was overwritten", file);
                } else {
                    warning = String.format("Unable to remove Gradle build file %s", file);
                }

                warnings.add(warning);
            }
        }

        VersionControlIgnoresManager ignoresManager = versionControlIgnoresManager.get();
        if (ignoresManager != null) {
            List<String> ignoredEntries = new LinkedList<String>();
            ignoredEntries.add(ignoresManager.sanitiseGitIgnoreGlob(true, "/.gradle/", true));
            ignoredEntries.add(ignoresManager.sanitiseGitIgnoreGlob(true, "/reports/", true));
            ignoredEntries.add(ignoresManager.sanitiseGitIgnoreGlob(true, Workspace.getDefaults().getProperty(Constants.DEFAULT_PROP_TARGET_DIR), true));

            ignoresManager.addIgnores(enabledIgnorePlugins, workspaceRoot, ignoredEntries);
        }
    }
}