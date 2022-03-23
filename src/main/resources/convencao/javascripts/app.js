
function jsFecharDialogo(xhr, status, args, widgetVar, widgetEfeito) {
    if(args.validationFailed || !args.loggedIn) {
        PF(widgetVar).jq.effect(widgetEfeito, {times:5}, 100);
    }
    else {
        PF(widgetVar).hide();
        $('#loginLink').fadeOut();
    }
}	