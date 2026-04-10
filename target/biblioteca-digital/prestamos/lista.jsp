<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:if test="${empty sessionScope.usuarioLogueado}">
    <c:redirect url="/login"/>
</c:if>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Préstamos — Biblioteca UNTEC</title>
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
        <span class="navbar-usuario">
            <c:out value="${sessionScope.usuarioLogueado.nombre}"/>
        </span>
        <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Salir</a>
    </div>
</nav>

<div class="container">

    <div class="page-header">
        <h2>&#128203; Gestión de préstamos</h2>
        <c:if test="${sessionScope.usuarioLogueado.admin}">
            <a href="${pageContext.request.contextPath}/prestamos?accion=nuevo"
               class="btn btn-success">&#10133; Nuevo préstamo</a>
        </c:if>
    </div>

    <%-- Alerta de operación exitosa --%>
    <c:if test="${not empty mensajeExito}">
        <div class="alerta alerta-exito"><c:out value="${mensajeExito}"/></div>
    </c:if>

    <%-- Tabla de préstamos --%>
    <div class="card">
        <div class="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Libro</th>
                        <th>Usuario</th>
                        <th>Fecha préstamo</th>
                        <th>Fecha devolución</th>
                        <th>Estado</th>
                        <c:if test="${sessionScope.usuarioLogueado.admin}">
                            <th>Acciones</th>
                        </c:if>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${prestamos}" varStatus="st">
                        <tr>
                            <td><c:out value="${st.index + 1}"/></td>
                            <td><strong><c:out value="${p.tituloLibro}"/></strong></td>
                            <td><c:out value="${p.nombreUsuario}"/></td>
                            <td><c:out value="${p.fechaPrestamo}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty p.fechaDevolucion}">
                                        <c:out value="${p.fechaDevolucion}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color:#aaa;">Pendiente</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${p.activo}">
                                        <span class="badge badge-naranja">Activo</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-verde">Devuelto</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <c:if test="${sessionScope.usuarioLogueado.admin}">
                                <td>
                                    <c:if test="${p.activo}">
                                        <a href="${pageContext.request.contextPath}/prestamos?accion=devolver&id=${p.id}"
                                           class="btn btn-primary btn-sm"
                                           onclick="return confirm('¿Registrar devolución de &quot;<c:out value="${p.tituloLibro}"/>&quot;?')">
                                            Devolver
                                        </a>
                                    </c:if>
                                </td>
                            </c:if>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty prestamos}">
                        <tr>
                            <td colspan="7" style="text-align:center; color:#888; padding:2rem;">
                                No hay préstamos registrados.
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>

</div>
</body>
</html>
