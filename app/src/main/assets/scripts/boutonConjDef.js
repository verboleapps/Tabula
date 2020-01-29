
$(function() {
  cacheDeclX();
  var el3X = document.getElementById("conjugaisonX");
  el3X.addEventListener("touchstart", cacheDefX, false);

  var el4X = document.getElementById("definitionX");
  el4X.addEventListener("touchstart", cacheDeclX, false);
  });

function cacheDefX() {

    $('#declX').show();
    $('#defX').hide();
    $('#definitionX').attr('class','conjdef2G');
    $('#boutonDefX').css('color','blue');
    $('#conjugaisonX').attr('class','conjdef1D');
    $('#boutonConjX').css('color','black');
}
function cacheDeclX() {

    $('#defX').show();
    $('#declX').hide();
    $('#definitionX').attr('class','conjdef1G');
    $('#boutonDefX').css('color','black');
    $('#conjugaisonX').attr('class','conjdef2D');
    $('#boutonConjX').css('color','blue');
}