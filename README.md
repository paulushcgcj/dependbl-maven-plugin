# Dependbl Maven Plugin

A dependency management plugin used to manage version inside your `pom.xml`.

This plugin was born due to a personal necessity. In one of my projects, I needed to add or update some dependencies in my pom file in an automated way. When one dependency was updated, we needed to update all other projects that were using this dependency, and to achieve that, we used some CI triggers to trigger an automated build passing down some parameters (group, artifact and version) to update, run some tests and commit if all tests passed.

This saved us a bunch of time and effort on updating vital dependencies that required updates to check for breaking changes.


## How to use it

To use this plugin is super simple, first add it to your build plugins and then execute one of the two possible stages.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.paulushc</groupId>
            <artifactId>dependbl-maven-plugin</artifactId>
            <version>x.y.z</version>
        </plugin>
    </plugins>
</build>
```

As you can see, there is no configuration required to be set on this plugin and also this plugin won't be executed at any of the lifecycle phases of maven by default, you have to manually execute it.

### Adding / Updating dependency

Adding a new dependency or updating an existing one is simple as that. all you have to to is execute the following command.

```bash
mvn dependbl:repack -Dgroupid=my.dependency.group -Dartifact=ependency-artifact -Dversion=version.to.use
```

### Removing dependency
```bash
mvn dependbl:unpack -Dgroupid=my.dependency.group -Dartifact=ependency-artifact
```

After any of those two stages, run a `commit` to apply the changes.

```bash
mvn dependbl:commit
```

You have to execute each dependency ***one by one***, but you can do a change and a commit at the same execution. This is to prevent any kind of issue when passing down the dependencies along with versions.