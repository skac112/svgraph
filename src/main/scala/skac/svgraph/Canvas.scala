package skac.svgraph
import org.scalajs.jquery.jQuery
import org.scalajs.jquery._
import scala.scalajs.js
import graph._
import transform._
import scala.math._
import skac.euler.General._
import skac.euler._
import com.github.skac112.vgutils._
import org.scalajs.dom
import dom.document

object Canvas {
  // type SkinGraph = Graph[NodeSkin, EdgeSkin]
}

/**
 * Manages displaying and interacting with skin graph (its nodes and edges) in
 * SVG. It has assigned transform which transforms points in "abstract"
 * coordinate system (ACS) to "paper" coordinate system (PCS). It is a
 * composition of scale and translation. Graph is given with node locations in
 * ACS and canvas calculate and draws them and edge skins in PCS.
 * Internally all graphic elements (for node and edge skins) are contained in an
 * SVG group (so-called elements group) and translation part of transform is
 * realized by setting translation for this group. Each element has also its own
 * SVG group where all its graphic "lives". Group of each node skin has
 * translation which corresponds to location of node skin multiplied by scale of
 * transform. Because of being contained in elements group, coordinates of these
 * group don't take into consideration a translation of canvas transform and so
 * can be treated as using "elements group coordinate system" (EGCS).
 * Node locations pertains to ACS, but size of their elements is
 * determined due to PCS, so scaling (equivalent to changing scale component of
 * transform) changes only position of node skin elements in PCS but not their
 * size. So scaling effectively make drawing sparser (zoomin in) or denser
 * (zoomin out).
 */
class Canvas(paperSelector: String) {
  import Canvas._
  private var graphO: Option[SkinGraph] = None
  private val paper = Snap(paperSelector)
  private var elemsGroupO: Option[SnapGroupElement] = None
  private var dragging: Boolean = false
  private var endDrag: Boolean = false
  private var dragPoint: Point = Point(0, 0)
  private def graph = graphO.get
  private def elemsGroup = elemsGroupO.get
  private def jqEl = jQuery(paperSelector)
  jqEl.mousedown(canvasMouseDown _)
  jqEl.mousemove(canvasMouseMove _)
  jqEl.mouseup(canvasMouseUp _)

  /**
   * Map of svg group for each node skin
   */
  private var nodeGroups: Map[NodeSkin, SnapGroupElement] = Map()

  /**
   * Map of svg group for each edge skin
   */
  private var edgeGroups: Map[EdgeSkin, SnapGroupElement] = Map()

  /**
   * Transform transforming points from "abstract" coordinate system (ACS)
   * to "paper" coordinate system (PCS). It is a composition of scaling and
   * translation.
   */
  private var trans: ScaleTransl = ScaleTransl.id

  /**
   * Scale Transform transforming points from "abstract" coordinate system (ACS)
   * to "elements group" coordinate system (EGCS).
   */
  private def egTrans = Scale(trans.scale)

  /**
   * Transform point in ACS to point in EGCS.
   */
  private def ptAtoEG(pt: Point): Point = egTrans transPt pt

  def setGraph(skinGraph: SkinGraph): Unit = {
    paper.clear()
    // elements group - container for all skins
    val el_g = paper.g()
    this.trans = fitTrans(skinGraph)
    // container group has translation always equal to point of translation in
    // ACS -> EGCS translate transform
    val m = translMatrix(trans.transl)
    // println("transformacja acs -> egcs:")
    // println(toTransString(m))
		// el_g.transform(toTransString(m))
    this.elemsGroupO = Some(el_g)
    this.updateElGrTrans()
    this.graphO = Some(skinGraph)
    draw()
  }

  private def paperWidth: Double = jqEl.width()

  private def paperHeight: Double = jqEl.height()

  /**
    * Calculates fitting transform - a transform which fits graphics of a skin
    * graph to a canvas - making them all visible and setting a scale as large as
    * possible
    * @param skinGraph
    * @return
    */
  private def fitTrans(skinGraph: SkinGraph): ScaleTransl = {
    val locations: Set[Point] = skinGraph.nodes map {_.Data.location} toSet
    // bounding box of locations of nodes, graphic of skins is not taken into condideration
    val b_box = Bounds.forPts(locations)
    // scale is calculated to contain both width and height of drawing in
    // paper size
    val scale = min(paperWidth / b_box.w, paperHeight / b_box.h)
    // first scales, then translates - translation (x, y) is calculated such that:
    // s*draw_left + x = 0
    // s*draw_top + y = 0
    // where s is a scale
    val transl = b_box.tl * (-scale)
    ScaleTransl(scale, transl)
  }

  private def translMatrix(transl: Point): SnapMatrix =
   Snap.matrix(1.0, .0, .0, 1.0, transl.x, transl.y)

  private def draw(): Unit = {
    drawNodes()
    drawEdges()
  }

  private def drawNodes(): Unit = graph.nodes foreach drawNode _

  private def drawEdges(): Unit = graph.edges foreach drawEdge _

  private def drawNode(node: NodeInfo[NodeSkin]): Unit = {
    val skin = node.Data
    // creating svg group for node
    val gr = paper.g()
    nodeGroups = nodeGroups + (skin -> gr)
    // moving group to container location (inside elements group)
    updateNodePos(node)
    elemsGroupO.get.add(gr)
    node.Data.draw(graph, gr, paper)
  }

  /**
   * Modifies position of a node skin group in a container group.
   * Should be called after change of node skin location or scale of canvas
   * transform.
   */
  private def updateNodePos(node: NodeDesignator): Unit = {
    val skin = graph.node(node).get.Data
    // translation matrix for a translation given by multiplying scale and location of a skin.
    val m = translMatrix(ptAtoEG(skin.location))
    nodeGroups(skin).transform(toTransString(m))
  }

  /**
   * Sets start and end of edge skin. Method should be called after change of
   * position in drawing of at least one of incident nodes of an edge.
   */
  private def updateEdgeEnds(edge: EdgeDesignator): Unit = {
    // step 1 - calculating EGCS locations of source and destination node of an
    // edge
    val edge_info = graph.edge(edge).get
    val node_skins: List[NodeSkin] =  List(edge_info.SrcNode, edge_info.DstNode) map nodeSkin _
    val node_locs: List[Point] = node_skins map {skin => egTrans.transPt(skin.location)}
    // step 2 - calculating directions
    val skin = edgeSkin(edge)
    val dirs = skin.directions(node_locs(0), node_locs(1))
    // step 3 and 4 - calculating extents and joint points and submitting joint
    // point to edge skin
    skin.start = node_locs(0) + Point.withAngle(dirs._1, node_skins(0).extent(dirs._1))
    skin.end = node_locs(1) - Point.withAngle(dirs._2, node_skins(1).extent(dirs._2))
  }

  private def drawEdge(edge: EdgeInfo[EdgeSkin]): Unit = {
    updateEdgeEnds(edge)
    val skin = edge.Data
    // creating svg group for edge
    val gr = paper.g()
    elemsGroupO.get.add(gr)
    skin.draw(graph, gr, paper)
    edgeGroups = edgeGroups + (skin -> gr)
  }

  private def updateIncidentEdges(node: NodeDesignator): Unit = {
    graph.edges(node) foreach {edge =>
      updateEdgeEnds(edge)
      val skin = edgeSkin(edge)
      val gr = edgeGroups(skin)
      skin.update(graph, gr, paper)
    }
  }

  private def nodeSkin(node: NodeDesignator): NodeSkin = graph.node(node).get.Data

  private def edgeSkin(edge: EdgeDesignator): EdgeSkin = graph.edge(edge).get.Data

  private def toTransString(matrix: SnapMatrix) =
    s"matrix(${matrix.a},${matrix.b},${matrix.c},${matrix.d},${matrix.e},${matrix.f})"

  def canvasMouseDown(event: JQueryEventObject): Unit = {
    if (event.target == jqEl.get(0)) {
      println("mouse down")
      this.dragging = true;
      this.dragPoint = Point(event.pageX, event.pageY);
    }
  }

  def canvasMouseMove(event: JQueryEventObject): Unit = {
      if (event.target == jqEl.get(0) && this.dragging) {
        val new_drag_p = Point(event.pageX, event.pageY)
        val move = new_drag_p - this.dragPoint
        this.dragPoint = new_drag_p
        this.trans = this.trans.copy(transl = this.trans.transl + move)
        this.updateElGrTrans()
      }
  }

  def canvasMouseUp(event: JQueryEventObject): Unit = {
    if (this.dragging) {
      this.dragging = false;
      this.endDrag = true;
    }
  }

  /**
   * Updates transformation of elements group. Should be called after changing
   * of transformation.
   */
  private def updateElGrTrans() {
    if (!elemsGroupO.isEmpty) {
      val m = translMatrix(trans.transl)
      elemsGroup.transform(toTransString(m))
    }
  }

  /**
   * Moves node skin to a given point in ACS coordinates and updates incident
   * edges.
   */
  def moveNode(node: NodeDesignator, pt: Point): Unit = {
    val skin = graph.node(node).get.Data
    skin.location = pt
    updateNodePos(node)
    updateIncidentEdges(node)
  }

  def addNode(data: NodeSkin): Unit = ???
  def removeNode(node: NodeDesignator): Unit = ???
  def addEdge(data: EdgeSkin, srcNode: NodeDesignator, dstNode: NodeDesignator): Unit = ???
  def removeEdge(edge: EdgeDesignator): Unit = ???
  def updateNode(node: NodeDesignator, newData: NodeSkin): Unit = ???
  def updateEdge(edge: EdgeDesignator, newData: EdgeSkin): Unit = ???
  
    // canvas_dom_el.mousedown(function(ev) {
    //   if (ev.target == canvas_dom_el.get(0)) {
    //     canvas.draggingStart = true;
    //     canvas.dragX = ev.clientX;
    //     canvas.dragY = ev.clientY;
    //   }
    // });

    // wleczenie calego rysunku
    // canvas_dom_el.mousemove(function(ev) {
    //   if (ev.target == canvas_dom_el.get(0) && (canvas.dragging || canvas.draggingStart)) {
    //     canvas.draggingStart = false;
    //     canvas.dragging = true;
    //     var moveX = ev.clientX - canvas.dragX;
    //     var moveY = ev.clientY - canvas.dragY;
    //     canvas.dragX = ev.clientX;
    //     canvas.dragY = ev.clientY;
    //     var m = canvas.elems.transform().globalMatrix;
    //     m.add(1, 0, 0, 1, moveX, moveY);
    //     canvas.x -= moveX / canvas.scale;
    //     canvas.y -= moveY / canvas.scale;
    //     canvas.elems.transform(m);
    //   }
    // });
    //
    // canvas_dom_el.mouseup(function(ev) {
    //   if (canvas.dragging) {
    //     canvas.dragging = false;
    //     canvas.endDrag = true;
    //   }
    //   canvas.draggingStart = false;
    // });


    // canvas_dom_el.click(function(ev) {
    //   if (!canvas.endDrag && ev.target == canvas_dom_el.get(0)) {
    //     var x = canvas.abstractX(ev.clientX);
    //     var y = canvas.abstractY(ev.clientY);
    //     var zoom_f = 1.5;
    //     if (ev.shiftKey) {
    //       zoom_f = 1 / zoom_f;
    //     }
    //     canvas.zoom(zoom_f, x, y);
    //   }
    //   canvas.endDrag = false;
    // })
}
