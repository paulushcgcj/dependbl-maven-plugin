package io.github.paulushcgcj.mojos;

import io.github.paulushcgcj.models.DependencyModel;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import io.github.paulushcgcj.services.ProjectManager;

import java.util.ArrayList;
import java.util.List;

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
  @Parameter(property = "groupid", required = true)
  private String groupid;

  /**
   * The artifact of the dependency.
   *
   * @required
   */
  @Parameter(property = "artifact", required = true)
  private String artifact;

  /**
   * The version of the dependency.
   *
   * @required
   */
  @Parameter(property = "version", required = true)
  private String version;

  /**
   * The exclusion of the dependency.
   *
   * @required
   */
  @Parameter(property = "exclusion")
  private String[] excludes;

  /**
   * Execute the mojo
   *
   * @throws MojoExecutionException in case of any failure
   */
  public void execute() throws MojoExecutionException, MojoFailureException {

    validate();

    DependencyModel dependencyModel = new DependencyModel(groupid, artifact, version);
    dependencyModel.setExcludes(excludes);

    List<DependencyModel> dependencies = new ArrayList<>();
    dependencies.add(dependencyModel);

    new ProjectManager(getLog(),project).executeAdd(dependencies);

  }

  private void validate() throws MojoExecutionException {
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
  }



}
