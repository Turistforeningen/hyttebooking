'use strict';
/*
 * Controller for the ordersView. Sends a get request for orderHistory to the server.
 * Methods for getting and cancelling bookings.
 */
app.controller('orderController', function ($scope, $location, $routeParams, ordersService, api, $log) {
	$scope.currentPage =1;
	$scope.totalItems = 10;
	$scope.itemsPerPage = 10;
	
	$scope.setPage = function(pageNo) {
		$scope.getOrders(pageNo-1);
	};
	
	$scope.getOrders = function(page) {
		
		ordersService.getOrders(page, $scope.itemsPerPage)
		.success(function (userOrders) {
			$scope.currentPage = page +1;
			$scope.orders = userOrders.data;
			$scope.totalItems = userOrders.totalItems;
			
		})
		.error(function (error) {
			$log.info("problem");
			$scope.status = 'unable to load customer data' + error.message;
		});
	};


	$scope.cancelOrder = function (order) {
		ordersService.cancelOrder(order.id)
		.success(function (data) {
			var index = $scope.orders.indexOf(order)
			$scope.orders.splice(index, 1);
		})
		.error(function (error) {
			$scope.status = 'not found' + error.message;
		});
	};


	
	function init() {
			var pageNo = parseInt($routeParams.page);
			if(pageNo) {
				$scope.getOrders(pageNo-1)
			}
			else {
				$scope.getOrders(0);
			}	
	};
	init();
});

/*
 * Controller for testView.
 */
app.controller('testController', function ($scope, $window) {
	$scope.testExternalView =function() {
		$window.location.href ="http://www.vg.no";
	};
	
    init();

    function init() {
       
    };

});

/*
 * Controller for 

























View. The method postBooking uses the ordersService to 
 * post the booking to the server.
 */
app.controller('bookingController', function ($filter, $rootScope, $scope, ordersService, $log, $routeParams, $window) {
	$scope.personType;
	$scope.paid = 0;
	$scope.booking ={};
	$scope.beds = 20;
	$scope.price = 0;
	$scope.now = new Date();
	
	$scope.bedsTotal = function() {
		var total  =0;
		angular.forEach($scope.personType, function(value, key) {
			total += value.nr;
		});
		return total;	
	};
	
	/** Track changes from the datepicker calendars and display the from/to dates **/
	$scope.$watch('booking.dateTo', function(){
		$scope.dateTo = "" + $scope.booking.dateTo.getDate() + "/" + ($scope.booking.dateTo.getMonth() + 1) + " " + $scope.booking.dateTo.getFullYear();
	});
	
	$scope.$watch('booking.dateFrom', function(){
		$scope.dateFrom = "" + $scope.booking.dateFrom.getDate() + "/" + ($scope.booking.dateFrom.getMonth() + 1) + " " + $scope.booking.dateFrom.getFullYear();
		if ($scope.booking.dateTo < $scope.booking.dateFrom){
			$scope.booking.dateTo = $scope.booking.dateFrom;
		}
	});
	
	$scope.$on('event:booking', function(event) { 
		
        $scope.postBooking(); 
  
    });
	
    $scope.postBooking = function() {
    	
		$scope.booking.beds =($scope.bedsTotal()) + "";
		$scope.booking.guests = $scope.personType;
		$scope.booking.dateFrom= $filter('date')($scope.booking.dateFrom,'yyyy-MM-dd');
		$scope.booking.dateTo= $filter('date')($scope.booking.dateTo,'yyyy-MM-dd');
		
    	ordersService.postOrder($scope.booking)
		.success(function (data) {
			$scope.pay(data.id);
			$log.info("Det virket" + data.message);
		})
		.error(function (error) {
			$log.info("Det virket ikke. " + error.message);
			
		});
    };
    
    $scope.pay = function(bookingId) {
    	ordersService.startPayment(bookingId)
    	.success(function(data) {
    		$log.info(data.redirectUrl);
    		$window.location.href =data.redirectUrl;
    	})
    	.error(function(error) {
    		
    	});
    	
    }
    $scope.getPrices = function(id) {
    	ordersService.getPrices(id)
    	.success(function (data) {
    		$scope.personType = data;
    	})
    	.error(function(error) {
    		$log.info("");
    	});
    };
    
    $scope.authenticatePayment = function(transactionId, responseCode) {
    	ordersService.authenticatePayment(transactionId, responseCode)
    	.success(function(data) {
    		
    		$scope.paid = 1;
    	})
    	.error(function(error) {
    		$scope.paid = 2;
    		
    	});
    }
    
    init();
    function init() {
       var id = $routeParams.id;
       var type =$routeParams.type;
       var beds = $routeParams.beds;
       if(id && type) {
    	   $scope.cabinType = type;
    	   $scope.booking.cabinId = id;
    	   $scope.personType =$scope.getPrices(id);
    	   if(type=='large' && beds != null) {
    		   $scope.beds = beds;
    	   }
    	 if($routeParams.responseCode) {
    		 if($routeParams.responseCode == 'OK') {
    			 $scope.authenticatePayment($routeParams.transactionId, $routeParams.responseCode);
    		 }
    		 else {
    			 
    			 $scope.authenticatePayment($routeParams.transactionId, $routeParams.responseCode);
    		 }
    		
    		
    		 
    		
    	 }
       }
    };

});

/*
 * The authController is the controller of authView and is responsible for 
 * sending user credentials to server and take care of a authentication token return by the server.
 * 
 */
app.controller('authController', function ($log, $rootScope, $scope, $location, $cookieStore, authorization, api) {
	
	$rootScope.$on('event:loggingOut', function(event, data) {
		$scope.logout();
		 
	});
	
	$scope.login = function (credentials) {
		var success = function (data) {
			
			$scope.$emit('event:loggingIn', credentials.emailAdress);
			var token = data.authToken;
			api.init(token);
			$cookieStore.put('token', token);
			$cookieStore.put('name', credentials.emailAdress)
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
});


/*
 * Controller used by navbar in indexAngular.html to set 
 * active tab, and to decide what to show in navbar
 */
app.controller('headerController' ,function ($scope,$rootScope, $location, $cookieStore,$log) 
{ 
	$scope.loggedIn = false;
	$scope.name;
	
	$rootScope.$on('event:loggingIn', function(event, data) {
		$log.info(data + "  " + data + "   dfsdfsdf");
		$scope.loggedIn = true;
		$scope.name = data;
	});
	
	
	$scope.logoutAction = function() {
		$scope.name ="";
		$scope.loggedIn = false;
		$rootScope.$broadcast('event:loggingOut', null);
	};
	
	
    $scope.isActive = function (viewLocation) { 
        return viewLocation === $location.path();
    };
    
    
    init();
    function init() {
    	var name = $cookieStore.get('name');
    	if(name) {
    		$scope.name = name;
    		$scope.loggedIn = true;
    	}
    }
});