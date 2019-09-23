$(document).ready(function(){
    getQuote();

    $("#newButton").on("click", getQuote);
});


function getQuote() {
    $.getJSON({url: "http://" + window.location.hostname + "/wisdom/random",
        success: function(result) {
            $("#quote").html(result['message']);
        },
        error:  function(result) {
            $("#quote").html("Oh no - error getting quote!");
        }
  });
}