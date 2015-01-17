//Core app

var express = require('express')
var app = express()

//Routes
app.get('/', function (req, res) {
  res.send('Hello World!')
})

app.use(express.static(__dirname + '/public'));

var server = app.listen(3000, function () {

  console.log('Listening for connections on port 3000');

})
