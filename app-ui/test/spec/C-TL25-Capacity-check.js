'strict'
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
    
 
    
    
    describe('Availability for datepickers', function() {

        beforeEach(function() {
            compileDirective();
        });
      
        it('A fully booked day should display a red disabled button', function() {
        	var dirScope = elm.isolateScope();
        	
        	var date = new Date();
        	var year = date.getFullYear(), month = date.getMonth(), firstDayOfMonth = new Date(year, month, 1);
        	var key = year + ' ' + month;
        	//ensure that all buttons in calendar are in range
        	dirScope.minimunBookableDate = firstDayOfMonth;
        	var data = {};
        	data.bookedDays = "[1,10,9,0,0,0,0,10,10,10,10,10,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]";
        	deferred.resolve(data);
        	dirScope.$apply();
    		//availability data set
        	expect(dirScope.availability[key]).toBeDefined();
        	console.log(dirScope.availability[key]);
        	expect(elm.find('button').hasClass('btn-user-disabled')).toBe(false);
        	//beds capacity
        	dirScope.booking.guests[0].nr = 10;
        	spyOn(dirScope, 'disabled');
        	dirScope.$apply();
        
        	expect(dirScope.disabled).toHaveBeenCalled();
        	
        	//Calendar actually in view
        	//expect(elm.hasClass('default-calendar-button')).toBe(true);
        	//console.log(elm.find('button').attr('disabled'));
        	//expect(elm.find('button').attr('disabled')>=7).toBe(true);
        	
        });
        
        
        it('should display a not fully booked day as a clickable green', function() {
        	var dirScope = elm.isolateScope();
        	
        });
        
        it('should make sure disable (date available or not) method used by datepickers functions well', function() {
        	var dirScope = elm.isolateScope();
        	var date = new Date();
        	var year = date.getFullYear(), month = date.getMonth();
        	firstDayOfMonth = new Date(year, month, 1);
        	secondDayOfMonth = new Date(year, month, 2);
        	thirdDayOfMonth = new Date(year, month, 3);
        	var data = {};
        	data.bookedDays = "[1,10,9]";
        	deferred.resolve(data);
        	//fully booked
   
        	dirScope.$broadcast('nrOfBedsChosenEvent', 10);
        	dirScope.$apply();
        	//datepickers use this method to eval if a calendar button
        	//should be disabled 
        	expect(dirScope.disabled(firstDayOfMonth, 'day')).toBe(true);
        	expect(dirScope.disabled(secondDayOfMonth, 'day')).toBe(true);
        	expect(dirScope.disabled(thirdDayOfMonth, 'day')).toBe(true);
      
        	dirScope.$broadcast('nrOfBedsChosenEvent', 9);
        	dirScope.$apply();
        	expect(dirScope.disabled(firstDayOfMonth, 'day')).toBe(false);
        	expect(dirScope.disabled(secondDayOfMonth, 'day')).toBe(true);
        	expect(dirScope.disabled(thirdDayOfMonth, 'day')).toBe(true);
      
        	dirScope.$broadcast('nrOfBedsChosenEvent', 1);
        	dirScope.$apply();
        	expect(dirScope.disabled(firstDayOfMonth, 'day')).toBe(false);
        	expect(dirScope.disabled(secondDayOfMonth, 'day')).toBe(true);
        	expect(dirScope.disabled(thirdDayOfMonth, 'day')).toBe(false);
        	
        });
        
        it('should update availability when more or less guests has been selected', function() {
        
        	var dirScope = elm.isolateScope();
        	spyOn(dirScope, '$broadcast').andCallThrough();
        	expect(dirScope.$broadcast).not.toHaveBeenCalledWith('date:updateAvailability');
        	dirScope.booking.guests[1].nr =1;
        	dirScope.$apply();
        	expect(dirScope.$broadcast).toHaveBeenCalledWith('date:updateAvailability');
        });
    });
    
    
});