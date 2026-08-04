# GitOps avec ArgoCD

Ce document explique comment passer du deploiement Helm manuel au deploiement GitOps avec ArgoCD pour l'application Todo du projet SIGA Cloud Native Flow.

## Objectif

Avant ArgoCD, le deploiement Kubernetes est lance manuellement avec `helm upgrade --install`.

Avec ArgoCD, le cluster surveille le repository GitHub. Quand le workflow `Release And GitOps` met a jour `gitops/environments/test/image-tags.yaml`, ArgoCD detecte le changement et synchronise automatiquement l'application sur MicroK8s.

Flux cible :

```text
GitHub push
-> CI
-> Quality And Security
-> Release And GitOps
-> Push images GHCR
-> Update gitops/environments/test/image-tags.yaml
-> ArgoCD sync
-> MicroK8s deployment
```

## Fichiers ArgoCD

### `deploy/argocd/values.yaml`

Ce fichier configure l'installation d'ArgoCD via Helm.

- `global.domain`: nom logique utilise pour ArgoCD en local.
- `configs.cm.resource.customizations.health.networking.k8s.io_Ingress`: adapte l'etat de sante des Ingress a MicroK8s local. Son controleur ingress ne publie pas d'adresse LoadBalancer, alors que l'application reste accessible. Sans cette regle, ArgoCD peut afficher `Synced / Progressing` malgre des pods sains.
- `configs.params.server.insecure`: autorise l'acces HTTP local, pratique pour une demo avec port-forward.
- `server.service.type`: garde ArgoCD en `ClusterIP`, donc non expose publiquement par defaut.
- `controller.resources`: limite CPU/RAM du controleur ArgoCD.
- `repoServer.resources`: limite CPU/RAM du composant qui lit les repositories Git.
- `applicationSet.enabled`: garde ApplicationSet disponible pour une future extension multi-environnements.
- `notifications.enabled`: desactive les notifications pour simplifier la demo locale.

### `gitops/bootstrap/argocd/project.yaml`

Ce fichier cree un `AppProject` ArgoCD nomme `todo-platform`.

Son role est de limiter ce qu'ArgoCD a le droit de faire :

- lire uniquement le repository `SIGA__Cloud_Native_Flow`;
- deployer uniquement dans le namespace `cloud-native-flow`;
- autoriser la creation du namespace Kubernetes;
- autoriser les ressources namespaced necessaires comme `Deployment`, `Service`, `Secret`, `ConfigMap`, `Ingress`, `StatefulSet` et `PVC`.

### `gitops/bootstrap/argocd/app-todo-test.yaml`

Ce fichier cree l'application ArgoCD `todo-test`.

Son role est de dire a ArgoCD :

- quel repository Git surveiller;
- quel chart Helm utiliser : `deploy/helm/todo-platform`;
- quels fichiers de valeurs appliquer :
  - `gitops/environments/test/values.yaml`;
  - `gitops/environments/test/image-tags.yaml`;
- dans quel namespace deployer : `cloud-native-flow`;
- activer la synchronisation automatique.

La section `syncPolicy.automated` active :

- `prune`: supprime du cluster les ressources retirees de Git;
- `selfHeal`: corrige automatiquement une modification manuelle faite dans le cluster;
- `CreateNamespace=true`: cree le namespace cible si necessaire.

## Installation d'ArgoCD sur MicroK8s

Depuis la VM Ubuntu :

```bash
cd ~/SIGA__Cloud_Native_Flow
git pull --ff-only origin main
```

Ajouter le repository Helm officiel d'ArgoCD :

```bash
microk8s helm3 repo add argo https://argoproj.github.io/argo-helm
microk8s helm3 repo update
```

Creer le namespace ArgoCD :

```bash
microk8s kubectl create namespace argocd --dry-run=client -o yaml | microk8s kubectl apply -f -
```

Installer ArgoCD :

```bash
microk8s helm3 upgrade --install argocd argo/argo-cd \
  -n argocd \
  -f deploy/argocd/values.yaml \
  --wait \
  --timeout 10m
```

Verifier les pods :

```bash
microk8s kubectl get pods -n argocd
```

Apres une modification de `deploy/argocd/values.yaml`, appliquer la configuration versionnee :

```bash
microk8s helm3 upgrade argocd argo/argo-cd \
  -n argocd \
  -f deploy/argocd/values.yaml \
  --wait \
  --timeout 10m
```

Pour MicroK8s local, la personnalisation de sante de l'Ingress est intentionnelle : elle permet a ArgoCD de distinguer un Ingress local utilisable d'un Ingress cloud en attente d'une adresse LoadBalancer.

## Bootstrap GitOps

Appliquer le projet ArgoCD :

```bash
microk8s kubectl apply -f gitops/bootstrap/argocd/project.yaml
```

Appliquer l'application ArgoCD :

```bash
microk8s kubectl apply -f gitops/bootstrap/argocd/app-todo-test.yaml
```

Verifier l'application :

```bash
microk8s kubectl get applications -n argocd
microk8s kubectl describe application todo-test -n argocd
```

Verifier le deploiement applicatif :

```bash
microk8s kubectl get pods -n cloud-native-flow
microk8s kubectl get svc -n cloud-native-flow
microk8s kubectl get ingress -n cloud-native-flow
```

## Acces a l'interface ArgoCD

Recuperer le mot de passe admin initial :

```bash
microk8s kubectl -n argocd get secret argocd-initial-admin-secret \
  -o jsonpath="{.data.password}" | base64 -d; echo
```

Exposer ArgoCD en local :

```bash
microk8s kubectl port-forward --address 0.0.0.0 \
  -n argocd svc/argocd-server 8088:80
```

Depuis Windows, ouvrir :

```text
http://192.168.150.155:8088
```

Identifiants :

```text
Username: admin
Password: valeur recuperee depuis le secret argocd-initial-admin-secret
```

## Repository GitHub prive

Si le repository GitHub est prive, ArgoCD doit recevoir un acces en lecture.

Creer un token GitHub read-only, puis executer dans la VM :

```bash
microk8s kubectl -n argocd create secret generic repo-siga-cloud-native-flow \
  --from-literal=type=git \
  --from-literal=url=https://github.com/iheb-belaid/SIGA__Cloud_Native_Flow.git \
  --from-literal=username=TON_USERNAME_GITHUB \
  --from-literal=password=TON_TOKEN_GITHUB \
  --dry-run=client -o yaml | microk8s kubectl apply -f -
```

Ajouter le label attendu par ArgoCD :

```bash
microk8s kubectl -n argocd label secret repo-siga-cloud-native-flow \
  argocd.argoproj.io/secret-type=repository \
  --overwrite
```

Ne jamais committer ce token dans Git.

## Test du flux GitOps

Le test final consiste a faire un changement applicatif ou un commit vide, puis a verifier la chaine :

```bash
git commit --allow-empty -m "test: trigger gitops sync"
git push origin main
```

Ordre attendu :

```text
CI passe
Quality And Security passe
Release And GitOps construit et pousse les images
gitops/environments/test/image-tags.yaml est mis a jour
ArgoCD detecte le commit
ArgoCD synchronise todo-test
Les pods backend/frontend redemarrent avec les nouveaux tags
```

Verification :

```bash
microk8s kubectl get application todo-test -n argocd
microk8s kubectl get pods -n cloud-native-flow
microk8s kubectl get deployment -n cloud-native-flow \
  todo-platform-backend todo-platform-frontend \
  -o custom-columns=NAME:.metadata.name,IMAGE:.spec.template.spec.containers[0].image
```
