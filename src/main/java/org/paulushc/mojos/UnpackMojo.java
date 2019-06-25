package org.paulushc.mojos;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.paulushc.models.DependencyModel;
import org.paulushc.services.ProjectManager;

import java.util.ArrayList;
import java.util.List;

@Mojo(name = "unpack", requiresDirectInvocation = true, aggregator = true, threadSafe = true)
public class UnpackMojo extends AbstractMojo {

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
  @Parameter(property = "groupid",required = true)
  private String groupid;

  /**
   * The artifact of the dependency.
   *
   * @required
   */
  @Parameter(property = "artifact",required = true)
  private String artifact;

  public void execute() throws MojoExecutionException, MojoFailureException {

    validate();

    List<DependencyModel> dependencies = new ArrayList<>();
    dependencies.add(new DependencyModel(groupid,artifact));

    new ProjectManager(getLog(),project).executeRemoval(dependencies);

  }

  private void validate() throws MojoExecutionException {
    if (groupid == null || groupid.isEmpty())
      throw new MojoExecutionException("Parameter groupid is required");

    getLog().info("[dependbl] Groupid: " + groupid);

    if (artifact == null || artifact.isEmpty())
      throw new MojoExecutionException("Parameter artifact is required");

    getLog().info("[dependbl] Artifact: " + artifact);
  }

}
