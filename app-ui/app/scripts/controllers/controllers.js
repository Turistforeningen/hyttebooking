'use strict';

/**
 * @ngdoc object
 * 
 * @name dntApp.controller:orderController
 * @requires dntApp.bookingService
 * @requires ui.bootstrap.$modal
 * @description Controller for the ordersView. Responsible for retrieving the order history of a customer,
 * and contains methods for getting and canceling bookings.
 * 
 */
angular.module('dntApp').controller('orderController', ['$scope','$modal','$routeParams','bookingService', '$log',
                                                        function ($scope, $modal, $routeParams, bookingService, $log) {
	$scope.currentPage =1;
	$scope.totalItems = 10;
	$scope.itemsPerPage = 10;
	$scope.orders;
	$scope.errorMessage = '';
	
	$scope.setPage = function(pageNo) {
		if(pageNo>0) {
			$scope.getOrders(pageNo-1);
		}
	};
	
	/**
     * @ngdoc method
     * @name dntApp.object#getOrders
	 * @methodOf dntApp.controller:orderController
     * @param {Number} page 	What page of bookings in order history to request.
     * @description Method gets the order history of a user from the back end by 
     * utilizing the bookingservice's getOrders method. When promise is resolved
     * 
     */
	$scope.getOrders = function(page) {
		bookingService.getOrders(page, $scope.itemsPerPage)
		.then(function(userBookings){
			$scope.currentPage = page +1;
			$scope.orders = userBookings.data;
			$scope.totalItems = userBookings.totalItems;
		},
		function(error){
			$scope.errorMessage='unable to load your orders' + error.message;
		});
	};

	// Temporary replacing the code for canceling an order, instead showing the order information in an alert popup
	$scope.cancelOrder = function (order) {
		$scope.openDialog('/views/cancelConfirmModal.html', null).result.then(function () {
			bookingService.cancelOrder(order.id)
			.then(function(data){
				var index = $scope.orders.indexOf(order);
				$scope.orders.splice(index, 1);
			})
		});
	};
	
	$scope.open = function (order) {
		bookingService.getOrderSummary(order.id)
		.then(function(ord){
			$log.info(ord);
			var modalInstance = $scope.openDialog('/views/receiptModal.html', ord);
			//alert(JSON.stringify(ord));
		});
		//var modalInstance = $scope.openDialog('/views/receiptModal.html', order);//, $scope.booking);
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
angular.module('dntApp').controller('testController', ['$scope', function ($scope) {
	
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
	$scope.hideIndex = 0;
	
	/**
     * @ngdoc method
     * @name dntApp.object#postBooking
     * @methodOf dntApp.controller:bookingController
     * @description Posts booking to database, and depending on answer from server the pay method is run or an error message is put into scope.errorMessage
     */
	$scope.postBooking = function(booking) {
		if(validateBooking(booking)) {
			var processedBooking = angular.copy(booking);
			processedBooking.guests = removeUnpickedpriceCategories(processedBooking.guests);
			bookingService.postOrder(processedBooking)
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
	
	//removes all unused price categories. Can be used before posting a booking
	var removeUnpickedpriceCategories = function(priceMatrix) {
		var processedPrices = [];
		angular.forEach(priceMatrix, function(value) {
			if(value.nr > 0) {
				processedPrices.push(value);
			}
		});
		return processedPrices;
	}
	
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
			guestType.guestType = value.guestType;
			guestType.nr = 0;
			guestType.price = value.nonMemberPrice;
			guestType.isMember = false;
			
			if(guestType.price == guestTypeMember.price) {
				//same price most likely not an memberCategory. Should be fixed backend. Comparing prices
				//in front end wrong
				guestTypeMember = guestType;
				guestType = null;
				allGuests.push(guestTypeMember)
			}
			else {
				allGuests.push(guestTypeMember)
				nonMemberGuests.push(guestType)
			}
			
		 });
		$scope.hideIndex = allGuests.length;
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
				else if($routeParams.responseCode === 'Cancel'){

					$scope.openDialog('/views/statusModalCancellation.html', null);
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
angular.module('dntApp').controller('authController', ['$log', '$scope','$location','appStateService','authorization','api','$window', '$routeParams',
                                                       function ($log, $scope, $location, appStateService, authorization, api, $window, $routeParams) {
	$scope.showLogin = false;
	/**
	 * @ngdoc method
	 * @name dntApp.object#login
	 * @methodOf dntApp.controller:authController
	 * @description When user is trying to login, redirect to DNT connect
	 */
	$scope.newLogin = function () {
		appStateService.saveAttemptUrl();
		var success = function(data) {
			$window.location.href = data.redirectUrl;
		};
		var error = function(error) {
			$scope.errorMessage = error.message;
			$log.info("Could not connect to DNTConnect");
			$log.info(error);
		};
		
		authorization.newLogin().success(success).error(error);
	}
	

	$scope.logout = function () {
		appStateService.removeUserCredentials();
		$scope.$emit('event:signedOut');
		var success = function (data) {
			
		};

		var error = function (error) {
			$log.info(error);
		};
		authorization.logout().success(success).error(error);
	};
	
	$scope.checkLogin = function(encryptedData, hmac) {
		authorization.checkLogin(encryptedData, hmac).success(function(authData) {

			var token = authData.authToken;
			var name = authData.name || 'n/a';
			if(!angular.isUndefined(token)) {
				$scope.$emit('event:signedIn', authData);
				api.init(token);
				appStateService.insertUserCredentials(token, name, authData.isAdmin);
				appStateService.redirectToAttemptedUrl();
			}
			else {
				$log.info(token + " token undefined");
			}
		}).error(function(error) {
			$log.info("det virket ikke");
		});
	};

	var init = function() {
		var encryptedData = $routeParams.data;
		var hmac = $routeParams.hmac;
		if(encryptedData && hmac) {
			$scope.checkLogin(encryptedData, hmac);
		}
		else {
			if($location.path() == '/login') {
				$scope.showLogin = true;
			}
			
		}
	};
	init();
}]);


/**
 * @ngdoc object
 * 
 * @name dntApp.controller:headerController
 * @description Controller used by navbar  to set 
 * active tab, and to decide whether to show log in button or a drop down with options if user is logged in.
 * 
 */
angular.module('dntApp').controller('headerController', ['$scope','$rootScope', '$location', 'appStateService',
                                                         function ($scope,$rootScope, $location, appStateService) {
	$scope.loggedIn = false;
	$scope.isAdmin = false;
	$scope.name ='';

	$rootScope.$on('event:signedIn', function(event, data) {
		$scope.loggedIn = true;
		$scope.name = data.name;
		$scope.isAdmin = data.isAdmin;
	});
	
	$rootScope.$on('event:signedOut', function(event, data) {
		$scope.name ='';
		$scope.loggedIn = false;
		$scope.isAdmin = false;
	});
	
	$scope.isActive = function (viewLocation) {
		return viewLocation === $location.path();
	};



	function init() {
		var userData = appStateService.getUserCredentials();
		if(!angular.isUndefined(userData)) {
			$scope.name = userData.name;
			$scope.loggedIn = true;
			$scope.isAdmin = userData.isAdmin;
		}
	}
	init();
}]);