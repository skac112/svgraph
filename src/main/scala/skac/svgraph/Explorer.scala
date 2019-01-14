package skac.svgraph

import focus._
import Explorer._
import skac.euler._
import skac.euler.General._
//import FocusTraverser._

object Explorer {
  type NodeSkinFun[ND] = (FocusNode[ND], Double) => NodeSkin
  type EdgeSkinFun[ED] = (FocusEdge[ED], Double) => EdgeSkin
}

class Explorer[ND, ED](base: WeightedGraphView[ND, ED],
 paperSelector: String,
 nodeVisFun: FocusTraverser.VisFun,
 edgeVisFun: FocusTraverser.VisFun,
 nodeSkinFun: NodeSkinFun[ND],
 edgeSkinFun: EdgeSkinFun[ED]) {
  def dropFocus(node: NodeDesignator): Unit = ???
  def addFocus(node: NodeDesignator): Unit = ???
  def moveFocus(node: NodeDesignator): Unit = ???
  // var focusGraph = new FocusGraph[N, E]()
  val traverser = new FocusTraverser(base, nodeVisFun, edgeVisFun)
  val viewer = new Viewer(paperSelector)
  // viewer.setGraph(focusGraph)
}
