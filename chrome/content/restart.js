var restartFirefox = {
// main restart logic
	ourRestart : function() {
		nsIAppStartup = Components.interfaces.nsIAppStartup;
		Components.classes["@mozilla.org/toolkit/app-startup;1"].getService(nsIAppStartup)
			.quit(nsIAppStartup.eRestart | nsIAppStartup.eAttemptQuit);
	}
};

function isEmpty(obj) {
    for(var prop in obj) {
        if(obj.hasOwnProperty(prop))
            return false;
    }

    return true;
};

function switchproxy(proxy){
	var prefs = Components.classes["@mozilla.org/preferences-service;1"] .getService(Components.interfaces.nsIPrefBranch);
	prefs.setCharPref("network.proxy.socks",'');
	prefs.setIntPref("network.proxy.socks_port",0);
	prefs.setCharPref("network.proxy.ssl",'');
	prefs.setIntPref("network.proxy.ssl_port",0);
	prefs.setCharPref("network.proxy.ftp",'');
	prefs.setIntPref("network.proxy.ftp_port",0);
	if(proxy==="")
	{
		//do not use any type of proxy
		prefs.setIntPref("network.proxy.type", 0); 
	}
	else{
		ss=proxy.split(":")
		ip=ss[0];
		port=ss[1];
		prefs.setIntPref("network.proxy.type", 1); 
		prefs.setCharPref("network.proxy.http", ip);
		prefs.setIntPref("network.proxy.http_port", port); 
		prefs.setCharPref("javascript.enabled", "true");
	}
};

function changeProxy(city){
	//alert(city);
	$.post("http://10.5.20.62:8080/HttpProxyServer/proxyservlet",{location:city},function(data){
		alert(data);
		//var jsonData = JSON.parse(data);
		//alert(jsonData);
		//switchproxy(jsonData.proxy);
		switchproxy(data.proxy);
	},"json");
};

function refreshproxy(){
	//alert("refresh");
	var selected_city = $("radio[selected='true']").attr('label');
	if(!isEmpty(selected_city)){
		//alert(selected_city);
		changProxy(selected_city);
	}
};

$(document).ready(function(){
	switchproxy("");
	$('radio').attr('disabled','true');
	$('radio[label="不使用代理"]').attr('disabled','false');
	$.get("http://10.5.20.62:8080/HttpProxyServer/proxyservlet",function(data){
		var jsonData = JSON.parse(data);
		var cities = jsonData.cities.split(",");
		for (var i =0; i< cities.length; i++) 
		{
			city=cities[i];
			$('radio[label="'+city+'"]').attr('disabled','false');
		}
	},"html");
});