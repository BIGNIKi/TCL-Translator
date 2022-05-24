puts "[expr ((2 + 2) + 5) * 4]"
set X 100
set Y 256
set Z_LABEL "$Y plus $X equals "
puts {$Z_LABEL [expr $Y + $X]}
puts "Command for sum two numbers: \[expr \$a + \$b]"
puts [expr "5 * $X"]
set X [puts "sdfsdf"]
puts "$X"
puts [set X "123"]
puts "According to the bracket 4 * (5 + -3)  is: [expr 4 * (5 + -3)]"
set X 100
set Y 256
puts "$Z_LABEL {[expr $Y + $X]}"
puts "Command to add two nums: \[expr \$a + \$b]"