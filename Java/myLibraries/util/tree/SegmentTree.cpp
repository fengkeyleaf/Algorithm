const int MAX_N = 1 << 17;

// 存储线段树的全局数组
int n, dat[ 2 * MAX_N - 1 ];

// 初始化
void init( int n_ ) {
	// 为了简单起见，把元素个数扩大到2的幂
	n = 1;
	while ( n < n_ ) n *= 2;
	
	// 把所有的值都设置为INT_MAX
	for ( int i = 0; i < 2 * n - 1; i++ )
		dat[ i ] = INT_MAX;
}

/**
 * ( k - 1 ) / 2 -> 父亲的下标
 * k * 2 + 1 -> 左孩子的下标
 * k * 2 + 2 -> 右孩子的下标
 */
 
// 把第k个值(0 - indexed)更新为a
void update( int k, int a ) {
	// 叶子节点
	k += n - 1;
	dat[ k ] = a;
	
	// 向上更新
	while ( k > 0 ) {
		k = ( k - 1 ) / 2;
		dat[ k ] = min( dat[ k * 2 + 1 ], dat[ k * 2 + 2 ] );
	}
}

/**
 * 求[a,b]的最小值
 * 后面的参数是为了计算其方便而传入的。
 * k是节点的编号，l，r表示这个节点对应的是[l,r)区间。
 * 在外部调用时，用query( a, b, 0, 0, n )
 */
 
int query( int a, int b, int k, int l, int r ) {
	// 如果[a,b]和[l,r)不相交，则返回INT_MAX
	if ( r <= a || b <= l ) return INT_MAX;
	
	// 如果[a,b]完全包含[l,r)，则返回当前节点的值
	if ( a <= l && r <= b ) return dat[ k ];
	else {
		// 否则返回两个儿子中值的较小者
		int vl = query( a, b, k * 2 + 1, l, ( l + r ) / 2 );
		int vr = query( a, b, k * 2 + 2, ( l + r ) / 2, r );
		return min( vl, vr );
	}
}