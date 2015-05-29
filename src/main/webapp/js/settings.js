$(function () {
    var popGrowlDBReset;

    function blockUI(message) {
        $.blockUI({message: '<h1><img src="/busy.gif" /> ' + message + '</h1>'});
    }

    function endAjax() {
        $.unblockUI();
        growl();
    }

    function growl() {
        if (popGrowlDBReset) {
            $.growlUI('Database reset!', '');
            popGrowlDBReset = false;
        }
    }

    function successHandler(data) {
        if (data.user == null) {
            window.location.replace("login.jsp");
        }

        $("#user").text("Logged as: " + data.user);

        popGrowlDBReset = false;
        if (data.message == "DBResetOK") {
            popGrowlDBReset = true;
        }
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

    function sendResetDBRequest() {
        blockUI("Resetting the database...");
        $.get("upload", getActionFormData("reset_db"), successHandler, "json");
    }

    function sendUserRequest() {
        blockUI("Just one moment...");
        $.get("upload", getActionFormData("getUser"), successHandler, "json");
    }


    $(function () {
        // Logout
        $("#logoutClick").click(sendLogoutRequest);
        $("#reset_db").click(sendResetDBRequest);

        $.onload = sendUserRequest();

        // unblock when ajax activity stops
        $(document).ajaxStop(endAjax);
    });
});