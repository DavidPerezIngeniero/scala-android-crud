package com.github.scala_android.crud

import android.provider.BaseColumns
import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.mock.EasyMockSugar
import com.xtremelabs.robolectric.RobolectricTestRunner
import org.scalatest.matchers.ShouldMatchers
import android.content.Context
import com.github.triangle._
import CursorFieldAccess._
import res.R

/**
 * A test for {@link CrudListActivity}.
 * @author Eric Pabst (epabst@gmail.com)
 * Date: 2/18/11
 * Time: 6:22 PM
 */
@RunWith(classOf[RobolectricTestRunner])
class SQLiteEntityPersistenceFunctionalSpec extends EasyMockSugar with ShouldMatchers {
  object TestEntityType extends SQLiteCrudEntityType {
    def entityName = "Person"
    val fields = List(Field(persisted[Long]("age")))
    val childEntities = Nil

    val listLayout = R.layout.entity_list
    val headerLayout = R.layout.test_row
    val rowLayout = R.layout.test_row
    val entryLayout = R.layout.test_entry
    val addItemString = R.string.add_item
    val editItemString = R.string.edit_item
    val cancelItemString = R.string.cancel_item

    def getDatabaseSetup(context: Context) = new TestingDatabaseSetup(context)

    def activityClass = classOf[CrudActivity[_,_,_,_]]
    def listActivityClass = classOf[CrudListActivity[_,_,_,_]]
  }

  @Test
  def shouldUseCorrectColumnNamesForFindAll {
    val mockContext = mock[Context]
    val persistence = new SQLiteEntityPersistence(TestEntityType, mockContext)
    whenExecuting(mockContext) {
      val result = persistence.findAll(new SQLiteCriteria())
      result.getColumnIndex(BaseColumns._ID) should be (0)
      result.getColumnIndex("age") should be (1)
    }
  }
}