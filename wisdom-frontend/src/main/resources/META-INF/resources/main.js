function getQuote() {
    console.log("getting quote...");
    $.getJSON({url: window.location.origin + "/wisdom/random",
        success: function(result) {
            $("#quote").html(result['quote']);
            $("#author").html("- " + result['author']);
        },
        error:  function(result) {
            $("#quote").html("Oh no - error getting quote!");
            $("#author").html("- Arne Mejlholm");
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