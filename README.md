# Meetup-2

[![Build Status](https://cloud.drone.io/api/badges/Cloud-Native-Aalborg/Meetup-2/status.svg)](https://cloud.drone.io/Cloud-Native-Aalborg/Meetup-2)

This document contains the commands used in the "Automated install and observability" talk. 
Feel free to let me know if you have any improvements or catch any typos.



## Steps to setup cluster

Last time we allocated the VMs manually using the cloud UI. This time we will use Terraform to provision the VMs
for us, so that we essentially have our infrastructure-as-code. 

~~~Shell

# Prepare your main.tf terraform file, add token and ssh key

# Run terraform to provision
terraform apply

# Run terraform to clean up
terraform destroy

~~~

This will provision the VMs for us, but also run k3sup to get us running kubernetes, apply our secrets file, 
join the worker nodes and a bit more. 

Nice and simple eh? - The use of terraform here is kept simple on purpose for the demo. 

#### Additional information regarding terraform
If you work with terraform in your project you will probably want to use 
`terraform plan -out <planfile.out>`  and then use `terraform apply <planfile.out>` instead of doing the apply directly. The additional step allows you to see what actions terraform is performing. See more at : https://www.terraform.io/docs/commands/plan.html and in the same way you would probably want to use apply even on the destroy situation, where `terraform plan -out <destroyplanfile.out> -destroy` and inspect that before applying the destroy `terraform apply <destroyplanfile.out>`. There is a lot of things you can do with terraform and lots of information on how to use it. 

### FluxCD

Now that we have some infrastructure, lets get our application and observabilty stack installed. 
How do we do that in a simple automated fashion? Lets give FluxCD a try.

~~~Shell

# Terraform has already installed FluxCD for us, we just need to give it access to read and write to our github repository. 

# Add the following output as a deploy key in github repository
fluxctl identity --k8s-fwd-ns flux

# Wait a couple of minutes and flux will get access and start deploying for us. 

~~~

Now we have an operator that is monitoring docker hub for new container images and github for changes in our yaml files. 
Deployment is just a commit away... 

### Whoa, that was a lot of magic

Lets have a look at the interesting stuff. 

~~~Shell

main.tf
deploy/*.yaml 
 - Flux annotations

~~~

How about we scale our number of pods? 

~~~Shell

nano wisdom-service/deploy/wisdom-service-deployment.yaml
nano wisdom-frontend/deploy/wisdom-frontend-deployment.yaml
#set replicaes to 5

git commit && git push

~~~

### Building the applications

We are using drone.io to build our docker images. It will build every time we push to the master branch. 

Once the images are built, FluxCD will discover them and bump the
version in the deployment yaml files. 




## Observability
What can we do to see how our applications are running? 

- Distributed tracing using Jaeger
- Metrics using Prometheus and Grafana
- Aggregated logs using Loki


### Local storage

Add local storage provisioner, so helm can use local storage for the charts that require persistent storage. 

~~~Shell
kubectl apply -f https://raw.githubusercontent.com/rancher/local-path-provisioner/master/deploy/local-path-storage.yaml
~~~

### Install Helm and Grafana

Prometheus and Jaeger has already been installed by FluxCD in our cluster. We'd also like to run grafana, so lets 
install that using helm.

~~~Shell
helm init

kubectl --namespace kube-system create serviceaccount tiller
kubectl create clusterrolebinding tiller-cluster-rule \
 --clusterrole=cluster-admin --serviceaccount=kube-system:tiller
kubectl --namespace kube-system patch deploy tiller-deploy \
 -p '{"spec":{"template":{"spec":{"serviceAccount":"tiller"}}}}' 

~~~


#### Additional information regarding helm
If you work with helm and do not like having tiller with these credentials in your cluster, you might want to have a look at how you can use helm without tiller, and use `helm template`instead, you can apply this directly as is done above, you may however consider to emit the result and check that into git, that allows you to see changes from one version to another and you have an audit trail. 

~~~Shell
helm repo add loki https://grafana.github.io/loki/charts
helm repo update
helm install loki/loki-stack -n loki --namespace monitoring
helm install stable/grafana -n loki-grafana --namespace monitoring

#expose grafana
export POD_NAME=$(kubectl get pods --namespace monitoring -l "app=grafana,release=loki-grafana" -o jsonpath="{.items[0].metadata.name}")
kubectl --namespace monitoring port-forward $POD_NAME 3000


#add datasource loki
http://loki:3100

#add datasource prometheus
http://prometheus:9090
~~~

### Checking logs with Loki

How do we access our logs? What if we want to see them accross services and look at each pod?

### Grafana dashboards

Lets add some dashboards.

- Number of tweets
- Number of requests

### Distributed tracing with Jaeger

How long are our microservices taking? What part of the chain is the slowest? 

Lets use Jaeger to inspect our traffic. 


#### Tracing jaeger and traefik

What if the problem lies between the load balancer and our application? Lets add opentracing to traefik. 

~~~Shell
#manual edit the configmap
kubectl edit configmap -n kube-system traefik

#add this part
    [tracing]
      servicename = "traefik"
      [tracing.jaeger]
        samplingType = "const"
        samplingParam = 1.0
        propagation = "jaeger"
        localAgentHostPort = "jaeger-agent.monitoring:6831"
        samplingServerURL="http://jaeger-agent.monitoring:5778/sampling"
      [tracing.jaeger.collector]
        endpoint = "http://jaeger-collector.monitoring:14268/api/traces?format=jaeger.thrift"
#end

#delete the pod to reload config
kubectl delete pod -n kube-system $(kubectl -n kube-system get pods -l "app=traefik,release=traefik" -o jsonpath="{.items[0].metadata.name}")
~~~

## Links
Below you find links to the things we've used in this demo:

- https://k3s.io/

- https://github.com/alexellis/k3sup

- https://www.terraform.io/

- https://helm.sh/docs/

- https://fluxcd.io/

- https://prometheus.io/

- https://grafana.com/

- https://www.jaegertracing.io/

- https://docs.traefik.io/master/observability/tracing/jaeger/

- https://quarkus.io/

- https://github.com/Cloud-Native-Aalborg/Meetup-2

- https://cloud.drone.io/Cloud-Native-Aalborg/Meetup-2/

# About
Arne Mejlholm, Java developer at Spar Nord. Driving the adoptation of docker/kubernetes and a lot more 
in the IT development department. Passionated about devops to improve and learn.

- Arne Mejlholm mejlholm@mejlholm.org
- Twitter @mejlholm34263
