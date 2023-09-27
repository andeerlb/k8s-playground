## configuration on cluster accept ingress rule in file cluster.config
kind create cluster --name cluster-name --config ./cluster.config

## install loadbalancer on cluster
## more informations https://kind.sigs.k8s.io/docs/user/loadbalancer/
kubectl apply -f https://raw.githubusercontent.com/metallb/metallb/v0.13.7/config/manifests/metallb-native.yaml
## view pool of address to configure on loadbalancer
docker network inspect -f '{{.IPAM.Config}}' kind

## load local docker image to cluster
kind load docker-image image:tag --name cluster-name

## nginx-ingress
kubectl apply --filename https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/static/provider/kind/deploy.yaml
kubectl wait --namespace ingress-nginx --for=condition=ready pod --selector=app.kubernetes.io/component=controller --timeout=90s

## metric-server
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/download/v0.5.0/components.yaml
kubectl patch deployment metrics-server -n kube-system --patch "$(cat metric-server-patch.yml)"