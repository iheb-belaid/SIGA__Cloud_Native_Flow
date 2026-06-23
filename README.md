# SIGA Cloud Native Flow

Projet realise dans le cadre d'un stage DevOps chez SIGA autour du sujet :

**Cloud Native Flow : Automatisation CI/CD et Deploiement GitOps Securise**

## Objectif

Ce projet sert de base applicative pour mettre en place ensuite une chaine DevOps complete autour d'une application de gestion de taches.

L'objectif est de disposer d'une application separee en :

- un backend Spring Boot
- un frontend Angular
- une base de donnees PostgreSQL

Puis d'utiliser cette base pour construire les prochaines etapes du sujet :

- integration continue
- livraison continue
- deploiement GitOps
- securisation du cycle de deploiement

## Stack technique

- Java 21
- Spring Boot 3
- Angular 21
- PostgreSQL
- Maven
- npm

## Structure du projet

```text
.
|-- backend/   # API Spring Boot
|-- frontend/  # Application Angular
`-- README.md
```

## Fonctionnalites actuelles

- creation de categories
- creation de taches
- modification de taches
- suppression de taches
- affichage en tableau Kanban
- changement de statut par glisser-deposer

## Lancement du backend

Depuis le dossier `backend` :

```bash
./mvnw spring-boot:run
```

Sous Windows PowerShell :

```powershell
.\mvnw.cmd spring-boot:run
```

Le backend demarre par defaut sur :

```text
http://localhost:8081
```

## Lancement du frontend

Depuis le dossier `frontend` :

```bash
npm install
npm start
```

Le frontend demarre par defaut sur :

```text
http://localhost:4200
```

## Base de donnees

L'application utilise PostgreSQL avec la base :

```text
Siga-todo-cloud-native-Flow
```

Le backend a ete prepare pour creer automatiquement cette base si PostgreSQL est disponible localement et que la base n'existe pas encore.

## Auteur

Iheb Belaid  
Stage DevOps - SIGA
