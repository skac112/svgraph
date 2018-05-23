package skac.svgraph.focus

object FocusGraph {
  type NodeVisFun[ND] = FocusNode => Double
  type EdgeVisFun[ED] = FocusEdge => Double
}

class FocusGraph[ND, ED](nodeVisFun: NodeVisFun, edgeVisFun: EdgeVisFun) extends Graph[FocusNode, FocusEdge] {

}
