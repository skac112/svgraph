package skac
import scala.scalajs.js
import com.github.skac112.vgutils._
import skac.euler._
import skac.euler.General._

package object svgraph {
  type SkinGraph = Graph[NodeSkin, EdgeSkin]
  trait Skin {
    def label: String = ""
    def label_=(value: String): Unit = {}
    def shortDesc: String = ""
    def shortDesc_=(value: String): Unit = {}
    def desc: String = ""
    def desc_=(value: String): Unit = {}
    def draw(graph: SkinGraph, group: SnapGroupElement, paper: SnapPaper): Unit

    /**
     * Focus is a value in <0; 1> specifying relative, temporary importance of
     * an element.
     */
    def focus_=(value: Double): Unit = {}
    def focus: Double
  }

  type WebColor = String

  /**
   * Represents a node in drawing. It is used as a value of a Data property in
   * node of a skin graph. Conceptually, it represents some node of some other
   * graph (the one which is represented by skin graph). It has a location which
   * pertains to "abstract" coordinate system (ACS). Location can be set by
   * canvas or elsewhere. The method "extent" is to calculate the position of
   * incident edges.
   */
  trait NodeSkin extends Skin {

    /**
     * Sets location of a node in abstract coordinates.
     */
    def location_=(value: Point): Unit

    /**
     * Gets location of a node in abstract coordinates.
     */
    def location: Point

    /**
     * Implementation should return a value which is an "extent" of skin in given
     * direction. The extent should not count for additional elements of a skin
     * such as a label and only take into consideration a "body" of a node.
     * For example, when skin is a circle, extent is equal to the
     * radius. The value is used to calculate the position of incident edges.
     */
    def extent(dir: Angle): Double

    /**
     * Specific implementation should return a value of extent of skin in a given
     * direction. Value is used in layout. Layout should use it to appropriately
     * space node skins so that their layout extents not overlap. In general, this
     * value should be equal or greater in given direction than respective
     * value returned by extent(), due to elements such as a label.
     */
    def layoutExtent(dir: Angle): Double = extend(dir)
  }

  /**
   * Represents an edge in a drawing. Determining how edge will be drawn
   * (apart from actual drawing performed by method draw()) is a
   * multi-step process carried by both an edge skin ("this" object) and the
   * canvas and it is as follows:
   * 1. Canvas calculates locations of source
   * and destination nodes incident to this edge in EGCS.
   * 2. Canvas submits locations from step 1 to this edge skin, obtaining
   * directions for start and end of line of an edge (method
   * EdgeSkin.directions).
   * 3. For directions obtained, canvas determines how far a graphics for
   * respective node extents from its origin (method NodeSkin.extent).
   * 4. Canvas adds values of extents obtained to location of each node in EGCS
   * calculated in step 1, and sets these values as values of start and end
   * property of this edge skin.
   * This approach enables taking into consideration a variety of node shapes
   * and edge line types. A disadvantage of the method is that directions of
   * edge line used to calculate extents of nodes and thus start and end points
   * of an edge line correspond to centers of nodes rather than to points where
   * edge meets nodes (unless node skins are points or at least have zero
   * extent in given direction).
   */
  trait EdgeSkin extends Skin {
    /**
     * Sets start point of an edge skin. It is a value in paper coordinates.
     */
    def start_=(value: Point): Unit

    def start: Point

    /**
     * Sets end point of an edge skin. It is a value in paper coordinates.
     */
    def end_=(value: Point): Unit

    def end: Point

    /**
     * Given location of source and destination node (in PCS), it returns a pair
     * of directions of an edge for its start and end, respectively.
     **/
    def directions(srcLoc: Point, dstLoc: Point): (Angle, Angle)

    def update(graph: SkinGraph, group: SnapGroupElement, paper: SnapPaper): Unit

    /**
      * Updates edge graphics by animation. Default implementation just calls update.
      * @param group
      * @param time time of animation in milliseconds
      */
    def animUpdate(graph: SkinGraph, group: SnapGroupElement, paper: SnapPaper, time: Int): Unit =
      update(graph, group, paper)

    /**
     * Returns source node skin.
     */
    protected def srcNodeSkin(graph: SkinGraph): NodeSkin = {
      val e = graph.edge(this.eda).get
      graph.node(e.SrcNode).get.Data
    }

    /**
     * Returns destination node skin.
     */
    protected def dstNodeSkin(graph: SkinGraph): NodeSkin = {
      val e = graph.edge(this.eda).get
      graph.node(e.DstNode).get.Data
    }
  }
}
