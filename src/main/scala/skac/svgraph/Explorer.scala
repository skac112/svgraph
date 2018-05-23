package skac.svgraph

import focus._

object Explorer {
  type NodeVisFun[ND] = FocusNode[ND] => Double
  type EdgeVisFun[ED] = FocusEdge[ED] => Double
  type NodeSkinFun[ND] = (FocusNode[ND], Double) => NodeSkin
  type EdgeSkinFun[ED] = (FocusEdge[ED], Double) => EdgeSkin
}

import Explorer._

class Explorer[ND, ED](base: WeightedGraphView[ND, ED],
 paperSelector: String,
 nodeVisFun: NodeVisFun[ND],
 edgeVisFun: EdgeVisFun[ED],
 nodeSkinFun: NodeSkinFun[ND],
 edgeSkinFun: EdgeSkinFun[ED]) {
  def dropFocus(node: NodeDesignator): Unit = ???
  def addFocus(node: NodeDesignator): Unit = ???
  def moveFocus(node: NodeDesignator): Unit = ???
  // var focusGraph = new FocusGraph[N, E]()
  val traverser = new FocusTraverser(nodeVisFun, edgeVisFun)
  val viewer = new GraphViewer(paperSelector, nodeSkinFun, edgeSkinFun)
  // viewer.setGraph(focusGraph)
}
