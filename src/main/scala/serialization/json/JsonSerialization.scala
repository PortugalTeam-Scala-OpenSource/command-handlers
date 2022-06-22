package serialization.json

import logging.Logger
import serialization._

import scala.reflect.ClassTag

object JsonSerialization {

  object Play {
    import play.api.libs.json._
    case class JsonSerializer[A]()(implicit format: Format[A])
        extends Serializer[A] {
      override def serialize: A => String = toJson =>
        Json.prettyPrint(
          format.writes(toJson)
        )
    }
    case class JsonDeserializer[A]()(implicit format: Format[A], logger: Logger)
        extends Deserializer[A] {
      override def deserialize: String => Option[A] = string => {
        val json = Json.parse(string).validate[A]
        json match {
          case JsSuccess(value, path) => Some(value)
          case JsError(errors) =>
            logger.error(s"[serialization] ${errors}")
            None
        }
      }
    }

    case class JsonSerialization[A](format: Format[A])(implicit logger: Logger)
        extends Serializer[A]
        with Deserializer[A] {
      override def deserialize: String => Option[A] =
        JsonDeserializer[A]()(format, logger).deserialize
      override def serialize: A => String =
        JsonSerializer[A]()(format).serialize
    }
  }
}
