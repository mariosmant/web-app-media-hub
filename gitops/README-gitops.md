# RKE2 and Rancher Installation in WSL2

## Install RKE2

```bash
cd ~
sudo mkdir /etc/rancher
sudo mkdir /etc/rancher/rke2
```

Create a config.yaml

```bash
sudo nano /etc/rancher/rke2/config.yaml
```

And paste

```bash
cni: calico

cni_env:
  - name: CALICO_IPV4POOL_IPIP
    value: "Always"
  - name: IP_AUTODETECTION_METHOD
    value: "cidr=192.168.1.0/24"   # pick your LAN’s CIDR so Calico ignores it
  - name: CALICO_DISABLE_FILE_LOGGING
    value: "true
```

Then `Ctrl+O` to save and `Ctrl+X` to close `nano`.

Continue to install RKE2 as server.

```bash
curl -sfL https://get.rke2.io | sudo INSTALL_RKE2_TYPE=server sh -

sudo systemctl enable rke2-server

sudo systemctl start rke2-server
```

## Install Kubectl

```bash
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"

curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl.sha256"
```

The following should output `OK`

```bash
echo "$(cat kubectl.sha256)  kubectl" | sha256sum --check
```

Continue installation.

```bash
chmod +x kubectl

mkdir -p ~/.local/bin
mv ./kubectl ~/.local/bin/kubectl

mkdir -p ~/.kube
sudo cp /etc/rancher/rke2/rke2.yaml ~/.kube/config
sudo chown $(id -u):$(id -g) ~/.kube/config
chmod 600 ~/.kube/config

rm ~/kubectl.sha256

nano ~/.bashrc
```

Add the following lines at the bottom of the file

```bash
export PATH="$HOME/.local/bin:$PATH"

export KUBECONFIG=$HOME/.kube/config
```

Type `Ctrl+O` to save and then `Ctrl+X` to close `nano`.

## Install Helm

```bash
curl https://baltocdn.com/helm/signing.asc | gpg --dearmor | sudo tee /usr/share/keyrings/helm.gpg > /dev/null

sudo apt-get install apt-transport-https --yes

echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/helm.gpg] https://baltocdn.com/helm/stable/debian/ all main" | sudo tee /etc/apt/sources.list.d/helm-stable-debian.list

sudo apt-get update

sudo apt-get install helm
```

## Install jq

```bash
sudo apt install jq
```

## Install cert-manager.crds and cert-manager

```bash
kubectl create namespace cert-manager

kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.18.2/cert-manager.crds.yaml


helm repo add jetstack https://charts.jetstack.io

helm repo update

helm install cert-manager jetstack/cert-manager --namespace cert-manager --version v1.18.2 --set installCRDs=false --wait
```

## Install Rancher

```bash
helm repo add rancher-latest https://releases.rancher.com/server-charts/latest

helm repo update

kubectl create namespace cattle-system

helm install rancher rancher-latest/rancher --namespace cattle-system --set hostname=rancher.local --set replicas=1
```

## Keycloak (local)

```sql
-- Connect to PostgreSQL as the postgres superuser
psql -U postgres

-- 1. Create the database
CREATE DATABASE keycloak_db;

-- 2. Create the user with a strong password
CREATE USER keycloak_user WITH PASSWORD 'StrongP@ssw0rd';

-- 3. Grant all privileges on the database to the user
GRANT ALL PRIVILEGES ON DATABASE keycloak_db TO keycloak_user;

-- 4. (Optional) Ensure the user can manage schema objects
\c keycloak_db
GRANT ALL PRIVILEGES ON SCHEMA public TO keycloak_user;
```


## Admin DB (local)
```sql
-- Connect to PostgreSQL as the postgres superuser
psql -U postgres

-- 1. Create the database
CREATE DATABASE admin_db;

-- 2. Create the user with a strong password
CREATE USER mediahub_admin WITH PASSWORD 'StrongP@ssw0rd';

-- 3. Grant all privileges on the database to the user
GRANT ALL PRIVILEGES ON DATABASE admin_db TO mediahub_admin;

-- 4. Switch to the new database
\c admin_db

-- 5. Grant privileges on the public schema
GRANT ALL PRIVILEGES ON SCHEMA public TO mediahub_admin;
```
