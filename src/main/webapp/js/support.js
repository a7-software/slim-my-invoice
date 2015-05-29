$(function () {

    function blockUI(message) {
        $.blockUI({message: '<h1><img src="/busy.gif" /> ' + message + '</h1>'});
    }

    function successHandler(data) {
        if (data.user == null) {
            window.location.replace("login.jsp");
        }
        $("#user").text("Logged as: " + data.user);
    }

    function getActionFormData(type) {
        return {
            analysis: type
        };
    }

    function sendLogoutRequest() {
        blockUI("Logging out...");
        $.get("upload", getActionFormData("logout"), successHandler, "json");
    }

    function sendUserRequest() {
        blockUI("Just one moment...");
        $.get("upload", getActionFormData("getUser"), successHandler, "json");
    }


    $(function () {
        // Logout
        $("#logoutClick").click(sendLogoutRequest);

        $.onload = sendUserRequest();

        // unblock when ajax activity stops
        $(document).ajaxStop($.unblockUI);
    });
});