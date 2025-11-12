# 🚚 Système d'Optimisation de Tournées V2 (avec IA)

Ce projet est une application Spring Boot qui gère et optimise des tournées de livraison.

Cette V2 est une refonte majeure de la V1. Elle remplace la configuration XML et `data.sql` par **Spring Boot 3**, **Java 17**, la configuration **YAML** par profils, et les migrations **Liquibase**.

L'objectif principal est d'ajouter un troisième optimiseur basé sur **Spring AI (Ollama)**, capable d'analyser l'historique des livraisons (`DeliveryHistory`) pour proposer des tournées optimisées.

## 🛠️ Stack Technique (V2)

* **Core:** Spring Boot 3.2.0 (Java 17)
* **Data:** Spring Data JPA (Hibernate)
* **Base de données:** H2 (pour `dev`) & PostgreSQL (pour `qa`)
* **Migrations:** Liquibase (remplace `ddl-auto` et `data.sql`)
* **API:** Spring Web (REST)
* **Doc API:** Springdoc-OpenAPI (Swagger)
* **IA (LLM):** Spring AI
* **Moteur IA Local:** Ollama (avec les modèles `gemma:2b` )
* **Conteneurisation:** Docker
* **Utilitaires:** Lombok, Jackson (avec le module `jackson-datatype-hibernate6`)

## ✨ Fonctionnalités Clés (V2)

* **Configuration YAML:** Remplacement de `.properties` par `.yml` avec gestion des profils (`dev`, `qa`).
* **Gestion de DB (Liquibase):** Le schéma de la base de données est maintenant 100% géré par les scripts `changelog.xml`.
* **Nouvelles Entités:** Ajout de `Customer` et `DeliveryHistory` pour tracer les performances.
* **Logique Métier Avancée:** La complétion d'une `Delivery` (`/status?status=DELIVERED`) déclenche automatiquement la création d'un `DeliveryHistory`.
* **Optimiseur IA (Spring AI):** Ajout d'un `AIOptimizer` qui :
    1.  Analyse l'historique (`DeliveryHistory`).
    2.  Analyse les nouvelles livraisons.
    3.  Envoie un prompt détaillé à Ollama.
    4.  Parse la réponse JSON (l'ordre optimisé et les recommandations).
* **Activation par Profil:** Choix de l'optimiseur (IA, NN, ou CW) via `@ConditionalOnProperty` dans `application.yml`.
* **Tests:** Ajout d'un test d'intégration (`@SpringBootTest`) pour l'API `CustomerController`.
* **Docker:** Un `Dockerfile` multi-stage est inclus pour la conteneurisation.

## 🚀 Démarrage Rapide (L'essentiel)

Ce projet nécessite **Ollama** (le moteur IA) pour fonctionner en mode "AI".

### 1. Prérequis (Installation)

1.  **Java 17 & Maven:** Assurez-vous qu'ils sont installés.
2.  **Docker Desktop (Optionnel):** Nécessaire si vous voulez utiliser le `Dockerfile`.
3.  **Ollama (OBLIGATOIRE):**
    * Téléchargez et installez Ollama depuis [ollama.com](https://ollama.com/).
    * Lancez l'application Ollama (l'icône doit apparaître dans votre barre des tâches).
    * Ouvrez un terminal et téléchargez les modèles (nous recommandons `gemma:2b` qui est léger) :
        ```bash
        ollama pull gemma:2b
        ```
      *(Si vous avez une bonne carte graphique, vous pouvez aussi tester `ollama pull mistral`)*

### 2. Configuration (`application.yml`)

Le fichier `application.yml` est la nouvelle configuration. Assurez-vous que le modèle d'IA est correct :

```yaml
spring:
  # ... (configuration H2, JPA, etc.)
  
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        model: gemma:2b # <-- T2ekked belli l model howa li 3endek (gemma:2b ola mistral)

optimizer:
  type: "AI" # <-- Hada howa li kay-activer l'IA. (Beddelha l "NN" ila bghiti t-testi l khor)
```
### 3. Lancer l'Application
Assurez-vous qu'Ollama est en cours d'exécution (l'icône est visible).

Lancez l'application Spring Boot :
```
mvn clean spring-boot:run
```
🔗 Liens Utiles (Une fois lancé)

* Swagger UI (Documentation API): http://localhost:8080/swagger-ui.html

* H2 Console (Base de données): http://localhost:8080/h2-console

  * JDBC URL: jdbc:h2:file:./data/deliveriesdb

  * User: sa

  * Password: (laissez vide)

🧪 Scénario de Test V2 (Démo)
Voici comment tester la fonctionnalité principale de V2 (l'IA) avec Insomnia (ou Bruno).

Étape 1: Créer l'Historique (Simuler une livraison passée)
Nous devons d'abord créer un "historique" pour que l'IA puisse l'analyser.

1. POST /api/customers (Créer un client 1)

2. POST /api/deliveries (Créer une livraison 1, la lier au client 1, et ajouter plannedTime et date)

3. PUT /api/deliveries/1/status?status=DELIVERED

  . Cette action déclenche DeliveryHistoryService.

4. Vérifier H2: Allez sur http://localhost:8080/h2-console et lancez SELECT * FROM DELIVERY_HISTORY;. Vous devriez voir une nouvelle ligne.

*  Étape 2: Demander une Optimisation IA

Maintenant que nous avons un historique, créons de nouvelles livraisons et demandons à l'IA de les optimiser.

1. POST /api/deliveries (Créer une livraison 2)

2. POST /api/deliveries (Créer une livraison 3)

3. POST /api/warehouses (Créer un entrepôt 1)

4. Lancer l'Optimiseur IA:

    * Method: POST

    * URL: http://localhost:8080/api/tours/optimize

    * Body (JSON):
   
```{
    "warehouseId": 1,
    "deliveryIds": [2, 3]
}
```
* Étape 3: Analyser la Réponse

1. Réponse JSON (Dans Insomnia): Vous recevrez la liste des livraisons ([2, 3] ou [3, 2]) dans l'ordre optimisé par l'IA, avec toutes leurs données.

2. Recommandations de l'IA (Dans le Terminal): Regardez le terminal où vous avez lancé mvn spring-boot:run. Vous verrez le log de l'IA :

```
--- Démarrage de l'Optimiseur IA (AIOptimizer) ---
--- Envoi du Prompt à l'IA ---
Réponse BRUTE de l'IA: {
  "orderedDeliveries": [2, 3],
  "recommendations": "L'ordre [2, 3] est optimal car la livraison 2 (Massira) est plus proche..."
}
Recommandations de l'IA: L'ordre [2, 3] est optimal car...
--- Fin de l'Optimiseur IA (Succès) ---
```
🐳 Bonus: Lancer avec Docker
Le projet inclut un Dockerfile multi-stage.

1. Construire l'image:
```
docker build -t delivery-optimizer .
```
2. Lancer le conteneur: (Important: host.docker.internal est nécessaire pour que le conteneur puisse "voir" Ollama qui tourne sur votre PC).

```
docker run -p 8080:8080 -e SPRING_AI_OLLAMA_BASE-URL="[http://host.docker.internal:11434](http://host.docker.internal:11434)" delivery-optimizer
```
## Diagrame de classe
<img width="893" height="635" alt="image" src="https://github.com/user-attachments/assets/eaf10df1-a247-48cd-86b8-629a0c6a76cd" />

📁 Structure du Projet (Fichiers Clés)

```
├── main
│   ├── java/com/delivery/optimizer
│   │   ├── controller  (Points d'entrée API: TourController, CustomerController...)
│   │   ├── dto         (DTOs: CompareRequest, DeliveryDTO...)
│   │   ├── mapper      (Mappers: DeliveryMapper, CustomerMapper...)
│   │   ├── model       (Entités JPA: Delivery, Customer, DeliveryHistory...)
│   │   ├── optimizer   (Les 3 algos: AIOptimizer, NearestNeighborOptimizer...)
│   │   ├── repository  (Spring Data JPA: DeliveryRepository, CustomerRepository...)
│   │   ├── service     (Logique métier: TourService, DeliveryHistoryService...)
│   │   └── DeliveryOptimizerApplication.java (Classe principale + Bean Hibernate6Module)
│   │
│   └── resources
│       ├── db/changelog/ (Tous les scripts Liquibase)
│       └── application.yml (Configuration principale)
│
└── test
    └── java/com/delivery/optimizer
        └── CustomerControllerTest.java (Test d'intégration V2)
        ```

