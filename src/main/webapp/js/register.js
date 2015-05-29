$(function () {

    var MIN_PWD_LENGTH = 8;

    function blockUI(message) {
        $.blockUI({message: '<h1><img src="/busy.gif" /> ' + message + '</h1>'});
    }

    function loginSuccessRedirect() {
        window.location = "../analyse.jsp";
    }

    function successHandler(data) {
        if (data.result == "success") {
            loginSuccessRedirect()
        }
        else if (data.result == "error") {
            $("#message_text").text(data.message);
            document.getElementById("message").style.visibility = "visible";
        }
    }

    function getRegisterFormData(pwdLenCheck, pwdMatchCheck) {
        return {
            analysis: "register",
            username: $("#username").val(),
            password: CryptoJS.SHA256($("#password").val()).toString(),
            email: $("#email").val(),
            verify_email: $("#verify_email").val(),
            passwordMinLenReached: pwdLenCheck,
            passwordMatched: pwdMatchCheck
        };
    }

    function sendRegisterRequest() {
        if ($("#password").val().toString().length < MIN_PWD_LENGTH) {
            blockUI("Processing request...")
            $.get("upload", getRegisterFormData("false", "null"), successHandler, "json");
        }
        else {
            if ($("#password").val().toString() != $("#verify_password").val().toString()) {
                blockUI("Processing request...")
                $.get("upload", getRegisterFormData("true", "false"), successHandler, "json");
            }
            else {
                blockUI("Processing request...")
                $.get("upload", getRegisterFormData("true", "true"), successHandler, "json");
            }
        }
    }

    $(function () {
        $("#submit").click(sendRegisterRequest);

        // unblock when ajax activity stops
        $(document).ajaxStop($.unblockUI);
    });
});