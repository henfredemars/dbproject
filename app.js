//Core app

var express = require('express');
var path = require('path');
var app = express();

//Routes
app.get('/', function (req, res) {
  res.send('Hello World! This is an expressjs route!');
});

app.use(express.static(path.join(__dirname, 'public')));

app.use(express.static(__dirname + '/public'));

var server = app.listen(3000, function () {

  console.log('Listening for connections on port 3000');

});
