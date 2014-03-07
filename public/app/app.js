var app = angular.module('dntApp' ,['ngRoute', 'ui.bootstrap', 'ngCookies', 'ngResource', 'ngSanitize', 'ngBootstrap']);

/*
 * Routes, routeinterceptor done below. Httpinterceptor, redirect to /login
 * when a request returns unauthorized.
 * 
 */
app.config(function ($routeProvider, $locationProvider, $httpProvider) {
	
	$httpProvider.responseInterceptors.push('httpInterceptor');
	
	$routeProvider
	.when('/', {
		controller: 'testController',
		templateUrl: '/assets/app/partials/testView.html'
		
	})
	.when('/orderHistory', {
		
		controller: 'orderController',
		templateUrl: '/assets/app/partials/ordersView.html'
			
	})
	.when('/booking', {
		
		controller: 'bookingController',
		templateUrl: '/assets/app/partials/bookingView.html'
	})
	.when('/login', {
		
		controller: 'authController',
		templateUrl: '/assets/app/partials/authView.html'
	})
	
	.otherwise({ redirectTo: '/'});
	
	
	
});

app.run(function (api) {
	  api.init();
});