This document is licensed under the GNU Free Documentation License,
Version 1.3, 3 November 2008

# Update Log

* this: fa370fa
* bnd: 84553aa

# Changes since bndtools 2.3.0 release

The changes were made to let the build be more in-line with what is delivered
by the bndtools project through its Gradle support (starting with
bndtools 2.4.0.M1).

Below the changes that affect users are detailed.

* The wrapper task now always uses the latest released Gradle version, unless
  unless the ```rootGradleVersion``` is set when running the task.
* The javadoc title, docTitle and windowTitle are now configurable.
* The ```jacocoTestReport``` task is automatically run when either of
  the ```test``` or ```check``` tasks are scheduled to run.
* The jacoco reports are now placed in ```generated/reports/jacoco```.
* The findbugs reports are now placed in ```generated/reports/findbugs```.
* All custom buildscripts were renamed to better reflect that they're hooks,
  when they're invoked and the scope they affect.
* Default tasks are no longer set up.
* The build dependencies cache directory is no longer configurable and is fixed
  to ```cnf/cache```.
* The build properties in ```cnf/build.gradle.properties``` were moved
  to ```gradle.properties```.
* Build dependencies now only support '*.uri' properties (configured
  in ```gradle.properties```), support for the '*.location' properties was
  dropped and the '*.url' properties are now interpreted as URIs, hence the
  name-change. It is also no longer needed to list the bnd jar in these
  properties because it is defined in the ```gradle.properties``` file.
* The template now automatically applies the
  buildscript ```cnf/gradle/template/javaProject.gradle``` to projects that
  hava applied the Gradle Java plugin.
* The bindex and repoindex properties for the jar and the main class are no
  longer configurable.
* All ```gradleBuild...``` properties were renamed and some were removed.
  * The property ```gradleBuildDependenciesCacheDir``` was removed.
  * The property ```gradleBuildBuildProperties``` was removed.
  * The ```gradleBuildLogging...``` properties were removed.
  * The property ```gradleBuildGradleVersion``` was renamed
    to ```rootGradleVersion```.
  * The property ```gradleBuildBndProjectsDefaultTasks``` was renamed
    to ```bnd_defaultTask``` in ```gradle.properties``` and is now a
    comma-separated list of tasks.
  * The property ```gradleBuildNonBndProjectsDefaultTasks``` was renamed
    to ```others_defaultTask``` in ```gradle.properties``` and is now a
    comma-separated list of tasks.
  * The property ```gradleBuildRootProjectDefaultTasks``` was renamed
    to ```root_defaultTask``` in ```gradle.properties``` and is now a
    comma-separated list of tasks.
  * The ```gradleBuildLibsDirName``` property was renamed
    to ```javaLibsDirName```.
  * The ```gradleBuildTest...``` properties were renamed
    to ```javaTest...```.
  * The ```gradleBuildJacoco...``` properties were renamed
    to ```jacoco...```.
  * The ```gradleBuildIndex...``` properties were renamed
    to ```index...```.
  * The ```gradleBuildJavaDoc...``` properties were renamed
    to ```javadoc...```.
  * The ```gradleBuildFindbugs...``` properties were renamed
    to ```findbugs...```.
* The findbugs include and exclude files were moved from ```cnf```
  to ```cnf/findbugs```.
* The official bnd plugin as delivered by the bnd project is now used. This adds
  support for all features that it implements, like setting the Java 8 compiler
  profile (through ```javac.profile``` in a bnd file).
* Many ```bnd...``` properties are no longer set  (as a result of using the
  official bnd plugin).
* Some tasks were renamed (as a result of using the official bnd plugin):

```
bundle     --> jar
bundleTest --> check
```

* A ```name.bndrun``` file will now create an ```export.name```  task
  automatically. The ```export``` task will depend on all such created export
  tasks in the project.
* The ```export``` tasks now put their artifacts in
  the ```generated/distributions``` directory instead of
  the ```generated/export``` directory (as a result of using the official bnd
  plugin).
* Logging is no longer set up.
* The default bnd directories are no longer setup on the project. The
  corresponding properties are

```
bndSrcDir
bndSrcBinDir
bndTestSrcDir
bndTestSrcBinDir
bndTargetDir
```

* The file ```cnf/gradle/bndLayout.gradle``` was removed. Projects usings it
  should manually setup their source sets
  (as described [here](BUILDING-GRADLE.md#AddingJavaProjectsToTheBuild)).
* The property ```in.ant``` is no longer set to indicate a headless build.
  Instead, the ```driver``` or ```gestallt``` macros from bnd can be used.
  For example: setting a different release repository in the gradle build can
  be accomplished by
  setting ```-releaserepo:${if;${driver;gradle};ReleaseCI;Release}``` in the
  file ```cnf/ext/repositories.bnd```.
* The ```jsr14``` compiler target is no longer directly supported.
  Refer to the official bnd plugin for details.
* Some task dependencies were adjusted, see
  the ```cnf/gradle/doc/template.svg``` diagram for details.
