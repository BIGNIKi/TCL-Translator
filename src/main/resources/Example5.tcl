set X 100
set Y 256.5
set Z [expr "1.1 + $Y - $X + 3.4"]
puts "$Z"
puts "Sqrt from $Y it is [expr sqrt($X)]"
puts "[expr sqrt(144)]"
puts "[expr sqrt(225.0)]"
puts "[expr -3 * 4 + 5]"
puts "[expr ((2 + 2) + 5) * 4]"
