package skac.svgraph

import org.scalajs.jquery._
import scala.scalajs.js.annotation.JSExportTopLevel
import graph._
import skac.euler._
import skac.euler.General._
// import graph.impl._
// import graph.impl.DefaultSkinGraph
import skin.node._
import skin.edge._
import scala.scalajs.js
import com.github.skac112.vgutils._

object SampleApp {
  def main(args: Array[String]): Unit = {
    println("Hello world!")
  }

  @JSExportTopLevel("addClickedMessage")
  def addClickedMessage(): Unit = {
    val c = new Canvas("#svg_canvas")
  }

  jQuery(init _)

  def init(): Unit = {
    try {
      // val p = Snap("#svg_canvas")
      // val l = p.line(10, 10, 100, 100)
      // val x1 = l.attr("x1").asInstanceOf[String].toDouble
      // val x1 = l.x1
      // println(l)
      val n1 = Circle(60)
      n1.location = Point(20, 20)
      n1.label = "node 1"
      val n2 = Circle(50)
      n2.location = Point(500, 500)
      n2.label = "node 2"
      val n3 = Circle(30)
      n3.location = Point(1000, 0)
      n3.label = "node 3"
      val e1 = Straight()
      val e2 = Straight()
      val e3 = Straight()
      var g: Graph[NodeSkin, EdgeSkin] = new skac.euler.impl.fastindex.immutable.Graph[NodeSkin, EdgeSkin]
      g = g + n1 + n2 + n3 +-> (e1, n1.da, n2.da) +-> (e2, n2.da, n3.da) +-> (e3, n3.da, n1.da)
      val c = new Canvas("#svg_canvas")
      c.setGraph(g)
    }
    catch {
      case t: Throwable => t.printStackTrace()
    }
  }
}
