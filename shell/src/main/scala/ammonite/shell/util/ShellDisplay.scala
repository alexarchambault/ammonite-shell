package ammonite.shell.util

import ammonite.interpreter.api.DisplayItem, DisplayItem._

import acyclic.file


object ShellDisplay {

  def pprintSignature(ident: String) = s"""Iterator(ReplBridge.shell.shellPPrint($$user.$ident, "$ident"))"""

  def apply(d: DisplayItem): String =
    d match {
      case Definition(label, name) =>
        s"""Iterator(ReplBridge.shell.shellPrintDef("$label", "$name"))"""
      case Identity(ident) =>
        pprintSignature(ident) +
          s""" ++ Iterator(" = ") ++ ammonite.pprint.PPrint($$user.$ident)"""
      case LazyIdentity(ident) =>
        s"""${pprintSignature(ident)} ++ Iterator(" = <lazy>")"""
      case Import(imported) =>
        s"""Iterator(ReplBridge.shell.shellPrintImport("$imported"))"""
    }

}
