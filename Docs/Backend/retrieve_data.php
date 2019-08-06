<?php

include 'db_functions.php';

$response = array("error" => FALSE);


if ($_SERVER['REQUEST_METHOD']=='POST' ) {

	$phone = $_POST["phone"];
	// $phone = '9839463490';

	$user = getUserByPhone($phone);

	if ( $user != false ) {
	 
		$response["error"] = FALSE;
		$response["user"]["name"] = $user["Name"];
		$response["user"]["phone"] = $user["Phone_Number"];
		$response["user"]["email"] = $user["Email"];
		$response["user"]["referral_phone_number"] = $user["Referral_Phone_Number"];
		echo json_encode($response);

    } else {

    	$response["error"] = TRUE;
		$response["error_msg"] = "Sorry The User Doesn't Exists!!";
		echo json_encode($response);

    }

}

?>
