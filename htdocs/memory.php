<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

include('dbcon.php');

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if ($android) {
    try {
        $stmt = $con->query('SELECT Title as title, Author as author, Memory as memory FROM memory');
        $data = $stmt->fetchAll(PDO::FETCH_ASSOC);
        echo json_encode($data);
    } catch (PDOException $e) {
        echo json_encode(["error" => "Database error: " . $e->getMessage()]);
    }
    exit();
}

if (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) {
    $Title = $_POST['Title'];
    $Author = $_POST['Author'];
    $Memory = $_POST['Memory'];

    if (empty($Title)) {
        $errMSG = "Title is required.";
    } else if (empty($Author)) {
        $errMSG = "Author is required.";
    } else if (empty($Memory)) {
        $errMSG = "Memory is required.";
    }

    if (!isset($errMSG)) {
        try {
            $stmt = $con->prepare('INSERT INTO memory(Title, Author, Memory) VALUES(:Title, :Author, :Memory)');
            $stmt->bindParam(':Title', $Title);
            $stmt->bindParam(':Author', $Author);
            $stmt->bindParam(':Memory', $Memory);

            if ($stmt->execute()) {
                $successMSG = "Data inserted successfully!";
            } else {
                $errMSG = "Failed to insert data.";
            }

        } catch (PDOException $e) {
            die("Database error: " . $e->getMessage());
        }
    }
}

if (isset($errMSG)) echo $errMSG;
if (isset($successMSG)) echo $successMSG;

if (!$android) {
?>
    <html>
        <body>
            <form action="<?php $_PHP_SELF ?>" method="POST">
                Title: <input type="text" name="Title" /><br>
                Author: <input type="text" name="Author" /><br>
                Memory: <input type="text" name="Memory" /><br>
                <input type="submit" name="submit" />
            </form>
        </body>
    </html>
<?php
}
?>
