-- module MyLibraries.Matrix (
--     Matrix(..),
--     get,
--     set,
--     getRow,
--     getCol,
--     getRowRange,
--     getRange
-- ) where

module MyLibraries.Matrix where

-- @author: Xiaoy Tongyang
-- @E-mail: xt1643@rit.edu
-- @Student's ID: 796000352

{-
 - data structure of Matrix and its methods    
 -}

-----------------------------------------------------------------------------
-- data declaration
-----------------------------------------------------------------------------

-- visibility: public
type Matrix a = [ [ a ] ]

-----------------------------------------------------------------------------
-- methods declaration
-----------------------------------------------------------------------------

{-
 - return matrix[ i ][ j ]
 - 
 - visibility: public
 -}

get :: Matrix a -> Int -> Int -> a
get lst i j = ( lst !! i ) !! j

set1 :: [ a ] -> a -> Int -> Int -> [ a ]
set1 [] _ _ _ = []
set1 ( x : xs ) e j c = 
    if j == c then e : xs 
    else ( x : set1 xs e j ( c + 1 ) )

{-
 - matrix[ i ][ j ] = e, return this matrix
 - 
 - visibility: public
 -}

set :: Matrix a -> a -> Int -> Int -> Matrix a
set [] _ _ _ = error "Empty Matrix"
set lst e i j = 
    let set' ( x : xs ) e i j c =
            if i == c then ( set1 x e j 0 ) : xs 
            else x : set' xs e i j ( c + 1 )
    in set' lst e i j 0

{-
 - get i-th row of this matrix
 - 
 - visibility: public
 -}

getRow :: Matrix a -> Int -> [ a ]
getRow [] _ = error "Empty Matrix"
getRow m i= m !! i

{-
 -  get i-th column of this matrix
 - 
 - visibility: public
 -}

getCol :: Matrix a -> Int -> [ a ]
getCol [] _ = []
getCol ( x : xs ) i = ( x !! i ) : getCol xs i 

{-
 - get from iMin-th to iMax-th columns of this matrix
 - 
 - visibility: public
 -}

getRowRange :: Matrix a -> Int -> Int -> Matrix a
getRowRange [] _ _ = []
getRowRange m iMin iMax = 
    let getRowRange' m' iMax' iMin' = 
            if iMin' <= iMax' then ( getRow m' iMin' ) : getRowRange' m' iMax' ( iMin' + 1 )
            else []
    in getRowRange' m iMax iMin

{-
 - get 
 - [ ( iMin, jMin )  ...
 -   ... ... ... ... ...
 -   ... ... ... ... ...
 -   ... ... ... ... ...
 -   ... ( iMax, jMax ) ]
 - of this matrix
 - 
 - visibility: public
 -}

getRange :: Matrix a -> Int -> Int -> Int -> Int -> [ a ]
getRange [] _ _ _ _ = []
getRange m iMin jMin iMax jMax = [ col !! j | col <- getRowRange m iMin iMax, j <- [ jMin..jMax ] ]
