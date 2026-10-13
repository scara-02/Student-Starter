# Course Registration Service – DevOps Lab (Question Set D)

## About the Project

This project is a simple Java course registration application. I used it to practise the main tools covered in the DevOps lab: Git, Maven, JUnit, Jenkins, and Kubernetes.

The work includes fixing registration issues, adding tests for those fixes, organizing the Maven project into modules, setting up a Jenkins pipeline, and deploying Nginx on a local Kubernetes cluster.

## 1. Git Workflow

I worked on the feature branch `feature/course-registration-fixes` and pushed the changes to GitHub:

Repository: https://github.com/scara-02/Student-Starter

The main changes were committed separately so the work is easier to follow. The latest confirmed commits include the Maven/Jenkins refactor and the Kubernetes configuration.

### Changes to the registration logic

I made two main fixes in the application:

- **Course capacity:** A student can register if a seat is still available. If the course is already full, the registration is rejected.
- **Duplicate course codes:** Course codes are checked without considering letter case. For example, `CS101`, `cs101`, and `Cs101` are treated as the same course.

I also added tests to check the capacity boundary, duplicate registrations with different letter cases, and registration for a different course. The tests help check that a rejected registration does not incorrectly change the student's registered courses, credits, or the course's enrollment count.

## 2. Maven Project Structure

I reorganized the project into a parent Maven project and a separate application module. This keeps the shared build settings in one place and the application code in its own module.


Student-Starter/
    pom.xml
    Jenkinsfile
    deployment.yaml
    service.yaml
    README.md
    course-registration-app/
        pom.xml
        src/
            main/java/com/university/
                Course.java
                RegistrationService.java
                Student.java
            test/java/com/university/
                CourseTest.java
                RegistrationServiceTest.java

### What the POM files do

 `pom.xml`

The root POM acts as the parent project. It uses `pom` packaging and lists `course-registration-app` as its module. It also keeps the shared Java and build-tool settings together:

- Java source and target version: `17`
- JUnit Jupiter version: `5.11.0`
- Maven Compiler Plugin: `3.13.0`
- Maven Surefire Plugin: `3.5.0`

`course-registration-app/pom.xml`

The child POM inherits the settings from the parent and contains the application module's dependencies. It does not need to specify a JUnit version because that version is managed by the parent POM.

### Build and test

Open a terminal in the folder containing the root `pom.xml`, then run:

```powershell
mvn clean test
mvn package
```

Both commands completed successfully during testing.

The test results were:

| Test class | Tests |
|---|---:|
| `CourseTest` | 3 |
| `RegistrationServiceTest` | 11 |
| **Total** | **14** |

There were **0 failures, 0 errors, and 0 skipped tests**.

After packaging, the JAR was created at:

```text
course-registration-app/target/course-registration-app-1.0-SNAPSHOT.jar
```

The JUnit XML reports are saved under:

```text
course-registration-app/target/surefire-reports/TEST-*.xml
```

## 3. Jenkins Pipeline

I added a `Jenkinsfile` in the project root to automate the build process. The pipeline has these stages:

1. **Checkout** – gets the code from the configured Git branch.
2. **Test** – runs `mvn clean test`.
3. **Package** – runs `mvn package` after the test stage passes.
4. **Publish Test Results** – reads the JUnit XML reports from the application module.
5. **Archive JAR** – saves the generated JAR as a Jenkins build artifact.

The pipeline uses Windows `bat` commands and runs Maven from the repository root. The Jenkins job completed successfully.

## 4. Kubernetes Deployment with Minikube

For the Kubernetes part, I started a local cluster using Minikube with the Docker driver and deployed Nginx.

### Deployment: `deployment.yaml`

The deployment uses the image `nginx:1.27` and is configured to keep **three replicas** running. The pods use the label `app: nginx-deployment`, and the Nginx container listens on port `80`.

Apply the file with:

```powershell
kubectl apply -f deployment.yaml
```

### Service: `service.yaml`

The service is named `nginx-service`. It uses the `NodePort` type and forwards traffic to port `80` on the Nginx pods.

Apply it with:

```powershell
kubectl apply -f service.yaml
```

### Commands used

Run these commands from the project folder:

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

Replace `<POD_NAME>` with the name of one of the actual pods shown by `kubectl get pods`.

To get a URL for the Nginx service, run:

```powershell
minikube service nginx-service --url
```

If Minikube asks you to keep the terminal open for the service tunnel, leave that terminal running while you test the URL.

### What I verified

- The Minikube node reached the `Ready` state.
- The deployment showed `3/3` ready replicas.
- The `nginx-service` NodePort service was created.
- The service had three pod endpoints on port `80`.
- When I deleted a pod, Kubernetes created a replacement and brought the deployment back to three running pods.

This shows the basic deployment, service, scaling, and self-healing behaviour of Kubernetes.

## 5. Screenshots for Submission

For the lab record, take screenshots of the actual output for these steps:

- Git branch and commit history.
- `mvn clean test` showing 14 tests with no failures.
- `mvn package` and the generated JAR file.
- Jenkins pipeline with a successful build.
- `kubectl get deployments` and `kubectl get pods` showing three ready replicas.
- `kubectl get services` showing `nginx-service` as a NodePort service.
- `kubectl get endpoints nginx-service` showing the pod endpoints.
- Deleting a pod and then showing the replacement pod.

Use your own terminal and Jenkins screenshots as evidence rather than adding output that was not actually produced.

## Conclusion

This lab brought together the main steps in a small DevOps workflow. I used Git to manage the changes, Maven and JUnit to build and test the Java application, Jenkins to automate the build, and Kubernetes to run and expose Nginx. I also checked how Kubernetes replaces a pod when one is deleted.
