<?php
	if(!isset($_POST['password']))
		die("Password not sent");
	if(!isset($_FILES['screenshot']))
		die("File not sent");
		
	$pass = $_POST['password'];
	$screenshot = $_FILES['screenshot'];
	
	if(isset($pass) && $pass == "CHANGEME") { // hard-coded password
		if($screenshot['type'] == "image/png") {
			$name = $screenshot["name"];
			$uploads_dest = __DIR__ . '/' . $name;
			$tmp_name = $screenshot["tmp_name"];
			
			move_uploaded_file($tmp_name, $uploads_dest);
		}
	}
?>