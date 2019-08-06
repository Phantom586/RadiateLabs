<?php


function storeUser($phone, $full_name, $email, $ref_no) {

    require 'Connect.php';

    $mysql_query = "INSERT INTO User_Table (Phone_Number, Name, Email, Sign_Up_Timestamp, Referral_Phone_Number) VALUES('$phone', '$full_name', '$email', null, '$ref_no')";

     if ($conn->query($mysql_query) === TRUE ) {
	
    echo "Insert Successful";
    return TRUE;

     } else {

    echo "Insert Unsuccessfull";
    return FALSE;
     }
    
}


function getUserByPhone($phone) {

    require 'Connect.php';

    $mysql_query = "SELECT * FROM User_Table WHERE Phone_Number='$phone'";
	
	$result = $conn->query($mysql_query);

	if ($result->num_rows > 0) {

		if ( $row = $result->fetch_array() ) {
		
            //echo " Phone Number : ". $row[0]. " Name : " . $row[1]. " Email : " . $row[2]. "<br>";
            return $row;

		}

	} else {

		echo NULL;
	
	}
    
}

function verify_referrer($ref_no){

    require 'Connect.php';

    $mysql_query = "SELECT *  FROM  User_Table WHERE Phone_Number='$ref_no'";

    $result = $conn->query($mysql_query);

    if( $result->num_rows > 0){
        return "TRUE";
    } else {
        return "FALSE";
    }

}

function update_referral_amt($user, $referrer){

    require 'Connect.php';

    $qry1 = "UPDATE User_Table SET Referral_Amount_Balance = Referral_Amount_Balance+10  WHERE Phone_Number='$user'";

   $qry2 =  "UPDATE User_Table SET Referral_Amount_Balance = Referral_Amount_Balance+10 ,  Number_of_Referrals = Number_of_Referrals+1 WHERE Phone_Number='$referrer'";

//    if ($conn->query($qry1) ===  TRUE){
//     print("QUERY_1 SUCCESSFUL -> ");
//    } else {
//     print("QUERY_1 UNSUCCESSFUL -> ");
//    }

//    if ($conn->query($qry2) ===  TRUE){
//     print("QUERY_2 SUCCESSFUL -> ");
//    } else {
//     print("QUERY_2 UNSUCCESSFUL -> ");
//    }

//    return TRUE;

   if( $conn->query($qry1) ===  TRUE and $conn->query($qry2) === TRUE){
            return "TRUE";
   } else {
       return "FALSE";
   }

}

?>
