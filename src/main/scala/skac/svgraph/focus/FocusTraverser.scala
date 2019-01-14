package skac.svgraph.focus

import skac.euler._
import skac.euler.General._
import skac.euler.analysis._

object FocusTraverser {
  /**
   * (focus, weight) => visibility
   */
  type VisFun = (Double, Double) => Double
}

import FocusTraverser._

/**
 * Extracts focus graph from graph view. Visibility is calculated by vis
 * functions from weight and focus of element and used as a criterion to decide
 * if a given element will be included in the result focus graph or not.
 */
class FocusTraverser[ND, ED](base: WeightedGraphView[ND, ED], nodeVisFun: VisFun, edgeVisFun: VisFun)
  extends GraphTraverser[ND, ED, FocusNode[ND], FocusEdge[ED], Double](base) {
  def dropFocus(baseNode: NodeDesignator): FocusGraph[ND, ED] = ???
//    val
//  }

//  initNode: NodeDesignator,
//  initStim: S,
//  stimMergeFun: StimMergeFun,
//  nodeAddFun: NodeAddFun,
//  edgeAddFun: EdgeAddFun,
//  initGraph: ResultGraph): ResultGraph
}
