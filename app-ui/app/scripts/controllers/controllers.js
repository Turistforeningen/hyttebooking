'use strict';

/**
 * @ngdoc object
 * 
 * @name dntApp.controller:orderController
 * @requires dntApp.bookingService
 * @description Controller for the ordersView. Sends a get request for orderHistory to the server.
 * Contains methods for getting and canceling bookings.
 * 
 */
angular.module('dntApp').controller('orderController', ['$scope','$location','$routeParams','bookingService', '$log',
                                                        function ($scope, $location, $routeParams, bookingService, $log) {
	$scope.currentPage =1;
	$scope.totalItems = 10;
	$scope.itemsPerPage = 10;

	$scope.setPage = function(pageNo) {
		$scope.getOrders(pageNo-1);
	};

	$scope.getOrders = function(page) {
		bookingService.getOrders(page, $scope.itemsPerPage)
		.then(function(userBookings){
			$scope.currentPage = page +1;
			$scope.orders = userBookings.data;
			$scope.totalItems = userBookings.totalItems;
		},
		function(error){
			$scope.status='unable to load customer data' + error.message;
		});
	};


	$scope.cancelOrder = function (order) {
		bookingService.cancelOrder(order.id)
		.then(function(data){
			var index = $scope.orders.indexOf(order);
			$scope.orders.splice(index, 1);
		},
		function(error){
			$scope.status = 'not found' + error.message;
		});
	};

	
	function init() {
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
	
	function init() {

	}
	init();
}]);


/**
 * @ngdoc object
 * 
 * @name dntApp.controller:bookingController
 * @requires dntApp.bookingService
 * @description Controller for the booking that works as the glue beetween the posting of bookings to the server,
 *  paymentflow, reading query parameters and opening of modals. Important for the {@link dntBookingModule.directive:dntBookingModule dntBookingModule} directive to work.
 *  
 * 
 */
angular.module('dntApp').controller('bookingController', ['$modal','$rootScope','$scope','bookingService','$log','$routeParams','$window',
                                                          function ($modal, $rootScope, $scope, bookingService, $log, $routeParams, $window) {
	$scope.errorMessage;
	$scope.booking ={};
	$scope.beds = 0;
	
	/**
     * @ngdoc method
     * @name dntApp.object#postBooking
     * @methodOf dntApp.controller:bookingController
     * @description Posts booking to database, and depending on answer from server the pay method is run or an error message is put into scope.errorMessage
     */
	$scope.postBooking = function(booking) {
		bookingService.postOrder(booking)
		.then(function(data){
			$scope.pay(data.id);
		},
		function(error){
			$scope.errorMessage = error.message;
		});
	};


	/**
     * @ngdoc method
     * @name dntApp.object#pay
     * @methodOf dntApp.controller:bookingController
     * @description When called tries to retrieve transactionId from server, and if successful redirects to external payment site
     */
	$scope.pay = function(bookingId) {
		bookingService.startPayment(bookingId)
		.then(function(data){
			$window.location.href =data.redirectUrl;
		},
		function(error){
			$scope.errorMessage = error.message;
		});
	};
	
	/**
     * @ngdoc method
     * @name dntApp.object#getPrices
     * @methodOf dntApp.controller:bookingController
     * @description Method retrieves price matrix from back end, and store them in $scope.booking which should be used by dntBookingModule directive
     */
	$scope.getPrices = function(id) {
		bookingService.getPrices(id)
		.then(function(data){
			$scope.booking.guests = data;
		},
		function(error){
			$scope.errorMessage = error.message;
		});
	};

	/**
     * @ngdoc method
     * @name dntApp.object#authenticatePayment
     * @methodOf dntApp.controller:bookingController
     * @description When external payment site redirect back to app, this method should be called. It will try to authenticate payment at the backend,
     * and open a modal showing status of payment.
     */
	$scope.authenticatePayment = function(transactionId, responseCode) {
		bookingService.authenticatePayment(transactionId, responseCode)
		.then(function(data){
			$scope.openDialog('/views/statusModalSuccess.html');
		},
		function(error){
			$scope.openDialog('/views/statusModalError.html');
		});
	};
	
	
	$scope.openBookingConfirmDialog = function() {
		$scope.booking.termsAndConditions = false;
		var modalInstance = $scope.openDialog('/views/bookingModal.html', $scope.booking);

		modalInstance.result.then(function () {
			$scope.postBooking($scope.booking);
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

	/**
     * @ngdoc method
     * @name dntApp.object#init
     * @methodOf dntApp.controller:bookingController
     * @description Every time an instance of bookingController starts, the init function will run. It checks the url for
     * different parameters and query parameters and depending on these set the initial state of the booking view.
     */
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


/**
 * @ngdoc object
 * 
 * @name dntApp.controller:authController
 * @requires dntApp.api 
 * @requires dntApp.authorization
 * @description The authController is the controller of authView and is responsible for 
 * sending user credentials to server and take care of a authentication token return by the server.
 * 
 */
angular.module('dntApp').controller('authController', ['$log','$rootScope','$scope','$location','$cookieStore','authorization','api','$window',
                                                       function ($log, $rootScope, $scope, $location, $cookieStore, authorization, api, $window) {

	$rootScope.$on('event:loggingOut', function(event, data) {
		$scope.logout();

	});
	
	/**
	 * @ngdoc method
	 * @name dntApp.object#login
	 * @methodOf dntApp.controller:authController
	 * @description When user is trying to login, redirect to DNT connect
	 */
	$scope.newLogin = function () {
	
		var success = function(data) {
			$log.info(data);
			$window.location.href = data.redirectUrl;
		};
		var error = function(error) {
			$scope.errorMessage = error.message;
			$log.info("Could not connect to DNTConnect");
			$log.info(error);
		};
		
		authorization.newLogin().success(success).error(error);
	}
	

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


/**
 * @ngdoc object
 * 
 * @name dntApp.controller:headerController
 * @description Controller used by navbar  to set 
 * active tab, and to decide whether to show log in button or a drop down with options if user is logged in.
 * 
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