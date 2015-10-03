<?php
/* 
 * The chat message entered ny the user is loaded in to a file.
 * This is to store the chat history. 
*/
session_start();
date_default_timezone_set('America/Los_Angeles');
if(isset($_SESSION['name'])){
	$filename1 = $_SESSION['name']."_".$_SESSION['friend'].".html";
    $filename2 = $_SESSION['friend']."_".$_SESSION['name'].".html";
   	$text = $_POST['text'];

   	$fp1 = fopen($filename1, 'a');
   	$fp2 = fopen($filename2, 'a');
   fwrite($fp1, "<div class='msgln'>(".date("h:i A").") <b>".$_SESSION['name']."</b>: ".stripslashes(htmlspecialchars($text))."<br></div>");
   fclose($fp1);
   fwrite($fp2, "<div class='msgln'>(".date("h:i A").") <b>".$_SESSION['name']."</b>: ".stripslashes(htmlspecialchars($text))."<br></div>");
   fclose($fp2);
}
?>
