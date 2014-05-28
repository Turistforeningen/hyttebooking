'use strict';

//CONTROLLER
//Tests that the bookingController behaves as it should
describe('bookingController for C-TL23-Submit', function () {
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
    	expect(scope.errorMessage.length>0).toBe(true);
    	
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
    	routeParams.id = 1;
    	routeParams.beds = 3;
    	routeParams.type = 'large';
    	//Mock scope.pay function, and test if method parameter is correct
    	var returnData = {'id' :20};
    	var bookingData = {cabinId:1, dateFrom : 2030404, dateTo : 2030404, guests : [{id: 1, nr: 1}, {id: 1,nr : 2}], termsAndConditions : true};
    	var url2 = '/api/cabins/' + routeParams.id + '/prices';
    	$httpBackend.when('GET', url2).respond({});
    	$httpBackend.expectGET(url2);
    	
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
    	
    	

    	scope.booking = bookingData;
    	scope.$apply();
    	scope.postBooking(bookingData);
    	scope.$apply();
    	$httpBackend.flush();
    	expect(mockService.postOrder).toHaveBeenCalled();
    	expect(mockService.postOrder).toHaveBeenCalledWith({cabinId:1, dateFrom : 2030404, dateTo : 2030404, guests : [{id: 1, nr: 1}, {id: 1,nr : 2}], termsAndConditions : true});
    	expect(scope.errorMessage).toBe('');
    	expect(resultId).toBe(20);
    	
    });
    
    it('should let user post booking if user somehow has selected more beds than available', function($controller) {
    	var resultId = -1;
    	routeParams.id = 1;
    	routeParams.beds = 2;
    	routeParams.type = 'large';
    	//Mock scope.pay function, and test if method parameter is correct
    	var returnData = {'id' :20};
    	var bookingData = {cabinId:1, dateFrom : 2030404, dateTo : 2030404, guests : [{id: 1, nr: 1}, {id: 1,nr : 2}], termsAndConditions : true};
    	var url2 = '/api/cabins/' + routeParams.id + '/prices';
    	$httpBackend.when('GET', url2).respond({});
    	$httpBackend.expectGET(url2);
    	
    	var controller = createController();
    	expect(scope.errorMessage).toBe('');
    	//assume bookingController is in a valid state
    	scope.validState = true;
    	scope.$apply();
    	
    	
 
    	scope.booking = bookingData;
    	scope.$apply();
    	scope.postBooking(bookingData);
    	scope.$apply();
    	$httpBackend.flush();
    	expect(mockService.postOrder).not.toHaveBeenCalled();
    	
    	expect(scope.errorMessage.length>0).toBe(true);
    	
    });
    
    it('should remove unused price categories before posting booking', function($controller) {
    	var resultId = -1;
    	routeParams.id = 1;
    	routeParams.beds = 1;
    	routeParams.type = 'large';

    	var url = '/api/cabins/' + routeParams.id + '/prices';
    	$httpBackend.when('GET', url).respond(returnData.guests);
    	$httpBackend.expectGET(url);
    	var controller = createController();
    	$httpBackend.flush();
    	scope.$apply();
    	
    	var nrInThisCategory = 1;
    	var length = scope.booking.guests.length;
    	//testing categories at random. All but two should be removed
    	var randNrSelected = Math.floor((Math.random()*length));
    	scope.booking.guests[randNrSelected].nr = nrInThisCategory;
    	var randNrSelected2 = Math.floor((Math.random()*length));
    	while(randNrSelected ==randNrSelected2) {
    		randNrSelected2 = Math.floor((Math.random()*length));
    	}
    	scope.booking.guests[randNrSelected2].nr = nrInThisCategory;
    	expect(scope.booking.guests.length > 2).toBe(true);
    	var g = scope.removeUnpickedPriceCategories(scope.booking.guests);
    	expect(g.length == 2).toBe(true);
    	expect(g[0].nr).toBe(nrInThisCategory);
    	expect(g[1].nr).toBe(nrInThisCategory);
    });
    
    it('should process a retrieved pricematrix into price categories', function($controller) {
    	
    	var controller = createController();
    	var priceMatrix = returnData.guests;
    	var guestCategories = scope.processPriceMatrix(priceMatrix);
    	
    	//method makes a member and a non member guest categories except for instances where
    	//non member and member prices are the same
    	expect(guestCategories.length == (priceMatrix.length *2) -1);
    	angular.forEach(guestCategories, function(value){
    		expect(value.nr).toEqual(0);
    		expect(value.id).toBeDefined();
    		expect(value.price).toBeDefined();
    		expect(value.ageRange).toBeDefined();
    		expect(value.guestType.length>0).toBe(true);
    	});
    });
    
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












//DNT SELECTOR
//tests for guest selector

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











//DNT PRICE VIEWER - show order summary, what guests the booking is composed of and total price.
describe('dntPriceViewer', function () {


    var scope, elm, $body = $('body');

    //Inject rootscope and comile and use them to set up directive for testing
    beforeEach(inject(function ($rootScope, $compile) {
        scope = $rootScope.$new();
        scope.guests = [{guestType: "ungdom", nr: 0, price:300}, {guestType: "barn", nr: 0, price:500}];
        scope.fromDate = new Date();
        scope.toDate = new Date();
    }));
    
    function compileDirective(tpl) {
        if (!tpl) tpl = '<dnt-price-viewer category-model="guests" from-date="fromDate" to-date="toDate"></dnt-price-viewer>';
    
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
        it('should produce a table with two entries', function() {
        	var dirScope = elm.isolateScope();
        	expect(elm.hasClass("prices")).toBeTruthy;
        	expect(elm.find('tr').length>0).toEqual(true);
        });
        
        it('should have total price set to zero', function() {
        	var dirScope = elm.isolateScope();
        	expect(dirScope.price).toBeDefined();
        	expect(dirScope.price).toEqual(0);
        });

    });
    
    describe('watching for changes for dates and guest composition', function() {

        beforeEach(function() {
            compileDirective();
        });
      
        it('should add entry in table if price category is added', function() {
        	var dirScope = elm.isolateScope();
        	var entries = elm.find('tr');
        	expect(entries.eq(0).hasClass('ng-hide')).toBe(true);
        	expect(entries.eq(1).hasClass('ng-hide')).toBe(true);
        	expect(entries.eq(2).hasClass('ng-hide')).toBe(false);
        	dirScope.categories[0].nr = 1;
        	dirScope.$apply();
        	expect(entries.eq(0).hasClass('ng-hide')).toBe(false);
        	//when class does not have ng-hide it is not hidden anymore
        });
        
        it('should remove entry from table when price category is removed', function() {
        	var dirScope = elm.isolateScope();
        	dirScope.categories[0].nr = 1;
        	dirScope.$apply();
        	var entries = elm.find('tr');
        	expect(entries.eq(0).hasClass('ng-hide')).toBe(false);
        	expect(entries.eq(1).hasClass('ng-hide')).toBe(true);
        	expect(entries.eq(2).hasClass('ng-hide')).toBe(false);
        	dirScope.categories[0].nr = 0;
        	dirScope.$apply();
        	expect(entries.eq(0).hasClass('ng-hide')).toBe(true);
        	//when class does not have ng-hide it is not hidden anymore
        });
        
        it('should change total price if guest added or removed', function() {
        	var dirScope = elm.isolateScope();
        	//0 NOK 
        	var totalAmountDisplayed = elm.find('tr').eq(2).find('td').eq(1).text();
        	dirScope.categories[0].nr = 1;
        	dirScope.toDate.setDate(dirScope.toDate.getDate() + 2);
        	dirScope.newDateRange();
        	dirScope.$apply();
        	expect(elm.find('tr').eq(2).find('td').eq(1).text()).not.toEqual(totalAmountDisplayed);
        	//300 per youth for 2 days
        	expect(dirScope.price).toBe(600);
        	dirScope.categories[0].nr = 0;
        	dirScope.$apply();
        	expect(elm.find('tr').eq(2).find('td').eq(1).text()).toEqual(totalAmountDisplayed);
        	expect(dirScope.price).toBe(0);
        });
    });
    
    describe('internal controller', function() {

        beforeEach(function() {
            compileDirective();
        });
      
        it('should calculate new price correctly', function() {
        	var dirScope = elm.isolateScope();
        	var unitPrice = 300;
        	dirScope.toDate.setDate(dirScope.toDate.getDate() + 1);
        	dirScope.newDateRange();
        	dirScope.$apply();
        	expect(dirScope.price).toBe(0);
        	dirScope.categories[0].nr = 1;
        	//300 per youth for 2 days
        	dirScope.$apply();
        	expect(dirScope.price).toBe(unitPrice);
        	dirScope.categories[0].nr = 4;
        	dirScope.toDate.setDate(dirScope.toDate.getDate() + 1);
        	dirScope.newDateRange();
        	dirScope.$apply();
        	expect(dirScope.price).toBe(unitPrice*2*4);
        	dirScope.$apply();
        	expect(elm.find('tr').eq(2).find('td').eq(1).text()).toEqual(unitPrice*2*4 + " NOK");
        	
        	dirScope.categories[0].nr = 3;
        	dirScope.categories[1].nr = 5;
        	dirScope.$apply();
        	var childPrice = 500;
        	expect(dirScope.price).toBe((unitPrice*3*2) + (childPrice*5*2));
        	dirScope.categories[0].nr = 0;
        	dirScope.categories[1].nr = 0;
        	dirScope.$apply();
        	expect(dirScope.price).toBe(0);
        	expect(elm.find('tr').eq(2).find('td').eq(1).text()).toEqual(0 + " NOK");
        });
        
        it('should calculate new price when date changes', function() {
        	var dirScope = elm.isolateScope();
        	dirScope.categories[0].nr = 1;
        	dirScope.$apply();
        	var unitPrice = 300;
        	expect(dirScope.price).toEqual(0);
        	dirScope.toDate.setDate(dirScope.toDate.getDate() + 1);
        	dirScope.$apply();
        	expect(dirScope.price).toEqual(unitPrice);
        });
    });
});











//DNT BOOKING MODULE directive. Contains two date pickers, responsible for obtaining availability,
//and react to changes from dntSelector. Its also a neat packaged booking module containing all html
//needed for booking
describe('dntBookingModule', function () {
	var $rootScope, mockService, deferred;;
    var scope, elm, $body = $('body');
    
   
    beforeEach(function () {
        module('templates', 'dntBookingModule', function($provide) {
          
            $provide.factory('bookingService', function($q) {
                // Service/Factory Mock
                return {
                	getAvailability: function(cabinId, startDate, endDate) {
                		deferred = $q.defer();
                	
                		
                		 return deferred.promise;
                	}
                }
            });
        });
    });
    
    //Inject rootscope and comile and use them to set up directive for testing
    beforeEach(inject(function ($rootScope, $compile, $filter) {
        scope = $rootScope.$new();
        scope.booking = {'cabinId': 1};
        scope.booking.guests = [{guestType: "ungdom", nr: 0, price:300}, {guestType: "barn", nr: 0, price:500}];
        scope.cabinType = 'large';
        scope.beds = 10;
       
        scope.errorMessage = '';
        scope.hideIndex = 1;
       
    }));
   
    
    
    function compileDirective(tpl) {
        if (!tpl) tpl = '<dnt-booking-module cabin-type="{{cabinType}}" number-of-beds="{{beds}}"  on-book="openBookingConfirmDialog()" booking-model="booking" error-model="errorMessage" divider-index="hideIndex"></dnt-price-viewer>';
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
        it('should init controller propertly', function() { 

        	var dirScope = elm.isolateScope();
        	var data = {};
        	data.bookedDays = "[0,4,4,0,0,0,0,10,20,20,20,20,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]";
        	deferred.resolve(data);
        	dirScope.$apply();
        	expect(dirScope.cabinType).toEqual('large');
        	expect(dirScope.beds).toEqual('10');
        	expect(dirScope.errorMessage).toEqual(null);
        	expect(dirScope.dividerIndex).toEqual(1);
        	//something wrong with the test. The provide method does not work
        	expect(dirScope.availability).not.toBeUndefined();
      
        	
        	var d = new Date();
        	var year = d.getFullYear(), month = d.getMonth();
    		var key = year + ' ' + month;
        	
    		var arr  = [0,4,4,0,0,0,0,10,20,20,20,20,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
    		expect(dirScope.availability[key]).toEqual(arr);
        });
        
        // a single test example, check the produced DOM
        it('should produce a dntSelector, two datepickers, booking button and a dntselector', function() { 

        	var dirScope = elm.isolateScope();
        	expect(elm.hasClass("bookingBannner")).toBeTruthy;
        	expect(elm.find('dnt-selector').length).toEqual(1);
        	expect(elm.find('dnt-price-viewer').length).toEqual(1);
        	expect(elm.find('table').length).toEqual(4);
        	
        });
        
        it('Should show layout for a large cabin because of cabin-type parameter', function() {

        	var dirScope = elm.isolateScope();
        	dirScope.$apply();
        	//ng-if
        	expect(elm.find('select').length==2).toBe(true);
        	expect(dirScope.cabinType).toEqual('large');
        });

    });
    
    describe('Availability for datepickers', function() {

        beforeEach(function() {
            compileDirective();
        });
      
        it('should trigger getAvailability function when from or to date changes', function() {
        	var dirScope = elm.isolateScope();
        	
        	
        	var date = new Date("2014-06-10");
        	var year = date.getFullYear(), month = date.getMonth();
        	var key = year + ' ' + month;
        	dirScope.$broadcast('date:change', date);
        	dirScope.$apply();
        	var data = {};
        	data.bookedDays = "[1,2,3,4]";
        	deferred.resolve(data);
        	dirScope.$apply();
        	expect(dirScope.availability[key]).not.toBeUndefined();
        	expect(dirScope.availability[key][0]).toEqual(1);
        	expect(dirScope.availability[key][1]).toEqual(2);
        	expect(dirScope.availability[key][2]).toEqual(3);
        	expect(dirScope.availability[key][3]).toEqual(4);
        	
        });
        
        it('should get availability from back end and broadcast a message', function() {
        	var dirScope = elm.isolateScope();
        	spyOn(dirScope, '$broadcast');
        	var data = {};
        	data.bookedDays = "[1,2,3,4]";
        	deferred.resolve(data);
        	dirScope.$apply();
        	expect(dirScope.$broadcast).toHaveBeenCalledWith('date:updateAvailability');
        });
        
      
    });
    
     

});