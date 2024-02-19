package AkkaHttp

import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import de.heikoseeberger.akkahttpjackson.JacksonSupport
import spray.json._

import java.util.UUID

case class User(name:String, age:Int)
case class UserAdded(id:String, timestamp:Long)
trait UserJsonProtocol extends DefaultJsonProtocol {

  implicit val userFormat = jsonFormat2(User)
  implicit val userAddedFormat = jsonFormat2(UserAdded)

}

object Main extends UserJsonProtocol with SprayJsonSupport{


  implicit val system = ActorSystem(Behaviors.empty, "AkkaHttpJson")

  val route:Route = (path("api" / "user") & post){

    entity(as[User]){
      user:User => complete(UserAdded(UUID.randomUUID().toString,System.currentTimeMillis()))
    }


  }


  def main(args: Array[String]): Unit = {
    Http().newServerAt("localhost",8081).bind(route)
  }


}
object MainCirce extends FailFastCirceSupport{

  import io.circe.generic.auto._ //implicit encoders and decoders

  implicit val system = ActorSystem(Behaviors.empty, "AkkaHttpJson")

  val route:Route = (path("api" / "user") & post){

    entity(as[User]){
      user:User => complete(UserAdded(UUID.randomUUID().toString,System.currentTimeMillis()))
    }


  }


  def main(args: Array[String]): Unit = {
    Http().newServerAt("localhost",8082).bind(route)
  }


}

object MainJackson extends JacksonSupport{

  implicit val system = ActorSystem(Behaviors.empty, "AkkaHttpJson")

  val route:Route = (path("api" / "user") & post){

    entity(as[User]){
      user:User => complete(UserAdded(UUID.randomUUID().toString,System.currentTimeMillis()))
    }


  }


  def main(args: Array[String]): Unit = {
    Http().newServerAt("localhost",8083).bind(route)
  }


}