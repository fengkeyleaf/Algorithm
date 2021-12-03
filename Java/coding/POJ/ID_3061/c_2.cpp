// O(n)
void solve() {
	int res = n + 1;
	int s = 0, t = 0, sum = 0;
	for (;;) {
		while ( t < n && sum < S )
			sum += a[ t++ ];
		
		if ( sum < S ) break;
		res = min( res, t - s );
		sum -= a[ s++ ];
	}
	
	// no soluton
	if ( res > n )
		res = 0;
	
	printf( "%d\n", res );
}