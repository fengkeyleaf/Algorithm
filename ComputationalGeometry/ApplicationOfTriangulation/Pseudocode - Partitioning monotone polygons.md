# 伪代码：单调多边形拆分

```Java
Algorithm MAKEMONOTONE(P)
Input. A simple polygon P stored in a doubly-connected edge list D.
输入：用双向边链表D表示的简单多边形
Output. A partitioning of P into monotone subpolygons, stored in D.
输出：拆分后的多个单调多边形，用D表示

1. Construct a priority queue Q on the vertices of P, using their y-coordinates as priority.
   If two points have the same y-coordinate, the one with smaller x-coordinate has higher
   priority.
   创建一个优先列队Q，以Y值大的为更高优先级，把所有顶点存储到Q中。如果两个顶点的Y相同，则X越小的优先级越高。
2. Initialize an empty binary search tree T.
   初始化一个空的二叉搜索树T。
3. while Q is not empty
   只要Q不为空：
4.	 do Remove the vertex vi with the highest priority from Q.
      执行：从Q中移除优先级最高的顶点vi。
5.		 Call the appropriate procedure to handle the vertex, depending on its type
         根据顶点的类型，调用相应的处理函数对该顶点进行处理。
```



```java
HANDLESTARTVERTEX(vi)
 
1. Insert ei in T and set helper(ei) to vi.
   将边ei插入到T中，并设置ei的helper为vi。
```



```java
HANDLEENDVERTEX(vi)
1. if helper(ei-1) is a merge vertex
   如果边ei-1的helper是merge vertex
2. 	  then Insert the diagonal connecting vi to helper(ei-1) in D.
      执行：在D插入连接vi和helper(ei-1)的内对角线
3. Delete ei-1 from T.
   从T中删除边ei-1
```



```java
HANDLESPLITVERTEX(vi)
1. Search in T to find the edge e j directly left of vi.
   在T搜索位于vi左边的第一条边ej
2. Insert the diagonal connecting vi to helper(ej) in D.
   在D插入连接vi和helper(ej)的内对角线
3. helper(ej)<-vi
   设置ej的helper为vi
4. Insert ei in T and set helper(ei) to vi.
   将边ei插入到T中，并设置ei的helper为vi。
```



```java
HANDLEMERGEVERTEX(vi)
// similar to HANDLEENDVERTEX()
1. if helper(ei-1) is a merge vertex
   如果边ei-1的helper是merge vertex
2. 	  then Insert the diagonal connecting vi to helper(ei-1) in D.
      执行：在D插入连接vi和helper(ei-1)的内对角线
3. Delete ei-1 from T.
   从T中删除边ei-1
// similar to HANDLESPLITVERTEX()
4. Search in T to find the edge ej directly left of vi.
   在T搜索位于vi左边的第一条边ej
5. if helper(ej) is a merge vertex
   如果边ej的helper是merge vertex
6. 	  then Insert the diagonal connecting vi to helper(e j) in D.
      在D插入连接vi和helper(ej)的内对角线
7. helper(ej)<-vi
   设置ej的helper为vi
```



```java
HANDLEREGULARVERTEX(vi)
   // left regular vertex
   // similar to HANDLEENDVERTEX()
1. if the interior of P lies to the right of vi
   如果多边形的内部落在vi的右侧
2. 	  then if helper(ei-1) is a merge vertex
   	  执行：如果边ei-1的helper是merge vertex
3. 		  then Insert the diagonal connecting vi to helper(ei-1) in D.
    	  执行：在D插入连接vi和helper(ei-1)的内对角线
4. Delete ei-1 from T.
   从T中删除边ei-1
   // similar to HANDLESTARTVERTEX()
5. Insert ei in T and set helper(ei) to vi.
   将边ei插入到T中，并设置ei的helper为vi。
   // right regular vertex
   // similar to the last part of HANDLEMVERTEX()
6. else Search in T to find the edge e j directly left of vi.
   反之在T搜索位于vi左边的第一条边ej
7. 	  if helper(ej) is a merge vertex
	  如果边ej的helper是merge vertex
8. 		  then Insert the diagonal connecting vi to helper(e-j) in D.
    	  在D插入连接vi和helper(ej)的内对角线
9. helper(ej)<-vi
  设置ej的helper为vi
```

> @author: Xiaoyu Tongyang, or call me sora for short

