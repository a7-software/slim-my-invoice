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
    <h1>Reset database</h1>

    <button type="button" class="btn btn-default btn-lg" data-toggle="modal" data-target="#myModal" style=
    "color:darkred;border-color: darkred">
        <i class="fa fa-trash-o"></i> Reset my database!
    </button>

    <!-- Modal -->
    <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-show="1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div>
                    <button type="button" class="close" data-dismiss="modal" >&times;</button>
                    <h1 class="modal-title" id="myModalLabel">You are about to reset the database...</h1>
                </div>
                <div>
                    <br>
                    <br>
                    <h4>Are you really sure to reset the database?</h4>
                    <h4> All data will be lost!</h4>
                    <br>
                </div>
                <div class="form-group">
                    <button  id="reset_db" type="button" class="btn btn-default btn-lg" data-dismiss="modal" style="color:darkred">
                        <span class="glyphicon glyphicon-warning-sign"></span> Reset my database!
                    </button>
                    <button type="button" class="btn btn-default btn-lg" data-dismiss="modal">
                        <span class="glyphicon glyphicon-remove"></span> Cancel
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="js/bower_components/jquery/dist/jquery.min.js"></script>
<script type="text/javascript" src="js/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/jquery.blockUI.js"></script>
<script type="text/javascript" src="js/settings.js"></script>
</body>
</html>
