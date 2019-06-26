package io.github.paulushcgcj.mojos;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import io.github.paulushcgcj.services.ProjectManager;

@Mojo(name = "commit", requiresDirectInvocation = true, threadSafe = true)
public class CommitMojo extends AbstractMojo {

  /**
   * The Maven Project.
   *
   * @since 1.0-alpha-1
   */
  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  private MavenProject project;

  public void execute() throws MojoExecutionException {

    ProjectManager manager = new ProjectManager(getLog(), project);
    manager.updatePom(project.getFile(), manager.backupFileManage(false));

  }

}