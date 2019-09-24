$(document).ready(function(){
    setTimeout(getQuote, 30000);

    $("#newButton").on("click", getQuote);
});


function getQuote() {
    console.log("getting quote...");
    $.getJSON({url: "http://" + window.location.hostname + "/wisdom/random",
        success: function(result) {
            $("#quote").html(result['message']);
        },
        error:  function(result) {
            $("#quote").html("Oh no - error getting quote!");
        }
    });
    setTimeout(getQuote, 30000);

}