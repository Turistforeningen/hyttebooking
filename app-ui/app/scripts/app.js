'use strict';

angular.module('dntApp',['ngRoute', 'ui.bootstrap', 'ngCookies', 'ngResource', 'ngSanitize', 'dntBookingModule']);

/*
 * Routes, routeinterceptor done below. Httpinterceptor, redirect to /login
 * when a request returns unauthorized.
 * 
 */
angular.module('dntApp').config(['$routeProvider', '$locationProvider', '$httpProvider', function ($routeProvider, $locationProvider, $httpProvider) {
	
	$httpProvider.responseInterceptors.push('httpInterceptor');
	
	$routeProvider
	.when('/', {
		controller: 'testController',
		templateUrl: '/assets/views/testView.html'
		
	})
	.when('/orderHistory', {
		
		controller: 'orderController',
		templateUrl: '/assets/views/ordersView.html'
			
	})
	.when('/booking/:id', {
		
		controller: 'bookingController',
		templateUrl: '/assets/views/bookingView.html'
	})
	.when('/login', {
		
		controller: 'authController',
		templateUrl: '/assets/views/authView.html'
	})
	.when('/admin', {
		controller: 'adminViewController',
		templateUrl: '/assets/views/adminView.html'
	})
	.when('/admin/cabin/:id', {
		controller: 'adminViewController',
		templateUrl: '/assets/views/adminView.html'
	})
	.when('/calendar', {
		controller: 'bookingController',
		templateUrl: '/assets/views/calendar.html'
	})
	.when('/termsAndConditions', {
		controller: '',
		templateUrl: '/assets/views/termsAndConditions.html'
	})
	.otherwise({ redirectTo: '/'});
	
	
	
}]);

angular.module('dntApp').run(function (api) {
	api.init();
});