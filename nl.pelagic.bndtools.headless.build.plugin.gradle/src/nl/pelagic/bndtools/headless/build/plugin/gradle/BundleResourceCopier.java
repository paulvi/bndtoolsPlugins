/*
 * GNU LIBRARY GENERAL PUBLIC LICENSE
 * Version 2, June 1991
 */
package nl.pelagic.bndtools.headless.build.plugin.gradle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.Bundle;

import aQute.lib.io.IO;

public class BundleResourceCopier {
    /** the bundle holding the resources */
    private Bundle bundle = null;

    /**
     * Constructor
     *
     * @param bundle
     *            the bundle holding the resources
     */
    public BundleResourceCopier(Bundle bundle) {
        super();
        this.bundle = bundle;
    }

    private void addOrRemoveDirectoryRecursive(File dstDir, String bundleDir, String relativePath, CopyMode mode, List<File> affected) throws IOException {
        String resourcePath = formatBundleEntryPath(new File(bundleDir, relativePath).getPath());
        Enumeration<String> bundleEntryPaths = bundle.getEntryPaths(resourcePath);
        if (bundleEntryPaths != null) {
            List<String> resourcePathEntries = Collections.list(bundleEntryPaths);
            Collections.sort(resourcePathEntries, Collections.reverseOrder());
            for (String resourcePathEntry : resourcePathEntries) {
                if (resourcePathEntry.startsWith(bundleDir)) {
                    resourcePathEntry = resourcePathEntry.substring(bundleDir.length());
                }

                if (resourcePathEntry.endsWith("/")) {
                    addOrRemoveDirectoryRecursive(dstDir, bundleDir, resourcePathEntry, mode, affected);
                } else {
                    affected.addAll(addOrRemoveFile(dstDir, bundleDir, resourcePathEntry, mode));
                }
            }
        }
    }

    /**
     * Add/remove a file (backed by a bundle resource) to/from a directory.
     *
     * @param dstDir
     *            the destination directory under which to add/remove a file
     * @param bundleDir
     *            the bundle directory under which the resource is located
     * @param relativePath
     *            the path of the resource (relative to bundleDir) in the bundle. The resource will be added/removed
     *            to/from the same path relative to dstDir. This parameter must only hold the path of a file.
     * @param mode
     * @throws IOException
     *             when relativePath is null or empty, when the resource could not be found in the bundle, when the
     *             directory holding the file could not be created (when add is true), or when the file could not be
     *             removed (when add is false)
     */
    public Collection<File> addOrRemoveFile(File dstDir, String bundleDir, String relativePath, CopyMode mode) throws IOException {
        List<File> affected = new LinkedList<>();

        if (relativePath == null || relativePath.length() == 0) {
            throw new IOException("Resource relative path can't be empty");
        }

        File dstFile = new File(dstDir, relativePath);
        File relativeDstFile = new File(relativePath);
        boolean dstFileExists = dstFile.exists();

        switch (mode) {
        case REMOVE :
            Files.deleteIfExists(dstFile.toPath());
            break;

        case CHECK :
            if (dstFileExists) {
                affected.add(relativeDstFile.getAbsoluteFile());
            }
            break;

        case ADD :
        case REPLACE :
            if (dstFileExists && !dstFile.isFile()) {
                throw new IOException("Target path exists and is not a plain file: " + dstFile);
            }

            if (dstFileExists && mode == CopyMode.ADD) {
                affected.add(relativeDstFile.getAbsoluteFile());
            } else {
                /* !exists || REPLACE */
                String resourcePath = formatBundleEntryPath(new File(bundleDir, relativePath).getPath());
                URL resourceUrl = bundle.getEntry(resourcePath);
                if (resourceUrl == null) {
                    throw new IOException("Resource " + resourcePath + " not found in bundle " + bundle.getSymbolicName());
                }

                File dstFileDir = dstFile.getParentFile();
                if (dstFileDir != null) {
                    Files.createDirectories(dstFileDir.toPath());
                }
                IO.copy(resourceUrl, dstFile);
                if (mode == CopyMode.REPLACE) {
                    affected.add(dstFile.getAbsoluteFile());
                }
            }
            break;

        default :
            throw new IllegalArgumentException("Unknown copy mode " + mode);
        }

        return affected;
    }

    /**
     * Add/remove files (backed by bundle resources) to/from a directory.
     *
     * @param dstDir
     *            the destination directory under which to add/remove files
     * @param bundleDir
     *            the bundle directory under which the resources are located
     * @param relativePaths
     *            the paths of the resources (relative to bundleDir) in the bundle. The resources will be added/removed
     *            to/from the same paths relative to dstDir. This parameter must only hold paths of files.
     * @param mode
     * @throws IOException
     *             when a relative path is null or empty, when a resource could not be found in the bundle, when a
     *             directory holding a file could not be created (when add is true), or when a file could not be removed
     *             (when add is false)
     */
    public Collection<File> addOrRemoveFiles(File dstDir, String bundleDir, String[] relativePaths, CopyMode mode) throws IOException {
        List<File> affected = new LinkedList<>();
        for (String templatePath : relativePaths) {
            affected.addAll(addOrRemoveFile(dstDir, bundleDir, templatePath, mode));
        }
        return affected;
    }

    /**
     * Recursively add/remove a directory and its files (backed by bundle resources) to/from a directory.
     *
     * @param dstDir
     *            the destination directory under which to add/remove the directory and its files
     * @param bundleDir
     *            the bundle directory under which the resources are located
     * @param relativePath
     *            the path of the resources (relative to bundleDir) in the bundle. The resources will be recursively
     *            added/removed to/from the same paths relative to dstDir. This parameter must only hold a paths of a
     *            directory. When null then "/" will be used.
     * @param mode
     * @return A list of existing files that were/would have been affected.
     * @throws IOException
     *             when a relative path is null or empty, when a resource could not be found in the bundle, when a
     *             directory holding a file could not be created (if add is true), or when a file could not be removed
     *             (when add is false)
     */
    public Collection<File> addOrRemoveDirectory(File dstDir, String bundleDir, String relativePath, CopyMode mode) throws IOException {
        List<File> affected = new LinkedList<>();

        String bundleDirFixed = bundleDir.replaceAll("^/+", "");
        if (!bundleDirFixed.endsWith("/")) {
            bundleDirFixed = bundleDirFixed + "/";
        }

        String relativePathFixed = relativePath;
        if (relativePathFixed == null) {
            relativePathFixed = "/";
        }
        if (!relativePathFixed.endsWith("/")) {
            relativePathFixed = relativePathFixed + "/";
        }

        addOrRemoveDirectoryRecursive(dstDir, bundleDirFixed, relativePathFixed, mode, affected);
        return affected;
    }

    /**
     * Recursively add/remove directories and their files (backed by bundle resources) to/from a directory.
     *
     * @param dstDir
     *            the destination directory under which to add/remove directories and files
     * @param bundleDir
     *            the bundle directory under which the resources are located
     * @param relativePaths
     *            the paths of the resources (relative to bundleDir) in the bundle. The resources will be recursively
     *            added/removed to/from the same paths relative to dstDir. This parameter must only hold paths of
     *            directories.
     * @param mode
     * @throws IOException
     *             when a relative path is null or empty, when a resource could not be found in the bundle, when a
     *             directory holding a file could not be created (when add is true), or when a file could not be removed
     *             (when add is false)
     */
    public void addOrRemoveDirectories(File dstDir, String bundleDir, String[] relativePaths, CopyMode mode) throws IOException {
        for (String templatePath : relativePaths) {
            addOrRemoveDirectory(dstDir, bundleDir, templatePath, mode);
        }
    }

    private String formatBundleEntryPath(String path) {
        // Bundle.getEntry* doesn't grok backslashes
        if (File.separatorChar != '\\') {
            return path;
        }

        return path.replace('\\', '/');
    }
}