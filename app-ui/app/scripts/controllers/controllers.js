'use strict';
/*
 * Controller for the ordersView. Sends a get request for orderHistory to the server.
 * Methods for getting and cancelling bookings.
 */
angular.module('dntApp').controller('orderController', ['$scope','$location','$routeParams','ordersService', '$log',
                                                        function ($scope, $location, $routeParams, ordersService, $log) {
	$scope.currentPage =1;
	$scope.totalItems = 10;
	$scope.itemsPerPage = 10;

	$scope.setPage = function(pageNo) {
		$scope.getOrders(pageNo-1);
	};

	$scope.getOrders = function(page) {
		$log.info('inni her ja');
		ordersService.getOrders(page, $scope.itemsPerPage)
		.success(function (userOrders) {
			$scope.currentPage = page +1;
			$scope.orders = userOrders.data;
			$scope.totalItems = userOrders.totalItems;

		})
		.error(function (error) {
			$log.info('problem');
			$scope.status = 'unable to load customer data' + error.message;
		});
	};


	$scope.cancelOrder = function (order) {
		ordersService.cancelOrder(order.id)
		.success(function (data) {
			var index = $scope.orders.indexOf(order);
			$scope.orders.splice(index, 1);
		})
		.error(function (error) {
			$scope.status = 'not found' + error.message;
		});
	};



	function init() {
		$log.info('inni init');
		var pageNo = parseInt($routeParams.page);
		if(pageNo) {
			$scope.getOrders(pageNo-1);
		}
		else {
			$scope.getOrders(0);
		}
	}
	init();
}]);

/*
 * Controller for testView.
 */
angular.module('dntApp').controller('testController', ['$scope','$window', function ($scope, $window) {
	$scope.testExternalView =function() {
		$window.location.href ='http://www.vg.no';
	};



	function init() {

	}
	init();
}]);

/*
 * Controller for 
View. The method postBooking uses the ordersService to 
 * post the booking to the server.
 */
angular.module('dntApp').controller('bookingController', ['$filter','$rootScope','$scope','ordersService','$log','$routeParams','$window',
                                                          function ($filter, $rootScope, $scope, ordersService, $log, $routeParams, $window) {
	$scope.errorMessage;
	$scope.personType = null;
	$scope.paid = 0;
	$scope.booking ={};
	$scope.beds = 0;
	$scope.price = 0;
	$scope.now = new Date();


	/** Track changes from the datepicker calendars and display the from/to dates **/
	$scope.$watch('booking.dateTo', function(){
		$scope.dateTo = '' + $scope.booking.dateTo.getDate() + '/' + ($scope.booking.dateTo.getMonth() + 1) + ' ' + $scope.booking.dateTo.getFullYear();
	});

	$scope.$watch('booking.dateFrom', function(){
		$scope.dateFrom = '' + $scope.booking.dateFrom.getDate() + '/' + ($scope.booking.dateFrom.getMonth() + 1) + ' ' + $scope.booking.dateFrom.getFullYear();
		if ($scope.booking.dateTo < $scope.booking.dateFrom){
			$scope.booking.dateTo = $scope.booking.dateFrom;
		}
	});

	$scope.$on('event:booking', function(event) {

		$scope.postBooking();

	});

	$scope.postBooking = function() {

		$scope.booking.guests = $scope.personType;
		$scope.booking.dateFrom= $filter('date')($scope.booking.dateFrom,'yyyy-MM-dd');
		$scope.booking.dateTo= $filter('date')($scope.booking.dateTo,'yyyy-MM-dd');

		ordersService.postOrder($scope.booking)
		.success(function (data) {
			$scope.pay(data.id);
			$log.info('Det virket' + data.message);
		})
		.error(function (error) {
			$scope.errorMessage = error.message;

		});
	};

	$scope.pay = function(bookingId) {
		ordersService.startPayment(bookingId)
		.success(function(data) {
			$log.info(data.redirectUrl);
			$window.location.href =data.redirectUrl;
		})
		.error(function(error) {
			$log.info(error.message);
		});
	};
	
	$scope.getPrices = function(id) {
		ordersService.getPrices(id)
		.success(function (data) {
			$scope.personType = data;
		})
		.error(function(error) {
			$log.info(error.message);
		});
	};

	$scope.authenticatePayment = function(transactionId, responseCode) {
		ordersService.authenticatePayment(transactionId, responseCode)
		.success(function(data) {

			$scope.paid = 1;
		})
		.error(function(error) {
			$scope.paid = 2;
			$log.info(error.message);

		});
	};


	function init() {
		var id = $routeParams.id;
		var type =$routeParams.type;
		var beds = $routeParams.beds;
		if(id && type) {
			$scope.cabinType = type;
			$scope.booking.cabinId = id;
			$scope.personType =$scope.getPrices(id);
			if(type==='large' && beds !== null) {
				$scope.beds = beds;
			}
			if($routeParams.responseCode) {
				if($routeParams.responseCode === 'OK') {
					$scope.authenticatePayment($routeParams.transactionId, $routeParams.responseCode);
				}
				else {

					$scope.authenticatePayment($routeParams.transactionId, $routeParams.responseCode);
				}




			}
		}
	}
	init();
}]);

/*
 * The authController is the controller of authView and is responsible for 
 * sending user credentials to server and take care of a authentication token return by the server.
 * 
 */
angular.module('dntApp').controller('authController', ['$log','$rootScope','$scope','$location','$cookieStore','authorization','api',
                                                       function ($log, $rootScope, $scope, $location, $cookieStore, authorization, api) {

	$rootScope.$on('event:loggingOut', function(event, data) {
		$scope.logout();

	});

	$scope.login = function (credentials) {
		var success = function (data) {

			$scope.$emit('event:loggingIn', credentials.emailAdress);
			var token = data.authToken;
			api.init(token);
			$cookieStore.put('token', token);
			$cookieStore.put('name', credentials.emailAdress);
			$location.path('/');
		};

		var error = function () {

		};
		authorization.login(credentials).success(success).error(error);
	};

	$scope.logout = function () {

		var success = function (data) {

			$cookieStore.remove('token');
			$cookieStore.remove('name');
			$location.path('/');
		};

		var error = function (error) {
			$log.info(error);
		};
		authorization.logout().success(success).error(error);
	};
}]);


/*
 * Controller used by navbar in indexAngular.html to set 
 * active tab, and to decide what to show in navbar
 */
angular.module('dntApp').controller('headerController', ['$scope','$rootScope', '$location', '$cookieStore' ,
                                                         function ($scope,$rootScope, $location, $cookieStore) {
	$scope.loggedIn = false;
	$scope.name ='';

	$rootScope.$on('event:loggingIn', function(event, data) {
		$scope.loggedIn = true;
		$scope.name = data;
	});


	$scope.logoutAction = function() {
		$scope.name ='';
		$scope.loggedIn = false;
		$rootScope.$broadcast('event:loggingOut', null);
	};


	$scope.isActive = function (viewLocation) {
		return viewLocation === $location.path();
	};



	function init() {
		var name = $cookieStore.get('name');
		if(name) {
			$scope.name = name;
			$scope.loggedIn = true;
		}
	}
	init();
}]);