# SIGA Cloud Native Flow

Projet realise dans le cadre du stage DevOps chez SIGA autour du sujet :

**Cloud Native Flow : Automatisation CI/CD et Deploiement GitOps Securise**

## Objectif

Transformer une application Todo Spring Boot / Angular / PostgreSQL en une
plateforme demonstrable avec CI/CD, DevSecOps, conteneurs, Kubernetes, GitOps
et observabilite.

## Architecture finale

```text
Utilisateur
    |
    v
MicroK8s Ingress -> Frontend Angular/Nginx -> Backend Spring Boot -> PostgreSQL
                                      |
                                      +-> Actuator/Prometheus

GitHub push -> CI -> Quality/Security -> Release/GitOps -> GHCR
                                                     |
                                                     v
                                           gitops/image-tags.yaml
                                                     |
                                                     v
                                                  ArgoCD
                                                     |
                                                     v
                                             MicroK8s / Helm
                                                     |
                                                     v
                                           Prometheus -> Grafana
```

Les schemas detailles sont dans [docs/architecture.md](docs/architecture.md).

## Stack technique

- Java 21, Spring Boot 3 et Maven
- Angular 21, Node.js 22 et Nginx
- PostgreSQL
- Docker et Docker Compose
- GitHub Actions sur un runner self-hosted Ubuntu
- GHCR pour les images Docker
- SonarQube, OWASP Dependency-Check et Trivy
- MicroK8s et Helm
- ArgoCD pour le GitOps
- Prometheus et Grafana pour le monitoring

## Fonctionnalites applicatives

- creation de categories et de taches
- modification et suppression de taches
- tableau Kanban
- changement de statut par glisser-deposer
- API de sante et metriques Spring Boot

## Demarrage local

Pour lancer l'ensemble avec Docker Compose :

```bash
cp deploy/docker/.env.example deploy/docker/.env
docker compose --env-file deploy/docker/.env \
  -f deploy/docker/docker-compose.dev.yml up --build
```

L'application est ensuite disponible sur `http://localhost:4200`.

Les variables backend conservees comme interface sont :

- `SPRING_PROFILES_ACTIVE`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_CORS_ALLOWED_ORIGINS`

## Workflows GitHub Actions

Les workflows sont executes dans cet ordre :

1. `ci.yml` : tests backend, tests frontend et build Angular.
2. `quality-security.yml` : couverture, SonarQube et OWASP.
3. `release-gitops.yml` : images Docker, Trivy, GHCR et mise a jour GitOps.

Le detail des jobs et des secrets est documente dans
[docs/ci-cd.md](docs/ci-cd.md).

## Kubernetes, GitOps et monitoring

- Chart Helm : `deploy/helm/todo-platform`
- Valeurs de l'environnement de test : `gitops/environments/test`
- Bootstrap ArgoCD : `gitops/bootstrap/argocd`
- Valeurs Prometheus/Grafana : `deploy/monitoring`

Guides disponibles :

- [Architecture](docs/architecture.md)
- [CI/CD et DevSecOps](docs/ci-cd.md)
- [MicroK8s](docs/microk8s.md)
- [GitOps avec ArgoCD](docs/gitops.md)
- [Monitoring](docs/monitoring.md)
- [Runbook de demonstration](docs/demo-runbook.md)
- [Index des preuves](docs/evidence/README.md)

## Documentation de demonstration

Le scenario final couvre le flux suivant :

```text
Commit UI -> CI -> SonarQube/OWASP -> Docker/Trivy -> GHCR
-> mise a jour GitOps -> ArgoCD -> MicroK8s -> Grafana
```

Le runbook fournit les commandes, les validations et les captures attendues.

## Auteur

Iheb Belaid  
Stage DevOps - SIGA
