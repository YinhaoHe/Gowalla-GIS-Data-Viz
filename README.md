# Gowalla Graph Data Visualization

Please download Graph-Viz-Gowalla
The project is using the [Play! Framework](https://www.playframework.com/) 

## Build

Prerequisites: scala, sbt, [mysql](https://dev.mysql.com/downloads/mysql/)

### Prepare the database
- start mysql server
- download and unzip the dataset from [Stanford's snap](https://snap.stanford.edu/data/loc-gowalla.html)
- place the data files in graph-viz-gowalla-master/data
```
> cd graph-viz-gowalla-master/data
> mysql -u root -p < setup_mysql.sql
```

### To run the project
```
> cd graph-viz-gowalla-master
> sbt run
```

You should see the demo on [http://localhost:9000](http://localhost:9000)
