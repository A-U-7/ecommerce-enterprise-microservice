# Kubernetes Manifests

Basic deployment and service YAMLs for selected modules.  Before applying you must build/push Docker images to a registry (`ecommerce/<module>:latest`).

Example apply:

```bash
kubectl apply -f k8s/service-registry-deployment.yml
kubectl apply -f k8s/config-server-deployment.yml
kubectl apply -f k8s/product-service-deployment.yml
kubectl apply -f k8s/order-service-deployment.yml
# etc...
```

Services communicate via Eureka DNS names (`product-service`, `order-service`, ...).
