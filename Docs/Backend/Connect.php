<?php

define('DB_HOST', 'localhost');
define('DB_UNAME', 'root');
define('DB_PASS', "DBP@ssNoq#123");
define('DB_NAME', 'db_noq');

$conn = new mysqli(DB_HOST, DB_UNAME, DB_PASS, DB_NAME);

if ($conn->connect_error) 
	die("Connection Failed". $conn->connect_error);
else
	echo "Connection Established";

?>
