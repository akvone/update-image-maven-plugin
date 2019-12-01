# Update image Maven plugin

## How to use 
### With maven
#### What you really need:
* Type: `mvn clean package -DskipTests com.github.akvone:update-image-maven-plugin:update` to make full update

#### May be useful also 
* Type: `mvn clean compile jar:jar org.springframework.boot:spring-boot-maven-plugin:repackage com.github.akvone:update-image-maven-plugin:update` 
  to make full update (fastest command, but works only with Spring Boot projects)
* Type: `mvn com.github.akvone:update-image-maven-plugin:update` to make partial update (without generating new jar)
* Type: `mvn com.github.akvone:update-image-maven-plugin:build-push` to make build and push (without generating new jar)

### With Intellij IDEA
1. Add Configuration
2. Create Maven Run Configuration:
3. Command line:
  `-N com.github.akvone:update-image-maven-plugin:update`
4. Before launch 
  `clean package -DskipTests`
  
![EXAMPLE](./documentation/images/How%20to.%20IntelliJ%20IDEA.png) 


## Configuration
Add required properties 
* in `{project root}/../config.yaml` (most preferable way)
* or in `{project root}/gitignore/config.yaml` (add `{project root}/gitignore` to `.gitignore` file then)

[See default config](./src/main/resources/config/default.yaml)
```
docker:
  host: "tcp://127.0.0.1:2375"
  username: "***"
  authorization: "***"
artifactory:
  url: "***"
  repository: "***"
cloudProvider:
  type: "openshift"
  url: "***"
  namespace: "***"
  authorizationToken: "***" # get it from openshift (Bearer token)
```

Note that all these values (even "***") are plugin defaults.

## Prerequisites
### Windows
* Install docker desktop
* Run docker and enable 'Docker without TLS' (`Docker` -> `Settings` -> `General` -> `Expose daemon on tcp://localhost:2375 without tls`)

### All platforms
* Add configuration file as described in *Configuration* section