package serialization

trait Serializer[A] {
  def serialize: A => String
}
