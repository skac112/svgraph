# svgraph
Graph visualization and interaction

SVGraph is a library for embedding a graph visualization, traversing and interacting in a webpage. SVG is used to draw and interact a graph. It is conceived as a generic graph viewer where any kind of graph can be "plugged-in". An input to a system is an object implementing a GraphView trait which exposes structure of a graph. Even very large graphs can be viewed and traversed using a concept of a focus graph - i.e. a subgraph of base graph in which nodes have a focus which is high for current node and decreases for subsequent levels of neighborhood.

WARNING: Project on a very early stage, unusable yet.
