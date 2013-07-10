 

 javac @sources.txt  -cp ./libs/joda-time-2.2.jar:./libs/postgresql-9.2-1002.jdbc4.jar:./libs/diewald_shapeFileReader.jar





 java -cp ./libs/joda-time-2.2.jar:./libs/postgresql-9.2-1002.jdbc4.jar:./libs/diewald_shapeFileReader.jar:./src EntryPoint localhost argus3 5432 rails rails data

 	or

 java -cp ./LocationMapperV3/libs/joda-time-2.2.jar:./LocationMapperV3/libs/postgresql-9.2-1002.jdbc4.jar:./LocationMapperV3/libs/diewald_shapeFileReader.jar:./LocationMapperV3/src EntryPoint localhost argus3 5432 rails rails LocationMapperV3/data