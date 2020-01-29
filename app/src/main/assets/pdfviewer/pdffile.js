
var url = getURLParameter('file');

function getURLParameter(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null
    }

/*
//var url = convertDataURIToBinary(getURLParameter('file'));

function convertDataURIToBinary(dataURI) {
  var raw = atob("'" + dataURI + "'");
  var rawLength = raw.length;
  var array = new Uint8Array(new ArrayBuffer(rawLength));

  for(var i = 0; i < rawLength; i++) {
    array[i] = raw.charCodeAt(i);
  }
  return array;
}
*/