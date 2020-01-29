
(function() {
 

function getWordUnderCursor(event) {
    var range, textNode, offset;
    
    if (document.caretPositionFromPoint) {    // Firefox
        range = document.caretPositionFromPoint(event.pageX, event.pageY);
        textNode = range.offsetNode;
        offset = range.offset;
        window.location = "yoururlscheme://somehost?greeting=hello" + "caretpos-";
    } else if (document.caretRangeFromPoint) {     // Chrome
 
        window.location = "yoururlscheme://somehost?greeting=hello" + "caretrange";
 range = document.caretRangeFromPoint(event.pageX, event.pageY);
 textNode = range.startContainer;
 offset = range.startOffset;
    }
 
    //data contains a full sentence
    //offset represent the cursor position in this sentence
    var data = textNode.data,
    i = offset,
    begin,
    end;
    
    //Find the begin of the word (space)
    while (i > 0 && data[i] !== " ") { --i; };
    begin = i;
    
    //Find the end of the word
    i = offset;
    while (i < data.length && data[i] !== " ") { ++i; };
    end = i;
    
    //Return the word under the mouse cursor
    return data.substring(begin, end);
}
 

 function getFullWord(event) {
    var i, begin, end, range, textNode, offset;
 
    if (document.caretRangeFromPoint) {
        range = document.caretRangeFromPoint(event.pageX, event.pageY);
 
        textNode = range.startContainer;
        offset = range.startOffset;
    }
 
    // Only act on text nodes
    if (!textNode || textNode.nodeType !== Node.TEXT_NODE) {
        return "";
    }
 
    var data = textNode.textContent;
 
    // Sometimes the offset can be at the 'length' of the data.
    // It might be a bug with this 'experimental' feature
    // Compensate for this below
    if (offset >= data.length) {
        offset = data.length - 1;
    }
 
    // Ignore the cursor on spaces - these aren't words
    if (isW(data[offset])) {
        return "";
    }
 
    // Scan behind the current character until whitespace is found, or beginning
    i = begin = end = offset;
    while (i > 0 && !isW(data[i - 1])) {
        i--;
    }
    begin = i;
 
    // Scan ahead of the current character until whitespace is found, or end
    i = offset;
    while (i < data.length - 1 && !isW(data[i + 1])) {
        i++;
    }
    end = i;
 
    // This is our temporary word
    var word = data.substring(begin, end + 1);
 
 
 
    // If at a node boundary, cross over and see what
    // the next word is and check if this should be added to our temp word
    if (end === data.length - 1 || begin === 0) {
 
        var nextNode = getNextNode(textNode);
        var prevNode = getPrevNode(textNode);
 
        // Get the next node text
        if (end == data.length - 1 && nextNode) {
            var nextText = nextNode.textContent;
 
            // Add the letters from the next text block until a whitespace, or end
            i = 0;
            while (i < nextText.length && !isW(nextText[i])) {
                word += nextText[i++];
            }
 
        }
        else if (begin === 0 && prevNode) {
                // Get the previous node text
            var prevText = prevNode.textContent;
 
 
            // Add the letters from the next text block until a whitespace, or end
            i = prevText.length - 1;
            while (i >= 0 && !isW(prevText[i])) {
                word = prevText[i--] + word;
            }
        }
    }
    return word;
 }

 // Helper functions
 
 // Whitespace checker
 function isW(s) {
    return /[ \f\n\r\t\v\u00A0\u2028\u2029]/.test(s);
 }
 
 // Barrier nodes are BR, DIV, P, PRE, TD, TR, ...
 function isBarrierNode(node) {
    return node ? /^(BR|DIV|P|PRE|TD|TR|TABLE)$/i.test(node.nodeName) : true;
 }
 
    // Try to find the next adjacent node
 function getNextNode(node) {
    var n = null;
        // Does this node have a sibling?
    if (node.nextSibling) {
    n = node.nextSibling;
 
    // Doe this node's container have a sibling?
    } else if (node.parentNode && node.parentNode.nextSibling) {
        n = node.parentNode.nextSibling;
    }
    return isBarrierNode(n) ? null : n;
 }
 
 // Try to find the prev adjacent node
 function getPrevNode(node) {
    var n = null;
 
    // Does this node have a sibling?
    if (node.previousSibling) {
    n = node.previousSibling;
 
    // Doe this node's container have a sibling?
    } else if (node.parentNode && node.parentNode.previousSibling) {
        n = node.parentNode.previousSibling;
    }
    return isBarrierNode(n) ? null : n;
 }
 
 // REF: http://stackoverflow.com/questions/3127369/how-to-get-selected-textnode-in-contenteditable-div-in-ie
 function getChildIndex(node) {
    var i = 0;
    while( (node = node.previousSibling) ) {
        i++;
    }
    return i;
 }




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
 
 var xx = e.pageX + 20;//document.body.scrollLeft; // + document.documentElement.scrollLeft;
 var yy = e.pageY + 30;//document.body.scrollTop; // + document.documentElement.scrollTop;
 
        //var word = getFullWord(e);
 
        var word = getWordAtPoint(e.target,xx,yy);
     //   var word = getWordUnderCursor(e);
 
        if (doitPresenter && word) {window.location = "urlscheme://urlscheme/" + word;}
 
    }

 
        document.addEventListener('touchstart',function(e){ // on page load
            var xt = e.pageX + document.body.scrollLeft; // + document.documentElement.scrollLeft;
            var yt = e.pageY + document.body.scrollTop; // + document.documentElement.scrollTop;
                                  
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
 
        };
 
}) ()
