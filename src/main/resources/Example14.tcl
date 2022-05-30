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