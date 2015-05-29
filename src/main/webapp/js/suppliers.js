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
            $("#message").text("ERROR: " + data.message);
        }
        $("#user").text("Logged as: " + data.user);
        document.getElementById("suppliersTable").innerHTML = data.table;
        /* var div = document.getElementById("message");
         div.style.visibility = "visible";*/
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

    function sendSupplierRequest() {
        blockUI("Fetching suppliers...");
        $.get("upload", getActionFormData("getSuppliers"), successHandler, "json");
    }

    $(function () {
        // Logout
        $("#logoutClick").click(sendLogoutRequest);

        $.onload = sendSupplierRequest();

        // unblock when ajax activity stops
        $(document).ajaxStop($.unblockUI);
    });
});