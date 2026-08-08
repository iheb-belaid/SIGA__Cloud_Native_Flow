# CI/CD et DevSecOps

## Ordre des workflows

```text
CI
  |
  v
Quality And Security
  |
  v
Release And GitOps
```

Les deux dependances sont implementees avec `workflow_run`. Un workflow
suivant ne doit continuer que si le workflow precedent termine avec le statut
`success`.

## CI

Fichier : `.github/workflows/ci.yml`.

Le job `Validate Backend And Frontend` :

- recupere le commit avec `actions/checkout`;
- installe Java 21;
- execute `./mvnw test` dans `backend`;
- installe Node.js 22;
- execute `npm ci` dans `frontend`;
- execute les tests Angular;
- construit le frontend Angular;
- publie l'artefact `frontend-dist` pour le diagnostic.

Le workflow ignore les changements limites a `gitops/**`, car ces changements
sont produits automatiquement par la release et ne doivent pas relancer une
nouvelle construction.

## Quality And Security

Fichier : `.github/workflows/quality-security.yml`.

Ce workflow produit :

- la couverture backend JaCoCo;
- la couverture frontend LCOV;
- une analyse SonarQube du backend et du frontend;
- des rapports OWASP Dependency-Check;
- des artefacts GitHub Actions telechargeables.

Les secrets suivants sont definis dans les settings du repository :

- `SONAR_HOST_URL` : URL du serveur SonarQube accessible depuis la VM;
- `SONAR_TOKEN` : jeton d'analyse SonarQube, jamais stocke dans Git.

## Release And GitOps

Fichier : `.github/workflows/release-gitops.yml`.

Le workflow :

1. construit les images backend et frontend;
2. les scanne avec Trivy sur les vulnerabilites HIGH/CRITICAL;
3. les pousse dans GHCR avec un tag `sha-<shortsha>`;
4. met a jour `gitops/environments/test/image-tags.yaml`;
5. committe cette mise a jour avec le compte automatique GitHub Actions.

Le tag SHA est immuable et permet de relier un deploiement a un commit precis.

## Runner self-hosted

Le runner est une VM Ubuntu dans VMware. Il est utilise parce qu'il peut
atteindre simultanement Docker, MicroK8s et les outils locaux comme SonarQube.

Verification sur la VM :

```bash
sudo systemctl status actions.runner.*
cd ~/actions-runner
pgrep -af 'Runner.Listener|Runner.Worker|runsvc'
```

Le runner doit etre `Idle` dans `Settings > Actions > Runners` avant de lancer
une demonstration.

## GHCR

Le workflow utilise `GITHUB_TOKEN` avec les permissions `packages: write`.
Aucun token GHCR manuel n'est necessaire dans ce repository si les permissions
du workflow et du repository l'autorisent.

## Diagnostic CI/CD

```bash
git status
git log -1 --oneline
```

Si le runner n'execute pas un job, verifier l'etat reseau de la VM, le service
systemd du runner et sa presence dans GitHub. Si un commit automatique GitOps
est apparu, synchroniser le poste local avant une nouvelle modification :

```powershell
git pull --rebase origin main
```
