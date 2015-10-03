<?php
	/*
	 * Creates a new session token when a friend is selected 
	*/
	session_start();
	if(isset($_SESSION['name']))
	{
		$frndname = $_POST['friendname'];
	   	$_SESSION['friend'] =stripslashes(htmlspecialchars($frndname));
	}
?>