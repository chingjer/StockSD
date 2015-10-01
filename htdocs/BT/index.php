<?php
require_once("header.inc");
?>
<title>BackTesting System</title>
<script src="/jslib/vendor/jquery.typoshadow.js"></script>
<link rel="stylesheet" href="/jslib/vendor/normalize.css">
<link rel="stylesheet" href="/jslib/vendor/main.css">
<script src="/jslib/vendor/main.js"></script>
<style type="text/css">
#typo {
	margin: 0;
	padding: 0;
	background-color: #3498db;
	font-weight: bold;
	font-size: 70px;
	color: #fff;
	text-align: center;
	line-height: 500px;
	height: 500px;
	-webkit-user-select: none;
	-moz-user-select: none;
	-ms-user-select: none;
	letter-spacing: 1px;
}
#typo .inner {
    position: relative;
    width: 976px;
    margin: 0 auto;
}
</style>
<?php
require_once("BTMenu.html");
?>
<div id="typo">
    <div class="inner">BackTesting System</div>
</div>
</body>
</html>
