
(function() {
 

    function getWordAtPoint(elem, x, y) {
        if(elem.nodeType == elem.TEXT_NODE) {
            var range = elem.ownerDocument.createRange();
            range.selectNodeContents(elem);
            var currentPos = 0;
            var endPos = range.endOffset;
            while(currentPos+1 < endPos) {
                range.setStart(elem, currentPos);
                range.setEnd(elem, currentPos+1);
                if(range.getBoundingClientRect().left <= x && range.getBoundingClientRect().right  >= x &&
                   range.getBoundingClientRect().top  <= y && range.getBoundingClientRect().bottom >= y)
                {
                    range.expand("word");
                    var ret = range.toString();
                    range.detach();
                    return(ret);
                }
                currentPos += 1;
            }
        }
        else {
            for(var i = 0; i < elem.childNodes.length; i++) {
                var range = elem.childNodes[i].ownerDocument.createRange();
                range.selectNodeContents(elem.childNodes[i]);
                if(range.getBoundingClientRect().left <= x && range.getBoundingClientRect().right  >= x &&
                    range.getBoundingClientRect().top  <= y && range.getBoundingClientRect().bottom >= y)
                {
                    range.detach();
                    return(getWordAtPoint(elem.childNodes[i], x, y));
                }
                else {
                    range.detach();
                }
            }
        }
        return(null);
    }
 
    var doitPresenter = false;
    var correcSCale = 1;
 
    function correcSc(sc) {
        correcSCale = sc;
    }

    touche = function(e){ // on page load
 
 var xx = e.pageX - document.body.scrollLeft; // + document.documentElement.scrollLeft;
 var yy = e.pageY - document.body.scrollTop; // + document.documentElement.scrollTop;
 
        //var word = getFullWord(e);
        var word = getWordAtPoint(e.target,xx,yy);
     //   var word = getWordUnderCursor(e);
 
        if (doitPresenter && word) {window.location = "urlscheme://urlscheme/" + word;}
    }

        document.addEventListener('touchstart',function(e){ // on page load
            var xt = e.pageX - document.body.scrollLeft; // + document.documentElement.scrollLeft;
            var yt = e.pageY - document.body.scrollTop; // + document.documentElement.scrollTop;
                                  
            window.location = "urlloc://urlloc/" + " x : " + xt + " - y : " + yt;
         //   window.location = "urlloc://urlloc/" + " scrleft : " + document.body.scrollLeft + " - scrtop : " + document.body.scrollTop;
                                  doitPresenter = true;
        }, false);
 
 
      //  document.addEventListener('touchend', touche, false);
        document.addEventListener('touchend', touche, false);
        document.addEventListener('touchmove', function(e){ // on page load
                //JSInterface.dismissFen();
            //    rangy.getSelection().removeAllRanges();
             //   clearTimeout(pressTimer);
                    doitPresenter = false;
          }, false);

        window.onload = function() {
            //document.designMode = "on";
        };
 
}) ()
