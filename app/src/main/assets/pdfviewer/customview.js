  //
    // NOTE: 
    // Modifying the URL below to another server will likely *NOT* work. Because of browser
    // security restrictions, we have to use a file server with special headers
    // (CORS) - most servers don't support cross-origin browser requests.
    //
    
    


    //
    // Disable workers to avoid yet another cross-origin issue (workers need the URL of
    // the script to be loaded, and currently do not allow cross-origin scripts)
    //
    var pdfDoc = null,
            pageNum = 1,
            scale = 1.0;

    var canvasOffsetTop = -1;
    var canvasOffsetLeft = -1;
    PDFJS.disableWorker = false;


  // === code initial : https://github.com/pauldmps/Android-pdf.js/blob/master/assets/pdfviewer/customview.js

    //
    // Get page info from document, resize canvas accordingly, and render page
    //
    function renderPage(num) {
          // Using promise to fetch the page

          pdfDoc.getPage(num).then(function(page) {

            var viewport = page.getViewport(scale);
            var ancCanvas = jQuery("#nouvCanvas");
            if (ancCanvas) {
                ancCanvas.remove();
            }
            var $canvas = jQuery("<canvas id=\"nouvCanvas\"></canvas>");
            var canvas = $canvas.get(0);
            //Set the canvas height and width to the height and width of the viewport
            var context = canvas.getContext("2d");
            canvas.height = viewport.height;
            canvas.width = viewport.width;

            //Append the canvas to the pdf container div
            var $pdfContainer = jQuery("#pdfContainer");
            $pdfContainer.css("height", canvas.height + "px").css("width", canvas.width + "px");

            $pdfContainer.append($canvas);

            var outputScale = getOutputScale();
            //JSInterface.debugJS('outp scale : ' + outputScale.sx);

            var canvasOffset = $canvas.offset();
            if (canvasOffsetLeft == -1) {
                canvasOffsetLeft = canvasOffset.left;
                canvasOffsetTop = canvasOffset.top;
            }

            JSInterface.debugJS('nouv page  : canvasOffset.top ' + canvasOffset.top + ' canvasOffset.left ' + canvasOffset.left);

            var $textLayerDiv = jQuery("<div />")
                .addClass("textLayer")
                .css("height", viewport.height + "px") //.css("height", viewport.height * outputScale.sy + "px")
                .css("width", viewport.width + "px") //.css("width", viewport.width * outputScale.sx + "px")
                .offset({
                    top: canvasOffsetLeft, //canvasOffset.top,
                    left: canvasOffsetTop //canvasOffset.left
                });

// NE PAS oublier cette ligne -> sinon, pdf s'affiche mais selection marche pas
            jQuery("#pdfContainer").append($textLayerDiv);

            /*
//The following few lines of code set up scaling on the context if we are on a HiDPI display
//=====> marche pas
            if (outputScale.scaled) {
                var cssScale = 'scale(' + (1 / outputScale.sx) + ', ' +
                    (1 / outputScale.sy) + ')';
                CustomStyle.setProp('transform', canvas, cssScale); // sinon reste grand bien scale mais coupe
                CustomStyle.setProp('transformOrigin', canvas, '0% 0%');

                if ($textLayerDiv.get(0)) {
                       CustomStyle.setProp('transform', $textLayerDiv.get(0), cssScale);
                       CustomStyle.setProp('transformOrigin', $textLayerDiv.get(0), '0% 0%');
                }
             }

             context._scaleX = outputScale.sx; // change rien ??
             context._scaleY = outputScale.sy;
             if (outputScale.scaled) {
                context.scale(outputScale.sx, outputScale.sy); // sinon reste petit mais entier
             }
*/
            page.getTextContent().then(function (textContent) {
                var textLayer = new TextLayerBuilder($textLayerDiv.get(0), 0); //The second zero is an index identifying
                //the page. It is set to page.number - 1.
                textLayer.setTextContent(textContent);
                var renderContext = {
                    canvasContext: context,
                    viewport: viewport,
                    textLayer: textLayer
                };
                page.render(renderContext);
                rescaleCanvas(canvas);
            });
           });
          // Update page counters
          //document.getElementById('page_num').textContent = pageNum;
          //document.getElementById('page_count').textContent = pdfDoc.numPages;

          JSInterface.getPages(pageNum, pdfDoc.numPages);

        }

    //
    // Go to previous page
    //
    function enArriere() {
      if (pageNum <= 1)
        return;
      pageNum--;
      renderPage(pageNum);
      JSInterface.getPages(pageNum, pdfDoc.numPages);
    }

    //
    // Go to next page
    //
    function enAvant() {
      if (pageNum >= pdfDoc.numPages)
        return;
      pageNum++;
      renderPage(pageNum);
      JSInterface.getPages(pageNum, pdfDoc.numPages);
    }

    function agrandit() {
        scale += 10/100;
        renderPage(pageNum);
    }
    function rapetisse() {
        scale -= 10/100;
        renderPage(pageNum);
    }
    //
    // Asynchronously download PDF as an ArrayBuffer
    //

    PDFJS.getDocument(url).then(function getPdfHelloWorld(_pdfDoc) {
      pdfDoc = _pdfDoc;
      renderPage(pageNum);
    });

//https://www.html5rocks.com/en/tutorials/canvas/hidpi/
//http://through-the-interface.typepad.com/through_the_interface/2017/02/scaling-html-canvases-for-hidpi-screens.html
    function rescaleCanvas(canvas) {

      var ctx = canvas.getContext('2d');
      var devicePixelRatio = window.devicePixelRatio || 1;
      var backingStoreRatio = ctx.webkitBackingStorePixelRatio ||

                          ctx.mozBackingStorePixelRatio ||

                          ctx.msBackingStorePixelRatio ||

                          ctx.oBackingStorePixelRatio ||

                          ctx.backingStorePixelRatio || 1;

      var  ratio = devicePixelRatio / backingStoreRatio;
      // upscale the canvas if the two ratios don't match

      if (devicePixelRatio !== backingStoreRatio) {

        var  oldWidth = canvas.width;
        var  oldHeight = canvas.height;
        canvas.width = oldWidth * ratio;
        canvas.height = oldHeight * ratio;
        canvas.style.width = oldWidth + 'px';
        canvas.style.height = oldHeight + 'px';
        // now scale the context to counter
        // the fact that we've manually scaled
        // our canvas element
        ctx.scale(ratio, ratio);
      }

    }




