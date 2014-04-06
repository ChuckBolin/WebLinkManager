<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>Add Web Link</title>
</head>
<body>

<?php  
  require "dbconnect.php";
  print('<h1>Add Web Link</h1>');
 
  $con = mysql_connect($sHostName,$sUserName,$sPassword);
  if (!$con)  {
    die('Could not connect: ' . mysql_error());
  }
  mysql_select_db($sDbName, $con);
  
  $sql="INSERT INTO $sDbName.tWebLinks(description, link) 
        VALUES('$_POST[description]','$_POST[link]')";  

  if (!mysql_query($sql,$con)){
    die('Error: ' . mysql_error());
  }
  echo "1 record added";
  
  mysql_close($con);	 

?>
<br />


<a href="wlmManager.php">Return to Web Link Manager</a><br />
</body>
</html>
