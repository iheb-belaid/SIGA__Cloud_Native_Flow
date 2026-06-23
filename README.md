# 📋 ToDo App - Spring Boot & CI/CD Deployment

Ce projet est une application web de gestion de tâches (*ToDo App*) développée avec **Spring Boot** (backend Java), et conçue pour être déployée à l’aide d’un pipeline **CI/CD** complet avec **Jenkins**, **Docker**, et **VM Ubuntu**.

---

## 🚀 Fonctionnalités

- ✅ Ajouter, afficher et supprimer des tâches  
- ✅ Backend développé en **Spring Boot**  
- ✅ Base de données **PostgreSQL**  
- ✅ Pipeline CI/CD automatisé avec **Jenkins**  
- ✅ Conteneurisation avec **Docker**  
- ✅ Déploiement automatique sur une **VM Ubuntu** via `scp` et `docker-compose`

---

## 🛠 Technologies utilisées

| Technologie     | Rôle                                 |
|----------------|--------------------------------------|
| Spring Boot     | Backend Java (REST API)             |
| PostgreSQL      | Base de données relationnelle        |
| Maven           | Outil de build Java                  |
| Jenkins         | Intégration et déploiement continu   |
| GitHub          | Hébergement du code source           |
| Docker          | Conteneurisation de l’application    |
| docker-compose  | Orchestration locale (sur VM)        |

---

## 📂 Structure du projet

```bash
.
├── src/                        # Code source Java
├── target/                     # Fichiers compilés (.jar)
├── Dockerfile                  # Image Docker de l'app
├── docker-compose.yml          # Déploiement multi-service (app + db)
├── Jenkinsfile                 # Pipeline Jenkins CI/CD
├── README.md                   # Ce fichier
└── ...
```

## 🔁 CI/CD avec Jenkins

Le pipeline Jenkins est structuré en 4 étapes principales :

Clonage du dépôt GitHub

Compilation du projet avec Maven (mvn clean package)

Transfert automatique du .jar, du Dockerfile et du docker-compose.yml vers la VM Ubuntu

Déploiement automatique via docker compose sur la VM

Une notification mail est envoyée à la fin du pipeline en cas de succès.

📦 Conteneurisation
Le Dockerfile construit une image Spring Boot à partir du .jar :

```bash
FROM openjdk:17-jdk-slim
ARG jar_FILE=target/ToDo-0.0.1-SNAPSHOT.jar
COPY ${jar_FILE} app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```
Le fichier docker-compose.yml orchestre les services suivants :

Un conteneur pour l'application Spring Boot

Un conteneur PostgreSQL

Un volume Docker pour persister les données

##  📧 Notification par email
À la fin du pipeline Jenkins, une notification mail est envoyée pour indiquer le succès du déploiement.
Configuration réalisée dans Manage Jenkins > Configure System > Extended Email Notification avec SMTP.

## 📌 Auteurs
👤 Iheb Belaid
Stage d’été DevOps (2025) – ST2i, Tunis
Projet : Mise en place d’un pipeline CI/CD avec Jenkins et déploiement sur VM avec Docker.

## 📄 Licence
Ce projet est open-source à but pédagogique, sans licence commerciale.

##  📷 Captures d'écran
<img width="1407" height="414" alt="image" src="https://github.com/user-attachments/assets/eadc86e4-2284-473c-b949-fd5376dfcf42" />
