$(function () {

    function blockUI(message) {
        $.blockUI({message: '<h1><img src="/busy.gif" /> ' + message + '</h1>'});
    }

    function loginSuccessRedirect() {
        window.location = "../analyse.jsp";
    }

    function successHandler(data) {
        if (data.result == "success") {
            loginSuccessRedirect(data)
        }
        else if (data.result == "error") {
            $("#message_text").text(data.message);
            document.getElementById("message").style.visibility = "visible";
        }
    }

    function getLoginFormData() {
        return {
            analysis: "login",
            username: $("#username").val(),
            password: CryptoJS.SHA256($("#password").val()).toString()
        };
    }

    function sendLoginRequest() {
        blockUI("Processing login...")
        $.get("upload", getLoginFormData(), successHandler, "json");
    }


    $(function () {
        $("#submit").click(sendLoginRequest);

        // unblock when ajax activity stops
        $(document).ajaxStop($.unblockUI);
    });
});