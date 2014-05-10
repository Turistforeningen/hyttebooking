'use strict';

beforeEach(module('dntApp'));
describe('cabinTableController', function () {


    var scope, fakeFactory, controller, q, deferred, routeParams, mockService, location;

    //Prepare the fake factory
    beforeEach(function () {
        mockService = {
            getCabins: function (page, pageSize) {
                deferred = q.defer();
                // Place the fake return object here
                var returnData = {"data": [{cabinType: 'large', id: 1, name:'Fjordheim', nrActiveBookings: 2, nrOfBeds: 10},{cabinType: 'large', id: 2, name:'Fjellheim', nrActiveBookings: 0, nrOfBeds: 2},{cabinType: 'small', id: 3, name:'Markheim', nrActiveBookings: 4, nrOfBeds: -1}],totalItems:3};
                deferred.resolve(returnData);
                return deferred.promise;
            }
        };
        spyOn(mockService, 'getCabins').andCallThrough();
    });

    //Inject fake factory into controller
    beforeEach(inject(function ($rootScope, $controller, $q, $location) {
        scope = $rootScope.$new();
        q = $q;
        location = $location;
        routeParams = {};
        controller = $controller('cabinTableController', { $scope: scope, $routeParams: routeParams, cabinService: mockService, $location:location });
    }));

	 it('should init controller correctly', function() {
	        expect(scope.currentPage).toBe(1);
	        expect(scope.totalItems).toBe(0);
	        expect(scope.error).toEqual('');
	        
	      
	    });
	    
	 it('should get cabins at init', function() {
		 scope.$apply();
	     expect(scope.cabins).toBeDefined();
	     expect(scope.cabins.length).toBe(3);
	     expect(scope.totalItems).toBe(3);
	     
	 });
	 
	it('should get another page if specified in routeParams', inject(function($controller) {
		var randNrSelected = Math.floor((Math.random()*10)+1);
		routeParams = {page: randNrSelected}; 
		controller = $controller('cabinTableController', { $scope: scope, $routeParams: routeParams, cabinService: mockService, $location:location }); 
		expect(mockService.getCabins).toHaveBeenCalledWith(randNrSelected-1, 10);
		
		
	}));
	it('should show cabin form if button is clicked', function() {
	    scope.page = 200;
	    scope.$broadcast('event:postCabinSuccess');
	    expect(mockService.getCabins).toHaveBeenCalledWith(199, 10);
	     
	});
	
	it('should route to a cabin details view if button clicked', function() {
	     scope.viewCabin(1);
	     expect(location.path()).toEqual("/admin/cabin/1");
	     scope.viewCabin(2);
	     expect(location.path()).toEqual("/admin/cabin/2");
	     
	});
 
});
