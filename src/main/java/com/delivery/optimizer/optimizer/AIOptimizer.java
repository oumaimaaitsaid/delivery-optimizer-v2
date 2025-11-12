package com.delivery.optimizer.optimizer;

import com.delivery.optimizer.model.Delivery;
import com.delivery.optimizer.model.DeliveryHistory;
import com.delivery.optimizer.model.Warehouse;
import com.delivery.optimizer.repository.DeliveryHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@ConditionalOnProperty(name = "optimizer.type", havingValue = "AI")
public class AIOptimizer implements TourOptimizer {

    private final OllamaChatClient aiClient;
    private final DeliveryHistoryRepository historyRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public AIOptimizer(OllamaChatClient aiClient,
                       DeliveryHistoryRepository historyRepository,
                       ObjectMapper objectMapper) {
        this.aiClient = aiClient;
        this.historyRepository = historyRepository;
        this.objectMapper = objectMapper;
    }


    private record AiOptimizationResponse(List<Long> orderedDeliveries, String recommendations) {
    }

    @Override
    public List<Delivery> calculateOptimalTour(Warehouse warehouse, List<Delivery> deliveries) {
        System.out.println("--- Démarrage de l'Optimiseur IA (AIOptimizer) ---");

        try {

            List<DeliveryHistory> history = historyRepository.findAll();
            String historyJson = objectMapper.writeValueAsString(history);
            String deliveriesJson = objectMapper.writeValueAsString(deliveries);
            String prompt = """
                TÂCHE TRÈS STRICTE: Répondez UNIQUEMENT avec un objet JSON. Ne dites rien d'autre.
                
                CONTEXTE:
                Je suis un service de logistique. Je dois ordonnancer des livraisons.
                
                DONNÉES:
                1. HISTORIQUE (Anciennes livraisons, analysez les retards "delay_in_minutes"):
                %s
                
                2. NOUVELLES LIVRAISONS (Celles-ci doivent être ordonnancées):
                %s
                
                INSTRUCTIONS:
                1. Analysez l'HISTORIQUE.
                2. Ordonnancez (trier) les NOUVELLES LIVRAISONS dans la liste "orderedDeliveries" (mettez juste les IDs).
                3. Écrivez une recommandation courte dans "recommendations".
                
                FORMAT DE SORTIE (UNIQUEMENT JSON, SANS AUCUN AUTRE TEXTE):
                {
                  "orderedDeliveries": [ID_1, ID_2, ...],
                  "recommendations": "Votre explication ici."
                }
                """.formatted(
                    historyJson,
                    deliveriesJson
            );
            System.out.println("--- Envoi du Prompt à l'IA ---");
            String aiResponse = aiClient.call(prompt);
            System.out.println("Réponse BRUTE de l'IA: " + aiResponse);
            String jsonResponse = extractJson(aiResponse);
            AiOptimizationResponse response = objectMapper.readValue(jsonResponse, AiOptimizationResponse.class);
            System.out.println("Recommandations de l'IA: " + response.recommendations());
            List<Long> optimizedIds = response.orderedDeliveries();
            Map<Long, Delivery> deliveryMap = deliveries.stream()
                    .collect(Collectors.toMap(Delivery::getId, delivery -> delivery));
            List<Delivery> optimizedList = new ArrayList<>();
            for (Long id : optimizedIds) {
                if (deliveryMap.containsKey(id)) {
                    optimizedList.add(deliveryMap.get(id));
                }
            }

            System.out.println("--- Fin de l'Optimiseur IA (Succès) ---");
            return optimizedList;

        } catch (Exception e) {
            System.err.println("ERREUR: L'optimiseur IA a échoué: " + e.getMessage());
            e.printStackTrace();
            return deliveries;
        }
    }
    private String extractJson(String text) {

        Pattern pattern = Pattern.compile("\\{(?s)(.*)\\}");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {

            return "{" + matcher.group(1) + "}";
        } else {

            throw new RuntimeException("Réponse de l'IA invalide: Aucun JSON trouvé.");
        }
    }
}