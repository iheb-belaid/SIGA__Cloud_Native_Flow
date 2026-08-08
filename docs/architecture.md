# Architecture du projet

## Vue applicative

```mermaid
flowchart LR
    user[Utilisateur] --> ingress[MicroK8s Ingress]
    ingress --> frontend[Frontend Angular / Nginx]
    ingress --> backend[Backend Spring Boot]
    frontend -->|/api via reverse proxy| backend
    backend --> postgres[(PostgreSQL avec PVC)]
    backend --> actuator[/Actuator health + Prometheus/]
```

Le frontend est servi par Nginx. Les appels commencant par `/api` sont
rediriges vers le Service Kubernetes `backend`. Le backend utilise PostgreSQL
et expose ses metriques sur `/actuator/prometheus`.

## Vue plateforme

```mermaid
flowchart TB
    github[Repository GitHub]
    runner[Runner self-hosted Ubuntu]
    ci[CI : tests et build]
    quality[Quality/Security : SonarQube + OWASP]
    release[Release : Docker + Trivy]
    ghcr[GHCR]
    tags[gitops/environments/test/image-tags.yaml]
    argocd[ArgoCD]
    helm[Helm chart todo-platform]
    k8s[MicroK8s : cloud-native-flow]
    prometheus[Prometheus]
    grafana[Grafana]
    sonar[SonarQube local]

    github --> runner
    runner --> ci
    ci --> quality
    quality --> sonar
    quality --> release
    release --> ghcr
    release --> tags
    tags --> argocd
    argocd --> helm
    helm --> k8s
    k8s --> prometheus
    prometheus --> grafana
    argocd --> prometheus
```

## Flux de livraison

```mermaid
sequenceDiagram
    actor Developer
    participant GitHub
    participant Actions as GitHub Actions
    participant GHCR
    participant ArgoCD
    participant K8s as MicroK8s
    participant Grafana

    Developer->>GitHub: Push du commit UI sur main
    GitHub->>Actions: CI
    Actions->>Actions: Tests backend/frontend et build
    Actions->>Actions: SonarQube, OWASP et Trivy
    Actions->>GHCR: Push des images SHA
    Actions->>GitHub: Mise a jour image-tags.yaml
    ArgoCD->>GitHub: Detection du changement GitOps
    ArgoCD->>K8s: Synchronisation Helm
    K8s-->>ArgoCD: Application Healthy
    K8s->>Grafana: Metriques Kubernetes et Spring Boot
    Developer->>Grafana: Verification des dashboards
```

## Responsabilites des composants

| Composant | Responsabilite |
|---|---|
| GitHub | Versionner le code et les configurations |
| GitHub Actions | Automatiser validation, securite et release |
| GHCR | Stocker les images Docker immuables par SHA |
| Helm | Decrire le deploiement parametrable |
| ArgoCD | Reconciler Git et etat Kubernetes |
| MicroK8s | Executer les workloads sur la VM Ubuntu |
| Prometheus | Collecter les metriques |
| Grafana | Visualiser et explorer les metriques |
