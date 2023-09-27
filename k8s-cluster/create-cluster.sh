#!/bin/bash

CLUSTER_NAME=$1

kind create cluster --name $CLUSTER_NAME --config ./cluster.config
kubectx kind-$CLUSTER_NAME

## load balancer
kubectl apply -f https://raw.githubusercontent.com/metallb/metallb/v0.13.7/config/manifests/metallb-native.yaml

TOTAL_PODS_LB=$(kubectl get pods --namespace metallb-system -l component=controller -o json | jq -r ".items | length")
while [ $TOTAL_PODS_LB -eq 0 ]
do
    TOTAL_PODS_LB=$(kubectl get pods --namespace metallb-system -l component=controller -o json | jq -r ".items | length")
done

kubectl wait --namespace metallb-system --for=condition=ready pod --selector=component=controller --timeout=300s

NETWORK_ADDRESS_CLUSTER=$(docker network inspect kind | jq -r '.[0].IPAM.Config[0].Gateway')
IP_COMPOSITION=""
IFS='.' read -ra ADDR <<< "$NETWORK_ADDRESS_CLUSTER"
count=0
for i in "${ADDR[@]}"; do
    if [[ $count -eq 0 ]]
    then
        IP_COMPOSITION="$i"
    else
        IP_COMPOSITION="$IP_COMPOSITION.$i"
    fi
    
    if [[ $count -eq 1 ]]
    then
        break
    fi

    count=$((count + 1))
done

## create metallb file
LB_FILE_NAME="metallb.yml"
rm -rf ./$LB_FILE_NAME
cat > ./$LB_FILE_NAME <<EOF
apiVersion: metallb.io/v1beta1
kind: IPAddressPool
metadata:
  name: lb
  namespace: metallb-system
spec:
  addresses:
  - ${IP_COMPOSITION}.255.200-${IP_COMPOSITION}.255.250
---
apiVersion: metallb.io/v1beta1
kind: L2Advertisement
metadata:
  name: empty
  namespace: metallb-system
EOF

kubectl apply -f ./$LB_FILE_NAME

## create ngingx
kubectl apply --filename https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/static/provider/kind/deploy.yaml
kubectl wait --namespace ingress-nginx --for=condition=ready pod --selector=app.kubernetes.io/component=controller --timeout=300s

## metric-server
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/download/v0.5.0/components.yaml
kubectl patch deployment metrics-server -n kube-system --patch "$(cat ./metric-server-patch.yml)"