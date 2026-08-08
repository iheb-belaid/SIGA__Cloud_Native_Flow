# Preuves du livrable

Ce dossier contient les captures et exports utilises pendant la presentation
finale. Les fichiers sont numerotes selon le parcours du commit jusqu'au
monitoring.

| Fichier | Preuve attendue |
|---|---|
| `01-ci-green.png` | CI verte, tests et build |
| `02-quality-security-green.png` | SonarQube, OWASP et rapports |
| `03-release-ghcr-trivy-green.png` | Trivy et push GHCR |
| `04-gitops-image-tags.png` | Tags SHA dans `image-tags.yaml` |
| `05-argocd-synced-healthy.png` | ArgoCD `Synced / Healthy` |
| `06-kubernetes-pods.png` | Pods applicatifs `Running` |
| `07-application-ingress.png` | Application accessible via `todo.local` |
| `08-grafana-cluster.png` | Dashboard Kubernetes Cluster |
| `09-grafana-todo-namespace.png` | Pods du namespace `cloud-native-flow` |
| `10-grafana-backend.png` | JVM et requetes Spring Boot |
| `11-grafana-argocd.png` | Metriques ArgoCD |

## Regles de securite

- ne pas capturer `SONAR_TOKEN`;
- ne pas capturer le mot de passe Grafana;
- ne pas capturer un token GitHub ou GHCR;
- masquer les valeurs sensibles avant de committer une image;
- conserver les logs textuels dans GitHub Actions plutot que de copier des
  secrets dans ce dossier.

## Resultats a noter dans le rapport

- SHA du commit de demonstration;
- statut de chaque workflow;
- tags des images GHCR;
- statut ArgoCD;
- noms et statuts des pods;
- cibles Prometheus `UP`;
- requetes PromQL utilisees;
- date et adresse IP de la VM au moment de la demonstration.
