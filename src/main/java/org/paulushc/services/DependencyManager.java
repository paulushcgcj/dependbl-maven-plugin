package org.paulushc.services;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.paulushc.models.DependencyModel;

import java.util.List;

public class DependencyManager {

  private Log log;

  public DependencyManager(Log log) {
    this.log = log;
  }

  public void removeFromList(
      List<Dependency> dependencyList,
      DependencyModel dependencyModel
  ) throws MojoFailureException {
    log.info("[dependbl] Removing artifact " + dependencyModel.getGroupId() + ":" + dependencyModel.getArtifactId());
    dependencyList.remove(getDependencyToBeRemoved(dependencyList, dependencyModel.getGroupId(), dependencyModel.getArtifactId()));
    log.info("[dependbl] Artifact " + dependencyModel.getGroupId() + ":" + dependencyModel.getArtifactId() + " removed");
  }

  /**
   * <p><b>Get Dependency to Remove</b></p>
   * Check if that dependency is on the list.
   *
   * @param dependencyList A list of dependencies.
   * @param groupId        Group id of the dependency.
   * @param artifactId     Artifact id of the dependency.
   * @return The dependency that was found on the list or null.
   * @throws MojoFailureException in case of no dependency found
   */
  private Dependency getDependencyToBeRemoved(
      List<Dependency> dependencyList,
      String groupId,
      String artifactId
  ) throws MojoFailureException {

    for (Dependency dependency : dependencyList) {
      if (
          dependency.getGroupId().equals(groupId) &&
              dependency.getArtifactId().equals(artifactId)
      ) {
        return dependency;
      }
    }
    log.error("[dependbl] No artifact " + groupId + ":" + artifactId + " has been found");
    throw new MojoFailureException("No artifact " + groupId + ":" + artifactId + " has been found");
  }

  public void addToList(
      List<Dependency> dependencyList,
      DependencyModel dependencyModel
  ) throws MojoFailureException {
    log.info("[dependbl] Adding artifact " + dependencyModel.getGroupId() + ":" + dependencyModel.getArtifactId() + ":" + dependencyModel.getVersion());
    setDependencyToBeAdded(dependencyList, dependencyModel.getGroupId(), dependencyModel.getArtifactId(), dependencyModel.getVersion());
    log.info("[dependbl] Artifact " + dependencyModel.getGroupId() + ":" + dependencyModel.getArtifactId() + ":" + dependencyModel.getVersion() + " was added");
  }

  private void setDependencyToBeAdded(
      List<Dependency> dependencyList,
      String groupId,
      String artifactId,
      String version
  ) throws MojoFailureException {

    for (Dependency dependency : dependencyList) {
      if (
          dependency.getGroupId().equals(groupId)
              && dependency.getArtifactId().equals(artifactId)
      ) {
        if (!dependency.getVersion().equals(version)) {

          log
              .warn(
                  "[dependbl] "
                      + groupId + ":" + artifactId
                      + " is already present at version "
                      + dependency.getVersion()
              );
          log
              .warn(
                  "[dependbl] Updating to "
                      + groupId + ":" + artifactId + ":" + version
              );

          dependency.setVersion(version);
        } else {
          log
              .warn(
                  "[dependbl] "
                      + groupId + ":" + artifactId + ":" + version
                      + " is already present"
              );
          throw new MojoFailureException("Artifact " + groupId + ":" + artifactId + ":" + version + " is already present");
        }

      }
    }

    Dependency dpdnc = new Dependency();
    dpdnc.setGroupId(groupId);
    dpdnc.setArtifactId(artifactId);
    dpdnc.setVersion(version);

    log
        .warn(
            "[dependbl] Adding "
                + groupId + ":" + artifactId + ":" + version
        );
    dependencyList.add(dpdnc);
  }


}
