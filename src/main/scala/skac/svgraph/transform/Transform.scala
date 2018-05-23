package skac.svgraph.transform

import skac.svgraph._
import com.github.skac112.vgutils._

object Transform {
  lazy val id = new Transform {
    def transPt(pt: Point): Point = pt
    def matrix = Snap.matrix(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
  }
}

trait Transform {
  def transPt(pt: Point): Point
  def matrix: SnapMatrix
}
