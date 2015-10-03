<?php
  /*
   * This code enables a new user to create an account. 
   */	
  
  // Establishing db connection
  $servername = "localhost";
  $username = "root";
  $password = "ravinder";
  $dbname = "chat";

  // Create connection
  $conn = new mysqli($servername, $username, $password, $dbname);
  // Check connection
  if (!$conn) 
  {
    die("Connection failed: " . $conn->connect_error);
  }


  function createForm()
  {
    echo'
    <div id="loginform">
    <form action="create.php" method="post">
    <label for="name">User name:</label>
    <input type="text" name="name" id="name" /><br><br>
    <label for="pwd">Password:</label>
    <input type="password" name="pwd" id="pwd" /><br><br>
    <input type="submit" name="createNew" id="createNew" value="Create Account" />
    </form>
    </div>
    ';
   }

   if(isset($_POST['createNew']))
   {
	   	$userName = $_POST['name'];
	    $passWord = $_POST['pwd'];
        $ravinder = "Ravinder";

	    if($userName == "" || $passWord =="")
	    {
	      	echo '<span class="error">Please type in a user name and password</span>';
	    }
	    else
	    {
	      	$sql = "SELECT COUNT(*) FROM USERS WHERE USER_NAME = '".$userName."'";
		    $result = mysqli_query($conn, $sql);
		    $count = $result->fetch_row();

		    if ($count[0] == 0)
		    {
		    	$sql = "INSERT INTO USERS VALUES ('".$userName."', '".$passWord."', 0)";
		    	$result = mysqli_query($conn, $sql);
		    	$sql1 = "INSERT INTO friends VALUES ('".$userName."', '".$ravinder."')";
		    	$result1 = mysqli_query($conn, $sql1);
		    	$sql2 = "INSERT INTO friends VALUES ('".$ravinder."', '".$userName."')";
		    	$result2 = mysqli_query($conn, $sql2);
		    	mysqli_close($conn);	
		    	header("Location: index.php");
		    }
		    else
		    {
		    	echo '<span class="error">User already exist</span>';
		    }
	   
	    } 
   }
?>

<!DOCTYPE html>
<html>
	<head>
		<title>chat</title>
		<link type="text/css" rel="stylesheet" href="style.css" />
	</head>
<body>
	<?php
		
		createForm();
	?>
</body>

</html> 