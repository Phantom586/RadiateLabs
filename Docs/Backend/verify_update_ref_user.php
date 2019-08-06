<?php

include 'db_functions.php';

$response = array("error" => FALSE);


if ($_SERVER['REQUEST_METHOD']=='POST' ) {

    $user = $_POST["user"];
    $ref = $_POST["referrer"];
    // $user = "7007502265";
    // $ref = "9839463490";

    $referrer_exists = verify_referrer($ref);

		if ($referrer_exists == "TRUE") {
            $result = update_referral_amt($user, $ref);
            if($result == "TRUE"){
               echo $result;
            } else {
                echo $result;
            }
		} else {
            echo $referrer_exists;
        }		
    
}

?>