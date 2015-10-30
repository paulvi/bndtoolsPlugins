package nl.pelagic.bndtools.headless.build.plugin.gradle.test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bndtools.api.NamedPlugin;
import org.bndtools.headless.build.manager.api.HeadlessBuildPlugin;
import org.bndtools.versioncontrol.ignores.manager.api.VersionControlIgnoresManager;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

import aQute.lib.io.IO;
import nl.pelagic.bndtools.headless.build.plugin.gradle.GradleHeadlessBuildPluginInformation;

@SuppressWarnings("restriction")
public class TestGradleHeadlessBuildPlugin {
    Bundle testBundle = null;
    BundleContext testBundleContext = null;

    MockVersionControlIgnoresManager versionControlIgnoresManager;

    HeadlessBuildPlugin impl;

    <T> T getService(Class<T> clazz) throws InterruptedException {
        ServiceTracker<T,T> st = new ServiceTracker<>(testBundleContext, clazz, null);
        st.open();
        return st.waitForService(1000);
    }

    @Before
    public void setup() throws InterruptedException {
        testBundle = FrameworkUtil.getBundle(this.getClass());
        assertThat(testBundle, notNullValue());

        testBundleContext = testBundle.getBundleContext();
        assertThat(testBundleContext, notNullValue());

        impl = getService(HeadlessBuildPlugin.class);
        assertThat(impl, notNullValue());

        versionControlIgnoresManager = (MockVersionControlIgnoresManager) getService(VersionControlIgnoresManager.class);
        assertThat(versionControlIgnoresManager, notNullValue());

        versionControlIgnoresManager.ignores.clear();
    }

    boolean removeAtEnd = true;

    @Test
    public void testGetInformation() throws Exception {
        NamedPlugin information = impl.getInformation();
        assertThat(information, notNullValue());
        assertThat(information.getName(), equalTo(GradleHeadlessBuildPluginInformation.NAME));
        assertThat(information.isEnabledByDefault(), equalTo(GradleHeadlessBuildPluginInformation.ENABLED_BY_DEFAULT));
        assertThat(information.isDeprecated(), equalTo(GradleHeadlessBuildPluginInformation.DEPRECATED));
    }

    @Test
    public void testSetupNonCnf() throws Exception {
        File wsDir = new File("testresources/testws");
        File cnfDir = new File(wsDir, "cnf");
        IO.delete(wsDir);

        boolean cnf = false;
        File projectDir = cnfDir;
        boolean add = true;
        Set<String> enabledIgnorePlugins = new HashSet<>();
        List<String> warnings = new LinkedList<>();

        try {
            impl.setup(cnf, projectDir, add, enabledIgnorePlugins, warnings);

            assertThat(warnings.size(), equalTo(0));

            assertThat(cnfDir.exists(), equalTo(false));
            assertThat(wsDir.exists(), equalTo(false));

            assertThat(versionControlIgnoresManager.ignores.toString(), versionControlIgnoresManager.ignores.isEmpty(), equalTo(true));
        } finally {
            if (removeAtEnd) {
                IO.delete(wsDir);
            }
        }
    }

    @Test
    public void testSetupCnfFirstTimeAdd() throws Exception {
        File wsDir = new File("testresources/testws");
        File cnfDir = new File(wsDir, "cnf");
        IO.delete(wsDir);

        boolean cnf = true;
        File projectDir = cnfDir;
        boolean add = true;
        Set<String> enabledIgnorePlugins = new HashSet<>();
        List<String> warnings = new LinkedList<>();

        try {
            impl.setup(cnf, projectDir, add, enabledIgnorePlugins, warnings);

            assertThat(warnings.toString(), warnings.size(), equalTo(0));

            List<File> generatedFilesWs = Arrays.asList(wsDir.listFiles());
            assertThat(generatedFilesWs.toString(), generatedFilesWs.size(), equalTo(6));
            assertThat(generatedFilesWs.contains(new File(wsDir, ".gitignore")), equalTo(true));
            assertThat(generatedFilesWs.contains(new File(wsDir, "BUILDING-GRADLE.md")), equalTo(true));
            assertThat(generatedFilesWs.contains(new File(wsDir, "build.gradle")), equalTo(true));
            assertThat(generatedFilesWs.contains(new File(wsDir, "cnf")), equalTo(true));
            assertThat(generatedFilesWs.contains(new File(wsDir, "gradle.properties")), equalTo(true));
            assertThat(generatedFilesWs.contains(new File(wsDir, "settings.gradle")), equalTo(true));

            List<File> generatedFilesCnf = Arrays.asList(cnfDir.listFiles());
            assertThat(generatedFilesCnf.size(), equalTo(2));
            assertThat(generatedFilesCnf.contains(new File(cnfDir, "findbugs")), equalTo(true));
            assertThat(generatedFilesCnf.contains(new File(cnfDir, "gradle")), equalTo(true));

            assertThat(versionControlIgnoresManager.ignores.toString(), versionControlIgnoresManager.ignores.size(), equalTo(1));
            IgnoreRecord actual = versionControlIgnoresManager.ignores.get(0);
            List<String> expectedIgnoredEntries = new LinkedList<>();
            expectedIgnoredEntries.add("/.gradle/");
            expectedIgnoredEntries.add("/reports/");
            expectedIgnoredEntries.add("/generated/");
            IgnoreRecord expected = new IgnoreRecord(new HashSet<String>(), wsDir, expectedIgnoredEntries);
            assertThat(actual, equalTo(expected));

            /* reset */

            versionControlIgnoresManager.ignores.clear();

            /* again */

            impl.setup(cnf, projectDir, add, enabledIgnorePlugins, warnings);

            assertThat(warnings.toString(), warnings.size(), equalTo(40));

            generatedFilesWs = Arrays.asList(wsDir.listFiles());
            assertThat(generatedFilesWs.toString(), generatedFilesWs.size(), equalTo(6));
            assertThat(generatedFilesWs.contains(new File(wsDir, ".gitignore")), equalTo(true));
            assertThat(generatedFilesWs.contains(new File(wsDir, "BUILDING-GRADLE.md")), equalTo(true));
            assertThat(generatedFilesWs.contains(new File(wsDir, "build.gradle")), equalTo(true));
            assertThat(generatedFilesWs.contains(new File(wsDir, "cnf")), equalTo(true));
            assertThat(generatedFilesWs.contains(new File(wsDir, "gradle.properties")), equalTo(true));
            assertThat(generatedFilesWs.contains(new File(wsDir, "settings.gradle")), equalTo(true));

            generatedFilesCnf = Arrays.asList(cnfDir.listFiles());
            assertThat(generatedFilesCnf.size(), equalTo(2));
            assertThat(generatedFilesCnf.contains(new File(cnfDir, "findbugs")), equalTo(true));
            assertThat(generatedFilesCnf.contains(new File(cnfDir, "gradle")), equalTo(true));

            assertThat(versionControlIgnoresManager.ignores.toString(), versionControlIgnoresManager.ignores.size(), equalTo(1));
            actual = versionControlIgnoresManager.ignores.get(0);
            expectedIgnoredEntries = new LinkedList<>();
            expectedIgnoredEntries.add("/.gradle/");
            expectedIgnoredEntries.add("/reports/");
            expectedIgnoredEntries.add("/generated/");
            expected = new IgnoreRecord(new HashSet<String>(), wsDir, expectedIgnoredEntries);
            assertThat(actual, equalTo(expected));
        } finally {
            if (removeAtEnd) {
                IO.delete(wsDir);
            }
        }
    }

    @Test
    public void testSetupCnfFirstTimeRemove() throws Exception {
        boolean removeAtEndSaved = removeAtEnd;
        removeAtEnd = false;
        testSetupCnfFirstTimeAdd();
        removeAtEnd = removeAtEndSaved;

        versionControlIgnoresManager.ignores.clear();

        File wsDir = new File("testresources/testws");
        File cnfDir = new File(wsDir, "cnf");

        boolean cnf = true;
        File projectDir = cnfDir;
        boolean add = false;
        Set<String> enabledIgnorePlugins = new HashSet<>();
        List<String> warnings = new LinkedList<>();

        try {
            impl.setup(cnf, projectDir, add, enabledIgnorePlugins, warnings);

            assertThat(warnings.toString(), warnings.size(), equalTo(0));

            List<File> generatedFilesWs = Arrays.asList(wsDir.listFiles());
            assertThat(generatedFilesWs.toString(), generatedFilesWs.size(), equalTo(1));
            assertThat(generatedFilesWs.contains(new File(wsDir, ".gitignore")), equalTo(true));

            assertThat(cnfDir.exists(), equalTo(false));

            assertThat(versionControlIgnoresManager.ignores.toString(), versionControlIgnoresManager.ignores.size(), equalTo(0));

            /* reset */

            versionControlIgnoresManager.ignores.clear();

            /* again */

            impl.setup(cnf, projectDir, add, enabledIgnorePlugins, warnings);

            assertThat(warnings.toString(), warnings.size(), equalTo(0));

            generatedFilesWs = Arrays.asList(wsDir.listFiles());
            assertThat(generatedFilesWs.toString(), generatedFilesWs.size(), equalTo(1));
            assertThat(generatedFilesWs.contains(new File(wsDir, ".gitignore")), equalTo(true));

            assertThat(cnfDir.exists(), equalTo(false));

            assertThat(versionControlIgnoresManager.ignores.toString(), versionControlIgnoresManager.ignores.size(), equalTo(0));
        } finally {
            if (removeAtEnd) {
                IO.delete(wsDir);
            }
        }
    }
}