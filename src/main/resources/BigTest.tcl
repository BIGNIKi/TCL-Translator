set Z 15
set Y 1.24
set X "It is a string."
puts "$Z $Y $X"
puts $Y
puts $X
puts $Z
puts {text pishu ya}
puts "Lets fuck it"
puts "Quotation is not, so difficult."
puts andrewChehen
puts amogus

set Z "Moscow"
set Z_LABEL "Capital of Russia is"

puts "$Z_LABEL \nBlabla"

set a 100.00
puts "No Wash $a"
puts "No Gami \$a"
puts "Yes Franc \$$a"

set Z "Moscow"
set Z_LABEL "Capital of Russia is"
puts "$Z_LABEL $Z"
puts {$Z_LABEL $Z}
puts "$Z_LABEL {$Z}"
puts {bla bla "dollar is $1 - 30 rubles"}
puts {There are no such symbols \n \r \x0a \t \t \t \f \v "Hello + 1" [sdfs]}

set x "abc"
puts "Simple replacement: $x"
set y [set x [set z 124]]
set x "asldfjlk"
puts "$z $x $y"
set z [set y [set x 1.234234]]
puts "$z $x $y"
set x [set z [set y "some text"]]
puts "$z $x $y"
set z {[set x "just a text"]}
puts "$z"

set x "abc"
puts "Simple replacement: $x"
set y [set x [set z 124]]
set x "asldfjlk"
puts "$z $x $y"
set z [set y [set x 1.234234]]
puts "$z $x $y"
set x [set z [set y "some text"]]
puts "$z $x $y"
set z {[set x "just a text"]}
puts "$z"
set a "[set x {text inside curly braces}]"

puts "After exucution of a we got: $a"
puts "value \$x : $x"
set b "\[set y {text inside it}]"
puts "$b"

set X 100
set Y 256.5
set Z [expr "1.1 + $Y - $X + 3.4"]
puts "$Z"
puts "Sqrt from $Y it is [expr sqrt($X)]"
puts "[expr sqrt(144)]"
puts "[expr sqrt(225.0)]"
puts "[expr -3 * 4 + 5]"
puts "[expr ((2 + 2) + 5) * 4]"

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
set x "Two";
set y 1;
set z "One";

switch $x "One" "puts One1" "Two" "puts Two2" "default" "puts no_matches"
switch $x \
  "One" 	"puts One1"  \
  "Two" 	"puts Two2" \
  "default" "puts no_matches";

puts [set xx "123"]

switch $x {
   "$z"		    {set y1 [expr $y+1]; puts "Match \$z. $y + $z is $y1" }
   "One"		{set y1 [expr $y+1]; puts "Match one. $y + one is $y1"}
   "Two"		{set y1 [expr $y+2]; puts "Match two. $y + two is $y1"}
   "Three"		{set y1 [expr $y+3]; puts "Match three. $y + three is $y1"}
   "default"	{puts "$x wasnt matched"}
}

set x 1;

if {$x == 2} {puts "$x equals 2"} else {puts "$x isnt equals 2"}

set temp "aaa"
set krya "aaa"

set y 5
set z "a"

if {false || 2.1 > 2.2 || $temp == $krya} {
    puts "true";
    set x "Three";
    switch $x {
       "$z"		    {set y1 [expr $y+1]; puts "Match \$z. $y + $z is $y1" }
       "One"		{set y1 [expr $y+1]; puts "Match one. $y + one is $y1"}
       "Two"		{set y1 [expr $y+2]; puts "Match two. $y + two is $y1"}
       "Three"		{set y1 [expr $y+3]; puts "Match three. $y + three is $y1"}
       "default"	{puts "$x wasnt matched"}
    }
} elseif {5.0 == 5 && $x == 1} {
    puts "One";
    set x 2
} else {
    puts "false"
}

puts "$x"
