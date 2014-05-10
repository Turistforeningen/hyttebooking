'use strict';

//CONTROLLER
//Tests that the bookingController behaves as it should
describe('cabinDetailsController for C-TL34-Cancel booking admin', function () {
	//unit test for bookingController
	 var scope,q, mockService, mockCabinService, $location, $rootScope, createController, routeParams, $http, $httpBackend;
	 beforeEach(module('dntAdminApp', 'templates'));
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
    
    it('should not be able to send request back end for cancellation if ableToCancel!=true', function() {
    	routeParams.id = 1;
    	var cabinId = 1;
    	var priceId = 2;
    	
    	var url2 = '/api/admin/cabins/1?page=0&size=10';
    	$httpBackend.when('GET', url2).respond(returnDetails);
    	$httpBackend.expectGET(url2);
    	var url = '/api/cabins/1/prices';
    	$httpBackend.when('GET', url).respond(returnData.guests);+
    	$httpBackend.expectGET(url);
    	var controller = createController();
    	$httpBackend.flush();
    	scope.$apply();
    	scope.cabinBookings[0].ableToCancel = false;
    	expect(mockCabinService.getCabinDetails).toHaveBeenCalled();
    	
    	//booking not true for id:2
    	scope.cancelOrder(scope.cabinBookings[0]);
    	scope.$apply();
    	expect(mockService.adminCancelOrder).not.toHaveBeenCalled();
    });
   
    it('should be able to send request back end for cancellation if ableToCancel==true', function() {
    	
    	var fakeModal = {
			    result: {
			        then: function(confirmCallback, cancelCallback) {
			            //Store the callbacks for later when the user clicks on the OK or Cancel button of the dialog
			            this.confirmCallBack = confirmCallback;
			            this.cancelCallback = cancelCallback;
			        }
			    },
			    close: function( item ) {
			        //The user clicked OK on the modal dialog, call the stored confirm callback with the selected item
			        this.result.confirmCallBack( item );
			    },
			    dismiss: function( type ) {
			        //The user clicked cancel on the modal dialog, call the stored cancel callback
			        this.result.cancelCallback( type );
			    }
			};
    	
    	routeParams.id = 1;
    	var cabinId = 1;
    	var priceId = 2;
    	
    	var url2 = '/api/admin/cabins/1?page=0&size=10';
    	$httpBackend.when('GET', url2).respond(returnDetails);
    	$httpBackend.expectGET(url2);
    	var url = '/api/cabins/1/prices';
    	$httpBackend.when('GET', url).respond(returnData.guests);+
    	$httpBackend.expectGET(url);
    	var controller = createController();
    	$httpBackend.flush();
    	scope.$apply();
    	spyOn(scope, 'openDialog').andCallFake(function(url, data) {
    		
    		return fakeModal;
    	});
    	
    	scope.$apply();
    	expect(mockCabinService.getCabinDetails).toHaveBeenCalled();
    
    	
    	
    	//booking true for id:1
    	var url3 = '/api/admin/bookings/1';
    	$httpBackend.when('DELETE', url3).respond();
    	$httpBackend.expectDELETE(url3);
    	scope.cancelOrder(scope.cabinBookings[0]);
    	scope.$apply();
    	//user press ok on dialog.
    	fakeModal.close();
    	scope.$apply();
    	$httpBackend.flush();
    	var cancelledStatus = 2;
    	scope.$apply();
    	expect(scope.cabinBookings[0].status).toBe(cancelledStatus);
    	expect(mockService.adminCancelOrder).toHaveBeenCalled();
    	expect(scope.error.length>0).not.toBeTruthy
    	
    	
    });
   
    
  
    
  
    var returnDetails = {"cabin":{"cabinType":"large","id":1,"name":"Fjordheim","nrOfBeds":"10"},
    					"bookingList":{"data":[{"ableToCancel":true,
    											"dateFrom":"1400536800000",
    											"dateTo":"1401400800000",
    											"id":1,
    											"nrOfBeds":"10",
    											"status":1,
    											"timeOfBooking":1399729481912,
    											"user":{"admin":true,"creationDate":"1399685097434","emailAddress":"ola@gmail.com","fullName":"ola nordmann","id":4059,"nrOfBookings":1}}],
    					"totalItems":1}};
    
    var returnData = {"guests": [{
                                     "id": 1,
                                     "memberPrice": 300,
                                     "nonMemberPrice": 400,
                                     "ageRange": "10-20",
                                     "guestType": "Voksen"
                                   },{
                                	   "id": 2,
                                       "memberPrice": 200,
                                       "nonMemberPrice": 300,
                                       "ageRange": "10-20",
                                       "guestType": "Ungdom"
                                   },{
                                	   "id": 3,
                                       "memberPrice": 100,
                                       "nonMemberPrice": 200,
                                       "ageRange": "10-20",
                                       "guestType": "Barn"
                                   },{
                                	   "id": 4,
                                       "memberPrice": 0,
                                       "nonMemberPrice": 0,
                                       "ageRange": "10-20",
                                       "guestType": "Spedbarn"
                                   }]};
    
  
});