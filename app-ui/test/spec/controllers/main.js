beforeEach(module('dntApp'));
describe('headerController', function(){
	var $scope, $location, $rootScope, createController;

    beforeEach(inject(function($injector) {
        $location = $injector.get('$location');
        $rootScope = $injector.get('$rootScope');
        $scope = $rootScope.$new();

        var $controller = $injector.get('$controller');

        createController = function() {
            return $controller('headerController', {
                '$scope': $scope
            });
        };
    }));

    it('should have loggedIn to false since user is not in cookiestore', function() {
        var controller = createController();
        expect($scope.loggedIn).toBe(false);
        expect($scope.name).toBe('');
    });
});

describe('dntApp', function () {
    var scope,
        controller,
        service;
    beforeEach(function () {
        module('dntApp');
    });
    
    // Mocking service?
    beforeEach(module(function($provide) {
        var service = { 
            getOrders: function (page, pageSize) {
                return {"data": [{"ableToCancel":true,"cabin":{"cabinType":"large","id":1,"name":"Fjordheim","nrActiveBookings":1},"dateFrom":1399586400000,"dateTo":1399759200000,"id":2,"nrOfBeds":"3","status":0,"timeOfBooking":1398328377255}],"totalItems":1}
            }
        };
        $provide.value('ordersService', service);
    }));
    
    describe('orderController', function () {
        beforeEach(inject(function ($rootScope, $controller) {
            scope = $rootScope.$new();
            controller = $controller('orderController', {
                '$scope': scope
            });
        }));
        
       
        it('checks that Arrived is correctly used', function() {
            // Arrange
            spyOn(service, 'getOrders');
            
            // Act
            scope.getOrders(1);
            
            // Assert
            expect(service.Arrive).toHaveBeenCalledWith('Franz', 'Kafka');
        });
    });
});

describe('orderController', function(){
var $scope, $location, $rootScope, createController;

beforeEach(inject(function($injector) {
    $location = $injector.get('$location');
    $rootScope = $injector.get('$rootScope');
    $scope = $rootScope.$new();
    var $controller = $injector.get('$controller');

    createController = function() {
        return $controller('orderController', {
            '$scope': $scope
        });
    };
}));
beforeEach(module(function($provide) {
    var service = { 
        getOrders: function (page, pageSize) {
            return {"data": [{"ableToCancel":true,"cabin":{"cabinType":"large","id":1,"name":"Fjordheim","nrActiveBookings":1},"dateFrom":1399586400000,"dateTo":1399759200000,"id":2,"nrOfBeds":"3","status":0,"timeOfBooking":1398328377255}],"totalItems":1}
        }
    };
    $provide.value('ordersService', service);
}));


it('should get some user orders and set the pagination variables accordinly', function() {
    
    spyOn(service, 'getOrders');
    var controller = createController();
    expect(service.getOrders).toHaveBeenCalledWith('0', '10');
    expect($scope.orders).not.toBe('undefined');
    
    
});
});

/*describe('orderController', function(){
	var $scope, $location, $rootScope,  $httpBackend, createController, ordersService;

    beforeEach(inject(function($injector) {
        $location = $injector.get('$location');
        $httpBackend = $injector.get('$httpBackend');
       
        $rootScope = $injector.get('$rootScope');
        ordersService =$injector.get('ordersService');
        spyOn(ordersService, 'getOrders').andCallThrough();
        $scope = $rootScope.$new();

        var $controller = $injector.get('$controller');

        createController = function() {
            return $controller('orderController', {
                '$scope': $scope
            });
        };
    }));
    
    afterEach(function() {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });
    
    it('should have currentPage set to 1 and totalItems set to 10 and itemsPerPage to 10', function() {
        var controller = createController();
        expect($scope.currentPage).toBe(1);
        expect($scope.totalItems).toBe(10);
        expect($scope.itemsPerPage).toBe(10);
    });
    
    it('should get some user orders and set the pagination variables accordinly', function() {
        var controller = createController();
        
        $httpBackend.expect('GET', '/api/bookings?page=0&size=10')
        .respond({{"data":[{"ableToCancel":true,"cabin":{"cabinType":"large","id":1,"name":"Fjordheim","nrActiveBookings":1},"dateFrom":1399586400000,"dateTo":1399759200000,"id":2,"nrOfBeds":"3","status":0,"timeOfBooking":1398328377255}],"totalItems":1}
            "success": true,
            "userOrders": 
        });
        $scope.$apply(function() {
            $scope.runTest();
        });
        expect($scope.orders).not.toBe('undefined');
        
        
    });
});*/
