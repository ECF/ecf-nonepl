<!doctype html public "-//w3c//dtd html 4.0 transitional//en">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title>eclipse communication framework project</title>
<link rel="stylesheet" href="default_style.css" type="text/css">
</head>
<body text="#000000" bgcolor="#ffffff" link="#0000ee" vlink="#551a8b" alink="#ff0000"><table width=100% BORDER=0 CELLPADDING=2 CELLSPACING=5 STYLE="page-break-before: always">
<table border=0 cellspacing=5 cellpadding=2 width="100%">
  <tbody>
    <tr>
      <td width="69%" class="bannertext">
        <font class="indextop style2">eclipse communication framework<br>project server @ Oregon State University Open Source Lab </font>
        <br><br>
      </td>
      <td width="31%">
        <div align="center">
          <img src="images/Idea.jpg" width="120" height="86" hspace="50" align="middle">
        </div>
      </td>
    </tr>
  </tbody>
</table>
<table width=1207 BORDER=0 CELLPADDING=2 CELLSPACING=0>
	<COL width=10>
	<COL width=35>
	<COL width=381>
	<COL width=722>
	<COL width=0>
	<COL width=36>
	<tr>
		<td colspan=4 width=500 valign=top bgcolor="#0080c0">
			<p align=left><font color="#ffffff">&nbsp;<b><font face="Arial, Helvetica">
			About the ECF Project</font></b></font>
			</p>
		</td>
	</tr>
	<tr>
		<td colspan=4 width=500 valign=top bgcolor="#ffffff">
		See the <a href="http://www.eclipse.org/ecf">ECF Home Page</a>
		</td>
	</tr>
	<tr>
		<td colspan=6 width=1203 valign=top bgcolor="#0080c0">
			<p align=left><font COLOR="#ffffff">&nbsp;<b><font FACE="Arial, Helvetica">
			ECF Extras via Eclipse Update Manager</font></b></font></p>
		</td>
	</tr>
	<tr>
		<td colspan=6 width=1203>
			<p><b>Directions to configure Eclipse for ECF JAR file updates</b></p>
		</td>
	</tr>
	<tr>
		<td colspan=6 width=1203>
			<p>&nbsp;&nbsp;In Eclipse, go to <b>Help</b> menu <b>&gt; Help
			Contents &gt; Workbook User Guide</b></p>
		</td>
	</tr>
	<tr>
		<td colspan=6 width=1203>
			<UL>
				<LI><P STYLE="margin-bottom: 0in">In Eclipse, choose Help -&gt;
				Software Updates -&gt; Find and Install -&gt; Search for new
				features to install -&gt; Next -&gt; New Remote Site
				</p>
				<UL>
					<LI><P STYLE="margin-bottom: 0in">In the <b>New Update Site</b>
					dialog:</p>
					<UL>
						<LI><P STYLE="margin-bottom: 0in">&quot;Name:&quot; enter: ECF
						updates
						</p>
						<LI><P STYLE="margin-bottom: 0in">&quot;URL&quot; enter:
						http://ecf1.osuosl.org/update
						</p>
					</UL>
					<p>As shown here:<BR><img src="images/new-update-site.jpg" name="Graphic24" align=bottom width=367 height=167 BORDER=0>
										</p>
				</UL>
			</UL>
		</td>
	</tr>
		<tr>
		<td colspan=6 width=1203 valign=top bgcolor="#0080c0">
			<P align=left><font COLOR="#ffffff">&nbsp;<font FACE="Arial, Helvetica"><b>ECF Extra Application Downloads</b> </font></font>
			</p>
		</td>
	</tr>
	<tr>
		<td colspan=6 width=1203></td>
	</tr>
	<tr>
        <td>
			<p><a href="../extras/org.eclipse.ecf.provider.jms-1.1.0.v20070911-1136.zip"><b>org.eclipse.ecf.provider.jms-1.1.0.v20070911-1136.zip</b></a>
						</p>
		</td>
	</tr>
	<tr>
		<td >
			<p><a href="../extras/org.eclipse.ecf.provider.yahoo-1.1.0.v20070911-1136.zip"><b>org.eclipse.ecf.provider.yahoo-1.1.0.v20070911-1136.zip</b></a>
						</p>
		</td>
	</tr>
    <tr>
         <td>
			 <p><a href="../extras/org.eclipse.ecf.provider.skype-1.1.0.v20070911-1136.zip"><b>org.eclipse.ecf.provider.skype-1.1.0.v20070911-1136.zip</b></a>
								                                                </p>
	     </td>
	</tr>
    <tr>
         <td>
			 <p>
				<a href="../extras/org.eclipse.ecf.provider.jgroups-1.1.0.v20070911-1136.zip"><b>org.eclipse.ecf.provider.jgroups-1.1.0.v20070911-1136.zip</b></a>
    </p>
	     </td>
	</tr>
	<tr>
		<td colspan=6 width=1203></td>
	</tr>
    <tr>
		<td colspan=6 width=1203 valign=top bgcolor="#0080c0">
			<P align=left><font COLOR="#ffffff">&nbsp;<font FACE="Arial, Helvetica"><b>ECF Extra Daily Application Downloads</b> </font></font>
			</p>
		</td>
	</tr>
	<tr>
		<td colspan=6 width=1203></td>
	</tr>
		<tr>
			 <td>
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

											  $files=directoryToArray("/var/www/localhost/htdocs/dailies",false);

											  foreach ($files as $file) {
											          echo '<tr> <td> <p><a href="http://ecf1.osuosl.org/dailies/' . $file . '">' . $file .'</a></p></td></tr>';
												  }
												  ?>

	     </td>
	<tr>
		<td colspan=6 width=1203></td>
	</tr>
	<tr>
		<td colspan=6 width=1203 valign=top bgcolor="#0080c0">
			<P align=left><font COLOR="#ffffff">&nbsp;<font FACE="Arial, Helvetica"><b>Anonymous
			CVS access to ECF Extras Source</b> </font></font>
			</p>
		</td>
	</tr>
	<tr>
		<td colspan=6 width=1203>
			<H4>To load ECF source code into your workspace download and use
			the project set file</H4>
			<p>Save one of the below project set files to local disk by
			choosing File -&gt; Save Link As... in your browser</p>
			<UL>
				<LI><P STYLE="margin-bottom: 0in"><a href="ecf1-anonymous.psf">ecf1-anonymous.psf</a>
								</p>
				<P STYLE="margin-bottom: 0in"></p>
			</UL>
			<OL>
				<p>Instructions for Using Project Set Files</p>
				<LI><P STYLE="margin-bottom: 0in">Download/Save Link As... the
				desired project set file to local disk
				</p>
				<LI><p>Within Eclipse, choose <b>File -&gt; Import -&gt; Team
				Project Set</b> and open the .psf file downloaded in step 1
				</p>
			</OL>
			<H5>Anonymous CVS server info</H5>
			<UL>
				<LI><P STYLE="margin-bottom: 0in">Host: <b>ecf1.osuosl.org</b></p>
				<LI><P STYLE="margin-bottom: 0in">Repository Path: <b>/ecf</b></p>
				<LI><P STYLE="margin-bottom: 0in">User: <b>anonymous</b>
				</p>
				<LI><P STYLE="margin-bottom: 0in">Password: &lt;empty&gt;
				</p>
				<LI><P STYLE="margin-bottom: 0in">Connection method: <b>pserver</b>
								</p>
				<p></p>
			</UL>
		</td>
	</tr>
</table>
<p><BR><BR>
</p>
</body>
</html>
