$(function () {

    function blockUI(message) {
        $.blockUI({message: '<h1><img src="/busy.gif" /> ' + message + '</h1>'});
    }

    function successHandler(data) {
        if (data.user == null) {
            window.location.replace("login.jsp");
        }
        $("#user").text("Logged as: " + data.user);
        $("#name").text(data.name);
        $("#vat").text(data.vat);
        $("#address").text(data.address);
    }

    function getActionFormData(action) {
        return {
            analysis: action
        };
    }

    function sendLogoutRequest() {
        blockUI("Logging out...");
        $.get("upload", getActionFormData("logout"), successHandler, "json");
    }

    function sendFullUserRequest() {
        blockUI("Just one moment...");
        $.get("upload", getActionFormData("getFullUser"), successHandler, "json");
    }


    $(function () {
        // Logout
        $("#logoutClick").click(sendLogoutRequest);

        $.onload = sendFullUserRequest();

        // unblock when ajax activity stops
        $(document).ajaxStop($.unblockUI);
    });

});