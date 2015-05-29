<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Slim My Invoice</title>
    <link type="text/css" href="js/bower_components/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link rel="stylesheet" href="/font-awesome-4.3.0/css/font-awesome.min.css">
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
                <li><a href="analyse.jsp"><i class="fa fa-eye"></i> Analyse</a></li>
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
<br>
<div class="container-fluid">
    <h1>Support</h1>
    <p>Developed by Kevin RENIER during his final year thesis at University of Liege for A7 Software.</p>
    <br>
    <h3>Contact</h3>
    <p>Email: kevin.renier[at]gmail.com</p>
</div>
<script type="text/javascript" src="js/bower_components/jquery/dist/jquery.min.js"></script>
<script type="text/javascript" src="js/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/jquery.blockUI.js"></script>
<script type="text/javascript" src="js/support.js"></script>
</body>
</html>
