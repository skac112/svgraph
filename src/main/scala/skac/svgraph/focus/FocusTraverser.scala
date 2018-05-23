package skac.svgraph.focus

object FocusTraverser {
  type VisFun = (Double, Double) => Double
}

/**
 * Extracts focus graph from graph view.
 */
class FocusTraverser[ND, ED](base: WeightedGraphView[ND, ED], nodeVisFun: VisFun, edgeVisFun: VisFun) {
  def dropFocus(baseNode: NodeDesignator): FocusGraph[ND, ED] = ???
}
