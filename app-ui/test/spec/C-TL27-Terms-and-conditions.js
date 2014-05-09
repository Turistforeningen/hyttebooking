'use strict';
//ORDERCONTROLLER - TESTING CANCELLING BOOKING REQ. REST OF TESTS FOR ORDERCONTROLLER IS IN C-TL38-Order-History
beforeEach(module('dntApp'));
describe('bookingController for CTL27-TAC', function(){
    var scope,mockService, $rootScope, createController, appStateService, routeParams, $http, $httpBackend, $q;
    
    
    beforeEach(inject(function($injector) {
    	$http = $injector.get('$http');
    	$httpBackend = $injector.get('$httpBackend');
    	$q = $injector.get('$q');
        $rootScope = $injector.get('$rootScope');
        	
        mockService = $injector.get('bookingService');
        spyOn(mockService, 'postOrder').andCallThrough();
        spyOn(mockService, 'authenticatePayment').andCallThrough();
        
        routeParams = {};
        appStateService = $injector.get('appStateService');
        scope = $rootScope.$new();
        
        var $controller = $injector.get('$controller');

        createController = function() {
            return $controller('bookingController', {
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
    
    it('should have these properties and init', function() {
    	var data = {};
    	routeParams.id = 1;
    	routeParams.type = 'small';
    	$httpBackend.when('GET', '/api/cabins/1/prices').respond(data);
    	$httpBackend.expectGET('/api/cabins/1/prices');
    	var controller = createController();
    	scope.$apply();
    	$httpBackend.flush();
    	expect(scope.validState).toBeDefined();
    	expect(scope.errorMessage).toBeDefined();
    	expect(scope.booking).toBeDefined();
    	expect(scope.beds).toBeDefined();
    	expect(scope.hideIndex).toBeDefined();
    	expect(typeof scope.validState).toBe('boolean');
    	expect(typeof scope.errorMessage).toBe('string');
    	expect(typeof scope.booking).toBe('object');
    	expect(typeof scope.beds).toBe('number');
    	expect(typeof scope.hideIndex).toBe('number');
    	
    	
    	
 
    });
    
    it('should not post a booking if terms and conditions has not been checked', function() {
    	var data = {status: "ok"};
    	routeParams.id = 1;
    	routeParams.type = 'small';
    	$httpBackend.when('GET', '/api/cabins/1/prices').respond(data);
    	$httpBackend.expectGET('/api/cabins/1/prices');
    	var controller = createController();
    	
    	scope.$apply();
    	scope.validState = true;
    	var booking = {dateFrom: "xxx", dateTo: "xxx", termsAndConditions: false};
    	scope.postBooking(booking);
    	scope.$apply;
    	
    	$httpBackend.flush();
    	expect(mockService.postOrder).not.toHaveBeenCalled();
    });
   
   // $httpBackend.when('POST', '/api/bookings').respond(data);
//	$httpBackend.expectPOST('/api/bookings');
 
});