<!DOCTYPE html>
<html>
<head>
    <title>Risible Demo App</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap -->
    <link href="${request.contextPath}/css/bootstrap.css" rel="stylesheet" media="screen">
    <link href="${request.contextPath}/css/app.css" rel="stylesheet" media="screen">

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="${request.contextPath}/js/html5shiv.js"></script>
    <script src="${request.contextPath}/js/respond.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="navbar navbar-default navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <a href="../" class="navbar-brand">Risible2 Demo</a>
            <button class="navbar-toggle" type="button" data-toggle="collapse" data-target="#navbar-main">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
        </div>
        <ul class="nav navbar-nav navbar-right">
            <li><a href="http://builtwithbootstrap.com/" target="_blank">Logout</a></li>
        </ul>
    </div>
</div>
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="${request.contextPath}/js/jquery.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="${request.contextPath}/js/bootstrap.min.js"></script>
<div class="container">
    <sitemesh:write property='body'/>
</div>
</body>
</html>
