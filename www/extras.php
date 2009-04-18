<!doctype html public "-//w3c//dtd html 4.0 transitional//en">
<html>
<head>
<title>eclipse communication framework dailies</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="default_style.css" type="text/css">
</head>
<body text="#000000" bgcolor="#ffffff" link="#0000ee" vlink="#551a8b" alink="#ff0000"><table width=100% BORDER=0 CELLPADDING=2 CELLSPACING=5 STYLE="page-break-before: always">
<h1>ECF Extra Released Downloads (zips)</h1>
<div>
<p>
</p>
<?php
    function directoryToArray($directory, $recursive) {
      $array_items = array();
      if ($handle = opendir($directory)) {
        while (false !== ($file = readdir($handle))) {
          if ($file != "." && $file != "..") {
            $array_items[] = preg_replace("/\/\//si", "/", $file);
          }
        }
      }
      closedir($handle);
      rsort($array_items,SORT_STRING);
      return $array_items;
    }

    $files=directoryToArray("/var/www/localhost/htdocs/OSUrelease",false);

    foreach ($files as $file) {
       echo '<tr> <td> <p><a href="http://ecf1.osuosl.org/OSUrelease/' . $file . '">' . $file .'</a></p></td></tr>';
    }
?>
</div>
</body>
</html>
