module MyLibraries.Graph where

-- @author: Xiaoyu Tongyang
-- @E-mail: xt1643@rit.edu
-- @Student's ID: 796000352

{-
 - data structure of Graph and its methods'
 - 
 - reference resource:
 - https://dl-acm-org.ezproxy.rit.edu/doi/10.1145/199448.199530
 -}

import Data.Array -- inculding Data.Ix

-- https://hackage.haskell.org/package/base-4.13.0.0/docs/Data-Ix.html

-----------------------------------------------------------------------------
-- data declaration
-----------------------------------------------------------------------------

--The type Vertex may be any type belonging to the Haskell index class Ix
-- ( Ix i ) => 
type Vertex i = i

-- Graphs, therefore, may be thought of as a table indexed by vertices.
type Table i a = Array ( Vertex i ) a
type Graph i = Table ( Vertex i ) [ Vertex i ]

-- An edge is a pair of vertices.
type Edge i = ( Vertex i, Vertex i )

-- Because we are using an array-based implementation 
-- we often need to provide a pair of vertices as array bounds. So for convenience we define,
type Bounds i = ( Vertex i, Vertex i )

-- https://hackage.haskell.org/package/array-0.5.4.0/docs/Data-Array.html#t:Array
-- (!) :: Ix i => Array i e -> i -> e infixl 9
-- The value at the given index in an array.

-- indices :: Ix i => Array i e -> [i]
-- The list of indices of an array in ascending order.


-----------------------------------------------------------------------------
-- methods declaration
-----------------------------------------------------------------------------

{-
 - We provide vertices as an alternative for indices, which returns a list of all the vertices in a graph.
 - 
 - visibility: public
 -}

vertices :: ( Ix i ) => Graph i -> [ Vertex i ]
vertices = indices

{-
-- it is convenient to extract a list of edges from the graph
 - 
 - visibility: public
 -}

edges :: ( Ix i ) => Graph i -> [ Edge i ]
edges g = [ ( v, w ) | v <- vertices g, w <- g ! v ]

-- bounds :: Array i e -> (i, i)
-- The bounds with which an array was constructed.

{-
 -To manipulate tables (including graphs) we provide a generic function mapT 
 - which applies its function argument to every table index fentry pair, and builds a new table.
 - 
 - visibility: public
 -}

mapT :: ( Ix i ) => ( Vertex i -> a -> b ) -> Table i a -> Table i b
mapT f t = array ( bounds t ) [ ( v, f v ( t ! v ) ) | v <- indices t ]

{-
 - builds a table detailing the number of edges leaving each vertex.
 - 
 - visibility: public
 -}

outdegree :: ( Ix i ) => Graph i -> Table i Int
outdegree g = mapT numEdges g
    where numEdges v ws = length ws

-- accumArray
-- The accumArray function deals with repeated indices in the association list using an accumulating function which combines the values of associations with the same index.

-- hist :: (Ix a, Num b) => (a,a) -> [a] -> Array a b
-- hist bnds is = accumArray (+) 0 bnds [(i, 1) | i<-is, inRange bnds i]

-- flip :: (a -> b -> c) -> b -> a -> c
-- flip f takes its (first) two arguments in the reverse order of f.

{-
 - To build up a graph from a list of edges
 - 
 - visibility: public
 -}

buildG :: ( Ix i ) => Bounds i -> [ Edge i ] -> Graph i
buildG bnds es = accumArray ( flip ( : ) ) [] bnds es 

{-
 - Combining the functions edges and buildG gives us a
 - way to reverse all the edges in a graph giving the transpose
 - of the graph:
 - 
 - visibility: public
 -}

transpose :: ( Ix i ) => Graph i -> Graph i
transpose g = buildG ( bounds g ) ( reverseE g )

{-
 - We extract theedges from the original graph, reverse their
 - direction, and rebuild agraph with the new edges.
 - 
 - visibility: public
 -}

reverseE :: ( Ix i ) =>  Graph i -> [ Edge i ]
reverseE g = [ ( w, v ) | ( v, w ) <- edges g ]

{-
 - Now by using trensposeG
diately define an indegree table for vertices:
 - 
 - visibility: public
 -}

indegree :: ( Ix i ) => Graph i -> Table i Int
indegree g = outdegree ( transpose g )
