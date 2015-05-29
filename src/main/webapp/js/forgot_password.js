$(function () {

    function blockUI(message) {
        $.blockUI({message: '<h1><img src="/busy.gif" /> ' + message + '</h1>'});
    }

    function loginSuccessRedirect(address) {
        window.location = "../login.jsp";
        alert("New temporary password sent to " + address + "!");
    }

    function successHandler(data) {
        if (data.result == "success") {
            loginSuccessRedirect(data.address)
        }
        else if (data.result == "error") {
            $("#message_text").text(data.message);
            document.getElementById("message").style.visibility = "visible";
        }
    }

    function getPasswordRequestFormData() {
        return {
            analysis: "forgot_password",
            username: $("#username").val()
        };
    }

    function sendPasswordRequest() {
        blockUI("Processing request...")
        $.get("upload", getPasswordRequestFormData(), successHandler, "json");
    }


    $(function () {
        $("#submit").click(sendPasswordRequest);

        // unblock when ajax activity stops
        $(document).ajaxStop($.unblockUI);
    });
});