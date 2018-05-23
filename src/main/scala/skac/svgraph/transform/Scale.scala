package skac.svgraph.transform

import skac.svgraph._
import com.github.skac112.vgutils._

/**
 * Scaling transform.
 */
case class Scale(scale: Double) extends Transform {
  def transPt(point: Point): Point = point * scale
  def matrix = Snap.matrix(scale, 0, 0, scale, 0, 0)
}
