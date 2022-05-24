puts "[expr ((2 + 2 % 1) + 5) * 4 / 2]"
set X 100
puts [expr "$X * 5"]
set X 100
puts "[expr 100]"
puts "[expr "$X"]"
puts "[expr 1 + sqrt(144)]"
set X 10
puts "[expr pow(2, $X)]"
puts "[expr pow($X, 2)]"
puts "[expr 1 + sqrt(16)]"
puts "[expr 1 + sqrt($X)]"
puts "[expr pow($X, 2) + 5.0]"
set Y 3
set Z [expr (rand() + pow(2,$Y)) * sqrt(4)]
puts "$Z"
puts "[expr (rand() + pow(2,$Y) + (1 + 2 + 3 * 7 + (3*2))) * sqrt(4)]"