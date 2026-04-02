# lucaflix-backend-refactor

## 📌 Próxima versión refactorizada / Upcoming Refactored Version

Este repositorio contendrá la versión refactorizada del backend de **Lucaflix** tiente 144 tests.  
This repository will contain the refactored version of the **Lucaflix** backend has 144 tests.

---

## 🎯 Objetivos del refactor / Refactor Goals

Se planea implementar mejoras importantes para llevar el proyecto a un nivel profesional:  
We plan to implement major improvements to bring the project to a professional level:

1. **Arquitectura limpia / Clean Architecture**  
   Separación clara de responsabilidades entre Controller, Service, Repository, DTOs y Mappers.  
   Clear separation of responsibilities between Controller, Service, Repository, DTOs, and Mappers.

2. **Validaciones y seguridad / Validations and Security**
    - Validación de inputs (URL, queries, formularios, etc.)
    - JWT seguro y manejo de roles  
      Input validation (URL, queries, forms, etc.)  
      Secure JWT implementation and role management.

3. **Código en inglés / Code in English**  
   Todas las clases, variables y comentarios estarán en inglés para estandarización.  
   All classes, variables, and comments will be in English for standardization.

4. **Perfiles de aplicación / Application Profiles**
    - `dev`, `test` y `prod`  
      Configuración separada para cada entorno.  
      Separate configuration for each environment: `dev`, `test`, and `prod`.

5. **Pruebas / Testing**
    - Implementación de pruebas unitarias e integradas.  
      Unit and integration tests implementation.

6. **MapStruct / DTOs**
    - Ajuste de mappers automáticos con MapStruct
    - DTOs consistentes para separar la capa de presentación de la lógica  
      Automatic mappers with MapStruct  
      Consistent DTOs to separate presentation from business logic.

7. **Manejo de errores / Error Handling**
    - Global exception handling con mensajes claros  
      Global exception handling with clear messages.

8. **Docker Compose / Variables de entorno / Local testing**
    - Entorno local fácil de levantar
    - Variables de entorno configurables  
      Easy local setup with Docker Compose  
      Configurable environment variables.

9. **Spring Actuator + Prometheus**
    - Monitoreo de métricas y salud del sistema  
      System health and metrics monitoring.

10. **Documentación con Swagger / API Documentation with Swagger**
    - Documentación completa de todos los endpoints
    - Facilita pruebas y entendimiento de la API  
      Complete documentation for all endpoints  
      Makes API testing and understanding easier.

---

## 💡 Beneficios esperados / Expected Benefits

- Código limpio y mantenible / Clean and maintainable code
- Sistema más seguro y robusto / More secure and robust system
- Escalabilidad y facilidad de pruebas / Easier testing and scalability
- Configuración profesional para cualquier entorno / Professional setup for any environment

---

## ⚠️ Nota / Note

Esta versión aún está en desarrollo y se construirá sobre la versión legacy, garantizando compatibilidad con frontend y app.  
This version is under development and will be built on top of the legacy version, ensuring compatibility with frontend and app.