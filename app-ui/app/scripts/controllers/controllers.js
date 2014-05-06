'use strict';

/**
 * @ngdoc object
 * 
 * @name dntApp.controller:orderController
 * @requires dntApp.bookingService
 * @requires dntApp.appStateService
 * @requires ui.bootstrap.$modal
 * @description Controller for the `ordersView`. Responsible for retrieving the order history of a customer,
 * and contains methods for getting and canceling bookings.
 * 
 */
angular.module('dntApp').controller('orderController', ['$scope','$modal','$routeParams','bookingService', '$log', 'appStateService',
                                                        function ($scope, $modal, $routeParams, bookingService, $log, appStateService ) {
	$scope.currentPage =1;
	$scope.totalItems = 0;
	$scope.itemsPerPage = 10;
	$scope.orders;
	$scope.errorMessage = '';
	$scope.user = {};
	
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
     * the data is made available in the scope.
     */
	$scope.getOrders = function(page) {
		bookingService.getOrders(page, $scope.itemsPerPage)
		.then(function(userBookings){
			$scope.currentPage = page +1;
			$scope.orders = userBookings.data;
			if(userBookings.totalItems) {
				$scope.totalItems = userBookings.totalItems;
			}
			$scope.user = appStateService.getUserCredentials();
		},
		function(error){
			$scope.errorMessage='unable to load your orders' + error.message;
		});
	};

	/**
     * @ngdoc method
     * @name dntApp.object#cancelOrder
	 * @methodOf dntApp.controller:orderController
     * @param {JSON object} order 	What booking to cancel.
     * @description Method first open a confirm modal to confirm cancellation with customer.
     * If confirmed a request for cancelling this booking is posted to the back end.
     * If promise is resolved (accepted), the order is removed from the orders array.
     */
	$scope.cancelOrder = function (order) {
		if(order.ableToCancel) {
			$scope.openDialog('/views/cancelConfirmModal.html', null).result.then(function () {
				bookingService.cancelOrder(order.id)
				.then(function(data){
					var index = $scope.orders.indexOf(order);
					$scope.orders.splice(index, 1);
				})
			});
		}
		
	};
	
	$scope.open = function (order) {
		bookingService.getOrderSummary(order.id)
		.then(function(ord){
			$log.info(ord);
			var modalInstance = $scope.openDialog('/views/receiptModal.html', ord);
		});
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
	
	/**
     * @ngdoc method
     * @name dntApp.object#init
	 * @methodOf dntApp.controller:orderController
     * @description Init method. If a page url parameter is present on initialization of the controller
     * the method will try to retrieve that page of booking. I.e restoring the view, if browser is reloaded.
     * NOT WORKING YET, LOCATION must be used to set url parameters when selecting a page.
     */
	$scope.init = function() {
		var pageNo = parseInt($routeParams.page);
		if(pageNo) {
			$scope.getOrders(pageNo-1);
		}
		else {
			$scope.getOrders(0);
		}
	}
	$scope.init();
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
 * @requires dntApp.appStateService
 * @requires ui.bootstrap.$modal
 * @description `bookingController` works as the glue between the back end and the front end.
 *  It is responsible for retrieving and posting bookings, and 
 *  a correct flow when paying using Nets.
 *  Important for the {@link dntBookingModule.directive:dntBookingModule dntBookingModule} directive,
 *  since it retrieves all the data needed by this directive.
 */
angular.module('dntApp').controller('bookingController', ['$modal','$scope','bookingService','$log','$routeParams','$window', 'appStateService',
                                                          function ($modal, $scope, bookingService, $log, $routeParams, $window, appStateService) {
	$scope.validState = true;
	$scope.errorMessage ='';
	$scope.booking ={};
	$scope.beds = 0;
	$scope.hideIndex = 0;
	
	/**
     * @ngdoc method
     * @name dntApp.object#postBooking
     * @methodOf dntApp.controller:bookingController
     * @param {JSON object} booking 	Object containing all the data required by the back end to book a cabin
     * @description Posts booking to the back end, and depending on answer from server calls the pay method,
     *  or an error message is put into scope.errorMessage (Two way binded with the dntBookingModule directive as 
     *  the errorModel). Before posting a booking, the booking object is validated and processed. If
     *  the promise returned by the postOrder in bookingService is resolved,
     *  pay function is called.
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
	
	/**
     * @ngdoc method
     * @name dntApp.object#validateBooking
     * @methodOf dntApp.controller:bookingController
     * @param {JSON object} booking 	Object containing all the data required by the back end to book a cabin
     * @description ValidateBooking validate the booking object, by checking the controllers state,
     * is any important properties are undefined and that at a booking contain at least one guest.
     * If the validation fails, a descriptive error message is put into the scope.
     * @returns {Boolean} Whether a error has been found or not
     */
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
     * @param {Number} bookingId 	the id of the booking to setup a payment for.
     * @description To setup a payment with Netaxept the back end have to be called. The
     * back end registers a payment with Nets and return a redirect url to the front end.
     * This can be retrieved when the promise is resolved, and the user will be 
     * redirected to the url specified by data.redirectUrl.
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
     * @param {Number} cabinId 	the id of the cabin to retrieve prices from.
     * @description Method retrieves price matrix from back end, and store them in $scope.booking.guests.
     *  (will be utilized by dntSelector in the booking component). The price matrix is processed using
     *  the processPriceMatrix method.
     */
	$scope.getPrices = function(cabinId) {
		bookingService.getPrices(cabinId)
		.then(function(data){
			$scope.booking.guests = processPriceMatrix(data);
		},
		function(error){
			$scope.errorMessage = error.message;
		});
	};
	
	/**
     * @ngdoc method
     * @name dntApp.object#removeUnpickedpriceCategories
     * @methodOf dntApp.controller:bookingController
     * @param {JSON object} priceCategories 	An object containing a price matrix
     * @description Removes all unused price categories. Can be used before posting a booking to
     * the back end. It iterate through the priceCategories and removes all entries where nr is not
     * more than zero.
     * @returns {JSON object} A subset of the the price categories
     */
	//removes all unused price categories. Can be used before posting a booking
	var removeUnpickedpriceCategories = function(priceCategories) {
		var processedPrices = [];
		angular.forEach(priceCategories, function(value) {
			if(value.nr > 0) {
				processedPrices.push(value);
			}
		});
		return processedPrices;
	}
	
	/**
     * @ngdoc method
     * @name dntApp.object#processPriceMatrix
     * @methodOf dntApp.controller:bookingController
     * @param {JSON object} priceMatrix 	An object containing a price matrix
     * @description Process the price matrix into a more suitable array used in the view.
     * A row in the priceMatrix contains the price for a member and a non member.
     * This is split into two categories x, member and x, non-member and added 
     * to an array containing all price categories. A "nr" property is also added for each 
     * category and represents the number of person selected  by customer for that category.
     * @returns {JSON object} A list of all price categories.
     */
	var processPriceMatrix = function(priceMatrix) {
		var nonMemberGuests = [];
		var allGuests = [];
		angular.forEach(priceMatrix, function(value){
			
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
     * @param {Number} transactionId 	The transaction id of the payment. (Returned from Nets as a url parameter) 
     * @param {String} responseCode 	The response code saying whether a payment was cancelled, went through etc. (Returned from Nets as a url parameter) 
     * @description When external payment site redirect back to front end, this method should be called. It will try to authenticate payment at the backend,
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
	
	/**
     * @ngdoc method
     * @name dntApp.object#openBookingConfirmDialog
     * @methodOf dntApp.controller:bookingController
     * @description When user click the book-button in the booking component, this function is called.
     * The booking object is validated, and an termsAndConditions boolean property is inserted into
     * the booking object. A modal containing the order summary and a checkbox is opened. 
     * When terms and conditions is accepted the postBooking method is called.
     */
	$scope.openBookingConfirmDialog = function() {
		if(validateBooking($scope.booking)) {
		$scope.booking.termsAndConditions = false;
		var data = {};
		data.booking = $scope.booking;
		data.user = appStateService.getUserCredentials();
		var modalInstance = $scope.openDialog('/views/bookingModal.html', data);

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
     * @description Every time an instance of bookingController initialize, the init function will run. It checks the url for
     * different parameters and query parameters and depending on these set the initial state of the booking view.
     * For example if the url contain a responseCode the init function now know that a customer has paid for booking
     * and the payment should be authenticated. If some url parameters are not present or wrong the validState flag
     * is set to false.
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
 * @name dntCommon.controller:authController
 * @requires dntCommon.api 
 * @requires dntCommon.authorization
 * @requires dntCommon.appStateService
 * @description The `authController`  is responsible for 
 * DNT connect flow, and user authentication. Used in authView and in navbar.
 * 
 * Example flow:
 * 
 * 1. User not logged in and entering /orderHistory in Browser
 * 2. Backend responds with error unauthorized.
 * 3. HttpInterceptor intercepts when unauthorized, saves the location and redirects user to /login.
 * 4. User press login button and newLogin is called.
 * 5. Backend encrypts a timestamp and a makes hmac and constructs a redirectUrl for the front end.
 * 6. Front end redirect using the redirectUrl.
 * 7. User enters credentials at DNT sign in page and is returned back to /login
 * 8. authController inits and hmac and encrypted data retrieved from the url.
 * 9. authControllers' checkLogin will send data and hmac to back end and a auth token sent back
 * 10.checkLogin will restore location (p. 3) and emits a "sign in" event
 */
angular.module('dntCommon').controller('authController', ['$log', '$scope','$location','appStateService','authorization','api','$window', '$routeParams',
                                                       function ($log, $scope, $location, appStateService, authorization, api, $window, $routeParams) {
	$scope.showLogin = false;
	$scope.loginErrorMessage ='';
	
	/**
	 * @ngdoc method
	 * @name dntCommon.object#newLogin
	 * @methodOf dntCommon.controller:authController
	 * @description When user press a log in button this method should be used. It will
	 * first save the url/state in a cookie using appStateservice, and then request a redirect url 
	 * from the back end. When the promise is resolved an redirectUrl containing an url tot the DNT login site, and
	 * url parameters, clientId, hmac (message authentication code), and data (encrypted) is retrieved.
	 *  The front end will then redirect to this url (log in at Den Norske Turistforeningen).
	 */ 
	$scope.newLogin = function () {
		appStateService.saveAttemptUrl();
		var success = function(data) {
			if(data.redirectUrl) {
				$window.location.href = data.redirectUrl;
			}
			else {
				$scope.loginErrorMessage = 'Problem with back end';
			}
		};
		var error = function(error) {
			$scope.loginErrorMessage = 'Unable to contact server';
		};
		
		authorization.newLogin().success(success).error(error);
	}
	
	/**
	 * @ngdoc method
	 * @name dntCommon.object#logout
	 * @methodOf dntCommon.controller:authController
	 * @description When a user click a logout button this function should be called. 
	 * All user credentials are removed from the brower, like the token and the name of logged in customer.
	 * A "signed out" event is emitted to notify the navbar.
	 */
	$scope.logout = function () {
		appStateService.removeUserCredentials();
		$scope.$emit('event:signedOut');
		var success = function (data) {
			api.destroy();
		};

		var error = function (error) {
			api.destroy();
			$scope.loginErrorMessage = 'Unable to contact server';
		};
		authorization.logout().success(success).error(error);
	};
	
	/**
	 * @ngdoc method
	 * @name dntCommon.object#checkLogin
	 * @methodOf dntCommon.controller:authController
	 * @param {String} encryptedData encrypted data string
	 * @param {String} hmac	message authentication code
	 * @description  When DNT Connect redirect back to the front it redirect to /login and authController with
	 * some url parameters. These are retrieved and checkLogin should be called.
	 * The encryptedData and hmac are sent to the backend and decrypted and checked and a token
	 * is sent back. If user is authenticated at nets the authentication token and name is inserted 
	 * into a cookie using the appStateService. Eventually the front end is redirected (redirectToAtteptedUrl) back to
	 * to the place where the authentication was initialiazed.
	 */
	$scope.checkLogin = function(encryptedData, hmac) {
		authorization.checkLogin(encryptedData, hmac).success(function(authData) {
			var token = authData.authToken;
			var name = authData.name || 'n/a';
			if(!angular.isUndefined(token)) {
				$scope.$emit('event:signedIn', authData);
				api.init(token);
				appStateService.insertUserCredentials(token, authData.id, name, authData.isAdmin, authData.email);
				appStateService.redirectToAttemptedUrl();
			}
			else {
				$scope.loginErrorMessage = 'Token could not be retrieved from server';
			}
		}).error(function(error) {
			$scope.loginErrorMessage = 'Unable to sign in using DNT Connect. Try again';
		});
	};
	
	/**
	 * @ngdoc method
	 * @name dntCommon.object#init
	 * @methodOf dntCommon.controller:authController
	 * @description  When `authController` is initialized url is checked for parameters like
	 * data and hmac, which indicate that the user has been redirected from DNT Connect.
	 * When these parameters are present, `checkLogin` is called.
	 */
	$scope.init = function() {
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
	$scope.init();
}]);


/**
 * @ngdoc object
 * 
 * @name dntCommon.controller:headerController
 * @description Controller used by navbar  to set 
 * active tab, and to decide whether to show log in button, or a drop down with options if user is logged in.
 * 
 */
angular.module('dntCommon').controller('headerController', ['$scope','$rootScope', '$location', 'appStateService',
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


	/**
	 * @ngdoc method
	 * @name dntCommon.object#init
	 * @methodOf dntCommon.controller:authController
	 * @description  When `headerController` is initialized `appStateService` is used to retrieve user
	 * credentials. If they exist name is made available in the scope, and the drop down list is displayed
	 * in the view.
	 */
	 $scope.init = function() {
		var userData = appStateService.getUserCredentials();
		if(!angular.isUndefined(userData.token)) {
			$scope.name = userData.name;
			$scope.loggedIn = true;
			$scope.isAdmin = userData.isAdmin;
		}
	};
	$scope.init();
}]);