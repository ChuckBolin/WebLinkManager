<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>Video Links Search Results</title>
<style>

#vlinktable{
  border: 1px solid black;
  color: rgb(242,240,140);
  background-color: rgb(48,177,155);
  border-color: rgb(255,255,255);  
  width: 1000 px;
  font-family: Arial;
  font-size: 14px;
  font_weight: normal;
  table-layout:fixed;
}

td{
  width: 250 px;
  overflow: hidden;
  border: 1px solid black;
}
tr{
  border: 1px solid black;
}
</style>
</head>

<body>
<?php
  require "dbconnect.php";
  $con = mysql_connect($sHostName,$sUserName,$sPassword);
  if (!$con)  {
    die('Could not connect: ' . mysql_error());
    }
  mysql_select_db($sDbName, $con);
  $sql="SELECT * FROM $sDbName.tblVLinks";
  $result = mysql_query($sql);
  $num = mysql_numrows($result);
  $preImage = "http://i4.ytimg.com/vi/";
  $postImage = "/default.jpg";
  //mysql_close($con);
  $pos = 0;
  $rowCount = 0;

  $tokens = explode(" ", $_POST[searchstring]); //tokens is an array of terms from searchstring

  print('<h1>Mush4Brains Video Link Search Results for : ');      
  for($i = 0; $i < count($tokens); $i++){
    print($tokens[$i].' ');
  }
  print('</h1>');
?>

<?php
//http://forrst.com/posts/Grab_Youtube_or_Vimeo_Info_with_PHP-0el
  function video_info($url) {

  // Handle Youtube
  if (strpos($url, "youtube.com")) {
      $url = parse_url($url);
      $vid = parse_str($url['query'], $output);
      $video_id = $output['v'];
      $data['video_type'] = 'youtube';
      $data['video_id'] = $video_id;
      $xml = simplexml_load_file("http://gdata.youtube.com/feeds/api/videos?q=$video_id");

      foreach ($xml->entry as $entry) {
          // get nodes in media: namespace
          $media = $entry->children('http://search.yahoo.com/mrss/');
          
          // get video player URL
          $attrs = $media->group->player->attributes();
          $watch = $attrs['url']; 
          
          // get video thumbnail
          $data['thumb_1'] = $media->group->thumbnail[0]->attributes(); // Thumbnail 1
          $data['thumb_2'] = $media->group->thumbnail[1]->attributes(); // Thumbnail 2
          $data['thumb_3'] = $media->group->thumbnail[2]->attributes(); // Thumbnail 3
          $data['thumb_large'] = $media->group->thumbnail[3]->attributes(); // Large thumbnail
          $data['tags'] = $media->group->keywords; // Video Tags
          $data['cat'] = $media->group->category; // Video category
          $attrs = $media->group->thumbnail[0]->attributes();
          $thumbnail = $attrs['url']; 
          
          // get <yt:duration> node for video length
          $yt = $media->children('http://gdata.youtube.com/schemas/2007');
          $attrs = $yt->duration->attributes();
          $data['duration'] = $attrs['seconds'];
          
          // get <yt:stats> node for viewer statistics
          $yt = $entry->children('http://gdata.youtube.com/schemas/2007');
          $attrs = $yt->statistics->attributes();
          if(is_array($attrs)){
            $data['views'] = $attrs['viewCount']; //$viewCount =, removed this for existing code
          }  
          $data['title']=$entry->title;
          $data['info']=$entry->content;
          
          //my mod not working
          /*
          $attrs = $yt->rating->attributes();   
          if($yt->rating->attributes()){
            $data['like'] = $attrs['numLikes'];
            $data['dislike'] = $attrs['numDislikes'];          
          }
          */
          
          // get <gd:rating> node for video ratings
          $gd = $entry->children('http://schemas.google.com/g/2005'); 
          if ($gd->rating) {
              $attrs = $gd->rating->attributes();
              $data['rating'] = $attrs['average']; 
          } else { $data['rating'] = 0;}
      } // End foreach
  } // End Youtube

  // Set false if invalid URL
  else { $data = false; }

  return $data;

  }
?>
<?php

  function GetYoutubeID($url){
    $hash = "";
    $pos = stripos($url,"&v=");
    if($pos > -1){
      $hash = substr($url,$pos+3,11);
      return $hash;
    }
    else{
      return "";
    }
  }
?>
<a href="vlinks.php">Return to Video Link Search</a>
<form name="form1" method="post" action="">
<?php
  $newrow = 0;
  print('<table id="vlinktable">');
  print('<col width="250px" />');
  print('<col width="250px" />');
  print('<col width="250px" />');
  print('<col width="250px" />');
  if($result){
    while($row = mysql_fetch_array($result)){
      if(substr($row[2], -26) == "&feature=player_detailpage"){
        $hash = substr($row[2],-37,11); 
      }
      elseif(substr($row[2], -15) == "&mode=transport"){
        $hash = substr($row[2],-26,11); 
      }
      else{
        $hash = substr($row[2],-11); 
      }    
      $p1 = strtoupper($_POST[searchstring]);
      
      //searching for 1 or more terms
      if($p1 != "DUMP" && $p1 != "*"){
        for($i = 0; $i < count($tokens); $i++){
          if(stripos($row[1], (string)$tokens[$i]) > -1 || stripos($row[4], (string)$tokens[$i]) > -1){          
            $rowCount++;    
            if($newrow == 0){
              print('<tr>');
            }      
            print('<td>');
            print($rowCount.'     '.'<input name="chk['.$row[0].']" type="checkbox" id="chk['.$row[0].']" value="chk['.$row[0].']"  >'.'<i>Check to Delete</i><br />'); 

            //print stats
            $video = video_info($row[2]);
            if($video){
              print("<b>".$video['title']."</b><br />");
              print('Views: '.number_format($video['views'],0,'.',',')."<br />");
              print('Duration: '.$video['duration']."s<br />");
              print('Rating: '.number_format($video['rating'],2));
            }

            print('<br />');
            $row[2] = str_ireplace ("youtube.com","youtubeskip.com",$row[2]);
            //$thumb = $video['thumb_3'];
            print('<br /><a href='.$row[2].' target=\"_BLANK\"><img width="240" height="180" src="'.$preImage.$hash.$postImage.'"></a></td>');//<br /><td>
            //print('<br /><a href='.$row[2].' target=\"_BLANK\"><img src="'.$thumb.'"></a></td>');//<br /><td>
            if($newrow == 3){
              print('</tr>');    
              $newrow = 0;
            }  
            else{
              $newrow++;
            }
          
            break;        
          }//if      
        }//for
      }
      //dump all vlinks, DUMP or *
      //**********************************************************
      else{
            $rowCount++;    
            if($newrow == 0){
              print('<tr>');
            }      
            print('<td>');
            print($rowCount.'     '.'<input name="chk['.$row[0].']" type="checkbox" id="chk['.$row[0].']" value="chk['.$row[0].']"  >'.'<i>Check to Delete</i><br />'); 
            
            
            //print stats
            $video = video_info($row[2]);
            if($video){
              print("<b>".$video['title']."</b><br />");
              print('Views: '.number_format($video['views'],0,'.',',')."<br />");
              print('Duration: '.$video['duration']."s<br />");
              print('Rating: '.number_format($video['rating'],2));
            }
       
            print('<br />');
            $row[2] = str_ireplace ("youtube.com","youtubeskip.com",$row[2]);
            print('<br /><a href='.$row[2].' target=\"_BLANK\"><img width="240" height="180" src="'.$preImage.$hash.$postImage.'"></a></td>');//<br /><td>

            //print('<br /><a href='.$row[2].' target=\"_BLANK\"><img src="'.$preImage.$hash.$postImage.'"></a></td>');//<br /><td>
            if($newrow == 3){
              print('</tr>');    
              $newrow = 0;
            }  
            else{
              $newrow++;
            }


      }//else      
    }
  } 
  print('</table>');
  
?>
<br />
<input name="delete" type="submit" id="delete" value="Delete"><br />
</form>
<!-- <a href="ouSearchVLM.php">Return to Video Link Search</a><br /> -->
<a href="vlinks.php">Return to Video Link Search</a><br />

<?php
  // Check if delete button active, start this
  if (!empty($_POST['delete'])) {
      foreach ($_POST['chk'] as $id => $value) {
          $sql = 'DELETE FROM `'.$sDbName.'`'.'.`'.tblVLinks.'` WHERE `'.tblVLinks.'`.`id` = '.(int)$id;
          echo($sql);
          mysql_query($sql);
      }
    mysql_close($con); 
    
    header("Location: vlinks.php");
    exit;      
  }
?>
  
</body>
</html>
