package skac.svgraph.skin.node

import skac.svgraph._
import scala.scalajs.js
import com.github.skac112.vgutils._

object Circle {
  def apply(r: Double = 50.0) = new Circle(r)
}

/**
 * Node skin consisting of a circle and some accompanied elements (such as label
 * text and icon)
 */
class Circle(val r: Double = 50.0) extends NodeSkin {
  override var location = Point(0, 0)
  private var cO: Option[SnapCircleElement] = None
  private var lO: Option[SnapElement] = None
  private var _focus = 0.0
  private var _label: String = ""
  private var _fill: WebColor = "#800"
  private var _stroke: WebColor = "#000"
  private var _strokeWidth = 0.0
//  private var _paperO: Option[SnapPaper] = None

  override def draw(graph: SkinGraph, group: SnapGroupElement, paper: SnapPaper): Unit = {
    val c = paper.circle(.0, .0, r)
    c.attr(attrs)
    this.cO = Some(c)
    group.add(this.cO.get)
    if (!_label.isEmpty) {
      val l = paper.text(r * 1.1, r * 1.1, _label)
      this.lO = Some(l)
      group.add(this.lO.get)
    }
  }

  override def focus_=(value: Double) {
    _focus = value
  }

  override def focus: Double  = _focus

  override def label: String = _label

  override def label_=(value: String): Unit = {
    _label = value
  }

  def extent(dir: Angle): Double = r

  def fill = _fill

  def fill_=(value: WebColor): Unit = {_fill = value}

  def stroke = _stroke

  def stroke_=(value: WebColor): Unit = {_stroke = value}

  def strokeWidth = _strokeWidth

  def strokeWidth_=(value: Double): Unit = {_strokeWidth = value}

//  def paperO: Option[SnapPaper] = _paperO

//  def paper: SnapPaper = _paperO.get

//  def paper_=(value: SnapPaper): Unit = {_paperO = Some(value)}

  private def attrs = js.Dictionary("fill" -> _fill, "stroke" -> _stroke, "strokeWidth" -> _strokeWidth)

  def c = cO.get
}
