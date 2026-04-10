<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty sessionScope.usuarioLogueado}">
    <c:redirect url="/login"/>
</c:if>
<%-- Solo admins pueden acceder a este formulario --%>
<c:if test="${!sessionScope.usuarioLogueado.admin}">
    <c:redirect url="/libros"/>
</c:if>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
        <c:choose>
            <c:when test="${modoEdicion}">Editar libro</c:when>
            <c:otherwise>Nuevo libro</c:otherwise>
        </c:choose>
        — Biblioteca UNTEC
    </title>
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
        <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Salir</a>
    </div>
</nav>

<div class="container" style="max-width:600px;">

    <div class="page-header">
        <h2>
            <c:choose>
                <c:when test="${modoEdicion}">&#9998; Editar libro</c:when>
                <c:otherwise>&#10133; Nuevo libro</c:otherwise>
            </c:choose>
        </h2>
        <a href="${pageContext.request.contextPath}/libros" class="btn btn-secondary">
            &#8592; Volver al catálogo
        </a>
    </div>

    <%-- Error de validación --%>
    <c:if test="${not empty error}">
        <div class="alerta alerta-error"><c:out value="${error}"/></div>
    </c:if>

    <%-- Formulario de libro --%>
    <div class="card">
        <div class="card-header">
            <c:choose>
                <c:when test="${modoEdicion}">Datos del libro a actualizar</c:when>
                <c:otherwise>Completá los datos del nuevo libro</c:otherwise>
            </c:choose>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/libros" method="post">

                <%-- ID oculto para modo edición --%>
                <c:if test="${modoEdicion}">
                    <input type="hidden" name="id" value="${libro.id}">
                </c:if>

                <div class="form-group">
                    <label for="titulo">Título <span style="color:red">*</span></label>
                    <input type="text"
                           id="titulo"
                           name="titulo"
                           class="form-control"
                           value="<c:out value='${libro.titulo}'/>"
                           placeholder="Ej: Clean Code"
                           required>
                </div>

                <div class="form-group">
                    <label for="autor">Autor <span style="color:red">*</span></label>
                    <input type="text"
                           id="autor"
                           name="autor"
                           class="form-control"
                           value="<c:out value='${libro.autor}'/>"
                           placeholder="Ej: Robert C. Martin"
                           required>
                </div>

                <div class="form-group">
                    <label for="isbn">ISBN</label>
                    <input type="text"
                           id="isbn"
                           name="isbn"
                           class="form-control"
                           value="<c:out value='${libro.isbn}'/>"
                           placeholder="Ej: 978-0132350884">
                </div>

                <div class="form-group">
                    <label for="categoria">Categoría</label>
                    <select id="categoria" name="categoria" class="form-control">
                        <option value="">— Seleccionar —</option>
                        <c:set var="cats" value="Programación,Informática,Bases de Datos,Literatura,Ciencias,Matemáticas,Historia,Otro"/>
                        <c:forTokens var="cat" items="${cats}" delims=",">
                            <option value="${cat}"
                                <c:if test="${libro.categoria == cat}">selected</c:if>>
                                <c:out value="${cat}"/>
                            </option>
                        </c:forTokens>
                    </select>
                </div>

                <div class="form-group">
                    <label for="anio">Año de publicación</label>
                    <input type="number"
                           id="anio"
                           name="anio"
                           class="form-control"
                           value="<c:out value='${libro.anio > 0 ? libro.anio : ""}'/>"
                           min="1000"
                           max="2099"
                           placeholder="Ej: 2008">
                </div>

                <div style="display:flex; gap:1rem; margin-top:1.5rem;">
                    <button type="submit" class="btn btn-primary">
                        <c:choose>
                            <c:when test="${modoEdicion}">&#10003; Actualizar libro</c:when>
                            <c:otherwise>&#10133; Agregar libro</c:otherwise>
                        </c:choose>
                    </button>
                    <a href="${pageContext.request.contextPath}/libros"
                       class="btn btn-secondary">Cancelar</a>
                </div>

            </form>
        </div>
    </div>

</div>
</body>
</html>
