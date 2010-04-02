<?xml version="1.0" encoding="UTF-8"?>
<md:mspec xmlns:md="http://www.eclipse.org/buckminster/MetaData-1.0" 
    name="org.eclipse.ecf.call.sip" 
    materializer="p2" 
    url="org.eclipse.ecf.call.sip.cquery">
    
    <md:mspecNode namePattern="^org\.eclipse\.ecf\.provider\.call(\..+)?" materializer="workspace"/>
    <md:mspecNode namePattern="^org\.eclipse\.ecf\.provider\.fmj?" materializer="workspace"/>

    <md:mspecNode namePattern=".*" installLocation="${targetPlatformPath}"/>
</md:mspec>
	
