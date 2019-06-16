package org.paulushc;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

@Mojo(name = "repack", requiresDirectInvocation = true, aggregator = true, threadSafe = true)
public class RepackMojo extends AbstractMojo {

  /**
   * @parameter default-value="${project}"
   * @required
   * @readonly
   */
  @Parameter(property = "project", defaultValue = "${project}")
  private MavenProject project;

  /**
   * The groupid of the dependency.
   *
   * @required
   */
  @Parameter(property = "groupid")
  private String groupid;

  /**
   * The artifact of the dependency.
   *
   * @required
   */
  @Parameter(property = "artifact")
  private String artifact;

  /**
   * The version of the dependency.
   *
   * @required
   */
  @Parameter(property = "version")
  private String version;

  /**
   * Execute the mojo
   * @throws MojoExecutionException in case of any failure
   */
  public void execute() throws MojoExecutionException {

    if (groupid == null || groupid.isEmpty()) {
      throw new MojoExecutionException("Parameter groupid is required");
    }

    getLog()
        .info("[dependbl] Groupid: " + groupid);

    if (artifact == null || artifact.isEmpty()) {
      throw new MojoExecutionException("Parameter artifact is required");
    }

    getLog()
        .info("[dependbl] Artifact: " + artifact);

    if (version == null || version.isEmpty()) {
      throw new MojoExecutionException("Parameter version is required");
    }

    getLog()
        .info("[dependbl] Version: " + version);

    File outFile = project.getFile();
    File backupFile = new File(outFile.getParentFile(), outFile.getName() + ".versionsBackup");

    if (backupFile.exists()) {
      throw new MojoExecutionException(
          "There's a change in progress, commit your current change with dependbl:commit first"
      );
    }

    Dependency dpdnc = new Dependency();
    dpdnc.setGroupId(groupid);
    dpdnc.setArtifactId(artifact);
    dpdnc.setVersion(version);

    boolean mustAddDependency = true;
    boolean mustUpdatePom = true;

    for (Dependency dependency : project.getDependencies()) {
      if (
          dependency.getGroupId().equals(groupid)
          && dependency.getArtifactId().equals(artifact)
      ) {
        if (dependency.getVersion().equals(version)) {
          getLog()
              .info(
                  "[dependbl] "
                      + groupid
                      + ":"
                      + artifact
                      + ":"
                      + version
                      + " is already present"
              );
          mustAddDependency = false;
          mustUpdatePom = false;
        } else {
          dependency.setVersion(version);
          mustAddDependency = false;
        }
        break;
      }
    }

    if (mustAddDependency) {
      project.getDependencies().add(dpdnc);
    }

    if (mustUpdatePom) {

      getLog()
          .info("[dependbl] Generating intermediate version of POM file ");

      try {


        Writer wr = new FileWriter(backupFile);
        if (mustAddDependency) {
          project.getOriginalModel().addDependency(dpdnc);
        } else {
          for (Dependency dependency : project.getOriginalModel().getDependencies()) {
            if (
                dependency.getGroupId().equals(groupid)
                    && dependency.getArtifactId().equals(artifact)
            ) {
              if (!dependency.getVersion().equals(version)) {
                dependency.setVersion(version);
              }
              break;
            }
          }
        }

        project.writeOriginalModel(wr);

        getLog()
            .info(
                "[dependbl] Intermediate version of POM file generatec,"
                  + " run a dependbl:commit to persist changes"
            );

      } catch (Exception e) {
        e.printStackTrace();
      }

    }

  }
}
