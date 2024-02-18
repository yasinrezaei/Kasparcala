package AkkaTesting

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class InterceptionLoggsSpec extends TestKit(ActorSystem("InterceptingLoggs"/*,ConfigFactory.load().getConfig("interceptingLogMessages")*/))
with ImplicitSender
with AnyWordSpecLike
with BeforeAndAfterAll{
  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  import InterceptionLoggsSpec._
  val item = "Rock the jvm akka course"
  "A checkout flow" should {
    "correctly log the dispatch of an order" in {
      EventFilter.info(pattern = s"Order [0-9]+ for item $item has been dispatched") intercept {
        val checkoutRef = system.actorOf(Props[CheckoutActor])
        checkoutRef ! Checkout(item, creditCard = "1234-1234-1234-1234")
      }
    }

    "freak out if the payment denied" in {
      EventFilter[RuntimeException](occurrences = 1) intercept{
        val checkoutRef = system.actorOf(Props[CheckoutActor])
        checkoutRef ! Checkout(item, creditCard = "0234-1234-1234-1234")
      }
    }
  }
}

object InterceptionLoggsSpec{
  case class Checkout(item:String, creditCard:String)
  case class DispatchOrder(item:String)
  case class AuthorizeCard(creditCard: String)
  case object PaymentAccepted
  case object PaymentDenied
  case object OrderConfirmed


  class CheckoutActor extends Actor {
    private val paymentManager = context.actorOf(Props[PaymentManager])
    private val fulfillmentManager = context.actorOf(Props[FulfillmentManager])

    override def receive: Receive = awwaitingCheckout
    def awwaitingCheckout:Receive = {

      case Checkout(item,creditCard) =>
        paymentManager ! AuthorizeCard(creditCard)
        context.become(pendingPayment(item))
    }
    def pendingPayment(item: String):Receive =  {
      case PaymentAccepted =>
        fulfillmentManager ! DispatchOrder(item)
        context.become(pendingFulfillment(item))
      case PaymentDenied => throw new RuntimeException("runtime error")
    }
    def pendingFulfillment(item: String):Receive= {
      case OrderConfirmed => context.become(awwaitingCheckout)
    }
  }

  class PaymentManager extends Actor {

    override def receive: Receive = {
      case AuthorizeCard(card) =>
        if(card.startsWith("0")) sender() ! PaymentDenied
        else sender() ! PaymentAccepted
    }

  }
  class FulfillmentManager extends Actor with ActorLogging{
    var oredrId = 43
    override def receive: Receive = {

      case DispatchOrder(item:String) =>
        oredrId +=1
        log.info(s"Order $oredrId for item $item has been dispatched")
        sender() ! OrderConfirmed
    }
  }
}
