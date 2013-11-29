var selected_city="";

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

function resetproxy() {
	var prefs = Components.classes["@mozilla.org/preferences-service;1"] .getService(Components.interfaces.nsIPrefBranch);
	prefs.setIntPref("network.proxy.type", 0); 
	prefs.setCharPref("network.proxy.socks",'');
	prefs.setIntPref("network.proxy.socks_port",0);
	prefs.setCharPref("network.proxy.ssl",'');
	prefs.setIntPref("network.proxy.ssl_port",0);
	prefs.setCharPref("network.proxy.ftp",'');
	prefs.setIntPref("network.proxy.ftp_port",0);
};

function switchproxy(proxy){
	var prefs = Components.classes["@mozilla.org/preferences-service;1"] .getService(Components.interfaces.nsIPrefBranch);
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
	selected_city = city;
	resetproxy();
	$.post("http://10.5.20.62:8080/HttpProxyServer/proxyservlet",{location:city},function(data){
		//alert(data.proxy);
		switchproxy(data.proxy);
	},"json");
};

function refreshproxy(){
	changeProxy(selected_city);
};

$(document).ready(function(){
	$('radio').attr('disabled','true');
	$('radio[label="不使用代理"]').attr('disabled','false');
	resetproxy();
	var colums = 20;
	$.get("http://10.5.20.62:8080/HttpProxyServer/proxyservlet",function(data){
		var jsonData = JSON.parse(data);
		var cities = jsonData.cities.split(",");
		for (var j = 0; j < Math.ceil(cities.length/colums); j++) 
		{
			var new_row = "<row name='proxyrow"+j+"'>";
			for (var i = 0; i <colums && (j*colums+i < cities.length); i++) 
			{
				city = cities[j*colums+i];
				new_row = new_row + "<radio type='radio' label='"+city+"' oncommand='changeProxy(this.label)'/>";
			}
			new_row = new_row+"</row>";
			$("rows[name='all_proxy']").append($(new_row));			 
		}
	},"html");
});