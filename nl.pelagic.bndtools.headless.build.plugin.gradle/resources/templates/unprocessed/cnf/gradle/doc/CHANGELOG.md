# Changes since bndtools 2.3.0 release

* The property ```gradleBuildRootProjectDefaultTasks``` was renamed
  to ```root_defaultTask``` and is now a comma-separated list of tasks.
* The bindex and repoindex properties for the jar and the main class can no
  longer be overridden.
* The ```gradleBuildLibsDirName...``` property was renamed
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
* The file ```cnf/build.gradle.properties``` was moved
  to ```cnf/gradle/build.gradle.properties```.
* The findbugs include and exclude files were moved from ```cnf```
  into ```cnf/findbugs```.
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

* A ```name.bndrun``` file will now create a ```export.name```  task
  automatically. The ```export``` will depend on all such created export
  task(s).
* The ```export``` tasks now put their artifacts in
  the ```generated/distributions``` directory instead of
  the ```generated/export``` (as a result of using the official bnd plugin).
* The default log level is now set to ```WARN```.
* Standard output is now captured on log level ```WARN```.
* The default bnd directories are no longer setup on the project. The
  corresponding properties are

```
bndSrcDir
bndSrcBinDir
bndTestSrcDir
bndTestSrcBinDir
bndTargetDir
```

* The property ```gradleBuildNonBndProjectsDefaultTasks``` was renamed
  to ```others_defaultTask``` and is now a comma-separated list of tasks.
* The property ```gradleBuildBndProjectsDefaultTasks``` was renamed
  to ```bnd_defaultTask``` and is now a comma-separated list of tasks.
* The file ```cnf/gradle/bndLayout.gradle``` was removed. Projects usings it
  should manually setup their source sets
  (as described [here](#AddingJavaProjectsToTheBuild)).
* The property ```in.ant``` is no longer set to indicate a headless build.
  Instead, you can now use the ```driver``` of ``gestallt``` macros from bnd.
  For example: setting a different release repository in the gradle build can
  be accomplished by
  setting ```-releaserepo: ${if;${driver;gradle};ReleaseCI;Release}``` in the
  file ```cnf/ext/repositories.bnd```.
* The ```jsr14``` compiler support has changed is no longer directly supported.
  Refer to the official bnd plugin for details.
* Some task dependencies were adjusted, see
  the ```cnf/gradle/doc/template.svg``` graph for details.
