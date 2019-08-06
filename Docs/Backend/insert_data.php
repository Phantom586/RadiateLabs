<?php

include "db_functions.php";


$response = array("error" => FALSE);

if ($_SERVER['REQUEST_METHOD']=='POST' ) {

    define('DB_TABLE', 'User_Table');

    $full_name = $_POST["full_name"];
    $email = $_POST["email"];
    $phone = $_POST["phone"];
    $ref_no = $_POST["ref_no"];

     // $full_name = "Sanjay Chaurasia";
     // $email = "chaurasiask2016@gmail.com";
     // $phone = '9839463490';
     // $ref_no = '8795919139';

     $user = storeUser($phone, $full_name, $email, $ref_no);

     if ( $user ) {

          $response["error"] = FALSE;
          $response["response"] = "Data Inserted Successfully"; 
          echo json_encode($response);

     } else {

          $response["error"] = TRUE;
	     $response["error_msg"] = "Unknown error occurred in registrtation!";
          echo json_encode($response);

     }

}

?>
