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