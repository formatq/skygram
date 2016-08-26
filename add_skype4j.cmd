:: https://ci.samczsun.com/job/Skype4J/lastSuccessfulBuild/artifact/target/skype4j-0.2.0-SNAPSHOT.jar
:: https://ci.samczsun.com/job/Skype4J/lastSuccessfulBuild/artifact/target/skype4j-0.2.0-SNAPSHOT-sources.jar
:: https://ci.samczsun.com/job/Skype4J/lastSuccessfulBuild/artifact/target/skype4j-0.2.0-SNAPSHOT-javadoc.jar


mvn install:install-file -Djavadoc=e:\aivanov\Downloads\skype4j-0.2.0-SNAPSHOT-sources.jar -Dsources=e:\aivanov\Downloads\skype4j-0.2.0-SNAPSHOT-javadoc.jar -Dfile=e:\aivanov\Downloads\skype4j-0.2.0-SNAPSHOT.jar -DgroupId=com.samczsun -DartifactId=skype4j -Dversion=0.2.0-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
