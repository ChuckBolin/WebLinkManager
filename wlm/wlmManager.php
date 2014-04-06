<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<! -- wlmManager.php
      Written by Mush4Brains, April 2014 -->
      
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<link rel="shortcut icon" href="http://mush4brains.com/favicon.ico" >
<title>Web Link Manager</title>

</head>

<body>
<center><img src="http://www.mush4brains.com/images/mushbrainsbanner.jpg"></center><br />
<h1>Enter a New Web Link</h1>

<form name="weblinkinput" action="wlmAddLink.php" onsubmit="return validateForm();" method="post">
  Description: <input type="text" name="description" size="50" /><br />
  Link:       <input type="text" name="link" size="128" /><br />
  <br />
  <input type="reset" value="Reset" />
  <input type="submit" name="submit" value="Submit">
</form><br /><hr />

<script>
  function validateForm(){
    var x=document.forms["weblinkinput"]["description"].value;
    if (x==null || x==""){
      alert('Enter a description!');
      return false;
    }
    var y=document.forms["weblinkinput"]["link"].value;
    if (y==null || y==""){
      alert('Enter a link:');
      return false;
    }    

    var sRet = y.match(/www./i);
    if(!sRet){
      alert('Link must be a URL!');
      return false;
    }
  }  

  
</script>

</body>
</html>
