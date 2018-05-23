package skac.svgraph.skin.edge
import com.github.skac112.vgutils._
import skac.svgraph._
import scala.scalajs._
import skac.svgraph.skin.node._
import scala.math._
import com.github.skac112.vgutils.MathUtils._

object Straight {
  def apply() = new Straight
}

/**
 * Edge skin consisting of a straight line.
 */
class Straight extends EdgeSkin {
  /**
   * Relative (due to joint radius) offset of a center of a joint circle from an
   * edge of a node skin. Zero means that center of a joint circle lies on an
   * edge of a node skin. Positive values means that joint is shifted
   * respectively outside / inside of an source / destination node skin
   */
  private val jntOffsetFactor: Double = -0.4
  private var _focus: Double = 0.0
  private var _start: Point = Point(0, 0)
  private var _end: Point = Point(0, 0)
  private var _color: WebColor = "#008"
  private var _width: Double = 1.0
  private var lineO: Option[SnapLineElement] = None
  private var srcJointO: Option[SnapElement] = None
  private var dstJointO: Option[SnapElement] = None

//  private var _paperO: Option[SnapPaper] = None

  override def focus_=(value: Double) {
    _focus = value
  }

  override def focus: Double  = _focus

  /**
    * Sets start point of an edge skin. It is a value in paper coordinates.
    */
  override def start_=(value: Point): Unit = {_start = value}

  override def start = _start

  /**
    * Sets end point of an edge skin. It is a value in paper coordinates.
    */
  override def end_=(value: Point): Unit = {_end = value}

  override def end = _end

  /**
    * Given location of source and destination node (in PCS), it returns a pair
    * of directions of an edge for its start and end, respectively.
    * For straight line both directions are just unit vector for difference of dstLoc and srcLoc
    **/
  override def directions(srcLoc: Point, dstLoc: Point): (Angle, Angle) = {
    val dir = (dstLoc - srcLoc).angle
    (dir, dir)
  }

  override def update(graph: SkinGraph, group: SnapGroupElement, paper: SnapPaper): Unit = {
    draw(graph, group, paper)
  }

  override def animUpdate(graph: SkinGraph, group: SnapGroupElement, paper: SnapPaper, time: Int): Unit = {
    val animAttrs = js.Dictionary[Any]("x1" -> _start.x, "y1" -> _start.y, "x2" -> _end.x, "y2" -> _end.y)
    this.lineO foreach {_.animate(animAttrs, time)}
  }

  override def draw(graph: SkinGraph, group: SnapGroupElement, paper: SnapPaper): Unit = {
    clearGroup(group)
    makeGraphics(graph, paper)
    addGraphics(group, paper)
  }

  private def clearGroup(group: SnapGroupElement): Unit = this.lineO foreach {_.remove}

  private def makeGraphics(graph: SkinGraph, paper: SnapPaper): Unit = {
    val line: SnapLineElement = paper.line(_start.x, _start.y, _end.x, _end.y)
    line.attr(attrs)
    this.lineO = Some(line)
    val src_joint = jointGraphics(graph, paper, true)
    this.srcJointO = Some(src_joint)
    val dst_joint = jointGraphics(graph, paper, false)
    this.dstJointO = Some(dst_joint)
  }

  /**
   * Returns graphic of joint on end of an edge. It is a small circle minus part
   * "cut-out" by a circle of a node skin.
   */
  def jointGraphics(graph: SkinGraph, paper: SnapPaper, isSrcJoint: Boolean): SnapElement = {

    /**
     * Selects one of two values according to isSrcJoint values
     */
    def oneOf[T](arg1: T, arg2: T): T = isSrcJoint match {
      case true => arg1
      case _ => arg2
    }

    val node: NodeSkin = oneOf(this.srcNodeSkin(graph), this.dstNodeSkin(graph))
    val other_node: NodeSkin = oneOf(this.dstNodeSkin(graph), this.srcNodeSkin(graph))
    val circle = node.asInstanceOf[Circle]
    // val other_circle = other_node.asInstanceOf[Circle]
    // joint radius
		val jnt_r = 5.0
    val p1 = oneOf(_start, _end)
    val p2 = oneOf(_end, _start)
    val a = (p2 - p1).angle
    val a_offset = oneOf(a, -a)
    // center of a circle of joint
    val jnt_c = p1 + Point.withAngle(a_offset, jntOffsetFactor * jnt_r)
    println(Point.withAngle(a_offset, jntOffsetFactor * jnt_r))
    // center of a circle of a node skin
    val c_cen = p1 + Point.withAngle(-a, circle.r)
    val int_pts = c2i(c_cen, circle.r, jnt_c, jnt_r)
    val pi1 = int_pts.toSeq(0)
    val pi2 = int_pts.toSeq(1)
    // selecting first and second point from intersection points
    val vec1 = jnt_c - c_cen
    val vec2 = pi1 - pi2
    val (p_start, p_end) = if (vec1 ** vec2 > 0) (pi2, pi1) else (pi1, pi2)

    // determining large arc flags for arcs with radius jnt_r
    val laf = jntOffsetFactor > -(circle.r - sqrt(circle.r*circle.r - jnt_r*jnt_r)) / jnt_r

		val path_str = for (
      p <- PathString().moveTo(false, p_start);
      p <- oneOf(p.circleArc(false, jnt_r, laf, true, p_end.x, p_end.y)
			            .circleArc(false, circle.r, false, false, p_start.x, p_start.y),
		             p.circleArc(false, circle.r, false, true, p_end.x, p_end.y)
				          .circleArc(false, jnt_r, laf, true, p_start.x, p_start.y))) yield p.close

		val path = paper.path(path_str.str)
		// val g = paper.g()
    val g_attr = js.Dictionary[Any]("fill" -> _color)
		path.attr(g_attr)
    path
		// g.add(path)
		// // g.addClass("joint");
		// // g.node.onclick(other_node.moveToCenter);
		// g
	}

  private def addGraphics(group: SnapGroupElement, paper: SnapPaper): Unit = {
    group.add(this.lineO.get)
    group.add(this.srcJointO.get)
    group.add(this.dstJointO.get)
  }

  def attrs: js.Dictionary[Any] = js.Dictionary("stroke" -> _color, "strokeWidth" -> _width)

  def color_=(value: WebColor): Unit = {_color = value}

  def width_=(value: Double): Unit = {_width = value}

  private def line = lineO.get


//  def paperO: Option[SnapPaper] = _paperO

//  def paper: SnapPaper = _paperO.get

//  def paper_=(value: SnapPaper): Unit = {_paperO = Some(value)}
}
