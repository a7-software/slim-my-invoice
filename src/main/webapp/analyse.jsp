<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Slim My Invoice</title>
    <link type="text/css" href="js/bower_components/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link rel="stylesheet" href="/font-awesome-4.3.0/css/font-awesome.min.css">
    <link type="text/css" href="css/jquery.freetrans.css" rel="stylesheet"/>
</head>
<body>


<nav class="navbar navbar-default">
    <div class="container-fluid">
        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <div class="navbar-header">
                <a class="navbar-brand">Slim My Invoice</a>
            </div>
            <ul class="nav navbar-nav">
                <li class="active"><a href="analyse.jsp"><i class="fa fa-eye"></i> Analyse<span class="sr-only">(current)</span></a></li>
                <li><a href="suppliers.jsp"><i class="fa fa-users"></i> Suppliers</a></li>
                <li><a href="invoices.jsp"><i class="fa fa-eur"></i> Invoices</a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li><a id="user" class="navbar-brand"></a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Menu <span class="caret"></span></a>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="aboutme.jsp" class="glyphicon glyphicon-user"> About me</a></li>
                        <li class="divider"></li>
                        <li><a href="support.jsp" class="glyphicon glyphicon-question-sign"> Support</a></li>
                        <li class="divider"></li>
                        <li><a href="settings.jsp" class="glyphicon glyphicon-cog"> Settings</a></li>
                        <li class="divider"></li>
                        <li><a id="logoutClick" href="login.jsp" title="Click to logout from current session" class="glyphicon glyphicon-log-out"></span> Logout</a></li>
                    </ul>
                </li>
            </ul>
        </div><!-- /.navbar-collapse -->
    </div><!-- /.container-fluid -->
</nav>
<div id="message_success" class="alert alert-success" style="display: none">
    <div id="success_icon" style="display: initial;"><i class="fa fa-check-circle"></i><strong> </strong></div>
    <div id="message_success_text" style="display: initial;"></div>
</div>
<div id="message_error" class="alert alert-danger" style="display: none">
    <div id="error_icon" style="display: initial;"><i class="fa fa-exclamation-circle"></i><strong> Error: </strong></div>
    <div id="message_error_text" style="display: initial;"></div>
</div>

<div class="container-fluid">
    <div class="row">
        <div class="col-md-6" id="picture">
            <div id="image">
                <img style="border:1px solid black" width="100%" src="${imgPath}" data-param-id="parameters"/>
            </div>
            <br>
            <br>
            <div class="form-inline" id="rotateButtons" align="left">
                <button id="rotate_left" type="button" class="btn btn-default"><i class="fa fa-rotate-left"></i> Rotate counterclockwise</button>
                <button id="rotate_right" type="button" class="btn btn-default"><i class="fa fa-rotate-right"></i> Rotate clockwise</button>
            </div>
            <div class="form-inline" id="nextPrevButtons" align="right">
                <button id="previous_page" type="button" class="btn btn-default">Previous page</button>
                <button id="next_page" type="button" class="btn btn-default">Next page</button>
            </div>

            <h3>Processing file:</h3>
            <pre id="img_name"></pre>

            <h3>File upload:</h3>
            Select a file to upload: <br />
            <form id="form-upload">
                <input type="file" action="file-upload-1.htm" name="upload" id="upload" enctype="multipart/form-data">
            </form>
        </div>

        <div id="analysisColumn" class="col-md-6">
            <div id="ocrAndSupplierArea" class="form-group">
                <h3>OCR</h3>
                <div class="radio"><label>
                    <input type="radio" name="language" value="fra" checked> French
                </label></div>
                <div class="radio"><label>
                    <input type="radio" name="language" value="eng"> English
                </label></div>
                <button id="auto" type="button" class="btn btn-default"><span class="glyphicon glyphicon-search"></span> Auto analyse</button>
                <br>
                <h3>Supplier</h3>
                <h4>Find supplier</h4>
                <table style="width:100%">
                    <tr>
                        <td width="15%">VAT number</td>
                        <td width="4%"> </td>
                        <td width="35%">BE <input type="text"
                                                  onkeypress="if(this.value.match(/[^\d|^\\.|^\\,|^\\-]/)) this.value=this.value.replace(/[^\d|^\\.|^\\,|^\\-]/,'')"
                                                  onkeyup   ="if(this.value.match(/[^\d|^\\.|^\\,|^\\-]/)) this.value=this.value.replace(/[^\d|^\\.|^\\,|^\\-]/,'')"
                                                  id="supplierVatEntry" maxlength="15"/></td>
                        <td width="10%"><button id="supplier_ocr" type="button" class="btn btn-default">OCR</button></td>
                        <td width="13%"><button id="supplier_manual" type="button" class="btn btn-default">Manual</button></td>
                        <td width="23%"> </td>
                    </tr>
                </table>
                <h4>Supplier info</h4>
                <table style="width:100%">
                    <tr>
                        <td width="10%">Name</td>
                        <td width="80%"><pre id="supplier"></pre></td>
                    </tr>
                </table>
                <table style="width:100%">
                    <tr>
                        <td width="10%">Street</td>
                        <td width="40%"><pre id="street"></pre></td>
                        <td width="10%"> </td>
                        <td width="10%">Number</td>
                        <td width="20%"><pre id="number"></pre></td>
                    </tr>
                    <tr>
                        <td width="10%">City</td>
                        <td width="40%"><pre id="city"></pre></td>
                        <td width="10%"> </td>
                        <td width="10%">Zip</td>
                        <td width="20%"><pre id="zip"></pre></td>
                    </tr>
                </table>
                <table style="width:100%">
                    <tr>
                        <td width="10%">Country</td>
                        <td width="25%"><pre id="country"></pre></td>
                        <td width="10%"> </td>
                        <td width="13%">Vat Number</td>
                        <td width="32%"><pre id="vatNb"></pre></td>
                    </tr>
                </table>
            </div>
            <br>
            <div id="dateAndAmountsArea">
                <h3>Date, reference & amounts</h3>
                <table style="width:100%">
                    <thead>
                    <tr>
                        <th width="13%">Field</th>
                        <th width="2%"></th>
                        <th width="27%">Preview</th>
                        <th width="4%"></th>
                        <th width="20%">Edit</th>
                        <th width="4%"></th>
                        <th width="30%">Actions</th>
                    </tr>
                    </thead>
                    <tr>
                        <td width="13%">Date </td>
                        <td width="2%"><div title="A zone for this field is present in the supplier's template." id="date_template"><i class="fa fa-crosshairs"></i></div></td>
                        <td width="27%"><pre id="date"></pre></td>
                        <td width="4%"> </td>
                        <td width="20%"><input type="text" id="dateEntry"/></td>
                        <td width="4%"> </td>
                        <td width="30%"><button id="date_ocr" type="button" class="btn btn-default">OCR</button> <button id="date_manual" type="button" class="btn btn-default">Manual</button></td>
                    </tr>
                    <tr>
                        <td width="13%">Invoice ref</td>
                        <td width="2%"><div title="A zone for this field is present in the supplier's template." id="id_template"><i class="fa fa-crosshairs"></i></div></td>
                        <td width="27%"><pre id="id"></pre></td>
                        <td width="4%"> </td>
                        <td width="20%"><input type="text" id="idEntry"/></td>
                        <td width="4%"> </td>
                        <td width="30%"><button id="id_ocr" type="button" class="btn btn-default">OCR</button> <button id="id_manual" type="button" class="btn btn-default">Manual</button></td>
                    </tr>
                    <tr>
                        <td width="13%">Subtotal </td>
                        <td width="2%"><div title="A zone for this field is present in the supplier's template." id="subtotal_template"><i class="fa fa-crosshairs"></i></div></td>
                        <td width="27%"><pre id="subtotal"></pre></td>
                        <td width="4%"> </td>
                        <td width="20%"><input type="text" id="subtotalEntry"
                                               onkeypress="if(this.value.match(/[^\d|^\\.|^\\,|^\\-]/)) this.value=this.value.replace(/[^\d|^\\.|^\\,|^\\-]/,'')"
                                               onkeyup   ="if(this.value.match(/[^\d|^\\.|^\\,|^\\-]/)) this.value=this.value.replace(/[^\d|^\\.|^\\,|^\\-]/,'')"/></td>
                        <td width="4%"> </td>
                        <td width="30%"><button id="subtotal_ocr" type="button" class="btn btn-default">OCR</button> <button id="subtotal_manual" type="button" class="btn btn-default">Manual</button></td>
                    </tr>
                    <tr>
                        <td width="13%">VAT rate </td>
                        <td width="2%"><div title="A zone for this field is present in the supplier's template." id="vatRate_template"><i class="fa fa-crosshairs"></i></div></td>
                        <td width="27%"><pre id="vatRate"></pre></td>
                        <td width="4%"> </td>
                        <td width="20%"><input type="text" id="vatRateEntry"
                                               onkeypress="if(this.value.match(/[^\d|^\\.|^\\,]/)) this.value=this.value.replace(/[^\d|^\\.|^\\,]/,'')"
                                               onkeyup   ="if(this.value.match(/[^\d|^\\.|^\\,]/)) this.value=this.value.replace(/[^\d|^\\.|^\\,]/,'')"/></td>
                        <td width="4%"> </td>
                        <td width="30%"><button id="vatRate_ocr" type="button" class="btn btn-default">OCR</button> <button id="vatRate_manual" type="button" class="btn btn-default">Manual</button></td>
                    </tr>
                    <tr>
                        <td width="13%">VAT </td>
                        <td width="2%"><div title="A zone for this field is present in the supplier's template." id="vat_template"><i class="fa fa-crosshairs"></i></div></td>
                        <td width="27%"><pre id="vat"></pre></td>
                        <td width="4%"> </td>
                        <td width="20%"><input type="text" id="vatEntry"
                                               onkeypress="if(this.value.match(/[^\d|^\\.|^\\,|^\\-]/)) this.value=this.value.replace(/[^\d|^\\.|^\\,|^\\-]/,'')"
                                               onkeyup   ="if(this.value.match(/[^\d|^\\.|^\\,|^\\-]/)) this.value=this.value.replace(/[^\d|^\\.|^\\,|^\\-]/,'')"/></td>
                        <td width="4%"> </td>
                        <td width="30%"><button id="vat_ocr" type="button" class="btn btn-default">OCR</button> <button id="vat_manual" type="button" class="btn btn-default">Manual</button></td>
                    </tr>
                    <tr>
                        <td width="12%">Total </td>
                        <td width="3%"><div title="A zone for this field is present in the supplier's template." id="total_template"><i class="fa fa-crosshairs"></i></div></td>
                        <td width="27%"><pre id="total"></pre></td>
                        <td width="4%"> </td>
                        <td width="20%"><input type="text" id="totalEntry"
                                               onkeypress="if(this.value.match(/[^\d|^\\.|^\\,|^\\-]/)) this.value=this.value.replace(/[^\d|^\\.|^\\,|^\\-]/,'')"
                                               onkeyup   ="if(this.value.match(/[^\d|^\\.|^\\,|^\\-]/)) this.value=this.value.replace(/[^\d|^\\.|^\\,|^\\-]/,'')"/></td>
                        <td width="4%"> </td>
                        <td width="30%"><button id="total_ocr" type="button" class="btn btn-default">OCR</button> <button id="total_manual" type="button" class="btn btn-default">Manual</button></td>
                    </tr>
                </table>
                <br>
                <div class="form-inline">
                    <div align="left">
                        <button id="encode_invoice" type="button" class="btn btn-default"><span class="glyphicon glyphicon-floppy-save"></span> Save Invoice</button>
                        <button id="encode_supplier" type="button" class="btn btn-default">Save Supplier</button>
                        <button id="update_supplier" type="button" class="btn btn-default">Update Supplier template</button>
                    </div>
                </div>
            </div>
            <textarea id="parameters" style="display:none;" readonly></textarea>
        </div>
        </form>
        </div>
    </div>
</div>
<script type="text/javascript" src="js/bower_components/jquery/dist/jquery.min.js"></script>
<script type="text/javascript" src="js/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/Matrix.js"></script>
<script type="text/javascript" src="js/jquery.freetrans.js"></script>
<script type="text/javascript" src="js/drawer.js"></script>
<script type="text/javascript" src="js/picdrag.js"></script>
<script type="text/javascript" src="js/jquery.blockUI.js"></script>
<script type="text/javascript" src="js/analyse.js"></script>
</body>
</html>