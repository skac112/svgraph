package skac.svgraph

class Viewer[ND, ED](paperSelector: String) {
  private val canvas = new Canvas(paperSelector)
  private var skinGraph
  private var focusGraph

  def setGraph(graph: FocusGraph[ND, ED]): Unit = {
    focusGraph = graph
    skinGraph = layout(skin(graph))
    canvas.setGraph(skinGraph)
  }

  def addGraph(graph: FocusGraph[ND, ED]): Unit = {
    val new_focus_graph = mergeFocusGraphs(focusGraph, graph, 'ADD)
    val new_skin_g_1 = skin(new_focus_graph)
    val new_skin_g_2 = moveExistSkinLocs(new_skin_g_1, skinGraph)
    val new_skin_g_3 = layoutWithPinned(new_skin_g_2, skinGraph)
    val skin_delta = skinDelta(skinGraph, new_skin_g_3)
    focusGraph = new_focus_graph
    applySkinDelta(skin_delta)
  }

  def moveGraph(graph: FocusGraph[ND, ED]): Unit = ???

  private def layout(skinGraph: SkinGraph): SkinGraph = ???

  private def skin(focusGraph: FocusGraph[ND, ED]): SkinGraph = ???
}
