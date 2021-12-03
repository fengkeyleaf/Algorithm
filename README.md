# Introduction to Algorithm Repository

IMPORTANT: All updated code is in the Java folder, others are out of date, but I leave them as they are for now. Maybe remove in the future.

 I plan to update this Repository as follows:

1. ~~Add two Convex Hull algorithms: Brute force and Graham's Scan, as well as visualization~~. 
2. ~~Add visualization to Bentley Ottmann's algrithom, that is, Geometric Intersection~~.
3. ~~Reconstruct the visualization program in the triangluation program, make it aligned to the one taking advantage of normalization in graphics, which is the one other programs use as well~~. 
4. And there is also a web-based teaching program for the triangulation algorithm, which is more interactive than Java implementation.
5. ~~Add Point Location ( Trapezoid Map and Search structure ), as well as visualization~~.
6. Four Animation algorithms, implemented with three.js;
7. DFS in Haskell;

## 1. Algorithm

### 1.1 Computational Geometry

#### 1.1.1 Numerical  Tests

| Description      | Entry method                                                 |
| :--------------- | ------------------------------------------------------------ |
| toLeft test      | [boolean toLeft( Vector base1, Vector base2, Vector point )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/tools/Triangles.java#L163) |
| in inCircle test | [double inCircle( Vector a, Vector b, Vector c, Vector p )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/tools/Circles.java#L42) |

#### 1.1.2 Convex Hull

| Description                         | Entry method\File                                            |
| ----------------------------------- | ------------------------------------------------------------ |
| Graham's Scan                       | [List\<Vector> grahamScan( List\<Vector> points )](https://github.com/fengkeyleaf/Algorithm/blob/43b0bc5aadfaddb07352e3c021bd626ff57b9bb0/Java/myLibraries/util/geometry/tools/ConvexHull.java#L114) |
| Brute force                         | [List\<Vector> slowConvexHull( List\<Vector> points )](https://github.com/fengkeyleaf/Algorithm/blob/43b0bc5aadfaddb07352e3c021bd626ff57b9bb0/Java/myLibraries/util/geometry/tools/ConvexHull.java#L42) |
| Program ( including visualization ) | [Programming Assignment 1 - Convex Hull](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/CSCI716/assign_1/AssignmentOne.java) |

#### 1.1.3 Geometric Intersection

| Description                                                  | Entry method\File                                            |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| Line and line                                                | [Vector lineIntersect( Line line1, Line line2 )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/tools/GeometricIntersection.java#L179) |
| Line and Circle                                              | [Line lineCircleIntersect( Line line, Circle cycle )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/tools/GeometricIntersection.java#L67) |
| Bentley Ottmann's algrithom( Intersection Of segment, ray, line and Circle ) | [List\<EventPoint2D> findIntersection( List\<IntersectionShape> shapes )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/tools/GeometricIntersection.java#L479) |
| Program ( including visualization )                          | [CG2017 PA1-2 Crossroad](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/PA_1/problem_2/Main.java) |

#### 1.1.4 Triangulation

| Description                         | Entry method\File                                            |
| :---------------------------------- | ------------------------------------------------------------ |
| Partionting monotone polygons       | [List\<Face> makeMonotone( List\<Vertex> vertices )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/tools/MonotonePolygons.java#L288) |
| Triangulation                       | [List\<Face> preprocessMonotonePolygon( List\<Face> monotonePolygons )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/tools/MonotonePolygons.java#L495) |
| BFS in a dual graph                 | [void BFS( int sizeOfGraph, DualVertex start, DualVertex end )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/graph/tools/SingleShortestPath.java#L241) |
| Funnel algorithm                    | [List\<Vector> Funnel( DualVertex startTriangle, Vector startPoint, Vector endPoint )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/graph/tools/SingleShortestPath.java#L145) |
| Program ( including visualization ) | [CG2017 PA2-1 Shortest Path in The Room](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/PA_2/problem_1/Main.java) |

#### 1.1.5 Point Location

| Description                                | Entry method\File                                            |
| :----------------------------------------- | ------------------------------------------------------------ |
| Build trapezoidal Map and search structure | [SearchStructure trapezoidalMap( List\<Line> lines, SearchVertex box )](https://github.com/fengkeyleaf/Algorithm/blob/43b0bc5aadfaddb07352e3c021bd626ff57b9bb0/Java/myLibraries/util/geometry/tools/PointLocation.java#L68) |
| Point Locatoin                             | [public SearchVertex get( Line line )](https://github.com/fengkeyleaf/Algorithm/blob/43b0bc5aadfaddb07352e3c021bd626ff57b9bb0/Java/myLibraries/util/graph/SearchStructure.java#L167) |
| Program ( including visualization )        | [Programming Assignment 3 - Trapezoidal Map and Planar Point Location](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/CSCI716/assign_3/AssignmentThree.java) |

#### 1.2 POJ

| Problem                     | Description                             | Entry File                                                   |
| --------------------------- | :-------------------------------------- | ------------------------------------------------------------ |
| Subsequence(ID 3061)        | Two approaches, binary search and ruler | [Subsequence.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/coding/POJ/ID_3061/Subsequence.java) |
| Face The Right Way(ID 3276) | One approach, switch                    | [FaceTheRightWay.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/coding/POJ/ID_3276/FaceTheRightWay.java) |

## 2. Data Structure

### 2.1 Tree

| Description                                                  | Entry File                                                   |
| :----------------------------------------------------------- | ------------------------------------------------------------ |
| Binary Search Tree ( put(), deleteMin(), deleteMax(), delete(), min(), max(), etc.) | [BinarySearchTree.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/tree/BinarySearchTree.java) |
| Red Black Tree ( put(), deleteMin(), deleteMax(), delete(), min(), max(), etc. ) | [RedBlackTree.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/tree/RedBlackTree.java) |
| Segment Tree ( Range maximum and minimum Query )             | [SegmentTree.java](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/tree/SegmentTree.java) |

### 2.2 Computational Geometry

#### 2.2.1 DCEL

| Description                                                  | Entry File/Package                                           |
| :----------------------------------------------------------- | :----------------------------------------------------------- |
| Doubly-connected edge list                                   | [DCEL](https://github.com/fengkeyleaf/Algorithm/tree/main/Java/myLibraries/util/geometry/DCEL) |
| Get all incident edges of the vertex                         | [List\<HalfEdge> allIncidentEdges( Vertex vertex )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/DCEL/DCEL.java#L231) |
| Walk around all halfEdges, starting at face and get visited vertices | [List\<Vertex> walkAroundVertex( Face face )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/DCEL/DCEL.java#L222) |
| Find the first ClockWise Edge with two vertices destination and origin | [HalfEdge firstClockWiseEdge( Vertex destination, Vertex origin )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/DCEL/DCEL.java#L52) |
| Find the first CounterClockWise Edge with two vertices destination and origin | [HalfEdge firstCounterClockWiseEdge( Vertex origin, Vertex destination )](https://github.com/fengkeyleaf/Algorithm/blob/main/Java/myLibraries/util/geometry/DCEL/DCEL.java#L85) |

#### 2.2.2 Point Location

| Description                       | Entry File/Package                                           |
| :-------------------------------- | :----------------------------------------------------------- |
| Trapezoidal Map                   | [public class Trapezoid](https://github.com/fengkeyleaf/Algorithm/blob/43b0bc5aadfaddb07352e3c021bd626ff57b9bb0/Java/myLibraries/util/geometry/elements/Trapezoid.java#L54) |
| Search Structure( Tree-like DGA ) | [public class SearchStructure](https://github.com/fengkeyleaf/Algorithm/blob/43b0bc5aadfaddb07352e3c021bd626ff57b9bb0/Java/myLibraries/util/graph/SearchStructure.java#L35) |
