/*
 * Controller for the ordersView. Sends a get request for orderHistory to the server.
 * Methods for getting and cancelling bookings.
 */
app.controller('orderController', function ($scope, $location, $routeParams, ordersService, api, $log) {
	$scope.currentPage = 1;
	$scope.totalItems = 10;
	$scope.itemsPerPage = 10;
	$scope.setPage = function(page) {
		$scope.getOrders(page-1);
	};


	$scope.getOrders = function(page) {
		$log.info($scope.itemsPerPage);
		ordersService.getOrders(page, $scope.itemsPerPage)
		.success(function (userOrders) {
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



	init();
	function init() {
			var page = $routeParams.page;
			if(page) {
				$scope.currentPage = page;
				$scope.getOrders(page-1);
			}
			else {
				$scope.getOrders(0);
			}
			
		
	};
});

/*
 * Controller for testView.
 */
app.controller('testController', function ($scope) {
	
	  
    init();

    function init() {
       
    }

});

/*
 * Controller for bookingView. Possible to add danger and sucess alert to the view
 * by using the method addAlert. The method postBooking uses the ordersService to 
 * post the booking to the server.
 */
app.controller('bookingController', function ($scope, ordersService, $log, $routeParams) {
	
	$scope.booking ={};
	$scope.person = {};
	$scope.beds = 20;
	
	$scope.bedsLeft = function() {
		var left = $scope.beds;
		angular.forEach($scope.person, function(value, key) {
			left = left -value;
		});
		if(left>0) {
			return left;
		}
		else {
			return 0;
		}
		
	};
	
	$scope.range = function(isSet) {
		var bedsLeft = $scope.bedsLeft();
		var end = bedsLeft;
		if((isSet != null || isSet>0) && end<=isSet) {
			end = isSet + bedsLeft;
		}
		
		
	    var result = [];
	    for (var i = 0; i <= end; i++) {
	        result.push(i);
	    }
	    
	    return result;
	};
	
    $scope.postBooking = function(booking) {
		
    	ordersService.postOrder(booking)
		.success(function (success) {
			
		})
		.error(function (error) {
			
		});
    };
    
    init();
    function init() {
       var id = $routeParams.id;
       if(id) {
    	   $scope.booking.cabinId = id;
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