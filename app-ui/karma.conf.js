// Karma configuration
// http://karma-runner.github.io/0.10/config/configuration-file.html

module.exports = function(config) {
  config.set({
    // base path, that will be used to resolve files and exclude
    basePath: '',

    // testing framework to use (jasmine/mocha/qunit/...)
    frameworks: ['jasmine'],
    
    // list of files / patterns to load in the browser
    files: [
      'app/bower_components/angular/angular.js',
      'app/bower_components/jquery/dist/jquery.js',
      'bower_components/bootstrap/dist/js/bootstrap.min.js',
      "app/scripts/ui-bootstrap-tpls.js",
      'app/bower_components/angular-mocks/angular-mocks.js',
      'app/bower_components/angular-resource/angular-resource.js',
      'app/bower_components/angular-cookies/angular-cookies.js',
      'app/bower_components/angular-sanitize/angular-sanitize.js',
      'app/bower_components/angular-route/angular-route.js',
      'app/scripts/*.js',
      'app/scripts/**/*.js',
      'test/mock/**/*.js',
      'test/spec/**/*.js',
      'app/views/**/*.html'
    ],

    // list of files / patterns to exclude
    exclude: [],

    // web server port
    port: 8080,

    // level of logging
    // possible values: LOG_DISABLE || LOG_ERROR || LOG_WARN || LOG_INFO || LOG_DEBUG
    logLevel: config.LOG_INFO,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: false,
    
    preprocessors: {
    	'app/scripts/**/*.js': ['coverage'],
        'app/views/**/*.html': ['ng-html2js']
      },
      
    
      ngHtml2JsPreprocessor: {
          // strip this from the file path
          stripPrefix: 'app/',
          // prepend this to the
          prependPrefix: '',

          moduleName: 'templates'
          },
    
    // Start these browsers, currently available:
    // - Chrome
    // - ChromeCanary
    // - Firefox
    // - Opera
    // - Safari (only Mac)
    // - PhantomJS
    // - IE (only Windows)
    //browsers: ['Chrome','Firefox', 'IE'],
    browsers: ['Chrome'],

    // Continuous Integration mode
    // if true, it capture browsers, run tests and exit
    singleRun: false,
	 // here we specify which of the files we want to appear in the coverage report
	   
	    // add the coverage plugin
	    plugins: [ 'karma-jasmine', 'karma-ng-html2js-preprocessor', 'karma-firefox-launcher', 'karma-chrome-launcher', 'karma-coverage' ],
	    // add coverage to reporters
	    reporters: ['dots', 'coverage'],
	    // tell karma how you want the coverage results
	    coverageReporter: {
	      type : 'html',
	      // where to store the report
	      dir : 'coverage/'
    }
  });
  
};
