'use strict';

//CONTROLLER
//Tests that the bookingController behaves as it should
describe('cabinDetailsController for C-TL28-Custom cabin price', function () {
	//unit test for bookingController
	 var scope,q, mockService, mockCabinService, $location, $rootScope, createController, routeParams, $http, $httpBackend;
	 beforeEach(module('dntAdminApp'));
    beforeEach(inject(function($injector) {
    	q = $injector.get('$q');
    	$http = $injector.get('$http');
    	$httpBackend = $injector.get('$httpBackend');
        $location = $injector.get('$location');
        $rootScope = $injector.get('$rootScope');
        
        mockService = $injector.get('bookingService');
        
        spyOn(mockService, 'adminCancelOrder').andCallThrough();
        
        
        mockCabinService = $injector.get('cabinService');
        spyOn(mockCabinService, 'getCabinDetails').andCallThrough();
        spyOn(mockCabinService, 'getPrices').andCallThrough();
        spyOn(mockCabinService, 'removePriceFromCabin').andCallThrough();
        spyOn(mockCabinService, 'addPriceFromCabin').andCallThrough();
        
        routeParams = {};
        scope = $rootScope.$new();
        
        var $controller = $injector.get('$controller');

        createController = function() {
            return $controller('cabinDetailsController', {
                '$scope': scope,
                'bookingService' : mockService,
                'cabinService' : mockCabinService,
                '$routeParams': routeParams,
                '$location' : $location
            });
        };
    }));
  
    afterEach(function() {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
      });

    it('Check initial state of variables', function () {
    
    	var controller = createController();
    	scope.$apply();
    	expect(scope.currentPage).toBe(1);
    	expect(scope.totalItems).toBe(0);
        expect(scope.itemsPerPage).toBe(10);
        expect(scope.error).toBe('');
        expect(scope.id).toBe(-1);
        expect(scope.priceCategories).toEqual({});
        //no id in routeParams so backend not called
    });
    
    it('should get current prices for cabin if id routeParams', function () {
    	routeParams.id = 1;
    	
    	var url2 = '/api/admin/cabins/1?page=0&size=10';
    	$httpBackend.when('GET', url2).respond(returnDetails);
    	$httpBackend.expectGET(url2);
    	var url = '/api/cabins/1/prices';
    	$httpBackend.when('GET', url).respond(returnData.guests);+
    	$httpBackend.expectGET(url);
    	var controller = createController();
    	$httpBackend.flush();
    	scope.$apply();
    	expect(scope.id).toBe(1);
    	expect(scope.cabinDetails).toBeDefined();
    	expect(scope.totalItems).toBe(1);
    	expect(scope.cabinDetails).toEqual(returnDetails.cabin);
    	scope.$apply();
    	expect(scope.priceCategories).toBeDefined();
    	expect(scope.priceCategories).toEqual(returnData.guests);
    	
    
  
    });
    
    it('should be able to remove a price from the table', function () {
    	routeParams.id = 1;
    	var cabinId = 1;
    	var priceId = 1;
    	
    	var url2 = '/api/admin/cabins/1?page=0&size=10';
    	$httpBackend.when('GET', url2).respond(returnDetails);
    	$httpBackend.expectGET(url2);
    	var url = '/api/cabins/1/prices';
    	$httpBackend.when('GET', url).respond(returnData.guests);+
    	$httpBackend.expectGET(url);
    	var controller = createController();
    	$httpBackend.flush();
    	scope.$apply();
    	
    	
    	expect(scope.cabinDetails).toEqual(returnDetails.cabin);
    	scope.$apply();
    	
    	expect(scope.priceCategories).toEqual(returnData.guests);
    	
    	//delete price 2.
    	
    	var url3 = '/api/cabins/'+cabinId+'/prices/' + priceId;
    	$httpBackend.when('DELETE', url3).respond();
    	$httpBackend.expectDELETE(url3);
    	scope.removePrice(cabinId, scope.priceCategories[priceId-1]);
    	$httpBackend.flush();
    	scope.$apply();
    	angular.forEach(scope.priceCategories, function(value){
    	       expect(value.id).not.toEqual(priceId);
    	     });
    	expect(mockCabinService.removePriceFromCabin).toHaveBeenCalledWith(cabinId, priceId);
    	expect(scope.priceCategories).toEqual([]);
    });
    
    it('should not remove price if no response from backend', function () {
    	routeParams.id = 1;
    	var cabinId = 1;
    	var priceId = 1;
    	
    	var url2 = '/api/admin/cabins/1?page=0&size=10';
    	$httpBackend.when('GET', url2).respond(returnDetails);
    	$httpBackend.expectGET(url2);
    	var url = '/api/cabins/1/prices';
    	$httpBackend.when('GET', url).respond(returnData.guests);+
    	$httpBackend.expectGET(url);
    	var controller = createController();
    	$httpBackend.flush();
    	scope.$apply();
    	
    	
    	expect(scope.cabinDetails).toEqual(returnDetails.cabin);
    	scope.$apply();
    	
    	expect(scope.priceCategories).toEqual(returnData.guests);
    	
    	//delete price 2.
    	
    	var url3 = '/api/cabins/'+cabinId+'/prices/' + priceId;
    	$httpBackend.when('DELETE', url3).respond(400, {message: "error"});
    	$httpBackend.expectDELETE(url3);
    	scope.removePrice(cabinId, scope.priceCategories[priceId-1]);
    	$httpBackend.flush();
    	scope.$apply();
    	var idFound = false;
    	angular.forEach(scope.priceCategories, function(value){
    	       if(value.id == priceId) {
    	    	   idFound = true;
    	       }
    	     });
    	expect(idFound).toBe(true);
    	expect(mockCabinService.removePriceFromCabin).toHaveBeenCalledWith(cabinId, priceId);
    	expect(scope.priceCategories[priceId-1].id).toEqual(priceId);
    });
    
    it('Check be able to replace a price row into the pice matrix of a small cabin', function () {
        
    	routeParams.id = 1;
    	var cabinId = 1;
    	
    	
    	var url2 = '/api/admin/cabins/1?page=0&size=10';
    	$httpBackend.when('GET', url2).respond(returnDetails);
    	$httpBackend.expectGET(url2);
    	var url = '/api/cabins/1/prices';
    	$httpBackend.when('GET', url).respond(returnData.guests);+
    	$httpBackend.expectGET(url);
    	var controller = createController();
    	$httpBackend.flush();
    	scope.$apply();
    	
    	
    	expect(scope.cabinDetails).toEqual(returnDetails.cabin);
    	scope.$apply();
    	
    	expect(scope.priceCategories).toEqual(returnData.guests);
    	
    	//delete price 2.
    	var newPriceId = 2;
    	var url3 = '/api/cabins/'+cabinId+'/prices';
    	$httpBackend.when('POST', url3).respond({id: newPriceId});
    	$httpBackend.expectPOST(url3);
    	var postPrice= {
                "memberPrice": 110,
                "nonMemberPrice": 120,
                "ageRange": "10-20",
                "guestType": "Test"
              };
    	scope.addPrice(cabinId, postPrice);
    	$httpBackend.flush();
    	scope.$apply();
    	var idFound = false;
    	angular.forEach(scope.priceCategories, function(value){
    	       if(value.id == newPriceId) {
    	    	   idFound = true;
    	       }
    	     });
    	expect(idFound).toBe(true);
    	expect(mockCabinService.addPriceFromCabin).toHaveBeenCalledWith(cabinId, postPrice);
    	expect(scope.priceCategories[0].id).toEqual(newPriceId);
    	expect(scope.priceCategories.length).toEqual(1);
    	
    });
    
    var returnDetails = {"cabin":{"cabinType":"small","id":1,"name":"Fjordheim","nrOfBeds":"none"},
    					"bookingList":{"data":[{"ableToCancel":true,
    											"dateFrom":"1400536800000",
    											"dateTo":"1401400800000",
    											"id":1,
    											"nrOfBeds":"none",
    											"status":1,
    											"timeOfBooking":1399729481912,
    											"user":{"admin":true,"creationDate":"1399685097434","emailAddress":"ola@gmail.com","fullName":"Ola Nordmann","id":40596,"nrOfBookings":1}}],
    					"totalItems":1}};
    
    var returnData = {"guests": [{
                                     "id": 1,
                                     "memberPrice": 1000,
                                     "nonMemberPrice": 1500,
                                     "ageRange": "-",
                                     "guestType": "Hele"
                                   }]};
    
  
});