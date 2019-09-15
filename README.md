# Meetup-2

[![Build Status](https://cloud.drone.io/api/badges/Cloud-Native-Aalborg/Meetup-1/status.svg)](https://cloud.drone.io/Cloud-Native-Aalborg/Meetup-1)

This document contains the commands used in the kubernetes in 10 minutes presentation.
Feel free to let me know if you have improvments or catch any typos.



## Steps to setup cluster

Allocate 3 VMs on your favorite cloud hosting provider. 

Install k3sup

~~~Shell
curl -sLS https://get.k3sup.dev | sh
sudo install k3sup /usr/local/bin/
~~~

Use k3sup to install k3s on master node

~~~Shell
export IP=xxx.yyy.zzz.199
k3sup install --ip $IP
mv kubeconfig ~/.kube/config

kubectl get nodes
NAME                STATUS   ROLES    AGE     VERSION
debian-2gb-nbg1-1   Ready    worker   4m47s   v1.14.6-k3s.1
~~~

Use k3sup to join work nodes

~~~Shell
k3sup join --ip xxx.yyy.zzz.200 --server-ip $IP
k3sup join --ip xxx.yyy.zzz.201 --server-ip $IP
~~~

Check that the nodes are joining

~~~Shell
kubectl get nodes
NAME                STATUS   ROLES    AGE     VERSION
debian-2gb-nbg1-1   Ready    worker   4m47s   v1.14.6-k3s.1
debian-2gb-nbg1-2   Ready    worker   5m29s   v1.14.6-k3s.1
debian-2gb-nbg1-3   Ready    master   8m25s   v1.14.6-k3s.1
~~~

Check out pods (none yet)

~~~Shell
kubectl get pods
No resources found.
~~~


## Steps to build and deploy applications

Lets build our applications. This is automated, so not much to do here.

If you are interested in the applications things to noice are: 

 - java applications build on quarkus with graalvm for startup times and memory usage.
 - multi-stage docker builds, so no need for local java tools in the builds. 
 - secret is not part of repository. Copy secret.env.example to secret.env and insert your twitter account details. 
 - static yaml files in deploy documents our deployments
 - a git post-update hook updates the image tag version in the deployment yaml files
 - drone builds the images
 - flux monitors and deploy them

~~~Shell
kubectl apply -f namespaces

kubectl config set-context --current --namespace=apps
kubectl create secret generic wisdom-service-secret --from-env-file=secret.env

kubectl apply -f wisdom-service/deploy
kubectl apply -f wisdom-frontend/deploy

kubectl get pods
NAME                              READY   STATUS    RESTARTS   AGE
wisdom-frontend-6c86667dd-pmnsw   1/1     Running   0          2m59s
wisdom-frontend-6c86667dd-thjlc   1/1     Running   0          3m5s
wisdom-service-6c748d8748-5d87t   1/1     Running   0          3m6s
wisdom-service-6c748d8748-v5r9k   1/1     Running   0          2m58s
~~~

Add wisdom.mejlholm.org to /etc/hosts (or setup real dns)

~~~Shell
sudo nano /etc/hosts #add the following line
xxx.yyy.zzz.199  wisdom.mejlholm.org
~~~

Lets test our application

~~~Shell
curl http://wisdom.mejlholm.org/wisdom/random
{"message":"\"Debuggers don't remove bugs. They only show them in slow motion.\" - Unknown"}
~~~

And check it out in the browser:
http://wisdom.mejlholm.org/



## If you don't like the commandline that much

Sometimes you get a better overview with a ui - kubernetes web ui comes to the rescue.

~~~Shell
kubectl apply -f kubernetes-web-ui.yaml
kubectl proxy
kubectl -n kube-system describe secret $(kubectl -n kube-system get secret | grep admin-user | awk '{print $1}')
kubectl delete -f kubernetes-web-ui.yaml
~~~


## Bonus: Setup FluxCD as GitOps deployment operator

As a bonus we'll use the FluxCD operator to handle our deployments. What we commit to git will be deployed automatically for us. 

~~~Shell
export FLUX_FORWARD_NAMESPACE=flux

kubectl create ns flux
fluxctl install \
--git-user=mejlholm \
--git-email=mejlholm@users.noreply.github.com \
--git-url=git@github.com:Cloud-Native-Aalborg/Meetup-1 \
--git-paths=namespaces,wisdom-frontend/deploy,wisdom-service/deploy \
--namespace=flux | kubectl apply -f -

kubectl patch deployments -n flux flux --type='json' -p='[{"op": "add", "path": "/spec/template/spec/containers/0/args/-", "value": "--git-ci-skip"}]'
fluxctl identity --k8s-fwd-ns flux
# Manually add key to Deploy keys in GitHub settings. 

~~~

## Closing remarks
K3s is not HA ready yet - but it makes a great little tool for testing kubernetes (and it runs a raspberry pi).
K3s has an open port for the api by default, please check if running anything in the public cloud. 


## Links
Below you find links to the things we've used in this demo:

- https://k3s.io/

- https://github.com/alexellis/k3sup

- https://quarkus.io/

- https://github.com/Cloud-Native-Aalborg/Meetup-1

- https://kubernetes.io/docs/tasks/access-application-cluster/web-ui-dashboard/

- https://fluxcd.io/

- https://cloud.drone.io/Cloud-Native-Aalborg/Meetup-1/

# About
Arne Mejlholm, Java developer at Spar Nord. Driving the adoptation of docker/kubernetes and a lot more 
in the IT development department. Passionated about devops to improve and learn.

- Arne Mejlholm mejlholm@mejlholm.org
- Twitter @mejlholm34263
