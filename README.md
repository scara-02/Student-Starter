# Course Registration Service — DevOps Lab (Question Set D)

## 1. Project Overview

This project is a Java-based Course Registration Service. It demonstrates Git branching and commits, Maven multi-module configuration, JUnit testing, Jenkins CI/CD, and Kubernetes deployment and self-healing.

## 2. Git Workflow

- Feature branch: `feature/course-registration-fixes`
- Changes were developed and committed on the feature branch.
- The feature branch was pushed to the GitHub repository:
  `https://github.com/scara-02/Student-Starter`
- Latest confirmed commit for the Maven refactor and Jenkins pipeline: `edbb6cd` — `Refactor Maven project and add Jenkins pipeline`

### Application changes

- Fixed the course-capacity boundary check so registration is rejected when the course is already full.
- Added tests for accepting a registration when one seat remains and rejecting a registration when the course is full.
- Added case-insensitive duplicate course detection, so codes such as `CS101`, `cs101`, and `Cs101` are treated as duplicates.
- Added tests for lowercase and mixed-case duplicates and for registering a different course.

## 3. Maven Multi-Module Project

The project was refactored into a parent Maven project and a child application module.

```text
Student-Starter/
├── pom.xml
├── Jenkinsfile
├── deployment.yaml
├── service.yaml
├── README.md
└── course-registration-app/
    ├── pom.xml
    └── src/
        ├── main/java/com/university/
        │   ├── Course.java
        │   ├── RegistrationService.java
        │   └── Student.java
        └── test/java/com/university/
            ├── CourseTest.java
            └── RegistrationServiceTest.java
```

### Parent POM

The root `pom.xml`:

- Uses `course-registration-parent` as its artifact ID.
- Uses `pom` packaging.
- Declares `course-registration-app` as a module.
- Sets Java source and target to version 17.
- Manages JUnit Jupiter version `5.11.0`.
- Manages the Maven Compiler Plugin version `3.13.0` and Maven Surefire Plugin version `3.5.0`.

### Child POM

`course-registration-app/pom.xml` inherits from the parent POM. It declares the JUnit dependency without a version because the parent manages that version.

### Build commands

Run these commands from the directory containing the root `pom.xml`:

```powershell
mvn clean test
mvn package
```

### Verified local test results

The following results were observed in the terminal:

- `CourseTest`: 3 tests
- `RegistrationServiceTest`: 11 tests
- Total: 14 tests
- Failures: 0
- Errors: 0
- Skipped: 0
- `mvn clean test`: `BUILD SUCCESS`
- `mvn package`: `BUILD SUCCESS`

The generated JAR is:

```text
course-registration-app/target/course-registration-app-1.0-SNAPSHOT.jar
```

JUnit XML reports are generated under:

```text
course-registration-app/target/surefire-reports/TEST-*.xml
```

## 4. Jenkins Declarative Pipeline

The root `Jenkinsfile` defines these stages:

1. **Checkout** — checks out the configured source-control branch.
2. **Test** — runs `mvn clean test`.
3. **Package** — runs `mvn package` only after the test stage succeeds.
4. **Publish Test Results** — publishes `course-registration-app/target/surefire-reports/TEST-*.xml`.
5. **Archive JAR** — archives `course-registration-app/target/*.jar`.

The pipeline uses Windows `bat` commands and runs Maven from the repository root. The Jenkins job was run successfully.

## 5. Kubernetes with Minikube

A local Kubernetes cluster was started using Minikube and the Docker driver.

### Deployment configuration

The `deployment.yaml` file declares:

- API version: `apps/v1`
- Kind: `Deployment`
- Name: `nginx-deployment`
- Image: `nginx:1.27`
- Desired replicas: `3`
- Container port: `80`
- Pod selector and labels: `app: nginx-deployment`

Apply the deployment:

```powershell
kubectl apply -f deployment.yaml
```

### Service configuration

The `service.yaml` file declares:

- API version: `v1`
- Kind: `Service`
- Name: `nginx-service`
- Type: `NodePort`
- Service port: `80`
- Target port: `80`
- Selector: `app: nginx-deployment`

Apply the service:

```powershell
kubectl apply -f service.yaml
```

### Kubernetes commands used

```powershell
minikube start --driver=docker

kubectl get nodes
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml

kubectl get deployments
kubectl get pods
kubectl get services
kubectl get endpoints nginx-service

kubectl scale deployment nginx-deployment --replicas=3
kubectl get pods

kubectl delete pod <POD_NAME>
kubectl get pods
```

Replace `<POD_NAME>` with the name of an actual pod.

### Verification results

- Minikube node reached the `Ready` state.
- The deployment reached `3/3` ready replicas.
- The `nginx-service` Service was created as a NodePort Service.
- The Service endpoints showed three pod IP addresses on port `80`.
- After one pod was deleted, Kubernetes created a replacement and restored the count to three running pods.
- The Nginx Service can be opened using the URL returned by:

```powershell
minikube service nginx-service --url
```

On Windows with the Docker driver, keep the terminal running if Minikube says the terminal must remain open to maintain the service tunnel.

## 6. Evidence for Lab Submission

Capture and include screenshots showing:

- Git feature branch and commit history.
- Successful `mvn clean test` output showing 14 tests and zero failures.
- Successful `mvn package` output and the generated JAR.
- Jenkins pipeline build showing success.
- `kubectl get deployments` and `kubectl get pods` showing three ready replicas.
- `kubectl get services` showing the NodePort Service.
- `kubectl get endpoints nginx-service` showing the pod IP addresses.
- Pod deletion and the replacement pod appearing afterward.

## 7. Summary

The project demonstrates source control using Git, a Maven parent-and-child multi-module structure, automated JUnit tests, a Jenkins Declarative Pipeline, and Kubernetes deployment, service exposure, scaling, and self-healing using YAML configuration.
