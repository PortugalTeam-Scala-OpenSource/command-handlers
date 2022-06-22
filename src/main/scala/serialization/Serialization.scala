package serialization

trait Serialization[A] extends Serializer[A] with Deserializer[A]
