$(function () {

    var MIN_PWD_LENGTH = 8;

    function blockUI(message) {
        $.blockUI({message: '<h1><img src="/busy.gif" /> ' + message + '</h1>'});
    }

    function loginSuccessRedirect() {
        alert("Password changed!");
        window.location = "../login.jsp";
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

    function getChangePasswordFormData(pwdLenCheck, pwdMatchCheck) {
        return {
            analysis: "changePassword",
            username: $("#username").val(),
            oldPwd: CryptoJS.SHA256($("#oldPwd").val()).toString(),
            newPwd: CryptoJS.SHA256($("#newPwd1").val()).toString(),
            passwordMinLenReached: pwdLenCheck,
            passwordMatched: pwdMatchCheck
        };
    }

    function sendChangePasswordRequest() {
        if ($("#newPwd1").val().toString().length < MIN_PWD_LENGTH) {
            blockUI("Processing request...")
            $.get("upload", getChangePasswordFormData("false", "null"), successHandler, "json");
        }
        else {
            if ($("#newPwd1").val().toString() != $("#newPwd2").val().toString()) {
                blockUI("Processing request...")
                $.get("upload", getChangePasswordFormData("true", "false"), successHandler, "json");
            }
            else {
                blockUI("Processing request...")
                $.get("upload", getChangePasswordFormData("true", "true"), successHandler, "json");
            }
        }
    }

    $(function () {
        $("#submit").click(sendChangePasswordRequest);

        // unblock when ajax activity stops
        $(document).ajaxStop($.unblockUI);
    });
});