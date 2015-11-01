
var ks = function(e){
	return document.getElementById(e);
}


/**
 * [methodRow 拼接显示一个方法的调用统计]
 * @param  {[type]} accessCount  [调用总数]
 * @param  {[type]} successCount [成功调用数]
 * @param  {[type]} failedCount  [失败调用数]
 * @param  {[type]} avgCost      [平均调用耗时]
 * @param  {[type]} methodName   [方法名]
 * @param  {[type]} argsName     [方法参数]
 * @return {[type]}              [该方法的html]
 */
ks.methodRow = function (accessCount, successCount, failedCount, avgCost, methodName, argsName) {
	var line = '' ;
	var pline = ks.argsName2Line(argsName) ;
	line += "<td id='accessCount'>"+accessCount+"</td>" ;
	line += "<td id='successCount'>"+successCount+"</td>" ;
	line += "<td id='failedCount'>"+failedCount+"</td>" ;
	line += "<td id='avgCost(ms)'>"+avgCost+"</td>" ;
	line += "<td><button type='submit' class='btn' onclick='ks.showMethodInvoke(\""+methodName+"\", \""+pline+"\");'>调用</button></td>" ;
	return line ;
}


/**
 * [showMethodInvoke 显示方法调用面板]
 * @param  {[type]} methodName [方法名]
 * @param  {[type]} argsName   [参数名]
 * @return {void}
 */
ks.showMethodInvoke = function (methodName, argsName) {
    var pnames = ks.argsName2Array(argsName) ;

	//alert(methodName) ;
    var methodTitle = methodName +'(' ;
    var html = '' ;
    if(pnames)
    {
      for(var i in pnames)
      {
        var aname = pnames[i] ;
        html += "<div class='control-group'>" ;
        html += "<label class='control-label' for='p"+aname+"'>"+aname+"</label>";
        html += "<div class='controls'><input type='text' id='p"+aname+"'></div>" ;
        html += "</div>" ;
        if(i!=0) {
        	methodTitle +=',' ;
        }
        methodTitle +=aname ;
      }
    }
    methodTitle += ')' ;
    $("#myModalLabel").text(methodTitle) ;
    $('#parameter-div').html(html) ;
    $("#show_modal").click();
}




/**
 * [invokeMethod 调用方法]
 * @return {void} [无]
 */
ks.invokeMethod = function() {
	text = $("#myModalLabel").text() ;
	var flag = text.indexOf('(') ;
	var methodName = text.substring(0, flag) ;
	var argsName = text.substring(flag+1, text.length-1) ;
	var contextName = xl.P('contextName') ;
	var serviceName = xl.P('serviceName') ;

	//alert('serviceName='+serviceName + ', methodName='+methodName + ', argsName=' + argsName) ;
	var parameters = {} ;
	parameters['contextName'] = contextName ;
	parameters['serviceName'] = serviceName ;
	parameters['methodName'] = methodName ;
    var pnames = ks.argsName2Array(argsName) ;
    for(var i in pnames) {
		var a = pnames[i] ;
		var v = $("#p"+a).val();
		if(v) {
			parameters[a] = v ;
		}
	}

	$.getJSON("invoke.api", parameters,
	function(data, status) {
		var result = ks.checkResp(data) ;
        ks.formatJSON(result, 'result-shower') ;
		//$('#result-shower').text(JSON.stringify(result, " ")) ;//$('#result-shower').text(result) ;
	});
}





/**
 * [formatJSON 格式化JSON并进行展示]
 * @param  {[string]} jsonstr [JSON格式的字符串]
 * @param  {[string]} domid   [展示的容器id]
 * @return {[void]}         [无]
 */
ks.formatJSON = function(jsonstr, domid) {
    if(jsonstr && domid) {
        domid = '#' + domid;
        var options = 
        {
            dom: domid,
            isCollapsible: $('#CollapsibleView').prop('checked'),
            quoteKeys: $('#QuoteKeys').prop('checked'),
            tabSize: $('#TabSize').val(),
            imgCollapsed: "assets/js/format-json/images/Collapsed.gif", //收起的图片路径
            imgExpanded: "assets/js/format-json/images/Expanded.gif"  //展开的图片路径
        };
        
        var formatter = new JsonFormater(options);
        formatter.doFormat(jsonstr) ;
    }

}





/**
 * [argsName2Line 将数组拼接为字符串]
 * @param  {[Array]} argsName [参数名数组]
 * @return {[String]}          [字符串]
 */
ks.argsName2Line = function(argsName) {
	var pline = '' ;
	if(argsName && argsName.length>0) {
		for(var i in argsName) {
			if(i!=0) pline +=", " ;
			pline += argsName[i] ;
		}
	}
	return pline ;
}



/**
 * [argsName2Array 将字符串分割成数组]
 * @param  {[type]} argsName [参数名字符串, 以,间隔]
 * @return {[Array]}          [数组]
 */
ks.argsName2Array = function(argsName) {
	var pnames = new Array();
    if(argsName && argsName!='') {
        var array = argsName.split(",");
        for(var i in array) {
            var o = array[i] ;
            o = ks.trim(o) ;
            pnames.push(o) ;
        }
	}
	return pnames ;
}




/**
 * [checkResp 检查响应结果]
 * @param  {[type]} data [description]
 * @return {[type]}      [description]
 */
ks.checkResp = function(data) {
	if(data['code']!=200)
	{
	  alert(data['desc']) ;
	  return false ;
	}
	return data['result'];
}




/**
 * [trim 去除字符串的首尾空格]
 * @param  {[String]} str [字符串]
 * @return {[String]}     [去除字符串的首尾空格]
 */
ks.trim = function(str){
    return str.replace(/(^\s*)|(\s*$)/g,"");
}




/**
 * [buildNavbar 重新构建导航栏]
 * @return {[void]} [无]
 */
ks.rebuildNavbar = function()
{
    var container = $('#admin-nav-container') ;
    if(container)
    {
        var urlstr = window.location.href ;
        var pagename = "" ; var begin = 0 ; var end = urlstr.indexOf("?") ;
        if(end &&end>0) {
            begin = urlstr.lastIndexOf("/", end);
            if(begin && end>begin) {
                pagename = urlstr.substring(begin+1, end) ;
            }
        }

        //1. admin    
        var line = "<ul class='nav'>" ;
        line += "<li><a href='/admin/'>Home</a></li>" ;

        var match ;
        if("services.html" == pagename)
        {
            var contextName = xl.P("contextName") ;
            line += "<li><a href='/admin/contexts.html?contextName="+contextName+"'>Context</a></li>" ;
            line += "<li class='active'><a>Service</a></li>" ;
            line += "<li><a>Method</a></li>" ;
            match = 1 ;
        }
        else if("methods.html" == pagename)
        {
            var contextName = xl.P("contextName") ; var serviceName = xl.P("serviceName") ;
            line += "<li><a href='/admin/contexts.html?contextName="+contextName+"'>Context</a></li>" ;
            line += "<li><a href='/admin/services.html?contextName="+contextName+"&serviceName="+serviceName+"'>Context</a></li>" ;
            line += "<li class='active'><a>Method</a></li>" ;
            match = 1 ;
        }

        if(match) {
            $('#admin-nav-container').html(line) ;
        }
    }



}