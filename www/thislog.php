<html> 
<head>
<title>Log Web Page Under Construction</title>
</head>

<body>

<pre>
<?php 
$logfile=$_GET['lfile'];

$file=file_get_contents($logfile);
echo $file;
 ?>
</pre>
</body>
</html>

