/*******************************************************************************
 * Copyright 2010 Maxime Lévesque
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.squeryl.logging

import org.squeryl.dsl.ast.ExpressionNode


class StatementExecution(_definingClass: Class[_], val start: Long, val end: Long, jdbcStatement: String) {


  /**
   * The use of this method is to allow to unambiguously link the statement execution the code
   * that calls this statement.
   *
   * select, compute, groupBy statements always have closure, this is the class returned by this
   * method for this kind of statements. For statements like Table[A] (.lookup, .delete, .update(a:A))
   * the defining class is the A class within Table[A] (or View[A]).
   */
  def definingClass: Class[_] = _definingClass

 /**
  * The use of this method is to allow to unambiguously link the statement execution the code
  * that calls this statement.
  */
  
  def callSite: StackTraceElement = {

    val st = Thread.currentThread.getStackTrace
    var i = 0
    while(st.length < i) {
      val e = st(i)
      // TODO : make top level call in a method who's only purpose is to identify the call site in the user code ?
      if(e.getClassName.startsWith("org.squeryl.") || e.getClassName.startsWith("scala."))
        i = i + 1
      else
        return e
    }

    error("could not find stack element")
  }
}  

trait StatisticsListener {

  def queryExecuted(se: StatementExecution): Unit

  def resultSetIterationEnded(se: StatementExecution, iterationEndTime: Long, numberOfRowsFetched: Int, iterationCompleted: Boolean): Unit

  def updateExecuted(se: StatementExecution): Unit

  def insertExecuted(se: StatementExecution): Unit

  def deleteExecuted(se: StatementExecution): Unit
}

object StackMarker {

  def lastSquerylStackFrame[A](a: =>A) = a
}