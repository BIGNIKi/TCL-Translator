proc FOR {a b c} {
	puts "Command FOR was changed on puts";
	puts "Args: $a $b $c"
}

FOR {set i 1} {$i < 10} {incr i}