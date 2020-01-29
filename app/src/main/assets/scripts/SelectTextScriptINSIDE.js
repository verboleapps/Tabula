

function getFullWord(x,y) {
    var i, begin, end, range, textNode, offset;
    
    if (document.caretRangeFromPoint) {
        
        range = document.caretRangeFromPoint(x, y);
        textNode = range.startContainer;
        offset = range.startOffset;
        
        if (offset == 0) {
            var elem = document.elementFromPoint(x,y);
            return getWordAtPointOLD(elem.parentNode,x,y);
        }
        
    }
    
    var tagName = textNode.parentNode.tagName;
    if (tagName == 'a' || tagName == 'A') { // renvoie le nom du TAG en MAJ !
        var href = textNode.parentNode.getAttribute('href');
        if (href) {
        }
        return(null);
    }
    
    var data = textNode.textContent;
    
    if (offset >= data.length) {
        offset = data.length - 1;
    }
    
    // Ignore the cursor on spaces - these aren't words
    if (isW(data[offset])) {
      //  return "";
    }
    
    // Scan behind the current character until whitespace is found, or beginning
    i = begin = end = offset;
    while (i > 0 && !isSpec(data[i - 1])) {   
        i--;
    }
    begin = i;
    
    // Scan ahead of the current character until whitespace is found, or end
    i = offset;
    while (i < data.length - 1 && !isSpec(data[i + 1])) {
        i++;
    }
    end = i;
    
    // This is our temporary word
    var word = data.substring(begin, end + 1);
    
    //essai ....
    var pref = data.substring(begin - 2, begin);
    //===============
    
    if (isNaN(word)) {
        var newrange = document.createRange();
        range.selectNodeContents(textNode);
        newrange.setStart(textNode, begin);
        newrange.setEnd(textNode, end + 1);
        var highlightDiv = document.createElement('span');
                     highlightDiv.id = 'highlight32';
                     highlightDiv.style.backgroundColor = "rgba(0,150,230,0.3)";//'#00BFFF';
                     // Обернем наш Range в спан
                     newrange.surroundContents(highlightDiv);
                     newrange.detach();

        if (pref == '. ' || pref == '! ' || pref == '? ' || pref == ' ') {
            word = '?' + word;
        }
    }
    
    
    return word;
}


// Helper functions

// Whitespace checker
function isW(s) {
    return /[ \f\n\r\t\v\u00A0\u2028\u2029]/.test(s);
}


function isSpec(s) {
    return /[().;,<>?:!\[\]\"\' \f\n\r\t\v\u00A0\u2028\u2029]/.test(s);
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


function getChildIndex(node) {
    var i = 0;
    while( (node = node.previousSibling) ) {
        i++;
    }
    return i;
}
             

//===========================
function getWordAtPoint(elem, x, y) {
    if(elem.nodeType == elem.TEXT_NODE) {
             
        return getFullWord(x,y);
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

function getWordAtPointOLD(elem, x, y) {
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
             
                // var sel = document.getSelection();
                // sel.removeAllRanges();
                // sel.addRange(range);
                // pour enlever selection : window.getSelection().removeAllRanges();
             //if (isNaN(ret)) {}
             var highlightDiv = document.createElement('span');
                             highlightDiv.id = 'highlight32';
                             highlightDiv.style.backgroundColor = "rgba(0,150,230,0.3)";//'#00BFFF';
                             // Обернем наш Range в спан
                             range.surroundContents(highlightDiv);
                             range.detach();
                             return('?' + ret);

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
                return(getWordAtPointOLD(elem.childNodes[i], x, y));
            }
            else {
                range.detach();
            }
        }
    }
    return(null);
}

function unhighlight() {
    var el = document.getElementById('highlight32');
    if (el) {
        var txt = el.innerHTML;
        var txtNode = document.createTextNode(txt);
        el.parentNode.replaceChild(txtNode,el);
        return true;
    }
    return false;
}

var selec = false;

$(function() {
       document.addEventListener("mouseup",
             function(event) {

                if (!selec) {
                    JSInterface.debugJS('touche - x : ' + event.clientX + ' - y : ' + event.clientY);
                    var el = document.elementFromPoint(event.clientX,event.clientY);
                    var word = getWordAtPoint(el,event.clientX,event.clientY);
                    if (word && isNaN(word)) {
                        JSInterface.debugJS(word);
                        JSInterface.getText(word);
                        select = true;
                        return word;
                    }
                    else {
                        return "";
                    }
                }

             }
             , false);
/*
      document.addEventListener("mousemove",
            function(event) {
                 selec = true;
            }
            , false);
*/
      document.addEventListener("mousedown",
          function(event) {
            selec = unhighlight();
      }
      , false);

 });







