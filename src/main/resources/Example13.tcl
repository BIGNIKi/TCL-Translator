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