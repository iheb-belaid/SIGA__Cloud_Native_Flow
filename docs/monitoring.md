# Monitoring et observabilite

Cette phase ajoute Prometheus et Grafana au cluster MicroK8s. Prometheus collecte
les metriques Kubernetes, du backend Spring Boot et d'ArgoCD. Grafana affiche
ces donnees sous forme de tableaux de bord.

## Architecture

```text
MicroK8s / pods / services / ArgoCD
              |
       ServiceMonitor CRDs
              |
          Prometheus
              |
           Grafana
```

Le backend expose ses metriques avec Actuator sur `/actuator/prometheus`.
Le fichier `deploy/helm/todo-platform/templates/backend-servicemonitor.yaml`
demande a Prometheus de lire cette URL sur le Service Kubernetes `backend`.

Le frontend et PostgreSQL sont surveilles au niveau Kubernetes : disponibilite
des pods, redemarrages, CPU et memoire. La surveillance SQL detaillee de
PostgreSQL necessiterait l'ajout ulterieur de `postgres-exporter`.

## Installation

Pre-requis MicroK8s : `dns`, `hostpath-storage` et `metrics-server` doivent etre
actives. Ne pas activer en parallele l'addon MicroK8s `prometheus` ou
`observability`, car cette implementation utilise Helm.

```bash
microk8s status --wait-ready
microk8s kubectl top nodes
microk8s helm3 repo add prometheus-community https://prometheus-community.github.io/helm-charts
microk8s helm3 repo update
microk8s kubectl create namespace monitoring --dry-run=client -o yaml | microk8s kubectl apply -f -
```

Le mot de passe Grafana est cree hors Git :

```bash
read -s -p "Mot de passe Grafana : " GRAFANA_PASSWORD
echo
microk8s kubectl create secret generic grafana-admin \
  -n monitoring \
  --from-literal=admin-user=admin \
  --from-literal=admin-password="$GRAFANA_PASSWORD"
unset GRAFANA_PASSWORD
```

Installation de la stack :

```bash
microk8s helm3 upgrade --install monitoring \
  prometheus-community/kube-prometheus-stack \
  -n monitoring \
  -f deploy/monitoring/kube-prometheus-stack-values.yaml \
  --wait \
  --timeout 20m
```

Les valeurs `serviceMonitorSelectorNilUsesHelmValues: false` et
`serviceMonitorNamespaceSelector: {}` permettent a Prometheus de decouvrir le
ServiceMonitor du backend dans `cloud-native-flow` et ceux d'ArgoCD dans
`argocd`.

## Metriques ArgoCD

Les metriques des composants ArgoCD sont activees dans
`deploy/argocd/values.yaml`. Apres l'installation de Prometheus, appliquer la
configuration ArgoCD :

```bash
microk8s helm3 upgrade argocd argo/argo-cd \
  -n argocd \
  -f deploy/argocd/values.yaml \
  --wait \
  --timeout 10m
```

Verifier les ServiceMonitors :

```bash
microk8s kubectl get servicemonitors -A
microk8s kubectl get application todo-test -n argocd
```

## Acces local

Afficher l'adresse IP de la VM :

```bash
hostname -I
```

Grafana :

```bash
microk8s kubectl port-forward --address 0.0.0.0 \
  -n monitoring svc/monitoring-grafana 3000:80
```

Prometheus :

```bash
microk8s kubectl port-forward --address 0.0.0.0 \
  -n monitoring svc/monitoring-kube-prometheus-prometheus 9090:9090
```

Depuis Windows, ouvrir `http://IP_VM:3000` pour Grafana et
`http://IP_VM:9090/targets` pour Prometheus. L'utilisation de
`--address 0.0.0.0` est reservee a la VM de demonstration locale.

## Verification

```bash
microk8s kubectl get pods -n monitoring
microk8s kubectl get servicemonitors -A
microk8s kubectl get prometheus -n monitoring
microk8s kubectl get pvc -n monitoring
```

Dans Prometheus, verifier les cibles dans `Status > Target health`. Les cibles
doivent etre `UP`.

Requetes utiles :

```promql
up
up{namespace="cloud-native-flow"}
kube_pod_status_ready{namespace="cloud-native-flow",condition="true"}
jvm_memory_used_bytes
rate(http_server_requests_seconds_count[5m])
argocd_app_info{name="todo-test"}
```

## Dashboards Grafana

Les dashboards Kubernetes fournis par `kube-prometheus-stack` sont actifs par
defaut. Pour la demonstration, verifier au minimum :

- Kubernetes / Compute Resources / Cluster
- Kubernetes / Compute Resources / Namespace (Pods)
- Node Exporter / Nodes
- un dashboard Spring Boot base sur les metriques JVM et HTTP
- un dashboard ArgoCD sur la synchronisation et la sante des applications

Le dashboard de demonstration doit montrer :

- l'etat du noeud MicroK8s et sa consommation CPU/memoire ;
- les pods backend, frontend et PostgreSQL ;
- la memoire JVM, le nombre de requetes et les erreurs HTTP du backend ;
- l'etat `Synced` et `Healthy` de l'application `todo-test`.

## Diagnostic rapide

```bash
microk8s kubectl describe servicemonitor -n cloud-native-flow todo-platform-backend
microk8s kubectl logs -n monitoring statefulset/monitoring-kube-prometheus-prometheus
microk8s kubectl get events -n monitoring --sort-by=.metadata.creationTimestamp
```
