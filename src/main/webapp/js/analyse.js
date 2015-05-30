$(function () {
    var popGrowlInvoice;
    var popGrowlSupplierSaved;
    var popGrowlSupplierUpdated;
    var popGrowlSupplierUpdatedAndSaved;

    function blockUI(message) {
        $.blockUI({message: '<h1><img src="/busy.gif" /> ' + message + '</h1>'});
    }

    function endAjax() {
        $.unblockUI();
        growlInvoice();
    }

    function growlInvoice() {
        if (popGrowlInvoice) {
            $.growlUI('Invoice saved!', '');
            popGrowlInvoice = false;
        }
        else if (popGrowlSupplierSaved) {
            $.growlUI('Supplier saved!', '');
            popGrowlSupplierSaved = false;
        }
        else if (popGrowlSupplierUpdated) {
            $.growlUI('Supplier updated!', '');
            popGrowlSupplierUpdated = false;
        }
        else if (popGrowlSupplierUpdatedAndSaved) {
            $.growlUI('Supplier saved!', 'Supplier was not in the database...');
            popGrowlSupplierUpdatedAndSaved = false;
        }
    }

    function initialisePage() {
        $(".ft-container").remove();
        $(".rect").remove();

        $("#user").text(" ");
        $("#img_name").text(" ");
        $("#message_error_text").text(" ");
        $("#message_success_text").text(" ");
        $("#vatNbIn").text(" ");
        $("#supplier").text(" ");
        $("#street").text(" ");
        $("#number").text(" ");
        $("#city").text(" ");
        $("#zip").text(" ");
        $("#country").text(" ");
        $("#vatNb").text(" ");
        $("#date").text(" ");
        $("#id").text(" ");
        $("#subtotal").text(" ");
        $("#vatRate").text(" ");
        $("#vat").text(" ");
        $("#total").text(" ");
        $('#dateEntry').val("");
        $('#idEntry').val("");
        $('#subtotalEntry').val("");
        $('#vatRateEntry').val("");
        $('#vatEntry').val("");
        $('#totalEntry').val("");

        document.getElementById("date_template").style.visibility = "hidden";
        document.getElementById("id_template").style.visibility = "hidden";
        document.getElementById("subtotal_template").style.visibility = "hidden";
        document.getElementById("vatRate_template").style.visibility = "hidden";
        document.getElementById("vat_template").style.visibility = "hidden";
        document.getElementById("total_template").style.visibility = "hidden";

        popGrowlInvoice = false;
        popGrowlSupplierSaved = false;
        popGrowlSupplierUpdated = false;
        popGrowlSupplierUpdatedAndSaved = false;

        document.getElementById("encode_supplier").style.display = "none";
        document.getElementById("update_supplier").style.display = "none";
        document.getElementById("nextPrevButtons").style.display = "none";
        document.getElementById("rotateButtons").style.display = "inline";
        document.getElementById("message_error").style.display = "none";
        document.getElementById("message_success").style.display = "inherit";

        document.getElementById("auto").disabled = true;
        document.getElementById("supplier_ocr").disabled = true;
        document.getElementById("supplier_manual").disabled = true;
        document.getElementById("date_ocr").disabled = true;
        document.getElementById("date_manual").disabled = true;
        document.getElementById("id_ocr").disabled = true;
        document.getElementById("id_manual").disabled = true;
        document.getElementById("subtotal_ocr").disabled = true;
        document.getElementById("subtotal_manual").disabled = true;
        document.getElementById("vatRate_ocr").disabled = true;
        document.getElementById("vatRate_manual").disabled = true;
        document.getElementById("vat_ocr").disabled = true;
        document.getElementById("vat_manual").disabled = true;
        document.getElementById("total_ocr").disabled = true;
        document.getElementById("total_manual").disabled = true;
        document.getElementById("encode_supplier").disabled = true;
        document.getElementById("update_supplier").disabled = true;
        document.getElementById("encode_invoice").disabled = true;
        document.getElementById("previous_page").disabled = false;
        document.getElementById("next_page").disabled = false;

        document.getElementById("message_success").style.color = "#3c763d";
        document.getElementById("message_success").style.backgroundColor = "#dff0d8";
        document.getElementById("message_success").style.borderColor = "#d6e9c6";
        document.getElementById("success_icon").style.display = "initial";

        document.getElementById("date").style.backgroundColor = "#f5f5f5";
        document.getElementById("id").style.backgroundColor = "#f5f5f5";
        document.getElementById("subtotal").style.backgroundColor = "#f5f5f5";
        document.getElementById("vatRate").style.backgroundColor = "#f5f5f5";
        document.getElementById("vat").style.backgroundColor = "#f5f5f5";
        document.getElementById("total").style.backgroundColor = "#f5f5f5";
    }

    function successHandler(data) {
        // Redirect to login page if no user logged in.
        if (data.user == null) {
            window.location.replace("login.jsp");
        }

        initialisePage()

        // Message content
        if (data.result == "success") {
            if (data.message == "hide") {
                $("#message_success_text").text("hidden");
                $("#message_error_text").text("hidden");
                document.getElementById("message_success").style.visibility = "hidden";
                document.getElementById("message_error").style.display = "none";

            }
            else {
                $("#message_success_text").text(data.message);
                document.getElementById("message_error").style.display = "none";
                document.getElementById("message_success").style.display = "inherit";
                document.getElementById("message_success").style.visibility = "visible";
                if (data.message == "First upload an invoice.") {
                    document.getElementById("message_success").style.color = "#31708f";
                    document.getElementById("message_success").style.backgroundColor = "#d9edf7";
                    document.getElementById("message_success").style.borderColor = "#bce8f1";
                    document.getElementById("success_icon").style.display = "none";
                }
            }
            // Image
            if (data.imgPath != null) {
                var rn = Math.floor(10000 * Math.random());
                $("#picture img").attr("src", data.imgPath + "?v=" + rn);
                document.getElementById("image").style.display = "inline";
            }
        }
        else {
            $("#message_error_text").text(data.message);
            document.getElementById("message_error").style.display = "inherit";
            document.getElementById("message_success").style.display = "none";
        }
        if (data.message.indexOf("encoded") > -1) {
            popGrowlInvoice = true;
        }
        else if (data.message.indexOf("Added supplier") > -1) {
            popGrowlSupplierSaved = true;
        }
        else if (data.message.indexOf("has been updated!") > -1) {
            popGrowlSupplierUpdated = true;
        }
        else if (data.message.indexOf("was not in the database and have been added") > -1) {
            popGrowlSupplierUpdatedAndSaved = true;
        }


        if (data.displaySaveSupplier) {
            document.getElementById("encode_supplier").style.display = "inline";
        }
        if (data.displayUpdateSupplier) {
            document.getElementById("update_supplier").style.display = "inline";
        }
        if (data.nextPrevButtons) {
            document.getElementById("nextPrevButtons").style.display = "block";
            if (data.grayPrevButton) {
                document.getElementById("previous_page").disabled = true;
            }
            if (data.grayNextButton) {
                document.getElementById("next_page").disabled = true;
            }
        }


        // Contextual fields
        $("#user").text("Logged as: " + data.user);
        $("#img_name").text(data.imgName);


        if (data.imgPath == null) {
            document.getElementById("image").style.display = "none";
            document.getElementById("rotateButtons").style.display = "none";
            document.getElementById("nextPrevButtons").style.display = "none";
        }
        else {
            document.getElementById("auto").disabled = false;
            document.getElementById("supplier_ocr").disabled = false;
            document.getElementById("supplier_manual").disabled = false;
            // Supplier fields
            $("#vatNbIn").text(data.vatNb);
            $("#supplier").text(data.supplier);
            $("#street").text(data.street);
            $("#number").text(data.number);
            $("#city").text(data.city);
            $("#zip").text(data.zip);
            $("#country").text(data.country);
            $("#vatNb").text(data.vatNb);

            if (data.vatNb != null && data.vatNb.length > 5) {
                document.getElementById("supplier_ocr").disabled = false;
                document.getElementById("supplier_manual").disabled = false;
                document.getElementById("date_ocr").disabled = false;
                document.getElementById("date_manual").disabled = false;
                document.getElementById("id_ocr").disabled = false;
                document.getElementById("id_manual").disabled = false;
                document.getElementById("subtotal_ocr").disabled = false;
                document.getElementById("subtotal_manual").disabled = false;
                document.getElementById("vatRate_ocr").disabled = false;
                document.getElementById("vatRate_manual").disabled = false;
                document.getElementById("vat_ocr").disabled = false;
                document.getElementById("vat_manual").disabled = false;
                document.getElementById("total_ocr").disabled = false;
                document.getElementById("total_manual").disabled = false;
                document.getElementById("encode_supplier").disabled = false;
                document.getElementById("update_supplier").disabled = false;
                if (data.displaySaveInvoice) {
                    document.getElementById("encode_invoice").disabled = false;
                }

                var zones = data.zones;
                if (zones != null) {
                    if (zones.indexOf("date") > -1) {
                        document.getElementById("date_template").style.visibility = "visible";
                    }
                    if (zones.indexOf("id") > -1) {
                        document.getElementById("id_template").style.visibility = "visible";
                    }
                    if (zones.indexOf("sub") > -1) {
                        document.getElementById("subtotal_template").style.visibility = "visible";
                    }
                    if (zones.indexOf("rate") > -1) {
                        document.getElementById("vatRate_template").style.visibility = "visible";
                    }
                    if (zones.indexOf("vat") > -1) {
                        document.getElementById("vat_template").style.visibility = "visible";
                    }
                    if (zones.indexOf("total") > -1) {
                        document.getElementById("total_template").style.visibility = "visible";
                    }
                }

                if (data.redDate != null && data.redDate) {
                    document.getElementById("date").style.backgroundColor = "lightcoral";
                }
                if (data.redRef != null && data.redRef) {
                    document.getElementById("id").style.backgroundColor = "lightcoral";
                }
                if (data.redSub != null && data.redSub) {
                    document.getElementById("subtotal").style.backgroundColor = "lightcoral";
                }
                if (data.redRate != null && data.redRate) {
                    document.getElementById("vatRate").style.backgroundColor = "lightcoral";
                }
                if (data.redVat != null && data.redVat) {
                    document.getElementById("vat").style.backgroundColor = "lightcoral";
                }
                if (data.redTotal != null && data.redTotal) {
                    document.getElementById("total").style.backgroundColor = "lightcoral";
                }

                // Data & Amounts fields
                $("#date").text(data.date);
                $("#id").text(data.id);
                $("#subtotal").text("\u20ac" + " " + data.subtotal);
                $("#vatRate").text(data.vatRate);
                $("#vat").text("\u20ac" + " " + data.vat);
                $("#total").text("\u20ac" + " " + data.total);
            }
        }
    }

    function getFormDataManual(fieldName) {
        return {
            analysis: "manual",
            field: fieldName,
            language: $("[name=language]:checked").val(),
            date: document.getElementById('dateEntry').value,
            id: document.getElementById('idEntry').value,
            subtotal: document.getElementById('subtotalEntry').value,
            vatRate: document.getElementById('vatRateEntry').value,
            vat: document.getElementById('vatEntry').value,
            total: document.getElementById('totalEntry').value
        };
    }

    function getOCRFormData(clickAction) {
        return {
            analysis: clickAction,
            ocr_areas: $("#parameters").val(),
            language: $("[name=language]:checked").val()
        };
    }

    function getSupplierFromVatFormData() {
        return {
            analysis: "get_supplier_from_vat_manual",
            language: $("[name=language]:checked").val(),
            vat: document.getElementById('supplierVatEntry').value
        };
    }

    function getActionFormData(clickAction) {
        return {
            analysis: clickAction,
            language: $("[name=language]:checked").val()
        };
    }

    function sendAutoRequest() {
        blockUI("Performing auto-analysis...");
        $.get("upload", getActionFormData("auto"), successHandler, "json");
        growlInvoice();
    };

    function sendManualDateRequest() {
        blockUI("Analysing date entry...");
        $.get("upload", getFormDataManual("date"), successHandler, "json");
    }

    function sendManualIdRequest() {
        blockUI("Analysing ref entry...");
        $.get("upload", getFormDataManual("ref"), successHandler, "json");
    }

    function sendManualSubtotalRequest() {
        blockUI("Analysing subtotal entry...");
        $.get("upload", getFormDataManual("subtotal"), successHandler, "json");
    }

    function sendManualVatRateRequest() {
        blockUI("Analysing vat rate entry...");
        $.get("upload", getFormDataManual("vatRate"), successHandler, "json");
    }

    function sendManualVatRequest() {
        blockUI("Analysing vat entry...");
        $.get("upload", getFormDataManual("vat"), successHandler, "json");
    }

    function sendManualTotalRequest() {
        blockUI("Analysing total entry...");
        $.get("upload", getFormDataManual("total"), successHandler, "json");
    }

    function sendSupplierOcrRequest() {
        blockUI("OCRing vat number...");
        $.get("upload", getOCRFormData("get_supplier_from_vat_ocr"), successHandler, "json");
    }

    function sendDateOcrRequest() {
        blockUI("OCRing date...");
        $.get("upload", getOCRFormData("date"), successHandler, "json");
    }

    function sendIdOcrRequest() {
        blockUI("OCRing reference...");
        $.get("upload", getOCRFormData("ref"), successHandler, "json");
    }

    function sendSubtotalOcrRequest() {
        blockUI("OCRing subtotal...");
        $.get("upload", getOCRFormData("subtotal"), successHandler, "json");
    }

    function sendVatRateOcrRequest() {
        blockUI("OCRing vat rate...");
        $.get("upload", getOCRFormData("vatRate"), successHandler, "json");
    }

    function sendVatOcrRequest() {
        blockUI("OCRing vat...");
        $.get("upload", getOCRFormData("vat"), successHandler, "json");
    }

    function sendTotalOcrRequest() {
        blockUI("OCRing total...");
        $.get("upload", getOCRFormData("total"), successHandler, "json");
    }

    function sendAddSupplierRequest() {
        blockUI("Saving supplier...");
        $.get("upload", getActionFormData("add_supplier"), successHandler, "json");
    }

    function sendUpdateSupplierRequest() {
        blockUI("Updating supplier...");
        $.get("upload", getActionFormData("update_supplier"), successHandler, "json");
    }

    function sendEncodeInvoiceRequest() {
        blockUI("Saving invoice...");
        $.get("upload", getActionFormData("encode_invoice"), successHandler, "json");
    }

    function sendNextPageRequest() {
        blockUI("Getting next page...");
        $.get("upload", getActionFormData("next_page"), successHandler, "json");
    }

    function sendPreviousPageRequest() {
        blockUI("Getting previous page...");
        $.get("upload", getActionFormData("previous_page"), successHandler, "json");
    }

    function sendRotateLeftRequest() {
        blockUI("Rotating invoice...");
        $.get("upload", getActionFormData("rotate_left"), successHandler, "json");
    }

    function sendRotateRightRequest() {
        blockUI("Rotating invoice...");
        $.get("upload", getActionFormData("rotate_right"), successHandler, "json");
    }

    function sendSupplierFromVatRequest() {
        blockUI("Retrieving supplier...");
        $.get("upload", getSupplierFromVatFormData(), successHandler, "json");
    }

    function sendLogoutRequest() {
        blockUI("Logging out...");
        $.get("upload", getActionFormData("logout"), successHandler, "json");
    }

    function sendUserRequest() {
        blockUI("Just a moment...");
        $.get("upload", getActionFormData("getUser"), successHandler, "json");
    }


    $(function () {
        // Upload files
        $('#upload').bind("change", function () {
            blockUI("Uploading and analysing file...");
            var formData = new FormData($("#form-upload")[0]);
            //loop for add $_FILES["upload"+i] to formData
            for (var i = 0, len = document.getElementById('upload').files.length; i < len; i++) {
                formData.append("upload" + (i + 1), document.getElementById('upload').files[i]);
            }
            //send formData to server-side
            $.ajax({
                url: "upload",
                type: "POST",
                data: formData,
                dataType: "json",
                async: true,
                processData: false,  // tell jQuery not to process the data
                contentType: false,   // tell jQuery not to set contentType
                success: successHandler,
                error: successHandler
            });
        });
        // For selecting zones on the file
        $("#picture").find("img").drawer({});

        // Handle buttons
        // General buttons
        $("#auto").click(sendAutoRequest);
        $("#encode_supplier").click(sendAddSupplierRequest);
        $("#update_supplier").click(sendUpdateSupplierRequest);
        $("#encode_invoice").click(sendEncodeInvoiceRequest);

        // OCR zones buttons
        $("#date_ocr").click(sendDateOcrRequest);
        $("#id_ocr").click(sendIdOcrRequest);
        $("#subtotal_ocr").click(sendSubtotalOcrRequest);
        $("#vatRate_ocr").click(sendVatRateOcrRequest);
        $("#vat_ocr").click(sendVatOcrRequest);
        $("#total_ocr").click(sendTotalOcrRequest);

        // Manual
        $("#date_manual").click(sendManualDateRequest);
        $("#id_manual").click(sendManualIdRequest);
        $("#subtotal_manual").click(sendManualSubtotalRequest);
        $("#vatRate_manual").click(sendManualVatRateRequest);
        $("#vat_manual").click(sendManualVatRequest);
        $("#total_manual").click(sendManualTotalRequest);

        // Page(s) handling
        $("#next_page").click(sendNextPageRequest);
        $("#previous_page").click(sendPreviousPageRequest);
        $("#rotate_left").click(sendRotateLeftRequest);
        $("#rotate_right").click(sendRotateRightRequest);

        // Get supplier
        $("#supplier_manual").click(sendSupplierFromVatRequest);
        $("#supplier_ocr").click(sendSupplierOcrRequest);

        // Logout
        $("#logoutClick").click(sendLogoutRequest);

        // Done when page is loaded to request the logged user
        $.onload = sendUserRequest();

        // unblock when ajax activity stops
        $(document).ajaxStop(endAjax);
    });
});