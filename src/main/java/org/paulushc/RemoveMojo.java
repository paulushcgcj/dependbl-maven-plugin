package org.paulushc;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

@Mojo(name = "unpack", requiresDirectInvocation = true, aggregator = true, threadSafe = true)
public class RemoveMojo  extends AbstractMojo {

  /**
   * @parameter default-value="${project}"
   * @required
   * @readonly
   */
  @Parameter(property = "project", defaultValue = "${project}")
  private MavenProject project;

  /**
   * The groupid of the dependency.
   * @required
   */
  @Parameter(property = "groupid")
  private String groupid;

  /**
   * The artifact of the dependency.
   * @required
   */
  @Parameter(property = "artifact")
  private String artifact;

  public void execute() throws MojoExecutionException, MojoFailureException {

    if (groupid == null || groupid.isEmpty())
      throw new MojoExecutionException("Parameter groupid is required");

    getLog()
        .info("[dependbl] Groupid: " + groupid);

    if (artifact == null || artifact.isEmpty())
      throw new MojoExecutionException("Parameter artifact is required");

    getLog()
        .info("[dependbl] Artifact: " + artifact);

    File outFile = project.getFile();
    File backupFile = new File(outFile.getParentFile(), outFile.getName() + ".versionsBackup");

    if(backupFile.exists()){
      throw new MojoExecutionException("There is a change in progress, please commit your current change with dependbl:commit first");
    }

    Dependency dependencyToRemove = null;
    boolean mustRemoveDependency = false;

    for (Dependency dependency : project.getDependencies()) {
      if (
          dependency.getGroupId().equals(groupid) &&
              dependency.getArtifactId().equals(artifact)
      ) {
        mustRemoveDependency = true;
        dependencyToRemove = dependency;
        break;
      }
    }

    if (mustRemoveDependency) {
      project.getDependencies().remove(dependencyToRemove);
    }else{
      getLog()
          .info("[dependbl] No artifact " + groupid+":"+artifact+" has been found");
      throw new MojoFailureException("No artifact " + groupid+":"+artifact+" has been found");
    }

    if (mustRemoveDependency) {

      getLog()
          .info("[dependbl] Generating intermediate version of POM file ");

      try {


        Writer wr = new FileWriter(backupFile);
        {
          for (Dependency dependency : project.getOriginalModel().getDependencies()){
            if (
                dependency.getGroupId().equals(groupid) &&
                    dependency.getArtifactId().equals(artifact)
            ) {
              dependencyToRemove = dependency;
              break;
            }
          }
          project.getOriginalModel().removeDependency(dependencyToRemove);
        }

        project.writeOriginalModel(wr);

        getLog()
            .info("[dependbl] Intermediate version of POM file generatec, run a dependbl:commit to persist changes");

      } catch (Exception e) {
        e.printStackTrace();
      }

    }

  }
}
