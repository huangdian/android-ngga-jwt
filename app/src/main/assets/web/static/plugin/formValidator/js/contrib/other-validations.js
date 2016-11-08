/*
 This file contains validations that are too specific to be part of the core
 Please reference the file AFTER the translation file or the rules will be overwritten
 Use at your own risk. We can't provide support for most of the validations
*/
(function($){
	if($.validationEngineLanguage == undefined || $.validationEngineLanguage.allRules == undefined )
		alert("Please include other-validations.js AFTER the translation file");
	else {
		$.validationEngineLanguage.allRules["postcodeUK"] = {
		        // UK zip codes
		        "regex": /^([A-PR-UWYZ0-9][A-HK-Y0-9][AEHMNPRTVXY0-9]?[ABEHMNPRVWXY0-9]? {1,2}[0-9][ABD-HJLN-UW-Z]{2}|GIR 0AA)$/,
				"alertText": "* Invalid postcode"
		};
		$.validationEngineLanguage.allRules["postcodeUS"] = {
		        // US zip codes | Accepts 12345 and 12345-1234 format zipcodes
                "regex": /^\d{5}(-\d{4})?$/,
                "alertText": "* Invalid zipcode"
		};
		$.validationEngineLanguage.allRules["postcodeDE"] = {
		        // Germany zip codes | Accepts 12345 format zipcodes
                "regex": /^\d{5}?$/,
                "alertText": "* Invalid zipcode"
		};
		$.validationEngineLanguage.allRules["postcodeAT"] = {
		        // Austrian zip codes | Accepts 1234 format zipcodes
                "regex": /^\d{4}?$/,
                "alertText": "* Invalid zipcode"
		};
    $.validationEngineLanguage.allRules["postcodeJP"] = {
      // JP zip codes | Accepts 123 and 123-1234 format zipcodes
      "regex": /^\d{3}(-\d{4})?$/,
      "alertText": "* 郵便番号が正しくありません"
    };
		$.validationEngineLanguage.allRules["onlyLetNumSpec"] = {
				// Good for database fields
				"regex": /^[0-9a-zA-Z_-]+$/,
				"alertText": "* Only Letters, Numbers, hyphen(-) and underscore(_) allowed"
		};
		$.validationEngineLanguage.allRules["cnmobilePhone"] = {
				// Good for database fields
				"regex": "^13[0-9]{9}|15[012356789][0-9]{8}|18[0256789][0-9]{8}|147[0-9]{8}$",
				"alertText": "* 请输入正确的手机号"
		};
	//	# more validations may be added after this point
	}
})(jQuery);

var validate=new Object();
var aCity={11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江",31:"上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",42:"湖北",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",54:"西藏",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外"}; 
validate.isCardID=function(field, rules, i, options){ 
	var sId=field.val();
	var iSum=0 ;
	if(!/^\d{17}(\d|X|)$/i.test(sId)) return "你输入的身份证长度或格式错误"; 
	sId=sId.replace(/X$/i,"a"); 
	if(aCity[parseInt(sId.substr(0,2))]==null) return "你的身份证地区非法"; 
	sBirthday=sId.substr(6,4)+"-"+Number(sId.substr(10,2))+"-"+Number(sId.substr(12,2)); 
	var d=new Date(sBirthday.replace(/-/g,"/")) ;
	if(sBirthday!=(d.getFullYear()+"-"+ (d.getMonth()+1) + "-" + d.getDate()))return "身份证上的出生日期非法"; 
	for(var i = 17;i>=0;i --) iSum += (Math.pow(2,i) % 11) * parseInt(sId.charAt(17 - i),11) ;
	if(iSum%11!=1) return "你输入的身份证号非法"; 
	//return true;//aCity[parseInt(sId.substr(0,2))]+","+sBirthday+","+(sId.substr(16,1)%2?"男":"女") 
} ;
validate.YYYYMM=function(field, rules, i, options){ 
	var month=field.val();
	var reg=/^[1-9](\d{3})(0[1-9]|1[0-2])$/
	if(!reg.test(month)){
		return "请按格式输入yyyymm"
	}
};

validate.ajaxValidate=function(field, rules, i, options){
	var ajaxLastCheck=field.attr('ajaxLastCheck');
	if(ajaxLastCheck&&options.isError){
		return;
	}
	var action=field.attr('ajaxAction');
	var key=field.attr('name');
	var value=field.val();
	var param=key+"="+value;
	var f=$.ajax({	url:action,
					data:param,
					dataType:"json",
					async: false
				});
	var correct='<img src="static/img/icons/icon_approve.png"/>';
	var incorrect='<img src="static/img/icons/icon_missing.png"/>';
	if(f.status=200){
		var result=jQuery.parseJSON(f.responseText);
		if(result.isValable){
			field.next("span").empty().append(correct);
			return;
		}
		field.next("span").empty().append(incorrect);
		return result.info;
	}else{
		return "验证码输入错误";
	}
};