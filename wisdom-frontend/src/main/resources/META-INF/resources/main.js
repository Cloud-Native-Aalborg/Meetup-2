$(document).ready(function(){
    $.getJSON({url: "http://" + window.location.hostname + "/wisdom/random", success: function(result) {
        $("#quote").html(result['message']);
  }});
});