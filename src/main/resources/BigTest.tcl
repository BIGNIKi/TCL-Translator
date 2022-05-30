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

puts "$Z_LABEL Blabla"

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
    if{$x == 3} {
        puts "kek"
    } else
    {
        puts "TrueMan"
    }
} else {
    puts "false"
}

puts "$x"

set x 2;

if {$x == 2} {
if {$x == 2} {
if {$x == 2} {
if {$x == 2} {
if {$x == 2} {
puts "$x equals 2"
} else {
puts "$x isnt equals 2"
}
} else {
puts "$x isnt equals 2"
}
} else {
puts "$x isnt equals 2"
}
} else {
puts "$x isnt equals 2"
}
} else {
puts "$x isnt equals 2"
}

set x 1;
while {$x < 5} {puts "value x equals $x"; set x [expr $x + 1]}

puts "The first one cycle has finished when X equaled $x"

set x 0;
while {$x < 5} {
	set x [expr $x + 1];
	if {$x > 6} {break};
 	if "$x > 3" {continue};
	puts "value of x is $x";
}

puts "The second one cycle has finished when X equaled $x"

for {puts "Start"; set i 0} {$i < 2 && 5 == 5} {incr i; puts "After command incr: $i"; } {
	puts "Inside first loop: $i"
}

set Y 5
for {set i 3} {$i >= 0} {incr i -1} {
	if {$i == 3} {
	    continue
	} elseif {$i == 2} {
	    puts "[expr (pow(2,$Y) + (1 + 2 + 3 * 7 + (3*2))) * sqrt(4)]"
	} else {
	    break
	}

}

puts "Start"; set i 0;
while {$i < 2} {
	puts "Inside first loop: $i"
	incr i;
	puts "After command incr: $i";
}
proc sum {arg1 arg2} {
for {puts "Start"; set i 0} {$i < 2 && 5 == 5} {incr i; puts "After command incr: $i"; } {
	puts "Inside first loop: $i"
}

set Y 5
for {set i 3} {$i >= 0} {incr i -1} {
	if {$i == 3} {
	    continue
	} elseif {$i == 2} {
	    puts "[expr (pow(2,$Y) + (1 + 2 + 3 * 7 + (3*2))) * sqrt(4)]"
	} else {
	    break
	}

}

puts "Start"; set i 0;
while {$i < 2} {
	puts "Inside first loop: $i"
	incr i;
	puts "After command incr: $i";
}

	set x [expr $arg1+$arg2];
	return $x
}

puts " Sum 2 + 3 equals: [sum 2 3]"
proc FOR {a b c} {
	puts "Command FOR was changed on puts";
	puts "Args: $a $b $c"
}

FOR {set i 1} {$i < 10} {incr i}
proc plusFive {a} {
	for {set i 0} {$i < 5} {incr i} {
	    incr a
	}
	return $a
}

set x 12
set x [plusFive $x]
puts "$x"
set x [plusFive $x]
puts "$x"
set inc {{x} {incr x;
return $x}}
puts [apply $inc 1]

puts [apply {{a b} {return [expr $a + $b]}} 1 2]

proc foo {lambda item} {
        return [apply $lambda $item]
}
puts [foo {{x} {set a [expr $x*4 + $x - 2]; return $a}} 4]

set script {{a b c} {puts "$a $b $c"}}
apply $script a b c

proc foo2 {lambda a b} {
        return [apply $lambda $a $b]
}
puts [foo2 {{x y} {set a [expr $x*4 + $y - 2]; return $a}} 2 4]