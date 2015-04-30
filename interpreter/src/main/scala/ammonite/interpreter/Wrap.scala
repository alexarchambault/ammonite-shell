package ammonite.interpreter

object Wrap {

  def obj(code: String, displayCode: String, previousImportBlock: String, wrapperName: String): String =
    s"""$previousImportBlock

        object $wrapperName{
          $code
          def $$main() = {val $$user = this; $displayCode}
        }
     """

  def cls(code: String, displayCode: String, previousImportBlock: String, wrapperName: String, instanceSymbol: String): String =
    s"""object $wrapperName extends AnyRef {
          val $instanceSymbol = new $wrapperName
        }

        object $wrapperName$$Main extends AnyRef {
          $previousImportBlock

          def $$main() = {
            val $$execute = $wrapperName.$instanceSymbol
            import $wrapperName.$instanceSymbol.$$user
            $displayCode
          }
        }


        class $wrapperName extends Serializable {
          $previousImportBlock

          class $$user extends Serializable {
            $code
          }

          val $$user = new $$user
        }
     """

  def classWrapImportsTransform(instanceSymbol: String)(r: Res[Evaluated[_]]): Res[Evaluated[_]] =
    r .map { ev =>
      ev.copy(imports = ev.imports.map{ d =>
        if (d.wrapperName == d.prefix) // Assuming this is an import of REPL variables
          d.copy(prefix = d.prefix + "." + instanceSymbol + ".$user")
        else
          d
      })
    }

}