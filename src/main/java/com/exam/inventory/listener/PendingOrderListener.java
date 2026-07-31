package com.exam.inventory.listener;

import com.exam.inventory.dto.DeductStockItemRequest;
import com.exam.inventory.dto.DeductStockRequest;
import com.exam.inventory.service.InventoryService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PendingOrderListener {

    private final InventoryService inventoryService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-events", groupId = "inventory-saga-group")
    public void consumeOrderEvents(String rawPayload) {
        try {
            log.info("Saga Listener: Evento recibido en order-events -> {}", rawPayload);
            
            JsonNode root = objectMapper.readTree(rawPayload);
            String status = root.path("status").asText();
            String orderId = root.path("orderId").asText();

            // Filtrar únicamente los eventos PENDIENTE
            if (!"PENDIENTE".equalsIgnoreCase(status)) {
                return;
            }

            log.info("Saga Coreografiada: Se detectó orden PENDIENTE #{} tras recuperación de inventario.", orderId);

            JsonNode itemsNode = root.path("items");
            List<DeductStockItemRequest> items = new ArrayList<>();
            
            if (itemsNode.isArray() && !itemsNode.isEmpty()) {
                for (JsonNode item : itemsNode) {
                    items.add(new DeductStockItemRequest(
                        item.path("productCode").asText(),
                        item.path("quantity").asInt()
                    ));
                }
            } else {
                log.error("Saga Fallida: La orden #{} no incluyó la lista de 'items' en el evento Kafka.", orderId);
            }

            boolean success = inventoryService.deductStock(new DeductStockRequest(items));

            // Publicar respuesta hacia 'inventory-events'
            String resultStatus = success ? "INVENTORY_SUCCESS" : "INVENTORY_FAILED";
            String responseEvent = String.format("{\"orderId\":\"%s\", \"status\":\"%s\"}", orderId, resultStatus);
            
            kafkaTemplate.send("inventory-events", orderId, responseEvent);
            log.info("Saga Coreografiada: Evento enviado a [inventory-events] para orden #{}: {}", orderId, resultStatus);

        } catch (Exception e) {
            log.error("Error al procesar el evento de reconciliación de la Saga en Inventario", e);
        }
    }
}