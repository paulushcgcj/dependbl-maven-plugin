package org.paulushc;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.WriterFactory;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;

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

    File outFile = project.getFile();
    File backupFile = new File(outFile.getParentFile(), outFile.getName() + ".versionsBackup");
    StringBuilder stringBuilder = new StringBuilder();

    getLog().info("Loading " + backupFile);
    try {
      for (String line : Files
          .readAllLines(backupFile.toPath(), Charset.forName("UTF-8"))) {
        stringBuilder.append(line).append("\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (backupFile.exists()) {
      getLog().info("Accepting all changes to " + outFile);
      try {
        writeFile(outFile,stringBuilder);
        backupFile.delete();
      } catch (IOException e) {
        throw new MojoExecutionException(e.getMessage(), e);
      }
    }else{
      throw new MojoExecutionException("No intermediate file found");
    }
  }

  /**
   * Writes a StringBuilder into a file.
   *
   * @param outFile The file to read.
   * @param input   The contents of the file.
   * @throws IOException when things go wrong.
   */
  protected final void writeFile(File outFile, StringBuilder input)
      throws IOException {
    Writer writer = WriterFactory.newXmlWriter(outFile);
    try {
      IOUtil.copy(input.toString(), writer);
    } finally {
      IOUtil.close(writer);
    }
  }
}