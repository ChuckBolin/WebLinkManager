<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<! -- vlinks.php
      Written by Mush4Brains, March 2013 -->
      
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<link rel="shortcut icon" href="http://mush4brains.com/favicon.ico" >
<title>Video Links</title>

</head>

<body>
<center><img src="http://www.mush4brains.com/images/mushbrainsbanner.jpg"></center><br />
<h1>Enter a New Video Link</h1>

<form name="iuAddVL" action="ixAddVL.php" onsubmit="return validateForm();" method="post">
  Video Type(e.g. Music, Instructional):<br />
  <input type="text" name="type" size="15" value="Music" /><br />
  Video Name: <input type="text" name="name" size="50" /><br />
  Link:       <input type="text" name="link" size="128" /><br />
  Tags:       <input type="text" name="tags" size="128" /><br />  
  <br />
  <input type="reset" value="Reset" />
  <input type="submit" name="submit" value="Submit">
</form><br /><hr />

<h1>Search Video Links</h1>

<form name="ouSearchVL" action="oxSearchVLM.php" onsubmit="return validateForm2();" method="post">
  Enter Keywords:<br />
  <input type="text" name="searchstring" size="64" value="" /><br />
  <br />
  <input type="reset" value="Reset" />
  <input type="submit" name="submit" value="Submit">
</form>


<script>
  function validateForm(){
    var x=document.forms["iuAddVL"]["name"].value;
    if (x==null || x==""){
      alert('Enter a name!');
      return false;
    }
    var y=document.forms["iuAddVL"]["type"].value;
    if (y==null || y==""){
      alert('Enter a type:');
      return false;
    }    
    var z=document.forms["iuAddVL"]["link"].value;
    if (z==null || z==""){
      alert('Enter a link!');
      return false;
    }
    var sRet = z.match(/www./i);
    if(!sRet){
      alert('Link must be a URL!');
      return false;
    }
  }  

  function validateForm2(){
    var x=document.forms["ouSearchVL"]["searchstring"].value;
    if (x==null || x==""){
      alert('Enter a search string!');
      return false;
    } 
  }    
</script>

</body>
</html>
