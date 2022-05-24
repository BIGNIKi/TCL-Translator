set x "abc"
puts "Simple replacement: $x"
set y [set x [set z 124]]
set x "asldfjlk"
puts "$z $x $y"