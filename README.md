# Inventory Service

Microservicio responsable del control de stock de productos por SKU (`productCode`). Procesa deducciones de inventario de forma atómica bajo el principio *All-or-Nothing* para evitar descuentos parciales.

## 🚀 Tecnologías
* **Runtime:** Java 17 / Spring Boot 3.x
* **Base de Datos:** H2
* **Mensajería:** Apache Kafka
* **Transaccionalidad:** Spring `@Transactional` con aislamiento en lotes

## ⚙️ Puerto y Endpoints
* **Puerto Local:** `8081`
* **Consola H2 (Dev):** `http://localhost:8081/h2-console` (JDBC URL: `jdbc:h2:mem:inventory_db`)
* **Endpoints HTTP:**
  * `POST /api/v1/inventory/` - Cargar o incrementar stock.
  * `GET /api/v1/inventory/{productCode}` - Consultar stock.
  * `POST /api/v1/inventory/deduct` - Descontar el stock.
  * `GET /actuator/health/readiness` - Health Check.

## 🛠️ Variables de Entorno Clave (Perfil `local`)
```env
SERVER_PORT=8081
SPRING_PROFILES_ACTIVE=local
SPRING_DATASOURCE_URL=jdbc:h2:mem:inventory_db
SPRING_H2_CONSOLE_ENABLED=true
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
