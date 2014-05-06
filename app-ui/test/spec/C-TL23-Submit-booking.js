'use strict';

//CONTROLLER

describe('bookingController', function () {
	//unit test for bookingController
	 var scope,q, mockService, $location, $rootScope, createController, routeParams, $http, $httpBackend;
    
    beforeEach(inject(function($injector) {
    	q = $injector.get('$q');
    	$http = $injector.get('$http');
    	$httpBackend = $injector.get('$httpBackend');
        $location = $injector.get('$location');
        $rootScope = $injector.get('$rootScope');
        
        mockService = $injector.get('bookingService');
        spyOn(mockService, 'getAvailability').andCallThrough();
        spyOn(mockService, 'authenticatePayment').andCallThrough();
        spyOn(mockService, 'startPayment').andCallThrough();
        spyOn(mockService, 'getPrices').andCallThrough();
        spyOn(mockService, 'postOrder').andCallThrough();
        
        routeParams = {};
        scope = $rootScope.$new();
        
        var $controller = $injector.get('$controller');

        createController = function() {
            return $controller('bookingController', {
                '$scope': scope,
                'bookingService' : mockService,
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
        // Before $apply is called the promise hasn't resolved
    	var controller = createController();
    	scope.$apply();
    	expect(scope.beds).toBe(0);
        //init does not contain any routeparams
        expect(scope.booking).toEqual({});
        expect(scope.errorMessage).toBe('');
    });
    
    it('should init controller with variables for beds and cabinid if routeParams is set',  function () {
        // Before $apply is called the promise hasn't resolved
    	routeParams.id = 1;
    	routeParams.type = 'large';
    	routeParams.beds = 20;
    	var url = '/api/cabins/' + routeParams.id + '/prices';
    	$httpBackend.when('GET', url).respond(returnData);
    	$httpBackend.expectGET(url);
    	
    	var controller = createController();
    	scope.$apply();
    	$httpBackend.flush();
    	expect(scope.booking).not.toBe({});
    	expect(mockService.getPrices).toHaveBeenCalled();
    	expect(mockService.getPrices).toHaveBeenCalledWith(1);
    	expect(scope.beds).toBe(20);
    	expect(mockService.authenticatePayment).not.toHaveBeenCalled();
    	
    });
    
    it('should not allow booking if routeParams is wrong or missing', function() {
        // Before $apply is called the promise hasn't resolved
    	routeParams.type = 'large';
    	routeParams.beds = 20;
    	
    	var controller = createController();
    	scope.$apply();
    	expect(scope.validState).toBe(false); //fail
    });
    
    it('error message should be put into scope.errorMessage', function() {
        // Before $apply is called the promise hasn't resolved
    	routeParams.id = -1;
    	routeParams.type = 'large';
    	routeParams.beds = 20;
    	
    	var url = '/api/cabins/' + routeParams.id + '/prices';
    	$httpBackend.when('GET', url).respond(404, {'message': 'Not found'});
    	$httpBackend.expectGET(url);
    	
    	expect(scope.errorMessage).toBeUndefined();
    	var controller = createController();
    	scope.$apply();
    	$httpBackend.flush();
    	//if server cant process price request because of invalid parameters etc, errorMessage should be set
    	expect(scope.errorMessage.length > 0).toBe(true);
    	expect(mockService.getPrices).toHaveBeenCalled();
    	//tests to see if bookingModule is available or not
    });
    
    it('should not post booking via postBooking unless booking contains datefrom and dateTo', function($controller) {
    	;
    	var controller = createController();
    	expect(scope.errorMessage).toBe('')
    	scope.$apply();
    	var booking = {};
    	scope.postBooking(booking);
    	expect(mockService.postOrder).not.toHaveBeenCalled();
    	expect(scope.errorMessage).not.toBeUndefined;
    	
    });
    
    it('should not open booking confirm dialog unless booking contains at least one person, datefrom, dateTo', function($controller) {
 
    	var controller = createController();
       	spyOn(scope, 'openBookingConfirmDialog').andCallThrough();
    	spyOn(scope, 'postBooking').andCallThrough();
    	spyOn(scope, 'openDialog').andCallThrough();
    	//assume the bookingController is in a valid state
    	scope.validState = true;
    	expect(scope.errorMessage).toBe('');
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
    	expect(scope.errorMessage).toEqual("Du må velge minst en person for å kunne reservere.");
    });
    
    it('should let user post booking if dateFrom, dateTo and date', function($controller) {
    	var resultId = -1;
    	//Mock scope.pay function, and test if method parameter is correct
    	var returnData = {'id' :20};
    	var url = '/api/bookings/';
    	$httpBackend.when('POST', url).respond(returnData);
    	$httpBackend.expectPOST(url);
    	var controller = createController();
    	spyOn(scope, "pay").andCallFake(function(id) {
    	     resultId = id; 
    	});
    	expect(scope.errorMessage).toBe('');
    	//assume bookingController is in a valid state
    	scope.validState = true;
    	scope.$apply();
    	
    	scope.booking = {"cabinId":1, "dateFrom" : 2030404, "dateTo" : 2030404, "guests" : [{"nr": 1}, {"nr" : 2}]};
    	scope.$apply();
    	expect(scope.booking.guests).not.toBeUndefined();
    	scope.postBooking(scope.booking);
    	scope.$apply();
    	$httpBackend.flush();
    	expect(mockService.postOrder).toHaveBeenCalled();
    	expect(mockService.postOrder).toHaveBeenCalledWith(scope.booking);
    	expect(scope.errorMessage).toBe('');
    	expect(resultId).toBe(20);
    	
    });
    
    
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
    
    var availData = [0,4,4,0,0,0,0,10,20,20,20,20,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];
});






//DNT SELECTOR

describe('dntSelector', function () {


    var scope, elm, $body = $('body');

    //Inject rootscope and comile and use them to set up directive for testing
    beforeEach(inject(function ($rootScope, $compile) {
        scope = $rootScope.$new();
        scope.beds = 10;
        scope.guests = [{type: "ungdom", nr: 0, price:300}, {type: "barn", nr: 0, price:500}];
        scope.hider = 1;
       
    }));
    
    function compileDirective(tpl) {
        if (!tpl) tpl = '<dnt-selector category-model="guests" divider-index="hider" number-of-beds="beds"></dnt-selector>';
    
        // inject allows you to use AngularJS dependency injection
        // to retrieve and use other services
        var element = angular.element(tpl);
        inject(function($compile) {
            elm = $compile(element)(scope);

        });
        $body.append(elm);
        // $digest is necessary to finalize the directive generation
        scope.$digest();
    }
    
    describe('initialisation', function() {
        // before each test in this block, generates a fresh directive
        beforeEach(function() {
            compileDirective();
        });
        // a single test example, check the produced DOM
        it('should produce 2 drop down and be collapsed', function() {
        	var dirScope = elm.isolateScope()
        	expect(elm.hasClass("selectNumber")).toBeTruthy;
        	expect(elm.find('select').length).toEqual(2);
        	//each drop down contains 11 options. Check html for this
        	expect(elm.find('option').length).toEqual(11*2);
        	expect(dirScope.isCollapsed).toBe(true);
        	expect(dirScope.categories).toBe(scope.guests);
        	
        });
        
        it('should produce correct inital beds left and options ranges used', function() {
        	var dirScope = elm.isolateScope();
        	expect(dirScope.bedsLeft()).toBe(10);
        	expect(dirScope.range(0).length).toBe(11);
        });
    });
    
    describe('range construction', function() {
        // before each test in this block, generates a fresh directive
        beforeEach(function() {
            compileDirective();
        });
       //all tests below only checks correct behavior for controller.
        //view and controller together will be tested using E2E tests
        it('watch should detect changes nr of the person types json', function() {
        	var dirScope = elm.isolateScope()
        	spyOn(dirScope, 'constructRange').andCallThrough();
        	expect(dirScope.constructRange).not.toHaveBeenCalled();
        	dirScope.categories[0].nr = 1;
        	console.log(dirScope.person);
        	scope.$digest();
        	
        	expect(dirScope.constructRange).toHaveBeenCalled();
        	expect(dirScope.range(1)).not.toBeUndefined();
        	expect(dirScope.range(1).length).toBe(11);
        	expect(dirScope.range(0).length).toBe(10);
        	expect(dirScope.range(2)).toBeUndefined();
        });
        
        it('should return no other option than zero or already selected value if cabin maxed out', function() {
        	var dirScope = elm.isolateScope()
        	spyOn(dirScope, 'constructRange').andCallThrough();
        	//maxed out first category
        	dirScope.categories[0].nr = 10;
        	scope.$digest();
        	expect(dirScope.constructRange).toHaveBeenCalled();
        	//for all categories with nr set to 0 should only display
        	//0 beds in drop down
        	expect(dirScope.range(0).length).toBe(1);
        	expect(dirScope.range(10).length).toBe(11);
        });
        
        it('should return an array from 0 - x value', function() {
        	var dirScope = elm.isolateScope()
        	var randNrSelected = Math.floor((Math.random()*10)+1);
        	console.log(randNrSelected + " ,random number selected for test")
        	dirScope.categories[0].nr = randNrSelected;
        	scope.$digest();
        	var bedsLeft = 10-randNrSelected;
        	var range = dirScope.range(0);
        	for (var i= 0; i<=bedsLeft; i++) {
        		expect(range[i]).toBe(i);
        	}
        	var range2 = dirScope.range(randNrSelected);
        	for (var i = 0; i<=10; i++) {
        		expect(range2[i]).toBe(i);
        	}
        });
        
    });
});