//Core app

var express = require('express')
var app = express()

app.get('/', function (req, res) {
  res.send('Hello World!')
})

var server = app.listen(3000, function () {

  console.log('Listening for connections on port 3000');

})
