# Runbook de demonstration finale

Ce runbook est destine a la presentation du flux complet du projet SIGA.
Le scenario utilise un petit changement UI visible afin de prouver que la
nouvelle image a ete construite et deployee.

## 1. Preparer l'environnement

Sur la VM Ubuntu :

```bash
hostname -I
microk8s status --wait-ready
microk8s kubectl get nodes
microk8s kubectl get pods -A
sudo systemctl status actions.runner.*
```

Verifier aussi que le runner est `Idle` dans GitHub et que SonarQube est UP :

```bash
curl -sS --max-time 10 http://127.0.0.1:9000/api/system/status
```

Verifier les services attendus :

```bash
microk8s kubectl get pods -n cloud-native-flow
microk8s kubectl get pods -n argocd
microk8s kubectl get pods -n monitoring
```

## 2. Valider l'Ingress

```bash
microk8s kubectl get ingressclass
microk8s kubectl get ingress -n cloud-native-flow
microk8s kubectl describe ingress -n cloud-native-flow
```

Configurer `<VM_IP> todo.local` dans le fichier hosts Windows, puis tester :

```powershell
curl.exe -H "Host: todo.local" http://<VM_IP>/api/categories
```

## 3. Creer le commit de demonstration

Dans le poste de developpement :

```powershell
git status
git pull --rebase origin main
```

Modifier uniquement un texte visible dans le tableau Angular, sans changer la
logique metier. Exemple : ajouter le suffixe `Cloud Native Flow` au titre du
tableau.

```powershell
git add frontend
git commit -m "demo: update todo board for end-to-end delivery"
git push origin main
```

## 4. Suivre GitHub Actions

Dans `Actions`, verifier dans l'ordre :

1. `CI` : tests backend/frontend et build Angular;
2. `Quality And Security` : SonarQube, OWASP et rapports;
3. `Release And GitOps` : build Docker, Trivy, GHCR et mise a jour GitOps.

Capturer une preuve uniquement quand chaque workflow est vert. Telecharger les
artefacts de couverture et OWASP sans exposer de secret.

## 5. Verifier GHCR et GitOps

Dans le repository GitHub, verifier les deux images :

```text
ghcr.io/<owner>/todo-backend:sha-<shortsha>
ghcr.io/<owner>/todo-frontend:sha-<shortsha>
```

Le fichier `gitops/environments/test/image-tags.yaml` doit contenir les memes
tags SHA. Si le workflow a ajoute un commit automatique, mettre le poste local
a jour :

```powershell
git pull --rebase origin main
```

## 6. Verifier ArgoCD et Kubernetes

Sur la VM :

```bash
microk8s kubectl get application todo-test -n argocd \
  -o jsonpath='{.status.sync.status}{" / "}{.status.health.status}{"\n"}'

microk8s kubectl get pods -n cloud-native-flow
microk8s kubectl get svc -n cloud-native-flow
microk8s kubectl get ingress -n cloud-native-flow

microk8s kubectl get deployment -n cloud-native-flow \
  -o custom-columns=NAME:.metadata.name,IMAGE:.spec.template.spec.containers[0].image
```

Resultats attendus : `Synced / Healthy`, pods `Running` et images portant le
nouveau tag SHA.

## 7. Tester l'application

Depuis Windows ou la VM :

```bash
curl http://todo.local/actuator/health
curl http://todo.local/api/categories
curl http://todo.local/api/tasks/board
```

Dans le navigateur, ouvrir `http://todo.local`, verifier le nouveau texte UI,
creer une categorie puis creer une tache. Retourner au tableau pour confirmer
que la tache est visible.

## 8. Verifier Prometheus et Grafana

Dans Prometheus, `Status > Target health` doit afficher le backend et les
metriques ArgoCD en `UP`.

Dans Grafana, ouvrir les dashboards Kubernetes Cluster, Namespace Pods pour
`cloud-native-flow` et Namespace Pods pour `argocd`.

Dans `Explore`, executer :

```promql
up{job="backend"}
jvm_memory_used_bytes{job="backend"}
rate(http_server_requests_seconds_count{job="backend"}[5m])
up{job=~".*argocd.*"}
```

Executer plusieurs appels API avant la requete HTTP pour faire apparaitre du
trafic dans le graphique.

## 9. Capturer les preuves

Enregistrer les captures selon la nomenclature de
[docs/evidence/README.md](evidence/README.md). Ne jamais inclure un mot de
passe, un token ou une valeur de Secret dans une capture.

## 10. Conclusion de la demonstration

La demonstration est reussie lorsque le commit UI est visible dans GitHub,
les trois workflows sont verts, les images GHCR portent le nouveau SHA,
ArgoCD affiche `Synced / Healthy`, l'application fonctionne via Ingress et
les dashboards Grafana montrent les metriques du cluster, du backend et
d'ArgoCD.
