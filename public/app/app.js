var app = angular.module('dntApp' ,['ngRoute']);

app.config(function ($routeProvider, $locationProvider) {
	$routeProvider
	.when('/', {
		controller: 'testController',
		templateUrl: '/assets/app/partials/testView.html'
		
	})
	.when('/orderHistory/:customerID', {
		
		controller: 'orderController',
		templateUrl: '/assets/app/partials/ordersView.html'
			
	})
	.when('/booking', {
		
		controller: 'bookingController',
		templateUrl: '/assets/app/partials/bookingView.html'
	})
	.otherwise({ redirectTo: '/'});
	
	
	//Does not remove # symbol if not supported by browser
	/*if(window.history && window.history.pushState){
	    $locationProvider.html5Mode(true);
	  }*/
	
});