<?php
  error_reporting(E_ALL);
  ini_set('display_errors', 1);
 
  include('dbcon.php');
 
  $userName = isset($_POST['userName']) ? $_POST['userName'] : '';
  $userPassword = isset($_POST['userPassword']) ? $_POST['userPassword'] : '';
 
  $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
 
  if ($userName != "") {
    $sql = "SELECT * FROM user WHERE userName = :userName AND userPassword = :userPassword";
    $stmt = $con->prepare($sql);
    $stmt->bindParam(':userName', $userName);
    $stmt->bindParam(':userPassword', $userPassword);
    $stmt->execute();
 
    if ($stmt->rowCount() == 0) {
      echo "'$userName' no name OR wrong password.";
    } else {
      $data = array();
 
      while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        array_push($data, array(
          'userName' => $row["userName"],
          'userPassword' => $row["userPassword"],
          'userAge' => $row["userAge"],
          'userEmail' => $row["userEmail"],
          'userNum' => $row["userNum"],
          'userPhone' => $row["userPhone"]
        ));
      }
 
      if (!$android) {
        echo "<pre>";
        print_r($data);
        echo '</pre>';
      } else {
        header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("user" => $data), JSON_PRETTY_PRINT + JSON_UNESCAPED_UNICODE);
        echo $json;
      }
    }
  } else {
    echo "login.";
  }
?>
 
<?php
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
 
if (!$android) {
?>
 
<html>
   <body>
      <form action="Login.php" method="POST">
         NAME: <input type="text" name="userName" />
         PASSWORD: <input type="text" name="userPassword" />
         <input type="submit" />
      </form>
   </body>
</html>
<?php
}
?>
