akka-mesos
==========

[![Build Status](https://travis-ci.org/drexin/akka-mesos.svg?branch=master)](https://travis-ci.org/drexin/akka-mesos)

Non-blocking driver for [Apache Mesos](http://mesos.apache.org) based on [Akka](http://akka.io).

### Usage

#### Registering a Framework with Mesos

```scala
import akka.actor.ActorSystem
import akka.libprocess.PID
import akka.mesos.Mesos
import akka.mesos.protos.FrameworkInfo

implicit val system = ActorSystem("Mesos", config)

val frameworkInfo = FrameworkInfo(
  name = "ExampleFramework",
  user = "user",
  failoverTimeout = Some(5.minutes),
  checkpoint = Some(true)
)

val framework = Mesos(system).registerFramework(Success(PID("127.0.0.1", 5050, "master")), frameworkInfo)
```

The `registerFramework` method returns a `Framework` object. This object contains a
`SchedulerDriver` and a `Source[SchedulerMessage]`. The `SchedulerDriver` can be
used to send scheduling messages to Mesos, while the
`Source[SchedulerMessages]` contains the scheduling messages that Mesos
sends to the framework.

#### Example

```scala
import akka.mesos.protos.TaskInfo
import akka.mesos.scheduler.SchedulerPublisher.{ StatusUpdate, ResourceOffers }
import akka.stream.ActorFlowMaterializer

// necessary to process the message stream
implicit val materializer = ActorFlowMaterializer()

// process incoming messages with the given function
framework.schedulerMessages.runForeach {
  case ResourceOffers(offers) =>
    // create a TaskInfo
    val taskInfo = TaskInfo(...)

    // find a matching offer
    val matchingOffer = offer.find(...)

    // if a matching offer is found, start the task with the offer
    matchingOffer.foreach { offer =>
      driver.launchTasks(Seq(task), Seq(offer.id))
    }

    // don't forget to decline the offers that didn't match
    (offers diff matchingOffer.toSeq).foreach { offer =>
      driver.declineOffer(offer.id)
    }

  case StatusUpdate(update) =>
    println(s"Task ${update.status.taskId} id now ${update.status.state}")
    // Status updates must be acknowledged so Mesos knows, that we
    // received and sucessfully processed this message. Otherwise
    // Mesos would keep sending this message
    framework.driver.acknowledgeStatusUpdate(update)

  case _ => // We are not interested in other messages for now
}
```
