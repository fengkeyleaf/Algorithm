module MyLibraries.DFS(
    Tree( .. ),
    Forest( .. ),
    Set( .. ),
    dfs,
    dff,
    preOrd,
    tabulate,
    preArr,
    reachable,
    path
) where

-- @author: Xiaoyu Tongyang
-- @E-mail: xt1643@rit.edu
-- @Student's ID: 796000352

{-
 - data structure of depth-first forest and its methods
 - 
 - reference resource:
 - https://dl-acm-org.ezproxy.rit.edu/doi/10.1145/199448.199530
 -}

import MyLibraries.Graph

import Data.Array -- inculding Data.Ix
import Control.Monad.ST
import Data.Array.ST

-----------------------------------------------------------------------------
-- data declaration
-----------------------------------------------------------------------------

-- A forest is a list of trees, and a tree is a node containing some value, together with a forest of sub-trees.
data Tree a = Node a ( Forest a ) deriving Show
type Forest a = [ Tree a ]

-- https://hackage.haskell.org/package/basic-prelude-0.7.0/docs/CorePrelude.html#t:Monoid
-- http://learnyouahaskell.com/a-fistful-of-monads#getting-our-feet-wet-with-maybe
-- https://hackage.haskell.org/package/base-4.15.0.0/docs/Control-Monad.html
-- http://learnyouahaskell.com/for-a-few-monads-more#state
-- https://stackoverflow.com/questions/42133257/data-constructor-error-when-implementing-state-monad
-- https://hackage.haskell.org/package/mtl-2.2.2/docs/Control-Monad-State-Lazy.html#g:4

-- ( Ix i ) => 
type Set s i = STUArray s ( Vertex i ) Bool

-- question to answer after finishing assign_7
-- https://stackoverflow.com/questions/5857744/parse-error-in-pattern-in-haskell/5857778#5857778

-----------------------------------------------------------------------------
-- methods declarations
-----------------------------------------------------------------------------

-----------------------------------------------------------------------------
-- constructing methods declarations
-----------------------------------------------------------------------------

-- https://hackage.haskell.org/package/array-0.5.4.0/docs/Data-Array-MArray.html#v:newArray
-- https://hackage.haskell.org/package/base-4.15.0.0/docs/Control-Monad-ST.html
-- https://hackage.haskell.org/package/array-0.5.4.0/docs/Data-Array-ST.html
-- https://hackage.haskell.org/package/array-0.5.4.0/docs/Data-Array-ST.html#t:STArray

{-
- array to mark visiting status for each vertex
-
- visibility: private
-}

mkEmpty :: ( Ix i ) => Bounds i -> ST s ( Set s i )
mkEmpty bnds = newArray bnds False

{-
- check the visiting status of v
-
- visibility: private
-}

contains :: ( Ix i ) => Set s i -> Vertex i -> ST s Bool
contains m v = readArray m v

{-
- mark v as visited
-
- visibility: private
-}

include :: ( Ix i ) => Set s i -> Vertex i -> ST s ()
include m v = writeArray m v True

-- https://www.dcc.fc.up.pt/~pbv/aulas/tapf/handouts/stmonad.html

{-
 - The prune function begins by introducing a fresh state thread,
 - then generates an empty set within that thread and calls
 - chop. The final result ofprune is the value generatedby
 - chop, the final state being discarded.
 -
 - visibility: private
 -}

prune :: ( Ix i ) => Bounds i -> Forest ( Vertex i ) -> Forest ( Vertex i )
prune bnds ts = runST $ do
    marks <-  mkEmpty bnds
    chop marks ts

{-
 - When chopping a list of trees, the root of the first is examined.
 - If it has occurred before, the whole tree is discarded.
 - If not, thevertex isaddedto the set represented bym, and
 - two further calls tochop aremade in sequence.
 - The first, namely, chop m ts, prunes the forest of descendants
 - of v, adding all these to the set of marked vertices.
 - Once this incomplete, thepruned subforest is named
 - as, andtheremainder of the original forest is chopped. The
 - 349 result of this is, in turn, named bs, and the resulting forest
 - is constructed from the two.
 -
 - visibility: private
 -}

chop :: ( Ix i ) => Set s i -> Forest ( Vertex i ) -> ST s ( Forest ( Vertex i ) )
chop m [] = return []
chop m ( ( Node v ts ) : us ) = do 
    visited <- contains m v
    if visited then
        chop m us
    else do
        include m v
        as <- chop m ts
        bs <- chop m us
        return ( ( Node v as ) : bs )

{-
 - given a graph g and a vertex v builds a tree rooted at v 
 - containing all the vertices in g reachable from v.
 -
 - visibility: private
 -}

generate :: ( Ix i ) => Graph i -> Vertex i -> Tree ( Vertex i )
generate g v = Node v ( map ( generate g ) ( g ! v ) )

{-
 - A depth-first search of a graph takes a graph and an initial
 - ordering of vertices. Allgraph vertices inthe initial ordering
 - will be in the returned forest.
 -
 - visibility: public
 -}

dfs :: ( Ix i ) => Graph i -> [ Vertex i ] -> Forest ( Vertex i )
dfs g vs = prune ( bounds g ) ( map ( generate g ) vs )


{-
 - Sometimes the initial ordering of vertices is not important.
 -
 - visibility: public
 -}

dff :: ( Ix i ) => Graph i -> Forest ( Vertex i )
dff g = dfs g ( vertices g )


-----------------------------------------------------------------------------
-- DFS related algorithms declarations
-----------------------------------------------------------------------------

--  Algorithm 1. Depth-first search numbering

{-
 - We can express depth-first ordering of a graph g most
 - simply by flattening the depth-first forest in preorder. Preorder
 - on trees and forests places ancestors before descendants
 - and left subtrees before right subtrees4:
 -
 - visibility: private
 -}

preorder :: Tree a -> [ a ]
preorder ( Node a ts ) = [ a ] ++ preorderF ts

{-
 - entry function to call preorder
 -
 - visibility: private
 -}

preorderF :: Forest a -> [ a ]
preorderF ts = concat ( map preorder ts )

{-
 - obtaining a list of vertices in depth-first order
 -
 - visibility: public
 -}

preOrd :: ( Ix i ) => Graph i -> [ Vertex i ]
preOrd g = preorderF ( dff g )

{-
 - However, it is often convenient to translate such an ordered
 - list into actual numbers. This zips the vertices together with the positive integers
 - 1, 2, 3,..., and (in linear time) builds an array of these
 - numbers, indexed by the vertices.
 -
 - visibility: public
 -}

tabulate :: ( Ix i ) => Bounds i -> [ Vertex i ] -> Table ( Vertex i ) Integer 
tabulate bnds vs = array bnds ( zip vs [ 1.. ] )

{-
 - (it turns out to be convenient for later algorithms if such
 - functions take the depth-first forest as an argument, rather
 - than construct the forest themselves.)
 -
 - visibility: public
 -}

preArr :: ( Ix i ) => Bounds i -> Forest ( Vertex i ) -> Table ( Vertex i ) Integer
preArr bnds ts = tabulate bnds ( preorderF ts )

-- Algorithm 6. Finding reachable vertices

{-
 - Finding all the vertices that are reachable from a single vertex
 - v demonstrates that the dfs doesn’t have to take all the
 - vertices as its second argument. Commencing a search at v
 - will construct a tree containing all of v’s reachable vertices.
 - We then flatten this with preorder to produce the desired
 - list.
 -
 - visibility: public
 -}

reachable :: ( Ix i ) => Graph i -> Vertex i -> [ Vertex i ]
reachable g v = preorderF ( dfs g [ v ] )

{-
 - One application of this algorithm is to test for the existence
 - of a path between two vertices:
 - 
 - The elem test is lazy: it returns True as soon as a match
 - is found. Thus the result of reachable is demanded lazily,
 - and so only produced lazily. As soon as the required vertex
 - is found the generation of the DFS forest ceaaes. Thus
 - dfs implements a true search and not merely a complete
 - traversal.
 - 
 - visibility: public
 -}

path :: ( Ix i ) => Graph i -> Vertex i -> Vertex i -> Bool
path g v w = w `elem` ( reachable g v )