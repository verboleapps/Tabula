
(function() {
 
    //rangy.init();
 var doitPresenter = false;
 var aEteHilighte = false;
 
 var highlighter;
 
 function highlight2() {
    var range = rangy.getSelection().getRangeAt(0);
    var newNode = document.createElement("span");
    newNode.setAttribute("class", "highlight");
    range.surroundContents(newNode)
 }
 
 function highlight() {
 
    if (highlighter) { window.location = "urlloc://urlloc/" + "highlighter";}
    else {
        window.location = "urlloc://urlloc/" + "pashighlighter2";
        highlighter = rangy.createHighlighter();
        highlighter.addClassApplier(rangy.createClassApplier("highlight"));
    }
    aEteHilighte = true;
   // highlighter.highlightSelection("highlight");
    rangy.getSelection().removeAllRanges();
 }
 
 document.addEventListener('touchstart',function(e){ // on page load
               window.location = "urlloc://urlloc/" + "x : " + e.pageX + " - y : " + e.pageY;
        doitPresenter = true;
                           
        if (aEteHilighte) { //highlighter.removeAllHighlights();
                           $('body').removeHighlight();
                           aEteHilighte = false;
                           }
        
    }, false);
 
 touche = function(e){ // on page load
    //JSInterface.dismissFen();
 
 
       // var yo = document.getSelection().toString();
       // webkit.messageHandlers.callbackHandler.postMessage(yo);
    var xt = e.pageX - document.body.scrollLeft; // + document.documentElement.scrollLeft;
    var yt = e.pageY - document.body.scrollTop; // + document.documentElement.scrollTop;
 
    if (doitPresenter) {
        var range = document.caretRangeFromPoint(xt, yt);
 
        var textNode = range.startContainer;
        var offset = range.startOffset;
        var sel = window.getSelection();
        sel.removeAllRanges();
        sel.addRange( range );
 
        var sel1 = rangy.getSelection().expand("word", {
                                        wordOptions: { includeTrailingSpace: false }
                                        });
 
        var seltxt = rangy.getSelection().toString();
 
       // $('body').highlight(seltxt);
        var re = /\W/g;   // regular expression : enleve tous les non words caracteres
        seltxt = seltxt.replace(re,"");
        /*
        re = /;/g;
        seltxt = seltxt.replace(re,"");
        re = /,/g;
        seltxt = seltxt.replace(re,"");
        seltxt = seltxt.replace("/","");
        */
        
        if (seltxt != "") {
            highlight2();
        //   jQuery.highlight(seltxt);
            aEteHilighte = true;
        //   highlight();
        window.location = "urlscheme://urlscheme/" + seltxt;
        }
    }
       // webkit.messageHandlers.callbackHandler.postMessage("-" + seltxt + "-");
 }
 
 document.addEventListener('touchend', touche, false);
 document.addEventListener('touchmove', function(e){ // on page load
                         //  window.location = "urlloc://urlloc/" + e.pageX + "-" + e.pageY;
        doitPresenter = false;
    }, false);
 
 
 $(document).ready(function() {
                   // initialization code goes here
                   window.location = "urlloc://urlloc/" + "ready";
                   rangy.init();
                   //$('body').removeHighlight();
                   
});
 
/*
 window.onload = function() {
 //document.designMode = "on";
  //  $('body').removeHighlight();
    window.location = "urlloc://urlloc/" + "onload";
    rangy.init();
 
 };
*/
 
})()
