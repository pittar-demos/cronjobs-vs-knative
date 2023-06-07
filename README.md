# restapi

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
mvn compile quarkus:dev
```

## DEMO: CronJob/Serving/Eventing

### CronJob

To run this "job" as a k8s `CronJob`, log into your OCP cluster and run:

```
oc apply -k manifests/cronjob
```

This will create a new `namespace` called "cronjob-test" and deploy the Cronjob.  The CronJob is set to run each minute, so you will have to wait 1min for the first job to run.

Once it does, you should see a request show up here: [webhook.site](https://webhook.site/#!/c832c74a-4418-4393-8915-3cb468562e87/7d4bf37f-e754-44ea-bb3c-b10acc345d22)

If you have OpenShift Logging installed, open Kibana and you can see any logs sent to "Standard Out" and "Standard Error" with the following query:

```
kubernetes.namespace_name:"cronjob-test" AND kubernetes.container_name.raw:"restclient"
```

### Knative Serving

First, follow the OCP docs to install the "OpenShift Serverless" operator in your cluster, then "Knative Serving" in the `knative-serving` namespace that will be created once the operator finishes installing.

Once Knative Serving is fully deployed, you will notice that the Developer UI now has an option to deploy a container image as a "Serverless Deployment".  Very convenient!

Instead of using the UI, we are simply going to create a Knative `Service`.  There's no need to create a `Deployment`, `Service`, and `Route`... the Knative service will do all the hard work for us!

To test this out, run the following command:

```
oc apply -k manifests/knative-serving
```

This will create a new `namespace` called "serving-test".  In there you will find a Knative Serivce spinning up that also includes a routable URL.

Click on the route URL and you will get taken to a very plain page that simply displays the test "Message sent: POST from knative service."

Again, return to [webhook.site](https://webhook.site/#!/c832c74a-4418-4393-8915-3cb468562e87/7d4bf37f-e754-44ea-bb3c-b10acc345d22) and you will see a new request has arrived.  This was sent when the "GET" method of the Quarkus app was hit.  

Since Knative Serving scales based on active requests, once the app has sent the request, your pod count should scale down to zero almost immediately.  If you hit the URL again, it will spin up a new pod to serve the request.

Again, open Kibana and run the following query to see the logs of all requests that have been served:

```
kubernetes.namespace_name:"serving-test" AND kubernetes.container_name.raw:"restclient"
```

### Knative Eventing

With the "OpenShift Serverless" operator already deployed into your cluster, install "Knative Eventing" in the `knative-eventing` namespace that was created once the operator finished installing.

Once Knative Eventing is fully deployed, you know have the ability to create "Event Sources", "Channels", and "Brokers".  Our "Event Sink" for this demo will be a "Knative Service".

To test this out, run the following command:

```
oc apply -k manifests/knative-eventing
```

This will create a new `namespace` called "eventing-test".  In there you will find a Knative Serivce spinning up that also includes a routable URL. You will also see a "Ping Source" that is set to a schedule to run every minute, and it will "POST" to the Knative Serivce we created.  In essence, this is a simplified (and more visible) "CronJob".

The Ping Source will activate after a minute.  Once it does, you will see your pod scale to 1 and a new message will be sent to the webhook site.

Again, return to [webhook.site](https://webhook.site/#!/c832c74a-4418-4393-8915-3cb468562e87/7d4bf37f-e754-44ea-bb3c-b10acc345d22) and you will see a new request has arrived.  This was sent when the "POST" method of the Quarkus app was hit by the Ping Source!

Since Knative Serving scales based on active requests, once the app has sent the request, your pod count should scale down to zero almost immediately.  If you hit the URL again, it will spin up a new pod to serve the request.

Again, open Kibana and run the following query to see the logs of all requests that have been served:

```
kubernetes.namespace_name:"eventing-test" AND kubernetes.container_name.raw:"restclient"
```

## Conclusion

First, Quarkus is awesome :) This Java-based Quarkus test app was compiled to a native binary and built with a minimual UBI image.  The [image is approximately 26MB in size](https://quay.io/repository/pittar/restclient?tab=tags) and the app starts in **0.017s**!!

To compare the methods above:

* `CronJob` is simple and doesn't require any extra operators to be deployed into your cluster.  They're not as "visible" as Knative Services, and perhaps a bit less flexible (they can't take a dynamic payload).
*  `Knative Service` is super simple and very flexible.  Since scaling is based on incoming requests, you have lots of options to "trigger" your Job.  Since the underlying app is a standard (Quarkus) app that exposes a REST API, you can have multiple REST methods (GET/POST, for example), multiple paths, and any combination of parameter or payload that you want.  From a developer's perspective, they are just building a "standard" REST API app.  Nothing fancy to technology or language to learn!
* `Knative Eventing` combine an "Event Source" (such as Ping Source) to kick off your Knative service with a Cron schedule (Ping Source), from an in-memory channel that's collecting CloudEvents, or from events coming from a Kafka Topic.  Again, this is familiar to developers, and scales very well.

Bottom line - if you want "CronJob"-like functionality, take a look at the combo of Knative Serving and Eventing with a Ping Source.  If you already have a way to send a REST API call to trigger a Job, give Knative Serving a try!