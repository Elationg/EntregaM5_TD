<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Redirige si no hay sesión activa --%>
<c:if test="${empty sessionScope.usuarioLogueado}">
    <c:redirect url="/login"/>
</c:if>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard — Biblioteca Digital UNTEC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<%-- NAVBAR --%>
<nav class="navbar">
    <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">
        &#128218; Biblioteca <span>UNTEC</span>
    </a>
    <ul class="navbar-nav">
        <li><a href="${pageContext.request.contextPath}/dashboard" class="activo">Inicio</a></li>
        <li><a href="${pageContext.request.contextPath}/libros">Libros</a></li>
        <li><a href="${pageContext.request.contextPath}/prestamos">Préstamos</a></li>
    </ul>
    <div style="display:flex; align-items:center; gap:0.5rem;">
        <span class="navbar-usuario">
            Hola, <strong><c:out value="${sessionScope.usuarioLogueado.nombre}"/></strong>
            <c:if test="${sessionScope.usuarioLogueado.admin}">
                &nbsp;<span class="badge badge-naranja">Admin</span>
            </c:if>
        </span>
        <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Salir</a>
    </div>
</nav>

<div class="container">

    <div class="page-header">
        <h2>&#127968; Panel principal</h2>
    </div>

    <%-- Estadísticas --%>
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-numero"><c:out value="${totalLibros}"/></div>
            <div class="stat-label">Libros en catálogo</div>
        </div>
        <div class="stat-card verde">
            <div class="stat-numero"><c:out value="${librosDisponibles}"/></div>
            <div class="stat-label">Libros disponibles</div>
        </div>
        <div class="stat-card naranja">
            <div class="stat-numero"><c:out value="${prestamosActivos}"/></div>
            <div class="stat-label">Préstamos activos</div>
        </div>
        <c:if test="${sessionScope.usuarioLogueado.admin}">
        <div class="stat-card rojo">
            <div class="stat-numero"><c:out value="${totalUsuarios}"/></div>
            <div class="stat-label">Usuarios registrados</div>
        </div>
        </c:if>
    </div>

    <%-- Accesos rápidos --%>
    <div class="card">
        <div class="card-header">&#9889; Acceso rápido</div>
        <div class="card-body" style="display:flex; gap:1rem; flex-wrap:wrap;">
            <a href="${pageContext.request.contextPath}/libros" class="btn btn-primary">
                &#128214; Ver catálogo de libros
            </a>
            <a href="${pageContext.request.contextPath}/prestamos" class="btn btn-success">
                &#128203; Ver préstamos
            </a>
            <c:if test="${sessionScope.usuarioLogueado.admin}">
                <a href="${pageContext.request.contextPath}/libros?accion=nuevo" class="btn btn-warning">
                    &#10133; Agregar libro
                </a>
                <a href="${pageContext.request.contextPath}/prestamos?accion=nuevo" class="btn btn-secondary">
                    &#128221; Nuevo préstamo
                </a>
            </c:if>
        </div>
    </div>

</div>
</body>
</html>
