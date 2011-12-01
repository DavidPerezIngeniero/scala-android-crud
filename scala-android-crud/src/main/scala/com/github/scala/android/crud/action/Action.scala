package com.github.scala.android.crud.action

import android.app.Activity
import com.github.scala.android.crud.common.PlatformTypes._
import android.content.{Context, Intent}
import android.net.Uri

/**
 * Represents something that a user can initiate.
 * @author Eric Pabst (epabst@gmail.com)
 * Date: 8/26/11
 * Time: 6:39 AM
 */
trait Command {
  /** The optional icon to display. */
  def icon: Option[ImgKey]

  /**
   * The title to display.
   * If the title is None, it can't be displayed in a context menu for a list item.
   * If both title and icon are None,
   * then it can't be displayed in the main options menu, but can still be triggered as a default.
   */
  def title: Option[SKey]

  /** A CommandID that can be used to identify if it's the same as another in a list.
    * It uses the title or else the icon or else the hash code.
    */
  def commandId: CommandId = title.orElse(icon).getOrElse(##)
}

/**
 * Represents an action that a user can initiate.
 * It's equals/hashCode MUST be implemented in order to suppress the action that is already happening.
 */
trait Action extends Command {
  /** Runs the action, given the uri and the current state of the application. */
  def invoke(uri: UriPath, activity: ActivityWithVars)
}

/**
  * An action class that can be mixed in with Action traits, and specifies the icon and title.
  */
abstract class BaseAction(val icon: Option[ImgKey], val title: Option[SKey]) extends Action

object Action {
  val CreateActionName = Intent.ACTION_INSERT
  val ListActionName = Intent.ACTION_PICK
  val DisplayActionName = Intent.ACTION_VIEW
  val UpdateActionName = Intent.ACTION_EDIT
  val DeleteActionName = Intent.ACTION_DELETE

  def toUri(uriPath: UriPath): Uri = uriPath.segments.foldLeft(Uri.EMPTY)((uri, segment) => Uri.withAppendedPath(uri, segment))

  implicit def toRichItent(intent: Intent) = new RichIntent(intent)

  //this is a workaround because Robolectric doesn't handle the full constructor
  def constructIntent(action: String, uriPath: UriPath, context: Context, clazz: Class[_]): Intent = {
    val intent = new Intent(action, toUri(uriPath))
    intent.setClass(context, clazz)
    intent
  }
}

case class RichIntent(intent: Intent) {
  def uriPath: UriPath = UriPath(intent.getData)
}

trait StartActivityAction extends Action {
  def determineIntent(uri: UriPath, activity: ActivityWithVars): Intent

  def invoke(uri: UriPath, activity: ActivityWithVars) {
    activity.startActivity(determineIntent(uri, activity))
  }
}

trait BaseStartActivityAction extends StartActivityAction {
  def action: String

  def activityClass: Class[_ <: Activity]

  def determineIntent(uri: UriPath, activity: ActivityWithVars): Intent = Action.constructIntent(action, uri, activity, activityClass)
}

//final to guarantee equality is correct
final case class StartActivityActionFromIntent(intent: Intent,
                                               icon: Option[ImgKey] = None,
                                               title: Option[SKey] = None) extends StartActivityAction {
  def determineIntent(uri: UriPath, activity: ActivityWithVars) = intent
}

//final to guarantee equality is correct
final case class StartNamedActivityAction(action: String,
                                          icon: Option[ImgKey], title: Option[SKey],
                                          activityClass: Class[_ <: Activity]) extends BaseStartActivityAction

trait EntityAction extends Action {
  def entityName: String
  def action: String
}

//final to guarantee equality is correct
final case class StartEntityActivityAction(entityName: String, action: String,
                                           icon: Option[ImgKey], title: Option[SKey],
                                           activityClass: Class[_ <: Activity]) extends BaseStartActivityAction with EntityAction {
  override def determineIntent(uri: UriPath, activity: ActivityWithVars): Intent =
    super.determineIntent(uri.specify(entityName), activity)
}

//final to guarantee equality is correct
final case class StartEntityIdActivityAction(entityName: String, action: String,
                                             icon: Option[ImgKey], title: Option[SKey],
                                             activityClass: Class[_ <: Activity]) extends BaseStartActivityAction with EntityAction {
  override def determineIntent(uri: UriPath, activity: ActivityWithVars) = super.determineIntent(uri.upToIdOf(entityName), activity)
}
