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

