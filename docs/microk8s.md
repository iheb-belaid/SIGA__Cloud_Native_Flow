# MicroK8s et deploiement Kubernetes

## Architecture de demonstration

La demonstration utilise une VM Ubuntu dans VMware avec un cluster MicroK8s
a un seul noeud. Ce noeud joue a la fois le role de control plane et de noeud
de travail. Ce choix est adapte a une demonstration locale, mais ne represente
pas un cluster de production hautement disponible.

## Pre-requis

Verifier l'etat de la VM et du cluster :

```bash
hostname -I
microk8s status --wait-ready
microk8s kubectl get nodes -o wide
microk8s kubectl top nodes
free -h
df -h
```

Addons attendus :

```bash
microk8s status
```

Les addons minimum sont `dns`, `helm3`, `hostpath-storage`, `ingress` et
`metrics-server`.

## Namespace et ressources

```bash
microk8s kubectl get all -n cloud-native-flow
microk8s kubectl get pvc -n cloud-native-flow
microk8s kubectl get ingress -n cloud-native-flow
```

Le chart deploye :

- un Deployment backend;
- un Deployment frontend;
- un StatefulSet PostgreSQL;
- les Services internes;
- les ConfigMaps et Secrets;
- un PVC PostgreSQL;
- un Ingress pour `/` et `/api`.

## Ingress

La classe Ingress doit etre celle installee dans le cluster. Ne pas deviner sa
valeur :

```bash
microk8s kubectl get ingressclass
microk8s kubectl get ingress -n cloud-native-flow
microk8s kubectl describe ingress -n cloud-native-flow
```

Si le resultat indique `traefik` ou `public`, reporter cette valeur dans
`gitops/environments/test/values.yaml` avec `ingress.className`, puis laisser
ArgoCD synchroniser le changement. La valeur actuelle `nginx` doit donc etre
confirmee avant la demonstration.

Afficher l'adresse actuelle de la VM :

```bash
hostname -I
```

Dans le fichier hosts Windows ouvert en administrateur, ajouter :

```text
<VM_IP> todo.local
<VM_IP> argocd.local
<VM_IP> grafana.local
<VM_IP> prometheus.local
```

Tester depuis Windows :

```powershell
curl.exe -H "Host: todo.local" http://<VM_IP>/api/categories
```

Puis ouvrir `http://todo.local` dans le navigateur.

Les interfaces d'administration sont accessibles avec les memes regles
Ingress : `http://argocd.local`, `http://grafana.local` et
`http://prometheus.local`.

## GitOps et verification

```bash
microk8s kubectl get application todo-test -n argocd \
  -o jsonpath='{.status.sync.status}{" / "}{.status.health.status}{"\n"}'

microk8s kubectl get deployment -n cloud-native-flow \
  -o custom-columns=NAME:.metadata.name,IMAGE:.spec.template.spec.containers[0].image
```

Le resultat attendu pour ArgoCD est `Synced / Healthy`.

## Acces de secours par port-forward

L'Ingress est l'acces principal. Pour un diagnostic local, utiliser :

```bash
microk8s kubectl port-forward --address 0.0.0.0 \
  -n cloud-native-flow svc/frontend 4200:80
```

Le port-forward est temporaire et doit etre relance apres un redemarrage de
la VM ou l'arret du terminal.

## Diagnostic

```bash
microk8s kubectl get events -n cloud-native-flow \
  --sort-by=.metadata.creationTimestamp
microk8s kubectl logs -n cloud-native-flow deploy/todo-platform-backend
microk8s kubectl logs -n cloud-native-flow deploy/todo-platform-frontend
microk8s kubectl describe pod -n cloud-native-flow <POD_NAME>
```
