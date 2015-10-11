package ammonite.shell
package classwrapper

object SerializationTests extends tests.SerializationTests(
  new AmmoniteClassWrapperChecker(),
  wrapperInstance = (_, _) => "INSTANCE.$ref$",
  expectReinitializing = false
)
