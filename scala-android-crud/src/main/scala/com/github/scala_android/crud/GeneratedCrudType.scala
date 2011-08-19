package com.github.scala_android.crud

import persistence.{IdPk, SeqEntityPersistence}
import res.R
import android.view.{ViewGroup, View}
import com.github.triangle.PortableField.identityField
import android.app.ListActivity
import android.widget.{ListAdapter, BaseAdapter}
import IdPk.idField

trait GeneratedCrudType[T <: AnyRef] extends CrudType {
  def newWritable = throw new UnsupportedOperationException("not supported")

  def openEntityPersistence(crudContext: CrudContext): SeqEntityPersistence[T]

  class SeqPersistenceAdapter[T <: AnyRef](findAllResult: AnyRef, contextItems: List[AnyRef], activity: ListActivity)
          extends BaseAdapter with AdapterCaching {
    val seq: Seq[T] = findAllResult.asInstanceOf[Seq[T]]

    def getCount: Int = seq.size

    def getItemId(position: Int): ID = idField(getItem(position))

    def getItem(position: Int) = seq(position)

    def getView(position: Int, convertView: View, parent: ViewGroup): View = {
      val view = if (convertView == null) activity.getLayoutInflater.inflate(rowLayout, parent, false) else convertView
      bindViewFromCacheOrItems(view, getItem(position) :: contextItems, position, activity)
      view
    }
  }

  def setListAdapter(findAllResult: AnyRef, contextItems: List[AnyRef], activity: ListActivity) {
    activity.setListAdapter(new SeqPersistenceAdapter[T](findAllResult, contextItems, activity))
  }

  def refreshAfterDataChanged(listAdapter: ListAdapter) {}

  override def getListActions(actionFactory: UIActionFactory) = super.getReadOnlyListActions(actionFactory)

  override def getEntityActions(actionFactory: UIActionFactory) = super.getReadOnlyEntityActions(actionFactory)

  override val cancelItemString = R.string.cancel_item
}

object GeneratedCrudType {
  val crudContextField = identityField[CrudContext]
}