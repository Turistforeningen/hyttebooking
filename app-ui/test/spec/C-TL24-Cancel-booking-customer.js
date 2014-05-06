'use strict';
//ORDERCONTROLLER - TESTING CANCELLING BOOKING REQ. REST OF TESTS FOR ORDERCONTROLLER IS IN C-TL38-Order-History
beforeEach(module('dntApp'));
describe('orderController', function(){
    var scope,mockService, $rootScope, createController, appStateService, routeParams, $http, $httpBackend;
    
    
    beforeEach(inject(function($injector) {
    	$http = $injector.get('$http');
    	$httpBackend = $injector.get('$httpBackend');
        $rootScope = $injector.get('$rootScope');
        	
        mockService = $injector.get('bookingService');
        spyOn(mockService, 'getOrders').andCallThrough();
        spyOn(mockService, 'cancelOrder').andCallThrough();
        spyOn(mockService, 'getOrderSummary').andCallThrough();
        
        routeParams = {};
        appStateService = $injector.get('appStateService');
        scope = $rootScope.$new();
        
        var $controller = $injector.get('$controller');

        createController = function() {
            return $controller('orderController', {
                '$scope': scope,
                'appStateService' : appStateService,
                'bookingService' : mockService,
                '$routeParams': routeParams
              
            });
        };
    }));
    
    afterEach(function() {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
      });
    
    it('should have these properties and init should call getOrders', function() {
    	var data = {};
    	$httpBackend.when('GET', '/api/bookings?page=0&size=10').respond(data);
    	$httpBackend.expectGET('/api/bookings?page=0&size=10');
    	var controller = createController();;
    	scope.$apply();
    	$httpBackend.flush();
    	
    	expect(scope.currentPage).toBeDefined();
    	expect(scope.totalItems).toBeDefined();
    	expect(scope.itemsPerPage).toBeDefined();
    	expect(scope.user).toBeDefined();
    	expect(scope.errorMessage).toBeDefined();
    	expect(scope.orders).toBeUndefined();
    	expect(typeof scope.currentPage).toBe('number');
    	expect(typeof scope.totalItems).toBe('number');
    	expect(typeof scope.itemsPerPage).toBe('number');
    	expect(typeof scope.errorMessage).toBe('string');
    	expect(typeof scope.user).toBe('object');
    	
    	expect(mockService.getOrders).toHaveBeenCalled();
    	expect(mockService.getOrders).toHaveBeenCalledWith(0, scope.itemsPerPage);
    	
 
    });
    
    it('should not be able to send request back end for cancellation if ableToCancel!=true', function() {
    	var data = {data: [{id:1, ableToCancel: true}, {id:2, ableToCancel: false}], totalItems: 2};
    	$httpBackend.when('GET', '/api/bookings?page=0&size=10').respond(data);
    	$httpBackend.expectGET('/api/bookings?page=0&size=10');
    	
    	
    	
    	var controller = createController();;
    	scope.$apply();
    
    	expect(mockService.getOrders).toHaveBeenCalled();
    	expect(mockService.getOrders).toHaveBeenCalledWith(0, scope.itemsPerPage);
    	
    	//booking not true for id:2
    	scope.cancelOrder(2);
    	scope.$apply();
    	$httpBackend.flush();
    	expect(mockService.cancelOrder).not.toHaveBeenCalled();
    });
   
    it('should be able to send request back end for cancellation if ableToCancel==true', function() {
    	var data = {data: [{id:1, ableToCancel: true}, {id:2, ableToCancel: false}], totalItems: 2};
    	$httpBackend.when('GET', '/api/bookings?page=0&size=10').respond(data);
    	$httpBackend.expectGET('/api/bookings?page=0&size=10');
    	
    	var controller = createController();;
    	scope.$apply();

    	
    	expect(mockService.getOrders).toHaveBeenCalled();
    	expect(mockService.getOrders).toHaveBeenCalledWith(0, scope.itemsPerPage);
    	
    	$httpBackend.when('DELETE', '/api/bookings/1').respond(data);
    	$httpBackend.expectDELETE('/api/bookings/1');
    	//booking true for id:1
    	scope.cancelOrder(1);
    	scope.$apply();
    	$httpBackend.flush();
    	expect(mockService.cancelOrder).not.toHaveBeenCalled();
    });
});