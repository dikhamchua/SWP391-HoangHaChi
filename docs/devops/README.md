# DevOps Notes

## Current Build

The project builds as a Maven WAR with final name `kiotretail`.

Primary command:

```bash
mvn clean package
```

Output:

```text
target/kiotretail.war
```

## Runtime

- Expected server: Apache Tomcat 10.1 according to README.
- Context path: `/kiotretail` according to `context.xml`.
- Database: SQL Server on local development environment.

## DevOps Debt

- No CI workflow is currently documented.
- No environment-specific configuration strategy is currently implemented.
- Database credentials are hardcoded and must be externalized before production use.
- Build Java version documentation and Maven compiler configuration should be reconciled.
