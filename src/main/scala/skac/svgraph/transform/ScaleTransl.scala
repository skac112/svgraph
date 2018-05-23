package skac.svgraph.transform

import skac.svgraph._
import com.github.skac112.vgutils._

object ScaleTransl {
  lazy val id = new ScaleTransl(1.0, Point(0.0, 0.0))
}

/**
 * Composition of scaling and translation.
 */
case class ScaleTransl(scale: Double, transl: Point) extends Transform {
  def transPt(point: Point) = point * scale + transl
  def matrix = Snap.matrix(scale, 0, 0, scale, transl.x, transl.y)
}
