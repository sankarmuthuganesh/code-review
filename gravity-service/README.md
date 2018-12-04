This is iv-gravity-service repository.

This repo contains the all of Gravity Business Logics for searching in GIT, reviewing codes in GIT (storing its reports in 
postgres database). This repo is more of IVF-Independent.

Extra Notes and Indepth View:
------------------------------

This is very indenpendent Service. It is not depending on Infoview Framework for its funtionalities. 
In future if modifications are to be made as per Infoview Framework, the source code can be modified by slight modifications of current 
code.

Works Pending:
---------------
1. Sonarqube account creation for 209 with credentials gravity/gravity9.
2. Bug fixing logic testing. (It works but needs to check whether it is working perfect.)

Highlights Of Gravity Service:
------------------------------
1. Proper Usage of Spring Conventions and Framework.
2. Very Flexible logics for codereview, searching in GIT.
3. For DB Connections  - JDBCTemplate is used directly. And you may not worry about Conncection closing issues or leakages. Because it is 
handled by HikariCp Connection pooling. Any to make a point, for connection to different schemas a new connection is not made.
Only one connection pool is created and for connecting to different schema a Wrapper class For Datasource is made and it changes schema on 
returning a connection. So only one connection pool is used. And very fast as far as I understand.

To be very highlighting of this service, other than bug finding it also contains some bug fixing logics and in future it has a greater scope
and very dynamic in the future -- ie., to add a bug finding logic or to add a bug fixing logic just pasting the logic codes packed as jar
in a folder will do the job. Need not alter the source code to add bug logics on Gravity.

For Git related api calls , ie., GitlabApi, jgit, this repo will be a good reference I believe.


I have created a branch named gravity-steps-further, you can clone and improve it further.

!!!!!!Happy Developing!!!!!!