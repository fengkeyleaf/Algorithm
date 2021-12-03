// input
int n, S;
int a[ MAX_N ];

int sum[ MAX_n + 1 ];

// O( nlogn )
void solve() {
	// get sum
	for ( int i = 0; i < n; i++ ) {
		sum[ i + 1 ] = sum[ i ] + a[ i ];
	}

	if ( sum[ n ] < S ) {
		// no solution
		printf( "0\n" );
		return;
	}

	int res = n;
	for ( int s = 0; sum[ s ] + S <= sum[ n ]; s++ ) {
		// bineary search to find t
		int t = lower_bound( sum + s, sum + n, sum[ s ] + S ) - sum;
		res = min( res, t -s );
	}

	printf( "%d\n", res );
}