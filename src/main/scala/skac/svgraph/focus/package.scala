package skac.svgraph

import skac.euler._

package object focus {
  type FocusGraph[ND, ED] = Graph[FocusNode[ND], FocusEdge[ED]]
}
