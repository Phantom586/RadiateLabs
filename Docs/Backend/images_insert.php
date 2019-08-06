<!DOCTYPE html>  
<html>  
    <head>  
        <title>Insert Images in Mysql Database from PHP</title>  
        <script src="https://code.jquery.com/jquery-3.4.1.min.js"
                integrity="sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo="
                crossorigin="anonymous">
        </script>  
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" 
            integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" 
            crossorigin="anonymous">  
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" 
                integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" 
                crossorigin="anonymous">
        </script> 
    </head>  
        <body>

            <form action="" method="POST" enctype="multipart/form-data">

                <input type="file" name="userfile[]" value="" multiple="">
                <input type="submit" name="submit" value="Upload">
            
            </form>

            <?php

                $phpFileUploadErrors = array(
                    0 => "There is no error, file uploaded successfully",
                    1 => "The Uploaded File exceeds the upload_max_filesize directive in php.ini",
                    2 => "The Uploaded File exceeds the MAX_FILE_SIZE directive that was specified in HTML form",
                    3 => "The Uploaded File was only partially uploded",
                    4 => "No File was Uploaded",
                    5 => "Missing a temporary folder",
                    6 => "Failed to write File to Disk.",
                    7 => "A PHP Extension stopped the File Upload",
                );

                if(isset($_FILES['userfile'])){
                    $file_array = reArrangeFiles($_FILES['userfile']);
                    for($i = 0; $i < count($file_array); $i++){
                        if($file_array[$i]['error']){
                            ?> <div class="alert alert-danger">
                            <?php echo $file_array[$i]['name'].' - '.$phpFileUploadErrors[$file_array[$i]['error']];
                            ?> </div> <?php
                        } else {
                            $extensions = array('jpg', 'png', 'gif', 'jpeg');
                            $file_ext = explode('.', $file_array[$i]['name']);
                            $file_ext = end($file_ext);

                            if(!in_array($file_ext, $extensions)){
                                ?> <div class="alert alert-danger">
                                <?php echo "{$file_array[$i]['name']} - Invalid File Extension!"; 
                                ?> </div> <?php
                            }
                        }
                    }
                }

                function reArrangeFiles( $file_post ){
                    $file_ar = array();
                    $file_count = count($file_post["name"]);
                    $file_keys = array_keys($file_post);

                    for($i = 0; $i < $file_count; $i++){
                        foreach($file_keys as $key) {
                            $file_ar[$i][$key] = $file_post[$key][$i];
                        }
                    }
                    return $file_ar;
                }

                function pre_r( $array ){
                    echo '<pre>';
                    print_r( $array );
                    echo '<pre>';

                }

            ?>

        </body>
</html>