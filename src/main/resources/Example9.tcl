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
