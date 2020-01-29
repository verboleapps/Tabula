
$(function() {
  cacheAnalyseX();
  var el1 = document.getElementById("analyseX");
  el1.addEventListener("touchstart", cacheDefX, false);
  
  var el2 = document.getElementById("definitionX");
  el2.addEventListener("touchstart", cacheAnalyseX, false);
  
  });

function cacheDefX() {
    $('#anaX').show();
    $('#defX').hide();
    $('#definitionX').attr('class','defanalyse2G');
    $('#boutonDefX').css('color','blue');
    $('#analyseX').attr('class','defanalyse1D');
    $('#boutonAnalyseX').css('color','black');
}

function cacheAnalyseX() {
    $('#defX').show();
    $('#anaX').hide();
    $('#definitionX').attr('class','defanalyse1G');
    $('#boutonDefX').css('color','black');
    $('#analyseX').attr('class','defanalyse2D');
    $('#boutonAnalyseX').css('color','blue');
}
