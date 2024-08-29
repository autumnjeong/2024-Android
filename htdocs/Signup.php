<?php
 
error_reporting(E_ALL);
ini_set('display_errors', 1);
 
include('dbcon.php');
 
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
 
if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
{
    $userName = $_POST['userName'];
    $userPassword = $_POST['userPassword'];
    $userAge = $_POST['userAge'];
    $userEmail = $_POST['userEmail'];
    $userNum = $_POST['userNum'];
    $userPhone = $_POST['userPhone'];
 
    if(empty($userName)){
        $errMSG = "Username is required.";
    }
    else if(empty($userPassword)){
        $errMSG = "Password is required.";
    }
    else if(empty($userAge)){
        $errMSG = "Age is required.";
    }
    else if(empty($userEmail)){
        $errMSG = "Email is required.";
    }
    else if(empty($userNum)){
        $errMSG = "User number is required.";
    }
    else if(empty($userPhone)){
        $errMSG = "Phone number is required.";
    }
 
    if(!isset($errMSG)){
        try{
            $stmt = $con->prepare('INSERT INTO user(userName, userPassword, userAge, userEmail, userNum, userPhone) VALUES(:userName, :userPassword, :userAge, :userEmail, :userNum, :userPhone)');
            $stmt->bindParam(':userName', $userName);
            $stmt->bindParam(':userPassword', $userPassword);
            $stmt->bindParam(':userAge', $userAge);
            $stmt->bindParam(':userEmail', $userEmail);
            $stmt->bindParam(':userNum', $userNum);
            $stmt->bindParam(':userPhone', $userPhone);
 
            if($stmt->execute())
            {
                $successMSG = "Data inserted successfully!";
            }
            else
            {
                $errMSG = "Failed to insert data.";
            }
 
        } catch(PDOException $e) {
            die("Database error: " . $e->getMessage());
        }
    }
}
 
if (isset($errMSG)) echo $errMSG;
if (isset($successMSG)) echo $successMSG;
 
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
 
if( !$android )
{
?>
    <html>
       <body>
            <form action="<?php $_PHP_SELF ?>" method="POST">
                Username: <input type="text" name="userName" /><br>
                Password: <input type="text" name="userPassword" /><br>
                Age: <input type="text" name="userAge" /><br>
                Email: <input type="text" name="userEmail" /><br>
                User Number: <input type="text" name="userNum" /><br>
                Phone Number: <input type="text" name="userPhone" /><br>
                <input type="submit" name="submit" />
            </form>
       </body>
    </html>
<?php
}
?>
