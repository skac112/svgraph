//package skac.svgraph
//
//import focus._
//
//object Skinner {
//  type NodeSkinFun[ND, ED] = FocusNode[ND] => NodeSkin
//  type EdgeSkinFun[ND, ED] = FocusEdge[ED] => EdgeSkin
//}
//
//case class Skinner[ND, ED](nodeSkinFun: NodeSkinFun, edgeSkinFun: EdgeSkinFun) extends FocusGraph[ND, ED] => FocusSkinGraph
