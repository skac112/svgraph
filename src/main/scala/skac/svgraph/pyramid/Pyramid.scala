package skac.svgraph.pyramid

import skac.euler._
import skac.euler.General._

object Pyramid {
//  type AnyGraph = Graph[_, _]
//  type BinaryGraphFun = (AnyGraph, AnyGraph) => AnyGraph
  /**
    * Binary block function. Arguments and result should be graph blocks - i.e graph columns (pyramids of isomorphic
    * graphs) with two layers (two graphs).
    */
//  type BinBlockFun = (Pyramid, Pyramid) => Pyramid

//  def binBlockAddFun = ???

  /**
    * (data1, data2, baseData) => newData
    */
  type NodeBlendFun = (Any, Any, Any) => Any

  type EdgeBlendFun = (Any, Any, Any) => Any
}

/**
  * Kind of a stack of graph views (which may be graphs) where each gv is smaller or equal (in graph-theoretic sense)
  * to gv lower in a stack (is a subgraph of it) and can have elements data altered.
  * Each gv (except for the bottom one, the base of the pyramid) has its elements (nodes and edges) matched to elements
  * of gv lower in a pyramid (by so-called roots).
  * This structure can handle graphs and graph views of explorer (base graph view, focus graph and skin graph).
  * Graph views in pyramid have assigned layers. Bottom layer (base layer) is 0.
  */
trait Pyramid {
  import Pyramid._
  def graphViews: Seq[GraphView[_, _]]
  def layerNum = graphViews.size
  def base = graphViews(0)
  def nodeRoots(layer: Int, nodeDes: NodeDesignator): NodeInfo[_]
  def edgeRoots(layer: Int, edgeDes: EdgeDesignator): EdgeInfo[_]
  def addNode(data: Any, layer: Int): Pyramid = ???
  def addEdge(data: Any, srcNode: NodeDesignator, dstNode: NodeDesignator, layer: Int): Pyramid = ???
  def updateNode(nodeDes: NodeDesignator, newData: Any, layer: Int): Pyramid = ???
  def updateEdge(edgeDes: EdgeDesignator, newData: Any, layer: Int): Pyramid = ???
  def addNodeRoot(highNodeDes: NodeDesignator, lowNodeDes: NodeDesignator, highLayer: Int): Pyramid = ???
  def lowerNode(nodeDes: NodeDesignator, layer: Int): NodeInfo[_] = ???
  def higherNode(nodeDes: NodeDesignator, layer: Int): Option[NodeInfo[_]] = ???
  def top(layer: Int): Pyramid = ???

  /**
    * Blends in other pyramid into this pyramid at a given layer. In newly generated pyramid a layer of blending
    * is the same as in this pyramid and the next layer of this pyramid and layer 1 (second) in other pyramid are added to
    * create new layer of blending in recursive execution where "new this" pyramid and rest of other pyramid are
    * blended in.
    * @param layer
    * @param other
    * @param nodeBlendFun
    * @param edgeBlendFun
    * @return
    */
  def blend(layer: Int, other: Pyramid, nodeBlendFun: NodeBlendFun, edgeBlendFun: EdgeBlendFun): Pyramid = {
    // blending of single layer from both pyramids
    val (new_this, new_other) = blendLayers(other, layer, nodeBlendFun, edgeBlendFun)
    // checking if there are higher layers to blend
    if (layerNum > layer + 2 && new_other.layerNum > 1) {
      // new blend because there are higher layers to blend
      new_this.blend(layer + 1, new_other, nodeBlendFun, edgeBlendFun)
    }
    else {
      new_this
    }
  }

  private def blendLayers(other: Pyramid, layer: Int, nodeBlendFun: NodeBlendFun, edgeBlendFun: EdgeBlendFun): (Pyramid, Pyramid) = {
    val (new_this, new_other_1) = other.graphViews(1).asInstanceOf[Graph[_, _]].nodes.foldLeft[(Pyramid, Pyramid)]((this, other)) {
      case ((curr_this: Pyramid, curr_other: Pyramid), oni) => {
        curr_this.graphViews(layer).node(curr_other.lowerNode(oni, 1)) match {
          // base node from other pyramid exists in gv No. "layer" of this pyramid - current "this" pyramid and
          // current other pyramid are updated
          case Some(tlni) => {
            curr_this.higherNode(tlni, layer) match {
              // node in layer-gv has got higher node in this pyramid (on layer+1 level) - node blending is needed
              case Some(tni) => {
                val blend_data = nodeBlendFun(tni.Data, oni.Data, tlni.Data)
                // updating changes also roots
                val new_this = curr_this.updateNode(tni, blend_data, layer + 1)
                val new_other = curr_other.updateNode(oni, blend_data, 1)
                (new_this, new_other)
              }
              // node in layer-gv has not higher node in this pyramid - node from layer 1 of other pyramid is added
              // and rooted in gv of level layer + 1 of this pyramid
              case _ => {
                val new_this_1 = curr_this.addNode(oni.Data, layer + 1)
                val new_this = new_this_1.addNodeRoot(oni, tlni, layer + 1)
                (new_this, curr_other)
              }
            }
          }
          // base node from other pyramid does not exists in base of this pyramid - nothing changes
          case _ => (curr_this, curr_other)
        }
      }
    }
    val new_other = new_other_1.top(1)
    (new_this, new_other)
  }
//  def blendAdd(layer: Int, other: Pyramid): Pyramid = blend(layer, other, binBlockAddFun)
}

