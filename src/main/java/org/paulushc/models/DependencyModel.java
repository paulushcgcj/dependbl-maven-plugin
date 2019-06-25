package org.paulushc.models;

import java.util.Objects;

public class DependencyModel {
  private String groupId;
  private String artifactId;
  private String version;

  public DependencyModel() {
  }

  public DependencyModel(String groupId, String artifactId, String version) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
  }

  public DependencyModel(String groupId, String artifactId) {
    this.groupId = groupId;
    this.artifactId = artifactId;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DependencyModel that = (DependencyModel) o;
    return Objects.equals(groupId, that.groupId) &&
        Objects.equals(artifactId, that.artifactId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupId, artifactId);
  }

  @Override
  public String toString() {
    return "DependencyModel(" +
        "groupId=" + groupId + ", " +
        "artifactId=" + artifactId + ", " +
        "version=" + version +
        ')';
  }
}
