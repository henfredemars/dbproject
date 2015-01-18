//Import everything we need
module.exports = function(grunt) {
require('matchdep').filterDev('grunt-*').forEach(grunt.loadNpmTasks);

grunt.initConfig({

//Express.js config
express: {
    all: {
        options: {
            bases: [__dirname + '/public'],
            port: 3000,
            hostname: "0.0.0.0",
            livereload: true
        }
    }
},

//jslint
jslint: {
  server: {
    src: ['*.js'],
    directives: {
      node: true,
      todo: true
    },
    options: {
      edition: 'latest'
    }
  }
},

//Keep an eye out for changes
watch: {
    all: {
            files: '**/*.html',
            options: {
              livereload: true
        }
    }
},

open: {
    all: {
        path: 'http://localhost:3000/index.html'
    }
}
});

grunt.registerTask('test', [
    'express',
    'open',
    'watch',
    'jslint'
    ]);
};
