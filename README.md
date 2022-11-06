# Introduction to Algorithm Repository

## 0. Overview

### 0.1 Implementation details

1. Dictionaries/HashMaps/HashSets may not be the solution we're looking for, so I haven't used Hash Table in the current implementation. We're always exploring an alternative solution that does not rely on the expected O(1) performance of operations involving a hash table. Well, the reason behind this is simply that this is an algorithm-based repository, not project-based one. We're eager for finding a cleverer and more amazing algorithm and data structure to solve the problem.
2. For computational geometry. Java is not that good at visualizing 3D scenario, so I thinking of not using Java when digging into 3D or higher-dimensions scenario. ( but there is indeed a 3D Java library )

### 0.2 updating plans:

1.  Linear programming, including visualization.
1.  ~~Ten programming assignments~~ from [Tsinghua Computational Geometry at edX](https://www.edx.org/course/computational-geometry)
1.  ~~Half-plane Intersections, Duality including visualization.~~
1.  ~~Delaunay Triangulation, a dual graph of Voronoi Diagrams, including visualization.~~
1.  ~~Orthogonal range searching with kd-tree, range tree, layered range tree ( fractional cascading ), including visualization.~~
1.  ~~Orthogonal windowing query with interval tree, interval tree enhanced with range tree, priority search tree and segment tree, including visualization.~~
1.  ~~Map Overlay, Boolean Operations, including visualization.~~
1.  ~~Voronoi Diagrams enhanced with Point location, including visualization. i.e. query a point in Voronoi Diagrams efficiently~~.
1.  ~~Point Location with the ability to handle all degenerate cases, including visualization~~.

## 1. Algorithm

### 1.1 Computational Geometry

Only support 2-dimensional scenario.

#### 1.1.1 Numerical  Tests

| Description   | Entry method                                                 |
| :------------ | ------------------------------------------------------------ |
| toLeft test   | [boolean toLeft( Vector base1, Vector base2, Vector point )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Triangles.java#L145) |
| inCircle test | [double inCircle( Vector a, Vector b, Vector c, Vector p )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Circles.java#L129) |

#### 1.1.2 Convex Hull

| Description                         | Entry method\File                                            |
| ----------------------------------- | ------------------------------------------------------------ |
| Graham's Scan                       | [List\<Vector> grahamScan( List\<Vector> points )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/ConvexHull.java#L117) |
| Brute force                         | [List\<Vector> slowConvexHull( List\<Vector> points )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/ConvexHull.java#L57) |
| Program ( including visualization ) | [CG2017 PA1-1 Convex Hull](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/CGTsinghua/PA_1/problem_1/Main.java) / [CG2017 PA5-2 Dynamic Convex Hull](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/CGTsinghua/PA_5/problem_1/Main.java) |

#### 1.1.3 Geometric Intersection

| Description                                                  | Entry method\File                                            |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| Line and line                                                | [Vector lineIntersect( Line l )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/GeometricIntersection.java#L71) |
| Segment and segment                                          | [Vector segmentIntersect( Line l )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/GeometricIntersection.java#L189) |
| Segment and Circle                                           | [Vector[] segmentCircle( Segment s, Circle c )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/GeometricIntersection.java#L230) |
| Line and Circle                                              | [Line lineCircleIntersect( Line line, Circle circle )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/GeometricIntersection.java#L258) |
| Brute Force                                                  | [List<Vector\> bruteForce( List<E\> S )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/GeometricIntersection.java#L38) |
| Bentley Ottmann's algrithom( Intersection Of segment, ray, line and Circle ) | [List\<EventPoint2D> findIntersection( List\<IntersectionShape> S )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/GeometricIntersection.java#L604) |
| Program ( including visualization )                          | [CG2017 PA1-2 Crossroad](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/CGTsinghua/PA_1/problem_2/Main.java) |

#### 1.1.4 Triangulation

| Description                         | Entry method\File                                            |
| :---------------------------------- | ------------------------------------------------------------ |
| Partionting monotone polygons       | [List\<Face> makeMonotone( List\<Vertex> vertices )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/MonotonePolygons.java#L348) |
| Triangulation                       | [List\<Face> triangulate( List\<Face> monotonePolygons )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Triangulation.java#L194) |
| BFS in a dual graph                 | [void BFS( int sizeOfGraph, DualVertex start, DualVertex end )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/graph/SingleShortestPath.java#L242) |
| Funnel algorithm                    | [List\<Vector> Funnel( DualVertex startTriangle, Vector startPoint, Vector endPoint )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/graph/SingleShortestPath.java#L146) |
| Program ( including visualization ) | [CG2017 PA2-1 Shortest Path in The Room](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/CGTsinghua/PA_2/problem_1/Main.java) |
| Pedagogical Aid Webpage             | [Pedagogical Aid of Triangulation](https://fengkeyleaf.github.io/finalProject/index.html) |

#### 1.1.5 Voronoi Diagrams

| Description                                                  | Entry method\File                                            |
| :----------------------------------------------------------- | ------------------------------------------------------------ |
| Build Voronoi Diagrams                                       | [BoundingBox voronoiDiagrams( List<Vector\> P )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/VoronoiDiagrams.java#L228) |
| Find on which cell ( Voronoi Face ) the query point is.      | [List<Face\> findCell( SearchVertex v, Vector p )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/VoronoiDiagrams.java#L63) |
| Generate segments from Voronoi edges to compute the trapezoidal Map of the Voronoi Diagrams. | [List<Line\> getSegments( BoundingBox b )]()                 |
| Program ( including visualization )                          | [CG2017 PA2-2 Find Dancing Partners](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/CGTsinghua/PA_2/problem_2/Main.java) |

#### 1.1.6 Delaunay Triangualtion

| Description                               | Entry method\File                                            |
| :---------------------------------------- | ------------------------------------------------------------ |
| Build Delaunay Triangulation              | [Face delaunayTriangulation( List<Vertex\> P )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Delaunay.java#L53) |
| Find a triangle piPjPk ∈ T containing pr. | [DelaunayVertex get( Vector pr )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/DelaunaySearch.java#L44) |
| Program ( including visualization )       | [CG2017 PA3-1 Delaunay Triangulation](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/CGTsinghua/PA_3/problem_1/Main.java) |

#### 1.1.7 Point Location

| Description                                | Entry method\File                                            |
| :----------------------------------------- | ------------------------------------------------------------ |
| Build trapezoidal Map and search structure | [BoundingBox trapezoidalMap( List<Line\> S, , List<Vector\> Q )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/PointLocation.java#L136) |
| Point Locatoin                             | [SearchVertex get( Vector p )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/SearchStructure.java#L163) |
| Program ( including visualization )        | [CG2017 PA3-2 Which wall are you looking at](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/CGTsinghua/PA_3/problem_2/Main.java) |

#### 1.1.8 Orthogonal Range Query

| Description                                         | Entry method\File                                            |
| :-------------------------------------------------- | ------------------------------------------------------------ |
| Build Kd-tree                                       | [KdNode build( List<Vector\> P, int d )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/KdTree.java#L358) |
| 2D Range Query in Kd-tree                           | [List<Vector\> query( List<Vector\> R )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/KdTree.java#L446) |
| Build Range Tree                                    | [RangeNode build( List<Vector\> P )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/RangeTree.java#L251) |
| 1D Range Query in Range Tree                        | [List<Vector\> query1D( List<Vector\> R )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/RangeTree.java#L519) |
| 2D Range Query in Range Tree                        | [List<Vector\> query2D( String[] R )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/RangeTree.java#L326) |
| Build layered range tree with fractional cascading, | [List<LayerNode\> build( RangeNode n )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/LayeredRangeTree.java#L64) |
| 2D Range Query in Layered  Range Tree               | [List<Vector\> query( String[] R )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/LayeredRangeTree.java#L126) |
| Program ( including visualization )                 | [CG2017 PA4-1 Planar Range Query](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/CGTsinghua/PA_4/problem_1/Main.java) |

#### 1.1.9 Orthogonal Windowing Query

| Description                                      | Entry method\File                                            |
| :----------------------------------------------- | ------------------------------------------------------------ |
| Build Interval Tree                              | [IntervalNode build( List<LineNode\> I )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/IntervalTree.java) |
| Stabbing query with Interval Tree                | [List<Line\> query( Vector q )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/IntervalTree.java#L284) |
| Build Interval Tree combing Range Tree           | [IntervalRangeNode build( List<LineNode\> I )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/IntervalRangeTree.java#L154) |
| Windowing query in Interval Range Tree           | [List<Line\> query( List<Vector\> R )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/IntervalRangeTree.java#L184) |
| Build Interval Tree combing Priority Search Tree | [PriorityNode build( List<LineNode\> I )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/PriorityIntervalTree.java#L361) |
| Windowing query in Priority Interval Tree        | [List<Vector\> query( QueryVector[] R )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/PriorityIntervalTree.java#L266) |
| Build Segment Tree                               | [SegmentNode build( List<Interval\> I )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/SegmentTree.java#L339) |
| Stabbing query with Segment Tree                 | [List<Line\> query( Vector q )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/SegmentTree.java#L384) |
| Build Segment Tree combing Range Tree            | [SegmentRangeNode build( List<Interval\> I )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/SegmentRangeTree.java#L212) |
| Windowing query in Segment Range Tree            | [List<Line\> query( List<Vector\> R )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/SegmentRangeTree.java#L237) |
| Program ( including visualization )              | [CG2017 PA4-2 Orthogonal Windowing Query](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/CGTsinghua/PA_4/problem_2/Main.java) |

#### 1.1.10 MapOverlay & Boolean Operations

| Description                                            | Entry method\File                                            |
| :----------------------------------------------------- | ------------------------------------------------------------ |
| Computing the overlay of two subdivisions              | [Face compute( Face s1, Face s2 )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/MapOverlay.java#L533) |
| Compute the intersection of two subdivisions, P1 ∩ P2. | [List<Face\> intersection( Face s1, Face s2 )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/BooleanOperations.java#L62) |
| Compute the union of two subdivisions, P1 ∪ P2,        | [List<Face\> union( Face s1, Face s2 )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/BooleanOperations.java#L95) |
| Compute the difference of two subdivisions, P1 \ P2.   | [List<Face\> difference( Face s1, Face s2 )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/BooleanOperations.java#L117) |

#### 1.1.11 Half-plane Intersection

| Description                         | Entry method\File                                            |
| :---------------------------------- | ------------------------------------------------------------ |
| Compute half-plane intersection.    | [void intersect( List<HalfPlane\> H )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/HalfPlaneIntersection.java#L420) |
| Get current result type.            | [Type getResultType()](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/HalfPlaneIntersection.java#L478) |
| Program ( including visualization ) | [CG2017 PA5-2 FruitNinja](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/CGTsinghua/PA_5/problem_2/Main.java) |

#### 1.1.12 Duality

| Description                                                  | Entry method\File                                            |
| :----------------------------------------------------------- | ------------------------------------------------------------ |
| Transform this point in the primary plane into the dual plane, point to line. | [Line toDuality()](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Vector.java#L251) |
| Transform this point in the dual plane into the primary plane, point to line. | [Line fromDuality()](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Vector.java#L264) |
| Transform this line in the primary plane into the dual plane, line to point. | [Vector toDuality()](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Line.java#L371) |

### 1.2 POJ

| Problem                     | Description                             | Entry File                                                   |
| --------------------------- | :-------------------------------------- | ------------------------------------------------------------ |
| Subsequence(ID 3061)        | Two approaches, binary search and ruler | [Subsequence.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/coding/POJ/ID_3061/Subsequence.java) |
| Face The Right Way(ID 3276) | One approach, switch                    | [FaceTheRightWay.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/coding/POJ/ID_3276/FaceTheRightWay.java) |

### 1.3 Sorting

| Description    | Entry method\File                                            |
| :------------- | ------------------------------------------------------------ |
| Counting sort  | [void countingSort( List\<NumberRadix> numbers, ... )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/lang/MySorts.java#L36) |
| Radix sort     | [List\<NumberRadix> radixSort( long[] arr, int radix )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/lang/MySorts.java#L58) |
| Insertion sort | [void insertionSort( List\<E> arrayToSort )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/lang/MySorts.java#L86) |
| Merge sort     | [List\<E> mergeSort( List\<E> arrayToSort )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/lang/MySorts.java#L110) |
| Bucket sort    | [void bucketSort( List\<Double> arrayToSort )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/lang/MySorts.java#L160) |

### 1.4 Graph

| Description                                                  | Entry method\File                                            |
| :----------------------------------------------------------- | ------------------------------------------------------------ |
| Breath First Search, BFS                                     | [void BFS( int sizeOfGraph, Vertex start, Vertex end )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/graph/SingleShortestPath.java#L298) |
| Depth Frist Search, DFS                                      | [int DFS( Vertex vertex, boolean[] visited )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/lang/MySorts.java#L160) |
| DFS in Haskell                                               | [dfs :: ( Ix i ) => Graph i -> [ Vertex i ] -> Forest ( Vertex i )](https://github.com/fengkeyleaf/Algorithm/blob/main/Haskell/DFS/MyLibraries/DFS.hs#L153) |
| Bellman Ford( Constricted to make only one edge of progress at a given step) | [void constrictedBellmanFord( Graph\<ShortestVertex> aGraph ... )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/graph/SingleShortestPath.java#L387) |
| Find the max flow in a internet flow                         | [int findMaxFlow( InternetFlowVertex start )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/graph/SingleShortestPath.java#L387) |
| Get all matching from a bipartite matching                   | [List<List\<InternetFlowVertex>> getAllMatching()](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/graph/SingleShortestPath.java#L387) |

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
| Doubly Linked List ( With the ability to remove / insert a node directly from / into the list. ) | [MyLinkedList.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/MyLinkedList.java) |

### 2.1 Tree

| Description                                                  | Entry File                                                   |
| :----------------------------------------------------------- | ------------------------------------------------------------ |
| Binary Search Tree ( put(), deleteMin(), deleteMax(), delete(), min(), max(), etc.) | [BinarySearchTree.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/tree/BinarySearchTree.java) |
| Red Black Tree ( put(), deleteMin(), deleteMax(), delete(), min(), max(), etc. ) | [RedBlackTree.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/tree/RedBlackTree.java) |
| Segment Tree ( Range maximum and minimum Query )             | [SegmentTree.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/tree/SegmentTree.java) |
| Priority Queue                                               | [MyPriorityQueue.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/tree/MyPriorityQueue.java) |
| Doubly Linked Binary Search Tree ( With the ability delete / insert a node directly from / into the BST ) | [DoublyLinkedBST.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/tree/DoublyLinkedBST.java) |
| Doubly Linked Red Black Tree ( With the ability delete / insert a node directly from / into the R-B Tree ) | [DoublyLinkedRBT.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/tree/DoublyLinkedRBT.java) |

### 2.2 Graph

| Description                       | Entry File                                                   |
| :-------------------------------- | ------------------------------------------------------------ |
| Graph                             | [Graph.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/graph/Graph.java) |
| Graph in Haskell                  | [Graph.hs](https://github.com/fengkeyleaf/Algorithm/blob/main/Haskell/DFS/MyLibraries/Graph.hs) |
| Strongly connected component, SCC | [SCC.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/graph/SCC.java) |
| Directed acyclic graph, DAG       | [DAG.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/graph/DAG.java) |
| Minimum spanning tree, MST.       | [MST.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/graph/MST.java) |
| Union Find                        | [UnionFind.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/graph/UnionFind.java) |
| Internet Flow                     | [InternetFlow.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/graph/InternetFlow.java) |
| Bipartite Matching                | [BipartiteMatching.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/graph/BipartiteMatching.java) |

### 2.3 Computational Geometry

Only support 2-dimensional scenario.

#### 2.3.1 DCEL

##### 2.3.1.1 Vertex

| Description                                                  | Entry File                                                   |
| :----------------------------------------------------------- | :----------------------------------------------------------- |
| Get all incident edges of the vertex                         | [public List<HalfEdge\> allIncidentEdges()](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Vertex.java#L121) |
| Find the first ClockWise Edge with two vertices destination and origin | [HalfEdge firstClockWiseEdge(  Vertex origin )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Vertex.java#L210) |
| Find the first CounterClockWise Edge with two vertices destination and origin | [HalfEdge firstCounterClockWiseEdge( Vertex destination )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Vertex.java#L245) |
| Connect two vertices by adding new half-edges                | [Face connect( Vertex v )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Vertex.java#L279) |
| Re-connect half-edges incident to this vertex.(Duality Implementation) | [void connect( List<HalfEdge\> E )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Vertex.java#L302) |

##### 2.3.1.2 Half-edge

| Description                                                  | Entry File                                                   |
| :----------------------------------------------------------- | :----------------------------------------------------------- |
| Walk around all halfEdges connected to this one, and get vertices incident to them. | [List<Vertex\> walkAroundVertex()](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/HalfEdge.java#L157) |
| Walk around and get all halfEdges connected to this one.     | [List<HalfEdge\> walkAroundEdge()](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/HalfEdge.java#L174) |
| Split the edge into two parts                                | [Vertex split( Vertex split )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/HalfEdge.java#L235) |
| Get all inner faces bounded by this half-edge, but not including holes. | [Collection<HalfEdge\> getInners()](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/HalfEdge.java#L371) |
| Sort half-edges in clock wise order with the point i as the center. | [List<HalfEdge\> sortInClockWise( List<HalfEdge\> E, Vector i )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/HalfEdges.java#L235) |

##### 2.3.1.3 Face

| Description                                                  | Entry File                                                   |
| :----------------------------------------------------------- | :----------------------------------------------------------- |
| Walk around all halfEdges, starting at this face and get visited halfEdges. | [List<HalfEdge\> walkAroundEdge()](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Face.java#L110) |
| Walk around all halfEdges, starting at this face and get visited vertices. | [List<Vertex\> walkAroundVertex()](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Face.java#L119) |
| Is the point inside this convex hull? but excluding the boundary. | [boolean isInsideConvexHull( Vector p )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Face.java#L155) |
| Is the point on this convex hull? including the boundary.    | [boolean isOnConvexHull( Vector p )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Face.java#L182) |
| Is the point inside This Polygon?                            | [boolean isInsidePolygon( Vector p )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Face.java#L206) |
| Is the point On This Polygon?                                | [boolean isOnPolygon( Vector p )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Face.java#L223) |

#### 2.3.2 Shapes

| Description                                   | Entry File/Package                                           |
| :-------------------------------------------- | :----------------------------------------------------------- |
| Vector (2D Point)                             | [Vector.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Vector.java) |
| BoundingBox ( Bounding box for a half plane ) | [BoundingBox.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/BoundingBox.java) |
| Arc                                           | [Arc.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Arc.java) |
| Circle                                        | [Circle.java](http://www.icourses.cn/web/sword/portal/shareDetails?&&&&&cId=6661#/course/chapter) |
| Line                                          | [Line.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Line.java) |
| Ray                                           | [Ray.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Ray.java) |
| Segment                                       | [Segment.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Segment.java) |
| Parabola                                      | [Parabola.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/Parabola.java) |
| HalfPlane                                     | [HalfPlane.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/HalfPlane.java) |

#### 2.3.3 Space Trees

##### 2.3.3.1 Orthogonal Range Query

| Description                                 | Entry File/Package                                           |
| :------------------------------------------ | :----------------------------------------------------------- |
| Kd-tree                                     | [KdTree.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/KdTree.java) |
| Range Tree                                  | [RangeTree.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/RangeTree.java) |
| Layered Range Tree ( Fractional Cascading ) | [LayeredRangeTree.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/LayeredRangeTree.java) |

##### 2.3.3.2 Orthogonal Windowing Query

| Description            | Entry File/Package                                           |
| :--------------------- | :----------------------------------------------------------- |
| Interval Tree          | [IntervalTree.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/IntervalTree.java) |
| Segment Tree           | [SegmentTree.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/SegmentTree.java) |
| Interval Range Tree    | [IntervalRangeTree.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/IntervalRangeTree.java) |
| Priority Interval Tree | [PriorityIntervalTree.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/PriorityIntervalTree.java) |
| Segment Range Tree     | [SegmentRangeTree.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/com/fengkeyleaf/util/geom/SegmentRangeTree.java) |

#### 2.3.4 Point Location

| Description                       | Entry File/Package                                           |
| :-------------------------------- | :----------------------------------------------------------- |
| Trapezoidal Map                   | [public class Trapezoid](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/TrapezoidalMap.java) |
| Search Structure( Tree-like DAG ) | [public class SearchStructure](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/SearchStructure.java) |
