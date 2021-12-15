-- module MyLibraries.MyMath (
--     multiple, multiple',
--     hasDuplicate,
--     isInRange,
--     factD, factR
-- ) where

module MyLibraries.MyMath where

-- @author: Xiaoy Tongyang
-- @E-mail: xt1643@rit.edu
-- @Student's ID: 796000352

import Data.List

{-
 - Math tool box
 -}

{-
 - Compute the factorial function n!
 - 
 - visibility: public
 -}

fact :: Int -> Integer
fact = product . ( ( flip take ) [ 1.. ] )

factD :: Int -> Double
factD = product . ( ( flip take ) [ 1.0.. ] )

factR :: Int -> Rational
factR = product . ( ( flip take ) [ 1.. ] )

{-
 - num % moder == 0
 - 
 - visibility: public
 -}

multiple :: Int -> Int -> Bool
num `multiple` moder = num `mod` moder == 0

multiple' :: Integer -> Integer -> Bool
num `multiple'` moder = num `mod` moder == 0

{-
 - given list has duplicates?
 - 
 - visibility: public
 -}

hasDuplicate :: Eq a => [ a ] -> Bool
hasDuplicate [] = False
hasDuplicate lst = ( length lst ) /= length ( nub lst )

{-
 - min <= i <= max
 - 
 - visibility: public
 -}

isInRange :: Int -> Int -> Int -> Bool
isInRange i min max = min <= i && i <= max