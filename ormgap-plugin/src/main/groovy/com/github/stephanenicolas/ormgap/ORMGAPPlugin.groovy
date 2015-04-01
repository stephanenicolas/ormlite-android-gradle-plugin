package com.github.stephanenicolas.ormgap;

import javassist.build.IClassTransformer
import org.gradle.api.Project

class ORMGAPPlugin extends AbstractORMGAPPlugin {

  @Override
  public IClassTransformer[] getTransformers(Project project) {
    return null;
  }

  @Override
  protected Class getPluginExtension() {
    ORMGAPPluginExtension
  }

  @Override
  protected String getExtension() {
    "ormgap"
  }
}
