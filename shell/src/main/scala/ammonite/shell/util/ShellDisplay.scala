package ammonite.shell.util

import ammonite.api.CodeItem, CodeItem._


object ShellDisplay {

  def apply(d: CodeItem): String =
    d match {
      case Definition(label, name) =>
        s"""ReplBridge.shell.Internal.printDef("$label", "$name")"""
      case Identity(ident) =>
        s"""ReplBridge.shell.Internal.print($$user.$ident, $$user.$ident, "$ident", _root_.scala.None)"""
      case LazyIdentity(ident) =>
        s"""ReplBridge.shell.Internal.print($$user.$ident, $$user.$ident, "$ident", _root_.scala.Some("<lazy>"))"""
      case Import(imported) =>
        s"""ReplBridge.shell.Internal.printImport("$imported")"""
    }

}
