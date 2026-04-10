<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty sessionScope.usuarioLogueado}">
    <c:redirect url="/login"/>
</c:if>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Catálogo de Libros — Biblioteca UNTEC</title>
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
        <li><a href="${pageContext.request.contextPath}/libros" class="activo">Libros</a></li>
        <li><a href="${pageContext.request.contextPath}/prestamos">Préstamos</a></li>
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
        <h2>&#128214; Catálogo de libros</h2>
        <c:if test="${sessionScope.usuarioLogueado.admin}">
            <a href="${pageContext.request.contextPath}/libros?accion=nuevo"
               class="btn btn-success">&#10133; Nuevo libro</a>
        </c:if>
    </div>

    <%-- Alerta de operación exitosa --%>
    <c:if test="${not empty mensajeExito}">
        <div class="alerta alerta-exito"><c:out value="${mensajeExito}"/></div>
    </c:if>

    <%-- Formulario de búsqueda --%>
    <form action="${pageContext.request.contextPath}/libros"
          method="get" class="search-form">
        <input type="text"
               name="buscar"
               class="form-control"
               placeholder="Buscar por título o autor..."
               value="<c:out value='${terminoBusqueda}'/>">
        <button type="submit" class="btn btn-primary">&#128269; Buscar</button>
        <c:if test="${not empty terminoBusqueda}">
            <a href="${pageContext.request.contextPath}/libros"
               class="btn btn-secondary">&#10006; Limpiar</a>
        </c:if>
    </form>

    <%-- Resultado de búsqueda --%>
    <c:if test="${not empty terminoBusqueda}">
        <p style="margin-bottom:1rem; color:#666; font-size:0.9rem;">
            Resultados para: <strong><c:out value="${terminoBusqueda}"/></strong>
            (<c:out value="${libros.size()}"/> encontrados)
        </p>
    </c:if>

    <%-- Tabla de libros --%>
    <div class="card">
        <div class="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Título</th>
                        <th>Autor</th>
                        <th>ISBN</th>
                        <th>Categoría</th>
                        <th>Año</th>
                        <th>Disponibilidad</th>
                        <c:if test="${sessionScope.usuarioLogueado.admin}">
                            <th>Acciones</th>
                        </c:if>
                    </tr>
                </thead>
                <tbody>
                    <%-- c:forEach itera la lista de libros --%>
                    <c:forEach var="libro" items="${libros}" varStatus="st">
                        <tr>
                            <td><c:out value="${st.index + 1}"/></td>
                            <td><strong><c:out value="${libro.titulo}"/></strong></td>
                            <td><c:out value="${libro.autor}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty libro.isbn}">
                                        <c:out value="${libro.isbn}"/>
                                    </c:when>
                                    <c:otherwise><span style="color:#aaa;">—</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:if test="${not empty libro.categoria}">
                                    <span class="badge badge-azul"><c:out value="${libro.categoria}"/></span>
                                </c:if>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${libro.anio > 0}">
                                        <c:out value="${libro.anio}"/>
                                    </c:when>
                                    <c:otherwise><span style="color:#aaa;">—</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${libro.disponible}">
                                        <span class="badge badge-verde">Disponible</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-rojo">Prestado</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <c:if test="${sessionScope.usuarioLogueado.admin}">
                                <td>
                                    <div class="acciones">
                                        <a href="${pageContext.request.contextPath}/libros?accion=editar&id=${libro.id}"
                                           class="btn btn-warning btn-sm">Editar</a>
                                        <a href="${pageContext.request.contextPath}/libros?accion=eliminar&id=${libro.id}"
                                           class="btn btn-danger btn-sm"
                                           onclick="return confirm('¿Eliminar el libro &quot;<c:out value="${libro.titulo}"/>&quot;?')">
                                            Eliminar
                                        </a>
                                    </div>
                                </td>
                            </c:if>
                        </tr>
                    </c:forEach>

                    <%-- Fila vacía si no hay resultados --%>
                    <c:if test="${empty libros}">
                        <tr>
                            <td colspan="8" style="text-align:center; color:#888; padding:2rem;">
                                No se encontraron libros.
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
