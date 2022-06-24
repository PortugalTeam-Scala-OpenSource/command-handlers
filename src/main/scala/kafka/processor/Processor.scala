package kafka.processor

import Reader.Output
import akka.actor.ActorSystem
import akka.kafka.scaladsl.{Consumer, Transactional}
import akka.kafka.{
  ConsumerMessage,
  ConsumerSettings,
  ProducerMessage,
  ProducerSettings,
  Subscriptions
}
import akka.kafka.scaladsl.Consumer.{DrainingControl, plainSource}
import akka.stream.KillSwitches
import akka.stream.scaladsl.{Keep, Sink}
import com.typesafe.config.ConfigFactory
import kafka.processor.Processor._
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{
  StringDeserializer,
  StringSerializer
}

import scala.concurrent.Future

object Processor {
  type Key = String
  type Command = String
  type Error = String
  type Event = String

  case class Processor(
      callback: (Key, Command) => Future[Either[Error, Event]]
  )(implicit actorSystem: ActorSystem) {

    implicit val executionContext = actorSystem.dispatcher
    val consumerSettings = ConsumerSettings[Key, Command](
      actorSystem,
      new StringDeserializer,
      new StringDeserializer
    )
      .withBootstrapServers("0.0.0.0:9092")
      .withGroupId("group")
      .withProperties(Map("auto.offset.reset" -> "earliest"))

    val producerSettings = ProducerSettings[Key, Event](
      actorSystem,
      new StringSerializer,
      new StringSerializer
    )
      .withBootstrapServers("0.0.0.0:9092")

    def start(
        topic: String,
        eventTopic: String,
        errorTopic: String
    ): Output = {
      val source =
        Transactional.source[Key, Command](
          consumerSettings,
          Subscriptions.topics(topic)
        )

      println(s"Starting processing of topic ${topic}")

      val transactionalId = "transaction-id-1"
      val output = source
        .mapAsync(100) { case consumerRecord =>
          val key = consumerRecord.record.key()
          val value = consumerRecord.record.value()
          val partitionOffset = consumerRecord.partitionOffset

          println(s"""
              |
              |OH! RECEIVED MESSAGE AT TOPIC ${topic}
              |key: ${key}
              |value: ${value}
              |partitionOffset: ${partitionOffset}
              |""".stripMargin)

          callback(key, value).map { result =>
            (key, value, partitionOffset, result)
          }
        }
        .map {
          case (key, value, partitionOffset, Left(error)) =>
            println(s"Publishing error to ${errorTopic}")
            ProducerMessage.single(
              new ProducerRecord(errorTopic, key, error),
              partitionOffset
            )
          case (key, value, partitionOffset, Right(event)) =>
            println(s"Publishing event to ${eventTopic}")
            ProducerMessage.single(
              new ProducerRecord(eventTopic, key, event),
              partitionOffset
            )
        }
        .via(Transactional.flow(producerSettings, transactionalId))
        .viaMat(KillSwitches.single)(Keep.right)
        .toMat(Sink.ignore)(Keep.both)
        .run()

      Output(
        killswitch = output._1.shutdown,
        done = output._2.map(_ => ())
      )
    }
  }

}

object ExampleOfKafka extends App {

  val config = ConfigFactory.load()
  implicit val actorSystem = ActorSystem("Example", config)

  val producerSettings = ProducerSettings[Key, Command](
    actorSystem,
    new StringSerializer,
    new StringSerializer
  )
    .withBootstrapServers("0.0.0.0:9092")

  val processor = Processor.Processor.apply(callback = {
    case ("Miguel", value) =>
      Future.successful(
        Left(s"Will not read messages to Miguel!!! >:(  ${value}")
      )
    case (key, value) =>
      Future.successful(Right(s"Read ${key} ${value}"))
  })

  val output = processor.start("messages", "eventTopic", "errorTopic")

}
