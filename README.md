# Inventory Service

Microservicio responsable del control de stock de productos por SKU (`productCode`). Procesa deducciones de inventario de forma atómica bajo el principio *All-or-Nothing* para evitar descuentos parciales.

## 🚀 Tecnologías
* **Runtime:** Java 21 / Spring Boot 3.x
* **Base de Datos:** PostgreSQL (Aurora Serverless v2)
* **Mensajería:** Apache Kafka
* **Transaccionalidad:** Spring `@Transactional` con aislamiento en lotes

## ⚙️ Puerto y Endpoints
* **Puerto Local:** `8081`
* **Endpoints HTTP:**
  * `POST /api/v1/inventory/deduct` - Deducción atómica de stock en lote (REST Síncrono).
  * `GET /actuator/health/readiness` - Health Check ALB.

## 🔄 Integración de Eventos (Kafka)
* **Consumidor:** `order-events` (Escucha ordenes en estado `PENDING` para reconciliación asíncrona).
* **Productor:** `inventory-events` (Publica `INVENTORY_SUCCESS` o `INVENTORY_FAILED`).

## 🛠️ Variables de Entorno Clave
```env
SERVER_PORT=8081
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/inventory_db
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092