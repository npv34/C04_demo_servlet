<%--
  Created by IntelliJ IDEA.
  User: luanpv
  Date: 7/29/25
  Time: 15:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Title</title>
    <style>
        table {
            width: 500px;
            border-collapse: collapse;
        }

        tr, td, th {
            border:  1px solid rgba(144, 136, 136, 0.15);
        }
    </style>
</head>
<body>
<h2>Student list</h2>
<table>
    <tr>
        <th>STT</th>
        <th>Name</th>
        <th>Gender</th>
        <th>Email</th>
        <th>Phone</th>
        <th></th>
    </tr>
    <c:forEach var="student" items="${requestScope.listStudent}">
    <tr>
        <td>1</td>
        <td><c:out value="${student.name}"/></td>
        <td><c:out value="${student.gender}"/></td>
        <td><c:out value="${student.email}"/></td>
        <td><c:out value="${student.phone}"/></td>
        <td><a href="/students/delete?id=<c:out value="${student.id}"/>">Delete</a></td>
    </tr>
    </c:forEach>
</table>
</body>
</html>
