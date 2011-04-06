package com.github.scala_android_crud.sample

import com.github.scala_android.crud.CrudApplication

/**
 * The sample application
 * @author Eric Pabst (epabst@gmail.com)
 * Date: 3/31/11
 * Time: 4:53 PM
 */

object SampleApplication extends CrudApplication {
  def allEntities = List(AuthorCrudType)
}