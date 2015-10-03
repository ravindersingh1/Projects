<?php
  session_start();

  /*
   * Establishing db connection
   */
  $servername = "localhost";
  $username = "root";
  $password = "ravinder";
  $dbname = "chat";

  $conn = new mysqli($servername, $username, $password, $dbname);

  if (!$conn) 
  {
    die("Connection failed: " . $conn->connect_error);
  }

  /*
   * Below function creates the login form
   */
  function loginForm()
  {
    echo'
    <div id="loginform">
    <form action="index.php" method="post">
    <p>Please login to continue:</p><br>
    <label for="name">User name:</label>
    <input type="text" name="name" id="name" /><br><br>
    <label for="pwd">Password:</label>
    <input type="password" name="pwd" id="pwd" /><br><br>
    <input type="submit" name="enter" id="enter" value="Enter" /><br><br>
    <input type="submit" name="createNew" id="createNew" value="Create Account" />
    </form>
    </div>
    ';
  }

  /* When the user enters the login details the below code checks
   * 1. If the person is a already registered user or not.
   * 2. If the credentials are correct.
   * 3. If the person has already logged in or not.
   * 4. If yes, it dosent allow to log in again.
   * 5. If the user is able to log in,it directs to the displayfriend.php
   *     where the friends of users are displayed.
   * 6. All the users details are present in the USERS table.
   */
  if(isset($_POST['enter']))
  {

    $userName = $_POST['name'];
    $passWord = $_POST['pwd'];

    if($_POST['name'] == "")
    {
        echo '<span class="error">Please type in a name</span>';
    }
    else
    {
      $sql = "SELECT COUNT(*) FROM USERS WHERE USER_NAME = '".$userName."' AND PASSWORD = '".$passWord."' ";
      $result = mysqli_query($conn, $sql);
      $count = $result->fetch_row();

      if ($count[0] == 1)
      {
         $sql = "SELECT LOGIN_FLAG FROM USERS WHERE USER_NAME = '".$userName."'";
         $result = mysqli_query($conn, $sql);
         $flag = $result->fetch_row();

         if($flag[0] == 0)
         {
            $sql = "UPDATE USERS  SET LOGIN_FLAG = 1 WHERE USER_NAME = '".$userName."'";
            $result = mysqli_query($conn, $sql);
            $_SESSION['name'] = stripslashes(htmlspecialchars($_POST['name']));
            header("Location: displayFriends.php");
         }
         else
         {
            echo '<span class="error">User already loged in</span>';
         }   
      }
      else
      {
        $sql = "SELECT COUNT(*) FROM USERS WHERE USER_NAME = '".$userName."'";
        $result = mysqli_query($conn, $sql);
        $count = $result->fetch_row();

       if ($count[0] == 1)
        {
          echo '<span class="error">Please enter the correct password</span>';
        }
        else
        {
          echo '<span class="error">You are not an registered user. Please create an account to chat</span>';
        } 
      }
    }
  }

  /*
   * Enables to create an account for new user .
   * This calls create.php where the new account is created. 
   */
  if(isset($_POST['createNew']))
  {
    header("Location: create.php");
  }

  /*
   * Deletes the chat history by emptying the contents if the file. 
   * This works only when the user wants to clear the chat history 
   */
  if(isset($_GET['clearChat']))
  {
    $filename1 = $_SESSION['name']."_".$_SESSION['friend'].".html";
    $fp1 = fopen($filename1, 'w');
    file_put_contents($filename1, "");
    fclose($fp1);
  }
  
  /*
   * When the users opts to logout while chatting, 
   * 1. The user's friend is notified that the user has logged out.
   * 2. The SESSION variables are destroyed. 
   */
  if(isset($_GET['logout']))
  {

    $sql = "UPDATE USERS  SET LOGIN_FLAG = 0 WHERE USER_NAME = '".$_SESSION['name']."'";
    $result = mysqli_query($conn, $sql);
    mysqli_close($conn);
    
    if(isset($_SESSION['friend'])){
    $filename2 = $_SESSION['friend']."_".$_SESSION['name'].".html";
    $fp2 = fopen($filename2, 'a');
    fwrite($fp2, "<div class='msgln'><i>User ". $_SESSION['name'] ." has left the chat session.</i><br></div>");
    fclose($fp2);
    }
    
    session_destroy();
    header("Location: index.php"); //Redirect the user
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
        if(!isset($_SESSION['name'])){
        loginForm();
        }
        /*
         * Checks if user has selected a friend to chat.
         * If yes, the chat box will be opened where
         * the user can chat with the friend.
         */
        elseif(isset($_SESSION['friend'])){
        ?>
        <div id="wrapper">
        <div id="menu">
        <p class="welcome">You are now chatting with <b><?php echo $_SESSION['friend']; ?></b></p>
        <p class="logout"><a id="exit" href="#">Logout</a></p>
        <p class="logout"><a id="clear" href="#">Clear History</a></p>
        <div style="clear:both"></div>
        </div>

        <div id="chatbox"></div>
      
      <form name="message" action="">
      <input name="usermsg" type="text" id="usermsg" size="63" />
      <input name="submitmsg" type="submit"  id="submitmsg" value="Send" />
      </form>
      </div>

      <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3/jquery.min.js">
      </script>

      <script type="text/javascript">

      $(document).ready(function()
      {

        $("#exit").click(function()
        {
         var exit = confirm("Are you sure you want to end the session?");
         if(exit==true)
          {
            window.location = 'index.php?logout=true';
          }
       });

       /*
        * User can clear the chat history.
        */
       $("#clear").click(function()
       {
           var exit = confirm("Are you sure you want to clear the chat history?");
           if(exit==true)
            {
              window.location = 'index.php?clearChat=true';
            }
       });

        /*
         * When the user clicks the send button to enter the chat message,
         * the chat message is posted by calling the post.php  
         */
        $("#submitmsg").click(function(){
          var clientmsg = $("#usermsg").val();
          $.post("post.php", {text: clientmsg});
          $("#usermsg").attr("value", "");
          return false;
        });
         
        /*
         * Calls the loadlog function every 10 millisecong to load the 
         * contents from the file which has the chat history to the chat box  
         */
        setInterval (loadLog, 10);
                                    
        /*
         * Loads the chat message in the chat box  
         */
        function loadLog(){
            var oldscrollHeight = $("#chatbox").attr("scrollHeight") - 20;

            $.ajax({ url: "<?php echo $_SESSION['name']; ?>_<?php echo $_SESSION['friend'];?>.html",
                     cache: false,
                     success: function(html){
                        $("#chatbox").html(html);
                        var newscrollHeight = $("#chatbox").attr("scrollHeight") - 20;
                        if(newscrollHeight != oldscrollHeight){
                            $("#chatbox").animate({ scrollTop: newscrollHeight }, 'normal'); 
                        }
                     },
            });
        }
      });
      </script>
      <?php
      }
      ?>


    </body>
  </html>
