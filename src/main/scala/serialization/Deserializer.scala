package serialization

trait Deserializer[A] {
  def deserialize: String => Option[A]
}
