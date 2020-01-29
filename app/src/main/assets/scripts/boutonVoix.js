
$(function() {
  
  cachePassiveX();
  var el1X = document.getElementById("boutonVoixPassiveX");
  el1X.addEventListener("touchstart", cacheActiveX, false);
  
  var el2X = document.getElementById("boutonVoixActiveX");
  el2X.addEventListener("touchstart", cachePassiveX, false);
  
  
  });


function cacheActiveX() {
    $('#voixPassiveX').show();
    $('#voixActiveX').hide();
    $('#boutonVoixActiveX').css('color','blue');
    $('#boutonVoixPassiveX').css('color','black');
    
}
function cachePassiveX() {
    $('#voixActiveX').show();
    $('#voixPassiveX').hide();
    $('#boutonVoixActiveX').css('color','black');
    $('#boutonVoixPassiveX').css('color','blue');
}
