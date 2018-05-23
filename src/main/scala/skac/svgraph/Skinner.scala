object Skinner {
  type NodeSkinFun[ND, ED] = FocusNode[ND] => NodeSkin
  type EdgeSkinFun[ND, ED] = FocusEdge[ED] => NodeSkin
}

case class Skinner[ND, ED](nodeSkinFun: NodeSkinFun, edgeSkinFun: EdgeSkinFun) extends Function2[FocusGraph[ND, ED], FocusSkinGraph]
