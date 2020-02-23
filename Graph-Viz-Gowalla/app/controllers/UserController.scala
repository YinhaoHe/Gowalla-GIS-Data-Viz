package controllers

import javax.inject._
import java.io.FileInputStream

import play.api._
import play.api.db._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.functional.syntax._
import scala.collection.mutable.MutableList

import io.circe.parser
import io.circe.generic.auto._

import services.Counter

// ToDo: change checkInTime from String type to a date time type if necessary
case class GowallaUser(userId: Int, checkInTime: String, checkInLatitude: Double, checkInLongitude: Double, locationId: Int)

@Singleton
class UserController @Inject() (
                                 db: Database,
                                 cc: ControllerComponents
                               ) extends AbstractController(cc) {

  def time(f: => Unit)={
	val s = System.currentTimeMillis
	f
	System.currentTimeMillis - s
}

  def getUsers(numUsers: Int = 5): String = {

    val users = MutableList[GowallaUser]()

    db.withConnection { conn =>
      val stm = conn.createStatement()
      val res = stm.executeQuery("""
      select
          *
      from
          usersWithSingleCheckin
      where
          userId <= %s
       """.format(numUsers))
      while (res.next()) {
          users.+=(GowallaUser(res.getInt(1), res.getString(2), res.getDouble(3), res.getDouble(4), res.getInt(5)))
      }
    }

    implicit val gowallaUserWrites = new Writes[GowallaUser] {
      def writes(user: GowallaUser) = Json.obj(
        "userId" -> user.userId,
        "latitude" -> user.checkInLatitude,
        "longitude" -> user.checkInLongitude
        //"locationId" -> user.locationId,
        //"checkInTime" -> user.checkInTime
      )
    }
    return Json.toJson(users).toString();
  }

  /**
    * Create an action that responds with the [[Counter]]'s current
    * count. The result is plain text. This `Action` is mapped to
    * `GET /count` requests by an entry in the `routes` config file.
    */
  def user(numUsers: Int = 5) = Action{
    val s = getUsers(numUsers);
      println("TIME ELAPSED to return vertices "+ time (getUsers(numUsers)))
    Ok( s )
  }
  

}
