<?php 

// This is just an example of reading server side data and sending it to the client.
// It reads a json formatted text file and outputs it.

$string = file_get_contents("https://energ-6a767.firebaseio.com/.json");
echo $string;

// Instead you can query your database and parse into JSON etc etc

?>