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
angular.module('dntApp').controller('bookingController', ['$modal','$rootScope','$scope','ordersService','$log','$routeParams','$window',
                                                          function ($modal, $rootScope, $scope, ordersService, $log, $routeParams, $window) {
	$scope.errorMessage;
	$scope.booking ={};
	$scope.beds = 0;
	
	$scope.$on('event:booking', function(event) {
		$scope.postBooking($scope.booking);
	});
	
	
	$scope.postBooking = function(booking) {
		ordersService.postOrder(booking)
		.success(function (data) {
			$scope.pay(data.id);
		})
		.error(function (error) {
			$scope.errorMessage = error.message;

		});
	};

	
	$scope.pay = function(bookingId) {
		ordersService.startPayment(bookingId)
		.success(function(data) {
			$window.location.href =data.redirectUrl;
		})
		.error(function(error) {
			$scope.errorMessage = error.message;
		});
	};
	
	
	$scope.getPrices = function(id) {
		ordersService.getPrices(id)
		.success(function (data) {
			$scope.booking.guests = data;
		})
		.error(function(error) {
			$log.info(error.message);
		});
	};

	
	$scope.authenticatePayment = function(transactionId, responseCode) {
		ordersService.authenticatePayment(transactionId, responseCode)
		.success(function(data) {
			$scope.openDialog('/views/statusModalSuccess.html');
		})
		.error(function(error) {
			$scope.openDialog('/views/statusModalError.html');
		});
	};
	
	
	$scope.openBookingConfirmDialog = function() {
		var modalInstance = $scope.openDialog('/views/bookingModal.html', $scope.booking);

		    modalInstance.result.then(function (selectedItem) {
		      $scope.selected = selectedItem;
		    }, function () {
		      $log.info('Modal dismissed at: ' + new Date());
		      
		    });
	};
	
	
	$scope.openDialog = function (url, data) {
		var modalInstance = $modal.open({
			templateUrl: url,
			controller: 'ModalInstanceCtrl',
			resolve: {
		        item: function () {
		          return data;
		        }
		    }
		});
		return modalInstance
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