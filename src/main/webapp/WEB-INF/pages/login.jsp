
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Signin</title>
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
            <h1 class="text-center login-title">Sign in</h1>
            <div class="account-wall">
                <form class="form-signin">
                    <input style="margin: 0;" type="text" id="username" class="form-control"
                           placeholder="VAT number (10 digits)"  maxlength="10"
                           title="Enter your username (the 10 digits in your VAT number)"
                           onkeypress="if(this.value.match(/[\D|\\,]/)) this.value=this.value.replace(/[\D|\\,]/g,'')"
                           onkeyup   ="if(this.value.match(/[\D|\\,]/)) this.value=this.value.replace(/[\D|\\,]/g,'')"
                           required autofocus>
                    <input style="margin: 0;" type="password" id="password" class="form-control" placeholder="Password" title="Enter your password" required>
                    <a href="../../forgot_password.jsp" class="pull-right need-help">Forgot my password? </a><span class="clearfix"></span>
                    <a href="../../change_password.jsp" class="pull-right need-help">Change my password?</a><span class="clearfix"></span>
                    <br>
                    <br>
                    <button id="submit" type="button" class="btn btn-lg btn-primary btn-block"> Sign in</button>
                </form>
            </div>
            <a href="../../register.jsp" class="text-center new-account">Create an account </a>
        </div>
    </div>
</div>
<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
<script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
<![endif]-->
<script type="text/javascript" src="/js/bower_components/jquery/dist/jquery.min.js"></script>
<script type="text/javascript" src="/js/jquery.freetrans.js"></script>
<script type="text/javascript" src="/js/sha256.js"></script>
<script type="text/javascript" src="js/jquery.blockUI.js"></script>
<script type="text/javascript" src="/js/login.js"></script>

</body>
</html>