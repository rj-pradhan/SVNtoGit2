var Prototype={Version:"1.5.0",BrowserFeatures:{XPath:!!document.evaluate},ScriptFragment:"(?:<script.*?>)((\n|\r|.)*?)(?:</script>)",emptyFunction:function(){
},K:function(x){
return x;
}};
var Class={create:function(){
return function(){
this.initialize.apply(this,arguments);
};
}};
var Abstract=new Object();
Object.extend=function(_2,_3){
for(var _4 in _3){
_2[_4]=_3[_4];
}
return _2;
};
Object.extend(Object,{inspect:function(_5){
try{
if(_5===undefined){
return "undefined";
}
if(_5===null){
return "null";
}
return _5.inspect?_5.inspect():_5.toString();
}
catch(e){
if(e instanceof RangeError){
return "...";
}
throw e;
}
},keys:function(_6){
var _7=[];
for(var _8 in _6){
_7.push(_8);
}
return _7;
},values:function(_9){
var _a=[];
for(var _b in _9){
_a.push(_9[_b]);
}
return _a;
},clone:function(_c){
return Object.extend({},_c);
}});
Function.prototype.bind=function(){
var _d=this,_e=$A(arguments),_f=_e.shift();
return function(){
return _d.apply(_f,_e.concat($A(arguments)));
};
};
Function.prototype.bindAsEventListener=function(_10){
var _11=this,_12=$A(arguments),_10=_12.shift();
return function(_13){
return _11.apply(_10,[(_13||window.event)].concat(_12).concat($A(arguments)));
};
};
Object.extend(Number.prototype,{toColorPart:function(){
var _14=this.toString(16);
if(this<16){
return "0"+_14;
}
return _14;
},succ:function(){
return this+1;
},times:function(_15){
$R(0,this,true).each(_15);
return this;
}});
var Try={these:function(){
var _16;
for(var i=0,_18=arguments.length;i<_18;i++){
var _19=arguments[i];
try{
_16=_19();
break;
}
catch(e){
}
}
return _16;
}};
var PeriodicalExecuter=Class.create();
PeriodicalExecuter.prototype={initialize:function(_1a,_1b){
this.callback=_1a;
this.frequency=_1b;
this.currentlyExecuting=false;
this.registerCallback();
},registerCallback:function(){
this.timer=setInterval(this.onTimerEvent.bind(this),this.frequency*1000);
},stop:function(){
if(!this.timer){
return;
}
clearInterval(this.timer);
this.timer=null;
},onTimerEvent:function(){
if(!this.currentlyExecuting){
try{
this.currentlyExecuting=true;
this.callback(this);
}
finally{
this.currentlyExecuting=false;
}
}
}};
String.interpret=function(_1c){
return _1c==null?"":String(_1c);
};
Object.extend(String.prototype,{gsub:function(_1d,_1e){
var _1f="",_20=this,_21;
_1e=arguments.callee.prepareReplacement(_1e);
while(_20.length>0){
if(_21=_20.match(_1d)){
_1f+=_20.slice(0,_21.index);
_1f+=String.interpret(_1e(_21));
_20=_20.slice(_21.index+_21[0].length);
}else{
_1f+=_20,_20="";
}
}
return _1f;
},sub:function(_22,_23,_24){
_23=this.gsub.prepareReplacement(_23);
_24=_24===undefined?1:_24;
return this.gsub(_22,function(_25){
if(--_24<0){
return _25[0];
}
return _23(_25);
});
},scan:function(_26,_27){
this.gsub(_26,_27);
return this;
},truncate:function(_28,_29){
_28=_28||30;
_29=_29===undefined?"...":_29;
return this.length>_28?this.slice(0,_28-_29.length)+_29:this;
},strip:function(){
return this.replace(/^\s+/,"").replace(/\s+$/,"");
},stripTags:function(){
return this.replace(/<\/?[^>]+>/gi,"");
},stripScripts:function(){
return this.replace(new RegExp(Prototype.ScriptFragment,"img"),"");
},extractScripts:function(){
var _2a=new RegExp(Prototype.ScriptFragment,"img");
var _2b=new RegExp(Prototype.ScriptFragment,"im");
return (this.match(_2a)||[]).map(function(_2c){
return (_2c.match(_2b)||["",""])[1];
});
},evalScripts:function(){
return this.extractScripts().map(function(_2d){
return eval(_2d);
});
},escapeHTML:function(){
var div=document.createElement("div");
var _2f=document.createTextNode(this);
div.appendChild(_2f);
return div.innerHTML;
},unescapeHTML:function(){
var div=document.createElement("div");
div.innerHTML=this.stripTags();
return div.childNodes[0]?(div.childNodes.length>1?$A(div.childNodes).inject("",function(_31,_32){
return _31+_32.nodeValue;
}):div.childNodes[0].nodeValue):"";
},toQueryParams:function(_33){
var _34=this.strip().match(/([^?#]*)(#.*)?$/);
if(!_34){
return {};
}
return _34[1].split(_33||"&").inject({},function(_35,_36){
if((_36=_36.split("="))[0]){
var _37=decodeURIComponent(_36[0]);
var _38=_36[1]?decodeURIComponent(_36[1]):undefined;
if(_35[_37]!==undefined){
if(_35[_37].constructor!=Array){
_35[_37]=[_35[_37]];
}
if(_38){
_35[_37].push(_38);
}
}else{
_35[_37]=_38;
}
}
return _35;
});
},toArray:function(){
return this.split("");
},succ:function(){
return this.slice(0,this.length-1)+String.fromCharCode(this.charCodeAt(this.length-1)+1);
},camelize:function(){
var _39=this.split("-"),len=_39.length;
if(len==1){
return _39[0];
}
var _3b=this.charAt(0)=="-"?_39[0].charAt(0).toUpperCase()+_39[0].substring(1):_39[0];
for(var i=1;i<len;i++){
_3b+=_39[i].charAt(0).toUpperCase()+_39[i].substring(1);
}
return _3b;
},capitalize:function(){
return this.charAt(0).toUpperCase()+this.substring(1).toLowerCase();
},underscore:function(){
return this.gsub(/::/,"/").gsub(/([A-Z]+)([A-Z][a-z])/,"#{1}_#{2}").gsub(/([a-z\d])([A-Z])/,"#{1}_#{2}").gsub(/-/,"_").toLowerCase();
},dasherize:function(){
return this.gsub(/_/,"-");
},inspect:function(_3d){
var _3e=this.replace(/\\/g,"\\\\");
if(_3d){
return "\""+_3e.replace(/"/g,"\\\"")+"\"";
}else{
return "'"+_3e.replace(/'/g,"\\'")+"'";
}
}});
String.prototype.gsub.prepareReplacement=function(_3f){
if(typeof _3f=="function"){
return _3f;
}
var _40=new Template(_3f);
return function(_41){
return _40.evaluate(_41);
};
};
String.prototype.parseQuery=String.prototype.toQueryParams;
var Template=Class.create();
Template.Pattern=/(^|.|\r|\n)(#\{(.*?)\})/;
Template.prototype={initialize:function(_42,_43){
this.template=_42.toString();
this.pattern=_43||Template.Pattern;
},evaluate:function(_44){
return this.template.gsub(this.pattern,function(_45){
var _46=_45[1];
if(_46=="\\"){
return _45[2];
}
return _46+String.interpret(_44[_45[3]]);
});
}};
var $break=new Object();
var $continue=new Object();
var Enumerable={each:function(_47){
var _48=0;
try{
this._each(function(_49){
try{
_47(_49,_48++);
}
catch(e){
if(e!=$continue){
throw e;
}
}
});
}
catch(e){
if(e!=$break){
throw e;
}
}
return this;
},eachSlice:function(_4a,_4b){
var _4c=-_4a,_4d=[],_4e=this.toArray();
while((_4c+=_4a)<_4e.length){
_4d.push(_4e.slice(_4c,_4c+_4a));
}
return _4d.map(_4b);
},all:function(_4f){
var _50=true;
this.each(function(_51,_52){
_50=_50&&!!(_4f||Prototype.K)(_51,_52);
if(!_50){
throw $break;
}
});
return _50;
},any:function(_53){
var _54=false;
this.each(function(_55,_56){
if(_54=!!(_53||Prototype.K)(_55,_56)){
throw $break;
}
});
return _54;
},collect:function(_57){
var _58=[];
this.each(function(_59,_5a){
_58.push((_57||Prototype.K)(_59,_5a));
});
return _58;
},detect:function(_5b){
var _5c;
this.each(function(_5d,_5e){
if(_5b(_5d,_5e)){
_5c=_5d;
throw $break;
}
});
return _5c;
},findAll:function(_5f){
var _60=[];
this.each(function(_61,_62){
if(_5f(_61,_62)){
_60.push(_61);
}
});
return _60;
},grep:function(_63,_64){
var _65=[];
this.each(function(_66,_67){
var _68=_66.toString();
if(_68.match(_63)){
_65.push((_64||Prototype.K)(_66,_67));
}
});
return _65;
},include:function(_69){
var _6a=false;
this.each(function(_6b){
if(_6b==_69){
_6a=true;
throw $break;
}
});
return _6a;
},inGroupsOf:function(_6c,_6d){
_6d=_6d===undefined?null:_6d;
return this.eachSlice(_6c,function(_6e){
while(_6e.length<_6c){
_6e.push(_6d);
}
return _6e;
});
},inject:function(_6f,_70){
this.each(function(_71,_72){
_6f=_70(_6f,_71,_72);
});
return _6f;
},invoke:function(_73){
var _74=$A(arguments).slice(1);
return this.map(function(_75){
return _75[_73].apply(_75,_74);
});
},max:function(_76){
var _77;
this.each(function(_78,_79){
_78=(_76||Prototype.K)(_78,_79);
if(_77==undefined||_78>=_77){
_77=_78;
}
});
return _77;
},min:function(_7a){
var _7b;
this.each(function(_7c,_7d){
_7c=(_7a||Prototype.K)(_7c,_7d);
if(_7b==undefined||_7c<_7b){
_7b=_7c;
}
});
return _7b;
},partition:function(_7e){
var _7f=[],_80=[];
this.each(function(_81,_82){
((_7e||Prototype.K)(_81,_82)?_7f:_80).push(_81);
});
return [_7f,_80];
},pluck:function(_83){
var _84=[];
this.each(function(_85,_86){
_84.push(_85[_83]);
});
return _84;
},reject:function(_87){
var _88=[];
this.each(function(_89,_8a){
if(!_87(_89,_8a)){
_88.push(_89);
}
});
return _88;
},sortBy:function(_8b){
return this.map(function(_8c,_8d){
return {value:_8c,criteria:_8b(_8c,_8d)};
}).sort(function(_8e,_8f){
var a=_8e.criteria,b=_8f.criteria;
return a<b?-1:a>b?1:0;
}).pluck("value");
},toArray:function(){
return this.map();
},zip:function(){
var _92=Prototype.K,_93=$A(arguments);
if(typeof _93.last()=="function"){
_92=_93.pop();
}
var _94=[this].concat(_93).map($A);
return this.map(function(_95,_96){
return _92(_94.pluck(_96));
});
},size:function(){
return this.toArray().length;
},inspect:function(){
return "#<Enumerable:"+this.toArray().inspect()+">";
}};
Object.extend(Enumerable,{map:Enumerable.collect,find:Enumerable.detect,select:Enumerable.findAll,member:Enumerable.include,entries:Enumerable.toArray});
var $A=Array.from=function(_97){
if(!_97){
return [];
}
if(_97.toArray){
return _97.toArray();
}else{
var _98=[];
for(var i=0,_9a=_97.length;i<_9a;i++){
_98.push(_97[i]);
}
return _98;
}
};
Object.extend(Array.prototype,Enumerable);
if(!Array.prototype._reverse){
Array.prototype._reverse=Array.prototype.reverse;
}
Object.extend(Array.prototype,{_each:function(_9b){
for(var i=0,_9d=this.length;i<_9d;i++){
_9b(this[i]);
}
},clear:function(){
this.length=0;
return this;
},first:function(){
return this[0];
},last:function(){
return this[this.length-1];
},compact:function(){
return this.select(function(_9e){
return _9e!=null;
});
},flatten:function(){
return this.inject([],function(_9f,_a0){
return _9f.concat(_a0&&_a0.constructor==Array?_a0.flatten():[_a0]);
});
},without:function(){
var _a1=$A(arguments);
return this.select(function(_a2){
return !_a1.include(_a2);
});
},indexOf:function(_a3){
for(var i=0,_a5=this.length;i<_a5;i++){
if(this[i]==_a3){
return i;
}
}
return -1;
},reverse:function(_a6){
return (_a6!==false?this:this.toArray())._reverse();
},reduce:function(){
return this.length>1?this:this[0];
},uniq:function(){
return this.inject([],function(_a7,_a8){
return _a7.include(_a8)?_a7:_a7.concat([_a8]);
});
},clone:function(){
return [].concat(this);
},size:function(){
return this.length;
},inspect:function(){
return "["+this.map(Object.inspect).join(", ")+"]";
}});
Array.prototype.toArray=Array.prototype.clone;
function $w(_a9){
_a9=_a9.strip();
return _a9?_a9.split(/\s+/):[];
}
if(window.opera){
Array.prototype.concat=function(){
var _aa=[];
for(var i=0,_ac=this.length;i<_ac;i++){
_aa.push(this[i]);
}
for(var i=0,_ac=arguments.length;i<_ac;i++){
if(arguments[i].constructor==Array){
for(var j=0,_ae=arguments[i].length;j<_ae;j++){
_aa.push(arguments[i][j]);
}
}else{
_aa.push(arguments[i]);
}
}
return _aa;
};
}
var Hash=function(obj){
Object.extend(this,obj||{});
};
Object.extend(Hash,{toQueryString:function(obj){
var _b1=[];
this.prototype._each.call(obj,function(_b2){
if(!_b2.key){
return;
}
if(_b2.value&&_b2.value.constructor==Array){
var _b3=_b2.value.compact();
if(_b3.length<2){
_b2.value=_b3.reduce();
}else{
key=encodeURIComponent(_b2.key);
_b3.each(function(_b4){
_b4=_b4!=undefined?encodeURIComponent(_b4):"";
_b1.push(key+"="+encodeURIComponent(_b4));
});
return;
}
}
if(_b2.value==undefined){
_b2[1]="";
}
_b1.push(_b2.map(encodeURIComponent).join("="));
});
return _b1.join("&");
}});
Object.extend(Hash.prototype,Enumerable);
Object.extend(Hash.prototype,{_each:function(_b5){
for(var key in this){
var _b7=this[key];
if(_b7&&_b7==Hash.prototype[key]){
continue;
}
var _b8=[key,_b7];
_b8.key=key;
_b8.value=_b7;
_b5(_b8);
}
},keys:function(){
return this.pluck("key");
},values:function(){
return this.pluck("value");
},merge:function(_b9){
return $H(_b9).inject(this,function(_ba,_bb){
_ba[_bb.key]=_bb.value;
return _ba;
});
},remove:function(){
var _bc;
for(var i=0,_be=arguments.length;i<_be;i++){
var _bf=this[arguments[i]];
if(_bf!==undefined){
if(_bc===undefined){
_bc=_bf;
}else{
if(_bc.constructor!=Array){
_bc=[_bc];
}
_bc.push(_bf);
}
}
delete this[arguments[i]];
}
return _bc;
},toQueryString:function(){
return Hash.toQueryString(this);
},inspect:function(){
return "#<Hash:{"+this.map(function(_c0){
return _c0.map(Object.inspect).join(": ");
}).join(", ")+"}>";
}});
function $H(_c1){
if(_c1&&_c1.constructor==Hash){
return _c1;
}
return new Hash(_c1);
}
ObjectRange=Class.create();
Object.extend(ObjectRange.prototype,Enumerable);
Object.extend(ObjectRange.prototype,{initialize:function(_c2,end,_c4){
this.start=_c2;
this.end=end;
this.exclusive=_c4;
},_each:function(_c5){
var _c6=this.start;
while(this.include(_c6)){
_c5(_c6);
_c6=_c6.succ();
}
},include:function(_c7){
if(_c7<this.start){
return false;
}
if(this.exclusive){
return _c7<this.end;
}
return _c7<=this.end;
}});
var $R=function(_c8,end,_ca){
return new ObjectRange(_c8,end,_ca);
};
var Ice=new Object;
Object.methods=function(_cb){
for(property in _cb){
this.prototype[property]=_cb[property];
}
};
Object.subclass=function(_cc){
var _cd=function(){
this.initialize.apply(this,arguments);
};
_cd.methods=this.methods;
_cd.subclass=this.subclass;
_cd.prototype.initialize=Function.NOOP;
_cd.methods(this.prototype);
_cd.prototype.initializeSuperclass=this.prototype.initialize?this.prototype.initialize:Function.NOOP;
_cd.methods(_cc||{});
return _cd;
};
Boolean.prototype.ifTrue=function(e){
if(this==true){
e();
}
return this;
};
Boolean.prototype.ifFalse=function(e){
if(this==false){
e();
}
return this;
};
Number.prototype.asZeroPrefixedString=function(){
return this<9?("0"+this):this.toString();
};
Date.prototype.toTimestamp=function(){
return this.toLocaleTimeString().substr(0,8);
};
Object.extend(String.prototype,{asBoolean:function(){
return "true"==this||"yes"==this;
},asNumber:function(){
return this*1;
},asElement:function(){
return document.getElementById(this);
},asExtendedElement:function(){
var _d0=this.asElement();
if(!_d0){
throw "cannot find element with id: '"+this+"'";
}
return Ice.ElementModel.Element.adaptToElement(_d0);
},asRegexp:function(){
return new RegExp(this);
},contains:function(_d1){
return this.indexOf(_d1)>=0;
},containsWords:function(){
return /(\w+)/.test(this);
}});
Object.extend(Array.prototype,{intersect:function(_d2){
return this.select(function(_d3){
return _d2.include(_d3);
});
},complement:function(_d4){
return this.reject(function(_d5){
return _d4.include(_d5);
});
},isEmpty:function(){
return this.length==0;
},isNotEmpty:function(){
return this.length>0;
},as:function(_d6){
_d6.apply(_d6,this);
},copy:function(){
return this.collect(function(_d7){
return _d7;
});
},copyFrom:function(_d8,_d9){
var _da=[];
var end=_d8+_d9;
for(var i=_d8;i<end;i++){
_da.push(this[i]);
}
return _da;
},broadcast:function(_dd){
this.each(function(_de){
_de(_dd);
});
},broadcaster:function(){
return function(_df){
this.broadcast(_df);
}.bind(this);
},asSet:function(){
var set=[];
this.each(function(_e1){
if(!set.include(_e1)){
set.push(_e1);
}
});
return set;
}});
Function.prototype.delayFor=function(_e2){
var _e3=this;
return function(){
var _e4=this;
var _e5=arguments;
var _e6=function(){
try{
_e3.apply(_e4,_e5);
}
finally{
clearInterval(_e5.id);
_e5.id=null;
}
};
var id=_e5.id=setInterval(_e6,_e2);
arguments.callee.cancel=function(){
clearInterval(id);
_e5.id=null;
};
};
};
Function.prototype.delayExecutionFor=function(_e8){
var _e9=this.delayFor(_e8);
_e9.apply();
return _e9;
};
Function.prototype.repeatEvery=function(_ea){
var _eb=this;
return function(){
var _ec=this;
var _ed=arguments;
var _ee=function(){
_eb.apply(_ec,_ed);
};
var id=setInterval(_ee,_ea);
arguments.callee.cancel=function(){
clearInterval(id);
};
};
};
Function.prototype.repeatExecutionEvery=function(_f0){
var _f1=this.repeatEvery(_f0);
_f1.apply(this);
return _f1;
};
Function.NOOP=function(){
};
window.width=function(){
return window.innerWidth?window.innerWidth:(document.documentElement&&document.documentElement.clientWidth)?document.documentElement.clientWidth:document.body.clientWidth;
};
window.height=function(){
return window.innerHeight?window.innerHeight:(document.documentElement&&document.documentElement.clientHeight)?document.documentElement.clientHeight:document.body.clientHeight;
};
["onLoad","onUnload","onResize","onScroll"].each(function(_f2){
if(!window[_f2]){
window[_f2]=function(_f3){
var _f4=_f2.toLowerCase();
var _f5=window[_f4];
var _f6=_f5?[_f5,_f3]:[_f3];
window[_f4]=_f6.broadcaster();
window[_f2]=function(_f7){
if(!_f6.detect(function(_f8){
return _f8.toString()==_f7.toString();
})){
_f6.push(_f7);
}
};
};
}
});
if(typeof OpenAjax!="undefined"){
if(typeof OpenAjax.addOnLoad!="undefined"){
var current=window.onLoad;
window.onLoad=OpenAjax.addOnLoad;
OpenAjax.addOnLoad(current);
}
if(typeof OpenAjax.addOnUnLoad!="undefined"){
var current=window.onUnload;
window.onUnload=OpenAjax.addOnUnLoad;
OpenAjax.addOnLoad(current);
}
}
window.onKeyPress=function(_f9){
var _fa=document.onkeypress;
document.onkeypress=_fa?function(e){
_f9(Ice.EventModel.Event.adaptToEvent(e));
_fa(e);
}:function(e){
_f9(Ice.EventModel.Event.adaptToKeyEvent(e));
};
};
[Ice].as(function(_fd){
_fd.Enumerator=Object.subclass({initialize:function(_fe){
this.indexedObject=_fe;
},_each:function(_ff){
for(var i=0;i<this.indexedObject.length;i++){
_ff(this.indexedObject[i],i);
}
},reverse:function(){
return new _fd.ReverseEnumerator(this.indexedObject);
}});
_fd.Enumerator.methods(Enumerable);
_fd.ReverseEnumerator=_fd.Enumerator.subclass({_each:function(_101){
for(var i=(this.indexedObject.length-1);i>=0;i--){
_101(this.indexedObject[i],i);
}
},reverse:function(){
return new _fd.Enumerator(this.indexedObject);
}});
Object.prototype.asEnumerator=function(){
return new _fd.Enumerator(this);
};
window.$enumerate=function(_103){
return new _fd.Enumerator(_103);
};
});
[Ice.Log=new Object].as(function(This){
This.Priority=Object.subclass({debug:function(_105,_106,_107,_108){
_105.debug(_106,_107,_108);
},info:function(_109,_10a,_10b,_10c){
_109.info(_10a,_10b,_10c);
},warn:function(_10d,_10e,_10f,_110){
_10d.warn(_10e,_10f,_110);
},error:function(_111,_112,_113,_114){
_111.error(_112,_113,_114);
}});
This.Debug=This.Priority.subclass({asString:function(){
return "Debug";
}});
This.Info=This.Debug.subclass({debug:Function.NOOP,asString:function(){
return "Info";
}});
This.Warn=This.Info.subclass({info:Function.NOOP,asString:function(){
return "Warn";
}});
This.Error=This.Warn.subclass({warn:Function.NOOP,asString:function(){
return "Error";
}});
This.Priority.DEBUG=new This.Debug;
This.Priority.INFO=new This.Info;
This.Priority.WARN=new This.Warn;
This.Priority.ERROR=new This.Error;
This.Priority.Levels=[This.Priority.DEBUG,This.Priority.INFO,This.Priority.WARN,This.Priority.ERROR];
This.Logger=Object.subclass({initialize:function(_115,_116,_117){
this.handler=_116||{debug:Function.NOOP,info:Function.NOOP,warn:Function.NOOP,error:Function.NOOP};
this.category=_115;
this.children=[];
this.priority=_117||This.Priority.ERROR;
},debug:function(_118,_119){
this.priority.debug(this.handler,this.category,_118,_119);
},info:function(_11a,_11b){
this.priority.info(this.handler,this.category,_11a,_11b);
},warn:function(_11c,_11d){
this.priority.warn(this.handler,this.category,_11c,_11d);
},error:function(_11e,_11f){
this.priority.error(this.handler,this.category,_11e,_11f);
},child:function(_120){
var _121=this.category.copy();
_121.push(_120);
var _122=new This.Logger(_121,this.handler);
this.children.push(_122);
return _122;
},threshold:function(_123){
this.priority=_123;
this.children.each(function(_124){
_124.threshold(_123);
});
},handleWith:function(_125){
this.handler=_125;
}});
This.WindowLogHandler=Object.subclass({initialize:function(_126,_127,_128,_129){
this.lineOptions=[25,50,100,200,400];
this.logger=_126;
this.logger.handleWith(this);
this.parentWindow=_127;
this.lines=_128||this.lineOptions[3];
this.thresholdPriority=_129||This.Priority.DEBUG;
this.categoryMatcher=/.*/;
this.closeOnExit=true;
this.parentWindow.onKeyPress(function(e){
var key=e.keyCode();
if((key==20||key==84)&&e.isCtrlPressed()&&e.isShiftPressed()){
this.enable();
}
}.bind(this));
},enable:function(){
try{
this.window=this.parentWindow.open("","log"+window.identifier,"scrollbars=1,width=800,height=680");
var _12c=this.window.document;
this.log=this.window.document.getElementById("log-window");
if(this.log){
return;
}
_12c.body.appendChild(_12c.createTextNode(" Close on exit "));
var _12d=_12c.createElement("input");
_12d.style.margin="2px";
_12d.setAttribute("type","checkbox");
_12d.defaultChecked=true;
_12d.checked=true;
_12d.onclick=function(){
this.closeOnExit=_12d.checked;
}.bind(this);
_12c.body.appendChild(_12d);
_12c.body.appendChild(_12c.createTextNode(" Lines "));
var _12e=_12c.createElement("select");
_12e.style.margin="2px";
this.lineOptions.each(function(_12f,_130){
var _131=_12e.appendChild(_12c.createElement("option"));
if(this.lines==_12f){
_12e.selectedIndex=_130;
}
_131.appendChild(_12c.createTextNode(_12f.toString()));
}.bind(this));
_12e.onchange=function(_132){
this.lines=this.lineOptions[_12e.selectedIndex];
this.clearPreviousEvents();
}.bind(this);
_12c.body.appendChild(_12e);
_12c.body.appendChild(_12c.createTextNode(" Category "));
var _133=_12c.createElement("input");
_133.style.margin="2px";
_133.setAttribute("type","text");
_133.setAttribute("value",this.categoryMatcher.source);
_133.onchange=function(){
this.categoryMatcher=_133.value.asRegexp();
}.bind(this);
_12c.body.appendChild(_133);
_12c.body.appendChild(_12c.createTextNode(" Level "));
var _134=_12c.createElement("select");
_134.style.margin="2px";
This.Priority.Levels.each(function(_135,_136){
var _137=_134.appendChild(_12c.createElement("option"));
if(this.thresholdPriority==_135){
_134.selectedIndex=_136;
}
_137.appendChild(_12c.createTextNode(_135.asString()));
}.bind(this));
this.logger.threshold(this.thresholdPriority);
_134.onchange=function(_138){
this.thresholdPriority=This.Priority.Levels[_134.selectedIndex];
this.logger.threshold(this.thresholdPriority);
}.bind(this);
_12c.body.appendChild(_134);
var _139=_12c.createElement("input");
_139.style.margin="2px";
_139.setAttribute("type","button");
_139.setAttribute("value","Stop");
_139.onclick=function(){
_139.setAttribute("value",this.toggle()?"Stop":"Start");
}.bind(this);
_12c.body.appendChild(_139);
var _13a=_12c.createElement("input");
_13a.style.margin="2px";
_13a.setAttribute("type","button");
_13a.setAttribute("value","Clear");
_13a.onclick=function(){
this.clearAllEvents();
}.bind(this);
_12c.body.appendChild(_13a);
this.log=_12c.body.appendChild(_12c.createElement("pre"));
this.log.id="log-window";
this.log.style.width="100%";
this.log.style.minHeight="0";
this.log.style.maxHeight="550px";
this.log.style.borderWidth="1px";
this.log.style.borderStyle="solid";
this.log.style.borderColor="#999";
this.log.style.backgroundColor="#ddd";
this.log.style.overflow="scroll";
this.window.onunload=function(){
this.disable();
}.bind(this);
}
catch(e){
this.disable();
}
},disable:function(){
this.logger.threshold(This.Priority.ERROR);
this.handle=Function.NOOP;
if(this.closeOnExit&&this.window){
this.window.close();
}
},toggle:function(){
if(this.handle==Function.NOOP){
delete this.handle;
return true;
}else{
this.handle=Function.NOOP;
return false;
}
},debug:function(_13b,_13c,_13d){
this.handle("#333","debug",_13b,_13c,_13d);
},info:function(_13e,_13f,_140){
this.handle("green","info",_13e,_13f,_140);
},warn:function(_141,_142,_143){
this.handle("orange","warn",_141,_142,_143);
},error:function(_144,_145,_146){
this.handle("red","error",_144,_145,_146);
},handle:function(_147,_148,_149,_14a,_14b){
if(this.categoryMatcher.test(_149.join("."))){
var _14c=this.log.ownerDocument;
var _14d=(new Date()).toTimestamp();
var _14e=_149.join(".");
(_14d+" "+_148+" \t["+_14e+"] : "+_14a+(_14b?("\n"+_14b):"")).split("\n").each(function(line){
if(line.containsWords()){
var _150=_14c.createElement("div");
_150.style.padding="3px";
_150.style.color=_147;
this.log.appendChild(_150).appendChild(_14c.createTextNode(line));
}
}.bind(this));
this.log.scrollTop=this.log.scrollHeight;
}
this.clearPreviousEvents();
},clearPreviousEvents:function(){
var _151=$A(this.log.childNodes);
_151.copyFrom(0,_151.length-this.lines).each(function(node){
this.log.removeChild(node);
}.bind(this));
},clearAllEvents:function(){
$A(this.log.childNodes).each(function(node){
this.log.removeChild(node);
}.bind(this));
}});
This.NOOPConsole={debug:Function.NOOP,info:Function.NOOP,warn:Function.NOOP,error:Function.NOOP};
This.FirebugLogHandler=Object.subclass({initialize:function(_154){
_154.handleWith(this);
this.logger=_154;
this.console=This.NOOPConsole;
window.onLoad(function(){
this.enable();
}.bind(this));
},enable:function(){
this.console=window.console;
this.logger.threshold(This.Priority.DEBUG);
},disable:function(){
this.console=This.NOOPConsole;
this.logger.threshold(This.Priority.ERROR);
},toggle:Function.NOOP,debug:function(_155,_156,_157){
_157?this.console.debug(this.format(_155,_156),_157):this.console.debug(this.format(_155,_156));
},info:function(_158,_159,_15a){
_15a?this.console.info(this.format(_158,_159),_15a):this.console.info(this.format(_158,_159));
},warn:function(_15b,_15c,_15d){
_15d?this.console.warn(this.format(_15b,_15c),_15d):this.console.warn(this.format(_15b,_15c));
},error:function(_15e,_15f,_160){
_160?this.console.error(this.format(_15e,_15f),_160):this.console.error(this.format(_15e,_15f));
},format:function(_161,_162){
return "["+_161.join(".")+"] "+_162;
}});
});
[Ice.Ajax=new Object].as(function(This){
This.Client=Object.subclass({initialize:function(_164){
this.logger=_164;
this.cookies=new Object;
document.cookie.split("; ").each(function(_165){
var _166=_165.split("=");
this.cookies[_166.first()]=_166.last();
}.bind(this));
try{
if(window.createRequest){
this.createRequest=function(){
var _167=new This.RequestProxy(window.createRequest(),this.logger);
_167.post=function(_168,path,_16a,_16b){
this.get(_168,path,_16a,_16b);
};
return _167;
}.bind(this);
}else{
if(window.XMLHttpRequest){
this.createRequest=function(){
return new This.RequestProxy(new XMLHttpRequest(),this.logger);
}.bind(this);
}else{
if(window.ActiveXObject){
this.createRequest=function(){
return new This.RequestProxy(new ActiveXObject("Microsoft.XMLHTTP"),this.logger);
}.bind(this);
}
}
}
}
catch(e){
this.logger.error("failed to create factory request",e);
}
},getAsynchronously:function(path,_16d,_16e){
return this.createRequest().getAsynchronously(path,_16d,_16e);
},getSynchronously:function(path,_170,_171){
return this.createRequest().getSynchronously(path,_170,_171);
},postAsynchronously:function(path,_173,_174){
return this.createRequest().postAsynchronously(path,_173,_174);
},postSynchronously:function(path,_176,_177){
return this.createRequest().postSynchronously(path,_176,_177);
}});
This.RequestProxy=Object.subclass({initialize:function(_178,_179){
this.identifier=+Math.random().toString().substr(2,7);
this.request=_178;
this.logger=_179;
this.callbacks=[];
this.responseCallback=function(){
if(this.isComplete()){
this.logger.debug("["+this.identifier+"] : receive ["+this.request.status+"] "+this.request.statusText);
}
this.callbacks.each(function(_17a){
try{
_17a(this);
}
catch(e){
this.logger.error("failed to respond",e);
}
}.bind(this));
}.bind(this);
},on:function(test,_17c){
this.callbacks.push(function(_17d){
if(test(_17d)){
_17c(_17d);
}
});
},isComplete:function(){
return this.request.readyState==4;
},isResponseValid:function(){
try{
return this.request.status>=0;
}
catch(e){
return false;
}
},isOk:function(){
try{
return this.request.status==200;
}
catch(e){
return false;
}
},isUnavailable:function(){
try{
return this.request.status==503;
}
catch(e){
return false;
}
},isOkAndComplete:function(){
return this.isComplete()&&this.isOk();
},isUnavailableAndComplete:function(){
return this.isComplete()&&this.isUnavailable();
},getAsynchronously:function(path,_17f,_180){
this.request.open("GET",path+"?"+_17f+"&rand="+Math.random(),true);
if(_180){
_180(this);
}
this.request.onreadystatechange=this.responseCallback;
this.logger.debug("["+this.identifier+"] : send asynchronous GET");
this.request.send("");
return this;
},postAsynchronously:function(path,_182,_183){
this.request.open("POST",path,true);
if(_183){
_183(this);
}
this.request.onreadystatechange=this.responseCallback;
this.logger.debug("["+this.identifier+"] : send asynchronous POST");
this.request.send(_182+"&rand="+Math.random()+"\n\n");
return this;
},getSynchronously:function(path,_185,_186){
this.request.open("GET",path+"?"+_185+"&rand="+Math.random(),false);
if(_186){
_186(this);
}
this.logger.debug("["+this.identifier+"] : send synchronous GET");
this.request.send("");
this.responseCallback();
return this;
},postSynchronously:function(path,_188,_189){
this.request.open("POST",path,false);
if(_189){
_189(this);
}
this.logger.debug("["+this.identifier+"] : send synchronous POST");
this.request.send(_188+"&rand="+Math.random()+"\n\n");
this.responseCallback();
return this;
},setRequestHeader:function(name,_18b){
this.request.setRequestHeader(name,_18b);
},getResponseHeader:function(name){
try{
return this.request.getResponseHeader(name);
}
catch(e){
return null;
}
},containsResponseHeader:function(name){
try{
var _18e=this.request.getResponseHeader(name);
return _18e&&_18e!="";
}
catch(e){
return false;
}
},content:function(){
try{
return this.request.responseText;
}
catch(e){
return "";
}
},contentAsDOM:function(){
return this.request.responseXML;
},close:function(){
this.request.onreadystatechange=Function.NOOP;
this.request.abort();
this.request=null;
this.callbacks=null;
this.logger.debug("["+this.identifier+"] : connection closed");
}});
});
[Ice.Parameter=new Object].as(function(This){
This.Query=Object.subclass({initialize:function(){
this.parameters=[];
},add:function(name,_191){
if(!this.parameters.detect(function(_192){
return _192.name==name&&_192.value==_191;
})){
this.parameters.push(name.associateWith(_191));
}
},addQuery:function(_193){
_193.serializeOn(this);
return this;
},asURIEncodedString:function(){
return this.parameters.inject("",function(_194,_195,_196){
return _194+=(_196==0)?_195.asURIEncodedString():"&"+_195.asURIEncodedString();
});
},asString:function(){
return this.parameters.inject("",function(_197,_198,_199){
return _197+"\n| "+_198.asString()+" |";
});
},sendOn:function(_19a){
_19a.send(this);
},send:function(){
if(!connection){
throw "default connection not available";
}
this.sendOn(connection);
},serializeOn:function(_19b){
this.parameters.each(function(_19c){
_19c.serializeOn(_19b);
});
}});
This.Query.create=function(_19d){
var _19e=new This.Query;
_19d.apply(this,[_19e]);
return _19e;
};
This.Association=Object.subclass({initialize:function(name,_1a0){
this.name=name;
this.value=_1a0;
},asURIEncodedString:function(){
return encodeURIComponent(this.name)+"="+encodeURIComponent(this.value);
},asString:function(){
return this.name+"="+this.value;
},serializeOn:function(_1a1){
_1a1.add(this.name,this.value);
}});
Object.prototype.associateWith=function(_1a2){
return new This.Association(this,_1a2);
};
});
[Ice.Geometry=new Object].as(function(This){
This.Point=Object.subclass({initialize:function(x,y){
this.x=x;
this.y=y;
},asString:function(){
return "point ["+this.x+", "+this.y+"]";
},toString:function(){
return this.asString();
},serializeOn:function(_1a6){
_1a6.add("ice.event.x",this.x);
_1a6.add("ice.event.y",this.y);
}});
});
[Ice.ElementModel=new Object].as(function(This){
This.TemporaryContainer=function(){
var _1a8=document.body.appendChild(document.createElement("div"));
_1a8.style.visibility="hidden";
This.TemporaryContainer=function(){
return _1a8;
};
return _1a8;
};
This.DisconnectAllListenersAndPeers=function(e){
var _1aa=e.getElementsByTagName("*");
for(var i=0;i<_1aa.length;i++){
var _1ac=_1aa[i];
var peer=_1ac.peer;
if(peer){
peer.eachListenerName(function(_1ae){
_1ac[_1ae.toLowerCase()]=null;
});
_1ac.peer=null;
peer.element=null;
}
}
};
This.Element=Object.subclass({MouseListenerNames:["onClick","onDblClick","onMouseDown","onMouseMove","onMouseOut","onMouseOver","onMouseUp"],KeyListenerNames:["onKeyDown","onKeyPress","onKeyUp","onHelp"],initialize:function(_1af){
this.element=_1af;
},id:function(){
return this.element.id;
},replaceHtml:function(html){
this.withTemporaryContainer(function(_1b1){
_1b1.innerHTML=html;
var _1b2=_1b1.firstChild;
this.disconnectAllListenersAndPeers();
this.replaceHostElementWith(_1b2);
});
},withAllChildElements:function(_1b3){
var _1b4=this.element.getElementsByTagName("*");
for(var i=0;i<_1b4.length;i++){
var peer=_1b4[i].peer;
if(peer){
_1b3(peer);
}
}
},disconnectAllListenersAndPeers:/MSIE/.test(navigator.userAgent)?function(){
This.DisconnectAllListenersAndPeers.delayFor(100)(this.element);
}:function(){
This.DisconnectAllListenersAndPeers(this.element);
},serializeOn:function(_1b7){
},sendOn:function(_1b8){
Query.create(function(_1b9){
this.serializeOn(_1b9);
}.bind(this)).sendOn(_1b8);
},send:function(){
this.sendOn(connection);
},withTemporaryContainer:function(_1ba){
try{
_1ba.apply(this,[This.TemporaryContainer()]);
}
finally{
This.TemporaryContainer().innerHTML="";
}
},replaceHostElementWith:function(_1bb){
this.displayOff();
this.element.parentNode.replaceChild(_1bb,this.element);
this.element=_1bb;
this.element.peer=this;
},displayOff:/Safari/.test(navigator.userAgent)?Function.NOOP:function(){
this.element.style.display="none";
},eachListenerName:function(_1bc){
this.MouseListenerNames.each(_1bc);
this.KeyListenerNames.each(_1bc);
}});
This.Element.adaptToElement=function(e){
if(e.peer){
return e.peer;
}
switch(e.tagName.toLowerCase()){
case "textarea":
case "input":
e.peer=new This.InputElement(e);
break;
case "th":
case "td":
case "tr":
e.peer=new This.TableCellElement(e);
break;
case "button":
e.peer=new This.ButtonElement(e);
break;
case "select":
e.peer=new This.SelectElement(e);
break;
case "form":
e.peer=new This.FormElement(e);
break;
case "body":
e.peer=new This.BodyElement(e);
break;
case "script":
e.peer=new This.ScriptElement(e);
break;
case "title":
e.peer=new This.TitleElement(e);
break;
case "a":
e.peer=new This.AnchorElement(e);
break;
case "fieldset":
e.peer=new This.FieldSetElement(e);
break;
default:
e.peer=new This.Element(e);
break;
}
return e.peer;
};
This.InputElement=This.Element.subclass({InputListenerNames:["onBlur","onFocus","onChange"],initialize:function(_1be){
this.element=_1be;
var type=_1be.type.toLowerCase();
this.isSubmitElement=type=="submit"||type=="image"||type=="button";
},isSubmit:function(){
return this.isSubmitElement;
},form:function(){
return This.Element.adaptToElement(this.element.form);
},focus:function(){
var _1c0=this.element.onfocus;
this.element.onfocus=Function.NOOP;
this.element.focus();
this.element.onfocus=_1c0;
},replaceHtml:function(html){
this.withTemporaryContainer(function(_1c2){
_1c2.innerHTML=html;
var _1c3=_1c2.firstChild;
this.element.className=_1c3.className;
this.element.disabled=_1c3.disabled;
this.element.src=_1c3.src;
this.element.value=_1c3.value;
this.element.readOnly=_1c3.readOnly;
if(_1c3.style.display){
this.element.style.display="none";
}else{
this.element.style.display="";
}
this.element.title=_1c3.title;
if(this.element.checked!=_1c3.checked){
this.element.checked=_1c3.checked;
}
this.eachListenerName(function(_1c4){
var name=_1c4.toLowerCase();
this.element[name]=_1c3[name]?_1c3[name].bind(this.element):null;
_1c3[name]=null;
}.bind(this));
});
},serializeOn:function(_1c6){
switch(this.element.type.toLowerCase()){
case "image":
case "textarea":
case "submit":
case "hidden":
case "password":
case "text":
_1c6.add(this.element.name,this.element.value);
break;
case "checkbox":
case "radio":
if(this.element.checked){
_1c6.add(this.element.name,this.element.value||"on");
}
break;
}
},eachListenerName:function(_1c7){
this.MouseListenerNames.each(_1c7);
this.KeyListenerNames.each(_1c7);
this.InputListenerNames.each(_1c7);
}});
This.SelectElement=This.InputElement.subclass({isSubmit:function(){
return false;
},replaceHtml:function(html){
this.withTemporaryContainer(function(_1c9){
_1c9.innerHTML=html;
var _1ca=_1c9.firstChild;
this.disconnectAllListenersAndPeers();
this.replaceHostElementWith(_1ca);
});
},serializeOn:function(_1cb){
$enumerate(this.element.options).select(function(_1cc){
return _1cc.selected;
}).each(function(_1cd){
_1cb.add(this.element.name,_1cd.value||_1cd.text);
}.bind(this));
}});
This.ButtonElement=This.InputElement.subclass({initialize:function(_1ce){
this.element=_1ce;
this.isSubmitElement=_1ce.type.toLowerCase()=="submit";
},isSubmit:function(){
return this.isSubmitElement;
},replaceHtml:function(html){
this.withTemporaryContainer(function(_1d0){
_1d0.innerHTML=html;
var _1d1=_1d0.firstChild;
this.disconnectAllListenersAndPeers();
this.replaceHostElementWith(_1d1);
});
},serializeOn:function(_1d2){
_1d2.add(this.element.name,this.element.value);
}});
This.FormElement=This.Element.subclass({FormListenerNames:["onReset","onSubmit"],formElements:/Safari/.test(navigator.userAgent)?function(){
var _1d3=[];
$enumerate(this.element.elements).reverse().each(function(_1d4){
if(!_1d3.detect(function(_1d5){
return _1d4.id&&_1d5.element.id&&_1d5.element.id==_1d4.id;
})){
_1d3.push(This.Element.adaptToElement(_1d4));
}
});
return _1d3;
}:function(){
return $enumerate(this.element.elements).collect(function(_1d6){
return This.Element.adaptToElement(_1d6);
});
},serializeOn:function(_1d7){
this.formElements().each(function(_1d8){
if(!_1d8.isSubmit()){
_1d8.serializeOn(_1d7);
}
});
},eachListenerName:function(_1d9){
this.MouseListenerNames.each(_1d9);
this.KeyListenerNames.each(_1d9);
this.FormListenerNames.each(_1d9);
}});
This.BodyElement=This.Element.subclass({replaceHtml:function(html){
this.disconnectAllListenersAndPeers();
this.element.innerHTML=html.substring(html.indexOf(">")+1,html.lastIndexOf("<"));
}});
This.ScriptElement=This.Element.subclass({replaceHtml:function(html){
var _1dc=html.substring(html.indexOf(">")+1,html.lastIndexOf("<"));
if(_1dc!=""&&_1dc!=";"){
var _1dd=function(){
eval(_1dc);
};
_1dd.apply(window);
}
}});
This.TitleElement=This.Element.subclass({replaceHtml:function(html){
this.element.ownerDocument.title=html.substring(html.indexOf(">")+1,html.lastIndexOf("<"));
}});
This.FieldSetElement=This.Element.subclass({isSubmit:function(html){
return false;
}});
This.AnchorElement=This.Element.subclass({focus:function(){
var _1e0=this.element.onfocus;
this.element.onfocus=Function.NOOP;
this.element.focus();
this.element.onfocus=_1e0;
},serializeOn:function(_1e1){
_1e1.add(this.element.name,this.element.name);
},form:function(){
var _1e2=this.element.parentNode;
while(_1e2){
if(_1e2.tagName&&_1e2.tagName.toLowerCase()=="form"){
return This.Element.adaptToElement(_1e2);
}
_1e2=_1e2.parentNode;
}
throw "Cannot find enclosing form.";
}});
This.TableCellElement=This.Element.subclass({replaceHtml:function(html){
this.withTemporaryContainer(function(_1e4){
_1e4.innerHTML="<TABLE>"+html+"</TABLE>";
var _1e5=_1e4.firstChild;
while((null!=_1e5)&&(this.element.id!=_1e5.id)){
_1e5=_1e5.firstChild;
}
this.disconnectAllListenersAndPeers();
this.replaceHostElementWith(_1e5);
});
}});
window.$element=This.Element.adaptToElement;
});
[Ice.EventModel=new Object,Ice.ElementModel.Element,Ice.Parameter.Query,Ice.Geometry].as(function(This,_1e7,_1e8,_1e9){
This.IE=new Object;
This.Netscape=new Object;
This.Event=Object.subclass({initialize:function(_1ea,_1eb){
this.event=_1ea;
this.currentElement=_1eb;
},cancel:function(){
this.cancelBubbling();
this.cancelDefaultAction();
},isKeyEvent:function(){
return false;
},isMouseEvent:function(){
return false;
},captured:function(){
return this.currentElement?_1e7.adaptToElement(this.currentElement):null;
},serializeEventOn:function(_1ec){
_1ec.add("ice.event.target",this.target()&&this.target().id());
_1ec.add("ice.event.captured",this.captured()&&this.captured().id());
_1ec.add("ice.event.type","on"+this.event.type);
},serializeOn:function(_1ed){
this.serializeEventOn(_1ed);
},sendOn:function(_1ee){
_1e8.create(function(_1ef){
"partial".associateWith("true").serializeOn(_1ef);
try{
this.captured().serializeOn(_1ef);
this.serializeOn(_1ef);
}
catch(e){
this.serializeOn(_1ef);
}
}.bind(this)).sendOn(_1ee);
},sendFullOn:function(_1f0){
_1e8.create(function(_1f1){
"partial".associateWith("false").serializeOn(_1f1);
try{
this.captured().serializeOn(_1f1);
this.captured().form().serializeOn(_1f1);
this.serializeOn(_1f1);
}
catch(e){
this.serializeOn(_1f1);
}
}.bind(this)).sendOn(_1f0);
},sendWithCondition:function(_1f2){
if(_1f2(this)){
this.send();
}
},send:function(){
this.cancel();
this.sendOn(connection);
},sendFull:function(){
this.cancel();
this.sendFullOn(connection);
}});
This.IE.Event=This.Event.subclass({target:function(){
return this.event.srcElement?_1e7.adaptToElement(this.event.srcElement):null;
},cancelBubbling:function(){
this.event.cancelBubble=true;
},cancelDefaultAction:function(){
this.event.returnValue=false;
}});
This.Netscape.Event=This.Event.subclass({target:function(){
return this.event.target?_1e7.adaptToElement(this.event.target):null;
},cancelBubbling:function(){
this.event.stopPropagation();
},cancelDefaultAction:function(){
this.event.preventDefault();
}});
var _1f3={isAltPressed:function(){
return this.event.altKey;
},isCtrlPressed:function(){
return this.event.ctrlKey;
},isShiftPressed:function(){
return this.event.shiftKey;
},isMetaPressed:function(){
return this.event.metaKey;
},serializeKeyAndMouseEventOn:function(_1f4){
_1f4.add("ice.event.alt",this.isAltPressed());
_1f4.add("ice.event.ctrl",this.isCtrlPressed());
_1f4.add("ice.event.shift",this.isShiftPressed());
_1f4.add("ice.event.meta",this.isMetaPressed());
}};
var _1f5={isMouseEvent:function(){
return true;
},serializeOn:function(_1f6){
this.serializeEventOn(_1f6);
this.serializeKeyAndMouseEventOn(_1f6);
this.pointer().serializeOn(_1f6);
_1f6.add("ice.event.left",this.isLeftButton());
_1f6.add("ice.event.right",this.isRightButton());
}};
This.IE.MouseEvent=This.IE.Event.subclass({pointer:function(){
return new _1e9.Point(this.event.clientX+(document.documentElement.scrollLeft||document.body.scrollLeft),this.event.clientY+(document.documentElement.scrollTop||document.body.scrollTop));
},isLeftButton:function(){
return this.event.button==1;
},isRightButton:function(){
return this.event.button==2;
}});
This.IE.MouseEvent.methods(_1f3);
This.IE.MouseEvent.methods(_1f5);
This.Netscape.MouseEvent=This.Netscape.Event.subclass({pointer:function(){
return new _1e9.Point(this.event.pageX,this.event.pageY);
},isLeftButton:function(){
return this.event.which==1;
},isRightButton:function(){
return this.event.which==3;
}});
This.Netscape.MouseEvent.methods(_1f3);
This.Netscape.MouseEvent.methods(_1f5);
var _1f7={keyCharacter:function(){
return String.fromCharCode(this.keyCode());
},isEnterKey:function(){
return this.keyCode()==13;
},isEscKey:function(){
return this.keyCode()==27;
},isBackspaceKey:function(){
return this.keyCode()==8;
},isDeleteKey:function(){
return this.keyCode()==46||this.keyCode()==63272;
},isSpaceKey:function(){
return this.keyCode()==32;
},isTabKey:function(){
return this.keyCode()==9||(this.isShiftPressed()&&this.keyCode()==25);
},isHomeKey:function(){
return this.keyCode()==36||this.keyCode()==63273;
},isEndKey:function(){
return this.keyCode()==35||this.keyCode()==63275;
},isPageUpKey:function(){
return this.keyCode()==33||this.keyCode()==63276;
},isPageDownKey:function(){
return this.keyCode()==34||this.keyCode()==63277;
},isArrowUpKey:function(){
return this.keyCode()==38||this.keyCode()==63232;
},isArrowDownKey:function(){
return this.keyCode()==40||this.keyCode()==63233;
},isArrowLeftKey:function(){
return this.keyCode()==37||this.keyCode()==63234;
},isArrowRightKey:function(){
return this.keyCode()==39||this.keyCode()==63235;
},isKeyEvent:function(){
return true;
},serializeOn:function(_1f8){
this.serializeEventOn(_1f8);
this.serializeKeyAndMouseEventOn(_1f8);
_1f8.add("ice.event.keycode",this.keyCode());
}};
This.IE.KeyEvent=This.IE.Event.subclass({keyCode:function(){
return this.event.keyCode;
}});
This.IE.KeyEvent.methods(_1f3);
This.IE.KeyEvent.methods(_1f7);
This.Netscape.KeyEvent=This.Netscape.Event.subclass({keyCode:function(){
return this.event.which==0?this.event.keyCode:this.event.which;
}});
This.Netscape.KeyEvent.methods(_1f3);
This.Netscape.KeyEvent.methods(_1f7);
This.UnknownEvent=This.Event.subclass({initialize:function(_1f9){
this.currentElement=_1f9;
},target:function(){
return _1e7.adaptToElement(this.currentElement);
},serializeEventOn:function(_1fa){
_1fa.add("ice.event.target",this.target()&&this.target().id());
_1fa.add("ice.event.captured",this.captured()&&this.captured().id());
_1fa.add("ice.event.type","unknown");
},cancelBubbling:Function.NOOP,cancelDefaultAction:Function.NOOP});
This.Event.adaptToPlainEvent=function(e,_1fc){
return window.event?new This.IE.Event(event,_1fc):new This.Netscape.Event(e,_1fc);
};
This.Event.adaptToMouseEvent=function(e,_1fe){
return window.event?new This.IE.MouseEvent(event,_1fe):new This.Netscape.MouseEvent(e,_1fe);
};
This.Event.adaptToKeyEvent=function(e,_200){
return window.event?new This.IE.KeyEvent(event,_200):new This.Netscape.KeyEvent(e,_200);
};
This.Event.adaptToEvent=function(e,_202){
var _203=window.event||e;
if(_203){
var _204="on"+_203.type;
var _205=function(name){
return name.toLowerCase()==_204;
};
if(_1e7.prototype.KeyListenerNames.detect(_205)){
return This.Event.adaptToKeyEvent(e,_202);
}else{
if(_1e7.prototype.MouseListenerNames.detect(_205)){
return This.Event.adaptToMouseEvent(e,_202);
}else{
return This.Event.adaptToPlainEvent(e,_202);
}
}
}else{
return new This.UnknownEvent(_202);
}
};
window.$event=This.Event.adaptToEvent;
});
[Ice].as(function(This){
This.Cookie=This.Parameter.Association.subclass({initialize:function(name,_209){
this.name=name;
this.value=_209||"";
this.save();
},saveValue:function(_20a){
this.value=_20a;
this.save();
},loadValue:function(){
this.load();
return this.value;
},save:function(){
document.cookie=this.name+"="+this.value;
return this;
},load:function(){
var _20b=This.Cookie.parse().detect(function(_20c){
return this.name==_20c[0];
}.bind(this));
this.value=_20b[1];
return this;
},remove:function(){
document.cookie=this.name+"=0; expires="+(new Date).toGMTString();
}});
This.Cookie.all=function(){
return This.Cookie.parse().collect(function(_20d){
var name=_20d[0];
var _20f=_20d[1];
return new This.Cookie(name,_20f);
});
};
This.Cookie.lookup=function(name){
var _211=This.Cookie.parse().detect(function(_212){
return name==_212[0];
});
if(_211){
return new This.Cookie(_211[0],_211[1]);
}else{
throw "Cannot find cookie named: "+name;
}
};
This.Cookie.parse=function(){
return document.cookie.split("; ").collect(function(_213){
return _213.split("=");
});
};
});
function iceSubmitPartial(form,_215,evt){
form=(form?form:_215.form);
Ice.Parameter.Query.create(function(_217){
"partial".associateWith(true).serializeOn(_217);
$event(evt,_215).serializeOn(_217);
if(form&&form.id){
$element(form).serializeOn(_217);
}
if(_215&&_215.id){
$element(_215).serializeOn(_217);
}
}).send();
resetHiddenFieldsFor(form);
}
function iceSubmit(_218,_219,_21a){
_218=(_218?_218:_219.form);
var _21b=$event(_21a,_219);
var form=$element(_218);
if(_21b.isKeyEvent()){
if(_21b.isEnterKey()){
var _21d=form?form.formElements().detect(function(_21e){
return _21e.id()==_21e.form().id()+":default";
}):null;
_21b.cancelDefaultAction();
Ice.Parameter.Query.create(function(_21f){
"partial".associateWith(false).serializeOn(_21f);
_21b.serializeOn(_21f);
if(_21d){
_21d.serializeOn(_21f);
}
if(form){
form.serializeOn(_21f);
}
}).send();
}
}else{
var _220=_219&&_219.id?$element(_219):null;
Ice.Parameter.Query.create(function(_221){
"partial".associateWith(false).serializeOn(_221);
_21b.serializeOn(_221);
if(_220){
_220.serializeOn(_221);
}
if(form){
form.serializeOn(_221);
}
}).send();
}
resetHiddenFieldsFor(_218);
}
function resetHiddenFieldsFor(_222){
$enumerate(_222.elements).each(function(_223){
if(_223.type=="hidden"&&_223.name!="icefacesID"&&_223.name!="viewNumber"){
_223.value="";
}
});
}
[Ice.Document=new Object,Ice.ElementModel.Element,Ice.Connection,Ice.Ajax].as(function(This,_225,_226,Ajax){
This.Synchronizer=Object.subclass({initialize:function(_228){
this.logger=_228.child("synchronizer");
this.ajax=new Ajax.Client(this.logger);
if(window.frames[0].location.hash.length>0){
this.reload();
}
},synchronize:function(){
try{
window.frames[0].location.hash="#reload";
this.logger.debug("mark document as modified");
this.synchronize=Function.NOOP;
}
catch(e){
this.logger.warn("could not mark document as modified",e);
}
},reload:function(){
try{
this.logger.info("synchronize body");
this.ajax.getAsynchronously(document.URL,"",function(_229){
_229.setRequestHeader("Connection","close");
_229.on(_226.Ok,function(){
var text=_229.content();
var _22b="<BODY";
var end="</BODY>";
_225.adaptToElement(document.body).replaceHtml(text.substring(text.indexOf(_22b),text.lastIndexOf(end)+end.length));
});
});
}
catch(e){
this.logger.error("failed to reload body",e);
}
}});
});
[Ice.Command=new Object].as(function(This){
This.Updates=function(_22e){
$enumerate(_22e.getElementsByTagName("update")).each(function(_22f){
try{
var _230=_22f.getAttribute("address");
var html=_22f.firstChild.data.replace(/<\!\#cdata\#/g,"<![CDATA[").replace(/\#\#>/g,"]]>");
_230.asExtendedElement().replaceHtml(html);
logger.debug("applied update : "+html);
}
catch(e){
logger.error("failed to insert element: "+html,e);
}
});
};
This.Redirect=function(_232){
var url=_232.getAttribute("url");
logger.info("Redirecting to "+url);
var _234=url.contains("?")?"&rvn=":"?rvn=";
window.location.href=url+_234+viewIdentifiers().first();
};
This.SessionExpired=function(){
logger.warn("Session has expired");
statusManager.sessionExpired.on();
application.dispose();
};
This.ServerError=function(_235){
logger.error("Server side error");
logger.error(_235.firstChild.data);
statusManager.serverError.on();
application.dispose();
};
});
function viewIdentifiers(){
return $enumerate(document.forms).select(function(form){
return form["viewNumber"];
}).collect(function(form){
return form["viewNumber"].value;
}).asSet();
}
function defaultParameters(){
return Ice.Parameter.Query.create(function(_238){
_238.add("focus",currentFocus);
_238.add("window",window.identifier);
_238.add("icefacesID",window.session);
viewIdentifiers().each(function(view){
_238.add("viewNumber",view);
});
});
}
var currentFocus;
Ice.Focus=new Object();
Ice.Focus.userInterupt=false;
Ice.Focus.userInterupt=function(e){
window.logger.debug("Interup pressed");
if(Ice.Focus.userInterupt==false){
window.logger.debug("User action. Set focus will be ignored.");
Ice.Focus.userInterupt=true;
}
};
Ice.Focus.setFocus=function(id){
if((Ice.Focus.userInterupt==false)&&(id!="")&&(id!="undefined")){
try{
id.asExtendedElement().focus();
setFocus(id);
window.logger.debug("Focus Set on ["+id+"]");
}
catch(e){
window.logger.error("Failed to set focus on ["+id+"]",e);
}
}else{
window.logger.debug("Focus interupted. Not Set on ["+id+"]");
}
};
document.onKeyDown=function(_23c){
var _23d=document.onkeydown;
document.onkeydown=_23d!=null?function(e){
_23c(Ice.EventModel.Event.adaptToKeyEvent(e));
_23d(e);
}:function(e){
_23c(Ice.EventModel.Event.adaptToKeyEvent(e));
};
};
document.onMouseDown=function(_240){
var _241=document.onmousedown;
document.onmousedown=_241!=null?function(e){
_240(e);
_241(e);
}:function(e){
_240(e);
};
};
document.onKeyDown(Ice.Focus.userInterupt);
document.onMouseDown(Ice.Focus.userInterupt);
function setFocus(id){
currentFocus=id;
}
window.onScroll(function(){
currentFocus=null;
window.focus();
});
[Ice.Status=new Object].as(function(This){
This.ElementIndicator=Object.subclass({initialize:function(_246,_247){
this.elementID=_246;
this.indicators=_247;
this.indicators.push(this);
this.off();
},on:function(){
this.indicators.each(function(_248){
if(_248!=this){
_248.off();
}
}.bind(this));
this.elementID.asElement().style.visibility="visible";
},off:function(){
this.elementID.asElement().style.visibility="hidden";
}});
This.ToggleIndicator=Object.subclass({initialize:function(_249,_24a){
this.onElement=_249;
this.offElement=_24a;
this.off();
},on:function(){
this.onElement.on();
this.offElement.off();
},off:function(){
this.onElement.off();
this.offElement.on();
}});
This.PointerIndicator=Object.subclass({initialize:function(_24b){
this.element=_24b;
this.previousCursor=this.element.style.cursor;
},on:/Safari/.test(navigator.userAgent)?Function.NOOP:function(){
this.element.style.cursor="wait";
},off:/Safari/.test(navigator.userAgent)?Function.NOOP:function(){
this.element.style.cursor=this.previousCursor;
}});
This.OverlayIndicator=Object.subclass({initialize:function(_24c,_24d,_24e){
this.message=_24c;
this.description=_24d;
this.panel=_24e;
},on:function(){
this.panel.on();
messageContainer=document.createElement("div");
messageContainer.style.position="absolute";
messageContainer.style.textAlign="center";
messageContainer.style.zIndex="10001";
messageContainer.style.color="black";
messageContainer.style.backgroundColor="white";
messageContainer.style.paddingLeft="0";
messageContainer.style.paddingRight="0";
messageContainer.style.paddingTop="15px";
messageContainer.style.paddingBottom="15px";
messageContainer.style.borderBottomColor="gray";
messageContainer.style.borderRightColor="gray";
messageContainer.style.borderTopColor="silver";
messageContainer.style.borderLeftColor="silver";
messageContainer.style.borderWidth="2px";
messageContainer.style.borderStyle="solid";
messageContainer.style.width="270px";
document.body.appendChild(messageContainer);
var _24f=document.createElement("div");
_24f.appendChild(document.createTextNode(this.message));
_24f.style.marginLeft="30px";
_24f.style.textAlign="left";
_24f.style.fontSize="14px";
_24f.style.fontSize="14px";
_24f.style.fontWeight="bold";
messageContainer.appendChild(_24f);
var _250=document.createElement("div");
_250.appendChild(document.createTextNode(this.description));
_250.style.fontSize="11px";
_250.style.marginTop="7px";
_250.style.marginBottom="7px";
_250.style.fontWeight="normal";
_24f.appendChild(_250);
var _251=document.createElement("input");
_251.type="button";
_251.value="Reload";
_251.style.fontSize="11px";
_251.style.fontWeight="normal";
_251.onclick=function(){
window.location.reload();
};
messageContainer.appendChild(_251);
var _252=function(){
messageContainer.style.left=((window.width()-messageContainer.clientWidth)/2)+"px";
messageContainer.style.top=((window.height()-messageContainer.clientHeight)/2)+"px";
}.bind(this);
_252();
window.onResize(_252);
messageContainer=null;
_24f=null;
_250=null;
_251=null;
}});
This.StatusManager=Object.subclass({initialize:function(){
if("connection-status".asElement()){
this.indicators=[];
var _253=new This.ElementIndicator("connection-working",this.indicators);
var _254=new This.ElementIndicator("connection-idle",this.indicators);
this.busy=new This.ToggleIndicator(_253,_254);
this.connectionLost=new This.ElementIndicator("connection-lost",this.indicators);
this.sessionExpired=this.connectionLost;
this.serverError=this.connectionLost;
}else{
this.busy=new This.PointerIndicator(document.body);
var _255="To reconnect click the Reload button on the browser or click the button below";
this.sessionExpired=new This.OverlayIndicator("User Session Expired",_255,this);
this.connectionLost=new This.OverlayIndicator("Network Connection Interrupted",_255,this);
this.serverError=new This.OverlayIndicator("Server Internal Error",_255,this);
}
},on:function(){
document.body.style.zIndex="0";
window.frames[0].document.body.style.backgroundColor="white";
var _256=document.getElementById("history-frame");
_256.style.position="absolute";
_256.style.visibility="visible";
_256.style.backgroundColor="white";
_256.style.zIndex="10000";
_256.style.top="0";
_256.style.left="0";
var _257=function(){
_256.style.width=window.width()+"px";
_256.style.height=window.height()+"px";
};
_257();
window.onResize(_257);
}});
});
[Ice.Connection=new Object,Ice.Connection,Ice.Ajax].as(function(This,_259,Ajax){
This.BadResponse=function(_25b){
return _25b.isComplete()&&!_25b.isResponseValid();
};
This.Receive=function(_25c){
return _25c.isOkAndComplete();
};
This.Ok=function(_25d){
return _25d.isOkAndComplete();
};
This.Unavailable=function(_25e){
return _25e.isUnavailableAndComplete();
};
This.SyncConnection=Object.subclass({initialize:function(_25f,_260,_261){
this.logger=_25f.child("sync-connection");
this.channel=new Ajax.Client(this.logger);
this.defaultQuery=_261;
this.onSendListeners=[];
this.onReceiveListeners=[];
this.connectionDownListeners=[];
this.timeoutBomb={cancel:Function.NOOP};
this.logger.info("synchronous mode");
this.sendURI=_260.context+"/block/send-receive-updates";
var _262=_260.timeout?_260.timeout:5000;
this.onSend(function(){
this.timeoutBomb.cancel();
this.timeoutBomb=this.connectionDownListeners.broadcaster().delayExecutionFor(_262);
}.bind(this));
this.onReceive(function(){
this.timeoutBomb.cancel();
}.bind(this));
this.whenDown(function(){
this.timeoutBomb.cancel();
}.bind(this));
this.receiveCallback=function(_263){
try{
this.onReceiveListeners.broadcast(_263);
}
catch(e){
this.logger.error("receive broadcast failed",e);
}
}.bind(this);
this.badResponseCallback=this.connectionDownListeners.broadcaster();
},send:function(_264){
this.logger.debug("send > "+_264.asString());
var _265=_264.addQuery(this.defaultQuery());
this.channel.postAsynchronously(this.sendURI,_265.asURIEncodedString(),function(_266){
_266.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
_266.on(_259.Receive,this.receiveCallback);
_266.on(_259.BadResponse,this.badResponseCallback);
this.onSendListeners.broadcast(_266);
}.bind(this));
},onSend:function(_267){
this.onSendListeners.push(_267);
},onReceive:function(_268){
this.onReceiveListeners.push(_268);
},whenDown:function(_269){
this.connectionDownListeners.push(_269);
},shutdown:function(){
this.send=Function.NOOP;
this.onSendListeners.clear();
this.onReceiveListeners.clear();
this.onRedirectListeners.clear();
this.connectionDownListeners.clear();
}});
});
window.connection={send:Function.NOOP};
Ice.Community=new Object;
[Ice.Community.Connection=new Object,Ice.Connection,Ice.Ajax].as(function(This,_26b,Ajax){
This.AsyncConnection=Object.subclass({initialize:function(_26d,_26e,_26f){
this.logger=_26d.child("async-connection");
this.sendChannel=new Ajax.Client(this.logger.child("ui"));
this.receiveChannel=new Ajax.Client(this.logger.child("blocking"));
this.defaultQuery=_26f;
this.onSendListeners=[];
this.onReceiveListeners=[];
this.connectionDownListeners=[];
this.listener={close:Function.NOOP};
this.timeoutBomb={cancel:Function.NOOP};
this.getURI=_26e.context+"/block/receive-updates";
this.sendURI=_26e.context+"/block/send-receive-updates";
this.receiveURI=_26e.context+"/block/receive-updated-views";
var _270=_26e.timeout?_26e.timeout:5000;
this.onSend(function(){
this.timeoutBomb.cancel();
this.timeoutBomb=this.connectionDownListeners.broadcaster().delayExecutionFor(_270);
}.bind(this));
this.onReceive(function(){
this.timeoutBomb.cancel();
}.bind(this));
this.badResponseCallback=function(){
this.connectionDownBroadcaster();
}.bind(this);
this.receiveCallback=function(_271){
try{
this.onReceiveListeners.broadcast(_271);
}
catch(e){
this.logger.error("receive broadcast failed",e);
}
}.bind(this);
this.updatedViewsCallback=function(_272){
try{
var _273=_272.contentAsDOM().documentElement;
switch(_273.tagName){
case "updated-views":
this.updatedViews.saveValue(_273.firstChild.data);
break;
default:
this.receiveCallback(_272);
}
}
finally{
this.connect();
}
}.bind(this);
this.listenerInitializerProcess=function(){
try{
this.listening=Ice.Cookie.lookup("bconn");
this.updatedViews=Ice.Cookie.lookup("updates");
}
catch(e){
this.listening=new Ice.Cookie("bconn","started");
this.updatedViews=new Ice.Cookie("updates","");
this.connect();
}
}.bind(this).repeatExecutionEvery(1000);
this.updatesListenerProcess=function(){
try{
var _274=this.updatedViews.loadValue().split(" ");
if(_274.intersect(viewIdentifiers()).isNotEmpty()){
this.sendChannel.postAsynchronously(this.getURI,this.defaultQuery().asURIEncodedString(),function(_275){
_275.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
_275.on(_26b.Receive,this.receiveCallback);
}.bind(this));
this.updatedViews.saveValue(_274.complement(viewIdentifiers()).join(" "));
}
}
catch(e){
this.logger.warn("failed to listen for updates",e);
}
}.bind(this).repeatExecutionEvery(300);
this.logger.info("asynchronous mode");
},connect:function(){
this.logger.debug("closing previous connection...");
this.listener.close();
this.logger.debug("connect...");
this.connectionDownBroadcaster=this.connectionDownListeners.broadcaster();
this.listener=this.receiveChannel.getAsynchronously(this.receiveURI,this.defaultQuery().asURIEncodedString(),function(_276){
_276.on(_26b.BadResponse,this.badResponseCallback);
_276.on(_26b.Receive,this.updatedViewsCallback);
}.bind(this));
},send:function(_277){
var _278=_277.addQuery(this.defaultQuery());
this.logger.debug("send > "+_278.asString());
this.sendChannel.postAsynchronously(this.sendURI,_278.asURIEncodedString(),function(_279){
_279.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
_279.on(_26b.Receive,this.receiveCallback);
this.onSendListeners.broadcast(_279);
}.bind(this));
},onSend:function(_27a){
this.onSendListeners.push(_27a);
},onReceive:function(_27b){
this.onReceiveListeners.push(_27b);
},whenDown:function(_27c){
this.connectionDownListeners.push(_27c);
},shutdown:function(){
this.send=Function.NOOP;
this.listener.close();
this.onSendListeners.clear();
this.onReceiveListeners.clear();
this.connectionDownListeners.clear();
this.updatesListenerProcess.cancel();
this.listenerInitializerProcess.cancel();
this.listening.remove();
this.updatedViews.remove();
}});
});
[Ice.Community].as(function(This){
This.Application=Object.subclass({initialize:function(){
var _27e=window.logger=this.logger=new Ice.Log.Logger(["window"]);
this.logHandler=window.console&&window.console.firebug?new Ice.Log.FirebugLogHandler(_27e):new Ice.Log.WindowLogHandler(_27e,window);
var _27f=new Ice.Document.Synchronizer(_27e);
window.statusManager=new Ice.Status.StatusManager();
window.identifier=Math.round(Math.random()*10000).toString();
window.connection=this.connection=configuration.synchronous?new Ice.Connection.SyncConnection(_27e,configuration.connection,defaultParameters):new This.Connection.AsyncConnection(_27e,configuration.connection,defaultParameters);
window.onKeyPress(function(e){
if(e.isEscKey()){
e.cancelDefaultAction();
}
});
this.connection.onSend(function(){
Ice.Focus.userInterupt=false;
});
this.connection.onReceive(function(_281){
var _282=_281.contentAsDOM().documentElement;
switch(_282.tagName){
case "updates":
Ice.Command.Updates(_282);
break;
case "redirect":
Ice.Command.Redirect(_282);
break;
case "server-error":
Ice.Command.ServerError(_282);
break;
case "session-expired":
Ice.Command.SessionExpired(_282);
break;
default:
throw "Unknown message received: "+_282.tagName;
}
}.bind(this));
this.connection.onReceive(function(){
_27f.synchronize();
});
this.connection.whenDown(function(){
_27e.warn("connection to server was lost");
statusManager.connectionLost.on();
this.dispose();
}.bind(this));
this.connection.onSend(function(_283){
statusManager.busy.on();
});
this.connection.onReceive(function(_284){
statusManager.busy.off();
});
this.logger.info("page loaded!");
},dispose:function(){
this.connection.shutdown();
this.logger.info("page unloaded!");
this.logHandler.disable();
this.dispose=Function.NOOP;
}});
window.onLoad(function(){
try{
this.application=new This.Application;
}
catch(ignore){
if(console){
console.error(ignore);
}
}
});
window.onUnload(function(){
try{
this.application.dispose();
}
catch(ignore){
if(console){
console.error(ignore);
}
}
});
});

if (typeof OpenAjax!='undefined' && typeof OpenAjax.registerLibrary!='undefined' && typeof OpenAjax.registerGlobals!='undefined'){OpenAjax.registerLibrary('icefaces-d2d','http://www.icefaces.org/','1.5.3');
OpenAjax.registerGlobals('icefaces-d2d', ['Class','Enumerable','defaultParameters','iceSubmit','$A','resetHiddenFieldsFor','$H','setFocus','property','$R','$break','Hash','ObjectRange','$w','Template','current','$continue','PeriodicalExecuter','viewIdentifiers','Try','currentFocus','Abstract','Ice','iceSubmitPartial']);}

