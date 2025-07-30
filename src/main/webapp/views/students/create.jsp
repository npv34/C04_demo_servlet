<%--
  Created by IntelliJ IDEA.
  User: luanpv
  Date: 7/30/25
  Time: 14:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h2>Add new student</h2>
<form action="/students/store" method="post">
    <table>
        <tr>
            <td>
                Name:
            </td>
            <td>
                <input type="text" name="name">
            </td>
        </tr>
        <tr>
            <td>
                Gender:
            </td>
            <td>
                <input type="radio" name="gender" value="1"> Male
                <input type="radio" name="gender" value="2"> Female
            </td>
        </tr>
        <tr>
            <td>Email</td>
            <td>
                <input type="text" name="email">
            </td>
        </tr>
        <tr>
            <td>Phone</td>
            <td>
                <input type="text" name="phone">
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <button type="submit">Save</button>
            </td>
        </tr>
    </table>
</form>
</body>
</html>
