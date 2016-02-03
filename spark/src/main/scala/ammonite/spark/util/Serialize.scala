package ammonite.spark.util

import java.io._

object Serialize {
  def to(m: AnyRef): Array[Byte] = {
    val baos = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(baos)
    try {
      oos.writeObject(m)
      baos.toByteArray
    }
    finally oos.close()
  }

  def from(b: Array[Byte], loader: ClassLoader): AnyRef = {
    val bais = new ByteArrayInputStream(b)
    val ois = new ClassLoaderObjectInputStream(loader, bais)
    try ois.readObject()
    finally ois.close()
  }
}


// from akka.util

/**
 * ClassLoaderObjectInputStream tries to utilize the provided ClassLoader to load Classes and falls
 * back to ObjectInputStreams resolver.
 *
 * @param classLoader - the ClassLoader which is to be used primarily
 * @param is - the InputStream that is wrapped
 */
class ClassLoaderObjectInputStream(classLoader: ClassLoader, is: InputStream) extends ObjectInputStream(is) {
  override protected def resolveClass(objectStreamClass: ObjectStreamClass): Class[_] =
    try Class.forName(objectStreamClass.getName, false, classLoader) catch {
      case cnfe: ClassNotFoundException ⇒ super.resolveClass(objectStreamClass)
    }
}