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