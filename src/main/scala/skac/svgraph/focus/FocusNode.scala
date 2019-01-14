package skac.svgraph.focus

case class FocusNode[ND](baseId: Any, baseData: ND, focus: Double, weight: Double, visibility: Double)
