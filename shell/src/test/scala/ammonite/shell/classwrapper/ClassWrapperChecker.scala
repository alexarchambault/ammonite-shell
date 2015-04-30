package ammonite.shell
package classwrapper

import ammonite.interpreter._

object ClassWrapperChecker {
  val instanceSymbol = "INSTANCE"
}

class ClassWrapperChecker extends Checker {
  import ClassWrapperChecker.instanceSymbol

  override def newInterpreter(): Interpreter[Preprocessor.Output, Iterator[String]] =
    new Interpreter(
      ShellInterpreter.bridgeConfig(),
      ShellInterpreter.preprocessor,
      ShellInterpreter.classWrap(instanceSymbol),
      handleResult = {
        val transform = ShellInterpreter.classWrapImportsTransform(instanceSymbol) _
        (buf, r) => transform(r)
      },
      stdout = allOutput += _,
      useClassWrapper = true,
      classWrapperInstance = Some(instanceSymbol)
    )
}