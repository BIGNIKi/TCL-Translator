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