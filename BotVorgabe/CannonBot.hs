--- module (NICHT AENDERN!)
module CannonBot where
--- imports (NICHT AENDERN!)
import Data.Char
import Util


--- external signatures (NICHT AENDERN!)
getMove :: String -> String
listMoves :: String -> String

--- YOUR IMPLEMENTATION STARTS HERE ---
-- INput 1111W11111/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/7B2 w
--converted "1111W11111/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w/1111111111/1111111111/b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/1111111B11" 0 15
--converted2 "1111W111111w1w1w1w1w1w1w1w1w1w1w1w1w1w1w11111111111111111111b1b1b1b1b1b1b1b1b1b1b1b1b1b1b11111111B11" 0 15
--converted2 for Testing  "1111W111111w1w1w1w1w1w1w1w1w1w1w1w1w1w1w11111111111111111111b1b1b1b1b1b1b1b1b1b1b1b1b1b1b11111111B11" 'w' 0
--3 111
zeilen =  ['0'..'9'] 
spalten = ['a'..'j']

data Koordinate = Stelle Int Int deriving (Show)

--UTILS
--Fehlervalues 'z' = Char not in Board  // 'k' = fehler bei Player Eingabe , also es gab kein w oder b 
--Stelle (-1) (-1)  = index nicht gultig
-- index -1  = koordinate Nicht im Board
-- 

--conveert Board ohne / und nur mit 1 , schneidet NICHT die letze Char ab 
convertBoard2 :: String -> String
convertBoard2 [] = []
convertBoard2 (x:xs)
   | x == '/' && head(xs) == '/' = "1111111111" ++ convertBoard2 xs
   | x == '/'  =  convertBoard2 xs 
   | isDigit x && x /= '1'  = "1" ++ convertBoard2 (intToDigit((digitToInt x)-1):xs ) 
   | otherwise = x:[] ++ convertBoard2 xs   
   
   
convertBoard1 :: String -> Int -> String
convertBoard1 [] _ = []
convertBoard1 (x:xs) index
  | index == 0 && x == '/' = "1111111111" ++ convertBoard1 ('/':xs) (index+1)
  | x == '/' && head(xs) == '/' =  "1111111111" ++ convertBoard1 xs (index+1)
  | x == '/' && (xs) == " w" =   "1111111111 w" 
  | x == '/' && (xs) == " b" =   "1111111111 b"
  | x == '/'  =  convertBoard1 xs (index+1)
  | isDigit x && x /= '1'  = "1" ++ convertBoard1 (intToDigit((digitToInt x)-1):xs ) (index+1)
  | otherwise = x:[] ++ convertBoard1 xs (index+1)
  


   --input index , output = Zeile
getZeile :: Int -> Int
getZeile a =  abs ((quot a 10) - 9)
--abs ((getZeile a) - 9)
--input index , output = spalte
getSpalte :: Int -> Int
getSpalte a = a `mod` 10


-- input index , output Koordinate (also Stelle a b)
getKoordinate :: Int -> Koordinate
getKoordinate a 
  | a > 99 || a < 0 = Stelle (-1) (-1)
  | otherwise = Stelle (getSpalte a) (getZeile a)

-- input Koordinate also Stelle a b , output index
getIndex :: Koordinate -> Int
getIndex (Stelle a b) 
  | a > 9 || a < 0 || b <0 || b > 9 = (-1)
  |otherwise = abs((b -9)*10) + (a)


--String = Board (converted) //Int1 = counter //Int2 = Index // Char = 1 oder w oder W oder B oder b 
getCharFromBoard :: String -> Int -> Int -> Char
getCharFromBoard [] _ _ = 'z'
getCharFromBoard (x:xs) counter index
  | index > 99 || index < 0 = 'z'
  | counter == index = x
  | otherwise = getCharFromBoard xs (counter+1) index
  
 --input koordinate also (Stelle a b ) , output String  
convertkoordinateToString :: Koordinate -> String
convertkoordinateToString (Stelle a b) = (spalten !! a):(zeilen !! b):[]


getGegner :: Char -> Char
getGegner player
  | player == 'w' = 'b'
  | player == 'b' = 'w'
  | otherwise = 'k'
  
getGegnerischeStadt :: Char -> Char
getGegnerischeStadt player
  | player == 'w' = 'B'
  | player == 'b' = 'W'
  | otherwise = 'K'
 

--input board , player , counter , Output alle koordinaten von Soldaten
getAllKordinatenVonPlayers :: String -> Char -> Int -> [Koordinate]
getAllKordinatenVonPlayers [] _ _ = []
getAllKordinatenVonPlayers (x:xs) player counter
  | x == player = getKoordinate counter : getAllKordinatenVonPlayers xs player (counter+1)
  | otherwise = getAllKordinatenVonPlayers xs player (counter+1)

  
 -- aca cojo una por una de las koordinaten de los player, also el output de arriba uno por uno, por cada uno todas las gultige, cuando lsa tenga las paso a [String] con la funcion de abajo y con la de mas abajo a String y tengo listMoves 
  
--selectGueltigenMoves :: Koordinate -> 
  
  
convertAllGutltigenMovesInStrings :: Koordinate -> [Koordinate] -> [String]
convertAllGutltigenMovesInStrings _ [] = []
convertAllGutltigenMovesInStrings (Stelle a b) (x:xs) =  ((convertkoordinateToString (Stelle a b)) ++ "-" ++ (convertkoordinateToString (x))) : convertAllGutltigenMovesInStrings (Stelle a b) xs
  




--y ya despues concateno la convert All con esta que esta abajo 



convertListToString :: [String] -> String
convertListToString [] = []
convertListToString (a:b) =  a ++ "," ++convertListToString (b)   

--testear esta mas que todo
-- input Kordinate von Soldat , board ,counter porque el player solo se puede mover una vez pal frente  player(lo puedo inferir en la primera ronda con un let, output Menge von Mögliche ZielKoordinaten  //TODO aqui los 16 casos probando si la ziel esta frei si la kopf todo eso
getAllGultigenZielMovesFromSoldat :: Koordinate -> String -> Int ->[Koordinate]
getAllGultigenZielMovesFromSoldat _ _ 41 = []
getAllGultigenZielMovesFromSoldat (Stelle a b) board counter = let player = getCharFromBoard board 0 (getIndex (Stelle a b))
                                                                   gegner = getGegner player
                                                                   cityGegner = getGegnerischeStadt player
   in case () of
   _ | player == 'w' &&  (b-1) >= 0 && counter == 0 && (getCharFromBoard board 0 (getIndex (Stelle a (b-1)))) /= 'w'  -> (Stelle a (b-1)): getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1) --move White Nach Vorne wenn Frei ist oder ein gegner da ist und liegt in die Tafel
     | player == 'w' &&  (b-1) >= 0 && counter == 1 &&(a-1) >= 0 && (getCharFromBoard board 0 (getIndex (Stelle (a-1) (b-1)))) /= 'w'  -> (Stelle (a-1) (b-1)): getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1) --move White Diagonal links nach Unten wenn Frei ist oder ein gegner da ist und liegt in die Tafel
     | player == 'w' &&  (b-1) >= 0 && counter == 2 &&(a+1) <= 9 && (getCharFromBoard board 0 (getIndex (Stelle (a+1) (b-1)))) /= 'w'  -> (Stelle (a+1) (b-1)): getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1) --move White Diagonal rechts nach Unten wenn Frei ist oder ein gegner da ist und liegt in die Tafel
     | player == 'w' &&                counter == 3 &&(a+1) <= 9 && (getCharFromBoard board 0 (getIndex (Stelle (a+1) (b)))) == 'b' || (getCharFromBoard board 0 (getIndex (Stelle (a+1) (b)))) == 'B'  -> (Stelle (a+1) (b)): getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1) -- Attack white rechts wenn Feld in Tafel ist und da ein gegner steht
     | player == 'w' &&                counter == 4 &&(a-1) >= 0 && (getCharFromBoard board 0 (getIndex (Stelle (a-1) (b)))) == 'b' || (getCharFromBoard board 0 (getIndex (Stelle (a-1) (b)))) == 'B' -> (Stelle (a-1) (b)): getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1) -- Attack white links wenn Feld in Tafel ist und da ein gegner steht
     | player == 'w' && isPlayerBedroht board (Stelle a b)  player  && counter == 5 && (b+2) <= 9 && (getCharFromBoard board 0 (getIndex (Stelle (a) (b+1)))) == '1' && (getCharFromBoard board 0 (getIndex (Stelle (a) (b+2)))) == '1'  -> (Stelle (a) (b+2)): getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1) -- Bedroht gerade und zwischenStelle frei ist
     | player == 'w' && isPlayerBedroht board (Stelle a b)  player  && counter == 6 && (b+2) <= 9 && (a+2) <= 9 && (getCharFromBoard board 0 (getIndex (Stelle (a+1) (b+1)))) == '1' && (getCharFromBoard board 0 (getIndex (Stelle (a+2) (b+2)))) == '1'  -> (Stelle (a+2) (b+2)): getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1) -- Bedroht diagonal rechts und zwischenStelle frei ist
     | player == 'w' && isPlayerBedroht board (Stelle a b)  player  && counter == 7 && (b+2) <= 9 && (a-2) >= 0 && (getCharFromBoard board 0 (getIndex (Stelle (a-1) (b+1)))) == '1' && (getCharFromBoard board 0 (getIndex (Stelle (a-2) (b+2)))) == '1'  -> (Stelle (a-2) (b+2)): getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1) -- Bedroht diagonal links und zwischenStelle frei ist
     | player == 'b' &&  (b+1) <= 9 && counter == 8 && (getCharFromBoard board 0 (getIndex (Stelle a (b+1)))) /= 'b'  -> (Stelle a (b+1)): getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1) --move Black Nach Vorne wenn Frei ist oder ein gegner da ist und liegt in die Tafel
     | player == 'b' &&  (b+1) <= 9 && counter == 9 &&(a-1) >= 0 && (getCharFromBoard board 0 (getIndex (Stelle (a-1) (b+1)))) /= 'b'  -> (Stelle (a-1) (b+1)): getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1) --move Black Diagonal links nach Obe wenn Frei ist oder ein gegner da ist und liegt in die Tafel
     | player == 'b' &&  (b+1) <= 9 && counter == 10 &&(a+1) <= 9 && (getCharFromBoard board 0 (getIndex (Stelle (a+1) (b+1)))) /= 'b'  -> (Stelle (a+1) (b+1)): getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1) --move Black Diagonal rechts nach Oben wenn Frei ist oder ein gegner da ist und liegt in die Tafel
     | player == 'b' &&                counter == 11 &&(a+1) <= 9 && (getCharFromBoard board 0 (getIndex (Stelle (a+1) (b)))) == 'w' || (getCharFromBoard board 0 (getIndex (Stelle (a+1) (b)))) == 'W' -> (Stelle (a+1) (b)): getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1) -- Attack Black rechts wenn Feld in Tafel ist und da ein gegner steht
     | player == 'b' &&                counter == 12 &&(a-1) >= 0 && (getCharFromBoard board 0 (getIndex (Stelle (a-1) (b)))) == 'w' || (getCharFromBoard board 0 (getIndex (Stelle (a-1) (b)))) == 'W'  -> (Stelle (a-1) (b)): getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1) -- Attack Black links wenn Feld in Tafel ist und da ein gegner steht
     | player == 'b' && isPlayerBedroht board (Stelle a b)  player  && counter == 13 && (b-2) >= 0 && (getCharFromBoard board 0 (getIndex (Stelle (a) (b-1)))) == '1' && (getCharFromBoard board 0 (getIndex (Stelle (a) (b-2)))) == '1'  -> (Stelle (a) (b-2)): getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1) -- Bedroht gerade und zwischenStelle frei ist
     | player == 'b' && isPlayerBedroht board (Stelle a b)  player  && counter == 14 && (b-2) >= 0 && (a+2) <= 9 && (getCharFromBoard board 0 (getIndex (Stelle (a+1) (b-1)))) == '1' && (getCharFromBoard board 0 (getIndex (Stelle (a+2) (b-2)))) == '1'  -> (Stelle (a+2) (b-2)): getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1) -- Bedroht diagonal rechts und zwischenStelle frei ist
     | player == 'b' && isPlayerBedroht board (Stelle a b)  player  && counter == 15 && (b-2) >= 0 && (a-2) >= 0 && (getCharFromBoard board 0 (getIndex (Stelle (a-1) (b-1)))) == '1' && (getCharFromBoard board 0 (getIndex (Stelle (a-2) (b-2)))) == '1'  -> (Stelle (a-2) (b-2)): getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1) -- Bedroht diagonal links und zwischenStelle frei ist
     | isValidCannonShotDiagonalLinksNachOben board (Stelle a b) player && counter == 16 && (getCharFromBoard board 0 (getIndex (Stelle (a-4) (b+4))) == gegner || getCharFromBoard board 0 (getIndex (Stelle (a-4) (b+4))) == cityGegner ) -> (Stelle (a-4) (b+4)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonShotDiagonalLinksNachOben board (Stelle a b) player && counter == 17 && (getCharFromBoard board 0 (getIndex (Stelle (a-5) (b+5))) == gegner  || getCharFromBoard board 0 (getIndex (Stelle (a-5) (b+5))) == cityGegner )-> (Stelle (a-5) (b+5)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonShotDiagonalLinksNachUnten board (Stelle a b) player && counter == 18 && (getCharFromBoard board 0 (getIndex (Stelle (a+4) (b-4))) == gegner || getCharFromBoard board 0 (getIndex (Stelle (a+4) (b-4))) == cityGegner )->(Stelle(a+4) (b-4)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonShotDiagonalLinksNachUnten board (Stelle a b) player && counter == 19 && (getCharFromBoard board 0 (getIndex (Stelle (a+5) (b-5))) == gegner || getCharFromBoard board 0 (getIndex (Stelle (a+5) (b-5))) == cityGegner )-> (Stelle (a+5) (b-5)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonShotDiagonalRechtsNachOben board (Stelle a b) player && counter == 20 && (getCharFromBoard board 0 (getIndex (Stelle (a+4) (b+4))) == gegner || getCharFromBoard board 0 (getIndex (Stelle (a+4) (b+4))) == cityGegner )-> (Stelle (a+4) (b+4)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonShotDiagonalRechtsNachOben board (Stelle a b) player && counter == 21 && (getCharFromBoard board 0 (getIndex (Stelle (a+5) (b+5))) == gegner || getCharFromBoard board 0 (getIndex (Stelle (a+5) (b+5))) == cityGegner ) -> (Stelle (a+5) (b+5)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonShotDiagonalRechtsNachUnten board (Stelle a b) player && counter == 22 && (getCharFromBoard board 0 (getIndex (Stelle (a-4) (b-4))) == gegner || getCharFromBoard board 0 (getIndex (Stelle (a-4) (b-4))) == cityGegner ) -> (Stelle (a-4) (b-4)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonShotDiagonalRechtsNachUnten board (Stelle a b) player && counter == 23 && (getCharFromBoard board 0 (getIndex (Stelle (a-5) (b-5))) == gegner || getCharFromBoard board 0 (getIndex (Stelle (a-5) (b-5))) == cityGegner ) -> (Stelle (a-5) (b-5)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonShotGeradeNachOben board (Stelle a b) player && counter == 24 && (getCharFromBoard board 0 (getIndex (Stelle (a) (b+4))) == gegner || getCharFromBoard board 0 (getIndex (Stelle (a) (b+4))) == cityGegner ) -> (Stelle (a) (b+4))  : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonShotGeradeNachOben board (Stelle a b) player && counter == 25 && (getCharFromBoard board 0 (getIndex (Stelle (a) (b+5))) == gegner || getCharFromBoard board 0 (getIndex (Stelle (a) (b+5))) == cityGegner ) -> (Stelle (a) (b+5)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonShotGeradeNachUnten board (Stelle a b) player && counter == 26 && (getCharFromBoard board 0 (getIndex (Stelle (a) (b-4))) == gegner || getCharFromBoard board 0 (getIndex (Stelle (a) (b-4))) == cityGegner  ) -> (Stelle (a) (b-4)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonShotGeradeNachUnten board (Stelle a b) player && counter == 27 && (getCharFromBoard board 0 (getIndex (Stelle (a) (b-5))) == gegner || getCharFromBoard board 0 (getIndex (Stelle (a) (b-5))) == cityGegner ) -> (Stelle (a) (b-5)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonShotWaargerechtNachLinks board (Stelle a b) player && counter == 28 && (getCharFromBoard board 0 (getIndex (Stelle (a-4) (b))) == gegner || getCharFromBoard board 0 (getIndex (Stelle (a-4) (b))) == cityGegner ) -> (Stelle (a-4) (b)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonShotWaargerechtNachLinks board (Stelle a b) player && counter == 29 && (getCharFromBoard board 0 (getIndex (Stelle (a-5) (b))) == gegner || getCharFromBoard board 0 (getIndex (Stelle (a-5) (b))) == cityGegner )-> (Stelle (a-5) (b)) :  getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonShotWaargerechtNachRechts board (Stelle a b) player && counter == 30 && (getCharFromBoard board 0 (getIndex (Stelle (a+4) (b))) == gegner || getCharFromBoard board 0 (getIndex (Stelle (a+4) (b))) == cityGegner )-> (Stelle (a+4) (b)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonShotWaargerechtNachRechts board (Stelle a b) player && counter == 31 && (getCharFromBoard board 0 (getIndex (Stelle (a+5) (b))) == gegner || getCharFromBoard board 0 (getIndex (Stelle (a+5) (b))) == cityGegner  )-> (Stelle (a+5) (b)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonMoveDiagonalLinksNachOben board (Stelle a b) player && counter == 32 -> (Stelle (a-3) (b+3)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonMoveDiagonalLinksNachUnten board (Stelle a b) player && counter == 33 -> (Stelle (a+3) (b-3)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonMoveDiagonalRechtsNachOben board (Stelle a b) player && counter == 34 -> (Stelle (a+3) (b+3)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonMoveDiagonalRechtsNachUnten board (Stelle a b) player && counter == 35 -> (Stelle (a-3) (b-3)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonMoveGeradeNachOben board (Stelle a b) player && counter == 36 -> (Stelle (a) (b+3)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonMoveGeradeNachUnten board (Stelle a b) player && counter == 37 -> (Stelle (a) (b-3)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonMoveWaagerechtNachLinks board (Stelle a b) player && counter == 38 -> (Stelle (a-3) (b)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | isValidCannonMoveWaagerechtNachRechts board (Stelle a b) player && counter == 39 -> (Stelle (a+3) (b)) : getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)
     | otherwise  -> getAllGultigenZielMovesFromSoldat (Stelle a b) board (counter+1)


getPossibleCityPlaces :: Char -> Int -> [Koordinate]
getPossibleCityPlaces _ 9 = []
getPossibleCityPlaces player counter 
   | player == 'w' = (Stelle counter 9) : getPossibleCityPlaces player (counter+1)
   | player == 'b' = (Stelle counter 0) : getPossibleCityPlaces player (counter+1)
   | otherwise = []

containsCity :: String -> Char -> Bool
containsCity [] _ = False
containsCity (x:xs) player 
    | (player == 'b' && x == 'B') = True
    | (player == 'w' && x == 'W') = True
    | otherwise = containsCity xs player

isValidCannonShotDiagonalRechtsNachOben :: String -> Koordinate -> Char -> Bool
isValidCannonShotDiagonalRechtsNachOben board (Stelle a b) player
    | (getCharFromBoard board 0 (getIndex (Stelle (a+1) (b+1))) == player && getCharFromBoard board 0 (getIndex (Stelle (a+2) (b+2))) == player && getCharFromBoard board 0 (getIndex (Stelle (a+3) (b+3))) == '1' && (getCharFromBoard board 0 (getIndex (Stelle (a+4) (b+4))) == getGegner player ||getCharFromBoard board 0 (getIndex (Stelle (a+4) (b+4))) == getGegnerischeStadt player) ) = True
    | (getCharFromBoard board 0 (getIndex (Stelle (a+1) (b+1))) == player && getCharFromBoard board 0 (getIndex (Stelle (a+2) (b+2))) == player && getCharFromBoard board 0 (getIndex (Stelle (a+3) (b+3))) == '1' && (getCharFromBoard board 0 (getIndex (Stelle (a+5) (b+5))) == getGegner player ||getCharFromBoard board 0 (getIndex (Stelle (a+5) (b+5))) == getGegnerischeStadt player) ) = True
    | otherwise = False

  
isValidCannonShotDiagonalRechtsNachUnten :: String -> Koordinate -> Char -> Bool
isValidCannonShotDiagonalRechtsNachUnten board (Stelle a b) player
    | (getCharFromBoard board 0 (getIndex (Stelle (a-1) (b-1))) == player && getCharFromBoard board 0 (getIndex (Stelle (a-2) (b-2))) == player && getCharFromBoard board 0 (getIndex (Stelle (a-3) (b-3))) == '1' && (getCharFromBoard board 0 (getIndex (Stelle (a-4) (b-4))) == getGegner player || getCharFromBoard board 0 (getIndex (Stelle (a-4) (b-4))) == getGegnerischeStadt player))  = True
    | (getCharFromBoard board 0 (getIndex (Stelle (a-1) (b-1))) == player && getCharFromBoard board 0 (getIndex (Stelle (a-2) (b-2))) == player && getCharFromBoard board 0 (getIndex (Stelle (a-3) (b-3))) == '1' && (getCharFromBoard board 0 (getIndex (Stelle (a-5) (b-5))) == getGegner player || getCharFromBoard board 0 (getIndex (Stelle (a-5) (b-5))) == getGegnerischeStadt player))  = True
    | otherwise = False

isValidCannonShotDiagonalLinksNachUnten :: String -> Koordinate -> Char -> Bool
isValidCannonShotDiagonalLinksNachUnten board (Stelle a b) player
    | (getCharFromBoard board 0 (getIndex (Stelle (a+1) (b-1))) == player && getCharFromBoard board 0 (getIndex (Stelle (a+2) (b-2))) == player && getCharFromBoard board 0 (getIndex (Stelle (a+3) (b-3))) == '1' && (getCharFromBoard board 0 (getIndex (Stelle (a+4) (b-4))) == getGegner player || getCharFromBoard board 0 (getIndex (Stelle (a+4) (b-4))) == getGegnerischeStadt player) ) = True
    | (getCharFromBoard board 0 (getIndex (Stelle (a+1) (b-1))) == player && getCharFromBoard board 0 (getIndex (Stelle (a+2) (b-2))) == player && getCharFromBoard board 0 (getIndex (Stelle (a+3) (b-3))) == '1' && (getCharFromBoard board 0 (getIndex (Stelle (a+5) (b-5))) == getGegner player || getCharFromBoard board 0 (getIndex (Stelle (a+5) (b-5))) == getGegnerischeStadt player)) = True
    | otherwise = False

isValidCannonShotDiagonalLinksNachOben :: String -> Koordinate -> Char -> Bool
isValidCannonShotDiagonalLinksNachOben board (Stelle a b) player
    | (getCharFromBoard board 0 (getIndex (Stelle (a-1) (b+1))) == player && getCharFromBoard board 0 (getIndex (Stelle (a-2) (b+2))) == player && getCharFromBoard board 0 (getIndex (Stelle (a-3) (b+3))) == '1' &&  (getCharFromBoard board 0 (getIndex (Stelle (a-4) (b+4))) == getGegner player || getCharFromBoard board 0 (getIndex (Stelle (a-4) (b+4))) == getGegnerischeStadt player))  = True
    | (getCharFromBoard board 0 (getIndex (Stelle (a-1) (b+1))) == player && getCharFromBoard board 0 (getIndex (Stelle (a-2) (b+2))) == player && getCharFromBoard board 0 (getIndex (Stelle (a-3) (b+3))) == '1' &&  (getCharFromBoard board 0 (getIndex (Stelle (a-5) (b+5))) == getGegner player || getCharFromBoard board 0 (getIndex (Stelle (a-5) (b+5))) == getGegnerischeStadt player))  = True
    | otherwise = False

isValidCannonShotGeradeNachOben :: String -> Koordinate -> Char -> Bool
isValidCannonShotGeradeNachOben board (Stelle a b) player
    | (getCharFromBoard board 0 (getIndex (Stelle (a) (b+1))) == player && getCharFromBoard board 0 (getIndex (Stelle (a) (b+2))) == player && getCharFromBoard board 0 (getIndex (Stelle (a) (b+3))) == '1' && (getCharFromBoard board 0 (getIndex (Stelle (a) (b+4))) == getGegner player || getCharFromBoard board 0 (getIndex (Stelle (a) (b+4))) == getGegnerischeStadt player  )) = True
    | (getCharFromBoard board 0 (getIndex (Stelle (a) (b+1))) == player && getCharFromBoard board 0 (getIndex (Stelle (a) (b+2))) == player && getCharFromBoard board 0 (getIndex (Stelle (a) (b+3))) == '1' && (getCharFromBoard board 0 (getIndex (Stelle (a) (b+5))) == getGegner player|| getCharFromBoard board 0 (getIndex (Stelle (a) (b+5))) == getGegnerischeStadt player )) = True
    | otherwise = False

isValidCannonShotGeradeNachUnten :: String -> Koordinate -> Char -> Bool
isValidCannonShotGeradeNachUnten board (Stelle a b) player
    | (getCharFromBoard board 0 (getIndex (Stelle (a) (b-1))) == player && getCharFromBoard board 0 (getIndex (Stelle (a) (b-2))) == player && getCharFromBoard board 0 (getIndex (Stelle (a) (b-3))) == '1' && (getCharFromBoard board 0 (getIndex (Stelle (a) (b-4))) == getGegner player|| getCharFromBoard board 0 (getIndex (Stelle (a) (b-4))) == getGegnerischeStadt player )) = True
    | (getCharFromBoard board 0 (getIndex (Stelle (a) (b-1))) == player && getCharFromBoard board 0 (getIndex (Stelle (a) (b-2))) == player && getCharFromBoard board 0 (getIndex (Stelle (a) (b-3))) == '1' && (getCharFromBoard board 0 (getIndex (Stelle (a) (b-5))) == getGegner player|| getCharFromBoard board 0 (getIndex (Stelle (a) (b-5))) == getGegnerischeStadt player )) = True
    | otherwise = False


isValidCannonShotWaargerechtNachLinks :: String -> Koordinate -> Char -> Bool
isValidCannonShotWaargerechtNachLinks board (Stelle a b) player
    | (getCharFromBoard board 0 (getIndex (Stelle (a-1) (b))) == player && getCharFromBoard board 0 (getIndex (Stelle (a-2) (b))) == player && getCharFromBoard board 0 (getIndex (Stelle (a-3) (b))) == '1' && (getCharFromBoard board 0 (getIndex (Stelle (a-4) (b))) == getGegner player|| getCharFromBoard board 0 (getIndex (Stelle (a-4) (b))) == getGegnerischeStadt player)) = True
    | (getCharFromBoard board 0 (getIndex (Stelle (a-1) (b))) == player && getCharFromBoard board 0 (getIndex (Stelle (a-2) (b))) == player && getCharFromBoard board 0 (getIndex (Stelle (a-3) (b))) == '1' && (getCharFromBoard board 0 (getIndex (Stelle (a-5) (b))) == getGegner player|| getCharFromBoard board 0 (getIndex (Stelle (a-5) (b))) == getGegnerischeStadt player )) = True
    | otherwise = False

isValidCannonShotWaargerechtNachRechts :: String -> Koordinate -> Char -> Bool
isValidCannonShotWaargerechtNachRechts board (Stelle a b) player
    | (getCharFromBoard board 0 (getIndex (Stelle (a+1) (b))) == player && getCharFromBoard board 0 (getIndex (Stelle (a+2) (b))) == player && getCharFromBoard board 0 (getIndex (Stelle (a+3) (b))) == '1' && (getCharFromBoard board 0 (getIndex (Stelle (a+4) (b))) == getGegner player|| getCharFromBoard board 0 (getIndex (Stelle (a+4) (b))) == getGegnerischeStadt player )) = True
    | (getCharFromBoard board 0 (getIndex (Stelle (a+1) (b))) == player && getCharFromBoard board 0 (getIndex (Stelle (a+2) (b))) == player && getCharFromBoard board 0 (getIndex (Stelle (a+3) (b))) == '1' && (getCharFromBoard board 0 (getIndex (Stelle (a+5) (b))) == getGegner player|| getCharFromBoard board 0 (getIndex (Stelle (a+5) (b))) == getGegnerischeStadt player)) = True
    | otherwise = False

--Cannon Move 

isValidCannonMoveGeradeNachOben :: String -> Koordinate -> Char -> Bool
isValidCannonMoveGeradeNachOben board (Stelle a b) player
   | (getCharFromBoard board 0 (getIndex (Stelle (a) (b+1))) == player && getCharFromBoard board 0 (getIndex (Stelle (a) (b+2))) == player && getCharFromBoard board 0 (getIndex (Stelle (a) (b+3))) == '1') = True
   | otherwise = False
   
isValidCannonMoveGeradeNachUnten :: String -> Koordinate -> Char -> Bool
isValidCannonMoveGeradeNachUnten board (Stelle a b) player
   | (getCharFromBoard board 0 (getIndex (Stelle (a) (b-1))) == player && getCharFromBoard board 0 (getIndex (Stelle (a) (b-2))) == player && getCharFromBoard board 0 (getIndex (Stelle (a) (b-3))) == '1') = True
   | otherwise = False
   
isValidCannonMoveDiagonalLinksNachOben :: String -> Koordinate -> Char -> Bool
isValidCannonMoveDiagonalLinksNachOben board (Stelle a b) player
   | (getCharFromBoard board 0 (getIndex (Stelle (a-1) (b+1))) == player && getCharFromBoard board 0 (getIndex (Stelle (a-2) (b+2))) == player && getCharFromBoard board 0 (getIndex (Stelle (a-3) (b+3))) == '1') = True
   | otherwise = False
 
isValidCannonMoveDiagonalLinksNachUnten :: String -> Koordinate -> Char -> Bool
isValidCannonMoveDiagonalLinksNachUnten board (Stelle a b) player
   | (getCharFromBoard board 0 (getIndex (Stelle (a+1) (b-1))) == player && getCharFromBoard board 0 (getIndex (Stelle (a+2) (b-2))) == player && getCharFromBoard board 0 (getIndex (Stelle (a+3) (b-3))) == '1') = True
   | otherwise = False 
 

 
isValidCannonMoveDiagonalRechtsNachOben :: String -> Koordinate -> Char -> Bool
isValidCannonMoveDiagonalRechtsNachOben board (Stelle a b) player
   | (getCharFromBoard board 0 (getIndex (Stelle (a+1) (b+1))) == player && getCharFromBoard board 0 (getIndex (Stelle (a+2) (b+2))) == player && getCharFromBoard board 0 (getIndex (Stelle (a+3) (b+3))) == '1') = True
   | otherwise = False 


isValidCannonMoveDiagonalRechtsNachUnten :: String -> Koordinate -> Char -> Bool
isValidCannonMoveDiagonalRechtsNachUnten board (Stelle a b) player
   | (getCharFromBoard board 0 (getIndex (Stelle (a-1) (b-1))) == player && getCharFromBoard board 0 (getIndex (Stelle (a-2) (b-2))) == player && getCharFromBoard board 0 (getIndex (Stelle (a-3) (b-3))) == '1') = True
   | otherwise = False 
 
isValidCannonMoveWaagerechtNachLinks :: String -> Koordinate -> Char -> Bool
isValidCannonMoveWaagerechtNachLinks board (Stelle a b) player
   | (getCharFromBoard board 0 (getIndex (Stelle (a-1) (b))) == player && getCharFromBoard board 0 (getIndex (Stelle (a-2) (b))) == player && getCharFromBoard board 0 (getIndex (Stelle (a-3) (b))) == '1') = True
   | otherwise = False 

isValidCannonMoveWaagerechtNachRechts :: String -> Koordinate -> Char -> Bool
isValidCannonMoveWaagerechtNachRechts board (Stelle a b) player
   | (getCharFromBoard board 0 (getIndex (Stelle (a+1) (b))) == player && getCharFromBoard board 0 (getIndex (Stelle (a+2) (b))) == player && getCharFromBoard board 0 (getIndex (Stelle (a+3) (b))) == '1') = True
   | otherwise = False 

naechsteKoordinateBerechnen :: Koordinate -> Koordinate
naechsteKoordinateBerechnen (Stelle a b ) = (Stelle (a-1) (b))

--board , koordinatevon Player player
isPlayerBedroht :: String -> Koordinate -> Char -> Bool
isPlayerBedroht board (Stelle a b) player 
    | getCharFromBoard board 0 (getIndex (Stelle (a+1) b)) == getGegner player = True
    | getCharFromBoard board 0 (getIndex (Stelle (a+1) (b+1))) == getGegner player = True
    | getCharFromBoard board 0 (getIndex (Stelle (a+1) (b-1))) == getGegner player = True
    | getCharFromBoard board 0 (getIndex (Stelle (a-1) b)) == getGegner player = True
    | getCharFromBoard board 0 (getIndex (Stelle (a-1) (b-1))) == getGegner player = True
    | getCharFromBoard board 0 (getIndex (Stelle (a-1) (b+1))) == getGegner player = True
    | getCharFromBoard board 0 (getIndex (Stelle (a) (b+1))) == getGegner player = True
    | getCharFromBoard board 0 (getIndex (Stelle (a) (b-1))) == getGegner player = True
    | otherwise = False

  
 --esta es la joda de la joda , input alle koordinate von players , board , output alle mögliche Moves 
fromAllSoldatenZuAlleGueltigenMoves :: [Koordinate] -> String -> [String]
fromAllSoldatenZuAlleGueltigenMoves [] _  = []
fromAllSoldatenZuAlleGueltigenMoves (x:xs) board = (convertAllGutltigenMovesInStrings  x (getAllGultigenZielMovesFromSoldat x board 0)) ++ fromAllSoldatenZuAlleGueltigenMoves xs board


convertKoordinatesToString :: [Koordinate] -> String
convertKoordinatesToString [] = ""
convertKoordinatesToString (x:xs) = (convertkoordinateToString x)++"-"++(convertkoordinateToString x)++"," ++ (convertKoordinatesToString xs)



listMoves a = let player = schneidePlayerChar a
                  board = convertBoard1 a 0
                  gegner = getGegner player
                  koordinatenAlleSoldaten = getAllKordinatenVonPlayers board player 0
                  firstSoldat  = head(koordinatenAlleSoldaten)
                  allMoves = fromAllSoldatenZuAlleGueltigenMoves koordinatenAlleSoldaten board
                  allMovesInSingleString = convertListToString allMoves
                  firstRoundBoolean =    (not (containsCity board player))  
                  firstRoundMoves = getPossibleCityPlaces player 1
                  firstRoundMovesInString = convertKoordinatesToString firstRoundMoves
 in case () of
  _ | firstRoundBoolean -> "[" ++  s firstRoundMovesInString ++ "]"
    | otherwise -> "[" ++  s allMovesInSingleString ++ "]"
--achtung cut last Komma


--zu checken Wer Player und Wer gegner ist 




getMove a = getFirstMoveFromListMoves (listMoves a) 0
--getMove a = getFirstMoveFromListMoves2 (listMoves a) 0 (getHashVonListMoves a)

getFirstMoveFromListMoves :: String -> Int -> String
getFirstMoveFromListMoves [] _ = []
getFirstMoveFromListMoves (x:xs) 5 = x:[]
getFirstMoveFromListMoves (x:xs) index
  | x == '[' = getFirstMoveFromListMoves xs (index+1)
  | otherwise = x : getFirstMoveFromListMoves xs (index+1)  

  
s :: String -> String
s  [] = []
s (x:[]) = []
s (x:xs) = x : s xs



schneidePlayerChar :: String -> Char
schneidePlayerChar (x:[]) = x
schneidePlayerChar (x:xs) = schneidePlayerChar xs


getHashVonListMoves :: String -> Int
getHashVonListMoves [] = 0
getHashVonListMoves a = (((length (listMoves a)) -1) `quot` (2)) +1


getFirstMoveFromListMoves2 :: String -> Int ->  Int -> String
getFirstMoveFromListMoves2 [] _ _ = []
getFirstMoveFromListMoves2 (x:xs) counter index
  | counter == (index+4) = x :[] 
  | counter >= index = x : getFirstMoveFromListMoves2 xs (counter+1) (index)
  | otherwise = getFirstMoveFromListMoves2 xs (counter+1) (index)  


