package sorm

import core._
import jdbc._
import persisted.Persisted
import reflection.Reflection
import samples._
import sext._
import sorm.Sorm._

object Sandbox extends App {

  case class A ( a : Int )

  val db = TestingInstance.mysql( Entity[A]() ).connection()

  val a = db.save(A( 23 ))

  db.save(a)
//  db.overwrite(a.copy(a = 312)).whereEqual("id", 1).fetch()
//  db.overwrite(A( 123 )).whereEqual("id", 1).fetch()

}
