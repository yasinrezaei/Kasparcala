package Akka.Actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object Exercises extends App {

  case class Vote(candidate:String)
  case class AggregateVotes(citizens:Set[ActorRef])
  case object VoteStatusRequest
  case class VoteStatusReply(candidate:Option[String])
  class Citizen extends Actor {

    override def receive: Receive = {
      case Vote(c) => context.become(voted(c))
      case VoteStatusRequest => sender() ! VoteStatusReply(None)
    }

    def voted(candidate: String):Receive = {
      case VoteStatusRequest =>
        sender() ! VoteStatusReply(Some(candidate))
    }
  }


  class VoteAggregator extends Actor {

    override def receive: Receive = awaitingCommand

    def awaitingCommand:Receive = {
      case AggregateVotes(citizens) =>
        citizens.foreach(citizen => citizen ! VoteStatusRequest)
        context.become(awaitingStatuses(citizens,Map()))
    }
    def awaitingStatuses(stillWaiting:Set[ActorRef],currentStats:Map[String,Int]):Receive = {
      case VoteStatusReply(None) => {
        sender() ! VoteStatusRequest
      }
      case VoteStatusReply(Some(candidate)) => {
        val newStillWaiting = stillWaiting - sender()

        val currentVoteOfCandidate = currentStats.getOrElse(candidate, 0)
        val newStats = currentStats + (candidate -> (currentVoteOfCandidate+1))
        if (newStillWaiting.isEmpty){
          println(s"[aggregator] poll stats:$newStats")
        }else{
          context.become(awaitingStatuses(newStillWaiting, newStats))
        }
      }
    }
  }

  val actorSystem = ActorSystem("Voting")

  val alice = actorSystem.actorOf(Props[Citizen],"alice")
  val bob = actorSystem.actorOf(Props[Citizen],"bob")
  val charlie = actorSystem.actorOf(Props[Citizen],"charlie")
  val daniel = actorSystem.actorOf(Props[Citizen],"daniel")
  val ali = actorSystem.actorOf(Props[Citizen],"ali")

  alice ! Vote("Martin")
  bob ! Vote("Jonas")
  charlie ! Vote("Roland")
  daniel ! Vote("Roland")
  ali ! Vote("Martin")

  val voteAggregator = actorSystem.actorOf(Props[VoteAggregator])
  voteAggregator ! AggregateVotes(Set(alice,bob,daniel,charlie,ali))

  //Thread.sleep(300)





}
