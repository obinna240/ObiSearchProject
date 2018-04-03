<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Hello Web</title>
</head>
<body>
<div>
<h2> Search Content</h2>
<form action="api/cms/search/getSearchResults" >
<input type="text" name="q">
<input type="submit"/>
</form>
</div>
<div>
<h2> Index Content</h2>
<form action="api/cms/index/add" method="post">
<input type="text" name="json">
<input type="submit"/>
</form>
</div>

</body>
</html>