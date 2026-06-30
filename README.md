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

Profils Spring Boot disponibles :

- `dev` par defaut pour le developpement local
- `prod` pour les executions conteneurisees et les environnements de deploiement

Variables d'environnement principales :

- `SPRING_PROFILES_ACTIVE`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_CORS_ALLOWED_ORIGINS`

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

Le frontend utilise maintenant des fichiers d'environnement Angular :

- `src/environments/environment.ts` pour le developpement
- `src/environments/environment.prod.ts` pour la production

## Base de donnees

L'application utilise PostgreSQL avec la base :

```text
Siga-todo-cloud-native-Flow
```

Le backend a ete prepare pour creer automatiquement cette base si PostgreSQL est disponible localement et que la base n'existe pas encore.

## Observabilite

Le backend expose des endpoints Actuator pour preparer le monitoring :

- `http://localhost:8081/actuator/health`
- `http://localhost:8081/actuator/prometheus`

## Integration Continue

Le workflow GitHub Actions principal se trouve dans :

- `.github/workflows/ci.yml`

Cette pipeline CI est declenchee sur :

- `push` sur `main`
- `pull_request` vers `main`

Elle execute :

- les tests backend Spring Boot
- l'installation des dependances frontend avec `npm ci`
- les tests frontend Angular
- le build Angular

Le build frontend est ensuite publie comme artefact GitHub Actions sous le nom `frontend-dist`.

## Auteur

Iheb Belaid  
Stage DevOps - SIGA
