$(function () {

    function blockUI(message) {
        $.blockUI({message: '<h1><img src="/busy.gif" /> ' + message + '</h1>'});
    }

    function successHandler(data) {
        if (data.user == null) {
            window.location.replace("login.jsp");
        }
        if (data.result == "success") {
            $("#message").text(data.message);
        } else {
            $("#message").text('ERROR: ' + data.message);
        }
        $("#user").text("Logged as: " + data.user);
        document.getElementById("invoicesTable").innerHTML = data.table;
        var div = document.getElementById("message");
        if (div != null) {
            div.style.visibility = "visible";
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

    function sendInvoicesRequest() {
        blockUI("Fetching invoice list...");
        $.get("upload", getActionFormData("getInvoices"), successHandler, "json");
    }

    $(function () {
        // Logout
        $("#logoutClick").click(sendLogoutRequest);

        $.onload = sendInvoicesRequest();

        // unblock when ajax activity stops
        $(document).ajaxStop($.unblockUI);
    });

});