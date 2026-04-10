# Biblioteca Digital UNTEC
**Proyecto M5 — Desarrollo de aplicaciones web dinámicas Java**

# Enlace Video:
https://youtu.be/jc3FeQE2CXU

## Descripción
Sistema web de gestión de biblioteca digital que permite administrar el catálogo de libros, usuarios y préstamos. Implementa el patrón MVC con Java EE (Servlets + JSP + JSTL), acceso a datos con JDBC y base de datos H2 embebida.

---

## Tecnologías utilizadas

| Tecnología       | Versión  | Rol                              |
|------------------|----------|----------------------------------|
| Java             | 11+      | Lenguaje base                    |
| Java EE Servlet  | 4.0      | Controladores (MVC)              |
| JSP + JSTL       | 2.3 / 1.2| Capa de Vista (MVC)              |
| H2 Database      | 2.2      | Base de datos embebida           |
| Apache Tomcat    | 9.x      | Servidor de aplicaciones         |
| Maven            | 3.6+     | Gestión de dependencias y build  |

---

## Estructura del proyecto

```
BibliotecaDigital/
├── pom.xml                          # Dependencias Maven
├── README.md
└── src/main/
    ├── java/
    │   ├── model/                   # Capa Modelo (MVC)
    │   │   ├── Libro.java
    │   │   ├── Usuario.java
    │   │   └── Prestamo.java
    │   ├── dao/                     # Capa de Acceso a Datos (JDBC + Singleton)
    │   │   ├── ConexionDB.java
    │   │   ├── LibroDAO.java
    │   │   ├── UsuarioDAO.java
    │   │   └── PrestamoDAO.java
    │   └── controller/              # Capa Controlador (Servlets - MVC)
    │       ├── LoginServlet.java
    │       ├── LogoutServlet.java
    │       ├── DashboardServlet.java
    │       ├── LibroServlet.java
    │       └── PrestamoServlet.java
    ├── resources/
    └── webapp/                      # Capa Vista (JSP + JSTL - MVC)
        ├── WEB-INF/web.xml
        ├── css/style.css
        ├── index.jsp                # Formulario de login
        ├── dashboard.jsp            # Panel principal con estadísticas
        ├── error.jsp
        ├── libros/
        │   ├── lista.jsp            # Catálogo con búsqueda y filtros
        │   └── formulario.jsp       # Alta/edición de libros
        └── prestamos/
            ├── lista.jsp            # Lista de préstamos
            └── formulario.jsp       # Nuevo préstamo
```

---

## Patrón MVC implementado

```
Usuario (Browser)
      │
      ▼
  CONTROLADOR ──── Servlet (LoginServlet, LibroServlet, PrestamoServlet)
      │                   │
      │              Llama al DAO
      │                   │
      │              MODELO ──── Clases Java (Libro, Usuario, Prestamo)
      │                   │
      │              Base de datos H2 (JDBC)
      │
      ▼
    VISTA ──── JSP + JSTL (index.jsp, lista.jsp, formulario.jsp...)
```

---

## Cómo ejecutar el proyecto

### Prerrequisitos
- Java JDK 11 o superior
- Apache Maven 3.6+
- Apache Tomcat 9.x

### Opción A — Con Maven + Tomcat manual

1. **Clonar / descomprimir** el proyecto

2. **Compilar y empaquetar** el WAR:
   ```bash
   cd BibliotecaDigital
   mvn clean package
   ```

3. **Desplegar en Tomcat**:
   - Copiar `target/biblioteca-digital.war` a `$TOMCAT_HOME/webapps/`
   - Iniciar Tomcat: `$TOMCAT_HOME/bin/startup.sh` (Linux/Mac) o `startup.bat` (Windows)

4. **Acceder en el navegador**:
   ```
   http://localhost:8080/biblioteca-digital/
   ```

### Opción B — Desde Eclipse Enterprise Edition

1. **Importar proyecto**: `File → Import → Existing Maven Projects`
2. **Agregar servidor**: `Window → Preferences → Server → Runtime Environments → Add → Apache Tomcat 9`
3. **Run on Server**: click derecho en el proyecto → `Run As → Run on Server`
4. Acceder en: `http://localhost:8080/biblioteca-digital/`

---

## Cuentas de acceso predeterminadas

| Rol          | Email                   | Contraseña    |
|--------------|-------------------------|---------------|
| Administrador| admin@untec.edu         | admin123      |
| Estudiante   | juan@untec.edu          | student123    |
| Estudiante   | maria@untec.edu         | student123    |

> La base de datos H2 se crea automáticamente en `~/biblioteca_untec.mv.db` al primer inicio.

---

## Funcionalidades implementadas

### Autenticación
- Login con email y contraseña
- Gestión de sesión con `HttpSession`
- Logout que invalida la sesión
- Control de acceso por rol (ADMIN / ESTUDIANTE)

### Gestión de Libros (CRUD)
- Listar todo el catálogo
- Buscar por título o autor
- Agregar nuevo libro (solo ADMIN)
- Editar libro existente (solo ADMIN)
- Eliminar libro (solo ADMIN)
- Estado de disponibilidad (Disponible / Prestado)

### Gestión de Préstamos
- Registrar nuevo préstamo (solo ADMIN)
- Listar todos los préstamos (ADMIN) o los propios (ESTUDIANTE)
- Registrar devolución (solo ADMIN)
- Control automático de disponibilidad del libro

### Dashboard
- Estadísticas en tiempo real: total libros, disponibles, préstamos activos, usuarios

---

## Base de datos H2

La base de datos se inicializa automáticamente al primer inicio. Esquema:

```sql
usuarios  (id, nombre, email, password, rol)
libros    (id, titulo, autor, isbn, categoria, anio, disponible)
prestamos (id, libro_id, usuario_id, fecha_prestamo, fecha_devolucion, estado)
```

Para acceder a la consola H2 (solo desarrollo):
- Agregar al `web.xml` el servlet de consola H2, o
- Acceder directamente a: `http://localhost:8080/biblioteca-digital/h2-console`

---

## Generar el archivo WAR

```bash
mvn clean package
# El archivo queda en: target/biblioteca-digital.war
```

---

## Notas académicas

Este proyecto implementa todos los requerimientos del Módulo 5:

- **Lección 1**: Proyecto Dynamic Web Project, estructura base
- **Lección 2**: Vistas JSP con JSTL (`c:forEach`, `c:if`, `c:choose`, `c:out`)
- **Lección 3**: Servlets con GET/POST, gestión de sesión con `HttpSession`
- **Lección 4**: Patrón DAO con JDBC y Singleton (`ConexionDB`)
- **Lección 5**: Patrón MVC completo (model/dao/controller/view)
- **Lección 6**: Empaquetado WAR, configuración `web.xml`, despliegue Tomcat
