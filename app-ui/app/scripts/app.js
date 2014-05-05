'use strict';

angular.module('dntApp',['ngRoute', 'ui.bootstrap', 'dntBookingModule', 'dntCommon', 'dntAdminApp']);

angular.module('dntAdminApp',['ngRoute', 'ui.bootstrap', 'dntCommon']);

angular.module('dntCommon',['ngCookies']);

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
		templateUrl: '/views/testView.html'
		
	})
	.when('/orderHistory', {
		
		controller: 'orderController',
		templateUrl: '/views/ordersView.html'
			
	})
	.when('/booking/:id', {
		
		controller: 'bookingController',
		templateUrl: '/views/bookingView.html'
	})
	.when('/login', {
		
		controller: 'authController',
		templateUrl: '/views/authView.html'
	})
	.when('/admin', {
		controller: 'adminViewController',
		templateUrl: '/views/admin/adminView.html'
	})
	.when('/admin/cabin/:id', {
		controller: 'cabinDetailsController',
		templateUrl: '/views/admin/cabinDetails.html'
	})
	.when('/termsAndConditions', {
		controller: '',
		templateUrl: '/views/termsAndConditions.html'
	})		
	.when('/bookingManual', {
		controller: '',
		templateUrl: '/views/bookingManual.html'
			
	})
	.otherwise({ redirectTo: '/'});
	
	
	
}]);

angular.module('dntApp').run(function (api) {
	api.init();
});