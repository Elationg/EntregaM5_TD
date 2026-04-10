<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Biblioteca Digital UNTEC — Iniciar sesión</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="login-wrapper">
    <div class="login-card">

        <!-- Logo / Título -->
        <div class="login-logo">
            <h1>&#128218; Biblioteca Digital</h1>
            <p>Universidad UNTEC</p>
        </div>

        <!-- Mensaje de error (JSTL c:if) -->
        <c:if test="${not empty error}">
            <div class="alerta alerta-error">
                <c:out value="${error}" />
            </div>
        </c:if>

        <!-- Formulario de login -->
        <form action="${pageContext.request.contextPath}/login" method="post">

            <div class="form-group">
                <label for="email">Correo electrónico</label>
                <input type="email"
                       id="email"
                       name="email"
                       class="form-control"
                       placeholder="usuario@untec.edu"
                       required
                       autofocus>
            </div>

            <div class="form-group">
                <label for="password">Contraseña</label>
                <input type="password"
                       id="password"
                       name="password"
                       class="form-control"
                       placeholder="••••••••"
                       required>
            </div>

            <button type="submit" class="btn btn-primary btn-full" style="margin-top:0.5rem;">
                Iniciar sesión
            </button>
        </form>

        <!-- Credenciales de prueba -->
        <div style="margin-top:1.5rem; padding-top:1rem; border-top:1px solid #eee; font-size:0.8rem; color:#888; text-align:center;">
            <strong>Cuentas de prueba:</strong><br>
            Admin: admin@untec.edu / admin123<br>
            Estudiante: juan@untec.edu / student123
        </div>

    </div>
</div>

</body>
</html>
