<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Error — Biblioteca Digital UNTEC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<c:if test="${not empty sessionScope.usuarioLogueado}">
<nav class="navbar">
    <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">
        &#128218; Biblioteca <span>UNTEC</span>
    </a>
    <ul class="navbar-nav">
        <li><a href="${pageContext.request.contextPath}/dashboard">Inicio</a></li>
    </ul>
    <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Salir</a>
</nav>
</c:if>

<div class="container" style="max-width:600px; margin-top:3rem; text-align:center;">
    <div class="card">
        <div class="card-body" style="padding:3rem 2rem;">
            <div style="font-size:4rem; margin-bottom:1rem;">&#9888;</div>
            <h2 style="color:#e74c3c; margin-bottom:1rem;">Se produjo un error</h2>

            <c:if test="${not empty errorMsg}">
                <div class="alerta alerta-error" style="text-align:left;">
                    <c:out value="${errorMsg}"/>
                </div>
            </c:if>

            <c:if test="${not empty pageContext.exception}">
                <div class="alerta alerta-error" style="text-align:left;">
                    <c:out value="${pageContext.exception.message}"/>
                </div>
            </c:if>

            <p style="color:#666; margin-bottom:1.5rem;">
                Si el error persiste, contactá al administrador del sistema.
            </p>

            <c:choose>
                <c:when test="${not empty sessionScope.usuarioLogueado}">
                    <a href="${pageContext.request.contextPath}/dashboard"
                       class="btn btn-primary">&#127968; Volver al inicio</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login"
                       class="btn btn-primary">&#128274; Ir al login</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

</body>
</html>
