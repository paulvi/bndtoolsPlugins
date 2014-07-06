# Building

In order to build this project you need a parallel checkout of bndtools and
this project. Such a checkout will look like:

```
<root>
  <bndtools>
  <bndtoolsPlugins>
```

The reason for this is that this project has symbolic links into the parallel
bndtools checkout.

To build the project, run

```
./gradlew build dist
```

