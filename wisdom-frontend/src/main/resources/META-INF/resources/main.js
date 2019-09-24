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
    reload();
}

function reload() {
   setTimeout(getQuote, 30000);
}

$(document).ready(function(){
    $("#newButton").on("click", getQuote);
    getQuote();
});