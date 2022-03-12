# Introduction to Algorithm Repository

## 0. Overview

### 0.1 Implementation details

1. Dictionaries/HashMaps/HashSets may not be the solution we're looking for, so I haven't used Hash Table in the current implementation. We're always exploring an alternative solution that does not rely on the expected O(1) performance of operations involving a hash table. Well, the reason behind this is simply that this is an algorithm-based repository, not project-based one. We're eager for finding a cleverer and more amazing algorithm and data structure to solve the problem.
2. For computational geometry. Java is not that good at visualizing 3D scenario, so I thinking of not using Java when digging into 3D or higher-dimensions scenario. ( but there is indeed a 3D Java library )

### 0.2 updating plans( All updated ):

1.  Voronoi Diagrams enhanced with Point location, including visualization. i.e. query a point in Voronoi Diagrams efficiently.
1.  Point Location with the ability to handle all degenerate cases, including visualization.

## 1. Algorithm

### 1.1 Computational Geometry

Only support 2-dimensional scenario.

#### 1.1.1 Numerical  Tests

| Description   | Entry method                                                 |
| :------------ | ------------------------------------------------------------ |
| toLeft test   | [boolean toLeft( Vector base1, Vector base2, Vector point )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/Triangles.java#L163) |
| inCircle test | [double inCircle( Vector a, Vector b, Vector c, Vector p )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/Circles.java#L122) |

#### 1.1.2 Convex Hull

| Description                         | Entry method\File                                            |
| ----------------------------------- | ------------------------------------------------------------ |
| Graham's Scan                       | [List\<Vector> grahamScan( List\<Vector> points )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/ConvexHull.java#L98) |
| Brute force                         | [List\<Vector> slowConvexHull( List\<Vector> points )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/ConvexHull.java#L42) |
| Program ( including visualization ) | [Programming Assignment 1 - Convex Hull](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/CSCI716/assign_1/AssignmentOne.java) |

#### 1.1.3 Geometric Intersection

| Description                                                  | Entry method\File                                            |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| Line and line                                                | [public Vector lineIntersect( Line l )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/elements/Line.java#L497) |
| Segment and segment                                          | [public Vector segmentIntersect( Line l )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/elements/Line.java#L469) |
| Line and Circle                                              | [Line lineCircleIntersect( Line line, Circle circle )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/GeometricIntersection.java#L67) |
| Bentley Ottmann's algrithom( Intersection Of segment, ray, line and Circle ) | [List\<EventPoint2D> findIntersection( List\<IntersectionShape> shapes )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/GeometricIntersection.java#L375) |
| Program ( including visualization )                          | [CG2017 PA1-2 Crossroad](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/PA_1/problem_2/Main.java) |

#### 1.1.4 Triangulation

| Description                         | Entry method\File                                            |
| :---------------------------------- | ------------------------------------------------------------ |
| Partionting monotone polygons       | [List\<Face> makeMonotone( List\<Vertex> vertices )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/MonotonePolygons.java#L288) |
| Triangulation                       | [List\<Face> preprocessMonotonePolygon( List\<Face> monotonePolygons )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/MonotonePolygons.java#L497) |
| BFS in a dual graph                 | [void BFS( int sizeOfGraph, DualVertex start, DualVertex end )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/graph/tools/SingleShortestPath.java#L241) |
| Funnel algorithm                    | [List\<Vector> Funnel( DualVertex startTriangle, Vector startPoint, Vector endPoint )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/graph/tools/SingleShortestPath.java#L145) |
| Program ( including visualization ) | [CG2017 PA2-1 Shortest Path in The Room](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/PA_2/problem_1/Main.java) |
| Pedagogical Aid Webpage             | [Pedagogical Aid of Triangulation](https://fengkeyleaf.github.io/finalProject/index.html) |

#### 1.1.5 Point Location

| Description                                | Entry method\File                                            |
| :----------------------------------------- | ------------------------------------------------------------ |
| Build trapezoidal Map and search structure | [BoundingBox trapezoidalMap( List<Line\> lines )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/PointLocation.java#L106) |
| Point Locatoin                             | [public SearchVertex get( Vector p )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/SearchStructure.java#L157) |
| Program ( including visualization )        | [Programming Assignment 3 - Trapezoidal Map and Planar Point Location](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/CSCI716/assign_3/AssignmentThree.java) |

#### 1.1.6 Voronoi Diagrams

| Description                                                  | Entry method\File                                            |
| :----------------------------------------------------------- | ------------------------------------------------------------ |
| Build Voronoi Diagrams                                       | [BoundingBox voronoiDiagrams( List<Face\> sites )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/VoronoiDiagrams.java#L223) |
| Find on which cell ( Voronoi Face ) the query point is.      | [List<Face\> findCell( SearchVertex v, Vector p )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/VoronoiDiagrams.java#L68) |
| Generate segments from Voronoi edges to compute the trapezoidal Map of the Voronoi Diagrams. | [List<Line\> getSegments( BoundingBox b )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/VoronoiDiagrams.java#L134) |
| Program ( including visualization )                          | [CG2017 PA2-2 Find Dancing Partners](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/PA_2/problem_2/Main.java) |

### 1.2 POJ

| Problem                     | Description                             | Entry File                                                   |
| --------------------------- | :-------------------------------------- | ------------------------------------------------------------ |
| Subsequence(ID 3061)        | Two approaches, binary search and ruler | [Subsequence.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/coding/POJ/ID_3061/Subsequence.java) |
| Face The Right Way(ID 3276) | One approach, switch                    | [FaceTheRightWay.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/coding/POJ/ID_3276/FaceTheRightWay.java) |

### 1.3 Sorting

| Description    | Entry method\File                                            |
| :------------- | ------------------------------------------------------------ |
| Counting sort  | [void countingSort( List\<NumberRadix> numbers, ... )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/lang/MySorts.java#L32) |
| Radix sort     | [List\<NumberRadix> radixSort( long[] arr, int radix )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/lang/MySorts.java#L54) |
| Insertion sort | [void insertionSort( List\<E> arrayToSort )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/lang/MySorts.java#L54) |
| Merge sort     | [List\<E> mergeSort( List\<E> arrayToSort )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/lang/MySorts.java#L32) |
| Bucket sort    | [void bucketSort( List\<Double> arrayToSort )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/lang/MySorts.java#L140) |

### 1.4 Graph

| Description                                                  | Entry method\File                                            |
| :----------------------------------------------------------- | ------------------------------------------------------------ |
| Breath First Search, BFS                                     | [void BFS( int sizeOfGraph, Vertex start, Vertex end )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/graph/tools/SingleShortestPath.java#L297) |
| Depth Frist Search, DFS                                      | [public int DFS( Vertex vertex, boolean[] visited )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/graph/Graph.java#L101) |
| DFS in Haskell                                               | [dfs :: ( Ix i ) => Graph i -> [ Vertex i ] -> Forest ( Vertex i )](https://github.com/fengkeyleaf/Algorithm/blob/main/Haskell/DFS/MyLibraries/DFS.hs#L153) |
| Bellman Ford( Constricted to make only one edge of progress at a given step) | [void constrictedBellmanFord( Graph\<ShortestVertex> aGraph ... )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/graph/tools/SingleShortestPath.java#L439) |
| Find the max flow in a internet flow                         | [int findMaxFlow( InternetFlowVertex start )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/graph/InternetFlow.java#L199) |
| Get all matching from a bipartite matching                   | [public List<List\<InternetFlowVertex>> getAllMatching()](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/graph/BipartiteMatching.java#L87) |

### 1.5 Animation

#### 1.5.1 keyframing

| Description                    | Entry File/Link                                              |
| :----------------------------- | ------------------------------------------------------------ |
| keyframing                     | [KeyFraming](https://github.com/fengkeyleaf/Algorithm/blob/main/web/myLibraries/animation/KeyFraming.js) |
| Linear Interpolation           | [static LinearInterpolation](https://github.com/fengkeyleaf/Algorithm/blob/main/web/myLibraries/animation/KeyFraming.js#L101) |
| Spherical Linear Interpolation | [static slerp](https://github.com/fengkeyleaf/Algorithm/blob/main/web/myLibraries/animation/KeyFraming.js#L150) |
| Animation Program              | [index.html](https://github.com/fengkeyleaf/Algorithm/blob/main/web/animation/assignmentOne/index.html) |
| Example Video                  | [Bilibili](https://www.bilibili.com/video/BV1f3411s7ji/)     |

#### 1.5.2 Collision System

| Description                                                  | Entry File/Link                                              |
| :----------------------------------------------------------- | ------------------------------------------------------------ |
| Collision Object                                             | [CollidingObject](https://github.com/fengkeyleaf/Algorithm/blob/main/web/myLibraries/animation/CollidingObject.js) |
| Detect collision and trackback to the point where the first collision happens | [static detectCollision](https://github.com/fengkeyleaf/Algorithm/blob/main/web/myLibraries/animation/Dynamics.js#L38) |
| Binary Search to find the point where the first collision happens | [static __binarySearchCollision](https://github.com/fengkeyleaf/Algorithm/blob/main/web/myLibraries/animation/Dynamics.js#L153) |
| Find collided objects( brute force )                         | [static __isColliding]()                                     |
| Animation Program - Billiards                                | [index.html](https://github.com/fengkeyleaf/Algorithm/blob/main/web/animation/assignmentTwo/index.html) |
| Example Video                                                | [Bilibili](https://www.bilibili.com/video/BV1Cr4y1D7Td/)     |

#### 1.5.3 Motion Capture System

| Description                                              | Entry File/Link                                              |
| :------------------------------------------------------- | ------------------------------------------------------------ |
| Set up Articulated Figures from the Skeleton in three.js | [static setupShapes](https://github.com/fengkeyleaf/Algorithm/blob/main/web/myLibraries/animation/MoCop.js#L39) |
| Animate Hierarchical Models                              | [static orientBone](https://github.com/fengkeyleaf/Algorithm/blob/main/web/myLibraries/animation/MoCop.js#L220) |
| Animation Program                                        | [index.html](https://github.com/fengkeyleaf/Algorithm/blob/main/web/animation/assignmentThree/index.html) |
| Example Video                                            | [Bilibili](https://www.bilibili.com/video/BV1zg411w7bn/)     |

#### 1.5.4 Particle System

| Description                                         | Entry File/Link                                              |
| :-------------------------------------------------- | ------------------------------------------------------------ |
| Particle System                                     | [ParticleSystem](https://github.com/fengkeyleaf/Algorithm/blob/main/web/myLibraries/animation/ParticleSystem.js) |
| Get random Unit Vector3 bounded by a given Cone.    | [randomUnitVector3InCone](https://github.com/fengkeyleaf/Algorithm/blob/main/web/myLibraries/animation/Particle.js#L109) |
| Animation Program - comet                           | [index.html](https://github.com/fengkeyleaf/Algorithm/blob/main/web/animation/assignmentFour/index.html) |
| Animation Program - Static wall and gravity applied | [SaticWall.html](https://github.com/fengkeyleaf/Algorithm/blob/main/web/animation/assignmentFour/SaticWall.html) |
| Example Video                                       | [Bilibili](https://www.bilibili.com/video/BV1gR4y1x7SQ/)     |

## 2. Data Structure

### 2.1 Basics

| Description                                                  | Entry File                                                   |
| :----------------------------------------------------------- | ------------------------------------------------------------ |
| Doubly Linked List ( With the ability to remove / insert a node directly from / into the list. ) | [MyLinkedList.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/MyLinkedList.java) |

### 2.1 Tree

| Description                                                  | Entry File                                                   |
| :----------------------------------------------------------- | ------------------------------------------------------------ |
| Binary Search Tree ( put(), deleteMin(), deleteMax(), delete(), min(), max(), etc.) | [BinarySearchTree.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/tree/BinarySearchTree.java) |
| Red Black Tree ( put(), deleteMin(), deleteMax(), delete(), min(), max(), etc. ) | [RedBlackTree.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/tree/RedBlackTree.java) |
| Segment Tree ( Range maximum and minimum Query )             | [SegmentTree.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/tree/SegmentTree.java) |
| Priority Queue                                               | [MyPriorityQueue.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/tree/MyPriorityQueue.java) |
| Doubly Linked Binary Search Tree ( With the ability delete / insert a node directly from / into the BST ) | [DoublyLinkedBST.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/tree/DoublyLinkedBST.java) |
| Doubly Linked Red Black Tree ( With the ability delete / insert a node directly from / into the R-B Tree ) | [DoublyLinkedRBT.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/tree/DoublyLinkedRBT.java) |

### 2.2 Graph

| Description                       | Entry File                                                   |
| :-------------------------------- | ------------------------------------------------------------ |
| Graph                             | [Graph.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/graph/Graph.java) |
| Graph in Haskell                  | [Graph.hs](https://github.com/fengkeyleaf/Algorithm/blob/main/Haskell/DFS/MyLibraries/Graph.hs) |
| Strongly connected component, SCC | [SCC.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/graph/SCC.java) |
| Directed acyclic graph, DAG       | [DAG.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/graph/DAG.java) |
| Minimum spanning tree, MST.       | [MST.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/graph/tools/MST.java) |
| Union Find                        | [UnionFind.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/graph/UnionFind.java) |
| Internet Flow                     | [InternetFlow.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/graph/InternetFlow.java) |
| Bipartite Matching                | [BipartiteMatching.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/graph/BipartiteMatching.java) |

### 2.3 Computational Geometry

Only support 2-dimensional scenario.

#### 2.3.1 DCEL

| Description                                                  | Entry File/Package                                           |
| :----------------------------------------------------------- | :----------------------------------------------------------- |
| Doubly-connected edge list                                   | [DCEL](https://github.com/fengkeyleaf/Algorithm/tree/main/Java/myLibraries/util/geometry/DCEL) |
| Get all incident edges of the vertex                         | [List\<HalfEdge> allIncidentEdges( Vertex vertex )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/DCEL/DCEL.java#L231) |
| Walk around all halfEdges, starting at face and get visited vertices | [List\<Vertex> walkAroundVertex( Face face )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/DCEL/DCEL.java#L222) |
| Find the first ClockWise Edge with two vertices destination and origin | [HalfEdge firstClockWiseEdge( Vertex destination, Vertex origin )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/DCEL/DCEL.java#L52) |
| Find the first CounterClockWise Edge with two vertices destination and origin | [HalfEdge firstCounterClockWiseEdge( Vertex origin, Vertex destination )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/DCEL/DCEL.java#L85) |

#### 2.3.2 Point Location

| Description                       | Entry File/Package                                           |
| :-------------------------------- | :----------------------------------------------------------- |
| Trapezoidal Map                   | [public class Trapezoid](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/TrapezoidalMap.java) |
| Search Structure( Tree-like DAG ) | [public class SearchStructure](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/SearchStructure.java) |

#### 2.3.3 others

| Description                                   | Entry File/Package                                           |
| :-------------------------------------------- | :----------------------------------------------------------- |
| BoundingBox ( Bounding box for a half plane ) | [BoundingBox.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/BoundingBox.java) |
