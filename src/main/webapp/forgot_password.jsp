<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Slim My Invoice</title>
    <link href="/js/bower_components/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/font-awesome-4.3.0/css/font-awesome.min.css">
    <link href="/css/signin.css" rel="stylesheet">
</head>
<body>
<div id="message" class="alert alert-danger">
    <div id="error_icon" style="display: initial;"><i class="fa fa-exclamation-circle"></i><strong> Error: </strong></div>
    <div id="message_text" style="display: initial;"></div>
</div>
<div class="container">
    <div class="row">
        <div class="col-sm-6 col-md-4 col-md-offset-4">
            <h1 class="text-center login-title">Forgot my password?</h1>
            <div class="account-wall">
                <form class="form-group">
                    <input type="text" id="username" class="form-control" placeholder="VAT number (10 digits)"
                           maxlength="10" title="Enter your username (the 10 digits in your VAT number)"
                           onkeypress="if(this.value.match(/\D/)) this.value=this.value.replace(/\D/g,'')"
                           onkeyup   ="if(this.value.match(/\D/)) this.value=this.value.replace(/\D/g,'')"
                           required autofocus>
                    <!--<input type="email" id="email" class="form-control" placeholder="Registration email" required>-->
                    <br>
                    <button id="submit" type="button" class="btn btn-lg btn-primary btn-block">Send my password</button>
                </form>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="/js/bower_components/jquery/dist/jquery.min.js"></script>
<script type="text/javascript" src="/js/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/jquery.blockUI.js"></script>
<script type="text/javascript" src="/js/forgot_password.js"></script>
</body>
</html>