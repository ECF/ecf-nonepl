<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Log Web Page </title>
<meta http-equiv="Content-Type"
  content="text/html; charset=iso-8859-1" />
</head>
<body>
<h1>ECF Build</h1>
<div>
<p>
When completed, this page will display statistics about the build.
</p>
<p>
You can get the latest DAILY builds at
</p>
<p><a href="http://ecf1.osuosl.org">http://ecf1.osuosl.org</a></p>
</p>

<p>
Daily builds occur whether or not the the repository has been updated.
The Daily build occurs at 4PM PDT.
</p>

<p>
The Auto build occurs every 45 minutes if the repository has been updated):
These Auto builds are not saved,
but the mailing list ecf-build gets notified when they occur and if they are successful or not.
</p>

<p>
The OSU Released build occurs on demand.
</p>

<p>
To subscribe to ecf-build, go to
<a href="http://dev.eclipse.org/mailman/listinfo/ecf-build">http://dev.eclipse.org/mailman/listinfo/ecf-build</a>
</p>
<p>


The latest OSU build is ...
</p>
</div>
<p> </p>
<?php
echo $_GET['log'];
$logxml=$_GET['log'];
$logxmlArray = explode("L",$logxml);
$logtime = substr($logxmlArray[0],3,12);
$logtime0= substr($logxmlArray[0],15,16);

$s1 = substr($logtime,0,8);
$s2 = substr($logtime,8,4);
$is2 = (int)$s2;

if ((int)$logtime0 >=50) { $is2++; }
$s2 = strval($is2);
$s3 = "ls /opt/build.ecf/logs/"."*".$s1."-".$s2."*";

exec($s3,$resultLS);
   $s4 = $resultLS[0];

$s5 = "http://ecf1.osuosl.org/thislog.php?lfile=".$s4;
?>
<p>
Its log file is
<a href=
<?php echo $s5 ?>
>Get Log.</a></p>
<p>
We don't save all the logs. So you may get a "file not found." We have a cron that deletes old
logs. So if you open up an email from ecf-build that is a week or so old, the log file may not be there.
It'll be there for recent builds.
</p>

</body>
</html>

