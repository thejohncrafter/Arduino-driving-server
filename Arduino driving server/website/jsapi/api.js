
/**
* This field stores the iframe used to send requestes to the server.
*/
var iframe;

/**
* This field stores the default "onAnswer()" method.
*/
var defaultOnAnswer = function(answ){console.log('Answer : ' + answ);};

/**
* This method is called when the server's answer is received by the iframe.
*/
var onAnswer = defaultOnAnswer;

/**
* This field stores the HIDs of all the hardwares (datas sent by the server).
*/
var HIDs;

/**
* This field stores the path to the root.
*/
var pathPre;

/**
* This function is used to initialize the iframe.
*/
function init(){
    
    iframe = document.getElementById('iframe');
    pathPre = iframe.src;
    
    var body = document.body,
        html = document.documentElement;
    var height = Math.max( body.scrollHeight, body.offsetHeight, html.clientHeight, html.scrollHeight, html.offsetHeight );
    
    parent.setIframeHeight(height);

    getHIDs();
    
}

/**
* This function is used by answer() to get the response of a request.
*/
function getResponse(){

    console.log('Getting response...');

    if(iframe.contentDocument){
        
        return iframe.contentDocument.getElementById('RESPONSE').innerHTML;

    }else{
        
        return iframe.contentWindow.getElementById('RESPONSE').innerHTML;

    }

}

/**
* This method is used to handle the response of the requestes.
*/
function answer(){

    try{

        // errors can occurate here (null pointer)...
        var answ = getResponse();

        onAnswer(answ);

    }catch(err){

        console.error('Error in answer() : ' + err + '; maybe the iframe has a bad reference.');

    }

}

/**
* This method is called when pressing on the "Check connection" button.
*/
function check(){

    onAnswer = function(answ){
        
        alert('You are connected (server answer : ' + answ + ') !');
        
        onAnswer = defaultOnAnswer;

    }

    iframe.src = pathPre + 'requestes?r=CHECK';

}

/**
* This method is used to get a hardware (the json object used to describe it) in HIDs.
*/
function getHardwareByName(name){
    
    console.log('searching for : ' + name);

    for(var i = 0; i < HIDs.HIDS.length; i++){
        
        var v = HIDs.HIDS[i];
        
        if(v.NAME === name){

            return v;

        }

    }

    console.log('not found');

}

/**
* This method is used to get the HIDs of all the hardwares by sending a request to the server.
*/
function getHIDs(){

    onAnswer = function(answ){

        HIDs = JSON.parse(answ);
        
        onAnswer = defaultOnAnswer;

    }

    iframe.src = pathPre + 'requestes?r=HIDS';

}

/**
* This method is used to get the HIDs of all the hardwares by sending a request to the server.
* The callback method when data comes.
*/
function getHIDs(callback){

    onAnswer = function(answ){
        
        HIDs = JSON.parse(answ);
        
        onAnswer = defaultOnAnswer;
        callback(HIDs);

    }

    iframe.src = pathPre + 'requestes?r=HIDS';

}

/**
* This method sends the given request to the server.
*/
function send(data){
    
    iframe.src = pathPre + 'requestes?r=SEND&p=' + data;
    
}

/**
* This method sends the given request to the server, 
* and calls the callback method when data comes.
*/
function send(data, callback){
    
    onAnswer = callback;
    iframe.src = pathPre + 'requestes?r=SEND&p=' + data;
    onAnswer = defaultOnAnswer;
    
}

/**
* This method is called by the page's loader script to show the driver pane.
*/
function loaded(){
    
    document.getElementById('onDisconnected').style.display = 'none';
    document.getElementById('onConnected').style.display = 'block';
    document.getElementById('onDisconnected').height = 0;
    console.log('Page loaded !');
    
}
