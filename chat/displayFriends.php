<?php
  /*
   * This code displays the list of user's friend
   * by querying the Friends table. 
   * Once the user selects a friend, this calls friend.php
   * to create a new session token called $_SESSION['friend']
   * and redirects to index.php which enables the user to chat 
   * with the selected friend.
  */
  session_start();
  // Establishing db connection
  $servername = "localhost";
  $username = "root";
  $password = "ravinder";
  $dbname = "chat";

  $conn = new mysqli($servername, $username, $password, $dbname);

  if (!$conn) 
  {
    die("Connection failed: " . $conn->connect_error);
  }
?>

<!DOCTYPE html>
  <html>
    <head>
      <title>chat</title>
      <link type="text/css" rel="stylesheet" href="style.css" />
    </head>
    <body>
        <div id="wrapper">
        <div id="menu">
        <p class="welcome">Welcome, <b><?php echo $_SESSION['name']; ?></b></p>
        <p class="logout"><a id="exit" href="#">Logout</a></p>
        <div style="clear:both"></div>
        </div>
        <div id="friendslist">
        <?php
          $sql = "SELECT * FROM FRIENDS WHERE USER = '".$_SESSION['name']."'";
          $result = mysqli_query($conn, $sql);
          if (mysqli_num_rows($result) > 0) 
          {
            // output data of each row
            while($row = mysqli_fetch_assoc($result)) 
            {
            echo '<p class="friends" id ="friends"><a id="'.$row["friend"].'" href="#">'.$row["friend"].'</a></p><br>';
            }
          } 
          else 
          {
            echo "You have no friends";
          }
        ?>
        </div>
      </div>
      <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3/jquery.min.js">
      </script>

      <?php
        echo'
          <script type="text/javascript"> 
            $(document).ready(function()
            {
            ';
              $sql = "SELECT * FROM FRIENDS WHERE USER = '".$_SESSION['name']."'";
              $result = mysqli_query($conn, $sql);
              while($row = mysqli_fetch_assoc($result)) 
              {
                echo '$(\'#'.$row["friend"].'\').click(function()
                {
                  $.post(
                    "friend.php", 
                    {
                     friendname: "'.$row["friend"].'"
                    },
                    function () {
                      window.location = \'index.php\';
                    });
                });';
              }
          echo '
            $("#exit").click(function()
            {
             var exit = confirm("Are you sure you want to end the session?");
             if(exit==true)
              {
                window.location = \'index.php?logout=true\';
              }
            });
            });
          </script>';
          mysqli_close($conn);
      ?>

</body>

</html> 