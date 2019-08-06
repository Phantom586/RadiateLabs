<?php

include 'db_functions.php';

$response = array("error" => FALSE);


if ($_SERVER['REQUEST_METHOD']=='POST' ) {

	$phone = $_POST["phone"];
	// $phone = '9839463490';

	$user = getUserByPhone($phone);

	if ( $user != false ) {
	 
		echo "". $user["Name"]. "-". $user["Phone_Number"]. "-". $user["Email"]. "-". $user["Referral_Phone_Number"]. "";

    } else {

    	$response["error"] = TRUE;
		$response["error_msg"] = "Sorry The User Doesn't Exists!!";
		echo json_encode($response);

    }

}

?>
