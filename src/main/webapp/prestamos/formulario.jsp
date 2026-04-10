<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty sessionScope.usuarioLogueado}">
    <c:redirect url="/login"/>
</c:if>
<c:if test="${!sessionScope.usuarioLogueado.admin}">
    <c:redirect url="/prestamos"/>
</c:if>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nuevo préstamo — Biblioteca UNTEC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<%-- NAVBAR --%>
<nav class="navbar">
    <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">
        &#128218; Biblioteca <span>UNTEC</span>
    </a>
    <ul class="navbar-nav">
        <li><a href="${pageContext.request.contextPath}/dashboard">Inicio</a></li>
        <li><a href="${pageContext.request.contextPath}/libros">Libros</a></li>
        <li><a href="${pageContext.request.contextPath}/prestamos" class="activo">Préstamos</a></li>
    </ul>
    <div style="display:flex; align-items:center; gap:0.5rem;">
        <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Salir</a>
    </div>
</nav>

<div class="container" style="max-width:600px;">

    <div class="page-header">
        <h2>&#128221; Registrar nuevo préstamo</h2>
        <a href="${pageContext.request.contextPath}/prestamos" class="btn btn-secondary">
            &#8592; Volver
        </a>
    </div>

    <%-- Error --%>
    <c:if test="${not empty error}">
        <div class="alerta alerta-error"><c:out value="${error}"/></div>
    </c:if>

    <%-- Sin libros disponibles --%>
    <c:if test="${empty librosDisponibles}">
        <div class="alerta alerta-info">
            No hay libros disponibles para préstamo en este momento.
        </div>
    </c:if>

    <div class="card">
        <div class="card-header">Completá los datos del préstamo</div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/prestamos" method="post">

                <div class="form-group">
                    <label for="libroId">Libro disponible <span style="color:red">*</span></label>
                    <select id="libroId" name="libroId" class="form-control" required>
                        <option value="">— Seleccionar libro —</option>
                        <%-- c:forEach para listar los libros disponibles --%>
                        <c:forEach var="libro" items="${librosDisponibles}">
                            <option value="${libro.id}">
                                <c:out value="${libro.titulo}"/> — <c:out value="${libro.autor}"/>
                                <c:if test="${not empty libro.categoria}">
                                    (<c:out value="${libro.categoria}"/>)
                                </c:if>
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="usuarioId">Usuario <span style="color:red">*</span></label>
                    <select id="usuarioId" name="usuarioId" class="form-control" required>
                        <option value="">— Seleccionar usuario —</option>
                        <%-- c:forEach para listar los usuarios --%>
                        <c:forEach var="usuario" items="${usuarios}">
                            <option value="${usuario.id}">
                                <c:out value="${usuario.nombre}"/>
                                (<c:out value="${usuario.email}"/>)
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="alerta alerta-info" style="margin-top:1rem;">
                    &#8505; La fecha de préstamo se registrará automáticamente con la fecha actual.
                </div>

                <div style="display:flex; gap:1rem; margin-top:1.5rem;">
                    <button type="submit"
                            class="btn btn-success"
                            <c:if test="${empty librosDisponibles}">disabled</c:if>>
                        &#10003; Registrar préstamo
                    </button>
                    <a href="${pageContext.request.contextPath}/prestamos"
                       class="btn btn-secondary">Cancelar</a>
                </div>

            </form>
        </div>
    </div>

</div>
</body>
</html>
