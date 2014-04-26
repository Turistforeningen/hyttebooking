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


'use strict';

describe('orderController', function () {


    var scope, fakeFactory, controller, q, deferred, routeParams;

    //Prepare the fake factory
    beforeEach(function () {
        mockService = {
        	getOrders: function (page, pageSize) {
                deferred = q.defer();
                // Place the fake return object here
                var returnData = {"data": [{"ableToCancel":true,"cabin":{"cabinType":"large","id":1,"name":"Fjordheim","nrActiveBookings":1},"dateFrom":1399586400000,"dateTo":1399759200000,"id":2,"nrOfBeds":"3","status":0,"timeOfBooking":1398328377255}],"totalItems":1};
                deferred.resolve(returnData);
                return deferred.promise;
            }
        };
        spyOn(mockService, 'getOrders').andCallThrough();
    });

    //Inject fake factory into controller
    beforeEach(inject(function ($rootScope, $controller, $q, $routeParams) {
        scope = $rootScope.$new();
        q = $q;
        routeParams = $routeParams
        controller = $controller('orderController', { $scope: scope, $routeParams: routeParams, bookingService: mockService });
    }));

    it('The order list object is not defined yet', function () {
        // Before $apply is called the promise hasn't resolved
    	expect(scope.itemsPerPage).toBe(10);
        expect(scope.orders).not.toBeDefined();
    });

    it('Applying the scope causes it to be defined', function () {
        // This propagates the changes to the models
        // This happens itself when you're on a web page, but not in a unit test framework
        scope.$apply();
        expect(scope.orders).toBeDefined();
    });

    it('Ensure that the method was invoked', function () {
        scope.$apply();
        expect(mockService.getOrders).toHaveBeenCalled();
    });

    it('Check the value returned', function () {
        scope.$apply();
        var compareData = [{"ableToCancel":true,"cabin":{"cabinType":"large","id":1,"name":"Fjordheim","nrActiveBookings":1},"dateFrom":1399586400000,"dateTo":1399759200000,"id":2,"nrOfBeds":"3","status":0,"timeOfBooking":1398328377255}];
        expect(scope.orders).toEqual(compareData);
        expect(scope.totalItems).toBe(1);
        expect(scope.orders[0].ableToCancel).toBe(true);
    });
    
    it('setPage should set page variable to x-1 of all input parameters (since bootstrap ui pagination)', function() {
    	
    	scope.setPage(4);
    	scope.$apply();
    	//server pages zero indexed 4-1 = 3
    	expect(mockService.getOrders).toHaveBeenCalledWith(3, 10);
    	scope.setPage(-5);
    	scope.$apply();
    	expect(mockService.getOrders).not.toHaveBeenCalledWith(-6, 10);
    	//currentPage contain the paginations current page.
    	expect(scope.currentPage).toBe(4);
    })
});

describe('bookingController', function () {
	//unit test for bookingController
	var scope, fakeFactory, controller, q, deferred, routeparams;

	//Prepare the fake factory
    beforeEach(function () {
        mockService = {
        	getAvailability: function (cabinId, startDate, endDate) {
                deferred = q.defer();
                // Place the fake return object here
                var returnData = [0,4,4,0,0,0,0,10,20,20,20,20,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];
                deferred.resolve(returnData);
                return deferred.promise;
            },
	        authenticatePayment: function (paymentId, response) {
	            deferred = q.defer();
	            // Place the fake return object here
	            var returnData = {};
	            deferred.resolve(returnData);
	            return deferred.promise;
	        },
	        startPayment: function (bookingId) {
	            deferred = q.defer();
	            // Place the fake return object here
	            var returnData = {};
	            deferred.resolve(returnData);
	            return deferred.promise;
	        },
	        getPrices: function (id) {
	            deferred = q.defer();
	            // Place the fake return object here
	            var returnData = {"guests": [
	                                        {
	                                            "nr": 0,
	                                            "price": 300,
	                                            "type": "Voksen, medlem"
	                                          },
	                                          {
	                                            "nr": 0,
	                                            "price": 150,
	                                            "type": "Ungdom, medlem"
	                                          },
	                                          {
	                                            "nr": 0,
	                                            "price": 100,
	                                            "type": "Barn, medlem"
	                                          },
	                                          {
	                                            "nr": 0,
	                                            "price": 0,
	                                            "type": "Spedbarn"
	                                          },
	                                          {
	                                            "nr": 0,
	                                            "price": 400,
	                                            "type": "Voksen"
	                                          },
	                                          {
	                                            "nr": 0,
	                                            "price": 200,
	                                            "type": "ungdom"
	                                          },
	                                          {
	                                            "nr": 0,
	                                            "price": 150,
	                                            "type": "barn"
	                                          }]};
	            if(id>0) {
	            	deferred.resolve(returnData);
	            }
	            else {
	            	deferred.reject({message: "not working"});
	            }
	            return deferred.promise;
	        },
	        postOrder: function (booking) {
	            deferred = q.defer();
	            // Place the fake return object here
	            var returnData = {'id' :20};
	            deferred.resolve(returnData);
	            return deferred.promise;
	        }
        };
        spyOn(mockService, 'getAvailability').andCallThrough();
        spyOn(mockService, 'authenticatePayment').andCallThrough();
        spyOn(mockService, 'startPayment').andCallThrough();
        spyOn(mockService, 'getPrices').andCallThrough();
        spyOn(mockService, 'postOrder').andCallThrough();
        
    });

    //Inject fake factory into controller
    beforeEach(inject(function ($rootScope, $controller, $q, $routeParams) {
        scope = $rootScope.$new();
        q = $q;
        controller = $controller('bookingController', { $scope: scope, bookingService: mockService });
        routeParams = {};
    }));

    it('Check initial state of variables', function () {
        // Before $apply is called the promise hasn't resolved
    	scope.$apply();
    	expect(scope.beds).toBe(0);
        //init does not contain any routeparams
        expect(scope.booking).toEqual({});
        expect(scope.errorMessage).toBeUndefined();
    });
    
    it('should init controller with variables for beds and cabinid if routeParams is set', inject(function($controller) {
        // Before $apply is called the promise hasn't resolved
    	routeParams.id = 1;
    	routeParams.type = 'large';
    	routeParams.beds = 20;
    	
    	controller = $controller('bookingController', { $scope: scope, $routeParams : routeParams, bookingService: mockService });
    	scope.$apply();
    	expect(scope.booking).not.toBe({});
    	expect(mockService.getPrices).toHaveBeenCalled();
    	expect(mockService.getPrices).toHaveBeenCalledWith(1);
    	expect(scope.beds).toBe(20);
    	expect(mockService.authenticatePayment).not.toHaveBeenCalled();
    }));
    
    it('should not allow booking if routeParams is wrong or missing', inject(function($controller) {
        // Before $apply is called the promise hasn't resolved
    	routeParams.type = 'large';
    	routeParams.beds = 20;
    	
    	controller = $controller('bookingController', { $scope: scope, $routeParams : routeParams, bookingService: mockService });
    	scope.$apply();
    	expect(scope.validState).toBe(false); //fail
    }));
    
    it('error message should be put into scope.errorMessage', inject(function($controller) {
        // Before $apply is called the promise hasn't resolved
    	routeParams.id = -1;
    	routeParams.type = 'large';
    	routeParams.beds = 20;
    	
    	expect(scope.errorMessage).toBeUndefined();
    	controller = $controller('bookingController', { $scope: scope, $routeParams : routeParams, bookingService: mockService });
    	scope.$apply();
    	//if server cant process price request because of invalid parameters etc, errorMessage should be set
    	expect(scope.errorMessage).toBe("not working");
    	expect(mockService.getPrices).toHaveBeenCalled();
    	//tests to see if bookingModule is available or not
    }));
    
    it('should not post booking via postBooking unless booking contains datefrom and dateTo', function($controller) {
    	expect(scope.errorMessage).toBeUndefined();
    	scope.$apply();
    	var booking = {};
    	scope.postBooking(booking);
    	expect(mockService.postOrder).not.toHaveBeenCalled();
    	expect(scope.errorMessage).not.toBeUndefined;
    	
    });
    
    it('should not open booking confirm dialog unless booking contains at least one person, datefrom, dateTo', function($controller) {
    	spyOn(scope, 'openBookingConfirmDialog').andCallThrough();
    	spyOn(scope, 'postBooking').andCallThrough();
    	spyOn(scope, 'openDialog').andCallThrough();
    	//assume the bookingController is in a valid state
    	scope.validState = true;
    	expect(scope.errorMessage).toBeUndefined();
    	scope.$apply();
    	scope.booking = {"cabinId":1, "dateFrom" : 2030404, "dateTo" : 2030404};
    	scope.$apply();
    	expect(scope.booking.guests).toBeUndefined();
    	scope.openBookingConfirmDialog();
    	scope.$apply();
    	expect(scope.openBookingConfirmDialog).toHaveBeenCalled();
    	expect(scope.booking).toEqual({"cabinId":1,"dateFrom" : 2030404, "dateTo" : 2030404});
    	expect(mockService.postOrder).not.toHaveBeenCalled();
    	expect(scope.postBooking).not.toHaveBeenCalled();
    	expect(scope.errorMessage).not.toBeUndefined;
    	scope.$apply();
    	expect(scope.errorMessage).toEqual("du må velge minst en person for å kunne reservere");
    });
    
    it('should let user post booking if dateFrom, dateTo and date', function($controller) {
    	var resultId = -1;
    	//Mock scope.pay function, and test if method parameter is correct
    	spyOn(scope, "pay").andCallFake(function(bookingId) {
    	     resultId = bookingId; 
    	});
    	expect(scope.errorMessage).toBeUndefined();
    	//assume bookingController is in a valid state
    	scope.validState = true;
    	scope.$apply();
    	scope.booking = {"cabinId":1, "dateFrom" : 2030404, "dateTo" : 2030404, "guests" : [{"nr": 1}, {"nr" : 2}]};
    	scope.$apply();
    	expect(scope.booking.guests).not.toBeUndefined();
    	scope.postBooking(scope.booking);
    	scope.$apply();
    	expect(mockService.postOrder).toHaveBeenCalled();
    	expect(mockService.postOrder).toHaveBeenCalledWith(scope.booking);
    	expect(scope.errorMessage).toBeUndefined;
    	expect(resultId).toBe(20);
    	
    });
    
});

