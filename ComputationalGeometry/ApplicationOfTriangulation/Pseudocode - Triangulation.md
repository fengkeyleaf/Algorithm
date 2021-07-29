# 伪代码：三角拆分

```java
Algorithm TRIANGULATEMONOTONEPOLYGON(P)
Input. A strictly y-monotone polygon P stored in a doubly-connected edge ist D.
输入：严格相对Y轴得单调多边形P，用DCEL表示
Output. A triangulation of P stored in the doubly-connected edge list D.
输出：三角拆分后得若干个三角形，用DCEL存储
1. Merge the vertices on the left chain and the vertices on the right chain of P into one sequence, sorted on decreasing y-coordinate. If two vertices have the same y-coordinate, then the leftmost one comes first. Let u1, . . . ,un denote the sorted sequence.
将左链和右链上的顶点存储在一个数组中，并序排序。如果两个顶点的Y值相同，则左边（X值小的靠前）。我们用u_1 ...., u_表示这个排序的顶点数组元素。
2. Initialize an empty stack S, and push u1 and u2 onto it.
初始化一个空stack S，把u_1和u_2入栈。
3. for j←3 to n−1
   遍历u_到u_n-1：
4. 	  do if uj and the vertex on top of S are on different chains
      执行：如果u_j和栈顶的顶点属于不同的链：
5. 	  	  then Pop all vertices from S.
    	  执行：弹出S中所有的顶点
6. 	  	  	  Insert into D a diagonal from uj to each popped vertex, except the last one.
    		  往D中添加连接u_j和每个弹出顶点的内对角线，除了栈底的那个顶点
7. 	  	  	  Push uj−1 and uj onto S.
    		  将u_j-1和u_j入栈
8. 	  	  else Pop one vertex from S.
    	  否则从S中弹出一个顶点
9. 	  	  	  Pop the other vertices from S as long as the diagonals from uj to them are inside P. Insert thesediagonals into D. Push
	  	  	  the last vertex that has been popped back onto S.
    		  持续从S中弹出顶点，直到u_j和弹出顶点的连线不为内对角线则停止弹出。把这些内对角线放入D中，把最后弹出的顶点放回S中
10. 	  	  Push uj onto S.
    		  将u_j入栈
11. Add diagonals from un to all stack vertices except the first and the last one.
    连接u_n和所有S中的顶点，形成内对角线，但不包括栈顶和栈底的顶点。
```

> @author: Xiaoyu Tongyang, or call me sora for short

