package skac.svgraph.focus

case class FocusEdge[ED](baseId: Any, baseData: ED, focus: Double, weight: Double, visibility: Double)
