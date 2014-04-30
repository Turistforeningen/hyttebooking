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
angular.module('dntApp').controller('orderController', ['$scope','$modal','$routeParams','bookingService', '$log',
                                                        function ($scope, $modal, $routeParams, bookingService, $log) {
	$scope.currentPage =1;
	$scope.totalItems = 10;
	$scope.itemsPerPage = 10;
	$scope.orders;
	
	$scope.setPage = function(pageNo) {
		if(pageNo>0) {
			$scope.getOrders(pageNo-1);
		}
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
		alert(JSON.stringify(order));/*
		bookingService.cancelOrder(order.id)
		.then(function(data){
			var index = $scope.orders.indexOf(order);
			$scope.orders.splice(index, 1);
		},
		function(error){
			$scope.status = 'not found' + error.message;
		});*/
	};
	
	$scope.open = function (order) {
		//alert(modalToOpen);
		var modalInstance = $scope.openDialog('/views/receiptModal.html', order);//, $scope.booking);
	}
	
	/*
	 * send in url to template and json data the 
	 * template will utilize
	 */
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
	$scope.validState = true;
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
		if(validateBooking(booking)) {
			bookingService.postOrder(booking)
			.then(function(data){
				$scope.pay(data.id);
			},
			function(error){
				$scope.errorMessage = error.message;
			});
		}
		
	};
	
	var validateBooking = function(booking) {
		if(!$scope.validState) {
			$scope.errorMessage = "Ingen hytteId spesifisert.";
			return false;
		}
		else if(angular.isUndefined(booking.dateFrom)) {
			$scope.errorMessage = "Du må velge ankomstdato for å kunne reservere.";
			return false;
		}
		else if(angular.isUndefined(booking.dateTo)) {
			$scope.errorMessage = "Du må velge avreisedato for å kunne reservere.";
			return false;
		}
		var personCount = 0;
		angular.forEach(booking.guests, function(value, key) {
			personCount += value.nr;
		});
		if(personCount <= 0) {
			$scope.errorMessage = "Du må velge minst en person for å kunne reservere.";
			return false
		}
		return true;
	}

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
			$scope.booking.guests = processPriceMatrix(data);
		},
		function(error){
			$scope.errorMessage = error.message;
		});
	};
	
	//split categories into json suitable for view
	var processPriceMatrix = function(matrix) {
		var nonMemberGuests = [];
		var allGuests = [];
		angular.forEach(matrix, function(value){
			
			var guestTypeMember = {};
			var guestType = {};
			guestTypeMember.id = value.id;
			guestTypeMember.ageRange = value.ageRange;
			guestTypeMember.guestType = value.guestType + ', medlem';
			guestTypeMember.nr = 0;
			guestTypeMember.price = value.memberPrice;
			guestTypeMember.isMember = true;
			
			guestType.id = value.id;
			guestType.ageRange = value.ageRange;
			guestType.guestType = value.guestType + ',';
			guestType.nr = 0;
			guestType.price = value.nonMemberPrice;
			guestType.isMember = false;
			
			allGuests.push(guestTypeMember)
			nonMemberGuests.push(guestType)
		 });
		allGuests.push.apply(allGuests, nonMemberGuests)
		
		return allGuests;
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
		if(validateBooking($scope.booking)) {
		$scope.booking.termsAndConditions = false;
		var modalInstance = $scope.openDialog('/views/bookingModal.html', $scope.booking);

		modalInstance.result.then(function () {
			$scope.postBooking($scope.booking);
		}, function () {
			$log.info('Modal dismissed at: ' + new Date());

		});
		}
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
		//should probably refuse showing booking if routeParams is invalid
		var id = $routeParams.id;
		var type =$routeParams.type;
		var beds = $routeParams.beds;
		if(id && type) {
			$scope.cabinType = type;
			$scope.booking.cabinId = id;
			$scope.personType =$scope.getPrices(id);
			if(type==='large') {
				if(beds) {
					$scope.beds = beds;
				}
				else {
					$scope.validState = false;
				}
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
		else {
			$scope.validState = false;
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