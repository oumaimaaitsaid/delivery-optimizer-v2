package com.delivery.optimizer.optimizer;

import com.delivery.optimizer.model.Delivery;
import com.delivery.optimizer.model.Tour; // Ghadi n7tajoh bach n-parsew l jawab
import com.delivery.optimizer.model.Warehouse;

// Imports li 7ellina bihom l mochkil l ouel
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.ai.ollama.OllamaChatClient;

// Imports jdad
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList; // <-- Import jdid
import java.util.List;

@Component
@ConditionalOnProperty(name = "optimizer.type", havingValue = "AI") // Drna l configuration
public class AIOptimizer implements TourOptimizer { // Kan-implementiw l interface

    private final OllamaChatClient aiClient;

    @Autowired
    public AIOptimizer(OllamaChatClient aiClient) {
        this.aiClient = aiClient;
    }

    // --- HNA L CORRECTION ---
    // L method daba khassha trejje3 "List<Delivery>" machi "Tour"
    @Override
    public List<Delivery> calculateOptimalTour(Warehouse warehouse, List<Delivery> deliveries) {

        System.out.println("--- Démarrage de l'Optimiseur IA (AIOptimizer) ---");

        // 1. Nqado l prompt (l message l l'IA)
        String prompt = "Bonjour, j'ai une tâche d'optimisation..."; // Ghadi nbdlo had l prompt

        // 2. N3eyyto l l'IA
        String aiResponse = aiClient.call(prompt);

        // 3. N-parsiw l jawab (JSON)
        System.out.println("Réponse de l'IA: " + aiResponse);

        // 4. N-construiw l lista dial les Deliveries men l jawab
        List<Delivery> optimizedList = new ArrayList<>();
        // ... (Logique pour parser le JSON et remplir la liste) ...

        System.out.println("--- Fin de l'Optimiseur IA ---");

        // Daba l return type s7i7
        return optimizedList;
    }
}