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
app.controller('testController', function ($scope) {
	$scope.personType = [
	                     {"nr": 0, "type":"voksen, medlem"},
	                     {"nr": 0, "type":"ungdom, medlem"},
	                     {"nr": 0, "type":"barn, medlem"},
	                     {"nr": 0, "type":"spedbarn"},
	                     {"nr": 0, "type":"voksen"},
	                     {"nr": 0, "type":"ungdom"},
	                     {"nr": 0, "type":"barn"}
	                     ];
	  
    init();

    function init() {
       
    };

});

/*
 * Controller for bookingView. Possible to add danger and sucess alert to the view
 * by using the method addAlert. The method postBooking uses the ordersService to 
 * post the booking to the server.
 */
app.controller('bookingController', function ($scope, ordersService, $log, $routeParams) {
	$scope.personType = [
	                     {"nr": 0, "type":"voksen medlem", "price": 300},
	                     {"nr": 0, "type":"ungdom medlem", "price": 150},
	                     {"nr": 0, "type":"barn medlem", "price": 100},
	                     {"nr": 0, "type":"spedbarn" ,"price": 0},
	                     {"nr": 0, "type":"voksen", "price": 400},
	                     {"nr": 0, "type":"ungdom","price": 200},
	                     {"nr": 0, "type":"barn", "price": 150}
	                     ];
	
	$scope.booking ={};
	$scope.beds = 20;
	$scope.price = 0;
	
	$scope.bedsTotal = function() {
		var total  =0;
		angular.forEach($scope.personType, function(value, key) {
			total += value.nr;
		});
		return total;	
	};
	
	$scope.$on('event:booking', function(event) { 
		
        $scope.postBooking();            
    });
	
    $scope.postBooking = function() {
    	
		$scope.booking.beds =($scope.bedsTotal()) + "";
		$log.info($scope.booking.beds);
		$scope.booking.Person = $scope.personType;
    	ordersService.postOrder($scope.booking)
		.success(function (success) {
			$log.info("Det virket" + success.message);
		})
		.error(function (error) {
			$log.info("Det virket ikke. " + error.message);
		});
    };
    
    
    init();
    function init() {
       var id = $routeParams.id;
       var type =$routeParams.type;
       if(id && type) {
    	   $scope.cabinType = type;
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