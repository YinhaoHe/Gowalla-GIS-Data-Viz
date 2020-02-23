package controllers

import javax.inject._
import java.io.FileInputStream

import play.api.db._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.functional.syntax._
import scala.collection.mutable.MutableList

import io.circe.parser
import io.circe.generic.auto._

import services.Counter

@Singleton
class FriendshipController @Inject() (
                                       db: Database,
                                       cc: ControllerComponents,
                                       counter: Counter) extends AbstractController(cc) {
  def time(f: => Unit)={
	val s = System.currentTimeMillis
	f
	System.currentTimeMillis - s
}
  def getFriendships(numUsers: Int = 5): String = {

    var friendships = MutableList[List[Int]]()
    // Since the number of friendships is O(n^2), we need to polish the logic here (maybe return by batches)
    var result = "";

    db.withConnection { conn =>

      val stm = conn.createStatement()
      val res = stm.executeQuery("""
      select
          *
      from
          friendships
      where
          userA <= %s and userB <= %s and userA < userB
                                 """.format(numUsers, numUsers))
      while (res.next()) {
        try {
          friendships += List(res.getInt(1), res.getInt(2))
        } catch {
          case ex: java.lang.OutOfMemoryError =>
            return "!!!Out of memory error when reading from database!!!";
        }
      }
    }
    try {
      result = Json.toJson(friendships).toString()
    } catch {
      case ex: java.lang.OutOfMemoryError =>
        result = "!!!Out of memory error when parsing data into Json string!!!";
    }
    return result;
  }

  /**
    * Create an action that responds with the [[Counter]]'s current
    * count. The result is plain text. This `Action` is mapped to
    * `GET /count` requests by an entry in the `routes` config file.
    */
  def friendship(numFriendships: Int = 5) = Action{
    val s = getFriendships(numFriendships);
          println("TIME ELAPSED to return edges "+ time (getFriendships(numFriendships)))
    Ok( s )
  }
}
