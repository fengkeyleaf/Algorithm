module Assign7 where

-- @author: Xiaoyu Tongyang
-- @E-mail: xt1643@rit.edu
-- @Student's ID: 796000352

import MyLibraries.Graph
import MyLibraries.DFS

-- Assign 7 mainly involves three files: Assign7.hs, Graph.hs and DFS.hs,
-- where Assign7.hs is the entry file for the later two.

-- Just load this file and then you can get access 
-- to funtions in both files.

-- Main functions for Graph and DFS are:
-- 1. Graph:
-- Build a graph from a list of edges: buildG

-- 2. DFS: 
-- A depth-first search of a graph: dfs or dff
-- Depth-first search numbering: preOrd or tabulate (preArr)
-- Finding reachable vertices: reachable
-- Test for the existenc of a path between two vertices: path