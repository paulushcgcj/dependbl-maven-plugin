package io.github.paulushcgcj.services;

import io.github.paulushcgcj.models.DependencyModel;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.WriterFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

public class ProjectManager {

  private Log log;
  private MavenProject project;
  private DependencyManager manager;

  public ProjectManager(Log log, MavenProject project) {
    this.log = log;
    this.project = project;
    this.manager = new DependencyManager(log);
  }

  public void executeAdd(List<DependencyModel> dependencies) throws MojoExecutionException, MojoFailureException {
    File backupFile = backupFileManage();

    for (DependencyModel dependencyModel : dependencies) {
      manager.addToList(project.getDependencies(), dependencyModel);
      manager.addToList(project.getOriginalModel().getDependencies(), dependencyModel);
    }

    //After the original model was changed, persist it
    writeOriginalModel(backupFile);
  }


  public void executeRemoval(List<DependencyModel> dependencies) throws MojoExecutionException, MojoFailureException {
    File backupFile = backupFileManage();

    for(DependencyModel dependencyModel : dependencies) {

      //This could be executed any given number or times
      manager.removeFromList(project.getDependencies(), dependencyModel);
      manager.removeFromList(project.getOriginalModel().getDependencies(), dependencyModel);

    }

    //After the original model was changed, persist it
    writeOriginalModel(backupFile);
  }

  public File backupFileManage() throws MojoExecutionException {
    return backupFileManage(true);
  }

  public File backupFileManage(boolean failIfExists) throws MojoExecutionException {
    File outFile = project.getFile();
    File backupFile = new File(outFile.getParentFile(), outFile.getName() + ".versionsBackup");

    if (failIfExists && backupFile.exists()) {
      throw new MojoExecutionException(
          "There's a change in progress, commit your current change with dependbl:commit first"
      );
    }

    if (!failIfExists && !backupFile.exists()) {
      throw new MojoExecutionException("No intermediate file found");
    }

    return backupFile;
  }

  public void writeOriginalModel(File backupFile) {
    try (Writer wr = new FileWriter(backupFile)) {
      project.writeOriginalModel(wr);
      log.info("[dependbl] Intermediate version of POM file generatec, run a dependbl:commit to persist changes");
    } catch (IOException e) {
      log.error(e);
    }
  }

  /**
   * Writes a StringBuilder into a file.
   *
   * @param outFile The file to read.
   * @param input   The contents of the file.
   * @throws IOException when things go wrong.
   */
  public final void writeFile(File outFile, StringBuilder input)
      throws IOException {
    Writer writer = WriterFactory.newXmlWriter(outFile);
    try {
      IOUtil.copy(input.toString(), writer);
    } finally {
      IOUtil.close(writer);
    }
  }

  public void updatePom(File outFile, File backupFile) throws MojoExecutionException {
    StringBuilder stringBuilder = loadPOMContent(backupFile);

    log.info("[dependbl] Accepting all changes to " + outFile.getAbsolutePath());
    try {
      writeFile(outFile, stringBuilder);
      Files.delete(backupFile.toPath());
    } catch (IOException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  public StringBuilder loadPOMContent(File backupFile) {
    StringBuilder stringBuilder = new StringBuilder();

    try {
      for (String line : Files.readAllLines(backupFile.toPath(), Charset.forName("UTF-8"))) {
        stringBuilder.append(line).append("\n");
      }
    } catch (IOException e) {
      log.error(e);
    }
    return stringBuilder;
  }

}
